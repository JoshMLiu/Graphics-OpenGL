package comp557.a1;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

import mintools.parameters.DoubleParameter;

public class BallJointDAGNode extends DAGNode {

	private double transx = 0d;
	private double transy = 0d;
	private double transz = 0d;
	
	public BallJointDAGNode(String n, double tx, double ty, double tz, double rx, double ry, double rz) {	
		name = n;
		transx = tx;
		transy = ty;
		transz = tz;
		dofs.add(new DoubleParameter("pitch", rx, -180d, 180d));
		dofs.add(new DoubleParameter("yaw", ry, -180d, 180d));
		dofs.add(new DoubleParameter("roll", rz, -180d, 180d));
	}
	
    public void display(GLAutoDrawable drawable) {
    	
    	GL2 gl = drawable.getGL().getGL2();
    	gl.glPushMatrix();
    	gl.glTranslatef((float)transx, (float)transy, (float)transz);
    	for (DoubleParameter param: dofs) {
    		switch(param.getName()) {
				case "pitch": gl.glRotatef(param.getFloatValue(), 1.0f, 0.0f, 0.0f);
	  		  	  			  break;     
				case "yaw": gl.glRotatef(param.getFloatValue(), 0.0f, 1.0f, 0.0f);
			    			break;  
				case "roll": gl.glRotatef(param.getFloatValue(), 0.0f, 0.0f, 1.0f);
					 		 break;     		      
				default: break;
    		}
    	}   	
    	super.display(drawable);
    	gl.glPopMatrix();
    }		
	
}
