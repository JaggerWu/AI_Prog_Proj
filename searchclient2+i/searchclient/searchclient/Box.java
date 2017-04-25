package searchclient;

/**
 * Created by ilma on 26/03/2017.
 */
public class Box {
    private char id;
    private String color;
    private LocationXY location;
    /************************************************/
    private boolean inFinalPosition = false;
    /************************************************/
    public Box(char letter, String color, LocationXY location) {
        this.id = letter;
        this.color = color;
        this.location = location;
    }
    /*************************/
    public Box(char letter, String color, LocationXY coordinate, boolean inFinalPosition) {
        this.id = letter;
        this.color = color;
        this.location = coordinate;
        this.inFinalPosition = inFinalPosition;
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
    /***************************************************/
    public boolean isInFinalPosition() {
        return inFinalPosition;
    }
    
    public void setInFinalPosition(boolean inFinalPosition) {
        this.inFinalPosition = inFinalPosition;
    }
    /*****************************************************/

}