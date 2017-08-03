package dedi.ui.views.configuration;


import java.beans.XMLDecoder;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.stream.Collectors;

import javax.measure.quantity.Length;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

import org.dawnsci.plotting.tools.Activator;
import org.dawnsci.plotting.tools.preference.detector.DiffractionDetector;
import org.dawnsci.plotting.tools.preference.detector.DiffractionDetectorConstants;
import org.dawnsci.plotting.tools.preference.detector.DiffractionDetectorHelper;
import org.dawnsci.plotting.tools.preference.detector.DiffractionDetectorPreferencePage;
import org.dawnsci.plotting.tools.preference.detector.DiffractionDetectors;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.AutoCompleteField;
import org.eclipse.jface.fieldassist.ComboContentAdapter;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import dedi.configuration.BeamlineConfiguration;
import dedi.configuration.preferences.BeamlineConfigurationBean;
import dedi.ui.GuiHelper;
import dedi.ui.TextUtil;
import dedi.ui.widgets.units.ComboUnitsProvider;
import dedi.ui.widgets.units.LabelWithUnits;

public class DetectorPanel implements Observer {
	private final static String TITLE =  "Detector";
	
	private BeamlineConfigurationTemplatesPanel templatesPanel;
	private static IPreferenceStore detectorPreferenceStore;
	private static List<DiffractionDetector> detectors;
	private Combo detectorTypesCombo; 
	private ComboViewer detectorTypesComboViewer;
	
