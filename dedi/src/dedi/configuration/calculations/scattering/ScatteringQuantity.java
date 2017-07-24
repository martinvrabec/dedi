package dedi.configuration.calculations.scattering;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.measure.quantity.Quantity;

import org.jscience.physics.amount.Amount;

public abstract class ScatteringQuantity implements Quantity {
	protected Amount<?> value;
	
	
	public ScatteringQuantity(){
	}
	
	
	public ScatteringQuantity(Amount<?> value){
		this.value = value;
	}
	
	
	public <T extends ScatteringQuantity> T to(T scatteringQuantity){
		return scatteringQuantity.fromQ(this.toQ());
	}
	
	
	public abstract <T extends ScatteringQuantity> T fromQ(Q q);
	
	public abstract Q toQ();
	
	
	public Amount<?> getValue(){
		return value;
	}
	
	
	public void setValue(Amount<?> value){
		this.value = value;
	}
	
	
	@Override
	public String toString(){
		return String.valueOf(value.getEstimatedValue());
	}

}
