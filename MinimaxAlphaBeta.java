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
				return this.board.evaluate();
		}
		else if(maximize) {
			int value = -1000;
			Board [] children = new Board[this.board.mobility(null)];
			int c = 0;
			 /// Μάλλον δεν χρειαζομαι τα fors επειδη έχω τους τρομερούς χαντλερς
			for (int i = 0; i < 7; i++) { //άλλαξε i/j σε col/row
				for (int j = 0; j < 7; j++) {
					if (board.checkLegalPlay(i,j)) {
						Board childBoard = new Board();
						childBoard.copyBoard(board);
						children[c] = childBoard;
						children[c].placePiece(i, j);
						this.r = i;
						this.c = j;
						MinimaxAlphaBeta child = new MinimaxAlphaBeta(children[c], this.depth -1 ,this.alpha, this.beta, false);
						
						int childMax = child.alphabeta(); // εδώ evaluate() ξανά ; 	
						c++;
						if (childMax < value) {
							value = childMax;
						}
						if (value < this.beta ) {
							this.beta = value;
						}
						if (this.alpha > this.beta) {
							i = 8;
							j = 8;
						}
					}
				}
			}
			return value;
		}
		else {
			int value = 1000;
			Board[] children = new Board[this.board.mobility(null)];
			int c = 0;
			for (int i = 0; i < 7; i++) {
				for (int j = 0;j < 7; j++) {
					if (board.checkLegalPlay(i,j)) {
						Board childBoard = new Board();
						childBoard.copyBoard(board);
						children[c] = childBoard;
						children[c].placePiece(i, j);
						MinimaxAlphaBeta child = new MinimaxAlphaBeta(children[c], this.depth -1 ,this.alpha, this.beta, true);
						
						int childMin = child.alphabeta();
						c++;
						if (childMin < value) {
							value = childMin;
						}
						if ( value < this.beta) {
							this.beta = value;
						}
						if (this.alpha > this.beta) {
							i = 8;
							j = 8;
						}
					}
				}
			}
			return value;
		}
		
	}
	
}