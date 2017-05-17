package clients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Stack;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import clients.Strategy.*;
import clients.Heuristic.*;


public class SearchClient {
    public class SubPlan{
        public LocationXY loc = null;
        public char id = ' ';
        public ArrayList<LocationXY> passcells = new ArrayList<LocationXY>();
        public ArrayList<LocationXY> avoidcells = null;
        SubPlan(LocationXY tloc, char tid, ArrayList<LocationXY> tavoid){
            loc = tloc;
            id = tid;
            avoidcells = tavoid;
        }
    }
    public Node initialState;	
    public HashMap<Character, String> colorSet = new HashMap<Character, String>();

    public Stack<PlanType> plans = new Stack<PlanType>();

    /********************************************************/
    public PriorityQueue<Goal> subGoals;
    private Node currentState = new Node(null);
    private ArrayList<Agent> agents = new ArrayList<>();
    private Command[] latestActionArray = null;
    private Boolean[] agentErrorState = null;
    private BufferedReader serverMessages = new BufferedReader(new InputStreamReader(System.in));
    private String[] latestServerOutput = null;
    public static boolean agentGoback = false;
    /******************************************************/       
    public int max_row;
    public int max_col;
    PriorityQueue<Goal> goalqueue;
    public SearchClient() throws Exception {
        loadMap();
        //System.err.println("Box number: " + this.initialState.boxMap.size());
        Node.computeGoalDistance();
        Node.deleteDeadBox(currentState);
        //Node.showDistance();
        findSubgoals();
        latestActionArray = new Command[currentState.agents.size()];
        agentErrorState = new Boolean[currentState.agents.size()];
        serverMessages.readLine();
        if(agents.size()<2){
            singleAgent();
        }else{
            multipleAgent();
        }             
    }

