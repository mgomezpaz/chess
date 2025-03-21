package dataaccess;

import model.UserData;
import util.SimpleBCrypt;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * MySQL implementation of UserDAO
 */
public class MySqlUserDAO implements UserDAO {
    private static MySqlUserDAO instance;

    private MySqlUserDAO() {}

    /**
     * Gets the singleton instance
     * @return The singleton instance
     */
    public static synchronized MySqlUserDAO getInstance() {
        if (instance == null) {
            instance = new MySqlUserDAO();
        }
        return instance;
    }

    @Override
    public void clear() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "DELETE FROM users";
            try (var preparedStatement = conn.prepareStatement(sql)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error clearing users: " + e.getMessage());
        }
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            // Check if user already exists
            if (getUser(user.username()) != null) {
                throw new DataAccessException("Username already exists");
            }

            // Hash the password using our SimpleBCrypt
            String hashedPassword = hashPassword(user.password());

            // Insert the user
            String sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
            try (var preparedStatement = conn.prepareStatement(sql)) {
                preparedStatement.setString(1, user.username());
                preparedStatement.setString(2, hashedPassword);
                preparedStatement.setString(3, user.email());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error creating user: " + e.getMessage());
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT username, password, email FROM users WHERE username = ?";
            try (var preparedStatement = conn.prepareStatement(sql)) {
                preparedStatement.setString(1, username);
                try (var resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return new UserData(
                            resultSet.getString("username"),
                            resultSet.getString("password"),
                            resultSet.getString("email")
                        );
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error getting user: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Hash a password using our SimpleBCrypt implementation
     */
    private String hashPassword(String password) {
        // Generate a salt and hash the password
        String salt = SimpleBCrypt.gensalt();
        return SimpleBCrypt.hashpw(password, salt);
    }
    
    /**
     * Verify a password against its hash
     */
    public boolean verifyPassword(String password, String hash) {
        try {
            // Use our SimpleBCrypt to check if the password matches the hash
            return SimpleBCrypt.checkpw(password, hash);
        } catch (Exception e) {
            // If verification fails for any reason, return false
            return false;
        }
    }
}