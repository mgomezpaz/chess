package service;

import dataaccess.*;
import result.ClearResult;

/**
 * Service for clearing the database
 */
public class ClearService {
    // need these to clear everything
    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public ClearService() {
        // using our memory implementations
        this.userDAO = new MemoryUserDAO();
        this.authDAO = new MemoryAuthDAO();
        this.gameDAO = new MemoryGameDAO();
    }

    /**
     * Clears all data from the database
     */
    public ClearResult clear() throws DataAccessException {
        // wipe everything clean
        try {
            // clear users first
            userDAO.clear();
            
            // then auth tokens
            authDAO.clear();
            
            // and finally games
            gameDAO.clear();
            
            // all good!
            return new ClearResult();
        } catch (Exception e) {
            // something went wrong
            throw new DataAccessException("Error clearing database: " + e.getMessage());
        }
    }
} 