    public void multipleAgent() throws IOException{
        goalqueue = new PriorityQueue<Goal>(subGoals);
        while(!goalqueue.isEmpty()){
            System.err.println("subgoals");
            System.err.println(goalqueue.peek().getId());
            Stack<SubPlan> substack = new Stack<SubPlan>();
            Agent currentagent = null;
            Goal currentgoal = goalqueue.peek();
            System.err.println("forwaimian");
            System.err.println(agents.size());
            for(int i = 0; i < agents.size(); i ++){
                System.err.println("forlimian");
                System.err.println("a: " + agents.get(i).getColor());
                System.err.println("goal: " + this.colorSet.get(Character.toUpperCase(currentgoal.getId())));
                if(agents.get(i).getColor() == "blue" || agents.get(i).getColor() == this.colorSet.get(Character.toUpperCase(currentgoal.getId())) ){
                    for(Goal goal : Node.goalDistance.keySet() ){
                            if(goal.getId() == currentgoal.getId() 
                                            && ( goal.getLocation().getRow() == currentgoal.getLocation().getRow() 
                                            && goal.getLocation().getCol() == currentgoal.getLocation().getCol())){
                                    for(LocationXY location : Node.goalDistance.get(goal).keySet()){
                                        if(location.getRow() == agents.get(i).getLocation().getRow() 
                                                    && location.getCol() == agents.get(i).getLocation().getCol()){
                                            currentagent = agents.get(i);
                                            System.err.println("current agent is : " + currentagent.getLabel());
                                            System.err.println("current agent location is : " + currentagent.getLocation());
                                            System.err.println("current goal location is : " + goal.getLocation());
                                            agents.get(i).doingcell = currentgoal.getLocation();
                                            agents.get(i).doingthing = currentgoal.getId();
                                            break;

                                        }
                                    }
                            }

                    }
                }
                if(currentagent == null)System.err.println("No agents could solve this goal");	

            }
            //System.exit(1);
            //substack.push(currentgoal.getId());
            if(currentgoal != null)substack.push(new SubPlan(currentgoal.getLocation(),currentgoal.getId(), null));
            Node myinitalState = currentState.getCopy();

            while(!substack.isEmpty()){
                System.err.println("substack");
                System.err.println(substack.peek().id);

                System.err.println(currentagent.getLabel());

                if(currentagent != null){
                    System.err.println("currentagent not null");
                    //currentagent.setClearMode(false);
                    System.err.println("peek: " + substack.peek().id);
                    if(substack.peek().id >= 'a' && substack.peek().id <= 'z'){//peek is a goal
                        System.err.println("Peek is a goal");
                        currentagent.currentGoal = currentgoal;
                        myinitalState.thisAgent = currentagent;

                        System.err.println(currentgoal.getId());
                        System.err.println(currentagent.getLabel());
                        //System.err.println(x);
                        //System.err.println(myinitalState.action);
                        //myinitalState.action = null;
                        currentagent.setStrategy(new StrategyBestFirst(new WeightedAStar(myinitalState,10)));

                        LinkedList<Node> plan = search(currentagent.getStrategy(), myinitalState);

                        System.err.println(substack.peek().id);
                        System.err.println(currentagent.getLabel());
                        if(plan != null){
                            System.err.println("plan not null");
                            //System.err.println(plan.getLast());
                            //System.err.println(plan.getFirst().action);
                            for(int i = 0; i < plan.size(); i ++){
                                System.err.println(plan.get(i));
                                System.err.println(plan.get(i).action);
                            }
                            substack.peek().passcells = nodesToCells(plan);
                            ArrayList<LocationXY> thispasscells = new ArrayList<LocationXY>();
                            thispasscells = substack.peek().passcells;
                            //System.err.println(myinitalState.agentMap.get(new LocationXY(1,18)).getLabel());
                            boolean cellsfree = checkclear(substack.peek().passcells, myinitalState);
                            System.err.println("cell free?" + cellsfree);
                            //boolean cellsfree = true;
                            System.err.println(myinitalState);
                            //System.err.println(myinitalState.agentbyID.get('1').getLabel());
                        if(cellsfree){
                            System.err.println("cells free");
                            //System.err.println("First: " + myinitalState.thisAgent.getLabel() );
                            //myinitalState = plan.getLast().getCopy();
                            //System.err.println("Second: " + myinitalState.thisAgent.getLabel() );
                            //currentagent.appendSolution(plan);
                            for(int i=0; i < agents.size(); i ++){
                                if(agents.get(i).getLabel() == currentagent.getLabel()){
                                    System.err.println(agents.get(i).getLabel());
                                    agents.get(i).appendSolution(plan);
                                    //a.appendSolution(subplan);
                                    //System.exit(1);
                                    break;
                                }
                            }
                                //System.err.println(currentagent.getLabel());
                                //System.err.println(currentagent.getNextAction());

                            substack.pop();
                            boolean cont = true;
                            while (cont) {
                                System.err.println("cont");
                                cont = performActions();
                                boolean status = currentState.changeState(latestActionArray, latestServerOutput, this);
                                //currentState.printState();
                                if(!status) {
                                    currentState.printState();
                                    System.exit(0);
                                }

                            }
                            //agentRequestBack(currentagent);
                            myinitalState = currentState.getCopy();
                            }else{//cells are not free
                                HashMap<LocationXY, Agent> barrieragents = new HashMap<LocationXY, Agent>();
                                HashMap<LocationXY, Box> barrierboxes = new HashMap<LocationXY, Box>();
                                System.err.println("Size : " + substack.peek().passcells.size());
                                SubPlan checking = substack.peek();
                                for(int i = checking.passcells.size() - 1; i >= 0 ; i -- ){
                                        System.err.println("int i: " + i);
                                    if(myinitalState.agentMap.containsKey(checking.passcells.get(i)) && myinitalState.agentMap.get(checking.passcells.get(i)).getLabel() != myinitalState.thisAgent.getLabel()){
                                        System.err.println("I'm here agent");
                                        barrieragents.put(checking.passcells.get(i), myinitalState.agentMap.get(checking.passcells.get(i)));
                                        substack.push(new SubPlan(checking.passcells.get(i), myinitalState.agentMap.get(checking.passcells.get(i)).getLabel(), thispasscells));
                                    }
                                    //System.err.println("Size : " + substack.peek().passcells.size());
                                    if(myinitalState.boxMapByLocation.containsKey(checking.passcells.get(i)) && myinitalState.boxMapByLocation.get(checking.passcells.get(i)).getColor() != myinitalState.thisAgent.getColor()){
                                        System.err.println("I'm here box");
                                        barrierboxes.put(checking.passcells.get(i), myinitalState.boxMapByLocation.get(checking.passcells.get(i)));
                                        substack.push(new SubPlan(checking.passcells.get(i), myinitalState.boxMapByLocation.get(checking.passcells.get(i)).getId(), thispasscells));
                                    }
                                }
                            }//cells are not free

                        }else{
                            System.err.println("Sha TM wanyi");
                            //plan is null
                        }

                        }else{//peek is not a goal
                            if(substack.peek().id >= '0' && substack.peek().id <= '9' ){
                                if(myinitalState.agentMap.get(substack.peek().loc) != null){
                                    System.err.println("I'm here peek is agent");
                                    Agent a = myinitalState.agentMap.get(substack.peek().loc);
                                    System.err.println("Which agent after peek check? " + a.getLabel());
                                    a.setClearMode(true);
                                    a.setClearCords(substack.peek().avoidcells);
                                    myinitalState.thisAgent = a;
                                    System.err.println("Which agent moving? " + myinitalState.thisAgent.getLabel());
                                    a.setStrategy(new StrategyBestFirst(new WeightedAStar(myinitalState,10)));

                                    LinkedList<Node> subplan = searchclear(myinitalState.thisAgent.getStrategy(), myinitalState, substack.peek().avoidcells);

                                    //System.err.println(a.getLabel() + "'s action " + a.getNextAction());
                                    //System.err.println( "0 's action " + myinitalState.agentbyID.get('0').getNextAction());
                                    //System.exit(1);//Stops here because we've already found the bug

                                    //System.exit(1);
                                    if(subplan != null){
                                            System.err.println("PLANNNNNNNNNNNNN" + subplan.getFirst());
                                    System.err.println(subplan.getFirst().action);
                                    System.err.println(subplan.getFirst().thisAgent.getLabel());
                                    substack.peek().passcells = nodesToCells(subplan);
                                    ArrayList<LocationXY> thispasscells = new ArrayList<LocationXY>();
                                    thispasscells = substack.peek().passcells;
                                    boolean cellsfree = checkclear(substack.peek().passcells, myinitalState);

                                            if(cellsfree){
                                    System.err.println("cells free");
                                    System.err.println(a.getLabel());
                                    //myinitalState = subplan.getLast().getCopy();
                                    for(int i=0; i < agents.size(); i ++){
                                            if(agents.get(i).getLabel() == a.getLabel()){
                                                    System.err.println(agents.get(i).getLabel());
                                                    agents.get(i).appendSolution(subplan);
                                                    //a.appendSolution(subplan);
                                                    //System.exit(1);
                                                    break;
                                            }
                                    }
                                    //a.appendSolution(subplan);
                                    //System.err.println(a.getNextAction());//problem
                                    //System.err.println(subplan);
                                    //System.exit(1);

                                    //myinitalState = subplan.getLast();
                                    substack.pop();
                                    boolean cont = true;
                                    while (cont) {
                                        System.err.println("cont");
                                        cont = performActions();
                                        boolean status = currentState.changeState(latestActionArray, latestServerOutput, this);
                                        a.setClearMode(false);
                                        a.setClearCords(null);
                                        //currentState.printState();
                                        if(!status) {
                                            currentState.printState();
                                            System.exit(0);
                                        }
                                    }
                                    myinitalState = currentState.getCopy();
                                }else{//cells are not free
                                        HashMap<LocationXY, Agent> barrieragents = new HashMap<LocationXY, Agent>();
                                        HashMap<LocationXY, Box> barrierboxes = new HashMap<LocationXY, Box>();
                                        System.err.println("Size : " + substack.peek().passcells.size());
                                        SubPlan checking = substack.peek();
                                        for(int i = checking.passcells.size() - 1; i >= 0 ; i -- ){
                                                System.err.println("int i: " + i);
                                        if(myinitalState.agentMap.containsKey(checking.passcells.get(i)) && myinitalState.agentMap.get(checking.passcells.get(i)).getLabel() != myinitalState.thisAgent.getLabel()){
                                                //System.err.println("I'm here agent");
                                                barrieragents.put(checking.passcells.get(i), myinitalState.agentMap.get(checking.passcells.get(i)));
                                                substack.push(new SubPlan(checking.passcells.get(i), myinitalState.agentMap.get(checking.passcells.get(i)).getLabel(), thispasscells));
                                        }
                                        //System.err.println("Size : " + substack.peek().passcells.size());
                                        if(myinitalState.boxMapByLocation.containsKey(checking.passcells.get(i)) && myinitalState.boxMapByLocation.get(checking.passcells.get(i)).getColor() != myinitalState.thisAgent.getColor()){
                                                //System.err.println("I'm here box");
                                                barrierboxes.put(checking.passcells.get(i), myinitalState.boxMapByLocation.get(checking.passcells.get(i)));
                                                substack.push(new SubPlan(checking.passcells.get(i), myinitalState.boxMapByLocation.get(checking.passcells.get(i)).getId(), thispasscells));
                                        }
                                    }



                                }//cells are not free
                                                }


                            }else{
                                substack.pop();
                            }
                        }else if(substack.peek().id >= 'A' && substack.peek().id <= 'Z' ){
                            System.err.println("I'm here peek is box");
                            Box b = myinitalState.boxMapByLocation.get(substack.peek().loc);
                            Agent a = null;
                            for(int i = 0; i < agents.size(); i ++){
                                if(b.getColor() == agents.get(i).getColor()){
                                    a = agents.get(i);
                                    System.err.println("Chosen agent " + a.getLabel() + " for box " + b.getId());
                                    break;
                                }
                            }
                            if(a != null){
                                    a.setClearMode(true);
                                    a.setClearCords(substack.peek().avoidcells);
                                    myinitalState.thisAgent = a;
                                    myinitalState.thisBox = b;
                                    System.err.println("Which agent moving? " + myinitalState.thisAgent.getLabel());
                                    a.setStrategy(new StrategyBestFirst(new WeightedAStar(myinitalState,10)));

                                    LinkedList<Node> subplan = searchclear(myinitalState.thisAgent.getStrategy(), myinitalState, substack.peek().avoidcells);

                                    if(subplan != null){
                                            substack.peek().passcells = nodesToCells(subplan);
                                            ArrayList<LocationXY> thispasscells = new ArrayList<LocationXY>();
                                            thispasscells = substack.peek().passcells;
                                            boolean cellsfree = checkclear(substack.peek().passcells, myinitalState);

                                            if(cellsfree){
                                                System.err.println("cells free");
                                                System.err.println(a.getLabel());
                                                System.err.println(b.getId());
                                                //myinitalState = subplan.getLast().getCopy();
                                                for(int i=0; i < agents.size(); i ++){
                                                        if(agents.get(i).getLabel() == a.getLabel()){
                                                                System.err.println(agents.get(i).getLabel());
                                                                agents.get(i).appendSolution(subplan);
                                                                //a.appendSolution(subplan);
                                                                //System.exit(1);
                                                                break;
                                                        }
                                                }
                                                //a.appendSolution(subplan);
                                                //System.err.println(a.getNextAction());//problem
                                                //System.err.println(subplan);
                                                //System.exit(1);

                                                //myinitalState = subplan.getLast();
                                                substack.pop();
                                                boolean cont = true;
                                                while (cont) {
                                                    System.err.println("cont");
                                                    cont = performActions();
                                                    boolean status = currentState.changeState(latestActionArray, latestServerOutput, this);
                                                    a.setClearMode(false);
                                                    a.setClearCords(null);
                                                    //currentState.printState();
                                                    if(!status) {
                                                        currentState.printState();
                                                        System.exit(0);
                                                    }
                                                }
                                                myinitalState = currentState.getCopy();
                                                myinitalState.thisBox = null;
                                            }else{//cells are not free
                                                HashMap<LocationXY, Agent> barrieragents = new HashMap<LocationXY, Agent>();
                                                HashMap<LocationXY, Box> barrierboxes = new HashMap<LocationXY, Box>();
                                                System.err.println("Size : " + substack.peek().passcells.size());
                                                SubPlan checking = substack.peek();
                                                for(int i = checking.passcells.size() - 1; i >= 0 ; i -- ){
                                                    System.err.println("int i: " + i);
                                                    if(myinitalState.agentMap.containsKey(checking.passcells.get(i)) && myinitalState.agentMap.get(checking.passcells.get(i)).getLabel() != myinitalState.thisAgent.getLabel()){
                                                        //System.err.println("I'm here agent");
                                                        barrieragents.put(checking.passcells.get(i), myinitalState.agentMap.get(checking.passcells.get(i)));
                                                        substack.push(new SubPlan(checking.passcells.get(i), myinitalState.agentMap.get(checking.passcells.get(i)).getLabel(), thispasscells));
                                                    }
                                                    //System.err.println("Size : " + substack.peek().passcells.size());
                                                    if(myinitalState.boxMapByLocation.containsKey(checking.passcells.get(i)) && myinitalState.boxMapByLocation.get(checking.passcells.get(i)).getColor() != myinitalState.thisAgent.getColor()){
                                                        //System.err.println("I'm here box");
                                                        barrierboxes.put(checking.passcells.get(i), myinitalState.boxMapByLocation.get(checking.passcells.get(i)));
                                                        substack.push(new SubPlan(checking.passcells.get(i), myinitalState.boxMapByLocation.get(checking.passcells.get(i)).getId(), thispasscells));
                                                    }
                                                }
                                            }//cells are not free
                                    }
                            }

                        }
                    }
                }
            }
            goalqueue.poll();
        }
    }

