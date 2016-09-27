package comp557.a1;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

import mintools.parameters.DoubleParameter;

public class BallJointDAGNode extends DAGNode {

	private double transx = 0d;
	private double transy = 0d;
	private double transz = 0d;
	private enum direction {UP, DOWN, LEFT, RIGHT, FORWARD, BACK};
	private direction currentdir = direction.FORWARD;
	
	public BallJointDAGNode(String n, double tx, double ty, double tz) {	
		name = n;
		transx = tx;
		transy = ty;
		transz = tz;
		dofs.add(new DoubleParameter("pitch", 0d, -90d, 90d));
		dofs.add(new DoubleParameter("yaw", 0d, -90d, 90d));
		dofs.add(new DoubleParameter("roll", 0d, -90d, 90d));
	}
	
	public void setDirection(String s) {
		switch(s) {
			case "up": currentdir = direction.UP;
				   	   break;
			case "down": currentdir = direction.DOWN;
		   			 	 break;
			case "left": currentdir = direction.LEFT;
		   			 	 break;
			case "right": currentdir = direction.RIGHT;
		   			  	  break;
			case "backward": currentdir = direction.BACK;
		   				 	 break;
		   	default: break;			 	 
		}
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
    	if (currentdir != direction.FORWARD) {
    		if (currentdir == direction.UP) gl.glRotatef(-90f, 1.0f, 0.0f, 0.0f);
    		else if (currentdir == direction.DOWN) gl.glRotatef(90f, 1.0f, 0.0f, 0.0f);
    		else if (currentdir == direction.LEFT) gl.glRotatef(-90f, 0.0f, 1.0f, 1.0f);
    		else if (currentdir == direction.RIGHT) gl.glRotatef(90f, 0.0f, 0.0f, 1.0f);
    		else if (currentdir == direction.BACK) gl.glRotatef(180f, 1.0f, 0.0f, 0.0f);
    	}
    	super.display(drawable);
    	gl.glPopMatrix();
    }		
	
}
