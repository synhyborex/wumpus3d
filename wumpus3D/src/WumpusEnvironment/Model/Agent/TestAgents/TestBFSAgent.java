package WumpusEnvironment.Model.Agent.TestAgents;

import WumpusEnvironment.Model.Agent.Agent;
import WumpusEnvironment.Model.Map.*;

public class TestBFSAgent extends Agent {
	
	private int totalSearchCost; //total search cost

	public TestBFSAgent(Grid g, Node start) {
		super(g, start);
		totalSearchCost = 0;
	}

	@Override
	public void nextStep() {
		
	}
	
	public void nextSearchStep(){
		log("Searching for goal square...");
	    addToFringe(getFairyLocation()); //add first node to fringe
	    //log(getSearchLocation().getX() + " " + getSearchLocation().getY());
	    while(peekNextFringeNode() != null){ //while there are still nodes to visit
	      Node curr = getNextFringeNode(); //get next node in fringe
	      if(!getFairyLocation().isEvaluated()){
	    	  System.out.println("uh oh " + getFairyLocation().getX() + " " + getFairyLocation().getY());
	      }
	      moveFairyLocation(curr); //move to next fringe location
	      //totalSearchCost += getSearchCost(); //update search cost

	      /* display search position */
	      /////////////////////////////
	      log("Search position: " + curr.getX() + " " + curr.getY());
	      /////////////////////////////
	      /*/////////////////////////*/

	      //if it's the goal node, return
	      if(fairyFoundGoal()){
	        log("Goal Found!");
	        //log("Total Search Cost: " + getSearchCost());
	        return;
	      }

	      //add new nodes
	      Node add = getNorthOfFairyLocation(); //add north location
	      if(!add.isEvaluated() && !fringeContains(add) && !add.isWall())
	        addToFringe(add);
	      add = getEastOfFairyLocation(); //add east location
	      if(!add.isEvaluated() && !fringeContains(add) && !add.isWall())
	        addToFringe(add);
	      add = getSouthOfFairyLocation(); //add south location
	      if(!add.isEvaluated() && !fringeContains(add) && !add.isWall())
	        addToFringe(add);
	      add = getWestOfFairyLocation(); //add west location
	      if(!add.isEvaluated() && !fringeContains(add) && !add.isWall())
	        addToFringe(add);
	      //log(""+getFringe().size());
	    }
	}

}
