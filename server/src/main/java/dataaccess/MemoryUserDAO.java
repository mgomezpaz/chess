package dataaccess;

import model.UserData;
import java.util.HashMap;
import java.util.Map;

/**
 * In-memory implementation of the UserDAO interface
 */
public class MemoryUserDAO implements UserDAO {
    // just store users in a map for now
    private final Map<String, UserData> users = new HashMap<>();

    @Override
    public void clear() throws DataAccessException {
        // easy, just clear the map
        users.clear();
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        // check if user already exists
        if (users.containsKey(user.username())) {
            throw new DataAccessException("User already exists");
        }
        
        // add the user to our map
        users.put(user.username(), user);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        // just return the user from the map (might be null)
        return users.get(username);
    }
} 