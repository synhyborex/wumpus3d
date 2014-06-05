package Agents;

import java.util.ArrayList;
import java.util.PriorityQueue;

import WumpusEnvironment.Model.Agent.Agent;
import WumpusEnvironment.Model.Map.Grid;
import WumpusEnvironment.Model.Map.Node;

public class TestA2Agent extends Agent
{
  ArrayList<Node> movesSoFar;
  PriorityQueue<GreedyNode> pq;
  GreedyNode[][] moves;
  boolean hitWall, hitWallSequence; //wall bools
  boolean pitSequence1, pitSequence2, pitSeqDone; //pit bools
  boolean getBackToPath, goingToPath; //path correction bools
  int hitCornerCount; //wall path correction
  int pathCorrection, backToPath, dirToPath;

  public TestA2Agent()
  {
    super();
    hitWall = false;
    hitWallSequence = false;
    pitSequence1 = false;
    pitSequence2 = false;
    pitSeqDone = false;
    getBackToPath = false;
    goingToPath = false;
    hitCornerCount = 0;
    pathCorrection = 0;
    backToPath = 0;
    dirToPath = -1;
    movesSoFar = new ArrayList<Node>();
    pq = new PriorityQueue<GreedyNode>();
    moves = new GreedyNode[100][100];
    for(int i = 0; i < 100; i++) //initialize moves array
      for(int j = 0; j < 100; j++)
        moves[i][j] = new GreedyNode();
  }

  /* returns the GreedyNode in the specified direction from the 
  specified GreedyNode--this GreedyNode comes from moves[][] */
  public GreedyNode getNode(int h, GreedyNode g){
    if(h == NORTH)
      return moves[g.node.getY()-1][g.node.getX()];
    if(h == SOUTH)
      return moves[g.node.getY()+1][g.node.getX()];
    if(h == EAST)
      return moves[g.node.getY()][g.node.getX()+1];
    if(h == WEST)
      return moves[g.node.getY()][g.node.getX()-1];
    return null;
  }

  protected int getLeft(){
    int a = HEADING;
    if(a == NORTH){
      return WEST;
    }
    if(a == EAST){
      return NORTH;
    }
    if(a == SOUTH){
      return EAST;
    }
    if(a == WEST){
      return SOUTH;
    }
    return -1;
  }

  protected int getRight(){
    int a = HEADING;
    if(a == NORTH){
      return EAST;
    }
    if(a == EAST){
      return SOUTH;
    }
    if(a == SOUTH){
      return WEST;
    }
    if(a == WEST){
      return NORTH;
    }
    return -1;
  }

