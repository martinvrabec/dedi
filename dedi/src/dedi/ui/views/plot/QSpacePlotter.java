package dedi.ui.views.plot;

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
		
		rescalePlot();
	}
	
	
	@Override
	protected double getDetectorWidth() {
		return  beamlineConfiguration.getDetector().getNumberOfPixelsX()*beamlineConfiguration.getDetector().getXPixelMM()*scaleFactor;
	}

	@Override
	protected double getDetectorHeight() {
		return beamlineConfiguration.getDetector().getNumberOfPixelsY()*beamlineConfiguration.getDetector().getYPixelMM()*scaleFactor;
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
		return beamlineConfiguration.getClearance()*beamlineConfiguration.getDetector().getXPixelMM()*scaleFactor;
	}

	@Override
	protected double getClearanceMinor() {
		return beamlineConfiguration.getClearance()*beamlineConfiguration.getDetector().getYPixelMM()*scaleFactor;
	}

	@Override
	protected double getBeamstopMajor() {
		return beamlineConfiguration.getBeamstop().getDiameterMM()*scaleFactor/2;
	}

	@Override
	protected double getBeamstopMinor() {
		return beamlineConfiguration.getBeamstop().getDiameterMM()*scaleFactor/2;
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
		return beamlineConfiguration.getBeamstop().getXCentre()*beamlineConfiguration.getDetector().getXPixelMM();
	}
	
	
	private double getBeamstopCentreYDetectorFrame(){
		return beamlineConfiguration.getBeamstop().getYCentre()*beamlineConfiguration.getDetector().getYPixelMM();
	}
	
	
	@Override
	protected double getCameraTubeMajor() {
		return beamlineConfiguration.getCameraTube().getDiameterMM()*scaleFactor/2;
	}

	@Override
	protected double getCameraTubeMinor() {
		return beamlineConfiguration.getCameraTube().getDiameterMM()*scaleFactor/2;
	}

	@Override
	protected double getCameraTubeCentreX() {
		return (beamlineConfiguration.getCameraTube().getXCentre() -  beamlineConfiguration.getBeamstop().getXCentre())
				*beamlineConfiguration.getDetector().getXPixelMM()*scaleFactor;
	}

	@Override
	protected double getCameraTubeCentreY() {
		return (beamlineConfiguration.getCameraTube().getYCentre() -  beamlineConfiguration.getBeamstop().getYCentre())
				*beamlineConfiguration.getDetector().getYPixelMM()*scaleFactor;
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

}
