package dedi.ui.widgets.units;

import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import dedi.ui.GuiHelper;

public class LabelUnitsProvider<T extends Quantity> extends UnitsProvider<T> {
	private Label label;

	public LabelUnitsProvider(Composite parent, Unit<T> unit) {
		super(parent);
		currentUnit = unit;
	}
	
	@Override
	void addUnitsChangeListener(IUnitsChangeListener listener) {
	}

	@Override
	void createUnitsProvider() {
		label = GuiHelper.createLabel(parent, currentUnit.toString());
	}

}