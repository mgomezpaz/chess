package dataaccess;

import chess.ChessGame;
import chess.ChessGameAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.GameData;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * MySQL implementation of the GameDAO interface
 */
public class MySqlGameDAO implements GameDAO {
    private static MySqlGameDAO instance;
    private final Gson gson;
    
    private MySqlGameDAO() {
        // Create Gson instance with type adapters needed for ChessGame serialization
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(ChessGame.class, new ChessGameAdapter());
        gson = builder.create();
    }
    
    public static synchronized MySqlGameDAO getInstance() {
        if (instance == null) {
            instance = new MySqlGameDAO();
        }
        return instance;
    }

    @Override
    public void clear() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "DELETE FROM games";
            try (var preparedStatement = conn.prepareStatement(sql)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error clearing games: " + e.getMessage());
        }
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            // Create a new chess game with default setup
            ChessGame game = new ChessGame();
            String gameJson = gson.toJson(game);
            
            String sql = "INSERT INTO games (game_name, white_username, black_username, game_data) VALUES (?, NULL, NULL, ?)";
            try (var preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, gameName);
                preparedStatement.setString(2, gameJson);
                
                preparedStatement.executeUpdate();
                
                try (var resultSet = preparedStatement.getGeneratedKeys()) {
                    if (resultSet.next()) {
                        return resultSet.getInt(1);
                    } else {
                        throw new DataAccessException("Failed to get generated game ID");
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error creating game: " + e.getMessage());
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT game_id, white_username, black_username, game_name, game_data FROM games WHERE game_id = ?";
            try (var preparedStatement = conn.prepareStatement(sql)) {
                preparedStatement.setInt(1, gameID);
                
                try (var resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int id = resultSet.getInt("game_id");
                        String whiteUsername = resultSet.getString("white_username");
                        String blackUsername = resultSet.getString("black_username");
                        String gameName = resultSet.getString("game_name");
                        String gameJson = resultSet.getString("game_data");
                        
                        // Deserialize the game data
                        ChessGame game = gson.fromJson(gameJson, ChessGame.class);
                        
                        return new GameData(id, whiteUsername, blackUsername, gameName, game);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error getting game: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<GameData> listGames() throws DataAccessException {
        List<GameData> games = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT game_id, white_username, black_username, game_name, game_data FROM games";
            try (var preparedStatement = conn.prepareStatement(sql)) {
                try (var resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        int id = resultSet.getInt("game_id");
                        String whiteUsername = resultSet.getString("white_username");
                        String blackUsername = resultSet.getString("black_username");
                        String gameName = resultSet.getString("game_name");
                        String gameJson = resultSet.getString("game_data");
                        
                        // Deserialize the game data
                        ChessGame game = gson.fromJson(gameJson, ChessGame.class);
                        
                        games.add(new GameData(id, whiteUsername, blackUsername, gameName, game));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error listing games: " + e.getMessage());
        }
        
        return games;
    }

    @Override
    public void updateGame(int gameID, String username, String playerColor) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            // First get the current game
            GameData currentGame = getGame(gameID);
            if (currentGame == null) {
                throw new DataAccessException("Can't find that game");
            }
            
            // Prepare the SQL based on the player color
            String sql;
            if (playerColor.equalsIgnoreCase("WHITE")) {
                sql = "UPDATE games SET white_username = ? WHERE game_id = ?";
            } else if (playerColor.equalsIgnoreCase("BLACK")) {
                sql = "UPDATE games SET black_username = ? WHERE game_id = ?";
            } else {
                throw new DataAccessException("Color must be WHITE or BLACK");
            }
            
            // Execute the update
            try (var preparedStatement = conn.prepareStatement(sql)) {
                preparedStatement.setString(1, username);
                preparedStatement.setInt(2, gameID);
                
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error updating game: " + e.getMessage());
        }
    }
    
    /**
     * Updates the game state in the database
     * @param gameID the ID of the game to update
     * @param game the updated game state
     * @throws DataAccessException if there is an error updating the game
     */
    public void updateGameState(int gameID, ChessGame game) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String gameJson = gson.toJson(game);
            
            String sql = "UPDATE games SET game_data = ? WHERE game_id = ?";
            try (var preparedStatement = conn.prepareStatement(sql)) {
                preparedStatement.setString(1, gameJson);
                preparedStatement.setInt(2, gameID);
                
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error updating game state: " + e.getMessage());
        }
    }
} 