package cinema.entities;

import cinema.utils.CachedClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor
@ToString
public class Ticket implements CachedClass<UUID> {

    @Getter @Setter
    private UUID uuid;

    @Getter @Setter
    private Client client;

    @Getter @Setter
    private Session session;

    @Getter @Setter
    private Byte seatRow;

    @Getter @Setter
    private Byte seatNumber;

    public UUID getId() {
        return getUuid();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ticket ticket = (Ticket) o;
        return uuid.equals(ticket.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

}
