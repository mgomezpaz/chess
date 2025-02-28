package request;

/**
 * Request object for joining a game
 */
public record JoinGameRequest(String authToken, String playerColor, int gameID) {
} 