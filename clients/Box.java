package clients;

/**
 * Created by ilma on 26/03/2017.
 */
public class Box {
    private char id;
    private String color;
    private LocationXY location;
    /************************************************/
    private boolean isInFinalPosition = false;
    /************************************************/
    public Box(char id, String color, LocationXY location) {
        this.id = id;
        this.color = color;
        this.location = location;
    }
    /*************************/
    public Box(char id, String color, LocationXY location, boolean isInFinalPosition) {
        this.id = id;
        this.color = color;
        this.location = location;
        this.isInFinalPosition = isInFinalPosition;
    }
    /******************************/
    
    public Box(){
    	this.location = new LocationXY();
    }
    
    public Box(char letter, String color, int trow, int tcol){
    	this.id = letter;
    	this.color = color;
    	this.location = new LocationXY(trow, tcol);
    }
    
    public Box(int trow, int tcol, char letter){
    	this.id = letter;
    	this.location = new LocationXY(trow, tcol);
    }

    public Box(String tcol, LocationXY loc, char letter) {
        this.id = letter;
        this.location = loc;
        this.color = tcol;
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
    /**
     * @return *************************************************/
    public boolean isBoxInFinalPosition() {
        return isInFinalPosition;
    }
    
    public void setInFinalPosition(boolean isInFinalPosition) {
        this.isInFinalPosition = isInFinalPosition;
    }
    /*****************************************************/

}