    private boolean checkclear(ArrayList<LocationXY> passcells, Node thisState) {
        for(int i = 0; i < passcells.size(); i ++){
            System.err.println(passcells.get(i));
            System.err.println(thisState.agentMap.containsKey(passcells.get(i)));
            //System.err.println(thisState.agentMap.get(passcells.get(i)).getLabel() != thisState.thisAgent.getLabel());
            if(thisState.agentMap.containsKey(passcells.get(i)) && thisState.agentMap.get(passcells.get(i)).getLabel() != thisState.thisAgent.getLabel()){
                return false;
            }
            if(thisState.boxMapByLocation.containsKey(passcells.get(i)) && thisState.boxMapByLocation.get(passcells.get(i)).getColor() != thisState.thisAgent.getColor()){
                return false;
            }
        }
        return true;
    }

    private ArrayList<LocationXY> nodesToCells(LinkedList<Node> plan) {
        ArrayList<LocationXY> celllist = new ArrayList<LocationXY>();
        for(int i = plan.size() - 1; i >= 0; i --){
            if(!celllist.contains(plan.get(i).thisAgent.getLocation())){
                celllist.add(plan.get(i).thisAgent.getLocation());
            }
            if(plan.get(i).thisBox != null){
                if(!celllist.contains(plan.get(i).thisBox.getLocation())){
                    celllist.add(plan.get(i).thisBox.getLocation());
                }
            }
        }
        return celllist;
    }


