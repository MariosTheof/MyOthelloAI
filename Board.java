package Othello;

import java.util.*;

// TODO win conditions

public class Board{

	public static enum Piece{
		BLACK, // ίσως τα αλλάξω σε '1' ή '-1' λόγω ui
		WHITE;
	}

	public Piece[][] board;
	private Piece currentPlayerPiece;
	private int counter;

	public int numberOfWhiteDisks;
	public int numberOfBlackDisks;

	private static final Point cornerArray[] = new Point[]{
			new Point(0,0),
			new Point(0,7),
			new Point (7,0),
			new Point(7,7),
	};


	public static class Point {
		public final int row;
		public final int col;

		public Point(int row, int col) {
			this.row = row;
			this.col = col;
		}
	}

	/*Όλες οι κατευθύνσεις αντιπροσωπευμένες από έναν πίνακα*/
	private static final Point[] possibleDirections = new Point[]{
			new Point(1, 0), // κάτω
			new Point(1, 1), // κάτω & δεξιά
			new Point(0, 1), // δεξιά
			new Point(-1, 1), // δεξιά & πάνω
			new Point(-1, 0), // πάνω
			new Point(-1, -1), // πάνω & αριστερά
			new Point(0, -1), // αριστερά
			new Point(1, -1), //κάτω & αριστερά
	};

	interface CellHandler {
		boolean handleCell(int row, int col, Piece piece);
	}

	/*iterateCells() accepts some direction and navigates through it
  till it hits an empty cell or border */
	void iterateCells(Point start, Point step, CellHandler handler) {

		for (int row = start.row + step.row, col = start.col + step.col;
				isValidPosition(row,col);
				row += step.row, col += step.col) {

			Piece piece = board[row][col]; 
			// empty cell
			if (piece == null) // fillArray() with EMPTY or use NULL
				break;
			// handler can stop iteration
			if (handler.handleCell(row, col, piece) == false)
				break;
		}
	}


	static class CheckCellHandler implements CellHandler {
		private final Piece otherPiece;
		private boolean directionHasOpponentsPlayerPiece = false;
		private boolean endsWithMine = false;

		public CheckCellHandler(Piece otherPiece) {
			this.otherPiece = otherPiece;
		}

		@Override
		public boolean handleCell(int row, int column, Piece piece) {
			if (piece == otherPiece) {
				directionHasOpponentsPlayerPiece = true; 
				return true;
			} else {
				endsWithMine = true; 
				return false;
			}
		}

		public boolean isGoodMove() {
			return directionHasOpponentsPlayerPiece && endsWithMine;
		}


	}

	class FlipCellHandler implements CellHandler {
		private final Piece myPiece;
		private final Piece otherPiece;
		private final List<Point> currentFlipList = new ArrayList<Point>();

		public FlipCellHandler(Piece myPiece, Piece otherPiece) {
			this.myPiece = myPiece;
			this.otherPiece = otherPiece;
		}

		@Override
		public boolean handleCell(int row, int column, Piece piece) {
			if (piece == myPiece) {
				// flip all cells
				for (Point p : currentFlipList) {
					board[p.row][p.col] = myPiece;
				}
				return false;
			} else {
				currentFlipList.add(new Point(row, column));
				return true;
			}
		}


	}




	private boolean isValidPosition(int x, int y){
		if( x < 0 || x >= 8 || y < 0 || y >=  8 ){
			return false;
		}
		return true;
	}

	/*implement CheckCellHandler and iterate
  through all possible directions to see if we can flip in that direction*/
	int checkLegalPlay(int row, int col) {
		int checkLegalPlay = 0 ;
		Piece otherPiece = ( currentPlayerPiece == Piece.BLACK ) ? Piece.WHITE : Piece.BLACK;
		Point start = new Point(row, col);
		for (Point step : possibleDirections) {
			// handler is stateful so create new for each direction
			CheckCellHandler checkCellHandler = new CheckCellHandler(otherPiece);
			iterateCells(start, step, checkCellHandler);
			if (checkCellHandler.isGoodMove())
				checkLegalPlay++;

		}
		return checkLegalPlay;
	}

