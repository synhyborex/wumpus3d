package WumpusEnvironment.Agent;
import WumpusEnvironment.Map.*;

/**
 * Represents the Agent that the programmer will be designing
 * @author Bhargav
 *
 */
public class Agent {
	public static final int NORTH = 0;
	public static final int EAST = 1;
	public static final int SOUTH = 2;
	public static final int WEST = 3;
	public static int HEADING; //the direction the Agent is facing
	
	/**
	 * The grid on which the <code>Agent</code> is operating
	 */
	protected static Grid grid;
	
	/**
	 * How many goals have been accomplished so far
	 */
	protected int goalsSoFar;
	
	/**
	 * The Node the Agent is currently occupying
	 */
	protected Node currentNode;
	
	public Agent(Grid g, Node start){
		grid = g;
		currentNode = start;
		HEADING = EAST;
		goalsSoFar = 0;
	}
	
	/**
	 * Defines what an <code>Agent</code> will do each step
	 */
	public void step(){
		//end of method
		if(currentNode.hasGoal())
			goalsSoFar++;
		if(goalsSoFar == grid.numGoals())
			gameOver();
	}
	
	/**
	 * Defines the system behavior when all objectives have been completed
	 */
	public void gameOver(){
		
	}

}
