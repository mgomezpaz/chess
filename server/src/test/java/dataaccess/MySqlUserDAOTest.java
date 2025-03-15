package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MySqlUserDAOTest {
    private MySqlUserDAO userDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        userDAO = MySqlUserDAO.getInstance();
        userDAO.clear();
    }

    @Test
    public void testCreateUser_Success() throws DataAccessException {
        // Arrange
        UserData user = new UserData("testuser", "password", "test@example.com");
        
        // Act
        userDAO.createUser(user);
        
        // Assert
        UserData retrievedUser = userDAO.getUser("testuser");
        assertNotNull(retrievedUser);
        assertEquals("testuser", retrievedUser.username());
        assertTrue(((MySqlUserDAO)userDAO).verifyPassword("password", retrievedUser.password()));
        assertEquals("test@example.com", retrievedUser.email());
    }

    @Test
    public void testCreateUser_DuplicateUsername() {
        // Arrange
        UserData user = new UserData("testuser", "password", "test@example.com");
        
        // Act & Assert
        assertDoesNotThrow(() -> userDAO.createUser(user));
        DataAccessException exception = assertThrows(DataAccessException.class, 
            () -> userDAO.createUser(user));
        assertTrue(exception.getMessage().contains("Username already exists"));
    }

    @Test
    public void testGetUser_UserExists() throws DataAccessException {
        // Arrange
        UserData user = new UserData("testuser", "password", "test@example.com");
        userDAO.createUser(user);
        
        // Act
        UserData retrievedUser = userDAO.getUser("testuser");
        
        // Assert
        assertNotNull(retrievedUser);
        assertEquals("testuser", retrievedUser.username());
    }

    @Test
    public void testGetUser_UserDoesNotExist() throws DataAccessException {
        // Act
        UserData retrievedUser = userDAO.getUser("nonexistentuser");
        
        // Assert
        assertNull(retrievedUser);
    }

    @Test
    public void testClear() throws DataAccessException {
        // Arrange
        UserData user = new UserData("testuser", "password", "test@example.com");
        userDAO.createUser(user);
        
        // Act
        userDAO.clear();
        
        // Assert
        UserData retrievedUser = userDAO.getUser("testuser");
        assertNull(retrievedUser);
    }
} 