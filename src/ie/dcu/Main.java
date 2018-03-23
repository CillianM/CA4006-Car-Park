package ie.dcu;

import javax.swing.*;

public class Main extends JPanel {

    private static final int TOTAL_CARS = 100;
    private static final int TOTAL_SPACES = 50;
    private static final int TOTAL_ENTRANCES = 3;
    private static final int TOTAL_EXITS = 3;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Bar Chart");
        BarChart chart = new BarChart(TOTAL_CARS, TOTAL_SPACES, TOTAL_ENTRANCES, TOTAL_EXITS);

        frame.getContentPane().add(chart);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        try {
            Thread.sleep(3000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        CarPark carPark = new CarPark(chart, TOTAL_CARS, TOTAL_SPACES, TOTAL_ENTRANCES, TOTAL_EXITS);
        for (int i = 0; i < TOTAL_CARS; i++) {
            new Person(carPark).start();

        }
    }
}
