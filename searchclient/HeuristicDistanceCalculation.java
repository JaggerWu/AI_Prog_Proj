package searchclient;

import java.lang.Math;
import java.lang.Character;
import java.util.HashMap;
import java.util.LinkedList;

import searchclient.NotImplementedException;


public class HeuristicDistanceCalculation {

    static HeuristicDistanceCalculation instance;

    private int[][] distancesMap;
    private HashMap<Character, LinkedList<Integer>> goals;

    private HeuristicDistanceCalculation(Node initialState) {
        distancesMap = createManhattanDistanceMatrix();
        initGoalLocations(initialState);
    }

    public static void initHeuristic(Node initialState) {
        if(instance == null) {
            instance = new HeuristicDistanceCalculation(initialState);
        }
    }

    private void initGoalLocations(Node initialState) {
    	throw new NotImplementedException();
    	
    	/*
    	 * Waiting for changing
    	 */
    	
    	/*
    	
        goals = new HashMap<Character, LinkedList<Integer>>();

        // Populate goals
        for(int r = 0;  r < Node.MAX_ROW; r++) {
            for(int c = 0; c < Node.MAX_COL; c++) {
                //char goalChr = initialState.goals[r][c];
            	char goalChr;
                if(initialState.b.getLocation().getX() == r && initialState.b.getLocation().getY() == c)goalChr = initialState.b.getId();
            	else goalChr = '\u0000';
                if(goalChr != '\u0000') {
                    int index = HeuristicDistanceCalculation.coordinateToIndex(r, c);
                    if (goals.get(goalChr) == null) {
                        LinkedList<Integer> newList = new LinkedList<Integer>();
                        newList.add(index);
                        goals.put(goalChr, newList);
                    }
                    else {
                        goals.get(goalChr).add(index);
                    }
                }
            }
        }
        */
    	
    	
    }
	
	// convert coordinate to index.
    private static int coordinateToIndex(int row, int col) {
        return row * Node.MAX_COL + col;
    }
	
	// convert index to coordinate.
    private static int[] indexToCoordinate(int index) {
        int[] result = new int[2];
        result[0] = index / Node.MAX_COL; // row
        result[1] = index % Node.MAX_COL; // col
        return result;
    }

    public static int agentToClosestBox(Node n) {
    	throw new NotImplementedException();
    	/*
    	 * Waiting for changing.
    	 */
    	
    	//return findTheClosestBox(n.agentRow, n.agentCol, n) - 1;
        //return findTheClosestBox(n.a.getLocation().getX(), n.a.getLocation().getY(), n) - 1;
    }

    public static int sumAllGoalsToClosestBox(Node n) {
        int totalDistance = 0;
        for (char goal : instance.goals.keySet()) {
            LinkedList<Integer> indexs = instance.goals.get(goal);
            for (int i=0; i<indexs.size(); i++) {
                int[] cellLocation = HeuristicDistanceCalculation.indexToCoordinate(indexs.get(i));
                int distance = findTheClosestBox(cellLocation[0], cellLocation[1], n, goal);
                totalDistance += distance;
            }
        }
        return totalDistance;

    }

    private static int findTheClosestBox(int row, int col, Node n) {
        return findTheClosestBox(row, col, n, '0');
    }

    private static int findTheClosestBox(int srcRow, int srcCol, Node n, char targetChr) {
    	throw new NotImplementedException();
    	
    	/*
         * Waiting for changing
         */
    	
    	
    	/*
    	targetChr = Character.toUpperCase(targetChr);
        int minDistance = Node.MAX_ROW + Node.MAX_COL;
        for (int r = 0; r < Node.MAX_ROW; r++) {
            for (int c = 0; c < Node.MAX_COL; c++) {
                //char box = n.boxes[r][c];
            	char box;
            	if(n.b.getLocation().getX() == r && n.b.getLocation().getY() == c)box = n.b.getId();
            	else box = '\u0000';
                //if ((targetChr == '0' && box != '\u0000') || box  == targetChr) {
                if ((targetChr == '0' && box != '\u0000') || box  == targetChr) {
                    int distance = instance.distancesMap[coordinateToIndex(r, c)][coordinateToIndex(srcRow, srcCol)];
                    if (distance < minDistance) {
                        minDistance = distance;
                    }
                }
            }
        }
        return minDistance;
        
        */
    }

