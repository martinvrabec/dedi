package dedi.configuration.calculations;

import javax.vecmath.Vector2d;

import dedi.configuration.BeamlineConfiguration;
import dedi.configuration.calculations.geometry.Ray;

public class BeamlineConfigurationUtil {
	
	private BeamlineConfigurationUtil(){
		throw new IllegalStateException("This class is not meant to be instantiated.");
	}
	
	
	/**
	 * Calculates the q value assuming that the detector's normal vector is parallel to the beam direction.
	 * Note: the given cameraLength and distance should be in the same units. 
	 * 
	 * @param distance     - distance between the point at which the incident beam hits the detector, and 
	 *                       the point on the detector at which the q value should be calculated.
	 * @param cameraLength - distance between the detector and the sample.
	 * @param wavelength   - wavelength of the X-ray beam.
	 * 
	 * @return The magnitude q of the scattering vector corresponding to the given parameters. The units of the returned value
	 *         will be the inverse of the units of the wavelength given.
	 *         
	 * @throws ArithmeticException if the given wavelength or camera length are zero.
	 *         IllegalArgumentException if any of the given parameters is negative.
	 */
	public static double calculateQValue(double distance, double cameraLength, double wavelength){
		if(cameraLength == 0 || wavelength == 0) throw new ArithmeticException();
		if(cameraLength < 0 || distance < 0 || wavelength < 0) throw new IllegalArgumentException();
		return 4*Math.PI*Math.sin(Math.atan(distance/cameraLength)/2)/wavelength;
	}
	
	
	/**
	 * Calculates the distance between the point at which the incident beam hits the detector and the circle of points 
	 * at which q equals the given q value.
	 * Assumes that the detector's normal vector is parallel to the beam direction.
	 * Note: wavelength and qValue should have their units such that their product is unity.
	 * The returned value will be in the same units as the given camera length.
	 * 
	 * @param qValue       - magnitude q of the scattering vector.
	 * @param cameraLength - distance between the detector and the sample.
	 * @param wavelength   - wavelength of the X-ray beam.
	 * 
	 * @throws IllegalArgumentException if any of the given parameters is negative, 
	 *         or if the given q value cannot be achieved for the given camera length and wavelength.
	 */
	public static double calculateDistanceFromQValue(double qValue, double cameraLength, double wavelength){
		if(qValue < 0 || cameraLength < 0 || wavelength < 0) throw new IllegalArgumentException();
		double temp = wavelength*qValue/(4*Math.PI);
		if(Math.abs(temp) > 1) throw new IllegalArgumentException();
		return Math.tan(2*Math.asin(temp))*cameraLength;
	}
	
	
	
	/**
	 * @param qvalue  - magnitude q of the scattering vector.
	 * @param bc      - beamline configuration.
	 * 
	 * @return The point at which q equals the given q value.
	 *         Returns null if any of the BeamlineConfiguration fields needed to perform the calculation are null,
	 *         if the given q value cannot be achieved for the given configuration, or if the camera length 
	 *         or wavelength are negative.
	 */
	public static Vector2d getPtForQ(double qvalue, BeamlineConfiguration bc){
		if(bc.getDetector() == null || bc.getBeamstop() == null || bc.getAngle() == null || 
		   bc.getWavelength() == null || bc.getCameraLength() == null){
			return null;
		}
		
		Ray ray = new Ray(new Vector2d(Math.cos(bc.getAngle()), Math.sin(bc.getAngle())), 
				          new Vector2d(bc.getBeamstopXCentreMM(), bc.getBeamstopYCentreMM()));
		
		try {
			return ray.getPtAtDistance(1.0e3*calculateDistanceFromQValue(qvalue, bc.getCameraLength(), bc.getWavelength()));
		} catch(IllegalArgumentException e) {
			return null;
		}
	}
}
