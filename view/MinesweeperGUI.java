package view;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import model.Minesweeper;
import model.MinesweeperConfig;
import model.MinesweeperException;
import model.GameState;
import model.Location;

public class MinesweeperGUI extends Application{
    private static HashMap<Location,Button> buttons;
    private static List<Button> buttonsList;
    private MediaPlayer guiSound;
    private Minesweeper board;
    
    /* GUI Visuals */
    private static VBox gui;
    private static VBox menu;
    private static GridPane gridPane;

    /* Menu Visuals */
    private static Label mines;         
    private static Label moves;
    private static Label gameState;

    /* Graphic Variables */
    private static final Image BLANK = new Image("media/images/BLANK.png");
    private static final Image FLAG = new Image("media/images/flag.png");
    private static final Image MINE = new Image("media/images/mine2.png");
    private static final Image COVERED = new Image("media/images/Covered1.png");

    /* Game Specification Variables */
    private static int ROWS = 16;
    private static int COLS = 16;
    private static int MINECOUNT = 40;
    private static final Random RNG = new Random();
    private static final int BOARDSIZE = 510;
    private static int BUTTONSIZE = BOARDSIZE / COLS;
    private static Boolean solvingBoard = false;


    @Override
    public void start(Stage stage) throws Exception {

        board = new Minesweeper(ROWS, COLS, MINECOUNT);

        gameState = makeGameStateLabel("Minesweeper");
        gridPane = makeBoard();
        menu = makeMenu();

        gui = new VBox();
        gui.setBackground(new Background(new BackgroundImage( new  Image("media/images/Background2.jpg"), BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT , BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
        gui.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.NONE, new CornerRadii(0), new BorderWidths(10))));

        gui.getChildren().addAll(gameState, menu, gridPane);
        gui.setAlignment(Pos.CENTER);
        setKeyEvents();
        
        stage.setScene(new Scene(gui));
        stage.setTitle("Minesweeper");
        stage.setMinWidth(700);
        stage.setMinHeight(800);
        stage.show();
    }





