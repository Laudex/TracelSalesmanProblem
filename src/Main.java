import GuidedLocalSearch.GuidedLocalSearchAction;
import IteratedLocalSearch.LSValidator;
import IteratedLocalSearch.LocalSearchAction;
import IteratedLocalSearch.Perturbation;
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
        Car car1 = new Car(100, 0);
        Car car2 = new Car(100, 0);

        ArrayList<Customer> customers = new ArrayList<>();
        ArrayList<Car> cars = new ArrayList<>();
        // cars.add(car1);
        //cars.add(car2);

        //Customer depot = new Customer(0, 0, 0, 0, 0, 500, 0, true);
        Customer customer2 = new Customer(1, 50, 45, 45, 71, 95, 10, false);
        Customer customer3 = new Customer(2, 71, 27, 65, 154, 220, 50, false);
        Customer customer4 = new Customer(3, 23, 85, 34, 314, 367, 23, false);

        // car1.setCurrentState(depot);
        // car2.setCurrentState(depot);


        // customers.add(depot);
        // customers.add(customer2);
        // customers.add(customer3);
        //  customers.add(customer4);
        FileServices.readFromTxt("C:\\instances\\C108.txt");
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

        int routeId = 1;
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
                System.out.println("HERE!!!");
                //printRoutes(routes);
                break;
            }
            routeId++;

        }

        double distance = RouteServices.totalDistance(routes, depot);
        double sum = 0;
        int size = 0;

        for (Route route : routes) {
            size = size + route.getListOfCustomers().size();
        }

        // System.out.println("Distance of first solution: " + distance);





        for (Route route : routes) {
            sum = sum + route.getFinishDepot();
        }

        System.out.println("Distance of first solution: " + distance);
        System.out.println("sum: " + sum);
        System.out.println("Best Distance : " + RouteServices.totalDistance(routes, depot));
        LocalSearchAction.execute(routes, depot);
        double startDistance = RouteServices.totalDistance(routes, depot);
        ArrayList<Route> startRoutes = new ArrayList<>(routes);
        for (int i = 1; i<500;i++) {
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
        printRoutes(startRoutes);
        System.out.println("Best Distance : " + startDistance);
        //GuidedLocalSearchAction.execute(routes, customers, depot);


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
}
