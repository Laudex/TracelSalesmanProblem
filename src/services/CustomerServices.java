package services;

import entities.Customer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CustomerServices {

    public static void calculateDistances(ArrayList<Customer> customers) {
        for (Customer customer : customers) {
            Map<Integer, Double> mappedDistances = new HashMap<>();
            for (Customer anotherCustomer : customers) {
                double distance = customer.getDistanceToCustomer(anotherCustomer);
                mappedDistances.put(anotherCustomer.getCustomerId(), distance);

            }
            customer.setMappedDistances(mappedDistances);
        }
    }

    public static boolean allCustomersAreServed(ArrayList<Customer> customers) {
        for (Customer customer : customers) {
            if (!customer.isServed()) {
                System.out.println("Warn: not all customers are served yet!");
                return false;
            }
        }
        return true;
    }
}
