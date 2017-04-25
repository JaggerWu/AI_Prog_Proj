package searchclient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import searchclient.Command.Type;
import java.util.List;

public class Node {
	private static final Random RND = new Random(1);

	//public static int MAX_ROW = 70;
	//public static int MAX_COL = 70;

	public static int MAX_ROW;
	public static int MAX_COL;

	public static int totalRows;
	public static int totalCols;
	//public int agentRow;
	//public int agentCol;

	//public ArrayList<Agent> = new ArrayList<Agent>;
	//public Agent a;
	//public Box b;
	//public Goal go;
	
	public HashMap<Character,Agent> agentbyID = new HashMap<Character, Agent>();
	public HashMap<Character,Goal> goalbyID = new HashMap<Character, Goal>();
	public HashMap<LocationXY, Box> boxbyID = new HashMap<LocationXY, Box>();
	
	public HashMap<LocationXY, Agent> agentMap = new HashMap<LocationXY, Agent>();
	public HashMap<LocationXY, Box> boxMap = new HashMap<LocationXY, Box>();
	public HashMap<LocationXY, Goal> goalMap = new HashMap<LocationXY, Goal>();
	public  static HashMap<LocationXY, Boolean> wallMap = new HashMap<LocationXY, Boolean>();
        /******************************************************/
	public static Agent agent0 = null;
        public Agent thisAgent = null;
        public List<Agent> agents = new ArrayList<Agent>();
        public HashMap<LocationXY, Agent> agentByCoordinate = new HashMap<LocationXY, Agent>();
        
        private HashMap<LocationXY, Box> boxesByCoordinate = new HashMap<LocationXY, Box>();
        private HashMap<Character, Box> boxesByID = new HashMap<Character, Box>();
    
        private static HashMap<LocationXY, Box> boxesByCoordinate1 = new HashMap<LocationXY, Box>();
        private static HashMap<Character, Box> boxesByID1 = new HashMap<Character, Box>();
        private static HashMap<LocationXY, Goal> goalsByCoordinate = new HashMap<LocationXY, Goal>();
        private static HashMap<Character, Goal> goalsByID = new HashMap<Character, Goal>();
        private int f;
        /********************************************************/
	
	public static HashMap<Goal, HashMap<LocationXY, Integer>> goalDistance = new HashMap<Goal, HashMap<LocationXY, Integer>>();
	public static HashMap<Box, HashMap<LocationXY, Integer>> boxDistance = new HashMap<Box, HashMap<LocationXY, Integer>>();
	
	public static HashMap<LocationXY, Goal> goalMapsta = new HashMap<LocationXY, Goal>();
	public static HashMap<LocationXY, Boolean> wallMapsta = new HashMap<LocationXY, Boolean>();
	public static HashMap<LocationXY, Box> boxMapsta = new HashMap<LocationXY, Box>();
	
	public static HashMap<Goal,Integer> priority;
	
	public Agent currentagent = new Agent();
	public Box currentbox = new Box();
	public LocationXY currentcell = new LocationXY();
        
        
	
	// Arrays are indexed from the top-left of the level, with first index being row and second being column.
	// Row 0: (0,0) (0,1) (0,2) (0,3) ...
	// Row 1: (1,0) (1,1) (1,2) (1,3) ...
	// Row 2: (2,0) (2,1) (2,2) (2,3) ...
	// ...
	// (Start in the top left corner, first go down, then go right)
	// E.g. this.walls[2] is an array of booleans having size MAX_COL.
	// this.walls[row][col] is true if there's a wall at (row, col)
	//

	//public boolean[][] walls = new boolean[70][70];
	//public char[][] goals = new char[70][70];
	//public char[][] boxes = new char[70][70];
	
	//public static boolean[][] walls;
        //public static HashMap<LocationXY, Boolean> walls = new HashMap<LocationXY, Boolean>();
	//public static char[][] goals;
	//public char[][] boxes;
	public static char[][] color;
	//public char[][] agent;
	
	
	public Node parent;
	public Command action;

	private int g;
	
	private int _hash = 0;

	public static void initNodeSize(int max_row, int max_col) {
		MAX_ROW = max_row;
		MAX_COL = max_col;
		//walls = new boolean[MAX_ROW][MAX_COL];
		//goals = new char[MAX_ROW][MAX_COL];
		//color = new char[MAX_ROW][MAX_COL];
	}
        public Node(Node parent) {
		this.parent = parent;
		this.boxesByCoordinate = new HashMap<LocationXY, Box>();
		//this.currentbox = parent.currentbox;
		if (parent == null) {
			g = 0;
		} else {
			g = parent.g() + 1;
			//this.currentbox = parent.currentbox;
		}
	}
	
	public Node (Node parent, Command.Type type, Command.Dir dir){
		
	}

	/*
	 * Calculating priority
	 */
        /*****************************************************************/
        public static void addGoal(Goal goal) {
            Node.goalsByCoordinate.put(goal.getLocation(), goal);
            Node.goalsByID.put(goal.getId(), goal);
        }
        
        public static HashMap<LocationXY, Goal> getGoalsByCoordinate() {
            return Node.goalsByCoordinate;
        }
        
        public void addBox(Box box) {
            this.boxesByCoordinate.put(box.getLocation(), box);
            this.boxesByID.put(box.getId(), box);
        }
        
        public HashMap<LocationXY, Box> getBoxesByCoordinate() {
            return this.boxesByCoordinate;
        }
        
        public static void addBox1(Box box) {
            Node.boxesByCoordinate1.put(box.getLocation(), box);
            Node.boxesByID1.put(box.getId(), box);
        }
        
        public static HashMap<LocationXY, Box> getBoxesByCoordinate1() {
            return boxesByCoordinate1;
	}
        
        public List<Agent> getAgents() {
            return agents;
	}

	public Agent getAgentById(char id){
            for(Agent agent : agents){
                if(agent.getLabel() == id){
                    return agent;
                }
            }
            return null;
	}

	public void setAgents(ArrayList<Agent> agents) {
            this.agents = agents;
	}
        
        public boolean isGoalState() {
            if(!thisAgent.isClearMode()) {
                Goal goal = thisAgent.getCurrentSubGoal();
                Box box = boxesByCoordinate.get(goal.getLocation());
                if (box != null && box.getId() == Character.toUpperCase(goal.getId())) {
                    box.setInFinalPosition(true);
                    return true;
                }
            } else {
                for(LocationXY cord : thisAgent.getClearCords()){
                    Box box = boxesByCoordinate.get(cord);
                    if(thisAgent.getLocation().equals(cord)){
                            return false;
                    } else if(box != null && box.getColor() != null && box.getColor().equals(thisAgent.getColor())){
                            return false;
                    }
                }
                return true;
            }
            return false;
	}
        
