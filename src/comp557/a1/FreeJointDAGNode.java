package comp557.a1;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

import mintools.parameters.DoubleParameter;

// Josh Liu ID:260612384

// Joint that can translate and rotate in any direction. Used only for root. 
public class FreeJointDAGNode extends DAGNode {
	
	// takes only a name 
	public FreeJointDAGNode(String n) {	
		// max and mins are set a borders of screen and 180 degrees on each side 
		name = n;
		dofs.add(new DoubleParameter("vertical", 0.0d, -20f, 20f));
		dofs.add(new DoubleParameter("horizontal", 0.0d, -20f, 20f));
		dofs.add(new DoubleParameter("forwardback", 0.0d, -20f, 20f));
		dofs.add(new DoubleParameter("pitch", 0.0d, -180d, 180d));
		dofs.add(new DoubleParameter("yaw", 0.0d, -180d, 180d));
		dofs.add(new DoubleParameter("roll", 0.0d, -180d, 180d));
	}
	
	// transforms based on slider values
    public void display(GLAutoDrawable drawable) {
    	
    	GL2 gl = drawable.getGL().getGL2();
    	gl.glPushMatrix();
    	for (DoubleParameter param: dofs) {
    		switch(param.getName()) {
   				case "pitch": gl.glRotatef(param.getFloatValue(), 1.0f, 0.0f, 0.0f);
				  		  	  break;     
   				case "yaw": gl.glRotatef(param.getFloatValue(), 0.0f, 1.0f, 0.0f);
   					    	break;  
   				case "roll": gl.glRotatef(param.getFloatValue(), 0.0f, 0.0f, 1.0f);
   							 break;     		
    			case "vertical": gl.glTranslatef(0.0f, param.getFloatValue(), 0.0f);
    							 break;
       			case "horizontal": gl.glTranslatef(param.getFloatValue(), 0.0f, 0.0f);
       							   break;  
       			case "forwardback": gl.glTranslatef(0.0f, 0.0f, param.getFloatValue());
       							    break;         
				default: break;
    		}
    	}   	
    	super.display(drawable);
    	gl.glPopMatrix();
    }
	
}

