package Beep.Beep.Dealership.Application.Core.Entities;

import Beep.Beep.Dealership.Application.Core.AssistFunction;
import com.google.gson.*;
import org.json.*;

public class Car extends Vehicle {
    private int _ID = 0;
    public Car(int ID) { this._ID = ID; }
    public int getID(){
        return _ID;
    }
    @Override
    public boolean equals(Object obj) {
        try{
            if(AssistFunction.IsEmptyOrNull(name))
                return false;
            Car c = (Car)obj;
            return (_ID==c.getID());
        }
        catch(Exception ex){
            return false;
        }
    }
    @Override
    public String toString() {
        return ("["+name+"]");
    }
}
