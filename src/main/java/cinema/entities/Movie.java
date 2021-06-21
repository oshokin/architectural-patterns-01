package cinema.entities;

import cinema.utils.CachedClass;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@ToString
public class Movie implements CachedClass<Integer> {

    @Getter @Setter
    private Integer id;

    @Getter @Setter
    private String name;

    @Getter @Setter
    private Short duration;

    @Getter @Setter
    private List<PriceEntry> pricesHistory;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return id.equals(movie.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    @ToString
    public static class PriceEntry {

        @Getter @Setter
        private LocalDateTime dateTime;

        @Getter @Setter
        private BigDecimal price;

    }

}
