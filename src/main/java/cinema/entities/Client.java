package cinema.entities;

import cinema.utils.CachedClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class Client implements CachedClass<Integer> {

    @Getter @Setter
    private Integer id;

    @Getter @Setter
    private String phoneNumber;

    @Getter @Setter
    private String firstName;

    @Getter @Setter
    private String lastName;

    @Override
    public String toString() {
        return String.join(" ", firstName, lastName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return phoneNumber.equals(client.phoneNumber);
    }

    @Override
    public int hashCode() {
        return phoneNumber.hashCode();
    }

}