        public void setParent(Node parent){
            if(parent == null){
                this.parent = null;
                g = 0;
            } else {
                this.parent = parent;
                this.g = parent.g;
            }
	}
	
        public Node getCopy(){
            Node copy = new Node(this);
            copy.setParent(parent);
            for(LocationXY key : this.getBoxesByCoordinate().keySet()) {
                copy.boxesByCoordinate.put(key, this.boxesByCoordinate.get(key));
                copy.boxesByID.put(this.boxesByCoordinate.get(key).getId(), this.boxesByCoordinate.get(key));
            }
            for (Agent agent : this.agents){
                copy.agents.add(agent.clone());
            }
            return copy;
	}
        
        public void setF(int f) {
		this.f = f;
	}
        /******************************************************************/
	
	public static void computeGoalDistance() {
            for (Goal goal : Node.getGoalsByCoordinate().values()) {
		HashMap<LocationXY, Integer> distanceMap = new HashMap<LocationXY, Integer>();
		LinkedList<LocationXY> frontier = new LinkedList<LocationXY>();
		HashSet<LocationXY> frontierHash = new HashSet<LocationXY>();
		// initially put only the goal in the distance map
		distanceMap.put(goal.getLocation(), 0);
		// and the goal's neighbours (4-vicinity) in the frontier
		for (LocationXY coordinate : goal.getLocation().getNeighbours()) {
                    if (Node.wallMapsta.get(coordinate) == null && 
			coordinate.getRow() > -1 && coordinate.getRow() < Node.MAX_ROW &&
			coordinate.getCol() > -1 && coordinate.getCol() < Node.MAX_COL) {
			frontier.add(coordinate);
			frontierHash.add(coordinate);
                    }
		}
		// then in each loop move elements in frontier to distanceMap (take min distance)
		// and add their neighbours to frontier
		while (!frontier.isEmpty()) {
                    LocationXY coordinate = frontier.poll();
                    frontierHash.remove(coordinate);
                    Integer minDistance = Integer.MAX_VALUE;
                    for (LocationXY neighbour : coordinate.getNeighbours()) {
			if (distanceMap.containsKey(neighbour) && distanceMap.get(neighbour) < minDistance) {
                            minDistance = distanceMap.get(neighbour);
			}
                    }
                    distanceMap.put(coordinate, minDistance+1);
                    for (LocationXY neighbour : coordinate.getNeighbours()) {
                        if (Node.wallMapsta.get(neighbour) == null && 
                            neighbour.getRow() > -1 && neighbour.getRow() < Node.MAX_ROW &&
                            neighbour.getCol() > -1 && neighbour.getCol() < Node.MAX_COL &&
                            !distanceMap.containsKey(neighbour) &&
                            !frontierHash.contains(neighbour)) {
                            frontier.add(neighbour);
                            frontierHash.add(neighbour);
                        }
                    }
                }
                    Node.goalDistance.put(goal, distanceMap);
                    System.err.println("goalDistance:  " + goalDistance.get(goal).size());
            }       
	}
	
	
	/*
	 * Test and show the distance
	 * 
	 * */
	
	public static void showDistance(){
            System.err.println("------The Goals Distance MARIX------");
            Iterator<HashMap.Entry<Goal,HashMap<LocationXY, Integer>>> iterGoals = goalDistance.entrySet().iterator();
            while (iterGoals.hasNext()) {
                Entry<Goal, HashMap<LocationXY, Integer>> entry = iterGoals.next();
                Goal goal = (Goal) entry.getKey();
                HashMap<LocationXY, Integer> coorDis = entry.getValue();
                System.err.println("Goal " + goal.getId() + ": ");
                Iterator<HashMap.Entry<LocationXY, Integer>> iterDistance = coorDis.entrySet().iterator();
                while(iterDistance.hasNext()){
                    Entry<LocationXY, Integer> entryDistance = iterDistance.next();
                    LocationXY coor = (LocationXY) entryDistance.getKey();
                    Integer distance = entryDistance.getValue();
                    System.err.print("( "+coor.getCol()+","+ coor.getRow()+") -> " + distance + ";   ");
                }
                System.err.println();
            }
            System.err.println();
	} 

	public static void setGoalsPriority(){
            priority = new HashMap<Goal, Integer>();
            ArrayList<Integer> distanceList = new ArrayList<Integer>();
            goalDistanceCalculationOfAgent0(distanceList); // calculating distances of each goal to the Agent0

            Collections.sort(distanceList);//From the smallest distance to the largest distance
            System.err.println("Distance : " + distanceList);

            intializeEachGoalPriority(distanceList);//according to the distance, set each goal's priority 
		
            /*
             * If a goal is assumed as a wall, then see how many goals it blocked.
             * If the goal blocks other goals, then the goals priority is lower than all the goals it blocks
             * */
            for(int i=0; i<distanceList.size(); i++){
                if(i==0 || distanceList.get(i) != distanceList.get(i-1) ){ // if goals distances are the same, then avoid the duplication calculation
                        Iterator<HashMap.Entry<Goal, Integer>> iterPriority = priority.entrySet().iterator();
                        while(iterPriority.hasNext()){
                            Entry<Goal, Integer> enrtyPriority = iterPriority.next();
                            if(enrtyPriority.getValue() == distanceList.get(i)){
                                Goal goal = enrtyPriority.getKey();
                                System.err.println("Goal " + goal.getId() + "\'s intially priority is : " + goal.getPriority());
                                @SuppressWarnings("unchecked")
								HashMap<LocationXY, Boolean> wallsTemp = (HashMap<LocationXY, Boolean>) Node.wallMapsta.clone();
                                wallsTemp.put(goal.getLocation(), true);
                                System.err.println("Now the goal " + goal.getId() + " is assumed as wall !");
                                /*
                                 * Calculating all distances from agent0 to other goals
                                 */
                                HashMap<LocationXY, Integer> agent0DistanceMap = new HashMap<LocationXY, Integer>();
                                distanceMatrix(wallsTemp, agent0DistanceMap);// generating distance matrix

                                for(int j=0; j<distanceList.size(); j++){
                                    if(j==0 || distanceList.get(j) != distanceList.get(j-1) ){
                                        Iterator<HashMap.Entry<Goal, Integer>> iterPriority1 = priority.entrySet().iterator();
                                        while(iterPriority1.hasNext()){
                                            Entry<Goal, Integer> enrtyPriority1 = iterPriority1.next();
                                            if(enrtyPriority1.getValue() == distanceList.get(j)){
                                                boolean isGoalExist = false;
                                                if(enrtyPriority1.getKey().getId() == goal.getId()){
                                                    isGoalExist = true ;
                                            } else{
                                                    Iterator<HashMap.Entry<LocationXY, Integer>> distance = agent0DistanceMap.entrySet().iterator();
                                                    while (distance.hasNext()) {
                                                        Entry<LocationXY, Integer> entry3 = distance.next();
                                                        if(entry3.getKey().getCol() == enrtyPriority1.getKey().getLocation().getCol() 
                                                                    && entry3.getKey().getRow() == enrtyPriority1.getKey().getLocation().getRow()){
                                                            isGoalExist = true;
                                                    }
                                            }
                                    }

                                    if(!isGoalExist){
                                            System.err.println("ATTENTION : The goal " + enrtyPriority1.getKey().getId() + " has been blocked!");
//							if(goal.getPriority() <=  entry2.getValue().getPriority()){
//								int temp = entry2.getValue().getPriority();
//								entry2.getValue().setPriority(goal.getPriority());
//								goal.setPriority(temp);
//							}

                                            /*
                                             * swift the priority of two goals
                                             * */
                                                    if(goal.getPriority() >=  enrtyPriority1.getKey().getPriority()){
                                                        int temp = enrtyPriority1.getKey().getPriority();
                                                        enrtyPriority1.getKey().setPriority(goal.getPriority());
                                                        goal.setPriority(temp);
                                                    }

                                                }
                                            }
                                        }
                                    }
                                }
                                System.err.println("Goal " + goal.getId() + "\'s finally priority is : " + goal.getPriority());

                            }
                        }
                }
            }
		
		/*
		 * print goals' priority
		 * */
		System.err.println();
		for(int i=0; i<distanceList.size(); i++){
			if(i==0 || distanceList.get(i) != distanceList.get(i-1) ){
				Iterator<HashMap.Entry<Goal, Integer>> iterPriority = priority.entrySet().iterator();
				while(iterPriority.hasNext()){
					Entry<Goal, Integer> enrtyPriority = iterPriority.next();
					if(enrtyPriority.getValue() == distanceList.get(i)){
						System.err.println("Goal " + enrtyPriority.getKey().getId() + "\'s final priority " + enrtyPriority.getKey().getPriority());
					}
				}
			}
		}
		
		/**
		 * if the last goal block other goal?
		 */
//		for(Goal goal : Node.getGoalsByCoordinate().values()){
//			//Goal priority is initialized as 0 so if it is higher we can assume that the priority has already been set.
//			if(goal.getPriority() < 1){
//				goal.setPriority(setSingleGoalPriority(goal));
//			}
//		}
	}

