package Beep.Beep.Dealership.Application.UI;

import Beep.Beep.Dealership.Application.Core.Information;
import javafx.application.Application;
import java.util.*;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.*;
import javafx.scene.*;
import javafx.stage.*;
import javafx.geometry.*;
import javafx.fxml.*;

public class Screens {
    private HashMap<String, Pane> screenMap = new HashMap<>();
    private Scene main;

    public Screens(Scene main) {
        this.main = main;
    }

    protected void addScreen(String name, Pane pane){
        if(!screenMap.containsKey(name))
            screenMap.put(name, pane);
    }

    protected void removeScreen(String name){
        if(screenMap.containsKey(name))
            screenMap.remove(name);
    }

    protected void activate(String name){
        main.setRoot(screenMap.get(name));
        main.getWindow().sizeToScene();
    }

    public void showWindow(String pathToFXML) {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(pathToFXML));
            this.showActualWindow((Parent)fxmlLoader.load(), 450, 300, true);
        } catch (Exception ex){
            return;
        }
    }

    public void showWindow(Parent fxmlParent) {
        this.showActualWindow(fxmlParent, 450, 300, true);
    }

    public void showActualWindow(Parent fxmlParent, int width, int height, Boolean blockOwner) {
        try {
            Parent root = fxmlParent;
            Stage stage = new Stage();
            if(blockOwner){
                stage.initModality(Modality.WINDOW_MODAL);
                stage.initOwner(this.main.getWindow());
            }
            stage.setTitle(Information.getTitle());
            stage.setScene(new Scene(root, width, height));
            stage.sizeToScene();
            if(blockOwner)
                stage.showAndWait();
            else
                stage.show();
            //main.getWindow().getScene().getWindow().hide();
        }
        catch(Exception e) { return; }
    }

    public void centerWindow() {
        try{
            this.main.getWindow().centerOnScreen();
        }
        catch (Exception ex) { return; }
    }

    public static void showMessage(String message){
        showMessage(message, Alert.AlertType.INFORMATION);
    }

    public static void showMessage(String message, Alert.AlertType type){
        Alert alert = new Alert(type, null, ButtonType.OK);
        alert.setTitle(Information.getTitle());
        alert.setHeaderText(message);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add("css/ShowMessage.css"); //Add custom css
        alert.showAndWait();
    }
}
