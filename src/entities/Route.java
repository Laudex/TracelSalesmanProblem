package entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Route {
    private int id;
    private Car car;
    private ArrayList<Customer> listOfCustomers;
    private Map<Customer, Double> customersAndStartServiceTime = new LinkedHashMap<>();
    private double startDepot;
    private double finishDepot;

    public Route(int id, Car car, double startDepot) {
        this.id = id;
        this.car = car;
        this.startDepot = startDepot;
    }

    public ArrayList<Customer> getListOfCustomers() {
        return listOfCustomers;
    }

    public void setListOfCustomers(ArrayList<Customer> listOfCustomers) {
        this.listOfCustomers = listOfCustomers;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public Map<Customer, Double> getCustomersAndStartServiceTime() {
        return customersAndStartServiceTime;
    }

    public void setCustomersAndStartServiceTime(Map<Customer, Double> customersAndStartServiceTime) {
        this.customersAndStartServiceTime = customersAndStartServiceTime;
    }

    public double getStartDepot() {
        return startDepot;
    }

    public void setStartDepot(double startDepot) {
        this.startDepot = startDepot;
    }

    public double getFinishDepot() {
        return finishDepot;
    }

    public void setFinishDepot(double finishDepot) {
        this.finishDepot = finishDepot;
    }
}
