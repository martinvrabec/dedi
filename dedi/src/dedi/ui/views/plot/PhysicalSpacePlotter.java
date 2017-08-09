package dedi.ui.views.plot;

import java.util.Arrays;

import org.eclipse.dawnsci.plotting.api.trace.IImageTrace;
import org.eclipse.january.dataset.DatasetFactory;


public class PhysicalSpacePlotter extends BaseBeamlineConfigurationPlotterImpl {
	public PhysicalSpacePlotter(IBeamlineConfigurationPlotView view) {
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
		return lengthMM;
	}


	@Override
	protected double getHorizontalLengthFromPixels(double lengthPixels) {
		return lengthPixels*beamlineConfiguration.getDetector().getXPixelMM();
	}


	@Override
	protected double getVerticalLengthFromMM(double lengthMM) {
		return lengthMM;
	}


	@Override
	protected double getVerticalLengthFromPixels(double lengthPixels) {
		return lengthPixels*beamlineConfiguration.getDetector().getYPixelMM();
	}
}
