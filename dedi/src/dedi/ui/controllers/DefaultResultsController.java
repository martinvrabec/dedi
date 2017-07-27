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
import dedi.ui.models.ResultsModel;
import dedi.ui.views.results.ResultsView;

public class DefaultResultsController extends AbstractResultsController {
	private DiffractionDetector detector;
	private Beamstop beamstop;
	private CameraTube cameraTube;
	private Integer clearance;
	private Double angle;
	private Double wavelength;
	private Double cameraLength;
	
	
	public DefaultResultsController(BeamlineConfiguration configuration, ResultsModel model) {
		super(configuration, model);
	}

	
	@Override
	public void updateRequestedQRange(ScatteringQuantity minRequested, ScatteringQuantity maxRequested){
		setModelProperty(ResultsModel.IS_SATISFIED_PROPERTY, false, boolean.class);
		
		if(minRequested == null || maxRequested == null)
			setModelProperty(ResultsModel.REQUESTED_Q_RANGE_PROPERTY, null, NumericRange.class);
		else {
			double min = minRequested.toQ().getValue().to(Q.BASE_UNIT).getEstimatedValue();
			double max = maxRequested.toQ().getValue().to(Q.BASE_UNIT).getEstimatedValue();
			setModelProperty(ResultsModel.REQUESTED_Q_RANGE_PROPERTY, new NumericRange(min, max), NumericRange.class);
			computeRequestedQRangeEndPoints();
			setModelProperty(ResultsModel.IS_SATISFIED_PROPERTY, isSatisfied(), boolean.class);
		}
		
	}
	
	

	@Override
	public void update(Observable o, Object arg) {
		computeQRanges();
	}
	
	
	private void computeQRanges(){
		detector = configuration.getDetector();
		beamstop = configuration.getBeamstop();
		cameraTube = configuration.getCameraTube();
		angle = configuration.getAngle();
		clearance = configuration.getClearance();
		wavelength = configuration.getWavelength();
		cameraLength = configuration.getCameraLength();
		
		if(detector == null || beamstop == null || angle == null || clearance == null){
			setVisibleQRange(null, null, null);
			setModelProperty(ResultsModel.FULL_Q_RANGE_PROPERTY, null, NumericRange.class);
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
			setModelProperty(ResultsModel.FULL_Q_RANGE_PROPERTY, null, NumericRange.class);
			return;
		}
		
		if(cameraTube != null)
			t1 = t1.intersect(ray.getCircleIntersectionParameterRange(cameraTube.getDiameterMM()/2, 
                    new Vector2d(cameraTube.getXCentre()*detector.getXPixelMM(),
             		             cameraTube.getYCentre()*detector.getYPixelMM())));
		
		if(t1 == null || t1.getMax() < 0){
			setVisibleQRange(null, null, null);
			setModelProperty(ResultsModel.FULL_Q_RANGE_PROPERTY, null, NumericRange.class);
			return;
		}
		
		if(t1.getMin() < 0) t1.setMin(0);
		
		Vector2d ptMin = new Vector2d(ray.getPt(t1.getMin()));
		ptMin.sub(new Vector2d(beamstop.getXCentre()*detector.getXPixelMM(), beamstop.getYCentre()*detector.getYPixelMM()));
		
		Vector2d ptMax = new Vector2d(ray.getPt(t1.getMax()));
		ptMax.sub(new Vector2d(beamstop.getXCentre()*detector.getXPixelMM(), beamstop.getYCentre()*detector.getYPixelMM()));
		
		
		if(wavelength == null || cameraLength == null){
			setVisibleQRange(null, new Vector2d(ray.getPt(t1.getMin())), new Vector2d(ray.getPt(t1.getMax())));
			setModelProperty(ResultsModel.FULL_Q_RANGE_PROPERTY, null, NumericRange.class);
			return;
		}
		
		
		setVisibleQRange(new NumericRange(BeamlineConfigurationUtil.calculateQValue(ptMin.length()*1.0e-3, cameraLength, wavelength), 
				                    BeamlineConfigurationUtil.calculateQValue(ptMax.length()*1.0e-3, cameraLength, wavelength)),
				   new Vector2d(ray.getPt(t1.getMin())), new Vector2d(ray.getPt(t1.getMax())));
		
		
		if(configuration.getMaxCameraLength() == null || configuration.getMinCameraLength() == null || 
				configuration.getMaxWavelength() == null || configuration.getMinWavelength() == null){
			setModelProperty(ResultsModel.FULL_Q_RANGE_PROPERTY, null, NumericRange.class);
			return;
		}
		
		NumericRange fullRange = new NumericRange(BeamlineConfigurationUtil.calculateQValue(ptMin.length()*1.0e-3, 
																	 configuration.getMaxCameraLength(), 
																	 configuration.getMaxWavelength()), 
												  BeamlineConfigurationUtil.calculateQValue(ptMax.length()*1.0e-3, 
													        		 configuration.getMinCameraLength(), 
													        		 configuration.getMinWavelength()));
		setModelProperty(ResultsModel.FULL_Q_RANGE_PROPERTY, fullRange, NumericRange.class);
	}


