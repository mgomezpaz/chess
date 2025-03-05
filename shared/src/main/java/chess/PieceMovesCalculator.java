package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public interface PieceMovesCalculator {
    Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position);

    // Helper to check if a position is on the board
    default boolean isValidPosition(int row, int col) {
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }
    
    // This helps with the duplicate code for sliding pieces (Bishop, Rook, Queen)
    default Collection<ChessMove> calculateSlidingMoves(ChessBoard board, ChessPosition position, int[][] directions) {
        List<ChessMove> moves = new ArrayList<>();
        ChessPiece piece = board.getPiece(position);
        
        // Try each direction until we hit something or the edge
        for (int[] direction : directions) {
            int row = position.getRow();
            int col = position.getColumn();
            
            while (true) {
                row += direction[0];
                col += direction[1];
                
                // make sure that the move is in the board
                if (col < 1 || col > 8 || row < 1 || row > 8) {
                    break;
                }
                
                // find information about the new position being considered
                ChessPosition newPosition = new ChessPosition(row, col);
                ChessPiece newPositionStatus = board.getPiece(newPosition);
                
                // if the position is empty, it is a possible move
                if (newPositionStatus == null) {
                    moves.add(new ChessMove(position, newPosition, null));
                    continue;
                }
                
                // if the position is not empty, make sure it is an enemy piece
                if (newPositionStatus.getTeamColor() != piece.getTeamColor()) {
                    moves.add(new ChessMove(position, newPosition, null));
                }
                break; // Can't move past a piece
            }
        }
        
        return moves;
    }
    
    // Helper for pieces that move in fixed steps (King, Knight)
    default Collection<ChessMove> calculateSteppingMoves(ChessBoard board, ChessPosition position, int[][] directions) {
        List<ChessMove> moves = new ArrayList<>();
        ChessPiece piece = board.getPiece(position);
        
        for (int[] direction : directions) {
            int row = position.getRow() + direction[0];
            int col = position.getColumn() + direction[1];
            
            // make sure that the move is in the board, if not, get the next direction
            if (col < 1 || col > 8 || row < 1 || row > 8) {
                continue;
            }
            
            // find information about the new position being considered
            ChessPosition newPosition = new ChessPosition(row, col);
            ChessPiece newPositionStatus = board.getPiece(newPosition);
            
            // if the position is empty, it is a possible move
            if (newPositionStatus == null) {
                moves.add(new ChessMove(position, newPosition, null));
                continue;
            }
            
            // if the position is not empty, make sure it is an enemy piece
            if (newPositionStatus.getTeamColor() != piece.getTeamColor()) {
                moves.add(new ChessMove(position, newPosition, null));
            }
        }
        
        return moves;
    }

    /* 
    DONE: Implement bishop moves
    */
    class BishopMovesCalculator implements PieceMovesCalculator {
        // get possible directions for the bishop
        private static final int[][] BISHOP_RULES = {
            {1, 1}, {-1, 1}, {-1, -1}, {1, -1}
        };
        
        public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
            return calculateSlidingMoves(board, position, BISHOP_RULES);
        }
    }

    /* DONE: Implement king moves, basically the same as bishop, 
    but chaning thhe possible directions. 
    */
    class KingMovesCalculator implements PieceMovesCalculator {
        // get possible directions for the king
        private static final int[][] KING_RULES = {
            {1, 0}, {-1, 0}, {0, 1}, {0, -1}, 
            {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        };
        
        public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
            return calculateSteppingMoves(board, position, KING_RULES);
        }
    }
    /* 
    DONE: Implement the queen moves, 
    this is basically a mix between king and bishop, 
    we can use the same logic as bishop but with the king directions
    */
    class QueenMovesCalculator implements PieceMovesCalculator {
        // get possible directions for the queen
        private static final int[][] QUEEN_RULES = {
            {1, 0}, {-1, 0}, {0, 1}, {0, -1},  // Rook-like moves
            {1, 1}, {1, -1}, {-1, 1}, {-1, -1} // Bishop-like moves
        };
        
        @Override
        public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
            return calculateSlidingMoves(board, position, QUEEN_RULES);
        }
    }

    /* 
    DONE: Implement knight moves, basically the same as king, 
    but changing the possible directions.
    */
    class KnightMovesCalculator implements PieceMovesCalculator {
        // get possible directions for the knight
        private static final int[][] KNIGHT_RULES = {
            {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
            {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };
        
        @Override
        public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
            return calculateSteppingMoves(board, position, KNIGHT_RULES);
        }
    }

    /* 
    DONE: Implement rook moves, 
    this is basically bishops  
    we can use the same logic as bishop but with the king directions
    */
    class RookMovesCalculator implements PieceMovesCalculator {
        // get possible directions for the rook
        private static final int[][] ROOK_RULES = {
            {1, 0}, {-1, 0}, {0, 1}, {0, -1}
        };
        
        @Override
        public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
            return calculateSlidingMoves(board, position, ROOK_RULES);
        }
    }

    /* 
    DONE: Implement pawn moves, 
    this is more complex than the other pieces, 
    we need to check if the pawn is moving forward, 
    if it is, we need to check if the position is empty or has a piece
    it can also move diagonally to kill another piece
    (include promotion cases)
    */
    class PawnMovesCalculator implements PieceMovesCalculator {
        @Override
        public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
            List<ChessMove> moves = new ArrayList<>();
            
            // get the color of the pawn
            ChessGame.TeamColor color = board.getPiece(position).getTeamColor();
            int startingPosition = position.getRow();
            
            // Direction multiplier (white moves up, black moves down)
            int forward = (color == ChessGame.TeamColor.WHITE) ? 1 : -1;
            
            // Forward move (one square)
            checkPawnForwardMove(board, position, moves, forward, 1);
            
            // Initial two-square move
            if ((color == ChessGame.TeamColor.WHITE && startingPosition == 2) || 
                (color == ChessGame.TeamColor.BLACK && startingPosition == 7)) {
                checkPawnForwardMove(board, position, moves, forward, 2);
            }
            
            // Diagonal captures
            checkPawnDiagonalCapture(board, position, moves, forward, 1);  // Right diagonal
            checkPawnDiagonalCapture(board, position, moves, forward, -1); // Left diagonal
            
            return moves;
        }
        
        // Helper for forward pawn moves
        private void checkPawnForwardMove(ChessBoard board, ChessPosition position, 
                                       List<ChessMove> moves, int forward, int squares) {
            int newRow = position.getRow() + (forward * squares);
            int col = position.getColumn();
            
            // make sure that the move is in the board (legal?)
            if (col < 1 || col > 8 || newRow < 1 || newRow > 8) {
                return;
            }
            
            ChessPosition newPosition = new ChessPosition(newRow, col);
            
            // For two-square moves, check if the path is clear
            if (squares == 2) {
                ChessPosition middlePosition = new ChessPosition(position.getRow() + forward, col);
                if (board.getPiece(middlePosition) != null || board.getPiece(newPosition) != null) {
                    return;
                }
                moves.add(new ChessMove(position, newPosition, null));
                return;
            }
            
            // For one-square moves
            if (board.getPiece(newPosition) == null) {
                // Check for promotion
                if (isPawnPromotion(newRow, board.getPiece(position).getTeamColor())) {
                    addAllPromotionMoves(position, newPosition, moves);
                } else {
                    moves.add(new ChessMove(position, newPosition, null));
                }
            }
        }
        
        // Helper for diagonal pawn captures
        private void checkPawnDiagonalCapture(ChessBoard board, ChessPosition position, 
                                           List<ChessMove> moves, int forward, int sideways) {
            int newRow = position.getRow() + forward;
            int newCol = position.getColumn() + sideways;
            
            // make sure that the move is in the board (legal?)
            if (newCol < 1 || newCol > 8 || newRow < 1 || newRow > 8) {
                return;
            }
            
            ChessPosition newPosition = new ChessPosition(newRow, newCol);
            ChessPiece targetPiece = board.getPiece(newPosition);
            
            // Only move diagonal if there's an enemy piece to capture
            if (targetPiece != null && 
                targetPiece.getTeamColor() != board.getPiece(position).getTeamColor()) {
                
                // Check for promotion after capture
                if (isPawnPromotion(newRow, board.getPiece(position).getTeamColor())) {
                    addAllPromotionMoves(position, newPosition, moves);
                } else {
                    moves.add(new ChessMove(position, newPosition, null));
                }
            }
        }
        
        // Helper to check if a row is a promotion row
        private boolean isPawnPromotion(int row, ChessGame.TeamColor color) {
            return (color == ChessGame.TeamColor.WHITE && row == 8) || 
                   (color == ChessGame.TeamColor.BLACK && row == 1);
        }
        
        // Helper to add all promotion options
        private void addAllPromotionMoves(ChessPosition start, ChessPosition end, List<ChessMove> moves) {
            // Include possible promotion moves
            moves.add(new ChessMove(start, end, ChessPiece.PieceType.QUEEN));
            moves.add(new ChessMove(start, end, ChessPiece.PieceType.ROOK));
            moves.add(new ChessMove(start, end, ChessPiece.PieceType.KNIGHT));
            moves.add(new ChessMove(start, end, ChessPiece.PieceType.BISHOP));
        }
    }
} 