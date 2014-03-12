package WumpusEnvironment.Agent.TestAgents;

//import WumpusEnvironment.*;
import java.util.Random;

import WumpusEnvironment.Agent.*;
import WumpusEnvironment.Map.*;

public class TestReflexAgent extends Agent {
	private final static int maxNumDirections = 4;
	
	public TestReflexAgent(Grid g, Node start){
		super(g,start);
	}

	@Override
	public void nextStep() {
		// Get Percepts
	    Node n = getCurrentNode();
	    int x = n.getX();
	    int y = n.getY();
	    Random generator;

	    // Carry out actions
	    int success = moveForward();
	    //turn to random direction if you can't move forward
	    if(success != SAFE && success != GOAL_FOUND){
	    	System.out.println("new dir");
	      generator = new Random(); //initialize random number generator
	      //randomly get the next direction
	      int randDirection = generator.nextInt(maxNumDirections);
	      turnTo(randDirection); //turn agent to that direction
	    }

	}

}
