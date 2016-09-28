package comp557.a1;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

import mintools.parameters.DoubleParameter;

// Josh Liu ID:260612384

// A joint that only rotates 90 degrees in one direction. Renders nothing.
public class HingeJointDAGNode extends DAGNode {

	// translation relative to parent 
	private double transx = 0d;
	private double transy = 0d;
	private double transz = 0d;
	
	// axis to rotate on
	private String axis;
	
	// flip to rotate in the other direction
	private boolean flipped = false;
	
	// takes in name, translation, and axis to rotate on
	public HingeJointDAGNode(String n, double tx, double ty, double tz, String a) {	
		name = n;
		transx = tx;
		transy = ty;
		transz = tz;
		axis = a;
		if (axis.equals("pitch")) {
			dofs.add(new DoubleParameter("pitch", 0d, 0d, 120d));		
		}
		else if (axis.equals("yaw")) {
			dofs.add(new DoubleParameter("yaw", 0d, 0d, 120d));
		}
		else if (axis.equals("roll")) {
			dofs.add(new DoubleParameter("roll", 0d, 0d, 120d));			
		}
	}
	
	public void flipAxis() {
		flipped = true;
	}
	
	// transfers flip to all children
    public void display(GLAutoDrawable drawable) {
    	
    	GL2 gl = drawable.getGL().getGL2();
    	gl.glPushMatrix();
    	gl.glTranslatef((float)transx, (float)transy, (float)transz);
    	if (flipped) {
    		if (axis.equals("pitch")) gl.glRotatef(180, 0.0f, 1.0f, 0.0f);
    		if (axis.equals("yaw")) gl.glRotatef(180, 1.0f, 0.0f, 0.0f);
    		if (axis.equals("roll")) gl.glRotatef(180, 0.0f, 0.0f, 1.0f);    		
    	}
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