    public void singleAgent() throws IOException{
        boolean finished = false;
        while(!finished) {
            ArrayList<Agent> orderedAgents = new ArrayList<>();
            ArrayList<Integer> order = new ArrayList<>();
            if(agents.size() > 1){
                for(Goal goal : Node.priority.keySet()){
                //System.err.println("Goal " + goal.getId()+ "   " + "priority" + goal.getPriority());
                order.add(goal.getPriority());
                }
                Collections.sort(order);
                Collections.reverse(order);
                //System.err.println(order);
                for(Integer i : order){
                    for(Goal goal : Node.priority.keySet()){
                        if(goal.getPriority() == i){
                            orderedAgents.add(findNextGoal(goal));
                        }
                    }
                }
            }else {
                orderedAgents.add(agents.get(0));
            }
            
            for (Agent agent : orderedAgents) {
                agent.tryToFindNextGoal(currentState, subGoals);
                if(!agent.isQuarantined() && !agent.isDone) {
                    System.err.println("Agent " + agent.getLabel() + " is trying to find solution for goal " + agent.getCurrentSubGoal().getId());
                    Node myinitalState = currentState.getCopy();
                    myinitalState.thisAgent = agent;
                    agent.setStrategy(new StrategyBestFirst(new WeightedAStar(myinitalState,10)));
                    LinkedList<Node> plan = search(agent.getStrategy(), myinitalState);
                    //System.err.println(plan);
                    if(plan != null) {
                        agent.appendSolution(plan);
                        boolean cont = true;
                        while (cont) {
                            cont = performActions();
                            boolean status = currentState.changeState(latestActionArray, latestServerOutput, this);
                            //currentState.printState();
                            if(!status) {
                                currentState.printState();
                                System.exit(0);
                            }
                        }
                        boolean error = false;
                        for(int i = 0; i < latestServerOutput.length; i++){
                            if(latestServerOutput[i] != null && latestServerOutput[i].equals("false")){
                                error = true;
                                agentErrorState[i] = true;
                                Agent failAgent = currentState.getAgentById(Integer.toString(i).charAt(0));
                                if(!currentState.agents.get(i).isQuarantined()) {
                                    System.err.println("Agent number " + failAgent.getLabel() + " is requesting clear");
                                    failAgent.requestClear(currentState);
                                }
                            } else {
                                agentErrorState[i] = false;
                                System.err.println("Agent number " + currentState.agents.get(i).getLabel() + " is done with no error");
                                for(Agent a : currentState.getAgents()){
                                    if(a.isQuarantined() && a.getQuarantinedBy().getLabel() == currentState.agents.get(i).getLabel()){
                                        a.setQuarantined(false);
                                    }
                                }
                            }
                        }
                        if(error){
                            while (performActions()) {
                                boolean status = currentState.changeState(latestActionArray, latestServerOutput, this);
                                if(!status) {
                                    currentState.printState();
                                    System.exit(0);
                                }
                            }
                        }
                    } else {
                        System.err.println("Solution could not be found");
                    }
                }
                //agentRequestBack(agent);     
            }

            boolean agentsDone = true;
            for(Agent a : currentState.agents){
                if(a.getCurrentSubGoal() != null){
                    agentsDone = false;
                }
            }
            if(agentsDone && subGoals.isEmpty()){
                finished = true;
            }
        }
    }

