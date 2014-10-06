package ray.renderer;

import ray.math.Point2;
import ray.math.Vector3;
import ray.misc.Color;
import ray.misc.IntersectionRecord;
import ray.misc.Ray;
import ray.misc.Scene;
import ray.sampling.SampleGenerator;

public class BruteForcePathTracer extends PathTracer {
    /**
     * @param scene
     * @param ray
     * @param sampler
     * @param sampleIndex
     * @param outColor
     */
    protected void rayRadianceRecursive(Scene scene, Ray ray, 
            SampleGenerator sampler, int sampleIndex, int level, Color outColor) {
    	// W4160 TODO (B)
    	//
        // Find the visible surface along the ray, then add emitted and reflected radiance
        // to get the resulting color.
    	//
    	// If the ray depth is less than the limit (depthLimit), you need
    	// 1) compute the emitted light radiance from the current surface if the surface is a light surface
    	// 2) reflected radiance from other lights and objects. You need recursively compute the radiance
    	//    hint: You need to call gatherIllumination(...) method.
    	if(level == 0)
    	{
    		outColor.set(0);
    	}
    	if(level == this.depthLimit)
    	{
    		return;
    	}
    	
    	if(level < this.depthLimit)
    	{
    		IntersectionRecord iRec = new IntersectionRecord();
    		
    		if (scene.getFirstIntersection(iRec, ray)) 
    		{
    			
    			Vector3 outDir = new Vector3(ray.direction); 
                outDir.scale(-1.0);
                outDir.normalize();
             
                
                //call gatherIllumination function to compute the radiance
                Color out = new Color();    
                gatherIllumination(scene, outDir, iRec, sampler, sampleIndex, level, out);
                
                //add to the output color
                outColor.add(out);
                
                //call emittedRadiance to compute the emitted light radiance from the current surface if the surface is a light surface
                this.emittedRadiance(iRec, outDir, out);
                
                //add to the output color
                outColor.add(out);
                return;

    		}    
            
		}

    	
    }

}
