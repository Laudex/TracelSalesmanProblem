package IteratedLocalSearch;

import entities.Customer;
import entities.Route;
import services.RouteServices;

import java.util.ArrayList;

public class Perturbation {

    private static Customer depot;

    private static ArrayList<Customer> validCombination = new ArrayList<>();
    private static ArrayList<Customer> validCombinationMin = new ArrayList<>();
    private static ArrayList<Customer> validCombinationMax = new ArrayList<>();

    public static ArrayList<Customer> getValidCombinationMin() {
        return validCombinationMin;
    }

    public static void setValidCombinationMin(ArrayList<Customer> validCombinationMin) {
        Perturbation.validCombinationMin = validCombinationMin;
    }

    public static ArrayList<Customer> getValidCombinationMax() {
        return validCombinationMax;
    }

    public static void setValidCombinationMax(ArrayList<Customer> validCombinationMax) {
        Perturbation.validCombinationMax = validCombinationMax;
    }

    public static ArrayList<Customer> getValidCombination() {
        return validCombination;
    }

    public static void setValidCombination(ArrayList<Customer> validCombination) {
        Perturbation.validCombination = validCombination;
    }

    public static void setDepot(Customer depot) {
        Perturbation.depot = depot;
    }

    public static void execute(ArrayList<Route> routes, Customer depot) {
        setDepot(depot);
        ArrayList<Route> routesCopy = new ArrayList<>(routes);
        Route minRoute = findMinRoute(routes);
        Route maxRoute = findMaxRoute(routes);

        for (int i = 0; i < 5; i++) {
            move(minRoute, maxRoute);
        }


        routes.remove(maxRoute);
        routes.remove(minRoute);
        Route newMinRoute = findMinRoute(routes);
        Route newMaxRoute = findMaxRoute(routes);
        routes.add(minRoute);
        routes.add(maxRoute);

        int check = newMinRoute.getListOfCustomers().size()  + newMaxRoute.getListOfCustomers().size();
        for (int i = 0; i < 5; i++) {
            ArrayList<Customer> copyMin = new ArrayList<>(newMinRoute.getListOfCustomers());
            ArrayList<Customer> copyMax = new ArrayList<>(newMaxRoute.getListOfCustomers());
            move(newMinRoute, newMaxRoute);
            if (newMinRoute.getListOfCustomers().size()  + newMaxRoute.getListOfCustomers().size() != check){
            }
        }

        for (int i = 0; i <= 10; i++){
            int first = (int) (Math.random() * routes.size());
            int second = (int) (Math.random() * routes.size());
            Route firstRoute = routes.get(first);
            Route secondRoute = routes.get(second);
            if (firstRoute.getId() != secondRoute.getId()){
                if (RouteServices.countDistance(firstRoute.getListOfCustomers(), depot) > RouteServices.countDistance(secondRoute.getListOfCustomers(), depot)){
                    move(secondRoute, firstRoute);
                } else {
                    move(firstRoute, secondRoute);
                }
            }
            if (firstRoute.getListOfCustomers().size() == 0){
                routes.remove(firstRoute);
            }
            if (secondRoute.getListOfCustomers().size() == 0){
                routes.remove(secondRoute);
            }
        }


    }

    public static Route findMinRoute(ArrayList<Route> routes) {
        Route minRoute = routes.get(0);
        for (Route route : routes) {
            if (minRoute.getFinishDepot() > route.getFinishDepot()) {
                minRoute = route;
            }
        }
        return minRoute;

    }

    public static Route findMaxRoute(ArrayList<Route> routes) {
        Route maxRoute = routes.get(0);
        for (Route route : routes) {
            if (maxRoute.getFinishDepot() < route.getFinishDepot()) {
                maxRoute = route;
            }
        }
        return maxRoute;
    }

