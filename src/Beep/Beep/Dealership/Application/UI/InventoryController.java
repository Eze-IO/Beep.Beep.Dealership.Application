package Beep.Beep.Dealership.Application.UI;

import Beep.Beep.Dealership.Application.Core.Entities.*;
import Beep.Beep.Dealership.Application.Core.AssistFunction;
import Beep.Beep.Dealership.Application.Core.Database;
import Beep.Beep.Dealership.Application.Core.Information;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.SplitPane.*;
import javafx.beans.value.*;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import javafx.scene.input.*;
import java.util.regex.*;
import javafx.event.*;
import javafx.stage.Stage;
import javafx.scene.*;

public class InventoryController {

    @FXML
    private BorderPane frame;
    @FXML
    private SplitPane split;
    @FXML
    private TextField searchBox;
    @FXML
    private Button searchButton;
    @FXML
    private Button clearSearch;
    @FXML
    private StackPane selectionView;
    @FXML
    private Button addButton;
    @FXML
    private Button salesButton;
    @FXML
    private Button deleteButton;
    @FXML
    private MenuItem exitItem;
    @FXML
    private MenuItem salesItem;
    @FXML
    private MenuItem aboutItem;
    @FXML
    private MenuItem logoutItem;
    @FXML
    private AnchorPane modelRightSide;

