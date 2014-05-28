package WumpusEnvironment.Model.Agent;

import javax.swing.JButton;

import WumpusEnvironment.Model.Map.*;
import WumpusEnvironment.View.MainWindow.*;

public class AgentHandler extends Thread {
	private boolean autoStep;
	private Agent agent;
	private Grid grid;
	
	public AgentHandler(Agent agent, Grid grid){
		autoStep = false;
		this.agent = agent;
		this.grid = grid;
	}
	
	public void run(){
		for(;;)
			if(autoStep && !agent.isDead() && !grid.isSolved()){
				agentStep();
			}
	}
	
	public void setAutoStep(boolean value){
		autoStep = value;
	}
	
	public void agentStep(){
		if(!agent.isDead() && !grid.isSolved()){
			agent.agentStep();
			if(agent.hasFairy()){
				if(agent.fairyFoundAllGoals()){
					Logger.generateLogEntry(agent,grid);
				}
			}
			else Logger.generateLogEntry(agent,grid);
		}
		if(agent.isDead()){
			Logger.writeToLog("* You died! Better luck next time. *\r\n");
			Logger.writeToLog("*** GAME OVER ***\r\n");
		}
		if(grid.isSolved()){
			Logger.writeToLog("* You found all the gold! *\r\n");
			Logger.writeToLog("*** GAME OVER ***\r\n");
		}
		try{
			sleep(ApplicationWindow.CURRENT_DELAY);
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}
	}
}
