package Beep.Beep.Dealership.Application.Core;

import lombok.Data;
import Beep.Beep.Dealership.Application.Core.Entities.API;
import java.io.IOException;
import java.util.Properties;
import java.io.*;


//A class for getting information from the configuration file
@Data
public class Information  {

    public Information() {
        try (InputStream input = Information.class.getClassLoader().getResourceAsStream("configurations.properties")) {

            Properties prop = new Properties();

            // load a properties file
            prop.load(input);

            // get the property value and set it
            this.title =  prop.getProperty("title");
            this.creators =  prop.getProperty("creators"); //"Created by Eze Ahuna & Muhammed Yusuf Yilmaz."

            //set API url inks

        } catch (IOException ex) {
            this.title = this.creators = String.valueOf("");
        }
    }

    //title of application
    private String title;
    //Names of contributors
    private String creators;
}
