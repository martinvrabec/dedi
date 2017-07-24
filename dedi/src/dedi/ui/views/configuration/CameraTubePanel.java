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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.jscience.physics.amount.Amount;

import dedi.configuration.BeamlineConfiguration;
import dedi.configuration.devices.CameraTube;
import dedi.configuration.preferences.BeamlineConfigurationBean;
import dedi.ui.GuiHelper;

import dedi.ui.widgets.ComboUnitsProvider;
import dedi.ui.widgets.LabelUnitsProvider;
import dedi.ui.widgets.LabelWithUnits;
import dedi.ui.widgets.TextWithUnits;


public class CameraTubePanel implements Observer {
	private PredefinedBeamlineConfigurationsPanel predefinedBeamlineConfigurationsPanel; 
	
	private Group cameraTubeGroup;
	private LabelWithUnits<Length> cameraTubeDiameter;
	private TextWithUnits<Dimensionless> xPositionText;
	private TextWithUnits<Dimensionless> yPositionText;
	//private Button checkBox;
	
	private final static String TITLE =  "Camera tube";
	
	private final static List<Unit<Length>> DIAMETER_UNITS = new ArrayList<>(Arrays.asList(SI.MILLIMETRE, SI.MICRO(SI.METER)));;
	
	
	public CameraTubePanel(Composite parent, PredefinedBeamlineConfigurationsPanel panel){
		predefinedBeamlineConfigurationsPanel = panel;
		panel.addObserver(this);
		
		cameraTubeGroup = GuiHelper.createGroup(parent, TITLE, 3);
		
		cameraTubeDiameter = new LabelWithUnits<>(cameraTubeGroup, "Diameter:", 
				                   new ComboUnitsProvider<>(cameraTubeGroup, DIAMETER_UNITS));
		cameraTubeDiameter.addAmountChangeListener(() -> textChanged());
		
		Group cameraTubePositionGroup = GuiHelper.createGroup(cameraTubeGroup, "Position", 3);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 3;
		cameraTubePositionGroup.setLayoutData(data);
		
		
		Unit<Dimensionless> Pixel = new BaseUnit<>("Pixel");
		xPositionText = new TextWithUnits<>(cameraTubePositionGroup, "x:", 
				                new LabelUnitsProvider<>(cameraTubePositionGroup, Pixel));
		xPositionText.addAmountChangeListener(() -> textChanged());
		
		
	
		yPositionText = new TextWithUnits<>(cameraTubePositionGroup, "y:", 
                new LabelUnitsProvider<>(cameraTubePositionGroup, Pixel));
		yPositionText.addAmountChangeListener(() -> textChanged());
		
		
		/*checkBox = new Button(parent, SWT.CHECK);
		checkBox.setText("Select whether to take the camera tube into account in the calculations");
		checkBox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				if(!((Button) e.getSource()).getSelection()){
					cameraTubeGroup.setEnabled(false);
					BeamlineConfiguration.getInstance().setCameraTube(null);
				}
				else {
					cameraTubeGroup.setEnabled(true);
					textChanged();
				}
			}
		});
		checkBox.setSelection(true);*/
		
		
		update(null, null);
	}
	
	
	private void textChanged(){
		 Amount<Length> diameter = cameraTubeDiameter.getValue(SI.MILLIMETER);
		 Amount<Dimensionless> xpixels = xPositionText.getValue();
		 Amount<Dimensionless> ypixels = yPositionText.getValue();
		 if(diameter == null || xpixels == null || ypixels == null)
			 BeamlineConfiguration.getInstance().setCameraTube(null);
		 else
			 BeamlineConfiguration.getInstance()
			    .setCameraTube(new CameraTube(diameter, xpixels.getEstimatedValue(), ypixels.getEstimatedValue()));
	}
	
	
	private void setValues(double diameter, double cameraTubeX, double cameraTubeY){
		 cameraTubeDiameter.setValue(Amount.valueOf(diameter, SI.MILLIMETER));
		 xPositionText.setValue(cameraTubeX);
		 yPositionText.setValue(cameraTubeY);
		 textChanged();
	}
	
	
	private void clearValues(){
		cameraTubeDiameter.clearText();
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
		if(beamlineConfiguration.getCameraTubeDiameter() == 0){
			setValues(0, 0, 0);
			cameraTubeGroup.setEnabled(false);
			BeamlineConfiguration.getInstance().setCameraTube(null);
			//checkBox.setSelection(false);
			//checkBox.setEnabled(false);
		}
		else {
			setValues(beamlineConfiguration.getCameraTubeDiameter(), beamlineConfiguration.getCameraTubeXCentre(), 
				 beamlineConfiguration.getCameraTubeYCentre());
			cameraTubeGroup.setEnabled(true);
			//checkBox.setSelection(true);
			//checkBox.setEnabled(true);
		}
	}
}
