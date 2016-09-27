package comp557.a1;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.util.gl2.GLUT;

import mintools.parameters.DoubleParameter;

public class SphericalDAGNode extends DAGNode {
	
	double transx, transy, transz, scalex, scaley, scalez = 0d;
	
	byte red = (byte) 234;
	byte green = (byte) 192;
	byte blue = (byte) 134;
	
	public SphericalDAGNode(String n, double tx, double ty, double tz, double sx, double sy, double sz) {	
		name = n;
		transx = tx;
		transy = ty;
		transz = tz;
		scalex = sx;
		scaley = sy;
		scalez = sz;
	}
	
	public void setColor(int r, int g, int b) {
		if (r >= 0f && r <= 1f) red = (byte) r;
		if (g >= 0f && g <= 1f) green = (byte) g;
		if (b >= 0f && b <= 1f) blue = (byte) b;		
	}
	
    public void display(GLAutoDrawable drawable) {
    	
    	GL2 gl = drawable.getGL().getGL2();
    	gl.glPushMatrix();
    	gl.glTranslatef((float)transx, (float)transy, (float)transz);
    	super.display(drawable);
    	gl.glScaled(scalex, scaley, scalez);
    	gl.glEnable(gl.GL_COLOR_MATERIAL);
    	gl.glColor3ub(red, green, blue);
    	gl.glRasterPos3f( .3f,.3f,.3f );    	
    	glut.glutSolidSphere(0.5, 100, 100);
    	gl.glDisable(gl.GL_COLOR_MATERIAL);
    	gl.glPopMatrix();
    }
    		
}