    public Agent findNextGoal(Goal goal){		
        if(!subGoals.isEmpty()){
            for (Box box : currentState.getBoxByLocation().values()) {
                if(Character.toUpperCase(goal.getId()) == box.getId()){
                    for(Agent agent : currentState.agents){
                        if(box.getColor().equals(agent.getColor())){
                        //if(box.getColor() == agent.getColor()){
                            return agent;
                        }
                    }
                }
            }
        }
        return null ;
    }

    private void loadMap() throws IOException {
        // Read lines specifying colors
        String line = serverMessages.readLine();
        String color;	
        boolean hascolor = false;
        while(line.matches("^[a-z]+:\\s*[0-9A-Z](\\s*,\\s*[0-9A-Z])*\\s*$")) {
            hascolor = true;
            line = line.replaceAll( "\\s", "" );
            color = line.split( ":" )[0];	
            for ( String id : line.split( ":" )[1].split( "," ) )
                colorSet.put( id.charAt( 0 ), color );
                //allServerMessages.add(line);
            line = serverMessages.readLine();
        }
        ArrayList<String> allServerMessages = new ArrayList<String>();
        max_col = line.length();
        max_row = 0;
        allServerMessages.add(line);
        while(!line.equals("")) {
            line = serverMessages.readLine();
            allServerMessages.add(line);
            max_row++;
            if(line.length() > max_col)max_col = line.length();
        }
        Node.initNodeSize(max_row, max_col);
        int row = 0;
        //boolean agentFound = false;
        this.initialState = new Node(null);	
        for(int i=0; i<allServerMessages.size(); i++) {
            for (int col = 0; col < allServerMessages.get(i).length(); col++) {

                char chr = allServerMessages.get(i).charAt(col);
                String tcolor;
                if (chr == '+') { // Wall.
                    this.initialState.wallMap.put(new LocationXY(row, col), true);
                    this.initialState.wallMapsta.put(new LocationXY(row, col), true);
                } else if ('0' <= chr && chr <= '9') { // Agent.
                    if(hascolor){
                        tcolor = colorSet.get(chr);
                    }else{
                        tcolor = "blue";
                    }
                    Agent newAgent = new Agent(tcolor, new LocationXY(row,col), chr);
                    this.initialState.agentMap.put(new LocationXY(row,col), new Agent(tcolor, new LocationXY(row,col), chr));
                    this.initialState.agentbyID.put(chr, new Agent(tcolor, new LocationXY(row,col), chr));
                    this.initialState.agentByCoordinate.put(new LocationXY(row,col), new Agent(tcolor, new LocationXY(row,col), chr));
                    Node.orignalAgents.add(new Agent(tcolor, new LocationXY(row,col), chr));
                    if(newAgent.getLabel() == '0'){
                        Node.setAgent0(newAgent);
                    }
                    currentState.agents.add(newAgent);
                    agents.add(newAgent);
                } else if ('A' <= chr && chr <= 'Z') { // Box.
                    if(hascolor){
                        tcolor = colorSet.get(chr);
                    }else{
                        tcolor = "blue";
                    }
                    Box newBox = new Box(tcolor, new LocationXY(row,col), chr);
                    this.initialState.boxMap.put(new LocationXY(row,col), new Box(tcolor, new LocationXY(row,col), chr));
                    this.initialState.boxMapsta.put(new LocationXY(row,col), new Box(tcolor, new LocationXY(row,col), chr));
                    this.initialState.currentbox = newBox;
                    currentState.addBox(newBox);
                    //currentState.addBox1(newBox);
                } else if ('a' <= chr && chr <= 'z') { // Goal.
                    if(hascolor){
                        tcolor = colorSet.get(chr);
                    }else{
                        tcolor = "blue";
                    }
                    this.initialState.goalMap.put(new LocationXY(col,row), new Goal(tcolor, new LocationXY(col,row), chr));
                    this.initialState.goalMapsta.put(new LocationXY(col,row), new Goal(tcolor, new LocationXY(row,col), chr));
                    Node.addGoal(new Goal(chr, new LocationXY(row, col)));
                } else if (chr == ' ') {
                    // Free space.
                } else {
                    System.err.println("Error, read invalid level character: " + chr + " " + (int) chr + "at Row" + row + "at Col" + col);
                    System.exit(1);
                }
            }
            row++;
        }
    }
    public void agentRequestBack(Agent agent) throws IOException{
        SearchClient.agentGoback = true;
        Node myCurrentState1 = currentState.getCopy();
        myCurrentState1.thisAgent = agent;
        myCurrentState1.printState();
        agent.setStrategy(new StrategyBestFirst(new WeightedAStar(myCurrentState1,10)));
        LinkedList<Node> plan1 = search(agent.getStrategy(), myCurrentState1);
        System.err.println(plan1);
        if(plan1 != null) {
            agent.appendSolution(plan1);
            boolean cont = true;
            while (cont) {
                cont = performActions();
                boolean status = currentState.changeState(latestActionArray, latestServerOutput, this);
                //currentState.printState();
                if(!status) {
                    currentState.printState();
                    System.exit(0);
                }
            }
        } else {
            System.err.println("Solution could not be found");
        }
       // agent.setAgentBackMode(false);
       SearchClient.agentGoback = false;
    }

