package dedi.ui.views.plot;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Observable;
import java.util.Observer;

import org.dawnsci.plotting.tools.preference.detector.DiffractionDetector;
import org.eclipse.dawnsci.plotting.api.IPlottingSystem;
import org.eclipse.dawnsci.plotting.api.PlotType;
import org.eclipse.dawnsci.plotting.api.PlottingFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.ViewPart;

import dedi.configuration.BeamlineConfiguration;
import dedi.configuration.devices.Beamstop;
import dedi.configuration.devices.CameraTube;
import dedi.ui.GuiHelper;
import dedi.ui.models.Results;
import dedi.ui.models.ResultsService;
import dedi.ui.widgets.plotting.Legend;


public class BeamlineConfigurationPlotView extends ViewPart implements IBeamlineConfigurationPlotView {

	private IBeamlineConfigurationPlotView thisInstance;
	private PageBook plotComposite;
	private IPlottingSystem<Composite> system;
	private IBeamlineConfigurationPlotter plotter;
	private Composite controlsPanel;
	private Composite plotTypesPanel;
	private Composite plotConfigurationPanel;
	private Legend legend;
	
	
	public BeamlineConfigurationPlotView() {
		thisInstance = this;
		
		try {
			system = PlottingFactory.createPlottingSystem(); 
		} catch (Exception ne) {
			ne.printStackTrace();
			// It creates the view but there will be no plotting system
			system = null;
		}
	}

	
	@Override
	public void createPartControl(Composite parent) {
		SashForm sashForm = new SashForm(parent, SWT.HORIZONTAL);
		sashForm.setLayout(new GridLayout(3, false));
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		
		plotComposite = new PageBook(sashForm, SWT.NONE);
		system.createPlotPart(plotComposite, getPartName(), getViewSite().getActionBars(), PlotType.IMAGE, this);  
		plotComposite.showPage(system.getPlotComposite());
		
		controlsPanel = new Composite(sashForm, SWT.NONE);
		GridLayoutFactory.swtDefaults().spacing(0, 20).numColumns(1).applyTo(controlsPanel);
		
		sashForm.setWeights(new int[]{70, 30});
		
		legend = new Legend(controlsPanel);
		
		plotConfigurationPanel = new Composite(controlsPanel, SWT.NONE);
		plotConfigurationPanel.setLayout(new GridLayout());
		
		plotTypesPanel = new Composite(controlsPanel, SWT.NONE);
		plotTypesPanel.setLayout(new GridLayout());		
		
		GuiHelper.createLabel(plotTypesPanel, "Select the type of plot:");
		
		Button physicalSpaceButton = new Button(plotTypesPanel, SWT.RADIO);
		physicalSpaceButton.setText("Axes in mm");
		physicalSpaceButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				if(((Button) e.getSource()).getSelection()) 
					setPlotType(new PhysicalSpacePlotter(thisInstance));
		}
		});
		
	    Button pixelSpaceButton = new Button(plotTypesPanel, SWT.RADIO);
	    pixelSpaceButton.setText("Axes in pixels");
	    pixelSpaceButton.addSelectionListener(new SelectionAdapter() {
	    	@Override
			public void widgetSelected(SelectionEvent e){
	    		if(((Button) e.getSource()).getSelection()) 
	    			setPlotType(new PixelSpacePlotter(thisInstance));
					
		}
		});
	    
	    Button qSpaceButton = new Button(plotTypesPanel, SWT.RADIO);
	    qSpaceButton.setText("Axes in q (nm^-1)");
	    qSpaceButton.addSelectionListener(new SelectionAdapter() {
	    	@Override
			public void widgetSelected(SelectionEvent e){
	    		if(((Button) e.getSource()).getSelection()) 
	    			setPlotType(new QSpacePlotter(thisInstance));
					
		}
		});
		
	    physicalSpaceButton.setSelection(true);
	    setPlotType(new PhysicalSpacePlotter(this)); // Default plot type;
		
	    plotTypesPanel.layout();
		
		system.setRescale(false);
	}
	
	
	@Override
	public IPlottingSystem<Composite> getPlottingSystem(){
		return system;
	}
	
	
	@Override
	public Composite getPlotConfigurationPanel(){
		return plotConfigurationPanel;
	}
	
	
	@Override
	public void setPlotType(IBeamlineConfigurationPlotter plot){
		if(plotter != null)
			plotter.dispose();
		plotter = plot;
		plotter.init();
	}

	
	@Override
	public Legend getLegend(){
		return legend;
	}
	

	@Override
	public void setFocus() {
		system.setFocus();
	}
}
