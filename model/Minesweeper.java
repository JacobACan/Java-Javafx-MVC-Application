package model;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import java.util.Random;
import java.util.Set;

import view.GUIUpdater;

public class Minesweeper {
    private GUIUpdater observer;

    public void register(GUIUpdater observer){
        this.observer = observer;
    }

    private void notifyObservers(Location location){
        if(observer != null){
            observer.update(this, location);
        }
    }


   int rows, cols;
   Character[][] board;
   public GameState state;
   public int moveCount;
   public int mineCount;

   public static final char MINE = 'M';
   public static final char COVERED = '-';

   public Set<Location> mines = new HashSet<>();
   public Set<Location> selections = new HashSet<>();
   
   public Minesweeper(int rows, int cols, int mineCount) {
    this.rows = rows;
    this.cols = cols;
    this.mineCount = mineCount;
    this.moveCount = 0;
    board = new Character[rows][cols];
    state = GameState.NOT_STARTED;
    initalizeBoard(rows, cols, mineCount);
   }

   public Minesweeper(Minesweeper currentConfig) {
    //should create a deep copy for use in successors
    this.rows = currentConfig.getRows();
    this.cols = currentConfig.getCols();
    this.mineCount = currentConfig.mineCount;
    this.moveCount = currentConfig.getMoveCount();
    this.board = Arrays.copyOf(currentConfig.board, currentConfig.board.length);
    this.state = currentConfig.gameState();
    this.selections = new HashSet<>(currentConfig.selections);
   }



   public void initalizeBoard(int rows, int cols, int mineCount){
       Random RNG = new Random();
       mines = new HashSet<>();
       //Filling board
       for(int r = 0; r < rows; r++){
        for(int c = 0; c < cols; c++){
            board[r][c] = '0';
         }
       }
       //Placing mines
       while(mineCount>0){
            Location candidate = new Location(RNG.nextInt(rows), RNG.nextInt(cols));
            if(mines.contains(candidate)){
                continue;
            }
            else{
                board[candidate.getRow()][candidate.getCol()] = MINE;
                mineCount--;
                mines.add(candidate);
            }
       }  
       //Placing numbers
       for(int r = 0; r < rows; r++){
        for(int c = 0; c < cols; c++){
            Location location = new Location(r, c);
            if(!mines.contains(location)){
                char number = (char)(mineAdjacencyCheck(location)+48);
                board[r][c] = number;
            }
         }
       }
   }

   @Override
   public String toString() {
    String string = "";
       for (int row = 0; row < rows; row++) {
        for (int col = 0; col < cols; col++) {
            if(isUncovered(row, col)){
                string += board[row][col]; 
            }
            else{
                string += COVERED;
            }
        }
        string += '\n';
       }
       return string;
   }
   public String printFull(){
       String string = "";
       for (int row = 0; row < rows; row++) {
        for (int col = 0; col < cols; col++) {
            string += board[row][col]; 
        }
        string += '\n';
       }
       return string;

   }
   public int getRows(){
       return rows;
   }
   public int getCols(){
       return cols; 
   }
   public int getMoveCount(){
       return moveCount;
   }
   public int getMineCount(){
       return mineCount;
   }
   public GameState gameState(){
       return state; 
   }
   public boolean isCovered(Location location){
       return !isUncovered(location.getRow(), location.getCol());
   }

   public char getSymbol(Location location){
       return board[location.getRow()][location.getCol()];
   }

   public Collection<Location> getPossibleSelections(){
       Set<Location> possibleSelections = new HashSet<>();
       for(int r = 0; r < rows; r++){
           for(int c = 0; c < cols; c++){
                if(!isUncovered(r, c) && board[r][c] != MINE){
                    Location location = new Location(r, c);
                    possibleSelections.add(location);
                }
           }
       }
       return possibleSelections;
   }

   
   boolean isUncovered(int row, int col){
       Location location = new Location(row, col);
       if(selections.contains(location)){
           return true; 
       }
       return false;
   }
   public void makeSelection(Location location) throws MinesweeperException{
     
     //Throws an exception if the selected location is out of bounds, or if it is already uncovered
     if(isUncovered(location.getRow(), location.getCol())){
       throw new MinesweeperException("This cell has already been uncovered!");
      }
      if(location.getRow() >= rows || location.getRow() < 0){
        throw new MinesweeperException("The row you entered is out of bounds!");
      }
      if(location.getCol() >= cols || location.getCol() < 0){
        throw new MinesweeperException("The column you  entered is out of bounds!");
      }
      else{
          selections.add(location);
        if (mineAdjacencyCheck(location) == 0) uncoverAdjacentBlankSpaces(location);
      }
      updateGameState();
        moveCount++;
      notifyObservers(location);
   }

