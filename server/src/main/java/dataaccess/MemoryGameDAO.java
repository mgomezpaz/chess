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
    private static MemoryGameDAO instance;
    private final Map<Integer, GameData> games = new HashMap<>();
    private int nextGameID = 1;
    
    private MemoryGameDAO() {}
    
    public static synchronized MemoryGameDAO getInstance() {
        if (instance == null) {
            instance = new MemoryGameDAO();
        }
        return instance;
    }

    @Override
    public void clear() throws DataAccessException {
        games.clear();
        nextGameID = 1;
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        try {
            int gameID = nextGameID++;
            
            String game = "{}"; 
            String whiteUsername = null;
            String blackUsername = null;
            
            GameData gameData = new GameData(gameID, whiteUsername, blackUsername, gameName, game);
            games.put(gameID, gameData);
            
            return gameID;
        } catch (Exception e) {
            throw new DataAccessException("Failed to create game: " + e.getMessage());
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return games.get(gameID);
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return new ArrayList<>(games.values());
    }

    @Override
    public void updateGame(int gameID, String username, String playerColor) throws DataAccessException {
        GameData game = games.get(gameID);
        
        if (game == null) {
            throw new DataAccessException("Can't find that game");
        }
        
        if (playerColor.equalsIgnoreCase("WHITE")) {
            GameData updatedGame = new GameData(
                game.gameID(),
                username,
                game.blackUsername(),
                game.gameName(),
                game.game()
            );
            games.put(gameID, updatedGame);
        } else if (playerColor.equalsIgnoreCase("BLACK")) {
            GameData updatedGame = new GameData(
                game.gameID(),
                game.whiteUsername(),
                username,
                game.gameName(),
                game.game()
            );
            games.put(gameID, updatedGame);
        } else {
            throw new DataAccessException("Color must be WHITE or BLACK");
        }
    }
} 