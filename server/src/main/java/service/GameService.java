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
 * Handles all game-related stuff
 */
public class GameService {
    // data access
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService() {
        // memory implementations for now
        this.gameDAO = new MemoryGameDAO();
        this.authDAO = new MemoryAuthDAO();
    }

    /**
     * Gets all the games
     */
    public ListGamesResult listGames(ListGamesRequest request) throws DataAccessException, UnauthorizedException {
        // check if user is logged in
        AuthData authData = authDAO.getAuth(request.authToken());
        if (authData == null) {
            throw new UnauthorizedException("Please log in first");
        }

        // grab all games
        Collection<GameData> games = gameDAO.listGames();
        return new ListGamesResult(games);
    }

    /**
     * Makes a new game
     */
    public CreateGameResult createGame(CreateGameRequest request) throws DataAccessException, UnauthorizedException, BadRequestException {
        // check if user is logged in
        AuthData authData = authDAO.getAuth(request.authToken());
        if (authData == null) {
            throw new UnauthorizedException("Please log in first");
        }

        // make sure we have a name
        if (request.gameName() == null || request.gameName().isEmpty()) {
            throw new BadRequestException("Game needs a name");
        }

        // create it
        int gameID = gameDAO.createGame(request.gameName());
        return new CreateGameResult(gameID);
    }

    /**
     * Adds a player to a game
     */
    public JoinGameResult joinGame(JoinGameRequest request) throws DataAccessException, UnauthorizedException, BadRequestException, AlreadyTakenException {
        // check if user is logged in
        AuthData authData = authDAO.getAuth(request.authToken());
        if (authData == null) {
            throw new UnauthorizedException("Please log in first");
        }

        // make sure game exists
        Integer gameID = request.gameID();
        if (gameID == null) {
            throw new BadRequestException("Game ID cannot be null");
        }
        
        GameData gameData = gameDAO.getGame(gameID);
        if (gameData == null) {
            throw new BadRequestException("Game doesn't exist");
        }

        // get player info
        String username = authData.username();
        String color = request.playerColor();
        
        // handle color choice
        if (color == null || color.isEmpty()) {
            throw new BadRequestException("Color cannot be empty");
        }
        
        if (color.equals("WHITE")) {
            // check if white is taken
            if (gameData.whiteUsername() != null && !gameData.whiteUsername().isEmpty()) {
                throw new AlreadyTakenException("White is already taken");
            }
            // add as white
            gameDAO.updateGame(gameID, username, "WHITE");
        } else if (color.equals("BLACK")) {
            // check if black is taken
            if (gameData.blackUsername() != null && !gameData.blackUsername().isEmpty()) {
                throw new AlreadyTakenException("Black is already taken");
            }
            // add as black
            gameDAO.updateGame(gameID, username, "BLACK");
        } else {
            // invalid color
            throw new BadRequestException("Color must be WHITE or BLACK");
        }

        // success
        return new JoinGameResult();
    }
} 