    public boolean performActions() throws IOException {
        String jointAction = "[";
        //String jointAction = "";
        int noActions = 0;
        ArrayList<Agent> actAgent = Agent.sortById(agents);
        for (int i = 0; i < actAgent.size(); i++) {
            Command action = actAgent.get(i).getNextAction();
            String actionStr = "";
            latestActionArray[i] = action;
            if(action == null){
                noActions++;
                actionStr = "NoOp";
            } else {
                actionStr = action.toString();
            }
            jointAction += actionStr;
            if(i < actAgent.size() - 1){
                jointAction += ",";
            }
        }
        jointAction += "]";
        if(noActions == actAgent.size()){
            return false;
        }
        System.err.println("Sending command: " + jointAction + "\n");
        // Place message in buffer
        System.out.println(jointAction);
        // Flush buffer
        System.out.flush();
        // Disregard these for now, but read or the server stalls when its output buffer gets filled!
        String percepts = serverMessages.readLine();
        System.err.println(percepts);
        if (percepts == null)
            return false;
        String strip = percepts.replaceAll("\\[", "").replaceAll("\\]","").replaceAll("\\s", "");
        String[] returnVals = strip.split(",");
        this.latestServerOutput = returnVals;
        for(String returnVal : returnVals){
            if(returnVal.equals("false")){
                return false;
            }
        }
        return true;
    }

