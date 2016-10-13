package comp557.projDemo;

import mintools.viewer.EasyViewer;
import mintools.viewer.TrackBallCamera;

import com.jogamp.opengl.DebugGL2;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;
import comp557.projDemo.Scene.ViewMode;

/**
 * OpenGL frame containing the target orientation 
 * @author kry
 *
 */
public class Canvas implements GLEventListener {

	String name;
	
	Scene scene;
	
	ViewMode mode;

	GLCanvas glCanvas;
	    
    TrackBallCamera tbc = new TrackBallCamera();
    	
	public Canvas( String name, ViewMode mode,  Scene scene ) {
		this.name = name;
		this.scene = scene;
		this.mode = mode;
		GLProfile glp = GLProfile.getDefault();
        GLCapabilities glcap = new GLCapabilities(glp);
        glCanvas = new GLCanvas( glcap );
        glCanvas.setSize( 300, 300 );
        glCanvas.addGLEventListener(this);
        final FPSAnimator animator; 
        animator = new FPSAnimator(glCanvas, 60);
        animator.start();
        tbc.attach( glCanvas );
        tbc.near.setDefaultValue(5.0);
        tbc.near.setValue(5);
	}
	
    
    /** 
     * initializes the class for display 
     */
    @Override
    public void init(GLAutoDrawable drawable) {
        drawable.setGL(new DebugGL2(drawable.getGL().getGL2()));
        GL2 gl = drawable.getGL().getGL2();
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0f); // Black Background
        gl.glClearDepth(1.0f); // Depth Buffer Setup
        gl.glEnable(GL.GL_DEPTH_TEST); // Enables Depth Testing
        gl.glDepthFunc(GL.GL_LEQUAL); // The Type Of Depth Testing To Do
        gl.glEnable( GL2.GL_NORMALIZE ); // normals stay normal length under scale
        
        // setup lights and default material
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();        
        gl.glEnable( GL2.GL_LIGHTING );
        gl.glEnable( GL2.GL_LIGHT0 );
        // WATCH OUT: need to provide homogeneous coordinates to many calls!! 
        gl.glLightfv( GL2.GL_LIGHT0, GL2.GL_POSITION, new float[] {10,10,10, 1}, 0 );
        gl.glLightfv( GL2.GL_LIGHT0, GL2.GL_AMBIENT, new float[] {0,0,0,1}, 0);
        gl.glLightModelfv( GL2.GL_LIGHT_MODEL_AMBIENT, new float[] {0.1f,0.1f,0.1f,1}, 0);
        // default material properties
        gl.glMaterialfv( GL.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, new float[] {1,1,1,1}, 0 );
        gl.glMaterialfv( GL.GL_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, new float[] {1,1,1,1}, 0 );
        gl.glMaterialfv( GL.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, new float[] {1,1,1,1}, 0 );
        gl.glMaterialf( GL.GL_FRONT_AND_BACK, GL2.GL_SHININESS, 50 );
        // default blending properties for some simple anti-aliasing
        gl.glEnable( GL.GL_BLEND );
        gl.glBlendFunc( GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA );
        gl.glEnable( GL.GL_LINE_SMOOTH );
        gl.glEnable( GL2.GL_POINT_SMOOTH );
        gl.glEnable( GL2.GL_MULTISAMPLE );
    }
        
    @Override
    public void display(GLAutoDrawable drawable) {
    	GL2 gl = drawable.getGL().getGL2();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        gl.glMatrixMode( GL2.GL_PROJECTION );
        gl.glLoadIdentity();
        tbc.applyProjectionTransformation(drawable);
        gl.glMatrixMode( GL2.GL_MODELVIEW );
        gl.glLoadIdentity();
        tbc.applyViewTransformation(drawable);
        
        scene.display(drawable, mode );
        
        EasyViewer.beginOverlay(drawable);
        EasyViewer.printTextLines( drawable, name, 10, 20, 12, GLUT.BITMAP_HELVETICA_18 );
        gl.glEnable( GL2.GL_LIGHTING );
        EasyViewer.endOverlay(drawable);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
    	// do nothing
    }
    
    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    	// do nothing, glViewPort already called by the component!
    }
	
}
