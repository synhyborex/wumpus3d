package WumpusEnvironment.Model.Agent.Search;

import WumpusEnvironment.Model.Map.*;

public class Fairy {
	
	private static final int SAFE = 0;
	private static final int HIT_WALL = 1;
	private static final int HIT_WUMPUS = 2;
	private static final int HIT_MINION = 3;
	private static final int HIT_PIT = 4;
	private static final int GOAL_FOUND = 5;
	
	private static final int SEARCH_COST = 1;
	private static final int ADJ_COST = 5;
	
	/**
	 * The grid on which the <code>Fairy</code> is operating
	 */
	private static Grid grid;
	
	/**
	 * How many goals have been accomplished so far
	 */
	private int goalsSoFar;
	
	/**
	 * The Node the <code>Fairy</code> is currently occupying
	 */
	private Node currentNode;
	
	/**
	 * The total cost of searching.
	 */
	private int searchCost;

	public Fairy(Node start) {
		currentNode = start;
		goalsSoFar = 0;
		searchCost = 0;
		
		//put Fairy on Grid
		grid = Grid.getInstance();
		grid.setNodeType(currentNode.getX(),currentNode.getY(),Grid.FAIRY,true);
	}
	
	/**
	 * Returns whether the <code>Fairy</code> has found all goals on the map
	 * @return <code>true</code> if all goals have been found, <code>false</code> otherwise
	 */
	public boolean fairyFoundAllGold(){
		return goalsSoFar == grid.getNumGoals();
	}
	
	/**
	 * A variation of <code>fairyFoundAllGold()</code> that should only be 
	 * used on maps with only one goal. Use on maps with more will return true
	 * as long as the <code>Fairy</code> has found at least one of the goals.
	 * @return <code>true</code> if the goal has been found, <code>false</code> otherwise
	 */
	public boolean fairyFoundGold(){
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
		searchCost += SEARCH_COST;
		return grid.getNode(currentNode.getX(),currentNode.getY());
	}
	
	public int moveFairyLocation(Node loc){
		grid.setNodeType(currentNode.getX(),currentNode.getY(),Grid.FAIRY,false); //Fairy has moved from this spot
		currentNode = loc;
		grid.setNodeType(currentNode.getX(),currentNode.getY(),Grid.FAIRY,true); //Fairy is now here
		grid.addToEvaluated(loc);
		
		searchCost += SEARCH_COST; //update search cost
		
		return newNodeStatus();
	}
	
	/**
	 * Determines the value of what is occupying the <code>Node</code> other than the <code>Agent</code>
	 * @return the value of what is occupying the <code>Node</code> other than the <code>Agent</code>
	 */
	private int newNodeStatus(){
		int value = SAFE;
		if(currentNode.hasGold()){
			value =  GOAL_FOUND;
			goalsSoFar++;
		}
		else if(currentNode.hasWall()) value =  HIT_WALL;
		else if(currentNode.hasWumpus()) value =  HIT_WUMPUS;
		else if(currentNode.hasPit()) value =  HIT_PIT;
		else if(currentNode.hasMinion()) value = HIT_MINION;
		//printAgentStatus(value); //print the status of the Agent at the new Node
		return value;
	}
	
	public Node getNorthOfFairyLocation(){
		searchCost += ADJ_COST;
		return grid.getNode(currentNode.getX(),currentNode.getY()-1);
	}
	
	public Node getEastOfFairyLocation(){
		searchCost += ADJ_COST;
		return grid.getNode(currentNode.getX()+1,currentNode.getY());
	}
	
	public Node getSouthOfFairyLocation(){
		searchCost += ADJ_COST;
		return grid.getNode(currentNode.getX(),currentNode.getY()+1);
	}
	
	public Node getWestOfFairyLocation(){
		searchCost += ADJ_COST;
		return grid.getNode(currentNode.getX()-1,currentNode.getY());
	}
	
	public int getSearchCost(){
		return searchCost;
	}

}
