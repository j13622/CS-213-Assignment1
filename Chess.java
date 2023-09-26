package chess;

import java.util.ArrayList;

class ReturnPiece {
	static enum PieceType {WP, WR, WN, WB, WQ, WK, 
		            BP, BR, BN, BB, BK, BQ};
	static enum PieceFile {a, b, c, d, e, f, g, h};
	
	PieceType pieceType;
	PieceFile pieceFile;
	int pieceRank;  // 1..8
	public String toString() {
		return ""+pieceFile+pieceRank+":"+pieceType;
	}
	public boolean equals(Object other) {
		if (other == null || !(other instanceof ReturnPiece)) {
			return false;
		}
		ReturnPiece otherPiece = (ReturnPiece)other;
		return pieceType == otherPiece.pieceType &&
				pieceFile == otherPiece.pieceFile &&
				pieceRank == otherPiece.pieceRank;
	}
	ArrayList<Integer> findSpots();
	void move();
	Player getColor() {
		switch(pieceType):
			case WP:
			case WR:
			case WN:
			case WB:
			case WQ:
			case WK:
				return Player.white;
			default:
				return Player.black;
	}
}

class ReturnPlay {
	enum Message {ILLEGAL_MOVE, DRAW, 
				  RESIGN_BLACK_WINS, RESIGN_WHITE_WINS, 
				  CHECK, CHECKMATE_BLACK_WINS,	CHECKMATE_WHITE_WINS, 
				  STALEMATE};
	
	ArrayList<ReturnPiece> piecesOnBoard;
	Message message;
}


class Pawn extends ReturnPiece {
	ArrayList<Integer> findSpots() {}
	void move() {}
}

class Bishop extends ReturnPiece {
	ArrayList<Integer> findSpots() {}
	void move() {}
}

class Knight extends ReturnPiece {
	ArrayList<Integer> findSpots() {}
	void move() {}	
}

class Rook extends ReturnPiece {
	ArrayList<Integer> findSpots() {}
	void move() {}	
}

class King extends ReturnPiece {
	ArrayList<Integer> findSpots() {}
	void move() {}	
}

class Queen extends ReturnPiece {
	ArrayList<Integer> findSpots() {}
	void move() {}	
}

public class Chess {
	