	private static void distanceMatrix(HashMap<LocationXY, Boolean> wallsTemp,
			HashMap<LocationXY, Integer> distanceMap) {
		LinkedList<LocationXY> frontier = new LinkedList<LocationXY>();
		HashSet<LocationXY> frontierHash = new HashSet<LocationXY>();
		// initially put only the goal in the distance map
		distanceMap.put(agent0.getLocation(), 0);
		// and the goal's neighbours (4-vicinity) in the frontier
		for (LocationXY coordinate : agent0.getLocation().getNeighbours()) {
			if (wallsTemp.get(coordinate) == null && 
					coordinate.getRow() > -1 && coordinate.getRow() < Node.totalRows &&
					coordinate.getCol() > -1 && coordinate.getCol() < Node.totalCols) {
				frontier.add(coordinate);
				frontierHash.add(coordinate);
			}
		}
		// then in each loop move elements in frontier to distanceMap (take min distance)
		// and add their neighbours to frontier
		while (!frontier.isEmpty()) {
			LocationXY coordinate = frontier.poll();
			frontierHash.remove(coordinate);
			Integer minDistance = Integer.MAX_VALUE;
			for (LocationXY neighbour : coordinate.getNeighbours()) {
				if (distanceMap.containsKey(neighbour) && distanceMap.get(neighbour) < minDistance) {
					minDistance = distanceMap.get(neighbour);
				}
			}
			distanceMap.put(coordinate, minDistance+1);
			for (LocationXY neighbour : coordinate.getNeighbours()) {
				if (wallsTemp.get(neighbour) == null && 
						neighbour.getRow() > -1 && neighbour.getRow() < Node.totalRows &&
						neighbour.getCol() > -1 && neighbour.getCol() < Node.totalCols &&
						!distanceMap.containsKey(neighbour) &&
						!frontierHash.contains(neighbour)) {
					frontier.add(neighbour);
					frontierHash.add(neighbour);
				}
			}
		}
	}

	/*
	 * according to the distance, set each goal's priority 
	 * */
	private static void intializeEachGoalPriority(
			ArrayList<Integer> distanceList) {
		for(int i=0; i<distanceList.size(); i++){
			if(i==0 || distanceList.get(i) != distanceList.get(i-1) ){ //Avoid duplication calculation of the same distances goals
				Iterator<HashMap.Entry<Goal, Integer>> iterPriority = priority.entrySet().iterator();
				int tempCount = 0;
				while(iterPriority.hasNext()){
					Entry<Goal, Integer> enrtyPriority = iterPriority.next();
					if(enrtyPriority.getValue() == distanceList.get(i)){
						//enrtyPriority.getKey().setPriority(i+1);//set priority as the index of the distance list
						//enrtyPriority.getKey().setPriority(distanceList.size()+ tempCount - i);//set priority as the index of the distance list in reverse order
						enrtyPriority.getKey().setPriority(distanceList.get(i)*10+ tempCount);//set priority as the index of the distance list in reverse order
						tempCount ++ ;
						System.err.println("Goal " + enrtyPriority.getKey().getId() + "\'s priority " + enrtyPriority.getKey().getPriority());
					}
				}
			}
		}
	}
	
	private static void goalDistanceCalculationOfAgent0(
			ArrayList<Integer> distanceList) {
		Iterator<HashMap.Entry<Goal,HashMap<LocationXY, Integer>>> iterGoals = goalDistance.entrySet().iterator();
		while (iterGoals.hasNext()) {
			Entry<Goal, HashMap<LocationXY, Integer>> entry = iterGoals.next();
			Goal goal = (Goal) entry.getKey();
			HashMap<LocationXY, Integer> coorDis = entry.getValue();
			Iterator<HashMap.Entry<LocationXY, Integer>> iterDistance = coorDis.entrySet().iterator();
			while(iterDistance.hasNext()){
				Entry<LocationXY, Integer> entryDistance = iterDistance.next();
				LocationXY coor = (LocationXY) entryDistance.getKey();
				Integer distance = entryDistance.getValue();
				if(agent0.getLocation().getRow() == coor.getRow() 
						&& agent0.getLocation().getCol() == coor.getCol()){
					priority.put(goal, distance);
					distanceList.add(distance);
					System.err.println("Distance from goal to Agent 0 : Goal -> " + goal.getId() + " , distance -> " + distance);
				}
			}
		}
	}
	
