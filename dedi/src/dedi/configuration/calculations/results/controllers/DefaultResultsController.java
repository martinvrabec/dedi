package dedi.configuration.calculations.results.controllers;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;

import javax.measure.unit.SI;
import javax.vecmath.Vector2d;

import org.apache.commons.beanutils.BeanUtils;
import org.dawnsci.plotting.tools.preference.detector.DiffractionDetector;
import org.eclipse.swt.widgets.Display;

import dedi.configuration.BeamlineConfiguration;
import dedi.configuration.calculations.BeamlineConfigurationUtil;
import dedi.configuration.calculations.NumericRange;
import dedi.configuration.calculations.geometry.Ray;
import dedi.configuration.calculations.results.models.IResultsModel;
import dedi.configuration.calculations.results.models.Results;
import dedi.configuration.calculations.scattering.Q;
import dedi.configuration.calculations.scattering.ScatteringQuantity;
import dedi.configuration.devices.Beamstop;
import dedi.configuration.devices.CameraTube;
import dedi.ui.views.results.ResultsView;

/**
 * This is the default implementation of a controller that performs the calculations of all the results stored in an {@link IResultsModel} 
 * and updates all registered models whenever the results change. 
 * 
 * In particular, it computes the visible and full Q range and their end points on the detector,
 * as well as the end points for the user-requested Q range. The bulk of the computation is in the computeQRanges() method. 
 */
