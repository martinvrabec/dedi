package dedi.ui.views.plot;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import dedi.ui.GuiHelper;

public class PlotConfigurationPanel {
	private BeamlineConfigurationPlotView plotView;
	
	public PlotConfigurationPanel(Composite parent, BeamlineConfigurationPlotView plotView) {
		this.plotView = plotView;
		
		Composite plotConfigurationComposite = new Composite(parent, SWT.NONE);
		plotConfigurationComposite.setLayout(new GridLayout());
		
		Label plotTypeSelectionLabel = GuiHelper.createLabel(plotConfigurationComposite, "Select the type of plot:");
		
		Button physicalSpaceButton = new Button(plotConfigurationComposite, SWT.RADIO);
		physicalSpaceButton.setText("Axes in mm");
		physicalSpaceButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				if(((Button) e.getSource()).getSelection()) 
					plotView.setPlotType(new PhysicalSpacePlot(plotView.getPlottingSystem(), plotView));
		}
		});
		
	    Button pixelSpaceButton = new Button(plotConfigurationComposite, SWT.RADIO);
	    pixelSpaceButton.setText("Axes in pixels");
	    pixelSpaceButton.addSelectionListener(new SelectionAdapter() {
	    	@Override
			public void widgetSelected(SelectionEvent e){
				plotView.setPlotType(new PixelSpacePlot(plotView.getPlottingSystem(), plotView));
					
		}
		});
		
	    physicalSpaceButton.setSelection(true);
	    plotView.setPlotType(new PhysicalSpacePlot(plotView.getPlottingSystem(), plotView));
	    
	    
	    
		Label itemSelectionLabel = GuiHelper.createLabel(plotConfigurationComposite, "Select the items that should be displayed on the plot:");
				
		Button detectorCheckBox = new Button(plotConfigurationComposite, SWT.CHECK);
		detectorCheckBox.setText("Detector");
		detectorCheckBox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				plotView.setDetectorIsPlot(((Button) e.getSource()).getSelection());
			}
		});
		detectorCheckBox.setSelection(true);
			
		
		Button beamstopCheckBox = new Button(plotConfigurationComposite, SWT.CHECK);
		beamstopCheckBox.setText("Beamstop");
		beamstopCheckBox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				plotView.setBeamstopIsPlot(((Button) e.getSource()).getSelection());
			}
		});
		beamstopCheckBox.setSelection(true);
		
		
		Button cameraTubeCheckBox = new Button(plotConfigurationComposite, SWT.CHECK);
		cameraTubeCheckBox.setText("Camera tube");
		cameraTubeCheckBox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				plotView.setCameraTubeIsPlot(((Button) e.getSource()).getSelection());
			}
		});
		cameraTubeCheckBox.setSelection(true);
	}
}
