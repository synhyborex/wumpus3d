package WumpusEnvironment.Model.Map;

/**
 * Represents an individual node in the Grid
 * @author Bhargav
 *
 */
public class Node implements Comparable<Node>{
	//location variables
	protected int x;
	protected int y;
	
	//status variables
	protected boolean hasGoal;
	protected boolean isWall;
	protected boolean hasWumpus;
	protected boolean hasPit;
	protected boolean hasMinion;
	protected boolean hasAgent;
	protected boolean hasFairy;
	protected boolean evaluated;
	
	//belief variables
	protected int[] beliefs;
	
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
		beliefs = new int[6];
		for(int i = 0; i < beliefs.length; i++)
			beliefs[i] = 6; //Agent.UNKNOWN
	}
	
	public Node(Node n){
		this.x = n.x;
		this.y = n.y;
		this.hasGoal = n.hasGoal;
		this.isWall = n.isWall;
		this.hasPit = n.hasPit;
		this.hasWumpus = n.hasWumpus;
		this.hasAgent = n.hasAgent;
		this.hasFairy = n.hasFairy;
		this.evaluated = false;
		this.beliefs = n.beliefs;
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
	public int getBelief(int index){return beliefs[index];}
	public void setBelief(int index, int value){beliefs[index] = value;}
	public void setAsEvaluated(){this.evaluated = true;}
	public boolean hasGoal(){return hasGoal;}
	public boolean isWall(){return isWall;}
	public boolean hasWumpus(){return hasWumpus;}
	public boolean hasPit(){return hasPit;}
	public boolean hasMinion(){return hasMinion;}
	public boolean hasAgent(){return hasAgent;}
	public boolean hasFairy(){return hasFairy;}
	public boolean isEvaluated(){return evaluated;}
	public String toString(){return "(" + x + "," + y + ")";}
	public int getCost(){return 0;} //COULD POTENTIALLY CHANGE!!!!
	
}