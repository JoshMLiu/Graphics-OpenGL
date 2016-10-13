package comp557.a2;

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.glu.GLU;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.vecmath.Point2d;

import mintools.parameters.BooleanParameter;
import mintools.parameters.DoubleParameter;
import mintools.parameters.IntParameter;
import mintools.swing.VerticalFlowPanel;
import mintools.viewer.FlatMatrix4f;
import mintools.viewer.TrackBallCamera;

import com.jogamp.opengl.util.gl2.GLUT;

/**
 * Assignment 2 - depth of field blur, and anaglyphys
 * 
 * For additional information, see the following paper, which covers
 * more on quality rendering, but does not cover anaglyphs.
 * 
 * The Accumulation Buffer: Hardware Support for High-Quality Rendering
 * Paul Haeberli and Kurt Akeley
 * SIGGRAPH 1990
 * 
 * http://http.developer.nvidia.com/GPUGems/gpugems_ch23.html
 * GPU Gems [2007] has a slightly more recent survey of techniques.
 *
 * http://www.cs.duke.edu/courses/fall01/cps124/resources/p309-haeberli.pdf
 * @author Josh Liu ID#:260612384
 */
public class A2App extends A2Base {

	private String name = "Comp 557 Assignment 2 - JOSH LIU ID:260612384";
	
    /** Viewing mode as specified in the assignment */
    int viewingMode = 1;
    
    /**
     * Note that limits are set to try to prevents you from choosing bad values (e.g., a near plane behind the eye,
     * or near plane beyond far plane).  But feel free to change the limits and experiment.
     * Also note that these quantities give the z coordinate in the world coordinate system!
     * */
    private DoubleParameter eyeZPosition = new DoubleParameter( "eye z position in world", 0.3, 0.1, 10 ); 
    private DoubleParameter near = new DoubleParameter( "near z position in world", 0.2, 0.05, 0.5 ); 
    private DoubleParameter far  = new DoubleParameter( "far z position in world", -0.2, -2, -0.1 ); 
    private DoubleParameter focalPlaneZPosition = new DoubleParameter( "focal z position in world", 0, -1.5, 0.4 );     

    /** samples and aperture size are for drawing depth of field blur */    
    private IntParameter samples = new IntParameter( "samples", 2, 1, 100 );   
    
    /** In the human eye, pupil diameter ranges between approximately 2 and 8 mm */
    private DoubleParameter aperture = new DoubleParameter( "aperture size", 0.003, 0, 0.01 );
    
    /** x eye offsets for testing (see objective 4) */         
    private DoubleParameter eyeXOffset = new DoubleParameter("eye offset in x", 0.0, -0.3, 0.3);
    /** y eye offsets for testing (see objective 4) */
    private DoubleParameter eyeYOffset = new DoubleParameter("eye offset in y", 0.0, -0.3, 0.3);
    
    /** controls for drawing frustums, or not, as drawing lots of frustums will be confusing!! */
    private BooleanParameter drawCenterEyeFrustum = new BooleanParameter( "draw center eye frustum", true );    
    private BooleanParameter drawEyeFrustums = new BooleanParameter( "draw left and right eye frustums", true );
    
	/**
	 * The eye disparity should be constant, but can be adjusted to test the
	 * creation of left and right eye frustums or likewise, can be adjusted for
	 * your own eyes!! Note that 63 mm is a good inter occular distance for the
	 * average human, but you may likewise want to lower this to reduce the
	 * depth effect (images may be hard to fuse with cheap 3D colour filter
	 * glasses). Setting the disparity negative should help you check if you
	 * have your left and right eyes reversed!
	 */
    private DoubleParameter eyeDisparity = new DoubleParameter("eye disparity", 0.04, -0.1, 0.1 );

    private GLUT glut = new GLUT();
    private GLU glu = new GLU();
	FastPoissonDisk fdp = new FastPoissonDisk();
    
    private Scene scene = new Scene();

    /**
     * Launches the application
     * @param args
     */
    public static void main(String[] args) {
        new A2App();
    }
    
    /** Main trackball for viewing the world and the two eye frustums */
    private TrackBallCamera tbc = new TrackBallCamera();
    /** Second trackball for rotating the scene */
    private TrackBallCamera tbc2 = new TrackBallCamera();
    
