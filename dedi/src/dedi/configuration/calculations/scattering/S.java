package dedi.configuration.calculations.scattering;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
import org.jscience.physics.amount.Amount;

public class S extends ScatteringQuantity {
	public static final String NAME = "s";
	public static final Unit<?> BASE_UNIT = SI.METER.inverse(); 
	private static final List<Unit<?>> UNITS = new ArrayList<>(Arrays.asList(SI.NANO(SI.METER).inverse(), NonSI.ANGSTROM.inverse()));
	
	public S(){
	}
	
	public S(Amount<?> value) {
		super(value.to(S.BASE_UNIT));
	}
	
	
	@Override
	public Unit<?> getBaseUnit(){
		return S.BASE_UNIT;
	}
	
	
	@Override
	public List<Unit<?>> getUnits(){
		return S.UNITS;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public S fromQ(Q q) {
		return new S(q.to(new D()).getValue().inverse());
	}

	@Override
	public Q toQ() {
		return new Q(this.getValue().times(Math.PI*2));
	}

	@Override
	public String getQuantityName() {
		return NAME;
	}
}
