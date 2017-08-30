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

public class DoubleTheta extends ScatteringQuantity implements Angle {
	private Amount<Length> wavelength;
	public static final String NAME = "2\u03B8";
	public static final Unit<DoubleTheta> BASE_UNIT = SI.RADIAN.asType(DoubleTheta.class);
	private static final List<Unit<? extends ScatteringQuantity>> UNITS = 
			new ArrayList<>(Arrays.asList(NonSI.DEGREE_ANGLE.asType(DoubleTheta.class), SI.RADIAN.asType(DoubleTheta.class)));
	
	
	public DoubleTheta(){
	}
	
	
	public DoubleTheta(Double wavelength){
		this.wavelength = (wavelength == null) ? null : Amount.valueOf(wavelength, SI.METER);
	}
	
	
	public DoubleTheta(double doubleTheta, Double wavelength){
		super(Amount.valueOf(doubleTheta, BASE_UNIT));
		this.wavelength = (wavelength == null) ? null : Amount.valueOf(wavelength, SI.METER);
	}
	
	public DoubleTheta(Amount<? extends ScatteringQuantity> doubleTheta, Amount<Length> wavelength) {
		super(doubleTheta.to(BASE_UNIT));
		this.wavelength = wavelength;
	}
	
	
	public DoubleTheta(Amount<Length> wavelength) {
		this.wavelength = wavelength;
	}
	
	
	@Override
	public Unit<? extends ScatteringQuantity> getBaseUnit() {
		return BASE_UNIT;
	}

	@Override
	public List<Unit<? extends ScatteringQuantity>> getUnits() {
		return UNITS;
	}

	@SuppressWarnings("unchecked")
	@Override
	public DoubleTheta fromQ(Q q) {
		if(wavelength == null) return null;
		return 
		 new DoubleTheta(2*Math.asin(q.getValue().to(Q.BASE_UNIT).getEstimatedValue()*wavelength.doubleValue(SI.METER)/(4*Math.PI)), 
				         wavelength.doubleValue(SI.METER));
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
	
}