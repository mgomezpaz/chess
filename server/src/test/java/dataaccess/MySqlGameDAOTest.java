package dataaccess;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MySqlGameDAOTest {
    private MySqlGameDAO gameDAO;

    @BeforeEach
    public void setUp() throws DataAccessException, SQLException {
        gameDAO = MySqlGameDAO.getInstance();
        
        // Drop and recreate the games table directly
        try (Connection conn = DatabaseManager.getConnection()) {
            // Drop the table
            try (var statement = conn.prepareStatement("DROP TABLE IF EXISTS games")) {
                statement.executeUpdate();
            }
            
            // Create the table with the correct schema - using the same column names as in DatabaseManager
            String createGamesTable = """
                CREATE TABLE IF NOT EXISTS games (
                    game_id INT NOT NULL AUTO_INCREMENT,
                    white_username VARCHAR(255),
                    black_username VARCHAR(255),
                    game_name VARCHAR(255) NOT NULL,
                    game_data TEXT NOT NULL,
                    PRIMARY KEY (game_id)
                )
            """;
            
            try (var statement = conn.prepareStatement(createGamesTable)) {
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error setting up test: " + e.getMessage());
        }
    }

    @Test
    public void testCreateGame_Success() throws DataAccessException {
        // Act
        int gameID = gameDAO.createGame("Test Game");
        
        // Assert
        assertTrue(gameID > 0);
        GameData game = gameDAO.getGame(gameID);
        assertNotNull(game);
        assertEquals("Test Game", game.gameName());
        assertNull(game.whiteUsername());
        assertNull(game.blackUsername());
        assertNotNull(game.game());
    }

    @Test
    public void testGetGame_GameExists() throws DataAccessException {
        // Arrange
        int gameID = gameDAO.createGame("Test Game");
        
        // Act
        GameData game = gameDAO.getGame(gameID);
        
        // Assert
        assertNotNull(game);
        assertEquals(gameID, game.gameID());
        assertEquals("Test Game", game.gameName());
    }

    @Test
    public void testGetGame_GameDoesNotExist() throws DataAccessException {
        // Act
        GameData game = gameDAO.getGame(999);
        
        // Assert
        assertNull(game);
    }

    @Test
    public void testListGames_Empty() throws DataAccessException {
        // Act
        List<GameData> games = gameDAO.listGames();
        
        // Assert
        assertTrue(games.isEmpty());
    }

    @Test
    public void testListGames_MultipleGames() throws DataAccessException {
        // Arrange
        int gameID1 = gameDAO.createGame("Game 1");
        int gameID2 = gameDAO.createGame("Game 2");
        
        // Act
        List<GameData> games = gameDAO.listGames();
        
        // Assert
        assertEquals(2, games.size());
        assertTrue(games.stream().anyMatch(g -> g.gameID() == gameID1));
        assertTrue(games.stream().anyMatch(g -> g.gameID() == gameID2));
    }

    @Test
    public void testUpdateGame_White() throws DataAccessException {
        // Arrange
        int gameID = gameDAO.createGame("Test Game");
        
        // Act
        gameDAO.updateGame(gameID, "testuser", "WHITE");
        
        // Assert
        GameData game = gameDAO.getGame(gameID);
        assertEquals("testuser", game.whiteUsername());
        assertNull(game.blackUsername());
    }

    @Test
    public void testUpdateGame_Black() throws DataAccessException {
        // Arrange
        int gameID = gameDAO.createGame("Test Game");
        
        // Act
        gameDAO.updateGame(gameID, "testuser", "BLACK");
        
        // Assert
        GameData game = gameDAO.getGame(gameID);
        assertNull(game.whiteUsername());
        assertEquals("testuser", game.blackUsername());
    }

    @Test
    public void testUpdateGame_InvalidColor() {
        // Arrange
        int gameID = 0;
        try {
            gameID = gameDAO.createGame("Test Game");
        } catch (DataAccessException e) {
            fail("Should not throw exception during setup");
        }
        
        // Act & Assert
        final int finalGameID = gameID;
        DataAccessException exception = assertThrows(DataAccessException.class, 
            () -> gameDAO.updateGame(finalGameID, "testuser", "PURPLE"));
        assertTrue(exception.getMessage().contains("Color must be WHITE or BLACK"));
    }

    @Test
    public void testClear() throws DataAccessException {
        // Arrange
        gameDAO.createGame("Test Game");
        
        // Act
        gameDAO.clear();
        
        // Assert
        List<GameData> games = gameDAO.listGames();
        assertTrue(games.isEmpty());
    }
} 