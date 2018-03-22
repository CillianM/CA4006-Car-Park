package ie.dcu;

import java.lang.reflect.InvocationTargetException;
import java.util.Random;

public class Person extends Thread {
    private CarPark carPark;
    private Car car;
    private Random random;

    Person(CarPark carPark) {
        this.carPark = carPark;
        car = new Car();
        random = new Random();
    }

    @Override
    public void run() {
        try {
            carPark.enterCarpark(car);
            sleep((long) 1000 * random.nextInt(15));
            carPark.leaveSpace(car);
        } catch (InterruptedException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
