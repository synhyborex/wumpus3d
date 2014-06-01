package WumpusEnvironment.Model.Map;

import WumpusEnvironment.Model.Agent.*;

/**
 * Represents the underlying x-y grid the environment uses
 * @author Bhargav
 *
 */
public class Grid {
	public static final int GOAL = 1;
	public static final int WALL = 2;
	public static final int WUMPUS = 3;
	public static final int PIT = 4;
	public static final int MINION = 5;
	public static final int AGENT = 6;
	public static final int FAIRY = 7;
	
	protected int width; //the horizontal number of Nodes
	protected int height; //the vertical number of Nodes
	protected int numGoals; //the number of goal Nodes on the map
	protected boolean solved; //whether or not the map has been solved
	protected int learningCount; //whether or not the map will test learning
	protected static Node[][] grid; //the map we're operating on
	protected static Node[] goalLocations; //the locations of the goals on the map
	protected static Node agentLocation; //the location of the Agent on the map
	protected static int agentHeading; //the way the Agent is facing
	protected static Node agentStartLocation; //the location of the Agent at the initialization of the map
	
	//singleton
	private static Grid instance = new Grid();
	
	private Grid(){
		width = 1;
		height = 1;
		numGoals = 0;
		learningCount = 0;
	}
	
	private Grid(Node[][] grid){
		Grid.grid = grid;
	}
	
	public void learningReset(){
		//move agent back to start
		agentLocation.hasAgent = false;
		agentLocation = agentStartLocation;
		agentLocation.hasAgent = true;
		
		//bring dead back to life
		for(int i = 0; i < height; i++){
			for(int j = 0; j < width; j++){
				if(grid[i][j].wumpusStatus == Node.DEAD)
					grid[i][j].wumpusStatus = Node.ALIVE;
				if(grid[i][j].minionStatus == Node.DEAD)
					grid[i][j].minionStatus = Node.ALIVE;
				
				//set goals again
				for(int k = 0; k < goalLocations.length; k++){
					if(i == goalLocations[k].y && j == goalLocations[k].x){
						grid[i][j].hasGoal = true;
					}
				}
			}
		}
		
	}
	
	public static Grid getInstance(){return instance;}
	
	/*public Grid(int width, int height, int numGoals){
		this.width = width;
		this.height = height;
		this.numGoals = numGoals;
		setSolved(false);
		
		//initialize grid
		grid = new Node[height][width];
		goalLocations = new Node[numGoals];
		gridInit();
	}*/
	
	/**
	 * The alternate "constructor" to allow singleton
	 * @param width width of the grid
	 * @param height height of the grid
	 * @param numGoals the number of goals on the grid
	 */
	public void gridInit(int width, int height, int numGoals, int learning){
		this.width = width;
		this.height = height;
		this.numGoals = numGoals;
		this.learningCount = learning;
		this.solved = false;
		
		//initialize grid
		grid = new Node[height][width];
		goalLocations = new Node[numGoals];
		
		//make sure each index of array is a Node
		for(int i = 0; i < height; i++){
			for(int j = 0; j < width; j++){
				grid[i][j] = new Node(j,i);
				
				//all edges of the map are walls
				if((i == 0 || i == height-1) || (j == 0 || j == width-1)){
					grid[i][j].isWall = true;
				}
			}
		}
		
		//initialize goal locations array
		for(int i = 0; i < goalLocations.length; i++){
			goalLocations[i] = new Node(-1,-1);
		}
		
		//initialize location of Agent
		agentLocation = new Node(0,0);
		agentStartLocation = new Node(0,0);
	}
	
