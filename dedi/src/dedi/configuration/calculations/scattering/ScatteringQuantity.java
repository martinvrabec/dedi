package dedi.configuration.calculations.scattering;

import java.util.List;

import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;

import org.jscience.physics.amount.Amount;

public abstract class ScatteringQuantity<E extends Quantity>  {
	protected Amount<E> value;
	
	
	public ScatteringQuantity(){
	}
	
	
	public ScatteringQuantity(Amount<E> value){
		this.value = value;
	}
	
	
	public <U extends Quantity, T extends ScatteringQuantity<U>> T to(T scatteringQuantity){
		scatteringQuantity.setValue(this.toQ());
		return scatteringQuantity;
	}
	
	
	public abstract Q toQ();
	
	
	public Amount<E> getValue(){
		return value;
	}
	
	
	@SuppressWarnings("unchecked")
	public void setValue(Amount<?> value){
		this.value = (Amount<E>) value;
	}
	
	
	public abstract void setValue(Q q);
	
	public abstract Unit<E> getBaseUnit();
	
	public abstract List<Unit<E>> getUnits();
	
	public abstract String getQuantityName();
	
	
	@Override
	public String toString(){
		if(value == null) return "";
		return String.valueOf(value.getEstimatedValue());
	}

}