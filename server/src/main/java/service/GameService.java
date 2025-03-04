package service;

import dataaccess.*;
import exception.AlreadyTakenException;
import exception.BadRequestException;
import exception.UnauthorizedException;
import model.AuthData;
import model.GameData;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.ListGamesRequest;
import result.CreateGameResult;
import result.JoinGameResult;
import result.ListGamesResult;

import java.util.Collection;

/**
 * Handles all game-related operations
 * This includes creating games, joining games, and listing available games
 */
public class GameService {
    // Our data access objects
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService() {
        // Get the singleton instances so we're all using the same data
        this.gameDAO = MemoryGameDAO.getInstance();
        this.authDAO = MemoryAuthDAO.getInstance();
    }

    /**
     * Lists all available games in the system
     */
    public ListGamesResult listGames(ListGamesRequest request) throws DataAccessException, UnauthorizedException {
        // First, make sure the user is authenticated
        AuthData authData = authDAO.getAuth(request.authToken());
        if (authData == null) {
            throw new UnauthorizedException("Please log in first");
        }

        // User is authenticated, so get all the games
        Collection<GameData> games = gameDAO.listGames();
        return new ListGamesResult(games);
    }

    /**
     * Creates a new chess game
     */
    public CreateGameResult createGame(CreateGameRequest request) throws DataAccessException, UnauthorizedException, BadRequestException {
        // Check if the user is logged in
        AuthData authData = authDAO.getAuth(request.authToken());
        if (authData == null) {
            throw new UnauthorizedException("Please log in first");
        }

        // Validate the game name
        if (request.gameName() == null || request.gameName().isEmpty()) {
            throw new BadRequestException("Game needs a name");
        }

        // All good, create the game
        int gameID = gameDAO.createGame(request.gameName());
        return new CreateGameResult(gameID);
    }

    /**
     * Allows a user to join an existing game as either the white or black player
     */
    public JoinGameResult joinGame(JoinGameRequest request) throws DataAccessException, UnauthorizedException, BadRequestException, AlreadyTakenException {
        // First, check if the user is logged in
        AuthData authData = authDAO.getAuth(request.authToken());
        if (authData == null) {
            throw new UnauthorizedException("Please log in first");
        }

        // Validate the game ID
        Integer gameID = request.gameID();
        if (gameID == null) {
            throw new BadRequestException("Game ID cannot be null");
        }
        
        // Make sure the game exists
        GameData gameData = gameDAO.getGame(gameID);
        if (gameData == null) {
            throw new BadRequestException("Game doesn't exist");
        }

        // Get the player's info
        String username = authData.username();
        String color = request.playerColor();
        
        // Validate the color choice
        if (color == null || color.isEmpty()) {
            throw new BadRequestException("Color cannot be empty");
        }
        
        // Handle the different color choices
        if (color.equals("WHITE")) {
            // Check if white is already taken
            if (gameData.whiteUsername() != null && !gameData.whiteUsername().isEmpty()) {
                throw new AlreadyTakenException("White is already taken");
            }
            // Add the player as white
            gameDAO.updateGame(gameID, username, "WHITE");
        } else if (color.equals("BLACK")) {
            // Check if black is already taken
            if (gameData.blackUsername() != null && !gameData.blackUsername().isEmpty()) {
                throw new AlreadyTakenException("Black is already taken");
            }
            // Add the player as black
            gameDAO.updateGame(gameID, username, "BLACK");
        } else {
            // Invalid color specified
            throw new BadRequestException("Color must be WHITE or BLACK");
        }

        // Successfully joined the game
        return new JoinGameResult();
    }
} 