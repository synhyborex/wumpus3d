package WumpusEnvironment.Model.Agent;

import java.util.*;

import WumpusEnvironment.Model.Map.Node;

public class Fringe {

	protected ArrayDeque<Node> fringe;
	public Fringe() {
		fringe = new ArrayDeque<Node>();
	}
	
	public void addToFringeTail(Node n){
		fringe.addLast(n);
	}
	
	public void addToFringeHead(Node n){
		fringe.addFirst(n);
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
	
	public ArrayDeque<Node> getFringe(){
		return fringe;
	}

}
