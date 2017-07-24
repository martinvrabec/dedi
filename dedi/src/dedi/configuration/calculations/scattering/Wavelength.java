package dedi.configuration.calculations.scattering;

import org.jscience.physics.amount.Amount;


public class Wavelength extends BeamQuantity {

	public Wavelength() {
	}
	
	public Wavelength(Amount<?> value) {
		super(value);
	}
	

	@Override
	public Wavelength toWavelength() {
		return this;
	}

	@Override
	public BeamQuantity fromWavelength(Wavelength wavelength) {
		return wavelength;
	}

}