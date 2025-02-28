package service;

import dataaccess.*;
import exception.AlreadyTakenException;
import exception.UnauthorizedException;
import model.AuthData;
import model.UserData;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.LogoutResult;
import result.RegisterResult;

import java.util.UUID;

/**
 * Service class for user-related operations
 */
public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService() {
        this.userDAO = new MemoryUserDAO();
        this.authDAO = new MemoryAuthDAO();
    }

    /**
     * Registers a new user
     * @param request the registration request
     * @return the registration result
     * @throws AlreadyTakenException if the username is already taken
     * @throws DataAccessException if there is an error accessing the data store
     */
    public RegisterResult register(RegisterRequest request) throws DataAccessException, AlreadyTakenException {
        // Check if username already exists
        if (userDAO.getUser(request.username()) != null) {
            throw new AlreadyTakenException("Username already taken");
        }

        // Create new user
        UserData userData = new UserData(request.username(), request.password(), request.email());
        userDAO.createUser(userData);

        // Generate auth token
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, request.username());
        authDAO.createAuth(authData);

        return new RegisterResult(request.username(), authToken);
    }

    /**
     * Logs in a user
     * @param request the login request
     * @return the login result
     * @throws UnauthorizedException if the username or password is invalid
     * @throws DataAccessException if there is an error accessing the data store
     */
    public LoginResult login(LoginRequest request) throws DataAccessException, UnauthorizedException {
        // Get user data
        UserData userData = userDAO.getUser(request.username());
        if (userData == null || !userData.password().equals(request.password())) {
            throw new UnauthorizedException("Invalid username or password");
        }

        // Generate auth token
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, request.username());
        authDAO.createAuth(authData);

        return new LoginResult(request.username(), authToken);
    }

    /**
     * Logs out a user
     * @param request the logout request
     * @return the logout result
     * @throws UnauthorizedException if the auth token is invalid
     * @throws DataAccessException if there is an error accessing the data store
     */
    public LogoutResult logout(LogoutRequest request) throws DataAccessException, UnauthorizedException {
        // Verify auth token
        AuthData authData = authDAO.getAuth(request.authToken());
        if (authData == null) {
            throw new UnauthorizedException("Invalid auth token");
        }

        // Delete auth token
        authDAO.deleteAuth(request.authToken());

        return new LogoutResult();
    }
} 