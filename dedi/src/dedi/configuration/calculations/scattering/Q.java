package dedi.configuration.calculations.scattering;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.measure.unit.BaseUnit;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

import org.jscience.physics.amount.Amount;

public class Q extends ScatteringQuantity {
	public static final Unit<?> BASE_UNIT = SI.METER.inverse();
	public static final List<Unit<?>> UNITS = new ArrayList<Unit<?>>(Arrays.asList(SI.NANO(SI.METER).inverse(), NonSI.ANGSTROM.inverse()));
	
	public Q(){
	}
	
	public Q(double value){
		super(Amount.valueOf(value, Q.BASE_UNIT));
	}
	
	public Q(Amount<?> value) {
		super(value.to(Q.BASE_UNIT));
	}
	
	@Override
	public <T extends ScatteringQuantity> T to(T scatteringQuantity) {
		return scatteringQuantity.fromQ(this);
	}

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
	
}
