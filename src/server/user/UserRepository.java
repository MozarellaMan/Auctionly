package server.user;


import util.Util;

import java.io.Serializable;
import java.util.*;

public class UserRepository implements Serializable {
    private final Map<Integer, User> users;

    public UserRepository() {
        this.users = new HashMap<>();
    }

    public int register(String name, String email, Role role) {
        if (users.values().stream().anyMatch(user -> user.getEmail().equals(email))) {
            Util.warning("Email already exists! Registration failed.");
            return 0;
        }

        var newUser = User.of(name, email, role);

        users.put(newUser.getId(), newUser);

        return newUser.getId();
    }

    public Optional<User> get(int userId) {
        return Optional.ofNullable(users.get(userId));
    }

    public boolean exists(String name, String email) {
        return users.values().stream()
                .anyMatch(user -> user.getEmail().equals(email) && user.getName().equals(name));
    }

    public boolean exists(int userId) {
        return users.containsKey(userId);
    }

    public Optional<User> findUser(String name, String email) {
        return users.values().stream()
                .filter(user -> user.getEmail().equals(email) && user.getName().equals(name))
                .findFirst();
    }

    public List<User> list() {
        return new ArrayList<>(users.values());
    }
}
