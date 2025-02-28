package model;

/**
 * Represents user data for authentication and user management
 */
public record UserData(String username, String password, String email) {
    // Records automatically generate constructors, getters, equals, hashCode, and toString
}