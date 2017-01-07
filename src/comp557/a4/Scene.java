package comp557.a4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * Simple scene loader based on XML file format.
 */
public class Scene {
    
    /** List of surfaces in the scene */
    public List<Intersectable> surfaceList = new ArrayList<Intersectable>();
	
	/** All scene lights */
	public Map<String,Light> lights = new HashMap<String,Light>();

    /** Contains information about how to render the scene */
    public Render render;
    
    /** The ambient light colour */
    public Color3f ambient = new Color3f();

    /** 
     * Default constructor.
     */
    public Scene() {
    	this.render = new Render();
    }
    
    /**
     * renders the scene
     */
    public void render(boolean showPanel) {
 
        Camera cam = render.camera; 
        int w = cam.imageSize.width;
        int h = cam.imageSize.height;
        
        render.init(w, h, showPanel);
          
        // Calculate best number of areas per pizel to use for super sampling
		int samples = render.samples;
		double root = Math.sqrt(samples);
		int rows = 0;
		int rootFloor = (int) Math.floor(root);
		int rootCeil = (int) Math.ceil(root);
		int squareFloor = (int) Math.pow(rootFloor, 2);
		int squareCeil = (int)Math.pow(rootCeil, 2);
		if (samples - squareFloor < squareCeil - samples) {
			rows = (int) Math.sqrt(squareFloor);
		}
		else {
			rows = (int) Math.sqrt(squareCeil);
		}
		
		// Go through every pixel
        for ( int i = 0; i < h && !render.isDone(); i++ ) {
            for ( int j = 0; j < w && !render.isDone(); j++ ) {
            	
            	// Accumulate the colors and average them for supersampling
            	int tempr = 0;
            	int tempg = 0;
            	int tempb = 0;
            	double samplecenter = 0;
                double[] offset = new double[2];
                
                //Go through each of the areas of the pixel
                for (int k = 0; k < rows; k++) {
                	if (render.jitter) {
                		samplecenter = Math.random()*(1/(double)rows);
                	}
                	else {
                		samplecenter = (1/(double)rows)/2;
                	}
                	//Jitter sets a random offset instead of the default uniform one (middle of the area)
                	offset[0] = k/(double)rows + samplecenter;
                	for (int l = 0; l < rows; l++) {
                		if (render.jitter) {
                    		samplecenter = (Math.random()*(1/(double)rows));
                    	}
                    	else {
                    		samplecenter = (1/(double)rows)/2;
                    	}
                    	offset[1] = l/(double)rows + samplecenter;
                    	
                    	// Get the ray going through the area with offset
                    	Ray ray = new Ray();
                        generateRay(j, i, offset, cam, ray);

                        //Go through every surface and check which surface the ray intersects first
        				IntersectResult closestResult = new IntersectResult();
        				for (Intersectable in: surfaceList) {
        					IntersectResult result = new IntersectResult();
        					in.intersect(ray, result);
        					if (result.t != Double.POSITIVE_INFINITY) {
        						if (closestResult.t == Double.POSITIVE_INFINITY || result.t < closestResult.t) {
        							closestResult = result;							
        						}
        					} 
        				}
        				
        				//Get the color of that surface based on the lighting and material (or draw a background)
                    	Color3f c = new Color3f();
        				if (closestResult.t == Double.POSITIVE_INFINITY) {				
        					c.set(render.bgcolor);
        				}
        				else {	
        					getLightingCol(closestResult, c, ray);
        				}

        				//Add to the cumulative color
                		tempr += 255*c.x;
                		tempg += 255*c.y;
                		tempb += 255*c.z;
                		
                	}
                	
                }
            	       
                //Average the cumulative color of the pixel
            	int r = (int)(tempr/Math.pow(rows, 2));
                int g = (int)(tempg/Math.pow(rows, 2));
                int b = (int)(tempb/Math.pow(rows, 2));
                
				if (r > 255) r = 255;
				if (g > 255) g = 255;
				if (b > 255) b = 255;
				              
                int a = 255;
                int argb = (a<<24 | r<<16 | g<<8 | b);    
                
                // update the render image
                render.setPixel(j, i, argb);
            }
        }
        
        // save the final render image
        render.save();
        
        // wait for render viewer to close
        render.waitDone();
        
    }
    
