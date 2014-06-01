package WumpusEnvironment;

import javax.swing.SwingUtilities;
import WumpusEnvironment.View.MainWindow.ApplicationWindow;

public class Wumpus3D {

	public static void main(String[] args){
		// Create the frame that will display the environment
		SwingUtilities.invokeLater(new Runnable() {
	         @Override
	         public void run() {
	            new ApplicationWindow();  // run the constructor
	         }
        });
	}

}
