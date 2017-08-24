package dedi.configuration.calculations.scattering;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

import org.jscience.physics.amount.Amount;

public class D extends ScatteringQuantity {
	public static final String NAME = "d";
	public static final Unit<?> BASE_UNIT = SI.METER; 
	public static final List<Unit<?>> UNITS = new ArrayList<>(Arrays.asList(SI.NANO(SI.METER), NonSI.ANGSTROM));
	
	public D(){
	}
	
	public D(Amount<?> value) {
		super(value.to(D.BASE_UNIT));
	}
	
	
	@Override
	public Unit<?> getBaseUnit(){
		return D.BASE_UNIT;
	}
	
	
	@Override
	public List<Unit<?>> getUnits(){
		return D.UNITS;
	}

	@SuppressWarnings("unchecked")
	@Override
	public D fromQ(Q q) {
		return new D(q.getValue().inverse().times(2*Math.PI));
	}

	@Override
	public Q toQ() {
		return new Q(this.getValue().inverse().times(2*Math.PI));
	}

	public static void main(String[] args){
		Q qvalue = new Q(Amount.valueOf(1.0, Q.BASE_UNIT));
		Amount<?> dvalue = qvalue.to(new D()).getValue().to(D.BASE_UNIT);
		
		System.out.println(dvalue.getUnit());
		System.out.println(dvalue.getEstimatedValue());
	}
	
	@Override
	public String getQuantityName() {
		return NAME;
	}
}
