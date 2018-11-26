package Beep.Beep.Dealership.Application.UI;

import Beep.Beep.Dealership.Application.Core.*;
import Beep.Beep.Dealership.Application.Core.Entities.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.beans.value.*;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import javafx.scene.input.*;
import javafx.scene.paint.*;

import java.util.regex.*;
import javafx.event.*;

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
                if(newVal.doubleValue()<25)
                    clearSearch.setVisible(false);
                if(!AssistFunction.IsEmptyOrNull(searchBox.getText())&&(newVal.doubleValue()>20)){
                    clearSearch.setVisible(true);
                }
            });

            deleteButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent e) {
                    Node image = null;
                    String message = null;
                    if(removeFromView(_lastSelection)){
                        image = informationIcon();
                        message = "Successfully removed item with ID: "+_lastSelection;
                        Library.writeLog(message);
                    } else {
                        image = warningIcon();
                        message = "Failed to remove item with ID: "+_lastSelection;
                        Library.writeLog(message, LogType.WARN);
                    }
                    clearView();
                    selectionView.getChildren().add(new TreeView<String> (new TreeItem<String> (message, image)));
                    if(split.getDividers().get(0).getPosition()>0.5)
                        split.getDividers().get(0).setPosition(0.50);
                }
            });

            exitItem.setOnAction(event -> {
                Platform.exit();
            });

            salesItem.setOnAction(event -> {
                switchToSalesScreen();
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
                    salesButton.setDisable(true);
                    deleteButton.setDisable(true);
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
        }
        catch(Exception ex){
            Library.writeLog(ex);
        }
    }

    private String _toSearch;
    public void setToSearch(String toSearch){
        this._toSearch = toSearch;
    }

    //Clears the search box, results and disable buttons
    private void clearAll() {
        if(split.getDividers().get(0).getPosition()<0.5)
            split.getDividers().get(0).setPosition(1.0);
        salesButton.setDisable(true);
        deleteButton.setDisable(true);
        searchBox.setText("");
        clearView();
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
        Node n = new ImageView(new Image(getClass().getResourceAsStream("/images/calendar.png")));
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

    private Node errorIcon() {
        Node n = new ImageView(new Image(getClass().getResourceAsStream("/images/error.png")));
        return n;
    }

    private Node warningIcon() {
        Node n = new ImageView(new Image(getClass().getResourceAsStream("/images/warning.png")));
        return n;
    }

    private Node informationIcon() {
        Node n = new ImageView(new Image(getClass().getResourceAsStream("/images/info.png")));
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
                TreeItem<String> top = new TreeItem<String> (String.format("ID (%d)", c.getID()), carIcon());
                rootItem.getChildren().add(top);
                TreeItem<String> t = rootItem.getChildren().get(i);
                t.getChildren().add(new TreeItem<String> ("Make: "+c.make, makeIcon()));
                t.getChildren().add(new TreeItem<String> ("Model: "+c.model, modelIcon()));
                t.getChildren().add(new TreeItem<String> (String.format("Year: %s", c.year), yearIcon()));
                Label l = new Label();
                l.setText(String.format("Color: %s", c.color));
                l.setTextFill(Color.web("#ecf0f1"));
                try{
                    l.setTextFill(Color.web(c.color));
                }catch(Exception ex) {}
                HBox hbox = new HBox(5);
                hbox.getChildren().addAll(colorIcon(), l);
                t.getChildren().add(new TreeItem<String> ("", hbox));
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
            //selectionView.setStyle("/Design/TreeView.css");
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
            screen.centerWindow();
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
            screen.centerWindow();
        }
        catch (Exception ex) { return; }
    }
}
