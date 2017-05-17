package clients;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

public class PlanType {
	public Agent a;
	public Box tar_b;
	public LocationXY tar_cell;
	public String plan;
	public PriorityQueue planqueue;
	public boolean isgoal;
	public Path finalpath;
	public boolean withbox;
	
	
	public PlanType(){
		this.a = new Agent();
		this.tar_b = new Box();
		this.tar_cell = new LocationXY();
		this.finalpath = new Path(a, tar_b, tar_cell);
		this.withbox = false;
	}
	
	public PlanType(Agent wa, Box b, int row, int col){
		this.a = wa;
		this.tar_b = b;
		this.tar_cell = new LocationXY(row, col);
		this.finalpath = new Path(a, tar_b, tar_cell);
		this.withbox = true;
	}
	
	public PlanType(Agent wa, Box b, LocationXY lo){
		this.a = wa;
		this.tar_b = b;
		this.tar_cell = lo;
		this.finalpath = new Path(a, tar_b, tar_cell);
		this.withbox = true;
	}
	
	public PlanType(Agent wa, Box b, LocationXY lo, boolean tisgoal){
		this.a = wa;
		this.tar_b = b;
		this.tar_cell = lo;
		this.isgoal = tisgoal;
		this.finalpath = new Path(a, tar_b, tar_cell);
		this.withbox = true;
	}
	
	public PlanType(Agent wa, int row, int col){
		this.a = wa;
		this.tar_b = null;
		this.tar_cell = new LocationXY(row, col);
		this.finalpath = new Path(a, tar_b, tar_cell);
		this.withbox = false;
	}
	
	public PlanType(Agent wa, LocationXY lo){
		this.a = wa;
		this.tar_b = null;
		this.tar_cell = lo;
		this.finalpath = new Path(a, tar_b, tar_cell);
		this.withbox = false;
	}
	
	public PlanType(Agent wa, LocationXY lo, boolean tisgoal){
		this.a = wa;
		this.tar_b = null;
		this.tar_cell = lo;
		this.isgoal = tisgoal;
		this.finalpath = new Path(a, tar_b, tar_cell);
		this.withbox = false;
	}
	
	public PlanType(Box wb, LocationXY lo){
		this.tar_b = wb;
		this.a = null;
		this.tar_cell = lo;
		this.finalpath = new Path();
		this.finalpath.b = wb;
		this.withbox = true;
	}
	
	public PlanType(Box wb, LocationXY lo, boolean tisgoal){
		this.tar_b = wb;
		this.a = null;
		this.tar_cell = lo;
		this.isgoal = tisgoal;
		this.finalpath = new Path();
		this.finalpath.b = wb;
		this.withbox = true;
	}
	
	public PlanType(Agent wa){
		this.a = wa;
		this.tar_b = null;
		this.tar_cell = null;
		this.finalpath = new Path();
		this.finalpath.a = wa;
		this.withbox = false;
	}
	
	public PlanType(Agent wa, boolean tisgoal){
		this.a = wa;
		this.tar_b = null;
		this.tar_cell = null;
		this.isgoal = tisgoal;
		this.finalpath = new Path();
		this.finalpath.a = wa;
		this.withbox = false;
	}
	
	public PlanType(Box wb){
		this.tar_b = wb;
		this.a = null;
		this.tar_cell = null;
		this.finalpath = new Path();
		this.finalpath.b = wb;
		this.withbox = true;
	}
	
	public PlanType(Box wb, boolean tisgoal){
		this.tar_b = wb;
		this.a = null;
		this.tar_cell = null;
		this.isgoal = tisgoal;
		this.finalpath = new Path();
		this.finalpath.b = wb;
		this.withbox = true;
	}

	
	public boolean setPlan(String rplan){
		this.plan = rplan;
		return true;
	}
	
	
	public boolean setPlan(PriorityQueue wplan){
		this.planqueue = wplan;
		return true;
	}
	
	public static ArrayList<LocationXY> toCells(){
		throw new NotImplementedException();
	}
	
	public Path getPath(){
		return this.finalpath;
	}
	
	public LocationXY findTargetCell(ArrayList<LocationXY> locs, int maxrow, int maxcol){
		int i, j;
		int boundary = Integer.max(maxrow, maxcol);
		for(i = 1; i < boundary;  i ++){
			for(j = 0; j < locs.size(); j ++){
				for(LocationXY nei : locs.get(j).getNeighbours()){
					if(nei.getRow() <= maxrow && nei.getCol() <= maxcol){
						if(!this.finalpath.OnPath(nei))return nei;
					}
				}
			}
		}
		
		return null;
	}
	
	public ArrayList<Agent> findRightAgents(HashMap<Character, Agent> agents, Box b){
		char c = '0';
		ArrayList<Agent> possibleagents = new ArrayList<Agent>();
		int i;
		LocationXY loc = b.getLocation();
		for(i = 0;i < agents.size(); i ++ ){
			c = (char) ('0' + i);
			if(b.getColor() == agents.get(c).getColor()){
				possibleagents.add(agents.get(c));
			}
		}
		
		return possibleagents;
		
	}
	
	
}


