package model;

import chess.ChessGame;

/**
 * Represents data for a chess game
 */
public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
    // Records automatically generate constructors, getters, equals, hashCode, and toString
}