	/**
	 * @param row
	 * @param col
	 * Flips the pieces by iterating the board, in order to replace them with the appropriate colour tile.
	 */
	private void flipPieces(int row, int col) {
		Piece otherPiece = ( currentPlayerPiece == Piece.BLACK ) ? Piece.WHITE : Piece.BLACK;
		Point start = new Point(row,col);
		for (Point step: possibleDirections) {
			FlipCellHandler flipCellHandler = new FlipCellHandler(currentPlayerPiece, otherPiece);
			iterateCells(start,step,flipCellHandler);
		}
	}


	/*Αρχικοποίηση του πινακα*/
	public Board(){
		this.board = new Piece[8][8];
		this.currentPlayerPiece = Piece.BLACK;
		this.counter = 4;
		this.numberOfBlackDisks = 2;
		this.numberOfWhiteDisks = 2;
		// Αρχικές θέσεις

		this.board[3][3] = Piece.WHITE;
		this.board[3][4] = Piece.BLACK;
		this.board[4][3] = Piece.BLACK;
		this.board[4][4] = Piece.WHITE;
		// Test input for mobility, stability, discDif functions.
		//	this.board[2][2] = Piece.WHITE;
		//	this.board[1][1] = Piece.BLACK;
		/*this.board[1][0]= Piece.WHITE;
		this.board[2][0] = Piece.BLACK;
		this.board[3][0] = Piece.BLACK; */


	}

	public Piece[][] getBoard() {
		return board;
	}

	private int getCounter() {
		return this.counter;
	}

	public void setBoard(Piece[][] board) {
		this.board = board;
	}

	//placePieceForPlayer() & placePieceForAI()
	public boolean placePieceForPlayer(int i, int j) {
		if (this.checkLegalPlay(i,j) > 0) {
			this.board[i][j] = this.currentPlayerPiece;
			flipPieces(i,j);
			//updateBoard(i, j); // χρειάζονται τα updates ;;;;
			changeTurn();
			this.counter++;
			updateNumberOfDisks();
			return true;
		} else {
			System.out.println("Illegal piece placement \n");
			return false;
		}
	}

	public void placePieceForAi(int i, int j) {
		this.board[i][j] = this.currentPlayerPiece;
		flipPieces(i,j);
		this.counter++;
		updateNumberOfDisks();
		changeTurn();
	}

	public String getCurrentPlayerPiece(){
		if (currentPlayerPiece == Piece.BLACK){
			return "Black";
		}else {
			return "White";
		}
	}

	void changeTurn() {
		if (currentPlayerPiece == Piece.BLACK){
			currentPlayerPiece = Piece.WHITE;
		} else {
			currentPlayerPiece = Piece.BLACK;
		}
	}

	public int finished(){
		if(this.counter > 63) return -1;
		
		if ( mobility() == 0 ) { // no moves for currentPlayer
			return 0;
		}else {
			return 1;
		}
	}

	private void updateNumberOfDisks(){
		int white = 0;
		int black = 0;
		for(int i = 0; i < 8; i++){
			for(int j = 0; j < 8; j++){
				if(this.board[i][j] == Piece.BLACK){
					black++;
				}
				else if(this.board[i][j] == Piece.WHITE){
					white++;
				}
			}
		}
		this.numberOfBlackDisks = black;
		this.numberOfWhiteDisks = white;
	}

	public String winner(){
		if(this.numberOfBlackDisks > this.numberOfWhiteDisks){
			return "BLACK";
		}else if (this.numberOfBlackDisks < this.numberOfWhiteDisks){
			return "WHITE";
		}else{
			return null;
		}
	}

	/*Εκτυπώνει τον πίνακα στο cmd*/
	public void printBoard(){
		System.out.println("***********************************\n");
		System.out.println("|   || 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | ");
		for (int row = 0; row <= 7; row++){
			System.out.print("| " + (row + 1) + " |");
			for (int col = 0; col <= 7; col++){
				if (board[row][col] == Piece.BLACK){
					System.out.print("| " + "B" + " "); // ίσως θα βάλω \u25E6 και έτσι
				} else if(board[row][col] == Piece.WHITE){
					System.out.print("| " + "W" + " ");
				} else {
					System.out.print("| " + "-" + " ");
				}
			}
			System.out.print("| \n");
		}
	}/* /printBoard() */


	////////////////////////////////////////////////////////////////////

	/*
	 * Calculates the number of potential moves for each player
	 * 
	 * */
	int mobility(){
		int mobilityB = 0;
		for( int row=0; row < 7;row++){
			for( int col=0; col < 7;col++){
				if (board[row][col] == null ){
					mobilityB = mobilityB + checkLegalPlay(row,col);
				}
			}
		}
		return mobilityB;
	}



