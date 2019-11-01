package Beep.Beep.Dealership.Application.UI;

import Beep.Beep.Dealership.Application.Core.AssistFunction;
import Beep.Beep.Dealership.Application.Core.Database;
import Beep.Beep.Dealership.Application.Core.Entities.Vehicle;
import Beep.Beep.Dealership.Application.Core.Library;
import Beep.Beep.Dealership.Application.Core.LogType;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;

import java.awt.*;
import java.util.function.UnaryOperator;
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

            Pattern nvalidEditingState = Pattern.compile("^(197\\d{1}|198\\d{1}|199\\d{1}|200\\d{1}|201\\d{1}|202\\d{1})");
            UnaryOperator<TextFormatter.Change> nnfilter = nc -> {
                String text = nc.getControlNewText();
                if (validEditingState.matcher(text).matches()) {
                    return nc ;
                } else {
                    return null ;
                }
            };
            StringConverter<Integer> nconverter = new StringConverter<Integer>() {

                @Override
                public Integer fromString(String s) {
                    if (s.isEmpty()) {
                        return 0 ;
                    } else {
                        return Integer.valueOf(s);
                    }
                }

                @Override
                public String toString(Integer i) {
                    if(i.toString().length()>4)
                        return i.toString().substring(0, 4);
                    else
                        return i.toString();
                }
            };
            TextFormatter<Integer> ntextFormatter = new TextFormatter<>(nconverter, 0, nnfilter);
            yearBox.setTextFormatter(ntextFormatter);

            yearBox.textProperty().addListener((obs, oldText, newText) -> {
                toggleSubmitButton();
                try{
                    if(!newText.matches("\\d*(\\.\\d*)?")) {
                        yearBox.setText(newText.replaceAll("[^\\d]", ""));
                    }
                }
                catch(Exception ex){
                    Library.writeLog(ex);
                }
            });

            toggleSubmitButton();
        }
        catch(Exception ex){
            Library.writeLog(ex);
        }

    }

    private void saveToInventory(){
        try{
            Vehicle v = new Vehicle();
            v.name = "unknown";
            v.make = makeBox.getText();
            v.model = modelBox.getText();
            v.year = Integer.parseInt(yearBox.getText());
            v.color = colorBox.getText();
            v.price = Double.parseDouble(priceBox.getText());
            v.sold = false;
            if(Database.saveAnItem(v)){
                Library.writeLog("Added new car to the database");
                updateStatus("Successfully saved new car!");
            } else {
                Library.writeLog("Failed to add a new car to the database");
                updateStatus("Failed to save new car!");
            }
        }
        catch(Exception ex) {
            Library.writeLog(ex, LogType.ERROR);
            return;
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
