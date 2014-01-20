package WumpusEnvironment;
import WumpusEnvironment.Map.Grid;
import WumpusEnvironment.Agent.Agent;

import javax.media.opengl.*;
import javax.media.opengl.awt.*;
import javax.media.opengl.glu.*;
import javax.swing.*;
import com.jogamp.opengl.util.*;
import static javax.media.opengl.GL.*;  // GL constants
import static javax.media.opengl.GL2.*; // GL2 constants

public class ApplicationWindow implements GLEventListener{
	
	private double theta = 0;
	private double s = 0;
	private double c = 0;
	protected GLU glu;  // for the GL Utility
	
	protected static Grid grid; 
	
	public static void main(String[] args){
		grid = new Grid(10,10);
		grid.printGrid();
		Agent a = new Agent(grid);
		grid.printGrid();
		// Create the frame that will display the environment
		JFrame frame = new JFrame("Wumpus 3D");
		
		//set frame details
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setMinimumSize(new java.awt.Dimension(500, 500));
		
		// The OpenGL profile. Handles the version of OpenGL to use
		GLProfile glp = GLProfile.get(GLProfile.GL2);
		GLProfile.initSingleton();
		GLCapabilities caps = new GLCapabilities(glp);
		GLCanvas canvas = new GLCanvas(caps);
		
		// The Animator we need for the render loop
		Animator animator = new Animator(canvas);
		animator.start();
		
		//create components and put them in the frame
		frame.getContentPane().add(canvas);
		canvas.addGLEventListener(new ApplicationWindow());
		
		//size and display the frame
		frame.pack();
		frame.setVisible(true);
	}

	@Override
	public void display(GLAutoDrawable arg) {
		//put drawing code here
		updateMovingTri();
	    renderMovingTri(arg);
	}

	@Override
	public void dispose(GLAutoDrawable arg) {
		// cleanup code
		
	}

	@Override
	public void init(GLAutoDrawable arg) {
		// OpenGL initialization code
		arg.getGL().setSwapInterval(1); //set v-sync
		/*GL2 gl = arg.getGL().getGL2();      // get the OpenGL graphics context
		glu = new GLU();                         // get GL Utilities
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // set background (clear) color
		gl.glClearDepth(1.0f);      // set clear depth value to farthest
		gl.glEnable(GL_DEPTH_TEST); // enables depth testing
		gl.glDepthFunc(GL_LEQUAL);  // the type of depth test to do
		gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST); // best perspective correction
		gl.glShadeModel(GL_SMOOTH); // blends colors nicely, and smoothes out lighting*/
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		// what to do when window is resized
		/*GL2 gl = drawable.getGL().getGL2();  // get the OpenGL 2 graphics context
		 
	      if (height == 0) height = 1;   // prevent divide by zero
	      float aspect = (float)width / height;
	 
	      // Set the view port (display area) to cover the entire window
	      gl.glViewport(0, 0, width, height);
	 
	      // Setup perspective projection, with aspect ratio matches viewport
	      gl.glMatrixMode(GL_PROJECTION);  // choose projection matrix
	      gl.glLoadIdentity();             // reset projection matrix
	      //glu.gluPerspective(45.0, aspect, 0.1, 100.0); // fovy, aspect, zNear, zFar
	 
	      // Enable the model-view transform
	      gl.glMatrixMode(GL_MODELVIEW);
	      gl.glLoadIdentity(); // reset*/
		
	}
	
	protected void updateMovingTri() {
	    // what to update each frame
		theta += 0.01;
	    s = Math.sin(theta);
	    c = Math.cos(theta);
	}
	
	protected void renderMovingTri(GLAutoDrawable drawable) {
	    GL2 gl = drawable.getGL().getGL2();
	    
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
	    gl.glEnd();
	}
}
