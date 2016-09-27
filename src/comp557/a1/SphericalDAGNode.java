package comp557.a1;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.util.gl2.GLUT;

import mintools.parameters.DoubleParameter;

public class SphericalDAGNode extends DAGNode {

	double transx, transy, transz, scalex, scaley, scalez = 0d;
	float red, green, blue = 0;
	
	public SphericalDAGNode(String n, double tx, double ty, double tz, double sx, double sy, double sz) {	
		name = n;
		transx = tz;
		transy = ty;
		transz = tz;
		scalex = sx;
		scaley = sy;
		scalez = sz;
	}
	
	public void setColor(float r, float g, float b) {
		if (r >= 0f && r <= 1f) red = r;
		if (g >= 0f && g <= 1f) green = g;
		if (b >= 0f && b <= 1f) blue = b;		
	}
	
    public void display(GLAutoDrawable drawable) {
    	
    	GL2 gl = drawable.getGL().getGL2();
    	gl.glPushMatrix();
    	gl.glTranslatef((float)transx, (float)transy, (float)transz);
    	gl.glScaled(scalex, scaley, scalez);
    	gl.glEnable(gl.GL_COLOR_MATERIAL);
    	gl.glColor4f(red, green, blue, 1);
    	gl.glRasterPos3f( .3f,.3f,.3f );    	
    	glut.glutSolidSphere(0.5, 100, 100);
    	gl.glDisable(gl.GL_COLOR_MATERIAL);
    	super.display(drawable);
    	gl.glPopMatrix();
    }
    		
}
