package Model;

import View.MazeDisplayer;
import ViewModel.MyViewModel;
import algorithms.mazeGenerators.*;
import algorithms.search.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;

import java.io.File;
import java.util.Observer;

public interface IModel {
    // Maze
    void generateMaze(int rows, int cols);
    Maze getMaze();
    Solution getSolution();
    void solveMaze();


    // Player
    void movePlayer(KeyCode movement);
    int getPlayerPositionRow();
    int getPlayerPositionColumn();
    void MouseMovePlayer(MouseEvent mouseE, MazeDisplayer mazeDis);


    // Server
    void startServers();
    void stopServers();


    // Game General
    void saveMaze(File f);
    void loadFile(File f);
    void exitApp();

    void assignObserver(Observer o);
}
