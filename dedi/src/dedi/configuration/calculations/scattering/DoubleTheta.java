package dedi.configuration.calculations.scattering;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import javax.measure.quantity.Angle;
import javax.measure.quantity.Length;
import javax.measure.unit.SI;
import javax.measure.unit.NonSI;
import javax.measure.unit.Unit;

import org.jscience.physics.amount.Amount;

public class DoubleTheta extends ScatteringQuantity<Angle> {
	private Amount<Length> wavelength;
	public static final String NAME = "2\u03B8";
	public static final Unit<Angle> BASE_UNIT = SI.RADIAN;
	private static final List<Unit<Angle>> UNITS = 
			new ArrayList<>(Arrays.asList(NonSI.DEGREE_ANGLE, SI.RADIAN));
	
	
	public DoubleTheta(){
	}
	
	
	public DoubleTheta(Double wavelength){
		this.wavelength = (wavelength == null) ? null : Amount.valueOf(wavelength, SI.METER);
	}
	
	
	public DoubleTheta(double doubleTheta, Double wavelength){
		super(Amount.valueOf(doubleTheta, BASE_UNIT));
		this.wavelength = (wavelength == null) ? null : Amount.valueOf(wavelength, SI.METER);
	}
	
	public DoubleTheta(Amount<Angle> doubleTheta, Amount<Length> wavelength) {
		super(doubleTheta.to(BASE_UNIT));
		this.wavelength = wavelength;
	}
	
	
	public DoubleTheta(Amount<Length> wavelength) {
		this.wavelength = wavelength;
	}
	
	
	@Override
	public Unit<Angle> getBaseUnit() {
		return BASE_UNIT;
	}

	@Override
	public List<Unit<Angle>> getUnits() {
		return UNITS;
	}

	
	@Override
	public Q toQ() {
		if(wavelength == null) return new Q();
		return new Q(4*Math.PI*Math.sin(value.to(BASE_UNIT).getEstimatedValue()/2)/
				     wavelength.doubleValue(SI.METER));
	}

	@Override
	public String getQuantityName() {
		return NAME;
	}
	
	
	public void setWavelength(Double wavelength){
		this.wavelength = (wavelength == null) ? null : Amount.valueOf(wavelength, SI.METER);
	}
	
	
	public void setWavelength(Amount<Length> wavelength){
		this.wavelength = wavelength;
	}


	@Override
	public void setValue(Q q) {
		this.value = 
				Amount.valueOf(2*Math.asin(q.getValue().to(Q.BASE_UNIT).getEstimatedValue()*wavelength.doubleValue(SI.METER)/(4*Math.PI)), BASE_UNIT);
	}
	
}