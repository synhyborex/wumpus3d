package WumpusEnvironment.Agent;
import WumpusEnvironment.Map.*;

/**
 * Represents the Agent that the programmer will be designing
 * @author Bhargav
 *
 */
public abstract class Agent {
	public static final int NORTH = 0;
	public static final int EAST = 1;
	public static final int SOUTH = 2;
	public static final int WEST = 3;
	public int HEADING; //the direction the Agent is facing
	
	public static final int SAFE = 0;
	public static final int HIT_WALL = 1;
	public static final int HIT_WUMPUS = 2;
	public static final int HIT_MINION = 3;
	public static final int HIT_PIT = 4;
	public static final int GOAL_FOUND = 5;
	
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
	
	public Agent(){
		
	}
	
	public Agent(Grid g, Node start){
		grid = g;
		currentNode = start;
		HEADING = EAST;
		goalsSoFar = 0;
		
		//put Agent on Grid
		grid.setNode(currentNode.getX(),currentNode.getY(),Grid.AGENT,true);
	}
	
	/**
	 * Defines what an <code>Agent</code> will do each step
	 */
	public void step(){
		//need check for fairy first
		//nextSearchStep();
		
		nextStep();
		
		//end of method
		//if(currentNode.hasGoal())
			//goalsSoFar++;
		if(goalsSoFar == grid.numGoals())
			gameOver();
	}
	
	/**
	 * The next step the <code>Agent</code> will take. This method will be written by the student.
	 */
	public abstract void nextStep();
	
	/**
	 * The next step the <code>Fairy</code> will take. This method will be written by the student.
	 * This method is not abstract, in order to allow for the instructor to provide
	 * <code>Agent</code> files that do not require <code>nextSearchStep()</code> to be included for the assignment. 
	 */
	public void nextSearchStep(){}
	
	/**
	 * Defines the system behavior when all objectives have been completed
	 */
	protected void gameOver(){
		grid.setSolved(true);
	}
	
	/**
	 * Moves the <code>Agent<code> forward by one space, if possible.
	 * @return the value of what is occupying the <code>Node</code> other than the <code>Agent</code>, or SAFE if the Agent is alone
	 */
	public int moveForward(){
		int addToX = 0, addToY = 0;
		switch(HEADING){
			case NORTH:
				if(!grid.getNode(currentNode.getX(),currentNode.getY()-1).hasWall()){
					addToX = 0;
					addToY = -1;
				}
				break;
			case EAST:
				if(!grid.getNode(currentNode.getX()+1,currentNode.getY()).hasWall()){
					addToX = 1;
					addToY = 0;
				}
				break;
			case SOUTH:
				if(!grid.getNode(currentNode.getX(),currentNode.getY()+1).hasWall()){
					addToX = 0;
					addToY = 1;
				}
				break;
			case WEST:
				if(!grid.getNode(currentNode.getX()-1,currentNode.getY()).hasWall()){
					addToX = -1;
					addToY = 0;
				}
				break;
		}
		//hit a wall
		if(addToX + addToY == 0) return HIT_WALL;
		
		//successfully moved forward
		grid.setNode(currentNode.getX(),currentNode.getY(),Grid.AGENT,false); //Agent has moved from this spot
		//currentNode.setX(currentNode.getX()+addToX);
		//currentNode.setY(currentNode.getY()+addToY);
		currentNode = grid.getNode(currentNode.getX()+addToX,currentNode.getY()+addToY);
		grid.setNode(currentNode.getX(),currentNode.getY(),Grid.AGENT,true); //Agent is now here

		
		
		//also need to adjust score for movement
		return newNodeStatus();
	}
	
	/**
	 * Faces the <code>Agent</code> to the specified direction
	 * @param heading the direction to face the <code>Agent</code>
	 */
	public void turnTo(int heading){
		HEADING = heading;
	}
	
	/**
	 * Faces the <code>Agent</code> right from its current heading
	 */
	public void turnRight(){
		if(HEADING == WEST)
			HEADING = NORTH;
		else HEADING++;
	}
	
	/**
	 * Faces the <code>Agent</code> left from its current heading
	 */
	public void turnLeft(){
		if(HEADING == NORTH)
			HEADING = WEST;
		else HEADING--;
	}
	
	/**
	 * Determines the value of what is occupying the <code>Node</code> other than the <code>Agent</code>
	 * @return the value of what is occupying the <code>Node</code> other than the <code>Agent</code>
	 */
	protected int newNodeStatus(){
		int value = SAFE;
		if(currentNode.hasGoal()) value =  GOAL_FOUND;
		else if(currentNode.hasWumpus()) value =  HIT_WUMPUS;
		else if(currentNode.hasPit()) value =  HIT_PIT;
		else if(currentNode.hasMinion()) value = HIT_MINION;
		determinePenalty(value); //figure out what, if any, penalties to apply to score
		//printAgentStatus(value); //print the status of the Agent at the new Node
		return value;
	}
	
	/**
	 * Determines the penalty to be incurred by a certain move
	 * @param value the value of what is occupying the <code>Node</code> other than the <code>Agent</code>
	 */
	protected void determinePenalty(int value){
		switch(value){
			case SAFE:
				//do nothing presumably
				break;
			case HIT_MINION:
				//what to do if we hit a minion
				break;
			case HIT_WUMPUS:
				//what to do if we hit a Wumpus
				break;
			case HIT_PIT:
				//what to do if we hit a pit
				break;
			case GOAL_FOUND:
				//what to do if we hit a goal
				goalsSoFar++;
				break;
		}
	}
	
	/** getters and setters **/
	/**
	 * Returns the current <code>Node</code> that the <code>Agent</code> is occupying
	 * @return the current <code>Node</code> that the <code>Agent</code> is occupying
	 */
	public Node getCurrentNode(){return currentNode;}
	
	/**
	 * Returns the current heading of the <code>Agent</code> as a string
	 * @return the current heading of the <code>Agent</code> as a string
	 */
	public String headingToString(){
		String ret = "failure";
		switch(HEADING){
			case NORTH:
				ret = "NORTH";
				break;
			case EAST:
				ret = "EAST";
				break;
			case SOUTH:
				ret = "SOUTH";
				break;
			case WEST:
				ret = "WEST";
				break;
		}
		return ret;
	}
	
	/**
	 * Returns the string representation of what occurred after a movement
	 * @return the string representation of what occurred after a movement
	 */
	public String movementStatusToString(){
		String ret = "failure";
		switch(newNodeStatus()){
			case SAFE:
				ret = "You feel safe here...\n";
				break;
			case HIT_WALL:
				ret = "Bonk! You walked into a wall!\n";
				break;
			case HIT_MINION:
				ret = "Ouch! Guess those minions aren't just for show after all.\n";
				break;
			case HIT_WUMPUS:
				ret = "The Wumpus has been waiting for a snack...looks like you're it.\n";
				break;
			case HIT_PIT:
				ret = "You've fallen into a pit! Good luck getting out before you turn to dust.\n";
				break;
			case GOAL_FOUND:
				ret = "You found gold!\n";
				break;
		}
		return ret;
	}
	
	public String locationToString(){
		return "Location: (" + currentNode.getX() + "," + currentNode.getY() + ")\n";
	}

}
