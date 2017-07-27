package dedi.ui.views.plot;

import org.eclipse.dawnsci.analysis.api.roi.IROI;
import org.eclipse.dawnsci.analysis.dataset.roi.LinearROI;
import org.eclipse.dawnsci.plotting.api.IPlottingSystem;
import org.eclipse.dawnsci.plotting.api.region.IRegion;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;

import dedi.configuration.BeamlineConfiguration;
import dedi.ui.models.ResultsModel;

public abstract class AbstractBeamlineConfigurationPlotter implements IBeamlineConfigurationPlotter {
	protected IPlottingSystem<Composite> system;
	protected BeamlineConfigurationPlotView view;
	protected BeamlineConfiguration config;
	protected ResultsModel results;
	
	protected IRegion detectorRegion;
	protected IROI detectorROI;
	protected IRegion beamstopRegion;
	protected IROI beamstopROI;
	protected IRegion clearanceRegion;
	protected IROI clearanceROI;
	protected IRegion cameraTubeRegion;
	protected IROI cameraTubeROI;
	protected IRegion visibleRangeRegion1;
	protected LinearROI visibleRangeROI1;
	protected IRegion visibleRangeRegion2;
	protected LinearROI visibleRangeROI2;
	protected IRegion inaccessibleRangeRegion;
	protected LinearROI inaccessibleRangeROI;
	protected IRegion requestedRangeRegion;
	protected LinearROI requestedRangeROI;
	
	
	public AbstractBeamlineConfigurationPlotter(IPlottingSystem<Composite> system, BeamlineConfigurationPlotView view) {
		this.system = system;
		this.view = view;
		config = this.view.getBeamlineConfiguration();
		results = this.view.getResults();
	}
	
	
	public void updatePlot(){
		system.clearRegions();
		for(IRegion region : system.getRegions()) system.removeRegion(region);
	    
		createDetectorRegion();
		createCameraTubeRegion();
		createBeamstopRegion();
		createRay();
	}
	
	protected abstract void createDetectorRegion();
	
	protected abstract void createBeamstopRegion();
	
	protected abstract void createCameraTubeRegion();
	
	protected abstract void createRay();
	
	protected void addRegion(IRegion region, IROI roi, Color colour){
		region.setROI(roi);
		region.setMobile(false);
		region.setActive(false);
		region.setUserRegion(false);
		region.setRegionColor(colour);
		system.addRegion(region);
	}
}
