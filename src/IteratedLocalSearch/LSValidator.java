package IteratedLocalSearch;

import entities.Car;
import entities.Customer;

import java.util.ArrayList;

public class LSValidator {

    private static int capacity;

    public static void setCapacity(int capacity) {
        LSValidator.capacity = capacity;
    }

    public static boolean validate(ArrayList<Customer> firstCustomers, ArrayList<Customer> secondCustomers, Customer depot, Car firstCar, Car secondCar){
        double finishTimeDepot = depot.getFinishTime();
        double distanceFirst = validateDistance(firstCustomers, depot);
        double distanceSecond = validateDistance(secondCustomers, depot);
        if (distanceFirst == -1 || distanceSecond == -1){
            return false;
        }
        if (!(validateCapacity(firstCustomers, firstCar) && validateCapacity(secondCustomers, secondCar))){
            System.out.println("Not enough capacity!");
            return false;
        }
        return true;

    }

    public static double validateDistance(ArrayList<Customer> customers, Customer depot){
        double distance = 0;
        int size = customers.size();
        try {
            if (size > 0) {
                Customer first = customers.get(0);
                distance = countDistanceAndServiceTime(depot, first);
            }
            for (int i = 0; i < size; i++) {
                if (i + 1 < size) {
                    Customer first = customers.get(i);
                    Customer second = customers.get(i + 1);
                    distance = distance + countDistanceAndServiceTime(first, second);

                }
            }
            Customer last = customers.get(size - 1);
            distance = distance + countDistanceAndServiceTime(last, depot);
            return distance;
        } catch (NotValidRouteException e){
            System.out.println("This move is not valid because of time window");
            return -1;
        }

    }

    public static double countDistanceAndServiceTime(Customer first, Customer second) throws NotValidRouteException {
        double distance = first.getMappedDistances().get(second.getCustomerId());
        if (distance < second.getStartTime()){
            distance = second.getStartTime() + second.getServiceTime();
        } else if (distance >= second.getStartTime() && distance <= second.getFinishTime()){
            distance = distance + second.getServiceTime();
        } else if (distance >= second.getFinishTime()){
            System.out.println("error, not valid");
            throw new NotValidRouteException("This route is not valid!");

        }
        return distance;
    }

    public static boolean validateCapacity(ArrayList<Customer> customers, Car car){
        int demand = 0;
        for (Customer customer : customers){
            demand = demand + customer.getDemand();
        }
        if (capacity < demand){
            return false;
        }
        return true;
    }
}