	private void setVisibleQRange(NumericRange range, Vector2d startPoint, Vector2d endPoint){
		if(range != null)
			setModelProperty(ResultsModel.HAS_SOLUTION_PROPERTY, true, boolean.class);
		else
			setModelProperty(ResultsModel.HAS_SOLUTION_PROPERTY, false, boolean.class);
		setModelProperty(ResultsModel.VISIBLE_Q_RANGE_PROPERTY, range, NumericRange.class);
		setModelProperty(ResultsModel.VISIBLE_RANGE_START_POINT_PROPERTY, startPoint, Vector2d.class);
		setModelProperty(ResultsModel.VISIBLE_RANGE_END_POINT_PROPERTY, endPoint, Vector2d.class);
		setModelProperty(ResultsModel.IS_SATISFIED_PROPERTY, isSatisfied(), boolean.class);
	}
	
	
	private void computeRequestedQRangeEndPoints() {
		NumericRange requestedQRange = (NumericRange) getModelProperty(ResultsModel.REQUESTED_Q_RANGE_PROPERTY);
		if(detector == null || beamstop == null || angle == null || clearance == null || wavelength == null || cameraLength == null || requestedQRange == null){
			setModelProperty(ResultsModel.REQUESTED_RANGE_START_POINT_PROPERTY, null, Vector2d.class);
			setModelProperty(ResultsModel.REQUESTED_RANGE_END_POINT_PROPERTY, null, Vector2d.class);
			return;
		}
		
		double initialPositionX = beamstop.getXCentre()*detector.getXPixelMM();
		double initialPositionY = beamstop.getYCentre()*detector.getYPixelMM();
		Vector2d initialPosition = new Vector2d(initialPositionX, initialPositionY);
		
		Ray ray = new Ray(new Vector2d(Math.cos(angle), Math.sin(angle)), initialPosition);

		Vector2d requestedRangeStartPoint = 
				ray.getPtAtDistance(1.0e3*BeamlineConfigurationUtil.calculateDistanceFromQValue(requestedQRange.getMin(), cameraLength, wavelength));
		Vector2d requestedRangeEndPoint = 
				ray.getPtAtDistance(1.0e3*BeamlineConfigurationUtil.calculateDistanceFromQValue(requestedQRange.getMax(), cameraLength, wavelength));
		setModelProperty(ResultsModel.REQUESTED_RANGE_START_POINT_PROPERTY, requestedRangeStartPoint, Vector2d.class);
		setModelProperty(ResultsModel.REQUESTED_RANGE_END_POINT_PROPERTY, requestedRangeEndPoint, Vector2d.class);
	}
	
	
	private boolean isSatisfied(){
		NumericRange visibleQRange = (NumericRange) getModelProperty(ResultsModel.VISIBLE_Q_RANGE_PROPERTY);
		NumericRange requestedQRange = (NumericRange) getModelProperty(ResultsModel.REQUESTED_Q_RANGE_PROPERTY);
		return  visibleQRange != null && requestedQRange != null &&
					visibleQRange.contains(requestedQRange);
	}
}
