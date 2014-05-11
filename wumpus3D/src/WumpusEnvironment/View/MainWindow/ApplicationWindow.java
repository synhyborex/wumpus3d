package WumpusEnvironment.View.MainWindow;
import WumpusEnvironment.Model.Agent.*;
import WumpusEnvironment.Model.Map.*;

import javax.media.opengl.*;
import javax.media.opengl.awt.*;
import javax.media.opengl.glu.*;
import javax.media.opengl.DebugGL3;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileFilter;

import com.jogamp.opengl.util.FPSAnimator;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.FloatBuffer; 
import java.nio.IntBuffer;
import java.util.*;
import java.lang.*;

public class ApplicationWindow  extends JFrame implements ActionListener{// implements GLEventListener{
	
	protected final static int MIN_DELAY = 0;
	protected final static int MAX_DELAY = 1000;
	protected final static int DEFAULT_DELAY = 100;
	public static int CURRENT_DELAY = DEFAULT_DELAY;
	
	protected GLU glu;  // for the GL Utility
	int program; //shader program
	static int g_width = 800;
	static int g_height = 600;
	
	//handles
	int h_aPosition;
	int h_aColor;
	
	private IntBuffer buffers = IntBuffer.allocate(2);
	IntBuffer vertexArray = IntBuffer.allocate(1);
	float CubePos[] = {
		    -0.5f, 0.5f, -0.5f, /*top face 4 verts :0 */
		    -0.5f, 0.5f, 0.5f,
		    0.5f, 0.5f, 0.5f,
		    0.5f, 0.5f, -0.5f,
		    -0.5f, -0.5f, -0.5f, /*bottom face 4 verts :4*/
		    -0.5f, -0.5f, 0.5f,
		    0.5f, -0.5f, 0.5f,
		    0.5f, -0.5f, -0.5f,
		    -0.5f, -0.5f, 0.5f, /*left face 4 verts :8*/
		    -0.5f, -0.5f, -0.5f,
		    -0.5f, 0.5f, -0.5f,
		    -0.5f, 0.5f, 0.5f,
		    0.5f, -0.5f, 0.5f, /*right face 4 verts :12*/
		    0.5f, -0.5f, -0.5f,
		    0.5f, 0.5f, -0.5f,
		    0.5f, 0.5f, 0.5f
		  };
	
	int idx[] = {0,1,2, 2,3,0, 4,5,6, 6,7,4, 8,9,10, 10,11,8,  12,13,14, 14,15,12};

	private float[] colorData = {
	        1, 0, 0,
	        1, 1, 0,
	        0, 1, 0,
	        0, 1, 0,
	        0, 0, 1,
	        1, 0, 0
	};

	FloatBuffer CubeBuffObj = FloatBuffer.wrap(CubePos);
	IntBuffer CubeIdxBuffObj = IntBuffer.wrap(idx);
	int idxlen;
	FloatBuffer colorFB = FloatBuffer.wrap(colorData);
	//GLuint h;
	//GLenum j;
	
	protected static Grid grid;
	protected Grid startGrid;
	protected static Agent agent;
	protected Agent startAgent;
	//protected final GLresources gr;
	
	//the main panel
	JPanel mainWindow;
	
	//menu bar
	JMenuBar menuBar;
	//file menu
	JMenu fileMenu; 
	JMenuItem newSessionOption;
	static JFileChooser agentChooser;
	
	//map menu
	JMenu mapMenu;
	JMenuItem chooseMapOption;
	static JFileChooser mapChooser;
	
	//slider and movement buttons
	JPanel sliderAndButtons;
	JLabel sliderLabel;
	JSlider delayInterval;
	JButton stopButton;
	JButton stepButton;
	JButton autoStepButton;
	JButton resetButton;
	boolean autoRun; //whether or not we're doing autostep
	
	//main view
	JSplitPane mainView; //the main view for the log and map
	//the log pane
	JPanel logPane; //the pane that will hold both the log and the save button
	//the map pane
	JPanel mapPane; //the map pane
	
