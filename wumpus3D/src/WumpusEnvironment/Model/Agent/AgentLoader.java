package WumpusEnvironment.Model.Agent;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
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

}
