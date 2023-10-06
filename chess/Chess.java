package chess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.EnumMap;
import java.util.EnumSet;
import java.lang.reflect.Field;

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
}

class ReturnPlay {
	enum Message {ILLEGAL_MOVE, DRAW, 
				  RESIGN_BLACK_WINS, RESIGN_WHITE_WINS, 
				  CHECK, CHECKMATE_BLACK_WINS,	CHECKMATE_WHITE_WINS, 
				  STALEMATE};
	
	ArrayList<ReturnPiece> piecesOnBoard;
	Message message;
}

class ReturnPlayCopy {
	enum Message {ILLEGAL_MOVE, DRAW, 
				  RESIGN_BLACK_WINS, RESIGN_WHITE_WINS, 
				  CHECK, CHECKMATE_BLACK_WINS,	CHECKMATE_WHITE_WINS, 
				  STALEMATE};
	
	ArrayList<ReturnPiece> piecesOnBoard;
	Message message;

	public ReturnPlayCopy(ReturnPlay returnPlay){
		this.piecesOnBoard = new ArrayList<ReturnPiece>();
		ArrayList<ReturnPiece> originalPieces = returnPlay.piecesOnBoard;
		for (ReturnPiece originalPiece : originalPieces){
			ReturnPiece piece = new ReturnPiece(originalPiece.pieceType, originalPiece.pieceFile, originalPiece.pieceRank);
			this.piecesOnBoard.add(piece);
		}
		/*
		Field[] fields = ReturnPlay.class.getDeclaredFields();
		try{
			for (Field field : fields){
				field.setAccessible(true);
				field.set(this, field.get(returnPlay));
			}
		} catch(IllegalAccessException exception) {
			exception.printStackTrace();
		}
		 */
		
	}
}

public class Chess {
	static enum Player { white, black }
	static ArrayList<FullPiece> pieces;
	static HashMap<Square, FullPiece> squares;
	static Player currentPlayer;
	static ReturnPlay state;

	public static ArrayList<ReturnPiece> castPieceArray(ArrayList<FullPiece> fullPieces) {
		ArrayList<ReturnPiece> returnPieces = new ArrayList<ReturnPiece>();
		for(FullPiece i : fullPieces) {
			returnPieces.add((ReturnPiece) i);
		}
		return returnPieces;
	}

	public static boolean isMyKingInCheck(){
		//use- see if current player king is in check after player makes move

		//gets current player's king's square
		Square kingSquare = null;
		if (currentPlayer == Chess.Player.white){
			for (FullPiece potentiallyKing : pieces){
				if (potentiallyKing.pieceType == ReturnPiece.PieceType.WK){
					kingSquare = new Square(potentiallyKing.pieceFile, potentiallyKing.pieceRank); 
				}
			}
		}
		else {
			for (FullPiece potentiallyKing : pieces){
				if (potentiallyKing.pieceType == ReturnPiece.PieceType.BK){
					kingSquare = new Square(potentiallyKing.pieceFile, potentiallyKing.pieceRank); 
				}
			}
		}
		//determines if any of opponenet's pieces can see the current player's king
		HashSet<Square> allSquaresOpponentSees = new HashSet<Square>();
		for (FullPiece opponentPiece : pieces){
			if (opponentPiece.color != currentPlayer && opponentPiece.pieceType != ReturnPiece.PieceType.WK && opponentPiece.pieceType != ReturnPiece.PieceType.BK){
				HashSet<Square> currentOpponentPieceSees = new HashSet<Square>();
				currentOpponentPieceSees = opponentPiece.see();
				allSquaresOpponentSees.addAll(currentOpponentPieceSees);
			}
		}
		if (allSquaresOpponentSees.contains(kingSquare)){
			return true;
		}

		return false;
	}

