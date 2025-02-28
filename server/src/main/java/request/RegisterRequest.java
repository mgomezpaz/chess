package request;

/**
 * Request object for user registration
 */
public record RegisterRequest(String username, String password, String email) {
} 