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
                    if(agents.get(i).getColor() == this.colorSet.get(Character.toUpperCase(currentgoal.getId()))){

                        currentagent = agents.get(i);
                        agents.get(i).doingcell = currentgoal.getLocation();
                        agents.get(i).doingthing = currentgoal.getId();
                        break;
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
            	ArrayList<Agent> orderedAgents = new ArrayList<Agent>();
            	ArrayList<Integer> order = new ArrayList<Integer>();
            	for(Goal goal : Node.priority.keySet()){
            		//System.err.println("Goal " + goal.getId()+ "   " + "priority" + goal.getPriority());
            		order.add(goal.getPriority());
            	}
            	
            	Collections.sort(order);
            	Collections.reverse(order);
            	System.err.println(order);
            	for(Integer i : order){
                    for(Goal goal : Node.priority.keySet()){
                        if(goal.getPriority() == i){
                            orderedAgents.add(findNextGoal(goal));
                        }
                    }
            	}
            	
            	for(Agent agent : orderedAgents){
                    System.err.print(agent.getLabel()+ "   ");
                    System.err.println();
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
        
	/*
	public LinkedList<Node> SearchSbuplan(Strategy strategy, Agent a, Box b, LocationXY lo) throws IOException{
		Node leafNode = strategy.getAndRemoveLeaf();
		
		if(leafNode.isGoalState()){
			PlanType oneplan = new PlanType(leafNode.currentagent, leafNode.currentbox, leafNode.currentcell);
			return leafNode.extractPlan();
			
		} 
		
		return null;
	}
	 */
        
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
/*	public LinkedList<Node> Search(Strategy strategy) throws IOException {
		System.err.format("Search starting with strategy %s.\n", strategy.toString());
		strategy.addToFrontier(this.initialState);//read map complete here
		//System.err.println(this.initialState.a.getLocation().getRow());
		//System.err.println(this.initialState.a.getLocation().getY());
		//System.err.println("Frontier num:" + strategy.countFrontier());
		int iterations = 0;
		
		Node.computeBoxDistance();
		Node.computeGoalDistance();
		Node.showDistance();
                
		//Priority Done
		
		@SuppressWarnings("unchecked")
		HashMap<Goal, Integer> priomap = (HashMap<Goal, Integer>) this.initialState.priority.clone();
		
		List<Map.Entry<Goal, Integer>> priolist = new ArrayList<Map.Entry<Goal, Integer>>(priomap.entrySet());
		
		goalqueue = new PriorityQueue<Goal>();
		
		Collections.sort(priolist, new Comparator<Map.Entry<Goal, Integer>>() {  
			public int compare(Map.Entry<Goal, Integer> o1,  Map.Entry<Goal, Integer> o2) {  
				return (o1.getValue()).toString().compareTo(o2.getValue().toString());  
				}  
			});  
		
		for(int ii = 0; ii < priolist.size(); ii ++){
			goalqueue.add(priolist.get(ii).getKey());
		}
		
		//the goal queue generated
		Goal topgoal;
		Goal temp;
		Path allpath = new Path();
		Node currentnode = this.initialState;
		PlanType oneplan;
		
		
		while(!goalqueue.isEmpty()){
			
			
			topgoal = goalqueue.poll();
			temp = topgoal;
			//When set current?
			//only set by distance?
			
			oneplan = new PlanType(currentnode.currentagent, currentnode.currentbox, currentnode.currentcell, Goal.isGoal(currentnode.currentcell, currentnode));
			
			//plans.push(oneplan);
			//onepath = findPath(strategy, oneplan);
			Path subpath = solve(strategy, oneplan, currentnode);
			allpath.pathbynode.addAll(subpath.pathbynode);
			currentnode = subpath.pathbynode.getLast();
		}
		
		return allpath.pathbynode;
	}*/
	
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
	/*
	public Path solve(Strategy strategy, PlanType plan, Node currentnode){
		//throw new NotImplementedException();
		Node currentstate = currentnode;
		Stack<PlanType> planandsub = new Stack<PlanType>();
		Path onepath = new Path();
		Path temppath = new Path();
		ArrayList<LocationXY> cellList = new ArrayList<LocationXY>();
		planandsub.push(plan);
		while(!planandsub.isEmpty()){
			// is it findPath or findNoBoxPath?
			//!!!
			//!!!
			//!!!
			//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			//NOTICENOTICENOTICENOTICENOTICENOTICENOTICENOTICENOTICENOTICENOTICENOTICENOTICENOTICENOTICENOTICENOTICENOTICENOTICENOTICENOTICENOTICENOTICENOTICENOTICENOTICENOTICENOTICENOTICENOTICENOTICE
			planandsub.peek().finalpath = findWithBarrierPath(strategy, plan);
			cellList.addAll(planandsub.peek().finalpath.path);
			if(planandsub.peek().finalpath.findBlockedLocation(currentstate)){
				//Implement the Nodelist to Locations
				onepath.pathbynode.addAll(planandsub.peek().finalpath.pathbynode);
				//temppath.pathbynode.addAll(onepath.pathbynode);
				//onepath.pathbynode.clear();
				//onepath.pathbynode.addAll(planandsub.peek().finalpath.pathbynode);
				//onepath.pathbynode.addAll(temppath.pathbynode);
				//temppath.pathbynode.clear();
				planandsub.pop();
				
			}else{
				//cellList.addAll()
				for(int i = 0; i < plan.finalpath.barriers.size(); i ++){
					//add the plans of the barriers
					switch(currentstate.checkCellBlock(plan.finalpath.barriers.get(i))){
						case 'a':
							PlanType subplana = new PlanType(currentstate.agentMap.get(plan.finalpath.barriers.get(i)));
							//planandsub.push(subplana);
							//Find target cell
							subplana.tar_cell = subplana.findTargetCell(cellList, max_row, max_col);
							subplana.withbox = false;
							//Add to stack
							planandsub.push(subplana);
							//NOTHERE-Find agent to cell path
							
							//NOTHERE-add this path to the full path
							break;
						case 'b':
							PlanType subplanb = new PlanType(currentstate.boxMap.get(plan.finalpath.barriers.get(i)));
							//planandsub.push(subplanb);
							//Find good agent and target cell
							if(currentstate.goalMap.containsKey(plan.finalpath.barriers.get(i))){
								goalqueue.add(currentstate.goalMap.get(plan.finalpath.barriers.get(i)));
							}
							Agent target_agent;
							ArrayList<Agent> agentlist;
							agentlist = subplanb.findRightAgents(currentstate.agentbyID, subplanb.tar_b);
							ArrayList<PlanType> agentplans = new ArrayList<PlanType>();
							ArrayList<Integer> agentBarriers = new ArrayList<Integer>();
							//PlanType aplan = new PlanType();
							boolean goodagentfound = false;
							for(int ii = 0; ii < agentlist.size(); ii ++){
								agentplans.add(new PlanType());
								agentplans.get(ii).a = agentlist.get(ii);
								agentplans.get(ii).tar_b = subplanb.tar_b;
								findWithBarrierPath(strategy, agentplans.get(ii));
								boolean hasbarrier;
								hasbarrier = agentplans.get(ii).getPath().findBlockedLocation(currentstate);
								if(!hasbarrier){
									subplanb.a = agentlist.get(ii);
									goodagentfound = true;
									break;
								}
								else{
									agentBarriers.add(agentplans.get(ii).getPath().barriers.size());
								}
							}
							int which = 0;
							if(!goodagentfound){
								
								for(int kk = 0; kk < agentBarriers.size(); kk ++){
									if(agentBarriers.get(kk) < agentBarriers.get(which)){
										which = kk;
									}
								}
								//subplanb = agentplans.get(which);
								subplanb.a = agentlist.get(which);
							}
							subplanb.withbox = true;
							subplanb.tar_cell = subplanb.findTargetCell(cellList, max_row, max_col);
							//Add to Stack
							planandsub.push(subplanb);
							//NOTHERE-Find agent & box to target path
							
							//NOTHERE-add this path to the full path
							break;
						default:
							System.err.println("Error block checking");
					}
				}
			}
		}
		return onepath;
	}*/
    

	public static void main(String[] args) throws Exception {
            System.err.println("SearchClient initializing. I am sending this using the error output stream.");
            // Read level and create the initial state of the problem           
            //Node.computeGoalDistance();
            //Node.showDistance();
            
            try {
                SearchClient client = new SearchClient();

            } catch (IOException e) {
                System.err.println(e.getMessage());
            // Got nowhere to write to probably
            }
	}
}
