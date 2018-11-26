package Beep.Beep.Dealership.Application.UI;

import Beep.Beep.Dealership.Application.Core.AssistFunction;
import Beep.Beep.Dealership.Application.Core.Information;
import Beep.Beep.Dealership.Application.Core.Library;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.control.*;
import javafx.application.*;
import javafx.util.Callback;
import javafx.scene.control.Alert.*;
import java.io.*;

public class LoginController {
    @FXML
    private BorderPane frame;
    @FXML
    private Button submitButton;
    @FXML
    private TextField usernameBox;
    @FXML
    private PasswordField passwordBox;
    @FXML
    private Label statusMessage;
    @FXML
    private MenuItem exitItem;
    @FXML
    private MenuItem aboutItem;
    @FXML
    private Button uclearSearch;
    @FXML
    private Button pclearSearch;
    @FXML
    private Menu helpMenu;
    @FXML
    private MenuItem logItem;

    public void initialize() {
        try{
            updateStatus(null);
            exitItem.setOnAction(event -> {
                Platform.exit();
            });

            aboutItem.setOnAction(event -> {
                Alert alert = new Alert(AlertType.INFORMATION, null, ButtonType.OK);
                alert.setTitle("About");
                alert.setHeaderText(Information.getTitle());
                alert.setContentText(Information.getCompanyInfo());
                DialogPane dialogPane = alert.getDialogPane();
                dialogPane.getStylesheets().add("css/Dialog.css");
                alert.showAndWait();
            });

            submitButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent e) {
                    switchToInventoryScreen();
                }
            });

            //Clear search text field on 'X' click
            uclearSearch.setOnAction(new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent e) {
                    usernameBox.setText(null);
                }
            });

            //Clear search text field on 'X' click
            pclearSearch.setOnAction(new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent e) {
                    passwordBox.setText(null);
                }
            });

            usernameBox.textProperty().addListener((obs, oldText, newText) -> {
                uclearSearch.setVisible(true);
                if(AssistFunction.IsEmptyOrNull(usernameBox.getText())){
                    uclearSearch.setVisible(false);
                }
            });

            passwordBox.textProperty().addListener((obs, oldText, newText) -> {
                pclearSearch.setVisible(true);
                if(AssistFunction.IsEmptyOrNull(passwordBox.getText())||passwordBox.getText().length()<=0){
                    pclearSearch.setVisible(false);
                }
            });

            helpMenu.setOnShowing(event -> {
                logItem.setDisable(true);
                if(Library.logFileExists())
                    logItem.setDisable(false);
            });

            logItem.setOnAction(event -> {
                AssistFunction.OpenFile(Library.getLogFile());
            });
        } catch (Exception ex){
            Library.writeLog(ex);
        }
    }

    //Switches to the inventory screen
    private void switchToInventoryScreen() {
        try{
            Screens screen = new Screens(frame.getScene());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Inventory.fxml"));
            loader.setController(new InventoryController());
            screen.addScreen("inventory", (Pane)loader.load());
            screen.activate("inventory");
            screen.centerWindow();
        }
        catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

    private void updateStatus(String message){
        if(!AssistFunction.IsEmptyOrNull(message))
            statusMessage.setText(message);
        else
            statusMessage.setText("");
    }
}
