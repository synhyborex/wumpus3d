package WumpusEnvironment.Model.Agent.TestAgents;

import WumpusEnvironment.Model.Agent.Agent;
import WumpusEnvironment.Model.Map.Grid;
import WumpusEnvironment.Model.Map.Node;

public class TestAgent extends Agent {
	
	public TestAgent(){
		super();
	}
	
	public void nextStep(){
		if(moveForward() == HIT_WALL){
			turnRight();
		}
	}

}
