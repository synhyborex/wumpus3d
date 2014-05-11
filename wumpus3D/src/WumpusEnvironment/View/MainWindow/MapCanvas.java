package WumpusEnvironment.View.MainWindow;

import javax.media.opengl.*;
import javax.media.opengl.awt.*;
import javax.media.opengl.glu.GLU;

import WumpusEnvironment.Model.Agent.Agent;
import WumpusEnvironment.Model.Map.*;
import static javax.media.opengl.GL.*;  // GL constants
import static javax.media.opengl.GL2.*; // GL2 constants
 
/**
 * JOGL 2.0 Program Template (GLCanvas)
 * This is a "Component" which can be added into a top-level "Container".
 * It also handles the OpenGL events to render graphics.
 */
@SuppressWarnings("serial")
public class MapCanvas extends GLJPanel implements GLEventListener {
   private GLU glu;  // for the GL Utility
   private float anglePyramid = 0;    // rotational angle in degree for pyramid
   private float angleCube = 0;       // rotational angle in degree for cube
   private float speedPyramid = 2.0f; // rotational speed for pyramid
   private float speedCube = -1.5f;   // rotational speed for cube
   private Grid map;
 
   /** Constructor to setup the GUI for this Component */
   public MapCanvas() {
      this.addGLEventListener(this);
      map = Grid.getInstance();
   }
 
   // ------ Implement methods declared in GLEventListener ------
 
   /**
    * Called back immediately after the OpenGL context is initialized. Can be used
    * to perform one-time initialization. Run only once.
    */
   @Override
   public void init(GLAutoDrawable drawable) {
	   GL2 gl = drawable.getGL().getGL2();      // get the OpenGL graphics context
      glu = new GLU();                         // get GL Utilities
      gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // set background (clear) color
      gl.glClearDepth(1.0f);      // set clear depth value to farthest
      gl.glEnable(GL_DEPTH_TEST); // enables depth testing
      gl.glDepthFunc(GL_LEQUAL);  // the type of depth test to do
      gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST); // best perspective correction
      gl.glShadeModel(GL_SMOOTH); // blends colors nicely, and smoothes out lighting
 
