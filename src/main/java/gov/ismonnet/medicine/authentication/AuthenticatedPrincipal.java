package gov.ismonnet.medicine.authentication;

import java.security.Principal;
import java.util.Objects;

class AuthenticatedPrincipal implements Principal {

    private final int id;
    private final String name;

    public AuthenticatedPrincipal(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AuthenticatedPrincipal)) return false;
        AuthenticatedPrincipal that = (AuthenticatedPrincipal) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "SimplePrincipal{" +
                "name='" + name + '\'' +
                '}';
    }
}
