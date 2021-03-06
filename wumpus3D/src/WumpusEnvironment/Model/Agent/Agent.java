package WumpusEnvironment.Model.Agent;

import WumpusEnvironment.View.MainWindow.*;
import WumpusEnvironment.Model.Agent.Search.Fairy;
import WumpusEnvironment.Model.Agent.Search.Fringe;
import WumpusEnvironment.Model.Map.*;

import java.util.*;

/**
 * Represents the Agent that the programmer will be designing
 * @author Bhargav
 *
 */
public abstract class Agent {
	//agent directional variables
	public static final int NORTH = 0;
	public static final int EAST = 1;
	public static final int SOUTH = 2;
	public static final int WEST = 3;
	public static int HEADING; //the direction the Agent is facing
	
	//agent status variables
	public static final int SAFE = 1;
	public static final int HIT_WALL = 2;
	public static final int DIED_TO_WUMPUS = 3;
	public static final int DAMAGED_BY_MINION = 4;
	public static final int DIED_TO_MINION = 5;
	public static final int DIED_TO_PIT = 6;
	public static final int GOAL_FOUND = 7;
	
	//agent arrow status variables
	public static final int HIT_MINION = 0;
	public static final int HIT_WUMPUS = 1;
	public static final int HIT_NOTHING = 2;
	
	//agent belief variables	
	//belief types
	public static final int SAFE_HERE = 0;
	public static final int MINION_HERE = 1;
	public static final int PIT_HERE = 2;
	public static final int WUMPUS_HERE = 3;
	public static final int GOLD_HERE = 4;
	public static final int WALL_HERE = 5;

	//belief values
	public static final int UNKNOWN = 6;
	public static final int NO = 7;
	public static final int MAYBE = 8;
	public static final int YES = 9;
	
	//movement and search costs
	private static int MOVE_COST = 20;
	private static int TURN_COST = 5;
	private static int GOLD_HINT_COST = 25;
	private static int CURRENT_NODE_COST = 1;
	private static int ADJACENT_NODE_COST = 5;
	private static int HIT_MINION_COST = 1000;
	private static int ARROW_COST = 10;
	private static int GOLD_REWARD = 5000;
	private static int START_POINTS = 10000;
	private static int LIFE_MULT = 50;
	
	/**
	 * The grid on which the <code>Agent</code> is operating
	 */
	private static Grid grid;
	
	/**
	 * The Fairy that will assist the Agent in doing off-line searches
	 */
	private Fairy fairy;
	
	/**
	 * The fringe that may be used for off-line searches
	 */
	private Fringe fringe;
	
	/**
	 * How many goals have been accomplished so far
	 */
	private int goalsSoFar;
	
	/**
	 * The Node the <code>Agent</code> is currently occupying
	 */
	private Node currentNode;
	
	/**
	 * The status of the <code>Agent</code> in its currently occupied <code>Node</code>
	 */
	private int currentNodeStatus;
	
	/**
	 * The Node at which the <code>Agent</code> starts on the map. Used for reset().
	 */
	private Node startNode;
	
	/*
	 * variables for various points
	 */
	/**
	 * How much life the <code>Agent</code> has left
	 */
	private int lifePoints;
	
	/**
	 * What it has cost the <code>Agent</code> so far to move around the world
	 */
	private int movementCost;
	
	public Agent(){
		grid = Grid.getInstance();
		fairy = null;
		fringe = new Fringe();
		startNode = new Node(0,0);
		currentNode = startNode;
		currentNodeStatus = SAFE;
		HEADING = EAST;
		goalsSoFar = 0;
		lifePoints = 100;
		movementCost = 0;
	}
	
	/**
	 * Initializes the costs for Agent actions from the properties.dat file.
	 * @param costs the array of costs--this is only done to avoid having too many input parameters
	 */
	public static void initCosts(int[] costs){
		START_POINTS = costs[0];
		MOVE_COST = costs[1];
		TURN_COST = costs[2];
		ARROW_COST = costs[3];
		CURRENT_NODE_COST = costs[4];
		ADJACENT_NODE_COST = costs[5];
		GOLD_HINT_COST = costs[6];
		HIT_MINION_COST = costs[7];
		GOLD_REWARD = costs[8];
		LIFE_MULT = costs[9];
	}
	
