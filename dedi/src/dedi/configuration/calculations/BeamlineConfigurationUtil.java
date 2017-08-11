package dedi.configuration.calculations;

import javax.vecmath.Matrix3d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import org.dawnsci.plotting.tools.preference.detector.DiffractionDetector;
import org.eclipse.dawnsci.analysis.api.diffraction.DetectorProperties;
import org.eclipse.dawnsci.analysis.api.roi.IRectangularROI;
import org.eclipse.dawnsci.analysis.dataset.roi.RectangularROI;

import dedi.configuration.BeamlineConfiguration;
import dedi.configuration.calculations.geometry.Conic;
import dedi.configuration.calculations.geometry.Ray;

public class BeamlineConfigurationUtil {
	
	public static double calculateQValue(double distance, double cameraLength, double wavelength){
		return 4*Math.PI*Math.sin(Math.atan(distance/cameraLength)/2)/wavelength;
	}
	
	
	public static double calculateDistanceFromQValue(double qValue, double cameraLength, double wavelength){
		double temp = wavelength*qValue/(4*Math.PI);
		if(Math.abs(temp) > 1) throw new IllegalArgumentException();
		return Math.tan(2*Math.asin(temp))*cameraLength;
	}
	
	
	public static Vector2d getPtForQ(double qvalue, BeamlineConfiguration bc){
		if(bc.getDetector() == null || bc.getBeamstop() == null || bc.getAngle() == null || 
				bc.getWavelength() == null || bc.getCameraLength() == null){
			return null;
		}
		
		double initialPositionX = bc.getBeamstop().getXCentre()*bc.getDetector().getXPixelMM();
		double initialPositionY = bc.getBeamstop().getYCentre()*bc.getDetector().getYPixelMM();
		Vector2d initialPosition = new Vector2d(initialPositionX, initialPositionY);
		
		Ray ray = new Ray(new Vector2d(Math.cos(bc.getAngle()), Math.sin(bc.getAngle())), initialPosition);
		
		return ray.getPtAtDistance(1.0e3*calculateDistanceFromQValue(qvalue, bc.getCameraLength(), bc.getWavelength()));
	}
}
