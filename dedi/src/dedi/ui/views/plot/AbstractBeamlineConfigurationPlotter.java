package dedi.ui.views.plot;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.dawnsci.analysis.api.roi.IROI;
import org.eclipse.dawnsci.analysis.dataset.roi.CircularROI;
import org.eclipse.dawnsci.analysis.dataset.roi.EllipticalROI;
import org.eclipse.dawnsci.analysis.dataset.roi.LinearROI;
import org.eclipse.dawnsci.analysis.dataset.roi.RectangularROI;
import org.eclipse.dawnsci.plotting.api.IPlottingSystem;
import org.eclipse.dawnsci.plotting.api.axis.IAxis;
import org.eclipse.dawnsci.plotting.api.region.IRegion;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import dedi.configuration.BeamlineConfiguration;
import dedi.configuration.calculations.results.controllers.AbstractResultsController;
import dedi.configuration.calculations.results.models.Results;
import dedi.configuration.calculations.results.models.ResultsService;
import dedi.ui.GuiHelper;
import dedi.ui.widgets.plotting.ColourChangeEvent;
import dedi.ui.widgets.plotting.ColourChangeListener;
import dedi.ui.widgets.plotting.Legend;
import dedi.ui.widgets.plotting.LegendItem;


/**
 * Defines what items should be plotted and how their properties can be configured, but leaves the actual implementation
 *  of the plotting to subclasses.
 * The default implementation is the {@link BaseBeamlineConfigurationPlotterImpl}.
 */
public abstract class AbstractBeamlineConfigurationPlotter 
                      implements IBeamlineConfigurationPlotter, PropertyChangeListener, Observer, ColourChangeListener {
	
	private AbstractBeamlineConfigurationPlotter thisInstance;
	protected IPlottingSystem<Composite> system;
	protected IBeamlineConfigurationPlotView view;
	protected BeamlineConfiguration beamlineConfiguration;
	protected AbstractResultsController resultsController;

	private List<Control> controls;
	private String[]  plotItems = {"Detector", "Beamstop", "CameraTube", "Ray"};
	private String[] plotItemNames = {"Detector", "Beamstop", "Camera tube", "Q range"};
	
	protected boolean detectorIsPlot = true;
	protected boolean beamstopIsPlot = true;
	protected boolean cameraTubeIsPlot = true;
	protected boolean rayIsPlot = true;
			
	//Default colours of the objects to be plotted
	private Color detectorColour = new Color(Display.getDefault(), 30, 144, 255);
	private Color beamstopColour = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
	private Color clearanceColour = Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY);
	private Color cameraTubeColour = new Color(Display.getDefault(), 255, 255, 0);
	
	private final String[] legendLabels = {"Detector", "Beamstop", "Clearance", "Camera tube"};
	private final Color[] legendColours = {detectorColour, beamstopColour, clearanceColour, cameraTubeColour};
    private List<LegendItem> legendItems;
	
	protected Legend legend;
	protected Composite plotConfigurationPanel;
		
	
	
	public AbstractBeamlineConfigurationPlotter(IBeamlineConfigurationPlotView view) {
		this.view = view;
		system = view.getPlottingSystem();
		
		beamlineConfiguration = BeamlineConfiguration.getInstance();
		beamlineConfiguration.addObserver(this);
		
		resultsController = ResultsService.getInstance().getController();
		resultsController.addView(this);
		
		thisInstance = this;
	}
	
	
	@Override
	public void init(){
		legendItems = new ArrayList<>();
		
		legend = view.getLegend();
		for(int i = 0; i < legendLabels.length; i++){
			LegendItem item = legend.addLegendItem(legendLabels[i], legendColours[i]);
			item.addColourChangeListener(this);
			legendItems.add(item);
		}
		
		plotConfigurationPanel = view.getPlotConfigurationPanel();
		
		controls = new ArrayList<>();
		controls.add(GuiHelper.createLabel(plotConfigurationPanel, "Select the items that should be displayed on the plot:"));
		
		for(int i = 0; i < plotItems.length; i++){
			Button button = new Button(plotConfigurationPanel, SWT.CHECK);
			button.setText(plotItemNames[i]);
			String plotItem = plotItems[i];
			button.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e){
					try {
						Method method = AbstractBeamlineConfigurationPlotter.class.getDeclaredMethod("set" + plotItem + "IsPlot", 
								                                      new Class[] {boolean.class});
						method.invoke(thisInstance, ((Button) e.getSource()).getSelection());
					} catch (Exception ex) {
					}
				}
			});
			button.setSelection(true);
			controls.add(button);
		}
		
		plotConfigurationPanel.layout();
		updatePlot();
	}
	
	
	@Override 
	public void colourChanged(ColourChangeEvent event){
		updatePlot();
	}
	
	
	@Override
	public void update(Observable o, Object arg){
		updatePlot();
	}
	
	
	@Override
	public void propertyChange(PropertyChangeEvent e){
		updatePlot();
	}
	
	
	public void setDetectorIsPlot(boolean value){
		detectorIsPlot = value;
		updatePlot();
	}
	
	
	public void setBeamstopIsPlot(boolean value){
		beamstopIsPlot = value;
		updatePlot();
	}
	
	
	public void setCameraTubeIsPlot(boolean value){
		cameraTubeIsPlot = value;
		updatePlot();
	}
	
	
	public void setRayIsPlot(boolean value){
		rayIsPlot = value;
		updatePlot();
	}
	

	public void dispose(){
		clearPlot();
		beamlineConfiguration.deleteObserver(this);
		resultsController.removeView(this);
		view = null;
		system = null;
		for(LegendItem item : legendItems) item.removeColourChangeListener(this);
		for(Control control : controls) control.dispose();
	}
	

	public void clearPlot(){
		system.clearRegions();
		for(IRegion region : system.getRegions()) system.removeRegion(region);
	}
}
