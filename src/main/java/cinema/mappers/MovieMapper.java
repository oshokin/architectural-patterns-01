package cinema.mappers;

import cinema.entities.Movie;
import cinema.utils.IdentityMapper;
import cinema.utils.Mapper;
import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public class MovieMapper implements Mapper<Movie, Integer> {

    private final IdentityMapper cache = IdentityMapper.getInstance();

    @Getter
    @Setter
    private Connection connection;

    public MovieMapper(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Movie findById(Integer id) throws Exception {
        Movie funcResult = (Movie) cache.get(Movie.class, id);
        if (funcResult == null) {
            PreparedStatement baseStatement =
                    connection.prepareStatement(
                            "SELECT \n" +
                                    "    Т.id,\n" +
                                    "    Т.name,\n" +
                                    "    Т.duration\n" +
                                    "FROM \n" +
                                    "    movies AS Т\n" +
                                    "WHERE\n" +
                                    "    Т.id = ?\n" +
                                    "LIMIT 1");
            PreparedStatement pricesStatement =
                    connection.prepareStatement(
                            "SELECT\n" +
                                    "    T.date_time,\n" +
                                    "    T.price\n" +
                                    "FROM\n" +
                                    "    prices AS T\n" +
                                    "WHERE\n" +
                                    "    T.movie_id = ?");
            baseStatement.setInt(1, id);
            pricesStatement.setInt(1, id);
            try (ResultSet baseResultSet = baseStatement.executeQuery();
                    ResultSet pricesResultSet = pricesStatement.executeQuery()) {

                if (baseResultSet.next()) {
                    List<Movie.PriceEntry> pricesHistory = new ArrayList<>();
                    funcResult = new Movie(
                            baseResultSet.getInt("id"),
                            baseResultSet.getString("name"),
                            baseResultSet.getShort("duration"),
                            pricesHistory);
                    while (pricesResultSet.next()) {
                        Timestamp timestamp = pricesResultSet.getTimestamp("date_time");
                        LocalDateTime dateTime = null;
                        if (timestamp != null) dateTime = timestamp.toLocalDateTime();
                        pricesHistory.add(new Movie.PriceEntry(dateTime, pricesResultSet.getBigDecimal("price")));
                    }
                    funcResult.setPricesHistory(pricesHistory);
                    cache.put(funcResult);
                }
            } catch (Exception e) {
                throw e;
            }
        }
        return funcResult;
    }

    @Override
    public void save(Movie object) throws Exception {
        if (object == null) throw new IllegalArgumentException("movie cannot be null!");
        String queryText;
        boolean isNew = (object.getId() == null);
        if (isNew) {
            //можно было и upsert'нуть, но хз, есть ли upsert в mysql
            queryText =
                    "INSERT INTO movies\n" +
                            "    (name, duration)\n" +
                            "VALUES\n" +
                            "    (?, ?)";
        } else {
            queryText =
                    "UPDATE movies\n" +
                            "SET\n" +
                            "    name = ?,\n" +
                            "    duration = ?\n" +
                            "WHERE \n" +
                            "    id = ?\n";
        }
        PreparedStatement statement = connection.prepareStatement(queryText);

        statement.setString(1, object.getName());
        statement.setShort(2, object.getDuration());

        if (!isNew) statement.setInt(3, object.getId());
        try {
            int rowsAmount = statement.executeUpdate();
            if (rowsAmount == 0) throw new RuntimeException("No rows were updated");
            queryText =
                    "DELETE\n" +
                    "FROM \n" +
                    "    prices\n" +
                    "WHERE \n" +
                    "    movie_id = ?";
            statement = connection.prepareStatement(queryText);
            statement.setInt(1, object.getId());
            statement.executeUpdate();
            for (Movie.PriceEntry e : object.getPricesHistory()) {
                statement = connection.prepareStatement("" +
                        "INSERT INTO prices\n" +
                        "    (date_time, movie_id, price)\n" +
                        "VALUES\n" +
                        "    (?, ?, ?)");
                statement.setTimestamp(1, Timestamp.from(e.getDateTime().toInstant(ZoneOffset.UTC)));
                statement.setInt(2, object.getId());
                statement.setShort(3, object.getDuration());
                statement.executeUpdate();
            }
            cache.put(object);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void delete(Movie object) throws Exception {
        if (object == null) throw new IllegalArgumentException("movie cannot be null!");
        PreparedStatement statement = connection.prepareStatement(
                "DELETE \n" +
                        "FROM\n" +
                        "    movies AS T\n" +
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
