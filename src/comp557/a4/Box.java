package comp557.a4;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * A simple box class. A box is defined by it's lower (@see min) and upper (@see max) corner. 
 */
public class Box extends Intersectable {

	public Point3d max;
	public Point3d min;
	
    /**
     * Default constructor. Creates a 2x2x2 box centered at (0,0,0)
     */
    public Box() {
    	super();
    	this.max = new Point3d( 1, 1, 1 );
    	this.min = new Point3d( -1, -1, -1 );
    }	

	@Override
	public void intersect(Ray ray, IntersectResult result) {
		
		//Get the axis coords using the ray
		Vector3d d = ray.viewDirection;
		double aXMin = (this.min.x - ray.eyePoint.x)/d.x;
		double aXMax = (this.max.x - ray.eyePoint.x)/d.x;
		double aYMin = (this.min.y - ray.eyePoint.y)/d.y;
		double aYMax = (this.max.y - ray.eyePoint.y)/d.y;
		double aZMin = (this.min.z - ray.eyePoint.z)/d.z;
		double aZMax = (this.max.z - ray.eyePoint.z)/d.z;
		
		double tMinX = Math.min(aXMin, aXMax);
		double tMinY = Math.min(aYMin, aYMax);
		double tMinZ = Math.min(aZMin, aZMax);
		double tMaxX = Math.max(aXMin, aXMax);
		double tMaxY = Math.max(aYMin, aYMax);
		double tMaxZ = Math.max(aZMin, aZMax);
		
		//Find the first axis that intersects
		double tMin, tMax;
		Vector3d n = new Vector3d();
		if (tMinX > tMinY) {
			n.set(1, 0, 0); 
			tMin = tMinX; 
			if (ray.eyePoint.x < this.min.x) n.negate();
		}
		else { 
			n.set(0, 1, 0); 
			tMin = tMinY; 
			if (ray.eyePoint.y < this.min.y) n.negate(); 
		}
		if (tMinZ > tMin) { 
			n.set(0, 0, 1); 
			tMin = tMinZ; 
			if (ray.eyePoint.z < this.min.z) n.negate(); 
		}
		tMax = Math.min(Math.min(tMaxX, tMaxY), tMaxZ);
		
		if (tMin < 0 || tMin > result.t || tMax - tMin < 0) return;	
		
		Vector3d intersectionpoint = new Vector3d(d);
		intersectionpoint.scale(tMin);
		Point3d p = new Point3d(ray.eyePoint);
		p.add(intersectionpoint);
		
		result.n.set(n);
		result.p.set(p);
		result.t = tMin;
		result.material = this.material;
	}	

}
