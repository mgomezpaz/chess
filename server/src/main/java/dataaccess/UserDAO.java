package dataaccess;

import model.UserData;

/**
 * Interface for User data access operations
 */
public interface UserDAO {
    /**
     * Creates a new user in the database
     * @param userData the user to create
     * @throws DataAccessException if an error occurs or user already exists
     */
    void createUser(UserData userData) throws DataAccessException;

    /**
     * Retrieves a user from the database
     * @param username the username of the user to retrieve
     * @return the user data, or null if not found
     * @throws DataAccessException if an error occurs
     */
    UserData getUser(String username) throws DataAccessException;

    /**
     * Clears all users from the database
     * @throws DataAccessException if there is an error clearing users
     */
    void clear() throws DataAccessException;
} 