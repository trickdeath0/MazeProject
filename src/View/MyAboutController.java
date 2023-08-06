package View;


import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.nio.file.Paths;

public class MyAboutController {
    @FXML
    private javafx.scene.control.Button close_btn;
    private Stage aboutStage;

    public Canvas Us;

    public void openAboutStage() { //call it from MyViewController
        aboutStage = new Stage();
        aboutStage.setTitle("About Us");

        try {
            Pane pane = new Pane();
            Stage stage = new Stage();
            stage.setTitle("About Us");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("About.fxml").openStream());
            String path = "Resources/Images/aboutUs.png";
            Image imageUserWonScene = new Image(Paths.get(path).toUri().toString());
            ImageView imageviewUserWonScene = new ImageView(imageUserWonScene);
            /* add ImageView to Pane's children */
            pane.getChildren().add(imageviewUserWonScene);
            Scene scene = new Scene(root, 700, 500);

            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeMyAboutStage(ActionEvent actionEvent) {
        Stage stage = (Stage) close_btn.getScene().getWindow();
        stage.close();
    }
}
