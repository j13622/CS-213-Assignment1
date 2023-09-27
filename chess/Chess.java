package chess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


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

		//sets color
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

	//switch enum PieceFile to int
	static int fileToInt(PieceFile file) {
		switch(file) {
			case a:
				return 1;
			case b:
				return 2;
			case c:
				return 3;
			case d:
				return 4;
			case e:
				return 5;
			case f:
				return 6;
			case g:
				return 7;
			case h:
				return 8;
			default:
				return 1;
		}
	}

	//switch int to enum PieceFile
	static PieceFile intToFile(int x) {
		switch(x) {
			case 1:
				return PieceFile.a;
			case 2:
				return PieceFile.b;
			case 3:
				return PieceFile.c;
			case 4:
				return PieceFile.d;
			case 5:
				return PieceFile.e;
			case 6:
				return PieceFile.f;
			case 7:
				return PieceFile.g;
			case 8:
				return PieceFile.h;
			default:
				return PieceFile.a;
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

	//defined in subclasses
	abstract HashSet<Square> see(Chess.Player color, HashMap<Square, ReturnPiece> map);
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

	Pawn(PieceType pieceType, PieceFile pieceFile, int pieceRank) {
		super(pieceType, pieceFile, pieceRank);
	}

	HashSet<Square> see(Chess.Player color, HashMap<Square, ReturnPiece> squares) {
		HashSet<Square> moves = new HashSet<Square>();
		int intFile = ReturnPiece.fileToInt(pieceFile);
		boolean leftFile = intFile == 1;
		boolean rightFile = intFile == 8;
		int x;
		int y;
		if (color == Chess.Player.white) {
			x = 1;
			y = 2;
		}
		else {
			x = -1;
			y = 7;
		}
		Square aheadSquare = new Square(pieceFile, pieceRank+(x*1));
		ReturnPiece aheadPiece = squares.get(aheadSquare);
		if (aheadPiece == null) {
			moves.add(aheadSquare);
		}
		if (pieceRank == y && aheadPiece == null) {
			Square aheadSquare2 = new Square(pieceFile, pieceRank+(x*2));
			ReturnPiece aheadPiece2 = squares.get(aheadSquare2);
			if (aheadPiece2 == null) {
				moves.add(aheadSquare2);
			}
		}
		if (leftFile) {
			int diagRank = pieceRank+(x*1);
			int diagFile = intFile+1;
			Square diag = new Square(ReturnPiece.intToFile(diagFile), diagRank);
			ReturnPiece piece = squares.get(diag);
			if (piece != null && piece.color != color) {
				moves.add(diag);
			}
		}
		else if (rightFile) {
			int diagRank = pieceRank+(x*1);
			int diagFile = intFile-1;
			Square diag = new Square(ReturnPiece.intToFile(diagFile), diagRank);
			ReturnPiece piece = squares.get(diag);
			if (piece != null && piece.color != color) {
				moves.add(diag);
			}
		}
		else {
			int diagRank1 = pieceRank+(x*1);
			int diagFile1 = intFile-1;
			int diagRank2 = pieceRank+(x*1);
			int diagFile2 = intFile+1;
			Square diag1 = new Square(ReturnPiece.intToFile(diagFile1), diagRank1);
			Square diag2 = new Square(ReturnPiece.intToFile(diagFile2), diagRank2);
			ReturnPiece piece1 = squares.get(diag1);
			ReturnPiece piece2 = squares.get(diag2);
			if (piece1 != null && piece1.color != color) {
				moves.add(diag1);
			}
			if (piece2 != null && piece2.color != color) {
				moves.add(diag2);
			}
		}
		return moves;
	}
}

class Rook extends ReturnPiece {

	Rook(PieceType pieceType, PieceFile pieceFile, int pieceRank) {
		super(pieceType, pieceFile, pieceRank);		
	}

	//idea here is to go, say, right from rook and then keep looking right until hit piece or edge of board
	HashSet<Square> see(Chess.Player color, HashMap<Square, ReturnPiece> squares) {
		HashSet<Square> moves = new HashSet<Square>();
		int intFile = ReturnPiece.fileToInt(pieceFile);
		for (int i = intFile - 1; i >= 1; i--) {
			Square move = new Square(ReturnPiece.intToFile(i), pieceRank);
			ReturnPiece piece = squares.get(move);
			if (piece == null) {
				moves.add(move);
			}
			else if (piece.color != color) {
				moves.add(move);
				break;
			}
			else {
				break;
			}
		}
		for(int i = intFile + 1; i <= 8; i++) {
			Square move = new Square(ReturnPiece.intToFile(i), pieceRank);
			ReturnPiece piece = squares.get(move);
			if (piece == null) {
				moves.add(move);
			}
			else if (piece.color != color) {
				moves.add(move);
				break;
			}
			else {
				break;
			}
		}
		for(int i = pieceRank - 1; i >= 1; i--) {
			Square move = new Square(pieceFile, i);
			ReturnPiece piece = squares.get(move);
			if (piece == null) {
				moves.add(move);
			}
			else if (piece.color != color) {
				moves.add(move);
				break;
			}
			else {
				break;
			}
		}
		for(int i = pieceRank + 1; i <= 8; i++) {
			Square move = new Square(pieceFile, i);
			ReturnPiece piece = squares.get(move);
			if (piece == null) {
				moves.add(move);
			}
			else if (piece.color != color) {
				moves.add(move);
				break;
			}
			else {
				break;
			}
		}
		return moves;
	}
}

class Knight extends ReturnPiece {

	Knight(PieceType pieceType, PieceFile pieceFile, int pieceRank) {
		super(pieceType, pieceFile, pieceRank);
	}

	//knight has 8 possible moves. if either of those is outside of board (> 8 or < 1) then it's off board, not in if statement
	//if it's not a opposite color piece or empty then it's not added
	HashSet<Square> see(Chess.Player color, HashMap<Square, ReturnPiece> squares) {
		HashSet<Square> moves = new HashSet<Square>();
		int intFile = ReturnPiece.fileToInt(pieceFile);
		int[][] sqrs = {{intFile - 1, pieceRank - 2}, {intFile - 1, pieceRank + 2}, {intFile - 2, pieceRank - 1}, {intFile - 2, pieceRank + 1},
		{intFile + 1, pieceRank - 2}, {intFile + 1, pieceRank + 2}, {intFile + 2, pieceRank - 1}, {intFile + 2, pieceRank + 1}};
		for (int[] i : sqrs) {
			if (i[0] <= 8 && i[0] >= 1 && i[1] <= 8 && i[1] >= 1) {
				Square move = new Square(ReturnPiece.intToFile(i[0]), i[1]);
				ReturnPiece piece = squares.get(move);
				if (piece == null || piece.color != color) {
					moves.add(move);
				}
			}
		}
		return moves;
	}
}

class Bishop extends ReturnPiece {

	Bishop(PieceType pieceType, PieceFile pieceFile, int pieceRank) {
		super(pieceType, pieceFile, pieceRank);
	}

	//check comment above rook's 'see' function. same principle
	HashSet<Square> see(Chess.Player color, HashMap<Square, ReturnPiece> squares) {
		HashSet<Square> moves = new HashSet<Square>();
		int intFile = ReturnPiece.fileToInt(pieceFile);
		for(int i = intFile + 1, j = pieceRank + 1; i <= 8 && j <= 8; i++, j++) {
			Square move = new Square(ReturnPiece.intToFile(i), j);
			ReturnPiece piece = squares.get(move);
			if (piece == null) {
				moves.add(move);
			}
			else if (piece.color != color) {
				moves.add(move);
				break;
			}
			else {
				break;
			}
		}
		for(int i = intFile + 1, j = pieceRank - 1; i <= 8 && j >= 1; i++, j--) {
			Square move = new Square(ReturnPiece.intToFile(i), j);
			ReturnPiece piece = squares.get(move);
			if (piece == null) {
				moves.add(move);
			}
			else if (piece.color != color) {
				moves.add(move);
				break;
			}
			else {
				break;
			}
		}
		for(int i = intFile - 1, j = pieceRank + 1; i >= 1 && j <= 8; i--, j++) {
			Square move = new Square(ReturnPiece.intToFile(i), j);
			ReturnPiece piece = squares.get(move);
			if (piece == null) {
				moves.add(move);
			}
			else if (piece.color != color) {
				moves.add(move);
				break;
			}
			else {
				break;
			}
		}
		for(int i = intFile - 1, j = pieceRank - 1; i >= 1 && j >= 1; i--, j--) {
			Square move = new Square(ReturnPiece.intToFile(i), j);
			ReturnPiece piece = squares.get(move);
			if (piece == null) {
				moves.add(move);
			}
			else if (piece.color != color) {
				moves.add(move);
				break;
			}
			else {
				break;
			}
		}
		return moves;
	}
}

class Queen extends ReturnPiece {

	Queen(PieceType pieceType, PieceFile pieceFile, int pieceRank) {
		super(pieceType, pieceFile, pieceRank);
	}

	HashSet<Square> see(Chess.Player color, HashMap<Square, ReturnPiece> squares) {
		//should be able to just concat the for loops from bishop + rook
		return null;
	}
}

class King extends ReturnPiece {

	King(PieceType pieceType, PieceFile pieceFile, int pieceRank) {
		super(pieceType, pieceFile, pieceRank);
	}

	HashSet<Square> see(Chess.Player color, HashMap<Square, ReturnPiece> squares) {
		return null;
	}
}

class Square {
	ReturnPiece.PieceFile file;
	int rank;

	Square(ReturnPiece.PieceFile file, int rank) {
		this.file = file;
		this.rank = rank;
	}

	public String toString() {
		return ""+file+rank;
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
		
		if (firstPiece == null || currentPlayer != firstPiece.color) {
			System.out.println("first illegal");
			state.message = ReturnPlay.Message.ILLEGAL_MOVE;
			return state;
		}

		ReturnPiece.PieceFile secondFile = ReturnPiece.PieceFile.valueOf("" + ret.charAt(3));
		int secondRank = ret.charAt(4) - '0';
		Square secondSquare = new Square(secondFile, secondRank);
		HashSet<Square> possibleMoves = firstPiece.see(currentPlayer, squares);
		System.out.println(possibleMoves);
		if (possibleMoves.contains(secondSquare)) {
			firstPiece.pieceFile = secondFile;
			firstPiece.pieceRank = secondRank;
			squares.remove(firstSquare);
			ReturnPiece potentialPiece = squares.get(secondSquare);
			if (potentialPiece != null) {
				pieces.remove(potentialPiece);
			}
			squares.put(secondSquare, firstPiece);
		}
		else {
			System.out.println("second illegal");
			state.message = ReturnPlay.Message.ILLEGAL_MOVE;
			return state;
		}

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