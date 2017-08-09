package dedi.ui.views.plot;

import java.util.Arrays;

import org.eclipse.dawnsci.plotting.api.trace.IImageTrace;
import org.eclipse.january.dataset.DatasetFactory;


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
		
		if(beamlineConfiguration.getWavelength() != null && beamlineConfiguration.getCameraLength() != null && calibrantIsPlot)
			createCalibrantRings();
		
		if(maskIsPlot) createMask();
		
		createEmptyTrace();
		
		rescalePlot();
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
