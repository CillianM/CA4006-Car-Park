package ie.dcu;

import javax.swing.*;

public class Main extends JPanel {

    private static final int TOTAL_CARS = 100;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Bar Chart");
        BarChart chart = new BarChart(TOTAL_CARS);

        frame.getContentPane().add(chart);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        try {
            Thread.sleep(3000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        CarPark carPark = new CarPark(chart, TOTAL_CARS);
        for (int i = 0; i < TOTAL_CARS; i++) {
            new Person(carPark).start();

        }
    }
}
