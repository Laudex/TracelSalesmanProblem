import GuidedLocalSearch.GuidedLocalSearchAction;
import IteratedLocalSearch.LSValidator;
import IteratedLocalSearch.LocalSearchAction;
import IteratedLocalSearch.Perturbation;
import IteratedLocalSearch.StartSolution;
import entities.Car;
import entities.Customer;
import entities.Route;
import services.CarServices;
import services.CustomerServices;
import services.FileServices;
import services.RouteServices;

import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {

        ArrayList<Customer> customers = new ArrayList<>();
        ArrayList<Car> cars = new ArrayList<>();


        String file = "RC105.txt";
        FileServices.readFromTxt("C:\\instances\\" + file);
        cars = FileServices.getCars();
        customers = FileServices.getCustomers();
        Customer depot = customers.get(0);
        for (Car car : cars) {
            car.setCurrentState(depot);
        }
        LSValidator.setCapacity(cars.get(0).getCapacity());

        CustomerServices.calculateDistances(customers);


        //Find start solution:

        ArrayList<Route> routes = new ArrayList<>();


        //Advanced start solution search
        StartSolution.execute(routes, cars, customers, depot);

        //Start solution greedy. Not working for RC105

        /*int routeId = 1;
        for (Car car : cars) {
            int count = 0;
            Route newRoute = new Route(routeId, car, 0);
            Map<Customer, Double> customersAndStartServiceTime = new LinkedHashMap<>();
            ArrayList<Customer> listOfCustomers = new ArrayList<>();
            for (Customer customer : customers) {
               // if (count > 4) {
                //    break;
               // }
                if (customer.getCustomerId() == 0) {
                    continue;
                } else if (customer.isServed()) {
                    continue;
                } else if (CarServices.isMoveAcceptable(car, customer, depot)) {
                    double startServiceTime = CarServices.getStartServiceTime(car, customer);
                    customersAndStartServiceTime.put(customer, startServiceTime);
                    listOfCustomers.add(customer);
                    CarServices.moveToCustomer(car, customer);
                    customer.setServed(true);
                    count++;

                } else {
                    continue;
                }
            }
            CarServices.moveToDepot(car, depot);
            newRoute.setCustomersAndStartServiceTime(customersAndStartServiceTime);
            newRoute.setListOfCustomers(listOfCustomers);
            newRoute.setFinishDepot(car.getDistance());
            routes.add(newRoute);
            if (CustomerServices.allCustomersAreServed(customers)) {
                break;
            }
            routeId++;

        }*/

        double distance = RouteServices.totalDistance(routes, depot);

        System.out.println("Distance of first solution: " + distance);

        //Iterated Local Search
        LocalSearchAction.execute(routes, depot);
        double startDistance = RouteServices.totalDistance(routes, depot);
        ArrayList<Route> startRoutes = new ArrayList<>(routes);
        for (int i = 1; i< 1000;i++) {
            Perturbation.execute(routes, depot);
            LocalSearchAction.execute(routes, depot);
            double newDistance = RouteServices.totalDistance(routes, depot);
            if (newDistance > startDistance){
                continue;
            } else {
                startRoutes = new ArrayList<>(routes);
                startDistance = newDistance;

            }
        }

        String filenameOut = "C:\\instances\\" + file + "_sol.txt";
        //WRITE for ILS
        FileServices.printToTxt(startRoutes, filenameOut);

        //Guided Local Search
        GuidedLocalSearchAction.execute(routes, customers, depot, filenameOut);

        printSize(routes);


    }

    public static void printSize(ArrayList<Route> routes){
        int size = 0;
        for (Route route : routes) {
            size = size + route.getListOfCustomers().size();
        }
        System.out.println("Size of customers: "  + size);
    }

    public static void printRoutes(ArrayList<Route> routes) {
        System.out.println("Size: " + routes.size());
        int size = 0;
        for (Route route : routes) {
            size = size + route.getListOfCustomers().size();
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
        System.out.println("Size of customers: "  + size);
    }
}
