package ie.dcu;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class BarChart extends JPanel {
    private Map<String, Bar> bars =
            new LinkedHashMap<>();

    private int goingIn;
    private int entrance;
    private int spacesAvailable;
    private int exit;
    private int gone;
    private int max;

    BarChart(int total) {
        max = total;
        goingIn = total;
        entrance = spacesAvailable = exit = gone = 0;
        addBar("Going In", Color.lightGray, total);
        addBar("Entrance", Color.green, 0);
        addBar("Spaces Available",Color.cyan, total);
        addBar("Exit",Color.red, 0);
        addBar("Gone",Color.black, 0);
    }

    private void addBar(String label, Color color, int count) {
        bars.put(label, new Bar(label,color, count));
        repaint();
    }

    private void updateBar(String label, Color color, int count) {
        Bar bar = bars.get(label);
        bar.setColor(color);
        bar.setCount(count);
        repaint();
    }

    public void update() {
        removeAll();
        //bars.clear();
        updateBar("Going In", Color.lightGray, goingIn);
        updateBar("Entrance", Color.green, entrance);
        updateBar("Spaces Available", Color.cyan, spacesAvailable);
        updateBar("Exit", Color.red, exit);
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
            int height = (int) ((getHeight() - 5) * ((double) value / max));
            g.setColor(bar.getColor());
            g.fillRect(x, getHeight() - height, width, height);
            g.drawString(bar.getLabel(), x, getHeight()-height);
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

    public synchronized void setSpacesAvailable(int spacesAvailable) {
        this.spacesAvailable = spacesAvailable;
    }

    public synchronized void setExit(int exit) {
        this.exit = exit;
    }

    public synchronized void setGone(int gone) {
        this.gone = gone;
    }
}