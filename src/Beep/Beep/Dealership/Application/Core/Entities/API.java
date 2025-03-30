package Beep.Beep.Dealership.Application.Core.Entities;

import Beep.Beep.Dealership.Application.Core.Information;
import Beep.Beep.Dealership.Application.Core.Library;
import Beep.Beep.Dealership.Application.Core.LogType;
import javafx.application.Platform;
import lombok.Data;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/* Links for making api calls to the AWS Lambda functions */
@Data
public class API {
    public API() {
        try (InputStream input = Information.class.getClassLoader().getResourceAsStream("configurations.properties")) {

            Properties prop = new Properties();

            // load a properties file
            prop.load(input);

            //set API url inks
            this.GetList = prop.getProperty("getListUrl").trim();
            this.GetItem = prop.getProperty("getItemUrl").trim();
            this.PutItem = prop.getProperty("putItemUrl").trim();
            this.DeleteItem = prop.getProperty("deleteItemUrl").trim();

        } catch (IOException ex) {
            Library.writeLog("Failed to load API url(s): "+ex, LogType.CRITICAL_ERROR);
            Platform.exit(); //Close on error
        }
    }
    public String GetList;
    public String GetItem;
    public String DeleteItem;
    public String PutItem;
}
