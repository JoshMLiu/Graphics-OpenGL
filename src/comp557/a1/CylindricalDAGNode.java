package comp557.a1;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.fixedfunc.GLLightingFunc;

// Josh Liu ID:260612384

// Renders a cylinder with scaling and color
public class CylindricalDAGNode extends DAGNode {

	// translation relative to the parent
	double transx, transy, transz, scalex, scaley, scalez = 0d;
	
	// rgb values of cylinder
	byte red = (byte) 234;
	byte green = (byte) 192;
	byte blue = (byte) 134;
		
	// takes in name, translation, scale (order x, y, z)
	public CylindricalDAGNode(String n, double tx, double ty, double tz, double sx, double sy, double sz) {	
		name = n;
		transx = tx;
		transy = ty;
		transz = tz;
		scalex = sx;
		scaley = sy;
		scalez = sz;
	}
	
	public void setColor(int r, int g, int b) {
		if (r >= 0 && r <= 255) red = (byte) r;
		if (g >= 0 && g <= 255) green = (byte) g;
		if (b >= 0 && b <= 255) blue = (byte) b;		
	}
	
	// automatically rotates so that cylinder is facing up (like sitting on a table)
    public void display(GLAutoDrawable drawable) {
    	
    	GL2 gl = drawable.getGL().getGL2();
    	gl.glPushMatrix();
    	gl.glTranslatef((float)transx, (float)transy, (float)transz);
    	super.display(drawable);
    	gl.glScaled(scalex, scaley, scalez);
    	gl.glRotatef(90f, 1.0f, 0.0f, 0.0f);
    	gl.glEnable(GLLightingFunc.GL_COLOR_MATERIAL);
    	gl.glColor3ub(red, green, blue);
    	gl.glRasterPos3f( .3f,.3f,.3f );    	
    	glut.glutSolidCylinder(0.5d, 1.0f, 100, 100);
    	gl.glDisable(GLLightingFunc.GL_COLOR_MATERIAL);
    	gl.glPopMatrix();
    }
	
}
