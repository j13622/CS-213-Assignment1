package chess;

import java.util.ArrayList;
import java.util.HashMap;

abstract class ReturnPiece {
	static enum PieceType {WP, WR, WN, WB, WQ, WK, 
		            BP, BR, BN, BB, BK, BQ};
	static enum PieceFile {a, b, c, d, e, f, g, h};
	
	PieceType pieceType;
	PieceFile pieceFile;
	int pieceRank;  // 1..8
	Chess.Player color;

	ReturnPiece(PieceType pieceType, PieceFile pieceFile, int pieceRank) {
		this.pieceType = pieceType;
		this.pieceFile = pieceFile;
		this.pieceRank = pieceRank;
		switch(pieceType) {
			case WP:
			case WR:
			case WN:
			case WB:
			case WQ:
			case WK:
				this.color = Chess.Player.white;
				break;
			default:
				this.color = Chess.Player.black;
		}
	}

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
	boolean start = true;
	Pawn(PieceType pieceType, PieceFile pieceFile, int pieceRank) {
		super(pieceType, pieceFile, pieceRank);
	}
}

class Rook extends ReturnPiece {
	Rook(PieceType pieceType, PieceFile pieceFile, int pieceRank) {
		super(pieceType, pieceFile, pieceRank);
	}
}

class Knight extends ReturnPiece {
	Knight(PieceType pieceType, PieceFile pieceFile, int pieceRank) {
		super(pieceType, pieceFile, pieceRank);
	}
}

class Bishop extends ReturnPiece {
	Bishop(PieceType pieceType, PieceFile pieceFile, int pieceRank) {
		super(pieceType, pieceFile, pieceRank);
	}
}

class Queen extends ReturnPiece {
	Queen(PieceType pieceType, PieceFile pieceFile, int pieceRank) {
		super(pieceType, pieceFile, pieceRank);
	}
}

class King extends ReturnPiece {
	King(PieceType pieceType, PieceFile pieceFile, int pieceRank) {
		super(pieceType, pieceFile, pieceRank);
	}
}

class Square {
	ReturnPiece.PieceFile file;
	int rank;

	Square(ReturnPiece.PieceFile file, int rank) {
		this.file = file;
		this.rank = rank;
	}

	public boolean equals(Object other) {
		if (other == null || !(other instanceof Square)) {
			return false;
		}
		Square otherSquare = (Square)other;
		return file == otherSquare.file &&
				rank == otherSquare.rank;
	}

	public int hashCode() {
		return file.hashCode() + 31 * rank;
	}
}

public class Chess {
	static enum Player { white, black }
	static ArrayList<ReturnPiece> pieces;
	static HashMap<Square, ReturnPiece> squares;
	static Player currentPlayer;

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

		ReturnPiece.PieceFile firstFile = ReturnPiece.PieceFile.valueOf("" + ret.charAt(0));
		int firstRank = ret.charAt(1) - '0';
		Square firstSquare = new Square(firstFile, firstRank);
		ReturnPiece firstPiece = squares.get(firstSquare);
		System.out.println(firstPiece);
		
		

