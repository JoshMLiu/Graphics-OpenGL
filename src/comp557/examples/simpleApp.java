package comp557.examples;

import javax.swing.JPanel;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

import mintools.parameters.DoubleParameter;
import mintools.swing.VerticalFlowPanel;
import mintools.viewer.EasyViewer;
import mintools.viewer.SceneGraphNode;

public class simpleApp implements SceneGraphNode {

	public static void main( String[] args ) {
		new EasyViewer( "example", new simpleApp() );
	}
	
	public simpleApp() {
		// do nothing
	}
	
	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		double s = scale.getValue();
		gl.glScaled( s, s, s );
		EasyViewer.glut.glutSolidTeapot(1);
	}
	
	DoubleParameter scale = new DoubleParameter( "scale", 1, 0.1, 10 );
	@Override
	public JPanel getControls() {
		VerticalFlowPanel vfp = new VerticalFlowPanel();
		vfp.add( scale.getSliderControls(true) );
		return vfp.getPanel();
	}
	
	@Override
	public void init(GLAutoDrawable drawable) {
		// do nothing?
	}
}
