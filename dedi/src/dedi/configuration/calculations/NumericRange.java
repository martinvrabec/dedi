package dedi.configuration.calculations;

public class NumericRange {
	private double min;
	private double max;
	
	
	public NumericRange(double min, double max) {
		super();
		this.min = min;
		this.max = max;
		checkMinMax();
	}


	public double getMin() {
		return min;
	}


	public void setMin(double min) {
		this.min = min;
		checkMinMax();
	}


	public double getMax() {
		return max;
	}


	public void setMax(double max) {
		this.max = max;
		checkMinMax();
	}


	private void checkMinMax() {
		if (min > max) {
			double t = max;
			max = min;
			min = t;			
		}
	}
	
	
	public boolean contains(double value){
		return value >= min && value <= max;
	}
	
	
	public boolean contains(NumericRange other){
		if(other == null) return true;
		else return other.getMin() >= this.getMin() && other.getMax() <= this.getMax();
	}
	
	
	public NumericRange intersect(NumericRange other){
		if(other == null) return null;
		
		double otherMin = other.getMin();
		double otherMax = other.getMax();
		
		if(otherMin > max || min > otherMax) return null;
		
		return new NumericRange(Math.max(otherMin, min), Math.min(otherMax, max));
	}
	
	
	@Override
	public String toString(){
		return "[" + min + "," + max + "]";
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(max);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(min);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NumericRange other = (NumericRange) obj;
		if (Double.doubleToLongBits(max) != Double.doubleToLongBits(other.max))
			return false;
		if (Double.doubleToLongBits(min) != Double.doubleToLongBits(other.min))
			return false;
		return true;
	}
	
	
}
