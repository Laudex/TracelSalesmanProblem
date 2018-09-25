package services;

import entities.Customer;
import entities.Route;

import java.util.ArrayList;

public class RouteServices {

    public static double totalDistance(ArrayList<Route> routes, Customer depot) {
        double distance = 0;
        for (Route route : routes) {
            ArrayList<Customer> customers = route.getListOfCustomers();
            distance = distance + countDistance(customers, depot);
        }
        return distance;
    }

    public static double countDistance(ArrayList<Customer> customers, Customer depot) {
        if (customers.size() == 0){
            return 0;
        }
        double distance = 0;
        int size = customers.size();
        if (size > 0) {
            Customer first = customers.get(0);
            distance = countDistanceAndServiceTime(depot, first, distance);
        }
        for (int i = 0; i < size; i++) {
            if (i + 1 < size) {
                Customer first = customers.get(i);
                Customer second = customers.get(i + 1);
                distance = countDistanceAndServiceTime(first, second, distance);

            }
        }
        Customer last = customers.get(size - 1);
        distance = countDistanceAndServiceTime(last, depot, distance);
        return distance;
    }

    public static double countDistanceAndServiceTime(Customer first, Customer second, double currentDistance) {
        double distance = first.getMappedDistances().get(second.getCustomerId());
        if (distance + currentDistance < second.getStartTime()) {
            distance = second.getStartTime() + second.getServiceTime();
        } else if (distance + currentDistance >= second.getStartTime() && currentDistance + distance <= second.getFinishTime()) {
            distance = currentDistance + distance + second.getServiceTime();
        }
        return distance;
    }
}