	//Stability function: Checks whether corner and edges cells are stable.
	public int stability(){
		int currStab = 0;
		int col1 = 1; int row1 = 1;  int col2 = 1; int row2 = 1;
		changeTurn();
		if (board[0][0]== currentPlayerPiece){
			currStab++;
			while(board[0][col1]== currentPlayerPiece) {
				currStab++; col1++;
			}
			while(board[row1][0]== currentPlayerPiece) {
				currStab++; row1++;
			}
		}
		if (board[0][7]== currentPlayerPiece){
			currStab++;
			int i = 1;
			while (board[0][7-i] == currentPlayerPiece) {
				if (i!=col1) {
					currStab++; i++;
				}else {
					break;
				}
			}
			while(board[row2][7] == currentPlayerPiece) {
				currStab++; row2++;
			}			  					
		}
		if (board[7][0]== currentPlayerPiece){
			currStab++;
			int i = 1;
			while (board[7-i][0]== currentPlayerPiece){
				if (i!=row1) {
					currStab++; i++;
				}else {
					break;
				}
			}
			while(board[7][col2]== currentPlayerPiece) {
				currStab++; col2++;
			}	
		}
		if (board[7][7]== currentPlayerPiece){
			currStab++;
			int i = 6;
			while(board[i][7]== currentPlayerPiece) {
				if (i!=row2) {
					currStab++; i--;
				} else {
					break;
				}
			}
			i=6;
			while(board[7][i]== currentPlayerPiece) {
				if (i!=col2) {
					currStab++; i--;
				}else {
					break;
				}
			}
		}
		return currStab;
	}


	public int discDifference() {
		return numberOfBlackDisks - numberOfWhiteDisks;
	}


	protected int[][] weights = {
			{ 120, -20, 20, 5, 5, 20, -20, 120 },
			{ -20, -40, -5, -5, -5, -5, -40, -20 },
			{ 20, -5, 1, 5, 3, 3, 15, -5, 20 }, 
			{ 5, -5, 3, 3, 3, 3, -5, 5 },
			{ 5, -5, 3, 3, 3, 3, -5, 5 }, 
			{ 20, -5, 15, 3, 3, 15, -5, 20 },
			{ -20, -40, -5, -5, -5, -5, -40, -20 },
			{ 120, -20, 20, 5, 5, 20, -20, 120 }
	};

	public int squareWeights() {
		int score = 0;
		for (int row = 0; row < 7; row++) {
			for (int col = 0; col < 7; col++ ) {
				if (board[row][col] == currentPlayerPiece) {
					score += weights[row][col];
				}else {
					score -= weights[row][col];
				}
			}
		}
		return score;
	}


	public int corners(){
		int blackCorners = 0;
		int whiteCorners = 0;


		for( Point corners: cornerArray) {
			if(board[corners.row][corners.col] == Piece.BLACK){
				blackCorners++;
			} else if (board[corners.row][corners.col] == Piece.WHITE) {
				whiteCorners++;
			}
		}
		if (currentPlayerPiece == Piece.BLACK) {
			return 100 * (whiteCorners - blackCorners)
					/ (blackCorners + whiteCorners + 1);
		}else {
			return 100 * (blackCorners - whiteCorners)
					/ (blackCorners + whiteCorners + 1);
		}
	}
	////////////////////////////////////////////////////////////////////

	public void copyBoard(Board b) {
		Piece [][] newB = b.getBoard();
		for (int row = 0; row < 7; row++) {
			for (int col = 0; col < 7; col++) {
				this.board[row][col] = newB[row][col];
			}
		}

		updateNumberOfDisks();
		this.counter = b.getCounter();
		if (b.currentPlayerPiece == Piece.WHITE) {
			this.currentPlayerPiece = Piece.BLACK;
		}else {
			this.currentPlayerPiece = Piece.WHITE;
		}
	}

	public int evaluate(){
		if (counter < 20){
			return 	20*squareWeights()
					+ 10*mobility()
					+ 10000*corners()
					+ 10000*stability();
		}else if (counter < 58){ 
			return 10*discDifference()
					+ 2*mobility()
					+ 10*squareWeights()
					+ 10000*corners()
					+ 10000*stability();
		}else {
			return 500*discDifference()
					+10000*corners()
					+10000*stability();
		}
	}

}
