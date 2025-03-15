package dataaccess;

import model.AuthData;
import model.UserData;
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
        
        // Create a test user for auth tests
        userDAO.createUser(new UserData("testuser", "password", "test@example.com"));
    }

    @Test
    public void testCreateAuth_Success() throws DataAccessException {
        // Arrange
        AuthData auth = new AuthData("testtoken", "testuser");
        
        // Act
        authDAO.createAuth(auth);
        
        // Assert
        AuthData retrievedAuth = authDAO.getAuth("testtoken");
        assertNotNull(retrievedAuth);
        assertEquals("testtoken", retrievedAuth.authToken());
        assertEquals("testuser", retrievedAuth.username());
    }

    @Test
    public void testGetAuth_AuthExists() throws DataAccessException {
        // Arrange
        AuthData auth = new AuthData("testtoken", "testuser");
        authDAO.createAuth(auth);
        
        // Act
        AuthData retrievedAuth = authDAO.getAuth("testtoken");
        
        // Assert
        assertNotNull(retrievedAuth);
        assertEquals("testtoken", retrievedAuth.authToken());
        assertEquals("testuser", retrievedAuth.username());
    }

    @Test
    public void testGetAuth_AuthDoesNotExist() throws DataAccessException {
        // Act
        AuthData retrievedAuth = authDAO.getAuth("nonexistenttoken");
        
        // Assert
        assertNull(retrievedAuth);
    }

    @Test
    public void testDeleteAuth() throws DataAccessException {
        // Arrange
        AuthData auth = new AuthData("testtoken", "testuser");
        authDAO.createAuth(auth);
        
        // Act
        authDAO.deleteAuth("testtoken");
        
        // Assert
        AuthData retrievedAuth = authDAO.getAuth("testtoken");
        assertNull(retrievedAuth);
    }

    @Test
    public void testGetAllAuthData() throws DataAccessException {
        // Arrange
        authDAO.createAuth(new AuthData("token1", "testuser"));
        authDAO.createAuth(new AuthData("token2", "testuser"));
        
        // Act
        List<AuthData> authList = authDAO.getAllAuthData();
        
        // Assert
        assertEquals(2, authList.size());
        assertTrue(authList.stream().anyMatch(a -> a.authToken().equals("token1")));
        assertTrue(authList.stream().anyMatch(a -> a.authToken().equals("token2")));
    }

    @Test
    public void testClear() throws DataAccessException {
        // Arrange
        authDAO.createAuth(new AuthData("testtoken", "testuser"));
        
        // Act
        authDAO.clear();
        
        // Assert
        List<AuthData> authList = authDAO.getAllAuthData();
        assertTrue(authList.isEmpty());
    }
} 