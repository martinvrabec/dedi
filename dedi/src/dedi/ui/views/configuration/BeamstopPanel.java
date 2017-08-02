package dedi.ui.views.configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Length;
import javax.measure.unit.BaseUnit;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

import org.dawnsci.plotting.tools.preference.detector.DiffractionDetector;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.MessageDialog;
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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.jscience.physics.amount.Amount;

import dedi.Activator;
import dedi.configuration.BeamlineConfiguration;
import dedi.configuration.devices.Beamstop;
import dedi.configuration.preferences.BeamlineConfigurationBean;
import dedi.configuration.preferences.PreferenceConstants;
import dedi.ui.GuiHelper;
import dedi.ui.widgets.units.ComboUnitsProvider;
import dedi.ui.widgets.units.LabelUnitsProvider;
import dedi.ui.widgets.units.LabelWithUnits;
import dedi.ui.widgets.units.SpinnerWithUnits;
import dedi.ui.widgets.units.TextWithUnits;


public class BeamstopPanel implements Observer {
	private PredefinedBeamlineConfigurationsPanel predefinedBeamlineConfigurationsPanel;
	
	private LabelWithUnits<Length> beamstopDiameter;
	private Group beamstopPositionGroup;
	private TextWithUnits<Dimensionless> xPositionText;
	private TextWithUnits<Dimensionless> yPositionText;
	
	private Group clearanceGroup;
	SpinnerWithUnits<Dimensionless> clearanceValueSpinner;
	
	
	
	private final static String TITLE =  "Beamstop";
	
	private final static List<Unit<Length>> DIAMETER_UNITS = new ArrayList<>(Arrays.asList(SI.MILLIMETRE, SI.MICRO(SI.METER)));
	
	
	public BeamstopPanel(Composite parent, PredefinedBeamlineConfigurationsPanel panel) {
		predefinedBeamlineConfigurationsPanel = panel;
		panel.addObserver(this);
		
		Group beamstopGroup = GuiHelper.createGroup(parent, TITLE, 3);
		
		beamstopDiameter = new LabelWithUnits<>(beamstopGroup, "Diameter:", 
				                new ComboUnitsProvider<>(beamstopGroup, DIAMETER_UNITS));
		beamstopDiameter.addAmountChangeListener(() -> textChanged());
		
		beamstopPositionGroup = GuiHelper.createGroup(beamstopGroup, "Position", 3);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 3;
		beamstopPositionGroup.setLayoutData(data);
		
		
		Unit<Dimensionless> Pixel = new BaseUnit<>("Pixel");
		xPositionText = new TextWithUnits<>(beamstopPositionGroup, "x:", 
				                new LabelUnitsProvider<>(beamstopPositionGroup, Pixel));
		xPositionText.addAmountChangeListener(() -> textChanged());
		
		
	
		yPositionText = new TextWithUnits<>(beamstopPositionGroup, "y:", 
                new LabelUnitsProvider<>(beamstopPositionGroup, Pixel));
		yPositionText.addAmountChangeListener(() -> textChanged());
		
		
		//Clearance
		clearanceGroup = GuiHelper.createGroup(beamstopGroup, "Clearance", 3);
		clearanceGroup.setLayoutData(data);
		
		clearanceValueSpinner = new SpinnerWithUnits<>(clearanceGroup, "Clearance:", 
				                     new LabelUnitsProvider<>(clearanceGroup, Pixel));
		clearanceValueSpinner.setSpinnerValues(0, 0, Integer.MAX_VALUE, 0, 1, 1);
		clearanceValueSpinner.addAmountChangeListener(() -> 
		           BeamlineConfiguration.getInstance().setClearance((int) (clearanceValueSpinner.getValue().getEstimatedValue())));
	
		
		// Need to update because the predefinedBeamlineConfiguration could have been
		// initialised before this registered as its observer
		update(null, null);
	}
	
		
	private void textChanged(){
		 Amount<Length> diameter = beamstopDiameter.getValue(SI.MILLIMETER);
		 Amount<Dimensionless> xpixels = xPositionText.getValue();
		 Amount<Dimensionless> ypixels = yPositionText.getValue();
		 if(diameter == null || xpixels == null || ypixels == null)
			 BeamlineConfiguration.getInstance().setBeamstop(null);
		 else
			 BeamlineConfiguration.getInstance()
			    .setBeamstop(new Beamstop(diameter, xpixels.getEstimatedValue(), ypixels.getEstimatedValue()));
	}

	
	private void setValues(double diameter, double beamstopX, double beamstopY, int clearance){
		 beamstopDiameter.setValue(Amount.valueOf(diameter, SI.MILLIMETER));
		 xPositionText.setValue(beamstopX);
		 yPositionText.setValue(beamstopY);
		 clearanceValueSpinner.setSpinnerSelection(clearance);
		 textChanged();
	}
	
	
	private void clearValues(){
		beamstopDiameter.clearText();
		xPositionText.clearText();
		yPositionText.clearText();
		textChanged();
	}
	

	@Override
	public void update(Observable o, Object arg) {
		BeamlineConfigurationBean beamlineConfiguration = predefinedBeamlineConfigurationsPanel.getPredefinedBeamlineConfiguration();
		if(beamlineConfiguration == null) {
			clearValues();
			return;
		}
		setValues(beamlineConfiguration.getBeamstopDiameter(), beamlineConfiguration.getBeamstopXCentre(), 
				 beamlineConfiguration.getBeamstopYCentre(), beamlineConfiguration.getClearance());
	}
	
}
