package WumpusEnvironment.Model.Agent.TestAgents;

import WumpusEnvironment.Model.Agent.Agent;
import WumpusEnvironment.Model.Map.Grid;
import WumpusEnvironment.Model.Map.Node;

public class TestDFSAgent extends Agent {

	private int totalSearchCost; //total search cost
	
	public TestDFSAgent() {
		super();
		totalSearchCost = 0;
	}

	@Override
	public void nextStep() {

	}
	
	public void nextSearchStep(){
		Node moveTo = null;
	    //MyNode node = null;
	    log("Searching for goal square...");
	    addToFringe(getFairyLocation()); //add first node to fringe
	    //log(getFairyLocation().getX() + " " + getFairyLocation().getY());
	    while(getFringe().peek() != null){
	      //path.setParent(node);
	      /*int x = getFairyLocation().getX();
	      int y = getFairyLocation().getY();
	      int dir = -1;*/
	      moveTo = getNextFringeNode(); //get next node in fringe
	      //path.setCurr(moveTo);
	      /*if(moveTo.getX() == x+1 && moveTo.getY() == y)
	        dir = EAST;
	      else if(moveTo.getX() == x-1 && moveTo.getY() == y)
	        dir = WEST;
	      else if(moveTo.getY() == y-1 && moveTo.getX() == x)
	        dir = NORTH;
	      else if(moveTo.getY() == y+1 && moveTo.getX() == x)
	        dir = SOUTH;
	      else log("jumping");
	      //node = path;
	      switch(dir){
	        case NORTH:
	          //path = path.north;
	          log("NORTH");
	          break;
	        case EAST:
	          //path = path.east;
	          log("EAST");
	          break;
	        case SOUTH:
	          //path = path.south;
	          log("SOUTH");
	          break;
	        case WEST:
	          //path = path.west;
	          log("WEST");
	          break;
	        case -1:
	          log("-1");
	          break;
	      }*/
	      moveFairyLocation(moveTo); //move to next fringe location
	      //totalFairyCost += getFairyCost(); //update search cost

	      /* display search position */
	      /////////////////////////////
	      log("Search position: " + moveTo.getX() + " " + moveTo.getY());
	      /////////////////////////////
	      /*/////////////////////////*/

	      //if it's the goal node, return
	      if(fairyFoundGoal()){
	        log("Goal Found!");
	        //log("Total Fairy Cost: " + getFairyCost());
	        //log("Goal at " + path.getCurr().getX() + " " + path.getCurr().getY());
	        //log("Parent at " + path.getParent().getCurr().getX() + " " + path.getParent().getCurr().getY());
	        return;
	      }

	      //add new nodes
	      Node add = getNorthOfFairyLocation(); //add north location
	      if(!add.isEvaluated() && !fringeContains(add) && !add.isWall()){
	        addToFringeHead(add);
	        /*path.north = new MyNode();
	        path.north.parent = path;
	        path.north.curr = add;*/
	      }
	      add = getEastOfFairyLocation(); //add east location
	      if(!add.isEvaluated() && !fringeContains(add) && !add.isWall()){
	        addToFringeHead(add);
	        /*path.east = new MyNode();
	        path.east.parent = path;
	        path.east.curr = add;*/
	      }
	      add = getSouthOfFairyLocation(); //add south location
	      if(!add.isEvaluated() && !fringeContains(add) && !add.isWall()){
	        addToFringeHead(add);
	        /*path.south = new MyNode();
	        path.south.parent = path;
	        path.south.curr = add;*/
	      }
	      add = getWestOfFairyLocation(); //add west location
	      if(!add.isEvaluated() && !fringeContains(add) && !add.isWall()){
	        addToFringeHead(add);
	        /*path.west = new MyNode();
	        path.west.parent = path;
	        path.west.curr = add;*/
	      }
	    }
	}

}