	public ApplicationWindow(){
		super("Wumpus Environment 3D");
		grid = Grid.getInstance();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setMinimumSize(new Dimension(g_width, g_height));
		
		//create the main view
		mainWindow = new JPanel(new BorderLayout());
		mainWindow.setLayout(new BorderLayout()); //set layout
		//create the menu bar for the frame
		//first menu
		menuBar = new JMenuBar();
		fileMenu = new JMenu("File");
		newSessionOption = new JMenuItem("New Wumpus Environment Session...");
		newSessionOption.addActionListener(this);
		fileMenu.add(newSessionOption);
		
		//add to frame
		menuBar.add(fileMenu); //add file menu to menu bar
		setJMenuBar(menuBar); //add menu bar to frame
		
		add(mainWindow);
		pack();
		setVisible(true);
	}
	
	public static void main(String[] args){
		// Create the frame that will display the environment
		/*JFrame frame = new ApplicationWindow();		
		frame.pack();
		frame.setVisible(true);*/
		SwingUtilities.invokeLater(new Runnable() {
	         @Override
	         public void run() {
	            new ApplicationWindow();  // run the constructor
	         }
	      });
	}
	
	public void actionPerformed(ActionEvent e){
		Object source = e.getSource();
		
		//option for choosing agent
		if(source.equals(newSessionOption)){
			agentChooser = new JFileChooser("./Agents"); //the file chooser for the Agent
			agentChooser.setDialogTitle("Select an Agent");
			agentChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	        agentChooser.setFileFilter(new FileFilter() {
	        	 
	            public String getDescription() {
	                return "Agent class files (*.class)";
	            }
	         
	            public boolean accept(File f) {
	                if (f.isDirectory()) {
	                    return true;
	                } else {
	                    return f.getName().toLowerCase().endsWith(".class");
	                }
	            }
	        });
	        int result = agentChooser.showOpenDialog(this);
	        if(result == JFileChooser.APPROVE_OPTION){
	        	agent = AgentLoader.loadAgentFromFile(agentChooser.getSelectedFile());
	        	if(agent != null){
		        	addOtherMenus();
		        	showMainWindowContents();
	        	}
	        	else actionPerformed(e);
	        }
		}
		else if(source.equals(chooseMapOption)){
			mapChooser = new JFileChooser("./Maps"); //the file chooser for the map
			mapChooser.setDialogTitle("Select a Map");
			mapChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	        mapChooser.setFileFilter(new FileFilter() {
	        	 
	            public String getDescription() {
	                return "Map files (*.dat)";
	            }
	         
	            public boolean accept(File f) {
	                if (f.isDirectory()) {
	                    return true;
	                } else {
	                    return f.getName().toLowerCase().endsWith(".dat");
	                }
	            }
	        });
	        int result = mapChooser.showOpenDialog(this);
	        if(result == JFileChooser.APPROVE_OPTION){
	        	grid = MapLoader.loadMapFromFile(mapChooser.getSelectedFile());
	        	//grid.setCleanInitialGrid();
	        	showMap(MapLoader.isSearchMap());
	        }
		}
		else if(source.equals(stopButton)){
			stopButton.setEnabled(false);
			enableButtons(new JButton[]{stepButton,autoStepButton});
			autoRun = false;
		}
		else if(source.equals(stepButton)){
			nextStep();
		}
		else if(source.equals(autoStepButton)){
			stopButton.setEnabled(true);
			disableButtons(new JButton[]{stepButton,autoStepButton});
			autoRun = true;
			for(;;)
				if(autoRun)
					nextStep();
				else break;
		}
		else if(source.equals(resetButton)){
			autoRun = false;
			agent.secretReset();
			grid = grid.getStartGrid();
			agent = startAgent;
			grid = startGrid;
			stopButton.setEnabled(false);
			enableButtons(new JButton[]{stepButton,autoStepButton});
			Logger.generateLogEntry(agent,grid);
		}
    }
	
	protected void addOtherMenus(){
		//add the map menu
		menuBar.removeAll(); //clear all menus out of the menu bar
		mapMenu = new JMenu("Map");
		chooseMapOption = new JMenuItem("Open Map...");
		chooseMapOption.addActionListener(this);
		mapMenu.add(chooseMapOption);
		
		//add new menus to frame
		menuBar.add(fileMenu);
		menuBar.add(mapMenu); //add map menu to menu bar
		menuBar.validate();
	}
	
