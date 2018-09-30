package services;

import entities.Car;
import entities.Customer;
import entities.Route;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class FileServices {

    private static int numberOfCars;
    private static int capacity;
    private static ArrayList<Car> cars = new ArrayList<>();
    private static ArrayList<Customer> customers = new ArrayList<>();

    public static ArrayList<Car> getCars() {
        return cars;
    }

    public static ArrayList<Customer> getCustomers() {
        return customers;
    }

    public static int getNumberOfCars() {
        return numberOfCars;
    }

    public static void setNumberOfCars(int numberOfCars) {
        FileServices.numberOfCars = numberOfCars;
    }

    public static int getCapacity() {
        return capacity;
    }

    public static void setCapacity(int capacity) {
        FileServices.capacity = capacity;
    }

    public static void printToTxt(ArrayList<Route> routes, String filename){
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
            for (Route route : routes) {
                int k = 0;
                StringBuilder routeString = new StringBuilder();
                Iterator it = route.getCustomersAndStartServiceTime().entrySet().iterator();
                routeString.append("0 " + route.getStartDepot() + " ");
                while (it.hasNext()) {
                    k++;
                    Map.Entry pair = (Map.Entry) it.next();
                    Customer customer = (Customer) pair.getKey();
                    routeString.append(customer.getCustomerId() + " " + pair.getValue() + " ");
                }
                routeString.append("0 " + route.getFinishDepot());
                System.out.println(routeString.toString());
                if (k > 0) {
                    writer.write(routeString.toString());
                    writer.write("\n");
                }
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void readFromTxt(String filename) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(filename));
        String line;
        int count = 1;
        while((line = in.readLine()) != null)
        {
            if (count == 5){
                String[] lineFifth = line.split(" ");
                ArrayList<String> numberAndCapacity = new ArrayList<>();
                for (int i = 0; i < lineFifth.length; i++){
                   if (lineFifth[i].matches("\\d+")){
                       numberAndCapacity.add(lineFifth[i]);
                   }
                }
                numberOfCars = Integer.parseInt(numberAndCapacity.get(0));
                capacity = Integer.parseInt(numberAndCapacity.get(1));
                carInitialize();


            }
            if (count >= 10){
                String[] lineForCustomer = line.split(" ");
                ArrayList<String> newCustomer = new ArrayList<>();
                for (int i = 0; i < lineForCustomer.length; i++){
                    if (lineForCustomer[i].matches("\\d+")){
                        newCustomer.add(lineForCustomer[i]);
                    }
                }
                customerInitialize(newCustomer);
            }

            count ++;
        }
        in.close();
    }

    public static void carInitialize(){
        for(int i = 0; i < numberOfCars; i++){
            Car car = new Car(capacity, 0);
            cars.add(car);
        }
    }

    public static void customerInitialize(ArrayList<String> newCustomer){
        int customerId = Integer.parseInt(newCustomer.get(0));
        double xCoord = Double.parseDouble(newCustomer.get(1));
        double yCoord = Double.parseDouble(newCustomer.get(2));
        int demand = Integer.parseInt(newCustomer.get(3));
        double startTime = Double.parseDouble(newCustomer.get(4));
        double finishTime = Double.parseDouble(newCustomer.get(5));
        double serviceTime = Double.parseDouble(newCustomer.get(6));

        if (customerId == 0) {
            Customer customer = new Customer(customerId, xCoord, yCoord, demand, startTime, finishTime, serviceTime, true);
            customers.add(customer);
        } else {
            Customer customer = new Customer(customerId, xCoord, yCoord, demand, startTime, finishTime, serviceTime, false);
            customers.add(customer);
        }
    }
}
