package Agents;

import WumpusEnvironment.Model.Agent.*;
import WumpusEnvironment.Model.Map.*;

public class TestTableAgent extends Agent {
	
	//create new tablet to direct Agent
    private int[][] directionTable = new int[5][5];

	public TestTableAgent() {
		super();
		//manually enter directions to travel
        directionTable[1][1] = EAST;
        directionTable[2][1] = EAST;
        directionTable[3][1] = SOUTH;
        directionTable[3][2] = SOUTH;
        directionTable[3][3] = SOUTH;
        directionTable[3][4] = EAST;
	}

	@Override
	public void step() {
		// Get Percepts
	    Node n = getCurrentLocation();
	    int x = n.getX();
	    int y = n.getY();
      getDirectionOfGold();

	    // Carry out actions
	    turnTo(directionTable[x][y]);
      if(nearWumpus())
        fireArrow(HEADING);
	    moveForward();

	}

}
