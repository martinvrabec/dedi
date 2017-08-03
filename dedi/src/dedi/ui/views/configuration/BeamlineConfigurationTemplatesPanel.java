package dedi.ui.views.configuration;

import java.beans.XMLDecoder;
import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Observable;

import org.dawnsci.plotting.tools.preference.detector.DiffractionDetectorConstants;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.fieldassist.AutoCompleteField;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchActionConstants;

import dedi.Activator;
import dedi.configuration.preferences.BeamlineConfigurationBean;
import dedi.configuration.preferences.BeamlineConfigurationPreferenceHelper;
import dedi.configuration.preferences.BeamlineConfigurations;
import dedi.configuration.preferences.PreferenceConstants;
import dedi.ui.GuiHelper;

public class BeamlineConfigurationTemplatesPanel extends Observable {
	
	private List<BeamlineConfigurationBean> beamlineConfigurations;
	private BeamlineConfigurationBean beamlineConfigurationTemplate;
	private Group beamlineConfigurationGroup;
	
	private final static String TITLE =  "Beamline configuration templates";
	
	
	public BeamlineConfigurationTemplatesPanel(Composite parent) {
		beamlineConfigurationTemplate = null;
		
		beamlineConfigurationGroup = GuiHelper.createGroup(parent, TITLE, 2);
		
		Label beamlineConfigurationLabel = GuiHelper.createLabel(beamlineConfigurationGroup, "Predefined beamline configuration");
		
		Combo beamlineConfigurationsCombo = new Combo(beamlineConfigurationGroup, SWT.READ_ONLY | SWT.H_SCROLL);
		ComboViewer beamlineConfigurationsComboViewer = new ComboViewer(beamlineConfigurationsCombo);
		beamlineConfigurationsComboViewer.setContentProvider(ArrayContentProvider.getInstance());
		beamlineConfigurationsComboViewer.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element){
				if(element instanceof BeamlineConfigurationBean){
					BeamlineConfigurationBean beamlineConfiguration = (BeamlineConfigurationBean) element;
					return beamlineConfiguration.getName();
				}
				return super.getText(element);
			}
		});
		
		
		beamlineConfigurationsComboViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
			    if (selection.size() > 0){
			    	beamlineConfigurationTemplate = (BeamlineConfigurationBean) selection.getFirstElement();
			    	setChanged();
			    	notifyObservers();
			    }
			}
		});
			
		
		beamlineConfigurations = BeamlineConfigurationPreferenceHelper.getBeamlineConfigurationsListFromPreferences();
		beamlineConfigurationsComboViewer.setInput(beamlineConfigurations);
		if(beamlineConfigurations != null && !beamlineConfigurations.isEmpty())
			beamlineConfigurationsComboViewer.setSelection(new StructuredSelection(beamlineConfigurations.get(0)));
		
		BeamlineConfigurationPreferenceHelper.addBeamlineConfigurationPropertyChangeListener(new IPropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if(event.getProperty() == PreferenceConstants.BEAMLINE_CONFIGURATION){
					beamlineConfigurations = BeamlineConfigurationPreferenceHelper.getBeamlineConfigurationsListFromPreferences();
					beamlineConfigurationsComboViewer.setInput(beamlineConfigurations);
					if(beamlineConfigurations != null && !beamlineConfigurations.isEmpty())
						beamlineConfigurationsComboViewer.setSelection(new StructuredSelection(beamlineConfigurations.get(0)));
					beamlineConfigurationGroup.layout();
				}
				
			}
		});
		
		
		beamlineConfigurationGroup.layout();
	}

	
	public BeamlineConfigurationBean getPredefinedBeamlineConfiguration(){
		return beamlineConfigurationTemplate;
	}
	
	
}
