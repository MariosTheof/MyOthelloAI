package Othello;

import java.lang.reflect.Array;
import java.util.*;

// TODO stability() returns 0; needs fixing
// TODO copyBoard() probably needs improvement and tweaking // updatesBoard
// TODO squareWeights()

public class Board{

	public static enum Piece{
		BLACK, // ίσως τα αλλάξω σε '1' ή '-1' λόγω ui
		WHITE;
	}

	public Piece[][] board;
	public Piece currentPlayerPiece;
	private int counter;
	public int numberOfWhiteDisks;
	public int numberOfBlackDisks;
	

	Point topLeftStartingPointDownDir = new Point(0,0);
	Point topLeftStartingPointRightDir = new Point(0,0);
	
	Point topRightStartingPointDownDir = new Point(0,7);
	Point topRightStartingPointLeftDir = new Point(0,7);
	
	Point bottomLeftStartingPointUpDir = new Point(7,0);
	Point bottomLeftStartingPointRightDir = new Point(7,0);
	
	Point bottomRightStartingPointUpDir = new Point(7,7);
	Point bottomRightStartingPointLeftDir = new Point(7,7);


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
			new Point(1, 0),
			new Point(1, 1),
			new Point(0, 1),
			new Point(-1, 1),
			new Point(-1, 0),
			new Point(-1, -1),
			new Point(0, -1),
			new Point(1, -1),
	};

	interface CellHandler {
		boolean handleCell(int row, int col, Piece piece);

		boolean handleCell(Point step, int row, int col, Piece piece);
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

	void iterateCellsForStability(Point start, Point step, CellHandler handler) {
		for (int row = start.row + step.row, col = start.col + step.col;
				isValidPosition(row,col);
				row += step.row, col += step.col) {

			Piece piece = board[row][col]; 
			// empty cell
			if (piece == null) // fillArray() with EMPTY or use NULL
				break;
			// handler can stop iteration
			if (handler.handleCell(step, row, col, piece) == false)
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

		@Override
		public boolean handleCell(Point step, int row, int col, Piece piece) {
			// TODO Auto-generated method stub
			return false;
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

		@Override
		public boolean handleCell(Point step, int row, int col, Piece piece) {
			// TODO Auto-generated method stub
			return false;
		}
	}

	class StabilityHandler implements CellHandler {
		private final Piece myPiece;


		public StabilityHandler(Piece myPiece) {
			this.myPiece = myPiece;
		}

		public boolean handleCell(Point step,int row, int col, Piece piece) {
			if (piece == myPiece){				
				setNewStartingPoint(step,row,col);
				return true;
			}

			return false;
		} 

		public void setNewStartingPoint(Point step, int row, int col){
			if (step.row == 1 && step.col == 0 && row == 0) {		  
				topLeftStartingPointDownDir = new Point(row,col);
			}else if (step.row == 1 && step.col == 0 && row != 0 ){
				topRightStartingPointDownDir = new Point(row,col);
			}
			if (step.row == 0 && step.col == 1 && col == 0 ){
				topLeftStartingPointRightDir = new Point(row,col);
			}else if(step.row == 0 && step.col == 1 && col != 0 ){
				bottomLeftStartingPointRightDir = new Point(row,col);
			}
			if (step.row == -1 && step.col == 0 && row == 0){
				bottomRightStartingPointUpDir = new Point(row,col);
			}else if (step.row == -1 && step.col == 0 && row != 0){
				bottomLeftStartingPointUpDir = new Point(row,col);
			}
			if (step.row == 0 && step.col == -1 && col == 0 ) {
				topRightStartingPointLeftDir = new Point(row,col);
			}else if (step.row == 0 && step.col == -1 && col == 0){
				topLeftStartingPointRightDir = new Point(row,col);
			}
		}



		@Override
		public boolean handleCell(int row, int col, Piece piece) {
			// TODO Auto-generated method stub
			return false;
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
		//  fillArray(this.board);
		this.board[3][3] = Piece.WHITE;
		this.board[3][4] = Piece.BLACK;
		this.board[4][3] = Piece.BLACK;
		this.board[4][4] = Piece.WHITE;

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

	public boolean placePiece(int i, int j) {
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

	public String getCurrentPlayerPiece(){
		if (currentPlayerPiece == Piece.BLACK){
			return "Black";
		}else {
			return "White";
		}
	}

	private void changeTurn() {
		if (currentPlayerPiece == Piece.BLACK){
			currentPlayerPiece = Piece.WHITE;
		} else {
			currentPlayerPiece = Piece.BLACK;
		}
	}

	public boolean finished(){
		if(this.counter > 63 /* || legalMovesCounter == 0 */){
			return true;
		}else{
			return false;
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
  	int mobility(Piece currentPlayerPiece ){
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
  

	
	private int numberOfPotentialMoves(int row, int col, int mobilityB) {
		Piece otherPiece = ( currentPlayerPiece == Piece.BLACK ) ? Piece.WHITE : Piece.BLACK;
		Point start = new Point(row, col);
		for (Point step : possibleDirections) {
			// handler is stateful so create new for each direction
			CheckCellHandler checkCellHandler = new CheckCellHandler(otherPiece);
			iterateCells(start, step, checkCellHandler);
			if (checkCellHandler.isGoodMove())
				mobilityB++;
		}
		return mobilityB;
	}

	/*
	 * 
	 */
	public int stability(){
		
		Piece otherPiece = ( currentPlayerPiece == Piece.BLACK ) ? Piece.WHITE : Piece.BLACK;
		Point stepDownDir = new Point (1,0);
		Point stepRightDir = new Point (0,1);
		Point stepUpDir = new Point (-1,0);
		Point stepLeftDir = new Point (0,-1);
		
		
		/* Starting points for both directions of the top left corner. */
		if (  topLeftStartingPointDownDir != null ){
			StabilityHandler stabilityCellHandlerDownDir = new StabilityHandler(otherPiece);
			iterateCellsForStability(topLeftStartingPointDownDir, stepDownDir ,stabilityCellHandlerDownDir );
		}
		if ( topLeftStartingPointRightDir != null ) {
			StabilityHandler stabilityCellHandlerRightDir = new StabilityHandler(otherPiece);
			iterateCellsForStability(topLeftStartingPointRightDir, stepRightDir ,stabilityCellHandlerRightDir );
		}
		
		/* Starting points for both directions of the top right corner. */
		if ( topRightStartingPointDownDir != null ) {
			StabilityHandler stabilityCellHandlerDownDir = new StabilityHandler(otherPiece);
			iterateCellsForStability(topRightStartingPointDownDir, stepDownDir ,stabilityCellHandlerDownDir );
		}
		if ( topRightStartingPointLeftDir != null ) {
			StabilityHandler stabilityCellHandlerLeftDir = new StabilityHandler(otherPiece);
			iterateCellsForStability(topRightStartingPointLeftDir, stepLeftDir ,stabilityCellHandlerLeftDir );
		}
		
		/* Starting points for both directions of the bottom left corner. */
		if ( bottomLeftStartingPointUpDir != null) {
			StabilityHandler stabilityCellHandlerUpDir = new StabilityHandler(otherPiece);
			iterateCellsForStability(bottomLeftStartingPointUpDir, stepUpDir ,stabilityCellHandlerUpDir );
		}
		if ( bottomLeftStartingPointRightDir != null){
			StabilityHandler stabilityCellHandlerRightDir = new StabilityHandler(otherPiece);
			iterateCellsForStability(bottomLeftStartingPointRightDir, stepRightDir ,stabilityCellHandlerRightDir );
		}
		
		/* Starting points for both directions of the bottom right corner. */
		if (bottomRightStartingPointLeftDir != null) {
			StabilityHandler stabilityCellHandlerLeftDir = new StabilityHandler(otherPiece);
			iterateCellsForStability(bottomRightStartingPointLeftDir, stepLeftDir ,stabilityCellHandlerLeftDir );
		}
		if (bottomRightStartingPointUpDir != null) {
			StabilityHandler stabilityCellHandlerUpDir = new StabilityHandler(otherPiece);
			iterateCellsForStability(bottomRightStartingPointUpDir, stepUpDir ,stabilityCellHandlerUpDir );
		}
		
		return 0;
	}
	
	
	
	public int discDifference() {
		return numberOfBlackDisks - numberOfWhiteDisks;
	}
	
	//public int squareWeights() {
		int[] weights = {
		         200, -100, 100,  50,  50, 100, -100,  200,
		        -100, -200, -50, -50, -50, -50, -200, -100,
		         100,  -50, 100,   0,   0, 100,  -50,  100,
		          50,  -50,   0,   0,   0,   0,  -50,   50,
		          50,  -50,   0,   0,   0,   0,  -50,   50,
		         100,  -50, 100,   0,   0, 100,  -50,  100,
		        -100, -200, -50, -50, -50, -50, -200, -100,
		         200, -100, 100,  50,  50, 100, -100,  200,	
		};
	//}
	//Αν θέλουμε αλλάζουμε το return και βαζουμε παραμετρο currentPlayerPiece or smth
	public int corners(){
		  int blackCorners = 0;
		  int whiteCorners = 0;
		  List <Point> cornerList = new ArrayList<Point>() ;
		 	 cornerList.add(new Point(0,0));
		 	 cornerList.add(new Point(0,7));
		 	 cornerList.add(new Point(7,0));
		 	 cornerList.add(new Point(7,7));
		 	 for( Point corners: cornerList) {
		 			if(board[corners.row][corners.col] == Piece.BLACK){
		 				blackCorners++;
		 			} else if (board[corners.row][corners.col] == Piece.WHITE) {
		 				whiteCorners++;
		 			}
		 	 }
		 	// System.out.print("\nblack corners " + blackCorners);
		 	// System.out.print("\nwhite corners "+ whiteCorners);
		 	 return blackCorners - whiteCorners;
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
			this.currentPlayerPiece = Piece.WHITE;
		}else {
			this.currentPlayerPiece = Piece.BLACK;
		}
	}

    public int evaluate(){
        if (counter < 20){
        return 5*discDifference()
        		+ 10*mobility(currentPlayerPiece);
        }else if (counter < 58){ 
        	return 10*discDifference()
                    + 2*mobility(currentPlayerPiece)
                   // + 2*squareWeights()
                    + 10000*corners()
        			+ 10000*stability();
        }else {
        	return 500*discDifference()
        			//+ 500*parity()
        			+10000*corners()
        			+10000*stability();
        }
		

    }

}
