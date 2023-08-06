package View;

import Server.Configurations;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

public class PropertiesController implements Initializable{

    @FXML
    public Stage stage;
    public ChoiceBox<String> generator;
    public ChoiceBox<String> searchingAlgorithm;
    Properties properties = new Properties();

    public static String mazeName = "MyMazeGenerator";
    public static String searchName = "BreadthFirstSearch";
    private int player = Configurations.player();


    @Override
    public void initialize(URL location, ResourceBundle resources){
        generator.getItems().addAll("EmptyMazeGenerator", "SimpleMazeGenerator", "MyMazeGenerator");
        searchingAlgorithm.getItems().addAll("BreadthFirstSearch", "DepthFirstSearch", "BestFirstSearch");

        try{

            properties.load(new FileInputStream("Resources/config.properties"));

            switch (mazeName) {
                case "MyMazeGenerator" -> generator.setValue("MyMazeGenerator");
                case "SimpleMazeGenerator" -> generator.setValue("SimpleMazeGenerator");
                case "EmptyMazeGenerator" -> generator.setValue("EmptyMazeGenerator");
            }

            switch (searchName) {
                case "BestFirstSearch" -> searchingAlgorithm.setValue("BestFirstSearch");
                case "DepthFirstSearch" -> searchingAlgorithm.setValue("DepthFirstSearch");
                case "BreadthFirstSearch" -> searchingAlgorithm.setValue("BreadthFirstSearch");
            }

        }
        catch (Exception e){}
    }

    public void Spongebob(ActionEvent event) {
        player=1;
    }

    public void Petrick(ActionEvent event) {
        player=2;
    }

    public void Squidward(ActionEvent event) {
        player=3;
    }

    public void Mr_Krabs(ActionEvent event) {
        player=4;

    }

    public void Plankton(ActionEvent event) {
        player=5;

    }



    public void UpdateClicked(ActionEvent event) throws IOException {
        mazeName = generator.getValue();
        searchName = searchingAlgorithm.getValue();
        int Type = Configurations.player();
            try (OutputStream output = new FileOutputStream("Resources/config.properties")) {
                Properties prop = new Properties();
                // set the properties value
                prop.setProperty("mazeGeneratingAlgorithm", this.mazeName);
                prop.setProperty("mazeSearchingAlgorithm", this.searchName);
                prop.setProperty("player", String.valueOf(player));
                // save properties to project root folder
                prop.store(output, null);
                //Close The Screen

            } catch (Exception io) {
                io.printStackTrace();
            }
//
//        FileWriter br = new FileWriter("Resources/config.properties");
//        br.write("threadPoolSize=3\n" +
//                "mazeGeneratingAlgorithm=" + mazeName +"\n" +
//                "mazeSearchingAlgorithm=" + searchName);
//        br.close();

    }

}