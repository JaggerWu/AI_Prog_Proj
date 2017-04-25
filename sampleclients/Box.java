package sampleclient;

/**
 * Created by ilma on 26/03/2017.
 */
public class Box {
    private char id;
    private String color;
    private LocationXY location;
    private isFinalPosition = false;

    public Box(char letter, String color, LocationXY location) {
        this.id = letter;
        this.color = color;
        this.location = location;
    }

    public Box(char letter, String color, LocationXY location, boolean isFinalPosition) {
        this.id = letter;
        this.color = color;
        this.location = location;
        this. isFinalPosition = isFinalPosition;
    }

    public char getId() {
        return id;
    }

    public void setId(char id) {
        this.id = id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public LocationXY getLocation() {
        return location;
    }

    public void setLocation(LocationXY location) {
        this.location = location;
    }

    public boolean isFinalPosition() {
        return isFinalPosition;
    }

    public setInFianlPosition(boolean isFinalPosition) {
        this.isFinalPosition = isFinalPosition;

    }
}
