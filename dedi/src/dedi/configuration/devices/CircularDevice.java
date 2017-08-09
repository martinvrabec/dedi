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


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((diameter == null) ? 0 : diameter.hashCode());
		result = prime * result + ((xcentre == null) ? 0 : xcentre.hashCode());
		result = prime * result + ((ycentre == null) ? 0 : ycentre.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CircularDevice other = (CircularDevice) obj;
		if (diameter == null) {
			if (other.diameter != null)
				return false;
		} else if(other.diameter == null) return false; 
		  else if (!diameter.equals(other.diameter) && diameter.doubleValue(SI.METER) != other.diameter.doubleValue(SI.METER))
			return false;
		if (xcentre == null) {
			if (other.xcentre != null)
				return false;
		} else if (!xcentre.equals(other.xcentre))
			return false;
		if (ycentre == null) {
			if (other.ycentre != null)
				return false;
		} else if (!ycentre.equals(other.ycentre))
			return false;
		return true;
	}
}
