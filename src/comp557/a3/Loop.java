package comp557.a3;

import java.util.ArrayList;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * Class implementing the Loop subdivision scheme     
 * 
 * Josh Liu ID:260612384
 */
public class Loop {

    /**
     * Subdivides the provided half edge data structure
     * @param heds the mesh to subdivide
     * @return the subdivided mesh
     */
    public static HEDS subdivide( HEDS heds ) {
        
    	HEDS heds2 = new HEDS();

    	// adds the even and odd children to the first HEDS
    	evenOddChildren(heds);
    	
    	// Connecting to make the new HEDS of the subdivided shape
    	ArrayList<HalfEdge> faces = (ArrayList<HalfEdge>) heds.faces;
    	ArrayList<HalfEdge> faces2 = new ArrayList<HalfEdge>(); 
    	
    	for (HalfEdge f: faces) {
    		
    		// Manually get all the edges of the inside triangle and surrounding 3 triangles
    		// Attach everything in the right places
    		HalfEdge current = f;
    		
    		HalfEdge rightedge = new HalfEdge();
    		HalfEdge leftedge = new HalfEdge();
    		HalfEdge bottomedge = new HalfEdge();
    		
    		HalfEdge insideright = new HalfEdge();
    		HalfEdge insideleft = new HalfEdge();
    		HalfEdge insidedown = new HalfEdge();
    			
    		HalfEdge firstedge1 = current.child1;
    		HalfEdge firstedge2 = current.child2;
    		current = current.next;
    		HalfEdge secondedge1 = current.child1;
    		HalfEdge secondedge2 = current.child2;
    		current = current.next;
    		HalfEdge thirdedge1 = current.child1;
    		HalfEdge thirdedge2 = current.child2;
    		
    		insideright.head = firstedge1.head;
    		insideright.next = insideleft;
    		insideleft.head = secondedge1.head;
    		insideleft.next = insidedown;
    		insidedown.head = thirdedge1.head;
    		insidedown.next = insideright;
    		
    		insideright.twin = rightedge;
    		insideleft.twin = leftedge;
    		insidedown.twin = bottomedge;
    		
    		firstedge1.next = rightedge;
    		rightedge.twin = insideright;
    		rightedge.next = thirdedge2;
    		rightedge.head = thirdedge1.head;
    		thirdedge2.next = firstedge1;

    		secondedge1.next = leftedge;
    		leftedge.twin = insideleft;
    		leftedge.next = firstedge2;
    		leftedge.head = firstedge1.head;
    		firstedge2.next = secondedge1;

    		thirdedge1.next = bottomedge;
    		bottomedge.twin = insidedown;
    		bottomedge.next = secondedge2;
    		bottomedge.head = secondedge1.head;
    		secondedge2.next = thirdedge1;

    		// Add one edge from each new triangle (there's 4) to the new HEDS
    		faces2.add(firstedge2);
    		faces2.add(secondedge2);
    		faces2.add(thirdedge2);
    		faces2.add(insideright);
    		
    	}
    	
    	heds2.faces = faces2;
    	
    	// Set the normals of the vertices of the new HEDS
    	setNormals(heds2);
        return heds2;        
    }
    
