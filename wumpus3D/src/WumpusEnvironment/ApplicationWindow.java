package WumpusEnvironment;
import WumpusEnvironment.Map.*;
import WumpusEnvironment.Agent.*;
import WumpusEnvironment.Agent.TestAgents.*;

import javax.media.opengl.*;
import javax.media.opengl.awt.*;
import javax.media.opengl.glu.*;
import javax.media.opengl.DebugGL3;
import javax.swing.*;
import com.jogamp.opengl.util.*;
import static javax.media.opengl.GL.*;  // GL constants
import static javax.media.opengl.GL3.*; // GL3 constants
import static javax.media.opengl.GL2ES2.*; // GL3 constants
import static javax.media.opengl.GL3.*; // GL3 constants

import java.awt.*;
import java.io.File; 
import java.io.FileNotFoundException;
import java.io.IOException; 
import java.nio.FloatBuffer; 
import java.nio.IntBuffer;
import java.util.Scanner;

import javax.media.opengl.GLAutoDrawable; 
import javax.media.opengl.GLEventListener; 
import javax.media.opengl.GLException; 
import com.jogamp.common.nio.Buffers; 
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import com.hackoeur.jglm.*;
import com.hackoeur.jglm.buffer.*;
import com.hackoeur.jglm.support.*;

public class ApplicationWindow{// implements GLEventListener{
	
	protected static final String logSeparator = "---------------\n";
	
	protected GLU glu;  // for the GL Utility
	int program; //shader program
	static int g_width = 800;
	static int g_height = 500;
	
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
	//protected final GLresources gr;
	
	public static void main(String[] args){
		//grid initialization variables
		//if map has only one square, there was a problem with the map file
		int gridWidth = 1, gridHeight = 1, gridNumGoals = 0;
		grid = new Grid(gridWidth,gridHeight,gridNumGoals);
		
		//Agent initialization variables
		//if Agent spawns at (0,0), there was a problem with the map file
		int agentStartX = 0, agentStartY = 0;
		Agent a = new TestAgent(grid,new Node(agentStartX,agentStartY));
		try {
			Scanner sc = new Scanner(new File("table-map.txt"));
			/*
			 * THIS HAS NO ERROR CHECKING CODE. ASSUMES ALL MAP FILES FOLLOW
			 * THIS FORMAT!!! PROBABLY A BAD IDEA BUT SHOULD CHECK WITH KURFESS.
			 */
			gridWidth = sc.nextInt(); //first number is width
			gridHeight = sc.nextInt(); //second number is height
			gridNumGoals = sc.nextInt(); //third number is number of goals on the map
			sc.nextLine(); //throw away rest of line
			
			//create the grid so we can modify Nodes
			grid = new Grid(gridWidth+2,gridHeight+2,gridNumGoals);
			for(int i = 0; i < gridHeight; i++){
				String nextRow = sc.nextLine();
				for(int j = 0; j < gridWidth; j++){
					switch(nextRow.charAt(j)){
						case 'S':
							agentStartX = j+1;
							agentStartY = i+1;
							break;
						case 'X':
							grid.setNode(j+1,i+1,Grid.WALL,true);
							break;
						case 'G':
							grid.setNode(j+1,i+1,Grid.GOAL,true);
							break;
						case 'W':
							grid.setNode(j+1,i+1,Grid.WUMPUS,true);
							break;
						case 'M':
							grid.setNode(j+1,i+1,Grid.MINION,true);
							break;
						case 'P':
							grid.setNode(j+1,i+1,Grid.PIT,true);
							break;
					}
				}
			}
			sc.close();
			
			//create Agent now that Grid is fully instantiated
			a = new TestTableAgent(grid,new Node(agentStartX,agentStartY));
		} catch (FileNotFoundException e) {
			System.out.println("File not found!");
		}
		//grid = new Grid();
		// Create the frame that will display the environment
		JFrame frame = new JFrame("Wumpus Environment 3D");
		
		//set frame details
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setMinimumSize(new java.awt.Dimension(g_width, g_height));
		
		//create the menu bar for the frame
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenuItem newSessionOption = new JMenuItem("New Wumpus Environment Session");
		fileMenu.add(newSessionOption);
		menuBar.add(fileMenu);
		frame.setJMenuBar(menuBar);
		
		//create the panel that will display the map and the log
		JPanel mainView = new JPanel();
		//it will have a map and a log
		//create map here
		JTextArea log = new JTextArea( logSeparator +
									  " * Map start *\n" +
									  logSeparator ,40,100);
		log.setEditable(false);
		log.setFont(new Font("Consolas",Font.PLAIN, 12));
		JScrollPane logScrollPane = new JScrollPane(log);
		logScrollPane.setMinimumSize(new Dimension(100,100));
		mainView.add(logScrollPane);
		frame.add(mainView);
		
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
		
		
		generateLogEntry(a,log);
		while(!grid.isSolved()){
			a.step();
			generateLogEntry(a,log);
		}
		log.append("* You found all the gold! *\n");
		log.append("*** GAME OVER ***\n");
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
		vertexShaderSource[0] = "#version 330\n" +
		    "layout(location=0) attribute vec3 position;\n" +
		    "layout(location=1) attribute vec3 color;\n" +
		    "varying vec3 vColor;\n" +
		    "void main(void)\n" +
		    "{\n" +
		    "gl_Position = vec4(position, 1.0);\n" +
		    "vColor = vec4(color, 1.0);\n" +
		    "}\n";
		gl.glShaderSource(vertexShader, 1, vertexShaderSource, null);
		gl.glCompileShader(vertexShader);

		// Create and fragment shader.
		int fragmentShader = gl.glCreateShader(GL_FRAGMENT_SHADER);
		String[] fragmentShaderSource = new String[1];
		fragmentShaderSource[0] = "#version 330\n" +
		    "varying vec4 vColor;\n" +
		    "void main(void)\n" +
		    "{\n" +
		    "gl_FragColor = vColor;\n" +
		    "}\n";
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
		log.append(grid.gridToString());
		log.append(a.locationToString());
		log.append("Agent heading: " + a.headingToString() + "\n");
		log.append(a.movementStatusToString());
		log.append(logSeparator);
		log.append("\n");
	}
}
