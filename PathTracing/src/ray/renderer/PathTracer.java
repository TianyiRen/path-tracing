package ray.renderer;

import ray.brdf.BRDF;
import ray.material.Material;
import ray.math.Geometry;
import ray.math.Point2;
import ray.math.Vector3;
import ray.misc.Color;
import ray.misc.IntersectionRecord;
import ray.misc.Ray;
import ray.misc.Scene;
import ray.sampling.SampleGenerator;

public abstract class PathTracer extends DirectOnlyRenderer {

    protected int depthLimit = 5;
    protected int backgroundIllumination = 1;

    public void setDepthLimit(int depthLimit) { this.depthLimit = depthLimit; }
    public void setBackgroundIllumination(int backgroundIllumination) { this.backgroundIllumination = backgroundIllumination; }

    @Override
    public void rayRadiance(Scene scene, Ray ray, SampleGenerator sampler, int sampleIndex, Color outColor) {
    
        rayRadianceRecursive(scene, ray, sampler, sampleIndex, 0, outColor);
    }

    protected abstract void rayRadianceRecursive(Scene scene, Ray ray, SampleGenerator sampler, int sampleIndex, int level, Color outColor);

    public void gatherIllumination(Scene scene, Vector3 outDir, 
            IntersectionRecord iRec, SampleGenerator sampler, 
            int sampleIndex, int level, Color outColor) {
    	// W4160 TODO (B)
    	//
        // This method computes a Monte Carlo estimate of reflected radiance due to direct and/or indirect 
        // illumination.  It generates samples uniformly wrt. the projected solid angle measure:
        //
        //    f = brdf * radiance
        //    p = 1 / pi
        //    g = f / p = brdf * radiance * pi
    	// You need: 
    	//   1. Generate a random incident direction according to proj solid angle
    	//      pdf is constant 1/pi
    	//   2. Recursively find incident radiance from that direction
    	//   3. Estimate the reflected radiance: brdf * radiance / pdf = pi * brdf * radiance
    	Point2 seed = new Point2();
        sampler.sample(1, sampleIndex, seed); 
        
        //generate a random incident direction according to proj solid angle
        Vector3 incDir = new Vector3();
        incDir.normalize();
    	Geometry.squareToPSAHemisphere(seed, incDir);
        iRec.frame.frameToCanonical(incDir);
        
    	Color brdf = new Color();
    	Color out = new Color();
    	Color indirect = new Color();
        
    	Material material = iRec.surface.getMaterial();
    	BRDF b = material.getBRDF(iRec);
    	if ( b != null ) 
    	{

    		b.evaluate(iRec.frame, incDir, outDir, brdf);
            
            
            scene.incidentRadiance(iRec.frame.o, incDir, out);
            
            //generate the new reflected light 
            Ray newRay = new Ray(iRec.frame.o, incDir);
            newRay.makeOffsetRay();
            // recursively compute incident radiance
            rayRadianceRecursive(scene, newRay, sampler, sampleIndex, level+1, indirect);
           
            out.scale(brdf);
            out.scale(Math.PI); 
            indirect.scale(brdf);
            out.add(indirect);
            outColor.add(out);
    	}
 
        return;
    }
}