	/**
	 * The reset that happens to the <code>Agent</code> regardless of whether or not <code>reset()</code> is implemented.
	 */
	public void privateReset(){
		if(MapLoader.isSearchMap())
			fairy = new Fairy(startNode);
		else fairy = null;
		fringe = new Fringe();
		currentNodeStatus = SAFE;
		currentNode = startNode;
		HEADING = EAST;
		goalsSoFar = 0;
		lifePoints = 100;
		movementCost = 0;
		grid = Grid.getInstance();
		reset(); // the student's reset
	}
	
	/**
	 * Resets the <code>Agent</code> in the case that learning is being tested.
	 */
	public void learningReset(){
		if(MapLoader.isSearchMap())
			fairy = new Fairy(startNode);
		else fairy = null;
		currentNodeStatus = SAFE;
		currentNode = startNode;
		HEADING = EAST;
		goalsSoFar = 0;
		lifePoints = 100;
		grid = Grid.getInstance();
	}
	
	/**
	 * Sets the start location of the <code>Agent</code> to the given <code>Node</code>
	 * @param n the <code>Node</code> which will be the start location for the <code>Agent</code>
	 */
	public void setStartLocation(Node n){
		startNode = grid.getNode(n.getX(),n.getY());
		currentNode = startNode;
		grid.setAgentStartLocation(grid.getNode(n.getX(),n.getY()));
	}
	
	/**
	 * Defines what an <code>Agent</code> will do each step
	 */
	public void agentStep(){
		if(hasFairy() && !fairyFoundAllGoals()){
			searchStep();
		}
		else step();
		
		//end of method
		if(goalsSoFar == grid.getNumGoals())
			gameOverSuccess();
	}
	
	/**
	 * The next step the <code>Agent</code> will take. This method will be written by the student.
	 */
	public abstract void step();
	
	/**
	 * The reset method that students will implement in case they need any variables reset.
	 */
	public void reset(){}
	
	/**
	 * The next step the <code>Fairy</code> will take. This method will be written by the student.
	 * This method is not abstract, in order to allow for the instructor to provide
	 * <code>Agent</code> files that do not require <code>nextSearchStep()</code> to be included for the assignment. 
	 */
	public void searchStep(){}
	