      // ----- Your OpenGL initialization code here -----
   }
 
   /**
    * Call-back handler for window re-size event. Also called when the drawable is
    * first set to visible.
    */
   @Override
   public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
      GL2 gl = drawable.getGL().getGL2();  // get the OpenGL 2 graphics context
 
      if (height == 0) height = 1;   // prevent divide by zero
      float aspect = (float)width / height;
 
      // Set the view port (display area) to cover the entire window
      gl.glViewport(0, 0, width, height);
 
      // Setup perspective projection, with aspect ratio matches viewport
      gl.glMatrixMode(GL_PROJECTION);  // choose projection matrix
      gl.glLoadIdentity();             // reset projection matrix
      glu.gluPerspective(45.0, aspect, 0.1, 100.0); // fovy, aspect, zNear, zFar
      
      //set up camera
      int mapWidth = map.getWidth(), mapHeight = map.getHeight();
      int distance = -(mapWidth+mapHeight/2);
      int[] midPoint = {-mapWidth/2,-mapHeight/2}; 
      glu.gluLookAt(midPoint[0], midPoint[1], distance, midPoint[0], midPoint[1], 0, 0, 1, 0); //set camera
 
      // Enable the model-view transform
      gl.glMatrixMode(GL_MODELVIEW);
      gl.glLoadIdentity(); // reset
   }
 
   /**
    * Called back by the animator to perform rendering.
    */
   @Override
   public void display(GLAutoDrawable drawable) {
	   GL2 gl = drawable.getGL().getGL2();  // get the OpenGL 2 graphics context
	      gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear color and depth buffers
	      
	      drawMap(drawable);
	 
	      // ----- Render the Pyramid -----
	      /*gl.glLoadIdentity();                 // reset the model-view matrix
	      gl.glTranslatef(-1.6f, 0.0f, -16.0f); // translate left and into the screen
	      gl.glRotatef(anglePyramid, -0.2f, 1.0f, 0.0f); // rotate about the y-axis
	 
	      gl.glBegin(GL_TRIANGLES); // of the pyramid
	 
	      // Font-face triangle
	      gl.glColor3f(1.0f, 0.0f, 0.0f); // Red
	      gl.glVertex3f(0.0f, 1.0f, 0.0f);
	      gl.glColor3f(0.0f, 1.0f, 0.0f); // Green
	      gl.glVertex3f(-1.0f, -1.0f, 1.0f);
	      gl.glColor3f(0.0f, 0.0f, 1.0f); // Blue
	      gl.glVertex3f(1.0f, -1.0f, 1.0f);
	 
	      // Right-face triangle
	      gl.glColor3f(1.0f, 0.0f, 0.0f); // Red
	      gl.glVertex3f(0.0f, 1.0f, 0.0f);
	      gl.glColor3f(0.0f, 0.0f, 1.0f); // Blue
	      gl.glVertex3f(1.0f, -1.0f, 1.0f);
	      gl.glColor3f(0.0f, 1.0f, 0.0f); // Green
	      gl.glVertex3f(1.0f, -1.0f, -1.0f);
	 
	      // Back-face triangle
	      gl.glColor3f(1.0f, 0.0f, 0.0f); // Red
	      gl.glVertex3f(0.0f, 1.0f, 0.0f);
	      gl.glColor3f(0.0f, 1.0f, 0.0f); // Green
	      gl.glVertex3f(1.0f, -1.0f, -1.0f);
	      gl.glColor3f(0.0f, 0.0f, 1.0f); // Blue
	      gl.glVertex3f(-1.0f, -1.0f, -1.0f);
	 
	      // Left-face triangle
	      gl.glColor3f(1.0f, 0.0f, 0.0f); // Red
	      gl.glVertex3f(0.0f, 1.0f, 0.0f);
	      gl.glColor3f(0.0f, 0.0f, 1.0f); // Blue
	      gl.glVertex3f(-1.0f, -1.0f, -1.0f);
	      gl.glColor3f(0.0f, 1.0f, 0.0f); // Green
	      gl.glVertex3f(-1.0f, -1.0f, 1.0f);
	 
	      gl.glEnd(); // of the pyramid*/
	 
	      // ----- Render the Color Cube -----
	      /*gl.glLoadIdentity();                // reset the current model-view matrix
	      gl.glTranslatef(0.0f, 0.0f, -10.0f); // translate right and into the screen
	      //gl.glRotatef(angleCube, 1.0f, 1.0f, 1.0f); // rotate about the x, y and z-axes
	      render(drawable);
	      gl.glLoadIdentity();                // reset the current model-view matrix
	      gl.glTranslatef(-1.0f, 0.0f, -10.0f); // translate right and into the screen
	      //gl.glRotatef(angleCube, 1.0f, 1.0f, 1.0f); // rotate about the x, y and z-axes
	      render(drawable);
	      gl.glLoadIdentity();                // reset the current model-view matrix
	      gl.glTranslatef(1.5f, 0.0f, -10.0f); // translate right and into the screen
	      //gl.glRotatef(angleCube, 1.0f, 1.0f, 1.0f); // rotate about the x, y and z-axes
	      render(drawable);
	      gl.glLoadIdentity();                // reset the current model-view matrix
	      gl.glTranslatef(0.0f, 2.0f, -10.0f); // translate right and into the screen
	      //gl.glRotatef(angleCube, 1.0f, 1.0f, 1.0f); // rotate about the x, y and z-axes
	      render(drawable);
	      gl.glLoadIdentity();                // reset the current model-view matrix
	      gl.glTranslatef(-0.5f, 2.0f, -10.0f); // translate right and into the screen
	      //gl.glRotatef(angleCube, 1.0f, 1.0f, 1.0f); // rotate about the x, y and z-axes
	      render(drawable);
	 
	      // Update the rotational angle after each refresh.
	      anglePyramid += speedPyramid;
	      angleCube += speedCube;*/
   }
   
   private void drawMap(GLAutoDrawable drawable){
	   for(int i = 0; i < map.getHeight(); i++){
		   for(int j = 0; j < map.getWidth(); j++){
			   Node mapNode = map.getNode(j,i);
			    if(mapNode.hasGoal())
			    	drawGoal(drawable,mapNode);
				else if(mapNode.isWall())
					drawWall(drawable,mapNode);
				else if(mapNode.hasWumpus())
					drawWumpus(drawable,mapNode);
				else if(mapNode.hasPit())
					drawPit(drawable,mapNode);
				else if(mapNode.hasMinion())
					drawMinion(drawable,mapNode);
				else if(mapNode.hasAgent())
					drawAgent(drawable,mapNode);
				else if(mapNode.hasFairy())
					drawFairy(drawable,mapNode);
				else drawFloor(drawable,mapNode);
		   }
	   }
   }
   
   private void drawFloor(GLAutoDrawable drawable, Node mapNode) {
		// TODO Auto-generated method stub
		
	}
   
	private void drawFairy(GLAutoDrawable drawable, Node mapNode) {
		// TODO Auto-generated method stub
		
	}

	private void drawAgent(GLAutoDrawable drawable, Node mapNode) {
		// TODO Auto-generated method stub
		
	}
	
	private void drawMinion(GLAutoDrawable drawable, Node mapNode) {
		// TODO Auto-generated method stub
		
	}
	
	private void drawPit(GLAutoDrawable drawable, Node mapNode) {
		// TODO Auto-generated method stub
		
	}
	
	private void drawWumpus(GLAutoDrawable drawable, Node mapNode) {
		// TODO Auto-generated method stub
		
	}
	
	private void drawWall(GLAutoDrawable drawable, Node mapNode) {
	      // Get the OpenGL graphics context
	      GL2 gl = drawable.getGL().getGL2();
	      gl.glColor3f(0.5f, 0.0f, 0.7f); // green
	      gl.glLoadIdentity();                // reset the current model-view matrix
	      gl.glTranslatef(-mapNode.getX(), -mapNode.getY(), 0.0f); //translate to location on map
	      //gl.glRotatef(angleCube, 1.0f, 1.0f, 1.0f); // rotate about the x, y and z-axes
	      gl.glBegin(GL_QUADS); // of the color cube
	 	 
	      // Top-face
	      gl.glVertex3f(0.5f, 0.5f, -0.5f);
	      gl.glVertex3f(-0.5f, 0.5f, -0.5f);
	      gl.glVertex3f(-0.5f, 0.5f, 0.5f);
	      gl.glVertex3f(0.5f, 0.5f, 0.5f);
	 
	      // Bottom-face
	      gl.glVertex3f(0.5f, -0.5f, 0.5f);
	      gl.glVertex3f(-0.5f, -0.5f, 0.5f);
	      gl.glVertex3f(-0.5f, -0.5f, -0.5f);
	      gl.glVertex3f(0.5f, -0.5f, -0.5f);
	 
	      // Front-face
	      gl.glVertex3f(0.5f, 0.5f, 0.5f);
	      gl.glVertex3f(-0.5f, 0.5f, 0.5f);
	      gl.glVertex3f(-0.5f, -0.5f, 0.5f);
	      gl.glVertex3f(0.5f, -0.5f, 0.5f);
	 
	      // Back-face
	      gl.glVertex3f(0.5f, -0.5f, -0.5f);
	      gl.glVertex3f(-0.5f, -0.5f, -0.5f);
	      gl.glVertex3f(-0.5f, 0.5f, -0.5f);
	      gl.glVertex3f(0.5f, 0.5f, -0.5f);
	 
	      // Left-face
	      gl.glVertex3f(-0.5f, 0.5f, 0.5f);
	      gl.glVertex3f(-0.5f, 0.5f, -0.5f);
	      gl.glVertex3f(-0.5f, -0.5f, -0.5f);
	      gl.glVertex3f(-0.5f, -0.5f, 0.5f);
	 
	      // Right-face
	      gl.glVertex3f(0.5f, 0.5f, -0.5f);
	      gl.glVertex3f(0.5f, 0.5f, 0.5f);
	      gl.glVertex3f(0.5f, -0.5f, 0.5f);
	      gl.glVertex3f(0.5f, -0.5f, -0.5f);
	 
	      gl.glEnd(); // of the color cube
	   }
	
	private void drawGoal(GLAutoDrawable drawable, Node mapNode) {
		// TODO Auto-generated method stub
		
	}
   
   // Update the angle of the triangle after each frame
   private void update() {
   }
 
   /**
    * Called back before the OpenGL context is destroyed. Release resource such as buffers.
    */
   @Override
   public void dispose(GLAutoDrawable drawable) { }
}
