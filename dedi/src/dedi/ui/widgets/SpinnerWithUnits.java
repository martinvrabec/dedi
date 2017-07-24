package dedi.ui.widgets;

import javax.measure.quantity.Quantity;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;


public class SpinnerWithUnits<T extends Quantity> extends WidgetWithUnits<T> implements IAmountChangeListener {
	private Spinner spinner;
	private int numOfDecimalPlaces;
	private boolean isEdited = true;
	
	public SpinnerWithUnits(Composite parent, String name, UnitsProvider<T> provider) {
		super(parent, name, provider);
		spinner = new Spinner(parent, SWT.BORDER);
		spinner.moveBelow(nameLabel);
		numOfDecimalPlaces = 0;
		
		addAmountChangeListener(this);
		
		unitsProvider.addUnitsChangeListener(new IUnitsChangeListener() {
			@Override
			public void unitsChanged() {
				// TODO Update the minimum, maximum, digits and increment.
			}
		});
		
		spinner.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				if(!isEdited) return;
				isEdited = false;
				setValue(spinner.getSelection()/Math.pow(10, numOfDecimalPlaces));
				isEdited = true;
			}
		});
		
		parent.layout();
	}

	
	public void setSpinnerValues(int selection, int minimum, int maximum, int digits, int increment, int pageIncrement){
		spinner.setValues(selection, minimum, maximum, digits, increment, pageIncrement);
		numOfDecimalPlaces = digits;
	}
	
	
	public void setSpinnerSelection(int selection){
		spinner.setSelection(selection);
	}
	
	
	public void setNumberOfDecimalPlaces(int digits){
		spinner.setDigits(digits);
	}
	
	
	@Override
	public void amountChanged() {
		if(!isEdited) return;
		isEdited = false;
		spinner.setSelection((int) (currentAmount.getEstimatedValue()*Math.pow(10, numOfDecimalPlaces)));
		isEdited = true;
	}
}
