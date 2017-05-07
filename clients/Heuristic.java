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
        if(!n.thisAgent.isClearMode()) {
            return manhattanSubgoalHeuristic(n);
        } else {
            return clearModeHeuristic(n);
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
                    HashMap<LocationXY, Integer> goalMap = Node.goalDistance.get(currentSubGoal);
                    int dist = goalMap.get(box.getLocation());
                    if(dist < boxToSubGoalDistance){
                        boxToSubGoalDistance = dist;
                        targetBox = box; 
                    }
                }   
            }
            Command command = n.action;
            if(targetBox != null){
                //calculate manhattan distance from this agent to targetBox + distance from targetBox to its goal.
                HeuristicDistance = Math.abs(n.thisAgent.getLocation().getRow() - targetBox.getLocation().getRow()) +
                            Math.abs(n.thisAgent.getLocation().getCol() - targetBox.getLocation().getCol()) + 10*boxToSubGoalDistance;
                if(command != null && !(command.actionType == Command.Type.Move)){
                    //if the agent is moving boxes that is not the target box, the heuristic is worsened.
                    int boxRow = n.thisAgent.getLocation().getRow() + Command.dirToRowChange(command.dir2);
                    int boxCol = n.thisAgent.getLocation().getCol() + Command.dirToColChange(command.dir2);
                    if(!(boxRow == targetBox.getLocation().getRow() && boxCol == targetBox.getLocation().getCol() )){
                        HeuristicDistance += 30;//30
                        Box otherBox = n.getBoxByLocation().get(new LocationXY(boxRow, boxCol));
                        if(otherBox != null && otherBox.isBoxInFinalPosition()){
                            HeuristicDistance += 1000;//1000
                        }
                    }
                }
              if (command != null && (command.actionType == Command.Type.Push)){
                   int boxRow = targetBox.getLocation().getRow() + Command.dirToRowChange(command.dir2);
                   int boxCol = targetBox.getLocation().getCol() + Command.dirToColChange(command.dir2);
                   Box possibleBox2 = n.getBoxByLocation().get(new LocationXY(boxRow,boxCol));
                    if(possibleBox2 != null && !(currentSubGoal.getLocation().getRow() == targetBox.getLocation().getRow() &&
                                                    currentSubGoal.getLocation().getCol() == targetBox.getLocation().getCol())){
                        HeuristicDistance += 200;//200
                        if(possibleBox2.isBoxInFinalPosition()){
                            HeuristicDistance += 2000;//2000
                        }
                    }
                    for(Agent a : n.agents){
                        if(a.getLabel() != n.thisAgent.getLabel() && a.getLocation().equals(new LocationXY(boxRow,boxCol))){
                            HeuristicDistance += 200;//200
                        }
                    }
                }
                
                // if the thisAgent next movement conflict with an anther agent then the Heuristic will be worsen.
                for(Agent a : n.agents){
                    if(a.getLabel() != n.thisAgent.getLabel() && a.getLocation().equals(n.thisAgent.getLocation())){
                        HeuristicDistance += 200;//200
                    }
                }
                // if the thisAgent next movement conflict with a box then the Heuristic will be worsen.
                // if the box is in final position then  the Heuristic will become even worsen.
                Box possibleBox = n.getBoxByLocation().get(n.thisAgent.getLocation());
                if(possibleBox != null){
                    HeuristicDistance += 200;//200
                    if(possibleBox.isBoxInFinalPosition()){
                        HeuristicDistance += 2000;//2000
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
        return HeuristicDistance;
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
                    HeuristicDistance += 100;
            }
            if(notTargetBox){
                HeuristicDistance += 200;
            }
        }
        Box possibleBox = n.getBoxByLocation().get(n.thisAgent.getLocation());
        if(possibleBox != null){
            HeuristicDistance += 40;
        }
        for(Agent a : n.agents){
            if(a.getLabel() != n.thisAgent.getLabel() && a.getLocation().equals(n.thisAgent.getLocation())){
                HeuristicDistance += 20;
            }
        }
        Command com2 = n.action;
        if(com2 != null && com2.actionType != Command.Type.Move){
            int boxRow = n.thisAgent.getLocation().getRow() + Command.dirToRowChange(com2.dir2);
            int boxCol = n.thisAgent.getLocation().getCol() + Command.dirToColChange(com2.dir2);
            possibleBox = n.getBoxByLocation().get(new LocationXY(boxRow, boxCol));
            if(possibleBox != null){
                HeuristicDistance += 40;
                if(possibleBox.isBoxInFinalPosition()){
                    HeuristicDistance += 500;
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
