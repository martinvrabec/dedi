package dedi.ui.views.plot;

import dedi.configuration.calculations.BeamlineConfigurationUtil;
import dedi.configuration.calculations.scattering.Q;

public class PixelSpacePlotter extends BaseBeamlineConfigurationPlotterImpl {
	public PixelSpacePlotter(IBeamlineConfigurationPlotView view) {
		super(view);
	}
	
	
	@Override
	protected double getDetectorTopLeftX() {
		return 0; 
	};
	
	
	@Override
	protected double getDetectorTopLeftY() {
		return 0; 
	};
	
	
	@Override
	protected double getHorizontalLengthFromMM(double lengthMM) {
		return lengthMM/beamlineConfiguration.getDetector().getXPixelMM();
	}


	@Override
	protected double getHorizontalLengthFromPixels(double lengthPixels) {
		return lengthPixels;
	}


	@Override
	protected double getVerticalLengthFromMM(double lengthMM) {
		return lengthMM/beamlineConfiguration.getDetector().getYPixelMM();
	}


	@Override
	protected double getVerticalLengthFromPixels(double lengthPixels) {
		return lengthPixels;
	}
}
