package WumpusEnvironment.View.MainWindow;
import WumpusEnvironment.Model.Agent.*;
import WumpusEnvironment.Model.Map.*;

import javax.media.opengl.*;
import javax.media.opengl.awt.*;
import javax.media.opengl.glu.*;
import javax.media.opengl.DebugGL3;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import com.jogamp.opengl.util.*;
import static javax.media.opengl.GL.*;  // GL constants
import static javax.media.opengl.GL3.*; // GL3 constants
import static javax.media.opengl.GL2ES2.*; // GL3 constants
import static javax.media.opengl.GL3.*; // GL3 constants

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.FloatBuffer; 
import java.nio.IntBuffer;
import java.util.*;

import javax.media.opengl.GLAutoDrawable; 
import javax.media.opengl.GLEventListener; 
import javax.media.opengl.GLException; 
import com.jogamp.common.nio.Buffers; 
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import com.hackoeur.jglm.*;
import com.hackoeur.jglm.buffer.*;
import com.hackoeur.jglm.support.*;

public class ApplicationWindow  extends JFrame implements ActionListener{// implements GLEventListener{
	
	protected static final String logSeparator = "---------------\r\n";
	protected final static int MIN_DELAY = 0;
	protected final static int MAX_DELAY = 1000;
	protected final static int DEFAULT_DELAY = 100;	
	
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
	JScrollPane logScrollPane; //the scroll pane that will hold the log
	static JTextArea log; //the actual log
	JButton saveLogButton; //the button to save the log
	JFileChooser logSaver;
	//the map pane
	JPanel mapPane; //the map pane
	
	public ApplicationWindow(){
		super("Wumpus Environment 3D");
		grid = Grid.getInstance();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setMinimumSize(new Dimension(g_width, g_height));
		
		//create the main view
		mainWindow = new JPanel();
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
	}
	
