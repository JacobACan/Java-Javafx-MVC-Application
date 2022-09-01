package view;


import java.util.Collection;
import java.util.Scanner;

import model.GameState;
import model.Location;
import model.Minesweeper;
import model.MinesweeperConfig;
import model.MinesweeperException;

public class MinesweeperCLI {
    private static final int BOARD_ROWS = 8;
    private static final int BOARD_COLS = 8;
    private static final int BOARD_MINES = 10;

   private Minesweeper mineSweeper; 
   private int rows, cols, mineCount;
   
   public MinesweeperCLI(int rows, int cols, int mineCount){
      mineSweeper = new Minesweeper(rows, cols, mineCount);
      this.mineCount = mineCount;
      this.rows = mineSweeper.getRows();
      this.cols = mineSweeper.getCols();
   }
   public int getMineCount() {
     return mineCount;
   }

   private static void printBoard(MinesweeperCLI minesweeperCLI){
        System.out.println("\n");
        System.out.println(minesweeperCLI.mineSweeper.toString());
        System.out.println(String.format("Moves: %d", minesweeperCLI.mineSweeper.getMoveCount()));
   }
   private static void printCompleteBoard(MinesweeperCLI minesweeperCLI) {
        System.out.println(minesweeperCLI.mineSweeper.printFull());
   }
   
   private static void reset(MinesweeperCLI minesweeperCLI) {
    System.out.println("resetting....");
        int rows = minesweeperCLI.rows;
        int cols = minesweeperCLI.cols;
        int mineCount = minesweeperCLI.mineCount;
        minesweeperCLI.mineSweeper = new Minesweeper(rows, cols, mineCount);
   }
   private static void pick(int row, int col, MinesweeperCLI minesweeperCLI) {
    Location location = new Location(row, col);
    try {
        minesweeperCLI.mineSweeper.makeSelection(location);       
    } catch (MinesweeperException e) {
        System.out.println(e);
    }
   }
   private static void hint(MinesweeperCLI minesweeperCLI) {
       Collection<Location> possibleLocations = minesweeperCLI.mineSweeper.getPossibleSelections();
       Location location = possibleLocations.stream().findFirst().orElse(new Location(-1, -1));
        if (location.getRow() != -1) {
            System.out.println(String.format("Hint: pick %d %d", location.getRow(), location.getCol()));
        } else {
            System.out.println("No Hint Locations found");
        }
   }
   private static void quit() {
    System.out.println("Quitting game...");
   }
   private static void help() {
    System.out.println(String.format("\n\t\thelp - this help message\n\t\tpick <row> <col> - uncovers cell a <row> <col>\n\t\thint - displays a safe selection\n\t\tsolve - solves the board\n\t\treset - resets to a new game\n\t\tsolve - solves the given board\n\t\tquit - quits the game"));
   }
   private static void solve(MinesweeperCLI cli){
       //implement backtracker configuration 
       MinesweeperConfig solved = MinesweeperConfig.solve(cli.mineSweeper);
       System.out.println("Solving board...");
       if (solved!= null) {
           for(Location move : solved.getSelections()){
               pick(move.getRow(), move.getCol(), cli);
               printBoard(cli);
           }
           System.out.println("Board solved!");
       } else {
           System.out.println("No Solution!");
       }

   }


   private static MinesweeperCLI startGame() {
    MinesweeperCLI minesweeperCLI = new MinesweeperCLI(BOARD_ROWS, BOARD_COLS, BOARD_MINES);
    System.out.println(String.format("Mines: %d\nCommands:\n\t\thelp - this help message\n\t\tpick <row> <col> - uncovers cell a <row> <col>\n\t\thint - displays a safe selection\n\t\treset - resets to a new game\n\t\tquit - quits the game", minesweeperCLI.getMineCount()));
    return minesweeperCLI;
   }

   private static boolean enterCommand(Scanner scanner, MinesweeperCLI minesweeperCLI) {
    System.out.print("Enter a command: ");
    String userInput =  scanner.nextLine();
    String[] userInputArray = userInput.split(" ");
    userInput.strip();
    if (userInput.equals("hint")) {
        hint(minesweeperCLI);
        return true;
    } else if (userInput.equals("quit")) {
        quit();
        return false; 
    } else if (userInput.equals("help")) {
        help();
        return true;
    } else if (userInput.equals("reset")) {
        reset(minesweeperCLI);
        return true;
    
    } else if(userInput.equals("solve")) {
        solve(minesweeperCLI);
        return false; 
    } else if (userInputArray[0].equals("pick")){
        if (userInputArray.length == 3) {
            try {
                int row = Integer.parseInt(userInputArray[1]);
                int col = Integer.parseInt(userInputArray[2]);
                pick(row, col, minesweeperCLI);
            }
            catch (NumberFormatException e) {
                System.out.println("Invalid Row Col Selection");
            }
            return true;
        } else {
            System.out.println("Improper pick format: Try pick <row> <col>"); 
            return true;
        }
    } else {
        System.out.println("Command Not Recognized: try <help> for a list of commands");
        return true;
    }
       
   }
   private static boolean determineGameState(MinesweeperCLI minesweeperCLI) {
    if (minesweeperCLI.mineSweeper.gameState() == GameState.WON) {
        System.out.println("You Win!");
        printCompleteBoard(minesweeperCLI);
        return false;
    } else if (minesweeperCLI.mineSweeper.gameState() == GameState.LOST) {
        System.out.println("You LOST!");
        printCompleteBoard(minesweeperCLI);
        return false;
    } else if (minesweeperCLI.mineSweeper.gameState() == GameState.IN_PROGRESS) {
        return true;
    } else {
        return true;
    }
   }
   public static void main(String[] args) throws MinesweeperException {
    boolean continueGame = true;
    MinesweeperCLI minesweeperCLI = startGame();
    Scanner scanner = new Scanner(System.in);
    while(continueGame)  {
        printBoard(minesweeperCLI);
        continueGame = enterCommand(scanner, minesweeperCLI) && determineGameState(minesweeperCLI);
    }
   }
}
