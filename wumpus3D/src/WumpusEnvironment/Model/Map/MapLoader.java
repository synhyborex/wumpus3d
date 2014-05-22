package WumpusEnvironment.Model.Map;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Scanner;

import javax.swing.*;

public class MapLoader {

	public static final String PACKAGE = "Maps.";
    public static JFrame THIS;
    protected static boolean fairy = false;
    
    public static boolean isSearchMap(){
    	return fairy;
    }
	
	public static Grid loadMapFromFile(File file){
		//grid initialization variables
		//if map has only one square, there was a problem with the map file
		int gridWidth = 1, gridHeight = 1, gridNumGoals = 0;
		fairy = false; //make sure fairy is not true by default
		Grid grid = Grid.getInstance();
		grid.gridInit(gridWidth,gridHeight,gridNumGoals,0);
		
		//Agent initialization variables
		//if Agent spawns at (0,0), there was a problem with the map file
		int agentStartX = 0, agentStartY = 0;
		//Agent a = new TestAgent();
		try {
			Scanner sc = new Scanner(file);
			/*
			 * THIS HAS NO ERROR CHECKING CODE. ASSUMES ALL MAP FILES FOLLOW
			 * THIS FORMAT!!! PROBABLY A BAD IDEA BUT SHOULD CHECK WITH KURFESS.
			 */
			gridWidth = sc.nextInt(); //first number is width
			gridHeight = sc.nextInt(); //second number is height
			gridNumGoals = sc.nextInt(); //third number is number of goals on the map
			
			//see if there are any flags
			Scanner flags = new Scanner(sc.nextLine().trim());
			int learningCount = 0; //how many times to repeat the map
			while(flags.hasNext()){ //there are flags
				String flag = flags.next();
				if(flag.equals("fairy"))
					fairy = true;
				if(flag.equals("learning"))
					learningCount = flags.nextInt();
			}
			flags.close();
			
			//create the grid so we can modify Nodes
			//grid = Grid.getInstance();
			grid.gridInit(gridWidth+2,gridHeight+2,gridNumGoals,learningCount);
			for(int i = 0; i < gridHeight; i++){
				String nextRow = sc.nextLine();
				for(int j = 0; j < gridWidth; j++){
					switch(nextRow.charAt(j)){
						case 'S':
							agentStartX = j+1;
							agentStartY = i+1;
							break;
						/*case 'F':
							agentStartX = j+1;
							agentStartY = i+1;
							fairy = true;
							break;*/
						case 'X':
							grid.setNodeType(j+1,i+1,Grid.WALL,true);
							break;
						case 'G':
							grid.setNodeType(j+1,i+1,Grid.GOAL,true);
							grid.setGoalLocation(new Node(j+1,i+1));
							break;
						case 'W':
							grid.setNodeType(j+1,i+1,Grid.WUMPUS,true);
							break;
						case 'M':
							grid.setNodeType(j+1,i+1,Grid.MINION,true);
							break;
						case 'P':
							grid.setNodeType(j+1,i+1,Grid.PIT,true);
							break;
					}
				}
			}
			sc.close();
			//put Agent on Grid
			Node pass = new Node(agentStartX,agentStartY);
			grid.addToEvaluated(pass);
			grid.setNodeType(pass.getX(),pass.getY(),Grid.AGENT,true);
			grid.setAgentLocation(pass);
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(THIS,"Map file was not found!");
		}
		return grid;
	}

}
