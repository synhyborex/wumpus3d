package WumpusEnvironment.Model.Agent.TestAgents; 

import WumpusEnvironment.Model.Agent.Agent;
import WumpusEnvironment.Model.Map.*;

import java.util.*;

public class TestUniformAgent extends Agent 
{
	//test comment
  int goalX, goalY;
  double[][] cost;
  PriorityQueue<GreedyNode> pq;
  GreedyNode[][] moves;
  ArrayList<Node> path;
  private static final int MOVE_COST = 2;
  private static final int SEARCH_COST = 1;
  private static final int TURN_COST = 1;

  public TestUniformAgent(){
    super();
    goalX = 10;
    goalY = 10;
    pq = new PriorityQueue<GreedyNode>();
    path = new ArrayList<Node>();

    moves = new GreedyNode[100][100];
    for(int i = 0; i < 100; i++) //initialize moves array
      for(int j = 0; j < 100; j++)
        moves[i][j] = new GreedyNode();

    cost = new double[100][100];
    for(int i = 0; i < 100; i++) //initialize costs to max
      for(int j = 0; j < 100; j++)
        cost[i][j] = Integer.MAX_VALUE;
  }

  private int getTurnDirection(Node curr, Node moveTo){
    if(moveTo.getX() == curr.getX()+1 && moveTo.getY() == curr.getY())
      return EAST;
    else if(moveTo.getX() == curr.getX()-1 && moveTo.getY() == curr.getY())
      return WEST;
    else if(moveTo.getY() == curr.getY()-1 && moveTo.getX() == curr.getX())
      return NORTH;
    else if(moveTo.getY() == curr.getY()+1 && moveTo.getX() == curr.getX())
      return SOUTH;
    else if(moveTo.getY() == curr.getY() && moveTo.getX() == curr.getX())
      return -1;
    else return -2; //square not directly linked. need to backtrack.
  }

  private int getTurnCost(int heading, Node curr, Node moveTo){
    int dir = -1;
    if(moveTo.getX() == curr.getX()+1 && moveTo.getY() == curr.getY())
      dir = EAST;
    else if(moveTo.getX() == curr.getX()-1 && moveTo.getY() == curr.getY())
      dir = WEST;
    else if(moveTo.getY() == curr.getY()-1 && moveTo.getX() == curr.getX())
      dir = NORTH;
    else if(moveTo.getY() == curr.getY()+1 && moveTo.getX() == curr.getX())
      dir = SOUTH;

    if(dir-heading == 0) //same direction
      return 0;
    else if(Math.abs(dir-heading) == 2)
      return TURN_COST*2;
    else return TURN_COST;
  }

