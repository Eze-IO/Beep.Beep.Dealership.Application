package Beep.Beep.Dealership.Application.UI;

import Beep.Beep.Dealership.Application.Core.AssistFunction;
import Beep.Beep.Dealership.Application.Core.Database;
import Beep.Beep.Dealership.Application.Core.Library;
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
        try{
            updateStatus(null);

            submitButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent e) {
                    sellItem();
                }
            });

            priceBox.textProperty().addListener((obs, oldText, newText) -> {
                toggleSubmitButton();
                if(!newText.matches("\\d*(\\.\\d*)?")) {
                    priceBox.setText(newText.replaceAll("[^\\d]", ""));
                }
            });

            customerBox.textProperty().addListener((obs, oldText, newText) -> {
                toggleSubmitButton();
            });

            toggleSubmitButton();
        } catch(Exception ex){
            Library.writeLog(ex);
        }
    }

    private int _itemID;
    public void setItemID(int ID){
        this._itemID = ID;
    }

    private void toggleSubmitButton(){
        submitButton.setDisable((AssistFunction.IsEmptyOrNull(customerBox.getText())||AssistFunction.IsEmptyOrNull(priceBox.getText())));
    }

    //Displays status or error message
    private void updateStatus(String message){
        if(!AssistFunction.IsEmptyOrNull(message))
            statusMessage.setText(message);
        else
            statusMessage.setText("");
    }

    private void sellItem(){
        try{
            if(!AssistFunction.IsEmptyOrNull(customerBox.getText())
                    &&!AssistFunction.IsEmptyOrNull(priceBox.getText())){
                Car c = Database.getAnItem(_itemID);
                c.name = customerBox.getText();
                c.price = Double.parseDouble(priceBox.getText());
                c.sold = true;
                if(Database.updateAnItem(c)){
                    disableAll();
                    Library.writeLog("Sold car with ID ("+ c.getID() +") in the database");
                    updateStatus("Successfully updated car status!");
                }
                else {
                    Library.writeLog("Failed to change car status with ID ("+ c.getID() +") in the database)");
                    updateStatus("Failed to update car status!");
                }
            } else {
                updateStatus("Please fill the all text fields above!");
            }
        } catch(Exception er){
            Library.writeLog(er);
        }
    }

    private void disableAll(){
        try{
            priceBox.setDisable(true);
            customerBox.setDisable(true);
            submitButton.setDisable(true);
            submitButton = null;
        }catch(Exception ex){
            Library.writeLog(ex);
        }
    }
}
