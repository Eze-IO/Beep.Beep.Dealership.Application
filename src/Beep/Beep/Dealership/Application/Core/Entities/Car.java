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
            Car c = (Car)obj;
            return (_ID==c.getID());
        }
        catch(Exception ex){
            return false;
        }
    }

    public boolean updateObj(Vehicle vehicle){
        try{
            this.name = vehicle.name;
            this.make = vehicle.make;
            this.model = vehicle.model;
            this.color = vehicle.color;
            this.price = vehicle.price;
            this.year = vehicle.year;
            this.sold = vehicle.sold;
            return true;
        }
        catch(Exception ex){
            return false;
        }
    }

    //Make method the accessible for Car class
    @Override
    public Vehicle toVehicle() {
        return super.toVehicle();
    }

    @Override
    public String toString() {
        try {
            JSONObject jo = new JSONObject(this);
            return jo.toString();
        } catch(Exception ex) { return super.toString();}
    }
}