	public static boolean isOpponentKingInCheck(){
		//use- see if opponent king is in check after current player makes any move

		//gets opponent king's square
		Square kingSquare = null;
		if (currentPlayer == Chess.Player.white){
			for (FullPiece potentiallyKing : pieces){
				if (potentiallyKing.pieceType == ReturnPiece.PieceType.BK){
					kingSquare = new Square(potentiallyKing.pieceFile, potentiallyKing.pieceRank); 
				}
			}
		}
		else {
			for (FullPiece potentiallyKing : pieces){
				if (potentiallyKing.pieceType == ReturnPiece.PieceType.WK){
					kingSquare = new Square(potentiallyKing.pieceFile, potentiallyKing.pieceRank); 
				}
			}
		}
		//determines if any of current player's pieces can see opponent king
		HashSet<Square> allSquaresOpponentSees = new HashSet<Square>();
		for (FullPiece myPiece : pieces){
			if (myPiece.color == currentPlayer && myPiece.pieceType != ReturnPiece.PieceType.WK && myPiece.pieceType != ReturnPiece.PieceType.BK){
				HashSet<Square> myPiecesSee = new HashSet<Square>();
				myPiecesSee = myPiece.see();
				allSquaresOpponentSees.addAll(myPiecesSee);
			}
		}
		if (allSquaresOpponentSees.contains(kingSquare)){
			return true;
		}

		return false;
	}


	public static boolean isKingInCheck(Square KingSquare){
		//use- see if current player's king is hypothetically walking into check

		HashSet<Square> allSquaresOpponentSees = new HashSet<Square>();
		for (FullPiece opponentPiece : pieces){
			if (opponentPiece.color != currentPlayer && opponentPiece.pieceType != ReturnPiece.PieceType.WK && opponentPiece.pieceType != ReturnPiece.PieceType.BK){
				HashSet<Square> currentOpponentPieceSees = new HashSet<Square>();
				currentOpponentPieceSees = opponentPiece.see();
				allSquaresOpponentSees.addAll(currentOpponentPieceSees);
			}
		}
		if (allSquaresOpponentSees.contains(KingSquare)){
			return true;
		}

		return false;
	}

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
		
		//make a deep copy of original board state
		ReturnPlayCopy originalState = new ReturnPlayCopy(state);

		//-find out if it is chechmate
		//-must ensure next move gets king out of check if put into it
		
		state.message = null;

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
		int moveSubstringLength = ret.length();

		ReturnPiece.PieceFile firstFile = ReturnPiece.PieceFile.valueOf("" + ret.charAt(0));
		int firstRank = ret.charAt(1) - '0';
		Square firstSquare = new Square(firstFile, firstRank);
		FullPiece firstPiece = squares.get(firstSquare);
		
		if (firstPiece == null || currentPlayer != firstPiece.color) {
			System.out.println("first illegal - must move own piece");
			state.message = ReturnPlay.Message.ILLEGAL_MOVE;
			return state;
		}

		ReturnPiece.PieceFile secondFile = ReturnPiece.PieceFile.valueOf("" + ret.charAt(3));
		int secondRank = ret.charAt(4) - '0';
		Square secondSquare = new Square(secondFile, secondRank);
		FullPiece.enPassantPossible = false;
		HashSet<Square> possibleMoves = firstPiece.see();
		FullPiece potentialPiece = null;

