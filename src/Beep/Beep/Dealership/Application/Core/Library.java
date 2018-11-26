package Beep.Beep.Dealership.Application.Core;

import javafx.application.Platform;

import java.util.Calendar;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.*;

public class Library {

    private static String getDirectoryName(){
        String name = "Beep.Beep.Dealership.Application";
        if(System.getProperty("os.name").startsWith("Windows")){
            return name;
        } else {
            return String.format(".%s", name);
        }
    }
    private static Path _logFile = Paths.get(System.getProperty("user.home").toString(), getDirectoryName(), "event.log");
    public static String getLogFile() {
        createLogFile();
        return _logFile.toString();
    }

    public static Boolean logFileExists(){
        return new File(_logFile.toAbsolutePath().toString()).exists();
    }

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

    public static void writeLog(String message){
        writeActualLog(message, LogType.INFO);
    }

    public static void writeLog(String message, LogType type){
        writeActualLog(message, type);
    }

    public static void writeLog(Exception ex){
        writeActualLog(String.format("(%s) %s", ex.hashCode(), ex.getMessage()), LogType.ERROR);
    }

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
