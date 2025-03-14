package dataaccess;

import java.sql.*;
import java.util.Properties;
import java.io.IOException;
import java.io.InputStream;

/**
 * Database connection manager
 */
public class DatabaseManager {
    private static final String DATABASE_NAME;
    private static final String USER;
    private static final String PASSWORD;
    private static final String CONNECTION_URL;

    static {
        try (InputStream input = DatabaseManager.class.getClassLoader().getResourceAsStream("db.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            
            DATABASE_NAME = prop.getProperty("db.name");
            USER = prop.getProperty("db.user");
            PASSWORD = prop.getProperty("db.password");
            CONNECTION_URL = "jdbc:mysql://localhost:3306";
        } catch (IOException ex) {
            throw new RuntimeException("Failed to load database properties", ex);
        }
    }

    /**
     * Gets a connection to the database
     * @return A connection to the database
     * @throws DataAccessException if a connection cannot be established
     */
    public static Connection getConnection() throws DataAccessException {
        try {
            Connection connection = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
            connection.setCatalog(DATABASE_NAME);
            return connection;
        } catch (SQLException e) {
            throw new DataAccessException("Unable to connect to database: " + e.getMessage());
        }
    }

    /**
     * Creates the chess database if it doesn't exist
     * @throws DataAccessException if there is an error creating the database
     */
    public static void createDatabase() throws DataAccessException {
        try (Connection conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD)) {
            String statement = "CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME;
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error creating database: " + e.getMessage());
        }
    }

    /**
     * Initializes the database
     * @throws DataAccessException if there is an error initializing the database
     */
    public static void initializeDatabase() throws DataAccessException {
        try {
            createDatabase();
            dropTables();
            createTables();
        } catch (DataAccessException e) {
            throw new DataAccessException("Error initializing database: " + e.getMessage());
        }
    }

    /**
     * Drops all tables from the database
     * @throws DataAccessException if there is an error dropping tables
     */
    private static void dropTables() throws DataAccessException {
        try (Connection conn = getConnection()) {
            // Drop tables in reverse order of dependencies
            String dropGamesTable = "DROP TABLE IF EXISTS games";
            String dropAuthTable = "DROP TABLE IF EXISTS auth_tokens";
            String dropUsersTable = "DROP TABLE IF EXISTS users";

            try (var statement = conn.prepareStatement(dropGamesTable)) {
                statement.executeUpdate();
            }
            try (var statement = conn.prepareStatement(dropAuthTable)) {
                statement.executeUpdate();
            }
            try (var statement = conn.prepareStatement(dropUsersTable)) {
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error dropping tables: " + e.getMessage());
        }
    }

    /**
     * Creates the required tables if they don't exist
     * @throws DataAccessException if there is an error creating the tables
     */
    private static void createTables() throws DataAccessException {
        try (Connection conn = getConnection()) {
            // Create users table
            String createUsersTable = """
                CREATE TABLE IF NOT EXISTS users (
                    username VARCHAR(255) NOT NULL,
                    password VARCHAR(255) NOT NULL,
                    email VARCHAR(255) NOT NULL,
                    PRIMARY KEY (username)
                )
            """;

            // Create auth_tokens table
            String createAuthTable = """
                CREATE TABLE IF NOT EXISTS auth_tokens (
                    auth_token VARCHAR(255) NOT NULL,
                    username VARCHAR(255) NOT NULL,
                    PRIMARY KEY (auth_token),
                    FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
                )
            """;

            // Create games table
            String createGamesTable = """
                CREATE TABLE IF NOT EXISTS games (
                    game_id INT NOT NULL AUTO_INCREMENT,
                    white_username VARCHAR(255),
                    black_username VARCHAR(255),
                    game_name VARCHAR(255) NOT NULL,
                    game_data TEXT NOT NULL,
                    PRIMARY KEY (game_id)
                )
            """;

            // Execute the create table statements
            try (var statement = conn.prepareStatement(createUsersTable)) {
                statement.executeUpdate();
            }

            try (var statement = conn.prepareStatement(createAuthTable)) {
                statement.executeUpdate();
            }

            try (var statement = conn.prepareStatement(createGamesTable)) {
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error creating tables: " + e.getMessage());
        }
    }
}
