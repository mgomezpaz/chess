package dataaccess;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MySqlGameDAOTest {
    private MySqlGameDAO gameDAO;
    private MySqlUserDAO userDAO;
    
    @BeforeEach
    public void setUp() throws DataAccessException {
        gameDAO = MySqlGameDAO.getInstance();
        userDAO = MySqlUserDAO.getInstance();
        
        // Clear tables
        gameDAO.clear();
        
        // We need to clear and recreate users since games reference them
        userDAO.clear();
        userDAO.createUser(new UserData("whiteuser", "password", "white@example.com"));
        userDAO.createUser(new UserData("blackuser", "password", "black@example.com"));
    }
    
    @Test
    public void createGame_success() throws DataAccessException {
        // Act
        int gameId = gameDAO.createGame("Test Game");
        
        // Assert
        assertTrue(gameId > 0);
        GameData game = gameDAO.getGame(gameId);
        assertNotNull(game);
        assertEquals("Test Game", game.gameName());
        assertNotNull(game.game());
    }
    
    @Test
    public void getGame_nonExistentGame() throws DataAccessException {
        // Act
        GameData game = gameDAO.getGame(999);
        
        // Assert
        assertNull(game);
    }
    
    @Test
    public void listGames_success() throws DataAccessException {
        // Arrange
        gameDAO.createGame("Game 1");
        gameDAO.createGame("Game 2");
        
        // Act
        List<GameData> games = gameDAO.listGames();
        
        // Assert
        assertEquals(2, games.size());
        assertTrue(games.stream().anyMatch(game -> game.gameName().equals("Game 1")));
        assertTrue(games.stream().anyMatch(game -> game.gameName().equals("Game 2")));
    }
    
    @Test
    public void updateGame_success() throws DataAccessException {
        // Arrange
        int gameId = gameDAO.createGame("Test Game");
        
        // Act
        gameDAO.updateGame(gameId, "whiteuser", "WHITE");
        
        // Assert
        GameData game = gameDAO.getGame(gameId);
        assertEquals("whiteuser", game.whiteUsername());
        assertNull(game.blackUsername());
    }
    
    @Test
    public void updateGame_nonExistentGame() {
        // Act & Assert
        assertThrows(DataAccessException.class, () -> gameDAO.updateGame(999, "whiteuser", "WHITE"));
    }
    
    @Test
    public void updateGame_invalidColor() throws DataAccessException {
        // Arrange
        int gameId = gameDAO.createGame("Test Game");
        
        // Act & Assert
        assertThrows(DataAccessException.class, () -> gameDAO.updateGame(gameId, "whiteuser", "INVALID"));
    }
    
    @Test
    public void updateGameState_success() throws DataAccessException {
        // Arrange
        int gameId = gameDAO.createGame("Test Game");
        ChessGame game = new ChessGame();
        game.setTeamTurn(ChessGame.TeamColor.BLACK); // Change from default WHITE
        
        // Act
        gameDAO.updateGameState(gameId, game);
        
        // Assert
        GameData updatedGame = gameDAO.getGame(gameId);
        assertEquals(ChessGame.TeamColor.BLACK, updatedGame.game().getTeamTurn());
    }
    
    @Test
    public void clear_success() throws DataAccessException {
        // Arrange
        gameDAO.createGame("Test Game");
        
        // Act
        gameDAO.clear();
        
        // Assert
        List<GameData> games = gameDAO.listGames();
        assertTrue(games.isEmpty());
    }
} 