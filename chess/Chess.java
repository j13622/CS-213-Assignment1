package chess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.EnumMap;
import java.util.EnumSet;

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

public class Chess {
	static enum Player { white, black }
	static ArrayList<FullPiece> pieces;
	static HashMap<Square, FullPiece> squares;
	static Player currentPlayer;
	static ReturnPlay state;

	static boolean checkLastMove;
	static boolean promotedThisMove;
	
	public static ArrayList<ReturnPiece> castPieceArray(ArrayList<FullPiece> fullPieces) {
		ArrayList<ReturnPiece> returnPieces = new ArrayList<ReturnPiece>();
		for(FullPiece i : fullPieces) {
			returnPieces.add((ReturnPiece) i);
		}
		return returnPieces;
	}

	public static Player opponentColor(){
		Player opponent = (currentPlayer == Player.white) ? Player.black : Player.white;
		return opponent;
	}

	public static FullPiece getKing(Player player){
		FullPiece kng;
		for (FullPiece potentiallyKing : pieces){
			if (player == Chess.Player.white && potentiallyKing.pieceType == ReturnPiece.PieceType.WK) {
				return potentiallyKing;
			}
			else if (player == Chess.Player.black && potentiallyKing.pieceType == ReturnPiece.PieceType.BK) {
				return potentiallyKing;
			}
		}
		return null;
	}

	public static boolean isKingInCheck(FullPiece kingPiece, Player player){
		Square kingSquare = new Square(kingPiece.pieceFile, kingPiece.pieceRank);
		HashSet<Square> allSquaresSeen = new HashSet<Square>();
		for (FullPiece opponentPiece : pieces){
			if (opponentPiece.color != player){
				HashSet<Square> currentOpponentPieceSees = new HashSet<Square>();
				currentOpponentPieceSees = opponentPiece.see();
				opponentPiece.enPassantPossible = false;
				allSquaresSeen.addAll(currentOpponentPieceSees);
			}
		}
		if (allSquaresSeen.contains(kingSquare)){return true;}
		return false;
	}

	public static boolean checkmate(FullPiece opponentKing, Player opponent){
        //create an arraylist of arraylists, where each row is a legal move, and the entries in each row hold args to be fed into move function
		ArrayList<ArrayList<Object>> allLegalMoves = new ArrayList<ArrayList<Object>>();
        for (FullPiece opponentPiece : pieces){
            if (opponentPiece.color == opponent){
                HashSet<Square> currentOpponentPieceSees = new HashSet<Square>();
				currentOpponentPieceSees = opponentPiece.see();
                for (Square move : currentOpponentPieceSees){
                    ArrayList<Object> legalMove = new ArrayList<Object>();
                    legalMove.add(opponentPiece);
                    legalMove.add(move);
                    allLegalMoves.add(legalMove);
                }
            }
        }
        //for all legal moves, call the move function, determine if legal, undo move; if king is moved, check that square instead
        for (ArrayList<Object> move : allLegalMoves){
            Object[][] undoData = moveFctn((FullPiece)move.get(0), (Square)move.get(1));
			FullPiece piece = (FullPiece)move.get(0);
			piece.enPassantPossible = false;
			if (!isKingInCheck(opponentKing, opponent)) {
				undoMove((FullPiece)move.get(0), (Square)move.get(1), undoData);
				return false;
			}
            undoMove((FullPiece)move.get(0), (Square)move.get(1), undoData);
        }
		return true;
    }