	public Agent getAgent0(){
		Agent agent = new Agent();
		return agent;
	}
        
        public static void setAgent0(Agent agent0) {
		Node.agent0 = agent0;
	}

	//Function used to recursively set goal priority
	public static int setSingleGoalPriority(Goal goal){
		goal.setEvaluated(true);
		int bufferZoneOffset = 10;
		//Get coordinates for all adjacent cells, and put them in list
		LocationXY nCord = new LocationXY(goal.getLocation().getRow() - 1,goal.getLocation().getCol());
		LocationXY wCord = new LocationXY(goal.getLocation().getRow(), goal.getLocation().getCol() -1 );
		LocationXY sCord = new LocationXY(goal.getLocation().getRow() +1, goal.getLocation().getCol());
		LocationXY eCord = new LocationXY(goal.getLocation().getRow(), goal.getLocation().getCol() +1);
		ArrayList<LocationXY> newCords = new ArrayList<>();
		newCords.add(nCord);
		newCords.add(wCord);
		newCords.add(sCord);
		newCords.add(eCord);
		int returnVal = Integer.MAX_VALUE;
		for( LocationXY cord : newCords){
			//The goal is next to a "free" cell meaning that it has neither a wall or another goal cell. Base case for recursive function
			if(Node.getWallMapsta().get(cord) == null && Node.getGoalMapsta().get(cord) == null){
				//Need to also call the setter here in case this is a recursive call of the function.
				goal.setPriority(1 + bufferZoneOffset);
				return 1 + bufferZoneOffset;
			}
			//The goal cell is next another goal cell in this direction
			else if(Node.getGoalMapsta().get(cord) != null){
				Goal target = Node.getGoalMapsta().get(cord);
				//Skip this goal if it is already in the stack
				if(target.isEvaluated()){
					continue;
				}
				//Goal priority is initialized as 0 so if it is higher we can assume that the priority has already been set.
				if(target.getPriority() < 1) {
					//If priority has not been set, call this function recursively for the adjacent goal cell and add 1.
					int targetPrio = setSingleGoalPriority(target);
					if(targetPrio < goal.getPriority() || goal.getPriority() < 1) {
						goal.setPriority(targetPrio + 1 + bufferZoneOffset);
					}
				} else {
					//else just get the priority and add 1
					int targetPrio = target.getPriority();
					if(targetPrio < goal.getPriority() || goal.getPriority() < 1) {
						goal.setPriority(targetPrio + 1 + bufferZoneOffset);
					}
				}
				//Compare the priority to the priorities found in other directions. Note that a wall will not be able to set the returnVal.
				returnVal = goal.getPriority() < returnVal ? goal.getPriority() : returnVal;
			}
		}
		return returnVal;
	}
	
	
	/*
	 * 
	 */
	
	public int g() {
		return this.g;
	}

	public boolean isInitialState() {
		return this.parent == null;
	}

	public boolean isGoalState(boolean withbox) {
		if(withbox){
			if(this.currentbox.getLocation().theSamePlace(this.currentcell))return true;
			else return false;
		}else{
			if(this.currentagent.getLocation().theSamePlace(this.currentcell))return true;
			else return false;
		
	
		}
	}
        public ArrayList<Node> getExpandedNodes() {
            ArrayList<Node> expandedNodes = new ArrayList<Node>(Command.EVERY.length);
            for (Command c : Command.EVERY) {
                // Determine applicability of action
                int newAgentRow = thisAgent.getLocation().getRow() + Command.dirToRowChange(c.dir1);
                int newAgentCol = thisAgent.getLocation().getCol() + Command.dirToColChange(c.dir1);

                if (c.actionType == Type.Move) {
                    // Check if there's a wall or box on the cell to which the agent is moving
                    if (cellIsFree(newAgentRow, newAgentCol)) {
                        Node n = this.ChildNode();
                        n.action = c;
                        n.thisAgent.getLocation().setRow(newAgentRow);
                        n.thisAgent.getLocation().setCol(newAgentCol);
                        expandedNodes.add(n);
                    }
                } else if (c.actionType == Type.Push) {
                        // Make sure that there's actually a box to move
                		//System.err.println("push : BoxAt " + newAgentRow + " " + newAgentCol + " " + boxAt(newAgentRow, newAgentCol));
                        if (boxAt(newAgentRow, newAgentCol)) {
                            int newBoxRow = newAgentRow + Command.dirToRowChange(c.dir2);
                            int newBoxCol = newAgentCol + Command.dirToColChange(c.dir2);
                            // .. and that new cell of box is free
                            if (cellIsFree(newBoxRow, newBoxCol)) {
                                Node n = this.ChildNode();
                                n.action = c;
                                n.thisAgent.getLocation().setRow(newAgentRow);
                                n.thisAgent.getLocation().setCol(newAgentCol);
                                // TODO: eventually refactor with clone()
                                //System.err.println("boxesByCoordinate: " + this.boxesByCoordinate.get(new LocationXY(newAgentRow, newAgentCol)).getId());
                                Box boxToMove = this.boxesByCoordinate.get(
                                                                        new LocationXY(newAgentRow, newAgentCol));
                                Box boxToMoveCopy = new Box(boxToMove.getId(), boxToMove.getColor(),
                                                                                        new LocationXY(newBoxRow, newBoxCol), boxToMove.isInFinalPosition());
                                n.boxesByCoordinate.put(new LocationXY(newBoxRow, newBoxCol), boxToMoveCopy);
                                n.boxesByCoordinate.remove(new LocationXY(newAgentRow, newAgentCol));
                                n.boxesByID.remove(boxToMoveCopy.getId());
                                n.boxesByID.put(boxToMoveCopy.getId(), boxToMoveCopy);
                                expandedNodes.add(n);
                            }
                        }
                } else if (c.actionType == Type.Pull) {
                    // Cell is free where agent is going
                	//System.err.println("pull : BoxAt " + newAgentRow + " " + newAgentCol + " " + boxAt(newAgentRow, newAgentCol));
                	if (cellIsFree(newAgentRow, newAgentCol)) {
                        int boxRow = this.thisAgent.getLocation().getRow() + Command.dirToRowChange(c.dir2);
                        int boxCol = this.thisAgent.getLocation().getCol() + Command.dirToColChange(c.dir2);
                        // .. and there's a box in "dir2" of the agent
                        if (boxAt(boxRow, boxCol)) {
                            Node n = this.ChildNode();
                            n.action = c;
                            // TODO: eventually refactor with clone()
                            Box boxToMove = this.boxesByCoordinate.get(
                                                                    new LocationXY(boxRow, boxCol));
                            Box boxToMoveCopy = new Box(boxToMove.getId(), boxToMove.getColor(),
                                            new LocationXY(this.thisAgent.getLocation().getRow(),
                                                            this.thisAgent.getLocation().getCol()), boxToMove.isInFinalPosition());

                            n.boxesByCoordinate.put(new LocationXY(this.thisAgent.getLocation().getRow(),
                                            this.thisAgent.getLocation().getCol()), boxToMoveCopy);
                            n.boxesByCoordinate.remove(new LocationXY(boxRow, boxCol));
                            n.boxesByID.remove(boxToMoveCopy.getId());
                            n.boxesByID.put(boxToMoveCopy.getId(), boxToMoveCopy);
                            n.thisAgent.getLocation().setRow(newAgentRow);
                            n.thisAgent.getLocation().setCol(newAgentCol);
                            expandedNodes.add(n);
                        }
                    }
                }
            }
            return expandedNodes;
	}

