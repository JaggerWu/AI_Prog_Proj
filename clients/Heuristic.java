package clients;

import java.util.Comparator;
import java.util.HashMap;

public abstract class Heuristic implements Comparator<Node> {
    
    public Node initialState;
    public Heuristic(Node initialState) {
        // Here's a chance to pre-process the static parts of the level.
        this.initialState = initialState;
    }

    public int h(Node n) {
        if(SearchClient.agentGoback) {
            //System.err.println("I am -----------------------------------------");
            return agentHeuristic(n);
        } else if(n.thisAgent.isClearMode()){
            return clearModeHeuristic(n);
        } else {
            return manhattanSubgoalHeuristic(n);
            //return manhattanSubgoalHeuristic(n);
        }
    }

    public abstract int f(Node n);

    public int manhattanSubgoalHeuristic(Node n){
        int HeuristicDistance = 0;
        Goal currentSubGoal = null;
        //Find current subgoal.
        currentSubGoal = n.thisAgent.getCurrentSubGoal();
        if(currentSubGoal != null){
            Box targetBox = null;
            // find the closest box to the currenSubGoal which can be solved by this Agent.
            int boxToSubGoalDistance = Integer.MAX_VALUE;
            for(Box box : n.getBoxByLocation().values()){
                if(!box.isBoxInFinalPosition() && box.getId() == Character.toUpperCase(currentSubGoal.getId())){
                    //System.err.println("heuristic current subgoal : " + currentSubGoal.getId() + " ; Location is : " + currentSubGoal.getLocation());
                    //System.err.println("heuristic current box : " + box.getId() + " ; Location is : " + box.getLocation());
                    for(Goal goal : Node.goalDistance.keySet()){
                    	if(goal.getId() == currentSubGoal.getId() 
                    			&& goal.getLocation().getRow() == currentSubGoal.getLocation().getRow()
                    			&& goal.getLocation().getCol() == currentSubGoal.getLocation().getCol()){
                            HashMap<LocationXY, Integer> goalMap = Node.goalDistance.get(currentSubGoal);
                            if(goalMap.get(box.getLocation()) != null){
                                int dist = goalMap.get(box.getLocation());
                                if(dist < boxToSubGoalDistance){
                                    boxToSubGoalDistance = dist;
                                    targetBox = box; 
                                }
                            }
                    		
                    	}
                    }   
                } 
            }
            Command command = n.action;
            if(targetBox != null){
                HeuristicDistance =(Math.abs(n.thisAgent.getLocation().getRow() - targetBox.getLocation().getRow()) +
                            Math.abs(n.thisAgent.getLocation().getCol() - targetBox.getLocation().getCol())) + boxToSubGoalDistance;
                if(command != null && !(command.actionType == Command.Type.Move)){
                    int boxRow = n.thisAgent.getLocation().getRow() + Command.dirToRowChange(command.dir2);
                    int boxCol = n.thisAgent.getLocation().getCol() + Command.dirToColChange(command.dir2);

                    if(!(boxRow == targetBox.getLocation().getRow() && boxCol == targetBox.getLocation().getCol() )){
                        HeuristicDistance += 4;//4
                        Box otherBox = n.getBoxByLocation().get(new LocationXY(boxRow, boxCol));
                        if(otherBox != null && otherBox.isBoxInFinalPosition()){
                            HeuristicDistance += 100;//100
                        }
                    }
                }
                if(command != null && (command.actionType == Command.Type.Pull)){
                    int AgentRow = n.thisAgent.getLocation().getRow();
                    int AgentCol = n.thisAgent.getLocation().getCol();
                    
                    int targetBoxNeighbouberRow = targetBox.getLocation().getRow();
                    int targetBoxNeighbouberCol = targetBox.getLocation().getCol();
                    if( !((Node.wallMap.containsKey(new LocationXY(targetBoxNeighbouberRow-1, targetBoxNeighbouberCol))) && (Node.wallMap.containsKey(new LocationXY(targetBoxNeighbouberRow+1, targetBoxNeighbouberCol)))) && Node.wallMap.containsKey(new LocationXY(AgentRow-1, AgentCol)) && Node.wallMap.containsKey(new LocationXY(AgentRow+1, AgentCol))){
                        HeuristicDistance += 5;
                    }
                
                    if( !((Node.wallMap.containsKey(new LocationXY(targetBoxNeighbouberRow, targetBoxNeighbouberCol-1))) && (Node.wallMap.containsKey(new LocationXY(targetBoxNeighbouberRow, targetBoxNeighbouberCol+1)))) && Node.wallMap.containsKey(new LocationXY(AgentRow, AgentCol-1)) && Node.wallMap.containsKey(new LocationXY(AgentRow, AgentCol+1))){
                        HeuristicDistance += 5;
                    }
                }
                
                for(Agent a : n.agents){
                    if(a.getLabel() != n.thisAgent.getLabel() && a.getLocation().equals(n.thisAgent.getLocation())){
                        HeuristicDistance += 20;//20  //50
                    }
                }
                Box possibleBox = n.getBoxByLocation().get(n.thisAgent.getLocation());
                if(possibleBox != null){
                    HeuristicDistance += 20;//20
                    if(possibleBox.isBoxInFinalPosition()){
                        HeuristicDistance += 200;//200
                    }
                }
                
            } else {
                if(!n.isGoalState()) {
                    System.err.println("Heuristics: No free box can be used to solve current sub goal.");
                }
            }
        } else {
            System.err.println("Heuristics: No goal found.");
        }
        //System.err.println("HeuristicDistance:  " + HeuristicDistance);
        return HeuristicDistance;
    }
    