    public void initialize() {

        searchButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                populateView(searchBox.getText());
            }
        });

        //Clear search text field on 'X' click
        clearSearch.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                clearAll();
            }
        });

        //Show 'X' clear button when text exist in text field
        searchBox.textProperty().addListener((obs, oldText, newText) -> {
            clearSearch.setDisable(false);
            clearSearch.setVisible(true);
            if(AssistFunction.IsEmptyOrNull(searchBox.getText())){
                clearSearch.setVisible(false);
                clearSearch.setDisable(true);
                clearAll();
            }
        });

        addButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                try {
                    Screens s = new Screens(frame.getScene());
                    s.showWindow("AddInventory.fxml");
                    refreshView();
                }
                catch (Exception ex) {
                    return;
                }
            }
        });

        salesButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                switchToSalesScreen(_lastSelection);
            }
        });

        searchBox.widthProperty().addListener((obs, oldVal, newVal) -> {
            if(newVal.doubleValue()<20)
                clearSearch.setVisible(false);
            if(!AssistFunction.IsEmptyOrNull(searchBox.getText())&&(newVal.doubleValue()>20)){
                clearSearch.setVisible(true);
            }
        });

        deleteButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                if(removeFromView(_lastSelection)){
                    System.out.println("Successfully removed item with ID: "+_lastSelection);
                } else {
                    System.out.println("Failed to remove item with ID: "+_lastSelection);
                }
                refreshView();
            }
        });

        exitItem.setOnAction(event -> {
            Platform.exit();
        });

        salesItem.setOnAction(event -> {
            switchToSalesScreen();
        });

        aboutItem.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Hey!", ButtonType.OK);
            alert.setTitle("About");
            alert.setHeaderText(Information.getTitle());
            alert.setContentText(Information.getCompanyInfo());
            alert.showAndWait();
        });

        logoutItem.setOnAction(event -> {
            try{
                Screens screen = new Screens(frame.getScene());
                screen.addScreen("login", FXMLLoader.load(getClass().getResource("Login.fxml")));
                screen.activate("login");
            }
            catch(Exception er) {
                return;
            }
        });

        modelRightSide.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                _lastSelection = 0;
                salesButton.setDisable(true);
                deleteButton.setDisable(true);
            }
        });

        searchBox.setOnKeyPressed(new EventHandler<KeyEvent>()
        {
            @Override
            public void handle(KeyEvent ke)
            {
                if(ke.getCode().equals(KeyCode.ENTER))
                    searchButton.fire();
            }
        });

        if(!AssistFunction.IsEmptyOrNull(this._toSearch)){
            searchBox.setText(this._toSearch);
            populateView(this._toSearch);
        }
    }

    private String _toSearch;
    public void setToSearch(String toSearch){
        this._toSearch = toSearch;
    }

    //Clears the search box, results and disable buttons
    private void clearAll() {
        split.setDividerPosition(0, 1.0);
        salesButton.setDisable(true);
        deleteButton.setDisable(true);
        searchBox.setText("");
        clearView();
    }

    private Node carIcon() {
         Node n = new ImageView(new Image(getClass().getResourceAsStream("/car.png")));
        return n;
    }

    private Node makeIcon() {
        Node n = new ImageView(new Image(getClass().getResourceAsStream("/factory.png")));
        return n;
    }

    private Node modelIcon() {
        Node n = new ImageView(new Image(getClass().getResourceAsStream("/model.png")));
        return n;
    }

    private Node yearIcon() {
        Node n = new ImageView(new Image(getClass().getResourceAsStream("/calendar.png")));
        return n;
    }

    private Node colorIcon() {
        Node n = new ImageView(new Image(getClass().getResourceAsStream("/palette.png")));
        return n;
    }

    private Node soldIcon() {
        Node n = new ImageView(new Image(getClass().getResourceAsStream("/price-tag.png")));
        return n;
    }

    private Node priceIcon() {
        Node n = new ImageView(new Image(getClass().getResourceAsStream("/money.png")));
        return n;
    }

    private final Node searchIcon = new ImageView(
            new Image(getClass().getResourceAsStream("/search.png"))
    );

    private int _lastSelection;
    private void populateView(String filter){
        try {
            String sName = "Search Results";
            Car[] list = Database.searchForItems(filter);
            if(list.length<=0)
                sName = "No search results for '"+ filter +"'";
            TreeItem<String> rootItem = new TreeItem<String> (sName, searchIcon);
            rootItem.setExpanded(true);
            int i = 0;
            for(Car c : list){
                TreeItem<String> top = new TreeItem<String> (String.format("ID (%d)", c.getID()), carIcon());
                rootItem.getChildren().add(top);
                TreeItem<String> t = rootItem.getChildren().get(i);
                t.getChildren().add(new TreeItem<String> ("Make: "+c.make, makeIcon()));
                t.getChildren().add(new TreeItem<String> ("Model: "+c.model, modelIcon()));
                t.getChildren().add(new TreeItem<String> (String.format("Year: %s", c.year), yearIcon()));
                t.getChildren().add(new TreeItem<String> ("Color: "+c.color, colorIcon()));
                i++;
            }
            TreeView<String> tree = new TreeView<String> (rootItem);
            tree.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    salesButton.setDisable(true);
                    deleteButton.setDisable(true);
                    TreeItem<String> selectedItem = (TreeItem<String>) newValue;
                    System.out.println("User selected: "+ selectedItem.getValue());
                    if(!AssistFunction.IsEmptyOrNull(selectedItem.getValue())) {
                        _lastSelection = 0;
                        Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(selectedItem.getValue());
                        while(m.find())
                            _lastSelection = Integer.parseInt(m.group(1));
                        if(Database.getAnItem(_lastSelection)!=null){
                            salesButton.setDisable(false);
                            deleteButton.setDisable(false);
                        }
                    }
                }
            });
            clearView();
            if(AssistFunction.IsEmptyOrNull(filter))
                searchBox.setText("*");
            selectionView.getChildren().add(tree);
            if(split.getDividers().get(0).getPosition()>0.5)
                split.getDividers().get(0).setPosition(0.50);
        } catch(Exception er) {
            return;
        }
    }

    private void refreshView(){
        if(!AssistFunction.IsEmptyOrNull(Database.getLastSearch()))
            populateView(Database.getLastSearch());
    }

    private Boolean removeFromView(int ID){
        try{
            deleteButton.setDisable(true);
            return Database.removeAnItem(ID);
        }
        catch(Exception ex){
            return false;
        }
    }

    private void clearView() {
        selectionView.getChildren().clear();
    }

    //Switches to the sales screen and searches for given car ID
    private void switchToSalesScreen(int cardID) {
        try{
            Screens screen = new Screens(frame.getScene());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Sales.fxml"));
            SalesController controller = new SalesController();
            controller.setToSearch(String.format("%s", cardID));
            loader.setController(controller);
            screen.addScreen("sales", (Pane)loader.load());
            screen.activate("sales");
        }
        catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

    //Switches to the sales screen
    private void switchToSalesScreen() {
        try{
            Screens screen = new Screens(frame.getScene());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Sales.fxml"));
            loader.setController(new SalesController());
            screen.addScreen("sales", (Pane)loader.load());
            screen.activate("sales");
        }
        catch (Exception ex) { return; }
    }
}
