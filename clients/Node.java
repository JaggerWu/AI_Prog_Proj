package clients;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import clients.Command.Dir;
import clients.Command.Type;
import java.util.List;
import java.util.Objects;

public class Node {
    private static final Random RND = new Random(1);

    public static int MAX_ROW;
    public static int MAX_COL;

    //public static int totalRows;
    //public static int totalCols;
    public HashMap<Character,Agent> agentbyID = new HashMap<>();
    public HashMap<Character,Goal> goalbyID = new HashMap<>();
    public HashMap<LocationXY, Box> boxbyID = new HashMap<>();

    public HashMap<LocationXY, Agent> agentMap = new HashMap<>();
    public HashMap<LocationXY, Box> boxMap = new HashMap<>();
    public HashMap<LocationXY, Goal> goalMap = new HashMap<>();
    public  static HashMap<LocationXY, Boolean> wallMap = new HashMap<>();
    /******************************************************/
    public static Agent agent0 = null;
    public static List<Agent> orignalAgents = new ArrayList<Agent>() ;
    public Agent thisAgent = null;
    public Box thisBox = null;
    public List<Agent> agents = new ArrayList<>();
    public HashMap<LocationXY, Agent> agentByCoordinate = new HashMap<>();

    public HashMap<LocationXY, Box> boxMapByLocation = new HashMap<>();
    private HashMap<Character, Box> boxMapByID = new HashMap<>();

   // private static HashMap<LocationXY, Box> boxMapByLocation1 = new HashMap<LocationXY, Box>();
   // private static HashMap<Character, Box> boxMapByID1 = new HashMap<Character, Box>();
    private static HashMap<LocationXY, Goal> goalMapByLocation = new HashMap<>();
    private static HashMap<Character, Goal> goalMapByID = new HashMap<>();
    private int f;
    /********************************************************/

    public static HashMap<Goal, HashMap<LocationXY, Integer>> goalDistance = new HashMap<>();
    public static HashMap<Box, HashMap<LocationXY, Integer>> boxDistance = new HashMap<>();

    public static HashMap<LocationXY, Goal> goalMapsta = new HashMap<>();
    public static HashMap<LocationXY, Boolean> wallMapsta = new HashMap<>();
    public static HashMap<LocationXY, Box> boxMapsta = new HashMap<>();

    public static HashMap<Goal,Integer> priority;

    public Agent currentagent = new Agent();
    public Box currentbox = new Box();
    public LocationXY currentcell = new LocationXY();
    public static char[][] color;	
    public Node parent;
    public Command action;
    private int g;	
    private int _hash = 0;

    public static void initNodeSize(int max_row, int max_col) {
        MAX_ROW = max_row;
        MAX_COL = max_col;
    }
    public Node(Node parent) {
        this.parent = parent;
        this.boxMapByLocation = new HashMap<LocationXY, Box>();
        if (parent == null) {
            g = 0;
        } else {
            g = parent.g() + 1;
        }
    }

    public Node (Node parent, Command.Type type, Command.Dir dir){

    }

    public static void addGoal(Goal goal) {
        Node.goalMapByLocation.put(goal.getLocation(), goal);
        Node.goalMapByID.put(goal.getId(), goal);
    }

    public static HashMap<LocationXY, Goal> getGoalByLocation() {
        return Node.goalMapByLocation;
    }

    public void addBox(Box box) {
        this.boxMapByLocation.put(box.getLocation(), box);
        this.boxMapByID.put(box.getId(), box);
    }

    public HashMap<LocationXY, Box> getBoxByLocation() {
        return this.boxMapByLocation;
    }
   /* 
    public static void addBox1(Box box) {
        Node.boxMapByLocation1.put(box.getLocation(), box);
        Node.boxMapByID1.put(box.getId(), box);
    }

    public static HashMap<LocationXY, Box> getBoxByLocation1() {
        return boxMapByLocation1;
    }
    */
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

