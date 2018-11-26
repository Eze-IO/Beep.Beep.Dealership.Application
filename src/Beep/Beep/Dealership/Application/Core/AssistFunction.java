package Beep.Beep.Dealership.Application.Core;

import java.io.*;
import java.awt.Desktop;
import java.net.*;

public class AssistFunction {
    public static Boolean IsEmptyOrNull(String str){
        if(str == null || str.isEmpty())
            return true;
        return false;
    }

    public static Boolean IsInternetAvailable() {
        try {
            URL url = new URL("https://www.google.com/");
            URLConnection connection = url.openConnection();
            connection.connect();
            return true;
        }
        catch(Exception ex) {
            return false;
        }
    }

    public static Boolean OpenFile(String pathToFile){
        try {
            Desktop d = Desktop.getDesktop();
            d.open(new File(pathToFile));
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
