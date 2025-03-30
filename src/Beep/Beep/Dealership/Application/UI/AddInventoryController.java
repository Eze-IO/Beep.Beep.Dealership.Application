package Beep.Beep.Dealership.Application.UI;

import Beep.Beep.Dealership.Application.Core.AssistFunction;
import Beep.Beep.Dealership.Application.Core.Database;
import Beep.Beep.Dealership.Application.Core.Entities.Car;
import Beep.Beep.Dealership.Application.Core.Entities.Vehicle;
import Beep.Beep.Dealership.Application.Core.Library;
import Beep.Beep.Dealership.Application.Core.LogType;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;

import java.awt.*;
import java.util.Arrays;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddInventoryController {
    @FXML
    private TextField makeBox;
    @FXML
    private TextField modelBox;
    @FXML
    private TextField colorBox;
    @FXML
    private TextField yearBox;
    @FXML
    private TextField priceBox;
    @FXML
    private Button submitButton;
    @FXML
    private Label statusLabel;

    public void initialize() {

        try{
            //Clear on start
            updateStatus(null);

            //Set submit button click event
            submitButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent e) {
                    saveToInventory();
                }
            });

            makeBox.textProperty().addListener((obs, oldText, newText) -> {
                toggleSubmitButton();
            });

            modelBox.textProperty().addListener((obs, oldText, newText) -> {
                toggleSubmitButton();
            });

            colorBox.textProperty().addListener((obs, oldText, newText) -> {
                toggleSubmitButton();
            });

            priceBox.addEventFilter(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>() {
                @Override public void handle(KeyEvent keyEvent) {
                    if (!".0123456789".contains(keyEvent.getCharacter())) {
                        keyEvent.consume();
                    }
                }
            });

            priceBox.textProperty().addListener(new ChangeListener<String>() {
                @Override public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                    toggleSubmitButton();

                    try{
                        newValue = Double.valueOf(newValue).toString();
                    }
                    catch(Exception ex){
                        newValue = "0.0";
                    }

                    String regex = "^[0-9]+([,.][0-9]+?)?$";
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(newValue);
                    if(!matcher.matches()) {
                        priceBox.setText(oldValue);
                    }
                }
            });

            yearBox.addEventFilter(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>() {
                @Override public void handle(KeyEvent keyEvent) {
                    if (!"0123456789".contains(keyEvent.getCharacter())) {
                        keyEvent.consume();
                    }
                }
            });

            yearBox.textProperty().addListener(new ChangeListener<String>() {
                @Override public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                    toggleSubmitButton();

                    String regex = "^[1-9]\\d*$";
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(newValue);
                    if(!matcher.matches()&&newValue.length()>1) {
                        yearBox.setText(oldValue);
                    }

                    if(newValue.length()>4){
                        yearBox.setText(oldValue);
                    }
                }
            });

            toggleSubmitButton();
        }
        catch(Exception ex){
            Library.writeLog(ex);
        }

    }

    private static Car _lastItemAdded = null;

    public static Car GetLastSaved() {
        return AddInventoryController._lastItemAdded;
    }

    private void saveToInventory(){
        try{
            Vehicle v = new Vehicle();
            v.name = "unknown";
            v.make = makeBox.getText().trim();
            v.model = modelBox.getText().trim();
            v.year = Integer.parseInt(yearBox.getText().trim());
            v.color = colorBox.getText().trim();
            v.price = Double.parseDouble(priceBox.getText().trim());
            v.sold = false;

            if(Database.saveAnItem(v)){
                Library.writeLog("Added new car to the database");
                updateStatus("Successfully saved new car!");
            } else {
                Library.writeLog("Failed to add a new car to the database");
                updateStatus("Failed to save new car!");
            }

            try {
                Car lastCar = Arrays.stream(Database.searchForItems(String.format("%s %s %s %s", v.year, v.price, v.make, v.model), true)).findFirst().get();
                AddInventoryController._lastItemAdded = lastCar;
            }
            catch(Exception ex) {
                Library.writeLog(ex);
            }
        }
        catch(Exception ex) {
            Library.writeLog(ex, LogType.ERROR);
        }
    }

    private void toggleSubmitButton(){
        submitButton.setDisable((AssistFunction.IsEmptyOrNull(makeBox.getText())||AssistFunction.IsEmptyOrNull(modelBox.getText())||AssistFunction.IsEmptyOrNull(colorBox.getText())
                ||AssistFunction.IsEmptyOrNull(yearBox.getText())||AssistFunction.IsEmptyOrNull(priceBox.getText())));
    }

    //Displays status or error message
    private void updateStatus(String message){
        if(!AssistFunction.IsEmptyOrNull(message))
            statusLabel.setText(message);
        else
            statusLabel.setText("");
    }
}
