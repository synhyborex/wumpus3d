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
	
	protected int width; //the horizontal number of Nodes
	protected int height; //the vertical number of Nodes
	protected int numGoals; //the number of goal Nodes on the map
	protected static Node[][] grid; //the map we're operating on
	
	public Grid(int width, int height){
		this.width = width;
		this.height = height;
		
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
	}
	
	/**
	 * Returns the current instance of this class.
	 * @return <code>this</code>
	 */
	public Grid currentInstance(){
		return this;
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
		default:
			System.out.println("Improper map object to change.");
			break;
		}
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
				else System.out.print("- ");
			}
			System.out.println();
		}
	}
}