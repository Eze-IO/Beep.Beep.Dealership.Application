package Beep.Beep.Dealership.Application.Core;

import javafx.application.Platform;

import java.util.Calendar;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.*;

//Class for logging errors & information
public class Library {

    //Create directory for log file
    private static String getDirectoryName(){
        String name = "Beep.Beep.Dealership.Application";
        if(System.getProperty("os.name").startsWith("Windows")){
            return name;
        } else {
            return String.format(".%s", name);
        }
    }

    //Path to log file
    private static Path _logFile = Paths.get(System.getProperty("user.home").toString(), getDirectoryName(), "events.log");
    public static String getLogFile() {
        createLogFile();
        return _logFile.toString();
    }

    public static Boolean logFileExists(){
        return new File(_logFile.toAbsolutePath().toString()).exists();
    }

    //Create a log file if it doesn't exist on the home directory of the user
    private static void createLogFile(){
        try {
            File f = new File(_logFile.toAbsolutePath().toString());
            if(!Files.isDirectory(Paths.get(f.getParent()))) {
                Files.createDirectory(Paths.get(f.getParent()));
                Files.setAttribute(Paths.get(f.getParent()), "dos:hidden", true);
            }
            if(!f.exists()){
                try {
                    Files.createFile(f.toPath());
                } catch(Exception er){
                    return;
                }
            }
        } catch (Exception er){
            return;
        }
    }

    //Deletes log file created by 'createLogFile()' method
    private static void deleteLogFile(){
        try{
            File f = new File(_logFile.toAbsolutePath().toString());
            if(f.exists()){
                if(f.delete()){
                    Files.deleteIfExists(Paths.get(f.getParent()));
                }
            }
        } catch(Exception ex) {
            return;
        }
    }

    //Write log to output and file
    public static void writeLog(String message){
        writeActualLog(message, LogType.INFO);
    }

    //Write log to output and file
    public static void writeLog(String message, LogType type){
        writeActualLog(message, type);
    }

    //Write log to output and file for exceptions
    public static void writeLog(Exception ex){
        String message = ex.getMessage();
        if(AssistFunction.IsEmptyOrNull(message))
            message = "Unknown error!";
        writeActualLog(String.format("(%s) %s", ex.hashCode(), message), LogType.ERROR);
    }

    //Write log to output and file for exceptions
    public static void writeLog(Exception ex, LogType type){
        writeActualLog(String.format("(%s) %s", ex.hashCode(), ex.getMessage()), type);
    }

    private static void writeActualLog(String message, LogType type) {
        try{
            createLogFile();
            String log = String.format("%s [%s]: %s", Calendar.getInstance().getTime(), getNameType(type), message);
            System.out.println(log);
            try {
                FileWriter fw = new FileWriter(_logFile.toString(),true);
                if(new File(_logFile.toString()).length()>0)
                    fw.append(System.getProperty("line.separator")+log);
                else
                    fw.append(log);
                fw.close();
            } catch(Exception er){
                return;
            }
        }
        catch(Exception ex) {
            deleteLogFile();
            return;
        }
    }

    private static String getNameType(LogType type){
        String result = null;
        switch(type){
            case CRITICAL_ERROR:
                result = ("Critical Error");
                break;
            case ERROR:
                result = ("Error");
                break;
            case WARN:
                result = ("Warning");
                break;
           default:
               result = ("Information");
               break;
        }
        return result;
    }
}
