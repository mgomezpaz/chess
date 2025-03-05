package chess;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    private TeamColor teamTurn;

    // constructor
    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        teamTurn = TeamColor.WHITE;
    }
    
    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        // remember starting player is white
        return this.teamTurn;
    }
    
    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        // set the team turn
        this.teamTurn = team;
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
        // Grab the piece we want to move
        ChessPiece movingPiece = board.getPiece(startPosition);
        if (movingPiece == null) return null;

        // Get the raw moves this piece could make
        Collection<ChessMove> legalMoves = new ArrayList<>();
        Collection<ChessMove> rawMoves = movingPiece.pieceMoves(board, startPosition);

        // Test each move to make sure it doesn't put us in check
        for (ChessMove candidateMove : rawMoves) {
            // Create a "what-if" board to test this move
            ChessBoard whatIfBoard = new ChessBoard();
            
            // Copy over all the pieces except where we're moving from/to
            for (int r = 1; r <= 8; r++) {
                for (int c = 1; c <= 8; c++) {
                    ChessPosition spot = new ChessPosition(r, c);
                    ChessPiece pieceAtSpot = board.getPiece(spot);
                    
                    if (pieceAtSpot != null) {
                        whatIfBoard.addPiece(spot, pieceAtSpot);
                    }
                }
            }
            
            // Make our hypothetical move
            whatIfBoard.addPiece(startPosition, null);  // clear starting spot
            
            // Handle pawn promotions specially
            if (candidateMove.getPromotionPiece() != null) {
                whatIfBoard.addPiece(candidateMove.getEndPosition(), 
                    new ChessPiece(movingPiece.getTeamColor(), candidateMove.getPromotionPiece()));
            } else {
                whatIfBoard.addPiece(candidateMove.getEndPosition(), movingPiece);
            }
            
            // See if this move would leave us in check
            ChessGame whatIfGame = new ChessGame();
            whatIfGame.setBoard(whatIfBoard);
            
            if (!whatIfGame.isInCheck(movingPiece.getTeamColor())) {
                legalMoves.add(candidateMove);
            }
        }

        return legalMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        
        // get the piece we are moving
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (piece == null) {
            throw new InvalidMoveException("No piece at start position");
        }

        // check if the piece is the correct team
        if (piece.getTeamColor() != teamTurn) {
            throw new InvalidMoveException("Wrong team");
        }

        // check if the move is valid
        if (!validMoves(move.getStartPosition()).contains(move)) {
            throw new InvalidMoveException("Invalid move");
        }

        // is it a promotion?
        if (move.getPromotionPiece() != null) {
            // check if the promotion is valid
            if (!validMoves(move.getStartPosition()).contains(move)) {
                throw new InvalidMoveException("Invalid promotion");
            }
            // make the promotion
            board.addPiece(move.getEndPosition(), new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()));
            board.addPiece(move.getStartPosition(), null);
            // switch the team turn
            teamTurn = teamTurn == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
            return;
        }
        else {
            // make the move
            board.addPiece(move.getEndPosition(), board.getPiece(move.getStartPosition()));
            board.addPiece(move.getStartPosition(), null);
            // switch the team turn
            teamTurn = teamTurn == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        // find the position of the king
        ChessPosition kingPosition = null;
        // iterate through the board to find the position of the king
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition currentPosition = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(currentPosition);
                
                boolean isTeamKing = piece != null 
                        && piece.getTeamColor() == teamColor 
                        && piece.getPieceType() == ChessPiece.PieceType.KING;
                        
                if (isTeamKing) {
                    kingPosition = currentPosition;
                    break;
                }
            }
            if (kingPosition != null) break;
        }

        if (kingPosition == null) {
            throw new RuntimeException("King not in the board");
        }

        // Check if any enemy piece can capture the king
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition currentPosition = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(currentPosition);
                
                boolean isEnemyPiece = piece != null && piece.getTeamColor() != teamColor;
                if (isEnemyPiece) {
                    Collection<ChessMove> possibleMoves = piece.pieceMoves(board, currentPosition);
                    
                    // Check if any move can capture the king
                    for (ChessMove move : possibleMoves) {
                        if (move.getEndPosition().equals(kingPosition)) {
                            return true;  // king is in check
                        }
                    }
                }
            }
        }
        
        return false;  // king is not in check
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        // check if the team is in check
        if (!isInCheck(teamColor)) {
            return false;
        }

        // check if the team has any valid moves
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition currentPosition = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(currentPosition);

                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> possibleMoves = piece.pieceMoves(board, currentPosition);
                    for (ChessMove move : possibleMoves) {
                        if (validMoves(move.getStartPosition()).contains(move)) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        // Returns true if the given team has no legal moves but their king is not in immediate danger.
        // check if the team is in check
        if (isInCheck(teamColor)) {
            return false;
        }

        // get each piece of the team
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition currentPosition = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(currentPosition);

                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> possibleMoves = piece.pieceMoves(board, currentPosition);
                    for (ChessMove move : possibleMoves) {
                        if (validMoves(move.getStartPosition()).contains(move)) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return board.equals(chessGame.board) && teamTurn == chessGame.teamTurn;
    }

    // Extract duplicated code into a helper method
    private boolean isValidMove(ChessMove move, TeamColor teamColor) {
        // Create a copy of the board to test the move
        ChessBoard tempBoard = board.copyBoard();
        
        // Make the move on the temporary board
        tempBoard.addPiece(move.getEndPosition(), tempBoard.getPiece(move.getStartPosition()));
        tempBoard.addPiece(move.getStartPosition(), null);
        
        // Check if the king is in check after the move
        return !isInCheckAfterMove(tempBoard, teamColor);
    }

    private boolean isInCheckAfterMove(ChessBoard board, TeamColor teamColor) {
        // Find the king's position
        ChessPosition kingPosition = findKingPosition(board, teamColor);
        
        // Check if any opponent piece can attack the king
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(position);
                
                if (piece != null && piece.getTeamColor() != teamColor) {
                    Collection<ChessMove> pieceMoves = piece.pieceMoves(board, position);
                    for (ChessMove pieceMove : pieceMoves) {
                        if (pieceMove.getEndPosition().equals(kingPosition)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    // Refactor deeply nested code by extracting methods
    private boolean canTeamMakeValidMove(TeamColor teamColor) {
        // Get all pieces of the team
        List<PiecePosition> teamPieces = getTeamPieces(teamColor);
        
        // Check if any piece can make a valid move
        for (PiecePosition piecePosition : teamPieces) {
            if (canPieceMakeValidMove(piecePosition.piece, piecePosition.position, teamColor)) {
                return true;
            }
        }
        return false;
    }

    private boolean canPieceMakeValidMove(ChessPiece piece, ChessPosition position, TeamColor teamColor) {
        Collection<ChessMove> pieceMoves = piece.pieceMoves(board, position);
        for (ChessMove move : pieceMoves) {
            if (isValidMove(move, teamColor)) {
                return true;
            }
        }
        return false;
    }

    private List<PiecePosition> getTeamPieces(TeamColor teamColor) {
        List<PiecePosition> teamPieces = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    teamPieces.add(new PiecePosition(piece, position));
                }
            }
        }
        return teamPieces;
    }

    // Helper class to store piece and position together
    private static class PiecePosition {
        ChessPiece piece;
        ChessPosition position;
        
        PiecePosition(ChessPiece piece, ChessPosition position) {
            this.piece = piece;
            this.position = position;
        }
    }

    private ChessPosition findKingPosition(ChessBoard board, TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                
                if (piece != null && 
                    piece.getTeamColor() == teamColor && 
                    piece.getPieceType() == ChessPiece.PieceType.KING) {
                    return position;
                }
            }
        }
        throw new RuntimeException("King not found on the board");
    }
}