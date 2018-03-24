package ie.dcu;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class BarChart extends JPanel {
    private final int TOTAL_SPACES;
    private final int TOTAL_CARS;
    private final int NUM_OF_MULT_BARS = 2;

    private Map<String, Bar> bars =
            new LinkedHashMap<>();
    private Map<String, Bar> spacesMultiBar =
            new LinkedHashMap<>();
    private Map<String, Bar> exitedMultiBar =
            new LinkedHashMap<>();


    private AtomicInteger goingIn;
    private AtomicInteger entrancesFree;
    private AtomicInteger spacesAvailable;
    private AtomicInteger doubleParked;
    private AtomicInteger singleParked;
    private AtomicInteger seekingSpace;
    private AtomicInteger totalInCarPark;
    private AtomicInteger exitsFree;
    private AtomicInteger undelayedExit;
    private AtomicInteger delayedExit;


    BarChart(int totalCars, int totalSpaces, int totalEntrances, int totalExits) {
        this.TOTAL_CARS = totalCars;
        this.TOTAL_SPACES = totalSpaces;
        this.goingIn = new AtomicInteger(totalCars);
        this.spacesAvailable = new AtomicInteger(totalSpaces);
        this.singleParked = new AtomicInteger(0);
        this.doubleParked = new AtomicInteger(0);
        this.entrancesFree = new AtomicInteger(totalEntrances);
        this.totalInCarPark = new AtomicInteger(0);
        this.seekingSpace = new AtomicInteger(0);
        this.exitsFree = new AtomicInteger(totalExits);
        this.undelayedExit = new AtomicInteger(0);
        this.delayedExit = new AtomicInteger(0);

        //Spaces multi-bar
        addBar("Spaces Free", Color.cyan, spacesAvailable, spacesMultiBar);
        addBar("Single Parked", Color.pink, singleParked, spacesMultiBar);
        addBar("Double Parked", Color.magenta, doubleParked, spacesMultiBar);

        //Single Bars
        addBar("Going In", Color.lightGray, goingIn);
        addBar("Entrances Free", Color.green, entrancesFree);
        addBar("Total In CarPark", Color.blue, totalInCarPark);
        addBar("Seeking Space",Color.orange, seekingSpace);
        addBar("Exits Free",Color.red, exitsFree);

        //Exited multi-bar
        addBar("Undelayed Exit",Color.black, undelayedExit, exitedMultiBar);
        addBar("Delayed Exit", Color.gray, delayedExit, exitedMultiBar);
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
        //Spaces multi-bar
        updateBar("Spaces Free", Color.cyan, spacesAvailable, spacesMultiBar);
        updateBar("Single Parked", Color.pink, singleParked, spacesMultiBar);
        updateBar("Double Parked", Color.magenta, doubleParked, spacesMultiBar);

        //Single Bars
        updateBar("Going In", Color.lightGray, goingIn);
        updateBar("Entrances Free", Color.green, entrancesFree);
        updateBar("Total In CarPark", Color.blue, totalInCarPark);
        updateBar("Seeking Space",Color.orange, seekingSpace);
        updateBar("Exits Free", Color.red, exitsFree);

        //Exited multi-bar
        updateBar("Undelayed Exit", Color.black, undelayedExit, exitedMultiBar);
        updateBar("Delayed Exit", Color.gray, delayedExit, exitedMultiBar);
        repaint();
    }

    private int getBarHeight(Bar bar, int windowHeight, int fontHeight){
        return (int) ((windowHeight - (4 * fontHeight)) * ((double) bar.getCount() / TOTAL_CARS));
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
        int width = (getWidth() / (bars.size() + NUM_OF_MULT_BARS) - 2);
        int xOffset = width + 2;
        int windowHeight = getHeight();
        int fontHeight = g.getFontMetrics().getHeight();
        int x = 1;
        int y;
        int barHeight;

        //Draw spaces multi-bar
        drawMultiBar(g, spacesMultiBar, x, windowHeight, width, fontHeight);
        x += xOffset;

        //Draw the single bars
        for (Bar bar : bars.values()) {
            barHeight = getBarHeight(bar, windowHeight, fontHeight);
            y = windowHeight - barHeight - fontHeight;
            drawBar(g, bar, x, y, width, barHeight);
            x += xOffset;
        }

        //Draw exited multi-bar
        drawMultiBar(g, exitedMultiBar, x, windowHeight, width, fontHeight);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(800, 800);
    }

    public synchronized void setGoingIn(AtomicInteger goingIn) {
        this.goingIn = goingIn;
    }

    public synchronized void setEntrancesFree(AtomicInteger entrancesFree) {
        this.entrancesFree = entrancesFree;
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

    public synchronized void setSeekingSpace(AtomicInteger seekingSpace) {
        this.seekingSpace = seekingSpace;
    }

    public synchronized void setTotalInCarPark(AtomicInteger totalInCarPark) {
        this.totalInCarPark = totalInCarPark;
    }

    public synchronized void setExitsFree(AtomicInteger exitsFree) {
        this.exitsFree = exitsFree;
    }

    public synchronized void setUndelayedExit(AtomicInteger undelayedExit) {
        this.undelayedExit = undelayedExit;
    }

    public synchronized void setDelayedExit(AtomicInteger delayedExit) {
        this.delayedExit = delayedExit;
    }
}