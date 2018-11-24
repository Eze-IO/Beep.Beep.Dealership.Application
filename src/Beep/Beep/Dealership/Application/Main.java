package Beep.Beep.Dealership.Application;

import Beep.Beep.Dealership.Application.Core.Information;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import Beep.Beep.Dealership.Application.Core.*;
import Beep.Beep.Dealership.Application.Core.Entities.*;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        //Set variables
        Parent root = FXMLLoader.load(getClass().getResource("UI/Login.fxml"));
        primaryStage.setTitle(Information.getTitle());
        primaryStage.setScene(new Scene(root, 900, 600));
        primaryStage.getIcons().add(new Image("/horn.png"));

        //Set window size
        primaryStage.setMinHeight(primaryStage.getHeight());
        primaryStage.setMinWidth(primaryStage.getWidth());
        primaryStage.sizeToScene();


        //Show window
        primaryStage.show();
        primaryStage.sizeToScene();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
