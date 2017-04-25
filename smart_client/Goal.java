package sampleclient;

/**
 * Created by ilma on 26/03/2017.
 */
public class Goal {
    private char id;
    private LocationXY location;
    private int priority = 0;

    public Goal(char letter, LocationXY location) {
        this.id = letter;
        this.location = location;
    }

    public char getId() {
        return id;
    }

    public void setId(char id) {
        this.id = id;
    }

    public LocationXY getLocation() {
        return location;
    }

    public void setLocation(LocationXY location) {
        this.location = location;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
        //isBeingPrioritized = false;
    }
}
