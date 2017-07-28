package dedi.ui.controllers;

import java.util.Observable;
import java.util.Observer;

import javax.measure.unit.SI;
import javax.vecmath.Vector2d;

import org.dawnsci.plotting.tools.preference.detector.DiffractionDetector;

import dedi.configuration.BeamlineConfiguration;
import dedi.configuration.calculations.BeamlineConfigurationUtil;
import dedi.configuration.calculations.NumericRange;
import dedi.configuration.calculations.geometry.Ray;
import dedi.configuration.calculations.scattering.Q;
import dedi.configuration.calculations.scattering.ScatteringQuantity;
import dedi.configuration.devices.Beamstop;
import dedi.configuration.devices.CameraTube;
import dedi.ui.models.IResultsModel;
import dedi.ui.models.Results;
import dedi.ui.views.results.ResultsView;

public class DefaultResultsController extends AbstractResultsController {
	private DiffractionDetector detector;
	private Beamstop beamstop;
	private CameraTube cameraTube;
	private Integer clearance;
	private Double angle;
	private Double wavelength;
	private Double cameraLength;
	
	
	public DefaultResultsController(BeamlineConfiguration configuration){
		super(configuration);
	}

	
	@Override
	public void updateRequestedQRange(ScatteringQuantity minRequested, ScatteringQuantity maxRequested){
		if(minRequested == null || maxRequested == null)
			setRequestedQRange(null, null, null);
		else {
			double min = minRequested.toQ().getValue().to(Q.BASE_UNIT).getEstimatedValue();
			double max = maxRequested.toQ().getValue().to(Q.BASE_UNIT).getEstimatedValue();
			setRequestedQRange(new NumericRange(min,  max), getPtForQ(min), getPtForQ(max));
		}
		
	}
	
	
	private void updateRequestedQRange(double min, double max){
		setRequestedQRange(new NumericRange(min,  max), getPtForQ(min), getPtForQ(max));
	}
	
	
	private Vector2d getPtForQ(double qvalue){
		if(detector == null || beamstop == null || angle == null || wavelength == null || cameraLength == null){
			return null;
		}
		
		double initialPositionX = beamstop.getXCentre()*detector.getXPixelMM();
		double initialPositionY = beamstop.getYCentre()*detector.getYPixelMM();
		Vector2d initialPosition = new Vector2d(initialPositionX, initialPositionY);
		
		Ray ray = new Ray(new Vector2d(Math.cos(angle), Math.sin(angle)), initialPosition);
		
		return ray.getPtAtDistance(1.0e3*BeamlineConfigurationUtil.calculateDistanceFromQValue(qvalue, cameraLength, wavelength));
	}
	

	
	@Override
	public void update(Observable o, Object arg) {
		// Update BeamlineConfiguration state
		detector = configuration.getDetector();
		beamstop = configuration.getBeamstop();
		cameraTube = configuration.getCameraTube();
		angle = configuration.getAngle();
		clearance = configuration.getClearance();
		wavelength = configuration.getWavelength();
		cameraLength = configuration.getCameraLength();
		
		// Compute new results and store them in the model
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
		
		
		if(configuration.getMaxCameraLength() == null || configuration.getMinCameraLength() == null || 
				configuration.getMaxWavelength() == null || configuration.getMinWavelength() == null){
			setFullQRange(null);
			return;
		}
		
		NumericRange fullRange = new NumericRange(BeamlineConfigurationUtil.calculateQValue(ptMin.length()*1.0e-3, 
																	 configuration.getMaxCameraLength(), 
																	 configuration.getMaxWavelength()), 
												  BeamlineConfigurationUtil.calculateQValue(ptMax.length()*1.0e-3, 
													        		 configuration.getMinCameraLength(), 
													        		 configuration.getMinWavelength()));
		setFullQRange(fullRange);
		
		NumericRange oldRequestedRange = (NumericRange) getModelProperty(IResultsModel.REQUESTED_Q_RANGE_PROPERTY);
		if(oldRequestedRange != null)
			updateRequestedQRange(oldRequestedRange.getMin(), oldRequestedRange.getMax());
	}
}
