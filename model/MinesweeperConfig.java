package model;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import backtracker.Backtracker;
import backtracker.Configuration;

public class MinesweeperConfig implements Configuration{
    private Minesweeper minesweeper;
    private List<Location> selections; 
    private List<Configuration> successors = new LinkedList<>();

    public MinesweeperConfig(Minesweeper minesweeper){
        this.minesweeper = minesweeper;
        selections = new LinkedList<>();
    }

    public MinesweeperConfig(MinesweeperConfig currentConfig){
        //creating deep copy of the config
        this.minesweeper = new Minesweeper(currentConfig.minesweeper);
        selections = new LinkedList<>();
        selections.addAll(currentConfig.selections);
    }



    public static MinesweeperConfig solve(Minesweeper minesweeper){
        MinesweeperConfig minesweeperConfig = new MinesweeperConfig(minesweeper);
        Backtracker backtracker = new Backtracker(false);
        return (MinesweeperConfig) backtracker.solve(minesweeperConfig);
    }
    public List<Location> getSelections(){
        return selections; 
    }
    @Override
    public Collection<Configuration> getSuccessors() {
        for(int r = 0; r < minesweeper.getRows(); r++){
            for(int c = 0; c < minesweeper.getCols(); c++){
                if(!minesweeper.isUncovered(r, c)){
                    MinesweeperConfig successor = new MinesweeperConfig(this);
                    if(minesweeper.getSymbol(new Location(r,c)) == Minesweeper.MINE){
                        //do nothing
                    }
                    else{
                    try{
                        successor.minesweeper.makeSelection(new Location(r, c));
                        successor.selections.add(new Location(r, c));
                    }
                    catch(MinesweeperException me){
                        System.out.println(me);
                    }
                    this.successors.add(successor);
                    }
                }
            }
        }
        return this.successors;
    }

    @Override
    public boolean isGoal() {
        return this.minesweeper.gameState() == GameState.WON;
    }

    @Override
    public boolean isValid() {
        if(minesweeper.gameState() != GameState.LOST ){
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        String string = "";
        for(Location location : selections){
            string += location + " ";
        }
        string += "\n";
        string += minesweeper;
        return string;
    }

}
