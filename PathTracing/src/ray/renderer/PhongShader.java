package ray.renderer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ray.misc.Color;
import ray.misc.IntersectionRecord;
import ray.misc.Ray;
import ray.misc.Scene;
import ray.sampling.SampleGenerator;
import ray.light.*;
import ray.material.*;
import ray.math.Geometry;
import ray.math.Point2;
import ray.math.Point3;
import ray.math.Vector3;
import ray.background.Background;
import ray.background.Uniform;
import ray.brdf.BRDF;
import ray.brdf.Lambertian;
/*  
 * @author Tianyi Ren
*/
public class PhongShader implements Renderer {
    
    private double phongCoeff = 1.5;
    
    public PhongShader() { }
    
    public void setAlpha(double a) {
        phongCoeff = a;
    }
    public double getAlpha()
    {
    	return phongCoeff;
    }
    
    @Override
    public void rayRadiance(Scene scene, Ray ray, SampleGenerator sampler,
            int sampleIndex, Color outColor) {
        // W4160 TODO (A)
        // Here you need to implement the basic phong reflection model to calculate
        // the color value (radiance) along the given ray. The output color value 
        // is stored in outColor. 
        // 
        // For such a simple rendering algorithm, you might not need Monte Carlo integration
        // In this case, you can ignore the input variable, sampler and sampleIndex.
    	
    	IntersectionRecord iRec = new IntersectionRecord();
		
		if (scene.getFirstIntersection(iRec, ray)) 
		{
			List<PointLight> pointLights = new ArrayList<PointLight>();
            pointLights = scene.getPointLights();
    
            Point3 intersectPoint = new Point3(iRec.frame.o);
            Vector3 normal = new Vector3(iRec.frame.w);
            normal.normalize();
            
            Vector3 V = new Vector3(ray.direction);
            V.scale(-1); 
            V.normalize();
            
            Color diffuse = new Color(0.0, 0.0, 0.0);
            Color specular = new Color(0.0, 0.0, 0.0);
            
            for(int i=0; i<pointLights.size(); i++)
            {
            	Color Id = new Color(pointLights.get(i).diffuse);
                Color Is = new Color(pointLights.get(i).specular);
            	
            	Point3 origin = pointLights.get(i).location; 
            	Vector3 L = new Vector3(0.0, 0.0, 0.0);
            	L.sub(origin, intersectPoint);
            	L.normalize();
            	
            	Vector3 R = new Vector3(normal);
            	R.scale(2*L.dot(normal));
            	R.sub(L);
            	R.normalize();
            	

            	Color kd = new Color();
            	BRDF brdf = new Lambertian();
            	brdf.evaluate(iRec.frame, L, R, kd);
            	
            	double LdotN = (double) Math.max(0.0, L.dot(normal));
            	kd.scale(LdotN);
            	kd.scale(Id);
            	diffuse.add(kd);
            	
            	double RdotV = (double) Math.max(0.0, R.dot(V));
            	Is.scale(Math.pow(RdotV,phongCoeff));	
            	specular.add(Is);
            	
            }
            diffuse.add(specular);
            outColor.set(diffuse);

            
		}
		
		if (!scene.getFirstIntersection(iRec, ray)) 
		{
			
            Vector3 V = new Vector3(ray.direction);
            V.scale(-1);
            V.normalize();
			
			Background ka = scene.getBackground();
			Color ambient = new Color();
			ka.evaluate(V, ambient); 
			outColor.set(ambient);
			
		}
    	
    }
}
