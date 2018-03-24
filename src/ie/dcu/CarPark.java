package ie.dcu;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class CarPark {

    //The standard time it takes to travel through an entrance or exit with no problems
    private static final long BARRIER_TRAVEL_TIME = 500;

    //The time a person will seek a space until getting fed up and exiting immediately without parking
    private static final long FED_UP_TIME = 750;

    private BarChart barChart;
    private AtomicInteger outside;
    private AtomicInteger undelayedExit = new AtomicInteger(0);
    private AtomicInteger delayedExit = new AtomicInteger(0);
    private AtomicInteger spaces;
    private AtomicInteger doubleParked = new AtomicInteger(0);
    private AtomicInteger singleParked = new AtomicInteger(0);
    private AtomicInteger seekingSpace = new AtomicInteger(0);
    private AtomicInteger totalFedUp = new AtomicInteger(0);
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
                "Single Parked: " + singleParked + " " +
                "Seeking Spaces: " + seekingSpace + " " +
                "Total Fed Up: " + totalFedUp + " " +
                "Exits: " + exits + " " +
                "Undelayed Exit: " + undelayedExit + " " +
                "Delayed Exit: " + delayedExit);
    }

    private synchronized void updateGUI() throws InvocationTargetException, InterruptedException {
        SwingUtilities.invokeAndWait(() -> {
            barChart.setGoingIn(outside);
            barChart.setUndelayedExit(undelayedExit);
            barChart.setDelayedExit(delayedExit);
            barChart.setSpacesAvailable(spaces);
            barChart.setDoubleParked(doubleParked);
            barChart.setSingleParked(singleParked);
            barChart.setSeekingSpace(seekingSpace);
            barChart.setTotalFedUp(totalFedUp);
            barChart.setTotalInCarPark(totalInCarPark);
            barChart.setEntrancesFree(entrances);
            barChart.setExitsFree(exits);
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
        seekingSpace.incrementAndGet();
        entrances.incrementAndGet();
        printStatus();
        updateGUI();
        entranceSem.release();

        takeSpace(car);
    }

    private long calculateTimeToLeaveCarPark(Car car){
        if(car.isUnlucky()){
            //This simulates the driver taking a longer time than usual to leave,
            //for example there is an issue with their ticket and they have to
            //go to the security office
            return BARRIER_TRAVEL_TIME * (long)(Math.random() * 10);
        } else {
            return BARRIER_TRAVEL_TIME;
        }
    }

    void leaveCarpark(Car car) throws InterruptedException, InvocationTargetException {

        exitSem.acquire();
        exits.decrementAndGet();
        printStatus();
        updateGUI();
        long timeToLeaveCarPark = calculateTimeToLeaveCarPark(car);
        Thread.sleep(timeToLeaveCarPark); //make using the exit non-instantaneous
        exits.incrementAndGet();
        totalInCarPark.decrementAndGet();
        if(timeToLeaveCarPark > BARRIER_TRAVEL_TIME){
            delayedExit.incrementAndGet();
        } else {
            undelayedExit.incrementAndGet();
        }
        printStatus();
        updateGUI();
        exitSem.release();
    }

    private void takeSpace(Car car) throws InterruptedException, InvocationTargetException {
        //Search for a space
        if(spacesSem.tryAcquire(car.getSpace(), FED_UP_TIME, TimeUnit.MILLISECONDS))
        {
            //Found a space
            car.setGotToPark(true);
            spaces.set(spaces.get() - car.getSpace());
            if(car.getSpace() > 1){
                doubleParked.incrementAndGet();
            } else {
                singleParked.incrementAndGet();
            }
        } else {
            //After failing to acquire the semaphore before the FED_UP_TIME ran out
            //give up on trying to find a space and just leave
            car.setGotToPark(false);
            totalFedUp.incrementAndGet();

        }
        seekingSpace.decrementAndGet();
        printStatus();
        updateGUI();
    }

    void leaveSpace(Car car) throws InterruptedException, InvocationTargetException {
        spacesSem.release(car.getSpace());
        spaces.set(spaces.get() + car.getSpace());
        if(car.getSpace() > 1){
            doubleParked.decrementAndGet();
        } else {
            singleParked.decrementAndGet();
        }
        printStatus();
        updateGUI();
    }
}