		System.out.println(possibleMoves);
		if (possibleMoves.contains(secondSquare)) {

			//illegal if kings kiss
			if (firstPiece.pieceType == ReturnPiece.PieceType.WK || firstPiece.pieceType == ReturnPiece.PieceType.BK){
				HashSet<Square> opponentKingSees = new HashSet<Square>();
				if (currentPlayer == Chess.Player.white){
					for (FullPiece opponentKing : pieces){
						if (opponentKing.pieceType == ReturnPiece.PieceType.BK){
							opponentKingSees = opponentKing.see();
						}
					}
				}
				else {
					for (FullPiece opponentKing : pieces){
						if (opponentKing.pieceType == ReturnPiece.PieceType.WK){
							opponentKingSees = opponentKing.see();
						}
					}
				}
				if (opponentKingSees.contains(secondSquare)){
					System.out.println("third illegal - king kissing king");
					state.message = ReturnPlay.Message.ILLEGAL_MOVE;
					return state;
				}
			}

			//illegal to walk into check
			if (firstPiece.pieceType == ReturnPiece.PieceType.WK || firstPiece.pieceType == ReturnPiece.PieceType.BK){
				if (isKingInCheck(secondSquare)){
					System.out.println("fourth illegal - king walking into check");
					state.message = ReturnPlay.Message.ILLEGAL_MOVE;
					return state;
				}
			}


			firstPiece.pieceFile = secondFile;
			firstPiece.pieceRank = secondRank;
			squares.remove(firstSquare);
			potentialPiece = squares.get(secondSquare);
			if (potentialPiece != null) {
				pieces.remove(potentialPiece);
			}
			squares.put(secondSquare, firstPiece);

			//castle rights
			if (firstPiece.pieceType == ReturnPiece.PieceType.WK) {
				FullPiece.whiteCastleLong = false;
				FullPiece.whiteCastleShort = false;
			}
			if (firstPiece.pieceType == ReturnPiece.PieceType.BK) {
				FullPiece.blackCastleLong = false;
				FullPiece.blackCastleShort = false;
			}
			if (firstFile == ReturnPiece.PieceFile.a && firstRank == 1) {
				FullPiece.whiteCastleLong = false;
			}
			if (firstFile == ReturnPiece.PieceFile.h && firstRank == 1) {
				FullPiece.whiteCastleShort = false;
			}
			if (firstFile == ReturnPiece.PieceFile.a && firstRank == 8) {
				FullPiece.blackCastleLong = false;
			}
			if (firstFile == ReturnPiece.PieceFile.h && firstRank == 8) {
				FullPiece.blackCastleShort = false;
			}

			//enPassant
			if (FullPiece.enPassant != null && FullPiece.enPassantPossible && FullPiece.enPassant.pieceFile == secondFile && FullPiece.enPassant.pieceRank == secondRank) {
				int x;
				if (currentPlayer == Chess.Player.white) {
					x = -1;
				}
				else {
					x = 1;
				}
				Square enPassantSqr = new Square(secondFile, secondRank+(x*1));
				ReturnPiece passantPawn = squares.get(enPassantSqr);
				squares.remove(enPassantSqr);
				pieces.remove(potentialPiece);
			}

			//castling
			ReturnPiece.PieceFile initialRookFile = null;
			int castleRank = 0;
			ReturnPiece.PieceFile finalRookFile = null;
			if (firstPiece.pieceType == ReturnPiece.PieceType.WK && firstFile == ReturnPiece.PieceFile.e && secondFile == ReturnPiece.PieceFile.c) {
				initialRookFile = ReturnPiece.PieceFile.a;
				castleRank = 1;
				finalRookFile = ReturnPiece.PieceFile.d;
			}
			if (firstPiece.pieceType == ReturnPiece.PieceType.WK && firstFile == ReturnPiece.PieceFile.e && secondFile == ReturnPiece.PieceFile.g) {
				initialRookFile = ReturnPiece.PieceFile.h;
				castleRank = 1;
				finalRookFile = ReturnPiece.PieceFile.f;
			}
			if (firstPiece.pieceType == ReturnPiece.PieceType.BK && firstFile == ReturnPiece.PieceFile.e && secondFile == ReturnPiece.PieceFile.c) {
				initialRookFile = ReturnPiece.PieceFile.a;
				castleRank = 8;
				finalRookFile = ReturnPiece.PieceFile.d;
			}
			if (firstPiece.pieceType == ReturnPiece.PieceType.BK && firstFile == ReturnPiece.PieceFile.e && secondFile == ReturnPiece.PieceFile.g) {
				initialRookFile = ReturnPiece.PieceFile.h;
				castleRank = 8;
				finalRookFile = ReturnPiece.PieceFile.f;
			}
			if (initialRookFile != null) {
				Square rookSqr = new Square(initialRookFile, castleRank);
				FullPiece rook = squares.get(rookSqr);
				squares.put(new Square(finalRookFile, castleRank), rook);
				rook.pieceFile = finalRookFile;
				squares.remove(rookSqr);
			}
		} 
		else {
			System.out.println("second illegal - not in set of legal moves");
			state.message = ReturnPlay.Message.ILLEGAL_MOVE;
			return state;
		}

		
		//en passant
		if (firstPiece.pieceType == ReturnPiece.PieceType.WP && (firstRank + 2 == secondRank)) {
			FullPiece.enPassant = new Pawn(ReturnPiece.PieceType.WP, firstFile, 3);
		} 
		else if (firstPiece.pieceType == ReturnPiece.PieceType.BP && (firstRank - 2 == secondRank)) {
			FullPiece.enPassant = new Pawn(ReturnPiece.PieceType.BP, firstFile, 6);
		}
		else {
			FullPiece.enPassant = null;
		}

