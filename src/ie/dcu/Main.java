package ie.dcu;

public class Main {

    private static final int TOTAL_CARS = 1500;

    public static void main(String[] args) {
        CarPark carPark = new CarPark();
        for (int i = 0; i < TOTAL_CARS; i++) {
            new Person(carPark, i).start();

        }
    }
}
