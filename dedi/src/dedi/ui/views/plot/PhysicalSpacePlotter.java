package dedi.ui.views.plot;


public class PhysicalSpacePlotter extends BaseBeamlineConfigurationPlotterImpl {
	public PhysicalSpacePlotter(IBeamlineConfigurationPlotView view) {
		super(view);
	}
	

	@Override
	public void updatePlot(){
		clearPlot();
	    
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
		return beamlineConfiguration.getDetector().getNumberOfPixelsX()*beamlineConfiguration.getDetector().getXPixelMM();
	};
	
	
	@Override
	protected double getDetectorHeight() {
		return beamlineConfiguration.getDetector().getNumberOfPixelsY()*beamlineConfiguration.getDetector().getYPixelMM(); 
	};
	
	
	@Override
	protected double getDetectorTopLeftX() {
		return 0; 
	};
	
	
	@Override
	protected double getDetectorTopLeftY() {
		return 0; 
	};
	
	
	@Override
	protected double getClearanceMajor() {
		return beamlineConfiguration.getClearance()*beamlineConfiguration.getDetector().getXPixelMM();
	}

	
	@Override
	protected double getClearanceMinor(){
		return beamlineConfiguration.getClearance()*beamlineConfiguration.getDetector().getYPixelMM();
	}

	
	@Override
	protected double getBeamstopMajor() {
		return beamlineConfiguration.getBeamstop().getDiameterMM()/2;
	}


	@Override
	protected double getBeamstopMinor() {
		return beamlineConfiguration.getBeamstop().getDiameterMM()/2;
	}


	@Override
	protected double getBeamstopCentreX() {
		return beamlineConfiguration.getBeamstop().getXCentre()*beamlineConfiguration.getDetector().getXPixelMM();
	}


	@Override
	protected double getBeamstopCentreY() {
		return beamlineConfiguration.getBeamstop().getYCentre()*beamlineConfiguration.getDetector().getYPixelMM();
	}

	
	@Override
	protected double getCameraTubeMajor() {
		return beamlineConfiguration.getCameraTube().getDiameterMM()/2;
	}


	@Override
	protected double getCameraTubeMinor() {
		return beamlineConfiguration.getCameraTube().getDiameterMM()/2;
	}


	@Override
	protected double getCameraTubeCentreX() {
		return beamlineConfiguration.getCameraTube().getXCentre()*beamlineConfiguration.getDetector().getXPixelMM();
	}


	@Override
	protected double getCameraTubeCentreY() {
		return beamlineConfiguration.getCameraTube().getYCentre()*beamlineConfiguration.getDetector().getYPixelMM();
	}


	@Override
	protected double getVisibleRangeStartPointX() {
		return resultsController.getVisibleRangeStartPoint().x;
	}


	@Override
	protected double getVisibleRangeStartPointY() {
		return resultsController.getVisibleRangeStartPoint().y;
	}


	@Override
	protected double getVisibleRangeEndPointX() {
		return resultsController.getVisibleRangeEndPoint().x;
	}


	@Override
	protected double getVisibleRangeEndPointY() {
		return resultsController.getVisibleRangeEndPoint().y;
	}


	@Override
	protected double getRequestedRangeStartPointX() {
		return resultsController.getRequestedRangeStartPoint().x;
	}


	@Override
	protected double getRequestedRangeStartPointY() {
		return resultsController.getRequestedRangeStartPoint().y;
	}


	@Override
	protected double getRequestedRangeEndPointX() {
		return resultsController.getRequestedRangeEndPoint().x;
	}


	@Override
	protected double getRequestedRangeEndPointY() {
		return resultsController.getRequestedRangeEndPoint().y;
	}
	
	
}