	public static Object[][] moveFctn(FullPiece firstPiece, Square secondSquare) {
		Object[][] toReturn = new Object[3][3];

		//castling
		ReturnPiece.PieceFile initialRookFile = null;
		int castleRank = 0;
		ReturnPiece.PieceFile finalRookFile = null;
		if (firstPiece.pieceType == ReturnPiece.PieceType.WK && firstPiece.pieceFile == ReturnPiece.PieceFile.e && secondSquare.file == ReturnPiece.PieceFile.c) {
			initialRookFile = ReturnPiece.PieceFile.a;
			castleRank = 1;
			finalRookFile = ReturnPiece.PieceFile.d;
		}
		if (firstPiece.pieceType == ReturnPiece.PieceType.WK && firstPiece.pieceFile == ReturnPiece.PieceFile.e && secondSquare.file == ReturnPiece.PieceFile.g) {
			initialRookFile = ReturnPiece.PieceFile.h;
			castleRank = 1;
			finalRookFile = ReturnPiece.PieceFile.f;
		}
		if (firstPiece.pieceType == ReturnPiece.PieceType.BK && firstPiece.pieceFile == ReturnPiece.PieceFile.e && secondSquare.file == ReturnPiece.PieceFile.c) {
			initialRookFile = ReturnPiece.PieceFile.a;
			castleRank = 8;
			finalRookFile = ReturnPiece.PieceFile.d;
		}
		if (firstPiece.pieceType == ReturnPiece.PieceType.BK && firstPiece.pieceFile == ReturnPiece.PieceFile.e && secondSquare.file == ReturnPiece.PieceFile.g) {
			initialRookFile = ReturnPiece.PieceFile.h;
			castleRank = 8;
			finalRookFile = ReturnPiece.PieceFile.f;
		}
		if (initialRookFile != null) {
			Square rookSqr = new Square(initialRookFile, castleRank);
			FullPiece rook = squares.get(rookSqr);
			Square finalRookSquare = new Square(finalRookFile, castleRank); //must rm this
			toReturn[2][0] = rookSqr; //must put rook back here
			toReturn[2][1] = rook; //must move this back
			toReturn[2][2] = finalRookSquare;
			squares.put(finalRookSquare, rook);
			rook.pieceFile = finalRookFile;
			squares.remove(rookSqr);
		}

		FullPiece potentialPiece = null;
		Square firstSquare = new Square(firstPiece.pieceFile, firstPiece.pieceRank);
		firstPiece.pieceFile = secondSquare.file;
		firstPiece.pieceRank = secondSquare.rank;
		toReturn[0][0] = firstSquare;
		squares.remove(firstSquare);
		potentialPiece = squares.get(secondSquare);
		if (potentialPiece != null) {
			toReturn[0][1] = potentialPiece;
			pieces.remove(potentialPiece);
		}
		squares.put(secondSquare, firstPiece); //must remove later and put second piece back

		//enPassant
		Square enPassantSqr = null;
		ReturnPiece passantPawn = null;
		if (FullPiece.enPassant != null && firstPiece.enPassantPossible && FullPiece.enPassant.pieceFile == secondSquare.file && FullPiece.enPassant.pieceRank == secondSquare.rank) {
			int x;
			if (currentPlayer == Chess.Player.white) {
				x = -1;
			}
			else {
				x = 1;
			}
			enPassantSqr = new Square(secondSquare.file, secondSquare.rank+(x*1));
			passantPawn = squares.get(enPassantSqr);
			toReturn[1][0] = enPassantSqr; //must put back
			toReturn[1][1] = passantPawn; //must put back
			squares.remove(enPassantSqr);
			pieces.remove(passantPawn);
		}
		return toReturn;
	}

