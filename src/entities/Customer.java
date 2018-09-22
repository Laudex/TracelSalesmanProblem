package entities;

import java.util.Map;

public class Customer {

    private int customerId;
    private double xCoord;
    private double yCoord;
    private int demand;
    private double startTime;
    private double finishTime;
    private double serviceTime;
    private boolean isServed;

    private Map<Integer, Double> mappedDistances;

    public Customer(int customerId, double xCoord, double yCoord, int demand, double startTime, double finishTime, double serviceTime, boolean isServed) {
        this.customerId = customerId;
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.demand = demand;
        this.startTime = startTime;
        this.finishTime = finishTime;
        this.serviceTime = serviceTime;
        this.isServed = isServed;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerId=" + customerId +
                ", xCoord=" + xCoord +
                ", yCoord=" + yCoord +
                ", demand=" + demand +
                ", startTime=" + startTime +
                ", finishTime=" + finishTime +
                ", serviceTime=" + serviceTime +
                ", isServed=" + isServed +
                '}';
    }

    public boolean isServed() {
        return isServed;
    }

    public void setServed(boolean served) {
        isServed = served;
    }

    public double getDistanceToCustomer(Customer customer){
        double x = Math.abs(getxCoord() - customer.getxCoord());
        double y = Math.abs(getyCoord() - customer.getyCoord());
        return Math.sqrt(Math.pow(x,2) + Math.pow(y,2));
    }

    public Map<Integer, Double> getMappedDistances() {
        return mappedDistances;
    }

    public void setMappedDistances(Map<Integer, Double> mappedDistances) {
        this.mappedDistances = mappedDistances;
    }

    public int getCustomerId() {
        return customerId;
    }

    public double getxCoord() {
        return xCoord;
    }

    public double getyCoord() {
        return yCoord;
    }

    public int getDemand() {
        return demand;
    }

    public double getStartTime() {
        return startTime;
    }

    public double getFinishTime() {
        return finishTime;
    }

    public double getServiceTime() {
        return serviceTime;
    }
}
