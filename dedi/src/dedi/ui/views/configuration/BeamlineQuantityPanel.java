package dedi.ui.views.configuration;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.jscience.physics.amount.Amount;

import dedi.configuration.BeamlineConfiguration;
import dedi.configuration.preferences.BeamlineConfigurationBean;
import dedi.ui.GuiHelper;
import dedi.ui.TextUtil;
import dedi.ui.widgets.units.ComboUnitsProvider;
import dedi.ui.widgets.units.IAmountInputValidator;
import dedi.ui.widgets.units.TextWithUnits;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.measure.quantity.Energy;
import javax.measure.quantity.Length;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

import static dedi.configuration.calculations.scattering.BeamQuantity.Quantities;

public class BeamlineQuantityPanel implements Observer {
	private BeamlineConfigurationTemplatesPanel templatesPanel;
	
	private TextWithUnits<Energy> energy;
	private TextWithUnits<Length> wavelength;
	private Amount<Length> minWavelength;
	private Amount<Length> maxWavelength;
	private Amount<Energy> minEnergy;
	private Amount<Energy> maxEnergy;
	
	private boolean isEdited = true;
	
	private final static List<Unit<Energy>> ENERGY_UNITS = new ArrayList<>(Arrays.asList(SI.KILO(NonSI.ELECTRON_VOLT), NonSI.ELECTRON_VOLT));
	private final static List<Unit<Length>> WAVELENGTH_UNITS = new ArrayList<>(Arrays.asList(SI.NANO(SI.METER), NonSI.ANGSTROM));
	
	
	public BeamlineQuantityPanel(Composite parent, BeamlineConfigurationTemplatesPanel panel) {
		templatesPanel = panel;
		panel.addObserver(this);
		
		Group beamlineQuantityGroup = GuiHelper.createGroup(parent, "Beamline quantity", 3);
		
		minWavelength = Amount.valueOf(0, SI.METER);
		maxWavelength = Amount.valueOf(Double.MAX_VALUE, SI.METER);
		minEnergy = Amount.valueOf(0, SI.JOULE);
		maxEnergy = Amount.valueOf(Double.MAX_VALUE, SI.JOULE);
		
		
		ComboUnitsProvider<Energy> energyUnitsCombo = new ComboUnitsProvider<>(beamlineQuantityGroup, ENERGY_UNITS);
		energy = new TextWithUnits<>(beamlineQuantityGroup, "Energy", energyUnitsCombo,
						             input ->  input.isLessThan(maxEnergy) && input.isGreaterThan(minEnergy));
		energy.addAmountChangeListener(() -> textChanged(Quantities.ENERGY));
		GridDataFactory.fillDefaults().span(2, 1).applyTo(energy);
		energyUnitsCombo.moveBelow(energy);
		
		
		ComboUnitsProvider<Length> wavelengthUnitsCombo = new ComboUnitsProvider<>(beamlineQuantityGroup, WAVELENGTH_UNITS);
		wavelength = new TextWithUnits<>(beamlineQuantityGroup, "Wavelength", wavelengthUnitsCombo,
				                         input -> input.isLessThan(maxWavelength) && input.isGreaterThan(minWavelength));
		wavelength.addAmountChangeListener(() -> textChanged(Quantities.WAVELENGTH));
		GridDataFactory.fillDefaults().span(2, 1).applyTo(wavelength);
		wavelengthUnitsCombo.moveBelow(wavelength);
		
		setToolTipTexts();
		
		wavelength.addUnitsChangeListener(() -> setToolTipTexts());
		energy.addUnitsChangeListener(() -> setToolTipTexts());
		
		update(null, null);
	}
	
	
	private void textChanged(Quantities q){
		if(isEdited){
			try{ 
				isEdited = false;
				switch(q){
				case ENERGY:
					wavelength.setValue(new dedi.configuration.calculations.scattering.Energy(energy.getValue())
							.toWavelength().getAmount().to(SI.METER));
					BeamlineConfiguration.getInstance().setWavelength(wavelength.getValue(SI.METER).getEstimatedValue());
					break;
				case WAVELENGTH:
					energy.setValue(new dedi.configuration.calculations.scattering.Wavelength(wavelength.getValue())
							.to(new dedi.configuration.calculations.scattering.Energy()).getAmount().to(SI.JOULE));
					BeamlineConfiguration.getInstance().setWavelength(wavelength.getValue(SI.METER).getEstimatedValue());
					break;
				default:
			}
			} catch(NullPointerException e){
				switch(q){
					case ENERGY:
						wavelength.clearText();
						BeamlineConfiguration.getInstance().setWavelength(null);
						break;
					case WAVELENGTH:
						energy.clearText();
						BeamlineConfiguration.getInstance().setWavelength(null);
						break;
					default:
				}
			} finally {
				isEdited = true;
			}
		}
	}

	
	
	@Override
	public void update(Observable o, Object arg) {
		BeamlineConfigurationBean beamlineConfiguration = templatesPanel.getPredefinedBeamlineConfiguration();
		if(beamlineConfiguration == null) return;
		minWavelength = Amount.valueOf(beamlineConfiguration.getMinWavelength()*1.0e-9, SI.METER);
		maxWavelength = Amount.valueOf(beamlineConfiguration.getMaxWavelength()*1.0e-9, SI.METER);
		minEnergy = new dedi.configuration.calculations.scattering.Wavelength(maxWavelength)
				      .to(new dedi.configuration.calculations.scattering.Energy()).getAmount().to(SI.JOULE);
		maxEnergy = new dedi.configuration.calculations.scattering.Wavelength(minWavelength)
			          .to(new dedi.configuration.calculations.scattering.Energy()).getAmount().to(SI.JOULE);
		BeamlineConfiguration.getInstance().setMaxWavelength(maxWavelength.doubleValue(SI.METER));
		BeamlineConfiguration.getInstance().setMinWavelength(minWavelength.doubleValue(SI.METER));
		setToolTipTexts();
	}
	
	
	private void setToolTipTexts(){
		if(minEnergy == null || maxEnergy == null || minWavelength == null || maxWavelength == null){
			energy.setToolTipText("");
			wavelength.setToolTipText("");
			return;
		}
		energy.setToolTipText("Min energy: " + TextUtil.format(minEnergy.doubleValue(energy.getCurrentUnit())) + 
				              "\nMax energy: " + TextUtil.format(maxEnergy.doubleValue(energy.getCurrentUnit())));
		wavelength.setToolTipText("Min wavelength: " + TextUtil.format(minWavelength.doubleValue(wavelength.getCurrentUnit())) + 
	              "\nMax wavelength: " + TextUtil.format(maxWavelength.doubleValue(wavelength.getCurrentUnit())));
	}
	
}