    public static void move(Route min, Route max) {
        ArrayList<Customer> copyListOfCustomersMax = new ArrayList<>(max.getListOfCustomers());
        ArrayList<Customer> copyListOfCustomersMin = new ArrayList<>(min.getListOfCustomers());
        if (copyListOfCustomersMax.size() == 0 && copyListOfCustomersMin.size() == 0){
            return;
        }
        if (copyListOfCustomersMin.size() == 0){
            for (Customer customer : max.getListOfCustomers()){
                int j = copyListOfCustomersMax.indexOf(customer);
                copyListOfCustomersMin.add(customer);
                copyListOfCustomersMax.remove(customer);
                if (isValid(max, copyListOfCustomersMax, 1) && isValid(min, copyListOfCustomersMin, 0)) {
                    max.setListOfCustomers(getValidCombinationMax());
                    min.setListOfCustomers(getValidCombinationMin());
                    min.getCar().setDistance(RouteServices.countDistance(min.getListOfCustomers(), depot));
                    max.getCar().setDistance(RouteServices.countDistance(max.getListOfCustomers(), depot));
                    min.setFinishDepot(min.getCar().getDistance());
                    max.setFinishDepot(max.getCar().getDistance());

                    LocalSearchAction.setNewStartServiceTimes(min, depot);
                    LocalSearchAction.setNewStartServiceTimes(max, depot);
                    break;
                } else {
                    copyListOfCustomersMax.add(j, customer);
                    copyListOfCustomersMin.remove(customer);
                }
            }
        } else if (copyListOfCustomersMax.size() == 0){
            for (Customer customer : min.getListOfCustomers()){
                int j = copyListOfCustomersMin.indexOf(customer);
                copyListOfCustomersMax.add(customer);
                copyListOfCustomersMin.remove(customer);
                if (isValid(max, copyListOfCustomersMax,1) && isValid(min, copyListOfCustomersMin,0)) {
                    max.setListOfCustomers(getValidCombinationMax());
                    min.setListOfCustomers(getValidCombinationMin());
                    min.getCar().setDistance(RouteServices.countDistance(min.getListOfCustomers(), depot));
                    max.getCar().setDistance(RouteServices.countDistance(max.getListOfCustomers(), depot));
                    min.setFinishDepot(min.getCar().getDistance());
                    max.setFinishDepot(max.getCar().getDistance());

                    LocalSearchAction.setNewStartServiceTimes(min, depot);
                    LocalSearchAction.setNewStartServiceTimes(max, depot);
                    break;
                } else {
                    copyListOfCustomersMin.add(j, customer);
                    copyListOfCustomersMax.remove(customer);
                }
            }
        }
        for (Customer customer : max.getListOfCustomers()) {
            for (int i = 0; i <= min.getListOfCustomers().size(); i++) {
                int j = copyListOfCustomersMax.indexOf(customer);
                copyListOfCustomersMin.add(i, customer);
                copyListOfCustomersMax.remove(customer);
                if (isValid(max, copyListOfCustomersMax, 1) && isValid(min, copyListOfCustomersMin, 0)) {
                    max.setListOfCustomers(getValidCombinationMax());
                    min.setListOfCustomers(getValidCombinationMin());
                    min.getCar().setDistance(RouteServices.countDistance(min.getListOfCustomers(), depot));
                    max.getCar().setDistance(RouteServices.countDistance(max.getListOfCustomers(), depot));
                    min.setFinishDepot(min.getCar().getDistance());
                    max.setFinishDepot(max.getCar().getDistance());

                    LocalSearchAction.setNewStartServiceTimes(min, depot);
                    LocalSearchAction.setNewStartServiceTimes(max, depot);
                    return;
                } else {
                    copyListOfCustomersMax.add(j, customer);
                    copyListOfCustomersMin.remove(i);
                }

            }
        }
    }

    public static boolean isValid(Route route, ArrayList<Customer> customers, int indicator) {
        double distance = LSValidator.validateDistance(customers, depot);
        if (LSValidator.validateCapacity(customers, route.getCar())) {
            if (distance == -1) {
                if (recombination(customers)) {
                    if (indicator == 1) {
                        setValidCombinationMax(getValidCombination());
                    } else if (indicator == 0) {
                        setValidCombinationMin(getValidCombination());
                    }
                    return true;
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
        if (recombination(customers, distance)){
            if (indicator == 1){
                setValidCombinationMax(getValidCombination());
            } else if (indicator == 0){
                setValidCombinationMin(getValidCombination());
            }
            return true;
        }
        if (indicator == 1){
            setValidCombinationMax(customers);
        } else if (indicator == 0){
            setValidCombinationMin(customers);
        }
        return true;
    }
    public static boolean recombination(ArrayList<Customer> customers){
        for (int i = 0; i < customers.size(); i ++){
            if (swap(i, customers) != -1 ){
                return true;
            }
        }
        return false;
    }

    public static boolean recombination(ArrayList<Customer> customers, double distance){
        for (int i = 0; i < customers.size(); i ++){
            double newDistance = swap(i, customers, distance);
            if (newDistance != -1 && newDistance < distance ){
                distance = newDistance;
                return true;
            }
        }
        return false;
    }

    public static double swap(int i, ArrayList<Customer> customers, double distance){
        ArrayList<Customer> copyCustomers = new ArrayList<>(customers);
        for (int j = i; j < copyCustomers.size() - 1; j++){
            Customer secondCust = copyCustomers.remove(j+1);
            Customer firstCust = copyCustomers.remove(j);
            copyCustomers.add(j, secondCust);
            copyCustomers.add(j+1, firstCust);
            double newDistance = LSValidator.validateDistance(copyCustomers, depot);
            if (newDistance == -1 || newDistance >= distance){
                return swap(j+1, copyCustomers, distance);
            } else if (newDistance < distance){
                setValidCombination(copyCustomers);
                return newDistance;
            }
        }
        return -1;
    }

    public static double swap(int i, ArrayList<Customer> customers){
        ArrayList<Customer> copyCustomers = new ArrayList<>(customers);
        for (int j = i; j < copyCustomers.size() - 1; j++){
            Customer secondCust = copyCustomers.remove(j+1);
            Customer firstCust = copyCustomers.remove(j);
            copyCustomers.add(j, secondCust);
            copyCustomers.add(j+1, firstCust);
            double distance = LSValidator.validateDistance(copyCustomers, depot);
            if (distance == -1){
                return swap(j+1, copyCustomers);
            } else {
                setValidCombination(copyCustomers);
                return distance;
            }
        }
        return -1;
    }
}
