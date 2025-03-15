package service;

import dataaccess.*;
import result.ClearResult;

/**
 * Service for clearing all data
 */
public class ClearService {
    // Our data access objects
    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public ClearService(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public ClearService() {
        // Get the instances from our factory
        this.userDAO = DAOFactory.getUserDAO();
        this.authDAO = DAOFactory.getAuthDAO();
        this.gameDAO = DAOFactory.getGameDAO();
    }

    /**
     * Clears all data from the system - use with caution!
     */
    public ClearResult clear() throws DataAccessException {
        try {
            // Wipe everything clean in the correct order to avoid foreign key constraint issues
            authDAO.clear();  // Clear auth tokens first (they reference users)
            gameDAO.clear();  // Clear games next
            userDAO.clear();  // Clear users last
            
            // All done - everything is gone!
            return new ClearResult();
        } catch (Exception e) {
            // Something went wrong with the clearing process
            throw new DataAccessException("Failed to clear data: " + e.getMessage());
        }
    }
} 