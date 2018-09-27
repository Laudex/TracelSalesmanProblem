package GuidedLocalSearch;

import IteratedLocalSearch.LSValidator;
import IteratedLocalSearch.LocalSearchAction;
import entities.Car;
import entities.Customer;
import entities.Route;
import services.RouteServices;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class LocalSearch {

    private static double lambda = 10;

    private static Customer depot;
    private static ArrayList<Customer> firstBest = new ArrayList<>();
    private static ArrayList<Customer> secondBest = new ArrayList<>();
    private static double bestDistance;

    private static ArrayList<Customer> validCombination = new ArrayList<>();

    public static ArrayList<Customer> getValidCombination() {
        return validCombination;
    }

    public static void setValidCombination(ArrayList<Customer> validCombination) {
        LocalSearch.validCombination = validCombination;
    }

    public static void executeGLS(ArrayList<Route> routes, Customer depotOut, int[][] penaltyMap){
        ArrayList<Route> copyRoutes = new ArrayList<>(routes);
        depot = depotOut;
        for (Route firstRoute : routes) {
            for (Route secondRoute : routes) {
                if (firstRoute.getId() != secondRoute.getId()) {
                    makeSwapByOne(firstRoute, secondRoute, penaltyMap);
                }

            }
        }
        for(Route route : routes){
            double distance = RouteServices.countDistance(route.getListOfCustomers(), depot) + getCompositionForRoute(route.getListOfCustomers(), penaltyMap);
            if(recombination(route.getListOfCustomers(), distance, penaltyMap)){
                route.setListOfCustomers(getValidCombination());
                route.getCar().setDistance(RouteServices.countDistance(route.getListOfCustomers(), depot));
                route.setFinishDepot(route.getCar().getDistance());
                setNewStartServiceTimes(route, depot);
            }
        }
        for (Route firstRoute : routes) {
            for (Route secondRoute : routes) {
                if (firstRoute.getId() != secondRoute.getId()) {
                    makeSwapByOne(firstRoute, secondRoute, penaltyMap);
                }

            }
        }
    }

    public static boolean recombination(ArrayList<Customer> customers, double distance, int[][] penaltyMap){
        for (int i = 0; i < customers.size(); i ++){
            double newDistance = swap(i, customers, distance, penaltyMap);
            if (newDistance != -1 && newDistance < distance ){
                distance = newDistance;
                return true;
            }
        }
        return false;
    }
    public static double swap(int i, ArrayList<Customer> customers, double distance, int[][] penaltyMap){
        ArrayList<Customer> copyCustomers = new ArrayList<>(customers);
        for (int j = i; j < copyCustomers.size() - 1; j++){
            Customer secondCust = copyCustomers.remove(j+1);
            Customer firstCust = copyCustomers.remove(j);
            copyCustomers.add(j, secondCust);
            copyCustomers.add(j+1, firstCust);
            double newDistance = LSValidator.validateDistance(copyCustomers, depot) ;
            if (newDistance == -1 || newDistance + getCompositionForRoute(copyCustomers, penaltyMap) > distance){
                return swap(j+1, copyCustomers, distance, penaltyMap);
            } else if (newDistance < distance){
                setValidCombination(copyCustomers);
                return newDistance + getCompositionForRoute(copyCustomers, penaltyMap);
            }
        }
        return -1;
    }


    public static void makeSwapByOne(Route firstRoute, Route secondRoute, int[][] penaltyMap) {
        ArrayList<Customer> firstCustomers = firstRoute.getListOfCustomers();
        ArrayList<Customer> firstCustomersCopy = new ArrayList<>(firstCustomers);
        ArrayList<Customer> secondCustomers = secondRoute.getListOfCustomers();
        ArrayList<Customer> secondCustomersCopy = new ArrayList<>(secondCustomers);
        setBests(firstCustomers, secondCustomers);
        bestDistance = RouteServices.countDistance(firstCustomers, depot)  + RouteServices.countDistance(secondCustomers, depot) + lambda * (getCompositionForRoute(firstCustomers, penaltyMap) + getCompositionForRoute(secondCustomers, penaltyMap));

        swapByOneCustomer(firstCustomers, secondCustomers, firstRoute.getCar(), secondRoute.getCar(), penaltyMap);
        firstRoute.setListOfCustomers(firstBest);
        secondRoute.setListOfCustomers(secondBest);

        firstRoute.getCar().setDistance(RouteServices.countDistance(firstBest, depot));
        secondRoute.getCar().setDistance(RouteServices.countDistance(secondBest, depot));

        firstRoute.setFinishDepot(firstRoute.getCar().getDistance());
        secondRoute.setFinishDepot(secondRoute.getCar().getDistance());

        setNewStartServiceTimes(firstRoute, depot);
        setNewStartServiceTimes(secondRoute, depot);

    }

    public static void swapByOneCustomer(ArrayList<Customer> firstCustomers, ArrayList<Customer> secondCustomers, Car firstCar, Car secondCar, int[][] penaltyMap) {
        ArrayList<Customer> firstCustomersCopy = new ArrayList<>(firstCustomers);
        ArrayList<Customer> secondCustomersCopy = new ArrayList<>(secondCustomers);
        for (Customer firstCustomer : firstCustomers) {
            for (Customer secondCustomer : secondCustomers) {
                int firstIndex = firstCustomers.indexOf(firstCustomer);
                int secondIndex = secondCustomers.indexOf(secondCustomer);
                // System.out.println("Swap!");
                firstCustomersCopy.remove(firstIndex);
                secondCustomersCopy.remove(secondIndex);
                firstCustomersCopy.add(firstIndex, secondCustomer);
                secondCustomersCopy.add(secondIndex, firstCustomer);
                //validation
                boolean isValid = LSValidator.validate(firstCustomersCopy, secondCustomersCopy, depot, firstCar, secondCar);
                if (isValid) {
                    double newDistance = RouteServices.countDistance(firstCustomersCopy, depot) + RouteServices.countDistance(secondCustomersCopy, depot) + lambda * (getCompositionForRoute(firstCustomersCopy, penaltyMap) + getCompositionForRoute(secondCustomersCopy, penaltyMap));
                    // System.out.println("New Distance:" + newDistance);
                    if (newDistance < bestDistance) {
                        setBests(firstCustomersCopy, secondCustomersCopy);
                        bestDistance = newDistance;
                        //return;
                        swapByOneCustomer(firstCustomersCopy, secondCustomersCopy, firstCar, secondCar, penaltyMap);

                    } else {
                        //   System.out.println("New Distance is not better!");
                        //   System.out.println("New Distance: " + newDistance + " Best Distance: " + bestDistance);
                    }
                    //System.out.println("good");
                } else {
                    //System.out.println("Bad");
                }
                firstCustomersCopy.remove(firstIndex);
                secondCustomersCopy.remove(secondIndex);
                firstCustomersCopy.add(firstIndex, firstCustomer);
                secondCustomersCopy.add(secondIndex, secondCustomer);

            }

        }
    }

    public static void setBests(ArrayList<Customer> firstCustomers, ArrayList<Customer> secondCustomers) {
        firstBest = new ArrayList<>(firstCustomers);
        secondBest = new ArrayList<>(secondCustomers);
    }

    public static void setNewStartServiceTimes(Route route, Customer depot) {
        ArrayList<Customer> customers = route.getListOfCustomers();
        Map<Customer, Double> customersAndStartServiceTime = new LinkedHashMap<>();
        double distance = 0;
        int size = customers.size();
        if (size > 0) {
            Customer first = customers.get(0);
            distance = RouteServices.countDistanceAndServiceTime(depot, first, distance);
            customersAndStartServiceTime.put(first, distance - first.getServiceTime());
        }
        for (int i = 0; i < size; i++) {
            if (i + 1 < size) {
                Customer first = customers.get(i);
                Customer second = customers.get(i + 1);
                distance = RouteServices.countDistanceAndServiceTime(first, second, distance);
                customersAndStartServiceTime.put(second, distance - second.getServiceTime());

            }
        }
        route.setCustomersAndStartServiceTime(customersAndStartServiceTime);
    }

    public static double getCompositionForRoute(ArrayList<Customer> customers, int[][] penaltyMap){
        double composition = 0;
        if (customers.size() != 0){
            composition = composition + penaltyMap[0][customers.get(0).getCustomerId()];
            for(int i = 0; i < customers.size(); i++){
                for (int j = i + 1; j < customers.size(); j++){
                    Customer first = customers.get(i);
                    Customer second = customers.get(j);
                    composition = composition + penaltyMap[first.getCustomerId()][second.getCustomerId()];
                }
            }
            Customer last = customers.get(customers.size() - 1);
            composition = composition + penaltyMap[0][last.getCustomerId()];
        }
        return composition;
    }
}
