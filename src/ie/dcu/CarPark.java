package ie.dcu;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CarPark {

    private double spaces = 1000;
    private final Lock lock = new ReentrantLock();
    private final Condition carparkNotFull = lock.newCondition();
    private final Condition carparkNotEmpty = lock.newCondition();

    CarPark() {
    }

    public void takeSpace(double value) {
        lock.lock();
        try {
            while (spaces <= value) {
                System.out.println(Thread.currentThread().getName() + " : Not enough spaces");
                carparkNotFull.await();
            }

            spaces -= value;
            System.out.println(Thread.currentThread().getName() + " took a space, " + spaces + " spaces left");
            carparkNotEmpty.signalAll();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void leaveSpace(double value) {

        lock.lock();
        try {
            spaces += value;
            System.out.println(Thread.currentThread().getName() + " left their space, " + spaces + " spaces left");
            carparkNotFull.signalAll();
        } finally {
            lock.unlock();
        }
    }
}
