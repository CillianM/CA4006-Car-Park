import javax.swing.*;
import java.util.Scanner;

public class Main extends JPanel {

    //private static final int TOTAL_CARS = 2000;
    //private static final int TOTAL_SPACES = 1000;
    private static final int TOTAL_ENTRANCES = 3;
    private static final int TOTAL_EXITS = 3;

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        System.out.println("Assignment specifies 2000 cars and 1000 spaces. " +
                "Our program handles this fine, though it is a little slow as we " +
                "implement travel time and park time");
        System.out.println("For a quicker run we recommend using 100 cars and 50 spaces.");
        System.out.print("Enter amount of cars: ");
        int totalCars = scan.nextInt();
        System.out.print("Enter amount of spaces: ");
        int totalSpaces = scan.nextInt();

        JFrame frame = new JFrame("Bar Chart");
        BarChart chart = new BarChart(totalCars, totalSpaces, TOTAL_ENTRANCES, TOTAL_EXITS);

        frame.getContentPane().add(chart);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        try {
            Thread.sleep(3000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        CarPark carPark = new CarPark(chart, totalCars, totalSpaces, TOTAL_ENTRANCES, TOTAL_EXITS);
        for (int i = 0; i < totalCars; i++) {
            new Person(carPark).start();

        }
    }
}
