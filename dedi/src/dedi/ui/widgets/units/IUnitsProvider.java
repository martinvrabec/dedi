package dedi.ui.widgets.units;

import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;

import org.eclipse.swt.widgets.Composite;

public interface IUnitsProvider<T extends Quantity> {
	
	public Unit<T> getCurrentUnit();
	
	public void addUnitsChangeListener(IUnitsChangeListener listener);
	
	public void removeUnitsChangeListener(IUnitsChangeListener listener);
}