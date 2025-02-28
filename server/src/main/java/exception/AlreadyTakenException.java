package exception;

/**
 * Exception thrown when a resource is already taken (e.g., username, player color)
 */
public class AlreadyTakenException extends RuntimeException {
    public AlreadyTakenException(String message) {
        super(message);
    }
} 