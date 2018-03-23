package ie.dcu;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicInteger;

public class CarPark {

    private BarChart barChart;
    private AtomicInteger outside;
    private AtomicInteger gone = new AtomicInteger(0);
    private AtomicInteger spaces;
    private AtomicInteger doubleParked = new AtomicInteger(0);
    private AtomicInteger lookingForSpace = new AtomicInteger(0);
    private AtomicInteger totalInCarPark = new AtomicInteger(0);
    private AtomicInteger entrances = new AtomicInteger(3);
    private AtomicInteger exits = new AtomicInteger(3);

    CarPark(BarChart barChart, int outside, int spaces) {
        this.barChart = barChart;
        this.outside = new AtomicInteger(outside);
        this.spaces = new AtomicInteger(spaces);
    }

    private void printStatus() {
        System.out.println("Outside: " + outside + " " +
                "Entrances: " + entrances + " " +
                "Total in CarPark: " + totalInCarPark + " " +
                "Spaces: " + spaces + " " +
                "Double Parked: " + doubleParked + " " +
                "Looking For Spaces: " + lookingForSpace + " " +
                "Exits: " + exits + " " +
                "Gone: " + gone);
    }

    public synchronized void enterCarpark(Car car) throws InterruptedException, InvocationTargetException {
        while (entrances.get() == 0) {
            wait();
        }
        entrances.decrementAndGet();
        outside.decrementAndGet();
        totalInCarPark.incrementAndGet();
        lookingForSpace.incrementAndGet();
        printStatus();

        SwingUtilities.invokeAndWait(() -> {
            barChart.setGoingIn(outside);
            barChart.setTotalInCarPark(totalInCarPark);
            barChart.setLookingForSpace(lookingForSpace);
            barChart.setEntrance(entrances);
            barChart.update();
        });

        wait(2000);
        entrances.incrementAndGet();
        printStatus();
        SwingUtilities.invokeAndWait(() -> {
            barChart.setEntrance(entrances);
            barChart.update();
        });
        notify();


        takeSpace(car);
    }

    private synchronized void leaveCarpark(Car car) throws InterruptedException, InvocationTargetException {
        while (exits.get() == 0) {
            wait();
        }
        exits.decrementAndGet();
        printStatus();
        SwingUtilities.invokeAndWait(() -> {
            barChart.setExit(exits);
            barChart.update();
        });

        wait(2000);
        exits.incrementAndGet();
        totalInCarPark.decrementAndGet();
        gone.incrementAndGet();
        printStatus();
        SwingUtilities.invokeAndWait(() -> {
            barChart.setGone(gone);
            barChart.setTotalInCarPark(totalInCarPark);
            barChart.setExit(exits);
            barChart.update();
        });
        notify();
    }

    private synchronized void takeSpace(Car car) throws InterruptedException, InvocationTargetException {
        while (spaces.get() < car.getSpace()) {
            car.setAttempts(car.getAttempts() - 1);
            if (car.getAttempts() <= 0) {
                //TODO implement giving up on parking
            }
            wait();
        }
        spaces.set(spaces.get() - car.getSpace());
        if(car.getSpace() > 1){
            doubleParked.incrementAndGet();
        }
        lookingForSpace.decrementAndGet();
        printStatus();
        SwingUtilities.invokeAndWait(() -> {
            barChart.setSpacesAvailable(spaces);
            barChart.setDoubleParked(doubleParked);
            barChart.setLookingForSpace(lookingForSpace);
            barChart.update();
        });
    }

    synchronized void leaveSpace(Car car) throws InterruptedException, InvocationTargetException {
        spaces.set(spaces.get() + car.getSpace());
        if(car.getSpace() > 1){
            doubleParked.decrementAndGet();
        }
        printStatus();
        SwingUtilities.invokeAndWait(() -> {
            barChart.setSpacesAvailable(spaces);
            barChart.setDoubleParked(doubleParked);
            barChart.update();
        });
        notify();

        car.setGotToPark(true);
        leaveCarpark(car);
    }
}
