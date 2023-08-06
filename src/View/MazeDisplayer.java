package View;

import Server.Configurations;
import Server.Server;
import algorithms.search.AState;
import algorithms.search.Solution;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Properties;

public class MazeDisplayer extends Canvas {
    private int[][] maze;
    private Solution solution;
    // Player position
    private int playerRow;
    private int playerCol;
    private int goalRow;
    private int goalCol;
    // Image of player and wall
    StringProperty imageFileNameWall = new SimpleStringProperty();
    StringProperty imageFileNamePlayer = new SimpleStringProperty();
    StringProperty imageFileNameGoal = new SimpleStringProperty();
    StringProperty imageFileNameWinner = new SimpleStringProperty();
    StringProperty imageFileNamePath = new SimpleStringProperty();
    public boolean winner = false;
    public static MediaPlayer mediaPlayer;

    public void setMaze(int[][] maze) {
        this.maze = maze;
    }
    public void setSolution(Solution solution) { this.solution = solution;}
    public int getPlayerRow() {
        return playerRow;
    }
    public int getPlayerCol() {
        return playerCol;
    }
    public int getGoalRow() {
        return goalRow;
    }
    public void setGoalRow(int goalRow) {
        this.goalRow = goalRow;
    }
    public int getGoalCol() {
        return goalCol;
    }
    public void setGoalCol(int goalCol) {
        this.goalCol = goalCol;
    }
    public String getImageFileNameWall() {
        return imageFileNameWall.get();
    }
    public void setImageFileNameWall(String imageFileNameWall) {
        this.imageFileNameWall.set(imageFileNameWall);
    }
    public String getImageFileNamePlayer() {
        return imageFileNamePlayer.get();
    }
    public void setImageFileNamePlayer(String imageFileNamePlayer) { this.imageFileNamePlayer.set(imageFileNamePlayer); }
    private String getImageFileNameGoal() { return imageFileNameGoal.get();}
    public void setImageFileNameGoal(String imageFileNameGoal) {
        this.imageFileNameGoal.set(imageFileNameGoal);
    }
    public String getImageFileNameWinner() {return imageFileNameWinner.get();}
    private void setImageFileNameWinner(String imageFileNameWinner) { this.imageFileNameWinner.set(imageFileNameWinner); }
    private String getImageFileNamePath() { return this.imageFileNamePath.get();}
    private void setImageFileNamePath(String imageFileNamePath) {
        this.imageFileNamePath.set(imageFileNamePath);
    }

    public static int Type;

    public void setPlayerPosition(int row, int col) {
        this.playerRow = row;
        this.playerCol = col;
    }

    public MazeDisplayer() throws IOException {
        //audioChooser(0);
        widthProperty().addListener(e -> draw());
        heightProperty().addListener(e -> draw());
        imageSettingsGame();
    }

    public void imageSettingsGame() throws IOException {
        Path fileName = Path.of("Resources/config.properties");

        // Now calling Files.readString() method to
        // read the file
        String str = Files.readString(fileName);

        String lines[] = str.split("\\r?\\n");
        String number[] = lines[3].split("=");
        Type = Integer.parseInt(number[1]);

        setImageFileNamePlayer("Resources/Images/player" + Type + ".png");
        setImageFileNameGoal("Resources/Images/goal" + Type + ".png");
        setImageFileNameWinner("Resources/Images/win" + Type + ".gif");
        setImageFileNameWall("Resources/Images/wall.jpg");
        setImageFileNamePath("Resources/Images/path.jpg");
    }


    public void drawMaze(int[][] maze) {
        this.maze = maze;
        audioChooser(1);
        draw();
    }
    public void draw(){
        if (maze != null){
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            int rows = maze.length;
            int cols = maze[0].length;
            double cellHeight = canvasHeight/rows;
            double cellWidth = canvasWidth/cols;
            GraphicsContext graphicsContext = getGraphicsContext2D();
            graphicsContext.clearRect(0,0,canvasWidth, canvasHeight);
            if(!this.winner)
            {
                drawMazeWalls(graphicsContext, cellHeight, cellWidth, rows, cols);
                if(solution != null)
                    drawSolution(graphicsContext, cellHeight, cellWidth);
                drawGoal(graphicsContext, cellHeight, cellWidth);
                drawPlayer(graphicsContext, cellHeight, cellWidth);
            }
        }
    }
    private void drawGoal(GraphicsContext graphicsContext, double cellHeight, double cellWidth) {
        double x = getGoalCol() * cellWidth;
        double y = getGoalRow() * cellHeight;
        graphicsContext.setFill(Color.BLUE);
        Image goalImage = null;
        try {
            goalImage = new Image(new FileInputStream(getImageFileNameGoal()));
        } catch (FileNotFoundException e) {
            System.out.println("There is no goal image file");
        }
        if(goalImage == null)
            graphicsContext.fillRect(x, y, cellWidth, cellHeight);
        else
            graphicsContext.drawImage(goalImage, x, y, cellWidth, cellHeight);
    }
    private void drawSolution(GraphicsContext graphicsContext, double cellHeight, double cellWidth) {
        double x = getGoalCol() * cellWidth;
        double y = getGoalRow() * cellHeight;
        int playerCurrentRow = this.playerRow;
        int playerCurrentCol = this.playerCol;
        int goalRow = this.goalRow;
        int goalCol = this.goalCol;
        Solution sol = this.solution;
        ArrayList<AState> solutionList =  sol.getSolutionPath();
        String goalState = String.format("{%d,%d}", goalRow, goalCol);
        Image pathImage = null;
        try {
            pathImage = new Image(new FileInputStream(getImageFileNamePath()));
        } catch (FileNotFoundException e) {
            System.out.println("There is no path image file");
        }
        if(pathImage == null)
            graphicsContext.fillRect(x, y, cellWidth, cellHeight);
        else
            graphicsContext.drawImage(pathImage, x, y, cellWidth, cellHeight);
        int rows = this.maze.length;
        int cols = this.maze[0].length;
        String curState;
        for (int row = 0; row < rows ; row++) {
            for (int col = 0; col < cols; col++) {
                curState = String.format("{%d,%d}", row, col);
                if ((row != playerCurrentRow) || (col != playerCurrentCol))
                {
                    boolean partOfSolution = checkState(solutionList, curState);
                    boolean isGoal = curState.equals(goalState);
                    if (partOfSolution && !isGoal) {
                        graphicsContext.drawImage(pathImage, col * cellWidth, row * cellHeight, cellWidth, cellHeight);
                    }
                }
            }
        }
    }

