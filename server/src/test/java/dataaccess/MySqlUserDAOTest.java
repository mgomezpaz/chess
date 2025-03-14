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
    public void createUser_success() throws DataAccessException {
        // Arrange
        UserData user = new UserData("testuser", "password123", "test@example.com");
        
        // Act
        userDAO.createUser(user);
        
        // Assert
        UserData retrievedUser = userDAO.getUser("testuser");
        assertNotNull(retrievedUser);
        assertEquals("testuser", retrievedUser.username());
        assertEquals("test@example.com", retrievedUser.email());
        // Password should be hashed, so we can't directly compare it
    }
    
    @Test
    public void createUser_duplicateUsername() throws DataAccessException {
        // Arrange
        UserData user1 = new UserData("testuser", "password123", "test@example.com");
        UserData user2 = new UserData("testuser", "differentpassword", "different@example.com");
        
        // Act & Assert
        userDAO.createUser(user1);
        assertThrows(DataAccessException.class, () -> userDAO.createUser(user2));
    }
    
    @Test
    public void getUser_nonExistentUser() throws DataAccessException {
        // Act
        UserData user = userDAO.getUser("nonexistentuser");
        
        // Assert
        assertNull(user);
    }
    
    @Test
    public void verifyPassword_correctPassword() throws DataAccessException {
        // Arrange
        UserData user = new UserData("testuser", "password123", "test@example.com");
        userDAO.createUser(user);
        
        // Act
        boolean result = userDAO.verifyPassword("testuser", "password123");
        
        // Assert
        assertTrue(result);
    }
    
    @Test
    public void verifyPassword_incorrectPassword() throws DataAccessException {
        // Arrange
        UserData user = new UserData("testuser", "password123", "test@example.com");
        userDAO.createUser(user);
        
        // Act
        boolean result = userDAO.verifyPassword("testuser", "wrongpassword");
        
        // Assert
        assertFalse(result);
    }
    
    @Test
    public void clear_success() throws DataAccessException {
        // Arrange
        UserData user = new UserData("testuser", "password123", "test@example.com");
        userDAO.createUser(user);
        
        // Act
        userDAO.clear();
        
        // Assert
        UserData retrievedUser = userDAO.getUser("testuser");
        assertNull(retrievedUser);
    }
} 