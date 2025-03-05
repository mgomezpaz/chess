package service;

import dataaccess.DataAccessException;
import exception.AlreadyTakenException;
import exception.BadRequestException;
import exception.UnauthorizedException;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.ListGamesRequest;
import request.RegisterRequest;
import result.CreateGameResult;
import result.JoinGameResult;
import result.ListGamesResult;
import result.RegisterResult;
import dataaccess.*;
import chess.ChessGame;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {
    
    private GameService gameService;
    private UserService userService;
    private ClearService clearService;
    private String authToken;
    
    @BeforeEach
    public void setup() throws Exception {
        // Get DAO instances
        UserDAO userDAO = MemoryUserDAO.getInstance();
        AuthDAO authDAO = MemoryAuthDAO.getInstance();
        GameDAO gameDAO = MemoryGameDAO.getInstance();
        
        // Fresh instances for each test
        gameService = new GameService(gameDAO, authDAO);
        userService = new UserService(userDAO, authDAO);
        clearService = new ClearService(userDAO, authDAO, gameDAO);
        
        // Start with a clean slate
        clearService.clear();
        
        // Create a test player we can use
        var registerRequest = new RegisterRequest("testplayer", "password", "test@example.com");
        var registerResult = userService.register(registerRequest);
        authToken = registerResult.authToken();
    }
    
    @Test
    public void listGamesPositive() throws Exception {
        // Let's create a couple of games with fun names
        gameService.createGame(new CreateGameRequest(authToken, "Chess Ninjas"));
        gameService.createGame(new CreateGameRequest(authToken, "Pawn Stars"));
        
        // Now get the list
        var result = gameService.listGames(new ListGamesRequest(authToken));
        
        // Check that we got something back
        assertNotNull(result, "Result shouldn't be null");
        var games = result.games();
        assertNotNull(games, "Games collection shouldn't be null");
        
        // Should have exactly 2 games
        assertEquals(2, games.size(), "Should have exactly 2 games");
        
        // Make sure our game names are in there
        boolean foundNinjas = false;
        boolean foundPawnStars = false;
        
        for (var game : games) {
            if (game.gameName().equals("Chess Ninjas")) foundNinjas = true;
            if (game.gameName().equals("Pawn Stars")) foundPawnStars = true;
        }
        
        // Did we find both games?
        assertTrue(foundNinjas, "Couldn't find the Chess Ninjas game!");
        assertTrue(foundPawnStars, "Couldn't find the Pawn Stars game!");
    }
    
    @Test
    public void listGamesNegativeInvalidAuth() {
        // Try with a made-up auth token
        var request = new ListGamesRequest("bogus-token");
        
        // This should blow up with an auth error
        var exception = assertThrows(
            UnauthorizedException.class, 
            () -> gameService.listGames(request)
        );
        
        // Check the error message
        assertTrue(exception.getMessage().contains("log in"), 
                "Error should tell user to log in");
    }
    
    @Test
    public void createGamePositive() throws Exception {
        // Create a game with a clever name
        var request = new CreateGameRequest(authToken, "Knight Moves");
        var result = gameService.createGame(request);
        
        // Make sure we got a result
        assertNotNull(result, "Result shouldn't be null");
        
        // Game ID should be positive
        int gameID = result.gameID();
        assertTrue(gameID > 0, "Game ID should be positive, got: " + gameID);
        
        // Let's verify the game exists by listing games
        var listResult = gameService.listGames(new ListGamesRequest(authToken));
        
        // Find our game in the list
        boolean foundGame = false;
        for (GameData game : listResult.games()) {
            if (game.gameID() == gameID && "Knight Moves".equals(game.gameName())) {
                foundGame = true;
                break;
            }
        }
        
        // Did we find it?
        assertTrue(foundGame, "Our new game should be in the list");
    }
    
    @Test
    public void createGameNegativeInvalidAuth() {
        // Try with a fake auth token
        var request = new CreateGameRequest("not-a-real-token", "Doomed Game");
        
        // This should fail
        var exception = assertThrows(
            UnauthorizedException.class,
            () -> gameService.createGame(request)
        );
        
        // Check error message
        var msg = exception.getMessage();
        assertTrue(msg.contains("log in"), 
                "Error should mention logging in, but was: " + msg);
    }
    
    @Test
    public void createGameNegativeEmptyName() {
        // Try to create a game with no name
        var request = new CreateGameRequest(authToken, "");
        
        // This should fail with a bad request
        BadRequestException exception = assertThrows(
            BadRequestException.class,
            () -> gameService.createGame(request)
        );
        
        // Verify error message
        assertTrue(exception.getMessage().contains("Game needs a name"));
    }
    
    @Test
    public void joinGamePositive() throws Exception {
        // First create a game to join
        var createResult = gameService.createGame(
            new CreateGameRequest(authToken, "Join Me")
        );
        int gameID = createResult.gameID();
        
        // Now join as white
        var joinRequest = new JoinGameRequest(authToken, "WHITE", gameID);
        var result = gameService.joinGame(joinRequest);
        
        // Should get a result
        assertNotNull(result);
        
        // Check that we're now listed as the white player
        var listResult = gameService.listGames(new ListGamesRequest(authToken));
        
        GameData ourGame = null;
        for (var game : listResult.games()) {
            if (game.gameID() == gameID) {
                ourGame = game;
                break;
            }
        }
        
        // Make sure we found the game
        assertNotNull(ourGame, "Couldn't find our game in the list");
        
        // Check that we're the white player
        assertEquals("testplayer", ourGame.whiteUsername(), 
                "We should be the white player");
    }
    
    @Test
    public void joinGameNegativeInvalidAuth() throws Exception {
        // Create a game first
        var createResult = gameService.createGame(
            new CreateGameRequest(authToken, "Can't Touch This")
        );
        int gameID = createResult.gameID();
        
        // Try to join with a bad token
        var joinRequest = new JoinGameRequest("fake-token", "WHITE", gameID);
        
        // Should get an auth error
        var exception = assertThrows(
            UnauthorizedException.class,
            () -> gameService.joinGame(joinRequest)
        );
        
        // Check the message
        assertTrue(exception.getMessage().contains("log in"));
    }
    
    @Test
    public void joinGameNegativeInvalidGame() {
        // Try to join a game that doesn't exist
        var joinRequest = new JoinGameRequest(authToken, "WHITE", 12345);
        
        // Should fail with a bad request
        var exception = assertThrows(
            BadRequestException.class,
            () -> gameService.joinGame(joinRequest)
        );
        
        // Check error message
        assertTrue(exception.getMessage().contains("doesn't exist"));
    }
    
    @Test
    public void joinGameNegativeColorTaken() throws Exception {
        // Create a game
        var createResult = gameService.createGame(
            new CreateGameRequest(authToken, "No Room Left")
        );
        int gameID = createResult.gameID();
        
        // Join as white
        gameService.joinGame(new JoinGameRequest(authToken, "WHITE", gameID));
        
        // Try to join as white again - should fail
        var exception = assertThrows(
            AlreadyTakenException.class,
            () -> gameService.joinGame(new JoinGameRequest(authToken, "WHITE", gameID))
        );
        
        // Check error
        assertTrue(exception.getMessage().contains("already taken"));
    }
    
    @Test
    public void joinGameNegativeInvalidColor() throws Exception {
        // Create a game
        var createResult = gameService.createGame(
            new CreateGameRequest(authToken, "Bad Color")
        );
        int gameID = createResult.gameID();
        
        // Try to join with an invalid color
        var exception = assertThrows(
            BadRequestException.class,
            () -> gameService.joinGame(new JoinGameRequest(authToken, "GREEN", gameID))
        );
        
        // Check error
        assertTrue(exception.getMessage().contains("WHITE or BLACK"));
    }
} 