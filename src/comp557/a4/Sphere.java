package comp557.a4;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * A simple sphere class.
 */
public class Sphere extends Intersectable {
    
	/** Radius of the sphere. */
	public double radius = 1;
    
	/** Location of the sphere center. */
	public Point3d center = new Point3d( 0, 0, 0 );
    
    /**
     * Default constructor
     */
    public Sphere() {
    	super();
    }
    
    /**
     * Creates a sphere with the request radius and center. 
     * 
     * @param radius
     * @param center
     * @param material
     */
    public Sphere( double radius, Point3d center, Material material ) {
    	super();
    	this.radius = radius;
    	this.center = center;
    	this.material = material;
    }
    
    @Override
    public void intersect(Ray ray, IntersectResult result) {
    
    	//Get the intersection point
    	double a = ray.viewDirection.dot(ray.viewDirection); 	
		Vector3d eye = new Vector3d(ray.eyePoint);
		eye.sub(center);
		double b = ray.viewDirection.dot(eye) * 2;   			
		double c = eye.dot(eye) - Math.pow(radius, 2);
		double rooted = Math.pow(b, 2) - (4*a*c);
		if(rooted < 0) return;

		double t = Double.POSITIVE_INFINITY;
		double t1 = (-b + Math.sqrt(rooted)) / (2*a);   		
		double t2 = (-b - Math.sqrt(rooted)) / (2*a); 
		if (t1 < t2) {
			if (t1 > 0) t = t1;
		}
		else {
			if (t2 > 0) t = t2;
		}		
		if (t > result.t) return;
		Point3d intersectpoint = new Point3d();
		ray.getPoint(t, intersectpoint); 		
		result.p = intersectpoint;
		result.material = this.material;
		result.t = t;

		Vector3d norm = new Vector3d(intersectpoint);
		norm.sub(center);
		norm.scale(1/this.radius);
		result.n.set(norm);
	
    }
    
}
