package service;

import dataaccess.*;
import result.ClearResult;

/**
 * Wipes all data - mostly for testing
 */
public class ClearService {
    // need access to all the data
    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public ClearService() {
        // memory implementations for phase 3
        this.userDAO = new MemoryUserDAO();
        this.authDAO = new MemoryAuthDAO();
        this.gameDAO = new MemoryGameDAO();
    }

    /**
     * Nukes everything from orbit
     */
    public ClearResult clear() throws DataAccessException {
        try {
            // order doesn't really matter here
            userDAO.clear();
            authDAO.clear();
            gameDAO.clear();
            
            // all done
            return new ClearResult();
        } catch (Exception e) {
            // oops
            throw new DataAccessException("Failed to clear data: " + e.getMessage());
        }
    }
} 