package dedi.ui.views.plot;

import javax.measure.unit.SI;

import dedi.configuration.calculations.scattering.Q;

public class QSpacePlotter extends BaseBeamlineConfigurationPlotterImpl {
	private double scaleFactor; 
	
	public QSpacePlotter(IBeamlineConfigurationPlotView view) {
		super(view);
	}


	@Override
	public void updatePlot(){
		clearPlot();
	    
		if(beamlineConfiguration.getCameraLength() == null || beamlineConfiguration.getWavelength() == null) return;
		scaleFactor = 2e-12*Math.PI/(beamlineConfiguration.getCameraLength()*beamlineConfiguration.getWavelength()); 
		
		if(beamlineConfiguration.getDetector() != null && detectorIsPlot) 
			createDetectorRegion();
		
		if(beamlineConfiguration.getDetector() != null && beamlineConfiguration.getCameraTube() != null && cameraTubeIsPlot) 
			createCameraTubeRegion();
		
		if(beamlineConfiguration.getBeamstop() != null && beamlineConfiguration.getDetector() != null && beamstopIsPlot) 
			createBeamstopRegion();
		
		if(beamlineConfiguration.getBeamstop() != null && beamlineConfiguration.getDetector() != null && 
		   beamlineConfiguration.getAngle() != null && rayIsPlot) 
			createRay();
		
	    if(calibrantIsPlot) createCalibrantRings();
	    
		rescalePlot();
	}
	
	
	@Override
	protected double getDetectorWidth() {
		return  beamlineConfiguration.getDetectorWidthMM()*scaleFactor;
	}

	@Override
	protected double getDetectorHeight() {
		return beamlineConfiguration.getDetectorHeightMM()*scaleFactor;
	}

	@Override
	protected double getDetectorTopLeftX() {
		return -getBeamstopCentreXDetectorFrame()*scaleFactor;
	}

	@Override
	protected double getDetectorTopLeftY() {
		return -getBeamstopCentreYDetectorFrame()*scaleFactor;
	}

	@Override
	protected double getClearanceMajor() {
		return beamlineConfiguration.getClearanceMajorMM()*scaleFactor;
	}

	@Override
	protected double getClearanceMinor() {
		return beamlineConfiguration.getClearanceMinorMM()*scaleFactor;
	}

	@Override
	protected double getBeamstopMajor() {
		return beamlineConfiguration.getBeamstop().getRadiusMM()*scaleFactor;
	}

	@Override
	protected double getBeamstopMinor() {
		return beamlineConfiguration.getBeamstop().getRadiusMM()*scaleFactor;
	}

	@Override
	protected double getBeamstopCentreX() {
		return 0;
	}

	@Override
	protected double getBeamstopCentreY() {
		return 0;
	}


	private double getBeamstopCentreXDetectorFrame(){
		return beamlineConfiguration.getBeamstopXCentreMM();
	}
	
	
	private double getBeamstopCentreYDetectorFrame(){
		return beamlineConfiguration.getBeamstopYCentreMM();
	}
	
	
	@Override
	protected double getCameraTubeMajor() {
		return beamlineConfiguration.getCameraTube().getRadiusMM()*scaleFactor;
	}

	@Override
	protected double getCameraTubeMinor() {
		return beamlineConfiguration.getCameraTube().getRadiusMM()*scaleFactor;
	}

	@Override
	protected double getCameraTubeCentreX() {
		return (beamlineConfiguration.getCameraTubeXCentreMM() -  beamlineConfiguration.getBeamstopXCentreMM())*scaleFactor;
	}

	@Override
	protected double getCameraTubeCentreY() {
		return (beamlineConfiguration.getCameraTubeYCentreMM() -  beamlineConfiguration.getBeamstopYCentreMM())*scaleFactor;
	}

	@Override
	protected double getVisibleRangeStartPointX() {
		return (resultsController.getVisibleRangeStartPoint().x - getBeamstopCentreXDetectorFrame())*scaleFactor;
	}

	@Override
	protected double getVisibleRangeStartPointY() {
		return (resultsController.getVisibleRangeStartPoint().y - getBeamstopCentreYDetectorFrame())*scaleFactor;
	}

	@Override
	protected double getVisibleRangeEndPointX() {
		return (resultsController.getVisibleRangeEndPoint().x - getBeamstopCentreXDetectorFrame())*scaleFactor;
	}

	@Override
	protected double getVisibleRangeEndPointY() {
		return (resultsController.getVisibleRangeEndPoint().y - getBeamstopCentreYDetectorFrame())*scaleFactor;
	}

	@Override
	protected double getRequestedRangeStartPointX() {
		return (resultsController.getRequestedRangeStartPoint().x - getBeamstopCentreXDetectorFrame())*scaleFactor;
	}

	@Override
	protected double getRequestedRangeStartPointY() {
		return (resultsController.getRequestedRangeStartPoint().y - getBeamstopCentreYDetectorFrame())*scaleFactor;
	}

	@Override
	protected double getRequestedRangeEndPointX() {
		return (resultsController.getRequestedRangeEndPoint().x - getBeamstopCentreXDetectorFrame())*scaleFactor;
	}

	@Override
	protected double getRequestedRangeEndPointY() {
		return (resultsController.getRequestedRangeEndPoint().y - getBeamstopCentreYDetectorFrame())*scaleFactor;
	}


	@Override
	protected double getCalibrantRingMajor(Q q) {
		return q.getValue().to(SI.NANO(SI.METER).inverse()).getEstimatedValue();
	}


	@Override
	protected double getCalibrantRingMinor(Q q) {
		return q.getValue().to(SI.NANO(SI.METER).inverse()).getEstimatedValue();
	}

}