	private final static List<Unit<Length>> PIXEL_SIZE_UNITS = new ArrayList<>(Arrays.asList(SI.MILLIMETRE, SI.MICRO(SI.METER)));

	
	public DetectorPanel(Composite parent, BeamlineConfigurationTemplatesPanel panel) {
		templatesPanel = panel;
		panel.addObserver(this);
		
		Group detectorGroup = GuiHelper.createGroup(parent, TITLE, 3);

		
		//Label for the detector type
		Label detectorLabel = GuiHelper.createLabel(detectorGroup, "Detector type:");
		
		
		detectorTypesCombo = new Combo(detectorGroup, SWT.READ_ONLY | SWT.H_SCROLL);
		GridData data = new GridData();
		data.horizontalSpan = 2;
		detectorTypesCombo.setLayoutData(data);
		detectorTypesComboViewer = new ComboViewer(detectorTypesCombo);
		detectorTypesComboViewer.setContentProvider(ArrayContentProvider.getInstance());
		detectorTypesComboViewer.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element){
				if(element instanceof DiffractionDetector){
					DiffractionDetector detector = (DiffractionDetector) element;
					return detector.getDetectorName();
				}
				return super.getText(element);
			}
		});
				
		
		
		
		// Labels for the resolution of the detector
		Label resolutionLabel = GuiHelper.createLabel(detectorGroup, "Resolution (hxw):");
		
		Label resolutionValueLabel = new Label(detectorGroup, SWT.NONE);
		resolutionValueLabel.setText("");
		resolutionValueLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		resolutionValueLabel.setAlignment(SWT.RIGHT);
		
		// Placeholder
		new Label(detectorGroup, SWT.NONE);
		
		
		
		
		// Labels for the pixel sizes
		Label pixelSizeLabel = GuiHelper.createLabel(detectorGroup, "Pixel size:");
		
		Label pixelSizeValueLabel = GuiHelper.createLabel(detectorGroup, "");
		
		
		
		// A combo for choosing the unit in which the pixel size is displayed
		Combo unitsCombo = new Combo(detectorGroup, SWT.READ_ONLY);
		
		ComboViewer unitsComboViewer = new ComboViewer(unitsCombo);
		unitsComboViewer.setContentProvider(ArrayContentProvider.getInstance());
		unitsComboViewer.setLabelProvider(new LabelProvider() {
				@Override
				public String getText(Object element){
					if(element instanceof Unit<?>){
						@SuppressWarnings("unchecked")
						Unit<Length> unit = (Unit<Length>) element;
						return unit.toString() + " x " + unit.toString();
					}
					return super.getText(element);
				}
		});
		
		unitsComboViewer.setInput(PIXEL_SIZE_UNITS);
		unitsComboViewer.setSelection(new StructuredSelection(PIXEL_SIZE_UNITS.get(0)));
		unitsCombo.setVisible(false);
		
			
		// Listeners		
		detectorTypesComboViewer.addSelectionChangedListener(new ISelectionChangedListener() {
				@Override
				public void selectionChanged(SelectionChangedEvent event) {
					IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				    if (selection.size() > 0){
				    	unitsCombo.setVisible(true);
				        DiffractionDetector detector = (DiffractionDetector) selection.getFirstElement();
				        resolutionValueLabel.setText(detector.getNumberOfPixelsX() + " x " +
				        		detector.getNumberOfPixelsY());
				        @SuppressWarnings("unchecked")
						Unit<Length> unit = (Unit<Length>) unitsComboViewer.getStructuredSelection().getFirstElement();
				        pixelSizeValueLabel.setText(TextUtil.format(detector.getxPixelSize().doubleValue(unit)) + " x " +
				        		TextUtil.format(detector.getyPixelSize().doubleValue(unit)));
				        detectorGroup.layout();
				        BeamlineConfiguration.getInstance().setDetector(detector);
				    } else{
				    	resolutionValueLabel.setText("");
				    	pixelSizeValueLabel.setText("");
				    	unitsCombo.setVisible(false);
				    }
				}
					
			});
		
		
		
		
		unitsComboViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				if (selection.size() > 0){
					 @SuppressWarnings("unchecked")
					 Unit<Length> unit = (Unit<Length>) selection.getFirstElement();
					 DiffractionDetector detector = (DiffractionDetector) detectorTypesComboViewer.getStructuredSelection().getFirstElement();
					 if(detector != null){
						 pixelSizeValueLabel.setText(TextUtil.format(detector.getxPixelSize().doubleValue(unit)) + " x " +
				        		TextUtil.format(detector.getyPixelSize().doubleValue(unit)));
				         detectorGroup.layout();
					 }
				}
			}
		});
		
		
		
		 // Load detector preferences
		 getDetectorPreferences();
		 sendDetectorsToCombo();
		 detectorGroup.layout();
		 
		 detectorPreferenceStore.addPropertyChangeListener(new IPropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent event) {
					if(event.getProperty() == DiffractionDetectorConstants.DETECTOR){
						try{
							detectors = DiffractionDetectorHelper.getDetectorsFromPreferences().getDiffractionDetectors();
							sendDetectorsToCombo();
							if(detectors != null && !detectors.isEmpty())
								detectorTypesComboViewer.setSelection(new StructuredSelection(detectors.get(0)));
							else{
								resolutionValueLabel.setText("");
						    	pixelSizeValueLabel.setText("");
						    	unitsCombo.setVisible(false);
							}
							detectorGroup.layout();
						} catch(Exception e) {
						}
					}
					
				}
			});
		 
	
		 update(null, null);
	}


	private void getDetectorPreferences() {
		 detectorPreferenceStore = Activator.getPlottingPreferenceStore();
		 String xml = detectorPreferenceStore.getString(DiffractionDetectorConstants.DETECTOR);
		 try{
			 XMLDecoder xmlDecoder = new XMLDecoder(new ByteArrayInputStream(xml.getBytes()));
			 DiffractionDetectors diffDetectors = (DiffractionDetectors) xmlDecoder.readObject();
			 detectors = diffDetectors.getDiffractionDetectors();
			 xmlDecoder.close();
		 }catch(Exception e){
		 }
	}

	
	private void sendDetectorsToCombo(){
		 detectorTypesComboViewer.setInput(detectors);
		 /*String[] dds = detectors.stream().map(d -> d.getDetectorName()).toArray(String[]::new);
		 if (autoComplete==null) autoComplete = new AutoCompleteField(detectorTypesCombo, new ComboContentAdapter(), dds) ;
		 autoComplete.setProposals(dds);*/
	}

	
	@Override
	public void update(Observable o, Object arg) {
		BeamlineConfigurationBean beamlineConfiguration = templatesPanel.getPredefinedBeamlineConfiguration();
		if(beamlineConfiguration == null) return;
		try{
			detectorTypesComboViewer.setSelection(new StructuredSelection(beamlineConfiguration.getDetector()));
		} catch(Exception e){
			detectorTypesComboViewer.setSelection(new StructuredSelection(detectors.get(0)));
		}
		
	}
	
	
}
