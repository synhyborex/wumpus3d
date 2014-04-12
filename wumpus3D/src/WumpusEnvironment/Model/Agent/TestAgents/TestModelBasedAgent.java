package WumpusEnvironment.Model.Agent.TestAgents;

import WumpusEnvironment.Model.Agent.Agent;
import WumpusEnvironment.Model.Map.*;

public class TestModelBasedAgent extends Agent {
	
	private boolean turnedLeft; //agent just turned left
	
	public TestModelBasedAgent(){
		super();
		turnedLeft = false;
	}
	
	public void nextStep(){
		// Get Percepts
	      Node n = getAgentLocation();
	      int x = n.getX();
	      int y = n.getY();

	      // Carry out actions
	      int success = moveForward(); //try to move forward
	      if(success == HIT_WALL){ //hit a wall
	        turnLeft(); //turn away from wall
	        turnedLeft = true; //once true, stays true
	      }
	      //this will never happen if the agent has
	      //yet to encounter its first wall
	      else if(turnedLeft) //follow the wall countour
	        turnRight();
	}

}
