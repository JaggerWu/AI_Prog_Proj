package searchclient;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class Agent {
	private String color;
	private LocationXY location;
	//private int row;
	//private int col;
	private char lable;
	private boolean conflited;
	//private int[][] distance;
	//private Box currentBox = new Box();
	//private Goal currentGoal = new Goal();
	private PriorityQueue<Goal> goalList = new PriorityQueue<Goal>(); 
	public Box currentBox;
	public Goal currentGoal = null;
        /****************************************/
        private boolean clearMode = false;
        private ArrayList<LocationXY> clearCords;
        public boolean done = false;
        private boolean quarantined = false;
        private LinkedList<Node> solution = new LinkedList<Node>();
        private Strategy strategy;
        private Node latestAction = null;
        /*****************************************/
        
	
	
	public Agent(int trow, int tcol, char tlable){
            //this.location = new LocationXY(trow, tcol);
            this.location = new LocationXY(trow, tcol);
            this.lable = tlable;
            this.conflited = false;
	}
	
	public Agent(){
            this.location = new LocationXY();
            this.conflited = false;
	}
	
	public Agent(char tlable){
            this.location = new LocationXY();
            this.lable = tlable;
            this.conflited = false;
	}
	
	public Agent(String tcolor, int trow, int tcol, char tlable){
            this.color = tcolor;
            this.location = new LocationXY(trow, tcol);
            this.lable = tlable;
            this.conflited = false;
	}
	
	public Agent(String tcolor, LocationXY loc, char tlable){
            this.location = loc;
            this.color = tcolor;
            this.lable = tlable;
	}
        //Agent(this.lable, this.color, new LocationXY(this.location.getRow(), this.location.getCol()), this.currentGoal);
	public Agent(char tlable, String color, LocationXY coordinate, Goal currentSubGoal){
            this.lable = tlable;
            this.color = color;
            this.location = coordinate;
            this.currentGoal = currentSubGoal;
            this.solution = new LinkedList<>();
	}
        
	public boolean getMisstion(Box b, Goal g){
            this.currentBox = b;
            this.currentGoal = g;
            return true;
	}

	public boolean getGoals(Goal g){
            this.goalList.add(g);
            return true;
	}

        public void setCoordinate(LocationXY location) {
            this.location = location;
	}

	
	public LocationXY getLocation(){
            return this.location;
	}
	
	public boolean setColor(String c){
            this.color = c;
            return true;
	}
	
	public boolean setLabel(char l){
            this.lable = l;
            return true;
	}
	
	public char getLabel(){
            return this.lable;
	}
	
	public String getColor() {
            return color;
        }
        /*************************************/
        public Goal getCurrentSubGoal() {
            return currentGoal;
	}
        
        public boolean isClearMode() {
            return clearMode;
	}
        
        public void setClearMode(boolean clearMode) {
            this.clearMode = clearMode;
	}

	public ArrayList<LocationXY> getClearCords() {
            return clearCords;
	}

	public void setClearCords(ArrayList<LocationXY> clearCords) {
            this.clearCords = clearCords;
	}
        
        public boolean isQuarantined() {
            return quarantined;
	}
        
        public void appendSolution(LinkedList<Node> partialSolution){
            this.solution.addAll(partialSolution);
	}
        
        public void setStrategy(Strategy strategy) {
            this.strategy = strategy;
	}
        
        public Strategy getStrategy() {
            return strategy;
	}
        
        public void findNextGoal(Node currentState, PriorityQueue<Goal> subGoals){
            if(currentGoal != null){
                Box goalBox = currentState.getBoxesByCoordinate().get(currentGoal.getLocation());
                if(goalBox != null && goalBox.getId() == Character.toUpperCase(currentGoal.getId())){
                    currentGoal = null;
                } else {
                    return;
                }
            }
            if(color == null){
                currentGoal = subGoals.poll();
                done = false;
            } else {
                for(Goal goal : subGoals){
                    for(Box box : currentState.getBoxesByCoordinate().values()){
                        if(box.getColor().equals(color) && box.getId() == Character.toUpperCase(goal.getId())){
                            currentGoal = goal;
                            subGoals.remove(goal);
                            done = false;
                            return;
                        }
                    }
                }
            }
            if(currentGoal == null){
                done = true;
                System.err.println("Agent " + lable + " is done");
            }
	}
        
        public Command act() {
            if(solution.size() > 0){
                Node next = solution.getFirst();
                latestAction = next;
                solution.removeFirst();
                return next.action;
            } else {
                return null;
            }
	}
        
        @Override
	public Agent clone() {
            Agent newAgent = new Agent(this.lable, this.color, 
                                new LocationXY(this.location.getRow(), this.location.getCol()), this.currentGoal);
            newAgent.setClearMode(clearMode);
            newAgent.setClearCords(clearCords);
            return newAgent;
	}
        
        @Override
	public int hashCode() {
            int result = (int) lable;
            result = 31 * result + (color != null ? color.hashCode() : 0);
            result = 31 * result + location.hashCode();
            return result;
	}

	public static ArrayList<Agent> sortById(ArrayList<Agent> agents){
            ArrayList<Agent> returnArr = new ArrayList<>(agents.size());
            for( int i = 0; i < agents.size(); i++){
                returnArr.add(null);
            }
            for (Agent agent : agents){
                returnArr.remove(Character.getNumericValue(agent.getLabel()));
                returnArr.add(Character.getNumericValue(agent.getLabel()), agent);
            }
            return returnArr;
	}
        
	public Agent hereisAgent(LocationXY loc){
		if(this.location.theSamePlace(loc)){
			return this;
		}
		else return null;
	}
        
        /*************************************/
}
