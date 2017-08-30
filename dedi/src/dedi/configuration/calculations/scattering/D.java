package dedi.configuration.calculations.scattering;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.measure.quantity.Length;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

import org.jscience.physics.amount.Amount;

public class D extends ScatteringQuantity implements Length {
	public static final String NAME = "d";
	public static final Unit<D> BASE_UNIT = SI.METER.asType(D.class);
	private static final List<Unit<? extends ScatteringQuantity>> UNITS = 
			new ArrayList<>(Arrays.asList(SI.NANO(SI.METER.asType(D.class)), 
					                      NonSI.ANGSTROM.asType(D.class)));
	
	public D(){
	}
	
	public D(Amount<? extends ScatteringQuantity> value) {
		super(value.to(D.BASE_UNIT));
	}
	
	
	@Override
	public Unit<? extends ScatteringQuantity> getBaseUnit(){
		return D.BASE_UNIT;
	}
	
	
	@Override
	public List<Unit<? extends ScatteringQuantity>> getUnits(){
		return D.UNITS;
	}

	@SuppressWarnings("unchecked")
	@Override
	public D fromQ(Q q) {
		return new D(q.getValue().inverse().times(2*Math.PI).to(BASE_UNIT));
	}

	@Override
	public Q toQ() {
		return new Q(this.getValue().inverse().times(2*Math.PI).to(Q.BASE_UNIT));
	}

	
	@Override
	public String getQuantityName() {
		return NAME;
	}
}
