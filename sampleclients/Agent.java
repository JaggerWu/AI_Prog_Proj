package sampleclient;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by ilma on 26/03/2017.
 */
public class Agent {
    private char id;
    private String color;
    private LocationXY location;


    public Agent(char id, String color, LocationXY location) {
        this.id = id;
        this.color = color;
        this.location = location;
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

}
