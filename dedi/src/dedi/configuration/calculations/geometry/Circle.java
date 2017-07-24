package dedi.configuration.calculations.geometry;

import javax.measure.quantity.Length;

import org.eclipse.draw2d.geometry.Vector;
import org.jscience.physics.amount.Amount;

public class Circle {
	private Vector centre;
	private Amount<Length> diameter;
	
	
	public Circle(Vector centre, Amount<Length> diameter) {
		super();
		this.centre = centre;
		this.diameter = diameter;
	}


	public Vector getCentre() {
		return centre;
	}


	public void setCentre(Vector centre) {
		this.centre = centre;
	}


	public Amount<Length> getDiameter() {
		return diameter;
	}


	public void setDiameter(Amount<Length> diameter) {
		this.diameter = diameter;
	}
	
	
	public Amount<Length> getRadius(){
		return this.diameter.divide(2.0);
	}
	
	
}