		switch(currentPlayer) {
			case white:
				currentPlayer = Player.black;
				break;
			case black:
				currentPlayer = Player.white;
				break;
			default:
				System.out.println("ERROR: PLAYER NO COLOR");
		}
		return state;
	}
	
	
	/**
	 * This method should reset the game, and start from scratch.
	 */
	public static void start() {
		pieces = new ArrayList<ReturnPiece>();
		squares = new HashMap<Square, ReturnPiece>();
		currentPlayer = Chess.Player.white;
		ReturnPiece a1 = new Rook(ReturnPiece.PieceType.WR, ReturnPiece.PieceFile.a, 1);
		ReturnPiece b1 = new Knight(ReturnPiece.PieceType.WN, ReturnPiece.PieceFile.b, 1);
		ReturnPiece c1 = new Bishop(ReturnPiece.PieceType.WB, ReturnPiece.PieceFile.c, 1);
		ReturnPiece d1 = new Queen(ReturnPiece.PieceType.WQ, ReturnPiece.PieceFile.d, 1);
		ReturnPiece e1 = new King(ReturnPiece.PieceType.WK, ReturnPiece.PieceFile.e, 1);
		ReturnPiece f1 = new Bishop(ReturnPiece.PieceType.WB, ReturnPiece.PieceFile.f, 1);
		ReturnPiece g1 = new Knight(ReturnPiece.PieceType.WN, ReturnPiece.PieceFile.g, 1);
		ReturnPiece h1 = new Rook(ReturnPiece.PieceType.WR, ReturnPiece.PieceFile.h, 1);
		ReturnPiece a2 = new Pawn(ReturnPiece.PieceType.WP, ReturnPiece.PieceFile.a, 2);
		ReturnPiece b2 = new Pawn(ReturnPiece.PieceType.WP, ReturnPiece.PieceFile.b, 2);
		ReturnPiece c2 = new Pawn(ReturnPiece.PieceType.WP, ReturnPiece.PieceFile.c, 2);
		ReturnPiece d2 = new Pawn(ReturnPiece.PieceType.WP, ReturnPiece.PieceFile.d, 2);
		ReturnPiece e2 = new Pawn(ReturnPiece.PieceType.WP, ReturnPiece.PieceFile.e, 2);
		ReturnPiece f2 = new Pawn(ReturnPiece.PieceType.WP, ReturnPiece.PieceFile.f, 2);
		ReturnPiece g2 = new Pawn(ReturnPiece.PieceType.WP, ReturnPiece.PieceFile.g, 2);
		ReturnPiece h2 = new Pawn(ReturnPiece.PieceType.WP, ReturnPiece.PieceFile.h, 2);
		ReturnPiece a7 = new Pawn(ReturnPiece.PieceType.BP, ReturnPiece.PieceFile.a, 7);
		ReturnPiece b7 = new Pawn(ReturnPiece.PieceType.BP, ReturnPiece.PieceFile.b, 7);
		ReturnPiece c7 = new Pawn(ReturnPiece.PieceType.BP, ReturnPiece.PieceFile.c, 7);
		ReturnPiece d7 = new Pawn(ReturnPiece.PieceType.BP, ReturnPiece.PieceFile.d, 7);
		ReturnPiece e7 = new Pawn(ReturnPiece.PieceType.BP, ReturnPiece.PieceFile.e, 7);
		ReturnPiece f7 = new Pawn(ReturnPiece.PieceType.BP, ReturnPiece.PieceFile.f, 7);
		ReturnPiece g7 = new Pawn(ReturnPiece.PieceType.BP, ReturnPiece.PieceFile.g, 7);
		ReturnPiece h7 = new Pawn(ReturnPiece.PieceType.BP, ReturnPiece.PieceFile.h, 7);
		ReturnPiece a8 = new Rook(ReturnPiece.PieceType.BR, ReturnPiece.PieceFile.a, 8);
		ReturnPiece b8 = new Knight(ReturnPiece.PieceType.BN, ReturnPiece.PieceFile.b, 8);
		ReturnPiece c8 = new Bishop(ReturnPiece.PieceType.BB, ReturnPiece.PieceFile.c, 8);
		ReturnPiece d8 = new Queen(ReturnPiece.PieceType.BQ, ReturnPiece.PieceFile.d, 8);
		ReturnPiece e8 = new King(ReturnPiece.PieceType.BK, ReturnPiece.PieceFile.e, 8);
		ReturnPiece f8 = new Bishop(ReturnPiece.PieceType.BB, ReturnPiece.PieceFile.f, 8);
		ReturnPiece g8 = new Knight(ReturnPiece.PieceType.BN, ReturnPiece.PieceFile.g, 8);
		ReturnPiece h8 = new Rook(ReturnPiece.PieceType.BR, ReturnPiece.PieceFile.h, 8);
		squares.put(new Square(ReturnPiece.PieceFile.a, 1), a1);
		squares.put(new Square(ReturnPiece.PieceFile.b, 1), b1);
		squares.put(new Square(ReturnPiece.PieceFile.c, 1), c1);
		squares.put(new Square(ReturnPiece.PieceFile.d, 1), d1);
		squares.put(new Square(ReturnPiece.PieceFile.e, 1), e1);
		squares.put(new Square(ReturnPiece.PieceFile.f, 1), f1);
		squares.put(new Square(ReturnPiece.PieceFile.g, 1), g1);
		squares.put(new Square(ReturnPiece.PieceFile.h, 1), h1);
		squares.put(new Square(ReturnPiece.PieceFile.a, 2), a2);
		squares.put(new Square(ReturnPiece.PieceFile.b, 2), b2);
		squares.put(new Square(ReturnPiece.PieceFile.c, 2), c2);
		squares.put(new Square(ReturnPiece.PieceFile.d, 2), d2);
		squares.put(new Square(ReturnPiece.PieceFile.e, 2), e2);
		squares.put(new Square(ReturnPiece.PieceFile.f, 2), f2);
		squares.put(new Square(ReturnPiece.PieceFile.g, 2), g2);
		squares.put(new Square(ReturnPiece.PieceFile.h, 2), h2);
		squares.put(new Square(ReturnPiece.PieceFile.a, 7), a7);
		squares.put(new Square(ReturnPiece.PieceFile.b, 7), b7);
		squares.put(new Square(ReturnPiece.PieceFile.c, 7), c7);
		squares.put(new Square(ReturnPiece.PieceFile.d, 7), d7);
		squares.put(new Square(ReturnPiece.PieceFile.e, 7), e7);
		squares.put(new Square(ReturnPiece.PieceFile.f, 7), f7);
		squares.put(new Square(ReturnPiece.PieceFile.g, 7), g7);
		squares.put(new Square(ReturnPiece.PieceFile.h, 7), h7);
		squares.put(new Square(ReturnPiece.PieceFile.a, 8), a8);
		squares.put(new Square(ReturnPiece.PieceFile.b, 8), b8);
		squares.put(new Square(ReturnPiece.PieceFile.c, 8), c8);
		squares.put(new Square(ReturnPiece.PieceFile.d, 8), d8);
		squares.put(new Square(ReturnPiece.PieceFile.e, 8), e8);
		squares.put(new Square(ReturnPiece.PieceFile.f, 8), f8);
		squares.put(new Square(ReturnPiece.PieceFile.g, 8), g8);
		squares.put(new Square(ReturnPiece.PieceFile.h, 8), h8);
		pieces.add(a1);
		pieces.add(b1);
		pieces.add(c1);
		pieces.add(d1);
		pieces.add(e1);
		pieces.add(f1);
		pieces.add(g1);
		pieces.add(h1);
		pieces.add(a2);
		pieces.add(b2);
		pieces.add(c2);
		pieces.add(d2);
		pieces.add(e2);
		pieces.add(f2);
		pieces.add(g2);
		pieces.add(h2);
		pieces.add(a7);
		pieces.add(b7);
		pieces.add(c7);
		pieces.add(d7);
		pieces.add(e7);
		pieces.add(f7);
		pieces.add(g7);
		pieces.add(h7);
		pieces.add(a8);
		pieces.add(b8);
		pieces.add(c8);
		pieces.add(d8);
		pieces.add(e8);
		pieces.add(f8);
		pieces.add(g8);
		pieces.add(h8);
	}
}