	enum Player { white, black }
	ArrayList<ReturnPiece> pieces;
	Player currentPlayer;
	/**
	 * Plays the next move for whichever player has the turn.
	 * 
	 * @param move String for next move, e.g. "a2 a3"
	 * 
	 * @return A ReturnPlay instance that contains the result of the move.
	 *         See the section "The Chess class" in the assignment description for details of
	 *         the contents of the returned ReturnPlay instance.
	 */
	public static ReturnPlay play(String move) {
		
		ReturnPlay state = new ReturnPlay();
		state.piecesOnBoard = pieces;

		int first = 0;
        String ret;

        for(int i = 0; i < move.length(); i++) {
            if (move.charAt(i) != ' ') {
                first = i;
                break;
            }
        }

        if (move.length() >= first + 7 && move.charAt(first+6) != ' ') {
            ret = move.substring(first, first+7);
        } else {
            ret = move.substring(first, first+5);
        }

		PieceType firstFile = PieceType.valueOf(ret.charAt(0));
		int firstRank = ret.charAt(1) - '0';
		ReturnPiece firstPiece;

		for (int i = 0; i < pieces.size(); i++) {
			ReturnPiece currPiece = pieces[i];
			if (currPiece.pieceFile == firstFile && currPiece.pieceRank == firstRank) {
				if (currPiece.getColor() != currentPlayer) {
					state.message = Message.ILLEGAL_MOVE;
					return state;
				}
				firstPiece = currPiece;
				break;
			}
		}

		

		switch(currentPlayer) {
			case white:
				currentPlayer = Player.black;
				break;
			case black:
				currentPlayer = Player.white;
				break;
			default:
				System.out.println("ERROR: PLAYER NO COLOR")
		}
		return null;
	}
	
	
	/**
	 * This method should reset the game, and start from scratch.
	 */
	public static void start() {
		pieces = new ArrayList<ReturnPiece>();
		currentPlayer = Player.white;
		ReturnPiece a1 = new Rook();
		a1.pieceType = ReturnPiece.PieceType.WR; a1.pieceFile = ReturnPiece.PieceFile.a; a1.pieceRank = 1;
		ReturnPiece b1 = new Knight();
		b1.pieceType = ReturnPiece.PieceType.WN; b1.pieceFile = ReturnPiece.PieceFile.b; b1.pieceRank = 1;
		ReturnPiece c1 = new Bishop();
		c1.pieceType = ReturnPiece.PieceType.WB; c1.pieceFile = ReturnPiece.PieceFile.c; c1.pieceRank = 1;
		ReturnPiece d1 = new Queen();
		d1.pieceType = ReturnPiece.PieceType.WQ; d1.pieceFile = ReturnPiece.PieceFile.d; d1.pieceRank = 1;
		ReturnPiece e1 = new King();
		e1.pieceType = ReturnPiece.PieceType.WK; e1.pieceFile = ReturnPiece.PieceFile.e; e1.pieceRank = 1;
		ReturnPiece f1 = new Bishop();
		f1.pieceType = ReturnPiece.PieceType.WB; f1.pieceFile = ReturnPiece.PieceFile.f; f1.pieceRank = 1;
		ReturnPiece g1 = new Knight();
		g1.pieceType = ReturnPiece.PieceType.WN; g1.pieceFile = ReturnPiece.PieceFile.g; g1.pieceRank = 1;
		ReturnPiece h1 = new Rook();
		h1.pieceType = ReturnPiece.PieceType.WR; h1.pieceFile = ReturnPiece.PieceFile.h; h1.pieceRank = 1;
		ReturnPiece a2 = new Pawn();
		a2.pieceType = ReturnPiece.PieceType.WP; a2.pieceFile = ReturnPiece.PieceFile.a; a2.pieceRank = 2;
		ReturnPiece b2 = new Pawn();
		b2.pieceType = ReturnPiece.PieceType.WP; b2.pieceFile = ReturnPiece.PieceFile.b; b2.pieceRank = 2;
		ReturnPiece c2 = new Pawn();
		c2.pieceType = ReturnPiece.PieceType.WP; c2.pieceFile = ReturnPiece.PieceFile.c; c2.pieceRank = 2;
		ReturnPiece d2 = new Pawn();
		d2.pieceType = ReturnPiece.PieceType.WP; d2.pieceFile = ReturnPiece.PieceFile.d; d2.pieceRank = 2;
		ReturnPiece e2 = new Pawn();
		e2.pieceType = ReturnPiece.PieceType.WP; e2.pieceFile = ReturnPiece.PieceFile.e; e2.pieceRank = 2;
		ReturnPiece f2 = new Pawn();
		f2.pieceType = ReturnPiece.PieceType.WP; f2.pieceFile = ReturnPiece.PieceFile.f; f2.pieceRank = 2;
		ReturnPiece g2 = new Pawn();
		g2.pieceType = ReturnPiece.PieceType.WP; g2.pieceFile = ReturnPiece.PieceFile.g; g2.pieceRank = 2;
		ReturnPiece h2 = new Pawn();
		h2.pieceType = ReturnPiece.PieceType.WP; h2.pieceFile = ReturnPiece.PieceFile.h; h2.pieceRank = 2;
		ReturnPiece a7 = new Pawn();
		a7.pieceType = ReturnPiece.PieceType.BP; a7.pieceFile = ReturnPiece.PieceFile.a; a7.pieceRank = 7;
		ReturnPiece b7 = new Pawn();
		b7.pieceType = ReturnPiece.PieceType.BP; b7.pieceFile = ReturnPiece.PieceFile.b; b7.pieceRank = 7;
		ReturnPiece c7 = new Pawn();
		c7.pieceType = ReturnPiece.PieceType.BP; c7.pieceFile = ReturnPiece.PieceFile.c; c7.pieceRank = 7;
		ReturnPiece d7 = new Pawn();
		d7.pieceType = ReturnPiece.PieceType.BP; d7.pieceFile = ReturnPiece.PieceFile.d; d7.pieceRank = 7;
		ReturnPiece e7 = new Pawn();
		e7.pieceType = ReturnPiece.PieceType.BP; e7.pieceFile = ReturnPiece.PieceFile.e; e7.pieceRank = 7;
		ReturnPiece f7 = new Pawn();
		f7.pieceType = ReturnPiece.PieceType.BP; f7.pieceFile = ReturnPiece.PieceFile.f; f7.pieceRank = 7;
		ReturnPiece g7 = new Pawn();
		g7.pieceType = ReturnPiece.PieceType.BP; g7.pieceFile = ReturnPiece.PieceFile.g; g7.pieceRank = 7;
		ReturnPiece h7 = new Pawn();
		h7.pieceType = ReturnPiece.PieceType.BP; h7.pieceFile = ReturnPiece.PieceFile.h; h7.pieceRank = 7;
		ReturnPiece a8 = new Rook();
		a8.pieceType = ReturnPiece.PieceType.BR; a8.pieceFile = ReturnPiece.PieceFile.a; a8.pieceRank = 8;
		ReturnPiece b8 = new Knight();
		b8.pieceType = ReturnPiece.PieceType.BN; b8.pieceFile = ReturnPiece.PieceFile.b; b8.pieceRank = 8;
		ReturnPiece c8 = new Bishop();
		c8.pieceType = ReturnPiece.PieceType.BB; c8.pieceFile = ReturnPiece.PieceFile.c; c8.pieceRank = 8;
		ReturnPiece d8 = new Queen();
		d8.pieceType = ReturnPiece.PieceType.BQ; d8.pieceFile = ReturnPiece.PieceFile.d; d8.pieceRank = 8;
		ReturnPiece e8 = new King();
		e8.pieceType = ReturnPiece.PieceType.BK; e8.pieceFile = ReturnPiece.PieceFile.e; e8.pieceRank = 8;
		ReturnPiece f8 = new Bishop();
		f8.pieceType = ReturnPiece.PieceType.BB; f8.pieceFile = ReturnPiece.PieceFile.f; f8.pieceRank = 8;
		ReturnPiece g8 = new Knight();
		g8.pieceType = ReturnPiece.PieceType.BN; g8.pieceFile = ReturnPiece.PieceFile.g; g8.pieceRank = 8;
		ReturnPiece h8 = new Rook();
		h8.pieceType = ReturnPiece.PieceType.BR; h8.pieceFile = ReturnPiece.PieceFile.h; h8.pieceRank = 8;
		pieces.add(a1)
		pieces.add(b1)
		pieces.add(c1)
		pieces.add(d1)
		pieces.add(e1)
		pieces.add(f1)
		pieces.add(g1)
		pieces.add(h1)
		pieces.add(a2)
		pieces.add(b2)
		pieces.add(c2)
		pieces.add(d2)
		pieces.add(e2)
		pieces.add(f2)
		pieces.add(g2)
		pieces.add(h2)
		pieces.add(a7)
		pieces.add(b7)
		pieces.add(c7)
		pieces.add(d7)
		pieces.add(e7)
		pieces.add(f7)
		pieces.add(g7)
		pieces.add(h7)
		pieces.add(a8)
		pieces.add(b8)
		pieces.add(c8)
		pieces.add(d8)
		pieces.add(e8)
		pieces.add(f8)
		pieces.add(g8)
		pieces.add(h8)
	}
}

