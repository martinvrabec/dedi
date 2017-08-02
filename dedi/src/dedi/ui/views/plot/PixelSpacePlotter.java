package dedi.ui.views.plot;


public class PixelSpacePlotter extends BaseBeamlineConfigurationPlotterImpl {
	public PixelSpacePlotter(IBeamlineConfigurationPlotView view) {
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
		return beamlineConfiguration.getDetector().getNumberOfPixelsX();
	};
	
	
	@Override
	protected double getDetectorHeight() {
		return beamlineConfiguration.getDetector().getNumberOfPixelsY(); 
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
		return beamlineConfiguration.getClearance();
	}

	
	@Override
	protected double getClearanceMinor() {
		return beamlineConfiguration.getClearance();
	}
	

	@Override
	protected double getBeamstopMajor() {
		return beamlineConfiguration.getBeamstop().getDiameterMM()/2
                /beamlineConfiguration.getDetector().getXPixelMM();
	}


	@Override
	protected double getBeamstopMinor() {
		return beamlineConfiguration.getBeamstop().getDiameterMM()/2
                /beamlineConfiguration.getDetector().getYPixelMM();
	}


	@Override
	protected double getBeamstopCentreX() {
		return beamlineConfiguration.getBeamstop().getXCentre();
	}


	@Override
	protected double getBeamstopCentreY() {
		return beamlineConfiguration.getBeamstop().getYCentre();
	}
	
	
	@Override
	protected double getCameraTubeMajor() {
		return beamlineConfiguration.getCameraTube().getDiameterMM()/2
                /beamlineConfiguration.getDetector().getXPixelMM();
	}


	@Override
	protected double getCameraTubeMinor() {
		return beamlineConfiguration.getCameraTube().getDiameterMM()/2
                /beamlineConfiguration.getDetector().getYPixelMM();
	}


	@Override
	protected double getCameraTubeCentreX() {
		return beamlineConfiguration.getCameraTube().getXCentre();
	}


	@Override
	protected double getCameraTubeCentreY() {
		return beamlineConfiguration.getCameraTube().getYCentre();
	}


	@Override
	protected double getVisibleRangeStartPointX() {
		return resultsController.getVisibleRangeStartPoint().x/beamlineConfiguration.getDetector().getXPixelMM();
	}


	@Override
	protected double getVisibleRangeStartPointY() {
		return resultsController.getVisibleRangeStartPoint().y/beamlineConfiguration.getDetector().getYPixelMM();
	}


	@Override
	protected double getVisibleRangeEndPointX() {
		return resultsController.getVisibleRangeEndPoint().x/beamlineConfiguration.getDetector().getXPixelMM();
	}


	@Override
	protected double getVisibleRangeEndPointY() {
		return resultsController.getVisibleRangeEndPoint().y/beamlineConfiguration.getDetector().getYPixelMM();
	}


	@Override
	protected double getRequestedRangeStartPointX() {
		return resultsController.getRequestedRangeStartPoint().x/beamlineConfiguration.getDetector().getXPixelMM();
	}


	@Override
	protected double getRequestedRangeStartPointY() {
		return resultsController.getRequestedRangeStartPoint().y/beamlineConfiguration.getDetector().getYPixelMM();
	}


	@Override
	protected double getRequestedRangeEndPointX() {
		return resultsController.getRequestedRangeEndPoint().x/beamlineConfiguration.getDetector().getXPixelMM();
	}


	@Override
	protected double getRequestedRangeEndPointY() {
		return resultsController.getRequestedRangeEndPoint().y/beamlineConfiguration.getDetector().getYPixelMM();
	}
}
