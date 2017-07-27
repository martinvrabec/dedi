package dedi.ui.views.plot;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Observable;
import java.util.Observer;

import org.dawnsci.plotting.tools.preference.detector.DiffractionDetector;
import org.eclipse.dawnsci.plotting.api.IPlottingSystem;
import org.eclipse.dawnsci.plotting.api.PlotType;
import org.eclipse.dawnsci.plotting.api.PlottingFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.ViewPart;

import dedi.configuration.BeamlineConfiguration;
import dedi.configuration.devices.Beamstop;
import dedi.configuration.devices.CameraTube;
import dedi.ui.models.ResultsModel;
import dedi.ui.models.ResultsService;


public class BeamlineConfigurationPlotView extends ViewPart implements Observer, PropertyChangeListener {
	private final BeamlineConfiguration config;
	private final ResultsModel results;
	//private final Results RESULTS;
	
	private PageBook plotComposite;
	private IPlottingSystem<Composite> system;
	private IBeamlineConfigurationPlotter plotter;
	private Composite plotControlsPanel;
	
	private DiffractionDetector detector;
	private Beamstop beamstop;
	private CameraTube cameraTube;
	private Double angle;
	private Integer clearance;
	
	private boolean detectorIsPlot = true;
	private boolean beamstopIsPlot = true;
	private boolean cameraTubeIsPlot = true;
	
	
	//Default colours of the objects to be plotted
	private Color detectorColour = new Color(Display.getDefault(), 30, 144, 255);
	private Color beamstopColour = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
	private Color clearanceColour = Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY);
	private Color cameraTubeColour = new Color(Display.getDefault(), 255, 255, 0);
	
	private final String[] legendLabels = {"Detector", "Beamstop", "Clearance", "Camera tube"};
	private final Color[] legendColours = {detectorColour, beamstopColour, clearanceColour, cameraTubeColour};

	private Legend legend;
	
	
	public BeamlineConfigurationPlotView() {
		try {
			system = PlottingFactory.createPlottingSystem(); 
		} catch (Exception ne) {
			ne.printStackTrace();
			// It creates the view but there will be no plotting systems 
			system = null;
		}
		
		config = BeamlineConfiguration.getInstance();
		config.addObserver(this);
		
		results = ResultsService.getInstance().getModel();
		ResultsService.getInstance().getController().addView(this);
		
		
		// Cannot call update() at this point, because the plot has not been created yet.
		// Will be called at the end of createPartControl()
	}

	
	@Override
	public void createPartControl(Composite parent) {
		SashForm sashForm = new SashForm(parent, SWT.HORIZONTAL);
		sashForm.setLayout(new GridLayout(3, false));
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		
		plotComposite = new PageBook(sashForm, SWT.NONE);
		system.createPlotPart(plotComposite, getPartName(), getViewSite().getActionBars(), PlotType.IMAGE, this);  
		plotComposite.showPage(system.getPlotComposite());
		plotter = new PhysicalSpacePlot(system, this); // Default plot type;
		
		plotControlsPanel = new Composite(sashForm, SWT.NONE);
		plotControlsPanel.setLayout(new GridLayout());
		
		sashForm.setWeights(new int[]{70, 30});
		
		legend = new Legend(plotControlsPanel, legendLabels, legendColours);
		legend.addObserver(this);
		
		new PlotConfigurationPanel(plotControlsPanel, this);
		
		parent.layout();
		
		update(null, null); //Need to update the plot because the config might already have been initialised with some data 
		                   // before this view registered as its Observer.
		system.setRescale(false);
	}
	
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		plotter.updatePlot();
	};
	
	
	@Override
	public void update(Observable o, Object arg) {	
		//Update state
		detector = config.getDetector();
		beamstop = config.getBeamstop();
		cameraTube = config.getCameraTube();
		angle = config.getAngle();
		clearance = config.getClearance();
		
		// Let the plotter delegate update the plot
		plotter.updatePlot();
	}
	
	
	public IPlottingSystem<Composite> getPlottingSystem(){
		return system;
	}
	
	
	public Composite getPlotControlsPanel(){
		return plotControlsPanel;
	}
	
	
	public void setPlotType(AbstractBeamlineConfigurationPlotter plot){
		this.plotter = plot;
		update(null, null);
	}
	
	
	public Legend getLegend(){
		return legend;
	}
	
	
	
	public ResultsModel getResults(){
		return results;
	}
	
	
	public BeamlineConfiguration getBeamlineConfiguration(){
		return config;
	}
	
	
	public boolean cameraTubeIsPlot(){
		return cameraTubeIsPlot;
	}
	
	
	public boolean beamstopIsPlot(){
		return beamstopIsPlot;
	}
	
	
	public boolean detectorIsPlot(){
		return detectorIsPlot;
	}
	
	
	public void setDetectorIsPlot(boolean value){
		detectorIsPlot = value;
		update(null, null);
	}
	
	
	public void setBeamstopIsPlot(boolean value){
		beamstopIsPlot = value;
		update(null, null);
	}
	
	
	public void setCameraTubeIsPlot(boolean value){
		cameraTubeIsPlot = value;
		update(null, null);
	}

	
	
	//////////
	public DiffractionDetector getDetector() {
		return detector;
	}


	public Beamstop getBeamstop() {
		return beamstop;
	}


	public CameraTube getCameraTube() {
		return cameraTube;
	}


	public Double getAngle() {
		return angle;
	}


	public Integer getClearance() {
		return clearance;
	}
    
	/////////

	@Override
	public void setFocus() {
	}
}
