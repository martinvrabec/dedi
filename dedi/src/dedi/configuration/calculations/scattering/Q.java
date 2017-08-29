package dedi.configuration.calculations.scattering;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

import org.jscience.physics.amount.Amount;

public class Q extends ScatteringQuantity {
	private static final String NAME = "q";
	public static final Unit<?> BASE_UNIT = SI.METER.inverse();
	private static final List<Unit<?>> UNITS = new ArrayList<>(Arrays.asList(SI.NANO(SI.METER).inverse(), NonSI.ANGSTROM.inverse()));
	
	public Q(){
	}
	
	public Q(double value){
		super(Amount.valueOf(value, Q.BASE_UNIT));
	}
	
	public Q(Amount<?> value) {
		super(value.to(Q.BASE_UNIT));
	}
	
	
	@Override
	public Unit<?> getBaseUnit(){
		return Q.BASE_UNIT;
	}
	
	
	@Override
	public List<Unit<?>> getUnits(){
		return Q.UNITS;
	}
	
	@Override
	public <T extends ScatteringQuantity> T to(T scatteringQuantity) {
		return scatteringQuantity.fromQ(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Q fromQ(Q q) {
		return q;
	}

	@Override
	public Q toQ() {
		return this;
	}

	public static Q valueOf(Amount<?> value){
		return new Q(value);
	}


	@Override
	public String getQuantityName() {
		return NAME;
	}
}
