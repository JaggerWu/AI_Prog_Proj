package searchclient;

import searchclient.Command.Type;



public class POP {
	
	public static enum Barrier{
		Wall, Box, Agent, NotNeigh, Null, Unknown
	};
	public boolean checkPOP(String action, Node n){
		return true;
	}
	
	
	public Barrier preCondition(Node n, int newRow, int newCol, Type t){
		//throw new NotImplementedException();
		//Type i = Command.Type.Move;
		LocationXY newcell = new LocationXY(newRow, newCol);
		switch(t){
			case Move:{
				// Checking if cell is free
				if(n.wallMap.containsKey(newcell))return Barrier.Wall;
				if(n.agentMap.containsKey(newcell))return Barrier.Agent;
				if(n.boxMap.containsKey(newcell))return Barrier.Box;
				
				// Checking if cell is neighboured
				if(!LocationXY.isNeighboured(n.currentagent.getLocation(), newcell))return Barrier.NotNeigh;
				
				return Barrier.Null;
				//
			}
			
			case Push:{
				//Checking if cell is free
				if(n.wallMap.containsKey(newcell))return Barrier.Wall;
				if(n.agentMap.containsKey(newcell))return Barrier.Agent;
				if(n.boxMap.containsKey(newcell))return Barrier.Box;
				
				// Checking if cell is neighboured
				if(!LocationXY.isNeighboured(n.currentagent.currentBox.getLocation(), newcell))return Barrier.NotNeigh;
				if(!LocationXY.isNeighboured(n.currentagent.getLocation(), n.currentagent.currentBox.getLocation()))return Barrier.NotNeigh;
				
				return Barrier.Null;
			}
			
			case Pull:{
				//Checking if cell is free
				if(n.wallMap.containsKey(newcell))return Barrier.Wall;
				if(n.agentMap.containsKey(newcell))return Barrier.Agent;
				if(n.boxMap.containsKey(newcell))return Barrier.Box;
				
				// Checking if cell is neighboured
				if(!LocationXY.isNeighboured(n.currentagent.getLocation(), newcell))return Barrier.NotNeigh;
				if(!LocationXY.isNeighboured(n.currentagent.getLocation(), n.currentagent.currentBox.getLocation()))return Barrier.NotNeigh;
				
				return Barrier.Null;
			}
			
			default:{
				System.err.println("Unknown error in POP checking");
				return Barrier.Unknown;
			}
		}
	}
	
	//public Barrier ifEffectDone(){
		//throw new NotImplementedException();	
		
	//}
}
