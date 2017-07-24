package dedi.configuration.calculations;

import javax.vecmath.Matrix3d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import org.eclipse.dawnsci.analysis.api.diffraction.DetectorProperties;

import dedi.configuration.calculations.geometry.Conic;
import dedi.configuration.calculations.geometry.Ray;

public class BeamlineConfigurationUtil {
	
	public static double calculateQValue(Vector3d position, Vector3d beamvector, double wavelength){
		beamvector.normalize();
		position.normalize();
		double doubleTheta = Math.acos(beamvector.dot(position));
		return 4*Math.PI*Math.sin(doubleTheta/2)/wavelength;
	}
	
	
	public static double calculateQValue(double distance, double cameraLength, double wavelength){
		return 4*Math.PI*Math.sin(Math.atan(distance/cameraLength)/2)/wavelength;
	}
	
	
	public static double calculateDistanceFromQValue(double qValue, double cameraLength, double wavelength){
		double temp = wavelength*qValue/(4*Math.PI);
		if(Math.abs(temp) > 1) throw new IllegalArgumentException();
		return Math.tan(2*Math.asin(temp))*cameraLength;
	}
	
	
	public static Conic getCameraTubeProjectionOnDetectorPlane(DetectorProperties detector,  
			                                                   double ctx, double cty, double radius){
		Matrix3d transformation = detector.getOrientation(); 
		
		double a11 = transformation.m00;
		double a21 = transformation.m10;
		double a12 = transformation.m01;
		double a22 = transformation.m11;
		double xorigin = detector.getOrigin().x - ctx;
		double yorigin = detector.getOrigin().y - cty; 
		
		double coeffOfx2 = Math.pow(a11, 2) + Math.pow(a21, 2);
		double coeffOfy2 = Math.pow(a12, 2) + Math.pow(a22, 2);
		double coeffOfxy = 2*a11*a12 + 2*a21*a22;
		double coeffOfx  = 2*a11*xorigin +2*a21*yorigin;
		double coeffOfy =  2*a12*xorigin + 2*a11*yorigin;
		double constant =  Math.pow(xorigin, 2) + Math.pow(yorigin, 2) - Math.pow(radius, 2);
		
		return new Conic(coeffOfx2, coeffOfxy, coeffOfy2, coeffOfx, coeffOfy, constant);
	}
	
	
	public static NumericRange calculateQRangeVisible(DetectorProperties detector, Ray ray, Conic cameraTube, double wavelength){
		NumericRange t_range1 = ray.getConicIntersectionParameterRange(cameraTube);
		if(t_range1 == null) return null;
		
		NumericRange t_range2 = ray.getRectangleIntersectionParameterRange(new Vector2d(0, 0), detector.getDetectorSizeH(), detector.getDetectorSizeV());
		
		NumericRange t_range = t_range1.intersect(t_range2); 
		if(t_range == null) return null;
		
		
		Vector2d minPosition = ray.getPt(t_range.getMin());
		Vector3d minPosition3D = new Vector3d(minPosition.x, minPosition.y, 0);
		Vector2d maxPosition = ray.getPt(t_range.getMax());
		Vector3d maxPosition3D = new Vector3d(maxPosition.x, maxPosition.y, 0);
		
		Matrix3d transformation = detector.getOrientation();
		transformation.invert();
		
		Vector3d firstRow = new Vector3d(); 
		Vector3d secondRow = new Vector3d(); 
		Vector3d thirdRow = new Vector3d();
		
		transformation.getRow(0, firstRow);
		transformation.getRow(1, secondRow);
		transformation.getRow(2, thirdRow);
		
		Vector3d minPositionInLabFrame  = new Vector3d(firstRow.dot(minPosition3D), 
				                                       secondRow.dot(minPosition3D),
				                                       thirdRow.dot(minPosition3D));
		minPositionInLabFrame.add(detector.getOrigin());
		Vector3d maxPositionInLabFrame = new Vector3d(firstRow.dot(maxPosition3D), 
													  secondRow.dot(maxPosition3D),
													  thirdRow.dot(maxPosition3D));
		maxPositionInLabFrame.add(detector.getOrigin());
		
		return new NumericRange(calculateQValue(minPositionInLabFrame, detector.getBeamVector(), wavelength),
								calculateQValue(maxPositionInLabFrame, detector.getBeamVector(), wavelength));
	}
	
	
	
	/*public static NumericRange calculateQRange(Vector3d pt, Vector3d beamvector){
		double xPixelSize = 0.17220338983050845*1e-3; 
		double yPixelSize = 0.17212626563430611*1e-3;
		
		Circle beamstop = new Circle(new Vector(xPixelSize*1200, yPixelSize*(-100)), 
				                     Amount.valueOf(7, SI.MILLIMETRE));
		Circle cameraTube = new Circle(new Vector(xPixelSize*737.5, yPixelSize*839.5), 
									 Amount.valueOf(300, SI.MILLIMETRE));
		
		int clearance = 22;
		double angle = 0.993;
		
		double xclearance = clearance*Math.cos(angle)*xPixelSize; 
		double yclearance = clearance*Math.sin(angle)*yPixelSize;
		double lenOfClearance = Math.sqrt(xclearance*xclearance + yclearance*yclearance);
		
		Ray ray = new Ray(new Vector(Math.cos(angle), Math.sin(angle)), 
							beamstop.getCentre());
		
		Vector initialPosition = ray.getPt(beamstop.getRadius().doubleValue(SI.METER) + lenOfClearance);
		
		ray.setStartingPt(initialPosition);
		
		Range<Dimensionless> t_range1 = ray.getCircleIntersectionParameterRange(cameraTube);
		if(t_range1 == null) System.out.println("No solution.");
		
		Range<Dimensionless> t_range2 = ray.getRectangleIntersectionParameterRange(0.254, 0.289);
		if(t_range2 == null) System.out.println("No solution.");
		
		Range<Dimensionless> t_range = t_range1.intersect(t_range2);
		
		Vector minPosition = ray.getPt(t_range.getMin().getEstimatedValue());
		Vector maxPosition = ray.getPt(t_range.getMax().getEstimatedValue());
		
		double minlimit = beamstop.getCentre().getSubtracted(minPosition).getLength();
		double maxlimit = beamstop.getCentre().getSubtracted(maxPosition).getLength();
		
		Range<Dimensionless> qrange = new Range<>(Amount.valueOf(4*Math.PI*Math.sin(Math.atan(minlimit/1.2)/2)/0.1e-9, Unit.ONE),
						Amount.valueOf(4*Math.PI*Math.sin(Math.atan(maxlimit/1.2)/2)/0.1e-9, Unit.ONE));
		return null;
	}*/
}
