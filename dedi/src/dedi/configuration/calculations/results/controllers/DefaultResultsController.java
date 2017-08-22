package dedi.configuration.calculations.results.controllers;

import java.beans.PropertyChangeEvent;
import java.util.Observable;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import org.dawnsci.plotting.tools.preference.detector.DiffractionDetector;
import org.eclipse.dawnsci.analysis.api.diffraction.DetectorProperties;
import org.eclipse.dawnsci.analysis.api.diffraction.DiffractionCrystalEnvironment;
import org.eclipse.swt.widgets.Display;

import dedi.configuration.BeamlineConfiguration;
import dedi.configuration.calculations.BeamlineConfigurationUtil;
import dedi.configuration.calculations.NumericRange;
import dedi.configuration.calculations.geometry.Ray;
import dedi.configuration.calculations.results.models.IResultsModel;
import dedi.configuration.devices.Beamstop;
import dedi.configuration.devices.CameraTube;
import uk.ac.diamond.scisoft.analysis.diffraction.QSpace;

/**
 * This is the default implementation of a controller that performs the calculations of all the results stored in an {@link IResultsModel} 
 * and updates all registered views whenever the results change. 
 * 
 * In particular, it computes the visible and full Q range and the positions of their end points on the detector,
 * as well as the end points for the user-requested Q range. The bulk of the computation is in the computeQRanges() method. 
 */
public class DefaultResultsController extends AbstractResultsController {
	// Need to create a copy of the BeamlineConfiguration state each time update() is called,
	// because the computations are performed in a separate thread and
	// the configuration state could change while the calculations are being performed.
	private DiffractionDetector detector;
	private Double detectorWidthMM;
	private Double detectorHeightMM;
	private Beamstop beamstop;
	private Double beamstopXCentreMM;
	private Double beamstopYCentreMM;
	private CameraTube cameraTube;
	private Double cameraTubeXCentreMM;
	private Double cameraTubeYCentreMM;
	private Integer clearance;
	private Double clearanceRegionMajorMM;
	private Double clearanceRegionMinorMM;
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
	public void updateRequestedQRangeMin(Double minRequested){
		if(minRequested == null )
			setRequestedQRangeMin(null, null);
		else 
			setRequestedQRangeMin(minRequested, getPtForQ(minRequested));
		
	}
	
	
	@Override
	public void updateRequestedQRangeMax(Double maxRequested){
		if(maxRequested == null )
			setRequestedQRangeMax(null, null);
		else 
			setRequestedQRangeMax(maxRequested, getPtForQ(maxRequested));
	}
	
	
	private void updateRequestedQRangeEndPoints(){
		updateRequestedQRangeMin(getRequestedQRangeMin());
		updateRequestedQRangeMax(getRequestedQRangeMax());
	}
	
	
	private Vector2d getPtForQ(double qvalue){
		if(detector == null || beamstop == null || angle == null || wavelength == null || cameraLength == null)
			return null;
		
		Ray ray = new Ray(new Vector2d(Math.cos(angle), Math.sin(angle)), 
				          new Vector2d(beamstopXCentreMM, beamstopYCentreMM));
		
		return ray.getPtAtDistance(1.0e3*BeamlineConfigurationUtil.calculateDistanceFromQValue(qvalue, cameraLength, wavelength));
	}
	

	
	@Override
	public void update(Observable o, Object arg) {
		// Notify views that one of the models changed, namely that the BeamlineConfiguration changed.
		propertyChange(new PropertyChangeEvent(configuration, AbstractResultsController.BEAMLINE_CONFIGURATION_PROPERTY, null, configuration));
		
		updateState();
		
		// Compute the new results and store them in the model
		computeQRanges();
	}
	
	
	private void updateState(){
		// Create a deep copy of the current BeamlineConfiguration state.
		// (Note that Beamstop, CameraTube and primitive wrapper classes are immutable,
		// so do not need to create a copy of those).
		detector = (configuration.getDetector() == null) ? null : new DiffractionDetector(configuration.getDetector());
		detectorWidthMM = configuration.getDetectorWidthMM();
		detectorHeightMM = configuration.getDetectorHeightMM();
		beamstop = configuration.getBeamstop(); 
		beamstopXCentreMM = configuration.getBeamstopXCentreMM();
		beamstopYCentreMM = configuration.getBeamstopYCentreMM();
		cameraTube = configuration.getCameraTube(); 
		cameraTubeXCentreMM = configuration.getCameraTubeXCentreMM();
		cameraTubeYCentreMM = configuration.getCameraTubeYCentreMM();
		angle = configuration.getAngle();
		clearance = configuration.getClearance();
		clearanceRegionMajorMM = configuration.getClearanceAndBeamstopMajorMM();
		clearanceRegionMinorMM = configuration.getClearanceAndBeamstopMinorMM();
		wavelength = configuration.getWavelength();
		minWavelength = configuration.getMinWavelength();
		maxWavelength = configuration.getMaxWavelength();
		cameraLength = configuration.getCameraLength();
		minCameraLength = configuration.getMinCameraLength();
		maxCameraLength = configuration.getMaxCameraLength();
	}
	
