package ie.dcu;

import javax.swing.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CarPark {

    private BarChart barChart;
    private int outside;
    private int gone;
    private int spaces = 50;
    private int entrances = 3;
    private int exits = 3;
    private final Lock exitLock = new ReentrantLock();
    private final Lock entranceLock = new ReentrantLock();
    private final Lock lock = new ReentrantLock();
    private final Condition carparkNotFull = lock.newCondition();
    private final Condition carparkNotEmpty = lock.newCondition();
    private final Condition entrancesNotFull = entranceLock.newCondition();
    private final Condition entrancesNotEmpty = entranceLock.newCondition();
    private final Condition exitsNotFull = exitLock.newCondition();
    private final Condition exitsNotEmpty = exitLock.newCondition();

    CarPark(BarChart barChart, int outside) {
        this.barChart = barChart;
        this.outside = outside;
        gone = 0;
    }

    public void printStatus(){
        System.out.println("Outside: " + outside + " " + "Entrances: " + entrances + " " + "Spaces: " + spaces + " " + "Exits: " + exits + " " + "Gone: " + gone);
    }

    public void enterCarpark(Car car) {
        entranceLock.lock();
        try {
            while (entrances == 0) {
                //System.out.println(Thread.currentThread().getName() + " : No free entrances, waiting");
                entrancesNotFull.await();
            }
            entrances--;
            outside--;
            printStatus();

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    barChart.setGoingIn(outside);
                    barChart.setEntrance(entrances);
                    barChart.update();
                }
            });

            entrancesNotEmpty.signalAll();
            //System.out.println(Thread.currentThread().getName() + " using entrance, " + entrances + " entrances left");
            awaitSpace(car);
            entrances++;
            printStatus();
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    barChart.setEntrance(entrances);
                    barChart.update();
                }
            });

            entrancesNotFull.signalAll();
            //System.out.println(Thread.currentThread().getName() + " entered carpark ");
            entranceLock.unlock();
            takeSpace(car);


        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void leaveCarpark(Car car) {
        exitLock.lock();
        try {
            while (exits == 0) {
                //System.out.println(Thread.currentThread().getName() + " : No free exits, waiting");
                exitsNotFull.await();
            }
            exits--;
            printStatus();
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    barChart.setExit(exits);
                    barChart.update();
                }
            });

            exitsNotEmpty.signalAll();
            //System.out.println(Thread.currentThread().getName() + " using exit, " + exits + " exits left");
            exits++;
            gone++;
            printStatus();
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    barChart.setGone(gone);
                    barChart.setExit(exits);
                    barChart.update();
                }
            });

            exitsNotFull.signalAll();
            //System.out.println(Thread.currentThread().getName() + " left carpark ");

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            exitLock.unlock();
        }
    }

    //Don't let them in until there is a space available
    private void awaitSpace(Car car) {
        lock.lock();
        try {
            while (spaces <= car.getSpace()) {
                //System.out.println(Thread.currentThread().getName() + " : Not enough spaces");
                carparkNotFull.await();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    private void takeSpace(Car car) {
        lock.lock();
        try {
            spaces -= car.getSpace();
            printStatus();
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    barChart.setSpacesAvailable(spaces);
                    barChart.update();
                }
            });

            //System.out.println(Thread.currentThread().getName() + " took a space, " + spaces + " spaces left");
            carparkNotEmpty.signalAll();

        } finally {
            lock.unlock();
        }
    }

    void leaveSpace(Car car) {

        lock.lock();
        try {
            spaces += car.getSpace();
            printStatus();
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    barChart.setSpacesAvailable(spaces);
                    barChart.update();
                }
            });

            //System.out.println(Thread.currentThread().getName() + " left their space, " + spaces + " spaces left");
            carparkNotFull.signalAll();
            car.setGotToPark(true);
            leaveCarpark(car);
        } finally {
            lock.unlock();
        }
    }
}