  protected int getTurnDirection(Node curr, Node moveTo){
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

  protected boolean oppositeDirection(int dir1, int dir2){
    if((dir1 == NORTH && dir2 == SOUTH) || (dir2 == NORTH && dir1 == SOUTH))
      return true;
    if((dir1 == WEST && dir2 == EAST) || (dir2 == WEST && dir1 == EAST))
      return true;

    return false;
  }

  public void step()
  {
    if(pq.peek() != null){
      GreedyNode curr = pq.poll(); //get Node we're occupying
      movesSoFar.add(0,curr.node); //we have moved to the current spot
      setBelief(SAFE_HERE,YES); //it's safe where we are

      if(((!hitWall && !hitWallSequence) || hitCornerCount >= 3) 
        && (!pitSequence1 && !pitSequence2) && (!getBackToPath || backToPath == 0)){ //no walls or pits nearby
        hitCornerCount = 0; //if we are here, it means move away from corner
        int h = getDirectionOfGold(); //direction of gold
        int hitArrow = -1; //Node has been visited before or no enemy nearby

        if(nearMinion() || nearWumpus()){ //near shootable enemy?
          //see if the node has been visited
          GreedyNode g = getNode(h,curr);
          if(g == null) log("Bad direction!!!");
          else if(g.node == null){ //not visited yet
            hitArrow = fireArrow(h); //make sure no enemy is in front of you
            setBelief(h,WUMPUS_HERE,NO); //no wumpus or minion there for sure
            setBelief(h,MINION_HERE,NO);
          }
        }

        //move towards goal
        //THIS IS THE "SAFEST" PATH
        if(!nearPit()){ //not near a pit
          turnTo(h); //turn to direction
          if(moveForward() != HIT_WALL){ //actually moved forward
            if(!goingToPath){ //if not already going back to path
              if(pathCorrection == 1){ //have we moved past the obstacle we avoided?
                getBackToPath = true;
                pathCorrection = 0;
              }
              else if(pathCorrection == 0 && pitSeqDone){ //just finished pit avoidal
                pathCorrection++;
                pitSeqDone = false;
              }
            }
            moves[getCurrentLocation().getY()][getCurrentLocation().getX()] = new GreedyNode(curr.node); //update parent
          }
          else{ //hit a wall
            hitWall = true;
            setBelief(h,WALL_HERE,YES);
          }
        }
        else{ //near a pit
          //after this loop, all adjacent nodes except the one you've visited
          //will have the belief PIT_HERE, MAYBE
          for(int i = 0; i < 4; i++){ //each direction
            if(getBelief(i,SAFE_HERE) != YES){ //not visited before
              setBelief(i,PIT_HERE,MAYBE); //if it's not been visited, maybe pit there
            }
          }

          //move back to where you came from
          turnTo(getTurnDirection(curr.node, movesSoFar.get(1)));
          moveForward();
          pitSequence1 = true;
        }
      }
      else if(hitWall && hitCornerCount < 3){ //hit wall last time
        hitWallSequence = true;
        if(getBelief(getLeft(),SAFE_HERE) == YES){ //already visited it
          turnLeft(); //just move because we know it's safe
          if(moveForward() != HIT_WALL){ //moved forward
            hitWall = false; //didn't hit a wall
          }
          else setBelief(HEADING,WALL_HERE,YES); //hitWall is still true
        }
        else{ //not visited before
          if(nearWumpus() || nearMinion()){
            fireArrow(getLeft()); //make sure no enemy is in front of you
            setBelief(getLeft(),WUMPUS_HERE,NO); //no wumpus or minion there for sure
            setBelief(getLeft(),MINION_HERE,NO);
          }

          if(!nearPit()){ //not near a pit
            turnLeft();
            if(moveForward() != HIT_WALL){ //moved forward
              hitWall = false; //didn't hit a wall
              moves[getCurrentLocation().getY()][getCurrentLocation().getX()] = new GreedyNode(curr.node); //update parent
            }
            else setBelief(HEADING,WALL_HERE,YES); //hitWall is still true
          }
          else{ //near a pit
            //after this loop, all adjacent nodes except the one you've visited
            //will have the belief PIT_HERE, MAYBE
            for(int i = 0; i < 4; i++){ //each direction
              if(getBelief(i,SAFE_HERE) != YES){ //not visited before
                setBelief(i,PIT_HERE,MAYBE); //if it's not been visited, maybe pit there
              }
            }

            //move back to where you came from
            turnTo(getTurnDirection(curr.node, movesSoFar.get(1)));
            moveForward();
            pitSequence1 = true;
          }
        }

        if(hitWall) hitCornerCount++; //hit another wall, we are in corner
      }
      else if(hitWallSequence && !hitWall){ //not done getting around wall
        if(getBelief(getLeft(),SAFE_HERE) == YES){ //already visited it
          turnRight(); //just move because we know it's safe
          if(moveForward() != HIT_WALL){ //moved forward
            hitWallSequence = false; //hitWall still true, only set false if not wall
          }
          else{
            setBelief(HEADING,WALL_HERE,YES); //hitWall is still true
            hitWall = true;
          }
        }
        else{ //not visited before
          if(nearWumpus() || nearMinion()){
            fireArrow(getRight()); //make sure no enemy is in front of you
            setBelief(getRight(),WUMPUS_HERE,NO); //no wumpus or minion there for sure
            setBelief(getRight(),MINION_HERE,NO);
          }

          if(!nearPit()){ //not near a pit
            turnRight();
            if(moveForward() != HIT_WALL){
              hitWallSequence = false; //hitWall still true, only set false if not wall
              moves[getCurrentLocation().getY()][getCurrentLocation().getX()] = new GreedyNode(curr.node); //update parent
              hitCornerCount = 0;
            }
            else{
              setBelief(HEADING,WALL_HERE,YES); //hitWall is still true
              hitWall = true;
            }
          }
          else{ //near a pit
            //after this loop, all adjacent nodes except the one you've visited
            //will have the belief PIT_HERE, MAYBE
            for(int i = 0; i < 4; i++){ //each direction
              if(getBelief(i,SAFE_HERE) != YES){ //not visited before
                setBelief(i,PIT_HERE,MAYBE); //if it's not been visited, maybe pit there
              }
            }

            //move back to where you came from
            turnTo(getTurnDirection(curr.node, movesSoFar.get(1)));
            moveForward();
            pitSequence1 = true;
          }
        }
      }
      else if(pitSequence1){
        if(oppositeDirection(getRight(),getDirectionOfGold())){
          pitSequence1 = false;
        }
        else{
          if(getBelief(getRight(),SAFE_HERE) == YES){ //already visited it
            turnRight(); //just move because we know it's safe
            if(moveForward() != HIT_WALL){ //moved forward
              hitWall = false; //didn't hit a wall
            }
            else{
              setBelief(HEADING,WALL_HERE,YES);
              hitWall = true;
            }
          }
          else{ //not visited before
            if(nearWumpus() || nearMinion()){
              fireArrow(getRight()); //make sure no enemy is in front of you
              setBelief(getRight(),WUMPUS_HERE,NO); //no wumpus or minion there for sure
              setBelief(getRight(),MINION_HERE,NO);
            }

            if(!nearPit()){
              pitSequence1 = false;
              pitSequence2 = true;
              turnRight();
              if(moveForward() != HIT_WALL){ //moved forward
                if(!goingToPath)
                  backToPath++;
                hitWall = false; //didn't hit a wall
                moves[getCurrentLocation().getY()][getCurrentLocation().getX()] = new GreedyNode(curr.node); //update parent
                //hitCornerCount = 0;
              }
              else setBelief(HEADING,WALL_HERE,YES);
            }
            else{
              //after this loop, all adjacent nodes except the one you've visited
              //will have the belief PIT_HERE, MAYBE
              for(int i = 0; i < 4; i++){ //each direction
                if(getBelief(i,SAFE_HERE) != YES){ //not visited before
                  setBelief(i,PIT_HERE,MAYBE); //if it's not been visited, maybe pit there
                }
              }

              //move back to where you came from
              turnTo(getTurnDirection(curr.node, movesSoFar.get(1)));
              moveForward();
            }
            //else keep pitSequence1 true and do this again next step()
          }
        }
      }
      else if(pitSequence2){
        if(nearWumpus() || nearMinion()){
          fireArrow(getRight()); //make sure no enemy is in front of you
          setBelief(getRight(),WUMPUS_HERE,NO); //no wumpus or minion there for sure
          setBelief(getRight(),MINION_HERE,NO);
        }

        if(!nearPit()){ //not near a pit
          setBelief(getRight(),PIT_HERE,NO);
          turnRight();
          if(moveForward() != HIT_WALL){ //moved forward
            hitWall = false; //didn't hit a wall
            moves[getCurrentLocation().getY()][getCurrentLocation().getX()] = new GreedyNode(curr.node); //update parent
          }
          else{
            setBelief(HEADING,WALL_HERE,YES);
            hitWall = true;
          }
          pitSequence2 = false;
          pitSeqDone = true;
        }
        else{ //near a pit
          pathCorrection = 0;
          //after this loop, all adjacent nodes except the one you've visited
          //will have the belief PIT_HERE, MAYBE
          for(int i = 0; i < 4; i++){ //each direction
            if(getBelief(i,SAFE_HERE) != YES){ //not visited before
              setBelief(i,PIT_HERE,MAYBE); //if it's not been visited, maybe pit there
            }
          }
          setBelief(getRight(),PIT_HERE,YES);
          //move back to where you came from
          turnTo(getTurnDirection(curr.node, movesSoFar.get(1)));
          moveForward();
          pitSequence2 = false;
          pitSequence1 = true;
        }
      }
      else if(getBackToPath && backToPath > 0){ //trying to get back to main path
        if(!goingToPath){
          dirToPath = getRight();
          goingToPath = true;
        }
        if(oppositeDirection(dirToPath,getDirectionOfGold())){
          goingToPath = false;
          backToPath = 0;
        }
        else{
          if(nearWumpus() || nearMinion()){
            fireArrow(dirToPath); //make sure no enemy is in front of you
            setBelief(dirToPath,WUMPUS_HERE,NO); //no wumpus or minion there for sure
            setBelief(dirToPath,MINION_HERE,NO);
          }

          if(!nearPit()){ //not near a pit
            turnTo(dirToPath); //turn right
            if(moveForward() != HIT_WALL){ //moved forward
              backToPath--; //one less step to the path
              if(backToPath == 0){ //done getting back onto the main path
                getBackToPath = false;
                goingToPath = false;
              }
              hitWall = false; //didn't hit a wall
              moves[getCurrentLocation().getY()][getCurrentLocation().getX()] = new GreedyNode(curr.node); //update parent
            }
            else{
              setBelief(HEADING,WALL_HERE,YES);
              hitWall = true;
            }
          }
          else{ //near a pit
            //after this loop, all adjacent nodes except the one you've visited
            //will have the belief PIT_HERE, MAYBE
            for(int i = 0; i < 4; i++){ //each direction
              if(getBelief(i,SAFE_HERE) != YES){ //not visited before
                setBelief(i,PIT_HERE,MAYBE); //if it's not been visited, maybe pit there
              }
            }

            turnTo(getTurnDirection(curr.node, movesSoFar.get(1)));
            moveForward();

            pitSequence1 = true;
          }
        }
      }
      pq.offer(new GreedyNode(getCurrentLocation()));
    }
    else{ //pq is empty, we have just started the search
      Node t = getCurrentLocation();
      moves[t.getY()][t.getX()] = null; //first node has no parent
      pq.offer(new GreedyNode(t,getDistanceToGold(t)));
    }
  }

  public void reset()
  {
    super.reset(); 
    hitWall = false;
    hitWallSequence = false;
    pitSequence1 = false;
    pitSequence2 = false;
    pitSeqDone = false;
    getBackToPath = false;
    goingToPath = false;
    hitCornerCount = 0;
    pathCorrection = 0;
    backToPath = 0;
    dirToPath = -1;
    movesSoFar = new ArrayList<Node>();
    pq = new PriorityQueue<GreedyNode>();
    moves = new GreedyNode[100][100];
    for(int i = 0; i < 100; i++) //initialize moves array
      for(int j = 0; j < 100; j++)
        moves[i][j] = new GreedyNode();
  }
}