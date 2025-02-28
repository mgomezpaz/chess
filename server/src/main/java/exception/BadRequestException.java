package exception;

/**
 * Exception thrown when a request is malformed or invalid
 */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
} 