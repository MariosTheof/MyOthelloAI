package Othello;

public class MinimaxAlphaBeta {

	private int depth;
	private boolean maximize;
	private Board board;
	private int alpha, beta;
	private int r, c;

	public MinimaxAlphaBeta(Board board, int depth,int alpha, int beta, boolean maximize) {
		this.board = board;
		this.depth = depth;
		this.alpha = alpha;
		this.beta = beta;
		this.maximize = maximize;
	}


	public int alphabeta() {
		if (depth == 0 /*|| board.counter == 64*/) {
			//System.out.println("Evaluation() : " + this.board.evaluate());
			//System.out.println("Stability() : " + this.board.stability());
			//this.board.printBoard();
			return this.board.evaluate();
		}
		else if(maximize) {
			int value = -1000000;
			Board [] children = new Board[this.board.mobility()];
			int c = 0;
			/// Μάλλον δεν χρειαζομαι τα fors επειδη έχω τους τρομερούς χαντλερς
			for (int row = 0; row < 7; row++) { 
				for (int col = 0; col < 7; col++) {
					if (board.getBoard()[row][col] == null) {
						if (board.checkLegalPlay(row,col) > 0  ){
							Board childBoard = new Board();
							childBoard.copyBoard(board);
							children[c] = childBoard;
							children[c].placePieceForAi(row, col);

							MinimaxAlphaBeta child = new MinimaxAlphaBeta(children[c], this.depth -1 ,this.alpha, this.beta, false);

							int childMax = child.alphabeta(); 
							c++;
							if (childMax > value) {
								value = childMax;
								this.r = row; // row
								this.c = col; // column
							}
							if (value > this.alpha ) {
								this.alpha = value;
							}
							if (this.alpha >= this.beta) { // break
								row = 8;
								col = 8;
							}
						}
					}
				}
			}
			return value;
		}
		else { // minimize
			int value = 1000000;
			Board[] children = new Board[this.board.mobility()];
			int c = 0;
			for (int row = 0; row < 7; row++) {
				for (int col = 0; col < 7; col++) {
					if (board.checkLegalPlay(row,col) > 0 && board.getBoard()[row][col] == null) {
						Board childBoard = new Board();
						childBoard.copyBoard(board);
						children[c] = childBoard;
						children[c].placePieceForAi(row, col);
						MinimaxAlphaBeta child = new MinimaxAlphaBeta(children[c], this.depth -1 ,this.alpha, this.beta, true);

						int childMin = child.alphabeta();
						c++;
						if (childMin < value) {
							value = childMin;
							
						}
						if ( value < this.beta) {
							this.beta = value;
						}
						if (this.alpha >= this.beta) { //break
							row = 8;
							col = 8;
						}
					}
				}
			}
			return value;
		}

	}

	public int getRow() {
		return this.r;
	}
	public int getCol() {
		return this.c;
	}
}