package dedi.ui.widgets.units;

import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;

import org.eclipse.swt.widgets.Composite;


import dedi.ui.GuiHelper;

public class LabelUnitsProvider<T extends Quantity> extends WidgetUnitsProvider<T> {
	public LabelUnitsProvider(Composite parent, Unit<T> unit) {
		super(parent);
		currentUnit = unit;
		GuiHelper.createLabel(this, currentUnit.toString());
	}
}