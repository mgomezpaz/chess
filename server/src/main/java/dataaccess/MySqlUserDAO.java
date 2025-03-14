package dataaccess;

import model.UserData;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

            // Hash the password
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
     * Simple password hashing using SHA-256
     */
    private String hashPassword(String password) throws DataAccessException {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new DataAccessException("Error hashing password: " + e.getMessage());
        }
    }
    
    /**
     * Verify a password against its hash
     */
    public boolean verifyPassword(String password, String hash) throws DataAccessException {
        String hashedInput = hashPassword(password);
        return hashedInput.equals(hash);
    }
}