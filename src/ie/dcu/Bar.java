package ie.dcu;

import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Bar {
    private String label;
    private Color color;
    private AtomicInteger count;

    Bar(String label, Color color){
        this(label,color, new AtomicInteger(0));
    }

    Bar(String label, Color color, AtomicInteger count){
        setLabel(label);
        setColor(color);
        setCount(count);
    }

    public void setLabel(String label){
        this.label = label;
    }

    public void setColor(Color color){
        this.color = color;
    }

    public void setCount(AtomicInteger count){
        this.count = count;
    }

    public String getLabel() {
        return this.label;
    }

    public Color getColor() {
        return this.color;
    }

    public int getCount() {
        return this.count.get();
    }
}
