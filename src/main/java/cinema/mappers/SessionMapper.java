package cinema.mappers;

import cinema.entities.Session;
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

public class SessionMapper implements Mapper<Session, Integer> {

    private final IdentityMapper cache = IdentityMapper.getInstance();

    @Getter
    @Setter
    private Connection connection;

    public SessionMapper(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Session findById(Integer id) throws Exception {
        Session funcResult = (Session) cache.get(Session.class, id);
        if (funcResult == null) {
            PreparedStatement statement =
                    connection.prepareStatement(
                            "SELECT \n" +
                                    "    Т.id,\n" +
                                    "    Т.movie_id,\n" +
                                    "    Т.start_date,\n" +
                                    "    Т.tickets_amount,\n" +
                                    "    Т.price\n" +
                                    "FROM \n" +
                                    "    sessions AS Т\n" +
                                    "WHERE\n" +
                                    "    Т.id = ?\n" +
                                    "LIMIT 1");
            statement.setInt(1, id);
            MovieMapper movieMapper = new MovieMapper(connection);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Timestamp timestamp = resultSet.getTimestamp("start_date");
                    LocalDateTime dateTime = null;
                    if (timestamp != null) dateTime = timestamp.toLocalDateTime();
                    funcResult = new Session(
                            resultSet.getInt("id"),
                            movieMapper.findById(resultSet.getInt("movie_id")),
                            dateTime, resultSet.getShort("tickets_amount"),
                            resultSet.getBigDecimal("price"));
                    cache.put(funcResult);
                }
            } catch (Exception e) {
                throw e;
            }
        }
        return funcResult;
    }

    @Override
    public void save(Session object) throws Exception {
        if (object == null) throw new IllegalArgumentException("session cannot be null!");
        String queryText;
        boolean isNew = (object.getId() == null);
        if (isNew) {
            //можно было и upsert'нуть, но хз, есть ли upsert в mysql
            queryText =
                    "INSERT INTO sessions\n" +
                            "    (movie_id, start_date, tickets_amount, price)\n" +
                            "VALUES\n" +
                            "    (?, ?, ?, ?)";
        } else {
            queryText =
                    "UPDATE sessions\n" +
                            "SET\n" +
                            "    movie_id = ?,\n" +
                            "    start_date = ?,\n" +
                            "    tickets_amount = ?,\n" +
                            "    price = ?\n" +
                            "WHERE \n" +
                            "    id = ?\n";
        }
        PreparedStatement statement = connection.prepareStatement(queryText);

        statement.setInt(1, object.getMovie().getId());
        statement.setTimestamp(2, Timestamp.from(object.getStartDate().toInstant(ZoneOffset.UTC)));
        statement.setShort(3, object.getTicketsAmount());
        statement.setBigDecimal(4, object.getPrice());
        if (!isNew) statement.setInt(5, object.getId());
        try {
            int rowsAmount = statement.executeUpdate();
            if (rowsAmount == 0) throw new RuntimeException("No rows were updated");
            cache.put(object);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void delete(Session object) throws Exception {
        if (object == null) throw new IllegalArgumentException("session cannot be null!");
        PreparedStatement statement = connection.prepareStatement(
                "DELETE \n" +
                        "FROM\n" +
                        "    sessions AS T\n" +
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