    // Sets normals of vertices in a given HEDS, also used by HEDS. Could also be in HEDS class (probably better...)
    public static void setNormals(HEDS heds) {
    	
    	ArrayList<HalfEdge> faces = (ArrayList<HalfEdge>) heds.faces;
    	for (HalfEdge f: faces) {
    		HalfEdge start = f;
    		for (int i = 0; i < 3; i++) {
    			HalfEdge current = start;
    			// skip if norm was already caculated for a vertex
    			if (current.head.n != null) {
    				start = start.next;
    				continue;
    			}
    			
    			// get number of neighbours, stops when it makes a full loop
    			int neighbours = 1;
    			boolean boundary = false;
    			HalfEdge last = new HalfEdge();
    			do {
    				last = current;
    				current = current.next;
    				current = current.twin;
    				if (current == null) {
    					boundary = true;
    					break;
    				}
    				neighbours++;
    			} while (!current.equals(start));
    			
    			// if a boundary was found then go to the first boundary edge
    			if (boundary) {
    				current = start;
    				while(current.twin != null) {
    					current = current.twin;
    					current = current.prev();
    					neighbours++;
    				}
    			}
    			else {
        			current = start;
    			}
    			
    			// boundary normal rules
    			if (boundary) {
    				Point3d fv = current.prev().head.p;
    				Point3d lv = last.prev().head.p;				
    				Vector3d talong = new Vector3d(fv.x-lv.x, fv.y-lv.y, fv.z-lv.z);
    				
    				double x = 0;
    				double y = 0;
    				double z = 0;
    				
    				if (neighbours == 2) {
    					x = fv.x + lv.x - start.head.p.x;
    					y = fv.y + lv.y - start.head.p.y;
    					z = fv.z + lv.z - start.head.p.z;
    				}
    				else if (neighbours == 3) {
    					current = current.next;
    					current = current.twin;
    					Point3d sv = current.prev().head.p;
    					x = sv.x - start.head.p.x;
    					y = sv.y - start.head.p.y;
    					z = sv.z - start.head.p.z;
    				}
    				else if (neighbours == 4) {
    					double theta = Math.PI/(double)(neighbours-1);
    					current = current.next;
    					current = current.twin;
    					for (int k = 1; k < neighbours - 1; k++) {
    						Point3d tp = current.prev().head.p;
    						x += Math.sin(((double)k)*theta)*tp.x;
    						y += Math.sin(((double)k)*theta)*tp.y;
    						z += Math.sin(((double)k)*theta)*tp.z;
    						current = current.next;
        					current = current.twin;
    					}
    					double mult = 2*Math.cos(theta) - (double)2;
    					x = Math.sin(theta)*(fv.x-lv.x) + mult*x;
    					y = Math.sin(theta)*(fv.y-lv.y) + mult*y;
    					z = Math.sin(theta)*(fv.z-lv.z) + mult*z;
    				}
    				Vector3d taccross = new Vector3d(x, y, z);
    				// Did this clockwise by accident but reversed the cross product here
    				talong.cross(taccross, talong);
    				start.head.n = talong;
    			}
    			else {
    				// inside normal rules
    				double x1 = 0;
    				double y1 = 0;
    				double z1 = 0;
    				double x2 = 0;
    				double y2 = 0;
    				double z2 = 0;
    				for (int j = 0; j < neighbours; j++) {
    					current = current.prev();
    					Vertex v = current.head;
    					double mult1 = Math.cos((2*Math.PI*j)/(double)neighbours);
    					double mult2 = Math.sin((2*Math.PI*j)/(double)neighbours);
    					x1 += mult1*v.p.x;
    					y1 += mult1*v.p.y;
    					z1 += mult1*v.p.z;
    					x2 += mult2*v.p.x;
    					y2 += mult2*v.p.y;
    					z2 += mult2*v.p.z;
    				}
    				Vector3d tan1 = new Vector3d(x1, y1, z1);
    				Vector3d tan2 = new Vector3d(x2, y2, z2);
    				// Did it clockwise by accident but just reversed the cross product here
    				tan1.cross(tan2, tan1);
    				start.head.n = tan1;
    			}
    			start = start.next;
    		}
    		
    		
    	}
    }
    
    // For subdivision using Loop's rules (actually Warren's rules)
    public static double beta(int n) {
    	if (n > 3) return 3/(8*(double)n);
    	else return 3/(double)16;
    }
    
