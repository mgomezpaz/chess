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
        // Create a new server
        Server server = new Server();
        // Run the server on port 8080
        server.run(8080);
        
        System.out.println("Server started on port 8080");
    }
}