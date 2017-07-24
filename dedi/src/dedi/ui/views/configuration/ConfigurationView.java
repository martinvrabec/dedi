package dedi.ui.views.configuration;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;

import javax.measure.quantity.Angle;
import javax.measure.quantity.Length;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.about.ISystemSummarySection;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.jscience.physics.amount.Amount;

import dedi.configuration.BeamlineConfiguration;
import dedi.configuration.preferences.BeamlineConfigurationBean;
import dedi.configuration.preferences.PreferenceConstants;
import dedi.ui.GuiHelper;
import dedi.ui.widgets.ComboUnitsProvider;
import dedi.ui.widgets.IAmountChangeListener;
import dedi.ui.widgets.LabelUnitsProvider;
import dedi.ui.widgets.SpinnerWithUnits;
import dedi.ui.widgets.TextWithUnits;


public class ConfigurationView extends ViewPart implements Observer {
	
	private PredefinedBeamlineConfigurationsPanel predefinedBeamlineConfigurationsPanel;
	
	private TextWithUnits<Angle> angle;
	private Spinner cameraLengthValueSpinner;

	public static final String ID = "dedi.configurationpanel";
	
	
	public ConfigurationView() {
	}

	
	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout());
		
		ScrolledComposite scrolledComposite = new ScrolledComposite( parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL );
		scrolledComposite.setExpandVertical( true );
		scrolledComposite.setExpandHorizontal( true );
		
		Composite main = new Composite(scrolledComposite, SWT.NONE);
		main.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		GridLayout layout = new GridLayout(1, true);
		layout.verticalSpacing = 20;
		main.setLayout(layout);
		
		predefinedBeamlineConfigurationsPanel = new PredefinedBeamlineConfigurationsPanel(main);
		predefinedBeamlineConfigurationsPanel.addObserver(this);
	    new DetectorPanel(main, predefinedBeamlineConfigurationsPanel);
		new BeamstopPanel(main, predefinedBeamlineConfigurationsPanel);
		new CameraTubePanel(main, predefinedBeamlineConfigurationsPanel);
		new BeamlineQuantityPanel(main, predefinedBeamlineConfigurationsPanel); 
		
		
		Group cameraGroup = GuiHelper.createGroup(main, "", 3);
		
		/*cameraLengthValueSpinner = new SpinnerWithUnits<>(cameraGroup, "Camera Length", 
				                         new LabelUnitsProvider<>(cameraGroup, SI.METER));
		cameraLengthValueSpinner.addAmountChangeListener( () ->
				BeamlineConfiguration.getInstance().setCameraLength(cameraLengthValueSpinner.getValue(SI.METER).getEstimatedValue()));
		cameraLengthValueSpinner.setNumberOfDecimalPlaces(2);
		cameraLengthValueSpinner.setSpinnerValues(145, 120, 970, 2, 25, 1);*/
		
		Label cameraLengthLabel = GuiHelper.createLabel(cameraGroup, "Camera Length:");
		cameraLengthValueSpinner = new Spinner(cameraGroup, SWT.BORDER | SWT.READ_ONLY);
		cameraLengthValueSpinner.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				BeamlineConfiguration.getInstance().setCameraLength(cameraLengthValueSpinner.getSelection()/100.0);
			}
		});
		cameraLengthValueSpinner.setValues(145, 120, 970, 2, 25, 1);
		Label cameraLengthUnitLabel = GuiHelper.createLabel(cameraGroup, "m");
		
		
		Group angleGroup = GuiHelper.createGroup(main, "", 3);
		angle = new TextWithUnits<>(angleGroup, "Angle", 
				     new ComboUnitsProvider<>(angleGroup, new ArrayList<>(Arrays.asList(SI.RADIAN, NonSI.DEGREE_ANGLE))));  
		angle.addAmountChangeListener(() -> angleChanged());
		angle.setValue(Amount.valueOf(90, NonSI.DEGREE_ANGLE));
		
		main.layout();
		
		scrolledComposite.setMinSize( main.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
		main.addListener(SWT.Resize, new Listener() {
			int width = -1;
			@Override
			public void handleEvent(Event event) {
				 int newWidth = main.getSize().x;
			     if (newWidth != width) {
			        scrolledComposite.setMinHeight(main.computeSize(newWidth, SWT.DEFAULT).y);
			        width = newWidth;
			     }
			}
		});
		
		scrolledComposite.setContent(main);	
		
		update(null, null);
	}

	
	@Override
	public void setFocus() {
	}
	
	
	private void angleChanged(){
		Amount<Angle> newAngle = angle.getValue(SI.RADIAN);
		if(newAngle == null) BeamlineConfiguration.getInstance().setAngle(null);
		else BeamlineConfiguration.getInstance().setAngle(newAngle.getEstimatedValue());
	}

	
	@Override
	public void update(Observable o, Object arg) {
		BeamlineConfigurationBean beamlineConfiguration = predefinedBeamlineConfigurationsPanel.getPredefinedBeamlineConfiguration();
		if(beamlineConfiguration == null) return;
		cameraLengthValueSpinner.setValues((int) (beamlineConfiguration.getMinCameraLength()*100), 
				                           (int) (beamlineConfiguration.getMinCameraLength()*100), 
				                           (int) (beamlineConfiguration.getMaxCameraLength()*100), 2, 
				                           (int) beamlineConfiguration.getCameraLengthStepSize()*100, 1);
		cameraLengthValueSpinner.setSelection((int) (beamlineConfiguration.getMinCameraLength()*100));
		// Next line not needed because setSelection will fire the ModifyListener
		//BeamlineConfiguration.getInstance().setCameraLength(beamlineConfiguration.getMinCameraLength());
		BeamlineConfiguration.getInstance().setMinCameraLength(beamlineConfiguration.getMinCameraLength());
		BeamlineConfiguration.getInstance().setMaxCameraLength(beamlineConfiguration.getMaxCameraLength());
	}

}
