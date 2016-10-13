package comp557.a2;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import com.jogamp.opengl.DebugGL2;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLCapabilitiesImmutable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

import mintools.swing.ControlFrame;
import mintools.viewer.Interactor;

/**
 * Assignment 2 base framework
 * 
 * @author ethan
 */

// Josh Liu ID:260612384
public abstract class A2Base implements GLEventListener, Interactor {
    	
	// main window displaying the scene
	JFrame mainFrame;

	// control window with all the widgets
    ControlFrame controlFrame;
    
    GLCanvas glCanvas;
    
    GLCapabilities glc;
    
    private FPSAnimator animator;
    
	/**
	 * Default constructor
	 */
    public A2Base() {
        Dimension controlSize = new Dimension(640, 640);
        Dimension size = new Dimension(640, 480);
        controlFrame = new ControlFrame("Controls");
        controlFrame.setSelectedTab("Scene");        
        controlFrame.setSize(controlSize.width, controlSize.height);
        controlFrame.setLocation(size.width + 20, 0);
        
		glc = new GLCapabilities(GLProfile.getDefault());

        System.out.println("Here are the default Accum buffer properties:");
		System.out.println( "R bits = " + glc.getAccumRedBits() );
		System.out.println( "G bits = " + glc.getAccumGreenBits() );
		System.out.println( "B bits = " + glc.getAccumBlueBits() );
		System.out.println( "A bits = " + glc.getAccumAlphaBits() );

        System.out.println("Here are the default Accum buffer properties we are requesting:");
		glc.setAccumRedBits(32);
		glc.setAccumGreenBits(32);
		glc.setAccumBlueBits(32);
		glc.setAccumAlphaBits(32);
		System.out.println( "R bits = " + glc.getAccumRedBits() );
		System.out.println( "G bits = " + glc.getAccumGreenBits() );
		System.out.println( "B bits = " + glc.getAccumBlueBits() );
		System.out.println( "A bits = " + glc.getAccumAlphaBits() );
		
		glCanvas = new GLCanvas( glc );
        glCanvas.setSize( size.width, size.height );
        glCanvas.setIgnoreRepaint( true );
        glCanvas.addGLEventListener( this );
        
        // Create the main frame
        mainFrame = new JFrame();
        mainFrame.getContentPane().setLayout( new BorderLayout() );
        mainFrame.getContentPane().add( glCanvas, BorderLayout.CENTER );
        mainFrame.setLocation(0,0);        
        mainFrame.addWindowListener( new WindowAdapter() {
            @Override
            public void windowClosing( WindowEvent e ) {
                System.exit(0);
            }
        });
        mainFrame.pack();
    }
    
    /**
     * Start the app
     */
    public void start() {
        if (animator == null) {
        	// show the windows/frames
            mainFrame.setVisible( true );
            controlFrame.setVisible(true);
            
            glCanvas.requestFocus();
        	
            // start the FPS animator
        	animator = new FPSAnimator( glCanvas, 60 );
        	animator.start();    
        }
    }
    
    public void init( GLAutoDrawable drawable ) {
    	drawable.setGL( new DebugGL2( drawable.getGL().getGL2() ) );
        GL2 gl = drawable.getGL().getGL2();
        
        System.out.println("Here are the Accum buffer properties we got:");
        GLCapabilitiesImmutable cglc = glCanvas.getChosenGLCapabilities();
        System.out.println( "R bits = " + cglc.getAccumRedBits() );
        System.out.println( "G bits = " + cglc.getAccumGreenBits() );
        System.out.println( "B bits = " + cglc.getAccumBlueBits() );
        System.out.println( "A bits = " + cglc.getAccumAlphaBits() );
        
        gl.glShadeModel(GL2.GL_SMOOTH);              // Enable Smooth Shading
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);    // Black Background
        gl.glClearDepth(1.0f);                      // Depth Buffer Setup
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        gl.glEnable(GL.GL_LINE_SMOOTH);
        gl.glEnable(GL2.GL_POINT_SMOOTH);
        gl.glEnable(GL2.GL_NORMALIZE );
        gl.glEnable(GL.GL_DEPTH_TEST);              // Enables Depth Testing
        gl.glDepthFunc(GL.GL_LEQUAL);               // The Type Of Depth Testing To Do 
        gl.glLineWidth( 2 );                        // slightly fatter lines by default!
    }   

    @Override
    public void dispose(GLAutoDrawable drawable) {
    	// nothing to do
    }
        
    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        // do nothing
    }
}