	public ArrayList<Node> getExpandedNodesTest() {
		
		/**/
		ArrayList<Node> expandedNodes = new ArrayList<Node>(Command.EVERY.length);
		for (Command c : Command.EVERY) {
			// Determine applicability of action
			//int newAgentRow = this.agentRow + Command.dirToRowChange(c.dir1);
			//int newAgentCol = this.agentCol + Command.dirToColChange(c.dir1);
			int newAgentRow = this.currentagent.getLocation().getRow() + Command.dirToRowChange(c.dir1);
			int newAgentCol = this.currentagent.getLocation().getCol() + Command.dirToColChange(c.dir1);
			if (c.actionType == Type.Move) {
				// Check if there's a wall or box on the cell to which the agent is moving
				/*
				if(preCondition(this, newAgentRow, newAgentCol, c.actionType)){
				
				}
				*/
				if (this.cellIsFree(newAgentRow, newAgentCol)) {
					Node n = this.ChildNode();
					n.action = c;
					//n.agentRow = newAgentRow;
					//n.agentCol = newAgentCol;
					n.currentagent.getLocation().setRow(newAgentRow);
					n.currentagent.getLocation().setCol(newAgentCol);
					//System.err.println("set agent location successfully." + " row: " + newAgentRow + " col: " + newAgentCol);
					expandedNodes.add(n);
				}
			} else if (c.actionType == Type.Push) {
				// Make sure that there's actually a box to move
				if (this.boxAt(newAgentRow, newAgentCol)) {
					int newBoxRow = newAgentRow + Command.dirToRowChange(c.dir2);
					int newBoxCol = newAgentCol + Command.dirToColChange(c.dir2);
					// .. and that new cell of box is free
					if (this.cellIsFree(newBoxRow, newBoxCol)) {
						Node n = this.ChildNode();
						n.action = c;
						//n.agentRow = newAgentRow;
						//n.agentCol = newAgentCol;
						n.currentagent.getLocation().setRow(newAgentRow);
						n.currentagent.getLocation().setCol(newAgentCol);
						//System.err.println("set agent location successfully.");
						n.currentbox.getLocation().setRow(newBoxRow);
						n.currentbox.getLocation().setCol(newBoxCol);
						//n.boxes[newBoxRow][newBoxCol] = this.boxes[newAgentRow][newAgentCol];
						//n.boxes[newAgentRow][newAgentCol] = 0;
						expandedNodes.add(n);
					}
				}
			} else if (c.actionType == Type.Pull) {
				// Cell is free where agent is going
				if (this.cellIsFree(newAgentRow, newAgentCol)) {
					//int boxRow = this.agentRow + Command.dirToRowChange(c.dir2);
					//int boxCol = this.agentCol + Command.dirToColChange(c.dir2);
					int boxRow = this.currentagent.getLocation().getRow() + Command.dirToRowChange(c.dir2);
					int boxCol = this.currentagent.getLocation().getCol() + Command.dirToColChange(c.dir2);
					// .. and there's a box in "dir2" of the agent
					if (this.boxAt(boxRow, boxCol)) {
						Node n = this.ChildNode();
						n.action = c;
						//n.agentRow = newAgentRow;
						//n.agentCol = newAgentCol;
						n.currentagent.getLocation().setRow(newAgentRow);
						n.currentagent.getLocation().setCol(newAgentCol);
						//System.err.println("set agent location successfully.");
						n.currentbox.getLocation().setRow(this.currentagent.getLocation().getRow());
						n.currentbox.getLocation().setCol(this.currentagent.getLocation().getCol());
						//n.boxes[this.a.getLocation().getRow()][this.a.getLocation().getCol()] = this.boxes[boxRow][boxCol];
						//n.boxes[boxRow][boxCol] = 0;
						expandedNodes.add(n);
					}
				}
			}
		}
		Collections.shuffle(expandedNodes, RND);
		return expandedNodes;
		
		
		
		//throw new NotImplementedException();
	}
/***************************************************/      
        public boolean changeState(Command[] commands, String[] serverOutput, SearchClient client){
            for(int i = 0; i < commands.length; i++){
                Agent activeAgent = this.getAgentById(Integer.toString(i).charAt(0));
                if(commands[i] != null && !serverOutput[i].equals("false")) {
                    int newAgentRow = activeAgent.getLocation().getRow() + Command.dirToRowChange(commands[i].dir1);
                    int newAgentColumn = activeAgent.getLocation().getCol() + Command.dirToColChange(commands[i].dir1);
                    LocationXY newPos = new LocationXY(newAgentRow, newAgentColumn);
                    if (commands[i].actionType == Type.Pull) {
                        int boxRow = activeAgent.getLocation().getRow() + Command.dirToRowChange(commands[i].dir2);
                        int boxCol = activeAgent.getLocation().getCol() + Command.dirToColChange(commands[i].dir2);
                        if (boxAt(boxRow, boxCol) && boxesByCoordinate.get(new LocationXY(boxRow, boxCol)).getColor() == null || boxAt(boxRow, boxCol) && boxesByCoordinate.get(new LocationXY(boxRow, boxCol)).getColor().equals(activeAgent.getColor())) {
                            Box pullBox = this.boxesByCoordinate.get(new LocationXY(boxRow, boxCol));
                            Box pullBoxNew = new Box(pullBox.getId(), pullBox.getColor(), activeAgent.getLocation());
                            boxesByCoordinate.remove(pullBox.getLocation());

                            if(activeAgent.getCurrentSubGoal() != null && activeAgent.getCurrentSubGoal().getLocation().equals(pullBoxNew.getLocation()) && !activeAgent.isClearMode()) {
                                pullBoxNew.setInFinalPosition(true);
                                System.err.println("Set in final position");
                            }
                            //System.err.println("Agent " + activeAgent.getId() + " moved box from " + boxRow + ", " + boxCol + " to " + activeAgent.getCoordinate().getRow() + ", " + activeAgent.getCoordinate().getColumn());
                            activeAgent.setCoordinate(newPos);
                            boxesByCoordinate.put(pullBoxNew.getLocation(), pullBoxNew);
                            for(Goal goal : goalsByCoordinate.values()){
                                if(goal.getLocation().equals(pullBox.getLocation()) && goal.getId() == Character.toLowerCase(pullBox.getId())){
                                    if(!client.subGoals.contains(goal)){
                                        if(goal.getPriority() > 1 ){
                                            System.err.println("Priority was: " + goal.getPriority());
                                            goal.setPriority(goal.getPriority() - 1);
                                        }
                                        client.subGoals.offer(goal);
                                        System.err.println("subgoal " + goal.getId() + " at " + goal.getLocation().toString() + " reinserted in list. Priority: " + goal.getPriority());
                                    }
                                }
                            }
                        } else if(boxesByCoordinate.get(new LocationXY(boxRow, boxCol)).getColor() == null || boxesByCoordinate.get(new LocationXY(boxRow, boxCol)).getColor().equals(activeAgent.getColor())){
                            System.err.println("No box at: " + newPos.toString());
                            System.err.println("Client State corrupted");
                            return false;
                        }

                    } else if (commands[i].actionType == Type.Push) {
                        int newBoxRow = newAgentRow + Command.dirToRowChange(commands[i].dir2);
                        int newBoxCol = newAgentColumn + Command.dirToColChange(commands[i].dir2);
                        if (boxAt(newPos.getRow(), newPos.getCol()) && boxesByCoordinate.get(newPos).getColor() == null || boxAt(newPos.getRow(), newPos.getCol()) && boxesByCoordinate.get(newPos).getColor() != null && boxesByCoordinate.get(newPos).getColor().equals(activeAgent.getColor())) {
                            Box pushBox = this.boxesByCoordinate.get(newPos);
                            Box pushBoxNew = new Box(pushBox.getId(), pushBox.getColor(), new LocationXY(newBoxRow, newBoxCol));
                            boxesByCoordinate.remove(pushBox.getLocation());
                            activeAgent.setCoordinate(newPos);
                            if(activeAgent.getCurrentSubGoal() != null && activeAgent.getCurrentSubGoal().getLocation().equals(pushBoxNew.getLocation()) && !activeAgent.isClearMode()) {
                                pushBoxNew.setInFinalPosition(true);
                                System.err.println("Set in final position");
                            }
                            boxesByCoordinate.put(pushBoxNew.getLocation(), pushBoxNew);
                            for(Goal goal : goalsByCoordinate.values()){
                                if(goal.getLocation().equals(pushBox.getLocation()) && goal.getId() == Character.toLowerCase(pushBox.getId())){
                                    if(!client.subGoals.contains(goal)){
                                        if(goal.getPriority() > 1 ){
                                            System.err.println("Priority was: " + goal.getPriority());
                                            goal.setPriority(goal.getPriority() - 1);
                                        }
                                        client.subGoals.offer(goal);
                                        System.err.println("subgoal " + goal.getId() + " at " + goal.getLocation().toString() + " reinserted in list. Priority: " + goal.getPriority());
                                    }
                                }
                            }
                        } else if(boxesByCoordinate.get(newPos).getColor() == null ||  boxesByCoordinate.get(newPos).getColor().equals(activeAgent.getColor())){
                            System.err.println("No box at: " + newPos.toString());
                            System.err.println("Client state corrupted");
                            return false;
                        }
                    } else {
                        boolean movePossible = true;
                        for(Agent a : agents){
                            if(a.getLabel() != activeAgent.getLabel() && a.getLocation().equals(newPos)){
                                movePossible = false;
                            }
                        }
                        if (movePossible){
                            activeAgent.setCoordinate(newPos);
                        } //System.err.println("Agent " + activeAgent.getId() + "moved to " + newPos.getRow() + ", " + newPos.getColumn());
                    }
                }
            }
            return true;
	}
        