    public void findSubgoals(){
        Node.setGoalsPriority();
        subGoals = new PriorityQueue<>(30, subGoalComparator);
        for (Goal goal : Node.getGoalByLocation().values()){
            subGoals.offer(goal);
        }
    }

    public static Comparator<Goal> subGoalComparator = new Comparator<Goal>() {
        @Override
        public int compare(Goal o1, Goal o2) {
         return (int) o2.getPriority() - o1.getPriority();
        }
    };

    public static LinkedList<Node> search(Strategy strategy, Node initialState) {
        strategy.addToFrontier(initialState);
        while (true) {
            if (strategy.frontierIsEmpty()) {
                System.err.println("Agent " + initialState.thisAgent.getLabel() + " says: Frontier is empty");
                return null;
            }
            Node leafNode = strategy.getAndRemoveLeaf();
            if ( leafNode.isGoalState() ) {
                return leafNode.extractPlan();
            }
            strategy.addToExplored(leafNode);
            for (Node n : leafNode.getExpandedNodes()) {
                if ( !strategy.isExplored(n) && !strategy.inFrontier(n) ) {
                    strategy.addToFrontier(n);
                }
            }
        }
    }
	
	
    public static LinkedList<Node> searchclear(Strategy strategy, Node initialState, ArrayList<LocationXY> clearlocs) {
        strategy.addToFrontier(initialState);
        while (true) {
            if (strategy.frontierIsEmpty()) {
                System.err.println("Agent " + initialState.thisAgent.getLabel() + " says: Frontier is empty");
                return null;
            }
            Node leafNode = strategy.getAndRemoveLeaf();
            if ( leafNode.isClearState(clearlocs) ) {
                System.err.println("got clear plan");
                System.err.println(leafNode.thisAgent.getLabel());
                return leafNode.extractPlan();
            }
            strategy.addToExplored(leafNode);
            for (Node n : leafNode.getExpandedNodes()) {
                if ( !strategy.isExplored(n) && !strategy.inFrontier(n) ) {
                    strategy.addToFrontier(n);
                }
            }
        }
    }
    public Path findWithBarrierPath(Strategy strategy, PlanType plan){
        int iterations = 0;
        while (true) {
            if (iterations == 1000) {
                System.err.println(strategy.searchStatus());
                iterations = 0;
            }
            if (strategy.frontierIsEmpty()) {
                return null;
            }
            Node leafNode = strategy.getAndRemoveLeaf();
            leafNode.currentagent = plan.a;
            leafNode.currentbox = plan.tar_b;
            leafNode.currentcell = plan.tar_cell;

            //System.err.print("agentX : " + leafNode.a.getLocation().getRow() + " ");
            //System.err.print("agentY : " + leafNode.a.getLocation().getY() + " ");
            //System.err.print("HASH : " + leafNode.hashCode() + " ");
            //System.err.println();
            if (leafNode.isGoalState(plan.withbox)) {
                //System.err.println("Found goal state");
                //System.err.println("Box:" + leafNode.b.getId() + " " + leafNode.b.getLocation().getRow() + " " + leafNode.b.getLocation().getY());
                //PlanType oneplan = new PlanType(leafNode.currentagent, leafNode.currentbox, leafNode.currentcell);
                //plans.push(oneplan);
                plan.getPath().pathbynode = leafNode.extractPlan();
                plan.getPath().findCellfromNodes();
                return plan.getPath();
            }

            strategy.addToExplored(leafNode);
            for (Node n : leafNode.getExpandedNodesAcrosser()) { // The list of expanded nodes is shuffled randomly; see Node.java.
                if (!strategy.isExplored(n) && !strategy.inFrontier(n)) {
                    strategy.addToFrontier(n);
                }
            }
            iterations++;
        }
    }