    //Get the color of an intersection point
    private void getLightingCol(IntersectResult intersection, Color3f c, Ray ray) {

    	//Ambient 
    	c.x += ambient.x*intersection.material.diffuse.x;
		c.y += ambient.y*intersection.material.diffuse.y;
		c.z += ambient.z*intersection.material.diffuse.z;
		
		for (Light l: lights.values()) {	
			
			//No light comes from a shaded object
			if (!inShadow(intersection, l)) {

				Vector3d lightdir = new Vector3d(l.from);
				lightdir.sub(intersection.p);
				lightdir.normalize();
				
				//Lambertian
				c.x += (float)(l.color.x*l.power*intersection.material.diffuse.x*Math.max(0, intersection.n.dot(lightdir)));
				c.y += (float)(l.color.y*l.power*intersection.material.diffuse.y*Math.max(0, intersection.n.dot(lightdir)));
				c.z += (float)(l.color.z*l.power*intersection.material.diffuse.z*Math.max(0, intersection.n.dot(lightdir)));

				Vector3d half = new Vector3d(ray.viewDirection);
				half.negate();
				half.add(lightdir);
				half.normalize();
				
				//Blinn-Phong
				c.x += (float)(l.power*intersection.material.specular.x*Math.pow(Math.max(0, half.dot(intersection.n)), intersection.material.shinyness));
				c.y += (float)(l.power*intersection.material.specular.y*Math.pow(Math.max(0, half.dot(intersection.n)), intersection.material.shinyness));
				c.z += (float)(l.power*intersection.material.specular.z*Math.pow(Math.max(0, half.dot(intersection.n)), intersection.material.shinyness));
			}
		}
    }
    
    /**
     * Generate a ray through pixel (i,j).
     * 
     * @param i The pixel row.
     * @param j The pixel column.
     * @param offset The offset from the center of the pixel, in the range [-0.5,+0.5] for each coordinate. 
     * @param cam The camera.
     * @param ray Contains the generated ray.
     */
	public static void generateRay(final int i, final int j, final double[] offset, final Camera cam, Ray ray) {

		ray.eyePoint.set(cam.from);

		double w = cam.imageSize.width;
		double h = cam.imageSize.height;
		double d  = cam.from.distance(cam.to);
		double screentop = Math.tan(Math.toRadians(cam.fovy)/2.0) * d;

		//Get view area dimensions
		double ratio = w/h;
		double left = -ratio*screentop;
		double right = ratio*screentop;
		double top = screentop;
		double bottom = -screentop;
		double u = left + (right - left)*(i + offset[0])/w; 
		double v = top - (top - bottom)*(j + offset[1])/h; 

		//calculate the view Direction of the ray
		Vector3d cameraZ = new Vector3d(cam.from);
		cameraZ.sub(cam.to);
		cameraZ.normalize();
		Vector3d cameraX = new Vector3d();
		cameraX.cross(cam.up, cameraZ);
		cameraX.normalize();	
		Vector3d cameraY = new Vector3d();
		cameraY.cross(cameraX, cameraZ);
		cameraY.negate();
		cameraY.normalize();	
		cameraX.scale(u);
		cameraY.scale(v);
		cameraZ.scale(d);

		Vector3d s = new Vector3d(cam.from);
		s.add(cameraX);
		s.add(cameraY);
		s.sub(cameraZ);
		s.sub(cam.from);
		s.normalize();
		ray.viewDirection.set(s);
	}

	/**
	 * Shoot a shadow ray in the scene and get the result.
	 * 
	 * @param result Intersection result from raytracing. 
	 * @param light The light to check for visibility.
	 * 
	 * @return True if a point is in shadow, false otherwise. 
	 */
	public boolean inShadow(final IntersectResult result, final Light light) {
		
		Ray shadowRay = new Ray();
		IntersectResult shadowResult = new IntersectResult();
		
		Vector3d l = new Vector3d();
		l.sub(light.from, result.p);
		Vector3d l2 = new Vector3d(l);
		l2.normalize();
		
		//The shadow ray starts a little bit away from the point to prevent weird effects
		Point3d shadowOrigin = new Point3d(result.p);
		Vector3d space = new Vector3d(l2);
		space.scale(0.000001);
		shadowOrigin.add(space);
		
		shadowRay.eyePoint = shadowOrigin;
		shadowRay.viewDirection = new Vector3d(l2);
		double tMax = l.length();
		
		// Go through the other objects and see if one is between the point and the light source
		for (Intersectable in: surfaceList) {
			shadowResult = new IntersectResult();
			in.intersect(shadowRay, shadowResult);
			if (shadowResult.t != Double.POSITIVE_INFINITY) {
				if (shadowResult.t <= tMax) return true;						
			}
		}
		return false;
	}    
}