		FullPiece promPiece = null;
		//2 if statements below handle promotion
			if (secondRank == 8 && firstPiece.pieceType == ReturnPiece.PieceType.WP){
				pieces.remove(firstPiece);
				squares.remove(firstSquare);
				squares.remove(secondSquare);
				switch(ret.charAt(moveSubstringLength-1)){
					case 'N':
						promPiece = new Knight(ReturnPiece.PieceType.WN, secondFile, secondRank);
						break;
					case 'B':
						promPiece = new Bishop(ReturnPiece.PieceType.WB, secondFile, secondRank);
						break;
					case 'R':
						promPiece = new Rook(ReturnPiece.PieceType.WR, secondFile, secondRank);
						break;
					default:
						promPiece = new Queen(ReturnPiece.PieceType.WQ, secondFile, secondRank);
				}
			}
			if (secondRank == 1 && firstPiece.pieceType == ReturnPiece.PieceType.BP){
				pieces.remove(firstPiece);
				squares.remove(firstSquare);
				squares.remove(secondSquare);
				switch(ret.charAt(moveSubstringLength-1)){
					case 'N':
						promPiece = new Knight(ReturnPiece.PieceType.BN, secondFile, secondRank);
						break;
					case 'B':
						promPiece = new Bishop(ReturnPiece.PieceType.BB, secondFile, secondRank);
						break;
					case 'R':
						promPiece = new Rook(ReturnPiece.PieceType.BR, secondFile, secondRank);
						break;
					default:
						promPiece = new Queen(ReturnPiece.PieceType.BQ, secondFile, secondRank);
				}
			}

		if (promPiece != null) {
			squares.put(new Square(secondFile, secondRank), promPiece);
			pieces.add(promPiece);
		}

		if (isMyKingInCheck()){
			state.piecesOnBoard = originalState.piecesOnBoard;
			state.piecesOnBoard = castPieceArray(pieces);
			System.out.println("fifth illegal - can't put own king in check");
			state.message = ReturnPlay.Message.ILLEGAL_MOVE;
			return state;
		}

		if (isOpponentKingInCheck()){
			state.message = ReturnPlay.Message.CHECK;
			return state;
		}

		if (currentPlayer == Chess.Player.white) {
			currentPlayer = Player.black;
		} else {
			currentPlayer = Player.white;
		}

