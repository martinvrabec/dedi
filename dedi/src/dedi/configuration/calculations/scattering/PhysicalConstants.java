package dedi.configuration.calculations.scattering;

import javax.measure.quantity.ElectricCharge;
import javax.measure.quantity.Velocity;
import javax.measure.unit.SI;

import org.jscience.physics.amount.Amount;

public class PhysicalConstants {
	
	private PhysicalConstants() {
		throw new IllegalStateException("This class is not meant to be instantiated.");
	}
	
	/**
	 * Holds the speed of light in vacuum (exact). 299792458m/s
	 */
	public static final Amount<Velocity> c = Amount.valueOf(299792458, SI.METERS_PER_SECOND);

	/**
	 * Holds the Planck constant. 6.6260693E-34 Js
	 * 
	 * @see <a href="http://en.wikipedia.org/wiki/Planck_constant"> Wikipedia: Plank%27s constant</a>
	 */
	public static final Amount<?> h = Amount.valueOf(6.6260693E-34, 0.0000011E-34, SI.JOULE.times(SI.SECOND));

	/**
	 * Holds the elementary charge (positron charge). 1.60217653E-19C
	 * 
	 * @see <a href="http://en.wikipedia.org/wiki/Elementary_charge"> Wikipedia: Elementary Charge</a>
	 */
	public static final Amount<ElectricCharge> e = Amount.valueOf(1.60217653E-19, 0.00000014E-19, SI.COULOMB);
}
