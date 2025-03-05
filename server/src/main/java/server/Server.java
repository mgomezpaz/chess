package server;

import spark.*;

/**
 * This class manages the web server and registers all endpoints for the chess application.
 */
public class Server {
    private final Handler handler;

    /**
     * Creates a new server instance and initializes the handler.
     */
    public Server() {
        // Create a new handler for all the endpoints
        this.handler = new Handler();
    }

    /**
     * Runs the server on the specified port.
     * 
     * @param desiredPort The port to run the server on
     * @return The actual port the server is running on
     */
    public int run(int desiredPort) {
        // Set the port
        Spark.port(desiredPort);

        // Configure static files location for web interface
        Spark.staticFiles.location("web");

        // Create a filter that wraps the original request with a custom implementation
        Spark.before((request, response) -> {
            if ((request.requestMethod().equals("POST") || request.requestMethod().equals("PUT")) 
                    && request.contentType() != null 
                    && request.contentType().contains("application/json")) {
                
                String body = request.body();
                if (body.startsWith("\"") && body.endsWith("\"")) {
                    try {
                        // Unwrap the JSON string
                        String unwrapped = body.substring(1, body.length() - 1)
                                .replace("\\\"", "\"")
                                .replace("\\\\", "\\");
                        
                        // Store the unwrapped body for later use
                        request.attribute("unwrappedBody", unwrapped);
                    } catch (Exception e) {
                        System.err.println("Error unwrapping JSON string: " + e.getMessage());
                    }
                }
            }
        });

        // Add debug logging for development (comment out for production)
        Spark.before((req, res) -> {
            System.out.println("Received " + req.requestMethod() + " request to " + req.pathInfo());
            if (req.contentType() != null) {
                System.out.println("Content-Type: " + req.contentType());
            }
            if (req.body() != null && !req.body().isEmpty()) {
                System.out.println("Body: " + req.body());
            }
        });

        // Register all the endpoints for the chess server
        
        // User management endpoints
        Spark.post("/user", handler::register);      // Register a new user
        Spark.post("/session", handler::login);      // Login
        Spark.delete("/session", handler::logout);   // Logout
        
        // Game management endpoints
        Spark.get("/game", handler::listGames);      // List all games
        Spark.post("/game", handler::createGame);    // Create a new game
        Spark.put("/game", handler::joinGame);       // Join an existing game
        
        // Database management endpoint (for testing)
        Spark.delete("/db", handler::clear);         // Clear the database
        
        // Add exception handling for internal server errors
        Spark.exception(Exception.class, (e, req, res) -> {
            res.status(500);
            res.type("application/json");
            res.body("{\"message\": \"Error: Internal server error: " + e.getMessage() + "\"}");
            e.printStackTrace();
        });

        // Initialize the server
        Spark.init();

        // Wait until the server is fully initialized
        Spark.awaitInitialization();
        
        // Log that the server has started
        System.out.println("Server started on port " + Spark.port());
        
        // Return the actual port the server is running on
        return Spark.port();
    }

    /**
     * Stops the server.
     */
    public void stop() {
        // Shut down the server
        Spark.stop();
        
        // Wait until the server has fully stopped
        Spark.awaitStop();
        
        System.out.println("Server stopped");
    }
    
    /**
     * Main method to start the server directly.
     * This can be used for testing or running the server standalone.
     */
    public static void main(String[] args) {
        // Default port
        int port = 8080;
        
        // Use command line argument for port if provided
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number. Using default port 8080.");
            }
        }
        
        // Create and start the server
        Server server = new Server();
        server.run(port);
        
        System.out.println("Chess server running at http://localhost:" + port);
        System.out.println("Press Ctrl+C to stop the server");
    }
}
