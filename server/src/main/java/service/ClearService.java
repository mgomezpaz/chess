package service;

import dataaccess.*;
import result.ClearResult;

/**
 * Service for clearing all data
 */
public class ClearService {
    // We need access to all the DAOs to clear everything
    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public ClearService() {
        // Get the singleton instances of all our DAOs
        // This ensures we're clearing the same data that other services are using
        this.userDAO = MemoryUserDAO.getInstance();
        this.authDAO = MemoryAuthDAO.getInstance();
        this.gameDAO = MemoryGameDAO.getInstance();
    }

    /**
     * Clears all data from the system - use with caution!
     * As they say, "nuke it from orbit, it's the only way to be sure"
     */
    public ClearResult clear() throws DataAccessException {
        try {
            // Wipe everything clean
            // The order doesn't really matter since we're in memory,
            // but it might matter later with a real database (foreign keys, etc.)
            userDAO.clear();
            authDAO.clear();
            gameDAO.clear();
            
            // All done - everything is gone!
            return new ClearResult();
        } catch (Exception e) {
            // Something went wrong with the clearing process
            throw new DataAccessException("Failed to clear data: " + e.getMessage());
        }
    }
} 