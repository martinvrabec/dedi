package dedi.configuration.calculations.scattering;

import javax.measure.quantity.Length;
import javax.measure.quantity.Velocity;
import javax.measure.unit.SI;

import org.jscience.physics.amount.Amount;


public abstract class BeamQuantity {
	Amount<?> amount;
    
	public static enum Quantities {WAVELENGTH, ENERGY, BRAGG_ANGLE};
	
	public BeamQuantity() {
	}
	
	public BeamQuantity(Amount<?> amount) {
		super();
		this.amount = amount;
	}

	public BeamQuantity to(BeamQuantity quantity){
		return quantity.fromWavelength(this.toWavelength());
	}
	
	public abstract Wavelength toWavelength();
	
	public abstract BeamQuantity fromWavelength(Wavelength wavelength);
	
	public Amount<?> getAmount() {
		return amount;
	}

	public void setAmount(Amount<?> amount) {
		this.amount = amount;
	}
	
	
}