package dataaccess;

import model.GameData;
import java.util.Collection;
import java.util.List;

/**
 * Interface for Game data access operations
 */
public interface GameDAO {
    /**
     * Clears all game data from the database
     * @throws DataAccessException if an error occurs
     */
    void clear() throws DataAccessException;

    /**
     * Creates a new game in the database
     * @param gameName the name of the game to create
     * @return the ID of the created game
     * @throws DataAccessException if an error occurs
     */
    int createGame(String gameName) throws DataAccessException;

    /**
     * Retrieves a game from the database
     * @param gameID the ID of the game to retrieve
     * @return the game data, or null if not found
     * @throws DataAccessException if an error occurs
     */
    GameData getGame(int gameID) throws DataAccessException;

    /**
     * Lists all games in the database
     * @return a list of all games
     * @throws DataAccessException if an error occurs
     */
    List<GameData> listGames() throws DataAccessException;

    /**
     * Updates a game in the database
     * @param gameID the ID of the game to update
     * @param username the username of the player
     * @param playerColor the color of the player (WHITE or BLACK)
     * @throws DataAccessException if an error occurs
     */
    void updateGame(int gameID, String username, String playerColor) throws DataAccessException;
} 