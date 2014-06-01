package Agents;

import WumpusEnvironment.Model.Agent.Agent;
import WumpusEnvironment.Model.Map.Grid;
import WumpusEnvironment.Model.Map.Node;

public class TestAgent extends Agent {
	
	public TestAgent(){
		super();
	}
	
	public void step(){
		fireArrow(EAST);
	}

}
