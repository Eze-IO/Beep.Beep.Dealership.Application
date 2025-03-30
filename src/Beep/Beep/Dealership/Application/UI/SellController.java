package Beep.Beep.Dealership.Application.UI;

import Beep.Beep.Dealership.Application.Core.AssistFunction;
import Beep.Beep.Dealership.Application.Core.Database;
import Beep.Beep.Dealership.Application.Core.Library;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import Beep.Beep.Dealership.Application.Core.Entities.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class SellController {
    @FXML
    private BorderPane frame;
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

            Pattern validEditingState = Pattern.compile("-?(([1-9][0-9]*)|0)?(\\.[0-9]*)?");
            UnaryOperator<TextFormatter.Change> nfilter = nc -> {
                String text = nc.getControlNewText();
                if (validEditingState.matcher(text).matches()) {
                    return nc ;
                } else {
                    return null ;
                }
            };
            StringConverter<Double> converter = new StringConverter<Double>() {

                @Override
                public Double fromString(String s) {
                    if (s.isEmpty() || "-".equals(s) || ".".equals(s) || "-.".equals(s)) {
                        return 0.0 ;
                    } else {
                        return Double.valueOf(s);
                    }
                }

                @Override
                public String toString(Double d) {
                    return d.toString();
                }
            };
            TextFormatter<Double> textFormatter = new TextFormatter<>(converter, 0.0, nfilter);
            priceBox.setTextFormatter(textFormatter);

            priceBox.textProperty().addListener((obs, oldText, newText) -> {
                toggleSubmitButton();
            });

            customerBox.textProperty().addListener((obs, oldText, newText) -> {
                toggleSubmitButton();
            });

            try{
                Car c = Database.getAnItem(_itemID);
                priceBox.setText(Double.toString(c.price));
            } catch(Exception ee) { /* ignore */ }

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
            if(this._itemID == 0) {
                Screens.showMessage("A car item hasn't been selected!", Alert.AlertType.ERROR);
                Screens screen = new Screens(frame.getScene());
                screen.closeWindow();
                return;
            }
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
