package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import model.GameData;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Type;

/**
 * MySQL implementation of GameDAO
 */
public class MySqlGameDAO implements GameDAO {
    private static MySqlGameDAO instance;
    private final Gson gson;

    private MySqlGameDAO() {
        // Create a simple Gson instance without custom adapters
        gson = new GsonBuilder().create();
    }

    /**
     * Gets the singleton instance
     * @return The singleton instance
     */
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
            // Make a new game with default board setup
            ChessGame game = new ChessGame();
            String gameJson = gson.toJson(game);

            // Maybe add validation for game name?
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
                        String gameJson = resultSet.getString("game_data");
                        ChessGame game = gson.fromJson(gameJson, ChessGame.class);
                        
                        return new GameData(
                            resultSet.getInt("game_id"),
                            resultSet.getString("white_username"),
                            resultSet.getString("black_username"),
                            resultSet.getString("game_name"),
                            game
                        );
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
                        String gameJson = resultSet.getString("game_data");
                        ChessGame game = gson.fromJson(gameJson, ChessGame.class);
                        
                        games.add(new GameData(
                            resultSet.getInt("game_id"),
                            resultSet.getString("white_username"),
                            resultSet.getString("black_username"),
                            resultSet.getString("game_name"),
                            game
                        ));
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

            String sql;
            if (playerColor.equalsIgnoreCase("WHITE")) {
                sql = "UPDATE games SET white_username = ? WHERE game_id = ?";
            } else if (playerColor.equalsIgnoreCase("BLACK")) {
                sql = "UPDATE games SET black_username = ? WHERE game_id = ?";
            } else {
                throw new DataAccessException("Color must be WHITE or BLACK");
            }

            try (var preparedStatement = conn.prepareStatement(sql)) {
                preparedStatement.setString(1, username);
                preparedStatement.setInt(2, gameID);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error updating game: " + e.getMessage());
        }
    }

    // Type adapter for ChessGame
    private static class ChessGameAdapter implements JsonSerializer<ChessGame>, JsonDeserializer<ChessGame> {
        @Override
        public JsonElement serialize(ChessGame src, Type typeOfSrc, JsonSerializationContext context) {
            return context.serialize(src);
        }

        @Override
        public ChessGame deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            return context.deserialize(json, ChessGame.class);
        }
    }
} 