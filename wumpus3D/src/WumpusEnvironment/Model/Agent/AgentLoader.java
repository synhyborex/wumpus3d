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
        String fileName = file.getName();
        String filePath = file.getAbsolutePath();
        String parentDirectory = file.getParentFile().getName();

        String agentClassName = fileName.substring(0, fileName.lastIndexOf('.'));

        Object object = null;
        System.out.println(filePath.contains(parentDirectory));
        System.out.println(filePath.substring(0, filePath.indexOf(parentDirectory)));

        try {
        	File f = new File(filePath.substring(0, filePath.indexOf(parentDirectory)));
        	URL[] cp = {f.toURI().toURL()};
        	URLClassLoader urlcl = new URLClassLoader(cp);

            Class agentDefinition = null;
            if (parentDirectory.equals("Agents"))
            {
            	System.out.println(PACKAGE + agentClassName);
            	agentDefinition = urlcl.loadClass(PACKAGE + agentClassName);
                //agentDefinition = Class.forName(PACKAGE + agentClassName);
            }
            else
            {
            	System.out.println(PACKAGE + parentDirectory + "." + agentClassName);
            	agentDefinition = urlcl.loadClass(parentDirectory + "." + agentClassName);
            }
            object = agentDefinition.newInstance();

            agent = (Agent)object;
        }
        catch (InstantiationException e) {

            JOptionPane.showMessageDialog(THIS,
                "The chosen class must not be an interface or be abstract, " +
                "and it must not have any arguments in its constructor.");
        }
        catch (IllegalAccessException e) {

            JOptionPane.showMessageDialog(THIS,
                "The chosen class's constructor must be public.");
        }
        catch (ClassNotFoundException e) {

            JOptionPane.showMessageDialog(THIS,
                "The chosen class cannot be found.  Make sure the .java file is compiled.");
        }
        catch (ClassCastException e) {

            JOptionPane.showMessageDialog(THIS,
                "The chosen class does not extend from the Agent class.");
        }
        catch (Exception e) {
        	e.printStackTrace();

            JOptionPane.showMessageDialog(THIS,
                "The chosen class does not conform to the \"Agent\" class.");
        }

        return agent;
	}

}