	/**
	 * Prints the current Grid based on what each Node contains
	 */
	public void printGrid(){
		for(int i = 0; i < height; i++){
			for(int j = 0; j < width; j++){
				if(grid[i][j].hasGoal)
					System.out.print("G ");
				else if(grid[i][j].isWall)
					System.out.print("X ");
				else if(grid[i][j].hasWumpus)
					System.out.print("W ");
				else if(grid[i][j].hasPit)
					System.out.print("P ");
				else if(grid[i][j].hasMinion)
					System.out.print("M ");
				else if(grid[i][j].hasAgent)
					System.out.print(Agent.headingToArrowString() + " ");
				else if(grid[i][j].hasFairy)
					System.out.print("F ");
				else System.out.print("- ");
			}
			System.out.println();
		}
	}
	
	/**
	 * Returns the current Grid as a single string
	 */
	public String gridToString(){
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < height; i++){
			for(int j = 0; j < width; j++){
				if(grid[i][j].hasGoal)
					sb.append("G ");
				else if(grid[i][j].isWall)
					sb.append("X ");
				else if(grid[i][j].hasWumpus)
					sb.append("W ");
				else if(grid[i][j].hasPit)
					sb.append("P ");
				else if(grid[i][j].hasMinion)
					sb.append("M ");
				else if(grid[i][j].hasAgent)
					sb.append(Agent.headingToArrowString() + " ");
				else if(grid[i][j].hasFairy)
					sb.append("F ");
				else sb.append("- ");
			}
			sb.append("\r\n");
		}
		
