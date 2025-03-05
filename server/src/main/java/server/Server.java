package server;

import spark.*;

/**
 * The main server class for our chess application.
 * Sets up all the HTTP endpoints and middleware.
 */
public class Server {
    // Our request handler
    private final Handler handler;
    
    /**
     * Creates a new server instance
     */
    public Server() {
        // Create the handler that will process all our requests
        handler = new Handler();
    }
    
    /**
     * Starts the server on the specified port
     */
    public int run(int desiredPort) {
        // Tell Spark which port to use
        Spark.port(desiredPort);
        
        // Set up the directory for static web files
        Spark.staticFiles.location("web");
        
        // Add middleware to handle JSON string unwrapping
        // This helps with some clients that double-encode JSON
        Spark.before((req, res) -> {
            if ((req.requestMethod().equals("POST") || req.requestMethod().equals("PUT")) 
                    && req.contentType() != null 
                    && req.contentType().contains("application/json")) {
                
                String body = req.body();
                // If the body is wrapped in quotes, unwrap it
                if (body.startsWith("\"") && body.endsWith("\"")) {
                    try {
                        String unwrapped = body.substring(1, body.length() - 1)
                                .replace("\\\"", "\"")
                                .replace("\\\\", "\\");
                        
                        // Store it for handlers to use
                        req.attribute("unwrappedBody", unwrapped);
                    } catch (Exception e) {
                        System.err.println("Error unwrapping JSON: " + e.getMessage());
                    }
                }
            }
        });
        
        // Log all requests - helpful for debugging
        Spark.before((req, res) -> {
            System.out.println(req.requestMethod() + " " + req.pathInfo());
            if (req.body() != null && !req.body().isEmpty()) {
                System.out.println("Body: " + req.body());
            }
        });
        
        // Register all our endpoints
        
        // User endpoints
        Spark.post("/user", handler::register);      // Register
        Spark.post("/session", handler::login);      // Login
        Spark.delete("/session", handler::logout);   // Logout
        
        // Game endpoints
        Spark.get("/game", handler::listGames);      // List games
        Spark.post("/game", handler::createGame);    // Create game
        Spark.put("/game", handler::joinGame);       // Join game
        
        // Admin endpoint
        Spark.delete("/db", handler::clear);         // Clear DB
        
        // Global error handler
        Spark.exception(Exception.class, (e, req, res) -> {
            res.status(500);
            res.type("application/json");
            res.body("{\"message\": \"Error: Server error: " + e.getMessage() + "\"}");
            e.printStackTrace();
        });
        
        // Start the server
        Spark.init();
        
        // Wait for server to be ready
        Spark.awaitInitialization();
        
        // Log that we're up and running
        System.out.println("Server started on port " + Spark.port());
        
        return Spark.port();
    }
    
    /**
     * Stops the server
     */
    public void stop() {
        Spark.stop();
        Spark.awaitStop();
        System.out.println("Server stopped");
    }
    
    /**
     * Main method to run the server directly
     */
    public static void main(String[] args) {
        // Default to port 8080
        int port = 8080;
        
        // Use command line arg if provided
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid port number. Using default port 8080.");
            }
        }
        
        // Create and start the server
        Server server = new Server();
        server.run(port);
        
        System.out.println("Chess server running at http://localhost:" + port);
        System.out.println("Press Ctrl+C to stop");
    }
}
