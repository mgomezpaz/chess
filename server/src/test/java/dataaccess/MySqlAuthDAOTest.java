package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MySqlAuthDAOTest {
    private MySqlAuthDAO authDAO;
    private MySqlUserDAO userDAO;
    
    @BeforeEach
    public void setUp() throws DataAccessException {
        authDAO = MySqlAuthDAO.getInstance();
        userDAO = MySqlUserDAO.getInstance();
        
        // Clear both tables
        authDAO.clear();
        userDAO.clear();
        
        // Create a test user for auth tokens
        userDAO.createUser(new UserData("testuser", "password", "test@example.com"));
    }
    
    @Test
    public void createAuth_success() throws DataAccessException {
        // Arrange
        AuthData authData = new AuthData("testtoken", "testuser");
        
        // Act
        authDAO.createAuth(authData);
        
        // Assert
        AuthData retrievedAuth = authDAO.getAuth("testtoken");
        assertNotNull(retrievedAuth);
        assertEquals("testtoken", retrievedAuth.authToken());
        assertEquals("testuser", retrievedAuth.username());
    }
    
    @Test
    public void getAuth_nonExistentToken() throws DataAccessException {
        // Act
        AuthData authData = authDAO.getAuth("nonexistenttoken");
        
        // Assert
        assertNull(authData);
    }
    
    @Test
    public void deleteAuth_success() throws DataAccessException {
        // Arrange
        AuthData authData = new AuthData("testtoken", "testuser");
        authDAO.createAuth(authData);
        
        // Act
        authDAO.deleteAuth("testtoken");
        
        // Assert
        AuthData retrievedAuth = authDAO.getAuth("testtoken");
        assertNull(retrievedAuth);
    }
    
    @Test
    public void getAllAuthData_success() throws DataAccessException {
        // Arrange
        authDAO.clear();
        AuthData authData1 = new AuthData("token1", "testuser");
        AuthData authData2 = new AuthData("token2", "testuser");
        authDAO.createAuth(authData1);
        authDAO.createAuth(authData2);
        
        // Act
        List<AuthData> authList = authDAO.getAllAuthData();
        
        // Assert
        assertEquals(2, authList.size());
        assertTrue(authList.stream().anyMatch(auth -> auth.authToken().equals("token1")));
        assertTrue(authList.stream().anyMatch(auth -> auth.authToken().equals("token2")));
    }
    
    @Test
    public void clear_success() throws DataAccessException {
        // Arrange
        AuthData authData = new AuthData("testtoken", "testuser");
        authDAO.createAuth(authData);
        
        // Act
        authDAO.clear();
        
        // Assert
        List<AuthData> authList = authDAO.getAllAuthData();
        assertTrue(authList.isEmpty());
    }
} 