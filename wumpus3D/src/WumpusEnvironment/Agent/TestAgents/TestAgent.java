package WumpusEnvironment.Agent.TestAgents;

import WumpusEnvironment.Agent.Agent;
import WumpusEnvironment.Map.Grid;
import WumpusEnvironment.Map.Node;

public class TestAgent extends Agent {
	
	public TestAgent(Grid g, Node start){
		super(g,start);
	}
	
	public void nextStep(){
		if(moveForward() == HIT_WALL){
			turnRight();
		}
	}

}
