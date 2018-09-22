package entities;

public class Car {
    private int capacity;
    private double distance;
    private Customer currentState;

    public Car(int capacity, double distance) {
        this.capacity = capacity;
        this.distance = distance;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public Customer getCurrentState() {
        return currentState;
    }

    public void setCurrentState(Customer currentState) {
        this.currentState = currentState;
    }
}
