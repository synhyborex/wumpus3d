package WumpusEnvironment.View.MainWindow;

import static javax.media.opengl.GL.*;
import static javax.media.opengl.GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT;
import static javax.media.opengl.GL2GL3.GL_QUADS;
import static javax.media.opengl.fixedfunc.GLLightingFunc.*;
import static javax.media.opengl.fixedfunc.GLMatrixFunc.GL_MODELVIEW;
import static javax.media.opengl.fixedfunc.GLMatrixFunc.GL_PROJECTION;

import java.awt.event.*;
import java.io.IOException;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLException;
import javax.media.opengl.awt.GLJPanel;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import WumpusEnvironment.Model.Agent.*;
import WumpusEnvironment.Model.Map.*;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;
import com.jogamp.opengl.util.texture.TextureIO;
// GL constants
// GL2 constants
 
/**
 * JOGL 2.0 Program Template (GLCanvas)
 * This is a "Component" which can be added into a top-level "Container".
 * It also handles the OpenGL events to render graphics.
 */
@SuppressWarnings("serial")
public class MapCanvas extends GLJPanel implements GLEventListener, MouseListener, MouseMotionListener, MouseWheelListener {
   private GLU glu;  // for the GL Utility
   private float anglePyramid = 0;    // rotational angle in degree for pyramid
   private Grid map;
   
   //camera variables
   //set up camera
   int mapWidth, mapHeight;
   int distance; //how far away the camera is
   float[] eyePos;
   boolean zoomChanged = false;
   boolean rotChanged = false;
   private int startX, startY;
   private float radius, circleAngle;
   private float[] midPoint, upDownAngle;
   
   // Textures
   //wall texture
   private Texture textureWall;
   private String textureWallFileName = "images/legacy/wall.gif";
   private String textureWallFileType = ".gif";
	// Texture image flips vertically. Shall use TextureCoords class to retrieve the
   // top, bottom, left and right coordinates.
   private float textureWallTop, textureWallBottom, textureWallLeft, textureWallRight;
   
   //floor texture
   private Texture textureFloor, textureFloorEvaluated;
   private String textureFloorFileName = "images/legacy/floor.gif";
   private String textureFloorFileType = ".gif";
   // Texture image flips vertically. Shall use TextureCoords class to retrieve the
   // top, bottom, left and right coordinates.
   private float textureFloorTop, textureFloorBottom, textureFloorLeft, textureFloorRight;
   
   //belief textures
   private Texture textureKnowGold, textureThinkGold, textureKnowWumpus, textureThinkWumpus,
   					textureKnowMinion, textureThinkMinion, textureKnowPit, textureThinkPit,
   					textureKnowSafe, textureThinkSafe, textureKnowWall;
   private String textureBeliefFileName = "images/legacy/know_gold.gif";
   private String textureBeliefFileType = ".gif";
   // Texture image flips vertically. Shall use TextureCoords class to retrieve the
   // top, bottom, left and right coordinates.
   private float textureBeliefTop, textureBeliefBottom, textureBeliefLeft, textureBeliefRight;
   
   //pit texture
   private Texture texturePit;
   private String texturePitFileName = "images/legacy/pit.gif";
   private String texturePitFileType = ".gif";
   // Texture image flips vertically. Shall use TextureCoords class to retrieve the
   // top, bottom, left and right coordinates.
   private float texturePitTop, texturePitBottom, texturePitLeft, texturePitRight;
   
   //Gold texture
   private Texture textureGold;
   private String textureGoldFileName = "images/gold.png";
   private String textureGoldFileType = ".png";
   // Texture image flips vertically. Shall use TextureCoords class to retrieve the
   // top, bottom, left and right coordinates.
   private float textureGoldTop, textureGoldBottom, textureGoldLeft, textureGoldRight;
   
   //dead enemy texture
   private Texture textureDeadWumpus;
   private Texture textureDeadMinion;
   private String textureDeadEnemyFileName = "images/dead_wumpus.png";
   private String textureDeadEnemyFileType = ".png";
   
   //agent texture
   private Texture textureAgent;
   private String textureAgentFileName = "images/agent.png";
   private String textureAgentFileType = ".png";
   
 //agent texture
   private Texture textureFairy;
   private String textureFairyFileName = "images/fairy.png";
   private String textureFairyFileType = ".png";
   
   //Wumpus texture
   private Texture textureWumpus;
   private String textureWumpusFileName = "images/wumpus.png";
   private String textureWumpusFileType = ".png";
   
   //Wumpus texture
   private Texture textureMinion;
   private String textureMinionFileName = "images/minion.png";
   private String textureMinionFileType = ".png";
 
