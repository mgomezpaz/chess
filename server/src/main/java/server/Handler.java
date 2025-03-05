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
 * Handles all the HTTP requests for our server
 */
public class Handler {
    // services we need
    private final UserService userService;
    private final GameService gameService;
    private final ClearService clearService;
    
    // for JSON conversion
    private final Gson gson;

    public Handler() {
        // create all the services we'll need
        userService = new UserService();
        gameService = new GameService();
        clearService = new ClearService();
        
        // for converting to/from JSON
        gson = new Gson();
    }

    /**
     * Handles user registration
     */
    public Object register(Request req, Response res) {
        // set response type to JSON
        res.type("application/json");
        
        try {
            // convert JSON to our request object
            RegisterRequest request = gson.fromJson(req.body(), RegisterRequest.class);
            
            // call the service
            RegisterResult result = userService.register(request);
            
            // convert result back to JSON
            return gson.toJson(result);
        } catch (BadRequestException e) {
            // invalid request
            res.status(400);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        } catch (AlreadyTakenException e) {
            // username already exists
            res.status(403);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        } catch (Exception e) {
            // something else went wrong
            res.status(500);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    /**
     * Handles user login
     */
    public Object login(Request req, Response res) {
        // set response type to JSON
        res.type("application/json");
        
        try {
            // parse the request body
            LoginRequest request = gson.fromJson(req.body(), LoginRequest.class);
            
            // try to log in
            LoginResult result = userService.login(request);
            
            // success!
            return gson.toJson(result);
        } catch (UnauthorizedException e) {
            // bad credentials
            res.status(401);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        } catch (Exception e) {
            // other error
            res.status(500);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    /**
     * Handles user logout
     */
    public Object logout(Request req, Response res) {
        // set response type to JSON
        res.type("application/json");
        
        try {
            // get the auth token from the header instead of the body
            String authToken = req.headers("Authorization");
            
            // create request with the token
            LogoutRequest request = new LogoutRequest(authToken);
            
            // try to log out
            LogoutResult result = userService.logout(request);
            
            // success
            return gson.toJson(result);
            
        } catch (UnauthorizedException e) {
            // not logged in or invalid token
            res.status(401);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        } catch (Exception e) {
            // other error
            res.status(500);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    /**
     * Lists all the games
     */
    public Object listGames(Request req, Response res) {
        // set response type to JSON
        res.type("application/json");
        
        try {
            // auth token comes from header
            String authToken = req.headers("Authorization");
            
            // create request with the token
            ListGamesRequest request = new ListGamesRequest(authToken);
            
            // get the games
            ListGamesResult result = gameService.listGames(request);
            
            // return the list
            return gson.toJson(result);
        } catch (UnauthorizedException e) {
            // not logged in
            res.status(401);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        } catch (Exception e) {
            // something broke
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
            // Check if we have an unwrapped body
            String requestBody = req.attribute("unwrappedBody");
            if (requestBody == null) {
                // If not, use the regular body
                requestBody = req.body();
            }
            
            // Parse the body
            CreateGameRequest bodyRequest = gson.fromJson(requestBody, CreateGameRequest.class);
            
            // Rest of your handler code...
        } catch (Exception e) {
            // Error handling...
        }
    }

    /**
     * Joins an existing game
     */
    public Object joinGame(Request req, Response res) {
        // set response type to JSON
        res.type("application/json");
        
        try {
            // need auth token and join info
            String authToken = req.headers("Authorization");
            
            // parse body to get game ID and color
            JoinGameRequest bodyRequest = gson.fromJson(req.body(), JoinGameRequest.class);
            
            // combine into one request
            JoinGameRequest request = new JoinGameRequest(
                authToken, 
                bodyRequest.playerColor(), 
                bodyRequest.gameID()
            );
            
            // try to join
            JoinGameResult result = gameService.joinGame(request);
            
            // success
            return gson.toJson(result);
        } catch (UnauthorizedException e) {
            // not logged in
            res.status(401);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        } catch (BadRequestException e) {
            // bad game ID or color
            res.status(400);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        } catch (AlreadyTakenException e) {
            // color already taken
            res.status(403);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        } catch (Exception e) {
            // other error
            res.status(500);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    /**
     * Clears the database - DANGER ZONE!
     */
    public Object clear(Request req, Response res) {
        // set response type to JSON
        res.type("application/json");
        
        try {
            // no auth needed for this one - it's just for testing
            
            // nuke everything
            ClearResult result = clearService.clear();
            
            // all gone!
            return gson.toJson(result);
        } catch (Exception e) {
            // something went wrong with the clearing
            res.status(500);
            return gson.toJson(Map.of("message", "Error: Failed to clear database: " + e.getMessage()));
        }
    }
    
    // Helper method to handle errors consistently - not used yet but might be useful later
    private String handleError(Response res, int status, String message) {
        res.status(status);
        res.type("application/json");
        return gson.toJson(Map.of("message", "Error: " + message));
    }

    /**
     * Helper method to parse request bodies that might be JSON strings
     */
    private <T> T parseRequestBody(String body, Class<T> type) throws BadRequestException {
        try {
            // First try to parse it directly
            return gson.fromJson(body, type);
        } catch (Exception e) {
            // If that fails, it might be a JSON string that needs to be parsed again
            try {
                // Remove quotes if the string is wrapped in quotes
                if (body.startsWith("\"") && body.endsWith("\"")) {
                    body = body.substring(1, body.length() - 1);
                }
                // Try to parse the unwrapped string
                return gson.fromJson(body, type);
            } catch (Exception e2) {
                throw new BadRequestException("Invalid request format: " + body);
            }
        }
    }
} 
