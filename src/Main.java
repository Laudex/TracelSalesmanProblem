import IteratedLocalSearch.LSValidator;
import IteratedLocalSearch.LocalSearchAction;
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
        for (Car car: cars){
            car.setCurrentState(depot);
        }
        LSValidator.setCapacity(cars.get(0).getCapacity());

        CustomerServices.calculateDistances(customers);

        for (Customer customer : customers) {
            System.out.println("Cusomer Id = " + customer.getCustomerId());
            Map<Integer, Double> distances = customer.getMappedDistances();

            System.out.println(distances.toString());
        }

        //Find start solution:

        ArrayList<Route> routes = new ArrayList<>();

        int routeId = 1;
        for (Car car : cars) {
            Route newRoute = new Route(routeId, car, 0);
            Map<Customer, Double> customersAndStartServiceTime = new LinkedHashMap<>();
            ArrayList<Customer> listOfCustomers = new ArrayList<>();
            for (Customer customer : customers) {
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
                printRoutes(routes);
                break;
            }
            routeId++;

        }

        double distance = RouteServices.totalDistance(routes, depot);
        System.out.println("Distance of first solution: " + distance);

        LocalSearchAction.execute(routes, depot);
        printRoutes(routes);
        System.out.println("Best Distance : " + LocalSearchAction.getBestDistance());




    }

    public static void printRoutes(ArrayList<Route> routes) {
       // System.out.println("kek " + routes.size());
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
