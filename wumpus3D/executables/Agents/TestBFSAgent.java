package Agents;

import WumpusEnvironment.Model.Agent.Agent;
import WumpusEnvironment.Model.Map.*;

public class TestBFSAgent extends Agent {
	
	private int totalSearchCost; //total search cost

	public TestBFSAgent() {
		super();
		totalSearchCost = 0;
	}

	@Override
	public void step() {
		
	}
	
	public void searchStep(){
		log("Searching for goal square...");
	    addToFringe(getFairyLocation()); //add first node to fringe
	    //log(getSearchLocation().getX() + " " + getSearchLocation().getY());
	    while(peekNextFringeNode() != null){ //while there are still nodes to visit
	    	log(fringeToString());
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
	      if(!add.isEvaluated() && !fringeContains(add) && !add.hasWall())
	        addToFringe(add);
	      add = getEastOfFairyLocation(); //add east location
	      if(!add.isEvaluated() && !fringeContains(add) && !add.hasWall())
	        addToFringe(add);
	      add = getSouthOfFairyLocation(); //add south location
	      if(!add.isEvaluated() && !fringeContains(add) && !add.hasWall())
	        addToFringe(add);
	      add = getWestOfFairyLocation(); //add west location
	      if(!add.isEvaluated() && !fringeContains(add) && !add.hasWall())
	        addToFringe(add);
	      //log(""+getFringe().size());
	    }
	}

}
