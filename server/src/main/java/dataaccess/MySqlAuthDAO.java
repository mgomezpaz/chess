package dataaccess;

import model.AuthData;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * MySQL implementation of AuthDAO
 */
public class MySqlAuthDAO implements AuthDAO {
    private static MySqlAuthDAO instance;

    private MySqlAuthDAO() {}

    /**
     * Gets the singleton instance
     * @return The singleton instance
     */
    public static synchronized MySqlAuthDAO getInstance() {
        if (instance == null) {
            instance = new MySqlAuthDAO();
        }
        return instance;
    }

    @Override
    public void clear() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "DELETE FROM auth_tokens";
            try (var preparedStatement = conn.prepareStatement(sql)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error clearing auth tokens: " + e.getMessage());
        }
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "INSERT INTO auth_tokens (auth_token, username) VALUES (?, ?)";
            try (var preparedStatement = conn.prepareStatement(sql)) {
                preparedStatement.setString(1, auth.authToken());
                preparedStatement.setString(2, auth.username());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error creating auth token: " + e.getMessage());
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT auth_token, username FROM auth_tokens WHERE auth_token = ?";
            try (var preparedStatement = conn.prepareStatement(sql)) {
                preparedStatement.setString(1, authToken);
                try (var resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return new AuthData(
                            resultSet.getString("auth_token"),
                            resultSet.getString("username")
                        );
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error getting auth token: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "DELETE FROM auth_tokens WHERE auth_token = ?";
            try (var preparedStatement = conn.prepareStatement(sql)) {
                preparedStatement.setString(1, authToken);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting auth token: " + e.getMessage());
        }
    }

    @Override
    public List<AuthData> getAllAuthData() throws DataAccessException {
        List<AuthData> authList = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT auth_token, username FROM auth_tokens";
            try (var preparedStatement = conn.prepareStatement(sql)) {
                try (var resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        authList.add(new AuthData(
                            resultSet.getString("auth_token"),
                            resultSet.getString("username")
                        ));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error getting all auth tokens: " + e.getMessage());
        }
        return authList;
    }
} 