package Agents;

import WumpusEnvironment.Model.Agent.Agent;
import WumpusEnvironment.Model.Map.Grid;
import WumpusEnvironment.Model.Map.Node;

public class TestAgent extends Agent {
	
	public TestAgent(){
		super();
	}
	
	public void step(){
		fireArrow(HEADING);
		if(moveEast() == HIT_WALL)
			log("damn i'm dumb");
	}

}
