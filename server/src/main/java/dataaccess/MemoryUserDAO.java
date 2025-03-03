package dataaccess;

import model.UserData;
import java.util.HashMap;
import java.util.Map;

/**
 * Stores users in memory - will be replaced with database later
 */
public class MemoryUserDAO implements UserDAO {
    // just a simple map for now - username -> user data
    private final Map<String, UserData> users = new HashMap<>();

    @Override
    public void clear() throws DataAccessException {
        // wipe all users
        users.clear();
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        // make sure we don't have duplicate usernames
        if (users.containsKey(user.username())) {
            throw new DataAccessException("Username already exists");
        }
        
        // add to our map
        users.put(user.username(), user);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        // might return null if user doesn't exist
        return users.get(username);
    }
} 