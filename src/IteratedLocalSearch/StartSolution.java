package IteratedLocalSearch;

import entities.*;
import services.CarServices;
import services.CustomerServices;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class StartSolution {

    public static void execute(ArrayList<Route> routes, ArrayList<Car> cars, ArrayList<Customer> customers, Customer depot) {
        int routeId = 1;
        for (Car car : cars) {
            ArrayList<Customer> copyCustomer = new ArrayList<>(customers);
            Route newRoute = new Route(routeId, car, 0);
            Map<Customer, Double> customersAndStartServiceTime = new LinkedHashMap<>();
            ArrayList<Customer> listOfCustomers = new ArrayList<>();
            for (int i = 0; i < 500; i++) {
                //Customer customer = findMinDistance(car, copyCustomer);
                Customer customer = findMinDifference(car, copyCustomer);
                if (customer != null && CarServices.isMoveAcceptable(car, customer, depot)) {
                    double startServiceTime = CarServices.getStartServiceTime(car, customer);
                    customersAndStartServiceTime.put(customer, startServiceTime);
                    listOfCustomers.add(customer);
                    CarServices.moveToCustomer(car, customer);
                    customer.setServed(true);


                } else {
                    copyCustomer.remove(customer);
                }
                Customer customer1 = findMinStartTime(copyCustomer);
                if (customer1 != null && CarServices.isMoveAcceptable(car, customer1, depot)) {
                    double startServiceTime = CarServices.getStartServiceTime(car, customer1);
                    customersAndStartServiceTime.put(customer1, startServiceTime);
                    listOfCustomers.add(customer1);
                    CarServices.moveToCustomer(car, customer1);
                    customer1.setServed(true);
                } else {
                    copyCustomer.remove(customer1);
                }
                Customer customer2 = findMinDistance(car, copyCustomer);
                if (customer2 != null && CarServices.isMoveAcceptable(car, customer2, depot)) {
                    double startServiceTime = CarServices.getStartServiceTime(car, customer2);
                    customersAndStartServiceTime.put(customer2, startServiceTime);
                    listOfCustomers.add(customer2);
                    CarServices.moveToCustomer(car, customer2);
                    customer2.setServed(true);
                } else {
                    copyCustomer.remove(customer2);
                }
                for (Customer customer3 : copyCustomer) {
                    if (!customer3.isServed() && CarServices.isMoveAcceptable(car, customer3, depot)) {
                        double startServiceTime = CarServices.getStartServiceTime(car, customer3);
                        customersAndStartServiceTime.put(customer3, startServiceTime);
                        listOfCustomers.add(customer3);
                        CarServices.moveToCustomer(car, customer3);
                        customer3.setServed(true);
                        copyCustomer.remove(customer3);
                        break;
                    } else {
                        copyCustomer.remove(customer3);
                        break;
                    }
                }

            }

            CarServices.moveToDepot(car, depot);
            newRoute.setCustomersAndStartServiceTime(customersAndStartServiceTime);
            newRoute.setListOfCustomers(listOfCustomers);
            newRoute.setFinishDepot(car.getDistance());
            routes.add(newRoute);
            markServed(customers, listOfCustomers);
            if (CustomerServices.allCustomersAreServed(customers)) {
                break;
            }
            routeId++;

        }
    }

    public static Customer findMinDistance(Car car, ArrayList<Customer> customers) {
        double minDistance = 32000;
        Customer minCustomer = null;
        for (Customer customer : customers) {
            if (!customer.isServed()) {
                double newDistance = car.getCurrentState().getMappedDistances().get(customer.getCustomerId());
                if (newDistance < minDistance) {
                    minDistance = newDistance;
                    minCustomer = customer;
                }
            }
        }
        return minCustomer;
    }

    public static Customer findMinStartTime(ArrayList<Customer> customers) {
        double minStartTime = 1000;
        Customer minCustomer = null;
        for (Customer customer : customers) {
            if (!customer.isServed()) {
                double newStartTime = customer.getStartTime();
                if (newStartTime < minStartTime) {
                    minStartTime = newStartTime;
                    minCustomer = customer;
                }
            }
        }
        return minCustomer;
    }

    public static Customer findMinDifference(Car car, ArrayList<Customer> customers) {
        Customer currentState = car.getCurrentState();
        double minDiff = 32000;
        Customer minDiffCust = null;
        for (Customer customer : customers) {
            if (!customer.isServed()) {
                double distance = currentState.getMappedDistances().get(customer.getCustomerId());
                if (distance <= customer.getFinishTime()) {
                    if (distance < customer.getStartTime()) {
                        distance = customer.getStartTime();
                    }
                    if (distance < minDiff) {
                        minDiff = distance;
                        minDiffCust = customer;
                    }

                } else {
                    continue;
                }
            }
        }
        return minDiffCust;
    }

    public static Customer findMinFinishTime(ArrayList<Customer> customers) {
        Customer minFinishCust = null;
        double minFinishTime = 32000;
        for (Customer customer : customers) {
            if (!customer.isServed()) {
                double newFinishTime = customer.getFinishTime();
                if (newFinishTime < minFinishTime) {
                    minFinishTime = newFinishTime;
                    minFinishCust = customer;
                }
            }
        }
        return minFinishCust;
    }

    public static void markServed(ArrayList<Customer> allCustomers, ArrayList<Customer> changed) {
        for (Customer changedCustomer : changed) {
            for (Customer customer : allCustomers) {
                if (changedCustomer.getCustomerId() == customer.getCustomerId()) {
                    customer.setServed(true);
                }
            }
        }
    }
}