		state.piecesOnBoard = castPieceArray(pieces);
		return state;
	}
	
	
	/**
	 * This method should reset the game, and start from scratch.
	 */
	public static void start() {
		state = new ReturnPlay();
		pieces = new ArrayList<FullPiece>();
		squares = new HashMap<Square, FullPiece>();
		currentPlayer = Chess.Player.white;
		FullPiece.enPassant = null;
		FullPiece.blackCastleLong = true;
		FullPiece.blackCastleShort = true;
		FullPiece.whiteCastleLong = true;
		FullPiece.whiteCastleShort = true;
		FullPiece a1 = new Rook(ReturnPiece.PieceType.WR, ReturnPiece.PieceFile.a, 1);
		FullPiece b1 = new Knight(ReturnPiece.PieceType.WN, ReturnPiece.PieceFile.b, 1);
		FullPiece c1 = new Bishop(ReturnPiece.PieceType.WB, ReturnPiece.PieceFile.c, 1);
		FullPiece d1 = new Queen(ReturnPiece.PieceType.WQ, ReturnPiece.PieceFile.d, 1);
		FullPiece e1 = new King(ReturnPiece.PieceType.WK, ReturnPiece.PieceFile.e, 1);
		FullPiece f1 = new Bishop(ReturnPiece.PieceType.WB, ReturnPiece.PieceFile.f, 1);
		FullPiece g1 = new Knight(ReturnPiece.PieceType.WN, ReturnPiece.PieceFile.g, 1);
		FullPiece h1 = new Rook(ReturnPiece.PieceType.WR, ReturnPiece.PieceFile.h, 1);
		FullPiece a2 = new Pawn(ReturnPiece.PieceType.WP, ReturnPiece.PieceFile.a, 2);
		FullPiece b2 = new Pawn(ReturnPiece.PieceType.WP, ReturnPiece.PieceFile.b, 2);
		FullPiece c2 = new Pawn(ReturnPiece.PieceType.WP, ReturnPiece.PieceFile.c, 2);
		FullPiece d2 = new Pawn(ReturnPiece.PieceType.WP, ReturnPiece.PieceFile.d, 2);
		FullPiece e2 = new Pawn(ReturnPiece.PieceType.WP, ReturnPiece.PieceFile.e, 2);
		FullPiece f2 = new Pawn(ReturnPiece.PieceType.WP, ReturnPiece.PieceFile.f, 2);
		FullPiece g2 = new Pawn(ReturnPiece.PieceType.WP, ReturnPiece.PieceFile.g, 2);
		FullPiece h2 = new Pawn(ReturnPiece.PieceType.WP, ReturnPiece.PieceFile.h, 2);
		FullPiece a7 = new Pawn(ReturnPiece.PieceType.BP, ReturnPiece.PieceFile.a, 7);
		FullPiece b7 = new Pawn(ReturnPiece.PieceType.BP, ReturnPiece.PieceFile.b, 7);
		FullPiece c7 = new Pawn(ReturnPiece.PieceType.BP, ReturnPiece.PieceFile.c, 7);
		FullPiece d7 = new Pawn(ReturnPiece.PieceType.BP, ReturnPiece.PieceFile.d, 7);
		FullPiece e7 = new Pawn(ReturnPiece.PieceType.BP, ReturnPiece.PieceFile.e, 7);
		FullPiece f7 = new Pawn(ReturnPiece.PieceType.BP, ReturnPiece.PieceFile.f, 7);
		FullPiece g7 = new Pawn(ReturnPiece.PieceType.BP, ReturnPiece.PieceFile.g, 7);
		FullPiece h7 = new Pawn(ReturnPiece.PieceType.BP, ReturnPiece.PieceFile.h, 7);
		FullPiece a8 = new Rook(ReturnPiece.PieceType.BR, ReturnPiece.PieceFile.a, 8);
		FullPiece b8 = new Knight(ReturnPiece.PieceType.BN, ReturnPiece.PieceFile.b, 8);
		FullPiece c8 = new Bishop(ReturnPiece.PieceType.BB, ReturnPiece.PieceFile.c, 8);
		FullPiece d8 = new Queen(ReturnPiece.PieceType.BQ, ReturnPiece.PieceFile.d, 8);
		FullPiece e8 = new King(ReturnPiece.PieceType.BK, ReturnPiece.PieceFile.e, 8);
		FullPiece f8 = new Bishop(ReturnPiece.PieceType.BB, ReturnPiece.PieceFile.f, 8);
		FullPiece g8 = new Knight(ReturnPiece.PieceType.BN, ReturnPiece.PieceFile.g, 8);
		FullPiece h8 = new Rook(ReturnPiece.PieceType.BR, ReturnPiece.PieceFile.h, 8);
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
		state.piecesOnBoard = castPieceArray(pieces);
	}
}

abstract class FullPiece extends ReturnPiece {
	Chess.Player color;
	static FullPiece enPassant;
	static boolean enPassantPossible;
	static boolean whiteCastleShort;
	static boolean whiteCastleLong;
	static boolean blackCastleShort;
	static boolean blackCastleLong;

	//fileIntMap: maps enum Piecefile to int, e.g. d -> 4
	static EnumMap<PieceFile, Integer> fileIntMap = new EnumMap<>(PieceFile.class);
	//intFileMap: maps int to enum PieceFile, e.g. 3 -> c
	static HashMap<Integer, PieceFile> intFileMap = new HashMap<>();
	//typeColorMap: maps enum PieceType to enum Chess.Player, e.g. WK -> white, BB -> black
	static EnumMap<PieceType, Chess.Player> typeColorMap = new EnumMap<>(PieceType.class);