   /** Constructor to setup the GUI for this Component */
   public MapCanvas() {
      this.addGLEventListener(this);
      this.addMouseListener(this);
      this.addMouseMotionListener(this);
      this.addMouseWheelListener(this);
      map = Grid.getInstance();
      
      //camera stuff
      int mapWidth = map.getWidth(), mapHeight = map.getHeight();
      int distance = -(mapWidth+mapHeight/2);
      midPoint = new float[3];
      //midPoint[0] = -mapWidth/2;
      //midPoint[1] = -mapHeight/2;
      midPoint[0] = 0f;
      midPoint[1] = 0f;
      midPoint[2] = -0.5f;
      
      eyePos = new float[3];
      eyePos[0] = midPoint[0];
      eyePos[1] = midPoint[1]+(distance/3);
      eyePos[2] = distance/1.05f;
      circleAngle = 0;
      upDownAngle = new float[2];
      upDownAngle[0] = 0f;
      upDownAngle[1] = 0f;
      radius = vectorLength(eyePos);
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
         
         textureFloorFileName = "images/floor_evaluated.png";
         textureFloorFileType = ".png";
         textureFloorEvaluated = TextureIO.newTexture(
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
      
      // Load belief textures from image
      try {
         // Create a OpenGL Texture object from (URL, mipmap, file suffix)
         // Use URL so that can read from JAR and disk file.
         textureKnowGold = TextureIO.newTexture(
               getClass().getClassLoader().getResource(textureBeliefFileName), // relative to project root 
               false, textureBeliefFileType);
         
         textureBeliefFileName = "images/legacy/think_gold.gif";
         textureThinkGold = TextureIO.newTexture(
                 getClass().getClassLoader().getResource(textureBeliefFileName), // relative to project root 
                 false, textureBeliefFileType);
         
         textureBeliefFileName = "images/legacy/know_wumpus.gif";
         textureKnowWumpus = TextureIO.newTexture(
                 getClass().getClassLoader().getResource(textureBeliefFileName), // relative to project root 
                 false, textureBeliefFileType);
         
         textureBeliefFileName = "images/legacy/think_wumpus.gif";
         textureThinkWumpus = TextureIO.newTexture(
                 getClass().getClassLoader().getResource(textureBeliefFileName), // relative to project root 
                 false, textureBeliefFileType);
         
         textureBeliefFileName = "images/legacy/know_minion.gif";
         textureKnowMinion = TextureIO.newTexture(
                 getClass().getClassLoader().getResource(textureBeliefFileName), // relative to project root 
                 false, textureBeliefFileType);
         
         textureBeliefFileName = "images/legacy/think_minion.gif";
         textureThinkMinion = TextureIO.newTexture(
                 getClass().getClassLoader().getResource(textureBeliefFileName), // relative to project root 
                 false, textureBeliefFileType);
         
         textureBeliefFileName = "images/legacy/know_pit.gif";
         textureKnowPit = TextureIO.newTexture(
                 getClass().getClassLoader().getResource(textureBeliefFileName), // relative to project root 
                 false, textureBeliefFileType);
         
         textureBeliefFileName = "images/legacy/think_pit.gif";
         textureThinkPit = TextureIO.newTexture(
                 getClass().getClassLoader().getResource(textureBeliefFileName), // relative to project root 
                 false, textureBeliefFileType);
         
         textureBeliefFileName = "images/legacy/know_safe.gif";
         textureKnowSafe = TextureIO.newTexture(
                 getClass().getClassLoader().getResource(textureBeliefFileName), // relative to project root 
                 false, textureBeliefFileType);
         
         textureBeliefFileName = "images/legacy/think_safe.gif";
         textureThinkSafe = TextureIO.newTexture(
                 getClass().getClassLoader().getResource(textureBeliefFileName), // relative to project root 
                 false, textureBeliefFileType);
         
         textureBeliefFileName = "images/legacy/know_wall.gif";
         textureKnowWall = TextureIO.newTexture(
                 getClass().getClassLoader().getResource(textureBeliefFileName), // relative to project root 
                 false, textureBeliefFileType);

         // Use linear filter for texture if image is larger than the original texture
         gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
         // Use linear filter for texture if image is smaller than the original texture
         gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

         // Texture image flips vertically. Shall use TextureCoords class to retrieve
         // the top, bottom, left and right coordinates, instead of using 0.0f and 1.0f.
         TextureCoords textureCoords = textureKnowGold.getImageTexCoords();
         textureBeliefTop = textureCoords.top();
         textureBeliefBottom = textureCoords.bottom();
         textureBeliefLeft = textureCoords.right();
         textureBeliefRight = textureCoords.left();
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
      
      // Load gold texture from image
      try {
         // Create a OpenGL Texture object from (URL, mipmap, file suffix)
         // Use URL so that can read from JAR and disk file.
         textureGold = TextureIO.newTexture(
               getClass().getClassLoader().getResource(textureGoldFileName), // relative to project root 
               false, textureGoldFileType);

         // Use linear filter for texture if image is larger than the original texture
         gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
         // Use linear filter for texture if image is smaller than the original texture
         gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
         gl.glGenerateMipmap(GL_TEXTURE_2D);

         // Texture image flips vertically. Shall use TextureCoords class to retrieve
         // the top, bottom, left and right coordinates, instead of using 0.0f and 1.0f.
         TextureCoords textureCoords = textureGold.getImageTexCoords();
         textureGoldTop = textureCoords.top();
         textureGoldBottom = textureCoords.bottom();
         textureGoldLeft = textureCoords.left();
         textureGoldRight = textureCoords.right();
      } catch (GLException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }
      
      // Load dead enemy texture from image
      try {
         // Create a OpenGL Texture object from (URL, mipmap, file suffix)
         // Use URL so that can read from JAR and disk file.
         textureDeadWumpus = TextureIO.newTexture(
               getClass().getClassLoader().getResource(textureDeadEnemyFileName), // relative to project root 
               false, textureDeadEnemyFileType);
         
         textureDeadEnemyFileName = "images/dead_minion.png";
         textureDeadMinion = TextureIO.newTexture(
                 getClass().getClassLoader().getResource(textureDeadEnemyFileName), // relative to project root 
                 false, textureDeadEnemyFileType);

         // Use linear filter for texture if image is larger than the original texture
         gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
         // Use linear filter for texture if image is smaller than the original texture
         gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
         gl.glGenerateMipmap(GL_TEXTURE_2D);
      } catch (GLException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }
      
      // Load agent texture from image
      try {
         // Create a OpenGL Texture object from (URL, mipmap, file suffix)
         // Use URL so that can read from JAR and disk file.
         textureAgent = TextureIO.newTexture(
               getClass().getClassLoader().getResource(textureAgentFileName), // relative to project root 
               false, textureAgentFileType);

         // Use linear filter for texture if image is larger than the original texture
         gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
         // Use linear filter for texture if image is smaller than the original texture
         gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
         gl.glGenerateMipmap(GL_TEXTURE_2D);
      } catch (GLException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }
      
      // Load fairy texture from image
      try {
         // Create a OpenGL Texture object from (URL, mipmap, file suffix)
         // Use URL so that can read from JAR and disk file.
         textureFairy = TextureIO.newTexture(
               getClass().getClassLoader().getResource(textureFairyFileName), // relative to project root 
               false, textureFairyFileType);

         // Use linear filter for texture if image is larger than the original texture
         gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
         // Use linear filter for texture if image is smaller than the original texture
         gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
         gl.glGenerateMipmap(GL_TEXTURE_2D);
      } catch (GLException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }
      
      // Load Wumpus texture from image
      try {
         // Create a OpenGL Texture object from (URL, mipmap, file suffix)
         // Use URL so that can read from JAR and disk file.
         textureWumpus = TextureIO.newTexture(
               getClass().getClassLoader().getResource(textureWumpusFileName), // relative to project root 
               false, textureWumpusFileType);

         // Use linear filter for texture if image is larger than the original texture
         gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
         // Use linear filter for texture if image is smaller than the original texture
         gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
         gl.glGenerateMipmap(GL_TEXTURE_2D);
      } catch (GLException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }
      
      // Load minion texture from image
      try {
         // Create a OpenGL Texture object from (URL, mipmap, file suffix)
         // Use URL so that can read from JAR and disk file.
         textureMinion = TextureIO.newTexture(
               getClass().getClassLoader().getResource(textureMinionFileName), // relative to project root 
               false, textureMinionFileType);

         // Use linear filter for texture if image is larger than the original texture
         gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
         // Use linear filter for texture if image is smaller than the original texture
         gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
         gl.glGenerateMipmap(GL_TEXTURE_2D);
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
      //set camera
      glu.gluLookAt(//eye
    		  				eyePos[0], eyePos[1], eyePos[2],
    		  		//center
	    		  			midPoint[0], midPoint[1], midPoint[2],
    		  		//up
	    		  			0, 1, 0);
 
      // Enable the model-view transform
      gl.glMatrixMode(GL_MODELVIEW);
      gl.glLoadIdentity(); // reset
      
      // Prepare light parameters.
      float SHINE_ALL_DIRECTIONS = 1;
      float[] lightPos = {eyePos[0]*2, eyePos[1]*2, -radius, SHINE_ALL_DIRECTIONS};
      float[] lightColorAmbient = {0.3f, 0.3f, 0.3f, 1f};
      float[] lightColorDiffuse = {0.2f, 0.2f, 0.2f, 1f};
      float[] lightColorSpecular = {0.4f, 0.4f, 0.4f, 1f};

      // Set light parameters.
      gl.glLightfv(GL_LIGHT1, GL_POSITION, lightPos, 0);
      gl.glLightfv(GL_LIGHT1, GL_AMBIENT, lightColorAmbient, 0);
      gl.glLightfv(GL_LIGHT1, GL_DIFFUSE, lightColorDiffuse, 0);
      gl.glLightfv(GL_LIGHT1, GL_SPECULAR, lightColorSpecular, 0);

      // Enable lighting in 
      gl.glEnable(GL_LIGHT1);
      gl.glEnable(GL_LIGHTING);

      // Set material properties.
      float[] rgba = {1.0f, 1.0f, 1.0f};
      gl.glMaterialfv(GL_FRONT, GL_AMBIENT, rgba, 0);
      gl.glMaterialfv(GL_FRONT, GL_DIFFUSE, rgba, 0);
      gl.glMaterialfv(GL_FRONT, GL_SPECULAR, rgba, 0);
      gl.glMaterialf(GL_FRONT, GL_SHININESS, 0.5f);
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
	      if(zoomChanged || rotChanged){
	    	  if(zoomChanged)
	    		  zoomChanged = false;
	    	  if(rotChanged)
	    		  rotChanged = false;
	    	  reshape(drawable, 0, 0, this.getWidth(), this.getHeight());
	      }
   }
   
   private void drawMap(GLAutoDrawable drawable){
	   GL2 gl = drawable.getGL().getGL2();
	   //apply transformations to world
	   gl.glLoadIdentity();
	   gl.glRotatef(-circleAngle, 0f, 0f, 1f);
	   gl.glTranslatef((float)map.getWidth()/2f, (float)map.getHeight()/2f, 0f);
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
	    }
		if(mapNode.isWall()){
			drawWall(drawable,mapNode);
		}
		if(mapNode.hasWumpus()){
			if(mapNode.getWumpusStatus() != Node.DEAD)
				drawWumpus(drawable,mapNode);
			else drawDeadWumpus(drawable,mapNode);
		}
		if(mapNode.hasPit()){
			drawPit(drawable,mapNode);
		}
		if(mapNode.hasMinion()){
			if(mapNode.getMinionStatus() != Node.DEAD)
				drawMinion(drawable,mapNode);
			else drawDeadMinion(drawable,mapNode);
		}
		if(mapNode.hasFairy() && !(mapNode.hasGoal() || mapNode.hasAgent())){
			drawFairy(drawable,mapNode);
		}
		if(mapNode.hasAgent()){
			drawAgent(drawable,mapNode);
		}
		if(!mapNode.isWall() && !mapNode.hasPit()){
			drawFloor(drawable,mapNode);
		}
		//draw beliefs for all nodes
		drawBelief(drawable,mapNode);
   }
   
   private void drawFloor(GLAutoDrawable drawable, Node mapNode) {
	   GL2 gl = drawable.getGL().getGL2();
	   textureFloor.enable(gl);
	   textureFloorEvaluated.enable(gl);
	   gl.glPushMatrix();
	   //gl.glLoadIdentity();                // reset the current model-view matrix
	   if(mapNode.isEvaluated()) //show what the agent has knowledge of
		   textureFloorEvaluated.bind(gl);
	   else textureFloor.bind(gl);
       gl.glTranslatef(-mapNode.getX(), -mapNode.getY(), 0.0f); //translate to location on map
       gl.glNormal3f(0f,0f,-1f);
       gl.glBegin(GL_QUADS); // of the color cube
	      gl.glTexCoord2f(textureFloorLeft, textureFloorBottom);
	      gl.glVertex3f(-0.5f, -0.5f, 0.5f); // bottom-left of the texture and quad
	      gl.glTexCoord2f(textureFloorRight, textureFloorBottom);
	      gl.glVertex3f(0.5f, -0.5f, 0.5f);  // bottom-right of the texture and quad
	      gl.glTexCoord2f(textureFloorRight, textureFloorTop);
	      gl.glVertex3f(0.5f, 0.5f, 0.5f);   // top-right of the texture and quad
	      gl.glTexCoord2f(textureFloorLeft, textureFloorTop);
	      gl.glVertex3f(-0.5f, 0.5f, 0.5f);  // top-left of the texture and quad
	   gl.glEnd();
	   textureFloor.disable(gl);
	   textureFloorEvaluated.disable(gl);
	   gl.glPopMatrix();
	}
   
   private void drawBelief(GLAutoDrawable drawable, Node mapNode) {
	  //gl.glLoadIdentity();
	   GL2 gl = drawable.getGL().getGL2();
	   textureKnowGold.enable(gl);
	   textureThinkGold.enable(gl);
	   textureKnowWumpus.enable(gl);
	   textureThinkWumpus.enable(gl);
	   textureKnowPit.enable(gl);
	   textureThinkPit.enable(gl);
	   textureKnowMinion.enable(gl);
	   textureThinkMinion.enable(gl);
	   textureKnowSafe.enable(gl);
	   textureThinkSafe.enable(gl);
	   textureKnowWall.enable(gl);
	   /*textRenderer.begin3DRendering();
	    // optionally set the color
	    textRenderer.setColor(1.0f, 0.2f, 0.2f, 0.8f);
	    textRenderer.draw3D("Text to draw", drawable.getWidth()/2f, drawable.getHeight()/2f,-5f, 1f);
	    // ... more draw commands, color changes, etc.
	    textRenderer.flush();
	    textRenderer.end3DRendering();*/
	   boolean loaded = true;
	   //priority order in order of if/else list
	   if(mapNode.getBelief(Agent.WALL_HERE) == Agent.YES) textureKnowWall.bind(gl);
	   else if(mapNode.getBelief(Agent.GOLD_HERE) == Agent.YES) textureKnowGold.bind(gl);
	   else if(mapNode.getBelief(Agent.GOLD_HERE) == Agent.MAYBE) textureThinkGold.bind(gl);
	   else if(mapNode.getBelief(Agent.WUMPUS_HERE) == Agent.YES) textureKnowWumpus.bind(gl);
	   else if(mapNode.getBelief(Agent.WUMPUS_HERE) == Agent.MAYBE) textureThinkWumpus.bind(gl);
	   else if(mapNode.getBelief(Agent.PIT_HERE) == Agent.YES) textureKnowPit.bind(gl);
	   else if(mapNode.getBelief(Agent.PIT_HERE) == Agent.MAYBE) textureThinkPit.bind(gl);
	   else if(mapNode.getBelief(Agent.MINION_HERE) == Agent.YES) textureKnowMinion.bind(gl);
	   else if(mapNode.getBelief(Agent.MINION_HERE) == Agent.MAYBE) textureThinkMinion.bind(gl);
	   else if(mapNode.getBelief(Agent.SAFE_HERE) == Agent.YES) textureKnowSafe.bind(gl);
	   else if(mapNode.getBelief(Agent.SAFE_HERE) == Agent.MAYBE) textureThinkSafe.bind(gl);
	   else loaded = false;
	   
	   if(loaded){
		   gl.glPushMatrix();
		   gl.glTranslatef(-mapNode.getX(), -mapNode.getY(), -0.75f); //translate to location on map
		   gl.glScalef(0.6f, 0.6f, 0.6f);
	       gl.glNormal3f(0f,0f,-1f);
		   gl.glBegin(GL_QUADS); // of the color cube
		      gl.glTexCoord2f(textureBeliefLeft, textureBeliefBottom);
		      gl.glVertex3f(-0.5f, -0.5f, 0.5f); // bottom-left of the texture and quad
		      gl.glTexCoord2f(textureBeliefRight, textureBeliefBottom);
		      gl.glVertex3f(0.5f, -0.5f, 0.5f);  // bottom-right of the texture and quad
		      gl.glTexCoord2f(textureBeliefRight, textureBeliefTop);
		      gl.glVertex3f(0.5f, 0.5f, 0.5f);   // top-right of the texture and quad
		      gl.glTexCoord2f(textureBeliefLeft, textureBeliefTop);
		      gl.glVertex3f(-0.5f, 0.5f, 0.5f);  // top-left of the texture and quad
		   gl.glEnd();
		    gl.glPopMatrix();
	   }
	   textureKnowGold.disable(gl);
	   textureThinkGold.disable(gl);
	   textureKnowWumpus.disable(gl);
	   textureThinkWumpus.disable(gl);
	   textureKnowPit.disable(gl);
	   textureThinkPit.disable(gl);
	   textureKnowMinion.disable(gl);
	   textureThinkMinion.disable(gl);
	   textureKnowSafe.disable(gl);
	   textureThinkSafe.disable(gl);
	   textureKnowWall.disable(gl);
	}
   
	private void drawFairy(GLAutoDrawable drawable, Node mapNode) {
		// Get the OpenGL graphics context
		GL2 gl = drawable.getGL().getGL2();
		textureFairy.enable(gl);
		textureFairy.bind(gl);
		gl.glPushMatrix();
		gl.glColor3f(0.0f, 0.4f, 0.6f);
	    //gl.glLoadIdentity(); // reset the current model-view matrix
	    gl.glTranslatef(-mapNode.getX(), -mapNode.getY(), 0.0f); //translate to location on map
	    gl.glRotatef(25,1f,0f,0f); //rotate a little upwards
		GLUquadric quad = glu.gluNewQuadric();
		glu.gluQuadricTexture(quad,true);
		glu.gluQuadricDrawStyle(quad, GLU.GLU_FILL);
        glu.gluQuadricNormals(quad, GLU.GLU_FLAT);
        glu.gluQuadricOrientation(quad, GLU.GLU_OUTSIDE);
		glu.gluSphere(quad, 0.43, 25, 25);
		glu.gluDeleteQuadric(quad);
		gl.glPopMatrix();
		textureFairy.disable(gl);
	}

	private void drawAgent(GLAutoDrawable drawable, Node mapNode) {
		// Get the OpenGL graphics context
		GL2 gl = drawable.getGL().getGL2();
		textureAgent.enable(gl);
		textureAgent.bind(gl);
		gl.glPushMatrix();
	    //gl.glLoadIdentity();                // reset the current model-view matrix
	    gl.glTranslatef(-mapNode.getX(), -mapNode.getY(), 0.0f); //translate to location on map
	    //gl.glRotatef(25,1f,0f,0f); //rotate a little upwards
	    
	    //rotate to face the Agent's current heading
	    int rot = 30; //the base turn amount to face initial camera, or SOUTH
	    switch(map.getAgentHeading()){
	    	case 0: //NORTH
	    		rot += 180;
	    		break;
	    	case 1: //EAST
	    		rot += -90;
	    		break;
	    	case 2: //SOUTH
	    		rot += 0; //the base amount!
	    		break;
	    	case 3: //WEST
	    		rot += 90;
	    		break;
	    }
	    gl.glRotatef(rot, 0f, 0f, 1f);
	    
	    
		GLUquadric quad = glu.gluNewQuadric();
		glu.gluQuadricTexture(quad,true);
		glu.gluQuadricDrawStyle(quad, GLU.GLU_FILL);
        glu.gluQuadricNormals(quad, GLU.GLU_FLAT);
        glu.gluQuadricOrientation(quad, GLU.GLU_OUTSIDE);
		glu.gluSphere(quad, 0.43, 25, 25);
		glu.gluDeleteQuadric(quad);	
		gl.glPopMatrix();
		textureAgent.disable(gl);
	}
	
	private void drawMinion(GLAutoDrawable drawable, Node mapNode) {
		// Get the OpenGL graphics context
		GL2 gl = drawable.getGL().getGL2();
		textureMinion.enable(gl);
		textureMinion.bind(gl);
		gl.glPushMatrix();
	    //gl.glLoadIdentity();                // reset the current model-view matrix
	    gl.glTranslatef(-mapNode.getX(), -mapNode.getY(), 0.0f); //translate to location on map
	    //gl.glRotatef(25,1f,0f,0f); //rotate a little upwards
		GLUquadric quad = glu.gluNewQuadric();
		glu.gluQuadricTexture(quad,true);
		glu.gluQuadricDrawStyle(quad, GLU.GLU_FILL);
        glu.gluQuadricNormals(quad, GLU.GLU_FLAT);
        glu.gluQuadricOrientation(quad, GLU.GLU_OUTSIDE);
		glu.gluSphere(quad, 0.43, 25, 25);
		glu.gluDeleteQuadric(quad);
		textureMinion.disable(gl);
		gl.glPopMatrix();		
	}
	
	private void drawDeadMinion(GLAutoDrawable drawable, Node mapNode){
		GL2 gl = drawable.getGL().getGL2();
		textureDeadMinion.enable(gl);
		textureDeadMinion.bind(gl);
		float z = 0.45f;
		
		gl.glPushMatrix();
	    //gl.glLoadIdentity();                // reset the current model-view matrix
	    gl.glNormal3f(0f,0f,-1f); //one normal for the whole thing
	    gl.glColor3f(1f, 1f, 1f);
	    gl.glTranslatef(-mapNode.getX(), -mapNode.getY(), z); //translate to location on map
	    gl.glRotatef(180,0f,0f,1f);
		 
	    GLUquadric quad = glu.gluNewQuadric();
		glu.gluQuadricTexture(quad,true);
		glu.gluQuadricDrawStyle(quad, GLU.GLU_FILL);
        glu.gluQuadricNormals(quad, GLU.GLU_FLAT);
        glu.gluQuadricOrientation(quad, GLU.GLU_OUTSIDE);
		glu.gluDisk(quad, 0, 0.43, 25, 1);
		glu.gluDeleteQuadric(quad);
		
		gl.glPopMatrix();
	}
	
	private void drawPit(GLAutoDrawable drawable, Node mapNode) {
	   GL2 gl = drawable.getGL().getGL2();
	   texturePit.enable(gl);
	   texturePit.bind(gl);
	   gl.glPushMatrix();
	   //gl.glLoadIdentity();                // reset the current model-view matrix
	   gl.glColor3f(1.0f, 1.0f, 1.0f);
	   gl.glTranslatef(-mapNode.getX(), -mapNode.getY(), 0.0f); //translate to location on map
       gl.glBegin(GL_QUADS); // of the color cube
	      gl.glTexCoord2f(texturePitLeft, texturePitBottom);
	      gl.glVertex3f(-0.5f, -0.5f, 0.5f); // bottom-left of the texture and quad
	      gl.glTexCoord2f(texturePitRight, texturePitBottom);
	      gl.glVertex3f(0.5f, -0.5f, 0.5f);  // bottom-right of the texture and quad
	      gl.glTexCoord2f(texturePitRight, texturePitTop);
	      gl.glVertex3f(0.5f, 0.5f, 0.5f);   // top-right of the texture and quad
	      gl.glTexCoord2f(texturePitLeft, texturePitTop);
	      gl.glVertex3f(-0.5f, 0.5f, 0.5f);  // top-left of the texture and quad
	   gl.glEnd();
	   texturePit.disable(gl);
	   gl.glPopMatrix();
		
	}
	
	private void drawWumpus (GLAutoDrawable drawable, Node mapNode) {
		// Get the OpenGL graphics context
		GL2 gl = drawable.getGL().getGL2();
		textureWumpus.enable(gl);
		textureWumpus.bind(gl);
		gl.glPushMatrix();
	    //gl.glLoadIdentity();                // reset the current model-view matrix
	    gl.glColor3f(1.0f, 0.0f, 0.0f);	//red
	    gl.glTranslatef(-mapNode.getX(), -mapNode.getY(), 0.0f); //translate to location on map
	    gl.glRotatef(25,1f,0f,0f); //rotate a little upwards
		GLUquadric quad = glu.gluNewQuadric();
		glu.gluQuadricTexture(quad,true);
		glu.gluQuadricDrawStyle(quad, GLU.GLU_FILL);
        glu.gluQuadricNormals(quad, GLU.GLU_FLAT);
        glu.gluQuadricOrientation(quad, GLU.GLU_OUTSIDE);
		glu.gluSphere(quad, 0.43, 25, 25);
		glu.gluDeleteQuadric(quad);	
		gl.glPopMatrix();
		textureWumpus.disable(gl);
		
	}
	
	private void drawDeadWumpus(GLAutoDrawable drawable, Node mapNode){
		GL2 gl = drawable.getGL().getGL2();
		textureDeadWumpus.enable(gl);
		textureDeadWumpus.bind(gl);
		float z = 0.45f;
		
		gl.glPushMatrix();
	    //gl.glLoadIdentity();                // reset the current model-view matrix
	    gl.glNormal3f(0f,0f,-1f); //one normal for the whole thing
	    gl.glColor3f(1f, 1f, 1f);
	    gl.glTranslatef(-mapNode.getX(), -mapNode.getY(), z); //translate to location on map
	    gl.glRotatef(180,0f,0f,1f);
		 
	    GLUquadric quad = glu.gluNewQuadric();
		glu.gluQuadricTexture(quad,true);
		glu.gluQuadricDrawStyle(quad, GLU.GLU_FILL);
        glu.gluQuadricNormals(quad, GLU.GLU_FLAT);
        glu.gluQuadricOrientation(quad, GLU.GLU_OUTSIDE);
		glu.gluDisk(quad, 0, 0.43, 25, 1);
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
	      //gl.glLoadIdentity();                // reset the current model-view matrix
	      gl.glTranslatef(-mapNode.getX(), -mapNode.getY(), 0.125f); //translate to location on map
	      //gl.glScalef(1f, 1f, 0.85f); //make the walls a little shorter
	      gl.glBegin(GL_QUADS); // of the color cube
	 	 
	      // Front Face
	      gl.glNormal3f(0f,0f,1f);
	      gl.glTexCoord2f(textureWallLeft, textureWallBottom);
	      gl.glVertex3f(-0.5f, -0.5f, 0.5f); // bottom-left of the texture and quad
	      gl.glTexCoord2f(textureWallRight, textureWallBottom);
	      gl.glVertex3f(0.5f, -0.5f, 0.5f);  // bottom-right of the texture and quad
	      gl.glTexCoord2f(textureWallRight, textureWallTop);
	      gl.glVertex3f(0.5f, 0.5f, 0.5f);   // top-right of the texture and quad
	      gl.glTexCoord2f(textureWallLeft, textureWallTop);
	      gl.glVertex3f(-0.5f, 0.5f, 0.5f);  // top-left of the texture and quad

	      // Back Face
	      gl.glNormal3f(0f,0f,-1f);
	      gl.glTexCoord2f(textureWallRight, textureWallBottom);
	      gl.glVertex3f(-0.5f, -0.5f, -0.5f);
	      gl.glTexCoord2f(textureWallRight, textureWallTop);
	      gl.glVertex3f(-0.5f, 0.5f, -0.5f);
	      gl.glTexCoord2f(textureWallLeft, textureWallTop);
	      gl.glVertex3f(0.5f, 0.5f, -0.5f);
	      gl.glTexCoord2f(textureWallLeft, textureWallBottom);
	      gl.glVertex3f(0.5f, -0.5f, -0.5f);
	      
	      // Top Face
	      gl.glNormal3f(0f,1f,0f);
	      gl.glTexCoord2f(textureWallLeft, textureWallTop);
	      gl.glVertex3f(-0.5f, 0.5f, -0.5f);
	      gl.glTexCoord2f(textureWallLeft, textureWallBottom);
	      gl.glVertex3f(-0.5f, 0.5f, 0.5f);
	      gl.glTexCoord2f(textureWallRight, textureWallBottom);
	      gl.glVertex3f(0.5f, 0.5f, 0.5f);
	      gl.glTexCoord2f(textureWallRight, textureWallTop);
	      gl.glVertex3f(0.5f, 0.5f, -0.5f);
	      
	      // Bottom Face
	      gl.glNormal3f(0f,-1f,0f);
	      gl.glTexCoord2f(textureWallRight, textureWallTop);
	      gl.glVertex3f(-0.5f, -0.5f, -0.5f);
	      gl.glTexCoord2f(textureWallLeft, textureWallTop);
	      gl.glVertex3f(0.5f, -0.5f, -0.5f);
	      gl.glTexCoord2f(textureWallLeft, textureWallBottom);
	      gl.glVertex3f(0.5f, -0.5f, 0.5f);
	      gl.glTexCoord2f(textureWallRight, textureWallBottom);
	      gl.glVertex3f(-0.5f, -0.5f, 0.5f);
	      
	      // Right face
	      gl.glNormal3f(1f,0f,0f);
	      gl.glTexCoord2f(textureWallRight, textureWallBottom);
	      gl.glVertex3f(0.5f, -0.5f, -0.5f);
	      gl.glTexCoord2f(textureWallRight, textureWallTop);
	      gl.glVertex3f(0.5f, 0.5f, -0.5f);
	      gl.glTexCoord2f(textureWallLeft, textureWallTop);
	      gl.glVertex3f(0.5f, 0.5f, 0.5f);
	      gl.glTexCoord2f(textureWallLeft, textureWallBottom);
	      gl.glVertex3f(0.5f, -0.5f, 0.5f);
	      
	      // Left Face
	      gl.glNormal3f(-1f,0f,0f);
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
	      textureGold.enable(gl);
	      textureGold.bind(gl);
	      gl.glPushMatrix();
		  //gl.glLoadIdentity();                 // reset the model-view matrix
	      gl.glTranslatef(-mapNode.getX(), -mapNode.getY(), 0.0f); // translate
	      gl.glRotatef(anglePyramid, -0.2f, 0.0f, 1.0f); // rotate about the z-axis
	      gl.glRotatef(-90, 1.0f, 0.0f, 0.0f); //flip so tip faces up
	      gl.glScalef(0.25f, 0.25f, 0.25f);
	 
	      gl.glBegin(GL_TRIANGLES); // of the pyramid
	 
	      // Front-face triangle
	      float[] norm = normalize(new float[]{0f,2f,4f});
	      gl.glNormal3f(norm[0],norm[1],norm[2]);
	      gl.glTexCoord2f((textureGoldLeft+textureGoldRight)/2, (textureGoldTop+textureGoldBottom)/2);
	      gl.glVertex3f(0.0f, 1.0f, 0.0f);
	      gl.glTexCoord2f(textureGoldLeft, textureGoldTop);
	      gl.glVertex3f(-1.0f, -1.0f, 1.0f);
	      gl.glTexCoord2f(textureGoldRight, textureGoldTop);
	      gl.glVertex3f(1.0f, -1.0f, 1.0f);
	 
	      // Right-face triangle
	      norm = normalize(new float[]{4f,2f,0f});
	      gl.glNormal3f(norm[0],norm[1],norm[2]);
	      gl.glTexCoord2f((textureGoldLeft+textureGoldRight)/2, (textureGoldTop+textureGoldBottom)/2);
	      gl.glVertex3f(0.0f, 1.0f, 0.0f);
	      gl.glTexCoord2f(textureGoldRight, textureGoldTop);
	      gl.glVertex3f(1.0f, -1.0f, 1.0f);
	      gl.glTexCoord2f(textureGoldRight, textureGoldBottom);
	      gl.glVertex3f(1.0f, -1.0f, -1.0f);
	 
	      // Back-face triangle
	      norm = normalize(new float[]{0f,2f,-4f});
	      gl.glNormal3f(norm[0],norm[1],norm[2]);
	      gl.glTexCoord2f((textureGoldLeft+textureGoldRight)/2, (textureGoldTop+textureGoldBottom)/2);
	      gl.glVertex3f(0.0f, 1.0f, 0.0f);
	      gl.glTexCoord2f(textureGoldRight, textureGoldBottom);
	      gl.glVertex3f(1.0f, -1.0f, -1.0f);
	      gl.glTexCoord2f(textureGoldLeft, textureGoldBottom);
	      gl.glVertex3f(-1.0f, -1.0f, -1.0f);
	 
	      // Left-face triangle
	      norm = normalize(new float[]{-4f,2f,0f});
	      gl.glNormal3f(norm[0],norm[1],norm[2]);
	      gl.glTexCoord2f((textureGoldLeft+textureGoldRight)/2, (textureGoldTop+textureGoldBottom)/2);
	      gl.glVertex3f(0.0f, 1.0f, 0.0f);
	      gl.glTexCoord2f(textureGoldLeft, textureGoldBottom);
	      gl.glVertex3f(-1.0f, -1.0f, -1.0f);
	      gl.glTexCoord2f(textureGoldLeft, textureGoldTop);
	      gl.glVertex3f(-1.0f, -1.0f, 1.0f);
	 
	      gl.glEnd(); // of the pyramid
	      textureGold.disable(gl);
	      gl.glPopMatrix();
	}
   
   private void update() {
	   //goal updating
	   float speedPyramid = 2.0f;
	   anglePyramid += speedPyramid;
   }
 
   /**
    * Called back before the OpenGL context is destroyed. Release resource such as buffers.
    */
   @Override
   public void dispose(GLAutoDrawable drawable) { }

	@Override
	public void mouseDragged(MouseEvent e) {
		if(e.getX() > startX){ //mouse moved right
			startX = e.getX();
			circleAngle += 180f/360*2*Math.PI;
		}
		else if(e.getX() < startX){ //mouse moved left
			startX = e.getX();
			circleAngle -= 180f/360*2*Math.PI;
		}
		float moveAmount = 0.35f;
		if(e.getY() > startY){ //mouse moved up
			startY = e.getY();
			if(eyePos[1] + moveAmount < midPoint[1]){ //bounds on y so it can't go over the top of the map
				rotChanged = true;
				eyePos[1] += moveAmount;
				eyePos[2] -= moveAmount;
			}
		}
		else if(e.getY() < startY){ //mouse moved down
			startY = e.getY();
			if(eyePos[2] + moveAmount < midPoint[2]){ //bounds on y so it can't go under the map
				rotChanged = true;
				eyePos[1] -= moveAmount;
				eyePos[2] += moveAmount;
			}
		}
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		// do nothing for now
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// do nothing for now
	}

	@Override
	public void mousePressed(MouseEvent e) {
		startX = e.getX();
		startY = e.getY();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// do nothing for now		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// do nothing for now
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		float zoom = 0.35f;
		float[] unitEye = normalize(eyePos);
		if(e.getWheelRotation() < 0){ //scrolled up, zoom in
			zoomChanged  = true;
			if(vectorLength(eyePos) > 2.5f){
				eyePos[0] -= zoom*unitEye[0];
				eyePos[1] -= zoom*unitEye[1];
				if(eyePos[2] + zoom*unitEye[2] < midPoint[2]-2f){ //enable zooming even if z is 0
					eyePos[2] -= zoom*unitEye[2];
				}
			}
		}
		else if(e.getWheelRotation() > 0){ //scrolled down, zoom out
			if(Math.abs(eyePos[2] + zoom*unitEye[2]) < Math.abs(radius*2)){
				zoomChanged = true;
				eyePos[0] += zoom*unitEye[0];
				eyePos[1] += zoom*unitEye[1];
				eyePos[2] += zoom*unitEye[2];
			}
		}
	}
	
	private float vectorLength(float[] vec){
		return (float)Math.sqrt(Math.pow(vec[0], 2) + Math.pow(vec[1], 2) + Math.pow(vec[2], 2));
	}
	
	private float[] normalize(float[] vec){
		float[] ret = new float[3];
		//get length of vector
		float length = vectorLength(vec);
		
		//don't want to divide by 0
		if(length == 0){ 
			ret[0] = 0;
			ret[1] = 0;
			ret[2] = 0;
			return ret;
		}
		
		//divide all elements by length
		ret[0] = vec[0]/length;
		ret[1] = vec[1]/length;
		ret[2] = vec[2]/length;
		return ret;
	}
}