	public static void undoMove(FullPiece firstPiece, Square secondSquare, Object[][] undoData) {
		//undoes regular move
		Square firstSquare = (Square) undoData[0][0];
		firstPiece.pieceFile = firstSquare.file;
		firstPiece.pieceRank = firstSquare.rank;
		squares.put(firstSquare, firstPiece);
		if (undoData[0][1] != null) {
			FullPiece secondPiece = (FullPiece) undoData[0][1];
			pieces.add(secondPiece);
			squares.put(secondSquare, secondPiece);
		} else {
			squares.remove(secondSquare);
		}
		//undoes enpassant
		if (undoData[1][0] != null) {
			Square passantSqr = (Square) undoData[1][0];
			FullPiece passantPiece = (FullPiece) undoData[1][1];
			squares.put(passantSqr, passantPiece);
			pieces.add(passantPiece);
		}
		//undoes castle
		if (undoData[2][0] != null) {
			Square firstRookSquare = (Square) undoData[2][0];
			FullPiece rook = (FullPiece) undoData[2][1];
			Square finalRookSquare = (Square) undoData[2][2];
			squares.put(firstRookSquare, rook);
			squares.remove(finalRookSquare);
			rook.pieceFile = firstRookSquare.file;
		}
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

		state.message = null;
		boolean draw = false;

		String[] tempArray = move.split(" ");
		int count = 0;
		for (int i = 0; i < tempArray.length; i++) {
			tempArray[i] = tempArray[i].replaceAll(" ", "");
			if (!tempArray[i].equals("")) {
				count++;
			}
		}
		String[] ret = new String[count];
		count = 0;
		for (int i = 0; i < tempArray.length; i++) {
			if (!tempArray.equals("")) {
				ret[count] = tempArray[i];
				count++;
			}
		}

		if (ret.length == 1 && ret[0].equals("resign")) {
			if (currentPlayer == Player.white) {
				state.message = ReturnPlay.Message.RESIGN_BLACK_WINS;
			}
			else {
				state.message = ReturnPlay.Message.RESIGN_WHITE_WINS;
			}
			return state;
		}

		for (int i = 0; i < ret.length; i++) {
			if (ret[i].equals("draw?")) {
				draw = true;
				state.message = ReturnPlay.Message.DRAW;
			}
		}

		ReturnPiece.PieceFile firstFile = ReturnPiece.PieceFile.valueOf("" + ret[0].charAt(0));
		int firstRank = ret[0].charAt(1) - '0';
		Square firstSquare = new Square(firstFile, firstRank);
		FullPiece firstPiece = squares.get(firstSquare);
		
		if (firstPiece == null || currentPlayer != firstPiece.color) {
			state.message = ReturnPlay.Message.ILLEGAL_MOVE;
			return state;
		}

		//can't castle when king is in check
		if (checkLastMove && ((ret[0].equals("e1") && ret[1].equals("g1")) || (ret[0].equals("e1") && ret[1].equals("c1")) || 
		(ret[0].equals("e8") && ret[1].equals("g8")) || (ret[0].equals("e8") && ret[1].equals("c8")))){
			state.message = ReturnPlay.Message.ILLEGAL_MOVE;
			return state;
		}

		ReturnPiece.PieceFile secondFile = ReturnPiece.PieceFile.valueOf("" + ret[1].charAt(0));
		int secondRank = ret[1].charAt(1) - '0';
		Square secondSquare = new Square(secondFile, secondRank);
		HashSet<Square> possibleMoves = firstPiece.see();

		Object[][] priorStatus = null;
		if (possibleMoves.contains(secondSquare)) {
			priorStatus = moveFctn(firstPiece, secondSquare);
		} else {
			state.message = ReturnPlay.Message.ILLEGAL_MOVE;
			return state;
		}

		firstPiece.enPassantPossible = false;

		// did current player put self in check
		FullPiece currentKing = getKing(currentPlayer);
		if (isKingInCheck(currentKing, currentPlayer)){
			undoMove(firstPiece, secondSquare, priorStatus);
			state.message = ReturnPlay.Message.ILLEGAL_MOVE;
			return state;
		}

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
		int retLength = ret.length;
		//2 if statements below handle promotion
		if (secondRank == 8 && firstPiece.pieceType == ReturnPiece.PieceType.WP){
			pieces.remove(firstPiece);
			squares.remove(firstSquare);
			squares.remove(secondSquare);
			if (retLength == 3 || retLength == 4){
				if (!ret[2].equals("draw?")){
					if (ret[2].equals("N")){
						promPiece = new Knight(ReturnPiece.PieceType.WN, secondFile, secondRank);
					}
					else if (ret[2].equals("B")){
						promPiece = new Bishop(ReturnPiece.PieceType.WB, secondFile, secondRank);
					}
					else if (ret[2].equals("R")){
						promPiece = new Rook(ReturnPiece.PieceType.WR, secondFile, secondRank);
					}
					else {
						promPiece = new Queen(ReturnPiece.PieceType.WQ, secondFile, secondRank);
					}
				}
			}
			promotedThisMove = true;
		}
		if (secondRank == 1 && firstPiece.pieceType == ReturnPiece.PieceType.BP){
			pieces.remove(firstPiece);
			squares.remove(firstSquare);
			squares.remove(secondSquare);
			if (retLength == 3 || retLength == 4){
				if (!ret[2].equals("draw?")){
					if (ret[2].equals("N")){
						promPiece = new Knight(ReturnPiece.PieceType.BN, secondFile, secondRank);
					}
					else if (ret[2].equals("B")){
						promPiece = new Bishop(ReturnPiece.PieceType.BB, secondFile, secondRank);
					}
					else if (ret[2].equals("R")){
						promPiece = new Rook(ReturnPiece.PieceType.BR, secondFile, secondRank);
					}
					else {
						promPiece = new Queen(ReturnPiece.PieceType.BQ, secondFile, secondRank);
					}
				}
			}
			promotedThisMove = true;
		}
		if (promPiece != null) {
			squares.put(new Square(secondFile, secondRank), promPiece);
			pieces.add(promPiece);
		}

		checkLastMove = false;
		state.piecesOnBoard = castPieceArray(pieces);

		if (draw) {
			return state;
		}

		//did current player put the opponent in check, and if so, is it mate
		Player opponent = opponentColor();
		FullPiece oppKing = getKing(opponent);
		if (isKingInCheck(oppKing, opponent)){
			if (checkmate(oppKing, opponent)){
				if (opponent == Chess.Player.white){
					state.message = ReturnPlay.Message.CHECKMATE_BLACK_WINS;
				} else {
					state.message = ReturnPlay.Message.CHECKMATE_WHITE_WINS;
				}
				return state;
			}

			checkLastMove = true; //for next move, to ensure player gets out of check
			state.message = ReturnPlay.Message.CHECK;
		}

		if (currentPlayer == Player.white) {
			currentPlayer = Player.black;
		} else {
			currentPlayer = Player.white;
		}
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
	boolean enPassantPossible = false;
	Chess.Player color;
	static FullPiece enPassant;
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

	//defined in subclass
	abstract HashSet<Square> pawnCaptures();

	//defined in subclass
	protected abstract FullPiece clone();
}

class Pawn extends FullPiece {

