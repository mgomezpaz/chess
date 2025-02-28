package dataaccess;

/**
 * Indicates that an error occurred while accessing the data store
 */
public class DataAccessException extends Exception {
    public DataAccessException(String message) {
        super(message);
    }
} 