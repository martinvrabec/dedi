package dedi.ui.views.plot;

import org.eclipse.dawnsci.plotting.api.IPlottingSystem;
import org.eclipse.swt.widgets.Composite;

import dedi.ui.widgets.plotting.Legend;

public interface IBeamlineConfigurationPlotView {
	
	public IPlottingSystem<Composite> getPlottingSystem();
	
	
	/**
	 * @return The composite where plotters can put the controls needed to configure them.
	 * They are responsible for removing them when they are disposed.
	 */
	public Composite getPlotConfigurationPanel();
	
	
	/**
	 * @return The composite where plotters can put their legend.
	 * They are responsible for removing it when they are disposed.
	 */
	public Legend getLegend();
	
	
	public void setPlotType(IBeamlineConfigurationPlotter plot);
}
