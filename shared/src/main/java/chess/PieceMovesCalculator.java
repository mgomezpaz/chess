package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public interface PieceMovesCalculator {
    Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position);

    /* 
    DONE: Implement bishop moves
    */
    class BishopMovesCalculator implements PieceMovesCalculator {
        public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
            List<ChessMove> moves = new ArrayList<>();
            
            // get possible directions for the bishop
            int[][] bishopRules = {{1, 1}, {-1, 1}, {-1, -1}, {1, -1}};
            
            for (int[] bishopDirection : bishopRules) {
                int row = position.getRow();
                int col = position.getColumn();
                
                while (true) {
                    row += bishopDirection[0];
                    col += bishopDirection[1];

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
                    if (newPositionStatus.getTeamColor() != board.getPiece(position).getTeamColor()) {
                        moves.add(new ChessMove(position, newPosition, null));
                    }
                    break;
                }
            }
            
            return moves;
        }
    }

    /* DONE: Implement king moves, basically the same as bishop, 
    but chaning thhe possible directions. 
    */
    class KingMovesCalculator implements PieceMovesCalculator {
        public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
            List<ChessMove> moves = new ArrayList<>();
            
            // get possible directions for the king
            int[][] kingRules = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {-1, 1}, {-1, -1}, {1, -1}};
            
            for (int[] kingDirection : kingRules) {
                int row = position.getRow();
                int col = position.getColumn();
                
                // calculate the new position
                row += kingDirection[0];
                col += kingDirection[1];

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
                if (newPositionStatus.getTeamColor() != board.getPiece(position).getTeamColor()) {
                moves.add(new ChessMove(position, newPosition, null));
                }
            }
            
            return moves;
        }
    }
    /* 
    DONE: Implement the queen moves, 
    this is basically a mix between king and bishop, 
    we can use the same logic as bishop but with the king directions
    */
    class QueenMovesCalculator implements PieceMovesCalculator {
        @Override
        public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
            List<ChessMove> moves = new ArrayList<>();

            // get possible directions for the queen
            int[][] queenRules = {{1, 1}, {-1, 1}, {-1, -1}, {1, -1}, {1, 0}, {-1, 0}, {0, 1}, {0, -1}};
            
            for (int[] queenDirection : queenRules) {
                int row = position.getRow();
                int col = position.getColumn();
                
                while (true) {
                    row += queenDirection[0];
                    col += queenDirection[1];

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
                    if (newPositionStatus.getTeamColor() != board.getPiece(position).getTeamColor()) {
                        moves.add(new ChessMove(position, newPosition, null));
                    }
                    break;
                }
            }
            
            return moves;
        }
    }

    /* 
    DONE: Implement knight moves, basically the same as king, 
    but changing the possible directions.
    */
    class KnightMovesCalculator implements PieceMovesCalculator {
        @Override
        public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
            List<ChessMove> moves = new ArrayList<>();
            
            // get possible directions for the knight
            int[][] knightRules = {{2, 1}, {2, -1}, {-2, 1}, {-2, -1}, {1, 2}, {1, -2}, {-1, 2}, {-1, -2}};
            
            for (int[] knightDirection : knightRules) {
                int row = position.getRow();
                int col = position.getColumn();
                
                // calculate the new position
                row += knightDirection[0];
                col += knightDirection[1];

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
                if (newPositionStatus.getTeamColor() != board.getPiece(position).getTeamColor()) {
                moves.add(new ChessMove(position, newPosition, null));
                }
            }
            
            return moves;
        }
    }

    /* 
    DONE: Implement rook moves, 
    this is basically bishops  
    we can use the same logic as bishop but with the king directions
    */

    class RookMovesCalculator implements PieceMovesCalculator {
        @Override
        public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
            List<ChessMove> moves = new ArrayList<>();

            // get possible directions for the rook
            int[][] rookRules = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
            
            for (int[] rookDirection : rookRules) {
                int row = position.getRow();
                int col = position.getColumn();
                
                while (true) {
                    row += rookDirection[0];
                    col += rookDirection[1];

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
                    if (newPositionStatus.getTeamColor() != board.getPiece(position).getTeamColor()) {
                        moves.add(new ChessMove(position, newPosition, null));
                    }
                    break;
                }
            }
            
            return moves;
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

            // initialize possible directions for the pawn
            int[][] pawnRules;

            // load possible directions for white pawns
            if (color == ChessGame.TeamColor.WHITE) {
                pawnRules = new int[][]{{1, 0}, {1, 1}, {1, -1}, {2, 0}};
            } 
            // load possible directions for black pawns
            else {  // must be BLACK
                pawnRules = new int[][]{{-1, 0}, {-1, 1}, {-1, -1}, {-2, 0}};
            }
            
            // loop thorugh all possible directions
            for (int[] pawnDirection : pawnRules) {

                // get current location and possible new position
                int row = position.getRow();
                int col = position.getColumn();
                row += pawnDirection[0];
                col += pawnDirection[1];

                // make sure that the move is in the board (legal?)
                if (col < 1 || col > 8 || row < 1 || row > 8) {
                    continue;
                }

                // get information about the new position
                ChessPosition newPosition = new ChessPosition(row, col);
                ChessPiece newPositionStatus = board.getPiece(newPosition);

    
                // case 1: If moving forward by one
                if (pawnDirection[1] == 0 && (pawnDirection[0] == 1 || pawnDirection[0] == -1)) {
                    
                    // if the position is empty, it is a possible move
                    if (newPositionStatus == null) {
                        // If there is promotion
                        if ((color == ChessGame.TeamColor.WHITE && row == 8) || 
                            (color == ChessGame.TeamColor.BLACK && row == 1)) {
                            // Include possible promotion moves
                            moves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.QUEEN));
                            moves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.ROOK));
                            moves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.KNIGHT));
                            moves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.BISHOP));
                        } else {
                            moves.add(new ChessMove(position, newPosition, null));
                        }
                    }
                    continue;
                }

                // Case 2: If moving diagonally
                if (pawnDirection[1] == 1 || pawnDirection[1] == -1) {
                    
                    // Only move diagonal  if there's an enemy piece to capture
                    if (newPositionStatus != null && 
                        newPositionStatus.getTeamColor() != board.getPiece(position).getTeamColor()) {
                        // Check for promotion after capture
                        if ((color == ChessGame.TeamColor.WHITE && row == 8) || 
                            (color == ChessGame.TeamColor.BLACK && row == 1)) {
                            // Include possible promotion moves
                            moves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.QUEEN));
                            moves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.ROOK));
                            moves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.KNIGHT));
                            moves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.BISHOP));
                        } else {
                            moves.add(new ChessMove(position, newPosition, null));
                        }
                    }
                    continue;
                }

                // if moving by two
                if (pawnDirection[1] == 0 && (pawnDirection[0] == 2 || pawnDirection[0] == -2)) {

                    // Can only move two squares if path is clear
                    if (((color == ChessGame.TeamColor.WHITE && startingPosition == 2) || (color == ChessGame.TeamColor.BLACK && startingPosition == 7)) && newPositionStatus == null) {
                        // make sure the path is not blocked
                        int pathRow = position.getRow() + (pawnDirection[0] / 2);
                        ChessPosition pathPosition = new ChessPosition(pathRow, col);
                        if (board.getPiece(pathPosition) == null) {
                            moves.add(new ChessMove(position, newPosition, null));
                        }
                    }
                    continue;
                }
                
            }
            
            return moves;
        }
    }
} 