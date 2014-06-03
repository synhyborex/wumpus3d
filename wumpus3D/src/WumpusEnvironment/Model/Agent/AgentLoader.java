package WumpusEnvironment.Model.Agent;

import java.io.*;
import java.util.*;

import javax.swing.*;

public class AgentLoader {

	public static final String PACKAGE = "Agents.";
    public static JFrame THIS;
	
	public static Agent loadAgentFromFile(File file){
		Agent agent = null;
	    String str1 = file.getName();
	    String str2 = file.getParentFile().getName();
	    
	    int i = str1.lastIndexOf('.');
	    String str3 = str1.substring(0, i);
	    
	    Object localObject = null;
	    try
	    {
	      @SuppressWarnings("rawtypes")
		  Class localClass = null;
	      if (str2.equals("Agents")) {
	        localClass = Class.forName("Agents." + str3);
	      } else {
	        localClass = Class.forName("Agents." + str2 + "." + str3);
	      }
	      localObject = localClass.newInstance();
	      
	      agent = (Agent)localObject;
	    }
	    catch (InstantiationException localInstantiationException)
	    {
	      JOptionPane.showMessageDialog(THIS, "The chosen class must not be an interface or be abstract, and it must not have any arguments in its constructor.");
	    }
	    catch (IllegalAccessException localIllegalAccessException)
	    {
	      JOptionPane.showMessageDialog(THIS, "The chosen class's constructor must be public.");
	    }
	    catch (ClassNotFoundException localClassNotFoundException)
	    {
	      JOptionPane.showMessageDialog(THIS, "The chosen class cannot be found.  Make sure the .java file is compiled.");
	    }
	    catch (ClassCastException localClassCastException)
	    {
	      JOptionPane.showMessageDialog(THIS, "The chosen class does not extend from the Agent class.");
	    }
	    return agent;
	}
	
	public static void loadCostsFromFile(File file){
		int start_points = 0, move_cost = 0, turn_cost = 0, curr_node = 0, adj_node = 0, 
				gold_hint = 0, hit_minion = 0, gold_reward = 0, arrow_cost = 0;
		try {
			Scanner sc = new Scanner(file);
			while(sc.hasNextLine()){
				Scanner cost = new Scanner(sc.nextLine().trim()); //get the next cost
				if(cost.hasNext()){ //make sure not empty line
					switch(cost.next()){
						case "start_points":
							start_points = cost.nextInt();
							break;
						case "move_cost":
							move_cost = cost.nextInt();
							break;
						case "turn_cost":
							turn_cost = cost.nextInt();
							break;
						case "arrow_cost":
							arrow_cost = cost.nextInt();
							break;
						case "curr_node":
							curr_node = cost.nextInt();
							break;
						case "adj_node":
							adj_node = cost.nextInt();
							break;
						case "gold_hint":
							gold_hint = cost.nextInt();
							break;
						case "hit_minion":
							hit_minion = cost.nextInt();
							break;
						case "gold_reward":
							gold_reward = cost.nextInt();
							break;
					}
				}
				cost.close();
			}
			sc.close();
		}
		catch(FileNotFoundException e){
			System.out.println("properties file not found");
			e.printStackTrace();
		}
		
		//pass cost values to the Agent
		Agent.initCosts(new int[]{start_points,move_cost,turn_cost,arrow_cost,curr_node,adj_node,gold_hint,hit_minion,gold_reward});
	}

}
