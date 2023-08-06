package ViewModel;

import Model.IModel;
import View.MazeDisplayer;
import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;

import java.io.File;
import java.util.Observable;
import java.util.Observer;

public class MyViewModel extends Observable implements Observer{
    private IModel iModel;
    private int playerPositionRowIndex;
    private int playerPositionColumnIndex;
    public StringProperty playerRow = new SimpleStringProperty("");
    public StringProperty playerColumn = new SimpleStringProperty("");

    public Solution getSolution() {
        return iModel.getSolution();
    }
    public int getPlayerPositionRow() {
        return iModel.getPlayerPositionRow();
    }
    public int getPlayerPositionColumn() {
        return iModel.getPlayerPositionColumn();
    }
    public int getGoalRow() {
        return iModel.getMaze().getGoalPosition().getRowIndex();
    }
    public int getGoalCol() {
        return iModel.getMaze().getGoalPosition().getColumnIndex();
    }
    public int getStartRow() {
        return iModel.getMaze().getStartPosition().getRowIndex();
    }
    public int getStartCol() {
        return iModel.getMaze().getStartPosition().getColumnIndex();
    }
    public int[][] getMaze()
    {
        Maze maze = iModel.getMaze();
        if(maze == null) return null;
        return maze.getMazeArray();
    }
    public MyViewModel(IModel myModel) {
        this.iModel = myModel;
        this.iModel.assignObserver(this);
    }

    public void generateMaze(int rows, int cols){
        iModel.generateMaze(rows, cols);
    }

    public void solveMaze(){
        iModel.solveMaze();
    }

    public void movePlayer(KeyCode movement){
        iModel.movePlayer(movement);
    }

    public void saveMaze(File f){
        iModel.saveMaze(f);
    }

    public void loadMaze(File file){
        iModel.loadFile(file);
    }

    public void moveByMouse(MouseEvent mouseEvent, MazeDisplayer mazeDisplayer ){
        iModel.MouseMovePlayer(mouseEvent,mazeDisplayer);
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
        if (o == iModel){
            playerPositionRowIndex = this.iModel.getPlayerPositionRow();
            playerRow.set(playerPositionRowIndex + "");
            playerPositionColumnIndex = this.iModel.getPlayerPositionColumn();
            playerColumn.set(playerPositionColumnIndex + "");
            setChanged();
            notifyObservers(arg);
        }
    }
}