        public void printState() {
            StringBuilder builder = new StringBuilder();
            for (int i=0; i<Node.MAX_ROW; i++) {
                for (int j=0; j<Node.MAX_COL; j++) {
                    Agent cellAgent = null;
                    for(Agent agent : agents){
                        if(agent.getLocation().equals(new LocationXY(i,j))){
                            cellAgent = agent;
                        }
                    }
                    if (Node.wallMapsta.get(new LocationXY(i, j)) != null) {
                        builder.append('+');
                    } else if (cellAgent != null){
                        builder.append(cellAgent.getLabel());
                    } else if (this.boxesByCoordinate.get(new LocationXY(i, j)) != null) {
                        builder.append(this.boxesByCoordinate.get(new LocationXY(i, j)).getId());
                    } else if (Node.goalsByCoordinate.get(new LocationXY(i, j)) != null) {
                        builder.append(Node.goalsByCoordinate.get(new LocationXY(i, j)).getId());
                    } else {
                        builder.append(' ');
                    }
                }
                builder.append('\n');
            }
            System.err.print(builder.toString());
	}
	/*****************************************************/
	public ArrayList<Node> getExpandedNodesAcrosser() {
		
		
		ArrayList<Node> expandedNodes = new ArrayList<Node>(Command.EVERY.length);
		for (Command c : Command.EVERY) {
			// Determine applicability of action
			//int newAgentRow = this.agentRow + Command.dirToRowChange(c.dir1);
			//int newAgentCol = this.agentCol + Command.dirToColChange(c.dir1);
			int newAgentRow = this.currentagent.getLocation().getRow() + Command.dirToRowChange(c.dir1);
			int newAgentCol = this.currentagent.getLocation().getCol() + Command.dirToColChange(c.dir1);
			if (c.actionType == Type.Move) {
				// Check if there's a wall or box on the cell to which the agent is moving
				/*
				if(preCondition(this, newAgentRow, newAgentCol, c.actionType)){
				
				}
				*/
				//if (this.cellIsFree(newAgentRow, newAgentCol)) {
				if(!this.wallMap.containsKey(new LocationXY(newAgentRow, newAgentCol))){
					Node n = this.ChildNode();
					n.action = c;
					//n.agentRow = newAgentRow;
					//n.agentCol = newAgentCol;
					n.currentagent.getLocation().setRow(newAgentRow);
					n.currentagent.getLocation().setCol(newAgentCol);
					//System.err.println("set agent location successfully." + " row: " + newAgentRow + " col: " + newAgentCol);
					expandedNodes.add(n);
				}
			} else if (c.actionType == Type.Push) {
				// Make sure that there's actually a box to move
				if (this.boxAt(newAgentRow, newAgentCol)) {
					int newBoxRow = newAgentRow + Command.dirToRowChange(c.dir2);
					int newBoxCol = newAgentCol + Command.dirToColChange(c.dir2);
					// .. and that new cell of box is free
					//if (this.cellIsFree(newBoxRow, newBoxCol)) {
					if(!this.wallMap.containsKey(new LocationXY(newAgentRow, newAgentCol))){
						Node n = this.ChildNode();
						n.action = c;
						//n.agentRow = newAgentRow;
						//n.agentCol = newAgentCol;
						n.currentagent.getLocation().setRow(newAgentRow);
						n.currentagent.getLocation().setCol(newAgentCol);
						//System.err.println("set agent location successfully.");
						n.currentbox.getLocation().setRow(newBoxRow);
						n.currentbox.getLocation().setCol(newBoxCol);
						//n.boxes[newBoxRow][newBoxCol] = this.boxes[newAgentRow][newAgentCol];
						//n.boxes[newAgentRow][newAgentCol] = 0;
						expandedNodes.add(n);
					}
				}
			} else if (c.actionType == Type.Pull) {
				// Cell is free where agent is going
				//if (this.cellIsFree(newAgentRow, newAgentCol)) {
				if(this.wallMap.containsKey(new LocationXY(newAgentRow, newAgentCol))){
					//int boxRow = this.agentRow + Command.dirToRowChange(c.dir2);
					//int boxCol = this.agentCol + Command.dirToColChange(c.dir2);
					int boxRow = this.currentagent.getLocation().getRow() + Command.dirToRowChange(c.dir2);
					int boxCol = this.currentagent.getLocation().getCol() + Command.dirToColChange(c.dir2);
					// .. and there's a box in "dir2" of the agent
					if (this.boxAt(boxRow, boxCol)) {
						Node n = this.ChildNode();
						n.action = c;
						//n.agentRow = newAgentRow;
						//n.agentCol = newAgentCol;
						n.currentagent.getLocation().setRow(newAgentRow);
						n.currentagent.getLocation().setCol(newAgentCol);
						//System.err.println("set agent location successfully.");
						n.currentbox.getLocation().setRow(this.currentagent.getLocation().getRow());
						n.currentbox.getLocation().setCol(this.currentagent.getLocation().getCol());
						//n.boxes[this.a.getLocation().getRow()][this.a.getLocation().getCol()] = this.boxes[boxRow][boxCol];
						//n.boxes[boxRow][boxCol] = 0;
						expandedNodes.add(n);
					}
				}
			}
		}
		Collections.shuffle(expandedNodes, RND);
		return expandedNodes;
		
		
	}
        
