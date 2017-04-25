package searchclient;

import java.util.Comparator;

import searchclient.NotImplementedException;
import java.util.HashMap;

public abstract class Heuristic implements Comparator<Node> {
    public Node initialState;
    
	public Heuristic(Node initialState) {
            // Here's a chance to pre-process the static parts of the level.
            //HeuristicDistanceCalculation.initHeuristic(initialState);
            this.initialState = initialState;
	}

	public int h(Node n) {
		//throw new NotImplementedException();
		//return HeuristicDistanceCalculation.agentToClosestBox(n) 
        	//+ HeuristicDistanceCalculation.sumAllGoalsToClosestBox(n);
            //if(!n.thisAgent.isClearMode()) {
		return manhattanSubgoalHeuristic(n);
            //} else {
            //	return clearModeHeuristic(n);
            //}
	}

	public abstract int f(Node n);
        
    public int manhattanSubgoalHeuristic(Node n){
       // System.err.println("Node +++  " + n);
        int HeuristicDistance = 0;
        Goal currentSubGoal = null;
        //Find current main subgoal.
        currentSubGoal = n.thisAgent.getCurrentSubGoal();

        if(currentSubGoal != null){
            //find closest box that can be used to solve
            int boxDistance = Integer.MAX_VALUE;
            Box targetBox = null;
            //System.err.println("Box:  " + n.getBoxesByCoordinate().size());
            //System.err.println("Box location:  " + n.getBoxesByCoordinate().size());
            for(Box box : n.getBoxesByCoordinate().values()){
                if(!box.isInFinalPosition() && box.getId() == Character.toUpperCase(currentSubGoal.getId())){
                   // System.err.println("Current goal from  goalDistance:  " + Node.goalDistance.size());
                   // System.err.println("Current goal ID:  " + currentSubGoal.getId());
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
