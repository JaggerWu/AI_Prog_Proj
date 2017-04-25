package clients;
/**
 * Created by ilma on 26/03/2017.
 */
public class Goal {
    private char id;
    private String color;
    private LocationXY location;
    private int priority = 0;
    private boolean evaluated = false;

    public Goal(char letter, LocationXY location, String tcolor) {
        this.id = letter;
        this.location = location;
        this.color = tcolor;
    }
    
    public Goal(){
    	this.location = new LocationXY();
    }
    
    public Goal(char letter, String tcolor){
    	this.id = letter;
    	this.color = tcolor;
    	this.location = new LocationXY();
    }
    
    public Goal(char letter, String tcolor, int trow, int tcol){
    	this.id = letter;
    	this.color = tcolor;
    	this.location = new LocationXY(trow, tcol);
    }
    
    public Goal(int trow, int tcol, char letter){
    	this.id = letter;
    	this.location = new LocationXY(trow, tcol);
    }
    
    public Goal(String tcolor, LocationXY loc, char letter) {
        this.id = letter;
        this.color = tcolor;
        this.location = loc;
    }

    public char getId() {
        return id;
    }
    /**************************/
    public Goal(char letter, LocationXY coordinate) {
        this.id = letter;
        this.location = coordinate;
    }
    /******************************/

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

    public boolean isEvaluated() {
        return evaluated;
    }

    public void setEvaluated(boolean evaluated) {
        this.evaluated = evaluated;
    }
	
    public static boolean isGoal(LocationXY loc, Node n){
        //boolean is = false;
        for(Goal g : n.goalMap.values()){
            if(loc.theSamePlace(g.getLocation()))return true;
        }
        return false;
    }
}