package ie.dcu;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class CarPark {

    //The standard time it takes to travel through an entrance or exit with no problems
    private static final long BARRIER_TRAVEL_TIME = 500;

    private BarChart barChart;
    private AtomicInteger outside;
    private AtomicInteger gone = new AtomicInteger(0);
    private AtomicInteger spaces;
    private AtomicInteger doubleParked = new AtomicInteger(0);
    private AtomicInteger lookingForSpace = new AtomicInteger(0);
    private AtomicInteger totalInCarPark = new AtomicInteger(0);
    private AtomicInteger entrances;
    private AtomicInteger exits;

    private final Semaphore entranceSem;
    private final Semaphore exitSem;
    private final Semaphore spacesSem;

    CarPark(BarChart barChart, int outside, int spaces, int entrances, int exits) {
        this.barChart = barChart;
        this.outside = new AtomicInteger(outside);
        this.spaces = new AtomicInteger(spaces);
        this.entrances = new AtomicInteger(entrances);
        this.exits = new AtomicInteger(exits);

        //use fair semaphores to enable FIFO use of entrances and exits
        this.entranceSem = new Semaphore(entrances, true);
        this.exitSem = new Semaphore(exits, true);

        //Spaces don't need to be fair, it's a free for all
        this.spacesSem = new Semaphore(spaces);
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

    private synchronized void updateGUI() throws InvocationTargetException, InterruptedException {
        SwingUtilities.invokeAndWait(() -> {
            barChart.setGoingIn(outside);
            barChart.setGone(gone);
            barChart.setSpacesAvailable(spaces);
            barChart.setDoubleParked(doubleParked);
            barChart.setLookingForSpace(lookingForSpace);
            barChart.setTotalInCarPark(totalInCarPark);
            barChart.setEntrance(entrances);
            barChart.setExit(exits);
            barChart.update();
        });
    }

    public void enterCarpark(Car car) throws InterruptedException, InvocationTargetException {

        entranceSem.acquire();

        entrances.decrementAndGet();
        outside.decrementAndGet();
        Thread.sleep(BARRIER_TRAVEL_TIME); //make using the entrance non-instantaneous
        totalInCarPark.incrementAndGet();
        printStatus();
        updateGUI();
        lookingForSpace.incrementAndGet();
        entrances.incrementAndGet();
        printStatus();
        updateGUI();
        entranceSem.release();

        takeSpace(car);
    }

    private long calculateTimeToLeaveCarpark(Car car){
        if(car.isUnlucky()){
            //This simulates the driver taking a longer time than usual to leave,
            //for example there is an issue with their ticket and they have to
            //go to the security office
            return BARRIER_TRAVEL_TIME * (long)(Math.random() * 10);
        } else {
            return BARRIER_TRAVEL_TIME;
        }
    }

    private void leaveCarpark(Car car) throws InterruptedException, InvocationTargetException {

        exitSem.acquire();
        exits.decrementAndGet();
        printStatus();
        updateGUI();
        Thread.sleep(calculateTimeToLeaveCarpark(car)); //make using the exit non-instantaneous
        exits.incrementAndGet();
        totalInCarPark.decrementAndGet();
        gone.incrementAndGet();
        printStatus();
        updateGUI();
        exitSem.release();
    }

    private void takeSpace(Car car) throws InterruptedException, InvocationTargetException {
//        while (spaces.get() < car.getSpace()) {
//            car.setAttempts(car.getAttempts() - 1);
//            if (car.getAttempts() <= 0) {
//                //TODO implement giving up on parking
//            }
//            wait();
//        }

        spacesSem.acquire(car.getSpace());
        spaces.set(spaces.get() - car.getSpace());
        if(car.getSpace() > 1){
            doubleParked.incrementAndGet();
        }
        lookingForSpace.decrementAndGet();
        printStatus();
        updateGUI();
    }

    void leaveSpace(Car car) throws InterruptedException, InvocationTargetException {
        spacesSem.release(car.getSpace());
        spaces.set(spaces.get() + car.getSpace());
        if(car.getSpace() > 1){
            doubleParked.decrementAndGet();
        }
        printStatus();
        updateGUI();

        car.setGotToPark(true);
        leaveCarpark(car);
    }
}
