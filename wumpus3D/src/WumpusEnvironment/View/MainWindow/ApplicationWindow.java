package WumpusEnvironment.View.MainWindow;
import WumpusEnvironment.Model.Agent.*;
import WumpusEnvironment.Model.Map.*;

import javax.media.opengl.awt.*;
import javax.media.opengl.glu.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileFilter;
import com.jogamp.opengl.util.FPSAnimator;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class ApplicationWindow  extends JFrame implements ActionListener{// implements GLEventListener{
	private static final long serialVersionUID = 1L;
	protected final static int MIN_DELAY = 0;
	protected final static int MAX_DELAY = 1000;
	protected final static int DEFAULT_DELAY = 100;
	public static int CURRENT_DELAY = DEFAULT_DELAY;
	
	protected GLU glu;  // for the GL Utility
	int program; //shader program
	static int g_width = 800;
	static int g_height = 600;
	
	protected static Grid grid;
	protected static Agent agent;
	protected AgentHandler agentHandler;
	
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
	static JButton stopButton;
	static JButton stepButton;
	static JButton autoStepButton;
	static JButton resetButton;
	
	//main view
	JSplitPane mainView; //the main view for the log and map
	//the log pane
	JPanel logPane; //the pane that will hold both the log and the save button
	//the map pane
	JPanel mapPane; //the map pane
	JLabel loadDisplay;
	GLJPanel mapView;
	FPSAnimator mapAnimator;
	
	public ApplicationWindow(){
		super("Wumpus Environment 3D");
		grid = Grid.getInstance();
		agentHandler = null;
		mapAnimator = null;
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
	
	public void actionPerformed(ActionEvent e){
		Object source = e.getSource();
		
		//option for choosing agent
		if(source.equals(newSessionOption)){
			if(agentHandler != null) agentHandler.setAutoStep(false);
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
			if(agentHandler != null) agentHandler.setAutoStep(false);
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
	        	showMap(MapLoader.isSearchMap());
	        }
		}
		else if(source.equals(stopButton)){
			stopButton.setEnabled(false);
			enableButtons(new JButton[]{stepButton,autoStepButton});
			agentHandler.setAutoStep(false);
		}
		else if(source.equals(stepButton)){
			agentHandler.agentStep();
		}
		else if(source.equals(autoStepButton)){
			stopButton.setEnabled(true);
			disableButtons(new JButton[]{stepButton,autoStepButton});
			agentHandler.setAutoStep(true);
		}
		else if(source.equals(resetButton)){
			agentHandler.setAutoStep(false);
			
			//set fresh grid
			grid = MapLoader.loadMapFromFile(mapChooser.getSelectedFile());
			
			//set fresh agent
			agent.privateReset();
			
			//update buttons
			stopButton.setEnabled(false);
			enableButtons(new JButton[]{stepButton,autoStepButton});
			
			//set fresh logger
			Logger.clear();
			Logger.printMapStart();
			Logger.generateLogEntry(agent,grid);
		}
    }
	
	protected void addOtherMenus(){
		if(agentHandler != null) agentHandler.setAutoStep(false);
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
		if(agentHandler != null) agentHandler.setAutoStep(false);
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
		mapPane.setLayout(new BoxLayout(mapPane,BoxLayout.X_AXIS));		
		loadDisplay = new JLabel("Select a map to display");
		loadDisplay.setHorizontalAlignment(SwingConstants.CENTER);
		loadDisplay.setFont(new Font(loadDisplay.getFont().toString(),Font.ITALIC,loadDisplay.getFont().getSize()));
		//mapPane.add(Box.createRigidArea(new Dimension(0,50)));
		//mapPane.add(Box.createRigidArea(new Dimension(50,0)));
		mapPane.add(Box.createHorizontalGlue());
		mapPane.add(loadDisplay);
		mapPane.add(Box.createHorizontalGlue());
		
		//create the split pane that will display the map and the log
		mainView = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				mapPane,logPane);
		mainView.setDividerLocation(g_width-275); //log is smaller size than map
		mainView.setResizeWeight(1.0); //make sure only map gets resized when frame gets resized
		mainWindow.add(mainView,"Center");

		mainWindow.validate();
	}
	
	protected void showMap(boolean fairy){
		//display loading text
		mapPane.removeAll();
		mapPane.add(loadDisplay);
		mapPane.revalidate();
		loadDisplay.setText("Loading your map...");
		loadDisplay.paintImmediately(loadDisplay.getVisibleRect());
		
		//make sure all threads are closed before we fork off new ones!
		if(mapAnimator != null){
			if(mapAnimator.isStarted()) mapAnimator.stop();
		}
		if(agentHandler != null){
			agentHandler.stopThread();
			try {
				agentHandler.join(1000);
			} catch (InterruptedException e1) {
			}
		}
		
		//reset all components
		if(agentHandler != null) agentHandler.setAutoStep(false);
		agent.setStartLocation(grid.getAgentLocation());
		agent.privateReset();
		if(fairy){
			agent.setFairy(new Fairy(grid,grid.getAgentLocation()));
		}
		agentHandler = new AgentHandler(agent,grid); //create the agent handler thread
		//print start of log
		Logger.clear(); //clear any text that was there before
		Logger.printMapStart();
		Logger.generateLogEntry(agent,grid);
		
		// Create the OpenGL rendering canvas
        mapView = new MapCanvas();
 
        // Create a animator that drives canvas' display() at the specified FPS.
        mapAnimator = new FPSAnimator(mapView, 80, true);
 
        // Create the top-level container frame
        mapPane.removeAll();
        mapPane.add(mapView);
        
        //enable movement buttons
  		enableButtons(new JButton[]{stepButton,autoStepButton,resetButton});
  		stopButton.setEnabled(false);
  		
  		// start the animation loop
        mapAnimator.start(); 
		mainView.setLeftComponent(mapView);
		
		//now that everything is set up, start the agent handling thread
		agentHandler.start();
	}
	
	public static void disableButtons(JButton[] list){
		for(JButton j: list)
			j.setEnabled(false);
	}
	
	public static void enableButtons(JButton[] list){
		for(JButton j: list)
			j.setEnabled(true);
	}
	
	public static void disableMovementButtons(){
		disableButtons(new JButton[]{stopButton,stepButton,autoStepButton});
	}
}
