package dataaccess;

/**
 * Factory for creating DAO instances
 */
public class DAOFactory {
    // Set this to true to use MySQL implementation
    private static final boolean USE_MYSQL = true;

    /**
     * Gets a UserDAO instance
     * @return A UserDAO instance
     */
    public static UserDAO getUserDAO() {
        if (USE_MYSQL) {
            return MySqlUserDAO.getInstance();
        } else {
            return MemoryUserDAO.getInstance();
        }
    }

    /**
     * Gets an AuthDAO instance
     * @return An AuthDAO instance
     */
    public static AuthDAO getAuthDAO() {
        if (USE_MYSQL) {
            return MySqlAuthDAO.getInstance();
        } else {
            return MemoryAuthDAO.getInstance();
        }
    }

    /**
     * Gets a GameDAO instance
     * @return A GameDAO instance
     */
    public static GameDAO getGameDAO() {
        if (USE_MYSQL) {
            return MySqlGameDAO.getInstance();
        } else {
            return MemoryGameDAO.getInstance();
        }
    }

    /**
     * Checks if MySQL implementation is being used
     * @return true if MySQL is being used, false otherwise
     */
    public static boolean isUsingMySQL() {
        return USE_MYSQL;
    }
} 