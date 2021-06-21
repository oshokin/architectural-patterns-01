package cinema;

import cinema.entities.Client;
import cinema.mappers.ClientMapper;
import cinema.utils.IdentityMapper;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class Patternator {

    private Properties properties = new Properties();
    private final IdentityMapper cache = IdentityMapper.getInstance();

    public static void main(String[] args) {
        Patternator tester = new Patternator();
        tester.initializeProperties();
        try (Connection connection = tester.establishConnection()) {
            //думаю этого хватит
            //откуда такие веселые названия фильмов
            //побаловался с deeplearning4j и нагенерил нейросетью на датасете IMDB
            tester.checkPersonCRUD(connection);
        } catch (Exception e) {
            System.out.println("Couldn't make magic work:");
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void initializeProperties() {
        try {
            properties.load(getClass().getClassLoader().getResourceAsStream("application.properties"));
        } catch (IOException e) {
            System.out.println("Couldn't make magic work:");
            e.printStackTrace();
        }
    }

    private Connection establishConnection() throws Exception {
        Class.forName(properties.getProperty("jdbc.driver.name"));
        return DriverManager.getConnection(
                properties.getProperty("jdbc.url"),
                properties.getProperty("jdbc.user"),
                properties.getProperty("jdbc.password"));
    }

    private void checkPersonCRUD(Connection connection) throws Exception {
        ClientMapper mapper = new ClientMapper(connection);

        System.out.println("Let's see client №100");
        Client victim = mapper.findById(100);
        System.out.printf("Our victim is: %s%n", victim);
        if (victim == null) {
            System.out.println("Damn it! We got no victim!");
            return;
        }

        System.out.println("Let's see how caching works!");
        for (int i = 0; i < 100; i++) {
            Client cachedVictim = mapper.findById(100);
            System.out.printf("Cached victim is: %s and victim is %s%n",
                    cachedVictim, (cachedVictim == victim ?
                            "the same" : "different (check your hashmap realization!)"));
        }

        System.out.println("What if I was another person? Let's try this out!");
        victim.setFirstName("Ben");
        victim.setLastName("Laden");
        victim.setPhoneNumber("03");
        mapper.save(victim);

        //избегаем кеширования
        cache.clearAll();
        victim = mapper.findById(100);
        System.out.printf("Renamed victim is: %s%n", victim);

        System.out.println("Now let's erase the mofo! Why? For lulz");
        mapper.delete(victim);
        Client victimAfterRemoval = mapper.findById(100);
        System.out.printf("Our victim is: %s%n", victimAfterRemoval);
    }

}