    public boolean isGoalState() {
    	Agent orignalAgent = null;
    	for(Agent agent : Node.orignalAgents){
            if(agent.getLabel() == thisAgent.getLabel()){
                orignalAgent = agent;
            }
    	}
       // System.err.println("isGoalState orignalAgent location:  " + orignalAgent.getLocation());
       // System.err.println("isGoalState this agent location:  " + thisAgent.getLocation() + "  Agent:  " + thisAgent.getLabel() + "  isAgentBackMode: "+thisAgent.isAgentBackMode());
    	//System.err.println("this agent is go back mode :   "+thisAgent.isAgentBackMode());
        if(thisAgent.isClearMode()) {
            for(LocationXY cord : thisAgent.getClearCords()){
                Box box = boxMapByLocation.get(cord);
                if(thisAgent.getLocation().equals(cord)){
                    return false;
                } else if(box != null && box.getColor() != null && box.getColor().equals(thisAgent.getColor())){
                    return false;
                }
            }
            return true;
        }else if(SearchClient.agentGoback){
            //System.err.println("Agent trying to go back.");
            if(thisAgent.getLocation().getRow() == orignalAgent.getLocation().getRow() 
                    && thisAgent.getLocation().getCol() == orignalAgent.getLocation().getCol() ){
                //System.err.println("Agent back to orignal location.");
                return true;
            }
        } else {
            Goal goal = thisAgent.getCurrentSubGoal();
            Box box = boxMapByLocation.get(goal.getLocation());
            if (box != null && box.getId() == Character.toUpperCase(goal.getId())) {
                box.setInFinalPosition(true);
                return true;
            }
        }
        return false;
    }

    
    public boolean isClearState(ArrayList<LocationXY> locs){
    	System.err.println("Is clear state : " + thisAgent.getLabel() + " pos " + thisAgent.getLocation());
    	if(locs.contains(thisAgent.getLocation())){
    		return false;
    	}
    	if(thisBox == null)System.err.println("no box clear state");
    	if(thisBox != null && locs.contains(thisBox.getLocation())){
    		return false;
    	}
    	return true;
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
        for(LocationXY key : this.getBoxByLocation().keySet()) {
            copy.boxMapByLocation.put(key, this.boxMapByLocation.get(key));
            copy.boxMapByID.put(this.boxMapByLocation.get(key).getId(), this.boxMapByLocation.get(key));
        }
        for (Agent agent : this.agents){
            copy.agents.add(agent.clone());
            copy.agentMap.put(agent.clone().getLocation(), agent.clone());
        }
        return copy;
    }

    public void setF(int f) {
        this.f = f;
    }

    public HashMap<LocationXY, Goal>getGoalMap(){
        return this.goalMap;
    }

