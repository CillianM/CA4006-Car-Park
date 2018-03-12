package ie.dcu;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class BarChart extends JPanel {
    private Map<Color, Integer> bars =
            new LinkedHashMap<>();

    private int goingIn;
    private int entrance;
    private int parked;
    private int exit;
    private int gone;
    private int max;

    BarChart(int total) {
        max = total;
        goingIn = total;
        entrance = parked = exit = gone = 0;
        addBar(Color.lightGray, total);
        addBar(Color.green, 0);
        addBar(Color.cyan, total);
        addBar(Color.red, 0);
        addBar(Color.black, 0);
    }

    private void addBar(Color color, int value) {
        bars.put(color, value);
        repaint();
    }

    public void update() {
        removeAll();
        bars.clear();
        addBar(Color.lightGray, goingIn);
        addBar(Color.green, entrance);
        addBar(Color.cyan, parked);
        addBar(Color.red, exit);
        addBar(Color.black, gone);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int width = (getWidth() / bars.size()) - 2;
        int x = 1;
        for (Color color : bars.keySet()) {
            int value = bars.get(color);
            int height = (int) ((getHeight() - 5) * ((double) value / max));
            g.setColor(color);
            g.fillRect(x, getHeight() - height, width, height);
            g.setColor(Color.black);
            g.drawRect(x, getHeight() - height, width, height);
            x += (width + 2);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(800, 800);
    }

    public synchronized void setGoingIn(int goingIn) {
        this.goingIn = goingIn;
    }

    public synchronized void setEntrance(int entrance) {
        this.entrance = entrance;
    }

    public synchronized void setParked(int parked) {
        this.parked = parked;
    }

    public synchronized void setExit(int exit) {
        this.exit = exit;
    }

    public synchronized void setGone(int gone) {
        this.gone = gone;
    }
}