    /**
     * Creates the application
     */
    public A2App() {      
    	
    	// initialise A2Base
    	super();
    	
    	// add tabs to the control frame
        controlFrame.add("Camera", tbc.getControls());
        controlFrame.add("Scene TrackBall", tbc2.getControls());
        controlFrame.add("Scene", getControls());
    
        tbc.attach( glCanvas );
        tbc2.attach( glCanvas );
        // initially disable second trackball, and improve default parameters given our intended use
        tbc2.enable(false);
        tbc2.setFocalDistance( 0 );
        tbc2.panRate.setValue(5e-5);
        tbc2.advanceRate.setValue(0.005);
        this.attach( glCanvas );
        
        mainFrame.setTitle(name);
        
    	// add code here if additional initialisation is needed
    	// for depth of field
    	fdp.createSamples();
        
        // start the app
        start();
    }
    
    @Override
    public void dispose(GLAutoDrawable drawable) {
    	// nothing to do
    }
        
    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        // do nothing
    }
    
    @Override
    public void attach(Component component) {
        component.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() >= KeyEvent.VK_1 && e.getKeyCode() <= KeyEvent.VK_9) {
                    viewingMode = e.getKeyCode() - KeyEvent.VK_1 + 1;
                } else if ( e.getKeyCode() == KeyEvent.VK_0 ) {
                	viewingMode = 10;
                } else if ( e.getKeyCode() == KeyEvent.VK_MINUS ) {
                	viewingMode = 11;
                }
                if ( viewingMode == 1 ) {
                	tbc.enable(true);
                	tbc2.enable(false);
	            } else {
                	tbc.enable(false);
                	tbc2.enable(true);
	            }
                System.out.println("viewing mode " + viewingMode );
            }
        });
    }
    
    /**
     * @return a control panel
     */
    public JPanel getControls() {     
        VerticalFlowPanel vfp = new VerticalFlowPanel();
        vfp.add( eyeZPosition.getSliderControls(false));        
        vfp.add ( drawCenterEyeFrustum.getControls() );
        vfp.add( near.getSliderControls(false));
        vfp.add( far.getSliderControls(false));        
        vfp.add( focalPlaneZPosition.getSliderControls(false));        
        vfp.add( eyeXOffset.getSliderControls(false ) );
        vfp.add( eyeYOffset.getSliderControls(false ) );        
        vfp.add ( aperture.getSliderControls(false) );
        vfp.add ( samples.getSliderControls() );        
        vfp.add( eyeDisparity.getSliderControls(false) );        
        vfp.add ( drawEyeFrustums.getControls() );        
        VerticalFlowPanel vfp2 = new VerticalFlowPanel();
        vfp2.setBorder( new TitledBorder("Scene size and position" ));
        vfp2.add( scene.getControls() );
        vfp.add( vfp2.getPanel() );        
        return vfp.getPanel();
    }
             
    @Override
    public void init( GLAutoDrawable drawable ) {    	
    	// init gl drawable with default configuration
    	super.init(drawable);
    	
    	// add code here if additional gl drawable customization is needed
    }
 
    double w;
    double h;

	double screenWidthPixels = 2560;
	double screenWidthMeters = 0.31; 
	double metersPerPixel = screenWidthMeters / screenWidthPixels;
    
	// x,y coordinates of screen vertices
	double[] screenLeftTop, screenLeftBottom, screenRightTop, screenRightBottom;
	// coordinates for near plane for frustum
	double nearLeft, nearBottom, nearRight, nearTop;
	
    @Override
    public void display(GLAutoDrawable drawable) {        
        GL2 gl = drawable.getGL().getGL2();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);            
        // makes sure the screen isn't stuck at half size from modes 7 & 8
        gl.glViewport(0, 0, drawable.getSurfaceWidth(), drawable.getSurfaceHeight());
        
        // in world units
        w = drawable.getSurfaceWidth() * metersPerPixel;
        h = drawable.getSurfaceHeight() * metersPerPixel;
        
        // Initialize near plane world coordinates
        nearLeft = 0;
        nearBottom = 0;
        nearTop = 0;
        nearRight = 0;
        
        // Set world coordinates of screen
        // if the mode is a split view, the screen is half as wide
        if (viewingMode == 7 || viewingMode == 8) {
        	screenLeftTop = new double[] {-(w/4), (h/2)};
	        screenLeftBottom = new double[] {-(w/4), -(h/2)};
	        screenRightTop = new double[] {(w/4), (h/2)};
	        screenRightBottom = new double[] {(w/4), -(h/2)};
        }
        else {
	        screenLeftTop = new double[] {-(w/2), (h/2)};
	        screenLeftBottom = new double[] {-(w/2), -(h/2)};
	        screenRightTop = new double[] {(w/2), (h/2)};
	        screenRightBottom = new double[] {(w/2), -(h/2)};
        }
        
        // get frustum dimensions for center eye
        double eyeToNear = eyeZPosition.getValue() - near.getValue(); 
        if (eyeToNear <= 0) eyeToNear = 0.01;
        // For getting boundaries of near plane
        double opposite = 0;
        double ratio = 0;
        
        // left
        opposite = screenLeftBottom[0] - eyeXOffset.getValue();
        ratio = Math.atan(opposite/eyeZPosition.getValue());
        nearLeft = Math.tan(ratio)*eyeToNear;      
        // right
        opposite = screenRightBottom[0] - eyeXOffset.getValue();
        ratio = Math.atan(opposite/eyeZPosition.getValue());
        nearRight = Math.tan(ratio)*eyeToNear;
        // bottom 
        opposite = screenRightBottom[1] - eyeYOffset.getValue();
        ratio = Math.atan(opposite/eyeZPosition.getValue());
        nearBottom = Math.tan(ratio)*eyeToNear;
        // top
        opposite = screenRightTop[1] - eyeYOffset.getValue();
        ratio = Math.atan(opposite/eyeZPosition.getValue());
        nearTop = Math.tan(ratio)*eyeToNear;
        
        // for seperate eye frustums
        double leftEyeOffset = eyeXOffset.getValue() - (eyeDisparity.getValue())/2;
        double rightEyeOffset = eyeXOffset.getValue() + (eyeDisparity.getValue())/2;
        double leftLeft, leftRight, leftBottom, leftTop;
        double rightLeft, rightRight, rightBottom, rightTop;
        
        // frustum for left eye
        opposite = screenLeftBottom[0] - leftEyeOffset;
        ratio = Math.atan(opposite/eyeZPosition.getValue());
        leftLeft = Math.tan(ratio)*eyeToNear;      
        opposite = screenRightBottom[0] - leftEyeOffset;
        ratio = Math.atan(opposite/eyeZPosition.getValue());
        leftRight = Math.tan(ratio)*eyeToNear;
        leftBottom = nearBottom;
        leftTop = nearTop;
        
        // frustum for right eye
        opposite = screenLeftBottom[0] - rightEyeOffset;
        ratio = Math.atan(opposite/eyeZPosition.getValue());
        rightLeft = Math.tan(ratio)*eyeToNear;      
        opposite = screenRightBottom[0] - rightEyeOffset;
        ratio = Math.atan(opposite/eyeZPosition.getValue());
        rightRight = Math.tan(ratio)*eyeToNear;
        rightBottom = nearBottom;
        rightTop = nearTop;
        
        if ( viewingMode == 1 ) {
        	// We will use a trackball camera
            tbc.prepareForDisplay(drawable);
            // apply an arbitrary scale in this mode only just to make the 
            // scene and frustums small enough to be easily seen with the 
            // default track ball camera settings.
            gl.glScaled(15,15,15); 
            
            gl.glPushMatrix();
            tbc2.applyViewTransformation(drawable); // only the view transformation          
            scene.display(drawable);
            gl.glPopMatrix();
            
            // screen centered at world origin
            gl.glBegin(gl.GL_LINE_LOOP);
            	gl.glVertex2f((float)screenLeftTop[0], (float)screenLeftTop[1]);
            	gl.glVertex2f((float)screenLeftBottom[0], (float)screenLeftBottom[1]);
            	gl.glVertex2f((float)screenRightBottom[0], (float)screenRightBottom[1]);
            	gl.glVertex2f((float)screenRightTop[0], (float)screenRightTop[1]);
            gl.glEnd();
            
            // eye translated to coordinates given by sliders
            gl.glPushMatrix();
            gl.glTranslatef(eyeXOffset.getFloatValue(), eyeYOffset.getFloatValue(), eyeZPosition.getFloatValue());
            glut.glutSolidSphere(0.0125d, 100, 100); 
            gl.glPopMatrix();      
            
            // draw frustum
            gl.glPushMatrix();
            if (drawCenterEyeFrustum.getValue()) {
            	
                FlatMatrix4f matrix = new FlatMatrix4f();
            	FlatMatrix4f inverse = new FlatMatrix4f();
            	
            	double nearZ = eyeZPosition.getValue() - near.getValue();
            	double farZ = eyeZPosition.getValue() - far.getValue();
            	
            	if (nearZ <= 0) nearZ = 0.01;
                
                gl.glTranslatef(eyeXOffset.getFloatValue(), eyeYOffset.getFloatValue(), eyeZPosition.getFloatValue());               
            	            	
                gl.glPushMatrix();
	                gl.glLoadIdentity();
	                gl.glFrustum(nearLeft, nearRight, nearBottom, nearTop, nearZ, farZ);
	                gl.glGetFloatv(gl.GL_MODELVIEW_MATRIX, matrix.asArray(), 0);
                gl.glPopMatrix();
                
                matrix.reconstitute();
                inverse.getBackingMatrix().invert(matrix.getBackingMatrix());
                gl.glMultMatrixf(inverse.asArray(), 0);
                glut.glutWireCube(2);
            }
            gl.glPopMatrix();
            
            // draw focal plane
            double focusLeft, focusRight, focusBottom, focusTop;
	        double eyeToFocus = eyeZPosition.getValue() - focalPlaneZPosition.getValue();
	        double op = 0;
	        double ra = 0;
	        
	        // left
	        op = screenLeftBottom[0] - eyeXOffset.getValue();
	        ra = Math.atan(op/eyeZPosition.getValue());
	        focusLeft = Math.tan(ra)*eyeToFocus;      
	        // right
	        op = screenRightBottom[0] - eyeXOffset.getValue();
	        ra = Math.atan(op/eyeZPosition.getValue());
	        focusRight = Math.tan(ra)*eyeToFocus;
	        // bottom 
	        op = screenRightBottom[1] - eyeYOffset.getValue();
	        ra = Math.atan(op/eyeZPosition.getValue());
	        focusBottom = Math.tan(ra)*eyeToFocus;
	        // top
	        op = screenRightTop[1] - eyeYOffset.getValue();
	        ra = Math.atan(op/eyeZPosition.getValue());
	        focusTop = Math.tan(ra)*eyeToFocus;
            
	        gl.glPushMatrix();
	        	gl.glColor3f(0.5f, 0.5f, 0.5F);
	        	gl.glTranslated(eyeXOffset.getValue(), eyeYOffset.getValue(), 0);
	        	gl.glBegin(gl.GL_QUADS);
			        gl.glVertex3d(focusLeft, focusTop, focalPlaneZPosition.getValue());
			        gl.glVertex3d(focusRight, focusTop, focalPlaneZPosition.getValue());
			        gl.glVertex3d(focusRight, focusBottom, focalPlaneZPosition.getValue());
			        gl.glVertex3d(focusLeft , focusBottom, focalPlaneZPosition.getValue());
		        gl.glEnd();
		        gl.glColor3f(1f, 1f, 1f);
	        gl.glPopMatrix();
	        
	        // draw both eye frustums, left = red, right = blue
            if (drawEyeFrustums.getValue()) {
            	
            	gl.glPushMatrix();
	            	// draw left 
	                FlatMatrix4f leftmatrix = new FlatMatrix4f();
	            	FlatMatrix4f leftinverse = new FlatMatrix4f();
	            	
	            	double nearZ = eyeZPosition.getValue() - near.getValue();
	            	double farZ = eyeZPosition.getValue() - far.getValue();
	            	
	            	if (nearZ <= 0) nearZ = 0.01;
	                
	                gl.glTranslatef((float) leftEyeOffset, eyeYOffset.getFloatValue(), eyeZPosition.getFloatValue());               
	            	            	
	                gl.glPushMatrix();
		                gl.glLoadIdentity();
		                gl.glFrustum(leftLeft, leftRight, leftBottom, leftTop, nearZ, farZ);
		                gl.glGetFloatv(gl.GL_MODELVIEW_MATRIX, leftmatrix.asArray(), 0);
	                gl.glPopMatrix();
	                
	                leftmatrix.reconstitute();
	                leftinverse.getBackingMatrix().invert(leftmatrix.getBackingMatrix());
	                gl.glMultMatrixf(leftinverse.asArray(), 0);
	                gl.glColor3f(1f, 0f, 0f);
	                glut.glutWireCube(2);
	                
                gl.glPopMatrix();
                
                gl.glPushMatrix();
	                // draw right 
	                FlatMatrix4f rightmatrix = new FlatMatrix4f();
	            	FlatMatrix4f rightinverse = new FlatMatrix4f();
	            		                
	                gl.glTranslatef((float) rightEyeOffset, eyeYOffset.getFloatValue(), eyeZPosition.getFloatValue());               
	            	            	
	                gl.glPushMatrix();
		                gl.glLoadIdentity();
		                gl.glFrustum(rightLeft, rightRight, rightBottom, rightTop, nearZ, farZ);
		                gl.glGetFloatv(gl.GL_MODELVIEW_MATRIX, rightmatrix.asArray(), 0);
	                gl.glPopMatrix();
	                
	                rightmatrix.reconstitute();
	                rightinverse.getBackingMatrix().invert(rightmatrix.getBackingMatrix());
	                gl.glMultMatrixf(rightinverse.asArray(), 0);
	                gl.glColor3f(0f, 0f, 1f);
	                glut.glutWireCube(2);
	                gl.glColor3f(1f,  1f, 1f);
	                
	            gl.glPopMatrix(); 
	                
            }   
            
        } else if ( viewingMode == 2 ) {
        	
        	// the center eye view
        	tbc.prepareForDisplay(drawable);
        	
        	double nearZ = eyeZPosition.getValue() - near.getValue();
        	double farZ = eyeZPosition.getValue() - far.getValue();
        	if (nearZ <= 0) nearZ = 0.01;
        	  
        	gl.glPushMatrix();

    		gl.glLoadIdentity();		
    		glu.gluLookAt(eyeXOffset.getValue(), eyeYOffset.getValue(), eyeZPosition.getValue(), 
    				eyeXOffset.getValue(), eyeYOffset.getValue(), focalPlaneZPosition.getValue(), 0, 1, 0);

    		gl.glMatrixMode(gl.GL_PROJECTION);
        	gl.glLoadIdentity();
        	gl.glFrustum(nearLeft, nearRight, nearBottom, nearTop, nearZ, farZ);
        	        	
        	gl.glMatrixMode(gl.GL_MODELVIEW);
	        tbc2.applyViewTransformation(drawable); // only the view transformation
	        scene.display(drawable);            
        	gl.glPopMatrix();
        	
        } else if ( viewingMode == 3 ) {           
        	
        	// depth of field blur
        	tbc.prepareForDisplay(drawable);
        	double nearZ = eyeZPosition.getValue() - near.getValue();
        	double farZ = eyeZPosition.getValue() - far.getValue();
        	if (nearZ <= 0) nearZ = 0.01;	
        	
        	gl.glClear(gl.GL_ACCUM_BUFFER_BIT);
            
    		gl.glPushMatrix();
    		// write to the buffer once for each number of samples
        	for (int i = 0; i < samples.getValue(); i++) {
            	
        		// Get a point from poisson disk
        		// Make sure there are no out of bounds errors
        		Point2d p = new Point2d();
        		int size = samples.getValue()*10;
        		while(size > fdp.samples.size()) size = size/2;
        		fdp.get(p, i, size);
        		
        		// scale blur with aperture size
        		double x = p.x*aperture.getValue();
        		double y = p.y*aperture.getValue();
        		gl.glLoadIdentity();	
        		// The look at point is always the same to get focus point
        		glu.gluLookAt(eyeXOffset.getValue() + x, eyeYOffset.getValue() + y, eyeZPosition.getValue(), 
        				eyeXOffset.getValue(), eyeYOffset.getValue(), focalPlaneZPosition.getValue(), 0, 1, 0);

        		gl.glMatrixMode(gl.GL_PROJECTION);
            	gl.glLoadIdentity();
            	gl.glFrustum(nearLeft, nearRight, nearBottom, nearTop, nearZ, farZ);
            	        	
            	gl.glMatrixMode(gl.GL_MODELVIEW);
    	        tbc2.applyViewTransformation(drawable); // only the view transformation
    	        scene.display(drawable);    
    	        gl.glAccum(gl.GL_ACCUM, (float) 1/samples.getValue());
        	}
        	gl.glAccum(gl.GL_RETURN, 1f);
        	gl.glPopMatrix();        		
        	
        	
        } else if ( viewingMode == 4 ) {
        	
        	// left eye view
        	tbc.prepareForDisplay(drawable);
        	
        	double nearZ = eyeZPosition.getValue() - near.getValue();
        	double farZ = eyeZPosition.getValue() - far.getValue();
        	if (nearZ <= 0) nearZ = 0.01;
        	  
        	gl.glPushMatrix();

    		gl.glLoadIdentity();		
    		glu.gluLookAt(leftEyeOffset, eyeYOffset.getValue(), eyeZPosition.getValue(), 
    				leftEyeOffset, eyeYOffset.getValue(), focalPlaneZPosition.getValue(), 0, 1, 0);

    		gl.glMatrixMode(gl.GL_PROJECTION);
        	gl.glLoadIdentity();
        	gl.glFrustum(leftLeft, leftRight, leftBottom, leftTop, nearZ, farZ);
        	        	
        	gl.glMatrixMode(gl.GL_MODELVIEW);
	        tbc2.applyViewTransformation(drawable); // only the view transformation
	        scene.display(drawable);            
        	gl.glPopMatrix();
        	
        } else if ( viewingMode == 5 ) {  
        	
        	// right eye view
        	tbc.prepareForDisplay(drawable);
        	
        	double nearZ = eyeZPosition.getValue() - near.getValue();
        	double farZ = eyeZPosition.getValue() - far.getValue();
        	if (nearZ <= 0) nearZ = 0.01;
        	  
        	gl.glPushMatrix();

    		gl.glLoadIdentity();		
    		glu.gluLookAt(rightEyeOffset, eyeYOffset.getValue(), eyeZPosition.getValue(), 
    				rightEyeOffset, eyeYOffset.getValue(), focalPlaneZPosition.getValue(), 0, 1, 0);

    		gl.glMatrixMode(gl.GL_PROJECTION);
        	gl.glLoadIdentity();
        	gl.glFrustum(rightLeft, rightRight, rightBottom, rightTop, nearZ, farZ);
        	        	
        	gl.glMatrixMode(gl.GL_MODELVIEW);
	        tbc2.applyViewTransformation(drawable); // only the view transformation
	        scene.display(drawable);            
        	gl.glPopMatrix();
        	                               
        } else if ( viewingMode == 6 ) {            
        	
        	// draw anaglyph left = red, right = blue
        	
        	// left eye view
        	tbc.prepareForDisplay(drawable);
        	
        	double nearZ = eyeZPosition.getValue() - near.getValue();
        	double farZ = eyeZPosition.getValue() - far.getValue();
        	if (nearZ <= 0) nearZ = 0.01;
        	  
        	gl.glPushMatrix();
        	gl.glColorMask(true, false, false, true);
    		gl.glLoadIdentity();		
    		glu.gluLookAt(leftEyeOffset, eyeYOffset.getValue(), eyeZPosition.getValue(), 
    				leftEyeOffset, eyeYOffset.getValue(), focalPlaneZPosition.getValue(), 0, 1, 0);

    		gl.glMatrixMode(gl.GL_PROJECTION);
        	gl.glLoadIdentity();
        	gl.glFrustum(leftLeft, leftRight, leftBottom, leftTop, nearZ, farZ);
        	        	
        	gl.glMatrixMode(gl.GL_MODELVIEW);
	        tbc2.applyViewTransformation(drawable); // only the view transformation
	        scene.display(drawable);   
        	gl.glPopMatrix();
        	       
        	// right eye view        	  
        	gl.glPushMatrix();
        	gl.glColorMask(false, false, true, true);
    		gl.glLoadIdentity();		
    		glu.gluLookAt(rightEyeOffset, eyeYOffset.getValue(), eyeZPosition.getValue(), 
    				rightEyeOffset, eyeYOffset.getValue(), focalPlaneZPosition.getValue(), 0, 1, 0);

    		gl.glMatrixMode(gl.GL_PROJECTION);
        	gl.glLoadIdentity();
        	gl.glFrustum(rightLeft, rightRight, rightBottom, rightTop, nearZ, farZ);
        	        	
        	gl.glMatrixMode(gl.GL_MODELVIEW);
	        tbc2.applyViewTransformation(drawable); // only the view transformation
	        scene.display(drawable);    
	        
	        gl.glColorMask(true, true, true, true);
        	gl.glPopMatrix();
        	
        	
        } else if ( viewingMode == 7 ) {            
        	
        	// Stereoscopic - left on left
        	tbc.prepareForDisplay(drawable);
        	
        	double nearZ = eyeZPosition.getValue() - near.getValue();
        	double farZ = eyeZPosition.getValue() - far.getValue();
        	if (nearZ <= 0) nearZ = 0.01;
        	
        	// left eye view (on left)
        	gl.glPushMatrix();
        	gl.glViewport(0, 0, (int) (drawable.getSurfaceWidth()/2), (int) drawable.getSurfaceHeight());   	
        	gl.glLoadIdentity();		
    		glu.gluLookAt(leftEyeOffset, eyeYOffset.getValue(), eyeZPosition.getValue(), 
    				leftEyeOffset, eyeYOffset.getValue(), focalPlaneZPosition.getValue(), 0, 1, 0);

    		gl.glMatrixMode(gl.GL_PROJECTION);
        	gl.glLoadIdentity();
        	gl.glFrustum(leftLeft, leftRight, leftBottom, leftTop, nearZ, farZ);
        	        	
        	gl.glMatrixMode(gl.GL_MODELVIEW);
	        tbc2.applyViewTransformation(drawable); // only the view transformation
	        scene.display(drawable);   
        	gl.glPopMatrix();
        	
        	// right eye view (on right)     	  
        	gl.glPushMatrix();
        	gl.glViewport((int) (drawable.getSurfaceWidth()/2), 0, (int) (drawable.getSurfaceWidth()/2), (int) drawable.getSurfaceHeight());   	
    		gl.glLoadIdentity();		
    		glu.gluLookAt(rightEyeOffset, eyeYOffset.getValue(), eyeZPosition.getValue(), 
    				rightEyeOffset, eyeYOffset.getValue(), focalPlaneZPosition.getValue(), 0, 1, 0);

    		gl.glMatrixMode(gl.GL_PROJECTION);
        	gl.glLoadIdentity();
        	gl.glFrustum(rightLeft, rightRight, rightBottom, rightTop, nearZ, farZ);
        	        	
        	gl.glMatrixMode(gl.GL_MODELVIEW);
	        tbc2.applyViewTransformation(drawable); // only the view transformation
	        scene.display(drawable);    	        
        	gl.glPopMatrix();
        	
        } else if ( viewingMode == 8 ) {            
        	
        	// Steroscopic - left on right
        	
        	tbc.prepareForDisplay(drawable);
        	
        	double nearZ = eyeZPosition.getValue() - near.getValue();
        	double farZ = eyeZPosition.getValue() - far.getValue();
        	if (nearZ <= 0) nearZ = 0.01;
        	
        	// left eye view (on right)
        	gl.glPushMatrix();
        	gl.glViewport((int) (drawable.getSurfaceWidth()/2), 0, (int) (drawable.getSurfaceWidth()/2), (int) drawable.getSurfaceHeight());   	
        	gl.glLoadIdentity();		
    		glu.gluLookAt(leftEyeOffset, eyeYOffset.getValue(), eyeZPosition.getValue(), 
    				leftEyeOffset, eyeYOffset.getValue(), focalPlaneZPosition.getValue(), 0, 1, 0);

    		gl.glMatrixMode(gl.GL_PROJECTION);
        	gl.glLoadIdentity();
        	gl.glFrustum(leftLeft, leftRight, leftBottom, leftTop, nearZ, farZ);
        	        	
        	gl.glMatrixMode(gl.GL_MODELVIEW);
	        tbc2.applyViewTransformation(drawable); // only the view transformation
	        scene.display(drawable);   
        	gl.glPopMatrix();
        	
        	// right eye view (on left)	  
        	gl.glPushMatrix();
        	gl.glViewport(0, 0, (int) (drawable.getSurfaceWidth()/2), (int) drawable.getSurfaceHeight());   	
    		gl.glLoadIdentity();		
    		glu.gluLookAt(rightEyeOffset, eyeYOffset.getValue(), eyeZPosition.getValue(), 
    				rightEyeOffset, eyeYOffset.getValue(), focalPlaneZPosition.getValue(), 0, 1, 0);

    		gl.glMatrixMode(gl.GL_PROJECTION);
        	gl.glLoadIdentity();
        	gl.glFrustum(rightLeft, rightRight, rightBottom, rightTop, nearZ, farZ);
        	        	
        	gl.glMatrixMode(gl.GL_MODELVIEW);
	        tbc2.applyViewTransformation(drawable); // only the view transformation
	        scene.display(drawable);    	        
        	gl.glPopMatrix();
        	
        }       
    }
    
}
