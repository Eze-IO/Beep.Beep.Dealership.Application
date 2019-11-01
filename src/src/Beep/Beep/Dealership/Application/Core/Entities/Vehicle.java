package Beep.Beep.Dealership.Application.Core.Entities;

public class Vehicle {
    public String make;
    public String model;
    public String color;
    public double price;
    public boolean sold;
    public String name;
    public int year;

    //Get base class for inherited objects
    protected Vehicle toVehicle() {
        Vehicle v = new Vehicle();
        v.name = this.name;
        v.make = this.make;
        v.model = this.model;
        v.color = this.color;
        v.year = this.year;
        v.price = this.price;
        v.sold = this.sold;
        return v;
    }
}