    public HashMap<LocationXY, Boolean> getWallMap(){
        return Node.wallMap;
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
    /******************************************************************/

    public static void computeGoalDistance() {
        for (Goal goal : Node.getGoalByLocation().values()) {
            HashMap<LocationXY, Integer> distanceMap = new HashMap<>();
            LinkedList<LocationXY> frontier = new LinkedList<>();
            HashSet<LocationXY> frontierHash = new HashSet<>();
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
        }       
    }

    public static void deleteDeadBox(Node currentState){
    	ArrayList<Box> tempBoxList = new ArrayList<Box>();
    	for(LocationXY location : currentState.getBoxByLocation().keySet()){
            Box box = currentState.getBoxByLocation().get(location);
            boolean isDeadBox = true ;
            for(Goal goal : Node.goalDistance.keySet()){
                if(goal.getId() == Character.toLowerCase(box.getId())){
                    //System.err.println("goal ID is " +goal.getId() + " ; location " + goal.getLocation());
                    HashMap<LocationXY, Integer> distanceMap = goalDistance.get(goal);
                    //System.err.println("distance + " + distanceMap.get(box.getLocation()));
                    if(distanceMap.get(box.getLocation()) != null){
                        isDeadBox = false;
                    }
                }
            }
            
            for(Agent agent : orignalAgents){
            	if(agent.getColor() == box.getColor()){
                    isDeadBox = false;
                }
            }
            
            if(isDeadBox){
            	tempBoxList.add(box);
            }
    	}
        for(Box box : tempBoxList){
            boxMapsta.remove(box.getLocation());
            //initialState.getBoxMapByLocation().remove(box.getLocation());
            currentState.getBoxByLocation().remove(box.getLocation());
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
        //System.err.println("Distance : " + distanceList);

        intializeEachGoalPriority(distanceList);//according to the distance, set each goal's priority 

        /*
         * If a goal is assumed as a wall, then see how many goals it blocked.
         * If the goal blocks other goals, then the goals priority is lower than all the goals it blocks
         * */
        boolean isConflictExists = false ; // for there is no conflict exists in single agent level
        for(int i=0; i<distanceList.size(); i++){
            if(i==0 || !Objects.equals(distanceList.get(i), distanceList.get(i-1)) ){ // if goals distances are the same, then avoid the duplication calculation
                Iterator<HashMap.Entry<Goal, Integer>> iterPriority = priority.entrySet().iterator();
                while(iterPriority.hasNext()){
                    Entry<Goal, Integer> enrtyPriority = iterPriority.next();
                    if(Objects.equals(enrtyPriority.getValue(), distanceList.get(i))){
                        Goal goal = enrtyPriority.getKey();
                        //System.err.println("Goal " + goal.getId() + "\'s intially priority is : " + goal.getPriority());
                        HashMap<LocationXY, Boolean> wallsTemp = (HashMap<LocationXY, Boolean>) Node.wallMapsta.clone();
                        wallsTemp.put(goal.getLocation(), true);
                        //System.err.println("Now the goal " + goal.getId() + " is assumed as wall !");
                        /*
                         * Calculating all distances from agent0 to other goals
                         */
                        HashMap<LocationXY, Integer> agent0DistanceMap = new HashMap<>();
                        distanceMatrix(wallsTemp, agent0DistanceMap);// generating distance matrix

                        for(int j=0; j<distanceList.size(); j++){
                            if(j==0 || !Objects.equals(distanceList.get(j), distanceList.get(j-1)) ){
                                Iterator<HashMap.Entry<Goal, Integer>> iterPriority1 = priority.entrySet().iterator();
                                while(iterPriority1.hasNext()){
                                    Entry<Goal, Integer> enrtyPriority1 = iterPriority1.next();
                                    if(Objects.equals(enrtyPriority1.getValue(), distanceList.get(j))){
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
                                            isConflictExists = true;
                                            //System.err.println("ATTENTION : The goal " + enrtyPriority1.getKey().getId() + " has been blocked!");

                                            /*
                                             * swift the priority of two goals
                                             * */
                                            if(goal.getPriority() >= enrtyPriority1.getKey().getPriority()){
                                                int temp = enrtyPriority1.getKey().getPriority();
                                                enrtyPriority1.getKey().setPriority(goal.getPriority());
                                                goal.setPriority(temp);
                                            }

                                        }
                                        if(!isConflictExists) {
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
                        //System.err.println("Goal " + goal.getId() + "\'s finally priority is : " + goal.getPriority());

                    }
                }
            }
        }

        /*
         * print goals' priority
         * */
        /*
        System.err.println();
        for(int i=0; i<distanceList.size(); i++){
            if(i==0 || !Objects.equals(distanceList.get(i), distanceList.get(i-1)) ){
                Iterator<HashMap.Entry<Goal, Integer>> iterPriority = priority.entrySet().iterator();
                while(iterPriority.hasNext()){
                    Entry<Goal, Integer> enrtyPriority = iterPriority.next();
                    if(Objects.equals(enrtyPriority.getValue(), distanceList.get(i))){
                            System.err.println("Goal " + enrtyPriority.getKey().getId() + "\'s final priority " + enrtyPriority.getKey().getPriority());
                    }
                }
            }
        }*/
    }

    private static void distanceMatrix(HashMap<LocationXY, Boolean> wallsTemp,
        HashMap<LocationXY, Integer> distanceMap) {
            LinkedList<LocationXY> frontier = new LinkedList<>();
            HashSet<LocationXY> frontierHash = new HashSet<>();
            // initially put only the goal in the distance map
            distanceMap.put(agent0.getLocation(), 0);
            // and the goal's neighbours (4-vicinity) in the frontier
            for (LocationXY coordinate : agent0.getLocation().getNeighbours()) {
                if (wallsTemp.get(coordinate) == null && 
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
                    if (wallsTemp.get(neighbour) == null && 
                            neighbour.getRow() > -1 && neighbour.getRow() < Node.MAX_ROW &&
                            neighbour.getCol() > -1 && neighbour.getCol() < Node.MAX_COL &&
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
    private static void intializeEachGoalPriority(ArrayList<Integer> distanceList) {
        for(int i=0; i<distanceList.size(); i++){
            if(i==0 || !Objects.equals(distanceList.get(i), distanceList.get(i-1)) ){ //Avoid duplication calculation of the same distances goals
                Iterator<HashMap.Entry<Goal, Integer>> iterPriority = priority.entrySet().iterator();
                int tempCount = 0;
                while(iterPriority.hasNext()){
                    Entry<Goal, Integer> enrtyPriority = iterPriority.next();
                    if(Objects.equals(enrtyPriority.getValue(), distanceList.get(i))){
                        //enrtyPriority.getKey().setPriority(i+1);//set priority as the index of the distance list
                        //enrtyPriority.getKey().setPriority(distanceList.size()+ tempCount - i);//set priority as the index of the distance list in reverse order
                        enrtyPriority.getKey().setPriority(distanceList.get(i)*10+ tempCount);//set priority as the index of the distance list in reverse order
                        tempCount ++ ;
                        //System.err.println("Goal " + enrtyPriority.getKey().getId() + "\'s priority " + enrtyPriority.getKey().getPriority());
                    }
                }
            }
        }
    }

    private static void goalDistanceCalculationOfAgent0(ArrayList<Integer> distanceList) {
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
                    //System.err.println("Distance from goal to Agent 0 : Goal -> " + goal.getId() + " , distance -> " + distance);
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

    public ArrayList<Node> getExpandedNodes() {
        ArrayList<Node> expandedNodes = new ArrayList<>(Command.EVERY.length);
        for (Command command : Command.EVERY) {
            // Determine applicability of action
            int newAgentRow = thisAgent.getLocation().getRow() + Command.dirToRowChange(command.dir1);
            int newAgentCol = thisAgent.getLocation().getCol() + Command.dirToColChange(command.dir1);
            if (command.actionType != null) switch (command.actionType) {
                case Move:
                    // Check if there's a wall or box on the cell to which the agent is moving
                    if (cellIsFree(newAgentRow, newAgentCol)) {
                        Node n = this.ChildNode();
                        n.action = command;
                        n.thisAgent.getLocation().setRow(newAgentRow);
                        n.thisAgent.getLocation().setCol(newAgentCol);
                        n.agentMap.remove(this.thisAgent.getLocation());
                        n.agentMap.put(n.thisAgent.getLocation(), n.thisAgent);
                        n.agentbyID.remove(this.thisAgent.getLabel());
                        n.agentbyID.put(n.thisAgent.getLabel(), n.thisAgent);
                        expandedNodes.add(n);
                    }   break;
                case Push:
                    // Make sure that there's actually a box to move
                    if (boxAt(newAgentRow, newAgentCol)) {
                        int newBoxRow = newAgentRow + Command.dirToRowChange(command.dir2);
                        int newBoxCol = newAgentCol + Command.dirToColChange(command.dir2);
                        // .. and that new cell of box is free
                        //System.err.println(this.boxMapByLocation.get(new LocationXY(newAgentRow, newAgentCol)).getId());
                        //System.err.println(this.thisAgent.getColor());
                        if (cellIsFree(newBoxRow, newBoxCol) && this.boxMapByLocation.get(new LocationXY(newAgentRow, newAgentCol)).getColor().equals(thisAgent.getColor())) {
                            Node n = this.ChildNode();
                            n.action = command;
                            n.thisAgent.getLocation().setRow(newAgentRow);
                            n.thisAgent.getLocation().setCol(newAgentCol);
                            Box boxToMove = this.boxMapByLocation.get(
                                    new LocationXY(newAgentRow, newAgentCol));
                            Box boxToMoveCopy = new Box(boxToMove.getId(), boxToMove.getColor(),
                                    new LocationXY(newBoxRow, newBoxCol), boxToMove.isBoxInFinalPosition());
                            n.boxMapByLocation.put(new LocationXY(newBoxRow, newBoxCol), boxToMoveCopy);
                            n.boxMapByLocation.remove(new LocationXY(newAgentRow, newAgentCol));
                            n.boxMapByID.remove(boxToMoveCopy.getId());
                            n.boxMapByID.put(boxToMoveCopy.getId(), boxToMoveCopy);
                            n.agentMap.remove(this.thisAgent.getLocation());
                            n.agentMap.put(n.thisAgent.getLocation(), n.thisAgent);
                            n.agentbyID.remove(this.thisAgent.getLabel());
                            n.agentbyID.put(n.thisAgent.getLabel(), n.thisAgent);
                            if(this.thisBox != null){
                            	n.thisBox = boxToMoveCopy;
                            }
                            expandedNodes.add(n);
                        }
                    }
                    break;
                case Pull:
                    // Cell is free where agent is going
                    //System.err.println(newAgentRow + "," + newAgentCol);
                    //System.err.println((cellIsFree(newAgentRow-1, newAgentCol) + "," + wallMap.containsKey(new LocationXY(newAgentRow, newAgentCol-1)) +""+ wallMap.containsKey(new LocationXY(newAgentRow, newAgentCol+1))));
                    if (cellIsFree(newAgentRow, newAgentCol) /*&& 
                            !(cellIsFree(newAgentRow-1, newAgentCol) && wallMap.containsKey(new LocationXY(newAgentRow, newAgentCol-1)) && wallMap.containsKey(new LocationXY(newAgentRow, newAgentCol+1))) &&
                            !(cellIsFree(newAgentRow+1, newAgentCol) && wallMap.containsKey(new LocationXY(newAgentRow, newAgentCol-1)) && wallMap.containsKey(new LocationXY(newAgentRow, newAgentCol+1))) &&
                            !(cellIsFree(newAgentRow, newAgentCol-1) && wallMap.containsKey(new LocationXY(newAgentRow-1, newAgentCol)) && wallMap.containsKey(new LocationXY(newAgentRow+1, newAgentCol))) &&
                            !(cellIsFree(newAgentRow, newAgentCol+1) && wallMap.containsKey(new LocationXY(newAgentRow-1, newAgentCol)) && wallMap.containsKey(new LocationXY(newAgentRow+1, newAgentCol)))*/ ) {
                        int boxRow = this.thisAgent.getLocation().getRow() + Command.dirToRowChange(command.dir2);
                        int boxCol = this.thisAgent.getLocation().getCol() + Command.dirToColChange(command.dir2);
                        // .. and there's a box in "dir2" of the agent
                        if (boxAt(boxRow, boxCol)  && this.boxMapByLocation.get(new LocationXY(boxRow, boxCol)).getColor().equals(thisAgent.getColor())) {
                            Node n = this.ChildNode();
                            n.action = command;
                            // TODO: eventually refactor with clone()
                            Box boxToMove = this.boxMapByLocation.get(
                                    new LocationXY(boxRow, boxCol));
                            Box boxToMoveCopy = new Box(boxToMove.getId(), boxToMove.getColor(),
                                    new LocationXY(this.thisAgent.getLocation().getRow(),
                                            this.thisAgent.getLocation().getCol()), boxToMove.isBoxInFinalPosition());

                            n.boxMapByLocation.put(new LocationXY(this.thisAgent.getLocation().getRow(),
                                    this.thisAgent.getLocation().getCol()), boxToMoveCopy);
                            n.boxMapByLocation.remove(new LocationXY(boxRow, boxCol));
                            n.boxMapByID.remove(boxToMoveCopy.getId());
                            n.boxMapByID.put(boxToMoveCopy.getId(), boxToMoveCopy);
                            n.thisAgent.getLocation().setRow(newAgentRow);
                            n.thisAgent.getLocation().setCol(newAgentCol);
                            n.thisAgent.getLocation().setRow(newAgentRow);
                            n.thisAgent.getLocation().setCol(newAgentCol);
                            n.agentMap.remove(this.thisAgent.getLocation());
                            n.agentMap.put(n.thisAgent.getLocation(), n.thisAgent);
                            n.agentbyID.remove(this.thisAgent.getLabel());
                            n.agentbyID.put(n.thisAgent.getLabel(), n.thisAgent);
                            if(this.thisBox != null){
                            	n.thisBox = boxToMoveCopy;
                            }
                            expandedNodes.add(n);
                        }
                    }   
                    break;
                default:
                    break;
            }
        }
        return expandedNodes;
    }
/**
 * @param commands*      
 * @param serverOutput*      
 * @param client*      
 * @return **********************************************/     
    
    public boolean changeState(Command[] commands, String[] serverOutput, SearchClient client){
        for(int i = 0; i < commands.length; i++){
            Agent activeAgent = this.getAgentById(Integer.toString(i).charAt(0));
            if(commands[i] != null && !serverOutput[i].equals("false")) {
                int newAgentRow = activeAgent.getLocation().getRow() + Command.dirToRowChange(commands[i].dir1);
                int newAgentColumn = activeAgent.getLocation().getCol() + Command.dirToColChange(commands[i].dir1);
                LocationXY newPos = new LocationXY(newAgentRow, newAgentColumn);
                if (commands[i].actionType != null) switch (commands[i].actionType) {
                    case Pull:
                        int boxRow = activeAgent.getLocation().getRow() + Command.dirToRowChange(commands[i].dir2);
                        int boxCol = activeAgent.getLocation().getCol() + Command.dirToColChange(commands[i].dir2);
                        //System.err.println("Agent: " + activeAgent.getColor());
                        //System.err.println("Box: " + boxMapByLocation.get(new LocationXY(boxRow, boxCol)).getColor());
                        //System.err.println("Equal: " + boxMapByLocation.get(new LocationXY(boxRow, boxCol)).getColor().equals(activeAgent.getColor()));
                        if (boxAt(boxRow, boxCol) && boxMapByLocation.get(new LocationXY(boxRow, boxCol)).getColor() == null || boxAt(boxRow, boxCol) && boxMapByLocation.get(new LocationXY(boxRow, boxCol)).getColor().equals(activeAgent.getColor())) {
                            //System.err.println("Agent: " + activeAgent.getColor());
                            //System.err.println("Box: " + boxMapByLocation.get(new LocationXY(boxRow, boxCol)).getColor());
                            //System.err.println("Equal: " + boxMapByLocation.get(new LocationXY(boxRow, boxCol)).getColor().equals(activeAgent.getColor()));
                            Box pullBox = this.boxMapByLocation.get(new LocationXY(boxRow, boxCol));
                            Box pullBoxNew = new Box(pullBox.getId(), pullBox.getColor(), activeAgent.getLocation());
                            boxMapByLocation.remove(pullBox.getLocation());

                            if(activeAgent.getCurrentSubGoal() != null && activeAgent.getCurrentSubGoal().getLocation().equals(pullBoxNew.getLocation()) && !activeAgent.isClearMode()) {
                               
                                pullBoxNew.setInFinalPosition(true);
                                //System.err.println("Set Box " + pullBoxNew.getId() + " in final position");
                            }
                            activeAgent.setCoordinate(newPos);
                            boxMapByLocation.put(pullBoxNew.getLocation(), pullBoxNew);
                            for(Goal goal : goalMapByLocation.values()){
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
                        } else if(boxMapByLocation.get(new LocationXY(boxRow, boxCol)).getColor() == null || boxMapByLocation.get(new LocationXY(boxRow, boxCol)).getColor().equals(activeAgent.getColor())){
                            System.err.println("No box at: " + newPos.toString());
                            System.err.println("Client State corrupted");
                            return false;
                        }   break;
                    case Push:
                        int newBoxRow = newAgentRow + Command.dirToRowChange(commands[i].dir2);
                        int newBoxCol = newAgentColumn + Command.dirToColChange(commands[i].dir2);
                        if (boxAt(newPos.getRow(), newPos.getCol()) && boxMapByLocation.get(newPos).getColor() == null || boxAt(newPos.getRow(), newPos.getCol()) && boxMapByLocation.get(newPos).getColor() != null && boxMapByLocation.get(newPos).getColor().equals(activeAgent.getColor())) {
                            Box pushBox = this.boxMapByLocation.get(newPos);
                            Box pushBoxNew = new Box(pushBox.getId(), pushBox.getColor(), new LocationXY(newBoxRow, newBoxCol));
                            boxMapByLocation.remove(pushBox.getLocation());
                            activeAgent.setCoordinate(newPos);
                            if(activeAgent.getCurrentSubGoal() != null && activeAgent.getCurrentSubGoal().getLocation().equals(pushBoxNew.getLocation()) && !activeAgent.isClearMode()) {
                                pushBoxNew.setInFinalPosition(true);

                                System.err.println("Set Box " + pushBoxNew.getId() + " in final position");
                            }
                            boxMapByLocation.put(pushBoxNew.getLocation(), pushBoxNew);
                            for(Goal goal : goalMapByLocation.values()){
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
                        } else if(boxMapByLocation.get(newPos).getColor() == null ||  boxMapByLocation.get(newPos).getColor().equals(activeAgent.getColor())){
                            System.err.println("No box at: " + newPos.toString());
                            System.err.println("Client state corrupted");
                            return false;
                        }   break;
                    default:
                        boolean movePossible = true;
                        for(Agent a : agents){
                            if(a.getLabel() != activeAgent.getLabel() && a.getLocation().equals(newPos)){
                                movePossible = false;
                            }
                        }   if (movePossible){
                            activeAgent.setCoordinate(newPos);
                        }   break; 
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
                } else if (this.boxMapByLocation.get(new LocationXY(i, j)) != null) {
                    builder.append(this.boxMapByLocation.get(new LocationXY(i, j)).getId());
                } else if (Node.goalMapByLocation.get(new LocationXY(i, j)) != null) {
                    builder.append(Node.goalMapByLocation.get(new LocationXY(i, j)).getId());
                } else {
                    builder.append(' ');
                }
            }
            builder.append('\n');
        }
        System.err.print(builder.toString());
    }
   
    
    public ArrayList<LocationXY> commandToLocations(LocationXY startPos, Command command){
        ArrayList<LocationXY> retArr = new ArrayList<>();
        LocationXY newAgentPos = new LocationXY(startPos.getRow() + Command.dirToRowChange(command.dir1), startPos.getCol() + Command.dirToColChange(command.dir1));
        retArr.add(newAgentPos);
        if(command.actionType == Type.Push){
            LocationXY newBoxPos = new LocationXY(newAgentPos.getRow() + Command.dirToRowChange(command.dir2), newAgentPos.getCol() + Command.dirToColChange(command.dir2));
            retArr.add(newBoxPos);
        } else if(command.actionType == Type.Pull){
            LocationXY newBoxPos = new LocationXY(startPos.getRow() + Command.dirToRowChange(command.dir2), startPos.getCol() + Command.dirToColChange(command.dir2));
            retArr.add(newBoxPos);
        }
        return retArr;
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

    public boolean cellIsFree(int row, int col) {
        if(thisAgent.isClearMode()) {
            for (Agent agent : agents) {
                if (agent.getLabel() != thisAgent.getLabel() && agent.getLocation().equals(new LocationXY(row, col))) {
                    //System.err.println("agent here ");
                    return false;
                }
            }
        }
        Box box = boxMapByLocation.get(new LocationXY(row, col));
        boolean noBox;
        if(box == null//){
                || box.getColor() != null && !box.getColor().equals(thisAgent.getColor())){
            //System.err.println("box not here");
            noBox = true;
        } else {
            //System.err.println("box here");
            noBox = false;
        }
        return (Node.wallMap.get(new LocationXY(row, col)) == null && noBox);
    }

    private boolean boxAt(int row, int col) {
        return boxMapByLocation.containsKey(new LocationXY(row, col));
    }

    private Node ChildNode() {
        Node copy = new Node(this);
        for (LocationXY key : this.boxMapByLocation.keySet()) {
            copy.boxMapByLocation.put(key, this.boxMapByLocation.get(key));
            copy.boxMapByID.put(this.boxMapByLocation.get(key).getId(), this.boxMapByLocation.get(key));
        }
        for (Agent agent : this.agents) {
            copy.agents.add(agent.clone());
        }
        copy.thisAgent = this.thisAgent.clone();
        if(this.thisBox != null)copy.thisBox = new Box(thisBox.getId(), thisBox.getColor(), thisBox.getLocation(), thisBox.isBoxInFinalPosition());
        return copy;
    }

    public LinkedList<Node> extractPlan() {
            LinkedList<Node> plan = new LinkedList<>();
            Node n = this;
            while (!n.isInitialState()) {
                    Filewriter.giveittostring(n.action.toString());
                    plan.addFirst(n);
                    n = n.parent;
            }
            Filewriter.printtotxt();
            return plan;
    }

    @Override
    public int hashCode() {
        if(this._hash == 0){
            final int prime = 31;
            int result = 1;
            for(Map.Entry<LocationXY, Agent> entry : this.agentMap.entrySet()){
                result = prime * result + entry.getKey().getCol();
                result = prime * result + entry.getKey().getRow();
            }
            for(Map.Entry<LocationXY, Box> entry : this.boxMapByLocation.entrySet()){
                result = prime * result + entry.getKey().getCol();
                result = prime * result + entry.getKey().getRow();
            }
            for(Map.Entry<LocationXY, Goal> entry : this.goalMapByLocation.entrySet()){
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
        for(Map.Entry<LocationXY, Agent> entry : this.agentMap.entrySet()){
            if(other.agentMap.containsKey(entry.getKey())){
                if(entry.getValue().getLabel() != other.agentMap.get(entry.getKey()).getLabel())return false;
            }
            else return false;
        }
        if(this.boxMapByLocation.size() != other.boxMapByLocation.size()){
            return false;
        }
        for(Map.Entry<LocationXY, Box> entry : this.boxMapByLocation.entrySet()){
            if(other.boxMapByLocation.containsKey(entry.getKey())){
                if(entry.getValue().getId() != other.boxMapByLocation.get(entry.getKey()).getId())return false;
            }
            else return false;
        }
        if(this.goalMapByLocation.size() != other.goalMapByLocation.size()){
            return false;
        }
        for(Map.Entry<LocationXY, Goal> entry : this.goalMapByLocation.entrySet()){
            if(other.goalMapByLocation.containsKey(entry.getKey())){
                if(entry.getValue().getId() != other.goalMapByLocation.get(entry.getKey()).getId())return false;
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
        if(this.thisBox != null){
        	if(this.thisBox.getLocation().theSamePlace(other.thisAgent.getLocation())){
        		return false;
        	}
        }
        return true;	
    }

    @Override
    public String toString() {
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
                if (this.boxMapByLocation.containsKey(loc)) {
                    s.append(this.boxMapByLocation.get(loc).getId());
                //} else if (this.goals[row][col] > 0) {
                }else if (this.goalMapByLocation.containsKey(loc)) {
                    s.append(this.goalMapByLocation.get(loc).getId());
                    //s.append(this.goals[row][col]);
                }else if (this.wallMap.containsKey(loc)) {
                    s.append("+");
                //} else if (row == this.agentRow && col == this.agentCol) {
                }else if (this.agentMap.containsKey(loc)) {
                    s.append(this.agentMap.get(loc).getLabel());
                }else if(this.thisAgent.getLocation().theSamePlace(loc)){
                    s.append(this.thisAgent.getLabel());
                }else if(this.thisBox != null && this.thisAgent.getLocation().theSamePlace(loc)){
                	s.append(this.thisBox.getId());
                }else
                {
                    s.append(" ");
                }
            }
            s.append("\n");
        }
        return s.toString();
    }	
}