	static {
		//sets fileIntMap
		fileIntMap.put(PieceFile.a, 1);
		fileIntMap.put(PieceFile.b, 2);
		fileIntMap.put(PieceFile.c, 3);
		fileIntMap.put(PieceFile.d, 4);
		fileIntMap.put(PieceFile.e, 5);
		fileIntMap.put(PieceFile.f, 6);
		fileIntMap.put(PieceFile.g, 7);
		fileIntMap.put(PieceFile.h, 8);
		//sets intFileMap
		for(PieceFile i : fileIntMap.keySet()) {
			intFileMap.put(fileIntMap.get(i), i);
		}
		//sets typeColorMap
		for(PieceType i : EnumSet.allOf(PieceType.class)) {
			if (i.toString().charAt(0) == 'W') {
				typeColorMap.put(i, Chess.Player.white);
			}
			else {
				typeColorMap.put(i, Chess.Player.black);
			}
		}
		enPassant = null;
		enPassantPossible = false;
	}

	//constructor
	FullPiece(PieceType pieceType, PieceFile pieceFile, int pieceRank) {
		this.pieceType = pieceType;
		this.pieceFile = pieceFile;
		this.pieceRank = pieceRank;
		this.color = typeColorMap.get(pieceType);
	}

	//convert enum PieceFile to int
	static int fileToInt(PieceFile file) {
		return fileIntMap.get(file);
	}

	//convert int to enum PieceFile
	static PieceFile intToFile(int x) {
		return intFileMap.get(x);
	}

	//defined in subclass
	abstract HashSet<Square> see();
}

class Pawn extends FullPiece {

	Pawn(PieceType pieceType, PieceFile pieceFile, int pieceRank) {
		super(pieceType, pieceFile, pieceRank);
	}

