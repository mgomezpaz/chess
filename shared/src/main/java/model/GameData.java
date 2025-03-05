package model;

import chess.ChessGame;

/**
 * Represents game data in the chess application
 */
public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
}
