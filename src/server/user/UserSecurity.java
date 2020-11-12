package server.user;

import java.util.HashMap;
import java.util.Map;

public class UserSecurity {

    private final Map<User, AuthState> currentUsers;

    public UserSecurity() {
        this.currentUsers = new HashMap<>();
    }

    public void addUser(User user) {
        currentUsers.put(user, AuthState.UNAUTHENTICATED);
    }

    public boolean isAuthenticated(int userId, UserRepository userRepo) {
        var user = userRepo.get(userId).orElse(null);
        if (user == null) return false;
        return currentUsers.get(user).equals(AuthState.AUTHENTICATED);
    }

    public boolean authenticate(int userId, UserRepository userRepo) {
        var user = userRepo.get(userId).orElse(null);
        if (!userRepo.exists(userId)) return false;
        currentUsers.put(user, AuthState.AUTHENTICATED);
        return true;
    }

    public String list() {
        return currentUsers.entrySet().toString();
    }
}
