package WumpusEnvironment.Map;

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
	private boolean solved; //whether or not the map has been solved
	protected static Node[][] grid; //the map we're operating on
	
	public Grid(int width, int height, int numGoals){
		this.width = width;
		this.height = height;
		this.numGoals = numGoals;
		setSolved(false);
		
		//initialize grid
		grid = new Node[height][width];
		gridInit();
	}
	
	protected void gridInit(){
		//make sure each index of array is a Node
		for(int i = 0; i < height; i++){
			for(int j = 0; j < width; j++){
				grid[i][j] = new Node(j,i);
				
				//all edges of the map are walls
				if((i == 0 || i == height-1) || (j == 0 || j == width-1)){
					grid[i][j].hasWall = true;
				}
			}
		}
		
		//grid[8][8].hasGoal = true;
	}
	
	/**
	 * Returns the number of goals on the current map
	 * @return the number of goals on the current map
	 */
	public int numGoals(){
		return numGoals;
	}
	
	/**
	 * Allows the setting of the boolean values that represent map objects.
	 * @param y the horizontal position on the map
	 * @param x the vertical position on the map
	 * @param what which value to change
	 * @param b what to change <code>what</code> to
	 */
	public void setNode(int y, int x, int what, boolean b){
		switch(what){
			case GOAL:
				grid[x][y].hasGoal = b;
				break;
			case WALL:
				grid[x][y].hasWall = b;
				break;
			case WUMPUS:
				grid[x][y].hasWumpus = b;
				break;
			case PIT:
				grid[x][y].hasPit = b;
				break;
			case MINION:
				grid[x][y].hasMinion = b;
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
	
	public Node getNode(int y, int x){
		return grid[x][y];
	}
	
	/**
	 * Prints the current Grid based on what each Node contains
	 */
	public void printGrid(){
		for(int i = 0; i < height; i++){
			for(int j = 0; j < width; j++){
				if(grid[i][j].hasGoal)
					System.out.print("G ");
				else if(grid[i][j].hasWall)
					System.out.print("X ");
				else if(grid[i][j].hasWumpus)
					System.out.print("W ");
				else if(grid[i][j].hasPit)
					System.out.print("P ");
				else if(grid[i][j].hasMinion)
					System.out.print("M ");
				else if(grid[i][j].hasAgent)
					System.out.print("A ");
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
				else if(grid[i][j].hasWall)
					sb.append("X ");
				else if(grid[i][j].hasWumpus)
					sb.append("W ");
				else if(grid[i][j].hasPit)
					sb.append("P ");
				else if(grid[i][j].hasMinion)
					sb.append("M ");
				else if(grid[i][j].hasAgent)
					sb.append("A ");
				else if(grid[i][j].hasFairy)
					sb.append("F ");
				else sb.append("- ");
			}
			sb.append('\n');
		}
		
		return sb.toString();
	}

	/** getters and setters **/
	public boolean isSolved(){return solved;}
	public void setSolved(boolean solved){this.solved = solved;}
}