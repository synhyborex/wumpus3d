package WumpusEnvironment.Map;

/**
 * Represents an individual node in the Grid
 * @author Bhargav
 *
 */
public class Node implements Comparable<Node>{
	protected int x;
	protected int y;
	protected boolean hasGoal;
	protected boolean isWall;
	protected boolean hasWumpus;
	protected boolean hasPit;
	protected boolean hasMinion;
	protected boolean hasAgent;
	protected boolean hasFairy;
	protected boolean evaluated;
	
	/**
	 * Creates a new empty Node
	 * @param x the x-location of the Node in the Grid
	 * @param y the y-location of the Node in the Grid
	 */
	public Node(int x, int y){
		this.x = x;
		this.y = y;
		hasGoal = false;
		isWall = false;
		hasPit = false;
		hasMinion = false;
		hasWumpus = false;
		hasAgent = false;
		hasFairy = false;
		evaluated = false;
	}
	
	public int compareTo(Node n) {
		return 0;
	}
	
	public boolean equals(Node n){
		return (this.x == n.x) && (this.y == n.y);
	}
	
	/* getter methods */
	public int getX(){return x;}
	public int getY(){return y;}
	public void setX(int x){this.x = x;}
	public void setY(int y){this.y = y;}
	public void setAsEvaluated(){this.evaluated = true;}
	public boolean hasGoal(){return hasGoal;}
	public boolean isWall(){return isWall;}
	public boolean hasWumpus(){return hasWumpus;}
	public boolean hasPit(){return hasPit;}
	public boolean hasMinion(){return hasMinion;}
	public boolean hasAgent(){return hasAgent;}
	public boolean hasFairy(){return hasFairy;}
	public boolean isEvaluated(){return evaluated;}
	
}