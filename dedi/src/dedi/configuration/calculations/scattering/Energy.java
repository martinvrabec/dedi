package dedi.configuration.calculations.scattering;

import javax.measure.unit.SI;

import org.jscience.physics.amount.Amount;
import org.jscience.physics.amount.Constants;


public class Energy extends BeamQuantity {

	public Energy() {
	}
	
	public Energy(Amount<?> value) {
		super(value);
	}
	
	@Override
	public Wavelength toWavelength() {
		return new Wavelength(this.getAmount().inverse().times(Constants.c).times(Constants.ℎ).to(SI.METER));
	}

	
	@Override
	public BeamQuantity fromWavelength(Wavelength wavelength) {
		return new Energy(wavelength.getAmount().inverse().times(Constants.c).times(Constants.ℎ).to(SI.JOULE));
	}

}