package dataaccess;

import model.GameData;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * In-memory implementation of the GameDAO interface
 */
public class MemoryGameDAO implements GameDAO {
    // store games in a map with gameID as the key
    private final Map<Integer, GameData> games = new HashMap<>();
    // keep track of the next game ID
    private int nextGameID = 1;

    @Override
    public void clear() throws DataAccessException {
        // clear the games map
        games.clear();
        // reset the next game ID
        nextGameID = 1;
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        try {
            // create a new game with the next ID
            int gameID = nextGameID++;
            
            // create the game data with empty strings for usernames
            String whiteUsername = "";
            String blackUsername = "";
            String game = "{}"; // empty game for now
            GameData gameData = new GameData(gameID, whiteUsername, blackUsername, gameName, game);
            
            // add it to our map
            games.put(gameID, gameData);
            
            // return the ID
            return gameID;
        } catch (Exception e) {
            throw new DataAccessException("Error creating game: " + e.getMessage());
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        // return the game from the map (might be null)
        return games.get(gameID);
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        // return all the games as a collection
        return new ArrayList<>(games.values());
    }

    @Override
    public void updateGame(int gameID, String username, String playerColor) throws DataAccessException {
        // get the game
        GameData game = games.get(gameID);
        
        // make sure it exists
        if (game == null) {
            throw new DataAccessException("Game not found");
        }
        
        // update the game based on player color
        if (playerColor.equalsIgnoreCase("WHITE")) {
            // create a new game data with the white player set
            GameData updatedGame = new GameData(
                game.gameID(),
                username,
                game.blackUsername(),
                game.gameName(),
                game.game()
            );
            games.put(gameID, updatedGame);
        } else if (playerColor.equalsIgnoreCase("BLACK")) {
            // create a new game data with the black player set
            GameData updatedGame = new GameData(
                game.gameID(),
                game.whiteUsername(),
                username,
                game.gameName(),
                game.game()
            );
            games.put(gameID, updatedGame);
        } else {
            throw new DataAccessException("Invalid player color");
        }
    }
} 