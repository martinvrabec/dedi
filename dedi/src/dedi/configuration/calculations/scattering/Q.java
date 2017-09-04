package dedi.configuration.calculations.scattering;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.measure.unit.NonSI;
import javax.measure.unit.ProductUnit;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

import org.jscience.physics.amount.Amount;


/**
 * A class that represents the magnitude of a scattering vector.
 */
public class Q extends ScatteringQuantity<InverseLength> {
	private static final String NAME = "q";
	public static final Unit<InverseLength> BASE_UNIT = new ProductUnit<>(SI.METER.inverse());
	private static final List<Unit<InverseLength>> UNITS = 
			new ArrayList<>(Arrays.asList(new ProductUnit<InverseLength>(SI.NANO(SI.METER).inverse()), 
					                      new ProductUnit<InverseLength>(NonSI.ANGSTROM.inverse())));
	
	public Q(){
	}
	
	public Q(double value){
		super(Amount.valueOf(value, Q.BASE_UNIT));
	}
	
	public Q(Amount<InverseLength> value) {
		super(value.to(Q.BASE_UNIT));
	}
	
	
	@Override
	public Unit<InverseLength> getBaseUnit(){
		return Q.BASE_UNIT;
	}
	
	
	@Override
	public List<Unit<InverseLength>> getUnits(){
		return Q.UNITS;
	}
	

	@Override
	public Q toQ() {
		return this;
	}


	@Override
	public String getQuantityName() {
		return NAME;
	}

	@Override
	public void setValue(Q q) {
		setValue(q.getValue());
	}
}