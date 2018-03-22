package ie.dcu;

import javax.swing.*;

public class CarPark {

    private BarChart barChart;
    private int outside;
    private int gone;
    private int spaces = 50;
    private int lookingForSpace = 0;
    private int totalInCarPark = 0;
    private int entrances = 3;
    private int exits = 3;

    CarPark(BarChart barChart, int outside) {
        this.barChart = barChart;
        this.outside = outside;
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

    public synchronized void enterCarpark(Car car) throws InterruptedException {
        while (entrances == 0) {
            wait();
        }
        entrances--;
        outside--;
        totalInCarPark++;
        lookingForSpace++;
        printStatus();

        SwingUtilities.invokeLater(() -> {
            barChart.setGoingIn(outside);
            barChart.setTotalInCarPark(totalInCarPark);
            barChart.setLookingForSpace(lookingForSpace);
            barChart.setEntrance(entrances);
            barChart.update();
        });

        wait(2000);
        entrances++;
        printStatus();
        SwingUtilities.invokeLater(() -> {
            barChart.setEntrance(entrances);
            barChart.update();
        });
        notifyAll();


        takeSpace(car);
    }

    private synchronized void leaveCarpark(Car car) throws InterruptedException {
        while (exits == 0) {
            wait();
        }
        exits--;
        printStatus();
        SwingUtilities.invokeLater(() -> {
            barChart.setExit(exits);
            barChart.update();
        });

        wait(2000);
        exits++;
        totalInCarPark--;
        gone++;
        printStatus();
        SwingUtilities.invokeLater(() -> {
            barChart.setGone(gone);
            barChart.setTotalInCarPark(totalInCarPark);
            barChart.setExit(exits);
            barChart.update();
        });
        notifyAll();
    }

    private synchronized void takeSpace(Car car) throws InterruptedException {
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
        SwingUtilities.invokeLater(() -> {
            barChart.setSpacesAvailable(spaces);
            barChart.setLookingForSpace(lookingForSpace);
            barChart.update();
        });
    }

    synchronized void leaveSpace(Car car) throws InterruptedException {
        spaces += car.getSpace();
        printStatus();
        SwingUtilities.invokeLater(() -> {
            barChart.setSpacesAvailable(spaces);
            barChart.update();
        });
        notifyAll();

        car.setGotToPark(true);
        leaveCarpark(car);
    }
}
