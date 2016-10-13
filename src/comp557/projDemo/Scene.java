package comp557.projDemo;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector4d;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;

import mintools.parameters.BooleanParameter;
import mintools.parameters.DoubleParameter;
import mintools.swing.VerticalFlowPanel;
import mintools.viewer.EasyViewer;
import mintools.viewer.FancyAxis;
import mintools.viewer.FlatMatrix4d;
import mintools.viewer.SceneGraphNode;

public class Scene implements SceneGraphNode {

	enum ViewMode { WORLD, EYE, NDC, SCREEN }
			
	FancyAxis fa = new FancyAxis();
	GLU glu = new GLU();
	GLUT glut = new GLUT();
	
	@Override
	public void display(GLAutoDrawable drawable) {
		// this will probably not be used
		display( drawable, ViewMode.WORLD );
	}
	
	private float[][] colours = new float[][] {
			{.75f,1,1,1},
			{1,.75f,1,1},
			{1,1,.75f,1},
			{.75f,.75f,1,1},
			{1,.75f,.75f,1},
			{0.25f,0.25f,0.25f,1},
			{.75f,1,.75f,1}
		};
	final float[] red = {1,0,0,1};
	final float[] green = {0,1,0,1};
	final float[] blue = {0,0,1,1};
	final float[] white = {1,1,1,1};
	final float[] grey = {0.75f,0.75f,0.75f,1f};

	public void display( GLAutoDrawable drawable, ViewMode mode ) {

		GL2 gl = drawable.getGL().getGL2();
		
		// allocate locally to avoid multi-threading issues?
		FlatMatrix4d V = new FlatMatrix4d();
		FlatMatrix4d Vinv = new FlatMatrix4d();
		FlatMatrix4d P = new FlatMatrix4d();
		FlatMatrix4d Pinv = new FlatMatrix4d();

		gl.glPushMatrix();
		
		// make and get the viewing transformation
		gl.glLoadIdentity();		
		glu.gluLookAt( eye.x, eye.y, eye.z, lookAt.x, lookAt.y, lookAt.z, up.x, up.y, up.z );
		gl.glGetDoublev( GL2.GL_MODELVIEW_MATRIX, V.asArray(), 0 );
		
		// make and get the projection transformation
		if ( zNear.getValue() > zFar.getValue() - 0.1 ) {
			zFar.setValue( zNear.getValue() + 0.1 ); // avoid exceptions!
		}
		gl.glLoadIdentity();
		gl.glFrustum(left.getValue(), right.getValue(), bottom.getValue(), top.getValue(), zNear.getValue(), zFar.getValue() );
		gl.glGetDoublev( GL2.GL_MODELVIEW_MATRIX, P.asArray(), 0 );
		gl.glPopMatrix();
		
		V.reconstitute();
		P.reconstitute();
		Vinv.getBackingMatrix().invert( V.getBackingMatrix() );
		Pinv.getBackingMatrix().invert( P.getBackingMatrix() );
				
		if ( mode == ViewMode.WORLD ) {
			// do nothing
		} else if ( mode == ViewMode.EYE ) {
			gl.glMultMatrixd( V.asArray(), 0 );
		} else if ( mode == ViewMode.NDC ) {
			gl.glMultMatrixd( P.asArray(), 0 );
			gl.glMultMatrixd( V.asArray(), 0 );
			gl.glDisable( GL2.GL_LIGHTING );
			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2.GL_LINE);
		} else if ( mode == ViewMode.SCREEN ) {
			gl.glMatrixMode( GL2.GL_PROJECTION );
			gl.glLoadMatrixd( P.asArray(), 0 );
			gl.glMatrixMode( GL2.GL_MODELVIEW );
			gl.glLoadMatrixd( V.asArray(), 0 );
		}
		
