package IteratedLocalSearch;

import entities.Customer;
import entities.Route;
import services.RouteServices;

import java.util.ArrayList;

public class LocalSearchAction {

    private static Customer depot;
    private static ArrayList<Customer> firstBest = new ArrayList<>();
    private static ArrayList<Customer> secondBest = new ArrayList<>();
    private static double bestDistance;

    public static double getBestDistance() {
        return bestDistance;
    }

    public static void execute(ArrayList<Route> routes, Customer depotOut){
        depot = depotOut;
        for (Route firstRoute : routes){
            for (Route secondRoute : routes){
                if (firstRoute.getId()!=secondRoute.getId()){
                    makeSwapByOne(firstRoute, secondRoute);
                    System.out.println("kek");
                }

            }
        }

    }

    public static void makeSwapByOne(Route firstRoute, Route secondRoute){
        ArrayList<Customer> firstCustomers = firstRoute.getListOfCustomers();
        ArrayList<Customer> firstCustomersCopy = new ArrayList<>(firstCustomers);
        ArrayList<Customer> secondCustomers = secondRoute.getListOfCustomers();
        ArrayList<Customer> secondCustomersCopy = new ArrayList<>(secondCustomers);
        setBests(firstCustomers, secondCustomers);
        bestDistance = RouteServices.countDistance(firstCustomers, depot) + RouteServices.countDistance(secondCustomers, depot);
        System.out.println("Best Distance: " + bestDistance);
        for (Customer firstCustomer : firstCustomers){
            for (Customer secondCustomer : secondCustomers){
                int firstIndex = firstCustomers.indexOf(firstCustomer);
                int secondIndex = secondCustomers.indexOf(secondCustomer);
                System.out.println("Swap!");
                printToCheck(firstCustomersCopy);
                printToCheck(secondCustomersCopy);
                firstCustomersCopy.remove(firstIndex);
                secondCustomersCopy.remove(secondIndex);
                firstCustomersCopy.add(firstIndex, secondCustomer);
                secondCustomersCopy.add(secondIndex, firstCustomer);
                printToCheck(firstCustomersCopy);
                printToCheck(secondCustomersCopy);
                //validation
                boolean isValid = LSValidator.validate(firstCustomersCopy, secondCustomersCopy, depot, firstRoute.getCar(), secondRoute.getCar());
                if (isValid){
                    double newDistance = RouteServices.countDistance(firstCustomersCopy, depot) + RouteServices.countDistance(secondCustomersCopy, depot);
                    System.out.println("New Distance:" + newDistance);
                    if (newDistance < bestDistance){
                        setBests(firstCustomersCopy, secondCustomersCopy);
                        bestDistance = newDistance;
                    } else {
                        System.out.println("New Distance is not better!");
                    }
                    //System.out.println("good");
                } else {
                    System.out.println("Bad");
                }
                firstCustomersCopy.remove(firstIndex);
                secondCustomersCopy.remove(secondIndex);
                firstCustomersCopy.add(firstIndex, firstCustomer);
                secondCustomersCopy.add(secondIndex, secondCustomer);

            }
            firstRoute.setListOfCustomers(firstBest);
            secondRoute.setListOfCustomers(secondBest);
        }

    }

    public static void printToCheck(ArrayList<Customer> customers){
        //System.out.println("Checking!!!!!!!");
        for (Customer customer : customers){
            System.out.print(customer.getCustomerId() + " ");
        }
        System.out.println();
    }

    public static void setBests(ArrayList<Customer> firstCustomers,  ArrayList<Customer> secondCustomers) {
        firstBest = new ArrayList<>(firstCustomers);
        secondBest = new ArrayList<>(secondCustomers);
    }
}
