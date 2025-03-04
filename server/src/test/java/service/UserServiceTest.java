package service;

import dataaccess.DataAccessException;
import exception.AlreadyTakenException;
import exception.BadRequestException;
import exception.UnauthorizedException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.LogoutResult;
import result.RegisterResult;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    
    private UserService userService;
    private ClearService clearService;
    
    @BeforeEach
    public void setup() throws DataAccessException {
        // Start fresh for each test
        userService = new UserService();
        clearService = new ClearService();
        clearService.clear(); // wipe everything
    }
    
    @Test
    public void registerPositive() throws Exception {
        // Let's make a test user
        var request = new RegisterRequest("testuser", "password123", "test@example.com");
        
        // Register them
        var result = userService.register(request);
        
        // Make sure it worked
        assertNotNull(result);
        assertEquals("testuser", result.username());
        assertNotNull(result.authToken(), "Auth token shouldn't be null");
        assertTrue(result.authToken().length() > 10, "Auth token should be reasonably long");
    }
    
    @Test
    public void registerNegativeDuplicate() {
        // Create a user with a cool username
        RegisterRequest request = new RegisterRequest("cooluser", "password123", "cool@example.com");
        
        // First registration should work fine
        try {
            userService.register(request);
        } catch (Exception e) {
            fail("First registration should succeed: " + e.getMessage());
        }
        
        // Second registration with same username should fail
        AlreadyTakenException exception = assertThrows(AlreadyTakenException.class, () -> {
            userService.register(request);
        });
        
        // Check the error message
        String errorMsg = exception.getMessage();
        assertTrue(errorMsg.contains("username is taken"), 
                "Error should mention username is taken, but was: " + errorMsg);
    }
    
    @Test
    public void registerNegativeEmptyFields() {
        // Try with empty username
        Exception exception1 = assertThrows(BadRequestException.class, () -> {
            userService.register(new RegisterRequest("", "password123", "test@example.com"));
        });
        assertTrue(exception1.getMessage().contains("Username cannot be empty"));
        
        // Try with empty password
        var exception2 = assertThrows(BadRequestException.class, () -> {
            userService.register(new RegisterRequest("testuser", "", "test@example.com"));
        });
        assertTrue(exception2.getMessage().contains("Password cannot be empty"));
        
        // Try with empty email
        BadRequestException exception3 = assertThrows(BadRequestException.class, () -> {
            userService.register(new RegisterRequest("testuser", "password123", ""));
        });
        assertTrue(exception3.getMessage().contains("Email cannot be empty"));
    }
    
    @Test
    public void loginPositive() throws Exception {
        // First we need a user in the system
        RegisterRequest registerRequest = new RegisterRequest("logintest", "password123", "login@example.com");
        userService.register(registerRequest);
        
        // Now try to log in
        LoginRequest loginRequest = new LoginRequest("logintest", "password123");
        LoginResult result = userService.login(loginRequest);
        
        // Check the result
        assertNotNull(result, "Login result shouldn't be null");
        assertEquals("logintest", result.username(), "Username should match");
        assertNotNull(result.authToken(), "Should get a valid auth token");
    }
    
    @Test
    public void loginNegativeWrongPassword() throws Exception {
        // Register a user with a known password
        userService.register(new RegisterRequest("wrongpass", "correctpass", "wrong@example.com"));
        
        // Try to log in with wrong password
        LoginRequest badLogin = new LoginRequest("wrongpass", "wrongpass");
        
        // This should fail!
        var exception = assertThrows(UnauthorizedException.class, () -> {
            userService.login(badLogin);
        });
        
        // Check error message
        String msg = exception.getMessage();
        assertTrue(msg.contains("Invalid username or password"), 
                "Error should mention invalid credentials, but was: " + msg);
    }
    
    @Test
    public void loginNegativeNonexistentUser() {
        // Try logging in as a user that doesn't exist
        LoginRequest request = new LoginRequest("ghost_user", "password123");
        
        // Should fail with auth error
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> {
            userService.login(request);
        });
        
        // Verify error message
        assertTrue(exception.getMessage().contains("Invalid username or password"));
    }
    
    @Test
    public void logoutPositive() throws Exception {
        // Create a user and get their auth token
        RegisterRequest registerRequest = new RegisterRequest("logouttest", "password123", "logout@example.com");
        RegisterResult registerResult = userService.register(registerRequest);
        String token = registerResult.authToken();
        
        // Log them out
        LogoutResult result = userService.logout(new LogoutRequest(token));
        
        // Should work fine
        assertNotNull(result);
        
        // Try using the token again - should fail now
        LogoutRequest secondLogout = new LogoutRequest(token);
        assertThrows(UnauthorizedException.class, () -> {
            userService.logout(secondLogout);
        }, "Second logout with same token should fail");
    }
    
    @Test
    public void logoutNegativeInvalidToken() {
        // Make up a random token
        LogoutRequest request = new LogoutRequest("fake-token-that-doesnt-exist");
        
        // Try to log out with it
        Exception e = assertThrows(UnauthorizedException.class, () -> {
            userService.logout(request);
        });
        
        // Check the error
        String errorMsg = e.getMessage();
        assertTrue(errorMsg.contains("not logged in"), 
                "Error should say not logged in, but was: " + errorMsg);
    }
} 