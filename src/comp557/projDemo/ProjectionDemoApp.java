package comp557.projDemo;

import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import mintools.parameters.DoubleParameter;
import mintools.swing.ControlFrame;

import comp557.projDemo.Scene.ViewMode;

public class ProjectionDemoApp {

	static public void main( String[] args ) {
		new ProjectionDemoApp();
	}
	
	public ProjectionDemoApp() {
		DoubleParameter.DEFAULT_SLIDER_LABEL_WIDTH = 40;
		DoubleParameter.DEFAULT_SLIDER_TEXT_WIDTH = 60;
		Scene scene = new Scene();
		
		Canvas canvas1 = new Canvas( "World", ViewMode.WORLD, scene );
		Canvas canvas2 = new Canvas( "Camera", ViewMode.EYE, scene );
		Canvas canvas3 = new Canvas( "NDC", ViewMode.NDC, scene );
		Canvas canvas4 = new Canvas( "Screen", ViewMode.SCREEN, scene );
		
        final JFrame jframe; 
        String windowName = "Projection Transformation Demonstration";
        jframe = new JFrame(windowName);
        jframe.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent windowevent ) {
                jframe.dispose();
                System.exit( 0 );
            }
        });
        jframe.getContentPane().setLayout( new GridLayout( 2, 2, 2, 2 ) );
        jframe.getContentPane().add( canvas1.glCanvas );
        jframe.getContentPane().add( canvas2.glCanvas );
        jframe.getContentPane().add( canvas3.glCanvas );
        jframe.getContentPane().add( canvas4.glCanvas );
        jframe.setSize( 600, 620 );
        jframe.setVisible( true );
        
        ControlFrame controls = new ControlFrame("Controls");
        controls.add( "Scene", scene.getControls() );
        controls.add( "World TBC", canvas1.tbc.getControls() );
        controls.add( "Camera TBC", canvas2.tbc.getControls() );
        controls.add( "NDC TBC", canvas3.tbc.getControls() );
        controls.setVisible(true);
	}
}
