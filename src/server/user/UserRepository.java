package server.user;


import util.Util;

import java.util.HashMap;
import java.util.Map;

public class UserRepository {
    public Map<Integer, User> users;

    public UserRepository() {
        this.users = new HashMap<>();
    }

    public boolean register(String name, String email, Role role) {
        if (users.values().stream().anyMatch(user -> user.getEmail().equals(email))) {
            Util.warning("Email already exists! Registration failed.");
            return false;
        }

        var newUser = User.of(name, email, role);

        users.put(newUser.getId(), newUser);

        return true;
    }

}
