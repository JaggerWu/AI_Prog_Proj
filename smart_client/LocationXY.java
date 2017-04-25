package sampleclient;

/**
 * Created by ilma on 26/03/2017.
 */
public class LocationXY {
    private int X;
    private int Y;
    public LocationXY(int X, int Y) {
        this.X = X;
        this.Y = Y;
    }

    public LocationXY(LocationXY location){
        this.X = location.getX();
        this.Y = location.getY();
    }

    public int getY() {
        return Y;
    }

    public void setY(int Y) {
        this.Y = Y;
    }

    public int getX() {
        return X;
    }

    public void setX(int X) {
        this.X = X;
    }

    public String toString() {
        return this.X + ", " + this.Y;
    }
}
