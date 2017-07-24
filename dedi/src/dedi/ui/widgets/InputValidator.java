package dedi.ui.widgets;

import javax.measure.quantity.Quantity;

import org.jscience.physics.amount.Amount;

public interface InputValidator<T extends Quantity> {
	public boolean isValid(Amount<T> input);
}
