package dataaccess;

import model.GameData;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Stores chess games in memory
 */
public class MemoryGameDAO implements GameDAO {
    // gameID -> game data
    private final Map<Integer, GameData> games = new HashMap<>();
    // simple counter for game IDs
    private int nextGameID = 1;

    @Override
    public void clear() throws DataAccessException {
        // reset everything
        games.clear();
        nextGameID = 1; // start fresh
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        try {
            // grab ID and increment counter
            int gameID = nextGameID++;
            
            // empty game for now - we'll fill it in later
            String game = "{}"; 
            
            // no players yet
            String whiteUsername = "";
            String blackUsername = "";
            
            // create and store
            GameData gameData = new GameData(gameID, whiteUsername, blackUsername, gameName, game);
            games.put(gameID, gameData);
            
            return gameID;
        } catch (Exception e) {
            // something went wrong
            throw new DataAccessException("Failed to create game: " + e.getMessage());
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        // simple lookup
        return games.get(gameID);
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        // convert values to list
        return new ArrayList<>(games.values());
    }

    @Override
    public void updateGame(int gameID, String username, String playerColor) throws DataAccessException {
        // find the game
        GameData game = games.get(gameID);
        
        if (game == null) {
            throw new DataAccessException("Can't find that game");
        }
        
        // handle different colors
        if (playerColor.equalsIgnoreCase("WHITE")) {
            // can't modify records, so make a new one with updated white player
            GameData updatedGame = new GameData(
                game.gameID(),
                username,  // new white player
                game.blackUsername(),
                game.gameName(),
                game.game()
            );
            games.put(gameID, updatedGame);
        } else if (playerColor.equalsIgnoreCase("BLACK")) {
            // same for black player
            GameData updatedGame = new GameData(
                game.gameID(),
                game.whiteUsername(),
                username,  // new black player
                game.gameName(),
                game.game()
            );
            games.put(gameID, updatedGame);
        } else {
            // not a valid color
            throw new DataAccessException("Color must be WHITE or BLACK");
        }
    }
} 