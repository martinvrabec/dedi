package dedi.configuration.calculations.scattering;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.measure.unit.NonSI;
import javax.measure.unit.ProductUnit;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
import org.jscience.physics.amount.Amount;

public class S extends ScatteringQuantity {
	public static final String NAME = "s";
	public static final Unit<S> BASE_UNIT = new ProductUnit<>(SI.METER.inverse()); 
	private static final List<Unit<? extends ScatteringQuantity>> UNITS = 
			new ArrayList<>(Arrays.asList(new ProductUnit<S>(SI.NANO(SI.METER).inverse()), 
					                      new ProductUnit<S>(NonSI.ANGSTROM.inverse())));
	
	public S(){
	}
	
	public S(Amount<? extends ScatteringQuantity> value) {
		super(value.to(S.BASE_UNIT));
	}
	
	
	@Override
	public Unit<? extends ScatteringQuantity> getBaseUnit(){
		return S.BASE_UNIT;
	}
	
	
	@Override
	public List<Unit<? extends ScatteringQuantity>> getUnits(){
		return S.UNITS;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public S fromQ(Q q) {
		return new S(q.to(new D()).getValue().inverse().to(BASE_UNIT));
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
