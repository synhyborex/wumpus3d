package WumpusEnvironment.Agent;

import java.util.*;

import WumpusEnvironment.Map.Node;

public class Fringe {

	protected LinkedList<Node> fringe;
	public Fringe() {
		fringe = new LinkedList<Node>();
	}
	
	public void addToFringe(Node n){
		fringe.addLast(n);
	}
	
	public Node getNextFringeNode(){
		return fringe.poll();
	}
	
	public Node peekNextFringeNode(){
		return fringe.peek();
	}
	
	public boolean fringeContains(Node n){
		return fringe.contains(n);
	}
	
	public LinkedList<Node> getFringe(){
		return fringe;
	}

}