	protected void showMainWindowContents(){
		mainWindow.removeAll();
		//create slider and movement buttons
		sliderAndButtons = new JPanel();
		sliderLabel = new JLabel("Delay (ms)");
		delayInterval = new JSlider(JSlider.HORIZONTAL,
									MIN_DELAY,MAX_DELAY,DEFAULT_DELAY);
		delayInterval.setMajorTickSpacing(100);
		delayInterval.setMinorTickSpacing(50);
		delayInterval.setPaintTicks(true);
		delayInterval.setPaintLabels(true);
		delayInterval.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent arg0) {
            	CURRENT_DELAY = delayInterval.getValue();
            }
        });
		Dimension d = delayInterval.getPreferredSize();
		delayInterval.setPreferredSize(new Dimension(d.width+125,d.height));
		stopButton = new JButton("STOP");
		stepButton = new JButton("STEP");
		autoStepButton = new JButton("AUTO STEP");
		autoRun = false;
		resetButton = new JButton("RESET");
		disableButtons(new JButton[]{stopButton,stepButton,autoStepButton,resetButton});
		stopButton.addActionListener(this); //so we don't forget to do it later
		stepButton.addActionListener(this);
		autoStepButton.addActionListener(this);
		resetButton.addActionListener(this);
		sliderAndButtons.add(sliderLabel);
		sliderAndButtons.add(delayInterval);
		sliderAndButtons.add(stopButton);
		sliderAndButtons.add(stepButton);
		sliderAndButtons.add(autoStepButton);
		sliderAndButtons.add(resetButton);
		mainWindow.add(sliderAndButtons,"North");
		
		//it will have a map and a log
		//create log panel
		logPane = new Logger();
		
		//create panel for map display
		mapPane = new JPanel();
		
		//create the split pane that will display the map and the log
		mainView = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				mapPane,logPane);
		mainView.setDividerLocation(g_width-275); //log is smaller size than map
		mainView.setResizeWeight(1.0); //make sure only map gets resized when frame gets resized
		mainWindow.add(mainView,"Center");

		mainWindow.validate();
	}
	
	protected void showMap(boolean fairy){
		//agent.secretReset();
		//agent = startAgent;
		//grid = startGrid;
		agent.setStartLocation(grid.getAgentLocation());
		if(fairy){
			agent.setFairy(new Fairy(grid,grid.getAgentLocation()));
		}
		//print start of log
		Logger.clear(); //clear any text that was there before
		Logger.writeToLog(Logger.logSeparator +
				" * Map start *\r\n" +
				Logger.logSeparator);
		Logger.generateLogEntry(agent,grid);
		
		//enable movement buttons
		enableButtons(new JButton[]{stepButton,autoStepButton,resetButton});
		
		// Create the OpenGL rendering canvas
        GLJPanel canvas = new MapCanvas();
        //canvas.setMinimumSize(new Dimension(200, 200));
 
        // Create a animator that drives canvas' display() at the specified FPS.
        final FPSAnimator animator = new FPSAnimator(canvas, 60, true);
 
        // Create the top-level container frame
        mapPane.add(canvas,"Center");
        this.addWindowListener(new WindowAdapter() {
           @Override
           public void windowClosing(WindowEvent e) {
              // Use a dedicate thread to run the stop() to ensure that the
              // animator stops before program exits.
              new Thread() {
                 @Override
                 public void run() {
                    if (animator.isStarted()) animator.stop();
                    System.exit(0);
                 }
              }.start();
           }
        });
        animator.start(); // start the animation loop
		mainView.setLeftComponent(canvas);
	}
	
	protected void nextStep(){
		AgentHandler.agentStep(agent, grid, MapLoader.isSearchMap());
		if(agent.isDead()){
			Logger.writeToLog("* You died! Better luck next time. *\r\n");
			Logger.writeToLog("*** GAME OVER ***\r\n");
			disableButtons(new JButton[]{stopButton,stepButton,autoStepButton});
			autoRun = false;
		}
		else if(grid.isSolved()){
			Logger.writeToLog("* You found all the gold! *\r\n");
			Logger.writeToLog("*** GAME OVER ***\r\n");
			disableButtons(new JButton[]{stopButton,stepButton,autoStepButton});
			autoRun = false;
		}
	}
	
	protected void disableButtons(JButton[] list){
		for(JButton j: list)
			j.setEnabled(false);
	}
	
	protected void enableButtons(JButton[] list){
		for(JButton j: list)
			j.setEnabled(true);
	}
}