        private boolean cellIsFree(int row, int col) {
		if(thisAgent.isClearMode()) {
			for (Agent agent : agents) {
				if (agent.getLabel() != thisAgent.getLabel() && agent.getLocation().equals(new LocationXY(row, col))) {
					return false;
				}
			}
		}
		Box box = boxesByCoordinate.get(new LocationXY(row, col));
		boolean noBox;
		if(box == null || box.getColor() != null && !box.getColor().equals(thisAgent.getColor())){
			noBox = true;
		} else {
			noBox = false;
		}

		return (Node.wallMap.get(new LocationXY(row, col)) == null
					&& noBox);
	}
	

/*	public boolean cellIsFree(int row, int col) {
		//return !this.walls[row][col] && this.boxes[row][col] == 0 && this.agent[row][col] == 0;
		//System.err.println("a's X : " + this.a.getLocation().getRow());
		//System.err.println("row : " + row);
		//System.err.println("a's Y : " + this.a.getLocation().getCol());
		//System.err.println("col : " + col);
		//System.err.println("checkrow : " + (this.a.getLocation().getRow() != row));
		//System.err.println("checkcol : " + (this.a.getLocation().getCol() != col));
		//return !this.walls[row][col] && this.boxes[row][col] == 0 && !((this.b.getLocation().getRow() == row) && (this.b.getLocation().getCol() == col)) && !((this.a.getLocation().getRow() == row) && (this.a.getLocation().getCol() == col));
								

		//return !this.walls[row][col] && !((this.b.getLocation().getRow() == row) && (this.b.getLocation().getCol() == col)) && !((this.a.getLocation().getRow() == row) && (this.a.getLocation().getCol() == col));
		//throw new NotImplementedException();
		LocationXY loc = new LocationXY(row, col);
		return !this.wallMap.containsKey(loc) && !this.agentMap.containsKey(loc) && !this.boxMap.containsKey(loc);
	}*/

	private boolean boxAt(int row, int col) {
		//return (this.currentbox.getLocation().getRow() == row && this.currentbox.getLocation().getCol() == col);
		return boxesByCoordinate.containsKey(new LocationXY(row, col));
		//throw new NotImplementedException();
	}
/*
	private Node ChildNode() {
		Node copy = new Node(this);
		//for (int row = 0; row < MAX_ROW; row++) {
			//System.arraycopy(this.walls[row], 0, copy.walls[row], 0, MAX_COL);
			//System.arraycopy(this.boxes[row], 0, copy.boxes[row], 0, MAX_COL);
			//System.arraycopy(this.goals[row], 0, copy.goals[row], 0, MAX_COL);
		//}
		//copy.a = new Agent(this.a.getLocation().getRow(), this.a.getLocation().getCol(), this.a.getLabel());
		return copy;
	}*/
        
        private Node ChildNode() {
            Node copy = new Node(this);
            for (LocationXY key : this.boxesByCoordinate.keySet()) {
                copy.boxesByCoordinate.put(key, this.boxesByCoordinate.get(key));
                copy.boxesByID.put(this.boxesByCoordinate.get(key).getId(), this.boxesByCoordinate.get(key));
            }
            for (Agent agent : this.agents) {
                copy.agents.add(agent.clone());
            }
            copy.thisAgent = this.thisAgent.clone();
            return copy;
	}

	public LinkedList<Node> extractPlan() {
		LinkedList<Node> plan = new LinkedList<Node>();
		Node n = this;
		while (!n.isInitialState()) {
			Filewriter.giveittostring(n.action.toString());
			plan.addFirst(n);
			n = n.parent;
		}
		Filewriter.printtotxt();
		
		/*
		String planstring = "";
		int i = 0;
		for(i = 0; i < plan.size(); i ++){
			planstring =  plan.get(i).action.toString() + " ";
		}
		Filewriter.printtotxt(planstring);
		*/
		return plan;
	}
/*
        @Override
	public int hashCode() {
            final int prime = 31;
            int result = 1;

            result = prime * result + boxesByCoordinate.hashCode();
            result = prime * result + thisAgent.hashCode();
            return result;
	}

	@Override
	public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Node other = (Node) obj;

            if(!boxesByCoordinate.equals(other.getBoxesByCoordinate())){
                return false;
            }

            if(!thisAgent.equals(other.thisAgent)){
                return false;
            }
            return true;
	}
*/

