package model;

/**
 * Represents authentication data with token and associated username
 */
public record AuthData(String authToken, String username) {
    // Records automatically generate constructors, getters, equals, hashCode, and toString
}