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
import exception.BadRequestException;

import java.util.UUID;

/**
 * Handles all user-related operations
 * This includes registration, login, and logout functionality
 */
public class UserService {
    // Our data access objects
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService() {
        // Get the singleton instances to make sure we're all using the same data
        this.userDAO = MemoryUserDAO.getInstance();
        this.authDAO = MemoryAuthDAO.getInstance();
    }

    /**
     * Registers a new user in the system
     */
    public RegisterResult register(RegisterRequest request) throws DataAccessException, AlreadyTakenException, BadRequestException {
        // First, let's validate all the fields
        if (request.username() == null || request.username().isEmpty()) {
            throw new BadRequestException("Username cannot be empty");
        }
        if (request.password() == null || request.password().isEmpty()) {
            throw new BadRequestException("Password cannot be empty");
        }
        if (request.email() == null || request.email().isEmpty()) {
            throw new BadRequestException("Email cannot be empty");
        }
        
        // Make sure the username isn't already taken
        if (userDAO.getUser(request.username()) != null) {
            throw new AlreadyTakenException("Sorry, that username is taken");
        }

        // All good! Create the user
        UserData userData = new UserData(request.username(), request.password(), request.email());
        userDAO.createUser(userData);

        // Generate a random auth token using UUID
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, request.username());
        authDAO.createAuth(authData);

        // Return the result with username and token
        return new RegisterResult(request.username(), authToken);
    }

    /**
     * Logs in an existing user
     */
    public LoginResult login(LoginRequest request) throws DataAccessException, UnauthorizedException {
        // Try to find the user
        UserData userData = userDAO.getUser(request.username());
        
        // Check if user exists and password matches
        if (userData == null || !userData.password().equals(request.password())) {
            throw new UnauthorizedException("Invalid username or password");
        }

        // User is valid, create a new auth token
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, request.username());
        authDAO.createAuth(authData);

        // Return the result with username and new token
        return new LoginResult(request.username(), authToken);
    }

    /**
     * Logs out a user by invalidating their auth token
     */
    public LogoutResult logout(LogoutRequest request) throws DataAccessException, UnauthorizedException {
        // Check if the token is valid
        AuthData authData = authDAO.getAuth(request.authToken());
        if (authData == null) {
            throw new UnauthorizedException("You're not logged in");
        }

        // Valid token, so delete it
        authDAO.deleteAuth(request.authToken());

        // Nothing to return for logout, just success
        return new LogoutResult();
    }
} 