	@Override
	public int hashCode() {
            if(this._hash == 0){
                final int prime = 31;
                int result = 1;
                for(Map.Entry<LocationXY, Agent> entry : this.agentMap.entrySet()){
                    result = prime * result + entry.getKey().getCol();
                    result = prime * result + entry.getKey().getRow();
                }
                for(Map.Entry<LocationXY, Box> entry : this.boxesByCoordinate.entrySet()){
                    result = prime * result + entry.getKey().getCol();
                    result = prime * result + entry.getKey().getRow();
                }
                for(Map.Entry<LocationXY, Goal> entry : this.goalsByCoordinate.entrySet()){
                    result = prime * result + entry.getKey().getCol();
                    result = prime * result + entry.getKey().getRow();
                }
                for(Map.Entry<LocationXY, Boolean> entry : this.wallMap.entrySet()){
                    result = prime * result + entry.getKey().getCol();
                    result = prime * result + entry.getKey().getRow();
                }
                this._hash = result;
            }
            return this._hash;
	}

	@Override
	public boolean equals(Object obj) {
            if(this == obj){
                return true;
            }
            if(obj == null){
                return false;
            }
            if(this.getClass() != obj.getClass()){
                return false;
            }
            Node other = (Node) obj;
            if(this.currentagent.getLocation().theSamePlace(other.currentagent.getLocation())){
                //return false;
            }
            if(this.currentbox.getLocation().theSamePlace(other.currentbox.getLocation())){
                //return false;
            }
            if(this.currentcell.theSamePlace(other.currentcell)){
                //return false;
            }
            if(this.agentMap.size() != other.agentMap.size()){
                return false;
            }
            //for(Map.Entry<LocationXY, Agent> entry : this.agentMap.entrySet()){
            //   if(other.agentMap.containsKey(entry.getKey())){
            //        if(entry.getValue().getLabel() != other.agentMap.get(entry.getKey()).getLabel())return false;
            //    }
            //    else return false;
            //}
            if(this.boxesByCoordinate.size() != other.boxesByCoordinate.size()){
                return false;
            }
            for(Map.Entry<LocationXY, Box> entry : this.boxesByCoordinate.entrySet()){
                if(other.boxesByCoordinate.containsKey(entry.getKey())){
                    if(entry.getValue().getId() != other.boxesByCoordinate.get(entry.getKey()).getId())return false;
                }
                else return false;
            }
            if(this.goalsByCoordinate.size() != other.goalsByCoordinate.size()){
            	return false;
            }
            for(Map.Entry<LocationXY, Goal> entry : this.goalsByCoordinate.entrySet()){
                if(other.goalsByCoordinate.containsKey(entry.getKey())){
                    if(entry.getValue().getId() != other.goalsByCoordinate.get(entry.getKey()).getId())return false;
                }
                else return false;
            }
            if(other.wallMap.size() != this.wallMap.size()){
               return false;
            }
            for(Map.Entry<LocationXY, Boolean> entry : this.wallMap.entrySet()){
                if(!other.wallMap.containsKey(entry.getKey())){
                return false;
                }
            }
            if(!this.thisAgent.getLocation().theSamePlace(other.thisAgent.getLocation())){
                return false;
            }
            return true;	
	}

	@Override
	public String toString() {
	/*
		StringBuilder s = new StringBuilder();
		for (int row = 0; row < MAX_ROW; row++) {
			if (!this.walls[row][0]) {
				break;
			}
			for (int col = 0; col < MAX_COL; col++) {
				//if (this.boxes[row][col] > 0) {
					//s.append(this.boxes[row][col]);
				if (this.b.getLocation().getRow() == row && this.b.getLocation().getCol() == col) {
					s.append(b.getId());
				//} else if (this.goals[row][col] > 0) {
				}else if (this.go.getLocation().getRow() == row && this.go.getLocation().getCol() == col) {
					s.append(go.getId());
					//s.append(this.goals[row][col]);
				}else if (this.walls[row][col]) {
					s.append("+");
				//} else if (row == this.agentRow && col == this.agentCol) {
				}else if (row == this.a.getLocation().getRow() && col == this.a.getLocation().getCol()) {
					s.append(this.a.getLabel());
				}else {
					s.append(" ");
				}
			}
			s.append("\n");
		}
		return s.toString();
	*/
	/**/
	LocationXY loc = null;
	StringBuilder s = new StringBuilder();
	for (int row = 0; row < MAX_ROW; row++) {
		if (!this.wallMap.containsKey(new LocationXY(row, 0))) {
			break;
		}
		for (int col = 0; col < MAX_COL; col++) {
			//if (this.boxes[row][col] > 0) {
				//s.append(this.boxes[row][col]);
			loc = new LocationXY(row, col);
			if (this.boxesByCoordinate.containsKey(loc)) {
				s.append(this.boxesByCoordinate.get(loc).getId());
			//} else if (this.goals[row][col] > 0) {
			}else if (this.goalsByCoordinate.containsKey(loc)) {
				s.append(this.goalsByCoordinate.get(loc).getId());
				//s.append(this.goals[row][col]);
			}else if (this.wallMap.containsKey(loc)) {
				s.append("+");
			//} else if (row == this.agentRow && col == this.agentCol) {
			}else if (this.agentByCoordinate.containsKey(loc)) {
				s.append(this.agentByCoordinate.get(loc).getLabel());
			}else if(this.thisAgent.getLocation().theSamePlace(loc)){
				s.append(this.thisAgent.getLabel());
			}else
			{
				s.append(" ");
			}
		}
		s.append("\n");
	}
	return s.toString();
		


	//	throw new NotImplementedException();	
	}
	
	
	public HashMap<LocationXY, Goal>getGoalMap(){
		return this.goalMap;
	}
	
	public HashMap<LocationXY, Boolean> getWallMap(){
		return this.wallMap;
	}
	
	public static HashMap<LocationXY, Goal> getGoalMapsta(){
		return goalMapsta;
	}
        
        
	
	public static HashMap<LocationXY, Boolean> getWallMapsta(){
		return wallMapsta;
	}
	
	public static HashMap<LocationXY, Box> getBoxMapsta() {
		return boxMapsta;
	}
	
	public char checkCellBlock(LocationXY loc){
		if(this.agentMap.get(loc) != null)return 'a';
		else if(this.boxMap.get(loc) != null)return 'b';
		else if(this.wallMap.get(loc) != null)return 'w';
		else return 'f';
	}
		

}