		drawAll( drawable, Pinv, Vinv, mode );
		if ( mode == ViewMode.NDC ) {
			
			// clipping happens in homogeneous coordinates before 
			// the perspective division, so all the points with w negative are lost!
			// if we want to see them, then we need to redraw the whole
			// scene with a -1 scale on x, y, z, and w
			
			FlatMatrix4d S = new FlatMatrix4d();
			Matrix4d SM = S.getBackingMatrix();
			SM.setIdentity();
			SM.mul(-1);
			gl.glMultMatrixd( S.asArray(), 0 );
			
			drawAll( drawable, Pinv, Vinv, mode );			
	
			if ( showMatrix.getValue() ) {
				EasyViewer.beginOverlay(drawable);
				gl.glRasterPos2d(40, 40);
				Matrix4d PM = P.getBackingMatrix(); 
				String text = PM.toString() + "\n \n";
				Vector4d v = new Vector4d();
				Vector4d vt = new Vector4d();
				test.get(v);
				PM.transform(v, vt);
				text += v + " -> \n" + vt + "\n";
				EasyViewer.printTextLines( drawable,  text, 40, 40, 13, GLUT.BITMAP_8_BY_13 );
				EasyViewer.endOverlay(drawable);
			}
		}
	}
	
	private void drawAll(GLAutoDrawable drawable, FlatMatrix4d Pinv, FlatMatrix4d Vinv, ViewMode mode ) {
		GL2 gl = drawable.getGL().getGL2();
		
		fa.draw( gl );
		if ( mode == ViewMode.NDC ) { gl.glDisable( GL2.GL_LIGHTING ); }
		
		gl.glPushMatrix();
		gl.glTranslated( lookAt.x, lookAt.y, lookAt.z );
		gl.glColor3f( 0, 0, 1 );
        gl.glMaterialfv( GL.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, blue, 0 );
		glut.glutSolidSphere(0.25, 16, 10 );
		gl.glPopMatrix();
		
		if ( drawPlane.getValue() ) {
			float y = -1;
			gl.glColor3f( .7f, .7f, .7f );
			gl.glMaterialfv( GL.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, white, 0 );
	        gl.glMaterialf( GL.GL_FRONT_AND_BACK, GL2.GL_SHININESS, 127 );
	        for ( int i = -20; i < 20; i++ ) {
	        	for ( int j = -20; j <= 10; j++ ) {
	                gl.glBegin( GL2.GL_QUAD_STRIP );
	                gl.glMaterialfv( GL.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, ((i+j)%2)==0?grey:white, 0 );
	                gl.glNormal3f(0,1,0);
	                gl.glVertex3d( i, y, j );
	                gl.glVertex3d( i, y, j+1 );
	                gl.glVertex3d( i+1, y, j );
	                gl.glVertex3d( i+1, y, j+1 );        
	                gl.glEnd();
	        	}
	        }
		}
	        
		if ( drawObjects.getValue() ) {
	        gl.glPushMatrix();
	        gl.glTranslated(-3.5,0,3.5);
	        gl.glMaterialfv( GL.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, colours[0], 0 );
	        gl.glColor4fv( colours[0], 0 );
	        gl.glPushMatrix();                
	        gl.glRotated( -10, 0,1,0);
	        gl.glTranslated( 0,-.25,0);
	        glut.glutSolidTeapot(1);
	        gl.glPopMatrix();
	        
	        gl.glTranslated(0.85,0,-3);
	        gl.glRotated(-30,0,1,0);
	        gl.glMaterialfv( GL.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, colours[1], 0 );
	        gl.glColor4fv( colours[1], 0 );
	        glut.glutSolidCylinder(1, 1, 20, 20);
	        
	        gl.glTranslated(0.85,0,-3);
	        gl.glMaterialfv( GL.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, colours[2], 0 );
	        gl.glColor4fv( colours[2], 0 );
	        glut.glutSolidDodecahedron();
	
	        gl.glTranslated(0.85,0,-3);
	        gl.glMaterialfv( GL.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, colours[3], 0 );
	        gl.glColor4fv( colours[3], 0 );
	        glut.glutSolidRhombicDodecahedron();
	        
	        gl.glTranslated(0.85,0,-3);
	        gl.glMaterialfv( GL.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, colours[4], 0 );
	        gl.glColor4fv( colours[4], 0 );
	        glut.glutSolidCone(1, 2, 10, 10);
	        
	        gl.glTranslated(0.85,0,-3);
	        gl.glMaterialfv( GL.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, colours[5], 0 );
	        gl.glColor4fv( colours[5], 0 );
	        glut.glutSolidSphere(1, 20, 20 );
	        
	        gl.glMaterialfv( GL.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, colours[6], 0 );
	        gl.glColor4fv( colours[6], 0 );
	        gl.glTranslated( 1,0,-3 );
	        gl.glRotated(-45,0,1,0);
	        glut.glutSolidTorus( 0.25, 0.75, 10, 25 );	
	        gl.glPopMatrix();
		}
        
		gl.glPushMatrix();
		gl.glMultMatrixd( Vinv.asArray(), 0 ); /**** EYE SPACE NOW */
		
		fa.draw( gl );		

		gl.glDisable( GL2.GL_LIGHTING );
		gl.glPointSize(10);
		gl.glColor3f(1,1,1);
		gl.glBegin( GL.GL_POINTS );
		gl.glVertex4f( test.x, test.y, test.z, test.w );
		gl.glEnd();
		gl.glEnable(GL2.GL_LIGHTING);
		
		if ( mode == ViewMode.NDC ) { gl.glDisable( GL2.GL_LIGHTING ); }
		
		if ( showPair.getValue() ) {
			// draw points shooting off to infinity in both directions
			double d = System.nanoTime()/4/1e9;
			d = d - Math.floor(d);
			d = 1/(1-d)-1;
			gl.glPushMatrix();
			gl.glTranslated(0,0,d);
			gl.glColor3f( 1, 0, 0 );
	        gl.glMaterialfv( GL.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, red, 0 );
			glut.glutSolidSphere(0.25, 16, 10 );		
			gl.glPopMatrix();
			gl.glPushMatrix();
			gl.glTranslated(0,0,-d);
			gl.glColor3f( 0, 1, 0 );
	        gl.glMaterialfv( GL.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, green, 0 );
			glut.glutSolidSphere(0.25, 16, 10 );		
			gl.glPopMatrix();
		}
		
		gl.glMultMatrixd( Pinv.asArray(), 0 ); // ***** POST PROJECTION SPACE *
		gl.glDisable( GL2.GL_LIGHTING );
		gl.glColor3f(1,1,1);
		glut.glutWireCube(2);
		if ( mode == ViewMode.NDC ) {
			gl.glRasterPos3d( 1.1, 1.1, 1.1 );
            glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "(1,1,1)" );
			gl.glRasterPos3d( -1.1, -1.1, -1.1 );
            glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "(-1,-1,-1)" );
		}
		gl.glEnable( GL2.GL_LIGHTING );
		gl.glPopMatrix();
	}
	
	Vec3Parameter eye = new Vec3Parameter("eye (world space)", 0, 0, 2);
	Vec3Parameter lookAt = new Vec3Parameter("look at (world space)", 0, 0, -5);
	Vec3Parameter up = new Vec3Parameter("up (world space)", 0, 1, 0);
	
	Vec4Parameter test = new Vec4Parameter( "test (camera space)", 0, 0, -1, 1 );
	
	DoubleParameter left = new DoubleParameter( "left", -1, -10, 10 );
	DoubleParameter right = new DoubleParameter( "right", 1, -10, 10 );
	DoubleParameter top = new DoubleParameter( "top", 1, -10, 10 );
	DoubleParameter bottom = new DoubleParameter( "bottom", -1, -10, 10 );
	DoubleParameter zNear = new DoubleParameter( "near", 1, 0.1, 10 );
	DoubleParameter zFar = new DoubleParameter( "far", 3, 0.1, 20 );
	
	BooleanParameter drawPlane = new BooleanParameter( "draw plane", true );
	BooleanParameter drawObjects = new BooleanParameter( "draw objects", true );
	BooleanParameter showMatrix = new BooleanParameter("show P", false );
	BooleanParameter showPair = new BooleanParameter("show pair", false );
	
	@Override
	public JPanel getControls() {
		VerticalFlowPanel vfp = new VerticalFlowPanel();

		// view controls
		vfp.add( eye );
		vfp.add( lookAt );
		vfp.add( up );
		
		// scene controls

		vfp.add( drawPlane.getControls() );
		vfp.add( drawObjects.getControls() );
		vfp.add( showMatrix.getControls() );
		vfp.add( showPair.getControls() );

		vfp.add( test );
		
		// projection controls
		VerticalFlowPanel vfp1 = new VerticalFlowPanel();
		vfp1.setBorder( new TitledBorder( "Frustum" ) );
		vfp1.add( left.getSliderControls(false) );
		vfp1.add( right.getSliderControls(false) );
		vfp1.add( top.getSliderControls(false) );
		vfp1.add( bottom.getSliderControls(false) );
		vfp1.add( zNear.getSliderControls(false) );
		vfp1.add( zFar.getSliderControls(false) );
		vfp.add( vfp1.getPanel() );
				
		return vfp.getPanel();
	}
	
	@Override
	public void init(GLAutoDrawable drawable) {
		// nothing
	}
	
	public Scene() {

	}
	
}
