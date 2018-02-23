package ie.dcu;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CarPark {

    private int spaces = 1000;
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

    CarPark() {
    }

    public void enterCarpark(Car car) {
        entranceLock.lock();
        try {
            while (entrances == 0) {
                System.out.println(Thread.currentThread().getName() + " : No free entrances, waiting");
                entrancesNotFull.await();
            }
            entrances--;

            entrancesNotEmpty.signalAll();
            System.out.println(Thread.currentThread().getName() + " using entrance, " + entrances + " entrances left");
            awaitSpace(car);
            entrances++;

            entrancesNotFull.signalAll();
            System.out.println(Thread.currentThread().getName() + " entered carpark ");
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
                System.out.println(Thread.currentThread().getName() + " : No free exits, waiting");
                exitsNotFull.await();
            }
            exits--;

            exitsNotEmpty.signalAll();
            System.out.println(Thread.currentThread().getName() + " using exit, " + exits + " exits left");
            exits++;

            exitsNotFull.signalAll();
            System.out.println(Thread.currentThread().getName() + " left carpark ");

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
                System.out.println(Thread.currentThread().getName() + " : Not enough spaces");
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

            System.out.println(Thread.currentThread().getName() + " took a space, " + spaces + " spaces left");
            carparkNotEmpty.signalAll();

        } finally {
            lock.unlock();
        }
    }

    void leaveSpace(Car car) {

        lock.lock();
        try {
            spaces += car.getSpace();

            System.out.println(Thread.currentThread().getName() + " left their space, " + spaces + " spaces left");
            carparkNotFull.signalAll();
            car.setGotToPark(true);
            leaveCarpark(car);
        } finally {
            lock.unlock();
        }
    }
}
