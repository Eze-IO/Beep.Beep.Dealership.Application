package Beep.Beep.Dealership.Application;

import Beep.Beep.Dealership.Application.Core.Information;
import Beep.Beep.Dealership.Application.UI.Screens;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import Beep.Beep.Dealership.Application.Core.*;
import javax.swing.*;
import java.awt.*;
import javax.swing.plaf.metal.*;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        try{
            Information info = new Information();

            //Set variables
            Parent root = FXMLLoader.load(getClass().getResource("UI/Login.fxml"));
            primaryStage.setTitle(info.getTitle());
            primaryStage.setScene(new Scene(root, 900, 600));
            primaryStage.getIcons().add(new Image("/images/wheel.png"));

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
            Platform.exit(); //Close on error
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
