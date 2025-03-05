package server;

import com.google.gson.Gson;
import exception.AlreadyTakenException;
import exception.BadRequestException;
import exception.UnauthorizedException;
import request.*;
import result.*;
import service.ClearService;
import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;

import java.util.Map;

/**
 * Handles all the HTTP requests for our chess server.
 * This is where the HTTP layer meets our service layer.
 */
public class Handler {
    // Services that do the real work
    private final UserService userService;
    private final GameService gameService;
    private final ClearService clearService;
    
    // For JSON conversion
    private final Gson gson;
    
    /**
     * Creates a new handler with all the services it needs
     */
    public Handler() {
        // Create our services
        userService = new UserService();
        gameService = new GameService();
        clearService = new ClearService();
        
        // Set up Gson for JSON conversion
        gson = new Gson();
    }
    
    /**
     * Registers a new user
     */
    public Object register(Request req, Response res) {
        // Always return JSON
        res.type("application/json");
        
        try {
            // Parse the request body into our DTO
            RegisterRequest request = gson.fromJson(req.body(), RegisterRequest.class);
            
            // Let the service handle the registration logic
            RegisterResult result = userService.register(request);
            
            // Convert the result back to JSON and return it
            return gson.toJson(result);
        } catch (BadRequestException e) {
            // Missing required fields
            res.status(400);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        } catch (AlreadyTakenException e) {
            // Username already exists
            res.status(403);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        } catch (Exception e) {
            // Something else went wrong
            res.status(500);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }
    
    /**
     * Logs in an existing user
     */
    public Object login(Request req, Response res) {
        // JSON response
        res.type("application/json");
        
        try {
            // Get login info from the request body
            LoginRequest request = gson.fromJson(req.body(), LoginRequest.class);
            
            // Try to log in
            LoginResult result = userService.login(request);
            
            // Success! Return the auth token
            return gson.toJson(result);
        } catch (UnauthorizedException e) {
            // Bad credentials
            res.status(401);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        } catch (Exception e) {
            // Other error
            res.status(500);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }
    
    /**
     * Logs out a user
     */
    public Object logout(Request req, Response res) {
        res.type("application/json");
        
        try {
            // Auth token comes from the header
            String authToken = req.headers("Authorization");
            
            // Create the request object
            LogoutRequest request = new LogoutRequest(authToken);
            
            // Try to log out
            LogoutResult result = userService.logout(request);
            
            // All good!
            return gson.toJson(result);
        } catch (UnauthorizedException e) {
            // Not logged in or bad token
            res.status(401);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        } catch (Exception e) {
            // Something else broke
            res.status(500);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }
    
    /**
     * Gets a list of all games
     */
    public Object listGames(Request req, Response res) {
        res.type("application/json");
        
        try {
            // Need the auth token from the header
            String authToken = req.headers("Authorization");
            
            // Create the request
            ListGamesRequest request = new ListGamesRequest(authToken);
            
            // Get the games from the service
            ListGamesResult result = gameService.listGames(request);
            
            // Return the list as JSON
            return gson.toJson(result);
        } catch (UnauthorizedException e) {
            // Not logged in
            res.status(401);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        } catch (Exception e) {
            // Other error
            res.status(500);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }
    
    /**
     * Creates a new game
     */
    public Object createGame(Request req, Response res) {
        res.type("application/json");
        
        try {
            // Check for unwrapped body from our middleware
            String requestBody = req.attribute("unwrappedBody");
            if (requestBody == null) {
                requestBody = req.body();
            }
            
            // Get auth token from header
            String authToken = req.headers("Authorization");
            
            // Parse the game name from the body
            CreateGameRequest bodyRequest = gson.fromJson(requestBody, CreateGameRequest.class);
            
            // Build the full request
            CreateGameRequest request = new CreateGameRequest(authToken, bodyRequest.gameName());
            
            // Let the service create the game
            CreateGameResult result = gameService.createGame(request);
            
            // Return the game ID
            return gson.toJson(result);
        } catch (UnauthorizedException e) {
            // Not logged in
            return handleError(res, 401, e.getMessage());
        } catch (BadRequestException e) {
            // Bad game name or format
            return handleError(res, 400, e.getMessage());
        } catch (Exception e) {
            // Something else went wrong
            System.err.println("Error creating game: " + e.getMessage());
            e.printStackTrace();
            return handleError(res, 500, e.getMessage());
        }
    }
    
    /**
     * Joins an existing game
     */
    public Object joinGame(Request req, Response res) {
        res.type("application/json");
        
        try {
            // Get auth token from header
            String authToken = req.headers("Authorization");
            
            // Check for unwrapped body
            String requestBody = req.attribute("unwrappedBody");
            if (requestBody == null) {
                requestBody = req.body();
            }
            
            // Parse the join info
            JoinGameRequest bodyRequest = gson.fromJson(requestBody, JoinGameRequest.class);
            
            // Build the full request
            JoinGameRequest request = new JoinGameRequest(
                authToken, 
                bodyRequest.playerColor(), 
                bodyRequest.gameID()
            );
            
            // Try to join
            JoinGameResult result = gameService.joinGame(request);
            
            // Success!
            return gson.toJson(result);
        } catch (UnauthorizedException e) {
            // Not logged in
            res.status(401);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        } catch (BadRequestException e) {
            // Bad game ID or color
            res.status(400);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        } catch (AlreadyTakenException e) {
            // Color already taken
            res.status(403);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        } catch (Exception e) {
            // Other error
            res.status(500);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }
    
    /**
     * Clears the database - use with caution!
     */
    public Object clear(Request req, Response res) {
        res.type("application/json");
        
        try {
            // No auth needed - this is just for testing
            
            // Delete everything
            ClearResult result = clearService.clear();
            
            // All gone!
            return gson.toJson(result);
        } catch (Exception e) {
            // Something went wrong
            res.status(500);
            return gson.toJson(Map.of("message", "Error: Failed to clear database: " + e.getMessage()));
        }
    }
    
    // Helper for consistent error responses
    private String handleError(Response res, int status, String message) {
        res.status(status);
        res.type("application/json");
        return gson.toJson(Map.of("message", "Error: " + message));
    }
    
    /**
     * Parses request bodies that might be JSON strings
     */
    private <T> T parseRequestBody(String body, Class<T> type) throws BadRequestException {
        try {
            // First try parsing directly
            return gson.fromJson(body, type);
        } catch (Exception e) {
            // If that fails, try unwrapping quotes
            try {
                if (body.startsWith("\"") && body.endsWith("\"")) {
                    body = body.substring(1, body.length() - 1);
                }
                return gson.fromJson(body, type);
            } catch (Exception e2) {
                throw new BadRequestException("Invalid request format: " + body);
            }
        }
    }
} 
