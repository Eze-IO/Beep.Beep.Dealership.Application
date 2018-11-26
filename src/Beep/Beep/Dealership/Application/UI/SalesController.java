package Beep.Beep.Dealership.Application.UI;

import Beep.Beep.Dealership.Application.Core.AssistFunction;
import Beep.Beep.Dealership.Application.Core.Database;
import Beep.Beep.Dealership.Application.Core.Entities.Car;
import Beep.Beep.Dealership.Application.Core.Information;
import Beep.Beep.Dealership.Application.Core.Library;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.Parent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SalesController {


    @FXML
    private BorderPane frame;
    @FXML
    private StackPane selectionView;
    @FXML
    private TextField searchBox;
    @FXML
    private Button searchButton;
    @FXML
    private Button clearSearch;
    @FXML
    private Button sellButton;
    @FXML
    private Button inventoryButton;
    @FXML
    private MenuItem exitItem;
    @FXML
    private MenuItem inventoryItem;
    @FXML
    private MenuItem aboutItem;
    @FXML
    private MenuItem logoutItem;
    @FXML
    private SplitPane split;
    @FXML
    private AnchorPane modelRightSide;
    @FXML
    private Menu helpMenu;
    @FXML
    private MenuItem logItem;


    public void initialize() {

        try{
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

            sellButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent e) {
                    try {
                        Car c = Database.getAnItem(_lastSelection);
                        Screens s = new Screens(frame.getScene());
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Sell.fxml"));
                        SellController controller = new SellController();
                        controller.setItemID(_lastSelection);
                        fxmlLoader.setController(controller);
                        s.showWindow((Parent)fxmlLoader.load());
                        if(c!=null)
                            if(c.sold)
                                populateView(c.name);
                        else
                            refreshView();
                    }
                    catch (Exception ex) {
                        Library.writeLog(ex);
                        return;
                    }
                }
            });

            searchBox.widthProperty().addListener((obs, oldVal, newVal) -> {
                if(newVal.doubleValue() < 25)
                    clearSearch.setVisible(false);
                if(!AssistFunction.IsEmptyOrNull(searchBox.getText())&&(newVal.doubleValue()>20)){
                    clearSearch.setVisible(true);
                }
            });

            inventoryButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent e) {
                    switchToInventoryScreen(_lastSelection);
                }
            });

            exitItem.setOnAction(event -> {
                Platform.exit();
            });

            inventoryItem.setOnAction(event -> {
                switchToInventoryScreen();
            });

            aboutItem.setOnAction(event -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, null, ButtonType.OK);
                alert.setTitle("About");
                alert.setHeaderText(Information.getTitle());
                alert.setContentText(Information.getCompanyInfo());
                DialogPane dialogPane = alert.getDialogPane();
                dialogPane.getStylesheets().add("css/Dialog.css");
                alert.showAndWait();
            });

            logoutItem.setOnAction(event -> {
                try{
                    Screens screen = new Screens(frame.getScene());
                    screen.addScreen("login", FXMLLoader.load(getClass().getResource("Login.fxml")));
                    screen.activate("login");
                    Library.writeLog("User logged out!");
                }
                catch(Exception er) {
                    Library.writeLog(er);
                    return;
                }
            });

            modelRightSide.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    _lastSelection = 0;
                    inventoryButton.setDisable(true);
                    sellButton.setDisable(true);
                    refreshView();
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

            helpMenu.setOnShowing(event -> {
                logItem.setDisable(true);
                if(Library.logFileExists())
                    logItem.setDisable(false);
            });

            logItem.setOnAction(event -> {
                AssistFunction.OpenFile(Library.getLogFile());
            });

            if(!AssistFunction.IsEmptyOrNull(this._toSearch)){
                searchBox.setText(this._toSearch);
                populateView(this._toSearch);
            }

        }catch(Exception ex){
            Library.writeLog(ex);
        }
    }

    //Clears the search box, results and disable buttons
    private void clearAll() {
        if(split.getDividers().get(0).getPosition()<0.5)
            split.getDividers().get(0).setPosition(1.0);
        inventoryButton.setDisable(true);
        sellButton.setDisable(true);
        searchBox.setText("");
        clearView();
    }

    private String _toSearch;
    public void setToSearch(String toSearch){
        this._toSearch = toSearch;
    }

    private Node carIcon() {
        Node n = new ImageView(new Image(getClass().getResourceAsStream("/images/car.png")));
        return n;
    }

    private Node makeIcon() {
        Node n = new ImageView(new Image(getClass().getResourceAsStream("/images/factory.png")));
        return n;
    }

    private Node modelIcon() {
        Node n = new ImageView(new Image(getClass().getResourceAsStream("/images/model.png")));
        return n;
    }

    private Node yearIcon() {
        Node n = new ImageView(new Image(getClass().getResourceAsStream("/images/balloons.png")));
        return n;
    }

    private Node colorIcon() {
        Node n = new ImageView(new Image(getClass().getResourceAsStream("/images/palette.png")));
        return n;
    }

    private Node soldIcon() {
        Node n = new ImageView(new Image(getClass().getResourceAsStream("/images/price-tag.png")));
        return n;
    }

    private Node priceIcon() {
        Node n = new ImageView(new Image(getClass().getResourceAsStream("/images/money.png")));
        return n;
    }

    private Node nameIcon() {
        Node n = new ImageView(new Image(getClass().getResourceAsStream("/images/name.png")));
        return n;
    }

    private Node errorIcon() {
        Node n = new ImageView(new Image(getClass().getResourceAsStream("/images/error.png")));
        return n;
    }

    private final Node searchIcon = new ImageView(
            new Image(getClass().getResourceAsStream("/images/search.png"))
    );

    private int _lastSelection;
    private void populateView(String filter){
        try {
            String sName = "Search Results";
            Car[] list = Database.searchForItems(filter);
            if(list==null){
                clearView();
                selectionView.getChildren().add(new TreeView<String> (new TreeItem<String> ("Error failed to get results", errorIcon())));
                if(split.getDividers().get(0).getPosition()>0.5)
                    split.getDividers().get(0).setPosition(0.50);
                return;
            }
            if(list.length<=0)
                sName = "No search results for '"+ filter +"'";
            TreeItem<String> rootItem = new TreeItem<String> (sName, searchIcon);
            rootItem.setExpanded(true);
            int i = 0;
            for(Car c : list){
                rootItem.getChildren().add(new TreeItem<String> (String.format("ID (%d)", c.getID()), carIcon()));
                TreeItem<String> t = rootItem.getChildren().get(i);
                if(!AssistFunction.IsEmptyOrNull(c.name) && c.sold)
                    t.getChildren().add(new TreeItem<String> (String.format("Customer name: %s", c.name), nameIcon()));
                if(c.sold)
                    t.getChildren().add(new TreeItem<String> ("Status: sold", soldIcon()));
                else
                    t.getChildren().add(new TreeItem<String> ("Status: unsold", soldIcon()));
                t.getChildren().add(new TreeItem<String> (String.format("$%s", c.price), priceIcon()));
                i++;
            }
            TreeView<String> tree = new TreeView<String> (rootItem);
            tree.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    inventoryButton.setDisable(true);
                    sellButton.setDisable(true);
                    TreeItem<String> selectedItem = (TreeItem<String>) newValue;
                    System.out.println("User selected: "+ selectedItem.getValue());
                    if(!AssistFunction.IsEmptyOrNull(selectedItem.getValue())) {
                        _lastSelection = 0;
                        Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(selectedItem.getValue());
                        while(m.find())
                            _lastSelection = Integer.parseInt(m.group(1));
                        Car r = Database.getAnItem(_lastSelection);
                        if(r!=null){
                            inventoryButton.setDisable(false);
                            sellButton.setDisable(r.sold);
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

    private void clearView() {
        selectionView.getChildren().clear();
    }

    //Switches to the inventory screen and searches for given car ID
    private void switchToInventoryScreen(int cardID) {
        try{
            Screens screen = new Screens(frame.getScene());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Inventory.fxml"));
            InventoryController controller = new InventoryController();
            controller.setToSearch(String.format("%s", cardID));
            loader.setController(controller);
            screen.addScreen("inventory", (Pane)loader.load());
            screen.activate("inventory");
            screen.centerWindow();
        }
        catch (Exception ex) { return; }
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
        catch (Exception ex) { return; }
    }
}
