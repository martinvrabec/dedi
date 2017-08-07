package dedi.ui.views.plot;

public abstract class PlotDecorator implements IBeamlineConfigurationPlotter {
	private IBeamlineConfigurationPlotter wrappedPlotter;
	
	
	public PlotDecorator(IBeamlineConfigurationPlotter plotter) {
		wrappedPlotter = plotter;
	}
	
	
	@Override
	public void updatePlot() {
		wrappedPlotter.updatePlot();
	}
}
