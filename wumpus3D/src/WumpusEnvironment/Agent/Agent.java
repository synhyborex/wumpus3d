package WumpusEnvironment.Agent;

import WumpusEnvironment.ApplicationWindow;
import WumpusEnvironment.Map.*;
import java.util.*;

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
	public static int HEADING; //the direction the Agent is facing
	
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
	 * The Fairy that will assist the Agent in doing off-line searches
	 */
	protected Fairy fairy;
	
	/**
	 * The fringe that may be used for off-line searches
	 */
	protected Fringe fringe;
	
	/**
	 * How many goals have been accomplished so far
	 */
	protected int goalsSoFar;
	
	/**
	 * The Node the <code>Agent</code> is currently occupying
	 */
	protected Node currentNode;
	
	public Agent(Grid g, Node start){
		grid = g;
		fairy = null;
		fringe = new Fringe();
		currentNode = start;
		HEADING = EAST;
		goalsSoFar = 0;
		
		//put Agent on Grid
		grid.setNode(currentNode.getX(),currentNode.getY(),Grid.AGENT,true);
		grid.setAgentLocation(currentNode);
	}
	
	/**
	 * Defines what an <code>Agent</code> will do each step
	 */
	public void step(){
		if(!fairyFoundAllGoals()){
			nextSearchStep();
		}
		else nextStep();
		
		//end of method
		//if(currentNode.hasGoal())
			//goalsSoFar++;
		if(goalsSoFar == grid.getNumGoals())
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
				if(!grid.getNode(currentNode.getX(),currentNode.getY()-1).isWall()){
					addToX = 0;
					addToY = -1;
				}
				break;
			case EAST:
				if(!grid.getNode(currentNode.getX()+1,currentNode.getY()).isWall()){
					addToX = 1;
					addToY = 0;
				}
				break;
			case SOUTH:
				if(!grid.getNode(currentNode.getX(),currentNode.getY()+1).isWall()){
					addToX = 0;
					addToY = 1;
				}
				break;
			case WEST:
				if(!grid.getNode(currentNode.getX()-1,currentNode.getY()).isWall()){
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
		grid.setAgentLocation(currentNode);
		grid.addToEvaluated(currentNode); //this Node has now been evaluated

		
		
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
	 * Returns the distance to the closest goal. Treating the line from the
	 * <code>Agent</code> to the goal as a hypotenuse, the distance is calculated by
	 * adding together the lengths of the two legs of the triangle, where
	 * one unit is one <code>Node</code> on the map.
	 * @return the distance to the closest goal from the <code>Agent</code>
	 */
	public int getDistanceToGoal(){
		return grid.agentToClosestGoal();
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
	
	/** methods that we will punt to the Fringe class **/	
	public void addToFringe(Node n){
		fringe.addToFringe(n);
	}
	
	public Node getNextFringeNode(){
		return fringe.getNextFringeNode();
	}
	
	public Node peekNextFringeNode(){
		return fringe.peekNextFringeNode();
	}
	
	public boolean fringeContains(Node n){
		return fringe.fringeContains(n);
	}
	
	public LinkedList<Node> getFringe(){
		return fringe.getFringe();
	}
	
	/** methods that we will punt to the Fairy class **/
	public boolean fairyFoundAllGoals(){
		return fairy.fairyFoundAllGoals();
	}
	
	public boolean fairyFoundGoal(){
		return fairy.fairyFoundGoal();
	}
	
	public int fairyGoalsRemaining(){
		return fairy.fairyGoalsRemaining();
	}
	
	public Node getFairyLocation(){
		return fairy.getFairyLocation();
	}
	
	public void moveSearchLocation(Node loc){
		fairy.moveSearchLocation(loc);
	}
	
	public Node getNorthOfFairyLocation(){
		return fairy.getNorthOfFairyLocation();
	}
	
	public Node getEastOfFairyLocation(){
		return grid.getNode(currentNode.getX()+1,currentNode.getY());
	}
	
	public Node getSouthOfFairyLocation(){
		return grid.getNode(currentNode.getX(),currentNode.getY()+1);
	}
	
	public Node getWestOfFairyLocation(){
		return grid.getNode(currentNode.getX()-1,currentNode.getY());
	}
	
	/** getters and setters **/
	/**
	 * Returns the current <code>Node</code> that the <code>Agent</code> is occupying
	 * @return the current <code>Node</code> that the <code>Agent</code> is occupying
	 */
	public Node getAgentLocation(){return currentNode;}
	
	/**
	 * Assigns the given <code>Fairy</code> to the <code>Agent</code>
	 * @param f
	 */
	public void setFairy(Fairy f){fairy = f;}
	
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
	
	/*public int getMovementCost(){
	
	}*/
	
	/**
	 * Returns the current heading of the <code>Agent</code> as an arrow representing a cardinal direction
	 * @return the current heading of the <code>Agent</code> as an arrow representing a cardinal direction
	 */
	public static String headingToArrowString(){
		String ret = "failure";
		switch(HEADING){
			case NORTH:
				ret = "^";
				break;
			case EAST:
				ret = ">";
				break;
			case SOUTH:
				ret = "v";
				break;
			case WEST:
				ret = "<";
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
	
	/**
	 * Allows the developer to print a message to the log. Automatically adds  
	 * a newline character at the end of the string.
	 * @param s the String that the developer wants displayed in the log
	 */
	public void log(String s){
		ApplicationWindow.writeToLog(s+"\n");
		//ApplicationWindow.setLogMessage(s + "\n");
	}

}
