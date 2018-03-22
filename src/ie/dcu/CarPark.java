package ie.dcu;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

public class CarPark {

    private BarChart barChart;
    private int outside;
    private int gone;
    private int spaces;
    private int lookingForSpace = 0;
    private int totalInCarPark = 0;
    private int entrances = 3;
    private int exits = 3;

    CarPark(BarChart barChart, int outside, int spaces) {
        this.barChart = barChart;
        this.outside = outside;
        this.spaces = spaces;
        gone = 0;
    }

    private void printStatus() {
        System.out.println("Outside: " + outside + " " +
                "Entrances: " + entrances + " " +
                "Total in CarPark: " + totalInCarPark + " " +
                "Spaces: " + spaces + " " +
                "Looking For Spaces: " + lookingForSpace + " " +
                "Exits: " + exits + " " +
                "Gone: " + gone);
    }

    public synchronized void enterCarpark(Car car) throws InterruptedException, InvocationTargetException {
        while (entrances == 0) {
            wait();
        }
        entrances--;
        outside--;
        totalInCarPark++;
        lookingForSpace++;
        printStatus();

        SwingUtilities.invokeAndWait(() -> {
            barChart.setGoingIn(outside);
            barChart.setTotalInCarPark(totalInCarPark);
            barChart.setLookingForSpace(lookingForSpace);
            barChart.setEntrance(entrances);
            barChart.update();
        });

        wait(2000);
        entrances++;
        printStatus();
        SwingUtilities.invokeAndWait(() -> {
            barChart.setEntrance(entrances);
            barChart.update();
        });
        notify();


        takeSpace(car);
    }

    private synchronized void leaveCarpark(Car car) throws InterruptedException, InvocationTargetException {
        while (exits == 0) {
            wait();
        }
        exits--;
        printStatus();
        SwingUtilities.invokeAndWait(() -> {
            barChart.setExit(exits);
            barChart.update();
        });

        wait(2000);
        exits++;
        totalInCarPark--;
        gone++;
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
        while (spaces < car.getSpace()) {
            car.setAttempts(car.getAttempts() - 1);
            if (car.getAttempts() <= 0) {
                //TODO implement giving up on parking
            }
            wait();
        }
        spaces -= car.getSpace();
        lookingForSpace--;
        printStatus();
        SwingUtilities.invokeAndWait(() -> {
            barChart.setSpacesAvailable(spaces);
            barChart.setLookingForSpace(lookingForSpace);
            barChart.update();
        });
    }

    synchronized void leaveSpace(Car car) throws InterruptedException, InvocationTargetException {
        spaces += car.getSpace();
        printStatus();
        SwingUtilities.invokeAndWait(() -> {
            barChart.setSpacesAvailable(spaces);
            barChart.update();
        });
        notify();

        car.setGotToPark(true);
        leaveCarpark(car);
    }
}
