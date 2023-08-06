package View;


import javafx.event.ActionEvent;

public class StartGame {
    MazeDisplayer mazeDisplayer;

    public void StartGame(ActionEvent event) {
        Main.StartGameStage.hide();
        Main.ChoosePlayerStage.show();
    }
}