	// Create Manhattan distances between each node.
    private int[][] createManhattanDistanceMatrix() {
        int distanceMatrixSize = Node.MAX_ROW * Node.MAX_COL;
        int[][] distancesMatrix = new int[distanceMatrixSize][distanceMatrixSize];
        for(int u = 0; u < distanceMatrixSize; u++) {
            for(int v = 0; v < distanceMatrixSize; v++) {
                int[] cell_1_Coordinate = indexToCoordinate(u);
                int[] cell_2_Coordinate = indexToCoordinate(v);
				distancesMatrix[u][v] = Math.abs(cell_2_Coordinate[0]-cell_1_Coordinate[0]) + Math.abs(cell_2_Coordinate[1]-cell_1_Coordinate[1]);
            }

        }
        return distancesMatrix;
    }
    
    public int manhattanSubgoalHeuristic(Node n){
        int HeuristicDistance = 0;
        Goal currentSubGoal = null;
        //Find current main subgoal.
        currentSubGoal = n.thisAgent.getCurrentSubGoal();

        if(currentSubGoal != null){
            //find closest box that can be used to solve
            int boxDistance = Integer.MAX_VALUE;
            Box targetBox = null;
            for(Box box : n.getBoxesByCoordinate().values()){
                if(!box.isInFinalPosition() && box.getId() == Character.toUpperCase(currentSubGoal.getId())){
                    HashMap<LocationXY, Integer> goalMap = Node.goalDistance.get(currentSubGoal);
                    int dist = goalMap.get(box.getLocation());
                    if(dist < boxDistance){
                        targetBox = box;
                        boxDistance = dist;
                    }
                }   
            }
            Command com = n.action;
            if(targetBox != null){
            //calculate heuristic. There is extra weight in moving the box closer to its goal.
            HeuristicDistance = (10*boxDistance) + Math.abs(n.thisAgent.getLocation().getRow() - targetBox.getLocation().getRow()) +
                        Math.abs(n.thisAgent.getLocation().getCol() - targetBox.getLocation().getCol());
            if(com != null && !(com.actionType == Command.Type.Move)){

                //if the agent is moving boxes that is not the target box, the heuristic is worsened.
                int boxRow = n.thisAgent.getLocation().getRow() + com.dirToRowChange(com.dir2);
                int boxCol = n.thisAgent.getLocation().getCol() + com.dirToColChange(com.dir2);
                if(!(boxRow == targetBox.getLocation().getRow() && boxCol == targetBox.getLocation().getCol() )){
                        HeuristicDistance += 30;
                        Box otherBox = n.getBoxesByCoordinate().get(new LocationXY(boxRow, boxCol));
                        if(otherBox != null && otherBox.isInFinalPosition()){
                                HeuristicDistance += 1000;
                        }
                }
        }
        /*Box possibleBox = n.getBoxesByCoordinate().get(n.thisAgent.getCoordinate());
        if(possibleBox != null){
                ret += 200;
                if(possibleBox.isInFinalPosition()){
                        ret += 2000;
                }
        }
        for(Agent a : n.agents){
                if(a.getId() != n.thisAgent.getId() && a.getCoordinate().equals(n.thisAgent.getCoordinate())){
                        ret += 200;
                    }
            }*/

            } else {
                if(!n.isGoalState()) {
                    System.err.println("Error calculating heuristics: No free box to solve current main sub-goal");
                }
            }
        } else {
            System.err.println("Error calculating heuristics: No goal is current main sub-goal");
        }
        return HeuristicDistance;
    }
}