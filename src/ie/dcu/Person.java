package ie.dcu;

import java.util.Random;

public class Person extends Thread {
    private CarPark carPark;
    private Car car;
    private Random random;

    public Person(CarPark carPark, int id) {
        super("Person_" + id);
        this.carPark = carPark;
        car = new Car(id);
        random = new Random();
    }

    @Override
    public void run() {
        try {
            carPark.takeSpace(car.getSpace());
            sleep((long) 1000 * random.nextInt(15));
            if (car.isUnlucky()) {
                sleep((long) 1000 * random.nextInt(5));
            }
            carPark.leaveSpace(car.getSpace());
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
