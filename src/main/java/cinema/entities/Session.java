package cinema.entities;

import cinema.utils.CachedClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@AllArgsConstructor
@ToString
public class Session implements CachedClass<Integer> {

    @Getter @Setter
    private Integer id;

    @Getter @Setter
    private Movie movie;

    @Getter @Setter
    private LocalDateTime startDate;

    @Getter @Setter
    private Short ticketsAmount;

    @Getter @Setter
    private BigDecimal price;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Session session = (Session) o;
        return movie.equals(session.movie) && startDate.equals(session.startDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(movie, startDate);
    }

}