    // gets children of the edges of a HEDS
    private static void evenOddChildren(HEDS heds) {
    	
        ArrayList<HalfEdge> faces = (ArrayList<HalfEdge>) heds.faces;
        
        // Sets even vertices for boundary edges (and all edges connected to the vertex)
        for (HalfEdge f: faces) {
        	HalfEdge start = f;
        	for (int i = 0; i < 3; i++) {
        		HalfEdge current = start;
        		// Edge is a boundary
        		if (current.twin == null) {	
            		Vertex middle = current.head;
            		current = current.next;
            		current = current.next;
            		Vertex right = current.head;
            		current = current.next;
            		
            		HalfEdge temp = current;
            		temp = temp.next;
            		while (temp.twin != null) {   			
            			temp = temp.twin;
            			temp = temp.next;
            		}
            		Vertex left = temp.head;
            		
            		Vertex even = new Vertex();
            		even.p = new Point3d((left.p.x+right.p.x)/8 + 3*(middle.p.x)/4,
   							 			 (left.p.y+right.p.y)/8 + 3*(middle.p.y)/4,
   							 			 (left.p.z+right.p.z)/8 + 3*(middle.p.z)/4);
            		start.head.child = even;            		     
        		}
        		start = start.next;
        	}
        }
        
        // Set even and odd vertices and creates appropriate half edges
        for (HalfEdge f: faces) {
        	HalfEdge start = f;
            for (int i = 0; i < 3; i++) {          	
            	HalfEdge current = start;
            	ArrayList<Vertex> neighbours = new ArrayList<Vertex>();
            	
            	// boundary tells whether the edge is in a loop containing a boundary edge
            	// immediate boundary tells whether the starting edge is a boundary
            	boolean boundary = false;
            	boolean immediateboundary = false;
            	
            	if (start.twin == null) immediateboundary = true;
            	
            	// find number of neighbours of if the edge is a boundary
            	do {
            		if (current.twin == null) {
            			boundary = true;
            			break;
            		}
                	current = current.next;
                	current = current.next;
                	neighbours.add(current.head);
                	current = current.next;
                	current = current.twin;
                	current = current.next;
                	current = current.next;
            	} while (!current.equals(start));	
            	
            	HalfEdge evenedge = new HalfEdge();
            	current = start;
            	
            	// calculate odd vertices from Loop rules, even vertex already calculated
            	if (boundary) {
            		       		
            		Vertex odd = new Vertex();
            		
            		Vertex middle = current.head;
            		current = current.next;
            		current = current.next;
            		Vertex right = current.head;
            		current = current.next;
            		
            		if (immediateboundary) {
            			odd.p = new Point3d((middle.p.x+right.p.x)/2, (middle.p.y+right.p.y)/2, (middle.p.z+right.p.z)/2);
            			HalfEdge c1 = new HalfEdge();
                		c1.head = odd;
                		start.child1 = c1;
                		c1.parent = start;
            		}
            		
            		evenedge.parent = start;    		
            		evenedge.head = start.head.child;          		       		
            		
            	} 
            	else {
            		
            		// get even vertex for inside vertex
            		int n = neighbours.size();
                	double beta = beta(n);
                	Point3d oldvert = start.head.p;
                	double x = oldvert.x;
                	double y = oldvert.y;
                	double z = oldvert.z;
                	x = (1-n*beta)*x;
                	y = (1-n*beta)*y;
                	z = (1-n*beta)*z;
                	Point3d newvert = new Point3d();
                	newvert.add(new Point3d(x,y,z));
                	for (Vertex vert: neighbours) {
                		Point3d oldneighbour = vert.p;
                		x = beta*oldneighbour.x;
                		y = beta*oldneighbour.y;
                		z = beta*oldneighbour.z;
                		newvert.add(new Point3d(x,y,z));
                	}
                	Vertex child = new Vertex();
                	child.p = newvert;
                	start.head.child = child;
                	evenedge = new HalfEdge();
                	evenedge.head = child;
                	evenedge.parent = start;
            	}
            	
            	if (!immediateboundary) {
            		// Calculate odd vertex for a boundary
                	Vertex odd = new Vertex();
                	Vertex oddup, odddown, oddleft, oddright;
                	current = start;
                	oddleft = current.head;
                	current = current.next;
                	odddown = current.head;
                	current = current.next;
                	oddright = current.head;
                	current = current.next;
                	current = current.twin;
                	current = current.next;
                	oddup = current.head;
                	
                	double oddx = (oddup.p.x + odddown.p.x)/8 + 3*(oddleft.p.x + oddright.p.x)/8;
                	double oddy = (oddup.p.y + odddown.p.y)/8 + 3*(oddleft.p.y + oddright.p.y)/8;
                	double oddz = (oddup.p.z + odddown.p.z)/8 + 3*(oddleft.p.z + oddright.p.z)/8;
                	
                	odd.p = new Point3d(oddx, oddy, oddz);
                	HalfEdge oddedge = new HalfEdge();
                	oddedge.head = odd;
                	oddedge.parent = start;
                	start.child1 = oddedge;
            	}     	
            	start.child2 = evenedge;
            	// Go to next edge in face
            	start = start.next;
            }       	
        }
        
        // Sets twins for each edge (children), must be done after because all edges must be created first
        for (HalfEdge f: faces) {
        	HalfEdge current = f;
        	for (int i = 0; i < 3; i++) {
        		
        		// Boundary edge children have no twin
        		HalfEdge twin = current.twin; 
        		if (twin == null) {
        			current = current.next;
        			continue;
        		}
        		
        		HalfEdge original1 = current.child1;
        		HalfEdge twin1 = twin.child2;
        		original1.twin = twin1;
        		twin1.twin = original1;
        		
        		HalfEdge original2 = current.child2;
        		HalfEdge twin2 = twin.child1;
        		original2.twin = twin2;
        		twin2.twin = original2; 
        		
        		current = current.next;
        	}        	
        }
        
    }
    
}
  