public class DefaultResultsController extends AbstractResultsController {
	// Need to keep a copy of the BeamlineConfiguration state,
	// because the computations are performed in a separate thread.
	private DiffractionDetector detector;
	private Beamstop beamstop;
	private CameraTube cameraTube;
	private Integer clearance;
	private Double angle;
	private Double wavelength;
	private Double minWavelength;
	private Double maxWavelength;
	private Double cameraLength;
	private Double minCameraLength;
	private Double maxCameraLength;
	
	
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
	
	
	private void updateRequestedQRangeEndPoints(){
		NumericRange oldRequestedRange = getRequestedQRange();
		if(oldRequestedRange != null){
			double min = oldRequestedRange.getMin();
			double max = oldRequestedRange.getMax();
			setRequestedQRange(new NumericRange(min,  max), getPtForQ(min), getPtForQ(max));
		} else {
			setRequestedQRange(null, null, null);
		}
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
		// Create a deep copy of the current BeamlineConfiguration state.
		// (Note that Beamstop, CameraTube and primitive wrapper classes are immutable,
		// so do not need to create a copy of those).
		detector = (configuration.getDetector() == null) ? null : new DiffractionDetector(configuration.getDetector());
		beamstop = configuration.getBeamstop(); 
		cameraTube = configuration.getCameraTube(); 
		angle = configuration.getAngle();
		clearance = configuration.getClearance();
		wavelength = configuration.getWavelength();
		minWavelength = configuration.getMinWavelength();
		maxWavelength = configuration.getMaxWavelength();
		cameraLength = configuration.getCameraLength();
		minCameraLength = configuration.getMinCameraLength();
		maxCameraLength = configuration.getMaxCameraLength();
		
		// Compute the new results and store them in the model
		computeQRanges();
	}
	
	
	private void computeQRanges(){
		// Perform the computations in a separate thread.
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				// Update just the end points; the requested range is always user-defined
                // and is set via updateRequestedQRange() by the views that handle user input.
				Display.getDefault().asyncExec(() -> updateRequestedQRangeEndPoints()); 
				
				if(detector == null || beamstop == null || angle == null || clearance == null){
					Display.getDefault().asyncExec(() -> setVisibleQRange(null, null, null));
					Display.getDefault().asyncExec(() -> setFullQRange(null));
					return;
				}
				

				// Find the intersection pt of the clearance region with a line at the given angle starting at the beamstop centre.
				double initialPositionX = (clearance*detector.getXPixelMM() + beamstop.getRadiusMM())*Math.cos(angle) +
						                   configuration.getBeamstopXCentreMM();
				double initialPositionY = (clearance*detector.getYPixelMM() + beamstop.getRadiusMM())*Math.sin(angle) + 
						                   configuration.getBeamstopYCentreMM();
				Vector2d initialPosition = new Vector2d(initialPositionX, initialPositionY);
				
				
				// Find the region of a ray from the initial position that lies within the detector face.
				Ray ray = new Ray(new Vector2d(Math.cos(angle), Math.sin(angle)), initialPosition);
				NumericRange t1 = ray.getRectangleIntersectionParameterRange(new Vector2d(0, configuration.getDetectorHeightMM()), 
						                                                     configuration.getDetectorWidthMM(), configuration.getDetectorHeightMM());
				
				
				// Check whether the intersection is empty.
				if(t1 == null || t1.getMax() < 0){
					Display.getDefault().asyncExec(() -> setVisibleQRange(null, null, null));
					Display.getDefault().asyncExec(() -> setFullQRange(null));
					return;
				}
				
				
				// Find the region of the ray that lies within the camera tubes projection onto the detector face.
				if(cameraTube != null && cameraTube.getRadiusMM() != 0)
					t1 = t1.intersect(ray.getCircleIntersectionParameterRange(cameraTube.getRadiusMM(), 
		                              new Vector2d(configuration.getCameraTubeXCentreMM(),configuration.getCameraTubeYCentreMM())));
				
				
				// Check whether the intersection is empty.
				if(t1 == null || t1.getMax() < 0){
					Display.getDefault().asyncExec(() -> setVisibleQRange(null, null, null));
					Display.getDefault().asyncExec(() -> setFullQRange(null));
					return;
				}
				
				
				// Restrict the range to one that actually lies on the ray.
				if(t1.getMin() < 0) t1.setMin(0);
				
				
				// Find the points that correspond to the end points of the range and their distance from the beamstop centre.
				Vector2d ptMin = new Vector2d(ray.getPt(t1.getMin()));
				ptMin.sub(new Vector2d(configuration.getBeamstopXCentreMM(), configuration.getBeamstopYCentreMM()));
				
				Vector2d ptMax = new Vector2d(ray.getPt(t1.getMax()));
				ptMax.sub(new Vector2d(configuration.getBeamstopXCentreMM(), configuration.getBeamstopYCentreMM()));
				
				double ptMinx = ray.getPt(t1.getMin()).x;
				double ptMiny = ray.getPt(t1.getMin()).y;
				double ptMaxx = ray.getPt(t1.getMax()).x;
				double ptMaxy = ray.getPt(t1.getMax()).y;
				
				// If the wavelength or camera length are not known then can't actually calculate the visible Q value from the above distances,
				// so just set the end points of the Q ranges.
				if(wavelength == null || cameraLength == null){
					Display.getDefault().asyncExec(() -> setVisibleQRange(null, new Vector2d(ptMinx, ptMiny), new Vector2d(ptMaxx, ptMaxy)));
					Display.getDefault().asyncExec(() -> setFullQRange(null));
					return;
				}
				
				
				// Calculate the visible Q range.
				Display.getDefault().asyncExec(() -> setVisibleQRange(new NumericRange(BeamlineConfigurationUtil.calculateQValue(ptMin.length()*1.0e-3, cameraLength, wavelength), 
						                                                               BeamlineConfigurationUtil.calculateQValue(ptMax.length()*1.0e-3, cameraLength, wavelength)),
						                                              new Vector2d(ptMinx, ptMiny), new Vector2d(ptMaxx, ptMaxy)));
				
				
				// If min/max camera length or wavelength are not known then can't calculate the full range.
				if(maxCameraLength == null || minCameraLength == null || maxWavelength == null || minWavelength == null){
					Display.getDefault().asyncExec(() -> setFullQRange(null));
					return;
				}
				
				
				// Compute the full range.
				NumericRange fullRange = 
						new NumericRange(BeamlineConfigurationUtil.calculateQValue(ptMin.length()*1.0e-3, maxCameraLength, maxWavelength), 
										 BeamlineConfigurationUtil.calculateQValue(ptMax.length()*1.0e-3, minCameraLength, minWavelength));
				
				Display.getDefault().asyncExec(() -> setFullQRange(fullRange));
			}
		});
		
		thread.start();
	}
}
