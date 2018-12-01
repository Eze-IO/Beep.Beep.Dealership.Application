package Beep.Beep.Dealership.Application.UI;

import Beep.Beep.Dealership.Application.Core.AssistFunction;
import Beep.Beep.Dealership.Application.Core.Information;
import Beep.Beep.Dealership.Application.Core.Library;
import javafx.event.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.control.*;
import javafx.application.*;
import javafx.scene.control.Alert.*;

//Controller class for 'Login.fxml'
public class LoginController {

    //List of ui components
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
    /*-------------------------*/

    public void initialize() {
        try{

            //Empty login status label on initialization
            updateStatus(null);

            //Set 'exit' menu item click event
            exitItem.setOnAction(event -> {
                Platform.exit();
            });

            //Set 'about' menu item click event
            aboutItem.setOnAction(event -> {
                Alert alert = new Alert(AlertType.INFORMATION, null, ButtonType.OK);
                alert.setTitle("About");
                alert.setHeaderText(Information.getTitle());
                alert.setContentText(Information.getCompanyInfo());
                DialogPane dialogPane = alert.getDialogPane();
                dialogPane.getStylesheets().add("css/Dialog.css"); //Add custom css
                alert.showAndWait();
            });

            //Set 'exit' menu item click event
            submitButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent e) {
                    attemptLogin();
                }
            });

            //Clear username text field on 'X' click
            uclearSearch.setOnAction(new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent e) {
                    usernameBox.setText(null);
                }
            });

            //Clear password text field on 'X' click
            pclearSearch.setOnAction(new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent e) {
                    passwordBox.setText(null);
                }
            });

            //Set 'X' button visibility when username text changes or is empty
            usernameBox.textProperty().addListener((obs, oldText, newText) -> {
                uclearSearch.setVisible(true);
                if(AssistFunction.IsEmptyOrNull(usernameBox.getText())||passwordBox.getText().length()<=0){
                    uclearSearch.setVisible(false);
                }
            });

            //Set 'X' button visibility when password text changes or is empty
            passwordBox.textProperty().addListener((obs, oldText, newText) -> {
                pclearSearch.setVisible(true);
                if(AssistFunction.IsEmptyOrNull(passwordBox.getText())||passwordBox.getText().length()<=0){
                    pclearSearch.setVisible(false);
                }
            });

            /*
               Set event when the 'help' menu is
               showing and set the 'Open events logs'
               to visible if it exists
            */
            helpMenu.setOnShowing(event -> {
                logItem.setDisable(true);
                if(Library.logFileExists())
                    logItem.setDisable(false);
            });

            /*
                Set event for 'Open events logs'
                menu item when it's is clicked
             */
            logItem.setOnAction(event -> {
                AssistFunction.OpenFile(Library.getLogFile());
            });
        } catch (Exception ex){
            Library.writeLog(ex);
        }
    }

    //Variables for storing credentials
    private String username;
    private String password;

    //Logs the user in and switches the page if successful
    private void attemptLogin() {
        username = ((AssistFunction.IsEmptyOrNull(usernameBox.getText())) ? "N/A" : usernameBox.getText());
        Library.writeLog(String.format("User '%s' has logged in!", username));
        password = null;
        switchToInventoryScreen();
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

    //Sets the text in the status label on the UI
    private void updateStatus(String message){
        if(!AssistFunction.IsEmptyOrNull(message))
            statusMessage.setText(message);
        else
            statusMessage.setText("");
    }
}