    public int agentHeuristic(Node n){
       // System.err.println("This is agent heuristic ??????");
        int heuristicDistance = 0;
        Agent orginalAgent = null ;
        for(Agent agent : Node.orignalAgents){
            if(agent.getLabel() == n.thisAgent.getLabel()){
                orginalAgent = agent;
            }
        }
       // System.err.println("This is thisAgent " + n.thisAgent.getLocation());
       // System.err.println("This is orignalAgent " + orginalAgent.getLocation());
        heuristicDistance =Math.abs(n.thisAgent.getLocation().getRow() - orginalAgent.getLocation().getRow()) +
                            Math.abs(n.thisAgent.getLocation().getCol() - orginalAgent.getLocation().getCol());
        Command command = n.action;
        if(command != null && !(command.actionType == Command.Type.Move)){
            heuristicDistance += 10;
        }
       // System.err.println("heuristicDistance = " + heuristicDistance);
        return heuristicDistance;
    }
    
    public int clearModeHeuristic(Node n){
        int HeuristicDistance = 1;
        boolean notTargetBox = true;
        for(LocationXY location : n.thisAgent.getClearCords()){
            Box box = n.getBoxByLocation().get(location);
            if(box != null && box.getColor() != null && !box.getColor().equals(n.thisAgent.getColor())){
                HeuristicDistance += 20;
                HeuristicDistance += Math.abs(n.thisAgent.getLocation().getRow() - box.getLocation().getRow()) +
                                        Math.abs(n.thisAgent.getLocation().getCol() - box.getLocation().getCol());
                Command comand = n.action;
                if(comand != null && !(comand.actionType == Command.Type.Move)){
                    //if the agent is moving boxes that is not the target box, the heuristic is worsened.
                    int boxRow = n.thisAgent.getLocation().getRow() + Command.dirToRowChange(comand.dir2);
                    int boxCol = n.thisAgent.getLocation().getCol() + Command.dirToColChange(comand.dir2);
                    if((boxRow == box.getLocation().getRow() && boxCol == box.getLocation().getCol() )){
                        notTargetBox = false;
                    }
                }
            }
            if(location.equals(n.thisAgent.getLocation())){
                    HeuristicDistance += 10;
            }
            if(notTargetBox){
                HeuristicDistance += 20;
            }
        }
        Box possibleBox = n.getBoxByLocation().get(n.thisAgent.getLocation());
        if(possibleBox != null){
            HeuristicDistance += 4;
        }
        for(Agent a : n.agents){
            if(a.getLabel() != n.thisAgent.getLabel() && a.getLocation().equals(n.thisAgent.getLocation())){
                HeuristicDistance += 2;
            }
        }
        Command com2 = n.action;
        if(com2 != null && com2.actionType != Command.Type.Move){
            int boxRow = n.thisAgent.getLocation().getRow() + Command.dirToRowChange(com2.dir2);
            int boxCol = n.thisAgent.getLocation().getCol() + Command.dirToColChange(com2.dir2);
            possibleBox = n.getBoxByLocation().get(new LocationXY(boxRow, boxCol));
            if(possibleBox != null){
                HeuristicDistance += 4;
                if(possibleBox.isBoxInFinalPosition()){
                    HeuristicDistance += 50;
                }
            }
        }
        return HeuristicDistance;
    }

    @Override
    public int compare(Node n1, Node n2) {
        return this.f(n1) - this.f(n2);
    }

    public static class AStar extends Heuristic {
        public AStar(Node initialState) {
            super(initialState);
        }

        @Override
        public int f(Node n) {
            return n.g() + this.h(n);
        }

        @Override
        public String toString() {
            return "A* evaluation";
        }
    }

    public static class WeightedAStar extends Heuristic {
        private int W;

        public WeightedAStar(Node initialState, int W) {
                super(initialState);
                this.W = W;
        }

        @Override
        public int f(Node n) {
                return n.g() + this.W * this.h(n);
        }

        @Override
        public String toString() {
                return String.format("WA*(%d) evaluation", this.W);
        }
    }

    public static class Greedy extends Heuristic {
        public Greedy(Node initialState) {
                super(initialState);
        }

        @Override
        public int f(Node n) {
                return this.h(n);
        }

        @Override
        public String toString() {
                return "Greedy evaluation";
        }
    }
}
