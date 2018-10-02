package GuidedLocalSearch;

import IteratedLocalSearch.LocalSearchAction;
import entities.Customer;
import entities.Route;
import services.FileServices;
import services.RouteServices;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class GuidedLocalSearchAction {

    private static Customer depot;
    private static double lambda = 2;


    public static void execute(ArrayList<Route> routes, ArrayList<Customer> allCustomers, Customer depotOut, String filenameOut) {
        depot = depotOut;
        double[][] featureCostMap = new double[allCustomers.size()][allCustomers.size()];
        for (Customer firstCustomer : allCustomers) {
            for (Customer secondCustomer : allCustomers) {
                double distance = firstCustomer.getMappedDistances().get(secondCustomer.getCustomerId());
                featureCostMap[firstCustomer.getCustomerId()][secondCustomer.getCustomerId()] = distance;
            }
        }
        int[][] penaltyMap = new int[allCustomers.size()][allCustomers.size()];
        for (int i = 0; i < allCustomers.size(); i++) {
            for (int j = 0; j < allCustomers.size(); j++) {
                penaltyMap[i][j] = 0;
            }
        }
        int[][] featureIndicatorMap = new int[allCustomers.size()][allCustomers.size()];
        for (int i = 0; i < allCustomers.size(); i++) {
            for (int j = 0; j < allCustomers.size(); j++) {
                featureIndicatorMap[i][j] = 0;
            }
        }
        double[][] utilMap = new double[allCustomers.size()][allCustomers.size()];
        for (int i = 0; i < allCustomers.size(); i++) {
            for (int j = 0; j < allCustomers.size(); j++) {
                utilMap[i][j] = 0;
            }
        }
        double bestDistance = RouteServices.totalDistance(routes, depot);
        ArrayList<Route> bestRoutes = new ArrayList<>(routes);
        int k = 0;
        while (k < 500){
            LocalSearch.executeGLS(routes, depot, penaltyMap);
            findIndicates(featureIndicatorMap, routes);
            double newDistance = RouteServices.totalDistance(routes, depot);
            if (newDistance < bestDistance){
                bestDistance = newDistance;
                bestRoutes = new ArrayList<>(routes);
            }
            findUtil(penaltyMap, featureCostMap, featureIndicatorMap, utilMap, routes);
            k++;
        }
        FileServices.printToTxt(bestRoutes, filenameOut);





    }

    public static void printRoutes(ArrayList<Route> routes) {
        System.out.println("Size: " + routes.size());
        for (Route route : routes) {
            Iterator it = route.getCustomersAndStartServiceTime().entrySet().iterator();
            System.out.print(route.getStartDepot() + " 0 ");
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                Customer customer = (Customer) pair.getKey();
                System.out.print(customer.getCustomerId() + " " + pair.getValue() + " ");
            }
            System.out.println("0 " + "Depot Finish :" + route.getFinishDepot());
            System.out.println();
        }
    }

    public static void findIndicates(int[][] featureIndicatorMap, ArrayList<Route> routes) {
        for (int i = 0; i < featureIndicatorMap.length; i++) {
            for (int j = 0; j < featureIndicatorMap.length; j++) {
                featureIndicatorMap[i][j] = 0;
            }
        }
        for (Route route : routes) {
            ArrayList<Customer> customers = route.getListOfCustomers();
            if (customers.size() > 1) {
                Customer start = customers.get(0);
                Customer last = customers.get(customers.size() - 1);
                featureIndicatorMap[0][start.getCustomerId()] = 1;
                featureIndicatorMap[0][last.getCustomerId()] = 1;
                featureIndicatorMap[start.getCustomerId()][0] = 1;
                featureIndicatorMap[last.getCustomerId()][0] = 1;
                for (int i = 0; i < customers.size(); i++) {
                    if (i + 1 < customers.size()) {
                        Customer first = customers.get(i);
                        Customer second = customers.get(i + 1);
                        featureIndicatorMap[first.getCustomerId()][second.getCustomerId()] = 1;
                        featureIndicatorMap[second.getCustomerId()][first.getCustomerId()] = 1;

                    }
                }
            } else if (customers.size() == 1){
                Customer customer = customers.get(0);
                featureIndicatorMap[0][customer.getCustomerId()] = 1;
                featureIndicatorMap[customer.getCustomerId()][0] = 1;
            }
        }
    }

    public static double getComposition(int[][] penaltyMap, int[][] featureIndicatorMap){
        double composition = 0;
        for (int i = 0; i < penaltyMap.length; i++){
            for (int j = i + 1; j < penaltyMap.length; j++){
                composition = composition + penaltyMap[i][j] * featureIndicatorMap[i][j];
            }
        }
        return composition;
    }
    public static void findUtil(int[][] penaltyMap, double[][] featureCostMap, int[][] featureIndicatorMap, double[][] utilMap, ArrayList<Route> routes){
        for (int i = 0; i < utilMap.length; i++) {
            for (int j = 0; j < utilMap.length; j++) {
                utilMap[i][j] = 0;
            }
        }
        double maxUtil = 0;
        int x = 0;
        int y = 0;
        findIndicates(featureIndicatorMap, routes);
        for (int i = 0; i < utilMap.length; i++){
            for (int j = i + 1; j < utilMap.length; j++){
                utilMap[i][j] = featureIndicatorMap[i][j] * featureCostMap[i][j] / (1 + penaltyMap[i][j]);
                if (utilMap[i][j] > maxUtil) {
                    maxUtil = utilMap[i][j];
                    x = i;
                    y = j;
                }
            }
        }
        penaltyMap[x][y] ++;
        penaltyMap[y][x] ++;
    }


}
