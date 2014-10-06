package ray.surface;

import ray.accel.AxisAlignedBoundingBox;
import ray.material.Material;
import ray.math.Geometry;
import ray.math.Point2;
import ray.math.Point3;
import ray.math.Vector3;
import ray.misc.IntersectionRecord;
import ray.misc.LuminaireSamplingRecord;
import ray.misc.Ray;

/**
 * Represents a sphere as a center and a radius.
 *
 * @author ags
 */
public class Sphere extends Surface {
    
    /** Material for this sphere. */
    protected Material material = Material.DEFAULT_MATERIAL;
    
    /** The center of the sphere. */
    protected final Point3 center = new Point3();
    
    /** The radius of the sphere. */
    protected double radius = 1.0;
    
    /**
     * Default constructor, creates a sphere at the origin with radius 1.0
     */
    public Sphere() {        
    }
    
    /**
     * The explicit constructor. This is the only constructor with any real code
     * in it. Values should be set here, and any variables that need to be
     * calculated should be done here.
     *
     * @param newCenter The center of the new sphere.
     * @param newRadius The radius of the new sphere.
     * @param newMaterial The material of the new sphere.
     */
    public Sphere(Vector3 newCenter, double newRadius, Material newMaterial) {        
        material = newMaterial;
        center.set(newCenter);
        radius = newRadius;
        updateArea();
    }
    
    public void updateArea() {
    	area = 4 * Math.PI * radius*radius;
    	oneOverArea = 1. / area;
    }
    
    /**
     * @see ray1.surface.Surface#getMaterial()
     */
    public Material getMaterial() {
        return material;
    }
    
    /**
     * @see ray1.surface.Surface#setMaterial(ray1.material.Material)
     */
    public void setMaterial(Material material) {
        this.material = material;
    }
    
    /**
     * Returns the center of the sphere in the input Point3
     * @param outPoint output space
     */
    public void getCenter(Point3 outPoint) {        
        outPoint.set(center);        
    }
    
    /**
     * @param center The center to set.
     */
    public void setCenter(Point3 center) {        
        this.center.set(center);        
    }
    
    /**
     * @return Returns the radius.
     */
    public double getRadius() {
        return radius;
    }
    
    /**
     * @param radius The radius to set.
     */
    public void setRadius(double radius) {
        this.radius = radius;
        updateArea();
    }
    
    public boolean chooseSamplePoint(Point3 p, Point2 seed, LuminaireSamplingRecord lRec) {
        Geometry.squareToSphere(seed, lRec.frame.w);
        lRec.frame.o.set(center);
        lRec.frame.o.scaleAdd(radius, lRec.frame.w);
        lRec.frame.initFromW();
        lRec.pdf = oneOverArea;
        lRec.emitDir.sub(p, lRec.frame.o);
        return true;
    }
        
    /**
     * @see ray1.surface.Surface#intersect(ray1.misc.IntersectionRecord,
     *      ray1.misc.Ray)
     */
    public boolean intersect(IntersectionRecord outRecord, Ray ray) {
        // W4160 TODO (A)
    	// In this function, you need to test if the given ray intersect with a sphere.
    	// You should look at the intersect method in other classes such as ray.surface.Triangle.java
    	// to see what fields of IntersectionRecord should be set correctly.
    	// The following fields should be set in this function
    	//     IntersectionRecord.t
    	//     IntersectionRecord.frame
    	//     IntersectionRecord.surface
    	//
    	// Note: Although a ray is conceptually a infinite line, in practice, it often has a length,
    	//       and certain rendering algorithm relies on the length. Therefore, here a ray is a 
    	//       segment rather than a infinite line. You need to test if the segment is intersect
    	//       with the sphere. Look at ray.misc.Ray.java to see the information provided by a ray.
    	

    	double r = this.getRadius();
    	Vector3 L = new Vector3(ray.direction); 
    	L.normalize(); 
    	Point3 eye = new Point3(ray.origin);

    	Vector3 ec = new Vector3();
    	ec.sub(center, eye);     
 
    	double l1 = ec.dot(L); 
    	double l2 = ec.length();
    	
    	// check if the sphere is in front of the eye
    	if ( l1 < 0)
    		return false;
    	
    	// check if there's intersection
    	if ( l2 * l2 - l1 * l1 > r * r)
    		return false;
    	
    	// intersects at one point
    	else if ((l2 * l2 - l1 * l1) == (r * r))
    	{   		
    		L.scale(l1);  
    		Point3 p = new Point3();
    		p.add(eye, L);
    		
    		Vector3 n = new Vector3();
    		n.sub(p, center);
    		n.normalize();
    		
    		outRecord.t = l1;
    		outRecord.surface = this;
    		outRecord.frame.o.set(p);
    		outRecord.frame.w.set(n);
    		outRecord.frame.initFromW();
    		
    		return true;
    	}
    	// intersects at two points
    	else
    	{
    		double temp = r*r - (l2*l2 - l1*l1);

    		temp = Math.sqrt(temp);
    		double t = l1 - temp;
    		if (t < 0)
    		{
    			t = l1+temp;
    		}
    		
    		L.scale(t);
    		Point3 p = new Point3();
    		p.add(eye, L);
    		

    		Vector3 n = new Vector3();
    		n.sub(p, center);
    		n.normalize();
    		

    		outRecord.t = t;
    		outRecord.surface = this;
    		outRecord.frame.o.set(p);
    		outRecord.frame.w.set(n);
    		outRecord.frame.initFromW();
    		
    		return true;
    	}


    }
    
    /**
     * @see Object#toString()
     */
    public String toString() {
        return "sphere " + center + " " + radius + " " + material + " end";
    }
    
    /**
     * @see ray1.surface.Surface#addToBoundingBox(ray1.surface.accel.AxisAlignedBoundingBox)
     */
    public void addToBoundingBox(AxisAlignedBoundingBox inBox) {
        inBox.add(center.x - radius, center.y - radius, center.z - radius);
        inBox.add(center.x + radius, center.y + radius, center.z + radius);        
    }
    
}
