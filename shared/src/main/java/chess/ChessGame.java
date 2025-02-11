package chess;

import java.util.Collection;
/**
 * Key Methods:

validMoves: Takes as input a position on the chessboard and returns all moves the piece there can legally make. If there is no piece at that location, this method returns null. A move is valid if it is a "piece move" for the piece at the input location and making that move would not leave the team’s king in danger of check.
makeMove: Receives a given move and executes it, provided it is a legal move. If the move is illegal, it throws an InvalidMoveException. A move is illegal if it is not a "valid" move for the piece at the starting location, or if it’s not the corresponding team's turn.
isInCheck: Returns true if the specified team’s King could be captured by an opposing piece.
isInCheckmate: Returns true if the given team has no way to protect their king from being captured.
isInStalemate: Returns true if the given team has no legal moves but their king is not in immediate danger.
 */
/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    public ChessGame() {

    }
    private TeamColor teamTurn;
    private ChessBoard board = new ChessBoard();

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        // remember starting player is white
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        // set the team turn
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        
        ChessBoard board = getBoard();
        // get the piece at the position
        ChessPiece piece = board.getPiece(startPosition);

        // make sure there is a piece there
        if (piece == null) {
            return null;
        }

        // get the valid moves for the piece
        return piece.pieceMoves(board, startPosition);
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        // set the board
        this.board = board;
        // reset the board
        this.board.resetBoard();
        // set the team turn
        this.teamTurn = TeamColor.WHITE;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        // get the board
        return board;
    }
}
