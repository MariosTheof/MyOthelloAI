import java.util.*;

public class Main {

  public static void main(String[] args) {
      Scanner scan = new Scanner(System.in);
      Board board = new Board();

      board.printBoard();

      boolean gameContinues = true;
      while(gameContinues){
        System.out.println("\n");
        System.out.println(board.getCurrentPlayerPiece() + " player is playing \n");
        System.out.println("Where do you want to place the next piece? ");
        System.out.println("Row : ");
        int row = scan.nextInt();
        System.out.println("Column : ");
        int col = scan.nextInt();

        board.placePiece(row - 1,col - 1 ); // Για να λογικευτούν οι μεταβλητές
        board.printBoard();
        
        if (board.finished() == true) {
        	System.out.println("THE WINNER IS : " + board.winner());
        	gameContinues = false;
        }
        
      }
      scan.close();
  }
}
