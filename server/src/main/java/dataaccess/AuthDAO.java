package dataaccess;

import java.util.List;
import model.AuthData;

/**
 * Interface for Authentication data access operations
 */
public interface AuthDAO {
    /**
     * Creates a new authentication token in the database
     * @param authData the authentication data to create
     * @throws DataAccessException if there is an error creating the auth token
     */
    void createAuth(AuthData authData) throws DataAccessException;

    /**
     * Retrieves auth data from the database
     * @param authToken the auth token to retrieve
     * @return the auth data, or null if not found
     * @throws DataAccessException if an error occurs
     */
    AuthData getAuth(String authToken) throws DataAccessException;

    /**
     * Deletes an auth token from the database
     * @param authToken the auth token to delete
     * @throws DataAccessException if an error occurs
     */
    void deleteAuth(String authToken) throws DataAccessException;

    /**
     * Clears all authentication tokens from the database
     * @throws DataAccessException if there is an error clearing auth tokens
     */
    void clear() throws DataAccessException;

    /**
     * Retrieves all auth data from the database
     * @return a list of all auth data
     * @throws DataAccessException if an error occurs
     */
    List<AuthData> getAllAuthData() throws DataAccessException;
} 