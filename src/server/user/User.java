package server.user;

import java.io.Serializable;
import java.util.concurrent.ThreadLocalRandom;

public class User implements Serializable {
    private final int id;
    private final String name;
    private final String email;
    private final Role userRole;

    private User(String name, String email, Role userRole) {
        this.name = name;
        this.email = email;
        this.userRole = userRole;
        this.id = ThreadLocalRandom.current().nextInt(1, 10000);
    }

    public static User of(String name, String email, Role userRole) {
        return new User(name, email, userRole);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Role getUserRole() {
        return userRole;
    }

}
