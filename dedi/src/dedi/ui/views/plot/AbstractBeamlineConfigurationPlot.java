package dedi.ui.views.plot;

import org.eclipse.dawnsci.plotting.api.IPlottingSystem;
import org.eclipse.swt.widgets.Composite;

import dedi.ui.widgets.plotting.Legend;


public abstract class AbstractBeamlineConfigurationPlot {
	protected IPlottingSystem<Composite> system;
	
	public AbstractBeamlineConfigurationPlot(IPlottingSystem<Composite> system) {
		this.system = system;
	}
	
	public abstract void createPlotControls(Composite plotConfigurationPanel, Legend legend);
	
	public abstract void updatePlot();
	
	public abstract void dispose();
}