	private void computeQRanges(){
		// Perform the computations in a separate thread.
		// However, the updates of the results have to be done in the UI thread,
		// because they modify the GUI, so use Display.getDefault().asyncExec(Runnable).
		// Any variables that need to be passed to that Runnable that are liable to get modified  
		// in this thread before the update in the UI thread has finished must be passed as deep copies.
		Thread thread = new Thread(() -> {
			// Update just the end points of the requested range - the range itself is always user-defined
            // and is set via updateRequestedQRange() by the views that handle user input.
			Display.getDefault().asyncExec(DefaultResultsController.this :: updateRequestedQRangeEndPoints); 
			
			
			// Deal with special cases.
			if(detector == null || beamstop == null || angle == null || clearance == null){
				Display.getDefault().asyncExec(() -> setVisibleQRange(null, null, null));
				Display.getDefault().asyncExec(() -> setFullQRange(null));
				return;
			}
			

			// Find the intersection pt of the clearance region (beamstop + clearance) with a line at the given angle starting at the beamstop centre.
			Vector2d initialPosition = new Vector2d(clearanceRegionMajorMM*Math.cos(angle) + beamstopXCentreMM, 
					                                clearanceRegionMinorMM*Math.sin(angle) + beamstopYCentreMM);
			
			
			// Find the portion of a ray from the initial position at the given angle that lies within the detector face.
			Ray ray = new Ray(new Vector2d(Math.cos(angle), Math.sin(angle)), initialPosition);
			NumericRange t1 = ray.getRectangleIntersectionParameterRange(new Vector2d(0, detectorHeightMM), 
					                									 detectorWidthMM, detectorHeightMM);
			
			
			// Find the portion of the ray that lies within the camera tube's projection onto the detector face.
			if(t1 != null && cameraTube != null && cameraTube.getRadiusMM() != 0)
				t1 = t1.intersect(ray.getCircleIntersectionParameterRange(cameraTube.getRadiusMM(), 
	                                           new Vector2d(cameraTubeXCentreMM, cameraTubeYCentreMM)));
			
			
			// Check whether the intersection is empty.
			if(t1 == null){
				Display.getDefault().asyncExec(() -> setVisibleQRange(null, null, null));
				Display.getDefault().asyncExec(() -> setFullQRange(null));
				return;
			}
			
			
			
			// Find the points that correspond to the end points of the range.
			Vector2d ptMin = ray.getPt(t1.getMin());
			Vector2d ptMax = ray.getPt(t1.getMax());
			
			
			// If the wavelength or camera length are not known then can't actually calculate the visible Q values from the above distances,
			// so just set the end points of the Q ranges.
			if(wavelength == null || cameraLength == null){
				Display.getDefault().asyncExec(() -> setVisibleQRange(null, new Vector2d(ptMin), new Vector2d(ptMax)));
				Display.getDefault().asyncExec(() -> setFullQRange(null));
				return;
			}
			
			
			// Calculate the visible Q range.
			DetectorProperties detectorProperties = 
					new DetectorProperties(cameraLength*1e3, 
									       beamstopXCentreMM, beamstopYCentreMM, 
										   detector.getNumberOfPixelsY(), detector.getNumberOfPixelsX(), 
										   detector.getYPixelMM(), detector.getXPixelMM()); // Convert lengths to mm.
			QSpace qSpace = new QSpace(detectorProperties, new DiffractionCrystalEnvironment(wavelength*1e10)); // Need to convert wavelength to Angstroms.
			
			Vector3d visibleQMin = qSpace.qFromPixelPosition(ptMin.x/detector.getXPixelMM(), ptMin.y/detector.getYPixelMM());
			Vector3d visibleQMax = qSpace.qFromPixelPosition(ptMax.x/detector.getXPixelMM(), ptMax.y/detector.getYPixelMM());
			
			
			// Create a deep copy of ptMin and ptMax to pass to the Runnable below,
			// because the code that follows might modify them,
			// but we want the UI thread to use their current values.
			// (It's not enough to create the copy in the call to setVisibleQRange()).
			// Assume visibleQMin and visibleQMax won't change.
			Vector2d ptMinCopy = new Vector2d(ptMin);
			Vector2d ptMaxCopy = new Vector2d(ptMax);
			Display.getDefault().asyncExec(() -> 
				setVisibleQRange(new NumericRange(visibleQMin.length()*1e10, visibleQMax.length()*1e10), ptMinCopy, ptMaxCopy));
			
			
			// If min/max camera length or wavelength are not known then can't calculate the full range.
			if(maxCameraLength == null || minCameraLength == null || maxWavelength == null || minWavelength == null){
				Display.getDefault().asyncExec(() -> setFullQRange(null));
				return;
			}
			
			// Compute the full range.
			detectorProperties.getOrigin().z = minCameraLength*1e3;
			qSpace.setDiffractionCrystalEnvironment(new DiffractionCrystalEnvironment(minWavelength*1e10));
			Vector3d fullQMin = qSpace.qFromPixelPosition(ptMax.x/detector.getXPixelMM(), ptMax.y/detector.getYPixelMM());
			
			detectorProperties.getOrigin().z = maxCameraLength*1e3;
			qSpace.setDiffractionCrystalEnvironment(new DiffractionCrystalEnvironment(maxWavelength*1e10));
			Vector3d fullQMax = qSpace.qFromPixelPosition(ptMin.x/detector.getXPixelMM(), ptMin.y/detector.getYPixelMM());
			
			// Set the full range.
			Display.getDefault().asyncExec(() -> setFullQRange(new NumericRange(fullQMin.length()*1e10, fullQMax.length()*1e10)));
		});
		
		thread.start();
	}
	
	
	
