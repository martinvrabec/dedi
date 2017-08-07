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
	    
	    if(maskIsPlot)
			createMask();
	    
		rescalePlot();
	}
	

	@Override
	protected double getDetectorTopLeftX() {
		return -getBeamstopCentreXDetectorFrame()*scaleFactor;
	}

	@Override
	protected double getDetectorTopLeftY() {
		return -getBeamstopCentreYDetectorFrame()*scaleFactor;
	}

	
	private double getBeamstopCentreXDetectorFrame(){
		return beamlineConfiguration.getBeamstopXCentreMM();
	}
	
	
	private double getBeamstopCentreYDetectorFrame(){
		return beamlineConfiguration.getBeamstopYCentreMM();
	}
	
	
	@Override
	protected double getHorizontalLengthFromMM(double lengthMM) {
		return lengthMM*scaleFactor;
	}


	@Override
	protected double getHorizontalLengthFromPixels(double lengthPixels) {
		return lengthPixels*beamlineConfiguration.getDetector().getXPixelMM()*scaleFactor;
	}


	@Override
	protected double getVerticalLengthFromMM(double lengthMM) {
		return lengthMM*scaleFactor;
	}


	@Override
	protected double getVerticalLengthFromPixels(double lengthPixels) {
		return lengthPixels*beamlineConfiguration.getDetector().getYPixelMM()*scaleFactor;
	}

}