	//color -> Chess.currentPlayer, squares -> Chess.squares
	HashSet<Square> see() {
		HashSet<Square> moves = new HashSet<Square>();
		int intFile = FullPiece.fileToInt(pieceFile);
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

		FullPiece aheadPiece = Chess.squares.get(aheadSquare);
		if (aheadPiece == null) {
			moves.add(aheadSquare);
		}
		if (pieceRank == y && aheadPiece == null) {
			Square aheadSquare2 = new Square(pieceFile, pieceRank+(x*2));
			FullPiece aheadPiece2 = Chess.squares.get(aheadSquare2);
			if (aheadPiece2 == null) {
				moves.add(aheadSquare2);
			}
		}
		if (leftFile) {
			int diagRank = pieceRank+(x*1);
			FullPiece.PieceFile diagFile = FullPiece.intToFile(intFile+1);
			Square diag = new Square(diagFile, diagRank);
			FullPiece piece = Chess.squares.get(diag);
			if (enPassant != null && enPassant.color != color && enPassant.pieceFile == diagFile && enPassant.pieceRank == diagRank) {
				enPassantPossible = true;
				moves.add(diag);
			}
			if (piece != null && piece.color != color) {
				moves.add(diag);
			}
		}
		else if (rightFile) {
			int diagRank = pieceRank+(x*1);
			FullPiece.PieceFile diagFile = FullPiece.intToFile(intFile-1);
			Square diag = new Square(diagFile, diagRank);
			if (enPassant != null && enPassant.color != color && enPassant.pieceFile == diagFile && enPassant.pieceRank == diagRank) {
				enPassantPossible = true;
				moves.add(diag);
			}
			FullPiece piece = Chess.squares.get(diag);
			if (piece != null && piece.color != color) {
				moves.add(diag);
			}
		}
		else {
			int diagRank1 = pieceRank+(x*1);
			FullPiece.PieceFile diagFile1 = FullPiece.intToFile(intFile-1);
			int diagRank2 = pieceRank+(x*1);
			FullPiece.PieceFile diagFile2 = FullPiece.intToFile(intFile+1);
			Square diag1 = new Square(diagFile1, diagRank1);
			Square diag2 = new Square(diagFile2, diagRank2);
			FullPiece piece1 = Chess.squares.get(diag1);
			FullPiece piece2 = Chess.squares.get(diag2);
			if (enPassant != null && enPassant.color != color && enPassant.pieceFile == diagFile1 && enPassant.pieceRank == diagRank1) {
				enPassantPossible = true;
				moves.add(diag1);
			}
			if (enPassant != null && enPassant.color != color && enPassant.pieceFile == diagFile2 && enPassant.pieceRank == diagRank2) {
				enPassantPossible = true;
				moves.add(diag2);
			}
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

class Rook extends FullPiece {

	Rook(PieceType pieceType, PieceFile pieceFile, int pieceRank) {
		super(pieceType, pieceFile, pieceRank);		
	}

	//idea here is to go, say, right from rook and then keep looking right until hit piece or edge of board
	HashSet<Square> see() {
		HashSet<Square> moves = new HashSet<Square>();
		int intFile = FullPiece.fileToInt(pieceFile);
		for (int i = intFile - 1; i >= 1; i--) {
			Square move = new Square(FullPiece.intToFile(i), pieceRank);
			FullPiece piece = Chess.squares.get(move);
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
			Square move = new Square(FullPiece.intToFile(i), pieceRank);
			FullPiece piece = Chess.squares.get(move);
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
			FullPiece piece = Chess.squares.get(move);
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
			FullPiece piece = Chess.squares.get(move);
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

class Knight extends FullPiece {

	Knight(PieceType pieceType, PieceFile pieceFile, int pieceRank) {
		super(pieceType, pieceFile, pieceRank);
	}

	//knight has 8 possible moves. if either of those is outside of board (> 8 or < 1) then it's off board, not in if statement
	//if it's not a opposite color piece or empty then it's not added
	HashSet<Square> see() {
		HashSet<Square> moves = new HashSet<Square>();
		int intFile = FullPiece.fileToInt(pieceFile);
		int[][] sqrs = {{intFile - 1, pieceRank - 2}, {intFile - 1, pieceRank + 2}, {intFile - 2, pieceRank - 1}, {intFile - 2, pieceRank + 1},
		{intFile + 1, pieceRank - 2}, {intFile + 1, pieceRank + 2}, {intFile + 2, pieceRank - 1}, {intFile + 2, pieceRank + 1}};
		for (int[] i : sqrs) {
			if (i[0] <= 8 && i[0] >= 1 && i[1] <= 8 && i[1] >= 1) {
				Square move = new Square(FullPiece.intToFile(i[0]), i[1]);
				FullPiece piece = Chess.squares.get(move);
				if (piece == null || piece.color != color) {
					moves.add(move);
				}
			}
		}
		return moves;
	}
}

class Bishop extends FullPiece {

	Bishop(PieceType pieceType, PieceFile pieceFile, int pieceRank) {
		super(pieceType, pieceFile, pieceRank);
	}

	//check comment above rook's 'see' function. same principle
	HashSet<Square> see() {
		HashSet<Square> moves = new HashSet<Square>();
		int intFile = FullPiece.fileToInt(pieceFile);
		for(int i = intFile + 1, j = pieceRank + 1; i <= 8 && j <= 8; i++, j++) {
			Square move = new Square(FullPiece.intToFile(i), j);
			FullPiece piece = Chess.squares.get(move);
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
			Square move = new Square(FullPiece.intToFile(i), j);
			FullPiece piece = Chess.squares.get(move);
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
			Square move = new Square(FullPiece.intToFile(i), j);
			FullPiece piece = Chess.squares.get(move);
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
			Square move = new Square(FullPiece.intToFile(i), j);
			FullPiece piece = Chess.squares.get(move);
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

class Queen extends FullPiece {

	Queen(PieceType pieceType, PieceFile pieceFile, int pieceRank) {
		super(pieceType, pieceFile, pieceRank);
	}

	HashSet<Square> see() {
		//should be able to just concat the for loops from bishop + rook
		HashSet<Square> moves = new HashSet<Square>();
		int intFile = FullPiece.fileToInt(pieceFile);
		for (int i = intFile - 1; i >= 1; i--) {
			Square move = new Square(FullPiece.intToFile(i), pieceRank);
			FullPiece piece = Chess.squares.get(move);
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
			Square move = new Square(FullPiece.intToFile(i), pieceRank);
			FullPiece piece = Chess.squares.get(move);
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
			FullPiece piece = Chess.squares.get(move);
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
			FullPiece piece = Chess.squares.get(move);
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
		for(int i = intFile + 1, j = pieceRank + 1; i <= 8 && j <= 8; i++, j++) {
			Square move = new Square(FullPiece.intToFile(i), j);
			FullPiece piece = Chess.squares.get(move);
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
			Square move = new Square(FullPiece.intToFile(i), j);
			FullPiece piece = Chess.squares.get(move);
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
			Square move = new Square(FullPiece.intToFile(i), j);
			FullPiece piece = Chess.squares.get(move);
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
			Square move = new Square(FullPiece.intToFile(i), j);
			FullPiece piece = Chess.squares.get(move);
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

class King extends FullPiece {

	King(PieceType pieceType, PieceFile pieceFile, int pieceRank) {
		super(pieceType, pieceFile, pieceRank);
	}
	
	HashSet<Square> see() {
		HashSet<Square> moves = new HashSet<Square>();
		int intFile = FullPiece.fileToInt(pieceFile);
		int[][] sqrs = {{intFile + 1, pieceRank + 1}, {intFile + 1, pieceRank}, {intFile + 1, pieceRank - 1}, {intFile, pieceRank + 1},
		{intFile, pieceRank - 1}, {intFile - 1, pieceRank + 1}, {intFile - 1, pieceRank}, {intFile - 1, pieceRank - 1}};
		for (int[] i : sqrs) {
			if (i[0] <= 8 && i[0] >= 1 && i[1] <= 8 && i[1] >= 1) {
				Square move = new Square(FullPiece.intToFile(i[0]), i[1]);
				FullPiece piece = Chess.squares.get(move);
				if ((piece == null || piece.color != color) /*&& !Chess.isKingInCheck(move)*/) { 
					moves.add(move);
				}
			}
		}
		if (color == Chess.Player.white && whiteCastleLong) {
			FullPiece piece1 = Chess.squares.get(new Square(ReturnPiece.PieceFile.b, 1));
			FullPiece piece2 = Chess.squares.get(new Square(ReturnPiece.PieceFile.c, 1));
			FullPiece piece3 = Chess.squares.get(new Square(ReturnPiece.PieceFile.d, 1));
			if (piece1 == null && piece2 == null && piece3 == null) {
				moves.add(new Square(ReturnPiece.PieceFile.c, 1));
			}
		}
		else if (color == Chess.Player.white && whiteCastleShort) {
			FullPiece piece1 = Chess.squares.get(new Square(ReturnPiece.PieceFile.f, 1));
			FullPiece piece2 = Chess.squares.get(new Square(ReturnPiece.PieceFile.g, 1));
			if (piece1 == null && piece2 == null) {
				moves.add(new Square(ReturnPiece.PieceFile.g, 1));
			}
		}
		else if (color == Chess.Player.black && blackCastleLong) {
			FullPiece piece1 = Chess.squares.get(new Square(ReturnPiece.PieceFile.b, 8));
			FullPiece piece2 = Chess.squares.get(new Square(ReturnPiece.PieceFile.c, 8));
			FullPiece piece3 = Chess.squares.get(new Square(ReturnPiece.PieceFile.d, 8));
			if (piece1 == null && piece2 == null && piece3 == null) {
				moves.add(new Square(ReturnPiece.PieceFile.c, 8));
			}
		}
		else if (color == Chess.Player.black && blackCastleShort) {
			FullPiece piece1 = Chess.squares.get(new Square(ReturnPiece.PieceFile.f, 8));
			FullPiece piece2 = Chess.squares.get(new Square(ReturnPiece.PieceFile.g, 8));
			if (piece1 == null && piece2 == null) {
				moves.add(new Square(ReturnPiece.PieceFile.g, 8));
			}
		}
		return moves;
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