	@Override
	public Double getQResolution(double qValue){
		updateState();
		
		Vector2d pt = getPtForQ(qValue);
		if(pt == null) return null;
		
		DetectorProperties detectorProperties = new DetectorProperties(cameraLength*1e3, 
												       beamstopXCentreMM, beamstopYCentreMM, 
													   detector.getNumberOfPixelsY(), detector.getNumberOfPixelsX(), 
													   detector.getYPixelMM(), detector.getXPixelMM()); // Convert lengths to mm.
		QSpace qSpace = new QSpace(detectorProperties, new DiffractionCrystalEnvironment(wavelength*1e10)); // Need to convert wavelength to Angstroms.
		
		Vector3d q = qSpace.qFromPixelPosition(pt.x/detector.getXPixelMM(), pt.y/detector.getYPixelMM());
		
		int[] pixelCoords = qSpace.pixelPosition(q);
		
		Vector2d bottomLeftPixel = new Vector2d(pixelCoords[0]*detector.getXPixelMM(), pixelCoords[1]*detector.getYPixelMM());
		
		Ray ray = new Ray(new Vector2d(Math.cos(angle), Math.sin(angle)), new Vector2d(beamstopXCentreMM, beamstopYCentreMM));
		NumericRange t = ray.getRectangleIntersectionParameterRange(new Vector2d(bottomLeftPixel.x, bottomLeftPixel.y + detector.getYPixelMM()),
				                                                    detector.getXPixelMM(), detector.getYPixelMM());
		
		if(t == null || t.getMax() < 0) return null;
		if(t.getMin() < 0) t.setMin(0);
		
		Vector2d ptMin = new Vector2d(ray.getPt(t.getMin()));
		Vector2d ptMax = new Vector2d(ray.getPt(t.getMax()));
		
		double qMin = qSpace.qFromPixelPosition(ptMin.x/detector.getXPixelMM(), ptMin.y/detector.getYPixelMM()).length()*1e10;
		double qMax = qSpace.qFromPixelPosition(ptMax.x/detector.getXPixelMM(), ptMax.y/detector.getYPixelMM()).length()*1e10;
		
		return Math.max(qValue - qMin, qMax - qValue);
	}
}
