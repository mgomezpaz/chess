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
            int[][] bishop_rules = {{1, 1}, {-1, 1}, {-1, -1}, {1, -1}};
            
            for (int[] bishop_direction : bishop_rules) {
                int row = position.getRow();
                int col = position.getColumn();
                
                while (true) {
                    row += bishop_direction[0];
                    col += bishop_direction[1];

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
            int[][] king_rules = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {-1, 1}, {-1, -1}, {1, -1}};
            
            for (int[] king_direction : king_rules) {
                int row = position.getRow();
                int col = position.getColumn();
                
                // calculate the new position
                row += king_direction[0];
                col += king_direction[1];

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
            int[][] queen_rules = {{1, 1}, {-1, 1}, {-1, -1}, {1, -1}, {1, 0}, {-1, 0}, {0, 1}, {0, -1}};
            
            for (int[] queen_direction : queen_rules) {
                int row = position.getRow();
                int col = position.getColumn();
                
                while (true) {
                    row += queen_direction[0];
                    col += queen_direction[1];

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
            int[][] knight_rules = {{2, 1}, {2, -1}, {-2, 1}, {-2, -1}, {1, 2}, {1, -2}, {-1, 2}, {-1, -2}};
            
            for (int[] knight_direction : knight_rules) {
                int row = position.getRow();
                int col = position.getColumn();
                
                // calculate the new position
                row += knight_direction[0];
                col += knight_direction[1];

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
            int[][] rook_rules = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
            
            for (int[] rook_direction : rook_rules) {
                int row = position.getRow();
                int col = position.getColumn();
                
                while (true) {
                    row += rook_direction[0];
                    col += rook_direction[1];

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
            int starting_position = position.getRow();

            // initialize possible directions for the pawn
            int[][] pawn_rules;

            // load possible directions for white pawns
            if (color == ChessGame.TeamColor.WHITE) {
                pawn_rules = new int[][]{{1, 0}, {1, 1}, {1, -1}, {2, 0}};
            } 
            // load possible directions for black pawns
            else {  // must be BLACK
                pawn_rules = new int[][]{{-1, 0}, {-1, 1}, {-1, -1}, {-2, 0}};
            }
            
            // loop thorugh all possible directions
            for (int[] pawn_direction : pawn_rules) {

                // get current location and possible new position
                int row = position.getRow();
                int col = position.getColumn();
                row += pawn_direction[0];
                col += pawn_direction[1];

                // make sure that the move is in the board (legal?)
                if (col < 1 || col > 8 || row < 1 || row > 8) {
                    continue;
                }

                // get information about the new position
                ChessPosition newPosition = new ChessPosition(row, col);
                ChessPiece newPositionStatus = board.getPiece(newPosition);

    
                // case 1: If moving forward by one
                if (pawn_direction[1] == 0 && (pawn_direction[0] == 1 || pawn_direction[0] == -1)) {
                    
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
                if (pawn_direction[1] == 1 || pawn_direction[1] == -1) {
                    
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
                if (pawn_direction[1] == 0 && (pawn_direction[0] == 2 || pawn_direction[0] == -2)) {

                    // Can only move two squares if path is clear
                    if (((color == ChessGame.TeamColor.WHITE && starting_position == 2) || (color == ChessGame.TeamColor.BLACK && starting_position == 7)) && newPositionStatus == null) {
                        // make sure the path is not blocked
                        int path_row = position.getRow() + (pawn_direction[0] / 2);
                        ChessPosition path_position = new ChessPosition(path_row, col);
                        if (board.getPiece(path_position) == null) {
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