		return sb.toString();
	}
	
	public void addToEvaluated(Node n){
		grid[n.getY()][n.getX()].setAsEvaluated();
	}
	
	public int distanceToClosestGoal(Node node){
		return distanceBetweenNodes(node,findClosestGoal(node));
	}
	
	//returns -1 if you're directly on top of the goal and there are no others
	public int directionOfClosestGoal(Node node){
		Node goal = findClosestGoal(node);
		int ns = node.getY() - goal.getY(); //negative means SOUTH, positive means NORTH
		int ew = node.getX() - goal.getX(); //negative means EAST, positive means WEST
		
		if(ns == 0 && ew == 0) return -1; //both are 0
		//one of the two is 0
		if(ns == 0){
			if(ew < 0) return Agent.EAST;
			if(ew > 0) return Agent.WEST;
		}
		if(ew == 0){
			if(ns < 0) return Agent.SOUTH;
			if(ns > 0) return Agent.NORTH;
		}		
		
		//neither is 0
		//we want to return the direction that is further from the goal if, for example,
		//the goal is both NORTH and WEST of the given Node
		if(Math.abs(ns) > Math.abs(ew)){ //check abs of distance, since sign indicates direction
			if(ns < 0) return Agent.SOUTH;
			if(ns > 0) return Agent.NORTH;
		}
		else{ //ew is >= ns
			if(ew < 0) return Agent.EAST;
			if(ew > 0) return Agent.WEST;
		}
		
		return -2; //if it ever gets here it means I missed a case
	}
	
	//if this ever throws arrayindexoutofboundsexception then something is very wrong.
	//the only time it will ever get there is if for some reason this method is called
	//after all goals have been found...in which case it should be game over anyway
	protected Node findClosestGoal(Node node){
		if(numGoals == 1) return goalLocations[0];
		int ret = -1;
		int shortest = Integer.MAX_VALUE;
		for(int i = 0; i < goalLocations.length; i++){
			int dist = Integer.MAX_VALUE;
			if(!goalLocations[i].isEvaluated())
				dist = distanceBetweenNodes(node,goalLocations[i]);
			if(dist < shortest){
				shortest = dist;
				ret = i;
			}
		}		
		return goalLocations[ret];
	}
	
	protected int distanceBetweenNodes(Node a, Node b){
		return Math.abs(a.getX()-b.getX()) + Math.abs(a.getY()-b.getY());
	}

	/** getters and setters **/
	/**
	 * Returns the number of goals on the current map
	 * @return the number of goals on the current map
	 */
	public int getNumGoals(){
		return numGoals;
	}
	
	/**
	 * Returns the width of the grid
	 * @return the width of the grid
	 */
	public int getWidth(){
		return width;
	}
	
	/**
	 * Returns the height of the grid
	 * @return the height of the grid
	 */
	public int getHeight(){
		return height;
	}
	
	/**
	 * Returns the location of the <code>Agent</code> on the <code>Grid</code>
	 * @return the location of the <code>Agent</code> on the <code>Grid</code>
	 */
	public Node getAgentLocation(){
		return agentLocation;
	}
	
	/**
	 * Returns the current direction in which the <code>Agent</code> is facing
	 * @return the current direction in which the <code>Agent</code> is facing
	 */
	public int getAgentHeading(){
		return agentHeading;
	}
	
	/**
	 * Returns the <code>Node</code> at the requested location on the map
	 * @param x the x location on the map
	 * @param y the y location on the map
	 * @return the <code>Node</code> at the given (x,y) location on the map
	 */
	public Node getNode(int x, int y){
		return grid[y][x];
	}
	
	/**
	 * Returns whether or not all the goals on the map have been found.
	 * @return <code>true</code> if all goals have been found, <code>false</code> otherwise
	 */
	public boolean isSolved(){return solved;}
	
	/**
	 * Allows the setting of the boolean values that represent map objects.
	 * @param y the horizontal position on the map
	 * @param x the vertical position on the map
	 * @param what which value to change
	 * @param b what to change <code>what</code> to
	 */
	public void setNodeType(int y, int x, int what, boolean b){
		switch(what){
			case GOAL:
				grid[x][y].hasGoal = b;
				break;
			case WALL:
				grid[x][y].isWall = b;
				break;
			case WUMPUS:
				if(grid[x][y].hasWumpus)
					grid[x][y].wumpusStatus = Node.DEAD;
				else grid[x][y].hasWumpus = b;
				break;
			case PIT:
				grid[x][y].hasPit = b;
				break;
			case MINION:
				if(grid[x][y].hasMinion)
					grid[x][y].minionStatus = Node.DEAD;
				else grid[x][y].hasMinion = b;
				break;
			case AGENT:
				grid[x][y].hasAgent = b;
				break;
			case FAIRY:
				grid[x][y].hasFairy = b;
				break;
			default:
				System.out.println("Improper map object to change.");
				break;
		}
	}
	
	/**
	 * Will set the <code>Grid</code> to solved if there are no learning loops left.
	 * @return <code>true</code> if the Grid has been set to solved, <code>false</code> otherwise
	 */
	public boolean trySetSolved(){
		if(learningCount == 0){ //no more loops to repeat
			this.solved = true;
			return true;
		}
		else {
			learningCount--; //otherwise there is now one less
			return false;
		}
	}
	
	/**
	 * Sets the <code>Grid</code> to not-solved.
	 */
	public void setNotSolved(){solved = false;}
	
	/**
	 * Sets the location of the <code>Agent</code> to the given <code>Node</code>
	 * @param loc the <code>Node</code> that the <code>Agent</code> should be relocated to
	 */
	public void setAgentLocation(Node loc){
		agentLocation = loc;
	}
	
	/**
	 * Sets the direction the <code>Agent</code> is facing
	 * @param heading the heading of the <code>Agent</code> 
	 */
	public void setAgentHeading(int heading){
		agentHeading = heading;
	}
	
	/**
	 * Sets the start location of the <code>Agent</code> to the given <code>Node</code>
	 * @param loc the <code>Node</code> that the <code>Agent</code> starts at
	 */
	public void setAgentStartLocation(Node loc){
		agentStartLocation = loc;
	}
	
	/**
	 * Sets the given <code>Node</code> to contain a goal
	 * @param loc the <code>Node</code> that should be turned into a goal square
	 */
	public void setGoalLocation(Node loc){
		for(int i = 0; i < goalLocations.length; i++){
			if(goalLocations[i].getX() == -1){
				goalLocations[i] = loc;
				break;
			}
		}
	}
}