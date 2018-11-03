
 public class Heuteristics{}

    public class evaluate(){
        if discNumber < 20{
        return 5*discDifference
            + 10*Mobility;
        }else if (discNumber <58){}

    }
    // prepei na krataei to count
    // **auksanei to mob tou alla meiwnei toy antipalou
    public int mobility(Board board, Piece currentPlayerPiece ){
        int mobilityB= 0;
        for( int row=0; row<8;row++){
            for( int col=0; col<8;col++){
                if (board[row][col] == currentPlayerPiece){
                    board.numberOfPotentialMoves(row, col, mobilityB);
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

    // gia secured rows k columns isws?
    public int stability(Board board, Piece piece){
        int stabilityB = 0;
        /*All Variables must be moved to a different scope*/
        topLeftStartingPoint1 = board[0][0];
        topLeftStartingPoint2 = board[0][0];
        if ( topLeftStartingPoint.getCurrentPlayerPiece()== piece){
            new stabilityHandler
            iterateCells(topLeftStartingPoint1,(1,0),stabilityHandler);
            iterateCells(topRightStartingPoint2,(0,1),stabilityHandler);
        }
        /*Το ίδιο 3 φορές*/
        topRightStartingPoint1 = board[0][7];
        topRightStartingPoint2 = board[0][7];
        if (ομοίως)
    }
    public int potentialMobility(){}
    public int discDifference(Board board){
        return numberOfBlackDisks - numberOfWhiteDisks;
    }
    public int corners(Board board, Piece piece){
        int blackCorners = 0;
        int whiteCorners = 0;
        //corner must be moved to a different scope
        cornerList corners = {[0][0], [0][7] ,[7][0],[7][7]};
        for (int corner : corners){
            if board[][] == Piece.BLACK {
                blackCorners++;
            }else if board[][] == Piece.WHITE{
                whiteCorners++;
            }
        }
    }



}
