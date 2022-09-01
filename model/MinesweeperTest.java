package model;
import static org.junit.jupiter.api.Assertions.assertEquals;



import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import java.util.Collection;
import org.junit.platform.commons.annotation.Testable;


@Testable
public class MinesweeperTest {
    @Test
    public void testMakeSelection() throws MinesweeperException{
        int row = 1;
        int col = 1;
        Location location = new Location(row, col);
        Minesweeper minesweeper = new Minesweeper(4, 4, 15);
        Set<Location> expected = new HashSet<>();
        expected.add(location);

        minesweeper.makeSelection(location);

        assertEquals(expected, minesweeper.selections);
    }

    @Test
    public void testGetRowsGetCols() throws MinesweeperException{
        Minesweeper game = new Minesweeper(5, 8, 3);
        int expectedRows = 5;
        int expectedCols = 8;
        assertEquals(expectedRows, game.getRows());
        assertEquals(expectedCols, game.getCols());
    }

    @Test
    public void getPossibleSelectionsTest() {
        // Setup
        Minesweeper loosingGame = new Minesweeper(5, 5, 25);
        Minesweeper winningGame = new Minesweeper(1, 1, 0);
        Minesweeper inProgressGame = new Minesweeper(2, 2, 2);

        //Invoke
        Collection<Location> possibleLoosingGameLocations = loosingGame.getPossibleSelections();

        Collection<Location> possibleWinningGameLocations = winningGame.getPossibleSelections();
        Location winningLocation = possibleWinningGameLocations.stream().findFirst().orElse(new Location(-1, -1));
        
        Collection<Location> possibleInProgressGameLocations = inProgressGame.getPossibleSelections();
        Location inProgressLocation = possibleInProgressGameLocations.stream().findFirst().orElse(new Location(-1, -1));
        try {
            winningGame.makeSelection(winningLocation);
            inProgressGame.makeSelection(inProgressLocation);
        } catch (MinesweeperException e) {
            System.out.println(e);
        }


        //Analyze
        //size of loosing game collection should be 0;
        assertEquals(0, possibleLoosingGameLocations.size());

        //size of inprogress game locations should be two and should be in progress when move made.
        assertEquals(2, possibleInProgressGameLocations.size());
        assertEquals(GameState.IN_PROGRESS, inProgressGame.gameState());



        //size of winning game should be one and game should be won when move made.
        assertEquals(1, possibleWinningGameLocations.size());
        assertEquals(GameState.WON, winningGame.gameState());

    }

    @Test 
    public void gameStateTest() {
        // Setup
        Minesweeper loosingGame = new Minesweeper(5, 5, 25);
        Minesweeper winningGame = new Minesweeper(1, 1, 0);
        Minesweeper inProgressGame = new Minesweeper(2, 2, 2);
        Minesweeper notStartedGame = new Minesweeper(2, 2, 2);

        //Invoke
        Collection<Location> possibleWinningGameLocations = winningGame.getPossibleSelections();
        Location winningLocation = possibleWinningGameLocations.stream().findFirst().orElse(new Location(-1, -1));
        
        Collection<Location> possibleInProgressGameLocations = inProgressGame.getPossibleSelections();
        Location inProgressLocation = possibleInProgressGameLocations.stream().findFirst().orElse(new Location(-1, -1));
        try {
            loosingGame.makeSelection(new Location(0, 0));
            winningGame.makeSelection(winningLocation);
            inProgressGame.makeSelection(inProgressLocation);
        } catch (MinesweeperException e) {
            System.out.println(e);
        }


        //Analyze
        //after making any move on this board the user should lose;
        assertEquals(GameState.LOST, loosingGame.gameState());

        //This game should be in progress after making first valid move on board.
        assertEquals(GameState.IN_PROGRESS, inProgressGame.gameState());

        //This game should be won after making only valid move on board
        assertEquals(GameState.WON, winningGame.gameState());

        //This game should be not started because no moves have been made on it
        assertEquals(GameState.NOT_STARTED, notStartedGame.gameState());
    }
    @Test
    public void testIsCovered() throws MinesweeperException{
        Minesweeper minesweeper = new Minesweeper(4, 4, 2);
        Location location1 = new Location(1, 1);
        Location location2 = new Location(2, 2);

        minesweeper.makeSelection(location1);
        boolean coveredState = minesweeper.isCovered(location1);
        boolean coveredState2 = minesweeper.isCovered(location2);

        assertEquals(coveredState, false);
        assertEquals(coveredState2, true);

    }
    @Test
    public void testGetSymbol(){
        Minesweeper minesweeperMines = new Minesweeper(3, 3, 9);
        Location locationMine = new Location (2, 2);

        char symbolMine = minesweeperMines.getSymbol(locationMine);

        assertEquals(symbolMine, Minesweeper.MINE);

        Minesweeper minesweeperNone = new Minesweeper(3, 3, 0);
        Location location = new Location(2, 2);
        
        char symbol = minesweeperNone.getSymbol(location);

        assertEquals(symbol, '0');
    }
    

}
 