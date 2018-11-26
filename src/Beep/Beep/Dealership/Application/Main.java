package Beep.Beep.Dealership.Application;

import Beep.Beep.Dealership.Application.Core.Information;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import Beep.Beep.Dealership.Application.Core.*;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        try{
            //Set variables
            Parent root = FXMLLoader.load(getClass().getResource("UI/Login.fxml"));
            primaryStage.setTitle(Information.getTitle());
            primaryStage.setScene(new Scene(root, 900, 600));
            primaryStage.getIcons().add(new Image("/images/balloons.png"));

            //Set window size(s)
            primaryStage.setMinHeight(primaryStage.getHeight());
            primaryStage.setMinWidth(primaryStage.getWidth());

            //Show window
            primaryStage.show();
            primaryStage.sizeToScene();
            primaryStage.centerOnScreen();
        }
        catch (Exception ex) {
            Library.writeLog(ex, LogType.CRITICAL_ERROR);
            Platform.exit();
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
