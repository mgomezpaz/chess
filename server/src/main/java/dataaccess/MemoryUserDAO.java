package dataaccess;

import model.UserData;
import java.util.HashMap;
import java.util.Map;

/**
 * In-memory implementation of UserDAO
 */
public class MemoryUserDAO implements UserDAO {
    // Our singleton instance - we only want one of these!
    private static MemoryUserDAO instance;
    
    // Simple username -> userData map
    private final Map<String, UserData> users = new HashMap<>();
    
    // Private constructor so nobody can create instances directly
    private MemoryUserDAO() {
        // Nothing to initialize here
    }
    
    /**
     * Get the singleton instance - creates it if it doesn't exist yet
     */
    public static synchronized MemoryUserDAO getInstance() {
        if (instance == null) {
            // First time this is called
            instance = new MemoryUserDAO();
        }
        return instance;
    }

    @Override
    public void clear() throws DataAccessException {
        // Start fresh - wipe all users
        users.clear();
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        // Don't allow duplicate usernames
        if (users.containsKey(user.username())) {
            throw new DataAccessException("Username already exists");
        }
        
        // All good, add the user
        users.put(user.username(), user);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        // Simple lookup - might be null if user doesn't exist
        return users.get(username);
    }
} 