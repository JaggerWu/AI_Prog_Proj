package clients;

import clients.Heuristic.AStar;
import clients.Heuristic.WeightedAStar;
import static clients.SearchClient.search;
import clients.Strategy.StrategyBestFirst;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class Agent {
    private String color;
    private LocationXY location;
    private char label;
    private boolean conflited;
    private PriorityQueue<Goal> goalList = new PriorityQueue<Goal>(); 
    public Box currentBox;
    public Goal currentGoal = null;
    private boolean clearMode = false;
    private ArrayList<LocationXY> clearCords;
    public boolean isDone = false;
    private boolean quarantined = false;
    private LinkedList<Node> solution = new LinkedList<Node>();
    private Strategy strategy;
    private Node latestAction = null;
    private Agent quarantinedBy;
    private boolean agentBackMode = false;
    public LocationXY doingcell;
    public char doingthing;

    public Agent(){
        this.location = new LocationXY();
        this.conflited = false;
    }

    public Agent(char tlable){
        this.location = new LocationXY();
        this.label = tlable;
        this.conflited = false;
    }

    public Agent(int trow, int tcol, char tlable){
        this.location = new LocationXY(trow, tcol);
        this.label = tlable;
        this.conflited = false;
    }

    public Agent(String tcolor, LocationXY loc, char tlable){
        this.location = loc;
        this.color = tcolor;
        this.label = tlable;
    }

    public Agent(String tcolor, int trow, int tcol, char tlable){
        this.color = tcolor;
        this.location = new LocationXY(trow, tcol);
        this.label = tlable;
        this.conflited = false;
    }


    public Agent(char tlable, String color, LocationXY location, Goal currentGoal){
        this.label = tlable;
        this.color = color;
        this.location = location;
        this.currentGoal = currentGoal;
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
        this.label = l;
        return true;
    }

    public char getLabel(){
        return this.label;
    }

    public String getColor() {
        return color;
    }
    /**
 * @return ***********************************/
    public Goal getCurrentSubGoal() {
        return currentGoal;
    }

    public boolean isClearMode() {
        return clearMode;
    }

    public void setClearMode(boolean clearMode) {
        this.clearMode = clearMode;
    }
    
    public boolean isAgentBackMode() {
        return agentBackMode;
    }

    public void setAgentBackMode(boolean agentBack) {
        this.agentBackMode = agentBack;
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

    public void tryToFindNextGoal(Node currentState, PriorityQueue<Goal> subGoals){
        if(currentGoal != null){ //if current goal is done then set currentgoal to null
            Box goalBox = currentState.getBoxByLocation().get(currentGoal.getLocation());
            if(goalBox != null && goalBox.getId() == Character.toUpperCase(currentGoal.getId())){
                currentGoal = null;
            } else {
                return;
            }
        }
        if(color == null){ // single agent then get a goal from subGoal list
            currentGoal = subGoals.poll();
            isDone = false;
        } else {
            for(Goal goal : subGoals){
                for(Box box : currentState.getBoxByLocation().values()){
                    if(box.getId() == Character.toUpperCase(goal.getId()) && box.getColor().equals(color) ){
                        currentGoal = goal;
                        subGoals.remove(goal);
                        isDone = false;
                        return;
                    }
                }
            }
        }
        if(currentGoal == null){
            isDone = true;
            System.err.println("Agent " + label + " is done");
        }
    }

    public Command getNextAction() {
        if(solution.size() > 0){
            Node next = solution.getFirst();
            latestAction = next;
            solution.removeFirst();
            return next.action;
        } else {
            return null;
        }
    }
    
    public void setQuarantined(boolean quarantined) {
        this.quarantined = quarantined;
    }
    
    public Agent getQuarantinedBy() {
        return quarantinedBy;
    }

    public void setQuarantinedBy(Agent quarantinedBy) {
        this.quarantinedBy = quarantinedBy;
    }
    
    public void requestClear(Node currentState){
        ArrayList<LocationXY> clearCoordinates = new ArrayList<>();
        LocationXY pos = location;
        Command cmd;
        ArrayList<LocationXY> cmdEffectsCoordinates;
        System.err.println(pos);
        if(latestAction != null) {
            cmd = latestAction.action;
            cmdEffectsCoordinates = currentState.commandToLocations(pos, cmd);
            pos = new LocationXY(cmdEffectsCoordinates.get(0).getRow(), cmdEffectsCoordinates.get(0).getCol());
            clearCoordinates.addAll(cmdEffectsCoordinates);
            //System.err.println(cmd.toString());
        }
        cmd = getNextAction();
        while(cmd != null){
            //System.err.println(cmd.toString());
            cmdEffectsCoordinates = currentState.commandToLocations(pos, cmd);
            pos = new LocationXY(cmdEffectsCoordinates.get(0).getRow(), cmdEffectsCoordinates.get(0).getCol());
            clearCoordinates.addAll(cmdEffectsCoordinates);
            cmd = getNextAction();
        }
        for(Agent agent : currentState.agents){
            if(agent.getLabel() != label){
                ArrayList<LocationXY> agentClearCords = new ArrayList<>();
                for(LocationXY cord : clearCoordinates){
                    Box box = currentState.getBoxByLocation().get(cord);
                    if(agent.getLocation().equals(cord) || (box != null && box.getColor() != null && box.getColor().equals(agent.getColor()) && !box.getColor().equals(color))){
                        //System.err.println("box: " + box.getId());
                        System.err.println("agent :" + agent.getLabel());
                        agentClearCords.add(cord);
                    }
                }
                agent.clearCells(agentClearCords, currentState, this);
            }
        }
    }
    
    public void clearCells(ArrayList<LocationXY> coordinates, Node currentState, Agent sender){
        System.err.println("Agent number " + label + " attempting to clear");
        for(LocationXY cord : coordinates){
            System.err.println(cord.toString());
        }
        clearMode = true;
        solution.clear();
        clearCords = coordinates;
        Node myCurrentState = currentState.getCopy();
        myCurrentState.thisAgent = this;
        this.setStrategy(new StrategyBestFirst(new WeightedAStar(myCurrentState,10)));
        LinkedList<Node> plan = SearchClient.search(this.getStrategy(), myCurrentState);
        if(plan != null) {
            this.appendSolution(plan);
            
        } else {
            System.err.println("Solution could not be found");
        }
        clearMode = false;
        clearCords.clear();
        quarantined = true;
        quarantinedBy = sender;
    }
    
    public void requestAgentGoBack(Node currentState) {
        solution.clear();
        System.err.println("This is request go back ??????");
        this.setAgentBackMode(true);
        System.err.println("gobackmode = " + this.isAgentBackMode());
        Node myCurrentState = currentState.getCopy();
        myCurrentState.thisAgent = this;
        System.err.println("Print state");
        myCurrentState.printState();
        System.err.println("requestAgentGoBack before setStrategy:         " + myCurrentState.thisAgent.isAgentBackMode());
        this.setStrategy(new StrategyBestFirst(new WeightedAStar(myCurrentState,10)));
        System.err.println("requestAgentGoBack after setStrategy:         " + myCurrentState.thisAgent.isAgentBackMode());
        System.err.println("isAgentBackMode1:            " + this.isAgentBackMode());
        LinkedList<Node> plan = search(this.getStrategy(), myCurrentState);
        System.err.println("requestAgentGoBack after plan:         " + myCurrentState.thisAgent.isAgentBackMode());
        System.err.println("isAgentBackMode2:            " + this.isAgentBackMode());
        System.err.println(plan);
        if(plan != null) {
            this.appendSolution(plan);
        } else {
            System.err.println("Solution could not be found");
        }
        System.err.println("Agent return back done before.-------------------------------------------------------" + this.isAgentBackMode());
        this.setAgentBackMode(false);
        System.err.println("Agent return back done after.-------------------------------------------------------" + this.isAgentBackMode());
    }

    @Override
    public Agent clone() {
        Agent newAgent = new Agent(this.label, this.color, 
                            new LocationXY(this.location.getRow(), this.location.getCol()), this.currentGoal);
        newAgent.setClearMode(clearMode);
        newAgent.setClearCords(clearCords);
        return newAgent;
    }

    @Override
    public int hashCode() {
        int result = (int) label;
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

    
}
