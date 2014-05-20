package WumpusEnvironment.View.MainWindow;

import java.io.IOException;

import javax.media.opengl.*;
import javax.media.opengl.awt.*;
import javax.media.opengl.glu.*;

import com.jogamp.opengl.util.texture.*;

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
	public static final int MAP_VIEW = 0;
	public static final int AGENT_VIEW = 0;
	private int view;
   private GLU glu;  // for the GL Utility
   private float anglePyramid = 0;    // rotational angle in degree for pyramid
   private float angleCube = 0;       // rotational angle in degree for cube
   private float speedPyramid = 2.0f; // rotational speed for pyramid
   private float speedCube = -1.5f;   // rotational speed for cube
   private Grid map;
   
   // Textures
   //wall texture
   private Texture textureWall;
   private String textureWallFileName = "images/wall.gif";
   private String textureWallFileType = ".gif";
	// Texture image flips vertically. Shall use TextureCoords class to retrieve the
   // top, bottom, left and right coordinates.
   private float textureWallTop, textureWallBottom, textureWallLeft, textureWallRight;
   
   //floor texture
   private Texture textureFloor;
   private String textureFloorFileName = "images/floor.gif";
   private String textureFloorFileType = ".gif";
   // Texture image flips vertically. Shall use TextureCoords class to retrieve the
   // top, bottom, left and right coordinates.
   private float textureFloorTop, textureFloorBottom, textureFloorLeft, textureFloorRight;
   
   //pit texture
   private Texture texturePit;
   private String texturePitFileName = "images/pit.gif";
   private String texturePitFileType = ".gif";
   // Texture image flips vertically. Shall use TextureCoords class to retrieve the
   // top, bottom, left and right coordinates.
   private float texturePitTop, texturePitBottom, texturePitLeft, texturePitRight;
 
   /** Constructor to setup the GUI for this Component */
   public MapCanvas(int view) {
      this.addGLEventListener(this);
      map = Grid.getInstance();
      this.view = view;
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
      gl.glClearColor(1.0f, 1.0f, 1.0f, 0.0f); // set background (clear) color
      gl.glClearDepth(1.0f);      // set clear depth value to farthest
      gl.glEnable(GL_DEPTH_TEST); // enables depth testing
      gl.glDepthFunc(GL_LEQUAL);  // the type of depth test to do
      gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST); // best perspective correction
      gl.glShadeModel(GL_SMOOTH); // blends colors nicely, and smoothes out lighting
 
      // ----- Your OpenGL initialization code here -----
      // Load wall texture from image
      try {
         // Create a OpenGL Texture object from (URL, mipmap, file suffix)
         // Use URL so that can read from JAR and disk file.
         textureWall = TextureIO.newTexture(
               getClass().getClassLoader().getResource(textureWallFileName), // relative to project root 
               false, textureWallFileType);

         // Use linear filter for texture if image is larger than the original texture
         gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
         // Use linear filter for texture if image is smaller than the original texture
         gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

         // Texture image flips vertically. Shall use TextureCoords class to retrieve
         // the top, bottom, left and right coordinates, instead of using 0.0f and 1.0f.
         TextureCoords textureCoords = textureWall.getImageTexCoords();
         textureWallTop = textureCoords.top();
         textureWallBottom = textureCoords.bottom();
         textureWallLeft = textureCoords.left();
         textureWallRight = textureCoords.right();
      } catch (GLException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }
      
      // Load floor texture from image
      try {
         // Create a OpenGL Texture object from (URL, mipmap, file suffix)
         // Use URL so that can read from JAR and disk file.
         textureFloor = TextureIO.newTexture(
               getClass().getClassLoader().getResource(textureFloorFileName), // relative to project root 
               false, textureFloorFileType);

         // Use linear filter for texture if image is larger than the original texture
         gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
         // Use linear filter for texture if image is smaller than the original texture
         gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

         // Texture image flips vertically. Shall use TextureCoords class to retrieve
         // the top, bottom, left and right coordinates, instead of using 0.0f and 1.0f.
         TextureCoords textureCoords = textureFloor.getImageTexCoords();
         textureFloorTop = textureCoords.top();
         textureFloorBottom = textureCoords.bottom();
         textureFloorLeft = textureCoords.left();
         textureFloorRight = textureCoords.right();
      } catch (GLException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }
      
      // Load pit texture from image
      try {
         // Create a OpenGL Texture object from (URL, mipmap, file suffix)
         // Use URL so that can read from JAR and disk file.
         texturePit = TextureIO.newTexture(
               getClass().getClassLoader().getResource(texturePitFileName), // relative to project root 
               false, texturePitFileType);

         // Use linear filter for texture if image is larger than the original texture
         gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
         // Use linear filter for texture if image is smaller than the original texture
         gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
         gl.glGenerateMipmap(GL_TEXTURE_2D);

         // Texture image flips vertically. Shall use TextureCoords class to retrieve
         // the top, bottom, left and right coordinates, instead of using 0.0f and 1.0f.
         TextureCoords textureCoords = texturePit.getImageTexCoords();
         texturePitTop = textureCoords.top();
         texturePitBottom = textureCoords.bottom();
         texturePitLeft = textureCoords.left();
         texturePitRight = textureCoords.right();
      } catch (GLException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }
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
	      update();
	 
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
			   drawNode(drawable,mapNode);
		   }
	   }
   }
   
   private void drawNode(GLAutoDrawable drawable, Node mapNode){
	    if(mapNode.hasGoal()){
	    	drawGoal(drawable,mapNode);
	    	drawFloor(drawable,mapNode);
	    }
		else if(mapNode.isWall()){
			drawWall(drawable,mapNode);
		}
		else if(mapNode.hasWumpus()){
			drawWumpus(drawable,mapNode);
			drawFloor(drawable,mapNode);
		}
		else if(mapNode.hasPit()){
			drawPit(drawable,mapNode);
		}
		else if(mapNode.hasMinion()){
			drawMinion(drawable,mapNode);
			drawFloor(drawable,mapNode);
		}
		else if(mapNode.hasAgent()){
			drawAgent(drawable,mapNode);
			drawFloor(drawable,mapNode);
		}
		else if(mapNode.hasFairy()){
			drawFairy(drawable,mapNode);
			drawFloor(drawable,mapNode);
		}
		else drawFloor(drawable,mapNode);
   }
   
   private void drawFloor(GLAutoDrawable drawable, Node mapNode) {
	   GL2 gl = drawable.getGL().getGL2();
	   textureFloor.enable(gl);
	   textureFloor.bind(gl);
	   gl.glPushMatrix();
	   gl.glLoadIdentity();                // reset the current model-view matrix
	   gl.glColor3f(1.0f, 1.0f, 1.0f);
       gl.glTranslatef(-mapNode.getX(), -mapNode.getY(), 0.0f); //translate to location on map
       gl.glBegin(GL_QUADS); // of the color cube
	      gl.glTexCoord2f(textureWallLeft, textureWallBottom);
	      gl.glVertex3f(-0.5f, -0.5f, 0.5f); // bottom-left of the texture and quad
	      gl.glTexCoord2f(textureWallRight, textureWallBottom);
	      gl.glVertex3f(0.5f, -0.5f, 0.5f);  // bottom-right of the texture and quad
	      gl.glTexCoord2f(textureWallRight, textureWallTop);
	      gl.glVertex3f(0.5f, 0.5f, 0.5f);   // top-right of the texture and quad
	      gl.glTexCoord2f(textureWallLeft, textureWallTop);
	      gl.glVertex3f(-0.5f, 0.5f, 0.5f);  // top-left of the texture and quad
	   gl.glEnd();
	   textureFloor.disable(gl);
	   gl.glPopMatrix();
		
	}
   
	private void drawFairy(GLAutoDrawable drawable, Node mapNode) {
		// Get the OpenGL graphics context
		GL2 gl = drawable.getGL().getGL2();
		gl.glPushMatrix();
		gl.glColor3f(0.0f, 0.4f, 0.6f);
	    gl.glLoadIdentity(); // reset the current model-view matrix
	    gl.glTranslatef(-mapNode.getX(), -mapNode.getY(), 0.0f); //translate to location on map
		GLUquadric quad = glu.gluNewQuadric();
		glu.gluQuadricDrawStyle(quad, GLU.GLU_FILL);
        glu.gluQuadricNormals(quad, GLU.GLU_FLAT);
        glu.gluQuadricOrientation(quad, GLU.GLU_OUTSIDE);
		glu.gluSphere(quad, 0.43, 10, 15);
		glu.gluDeleteQuadric(quad);	
		gl.glPopMatrix();
		
	}

	private void drawAgent(GLAutoDrawable drawable, Node mapNode) {
		// Get the OpenGL graphics context
		GL2 gl = drawable.getGL().getGL2();
		gl.glPushMatrix();
		gl.glColor3f(0.5f, 0.0f, 0.7f);
	    gl.glLoadIdentity();                // reset the current model-view matrix
	    gl.glTranslatef(-mapNode.getX(), -mapNode.getY(), 0.0f); //translate to location on map
		GLUquadric quad = glu.gluNewQuadric();
		glu.gluQuadricDrawStyle(quad, GLU.GLU_FILL);
        glu.gluQuadricNormals(quad, GLU.GLU_FLAT);
        glu.gluQuadricOrientation(quad, GLU.GLU_OUTSIDE);
		glu.gluSphere(quad, 0.43, 10, 15);
		glu.gluDeleteQuadric(quad);	
		gl.glPopMatrix();
	}
	
	private void drawMinion(GLAutoDrawable drawable, Node mapNode) {
		// Get the OpenGL graphics context
		GL2 gl = drawable.getGL().getGL2();
		gl.glPushMatrix();
		gl.glColor3f(0.0f, 0.8f, 0.0f);
	    gl.glLoadIdentity();                // reset the current model-view matrix
	    gl.glTranslatef(-mapNode.getX(), -mapNode.getY(), 0.0f); //translate to location on map
		GLUquadric quad = glu.gluNewQuadric();
		glu.gluQuadricDrawStyle(quad, GLU.GLU_FILL);
        glu.gluQuadricNormals(quad, GLU.GLU_FLAT);
        glu.gluQuadricOrientation(quad, GLU.GLU_OUTSIDE);
		glu.gluSphere(quad, 0.43, 10, 15);
		glu.gluDeleteQuadric(quad);	
		gl.glPopMatrix();		
	}
	
	private void drawPit(GLAutoDrawable drawable, Node mapNode) {
	   GL2 gl = drawable.getGL().getGL2();
	   texturePit.enable(gl);
	   texturePit.bind(gl);
	   gl.glPushMatrix();
	   gl.glLoadIdentity();                // reset the current model-view matrix
	   gl.glColor3f(1.0f, 1.0f, 1.0f);
	   gl.glTranslatef(-mapNode.getX(), -mapNode.getY(), 0.0f); //translate to location on map
       gl.glBegin(GL_QUADS); // of the color cube
	      gl.glTexCoord2f(textureWallLeft, textureWallBottom);
	      gl.glVertex3f(-0.5f, -0.5f, 0.5f); // bottom-left of the texture and quad
	      gl.glTexCoord2f(textureWallRight, textureWallBottom);
	      gl.glVertex3f(0.5f, -0.5f, 0.5f);  // bottom-right of the texture and quad
	      gl.glTexCoord2f(textureWallRight, textureWallTop);
	      gl.glVertex3f(0.5f, 0.5f, 0.5f);   // top-right of the texture and quad
	      gl.glTexCoord2f(textureWallLeft, textureWallTop);
	      gl.glVertex3f(-0.5f, 0.5f, 0.5f);  // top-left of the texture and quad
	   gl.glEnd();
	   texturePit.disable(gl);
	   gl.glPopMatrix();
		
	}
	
	private void drawWumpus(GLAutoDrawable drawable, Node mapNode) {
		// Get the OpenGL graphics context
		GL2 gl = drawable.getGL().getGL2();
		gl.glPushMatrix();
		gl.glColor3f(0.7f, 0.0f, 0.0f);
	    gl.glLoadIdentity();                // reset the current model-view matrix
	    gl.glTranslatef(-mapNode.getX(), -mapNode.getY(), 0.0f); //translate to location on map
		GLUquadric quad = glu.gluNewQuadric();
		glu.gluQuadricDrawStyle(quad, GLU.GLU_FILL);
        glu.gluQuadricNormals(quad, GLU.GLU_FLAT);
        glu.gluQuadricOrientation(quad, GLU.GLU_OUTSIDE);
		glu.gluSphere(quad, 0.43, 10, 15);
		glu.gluDeleteQuadric(quad);	
		gl.glPopMatrix();
		
	}
	
	private void drawWall(GLAutoDrawable drawable, Node mapNode) {
	      // Get the OpenGL graphics context
	      GL2 gl = drawable.getGL().getGL2();
	      gl.glPushMatrix();
	      
	      // Enables this texture's target in the current GL context's state.
	      textureWall.enable(gl);  // same as gl.glEnable(texture.getTarget());
	      // gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_REPLACE);
	      // Binds this texture to the current GL context.
	      textureWall.bind(gl);  // same as gl.glBindTexture(texture.getTarget(), texture.getTextureObject());
	      
	      gl.glColor3f(0.7f, 0.0f, 0.0f);
	      gl.glLoadIdentity();                // reset the current model-view matrix
	      gl.glTranslatef(-mapNode.getX(), -mapNode.getY(), 0.0f); //translate to location on map
	      //gl.glRotatef(angleCube, 1.0f, 1.0f, 1.0f); // rotate about the x, y and z-axes
	      gl.glBegin(GL_QUADS); // of the color cube
	 	 
	      // Front Face
	      gl.glTexCoord2f(textureWallLeft, textureWallBottom);
	      gl.glVertex3f(-0.5f, -0.5f, 0.5f); // bottom-left of the texture and quad
	      gl.glTexCoord2f(textureWallRight, textureWallBottom);
	      gl.glVertex3f(0.5f, -0.5f, 0.5f);  // bottom-right of the texture and quad
	      gl.glTexCoord2f(textureWallRight, textureWallTop);
	      gl.glVertex3f(0.5f, 0.5f, 0.5f);   // top-right of the texture and quad
	      gl.glTexCoord2f(textureWallLeft, textureWallTop);
	      gl.glVertex3f(-0.5f, 0.5f, 0.5f);  // top-left of the texture and quad

	      // Back Face
	      gl.glTexCoord2f(textureWallRight, textureWallBottom);
	      gl.glVertex3f(-0.5f, -0.5f, -0.5f);
	      gl.glTexCoord2f(textureWallRight, textureWallTop);
	      gl.glVertex3f(-0.5f, 0.5f, -0.5f);
	      gl.glTexCoord2f(textureWallLeft, textureWallTop);
	      gl.glVertex3f(0.5f, 0.5f, -0.5f);
	      gl.glTexCoord2f(textureWallLeft, textureWallBottom);
	      gl.glVertex3f(0.5f, -0.5f, -0.5f);
	      
	      // Top Face
	      gl.glTexCoord2f(textureWallLeft, textureWallTop);
	      gl.glVertex3f(-0.5f, 0.5f, -0.5f);
	      gl.glTexCoord2f(textureWallLeft, textureWallBottom);
	      gl.glVertex3f(-0.5f, 0.5f, 0.5f);
	      gl.glTexCoord2f(textureWallRight, textureWallBottom);
	      gl.glVertex3f(0.5f, 0.5f, 0.5f);
	      gl.glTexCoord2f(textureWallRight, textureWallTop);
	      gl.glVertex3f(0.5f, 0.5f, -0.5f);
	      
	      // Bottom Face
	      gl.glTexCoord2f(textureWallRight, textureWallTop);
	      gl.glVertex3f(-0.5f, -0.5f, -0.5f);
	      gl.glTexCoord2f(textureWallLeft, textureWallTop);
	      gl.glVertex3f(0.5f, -0.5f, -0.5f);
	      gl.glTexCoord2f(textureWallLeft, textureWallBottom);
	      gl.glVertex3f(0.5f, -0.5f, 0.5f);
	      gl.glTexCoord2f(textureWallRight, textureWallBottom);
	      gl.glVertex3f(-0.5f, -0.5f, 0.5f);
	      
	      // Right face
	      gl.glTexCoord2f(textureWallRight, textureWallBottom);
	      gl.glVertex3f(0.5f, -0.5f, -0.5f);
	      gl.glTexCoord2f(textureWallRight, textureWallTop);
	      gl.glVertex3f(0.5f, 0.5f, -0.5f);
	      gl.glTexCoord2f(textureWallLeft, textureWallTop);
	      gl.glVertex3f(0.5f, 0.5f, 0.5f);
	      gl.glTexCoord2f(textureWallLeft, textureWallBottom);
	      gl.glVertex3f(0.5f, -0.5f, 0.5f);
	      
	      // Left Face
	      gl.glTexCoord2f(textureWallLeft, textureWallBottom);
	      gl.glVertex3f(-0.5f, -0.5f, -0.5f);
	      gl.glTexCoord2f(textureWallRight, textureWallBottom);
	      gl.glVertex3f(-0.5f, -0.5f, 0.5f);
	      gl.glTexCoord2f(textureWallRight, textureWallTop);
	      gl.glVertex3f(-0.5f, 0.5f, 0.5f);
	      gl.glTexCoord2f(textureWallLeft, textureWallTop);
	      gl.glVertex3f(-0.5f, 0.5f, -0.5f);
	 
	      gl.glEnd(); // of the color cube
	      // Disables this texture's target (e.g., GL_TEXTURE_2D) in the current GL
	      // context's state.
	      textureWall.disable(gl);  // same as gl.glDisable(texture.getTarget());
	      gl.glPopMatrix();
	   }
	
	private void drawGoal(GLAutoDrawable drawable, Node mapNode) {
		// Get the OpenGL graphics context
	      GL2 gl = drawable.getGL().getGL2();
	      gl.glPushMatrix();
		  gl.glLoadIdentity();                 // reset the model-view matrix
	      gl.glTranslatef(-mapNode.getX(), -mapNode.getY(), 0.0f); // translate
	      gl.glRotatef(anglePyramid, -0.2f, 0.0f, 1.0f); // rotate about the z-axis
	      gl.glRotatef(90, 1.0f, 0.0f, 0.0f); //flip so tip faces camera
	      gl.glScalef(0.25f, 0.25f, 0.25f);
	 
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
	 
	      gl.glEnd(); // of the pyramid
	      gl.glPopMatrix();
	}
   
   private void update() {
	   anglePyramid += speedPyramid;
   }
 
   /**
    * Called back before the OpenGL context is destroyed. Release resource such as buffers.
    */
   @Override
   public void dispose(GLAutoDrawable drawable) { }
}