	Pawn(PieceType pieceType, PieceFile pieceFile, int pieceRank) {
		super(pieceType, pieceFile, pieceRank);
	}

	public FullPiece clone(){
		Pawn copiedPawn = new Pawn(pieceType, pieceFile, pieceRank);
		return copiedPawn;
	}

	public HashSet<Square> pawnCaptures(){
		HashSet<Square> moves = new HashSet<Square>();
		int intFile = FullPiece.fileToInt(pieceFile);
		boolean leftFile = intFile == 1;
		boolean rightFile = intFile == 8;
		int x;
		if (color == Chess.Player.white) {
			x = 1;
		}
		else {
			x = -1;
		}
		if (leftFile) {
			int diagRank = pieceRank+(x*1);
			FullPiece.PieceFile diagFile = FullPiece.intToFile(intFile+1);
			Square diag = new Square(diagFile, diagRank);
			moves.add(diag);
		}
		else if (rightFile) {
			int diagRank = pieceRank+(x*1);
			FullPiece.PieceFile diagFile = FullPiece.intToFile(intFile-1);
			Square diag = new Square(diagFile, diagRank);
			moves.add(diag);
		}
		else {
			int diagRank1 = pieceRank+(x*1);
			FullPiece.PieceFile diagFile1 = FullPiece.intToFile(intFile-1);
			int diagRank2 = pieceRank+(x*1);
			FullPiece.PieceFile diagFile2 = FullPiece.intToFile(intFile+1);
			Square diag1 = new Square(diagFile1, diagRank1);
			Square diag2 = new Square(diagFile2, diagRank2);
			moves.add(diag1);
			moves.add(diag2);
		}
		return moves;
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

	public FullPiece clone(){
		Rook copiedRook = new Rook(pieceType, pieceFile, pieceRank);
		return copiedRook;
	}

	public HashSet<Square> pawnCaptures(){
		HashSet<Square> intelligentDesign = new HashSet<Square>();
		return intelligentDesign;
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

	public FullPiece clone(){
		Knight copiedKnight = new Knight(pieceType, pieceFile, pieceRank);
		return copiedKnight;
	}

	public HashSet<Square> pawnCaptures(){
		HashSet<Square> intelligentDesign = new HashSet<Square>();
		return intelligentDesign;
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

	public FullPiece clone(){
		Bishop copiedBishop = new Bishop(pieceType, pieceFile, pieceRank);
		return copiedBishop;
	}

	public HashSet<Square> pawnCaptures(){
		HashSet<Square> intelligentDesign = new HashSet<Square>();
		return intelligentDesign;
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

	public FullPiece clone(){
		Queen copiedQueen = new Queen(pieceType, pieceFile, pieceRank);
		return copiedQueen;
	}

	public HashSet<Square> pawnCaptures(){
		HashSet<Square> intelligentDesign = new HashSet<Square>();
		return intelligentDesign;
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

	public FullPiece clone(){
		King copiedKing = new King(pieceType, pieceFile, pieceRank);
		return copiedKing;
	}

	public HashSet<Square> pawnCaptures(){
		HashSet<Square> intelligentDesign = new HashSet<Square>();
		return intelligentDesign;
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
				if ((piece == null || piece.color != color)) { 
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
		if (color == Chess.Player.white && whiteCastleShort) {
			FullPiece piece1 = Chess.squares.get(new Square(ReturnPiece.PieceFile.f, 1));
			FullPiece piece2 = Chess.squares.get(new Square(ReturnPiece.PieceFile.g, 1));
			if (piece1 == null && piece2 == null) {
				moves.add(new Square(ReturnPiece.PieceFile.g, 1));
			}
		}
		if (color == Chess.Player.black && blackCastleLong) {
			FullPiece piece1 = Chess.squares.get(new Square(ReturnPiece.PieceFile.b, 8));
			FullPiece piece2 = Chess.squares.get(new Square(ReturnPiece.PieceFile.c, 8));
			FullPiece piece3 = Chess.squares.get(new Square(ReturnPiece.PieceFile.d, 8));
			if (piece1 == null && piece2 == null && piece3 == null) {
				moves.add(new Square(ReturnPiece.PieceFile.c, 8));
			}
		}
		if (color == Chess.Player.black && blackCastleShort) {
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