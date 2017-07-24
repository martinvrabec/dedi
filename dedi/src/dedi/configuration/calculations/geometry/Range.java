package dedi.configuration.calculations.geometry;

import javax.measure.quantity.Quantity;

import org.jscience.physics.amount.Amount;

public class Range<T extends Quantity> {
	Amount<T> min;
	Amount<T> max;
	
	public Range(Amount<T> min, Amount<T> max) {
		this.min = min;
		this.max = max;
		checkMinmax();
	}
	
	public Amount<T> getMin() {
		return min;
	}
	
	public Amount<T> getMax() {
		return max;
	}
	
	public void setMinimum(Amount<T> min) {
		this.min = min ;
		checkMinmax();	
	}
	
	public void setMaximum(Amount<T> max) {
		this.max = max ;
		checkMinmax();		
	}
	
	private void checkMinmax() {
		if (min == null || max == null) {
			return ;
		}
		if (min.isGreaterThan(max)) {
			Amount<T> t = max ;
			max = min ;
			min = t ;			
		}
	}
	
	
	public boolean contains(Amount<T> value){
		return (value.isGreaterThan(min) || value.approximates(min)) && (value.isLessThan(max) || value.approximates(max));
	}
	
	
	public Range<T> intersect(Range<T> other){
		if(other == null) return null;
		
		Amount<T> otherMin = other.getMin();
		Amount<T> otherMax = other.getMax();
		
		if(otherMin.isGreaterThan(max) || min.isGreaterThan(otherMax)) return null;
		
		double otherMinDoubleValue = otherMin.doubleValue(otherMin.getUnit());
		double otherMaxDoubleValue = otherMax.doubleValue(otherMin.getUnit());
		double thisMinDoubleValue = min.doubleValue(otherMin.getUnit());
		double thisMaxDoubleValue = max.doubleValue(otherMin.getUnit());
		
		double newmin = Math.max(otherMinDoubleValue, thisMinDoubleValue);
		double newmax = Math.min(otherMaxDoubleValue, thisMaxDoubleValue);
		
		return new Range<T>(Amount.valueOf(newmin, otherMin.getUnit()), Amount.valueOf(newmax, otherMin.getUnit()));
	}
	
	
	@Override
	public String toString(){
		return "[" + min.getEstimatedValue() + "," + max.getEstimatedValue() + "]";
	}
	
}
