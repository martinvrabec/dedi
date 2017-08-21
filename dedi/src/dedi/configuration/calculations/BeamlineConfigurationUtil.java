package dedi.configuration.calculations;

import javax.vecmath.Vector2d;

import dedi.configuration.BeamlineConfiguration;
import dedi.configuration.calculations.geometry.Ray;

public class BeamlineConfigurationUtil {
	
	private BeamlineConfigurationUtil(){
	}
	
	
	/**
	 * Calculates the q value assuming that the detector's normal vector is parallel to the beam direction.
	 * 
	 * @param distance     - distance between the point at which the incident beam hits the detector, and 
	 *                       the point at which the q value should be calculated.
	 * @param cameraLength - distance between the detector and the sample.
	 * @param wavelength   - wavelength of the X-ray beam.
	 * 
	 * @return The magnitude q of the scattering vector corresponding to the given parameters.
	 */
	public static double calculateQValue(double distance, double cameraLength, double wavelength){
		return 4*Math.PI*Math.sin(Math.atan(distance/cameraLength)/2)/wavelength;
	}
	
	
	/**
	 * Calculates the distance between the point at which the incident beam hits the detector and the circle of points 
	 * at which q equals the given q value.
	 * Assumes that the detector's normal vector is parallel to the beam direction.
	 * 
	 * @param qValue       - magnitude q of the scattering vector.
	 * @param cameraLength - distance between the detector and the sample.
	 * @param wavelength   - wavelength of the X-ray beam.
	 * 
	 * @throws IllegalArgumentException - if the given q value cannot be achieved for the given camera length and wavelength.
	 */
	public static double calculateDistanceFromQValue(double qValue, double cameraLength, double wavelength){
		double temp = wavelength*qValue/(4*Math.PI);
		if(Math.abs(temp) > 1) throw new IllegalArgumentException();
		return Math.tan(2*Math.asin(temp))*cameraLength;
	}
	
	
	
	/**
	 * @param qvalue  - magnitude q of the scattering vector.
	 * @param bc      - beamline configuration.
	 * 
	 * @return The point at which q equals the given q value.
	 *         Returns null if any of the BeamlineConfiguration fields needed to perform the calculation are null.
	 */
	public static Vector2d getPtForQ(double qvalue, BeamlineConfiguration bc){
		if(bc.getDetector() == null || bc.getBeamstop() == null || bc.getAngle() == null || 
		   bc.getWavelength() == null || bc.getCameraLength() == null){
			return null;
		}
		
		
		Ray ray = new Ray(new Vector2d(Math.cos(bc.getAngle()), Math.sin(bc.getAngle())), 
				          new Vector2d(bc.getBeamstopXCentreMM(), bc.getBeamstopYCentreMM()));
		
		return ray.getPtAtDistance(1.0e3*calculateDistanceFromQValue(qvalue, bc.getCameraLength(), bc.getWavelength()));
	}
}
