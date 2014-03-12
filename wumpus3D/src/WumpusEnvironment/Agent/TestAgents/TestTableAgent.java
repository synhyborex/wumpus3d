package WumpusEnvironment.Agent.TestAgents;

import WumpusEnvironment.Agent.*;
import WumpusEnvironment.Map.*;

public class TestTableAgent extends Agent {
	
	//create new tablet to direct Agent
    private int[][] directionTable = new int[5][5];

	public TestTableAgent(Grid g, Node start) {
		super(g, start);
		//manually enter directions to travel
        directionTable[1][1] = EAST;
        directionTable[2][1] = EAST;
        directionTable[3][1] = SOUTH;
        directionTable[3][2] = SOUTH;
        directionTable[3][3] = SOUTH;
        directionTable[3][4] = EAST;
	}

	@Override
	public void nextStep() {
		// Get Percepts
	    Node n = getCurrentNode();
	    int x = n.getX();
	    int y = n.getY();

	    // Carry out actions
	    turnTo(directionTable[x][y]);
	    moveForward();

	}

}
