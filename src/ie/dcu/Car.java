package ie.dcu;

public class Car {
    private int id;
    private double space;
    private boolean unlucky;

    Car(int id) {
        this.id = id;
        this.space = 1;
        this.unlucky = Math.random() >= 0.8;
        if (Math.random() >= 0.6) {
            this.space = 1.5;
        }
    }

    public double getSpace() {
        return space;
    }

    public void setSpace(double space) {
        this.space = space;
    }

    public boolean isUnlucky() {
        return unlucky;
    }

    public void setUnlucky(boolean unlucky) {
        this.unlucky = unlucky;
    }
}
