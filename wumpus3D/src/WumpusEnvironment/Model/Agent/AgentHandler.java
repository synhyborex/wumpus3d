package WumpusEnvironment.Model.Agent;

import WumpusEnvironment.Model.Map.*;
import WumpusEnvironment.View.MainWindow.*;

public class AgentHandler extends Thread {
	private boolean autoStep;
	private boolean stop;
	private Agent agent;
	private Grid grid;
	
	public AgentHandler(Agent agent, Grid grid){
		autoStep = false;
		stop = false;
		this.agent = agent;
		this.grid = grid;
	}
	
	public void run(){
		while(!agent.isDead() && !grid.isSolved()){
			if(stop) return;
			if(autoStep){
				agentStep();
			}
		}
	}
	
	public void setAutoStep(boolean value){
		autoStep = value;
	}
	
	public void stopThread(){stop = true;}
	
	public boolean isRunning(){
		return autoStep && !agent.isDead() && !grid.isSolved();
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
			ApplicationWindow.disableMovementButtons();
			autoStep = false;
			Logger.writeToLog("* You died! Better luck next time. *\r\n");
			Logger.writeToLog("*** GAME OVER ***\r\n");
		}
		if(grid.isSolved()){
			ApplicationWindow.disableMovementButtons();
			autoStep = false;
			Logger.writeToLog("* You found all the gold! *\r\n");
			Logger.writeToLog("*** GAME OVER ***\r\n");
		}
		try{
			sleep(ApplicationWindow.CURRENT_DELAY);
		}
		catch(InterruptedException e){
			currentThread().interrupt();
			return;
		}
	}
}
