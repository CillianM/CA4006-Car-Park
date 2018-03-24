package ie.dcu;

public class Car {
    private int space;
    private boolean unlucky;
    private boolean gotToPark;

    Car() {
        this.space = 1;
        this.unlucky = Math.random() >= 0.8;
        if (Math.random() >= 0.6) {
            this.space = 2;
        }
    }

    public int getSpace() {
        return space;
    }


    public boolean isUnlucky() {
        return unlucky;
    }

    public boolean isGotToPark() {
        return gotToPark;
    }

    public void setGotToPark(boolean gotToPark) {
        this.gotToPark = gotToPark;
    }
}
