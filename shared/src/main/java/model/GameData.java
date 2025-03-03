package model;

import chess.ChessGame;

/**
 * GameData
 * 
 * Field	Type
 * gameID	int
 * whiteUsername	String
 * blackUsername	String
 * gameName	String
 * game	ChessGame
 */
public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
}