   private void updateGameState(){
       //This is called when a move has been made, so game should at least be in progress now
       if(state == GameState.NOT_STARTED){
           state = GameState.IN_PROGRESS;
       }
       
       //Checks if all possible safe squares have been selected
       if(selections.size() == (rows*cols - mineCount)){
           state = GameState.WON;
       }

       //Checks if a mine has been selected
       for(Location mineLocation:mines){
        if(selections.contains(mineLocation)){
            state = GameState.LOST;
            break;
        }
    }
   }

   private int mineAdjacencyCheck(Location location){
    int mines = 0;
    int row = location.getRow();
    int col = location.getCol();
    //Checking each adjacent location for mines

    //-1 Row, -1 Col
    if(row-1 >= 0 && col-1>= 0){
     if(board[row-1][col-1] == MINE){
         mines++;
     } 
    }
    //-1 Row, 0 Col
    if(row-1 >= 0){
     if(board[row-1][col] == MINE){
         mines++;
     }
    }
    //-1 Row, +1 Col
    if(row-1 >= 0 && col+1 < cols){
     if(board[row-1][col+1] == MINE){
         mines++;
     }
    } 
    //0 Row, +1 Col
    if(col+1 < cols){
     if(board[row][col+1] == MINE){
         mines++;
     } 
    }
    //+1 Row, +1 Col
    if(row+1 < rows && col+1 < cols){
     if(board[row+1][col+1] == MINE){
         mines++;
     } 
    }  
    //+1 Row, 0 Col
    if(row+1 < rows){
     if(board[row+1][col] == MINE){
         mines++;
     } 
    } 
    //+1 Row, -1 Col
    if(row+1 < rows && col-1 >= 0){
     if(board[row+1][col-1] == MINE){
         mines++;
     } 
    }
    //0 Row, -1 Col
    if(col-1 >= 0){
     if(board[row][col-1] == MINE){
         mines++;
     } 
    }  
    return mines;
}

public Set<Location> revealHelper(){
    //modified version of getPossibleSelections for use with GUI revealer
    Set<Location> possibleSelections = new HashSet<>();
    for(int r = 0; r < rows; r++){
        for(int c = 0; c < cols; c++){
             if(!isUncovered(r, c)){
                 Location location = new Location(r, c);
                 possibleSelections.add(location);
             }
        }
    }
    return possibleSelections;
}

private void uncoverAdjacentBlankSpaces(Location location) {
    //Uncovers adjacent mines if not surroun
    int row = location.getRow();
    int col = location.getCol();
    //left
        Location leftLocation = new Location(row, col - 1);
        try {
             if (isCovered(leftLocation)) makeSelection(leftLocation);
        } catch (MinesweeperException e) {}
    
    //right
        Location rightLocation = new Location(row, col + 1) ;
        try {
            if (isCovered(rightLocation)) makeSelection(rightLocation);
        } catch (MinesweeperException e) {}

    //top
        Location topLocation = new Location(row - 1, col); 
        try {
            if (isCovered(topLocation))  makeSelection(topLocation);
        } catch (MinesweeperException e) {}
    //top-left
        Location topLeftLocation = new Location(row -1, col - 1);
        try {
             if (isCovered(topLeftLocation)) makeSelection(topLeftLocation);
        } catch (MinesweeperException e) {}
    //top-right
        Location topRightLocation = new Location(row - 1, col + 1);
        try {
             if (isCovered(topRightLocation)) makeSelection(topRightLocation);
        } catch (MinesweeperException e) {}
        
    //bottom
        Location bottomLocation = new Location(row + 1, col);
        try {
            if (isCovered(bottomLocation)) makeSelection(bottomLocation);
        } catch (MinesweeperException e) {}
    //bottom-left
        Location bottomLeftLocation = new Location(row + 1, col - 1);
        try {
            if (isCovered(bottomLeftLocation)) makeSelection(bottomLeftLocation);
        } catch (MinesweeperException e) {}
    //bottom-right
        Location bottomRightLocation = new Location(row + 1, col + 1);
        try {
            if (isCovered(bottomRightLocation)) makeSelection(bottomRightLocation);
        } catch (MinesweeperException e) {}



}

public void GUIHint(){
    if (state == GameState.IN_PROGRESS || state == GameState.NOT_STARTED) observer.hint(this);
}

public void GUISolveBoard() {
    if (state == GameState.NOT_STARTED || state == GameState.IN_PROGRESS) state = GameState.LOST;
    observer.solveBoard(this);
}



}