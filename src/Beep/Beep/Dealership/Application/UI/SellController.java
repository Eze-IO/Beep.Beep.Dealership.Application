package Beep.Beep.Dealership.Application.UI;

import Beep.Beep.Dealership.Application.Core.AssistFunction;
import Beep.Beep.Dealership.Application.Core.Database;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import Beep.Beep.Dealership.Application.Core.Entities.*;

public class SellController {
    @FXML
    private TextField customerBox;
    @FXML
    private TextField priceBox;
    @FXML
    private Label statusMessage;
    @FXML
    private Button submitButton;

    public void initialize() {
        updateStatus(null);

        submitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                sellItem();
            }
        });
    }

    private int _itemID;
    public void setItemID(int ID){
        this._itemID = ID;
    }

    //Displays status or error message
    private void updateStatus(String message){
        if(!AssistFunction.IsEmptyOrNull(message))
            statusMessage.setText(message);
        else
            statusMessage.setText("");
    }

    private void sellItem(){
        if(!AssistFunction.IsEmptyOrNull(customerBox.getText())
                &&!AssistFunction.IsEmptyOrNull(priceBox.getText())){
            Car c = Database.getAnItem(_itemID);
            c.name = customerBox.getText();
            c.price = Double.parseDouble(priceBox.getText());
            c.sold = true;
            if(Database.updateAnItem(c)){
                updateStatus("Successfully updated car status!");
            }
            else {
                updateStatus("Failed to update car status!");
            }
        } else {
            updateStatus("Please fill the all text fields above!");
        }
    }
}
