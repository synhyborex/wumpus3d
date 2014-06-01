package Agents; 

import WumpusEnvironment.Model.Agent.Agent;
import WumpusEnvironment.Model.Map.*;

public class GreedyNode implements Comparable<GreedyNode>{
  public Node node;
  public int toGoal;
  public double pathCost;
  
  public GreedyNode(){
    node = null;
    toGoal = Integer.MAX_VALUE;
    pathCost = Integer.MAX_VALUE;
  }

  public GreedyNode(Node n){
    node = n;
    toGoal = Integer.MAX_VALUE;
    pathCost = Integer.MAX_VALUE;
  }

  //0 = greedy
  //1 = uniform cost
  public GreedyNode(Node n, int i){
    node = n;
    toGoal = i;
    pathCost = Integer.MAX_VALUE;
  }

  public GreedyNode(double p, Node n){
    node = n;
    pathCost = p;
    toGoal = Integer.MAX_VALUE;
  }

  public GreedyNode(Node n, int toGoal, double pathCost){
    node = n;
    this.toGoal = toGoal;
    this.pathCost = pathCost;
  }

  public int compareTo(GreedyNode n){
    if(n.pathCost == Integer.MAX_VALUE){ //greedy
      if(this.toGoal - n.toGoal > 0)
        return 1;
      if(this.toGoal - n.toGoal < 0)
        return -1;
    }
    else if(n.toGoal == Integer.MAX_VALUE){ //uniform cost
      if(this.pathCost - n.pathCost > 0)
        return 1;
      if(this.pathCost - n.pathCost < 0)
        return -1;
    }
    else{ //a*
      double thisTot = this.pathCost + this.toGoal;
      double nTot = n.pathCost + n.toGoal;
      if(thisTot - nTot > 0)
        return 1;
      if(thisTot - nTot < 0)
        return -1;
    }
    return 0;
  }

  public boolean equals(Object n){
    if(n == null)
      return false;
    if(n.getClass() != this.getClass())
      return false;
    if(((GreedyNode)n).node.getX() == this.node.getX() 
      && ((GreedyNode)n).node.getY() == this.node.getY())
      return true;
    return false;
  }
}
