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
            
            // loop thorugh the directions
            for (int[] pawn_direction : pawn_rules) {
                int row = position.getRow();
                int col = position.getColumn();
                row += pawn_direction[0];
                col += pawn_direction[1];

                // make sure that the move is in the board
                if (col < 1 || col > 8 || row < 1 || row > 8) {
                    break;
                }

                // get information about the new position
                ChessPosition newPosition = new ChessPosition(row, col);
                ChessPiece newPositionStatus = board.getPiece(newPosition);

    
                // if moving forward by one
                if (pawn_direction[1] == 0 && (pawn_direction[0] == 1 || pawn_direction[0] == -1)) {
                    // if the position is empty, it is a possible move
                    if (newPositionStatus == null) {
                        moves.add(new ChessMove(position, newPosition, null));
                        continue;
                    }
                }

                // if moving diagonally
                if (pawn_direction[1] == 1 || pawn_direction[1] == -1) {
                    // if the position is not empty, make sure it is an enemy piece
                    if (newPositionStatus.getTeamColor() != board.getPiece(position).getTeamColor()) {
                        moves.add(new ChessMove(position, newPosition, null));
                    }
                }

                // if moving by two
                if (pawn_direction[1] == 0 && (pawn_direction[0] == 2 || pawn_direction[0] == -2)) {
                    // if the pawn in the starting position as white
                    if (color == ChessGame.TeamColor.WHITE && starting_position == 2) {
                        // if the position is empty, it is a possible move
                        if (newPositionStatus == null) {
                            moves.add(new ChessMove(position, newPosition, null));
                            continue;
                        }
                        
                    //if the pawn in the starting position as black
                    } else if (color == ChessGame.TeamColor.BLACK && starting_position == 7) {
                        // if the position is empty, it is a possible move
                        if (newPositionStatus == null) {
                                moves.add(new ChessMove(position, newPosition, null));
                            continue;
                        }
                    }
                } 
            }
            
            return moves;
        }
    }
} 