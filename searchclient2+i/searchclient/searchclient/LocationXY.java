package searchclient;

import java.util.LinkedList;

/**
 * Created by ilma on 26/03/2017.
 */
public class LocationXY {
    private int Row;
    private int Col;
    
    public enum cellState{
    	AGENT, BOX, WALL, EMPTY
    };
    
    public LocationXY(int X, int Y) {
        this.Row = X;
        this.Col = Y;
    }
   
    public LocationXY() {
    }
    
    public LocationXY(LocationXY location){
        this.Row = location.getRow();
        this.Col = location.getCol();
    }
   

    public int getCol() {
        return Col;
    }

    public void setCol(int Y) {
        this.Col = Y;
    }

    public int getRow() {
        return Row;
    }

    public void setRow(int X) {
        this.Row = X;
    }

    public String toString() {
        return this.Row + ", " + this.Col;
    }
    
    public static boolean isNeighboured(LocationXY src, LocationXY tgt){
    	if(src.getRow() + 1 == tgt.getRow() && src.getCol() == tgt.getCol())return true;
    	if(src.getRow() - 1 == tgt.getRow() && src.getCol() == tgt.getCol())return true;
    	if(src.getRow() == tgt.getRow() && src.getCol() + 1 == tgt.getCol())return true;
    	if(src.getRow() == tgt.getRow() && src.getCol() - 1 == tgt.getCol())return true;
    	return false;
    	
    }
    
    public LinkedList<LocationXY> getNeighbours(){
    	LinkedList<LocationXY> neighbour = new LinkedList<LocationXY>();
    	neighbour.add(new LocationXY(this.Row - 1, this.Col));
    	neighbour.add(new LocationXY(this.Row + 1, this.Col));
    	neighbour.add(new LocationXY(this.Row, this.Col - 1));
    	neighbour.add(new LocationXY(this.Row, this.Col + 1));
    	return neighbour;
    }
    
    public LinkedList<LocationXY> getMoreNeighbours(int r){
    	LinkedList<LocationXY> neighbour = new LinkedList<LocationXY>();
    	neighbour.add(new LocationXY(this.Row - r, this.Col));
    	neighbour.add(new LocationXY(this.Row + r, this.Col));
    	neighbour.add(new LocationXY(this.Row, this.Col - r));
    	neighbour.add(new LocationXY(this.Row, this.Col + r));
    	return neighbour;
    }
    
    public boolean theSamePlace(LocationXY other){
    	if(this.getRow() == other.getRow() && this.getCol() == other.getCol()){
    		return true;
    	}else{
    		return false;
    	}
    }
    
    @Override
    public int hashCode(){
        return 2468 + this.Row + this.Col; 
    }
    
    @Override
    public boolean equals(Object obj){
        if(this == obj){
            return true;
        }
        if(this.getClass() != obj.getClass()){
            return false;
        }
        LocationXY other = (LocationXY) obj;
        
        if(!this.theSamePlace(other)){
            return false;
        }
        
        return true;
        
    }
    
}