	/**
	 * Defines the system behavior when all objectives have been completed
	 */
	private void gameOverSuccess(){
		if(!grid.trySetSolved()){
			Logger.generateLogEntry(this, grid);
			Logger.writeToLog(Logger.logSeparator);
			Logger.writeToLog("\r\n\r\nLEARNING RESET\r\n");
			grid.learningReset();
			learningReset();
			//sleep after resetting to let user acclimate
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
			}
		}
	}
	
	/**
	 * Fires an arrow from the location of the Agent in the specified direction
	 * @param direction the cardinal direction in which to fire the arrow
	 * @return the value of what the arrow hit
	 */
	public int fireArrow(int direction){
		//display action in log
		Logger.writeToLog("* Fired an arrow *\r\n");
		movementCost += ARROW_COST; //there is a cost to fire an arrow
		Node arrowPos = currentNode; //start arrow at agent's location
		int addToX, addToY;
		switch(direction){
			case NORTH:
				addToX = 0;
				addToY = -1;
				break;
			case EAST:
				addToX = 1;
				addToY = 0;
				break;
			case SOUTH:
				addToX = 0;
				addToY = 1;
				break;
			case WEST:
				addToX = -1;
				addToY = 0;
				break;
			default:
				addToX = 0;
				addToY = 0;
		}
		//keep moving the arrow until it reaches an end condition
		while(!grid.getNode(arrowPos.getX()+addToX,arrowPos.getY()+addToY).hasWall()){
			arrowPos = grid.getNode(arrowPos.getX()+addToX,arrowPos.getY()+addToY);
			if(arrowPos.hasMinion() && arrowPos.getMinionStatus() != Node.DEAD){
				grid.setNodeType(arrowPos.getX(),arrowPos.getY(),Grid.MINION,false); //there is no longer a minion here
				Logger.writeToLog("You hear a shriek of pain...\r\n");
				return HIT_MINION;
			}
			else if(arrowPos.hasWumpus() && arrowPos.getWumpusStatus() != Node.DEAD){
				grid.setNodeType(arrowPos.getX(),arrowPos.getY(),Grid.WUMPUS,false); //there is no longer a Wumpus here
				Logger.writeToLog("You hear a deafening roar...\r\n");
				return HIT_WUMPUS;
			}
		}
		Logger.writeToLog("You listen for a dying scream, but it seems like your arrow missed...\r\n");
		return HIT_WALL;
	}
	
	/*
	 * Agent movement-related methods
	 */
	
	/**
	 * Moves the <code>Agent<code> forward by one space, if possible.
	 * @return the status of the <code>Agent</code> after the move
	 */
	public int moveForward(){
		movementCost += MOVE_COST;
		try{
			Thread.sleep(ApplicationWindow.CURRENT_DELAY);
		}
		catch(InterruptedException e){
		}
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
		grid.setNodeType(currentNode.getX(),currentNode.getY(),Grid.AGENT,false); //Agent has moved from this spot
		currentNode = grid.getNode(currentNode.getX()+addToX,currentNode.getY()+addToY);
		grid.setNodeType(currentNode.getX(),currentNode.getY(),Grid.AGENT,true); //Agent is now here
		grid.setAgentLocation(currentNode); //update current node the Agent is on
		grid.setAgentHeading(HEADING);
		grid.addToEvaluated(currentNode); //this Node has now been evaluated		
		
		//also need to adjust score for movement and search
		currentNodeStatus = currNodeStatus(); //update the status in the Agent's current Node
		return currentNodeStatus;
	}
	
	/**
	 * Faces the <code>Agent</code> to the specified direction
	 * @param heading the direction to face the <code>Agent</code>
	 */
	public void turnTo(int heading){
		//opposite direction
		if(Math.abs(HEADING - heading) == 2)
			movementCost += TURN_COST*2;
		//make sure not the same direction
		else if(HEADING - heading != 0)
			movementCost += TURN_COST;
		
		//update direction
		HEADING = heading;
	}
	
	/**
	 * Faces the <code>Agent</code> to the right of its current heading
	 */
	public void turnRight(){
		movementCost += TURN_COST;
		if(HEADING == WEST)
			HEADING = NORTH;
		else HEADING++;
		
		try{
			Thread.sleep(ApplicationWindow.CURRENT_DELAY);
		}
		catch(InterruptedException e){
		}
	}
	
	/**
	 * Faces the <code>Agent</code> to the left of its current heading
	 */
	public void turnLeft(){
		movementCost += TURN_COST;
		if(HEADING == NORTH)
			HEADING = WEST;
		else HEADING--;
		
		try{
			Thread.sleep(ApplicationWindow.CURRENT_DELAY);
		}
		catch(InterruptedException e){
		}
	}
	
	/**
	 * Moves the <code>Agent</code> north of its current location
	 */
	public int moveNorth(){
		if(HEADING != NORTH) //don't want to use turn cost if already facing there
			turnTo(NORTH);
		return moveForward();
	}
	
	/**
	 * Moves the <code>Agent</code> east of its current location
	 */
	public int moveEast(){
		if(HEADING != EAST) //don't want to use turn cost if already facing there
			turnTo(EAST);
		return moveForward();
	}
	
	/**
	 * Moves the <code>Agent</code> south of its current location
	 */
	public int moveSouth(){
		if(HEADING != SOUTH) //don't want to use turn cost if already facing there
			turnTo(SOUTH);
		return moveForward();
	}
	
	/**
	 * Moves the <code>Agent</code> west of its current location
	 */
	public int moveWest(){
		if(HEADING != WEST) //don't want to use turn cost if already facing there
			turnTo(WEST);
		return moveForward();
	}
	
	/**
	 * Returns the distance to the closest goal. Treating the line from the
	 * <code>Agent</code> to the goal as a hypotenuse, the distance is calculated by
	 * adding together the lengths of the two legs of the triangle, where
	 * one unit is one <code>Node</code> on the map.
	 * @return the distance to the closest goal from the <code>Agent</code>
	 */
	public int getDistanceToGold(){
		movementCost += GOLD_HINT_COST;
		return grid.distanceToClosestGoal(currentNode);
	}
	
	/**
	 * Returns the distance to the closest goal. Treating the line from the given
	 * <code>Node</code> to the goal as a hypotenuse, the distance is calculated by
	 * adding together the lengths of the two legs of the triangle, where
	 * one unit is one <code>Node</code> on the map.
	 * @param node the <code>Node</code> from which you want to find the distance to the closest goal
	 * @return the distance to the closest goal from the given <code>Node</code>
	 */
	public int getDistanceToGold(Node node){
		movementCost += GOLD_HINT_COST;
		return grid.distanceToClosestGoal(node);
	}
	
	/**
	 * Returns the direction of the closest goal from the location of the <code>Agent</code>
	 * @return the direction of the closest goal from the location of the <code>Agent</code>
	 */
	public int getDirectionOfGold(){
		movementCost += GOLD_HINT_COST;
		return grid.directionOfClosestGoal(currentNode);
	}
	
	/**
	 * Returns the direction of the closest goal from the location of the given <code>Node</code>
	 * @return the direction of the closest goal from the location of the given <code>Node</code>
	 */
	public int getDirectionOfGold(Node node){
		movementCost += GOLD_HINT_COST;
		return grid.directionOfClosestGoal(node);
	}
	
	/**
	 * Determines the value of what is occupying the <code>Node</code> other than the <code>Agent</code>
	 * @return the value of what is occupying the <code>Node</code> other than the <code>Agent</code>
	 */
	private int currNodeStatus(){
		int value = SAFE;
		if(currentNode.hasGold()) value = GOAL_FOUND;
		else if(currentNode.hasWumpus() && currentNode.getWumpusStatus() != Node.DEAD) value = DIED_TO_WUMPUS;
		else if(currentNode.hasPit()) value = DIED_TO_PIT;
		else if(currentNode.hasMinion() && currentNode.getMinionStatus() != Node.DEAD) value = DAMAGED_BY_MINION;
		determinePenalty(value); //figure out what, if any, penalties to apply to score
		return value;
	}
	
	/**
	 * Determines the penalty to be incurred by a certain move
	 * @param value the value of what is occupying the <code>Node</code> other than the <code>Agent</code>
	 */
	private void determinePenalty(int value){
		switch(value){
			case SAFE:
				//do nothing presumably
				break;
			case DAMAGED_BY_MINION:
				//what to do if we hit a minion
				movementCost += HIT_MINION_COST;
				lifePoints -= 10;
				if(lifePoints == 0)
					grid.setNodeType(currentNode.getX(),currentNode.getY(),Grid.AGENT,false); //Agent is dead
				break;
			case DIED_TO_WUMPUS:
				//what to do if we hit a Wumpus
				lifePoints = 0;
				grid.setNodeType(currentNode.getX(),currentNode.getY(),Grid.AGENT,false); //Agent is dead
				break;
			case DIED_TO_PIT:
				//what to do if we hit a pit
				lifePoints = 0;
				grid.setNodeType(currentNode.getX(),currentNode.getY(),Grid.AGENT,false); //Agent is dead
				break;
			case GOAL_FOUND:
				//what to do if we hit a goal
				movementCost -= GOLD_REWARD;
				goalsSoFar++;
				grid.setNodeType(currentNode.getX(), currentNode.getY(), Grid.GOAL, false);
				break;
		}
	}
	
	/*
	 * methods regarding Agent beliefs
	 */
	/**
	 * Returns the belief value for the given belief type at the location of the <code>Agent</code>
	 * @param type the belief type to return the belief value for
	 * @return the belief value for the given belief type at the location of the <code>Agent</code>
	 */
	public int getBelief(int type){
		return currentNode.getBelief(type);
	}
	
	/**
	 * Returns the belief value for the given belief type at the location of the given <code>Node</code>
	 * @param node the <code>Node</code> for which the belief value is to be returned
	 * @param type the belief type to return the belief value for
	 * @return the belief value for the given belief type at the location of the given <code>Node</code>
	 */
	public int getBelief(Node node, int type){
		return grid.getNode(node.getX(),node.getY()).getBelief(type);
	}
	
	/**
	 * Returns the belief value for the given belief type at the <code>Node</code> adjacent to the 
	 * <code>Agent</code> in the given direction
	 * @param direction the direction from which to get an adjacent <code>Node</code>
	 * @param type the belief type to return the belief value for
	 * @return the belief value for the given belief type at the <code>Node</code> adjacent to the <code>Agent</code> in the given direction
	 */
	public int getBelief(int direction, int type){
		return getAdjacentNode(currentNode,direction).getBelief(type);
	}
	
	/**
	 * Returns the belief value for the given belief type at the <code>Node</code> adjacent to the 
	 * given <code>Node</code> in the given direction
	 * @param node the <code>Node</code> for which the belief value is to be returned
	 * @param direction the direction from which to get an adjacent <code>Node</code>
	 * @param type the belief type to return the belief value for
	 * @return the belief value for the given belief type at the <code>Node</code> adjacent to the given <code>Node</code> in the given direction
	 */
	public int getBelief(Node node, int direction, int type){
		return getAdjacentNode(node,direction).getBelief(type);
	}
	
	/**
	 * Sets the belief value for the given belief type at the location of the <code>Agent</code>
	 * @param type the belief type to return the belief value for
	 * @param belief the belief value to set for the given belief type
	 * @return the belief value for the given belief type at the location of the <code>Agent</code>
	 */
	public void setBelief(int type, int belief){
		grid.getNode(currentNode.getX(),currentNode.getY()).setBelief(type,belief);
	}
	
	/**
	 * Sets the belief value for the given belief type at the location of the given <code>Node</code>
	 * @param node the <code>Node</code> for which the belief value is to be returned
	 * @param type the belief type to return the belief value for
	 * @param belief the belief value to set for the given belief type
	 * @return the belief value for the given belief type at the location of the given <code>Node</code>
	 */
	public void setBelief(Node node, int type, int belief){
		grid.getNode(node.getX(),node.getY()).setBelief(type,belief);
	}
	
	/**
	 * Returns the belief value for the given belief type at the <code>Node</code> adjacent to the 
	 * <code>Agent</code> in the given direction
	 * @param direction the direction from which to get an adjacent <code>Node</code>
	 * @param type the belief type to return the belief value for
	 * @return the belief value for the given belief type at the <code>Node</code> adjacent to the <code>Agent</code> in the given direction
	 */
	public void setBelief(int direction, int type, int belief){
		getAdjacentNode(currentNode,direction).setBelief(type,belief);
	}
	
	/**
	 * Sets the belief value for the given belief type at the <code>Node</code> adjacent to the 
	 * given <code>Node</code> in the given direction
	 * @param node the <code>Node</code> for which the belief value is to be returned
	 * @param direction the direction from which to get an adjacent <code>Node</code>
	 * @param type the belief type to return the belief value for
	 * @return the belief value for the given belief type at the <code>Node</code> adjacent to the given <code>Node</code> in the given direction
	 */
	public void setBelief(Node node, int direction, int type, int belief){
		getAdjacentNode(node,direction).setBelief(type,belief);
	}
	
	/*
	 * methods that we will punt to the Fringe class 
	 */	
	public void addToFringe(Node n){
		fringe.addToFringeTail(n);
	}
	
	public void addToFringeHead(Node n){
		fringe.addToFringeHead(n);
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
	
	public ArrayDeque<Node> getFringe(){
		return fringe.getFringe();
	}
	
	public String fringeToString(){
		return fringe.toString();
	}
	
	/* 
	 * methods that we will punt to the Fairy class  
	 */
	public boolean fairyFoundAllGoals(){
		return fairy.fairyFoundAllGold();
	}
	
	public boolean fairyFoundGoal(){
		return fairy.fairyFoundGold();
	}
	
	public int fairyGoalsRemaining(){
		return fairy.fairyGoalsRemaining();
	}
	
	public Node getFairyLocation(){
		return fairy.getFairyLocation();
	}
	
	public int moveFairyLocation(Node loc){
		return fairy.moveFairyLocation(loc);
	}
	
	public Node getNorthOfFairyLocation(){
		return fairy.getNorthOfFairyLocation();
	}
	
	public Node getEastOfFairyLocation(){
		return fairy.getEastOfFairyLocation();
	}
	
	public Node getSouthOfFairyLocation(){
		return fairy.getSouthOfFairyLocation();
	}
	
	public Node getWestOfFairyLocation(){
		return fairy.getWestOfFairyLocation();
	}
	
	/** getters and setters **/
	/**
	 * Returns the current <code>Node</code> that the <code>Agent</code> is occupying
	 * @return the current <code>Node</code> that the <code>Agent</code> is occupying
	 */
	public Node getCurrentLocation(){
		movementCost += CURRENT_NODE_COST;
		return grid.getNode(currentNode.getX(),currentNode.getY());
	}
	
	/**
	 * Returns whether or not the <code>Agent</code> is dead
	 * @return <code>true</code> if the <code>Agent</code> is dead, <code>false</code> otherwise
	 */
	public boolean isDead(){
		return lifePoints == 0;
	}
	
	/**
	 * Returns whether or not the map has been solved.
	 * @return whether or not the map has been solved.
	 */
	public boolean solvedPuzzle(){
		return goalsSoFar == grid.getNumGoals();
	}
	
	/**
	 * Returns the current number of points the <code>Agent</code> has earned from finding gold.
	 * @return the current number of points the <code>Agent</code> has earned from finding gold.
	 */
	public int getGoldPoints(){
		return goalsSoFar * GOLD_REWARD;
	}
	
	/**
	 * Returns whether or not one of the <code>Nodes</code> adjacent to the <code>Agent</code> contains a Wumpus minion
	 * @return true if one of the <code>Nodes</code> adjacent to the <code>Agent</code> contains a Wumpus minion, false otherwise
	 */
	public boolean nearMinion(){
		return (getAdjacentNode(currentNode,NORTH).hasMinion() || getAdjacentNode(currentNode,EAST).hasMinion()
				|| getAdjacentNode(currentNode,SOUTH).hasMinion() || getAdjacentNode(currentNode,WEST).hasMinion());
	}
	
	/**
	 * Returns whether or not one of the <code>Nodes</code> adjacent to the given <code>Node</code> contains a Wumpus minion
	 * @param node the <code>Node</code> from which you want want to test if there's a Wumpus minion adjacent
	 * @return true if one of the <code>Nodes</code> adjacent to the <code>Agent</code> contains a Wumpus minion, false otherwise
	 */
	public boolean nearMinion(Node node){
		return (getAdjacentNode(node,NORTH).hasMinion() || getAdjacentNode(node,EAST).hasMinion()
				|| getAdjacentNode(node,SOUTH).hasMinion() || getAdjacentNode(node,WEST).hasMinion());
	}
	
	/**
	 * Returns whether or not one of the <code>Nodes</code> adjacent to the <code>Agent</code> contains a Wumpus
	 * @return true if one of the <code>Nodes</code> adjacent to the <code>Agent</code> contains a Wumpus, false otherwise
	 */
	public boolean nearWumpus(){
		return (getAdjacentNode(currentNode,NORTH).hasWumpus() || getAdjacentNode(currentNode,EAST).hasWumpus()
				|| getAdjacentNode(currentNode,SOUTH).hasWumpus() || getAdjacentNode(currentNode,WEST).hasWumpus());
	}
	
	/**
	 * Returns whether or not one of the <code>Nodes</code> adjacent to the given <code>Node</code> contains a Wumpus
	 * @param node the <code>Node</code> from which you want want to test if there's a Wumpus adjacent
	 * @return true if one of the <code>Nodes</code> adjacent to the <code>Agent</code> contains a Wumpus, false otherwise
	 */
	public boolean nearWumpus(Node node){
		return (getAdjacentNode(node,NORTH).hasWumpus() || getAdjacentNode(node,EAST).hasWumpus()
				|| getAdjacentNode(node,SOUTH).hasWumpus() || getAdjacentNode(node,WEST).hasWumpus());
	}
	
	/**
	 * Returns whether or not one of the <code>Nodes</code> adjacent to the <code>Agent</code> contains a pit
	 * @return true if one of the <code>Nodes</code> adjacent to the <code>Agent</code> contains a pit, false otherwise
	 */
	public boolean nearPit(){
		return (getAdjacentNode(currentNode,NORTH).hasPit() || getAdjacentNode(currentNode,EAST).hasPit()
				|| getAdjacentNode(currentNode,SOUTH).hasPit() || getAdjacentNode(currentNode,WEST).hasPit());
	}
	
	/**
	 * Returns whether or not one of the <code>Nodes</code> adjacent to the given <code>Node</code> contains a pit
	 * @param node the <code>Node</code> from which you want want to test if there's a pit adjacent
	 * @return true if one of the <code>Nodes</code> adjacent to the <code>Agent</code> contains a pit, false otherwise
	 */
	public boolean nearPit(Node node){
		return (getAdjacentNode(node,NORTH).hasPit() || getAdjacentNode(node,EAST).hasPit()
				|| getAdjacentNode(node,SOUTH).hasPit() || getAdjacentNode(node,WEST).hasPit());
	}
	
	/**
	 * Returns whether or not one of the <code>Nodes</code> adjacent to the <code>Agent</code> contains gold
	 * @return true if one of the <code>Nodes</code> adjacent to the <code>Agent</code> contains gold, false otherwise
	 */
	public boolean nearGold(){
		return (getAdjacentNode(currentNode,NORTH).hasGold() || getAdjacentNode(currentNode,EAST).hasGold()
				|| getAdjacentNode(currentNode,SOUTH).hasGold() || getAdjacentNode(currentNode,WEST).hasGold());
	}
	
	/**
	 * Returns whether or not one of the <code>Nodes</code> adjacent to the given <code>Node</code> contains gold
	 * @param node the <code>Node</code> from which you want want to test if there's gold adjacent
	 * @return true if one of the <code>Nodes</code> adjacent to the <code>Agent</code> contains gold, false otherwise
	 */
	public boolean nearGold(Node node){
		return (getAdjacentNode(node,NORTH).hasGold() || getAdjacentNode(node,EAST).hasGold()
				|| getAdjacentNode(node,SOUTH).hasGold() || getAdjacentNode(node,WEST).hasGold());
	}
	
	/**
	 * Returns the <code>Node</code> adjacent to the given <code>Node</code> in the direction specified
	 * @param node the <code>Node</code> from which you want the adjacent <code>Node</code> returned
	 * @param direction the direction of the adjacent <code>Node</code>
	 * @return the <code>Node</code> adjacent to the given <code>Node</code> in the direction specified
	 */
	private Node getAdjacentNode(Node node, int direction){
		movementCost += ADJACENT_NODE_COST;
		Node ret;
		switch(direction){
			case NORTH:
				ret = grid.getNode(node.getX(),node.getY()-1);
				break;
			case EAST:
				ret = grid.getNode(node.getX()+1,node.getY());
				break;
			case SOUTH:
				ret = grid.getNode(node.getX(),node.getY()+1);
				break;
			case WEST:
				ret = grid.getNode(node.getX()-1,node.getY());
				break;
			default:
				ret = null;
				break;
		}
		return ret;
	}
	
	/**
	 * Will return the total movement cost up to this point.
	 * @return a value that is determined by how many moves have been made
	 */
	public int getMovementCost(){
		return movementCost;
	}
	
	/**
	 * Will return the total search cost up to this point.
	 * @return a value that is determined by how many moves have been made by the <code>Fairy</code>
	 */
	public int getSearchCost(){
		return fairy.getSearchCost();
	}
	
	/**
	 * Returns the current life points of the <code>Agent</code>
	 * @return
	 */
	public int getLifePoints(){
		return lifePoints;
	}
	
	/**
	 * Returns the performance value of the <code>Agent</code> to that point. The score is calculated by taking the
	 * max points and subtracting both the search cost and movement cost.
	 * @return the performance value of the <code>Agent</code> to that point
	 */
	public int getPerformanceValue(){
		if(isDead()) return 0; //no points if you die
		if(hasFairy())
			return START_POINTS - movementCost - fairy.getSearchCost() + (lifePoints * LIFE_MULT);
		else return START_POINTS - movementCost + (lifePoints * LIFE_MULT);
	}
	
	/**
	 * Returns whether or not the <code>Agent</code> has a <code>Fairy</code>
	 * @return <code>true</code> if the <code>Agent</code> has a <code>Fairy</code>, <code>false</code> otherwise
	 */
	public boolean hasFairy(){
		return !(fairy == null);
	}
	
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
		String ret = "failure that should never occur!!!";
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
		String ret = "";
		switch(currentNodeStatus){
			case SAFE:
				ret = safeString();
				break;
			case HIT_WALL:
				ret = "Bonk! You walked into a wall!\r\n";
				break;
			case DAMAGED_BY_MINION:
				if(lifePoints > 0)
					ret = "Ouch! Guess those minions aren't just for show after all.\r\n";
				else
					ret = "You perish valiantly doing battle against the hordes of minions.\r\n";
				break;
			case DIED_TO_WUMPUS:
				ret = "The Wumpus has been waiting for a snack...looks like you're it.\r\n";
				break;
			case DIED_TO_PIT:
				ret = "You've fallen into a pit! Good luck getting out before you turn to dust.\r\n";
				break;
			case GOAL_FOUND:
				ret = "You found gold!\r\n";
				break;
		}
		return ret;
	}
	
	/**
	 * Returns the string to be displayed when the <code>Agent</code> is considered to be safe
	 * @return the string to be displayed when the <code>Agent</code> is considered to be safe
	 */
	private String safeString(){
		String ret;
		boolean wumpus = nearWumpus(), pit = nearPit(), minion = nearMinion();
		if(wumpus && pit && minion)
			ret = "You smell something horrible nearby, feel a breeze, and hear Wumpus minions cackling nearby...\r\n";
		else if(wumpus && pit)
			ret = "You smell something horrible nearby and feel a breeze...\r\n";
		else if(wumpus && minion)
			ret = "You smell something horrible nearby and hear Wumpus minions cackling nearby...\r\n";
		else if(pit && minion)
			ret = "You feel a breeze and hear Wumpus minions cackling nearby...\r\n";
		else if(wumpus)
			ret = "You smell something horrible nearby...\r\n";
		else if(pit)
			ret = "You feel a breeze...\r\n";
		else if(minion)
			ret = "You hear Wumpus minions cackling nearby...\r\n";
		else ret = "You feel safe here...\r\n";
		if(nearGold())
			ret += "You see something glittering nearby...\r\n";
		return ret;
	}
	
	/**
	 * Gets the <code>Agent</code>'s current location and converts it to a string
	 * @return the <code>Agent</code>'s current location as a string
	 */
	public String locationToString(){
		return "Location: (" + currentNode.getX() + "," + currentNode.getY() + ")\r\n";
	}
	
	/**
	 * Allows the developer to print a message to the log. Automatically adds  
	 * a newline character at the end of the string.
	 * @param s the String that the developer wants displayed in the log
	 */
	public void log(String s){
		Logger.writeToLog(s+"\r\n");
	}

}
