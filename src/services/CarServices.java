package services;

import entities.*;

import java.math.BigInteger;

public class CarServices {

    public static void moveToDepot(Car car, Customer depot){
        Customer current = car.getCurrentState();
        double distance = current.getMappedDistances().get(depot.getCustomerId());
        car.setDistance(car.getDistance() + distance);
        car.setCurrentState(depot);
    }

    public static void moveToCustomer(Car car, Customer destCustomer){
        Customer startCust = car.getCurrentState();
        double distance = startCust.getMappedDistances().get(destCustomer.getCustomerId());
        car.setDistance(car.getDistance() + distance);
        car.setCurrentState(destCustomer);
        if (car.getDistance() >= destCustomer.getStartTime() && car.getDistance() <= destCustomer.getFinishTime()){
            unload(car);
        } else if (car.getDistance() <= destCustomer.getStartTime()){
            waitAndUnload(car);
        }
    }

    public static void unload(Car car){
        car.setCapacity(car.getCapacity() - car.getCurrentState().getDemand());
        car.getCurrentState().setServed(true);
        car.setDistance(car.getDistance() + car.getCurrentState().getServiceTime());
    }

    public static void waitAndUnload(Car car){
        double timeDifference = car.getCurrentState().getStartTime() - car.getDistance();
        car.setDistance(car.getDistance() + timeDifference);
        unload(car);
    }
    public static boolean isMoveAcceptable(Car car, Customer customer, Customer depot){
        if (customer.getDemand() > car.getCapacity()){
           // System.out.println("Error: Car doesn't have enough goods for customer!");
            return false;
        }
        double distance = car.getDistance() + car.getCurrentState().getMappedDistances().get(customer.getCustomerId());
        if (distance >= customer.getStartTime() && distance <= customer.getFinishTime()){
            double timeToDepot = distance + customer.getServiceTime() + customer.getMappedDistances().get(depot.getCustomerId());
            if (timeToDepot <= depot.getFinishTime()){
            //    System.out.println("Good: there is enough time to return to depot!");
                return true;
            } else {
              //  System.out.println("Error: not enough time to return to depot!");
                return false;
            }
        } else if (distance <= customer.getStartTime()){
            double timeToDepot = customer.getStartTime() + customer.getServiceTime() + customer.getMappedDistances().get(depot.getCustomerId());
            if (timeToDepot <= depot.getFinishTime()){
               // System.out.println("Good: there is enough time to return to depot!");
                return true;
            } else {
              //  System.out.println("Error: not enough time to return to depot!");
                return false;
            }
        } else if (distance >= customer.getFinishTime()){
            //System.out.println("Error: car be late to unload");
            return false;
        }
        return true;
    }

    public static double getStartServiceTime(Car car, Customer customer){
        double distance = car.getCurrentState().getMappedDistances().get(customer.getCustomerId());
        if (car.getDistance() + distance >= customer.getStartTime() && car.getDistance() + distance <= customer.getFinishTime()){
            return car.getDistance() + distance;
        } else {
            return customer.getStartTime();
        }
    }
}
