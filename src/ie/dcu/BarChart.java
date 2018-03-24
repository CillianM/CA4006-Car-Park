package ie.dcu;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class BarChart extends JPanel {
    private final int TOTAL_SPACES;
    private final int TOTAL_CARS;

    private Map<String, Bar> bars =
            new LinkedHashMap<>();
    private Map<String, Bar> spacesMultiBar =
            new LinkedHashMap<>();


    private AtomicInteger goingIn;
    private AtomicInteger entrance;
    private AtomicInteger spacesAvailable;
    private AtomicInteger doubleParked;
    private AtomicInteger singleParked;
    private AtomicInteger lookingForSpace;
    private AtomicInteger totalInCarPark;
    private AtomicInteger exit;
    private AtomicInteger gone;


    BarChart(int totalCars, int totalSpaces, int entrances, int exits) {
        this.TOTAL_CARS = totalCars;
        this.TOTAL_SPACES = totalSpaces;
        this.goingIn = new AtomicInteger(totalCars);
        this.spacesAvailable = new AtomicInteger(totalSpaces);
        this.singleParked = new AtomicInteger(0);
        this.doubleParked = new AtomicInteger(0);
        this.entrance = new AtomicInteger(entrances);
        this.totalInCarPark = new AtomicInteger(0);
        this.lookingForSpace = new AtomicInteger(0);
        this.exit = new AtomicInteger(exits);
        this.gone = new AtomicInteger(0);
        addBar("Going In", Color.lightGray, goingIn);
        addBar("Entrances Free", Color.green, entrance);
        addBar("Total In CarPark", Color.blue, totalInCarPark);
        addBar("Spaces Free", Color.cyan, spacesAvailable, spacesMultiBar);
        addBar("Single Parked", Color.pink, singleParked, spacesMultiBar);
        addBar("Double Parked", Color.magenta, doubleParked, spacesMultiBar);
        addBar("Looking For Space",Color.orange, lookingForSpace);
        addBar("Exits Free",Color.red, exit);
        addBar("Gone",Color.black, gone);
    }

    private void addBar(String label, Color color, AtomicInteger count, Map<String, Bar> barMap){
        barMap.put(label, new Bar(label,color, count));
        repaint();
    }

    private void addBar(String label, Color color, AtomicInteger count) {
        addBar(label,color, count, bars);
    }

    private void updateBar(String label, Color color, AtomicInteger count, Map<String, Bar> barMap){
        Bar bar = barMap.get(label);
        if(bar == null){
            addBar(label, color, count, barMap);
        } else {
            bar.setColor(color);
            bar.setCount(count);
            repaint();
        }
    }

    private void updateBar(String label, Color color, AtomicInteger count) {
        updateBar(label,color,count,bars);
    }

    public void update() {
        removeAll();
        updateBar("Going In", Color.lightGray, goingIn);
        updateBar("Entrances Free", Color.green, entrance);
        updateBar("Total In CarPark", Color.blue, totalInCarPark);
        updateBar("Spaces Free", Color.cyan, spacesAvailable, spacesMultiBar);
        updateBar("Single Parked", Color.pink, singleParked, spacesMultiBar);
        updateBar("Double Parked", Color.magenta, doubleParked, spacesMultiBar);
        updateBar("Looking For Space",Color.orange, lookingForSpace);
        updateBar("Exits Free", Color.red, exit);
        updateBar("Gone", Color.black, gone);
        repaint();
    }

    private int getBarHeight(Bar bar, int windowHeight, int fontHeight){
        return (int) ((windowHeight - (2 * fontHeight)) * ((double) bar.getCount() / TOTAL_CARS));
    }

    private void drawBar(Graphics g, Bar bar, int x, int y, int width, int barHeight){
        g.setColor(bar.getColor());
        g.fillRect(x, y, width, barHeight);
        g.setColor(Color.black);
        g.drawString(bar.getLabel() + ": " + bar.getCount(), x, y); //getHeight() - fontHeight
        g.drawRect(x, y, width, barHeight);
    }

    private void drawMultiBar(Graphics g, Map<String, Bar> barMap,
                              int x, int windowHeight, int width, int fontHeight){
        int oldHeight = windowHeight;
        for (Bar multiBar : barMap.values()) {
            int barHeight = getBarHeight(multiBar, windowHeight, fontHeight);
            int y = oldHeight - barHeight - fontHeight;
            drawBar(g, multiBar, x, y, width, barHeight);
            oldHeight = y;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int width = (getWidth() / (bars.size() + 1) - 2);
        int xOffset = width + 10;
        int windowHeight = getHeight();
        int fontHeight = g.getFontMetrics().getHeight();
        int x = 1;
        int y;
        int barHeight;

        //Draw spaces multibar
        drawMultiBar(g, spacesMultiBar, x, windowHeight, width, fontHeight);
        x += xOffset;

        //Draw the single bars
        for (Bar bar : bars.values()) {
            barHeight = getBarHeight(bar, windowHeight, fontHeight);
            y = windowHeight - barHeight - fontHeight;
            drawBar(g, bar, x, y, width, barHeight);
            x += xOffset;
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

    public synchronized void setDoubleParked(AtomicInteger doubleParked){
        this.doubleParked = doubleParked;
    }

    public synchronized void setSingleParked(AtomicInteger singleParked){
        this.singleParked = singleParked;
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