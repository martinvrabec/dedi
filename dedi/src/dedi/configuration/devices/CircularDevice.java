package dedi.configuration.devices;

import javax.measure.quantity.Length;
import javax.measure.unit.SI;

import org.jscience.physics.amount.Amount;

public class CircularDevice {
	private Amount<Length> diameter;
	private Double xcentre;
	private Double ycentre;
	
	
	/**
	 * @param diameter
	 *           The diameter of the device.
	 * @param xcentre
	 *           The x coordinate of the centre of the device in pixels.
	 * @param ycentre
	 *           The y coordinate of the centre of the device in pixels.
	 */
	public CircularDevice(Amount<Length> diameter, Double xcentre, Double ycentre) {
		super();
		this.diameter = diameter.copy();
		this.xcentre = xcentre;
		this.ycentre = ycentre;
	}


	public Amount<Length> getDiameter() {
		return diameter.copy();
	}


	/**
	 * @return The x coordinate of the centre of the device in pixels.
	 */
	public Double getXCentre() {
		return xcentre;
	}


	/**
	 * @return The y coordinate of the centre of the device in pixels.
	 */
	public Double getYCentre() {
		return ycentre;
	}


	/**
	 * @return The diameter of the device in millimeters.
	 */
	public Double getDiameterMM(){
		return diameter.doubleValue(SI.MILLIMETER);
	}
	
	
	/**
	 * @return The radius of the device in millimeters.
	 */
	public Double getRadiusMM(){
		return getDiameterMM()/2;
	}
}
