package request;

/**
 * Request object for creating a game
 */
public record CreateGameRequest(String authToken, String gameName) {
} 