    private boolean checkState(ArrayList<AState> solutionList, String curState) {
        LinkedList<String> solutionStates = new LinkedList<String>();
        for (AState aState : solutionList) {
            String state = aState.toString();
            solutionStates.add(state);
        }
        solutionStates.removeFirst();
        return solutionStates.contains(curState);
    }
    private void drawMazeWalls(GraphicsContext graphicsContext, double cellHeight, double cellWidth, int rows, int cols) {
        graphicsContext.setFill(Color.RED);
        Image wallImage = null;
        try{
            wallImage = new Image(new FileInputStream(getImageFileNameWall()));
        } catch (FileNotFoundException e) {
            System.out.println("There is no wall image file");
        }
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if(maze[i][j] == 1){
                    //if it is a wall:
                    double x = j * cellWidth;
                    double y = i * cellHeight;
                    if(wallImage == null)
                        graphicsContext.fillRect(x, y, cellWidth, cellHeight);
                    else
                        graphicsContext.drawImage(wallImage, x, y, cellWidth, cellHeight);
                }
            }
        }
    }

    private void drawPlayer(GraphicsContext graphicsContext, double cellHeight, double cellWidth) {
        double x = getPlayerCol() * cellWidth;
        double y = getPlayerRow() * cellHeight;
        graphicsContext.setFill(Color.GREEN);
        Image playerImage = null;
        try {
            playerImage = new Image(new FileInputStream(getImageFileNamePlayer()));
        } catch (FileNotFoundException e) {
            System.out.println("There is no player image file");
        }
        if(playerImage == null)
            graphicsContext.fillRect(x, y, cellWidth, cellHeight);
        else
            graphicsContext.drawImage(playerImage, x, y, cellWidth, cellHeight);
    }

    public void Zoom() {
        setOnScroll(scrollEvent -> {
            if (scrollEvent.isControlDown()) {
                double zoom_fac = 1.05;
                if (scrollEvent.getDeltaY() < 0) {
                    zoom_fac = 2.0 - zoom_fac;
                }
                Scale newScale = new Scale();
                newScale.setPivotX(scrollEvent.getX());
                newScale.setPivotY(scrollEvent.getY());
                newScale.setX(getScaleX() * zoom_fac);
                newScale.setY(getScaleY() * zoom_fac);
                getTransforms().add(newScale);
                scrollEvent.consume();
            }
        });
    }

    public static void audioChooser(int selectedAudioIndex) {
        /*stop the audio playing currently before switching */
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.pause();
        }

        String path = "";
        if (selectedAudioIndex == 0) {
            path = "Resources/Sounds/SB Music_ Grass Skirt Chase.mp3";
        }
        else if (selectedAudioIndex == 1) {
            path = "Resources/Sounds/SpongeBob - Krusty Krab (2019 Trap Remix).mp3";
        }
        else if (selectedAudioIndex == 2) {
            path = "Resources/Sounds/Victory - Sound Effect.mp3";
        }

        Media player = new Media(new File(path).toURI().toString());
        mediaPlayer = new MediaPlayer(player);
        mediaPlayer.play();
        mediaPlayer.setMute(MyViewController.mute);
    }

    public static void stopMusic() {
        mediaPlayer.setMute(MyViewController.mute);
    }

    public void win() {
        try {
            Pane pane = new Pane();
            Stage newStage = new Stage();
            //int Type = Configurations.player();
            String path = "Resources/Images/win" + Type + ".gif";
            Image imageUserWonScene = new Image(Paths.get(path).toUri().toString());
            ImageView imageviewUserWonScene = new ImageView(imageUserWonScene);
            /* add ImageView to Pane's children */
            pane.getChildren().add(imageviewUserWonScene);
            Scene scene = new Scene(pane);
            newStage.setScene(scene);
            /* show the UserWon scene */
            newStage.setTitle("You win!");
            newStage.setAlwaysOnTop(true);
            newStage.initModality(Modality.APPLICATION_MODAL);

            Button newGameBTN = new Button("New Game?");
            newGameBTN.setStyle("-fx-border-color: #b7ebeb; -fx-border-width: 5px; -fx-background-color: #23a1f5; -fx-font-size: 2em; -fx-text-fill: #54548e");
            pane.getChildren().add(newGameBTN);

            // action event
            EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
                public void handle(ActionEvent e)
                {
                    try {
                        newStage.hide();
                        Main.reset();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            };
            // when button is pressed
            newGameBTN.setOnAction(event);

            newStage.show();
            /* play the UserWon Audio */
            //audioChooser(2);
            /* if user presses the exit window button then stop the music from playing*/
            //goodBye.setOnCloseRequest( event ->  mediaPlayer.stop() );//Sets the value of the property onCloseRequest
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}