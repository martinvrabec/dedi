package dedi.ui.views.plot;

import org.eclipse.dawnsci.plotting.api.IPlottingSystem;
import org.eclipse.swt.widgets.Composite;

import dedi.ui.widgets.plotting.Legend;

public interface IBeamlineConfigurationPlotView {
	
	public IPlottingSystem<Composite> getPlottingSystem();
	
	public Composite getPlotConfigurationPanel();
	
	public Legend getLegend();
	
	public void setPlotType(IBeamlineConfigurationPlotter plot);
}
