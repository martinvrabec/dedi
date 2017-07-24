package dedi.ui.widgets;

import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;

import org.eclipse.swt.widgets.Composite;

public abstract class UnitsProvider<T extends Quantity> {
	protected Composite parent;
	protected Unit<T> currentUnit;
	
	public UnitsProvider(Composite parent) {
		this.parent = parent;
		currentUnit = null;
	}
	
	public Unit<T> getCurrentUnit(){
		return currentUnit;
	}
	
	abstract void createUnitsProvider();
	
	abstract void addUnitsChangeListener(IUnitsChangeListener listener);
}