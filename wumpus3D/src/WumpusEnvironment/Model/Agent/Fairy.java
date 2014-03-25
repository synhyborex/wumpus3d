package WumpusEnvironment.Model.Agent;

import WumpusEnvironment.Model.Map.*;

public class Fairy {
	
	public static final int SAFE = 0;
	public static final int HIT_WALL = 1;
	public static final int HIT_WUMPUS = 2;
	public static final int HIT_MINION = 3;
	public static final int HIT_PIT = 4;
	public static final int GOAL_FOUND = 5;
	
	/**
	 * The grid on which the <code>Fairy</code> is operating
	 */
	protected static Grid grid;
	
	/**
	 * How many goals have been accomplished so far
	 */
	protected int goalsSoFar;
	
	/**
	 * The Node the <code>Fairy</code> is currently occupying
	 */
	protected Node currentNode;

	public Fairy(Grid g, Node start) {
		grid = g;
		currentNode = start;
		goalsSoFar = 0;
		
		//put Fairy on Grid
		grid.setNode(currentNode.getX(),currentNode.getY(),Grid.FAIRY,true);
	}
	
	/**
	 * Returns whether the <code>Fairy</code> has found all goals on the map
	 * @return <code>true</code> if all goals have been found, <code>false</code> otherwise
	 */
	public boolean fairyFoundAllGoals(){
		return goalsSoFar == grid.getNumGoals();
	}
	
	/**
	 * A variation of <code>fairyFoundAllGoals()</code> that should only be 
	 * used on maps with only one goal. Use on maps with more will return true
	 * as long as the <code>Fairy</code> has found at least one of the goals.
	 * @return <code>true</code> if the goal has been found, <code>false</code> otherwise
	 */
	public boolean fairyFoundGoal(){
		return goalsSoFar >= 1;
	}
	
	/**
	 * Returns the number of goals that the <code>Fairy</code> still needs to find.
	 * This is much more useful on maps that have more than one goal, as
	 * <code>fairyFoundGoal()</code> will achieve the same effect on maps with only one.
	 * @return
	 */
	public int fairyGoalsRemaining(){
		return grid.getNumGoals() - goalsSoFar;
	}
	
	public Node getFairyLocation(){
		return grid.getNode(currentNode.getX(),currentNode.getY());
	}
	
	public int moveFairyLocation(Node loc){
		grid.setNode(currentNode.getX(),currentNode.getY(),Grid.FAIRY,false); //Fairy has moved from this spot
		currentNode = loc;
		grid.setNode(currentNode.getX(),currentNode.getY(),Grid.FAIRY,true); //Fairy is now here
		grid.addToEvaluated(loc);
		
		return newNodeStatus();
	}
	
	/**
	 * Determines the value of what is occupying the <code>Node</code> other than the <code>Agent</code>
	 * @return the value of what is occupying the <code>Node</code> other than the <code>Agent</code>
	 */
	protected int newNodeStatus(){
		int value = SAFE;
		if(currentNode.hasGoal()){
			value =  GOAL_FOUND;
			goalsSoFar++;
		}
		else if(currentNode.hasWumpus()) value =  HIT_WUMPUS;
		else if(currentNode.hasPit()) value =  HIT_PIT;
		else if(currentNode.hasMinion()) value = HIT_MINION;
		//printAgentStatus(value); //print the status of the Agent at the new Node
		return value;
	}
	
	public Node getNorthOfFairyLocation(){
		return grid.getNode(currentNode.getX(),currentNode.getY()-1);
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
	
	public int getSearchCost(){
		return 0;
	}

}
