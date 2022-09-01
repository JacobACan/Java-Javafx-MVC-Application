package view;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import model.GameState;
import model.Location;
import model.Minesweeper;
import model.MinesweeperObserver;

public class GUIUpdater implements MinesweeperObserver{
    MinesweeperGUI gui;
    Minesweeper model;

    private static final List<Thread> hintThreads = new ArrayList<>();
    private static final Random RNG = new Random(); 

    private int buttonSize;

    public GUIUpdater(MinesweeperGUI gui){
        this.gui = gui;
        this.buttonSize = MinesweeperGUI.getBUTTONSIZE();
    }
    
    @Override
    public void cellUpdated(Location location) {
        //get symbol from cell location
        char symbol = model.getSymbol(location);
        //get button from button dictionary
        Map<Location,Button> buttons = gui.getButtons();
        Button button = buttons.get(location);
        //set button's image to blank, and add a text with the number and color of symbol
        button.setGraphic(null);
        button.setText(String.valueOf(symbol));
        switch (symbol) {
            case 'M':
            button.setText("");
            ImageView bomb = new ImageView(MinesweeperGUI.getMineImage());
            bomb.setFitHeight(buttonSize);
            bomb.setFitWidth(buttonSize);
            button.setGraphic(bomb);
            break;
            case '0':
            button.setText("");
            if (model.gameState() != GameState.LOST) playSoundRandomHit();
            break;
            case '1':
            button.setTextFill(Color.BLUE);
            if (model.gameState() != GameState.LOST) playSoundRandomHit();
            break;
            case '2':
            button.setTextFill(Color.GREEN);
            if (model.gameState() != GameState.LOST) playSoundRandomHit();
            break;
            case '3':
            button.setTextFill(Color.RED);
            if (model.gameState() != GameState.LOST) playSoundRandomHit();
            break;
            case '4':
            button.setTextFill(Color.DARKBLUE);
            if (model.gameState() != GameState.LOST) playSoundRandomHit();
            break;
            case '5':
            button.setTextFill(Color.BROWN);
            if (model.gameState() != GameState.LOST) playSoundRandomHit();
            break;
            case '6':
            button.setTextFill(Color.CYAN);
            if (model.gameState() != GameState.LOST) playSoundRandomHit();
            break;
            case '7':
            button.setTextFill(Color.BLACK);
            if (model.gameState() != GameState.LOST) playSoundRandomHit();
            break;
            case '8':
            button.setTextFill(Color.GRAY);
            if (model.gameState() != GameState.LOST) playSoundRandomHit();
            default:
            break;
        }
        
    }

    public void update(Minesweeper minesweeper, Location location){
        model = minesweeper;
        cellUpdated(location);
        //get GUI moves label and update it with minesweeper.getmoves
        MinesweeperGUI.getMoves().setText(String.format("Moves: %d", minesweeper.getMoveCount()));
        //get game state and update it with minesweeper.getgamestate
        GameState state = minesweeper.gameState();
        //check for victory, separate method? if victory or loss, reveal entire board
        if(state == GameState.LOST){
            MinesweeperGUI.getGameState().setText("YOU LOSE!");
            MinesweeperGUI.getGameState().setTextFill(Color.RED);
            MinesweeperGUI.getGameState().setEffect(new DropShadow(5, Color.RED));
            MinesweeperGUI.getGameState().setOpacity(.6);
            revealBoard();
            playSound("media/sound/Lose.mp3");
        }
        else if(state == GameState.WON){
            MinesweeperGUI.getGameState().setText("YOU WIN!");
            MinesweeperGUI.getGameState().setTextFill(Color.GREEN);
            MinesweeperGUI.getGameState().setEffect(new DropShadow(5, Color.GREEN));
            MinesweeperGUI.getGameState().setOpacity(.6);
            revealBoard();
            playSound("media/sound/Win.mp3");
        }
        else{
            MinesweeperGUI.getGameState().setText("Game in progress....");
        }
    }

    private void revealBoard(){
        Set<Location> locations = model.revealHelper();
        for(Location location:locations){
            cellUpdated(location);
        }
    }
    public void solveBoard(Minesweeper model){
        this.model = model;
        MinesweeperGUI.getGameState().setText("SOLVED!");
        MinesweeperGUI.getGameState().setTextFill(Color.PALEGOLDENROD);
            MinesweeperGUI.getGameState().setEffect(new DropShadow(5, Color.PALEGOLDENROD));
            MinesweeperGUI.getGameState().setOpacity(.7);
        Set<Location> locations = model.revealHelper();
        for(Location location:locations){
            cellUpdated(location);
        }
    }

    public void hint(Minesweeper minesweeper){
        Collection<Location> possibleLocations = minesweeper.getPossibleSelections();
        Location location = possibleLocations.stream().findFirst().orElse(new Location(-1, -1));
        Map<Location,Button> buttons = gui.getButtons();
        Button button = buttons.get(location);
        runHintBlinkingAnimation(button);
    }


    private void runHintBlinkingAnimation(Button button) {
        //Blinking animation for hint
        Thread hintBlink = new Thread(new Runnable() {
            @Override
            public void run() {
                if (hintThreads.size() > 0) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {}
                }
                int msPassed = 0;
                int blinks = 0;
                while(hintThreads.size() <= 1 && blinks < 6) {
                    Thread blink = new Thread(new Runnable() {
                        public void run() {
                            if (button != null) {
                                button.setStyle("-fx-background-color: #00ff00");
                                button.setOpacity(.3);
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {}
                                button.setStyle("-fx-background-color: #ffffff");
                                button.setOpacity(1);
                                
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {}
                            }

                        };
                    });
                    if (msPassed > 500 || blinks == 0) {
                        blink.start();
                        msPassed = 0;
                        blinks++;
                    } else {
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {}
                        msPassed += 1;
                    }
                }
                hintThreads.remove(0);
            }
        });

        hintThreads.add(hintBlink);
        hintBlink.start(); 
    }
    private void playSound(String path) {
        if (gui.getGuiSound() != null) gui.getGuiSound().setVolume(.4);
        gui.setGuiSound(new MediaPlayer(new Media( new File(path).toURI().toString()))); 
        gui.getGuiSound().play();
    }
    private void playSoundRandomHit() {
        playSound(String.format("media/sound/Hit%d.mp3", RNG.nextInt(5)));
    }
}