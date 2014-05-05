package WumpusEnvironment.Model.Agent;

import javax.swing.JButton;

import WumpusEnvironment.Model.Map.*;
import WumpusEnvironment.View.MainWindow.*;

public class AgentHandler extends Thread {
	public static void agentStep(Agent agent, Grid grid, boolean fairy){
		if(!agent.isDead() && !grid.isSolved()){
			agent.step();
			if(fairy){
				if(agent.fairyFoundAllGoals()){
					Logger.generateLogEntry(agent,grid);
				}
			}
			else Logger.generateLogEntry(agent,grid);
		}
		try{
			sleep(ApplicationWindow.CURRENT_DELAY);
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}
	}
}
