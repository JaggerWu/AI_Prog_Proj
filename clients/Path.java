package clients;

import java.util.ArrayList;
import java.util.LinkedList;

public class Path {
	public Agent a;
	public Box b;
	public LocationXY tar_cell;
	public Node finalnode;
	
	public ArrayList<LocationXY> path;
	public ArrayList<LocationXY> barriers;
	public LinkedList<Node> pathbynode;
	
	
	public Path(){
		this.a = new Agent();
		this.b = new Box();
		this.tar_cell = new LocationXY();
		this.pathbynode = new LinkedList<Node>();
	}
	
	public Path(Agent ta, Box tb, LocationXY tc){
		this.a = ta;
		this.b = tb;
		this.tar_cell = tc;
		this.pathbynode = new LinkedList<Node>();
	}
	
	public boolean setPath(ArrayList<LocationXY> tpath){
		this.path = tpath;
		
		return true;
	}
	
	public boolean addtoPath(LocationXY cell){
		path.add(cell);
		return true;
	}
	
	public boolean removefromPath(LocationXY cell){
		if(this.path.contains(cell)){
			this.path.remove(cell);
			return true;
		}else{
			return false;
		}
	}
	
	@Override
	public String toString(){
		int i;
		String pathstring = "";
		for(i = 0; i < this.path.size(); i ++ ){
			pathstring = pathstring + "(" + path.get(i).getRow() + "," + path.get(i).getCol() + ")";
		}
		
		return pathstring;
	}
 /*   public boolean findBlockedLocation(Node currentState){
    	boolean found = false;
        for(LocationXY loc: path){
            if(!currentState.cellIsFree(loc.getRow(),loc.getCol())){
            	found = true;
            	this.barriers.add(loc);
            	}
        }
        
        return found;
    }*/
    public ArrayList<LocationXY> getBarriers(){
        return this.barriers;
    }
    
    public boolean OnPath(LocationXY loc){
    	return path.contains(loc);
    }
    
    public boolean findCellfromNodes(){
    	for(Node n: this.pathbynode){
    		this.path.add(n.currentagent.getLocation());
    	}
    	
    	return true;
    }
}