package View;


import Server.Configurations;
import algorithms.mazeGenerators.AMazeGenerator;
import javafx.event.ActionEvent;
import javafx.scene.layout.AnchorPane;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

public class ChoosePlayer {
    int player=1;


    public void Spongebob(ActionEvent event) throws IOException {
        player=1;
        ChangeCongf();
        Main.ChoosePlayerStage.hide();
        Main.ShowMain();
    }

    public void Petrick(ActionEvent event) throws IOException {
        player=2;
        ChangeCongf();
        Main.ChoosePlayerStage.hide();
        Main.ShowMain();
    }

    public void Squidward(ActionEvent event) throws IOException {
        player=3;
        ChangeCongf();
        Main.ChoosePlayerStage.hide();
        Main.ShowMain();
    }

    public void Mr_Krabs(ActionEvent event) throws IOException {
        player=4;
        ChangeCongf();
        Main.ChoosePlayerStage.hide();
        Main.ShowMain();
    }

    public void Plankton(ActionEvent event) throws IOException {
        player=5;
        ChangeCongf();
        Main.ChoosePlayerStage.hide();
        Main.ShowMain();
    }

    public void ChangeCongf()
    {
        try (OutputStream output = new FileOutputStream("Resources/config.properties")) {
            Properties prop = new Properties();
            // set the properties value
            prop.setProperty("mazeGeneratingAlgorithm", "MyMazeGenerator");
            prop.setProperty("mazeSearchingAlgorithm", "BestFirstSearch");
            prop.setProperty("player", String.valueOf(this.player));
            // save properties to project root folder
            prop.store(output, null);
            //Close The Screen

        } catch (Exception io) {
            io.printStackTrace();
        }
    }
    /// in configorain add func player --> return int that place in the config as "player"

    // in option
//    1. int Type = Configurations.player();
//
//    2.    {
//        try (OutputStream output = new FileOutputStream("Resources/config.properties")) {
//            Properties prop = new Properties();
//            // set the properties value
//        properties.setProperty(("mazeGeneratingAlgorithm"),("MyMazeGenerator"));
//        properties.setProperty("mazeSearchingAlgorithm", "BestFirstSearch");
//            prop.setProperty("player", Type);
//            // save properties to project root folder
//            prop.store(output, null);
//            //Close The Screen
//
//        } catch (Exception io) {
//            io.printStackTrace();
//        }




}
