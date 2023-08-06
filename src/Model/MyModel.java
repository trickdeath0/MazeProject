package Model;

import Client.*;
import Server.*;
import IO.*;
import View.Main;
import View.MazeDisplayer;
import ViewModel.MyViewModel;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Observer;

public class MyModel extends Observable implements IModel {
    private static Maze maze;
    private int playerRow;
    private int playerCol;
    private int goalRow;
    private int goalColumn;
    private Server serverGenerateMaze;
    private Server serverSolveMaze;
    private Solution solution;
    private MyViewModel myViewModel;
    private MazeDisplayer mazeDisplayer;
    boolean win = false;

    @Override
    public Maze getMaze() {
        return maze;
    }
    @Override
    public Solution getSolution() {
        return solution;
    }
    @Override
    public int getPlayerPositionRow() {
        return this.playerRow;
    }
    @Override
    public int getPlayerPositionColumn() {
        return this.playerCol;
    }

    public MyModel() throws IOException {
        myViewModel = new MyViewModel(this);
        mazeDisplayer = new MazeDisplayer();
        this.serverGenerateMaze =  new Server(5400, 1000, new ServerStrategyGenerateMaze());
        this.serverSolveMaze =  new Server(5401, 1000, new ServerStrategySolveSearchProblem());
        startServers();
    }

    @Override
    public void generateMaze(int rows, int cols) {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5400, (IClientStrategy) (inputStream, outputStream) -> {
                try {
                    ObjectOutputStream toServer = new ObjectOutputStream(outputStream);
                    ObjectInputStream fromServer = new ObjectInputStream(inputStream);
                    toServer.flush();
                    int[] mazeDimensions = new int[]{cols,rows};
                    toServer.writeObject(mazeDimensions); //send maze dimensions to server
                    toServer.flush();
                    byte[] compressedMaze = (byte[]) fromServer.readObject(); //read generated maze (compressed with MyCompressor) from server
                    InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                    byte[] decompressedMaze = new byte[rows * cols + 10000/*CHANGE SIZE ACCORDING TO YOU MAZE SIZE*/]; //allocating byte[] for the decompressed maze -
                    is.read(decompressedMaze); //Fill decompressedMaze with bytes
                    maze = new Maze(decompressedMaze);
                    playerRow = maze.getStartPosition().getRowIndex();
                    playerCol = maze.getStartPosition().getColumnIndex();
                    goalRow = maze.getGoalPosition().getRowIndex();
                    goalColumn = maze.getGoalPosition().getColumnIndex();
                    setChanged();
                    notifyObservers("Maze Generated");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void solveMaze() {
        maze.setStart(new Position(playerRow,playerCol));
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5401, (IClientStrategy) (inFromServer, outToServer) -> {
                try {
                    ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                    ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                    toServer.flush();
                    toServer.writeObject(maze); //send maze to server
                    toServer.flush();
                    solution = (Solution) fromServer.readObject(); //read generated maze (compressed with MyCompressor) from server
                    setChanged();
                    notifyObservers("solution");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }); client.communicateWithServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void movePlayer(KeyCode movement) {
        if(maze==null)
            return;
        else {
            switch (movement) {
                case UP:
                        movePlayer(playerRow - 1, playerCol);
                    break;
                case DOWN:
                        movePlayer(playerRow + 1, playerCol);
                    break;
                case RIGHT:
                        movePlayer(playerRow, playerCol + 1);
                    break;
                case LEFT:
                        movePlayer(playerRow, playerCol - 1);
                    break;
                case NUMPAD1:
                        movePlayer(playerRow + 1, playerCol - 1);
                    break;
                case NUMPAD2:
                        movePlayer(playerRow + 1, playerCol);
                    break;
                case NUMPAD3:
                        movePlayer(playerRow + 1, playerCol + 1);
                    break;
                case NUMPAD4:
                        movePlayer(playerRow, playerCol - 1);
                    break;
                case NUMPAD6:
                        movePlayer(playerRow, playerCol + 1);
                    break;
                case NUMPAD7:
                        movePlayer(playerRow - 1, playerCol - 1);
                    break;
                case NUMPAD8:
                        movePlayer(playerRow - 1, playerCol);
                    break;
                case NUMPAD9:
                        movePlayer(playerRow - 1, playerCol + 1);
                    break;
                default:
                    break;
            }
        }
    }
    public boolean legalMovement(int row, int col) {
        if (row >= 0 && row < maze.getMazeArray().length && col >= 0  && col < maze.getMazeArray()[0].length && maze.getMazeArray()[row][col] != 1)
            return true;
        return false;
    }

    public void movePlayer(int row, int col){
        if(this.maze != null)
        {
            if (!legalMovement(row, col))
                return;;
            this.playerRow = row;
            this.playerCol = col;
            setChanged();
            notifyObservers("Move Player");
            if(this.playerRow==this.goalRow && this.playerCol==this.goalColumn)
            {
                this.win = true;
                setChanged();
                notifyObservers("maze solved");
            }
        }
    }

    @Override
    public void MouseMovePlayer(MouseEvent mouseEvent, MazeDisplayer mazeDisplayer) {
        if (maze != null) {
            double xValue = mouseEvent.getX() / (mazeDisplayer.getWidth() / maze.getMazeArray()[0].length);
            double firstDouble = mouseEvent.getY();
            double yValue = firstDouble / (mazeDisplayer.getHeight() / maze.getMazeArray().length);
            if (yValue < mazeDisplayer.getPlayerRow())
                myViewModel.movePlayer(KeyCode.UP);
            else if (xValue > mazeDisplayer.getPlayerCol() +1)
                myViewModel.movePlayer(KeyCode.RIGHT);
            else if (xValue < mazeDisplayer.getPlayerCol())
                myViewModel.movePlayer(KeyCode.LEFT);
            else if (yValue > mazeDisplayer.getPlayerRow() +1)
                myViewModel.movePlayer(KeyCode.DOWN);
            mazeDisplayer.draw();
        }
    }

    @Override
    public void startServers() {
        this.serverGenerateMaze.start();
        this.serverSolveMaze.start();
    }

    @Override
    public void stopServers() {
        serverGenerateMaze.stop();
        serverSolveMaze.stop();
    }

    @Override
    public void saveMaze(File f) {
        try
        {
            if (f.exists())
                f.delete();
            f.createNewFile();
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f));
            out.writeObject(maze);
            out.flush();
            out.close();
        }
        catch (IOException e) {
        }
    }

    @Override
    public void loadFile(File f) {
        try{
            ObjectInputStream objectInputStream= new ObjectInputStream((new FileInputStream(f)));
            maze = (Maze) objectInputStream.readObject();
            playerRow = maze.getStartPosition().getRowIndex();
            playerCol = maze.getStartPosition().getColumnIndex();
            goalRow = maze.getGoalPosition().getRowIndex();
            goalColumn = maze.getGoalPosition().getColumnIndex();
            solution = null;
            this.win = false;
            // Observer
            setChanged();
            notifyObservers("Load Maze");
        }catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void exitApp() {
        stopServers();
    }

    @Override
    public void assignObserver(Observer o) {
        this.addObserver(o);
    }
}