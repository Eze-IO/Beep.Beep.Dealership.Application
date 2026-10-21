package Beep.Beep.Dealership.Application.Core.Entities;

import Beep.Beep.Dealership.Application.Core.AssistFunction;
import Beep.Beep.Dealership.Application.Core.Information;
import Beep.Beep.Dealership.Application.Core.Library;
import Beep.Beep.Dealership.Application.Core.LogType;
import lombok.Data;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/*
    Links for making api calls to the AWS Lambda functions.
    Each link is looked up in this order (first one found wins):
      1. Environment variable, e.g. BEEP_GET_LIST_URL
      2. Personal config file: <home>/.Beep.Beep.Dealership.Application/config.properties
      3. Bundled configurations.properties
 */
@Data
public class API {
    public static final String CONFIG_FILE_NAME = "config.properties";

    public API() {
        Properties prop = new Properties();

        // bundled defaults
        try (InputStream input = Information.class.getClassLoader().getResourceAsStream("configurations.properties")) {
            if (input != null)
                prop.load(input);
        } catch (IOException ex) {
            Library.writeLog("Failed to load bundled configurations.properties: "+ex, LogType.WARN);
        }

        // personal config file overrides bundled values
        Path userConfig = getUserConfigFile();
        if (Files.isRegularFile(userConfig)) {
            try (InputStream input = new FileInputStream(userConfig.toFile())) {
                prop.load(input);
            } catch (IOException ex) {
                Library.writeLog("Failed to load "+userConfig+": "+ex, LogType.ERROR);
            }
        }

        //set API url links
        this.GetList = lookup(prop, "getListUrl", "BEEP_GET_LIST_URL");
        this.GetItem = lookup(prop, "getItemUrl", "BEEP_GET_ITEM_URL");
        this.PutItem = lookup(prop, "putItemUrl", "BEEP_PUT_ITEM_URL");
        this.DeleteItem = lookup(prop, "deleteItemUrl", "BEEP_DELETE_ITEM_URL");
    }

    //Location of the personal config file that holds the API links
    public static Path getUserConfigFile() {
        return Library.getAppDirectory().resolve(CONFIG_FILE_NAME);
    }

    //Returns the environment variable if set, otherwise the property value
    private static String lookup(Properties prop, String key, String envName) {
        String value = System.getenv(envName);
        if (AssistFunction.IsEmptyOrNull(value))
            value = prop.getProperty(key);
        if (AssistFunction.IsEmptyOrNull(value)) {
            Library.writeLog(String.format("API link '%s' is not set. Set %s or add it to %s",
                    key, envName, getUserConfigFile()), LogType.CRITICAL_ERROR);
            return null;
        }
        return value.trim();
    }

    public String GetList;
    public String GetItem;
    public String DeleteItem;
    public String PutItem;
}
