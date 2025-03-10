import server.Server;

/**
 * Main class that starts the server
 */
public class Main {
    /**
     * Main method
     * @param args command line arguments
     */
    public static void main(String[] args) {
        // Default port
        int port = 8082;
        
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
        
        System.out.println("Lets play some chess! Chess server at http://localhost:" + port);
    }
}