  public void nextSearchStep(){
    /*log("Searching for goal square...");
    pq = new PriorityQueue<GreedyNode>();

    Node t = getSearchLocation();
    cost[t.getY()][t.getX()] = 0; //initial path cost 0
    pq.offer(new GreedyNode(0,t));*/
    if(pq.peek() != null){
      //log(""+cost[4][4]);
      GreedyNode curr = pq.poll();
      moveFairyLocation(curr.node); //move to next fringe location

      /* display search position */
      /////////////////////////////
      log("Search position: " + curr.node.getX() + " " + curr.node.getY());
      log("Cost so far: " + curr.pathCost);
      /////////////////////////////
      /*/////////////////////////*/

      //if it's the goal node, return
      if(fairyFoundGoal()){
        log("\nGoal Found!");
        //log("Number of Search Steps: " + getSearchCost());

        //find all parents and add to path
        GreedyNode m = curr;
        path.add(0,m.node);
        Node t = moves[m.node.getY()][m.node.getX()].node;
        while(moves[m.node.getY()][m.node.getX()] != null){
          m = moves[m.node.getY()][m.node.getX()];
          path.add(0,m.node);
        }
        return;
      }

    /*ADD NEW NODES*/

      //add north location
      Node add = getNorthOfFairyLocation();
      //get total path cost to this Node
      double pathCost = curr.pathCost + add.getCost();
      pathCost += MOVE_COST + getTurnCost(HEADING,curr.node,add);
      GreedyNode g = new GreedyNode(pathCost,add);
      if(!add.isEvaluated() && !add.isWall()){
        if(!pq.contains(g)){ //valid
          pq.offer(g);
          cost[add.getY()][add.getX()] = pathCost; //update path cost to this Node
          moves[add.getY()][add.getX()] = new GreedyNode(curr.node); //update parent
        }
        //Node already exists in pq, but this is a lower cost
        else if(cost[add.getY()][add.getX()] > g.pathCost){
          pq.remove(g); //remove old one
          pq.offer(g); //add new one
          cost[add.getY()][add.getX()] = g.pathCost; //update with new path cost
        }
      }

      //add east location
      add = getEastOfFairyLocation();
      //get total path cost to this Node
      pathCost = curr.pathCost + add.getCost();
      pathCost += MOVE_COST + getTurnCost(HEADING,curr.node,add);
      g = new GreedyNode(pathCost,add);
      if(!add.isEvaluated() && !add.isWall()){
        if(!pq.contains(g)){ //valid
          pq.offer(g);
          cost[add.getY()][add.getX()] = pathCost; //update path cost to this Node
          moves[add.getY()][add.getX()] = new GreedyNode(curr.node); //update parent
        }
        //Node already exists in pq, but this is a lower cost
        else if(cost[add.getY()][add.getX()] > g.pathCost){
          pq.remove(g); //remove old one
          pq.offer(g); //add new one
          cost[add.getY()][add.getX()] = g.pathCost; //update with new path cost
        }
      }

      //add south location
      add = getSouthOfFairyLocation();
      //get total path cost to this Node
      pathCost = curr.pathCost + add.getCost();
      pathCost += MOVE_COST + getTurnCost(HEADING,curr.node,add);
      g = new GreedyNode(pathCost,add);
      if(!add.isEvaluated() && !add.isWall()){
        if(!pq.contains(g)){ //valid
          pq.offer(g);
          cost[add.getY()][add.getX()] = pathCost; //update path cost to this Node
          moves[add.getY()][add.getX()] = new GreedyNode(curr.node); //update parent
        }
        //Node already exists in pq, but this is a lower cost
        else if(cost[add.getY()][add.getX()] > g.pathCost){
          pq.remove(g); //remove old one
          pq.offer(g); //add new one
          cost[add.getY()][add.getX()] = g.pathCost; //update with new path cost
        }
      }

      //add west location
      add = getWestOfFairyLocation();
      //get total path cost to this Node
      pathCost = curr.pathCost + add.getCost();
      pathCost += MOVE_COST + getTurnCost(HEADING,curr.node,add);
      g = new GreedyNode(pathCost,add);
      if(!add.isEvaluated() && !add.isWall()){
        if(!pq.contains(g)){ //valid
          pq.offer(g);
          cost[add.getY()][add.getX()] = pathCost; //update path cost to this Node
          moves[add.getY()][add.getX()] = new GreedyNode(curr.node); //update parent
        }
        //Node already exists in pq, but this is a lower cost
        else if(cost[add.getY()][add.getX()] > g.pathCost){
          pq.remove(g); //remove old one
          pq.offer(g); //add new one
          cost[add.getY()][add.getX()] = g.pathCost; //update with new path cost
        }
      }
    }
    else{ //pq is empty, we just started the search
      log("Uniform Cost Search\n"); //display search type
      Node t = getFairyLocation();
      cost[t.getY()][t.getX()] = 0; //initial path cost 0\
      moves[t.getY()][t.getX()] = null; //first node has no parent
      pq.offer(new GreedyNode(0,t));
    }
  }

  public void nextStep(){
    int dirToTurn = getTurnDirection(getAgentLocation(),(Node)path.get(0));
    if(dirToTurn >= 0){ //valid direction
      turnTo(dirToTurn);
      moveForward();
      path.remove(0); //you don't need to make the move anymore
    }
    else if(dirToTurn == -1) //same square
      path.remove(0);

    if(getAgentLocation().hasGoal()){
      log("\nGoal Reached!");
      //log("Total Search Cost: " + getSearchCost());
      log("Total Movement Cost: " + getMovementCost());
      return;
    }
  }

  public void reset(){
    super.reset();
    cost = new double[100][100];
    pq.clear();
    path.clear();

    //reset costs
    for(int i = 0; i < 100; i++)
      for(int j = 0; j < 100; j++)
        cost[i][j] = Integer.MAX_VALUE;

    //reset parent nodes
    for(int i = 0; i < 100; i++) //initialize moves array
      for(int j = 0; j < 100; j++)
        moves[i][j] = new GreedyNode();
  }
}