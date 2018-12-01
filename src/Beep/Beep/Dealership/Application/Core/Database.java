package Beep.Beep.Dealership.Application.Core;

import Beep.Beep.Dealership.Application.Core.Entities.Car;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import Beep.Beep.Dealership.Application.Core.Entities.Vehicle;
import com.google.gson.*;
import org.json.*;

/*
    Class for interacting with the No-SQL AWS DynamoDB database.
 */
public class Database {
    /*--------------------------------------------------------------------------------------------------------------------------------------*/
    //Links for making api calls to the AWS Lambda functions
    private static final String _getListUrl = "https://vo4yd50tli.execute-api.us-east-1.amazonaws.com/prod/BeepBeepInventoryGetList";
    private static final String _getItemUrl = "https://unyka7kh40.execute-api.us-east-1.amazonaws.com/prod/BeepBeepInventoryGetItem";
    private static final String _putItemUrl = "https://inout6gox5.execute-api.us-east-1.amazonaws.com/prod/BeepBeepInventoryPutItem";
    private static final String _deleteItemUrl = "https://342cdgz7r4.execute-api.us-east-1.amazonaws.com/prod/BeepBeepInventoryDeleteItem";
    /*--------------------------------------------------------------------------------------------------------------------------------------*/

    /*
        Get all 'car' items in the database
        returns an array of 'Car' objects
        if successful & if not it returns null.
     */
    public static Car[] getAllItems(){
        HttpURLConnection conn = null;
        try {
            //Check if there is internet
            if(!AssistFunction.IsInternetAvailable()){
                Library.writeLog("No internet connection available!", LogType.WARN);
                return null;
            }
            conn = (HttpURLConnection)new URL(_getListUrl).openConnection(); // Open link to get response
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            if(conn.getResponseCode() != 200) {
                //A response code of 500 or any other means it failed to get the item from api link
                Library.writeLog("Failed to get all item!", LogType.WARN);
            } else {
                /*
                    Get response from link, convert it the JSON
                    response to an array of 'Car' objects then
                    of course return it
                 */
                StringBuilder result = new StringBuilder();
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                result.append(br.readLine());
                JSONArray ja = new JSONArray(result.toString());
                Car[] list = new Car[ja.length()];
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject obj = ja.getJSONObject(i);
                    if(!AssistFunction.IsEmptyOrNull(obj.getString("ID"))){
                        Car tmp = new Car(Integer.parseInt(obj.getString("ID")));
                        if(!AssistFunction.IsEmptyOrNull(obj.getString("Name")))
                            tmp.name = obj.getString("Name");
                        if(!AssistFunction.IsEmptyOrNull(obj.getString("Make")))
                            tmp.make = obj.getString("Make");
                        if(!AssistFunction.IsEmptyOrNull(obj.getString("Model")))
                            tmp.model = obj.getString("Model");
                        if(!AssistFunction.IsEmptyOrNull(obj.getString("Color")))
                            tmp.color = obj.getString("Color");
                        if(!AssistFunction.IsEmptyOrNull(obj.getString("Year")))
                            tmp.year = Integer.parseInt(obj.getString("Year"));
                        if(!AssistFunction.IsEmptyOrNull(obj.getString("Price")))
                            tmp.price = Double.parseDouble(obj.getString("Price"));
                        if(!AssistFunction.IsEmptyOrNull(obj.getString("Sold")))
                            tmp.sold = Boolean.parseBoolean(obj.getString("Sold"));
                        list[i] = tmp;
                    } else {
                        list = new Car[list.length-1];
                    }
                }
                return list;
            }
            return null;
        }
        catch(Exception ex) {
            Library.writeLog(ex);
            return null;
        }
        finally {
            if(conn != null)
                conn.disconnect();
        }
    }

    private static String _lastSearch = null;
    /*
        Get last search from calling the
        'searchForItems(String search)'
        method. Can return as an empty string.
     */
    public static String getLastSearch() {
        if(AssistFunction.IsEmptyOrNull(_lastSearch))
            return ("");
        return _lastSearch;
    }

    private static final String _all = "*";
    /*
        Search for all 'car' items in the database
        that contains the search words and returns
        an array of 'Car' objects matching the search
        if successful & if not it returns null.
     */
    public static Car[] searchForItems(String search){
        try {
            _lastSearch = search;
            search = search.toLowerCase();
            if((search.trim().equals(_all.toLowerCase())) || AssistFunction.IsEmptyOrNull(search)){
                _lastSearch = _all;
                return Database.getAllItems();
            }
            /*
                Get list of all items or 'cars' in the database
                then format every attribute about the car in a string
                to see if the search is within the string, if it is then
                add it to a the list object and return the list in a 'car' array object.
            */
            List<Car> co = new ArrayList<Car>();
            Car[] list = Database.getAllItems();
            for(int x=0;x<list.length;x++){
                Car c = list[x];
                String soldStatus = "unsold";
                if(c.sold)
                    soldStatus = "sold";
                String sPrice = ("$"+Double.toString(c.price));
                StringBuilder sb = new StringBuilder
                        (String.format("ID(%s) Name(%s) Make(%s) Model(%s) Color(%s) Year(%s) Price(%s) Price(%s) %s, Status(%s)",
                                c.getID(), c.name, c.make, c.model, c.color, c.year, sPrice, c.price, soldStatus, soldStatus));
                sb = new StringBuilder(sb.toString().toLowerCase());
                for(String s : search.split(" ")){
                    if(sb.indexOf(s)>-1)
                        co.add(c);
                }
            }
            Car[] result = new Car[co.toArray().length];
            for(int x=0;x<result.length;x++){
                result[x] = (Car)co.toArray()[x];
            }
            return result;
        }
        catch (Exception ex) {
            Library.writeLog(ex);
            return null;
        }
    }

    /*
        Gets a 'car' item in the database by ID
        and returns true if the item exists.
     */
    public static Car getAnItem(int ID){
        HttpURLConnection conn = null;
        try{
            //Check if there is internet
            if(!AssistFunction.IsInternetAvailable()){
                Library.writeLog("No internet connection available!", LogType.WARN);
                return null;
            }
            conn = (HttpURLConnection)new URL(_getItemUrl).openConnection(); // Open link to get response
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept", "application/json");

            //Send a request to the server of an id, in JSON format
            JsonObject requestID = new JsonObject();
            requestID.addProperty("ID", Integer.toString(ID));
            OutputStream os = conn.getOutputStream();
            os.write(requestID.toString().getBytes());
            os.flush();

            if(conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                //A response code of 500 or any other means it failed to get the item from api link
                Library.writeLog("Failed to get item!", LogType.WARN);
            } else {
                /*
                    Get response from server and convert it to a 'car' object
                    and return the object. return null if error occurs
                 */
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder(br.readLine());
                JSONObject obj = new JSONObject(result.toString());
                if(!AssistFunction.IsEmptyOrNull(obj.getString("ID"))) {
                    Car tmp = new Car(Integer.parseInt(obj.getString("ID")));
                    if (!AssistFunction.IsEmptyOrNull(obj.getString("Name")))
                        tmp.name = obj.getString("Name");
                    if (!AssistFunction.IsEmptyOrNull(obj.getString("Make")))
                        tmp.make = obj.getString("Make");
                    if (!AssistFunction.IsEmptyOrNull(obj.getString("Model")))
                        tmp.model = obj.getString("Model");
                    if (!AssistFunction.IsEmptyOrNull(obj.getString("Color")))
                        tmp.color = obj.getString("Color");
                    if (!AssistFunction.IsEmptyOrNull(obj.getString("Year")))
                        tmp.year = Integer.parseInt(obj.getString("Year"));
                    if (!AssistFunction.IsEmptyOrNull(obj.getString("Price")))
                        tmp.price = Double.parseDouble(obj.getString("Price"));
                    if(!AssistFunction.IsEmptyOrNull(obj.getString("Sold")))
                        tmp.sold = Boolean.parseBoolean(obj.getString("Sold"));
                    return tmp;
                }
            }
            return null;
        }
        catch (Exception ex) {
            Library.writeLog(ex);
            return null;
        }
        finally {
            if(conn != null)
                conn.disconnect();
        }
    }

    /*
        Saves a 'car' item in the database
        and returns true if the item was saved.
     */
    public static Boolean saveAnItem(Vehicle machine){
        HttpURLConnection conn = null;
        try{
            //Check if there is internet
            if(!AssistFunction.IsInternetAvailable()){
                Library.writeLog("No internet connection available!", LogType.WARN);
                return false;
            }
            conn = (HttpURLConnection)new URL(_putItemUrl).openConnection(); // Open link to get response
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept", "application/json");

            /*
                Send car object to server as request in the JSON format
                the response will retrun true, if it worked!
             */
            JsonObject requestID = new JsonObject();
            requestID.addProperty("Name", machine.name);
            requestID.addProperty("Make", machine.make);
            requestID.addProperty("Model", machine.model);
            requestID.addProperty("Color", machine.color);
            requestID.addProperty("Year", Integer.toString(machine.year));
            requestID.addProperty("Price", Double.toString(machine.price));
            requestID.addProperty("Sold", Boolean.toString(machine.sold));
            OutputStream os = conn.getOutputStream();
            os.write(requestID.toString().getBytes());
            os.flush();

            if(conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                //A response code of 500 or any other means it failed to put the item from api link
                Library.writeLog("Failed to save item!", LogType.WARN);
            } else {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                return Boolean.parseBoolean(br.readLine().trim());
            }
            return false;
        }
        catch (Exception ex) {
            Library.writeLog(ex);
            return false;
        }
        finally {
            if(conn != null)
                conn.disconnect();
        }
    }

    /*
        Updates an existing 'car' item
        in the database and returns true
        if the item was updated.
     */
    public static Boolean updateAnItem(Car car){
        try{
            //Check if 'car' actually exists
            Car c = getAnItem(car.getID());
            if(car.equals(c)){ //if the car exists we save the new 'car' object and remove the old one
                if(saveAnItem(car.toVehicle()))
                    return removeAnItem(car.getID());
            }
            return false;
        }
        catch(Exception ex){
            Library.writeLog("Failed to update item!", LogType.WARN);
            Library.writeLog(ex);
            return false;
        }
    }

    /*
        Removes a 'car' item in the database by ID
        and returns true if the item doesn't exist.
     */
    public static Boolean removeAnItem(int ID){
        HttpURLConnection conn = null;
        try{
            if(!AssistFunction.IsInternetAvailable()){
                Library.writeLog("No internet connection available!", LogType.WARN);
                return false;
            }
            conn = (HttpURLConnection)new URL(_deleteItemUrl).openConnection(); //Open link to get response
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept", "application/json");

            /*
                Send id of car to be deleted in JSON format to server
                server returns a response of true if the car doesn't exists
             */
            JsonObject requestID = new JsonObject();
            requestID.addProperty("ID", Integer.toString(ID));
            OutputStream os = conn.getOutputStream();
            os.write(requestID.toString().getBytes());
            os.flush();

            if(conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                //A response code of 500 or any other means it failed to remove the item from api link
                Library.writeLog("Failed to remove item!", LogType.WARN);
            } else {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                return Boolean.parseBoolean(br.readLine().trim());
            }
            return false;
        }
        catch (Exception ex) {
            Library.writeLog(ex);
            return false;
        }
        finally {
            if(conn != null)
                conn.disconnect();
        }
    }
}