    /* 
    
    
    FACTORY METHODS
    
    
    */
    private Button makeButton(int row, int col) {
        //Graphics
        Button button = new Button();
        ImageView imageView = new ImageView(COVERED);
        ColorAdjust brightness = new ColorAdjust();
        brightness.setBrightness(0);
        imageView.setEffect(brightness);
        imageView.setFitHeight(BUTTONSIZE);
        imageView.setFitWidth(BUTTONSIZE);
        button.setBackground(new Background(new BackgroundImage(BLANK, BackgroundRepeat.ROUND, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
        button.setGraphic(imageView);
        button.setPadding(Insets.EMPTY);
        button.setPrefSize(BUTTONSIZE, BUTTONSIZE);
        button.setAlignment(Pos.CENTER);
        button.setTextAlignment(TextAlignment.CENTER);
        button.setFont(Font.font("Verdana", FontWeight.BOLD, BUTTONSIZE/1.66));
        button.setEffect(new DropShadow(2, Color.BLACK));
        button.setStyle("-fx-background-color: #ffffff");
        button.setCursor(Cursor.HAND);

        //Actions
        button.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent arg0) {
            makeMoveOnBoard(new Location(row, col));
            }
        });
        button.setOnMouseClicked(e -> {
            flagOnRightClick(e, button);
        });

        return button;
    }
    private GridPane makeBoard(){
        buttons = new HashMap<>();
        buttonsList = new ArrayList<>();
        board.register(new GUIUpdater(this));
        GridPane gridPane = new GridPane();
        for(int row = 0; row < ROWS; row++){
            for(int col = 0; col < COLS; col++){
                Button button = makeButton(row, col);
                gridPane.add(button, col, row);
                //Add buttons to a dictionary and list for further manipulation later
                buttons.put(new Location(row, col), button);
                buttonsList.add(button);
            }
        }
        //Graphics
        gridPane.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.NONE, new CornerRadii(2), new BorderWidths(6))));
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setEffect(new DropShadow(15, Color.GRAY));

        return gridPane;
    }   
    private VBox makeMenu(){
        VBox menu = new VBox();
        VBox counters = new VBox();
        HBox buttons = new HBox();

        //mines label counter
        mines = new Label(String.format("Mines: %d", MINECOUNT));
        mines.setFont(Font.font("Verdana", FontWeight.MEDIUM, 12));
        mines.setTextAlignment(TextAlignment.CENTER);
        mines.setTextFill(Color.WHITE);

        //moves label counter
        moves = new Label("Moves: 0");
        moves.setTextAlignment(TextAlignment.CENTER);
        moves.setFont(Font.font("Verdana", FontWeight.MEDIUM, 12));
        moves.setTextFill(Color.WHITE);

        //hint menu button
        Button hintButton = makeMenuButton("media/images/Hint.png", "Hint");
        hintButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                hint();
            }
        });

        //solve menu button
        Button solveButton = makeMenuButton("media/images/Solve.png", "Solve!");
        solveButton.setOnAction(new EventHandler<ActionEvent>() {
           @Override
           public void handle(ActionEvent arg0) {
               solve();
           } 
        });

        //reset menu button
        Button resetButton = makeMenuButton("media/images/Reset.png", "Reset");
        resetButton.setOnAction (new EventHandler<ActionEvent>() {
            public void handle(ActionEvent arg0) {
                reest(ROWS, COLS, MINECOUNT);
            };  
        });

        //buttons hbox
        buttons.getChildren().addAll(hintButton, resetButton, solveButton);
        buttons.setAlignment(Pos.CENTER);
        
        //counters vbox
        counters.getChildren().addAll(mines, moves);
        counters.setAlignment(Pos.CENTER);

        //menu vbox
        menu.getChildren().addAll(counters,buttons);
        menu.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.NONE, new CornerRadii(2), new BorderWidths(5))));
        menu.setAlignment(Pos.CENTER);

        return menu;
    }
    private Label makeGameStateLabel(String text) {
        Label gamestatelabel = new Label(text);
        gamestatelabel.minHeight(50);
        gamestatelabel.minWidth(Integer.MAX_VALUE);
        gamestatelabel.prefHeight(50);
        gamestatelabel.setFont(Font.font("Verdana", FontWeight.BOLD, 35));
        gamestatelabel.setEffect(new DropShadow(10, Color.GRAY));
        gamestatelabel.setTextAlignment(TextAlignment.CENTER);
        gamestatelabel.setTextFill(Color.WHITE);
        gamestatelabel.setOpacity(1);
        gamestatelabel.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.NONE, new CornerRadii(2), new BorderWidths(12))));

        return gamestatelabel;
    }
    
    private Button makeMenuButton(String graphic, String text) {
        Button button = new Button();
        button.setCursor(Cursor.HAND);
        button.setPrefSize(100, 40);
        ImageView imageView = new ImageView(new Image(graphic));
        ColorAdjust brightness = new ColorAdjust();
        brightness.setBrightness(.7);
        imageView.setEffect(brightness);
        button.setGraphic(imageView);
        button.setFocusTraversable(false);
        button.setStyle("-fx-background-color: transparent");

        return button;
    }






    /* 
    
    
    GUI Actions
    
    
    */
    private void playSound(String path) {
        //plays a sound and sets the previous sounds volume to 1/5 its original volume
        if (guiSound != null) guiSound.setVolume(.2);
        guiSound = new MediaPlayer(new Media( new File(path).toURI().toString()));
        guiSound.play();
    }
    private void reest(int rows, int cols, int mineCount) {
        //Resets the gui variables and board
        if (!solvingBoard) {
            ROWS = rows;
            COLS = cols;
            MINECOUNT = mineCount;
            BUTTONSIZE = BOARDSIZE/rows;
            board = new Minesweeper(ROWS, COLS, MINECOUNT);
            gridPane = makeBoard();
            moves.setText("Moves: 0");
            mines.setText(String.format("Mines: %d", mineCount));
            gameState = makeGameStateLabel("Minesweeper");
            gui.getChildren().setAll(gameState, menu, gridPane);
            playSound("media/sound/Reset.mp3");
        }
    }
    private void hint() {
        playSound("media/sound/Hint.mp3");
        board.GUIHint();
    }
    private void solve() {
        playSound("media/sound/Solve.mp3");
        MinesweeperConfig solvedBoard = MinesweeperConfig.solve(board);
        if (solvedBoard != null) {
            //Solution found
            List<Location> movesOnBoardToWin = solvedBoard.getSelections();
            showSolve(movesOnBoardToWin);
        } else {
            //No solution
            gameState.setText("No Solution");
            MinesweeperGUI.getGameState().setTextFill(Color.RED);
            MinesweeperGUI.getGameState().setEffect(new DropShadow(5, Color.RED));
            MinesweeperGUI.getGameState().setOpacity(.6);
        }
    }
    private void showSolve(List<Location> movesOnBoardToWin) {
        
        new Thread(()-> {
            solvingBoard = true;
            for (Location moveOnBoardToWin : movesOnBoardToWin) {
                if (moveOnBoardToWin != null)  {
                    Platform.runLater(() -> {
                        try {
                            board.makeSelection(moveOnBoardToWin);
                        } catch (MinesweeperException e) {}
                    });
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {}
                }
            }
            solvingBoard = false;
        }).start();
    }
    
    private void flagOnRightClick(MouseEvent e, Button button) {

        if (board.gameState() != GameState.LOST) { // only if the game is being played
            if (e.getButton() == MouseButton.SECONDARY &&  button.getGraphic() != null && button.getGraphic().getId() == null) { // only if the event is a left click and the location has not been flagged
                if (button.getText().length() <= 0) {//if the button has been uncovered
                    ImageView flag = new ImageView(FLAG);
                    flag.setFitHeight(BUTTONSIZE);
                    flag.setFitWidth(BUTTONSIZE);
                    flag.setId("flagged");
                    button.setGraphic(flag);
                    playSound("media/sound/Flag.mp3");
                }   
            } else if (e.getButton() == MouseButton.SECONDARY && button.getGraphic() != null &&  button.getGraphic().getId() != null){ // only if the event is a left click and the location has been flagged
                if (button.getText().length() <= 0) { //if the button has been uncovered
                    ImageView covered = new ImageView(COVERED);
                    covered.setFitHeight(BUTTONSIZE);
                    covered.setFitWidth(BUTTONSIZE);
                    button.setGraphic(covered);
                }   
            }
        }
    }
    private void makeMoveOnBoard(Location location) {
        if ((board.gameState() == GameState.NOT_STARTED || board.gameState() == GameState.IN_PROGRESS) && !solvingBoard) { // if game is being played and board is not being solved currently
            try {
              board.makeSelection(location);
            } catch(MinesweeperException e) {
                // makes a thread that visually shows a red blink around a cell that throws this exception when attempting to make a move on it
              Thread errorBlink = new Thread(new Runnable() {
                @Override
                public void run() {
                    playSound("media/sound/WrongHit.mp3");
                    for (int i = 0; i < 3; i++) {
                        Button button = buttons.get(location);
                        button.setBorder((new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, 
                        CornerRadii.EMPTY, BorderStroke.MEDIUM))));
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {}
                        button.setBorder((new Border(new BorderStroke(Color.RED, BorderStrokeStyle.NONE, 
                        CornerRadii.EMPTY, BorderStroke.MEDIUM))));
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {}
                    }
                }
              });
            errorBlink.start();
            }
        };
    }
    private void makeCorrectMoveOnBoard() {
        Collection<Location> possibleLocations = board.getPossibleSelections();
        Location location = possibleLocations.stream().findFirst().orElse(new Location(-1, -1));
        try {
            board.makeSelection(location);
        } catch (MinesweeperException e) {}
    }
    private void setKeyEvents() {
        //Sets different key presses that will have actions within the gui
        gui.setOnKeyReleased(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent e) {
                if (e.getText().equals("h")) hint();
                else if (e.getText().equals("r")) reest(ROWS, COLS, MINECOUNT);
                else if (e.getText().equals("s")) solve();
                else if (e.getText().equals("p")) {
                    board.GUIHint();
                    makeCorrectMoveOnBoard();
                }
                else if (e.getText().equals("1")) reest(1, 1, RNG.nextInt(2));
                else if (e.getText().equals("2")) reest(2, 2, 1 + RNG.nextInt(2));
                else if (e.getText().equals("3")) reest(4, 4, 4 + RNG.nextInt(2));
                else if (e.getText().equals("4")) reest(8, 8, 10 + RNG.nextInt(2));
                else if (e.getText().equals("5")) reest(16, 16, 40 + RNG.nextInt(4));
                else if (e.getText().equals("6")) reest(20, 20, 62 + RNG.nextInt(8));
                else if (e.getText().equals("7")) reest(25, 25, 97 + RNG.nextInt(16));
                else if (e.getText().equals("8")) reest(30, 30, 140 + RNG.nextInt(32));
                else if (e.getText().equals("9")) reest(40, 40, 250 + RNG.nextInt(64));

            };
        });
    }




    /* 
    
    
    GETTERS & SETTERS
    
    
    */
    public HashMap<Location, Button> getButtons() {
        return buttons;
    }
    public static Label getMines() {
        return mines;
    }
    public static Label getMoves() {
        return moves;
    }
    public static Image getMineImage() {
        return MINE;
    }
    public static Image getCoveredImage() {
        return COVERED;
    }
    public static Label getGameState() {
      return gameState;
    }
    public static List<Button> getButtonsList() {
      return buttonsList;
    }
    public static int getBUTTONSIZE() {
        return BUTTONSIZE;
    }
    public MediaPlayer getGuiSound() {
        return guiSound;
    }
    public void setGuiSound(MediaPlayer guiSound) {
        this.guiSound = guiSound;
    }
    
    
    public static void main(String[] args) {
        launch(args);
    }
}
