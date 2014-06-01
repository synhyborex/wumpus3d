package Agents;

//import WumpusEnvironment.*;
import java.util.Random;

import WumpusEnvironment.Model.Agent.*;
import WumpusEnvironment.Model.Map.*;

public class TestReflexAgent extends Agent {
	private final static int maxNumDirections = 4;
	
	public TestReflexAgent(){
		super();
	}

	@Override
	public void step() {
		// Get Percepts
	    Node n = getCurrentLocation();
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
