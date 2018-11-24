package Beep.Beep.Dealership.Application.UI;

import Beep.Beep.Dealership.Application.Core.AssistFunction;
import Beep.Beep.Dealership.Application.Core.Database;
import Beep.Beep.Dealership.Application.Core.Entities.Vehicle;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.awt.*;

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

        //Clear on start
        updateStatus(null);

        //Set submit button click event
        submitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                saveToInventory();
            }
        });
    }

    private void saveToInventory(){
        try{
            Vehicle v = new Vehicle();
            if(AssistFunction.IsEmptyOrNull(makeBox.getText()))
            {
                updateStatus("Make of car is required! Please enter a make!");
                return;
            }
            if(AssistFunction.IsEmptyOrNull(modelBox.getText()))
            {
                updateStatus("Model of car is required! Please enter the model!");
                return;
            }
            if(AssistFunction.IsEmptyOrNull(colorBox.getText()))
            {
                updateStatus("Color of car is required! Please enter the color!");
                return;
            }
            if(AssistFunction.IsEmptyOrNull(yearBox.getText()))
            {
                updateStatus("Year of car is required! Please enter the year!");
                return;
            }
            if(AssistFunction.IsEmptyOrNull(priceBox.getText()))
            {
                updateStatus("Price of car is required! Please enter the initial price!");
                return;
            }
            v.name = "unknown";
            v.make = makeBox.getText();
            v.model = modelBox.getText();
            v.year = Integer.parseInt(yearBox.getText());
            v.color = colorBox.getText();
            v.price = Double.parseDouble(priceBox.getText());
            v.sold = false;
            if(Database.saveAnItem(v))
                updateStatus("Successfully saved new car!");
            else
                updateStatus("Failed to save new car!");
        }
        catch(Exception ex) {
            return;
        }
    }

    //Displays status or error message
    private void updateStatus(String message){
        if(!AssistFunction.IsEmptyOrNull(message))
            statusLabel.setText(message);
        else
            statusLabel.setText("");
    }
}
