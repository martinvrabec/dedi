package dedi.ui.widgets.units;

import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;

import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.jscience.physics.amount.Amount;

import dedi.ui.GuiHelper;
import dedi.ui.TextUtil;

public class LabelWithUnits<T extends Quantity> extends WidgetWithUnits<T> implements IAmountChangeListener {
	private Label valueLabel;
	
	public LabelWithUnits(Composite parent, String name, UnitsProvider<T> provider) {
		super(parent, name, provider);
		valueLabel = GuiHelper.createLabel(parent, "");
		valueLabel.moveBelow(nameLabel);
		
		addAmountChangeListener(this);
		
		parent.layout();
	}
	
	
	public void clearText(){
		valueLabel.setText("");
		setValue(null);
	}


	@Override
	public void amountChanged() {
		if(currentAmount == null) return;
		valueLabel.setText(TextUtil.format(currentAmount.getEstimatedValue()));
	}
}
