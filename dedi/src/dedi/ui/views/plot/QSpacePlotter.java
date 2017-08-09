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
		if(beamlineConfiguration.getCameraLength() == null || beamlineConfiguration.getWavelength() == null) return;
		scaleFactor = 2e-12*Math.PI/(beamlineConfiguration.getCameraLength()*beamlineConfiguration.getWavelength()); 
		
		super.updatePlot();
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
