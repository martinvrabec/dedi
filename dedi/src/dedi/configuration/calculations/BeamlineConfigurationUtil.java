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
	
	
	public static IRectangularROI getPixel(Vector2d pt, DiffractionDetector detector){
		double topLeftX = Math.floor(pt.x/detector.getXPixelMM())*detector.getXPixelMM();
		double topLeftY = Math.floor(pt.y/detector.getYPixelMM())*detector.getYPixelMM();
		return new RectangularROI(topLeftX, topLeftY, detector.getXPixelMM(), detector.getYPixelMM(), 0);
	}
	
	
	public static NumericRange calculateQResolution(double qvalue, BeamlineConfiguration bc){
		Vector2d pt = getPtForQ(qvalue, bc);
		IRectangularROI pixel = getPixel(pt, bc.getDetector());
		return new NumericRange(calculateQValue(calculateShortestDistanceToRectangle(pixel.getPointX(), pixel.getPointY(), 
				   pixel.getLength(0), pixel.getLength(1), pt.x, pt.y), bc.getCameraLength(), bc.getWavelength()), 
				calculateQValue(calculateLongestDistanceToRectangle(pixel.getPointX(), pixel.getPointY(), 
						   pixel.getLength(0), pixel.getLength(1), pt.x, pt.y), bc.getCameraLength(), bc.getWavelength()));
	}
	
	private static double calculateLongestDistanceToRectangle(double topLeftX, double topLeftY, double width, double height, double x, double y) {
		return Math.max(Math.max(calculateLongestDistanceToHorizontalSegment(topLeftY, topLeftX, topLeftX + width, x, y),
		          calculateLongestDistanceToHorizontalSegment(topLeftY + height, topLeftX, topLeftX + width, x, y)),
		Math.max(calculateLongestDistanceToVerticalSegment(topLeftX, topLeftY, topLeftY + height, x, y), 
				calculateLongestDistanceToVerticalSegment(topLeftX + width, topLeftY, topLeftY + height, x, y)));
	}


	private static double calculateLongestDistanceToVerticalSegment(double x, double y1, double y2, double p, double q) {
		return Math.max(calculateDistance(x, y1, p, q), calculateDistance(x, y2, p, q));
	}


	private static double calculateLongestDistanceToHorizontalSegment(double y, double x1, double x2, double p, double q) {
		return Math.max(calculateDistance(x1, y, p, q), calculateDistance(x2, y, p, q));
	}


	private static double calculateShortestDistanceToRectangle(double topLeftX, double topLeftY, double width, double height, double x, double y){
		return Math.min(Math.min(calculateClosestDistanceToHorizontalSegment(topLeftY, topLeftX, topLeftX + width, x, y),
				          calculateClosestDistanceToHorizontalSegment(topLeftY + height, topLeftX, topLeftX + width, x, y)),
				Math.min(calculateClosestDistanceToVerticalSegment(topLeftX, topLeftY, topLeftY + height, x, y), 
						calculateClosestDistanceToVerticalSegment(topLeftX + width, topLeftY, topLeftY + height, x, y)));
	}
	
	private static double calculateDistance(double x1, double y1, double x2, double y2){
		return Math.sqrt(Math.pow((x1-x2), 2) + Math.pow((y1-y2), 2));
	}
	
	private static double calculateClosestDistanceToHorizontalSegment(double y, double x1, double x2, double p, double q){
		if(p <= x1 && p >= x2) return calculateDistance(p, y, p, q);
		return Math.min(calculateDistance(x1, y, p, q), calculateDistance(x2, y, p, q));
	}
	
	private static double calculateClosestDistanceToVerticalSegment(double x, double y1, double y2, double p, double q){
		if(q >= y1 && q <= y2) return calculateDistance(x, q, p, q);
		return Math.min(calculateDistance(x, y1, p, q), calculateDistance(x, y2, p, q));
	}
}
