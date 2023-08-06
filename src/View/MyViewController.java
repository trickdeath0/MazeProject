package View;

import Model.MyModel;
import ViewModel.MyViewModel;
import algorithms.search.Solution;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

public class MyViewController implements IView , Observer, Initializable{
    @FXML
    public MyViewModel viewModel;
    @FXML
    public TextField textField_mazeRows;
    @FXML
    public TextField textField_mazeColumns;
    @FXML
    public MazeDisplayer mazeDisplayer;
    @FXML
    public Label playerRow;
    @FXML
    public Label playerCol;
    public Button solveBtn;
    public BorderPane MazePane;
    @FXML
    public Pane pane;
    public static boolean mute = false;

    public void setViewModel(MyViewModel viewModel) {
        this.viewModel = viewModel;
        this.viewModel.addObserver(this);
    }

    StringProperty updatePlayerRow = new SimpleStringProperty();
    StringProperty updatePlayerCol = new SimpleStringProperty();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        playerRow.textProperty().bind(updatePlayerRow);
        playerCol.textProperty().bind(updatePlayerCol);
    }

    public void setUpdatePlayerRow(int updatePlayerRow) {
        this.updatePlayerRow.set(updatePlayerRow + "");
    }

    public void setUpdatePlayerCol(int updatePlayerCol) {
        this.updatePlayerCol.set(updatePlayerCol + "");
    }

    public void generateMaze(ActionEvent actionEvent) throws IOException {
        int rows = Integer.parseInt(textField_mazeRows.getText());
        int cols = Integer.parseInt(textField_mazeColumns.getText());
        viewModel.generateMaze(rows, cols);
        int[][] maze = viewModel.getMaze();
        this.mazeDisplayer.setPlayerPosition(viewModel.getStartRow(),viewModel.getStartCol());
        this.mazeDisplayer.setMaze(maze);
        this.mazeDisplayer.drawMaze(maze);
        solveBtn.setDisable(false); // make solve button active
    }

    public void solveMaze(){
        if(this.viewModel.getMaze() == null) {return;}
        viewModel.solveMaze();
    }

    public void showAlert(String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.show();
    }


    public void KeyPressed(KeyEvent keyEvent) {
        if(keyEvent.getCode().getName().equals("Ctrl"))
        {
            mazeDisplayer.Zoom();
            keyEvent.consume();
            return;
        }
        else
        {
            viewModel.movePlayer(keyEvent.getCode());
            keyEvent.consume();
        }
    }

    public void setPlayerPosition(int row, int col){
        mazeDisplayer.setPlayerPosition(row, col);
        setUpdatePlayerRow(row);
        setUpdatePlayerCol(col);
    }

    public void mouseCliked(MouseEvent mouseEvent) {
        mazeDisplayer.requestFocus();
    }


    /**
     * This method is called whenever the observed object is changed. An
     * application calls an {@code Observable} object's
     * {@code notifyObservers} method to have all the object's
     * observers notified of the change.
     *
     * @param o   the observable object.
     * @param arg an argument passed to the {@code notifyObservers}
     *            method.
     */
    @Override
    public void update(Observable o, Object arg) {
        String change = (String) arg;
        System.out.println("UPDATED: " + change);
        if (change == null) {return;}

        if (change.equals("Maze Generated"))
        {
            mazeGenerated();
        }
        if (change.equals("Move Player"))
        {
            playerMoved();
        }
        if (change.equals("solution"))
        {
            solution();
        }
        if (change.equals("maze solved"))
        {
            try {
                mazeSolved();
                mazeDisplayer.audioChooser(2);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (change.equals("Load Maze"))
        {
            LoadMaze();
        }
    }

    public void mouseClicked(MouseEvent mouseEvent) {
        mazeDisplayer.requestFocus();
    }

    public void LoadMaze() {
        mazeDisplayer.setSolution(null);
        mazeDisplayer.setGoalRow(getGoalRow());
        mazeDisplayer.setGoalCol(getGoalCol());
        mazeDisplayer.setPlayerPosition(viewModel.getStartRow(),viewModel.getStartCol());
        this.setUpdatePlayerRow(viewModel.getStartRow());
        this.setUpdatePlayerCol(viewModel.getStartCol());
        mazeDisplayer.drawMaze(viewModel.getMaze());
    }

    public void solution() {
        Solution sol = viewModel.getSolution();
        mazeDisplayer.setSolution(sol);
        mazeDisplayer.draw();
    }

    private void mazeSolved() throws IOException {
        mazeDisplayer.win();
        mazeDisplayer.setSolution(viewModel.getSolution());
    }

    public void About(){
        try {
            Stage stage = new Stage();
            stage.setTitle("About the game");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("About.fxml")/*.openStream()*/);
            Scene scene = new Scene(root, 450, 255);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {}
    }

    private void playerMoved() {
        setPlayerPosition(viewModel.getPlayerPositionRow(), viewModel.getPlayerPositionColumn());
        mazeDisplayer.draw();
    }

    private void mazeGenerated() {
        mazeDisplayer.setGoalRow(getGoalRow());
        mazeDisplayer.setGoalCol(getGoalCol());
        mazeDisplayer.setPlayerPosition(viewModel.getStartRow(),viewModel.getStartCol());
        this.setUpdatePlayerRow(viewModel.getStartRow());
        this.setUpdatePlayerCol(viewModel.getStartCol());
        mazeDisplayer.setSolution(null);
        mazeDisplayer.drawMaze(viewModel.getMaze());
    }


    public int getGoalRow()
    {
        int row = viewModel.getGoalRow();
        return row;
    }

    public int getGoalCol()
    {
        int col = viewModel.getGoalCol();
        return col;
    }

    public void NewButtonPressed(ActionEvent actionEvent) throws IOException {
        Main.reset();
    }

    public void about(ActionEvent actionEvent) {
        About();
    }


    public void keyBoardMap(ActionEvent event) {help();}
    public void help() {
        try {
            Stage stage = new Stage();
            stage.setTitle("Help...");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("Help.fxml")/*.openStream()*/);
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {

        }
    }

    public void Properties(){
        try {
            Stage stage = new Stage();
            stage.setTitle("Properties the game");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("Properties.fxml")/*.openStream()*/);
            Scene scene = new Scene(root, 578, 578);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {

        }
    }
    public void PropertiesButtonPressed(ActionEvent actionEvent) {
        Properties();
    }

    public void SaveButtonPressed(ActionEvent actionEvent) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Maze Saver Dialog");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("MAZE files (*.maze)", "*.maze");
        fileChooser.getExtensionFilters().add(extFilter);
        File f = fileChooser.showSaveDialog(null);
        if (f != null) {
            viewModel.saveMaze(f);
        }

    }
    public void moveByMouse(MouseEvent mouseEvent ){
        viewModel.moveByMouse(mouseEvent,mazeDisplayer);
    }
    public void LoadButtonPressed(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Maze Loader Dialog");
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("MAZE files (*.maze)", "*.maze");
        fileChooser.getExtensionFilters().add(extFilter);
        File f = fileChooser.showOpenDialog(null);
        if (f != null && f.exists() && !f.isDirectory()) {
            viewModel.loadMaze(f);
        }
    }

    public void setResize(Scene scene){
        mazeDisplayer.widthProperty().bind(pane.widthProperty());
        mazeDisplayer.heightProperty().bind(pane.heightProperty());
        scene.widthProperty().addListener((observable, oldValue, newValue) -> {
            mazeDisplayer.widthProperty().bind(pane.widthProperty());
        });
        scene.heightProperty().addListener((observable, oldValue, newValue) -> {
            mazeDisplayer.heightProperty().bind(pane.heightProperty());
        });
    }

    public void MuteMaze(ActionEvent event) {
        mute = !mute;
        MazeDisplayer.stopMusic();
    }

    public void exit(ActionEvent event) throws IOException {
        System.exit(0);
    }


}
