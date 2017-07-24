package dedi.configuration.devices;

import javax.measure.quantity.Length;
import javax.measure.unit.SI;

import org.jscience.physics.amount.Amount;

public class CircularDevice {
	private Amount<Length> diameter;
	private Double xcentre;
	private Double ycentre;
	
	
	public CircularDevice(Amount<Length> diameter, Double xcentre, Double ycentre) {
		super();
		this.diameter = diameter.copy();
		this.xcentre = xcentre;
		this.ycentre = ycentre;
	}


	public Amount<Length> getDiameter() {
		return diameter.copy();
	}


	public Double getXCentre() {
		return xcentre;
	}


	public Double getYCentre() {
		return ycentre;
	}


	public Double getDiameterMM(){
		return diameter.doubleValue(SI.MILLIMETER);
	}
	
	
}
