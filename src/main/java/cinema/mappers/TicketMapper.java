package cinema.mappers;

import cinema.entities.Ticket;
import cinema.utils.IdentityMapper;
import cinema.utils.Mapper;
import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

public class TicketMapper implements Mapper<Ticket, UUID> {

    private final IdentityMapper cache = IdentityMapper.getInstance();

    @Getter
    @Setter
    private Connection connection;

    public TicketMapper(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Ticket findById(UUID id) throws Exception {
        Ticket funcResult = (Ticket) cache.get(Ticket.class, id);
        if (funcResult == null) {
            PreparedStatement statement =
                    connection.prepareStatement(
                            "SELECT \n" +
                                    "    Т.uuid,\n" +
                                    "    Т.client_id,\n" +
                                    "    Т.session_id,\n" +
                                    "    Т.seat_row,\n" +
                                    "    Т.seat_number\n" +
                                    "FROM \n" +
                                    "    tickets AS Т\n" +
                                    "WHERE\n" +
                                    "    Т.uuid = ?\n" +
                                    "LIMIT 1");
            statement.setString(1, id.toString());
            ClientMapper clientMapper = new ClientMapper(connection);
            SessionMapper sessionMapper = new SessionMapper(connection);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    funcResult = new Ticket(
                            UUID.fromString(resultSet.getString("uuid")),
                            clientMapper.findById(resultSet.getInt("client_id")),
                            sessionMapper.findById(resultSet.getInt("session_id")),
                            resultSet.getByte("seat_row"),
                            resultSet.getByte("seat_number"));
                    cache.put(funcResult);
                }
            } catch (Exception e) {
                throw e;
            }
        }
        return funcResult;
    }

    @Override
    public void save(Ticket object) throws Exception {
        if (object == null) throw new IllegalArgumentException("ticket cannot be null!");
        boolean isNew = (object.getId() == null);
        PreparedStatement statement;
        if (isNew) {
            //можно было и upsert'нуть, но хз, есть ли upsert в mysql
            statement = connection.prepareStatement(
                    "INSERT INTO tickets\n" +
                            "    (uuid, client_id, session_id, seat_row, seat_number)\n" +
                            "VALUES\n" +
                            "    (?, ?, ?, ?, ?)");

            statement.setString(1, object.getUuid().toString());
            statement.setInt(2, object.getClient().getId());
            statement.setInt(3, object.getSession().getId());
            statement.setByte(4, object.getSeatRow());
            statement.setByte(5, object.getSeatNumber());
        } else {
            statement = connection.prepareStatement(
                    "UPDATE tickets\n" +
                            "SET\n" +
                            "    client_id = ?,\n" +
                            "    session_id = ?,\n" +
                            "    seat_row = ?,\n" +
                            "    seat_number = ?\n" +
                            "WHERE \n" +
                            "    uuid = ?\n");

            statement.setInt(1, object.getClient().getId());
            statement.setInt(2, object.getSession().getId());
            statement.setByte(3, object.getSeatRow());
            statement.setByte(4, object.getSeatNumber());
            statement.setString(5, object.getUuid().toString());
        }
        try {
            int rowsAmount = statement.executeUpdate();
            if (rowsAmount == 0) throw new RuntimeException("No rows were updated");
            cache.put(object);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void delete(Ticket object) throws Exception {
        if (object == null) throw new IllegalArgumentException("ticket cannot be null!");
        PreparedStatement statement = connection.prepareStatement(
                "DELETE \n" +
                        "FROM\n" +
                        "    tickets AS T\n" +
                        "WHERE\n" +
                        "    T.uuid = ?"
        );

        statement.setString(1, object.getUuid().toString());
        try {
            statement.executeUpdate();
            cache.remove(object);
        } catch (Exception e) {
            throw e;
        }
    }

}
