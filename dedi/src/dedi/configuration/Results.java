package dedi.configuration;

import java.util.Observable;
import java.util.Observer;

import javax.measure.unit.SI;
import javax.vecmath.Vector2d;

import org.dawnsci.plotting.tools.preference.detector.DiffractionDetector;

import dedi.configuration.calculations.BeamlineConfigurationUtil;
import dedi.configuration.calculations.NumericRange;
import dedi.configuration.calculations.geometry.Ray;
import dedi.configuration.calculations.scattering.ScatteringQuantity;
import dedi.configuration.devices.Beamstop;
import dedi.configuration.devices.CameraTube;

public final class Results extends Observable implements Observer {
	private NumericRange visibleQRange;
	private NumericRange fullQRange;
	private NumericRange requestedQRange;
	
	private Vector2d startPoint;
	private Vector2d endPoint;
	private Vector2d requestedRangeStartPoint;
	private Vector2d requestedRangeEndPoint;
	
	private boolean isSatisfied = false;
	
	private final BeamlineConfiguration CONFIG;
	private DiffractionDetector detector;
	private Beamstop beamstop;
	private CameraTube cameraTube;
	private Integer clearance;
	private Double angle;
	private Double wavelength;
	private Double cameraLength;
	
	
	private static final Results INSTANCE = new Results();
	
	
	public Results() {
		CONFIG = BeamlineConfiguration.getInstance();
		CONFIG.addObserver(this);
		visibleQRange = null;
		fullQRange = null;
		requestedQRange = null;
		startPoint = null;
		endPoint = null;
		update(null, null);
	}
	
	
	public static Results getInstance(){
		return INSTANCE;
	}
	
	
	@Override
	public void update(Observable o, Object arg) {
		detector = CONFIG.getDetector();
		beamstop = CONFIG.getBeamstop();
		cameraTube = CONFIG.getCameraTube();
		angle = CONFIG.getAngle();
		clearance = CONFIG.getClearance();
		wavelength = CONFIG.getWavelength();
		cameraLength = CONFIG.getCameraLength();
		
		computeQRanges();
	}
	
	
	private void computeQRanges(){
		if(detector == null || beamstop == null || angle == null || clearance == null){
			setVisibleQRange(null, null, null);
			setFullQRange(null);
			return;
		}
		
		
		double initialPositionX = (clearance*detector.getXPixelMM() + beamstop.getDiameterMM()/2)*Math.cos(angle) +
				                   beamstop.getXCentre()*detector.getXPixelMM();
		double initialPositionY = (clearance*detector.getXPixelMM() + beamstop.getDiameterMM()/2)*Math.sin(angle) + 
				                   beamstop.getYCentre()*detector.getYPixelMM();
		Vector2d initialPosition = new Vector2d(initialPositionX, initialPositionY);
		
		Ray ray = new Ray(new Vector2d(Math.cos(angle), Math.sin(angle)), initialPosition);
		NumericRange t1 = ray.getRectangleIntersectionParameterRange(new Vector2d(0, detector.getNumberOfPixelsY()*detector.getYPixelMM()), 
                		detector.getNumberOfPixelsX()*detector.getXPixelMM(), 
                		detector.getNumberOfPixelsY()*detector.getYPixelMM());
				
		if(t1 == null || t1.getMax() < 0){
			setVisibleQRange(null, null, null);
			setFullQRange(null);
			return;
		}
		
		if(cameraTube != null)
			t1 = t1.intersect(ray.getCircleIntersectionParameterRange(cameraTube.getDiameterMM()/2, 
                    new Vector2d(cameraTube.getXCentre()*detector.getXPixelMM(),
             		             cameraTube.getYCentre()*detector.getYPixelMM())));
		
		if(t1 == null || t1.getMax() < 0){
			setVisibleQRange(null, null, null);
			setFullQRange(null);
			return;
		}
		
		if(t1.getMin() < 0) t1.setMin(0);
		
		Vector2d ptMin = new Vector2d(ray.getPt(t1.getMin()));
		ptMin.sub(new Vector2d(beamstop.getXCentre()*detector.getXPixelMM(), beamstop.getYCentre()*detector.getYPixelMM()));
		
		Vector2d ptMax = new Vector2d(ray.getPt(t1.getMax()));
		ptMax.sub(new Vector2d(beamstop.getXCentre()*detector.getXPixelMM(), beamstop.getYCentre()*detector.getYPixelMM()));
		
		
		if(wavelength == null || cameraLength == null){
			setVisibleQRange(null, new Vector2d(ray.getPt(t1.getMin())), new Vector2d(ray.getPt(t1.getMax())));
			setFullQRange(null);
			return;
		}
		
		
		setVisibleQRange(new NumericRange(BeamlineConfigurationUtil.calculateQValue(ptMin.length()*1.0e-3, cameraLength, wavelength), 
				                    BeamlineConfigurationUtil.calculateQValue(ptMax.length()*1.0e-3, cameraLength, wavelength)),
				   new Vector2d(ray.getPt(t1.getMin())), new Vector2d(ray.getPt(t1.getMax())));
		
		
		if(CONFIG.getMaxCameraLength() == null || CONFIG.getMinCameraLength() == null || 
		   CONFIG.getMaxWavelength() == null || CONFIG.getMinWavelength() == null){
			setFullQRange(null);
			return;
		}
		
		
		setFullQRange(new NumericRange(BeamlineConfigurationUtil.calculateQValue(ptMin.length()*1.0e-3, 
				                                                                 CONFIG.getMaxCameraLength(), 
				                                                                 CONFIG.getMaxWavelength()), 
				                       BeamlineConfigurationUtil.calculateQValue(ptMax.length()*1.0e-3, 
				                    		                                     CONFIG.getMinCameraLength(), 
				                    		                                     CONFIG.getMinWavelength())));
	}
	
	
	private void setVisibleQRange(NumericRange range, Vector2d startPoint, Vector2d endPoint){
		visibleQRange = range;
		this.startPoint = startPoint;
		this.endPoint = endPoint;
		setChanged();
		notifyObservers();
	}
	
	
	private void setFullQRange(NumericRange fullQRange){
		this.fullQRange = fullQRange;
		setChanged();
		notifyObservers();
	}
	
	
	public void setRequestedQRange(ScatteringQuantity minRequested, ScatteringQuantity maxRequested){
		if(minRequested == null || maxRequested == null) setRequestedQRange(null);
		else{
			double min = minRequested.toQ().getValue().to(SI.METER.inverse()).getEstimatedValue();
			double max = maxRequested.toQ().getValue().to(SI.METER.inverse()).getEstimatedValue();
			setRequestedQRange(new NumericRange(min, max));
		}
	}
	
	
	public void setRequestedQRange(NumericRange requestedQRange){
		if(this.requestedQRange == null && requestedQRange == null) return;
		if(this.requestedQRange != null && this.requestedQRange.equals(requestedQRange)) return;
		this.requestedQRange = requestedQRange;
		computeRequestedQRangeEndPoints();
		isSatisfied = isSatisfied();
		setChanged();
		notifyObservers();
	}
	
	
	private void computeRequestedQRangeEndPoints() {
		if(detector == null || beamstop == null || angle == null || clearance == null || wavelength == null || cameraLength == null || requestedQRange == null){
			requestedRangeStartPoint = null;
			requestedRangeEndPoint = null;
			return;
		}
		
		double initialPositionX = beamstop.getXCentre()*detector.getXPixelMM();
		double initialPositionY = beamstop.getYCentre()*detector.getYPixelMM();
		Vector2d initialPosition = new Vector2d(initialPositionX, initialPositionY);
		
		Ray ray = new Ray(new Vector2d(Math.cos(angle), Math.sin(angle)), initialPosition);

		requestedRangeStartPoint = 
				ray.getPtAtDistance(1.0e3*BeamlineConfigurationUtil.calculateDistanceFromQValue(requestedQRange.getMin(), cameraLength, wavelength));
		requestedRangeEndPoint = 
				ray.getPtAtDistance(1.0e3*BeamlineConfigurationUtil.calculateDistanceFromQValue(requestedQRange.getMax(), cameraLength, wavelength));
	}


	public boolean isSatisfied(){
		return  visibleQRange != null && requestedQRange != null &&
				visibleQRange.contains(requestedQRange);
	}
	
	
	public NumericRange getQRange(){
		if(visibleQRange == null) return null;
		return new NumericRange(visibleQRange.getMin(), visibleQRange.getMax());
	}
	
	
	public NumericRange getFullQRange(){
		if(fullQRange == null) return null;
		return new NumericRange(fullQRange.getMin(), fullQRange.getMax());
	}
	
	
	public Vector2d getStartPoint(){
		if(startPoint == null) return null;
		return new Vector2d(startPoint);
	}
	
	
	public Vector2d getEndPoint(){
		if(endPoint == null) return null;
		return new Vector2d(endPoint);
	}
	

	public Vector2d getRequestedRangeStartPoint(){
		if(requestedRangeStartPoint == null) return null;
		return new Vector2d(requestedRangeStartPoint);
	}
	
	
	public Vector2d getRequestedRangeEndPoint(){
		if(requestedRangeEndPoint == null) return null;
		return new Vector2d(requestedRangeEndPoint);
	}
}
