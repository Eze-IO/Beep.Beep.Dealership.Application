package Beep.Beep.Dealership.Application.Core;

import java.io.*;
import java.awt.Desktop;
import java.net.*;
/*
    This class consists of functions
    that are useful or helpful
    throughout the program.
 */
public class AssistFunction {
    //Check if string is empty or null, returns true if null or empty!
    public static Boolean IsEmptyOrNull(String str){
        if(str == null || str.isEmpty())
            return true;
        return false;
    }

    //Check if there is internet is available, returns true if it is!
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

    //Opens a file, returns false on error
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