	public static void main(String[] args){
		// Create the frame that will display the environment
		JFrame frame = new ApplicationWindow();		
		
		// The OpenGL profile. Handles the version of OpenGL to use
		/*GLProfile glp = GLProfile.get(GLProfile.GL3);
		GLProfile.initSingleton();
		GLCapabilities caps = new GLCapabilities(glp);
		GLCanvas canvas = new GLCanvas(caps);
		
		// The Animator we need for the render loop
		//Animator animator = new Animator(canvas);
		//animator.start();
		
		//create components and put them in the frame
		frame.getContentPane().add(canvas);
		canvas.addGLEventListener(new ApplicationWindow());*/
		
		//size and display the frame
		frame.pack();
		frame.setVisible(true);		
		
		//create Agent now that Grid is fully instantiated
		//agent = AgentLoader.loadAgentFromFile(agentChooser.getSelectedFile());
		if(agent != null){

			
		}
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
	        	addOtherMenus();
	        	showMainWindowContents();
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
	        	grid.setCleanInitialGrid();
	        	showMap(MapLoader.isSearchMap());
	        }
		}
		else if(source.equals(stopButton)){
			stopButton.setEnabled(false);
			enableButtons(new JButton[]{stepButton,autoStepButton});
			autoRun = false;
		}
		else if(source.equals(stepButton)){
			runAgentStep(MapLoader.isSearchMap());
		}
		else if(source.equals(autoStepButton)){
			stopButton.setEnabled(true);
			disableButtons(new JButton[]{stepButton,autoStepButton});
			autoRun = true;
			while(autoRun)
				runAgentStep(MapLoader.isSearchMap());
		}
		else if(source.equals(resetButton)){
			autoRun = false;
			agent.secretReset();
			grid = grid.getStartGrid();
			agent = startAgent;
			grid = startGrid;
			stopButton.setEnabled(false);
			enableButtons(new JButton[]{stepButton,autoStepButton});
			generateLogEntry(agent,log);
		}
		else if(source.equals(saveLogButton)){
			logSaver = new JFileChooser(".");
			logSaver.setDialogTitle("Choose where to save the log");
			logSaver.setFileSelectionMode(JFileChooser.FILES_ONLY);
	        logSaver.setFileFilter(new FileFilter() {
	        	 
	            public String getDescription() {
	                return "Log files (*.txt)";
	            }
	         
	            public boolean accept(File f) {
	                if (f.isDirectory()) {
	                    return true;
	                } else {
	                    return f.getName().toLowerCase().endsWith(".txt");
	                }
	            }
	        });
	        int result = logSaver.showOpenDialog(this);
	        if(result == JFileChooser.APPROVE_OPTION){
	        	String logFileName = logSaver.getSelectedFile().getName();
	        	if(logFileName.indexOf('.') < 0)
	        		logFileName += ".txt";
	        	File logFile = new File(logFileName);
	        	try
	            {
	              FileWriter localFileWriter = new FileWriter(logFile);
	              PrintWriter localPrintWriter = new PrintWriter(localFileWriter);
	              
	              localPrintWriter.println(log.getText());
	              localPrintWriter.close();
	            }
	            catch (IOException localIOException)
	            {
	              localIOException.printStackTrace();
	            }
	        }
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
		logPane = new JPanel();
		logPane.setLayout(new BorderLayout());
		log = new JTextArea();
		log.setEditable(false);
		log.setFont(new Font("Consolas",Font.PLAIN, 12));
		log.setLineWrap(true);
		log.setWrapStyleWord(true);
		logScrollPane = new JScrollPane(log);
		logScrollPane.setWheelScrollingEnabled(true);
		logScrollPane.setMaximumSize(new Dimension(100, 400));
		saveLogButton = new JButton("Save log...");
		saveLogButton.addActionListener(this);
		logPane.add(logScrollPane,"Center");
		logPane.add(saveLogButton,"South");
		
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
		log.setText(null); //clear any text that was there before
		log.append(logSeparator +
				" * Map start *\r\n" +
				logSeparator);
		generateLogEntry(agent,log);
		
		//enable movement buttons
		enableButtons(new JButton[]{stepButton,autoStepButton,resetButton});
	}
	
	protected void runAgentStep(boolean fairy){
		if(!agent.isDead() && !grid.isSolved()){
			agent.step();
			if(fairy){
				if(agent.fairyFoundAllGoals()){
					generateLogEntry(agent,log);
				}
			}
			else generateLogEntry(agent,log);
		}
		if(agent.isDead()){
			log.append("* You died! Better luck next time. *\r\n");
			log.append("*** GAME OVER ***\r\n");
			disableButtons(new JButton[]{stopButton,stepButton,autoStepButton});
			autoRun = false;
		}
		else if(grid.isSolved()){
			log.append("* You found all the gold! *\r\n");
			log.append("*** GAME OVER ***\r\n");
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

	//@Override
	public void display(GLAutoDrawable drawable) {
		//put drawing code here
		//updateMovingTri();
	    //renderMovingTri(arg);
		GL3 gl = drawable.getGL().getGL3();
		drawable.setGL(new DebugGL3(drawable.getGL().getGL3()));
		
		gl.glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
		//gl.glUseProgram(program);
		
		gl.glEnableVertexAttribArray(h_aPosition);
		gl.glBindBuffer(GL_ARRAY_BUFFER,buffers.get(0));
		gl.glVertexAttribPointer(h_aPosition, 3, GL_FLOAT, false, 0, 0);
		
		gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,buffers.get(1));
		gl.glDrawElements(GL_TRIANGLES, idxlen, GL_UNSIGNED_SHORT, 0);
		
		gl.glDisableVertexAttribArray(h_aPosition);
   	    //gl.glBindVertexArray(vertexArray.get(0));
	    //gl.glDrawArrays(GL.GL_TRIANGLES, 0, 6);
	}

	////@Override
	public void dispose(GLAutoDrawable arg) {
		// cleanup code
		
	}

	//@Override
	public void init(GLAutoDrawable drawable) {
		// OpenGL initialization code
		drawable.getGL().setSwapInterval(1); //set v-sync
		GL3 gl = drawable.getGL().getGL3();      // get the OpenGL graphics context
		drawable.setGL(new DebugGL3(drawable.getGL().getGL3()));
		//glu = new GLU();                         // get GL Utilities
		gl.glClearColor(0.0f, 1.0f, 1.0f, 1.0f); // set background (clear) color
		gl.glClearDepth(1.0f);      // set clear depth value to farthest
		gl.glEnable(GL_DEPTH_TEST); // enables depth testing
		gl.glDepthFunc(GL_LEQUAL);  // the type of depth test to do
		//gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST); // best perspective correction
		//gl.glShadeModel(GL_SMOOTH); // blends colors nicely, and smoothes out lighting
		
		installShader(drawable);
		
		idxlen = 24;
		gl.glGenBuffers(2, buffers);
		gl.glBindBuffer(GL_ARRAY_BUFFER, buffers.get(0));
		gl.glBufferData(GL_ARRAY_BUFFER, 4 * 16 * 3, CubeBuffObj, GL_STATIC_DRAW);
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, buffers.get(1));
	    gl.glBufferData(GL_ARRAY_BUFFER, 4 * 8 * 3, CubeIdxBuffObj, GL_STATIC_DRAW);
	    
	    //set up handles
	    //h_aPosition = gl.glGetAttribLocation(program,"position");
	    //h_aColor = gl.glGetAttribLocation(program, "color");
	    
	    //gl.glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
	   // gl.glUseProgram(program);
		
		/*gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		gl.glGenBuffers(2, buffers);

	    gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, buffers.get(0));
	    gl.glBufferData(GL2.GL_ARRAY_BUFFER, 4 * 6 * 2, vertexFB, GL3.GL_STATIC_DRAW);

	    gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, buffers.get(1));
	    gl.glBufferData(GL2.GL_ARRAY_BUFFER, 4 * 6 * 3, colorFB, GL2.GL_STREAM_DRAW);
	    
	 // Create Vertex Array.
	    gl.glGenVertexArrays(1, vertexArray);
	    gl.glBindVertexArray(vertexArray.get(0));

	    // Specify how data should be sent to the Program.

	    // VertexAttribArray 0 corresponds with location 0 in the vertex shader.
	    gl.glEnableVertexAttribArray(0);
	    gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, buffers.get(0));
	    gl.glVertexAttribPointer(0, 2, GL.GL_FLOAT, false, 0, 0);

	    // VertexAttribArray 1 corresponds with location 1 in the vertex shader.
	    gl.glEnableVertexAttribArray(1);
	    gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, buffers.get(1));
	    gl.glVertexAttribPointer(1, 3, GL.GL_FLOAT, false, 0, 0);*/
	}

	//@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		g_width = width;
		g_height = height;
		// what to do when window is resized
		GL3 gl = drawable.getGL().getGL3();  // get the OpenGL 3 graphics context
		//drawable.setGL(new DebugGL3(drawable.getGL().getGL3()));
		 
	      if (height == 0) height = 1;   // prevent divide by zero
	      float aspect = (float)width / (float)height;
	 
	      // Set the view port (display area) to cover the entire window
	      gl.glViewport(0, 0, width, height);
	      //glu.gluPerspective(45.0, aspect, 0.1, 100.0); // fovy, aspect, zNear, zFar
	 
	      // Setup perspective projection, with aspect ratio matches viewport
	      /*gl.glMatrixMode(GL_PROJECTION);  // choose projection matrix
	      gl.glLoadIdentity();             // reset projection matrix
	      glu.gluPerspective(45.0, aspect, 0.1, 100.0); // fovy, aspect, zNear, zFar
	 
	      // Enable the model-view transform
	      gl.glMatrixMode(GL_MODELVIEW);
	      gl.glLoadIdentity(); // reset*/
		
	}
	
	protected void installShader(GLAutoDrawable drawable){
		GL3 gl = drawable.getGL().getGL3();
		drawable.setGL(new DebugGL3(drawable.getGL().getGL3()));
		// Create program.
		program = gl.glCreateProgram();

		// Create vertexShader.
		int vertexShader = gl.glCreateShader(GL_VERTEX_SHADER);
		String[] vertexShaderSource = new String[1];
		vertexShaderSource[0] = "#version 330\r\n" +
		    "layout(location=0) attribute vec3 position;\r\n" +
		    "layout(location=1) attribute vec3 color;\r\n" +
		    "varying vec3 vColor;\r\n" +
		    "void main(void)\r\n" +
		    "{\r\n" +
		    "gl_Position = vec4(position, 1.0);\r\n" +
		    "vColor = vec4(color, 1.0);\r\n" +
		    "}\r\n";
		gl.glShaderSource(vertexShader, 1, vertexShaderSource, null);
		gl.glCompileShader(vertexShader);

		// Create and fragment shader.
		int fragmentShader = gl.glCreateShader(GL_FRAGMENT_SHADER);
		String[] fragmentShaderSource = new String[1];
		fragmentShaderSource[0] = "#version 330\r\n" +
		    "varying vec4 vColor;\r\n" +
		    "void main(void)\r\n" +
		    "{\r\n" +
		    "gl_FragColor = vColor;\r\n" +
		    "}\r\n";
		gl.glShaderSource(fragmentShader, 1, fragmentShaderSource, null);
		gl.glCompileShader(fragmentShader);

		// Attach shaders to program.
		gl.glAttachShader(program, vertexShader);
		gl.glAttachShader(program, fragmentShader);
		gl.glLinkProgram(program);
	}
	
	void setProjMatrix(){
		Mat4 proj = Matrices.perspective(80.f,(float)g_width/g_height,0.1f,100.f);
	}
	
	protected void updateMovingTri() {
	    // what to update each frame
		/*theta += 0.01;
	    s = Math.sin(theta);
	    c = Math.cos(theta);*/
	}
	
	protected void renderMovingTri(GLAutoDrawable drawable) {
	    /*GL2 gl = drawable.getGL().getGL2();
	    
	    //clear background color
	    gl.glClear(GL.GL_COLOR_BUFFER_BIT);
	    
	    // draw a triangle filling the window
	    gl.glBegin(GL.GL_TRIANGLES);
	    gl.glColor3f(1, 0, 0);
	    gl.glVertex2d(-c, -c);
	    gl.glColor3f(0, 1, 0);
	    gl.glVertex2d(0, c);
	    gl.glColor3f(0, 0, 1);
	    gl.glVertex2d(s, -s);
	    gl.glEnd();*/
	}
	
	protected static void generateLogEntry(Agent a, JTextArea log){
		log.append(grid.gridToString()); //print the grid
		log.append(a.locationToString()); //print the Agent's location
		log.append("Agent heading: " + a.headingToString() + "\r\n"); //print the Agent's heading
		log.append(a.movementStatusToString()); //print what happened last step
		log.append(logSeparator); //print the separator for the next round
		log.append("\r\n");
	}
	
	public static JTextArea getLog(){return log;}
	
	public static void writeToLog(String s){log.append(s);}
	
	public static Grid currentGrid(){return grid;}
}
