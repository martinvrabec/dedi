package dedi.configuration.calculations.scattering;

import javax.measure.unit.SI;

import org.jscience.physics.amount.Amount;


public class Energy extends BeamQuantity {

	public Energy() {
	}
	
	public Energy(Amount<?> value) {
		super(value);
	}
	
	@Override
	public Wavelength toWavelength() {
		return new Wavelength(this.getAmount().inverse().times(Constants.c).times(Constants.h).to(SI.METER));
	}

	
	@Override
	public BeamQuantity fromWavelength(Wavelength wavelength) {
		return new Energy(wavelength.getAmount().inverse().times(Constants.c).times(Constants.h).to(SI.JOULE));
	}

}