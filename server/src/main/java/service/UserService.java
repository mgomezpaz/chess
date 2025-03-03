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
 * Handles user operations - register, login, logout
 */
public class UserService {
    // need these to access data
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService() {
        // TODO: maybe use dependency injection later?
        this.userDAO = new MemoryUserDAO();
        this.authDAO = new MemoryAuthDAO();
    }

    /**
     * Signs up a new user
     */
    public RegisterResult register(RegisterRequest request) throws DataAccessException, AlreadyTakenException {
        // check if username is taken
        if (userDAO.getUser(request.username()) != null) {
            throw new AlreadyTakenException("Sorry, that username is taken");
        }

        // create the user
        UserData userData = new UserData(request.username(), request.password(), request.email());
        userDAO.createUser(userData);

        // generate auth token - UUID is good enough for now
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, request.username());
        authDAO.createAuth(authData);

        // send back the token
        return new RegisterResult(request.username(), authToken);
    }

    /**
     * Logs in an existing user
     */
    public LoginResult login(LoginRequest request) throws DataAccessException, UnauthorizedException {
        // find the user
        UserData userData = userDAO.getUser(request.username());
        
        // validate credentials
        if (userData == null || !userData.password().equals(request.password())) {
            throw new UnauthorizedException("Invalid username or password");
        }

        // create a fresh auth token
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, request.username());
        authDAO.createAuth(authData);

        return new LoginResult(request.username(), authToken);
    }

    /**
     * Logs out a user
     */
    public LogoutResult logout(LogoutRequest request) throws DataAccessException, UnauthorizedException {
        // make sure token exists
        AuthData authData = authDAO.getAuth(request.authToken());
        if (authData == null) {
            throw new UnauthorizedException("You're not logged in");
        }

        // delete the token
        authDAO.deleteAuth(request.authToken());

        // nothing to return but success
        return new LogoutResult();
    }
} 