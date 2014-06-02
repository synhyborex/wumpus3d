package WumpusEnvironment.Model.Agent;

import WumpusEnvironment.Model.Map.*;
import WumpusEnvironment.View.MainWindow.*;

public class AgentHandler extends Thread {
	private boolean autoStep;
	private boolean stop;
	private Agent agent;
	private Grid grid;
	
	public AgentHandler(Agent agent){
		autoStep = false;
		stop = false;
		this.agent = agent;
		this.grid = Grid.getInstance();
	}
	
	public void run(){
		for(;;){
			if(stop) return;
			if(isRunning()){
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
			ApplicationWindow.changeResetButton(true);
			autoStep = false;
			Logger.writeToLog("* You died! Better luck next time. *\r\n");
			Logger.writeToLog("*** GAME OVER ***\r\n");
		}
		if(grid.isSolved()){
			ApplicationWindow.disableMovementButtons();
			ApplicationWindow.changeResetButton(true);
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
