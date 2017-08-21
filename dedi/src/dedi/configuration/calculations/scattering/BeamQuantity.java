package dedi.configuration.calculations.scattering;


import org.jscience.physics.amount.Amount;


public abstract class BeamQuantity {
	Amount<?> amount;
    
	public enum Quantities {WAVELENGTH, ENERGY};
	
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