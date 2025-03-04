package service;

import dataaccess.DataAccessException;
import exception.AlreadyTakenException;
import exception.BadRequestException;
import exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.CreateGameRequest;
import request.ListGamesRequest;
import request.RegisterRequest;
import result.ListGamesResult;
import result.RegisterResult;

import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTest {
    
    private ClearService clearService;
    private UserService userService;
    private GameService gameService;
    
    @BeforeEach
    public void setup() {
        // Get fresh service instances
        clearService = new ClearService();
        userService = new UserService();
        gameService = new GameService();
    }
    
    @Test
    public void clearPositive() throws Exception {
        // Let's add some stuff to the database first
        
        // Create a user with a fun username
        var registerRequest = new RegisterRequest("chessmaster", "kingme", "master@chess.com");
        var registerResult = userService.register(registerRequest);
        var token = registerResult.authToken();
        
        // Add a couple of games with creative names
        gameService.createGame(new CreateGameRequest(token, "Checkmate Chronicles"));
        gameService.createGame(new CreateGameRequest(token, "Pawn-tastic Adventure"));
        
        // Make sure our data is actually there
        var listRequest = new ListGamesRequest(token);
        var games = gameService.listGames(listRequest).games();
        
        // Should have 2 games
        assertEquals(2, games.size(), "Should have created 2 games");
        
        // Now for the main event - clear everything!
        var result = clearService.clear();
        
        // Should get a result object
        assertNotNull(result, "Clear result shouldn't be null");
        
        // Now let's verify everything is gone
        
        // 1. Auth token should be invalid now
        var ex1 = assertThrows(
            UnauthorizedException.class,
            () -> gameService.listGames(listRequest),
            "Should get auth error after clearing"
        );
        
        // 2. Should be able to register the same username again
        try {
            var newRegister = userService.register(registerRequest);
            assertNotNull(newRegister);
            assertEquals("chessmaster", newRegister.username());
        } catch (AlreadyTakenException e) {
            fail("Should be able to register same username after clearing, but got: " + e.getMessage());
        }
        
        // 3. Get a fresh token and verify no games exist
        var newToken = userService.register(new RegisterRequest("newuser", "password", "new@example.com")).authToken();
        var newListRequest = new ListGamesRequest(newToken);
        var newGames = gameService.listGames(newListRequest).games();
        
        // Should be empty!
        assertTrue(newGames.isEmpty(), "Game list should be empty after clearing");
    }
} 