package dedi.ui.views.plot;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.dawb.common.services.ServiceManager;
import org.eclipse.dawnsci.analysis.api.persistence.IPersistenceService;
import org.eclipse.dawnsci.analysis.api.persistence.IPersistentFile;
import org.eclipse.dawnsci.analysis.api.roi.IROI;
import org.eclipse.dawnsci.analysis.dataset.roi.CircularROI;
import org.eclipse.dawnsci.analysis.dataset.roi.EllipticalROI;
import org.eclipse.dawnsci.analysis.dataset.roi.LinearROI;
import org.eclipse.dawnsci.analysis.dataset.roi.RectangularROI;
import org.eclipse.dawnsci.plotting.api.IPlottingSystem;
import org.eclipse.dawnsci.plotting.api.axis.IAxis;
import org.eclipse.dawnsci.plotting.api.region.IRegion;
import org.eclipse.dawnsci.plotting.api.trace.IImageTrace;
import org.eclipse.dawnsci.plotting.api.trace.ITrace;
import org.eclipse.january.dataset.Dataset;
import org.eclipse.january.dataset.DatasetFactory;
import org.eclipse.january.dataset.DoubleDataset;
import org.eclipse.january.dataset.IDataset;
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
import uk.ac.diamond.scisoft.analysis.crystallography.CalibrantSelectedListener;
import uk.ac.diamond.scisoft.analysis.crystallography.CalibrantSelectionEvent;
import uk.ac.diamond.scisoft.analysis.crystallography.CalibrantSpacing;
import uk.ac.diamond.scisoft.analysis.crystallography.CalibrationFactory;
import uk.ac.diamond.scisoft.analysis.crystallography.CalibrationStandards;


/**
 * Defines what items should be plotted and how their properties can be configured, but leaves the actual implementation
 *  of the plotting to subclasses.
 * The default implementation is the {@link BaseBeamlineConfigurationPlotterImpl}.
 */
public abstract class AbstractBeamlineConfigurationPlotter 
                      implements IBeamlineConfigurationPlotter, PropertyChangeListener, Observer, ColourChangeListener, 
                                 CalibrantSelectedListener {
	
	private AbstractBeamlineConfigurationPlotter thisInstance;
	protected IPlottingSystem<Composite> system;
	protected IBeamlineConfigurationPlotView view;
	protected BeamlineConfiguration beamlineConfiguration;
	protected AbstractResultsController resultsController;

	private List<Control> controls;
	private String[]  plotItems = {"detector", "beamstop", "cameraTube", "ray", "mask", "calibrant"};
	private String[] plotItemNames = {"Detector", "Beamstop", "Camera tube", "Q range", "Mask", "Calibrant"};
	
	protected boolean detectorIsPlot = true;
	protected boolean beamstopIsPlot = true;
	protected boolean cameraTubeIsPlot = true;
	protected boolean rayIsPlot = true;
	protected boolean calibrantIsPlot = false;
	protected boolean maskIsPlot = false;
	
	protected CalibrantSpacing selectedCalibrant;
	private Label selectedCalibrantLabel;
	protected IDataset mask;
			
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
		
		beamlineConfiguration = ResultsService.getInstance().getBeamlineConfiguration();
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
						Field field = AbstractBeamlineConfigurationPlotter.class.getDeclaredField(plotItem + "IsPlot");
						field.setBoolean(thisInstance, ((Button) e.getSource()).getSelection());
						updatePlot();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			});
			try {
				Field field = AbstractBeamlineConfigurationPlotter.class.getDeclaredField(plotItem + "IsPlot");
				button.setSelection(field.getBoolean(thisInstance));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			controls.add(button);
		}
		
		selectedCalibrant = CalibrationFactory.getCalibrationStandards().getCalibrant(); 
		
		controls.add(GuiHelper.createLabel(plotConfigurationPanel, "The currently selected calibrant is :"));
		selectedCalibrantLabel = GuiHelper.createLabel(plotConfigurationPanel, "");
		controls.add(selectedCalibrantLabel);
		if(selectedCalibrant != null) selectedCalibrantLabel.setText(selectedCalibrant.getName());
		controls.add(GuiHelper.createLabel(plotConfigurationPanel, "(You can select another calibrant in diffraction preferences)"));
		
		CalibrationFactory.addCalibrantSelectionListener(this);
		
		
		IPersistenceService service;
		try {
			service = (IPersistenceService) ServiceManager.getService(IPersistenceService.class);
			IPersistentFile pf = service.getPersistentFile("/home/kce75424/Documents/Martin/DAWN/runtime-org.dawnsci.base.product/data/examples/I22 Beamline Calibrants/Masks/SAXS_mask.nxs");
			mask = pf.getMask(pf.getMaskNames(null).get(0),null);
		} catch (Exception e) {
			e.printStackTrace();
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
	
	
	@Override
	public void calibrantSelectionChanged(CalibrantSelectionEvent evt) {
		selectedCalibrant = CalibrationFactory.getCalibrationStandards().getCalibrant();
		if(selectedCalibrant != null) selectedCalibrantLabel.setText(selectedCalibrant.getName());
		plotConfigurationPanel.layout();
		updatePlot();
	}
	
	
	public void dispose(){
		clearPlot();
		beamlineConfiguration.deleteObserver(this);
		resultsController.removeView(this);
		view = null;
		system = null;
		for(LegendItem item : legendItems) item.removeColourChangeListener(this);
		CalibrationFactory.removeCalibrantSelectionListener(this);
		for(Control control : controls) control.dispose();
	}
	

	public void clearPlot(){
		system.clearRegions();
		for(IRegion region : system.getRegions()) system.removeRegion(region);
		for(ITrace trace : system.getTraces()) system.removeTrace(trace);
	}
}