    public Path findPath(Strategy strategy, PlanType plan){	
        int iterations = 0;
        while (true) {
            if (iterations == 1000) {
                System.err.println(strategy.searchStatus());
                iterations = 0;
            }
            if (strategy.frontierIsEmpty()) {
                return null;
            }
            Node leafNode = strategy.getAndRemoveLeaf();
            leafNode.currentagent = plan.a;
            leafNode.currentbox = plan.tar_b;
            leafNode.currentcell = plan.tar_cell;

            //System.err.print("agentX : " + leafNode.a.getLocation().getRow() + " ");
            //System.err.print("agentY : " + leafNode.a.getLocation().getY() + " ");
            //System.err.print("HASH : " + leafNode.hashCode() + " ");
            //System.err.println();
            if (leafNode.isGoalState(plan.withbox)) {
                //System.err.println("Found goal state");
                //System.err.println("Box:" + leafNode.b.getId() + " " + leafNode.b.getLocation().getRow() + " " + leafNode.b.getLocation().getY());
                //PlanType oneplan = new PlanType(leafNode.currentagent, leafNode.currentbox, leafNode.currentcell);
                //plans.push(oneplan);
                plan.getPath().pathbynode = leafNode.extractPlan();
                return plan.getPath();
            }
            strategy.addToExplored(leafNode);
            for (Node n : leafNode.getExpandedNodes()) { // The list of expanded nodes is shuffled randomly; see Node.java.
                if (!strategy.isExplored(n) && !strategy.inFrontier(n)) {
                    strategy.addToFrontier(n);
                }
            }
            iterations++;
        }
    }

    public static void main(String[] args) throws Exception {
        System.err.println("SearchClient initializing. I am sending this using the error output stream.");          
        try {
            SearchClient client = new SearchClient();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
