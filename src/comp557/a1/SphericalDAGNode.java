package comp557.a1;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.fixedfunc.GLLightingFunc;

// Josh Liu ID:260612384

// Renders a spherical shaped node with color and scaling
public class SphericalDAGNode extends DAGNode {
	
	// translation values are relative to the parent node
	double transx, transy, transz, scalex, scaley, scalez = 0d;
	
	// RGB color values of the sphere
	byte red = (byte) 234;
	byte green = (byte) 192;
	byte blue = (byte) 134;
	
	// takes in a name, translation relative to the parent, and values to scale by (order x, y, z)
	public SphericalDAGNode(String n, double tx, double ty, double tz, double sx, double sy, double sz) {	
		name = n;
		transx = tx;
		transy = ty;
		transz = tz;
		scalex = sx;
		scaley = sy;
		scalez = sz;
	}
	
	// takes in int values of rgb values
	public void setColor(int r, int g, int b) {
		if (r >= 0 && r <= 255) red = (byte) r;
		if (g >= 0 && g <= 255) green = (byte) g;
		if (b >= 0 && b <= 255) blue = (byte) b;		
	}
	
	// passes translate to children but not scale
    public void display(GLAutoDrawable drawable) {
    	
    	GL2 gl = drawable.getGL().getGL2();
    	gl.glPushMatrix();
    	gl.glTranslatef((float)transx, (float)transy, (float)transz);
    	super.display(drawable);
    	gl.glScaled(scalex, scaley, scalez);
    	gl.glEnable(GLLightingFunc.GL_COLOR_MATERIAL);
    	gl.glColor3ub(red, green, blue);
    	gl.glRasterPos3f( .3f,.3f,.3f );    	
    	glut.glutSolidSphere(0.5, 100, 100);
    	gl.glDisable(GLLightingFunc.GL_COLOR_MATERIAL);
    	gl.glPopMatrix();
    }
    		
}
