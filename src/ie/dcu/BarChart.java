package ie.dcu;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class BarChart extends JPanel {
    private Map<String, Bar> bars =
            new LinkedHashMap<>();

    private AtomicInteger goingIn;
    private AtomicInteger entrance;
    private AtomicInteger spacesAvailable;
    private AtomicInteger lookingForSpace;
    private AtomicInteger totalInCarPark;
    private AtomicInteger exit;
    private AtomicInteger gone;
    private int max; //Value does not change after being set once, no need for atomic

    BarChart(int totalCars, int totalSpaces) {
        max = totalCars;
        goingIn = new AtomicInteger(totalCars);
        spacesAvailable = new AtomicInteger(totalSpaces);
        entrance = new AtomicInteger(0);
        totalInCarPark = new AtomicInteger(0);
        lookingForSpace = new AtomicInteger(0);
        exit = new AtomicInteger(0);
        gone = new AtomicInteger(0);
        addBar("Going In", Color.lightGray, goingIn);
        addBar("Entrances Free", Color.green, entrance);
        addBar("Total In CarPark", Color.blue, totalInCarPark);
        addBar("Spaces Free", Color.cyan, spacesAvailable);
        addBar("Looking For Space",Color.orange, lookingForSpace);
        addBar("Exits Free",Color.red, exit);
        addBar("Gone",Color.black, gone);
    }

    private void addBar(String label, Color color, AtomicInteger count) {
        bars.put(label, new Bar(label,color, count));
        repaint();
    }

    private void updateBar(String label, Color color, AtomicInteger count) {
        Bar bar = bars.get(label);
        if(bar == null){
            addBar(label, color, count);
        } else {
            bar.setColor(color);
            bar.setCount(count);
            repaint();
        }
    }

    public void update() {
        removeAll();
        updateBar("Going In", Color.lightGray, goingIn);
        updateBar("Entrances Free", Color.green, entrance);
        updateBar("Total In CarPark", Color.blue, totalInCarPark);
        updateBar("Spaces Free", Color.cyan, spacesAvailable);
        updateBar("Looking For Space",Color.orange, lookingForSpace);
        updateBar("Exits Free", Color.red, exit);
        updateBar("Gone", Color.black, gone);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int width = (getWidth() / bars.size()) - 2;
        int x = 1;
        for (Bar bar : bars.values()) {
            int value = bar.getCount();
            int height = (int) ((getHeight() - 10) * ((double) value / max));
            g.setColor(bar.getColor());
            g.fillRect(x, getHeight() - height, width, height);
            g.setColor(Color.black);
            g.drawString(bar.getLabel() + ": " + bar.getCount(), x, getHeight()-height -1);
            g.drawRect(x, getHeight() - height, width, height);
            x += (width + 10);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(800, 800);
    }

    public synchronized void setGoingIn(AtomicInteger goingIn) {
        this.goingIn = goingIn;
    }

    public synchronized void setEntrance(AtomicInteger entrance) {
        this.entrance = entrance;
    }

    public synchronized void setSpacesAvailable(AtomicInteger spacesAvailable) {
        this.spacesAvailable = spacesAvailable;
    }

    public synchronized void setLookingForSpace(AtomicInteger lookingForSpace) {
        this.lookingForSpace = lookingForSpace;
    }

    public synchronized void setTotalInCarPark(AtomicInteger totalInCarPark) {
        this.totalInCarPark = totalInCarPark;
    }

    public synchronized void setExit(AtomicInteger exit) {
        this.exit = exit;
    }

    public synchronized void setGone(AtomicInteger gone) {
        this.gone = gone;
    }
}