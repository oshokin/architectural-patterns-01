package cinema.mappers;

import cinema.entities.Client;
import cinema.utils.IdentityMapper;
import cinema.utils.Mapper;
import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ClientMapper implements Mapper<Client, Integer> {

    private final IdentityMapper cache = IdentityMapper.getInstance();

    @Getter
    @Setter
    private Connection connection;

    public ClientMapper(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Client findById(Integer id) throws Exception {
        Client funcResult = (Client) cache.get(Client.class, id);
        if (funcResult == null) {
            PreparedStatement statement =
                    connection.prepareStatement(
                            "SELECT \n" +
                                    "    Т.id,\n" +
                                    "    Т.phone_number,\n" +
                                    "    Т.first_name,\n" +
                                    "    Т.last_name\n" +
                                    "FROM \n" +
                                    "    clients AS Т\n" +
                                    "WHERE\n" +
                                    "    Т.id = ?\n" +
                                    "LIMIT 1");
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    funcResult = new Client(
                            resultSet.getInt("id"),
                            resultSet.getString("phone_number"),
                            resultSet.getString("first_name"),
                            resultSet.getString("last_name"));
                    cache.put(funcResult);
                }
            } catch (Exception e) {
                throw e;
            }
        }
        return funcResult;
    }

    @Override
    public void save(Client object) throws Exception {
        if (object == null) throw new IllegalArgumentException("client cannot be null!");
        String queryText;
        boolean isNew = (object.getId() == null);
        if (isNew) {
            //можно было и upsert'нуть, но хз, есть ли upsert в mysql
            queryText =
                    "INSERT INTO clients\n" +
                            "    (phone_number, first_name, last_name)\n" +
                            "VALUES\n" +
                            "    (?, ?, ?)";
        } else {
            queryText =
                    "UPDATE clients\n" +
                            "SET\n" +
                            "    phone_number = ?,\n" +
                            "    first_name = ?,\n" +
                            "    last_name = ?\n" +
                            "WHERE \n" +
                            "    id = ?\n";
        }
        PreparedStatement statement = connection.prepareStatement(queryText);

        statement.setString(1, object.getPhoneNumber());
        statement.setString(2, object.getFirstName());
        statement.setString(3, object.getLastName());
        if (!isNew) statement.setInt(4, object.getId());
        try {
            int rowsAmount = statement.executeUpdate();
            if (rowsAmount == 0) throw new RuntimeException("No rows were updated");
            cache.put(object);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void delete(Client object) throws Exception {
        if (object == null) throw new IllegalArgumentException("client cannot be null!");
        PreparedStatement statement = connection.prepareStatement(
                "DELETE \n" +
                        "FROM\n" +
                        "    clients AS T\n" +
                        "WHERE\n" +
                        "    T.id = ?"
        );

        statement.setInt(1, object.getId());
        try {
            statement.executeUpdate();
            cache.remove(object);
        } catch (Exception e) {
            throw e;
        }
    }

}
