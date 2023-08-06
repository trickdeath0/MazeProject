package View;

import Model.IModel;
import Model.MyModel;
import ViewModel.MyViewModel;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.ImageCursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

public class Main extends Application {
    public static Stage StartGameStage;
    public static Scene StartGame;
    public static Parent ParentStartGame;
    public static Stage ChoosePlayerStage;
    public static Scene ChoosePlayer;
    public static Parent ParentChoosePlayer;

    public static Stage MainStage;
    public static Scene MainScene;
    public static Parent MainParent;

    public static Stage mazeNew;
    public static Stage goodBye = new Stage();



    private MediaPlayer music;

    @Override
    public void start(Stage primaryStage) throws Exception{
//        String fileName =  "Resources/Sounds/SB Music_ Grass Skirt Chase.mp3";
//        File musicPath = new File(fileName);
//        music = new MediaPlayer(new Media(musicPath.toURI().toString()));
//        music.setStartTime(Duration.millis(20000));
//        music.play();

        // config: --module-path "\path\to\javafx-sdk-17\lib" --add-modules javafx.controls,javafx.fxml,javafx.media

        // First Scene - Welcome
        ParentStartGame = FXMLLoader.load(getClass().getResource("StartGame.fxml"));
        StartGame = new Scene(ParentStartGame,800,450);
        Image image = new Image("Images/images.png");
        StartGame.setCursor(new ImageCursor(image, image.getWidth() / 2, image.getHeight() /2));
        StartGameStage = new Stage();
        StartGameStage.setScene(StartGame);
        StartGameStage.initStyle(StageStyle.UNDECORATED);
        StartGameStage.getIcons().add(new Image("Images/icon.png"));



        // Second Scene - Choose player
        ParentChoosePlayer = FXMLLoader.load(getClass().getResource("ChoosePlayer.fxml"));
        ChoosePlayer = new Scene(ParentChoosePlayer,500,200);
        ChoosePlayerStage = new Stage();
        ChoosePlayerStage.setScene(ChoosePlayer);
        ChoosePlayerStage.initStyle(StageStyle.UNDECORATED);
        ChoosePlayerStage.getIcons().add(new Image("Images/icon.png"));
        MazeDisplayer.audioChooser(0);

        // Main Game
        MainStage = primaryStage;
//        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MyView.fxml"));
//        Parent root =  fxmlLoader.load();
//        MainStage.setTitle("SpongeBob Maze Game");
//        Scene scene = new Scene(root, 800, 700);
//        MainStage.setScene(scene);
//        MainStage.getIcons().add(new Image("Images/icon.png"));
//        MyModel model = new MyModel();
//        setStageCloseEvent(MainStage, model);
//
//        //IModel model = new MyModel();
//        MyViewModel viewModel = new MyViewModel(model);
//        MyViewController view = fxmlLoader.getController();
//        view.setViewModel(viewModel);
//        view.setResize(scene);
        StartGameStage.show();
    }

    public static void reset () throws IOException {
        Main.mazeNew = MainStage;
        MazeDisplayer.audioChooser(0);
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("MyView.fxml"));
        Parent root = fxmlLoader.load();
        IModel model = new MyModel();
        MyViewModel viewModel = new MyViewModel(model);
        Scene scene = new Scene(root,800,700);
        MyViewController view = fxmlLoader.getController();
        view.setViewModel(viewModel);
        view.setResize(scene);
        Main.mazeNew.setScene(scene);
    }


    private static void setStageCloseEvent(Stage primaryStage, MyModel model) {
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent windowEvent) {
                // call stage to show img bye
                bye();

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Are you sure you want to exit?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK){
                    // ... user chose OK
                    // Close the program properly
                    model.exitApp();

                    System.exit(0);
                } else { windowEvent.consume(); goodBye.hide();}
            }
        });
    }

    public static void bye(){
        try {
            Pane pane = new Pane();

            String path = "Resources/Images/good_bye.jpg";
            Image imageUserWonScene = new Image(Paths.get(path).toUri().toString());
            ImageView imageviewUserWonScene = new ImageView(imageUserWonScene);
            /* add ImageView to Pane's children */
            pane.getChildren().add(imageviewUserWonScene);
            Scene scene = new Scene(pane);
            goodBye.setScene(scene);
            goodBye.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void ShowMain() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("MyView.fxml"));
        Parent root =  fxmlLoader.load();
        MainStage.setTitle("SpongeBob Maze Game");
        Scene scene = new Scene(root, 800, 700);
        MainStage.setScene(scene);
        MainStage.getIcons().add(new Image("Images/icon.png"));
        MyModel model = new MyModel();
        setStageCloseEvent(MainStage, model);

        //IModel model = new MyModel();
        MyViewModel viewModel = new MyViewModel(model);
        MyViewController view = fxmlLoader.getController();
        view.setViewModel(viewModel);
        view.setResize(scene);
        MainStage.show();
    }




    public static void main(String[] args) {
        launch(args);
    }
}
