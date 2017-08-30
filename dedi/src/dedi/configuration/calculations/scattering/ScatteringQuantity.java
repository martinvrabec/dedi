package dedi.configuration.calculations.scattering;

import java.util.List;

import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;

import org.jscience.physics.amount.Amount;

public abstract class ScatteringQuantity implements Quantity {
	protected Amount<? extends ScatteringQuantity> value;
	
	public ScatteringQuantity(){
	}
	
	
	public ScatteringQuantity(Amount<? extends ScatteringQuantity> value){
		this.value = value;
	}
	
	
	public <T extends ScatteringQuantity> T to(T scatteringQuantity){
		return scatteringQuantity.fromQ(this.toQ());
	}
	
	
	public abstract Unit<? extends ScatteringQuantity> getBaseUnit();
	
	public abstract List<Unit<? extends ScatteringQuantity>> getUnits();
	
	public abstract <T extends ScatteringQuantity> T fromQ(Q q);
	
	public abstract Q toQ();
	
	
	public Amount<? extends ScatteringQuantity> getValue(){
		return value;
	}
	
	
	public void setValue(Amount<? extends ScatteringQuantity> value){
		this.value = value;
	}
	
	
	public abstract String getQuantityName();
	
	
	@Override
	public String toString(){
		if(value == null) return "";
		return String.valueOf(value.getEstimatedValue());
	}

}
