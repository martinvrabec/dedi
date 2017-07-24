package dedi.ui.widgets;

import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.jscience.physics.amount.Amount;

import dedi.ui.GuiHelper;
import dedi.ui.TextUtil;

public class TextWithUnits<T extends Quantity> extends WidgetWithUnits<T> implements IAmountChangeListener {
	private InputValidator<T> validator;
	private Text text;
	private boolean isEdited = true;
	
	
	public TextWithUnits(Composite parent, String name, UnitsProvider<T> provider){
		this(parent, name, provider, new InputValidator<T>() {
			@Override
			public boolean isValid(Amount<T> input){
				return true;
			}
		});
	}
	
	
	public TextWithUnits(Composite parent, String name, UnitsProvider<T> provider, InputValidator<T> validator) {
		super(parent, name, provider);
		text = new Text(parent, SWT.SINGLE | SWT.BORDER);
		text.moveBelow(nameLabel);
		this.validator = validator;
		
		addAmountChangeListener(this);
		
		text.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				if(!isEdited) return;
				isEdited = false;
				try{
					Amount<T> newValue = Amount.valueOf(Double.parseDouble(text.getText()), unitsProvider.getCurrentUnit());
					if(validator.isValid(newValue)){
						text.setForeground(new Color(Display.getCurrent(), new RGB(0, 0, 0)));
						setValue(newValue);
					} else throw new NumberFormatException();
				} catch(NumberFormatException ex){
					text.setForeground(new Color(Display.getCurrent(), new RGB(255, 0, 0)));
					setValue(null);
				} finally {
					isEdited = true;
				}
			}
		});
		
		parent.layout();
	}
	
	
	
	public void setToolTipText(String ttt){
		text.setToolTipText(ttt);
	}
	
	
	public void clearText(){
		text.setText("");
	}
	

	@Override
	public void amountChanged() {
		if(!isEdited) return;
		try{
			isEdited = false;
			if(currentAmount == null || !validator.isValid(currentAmount))
				text.setForeground(new Color(Display.getCurrent(), new RGB(255, 0, 0)));
			else
				text.setForeground(new Color(Display.getCurrent(), new RGB(0, 0, 0)));
			text.setText(TextUtil.format(currentAmount.getEstimatedValue()));
		} catch(NullPointerException e){
			e.printStackTrace();
		} finally {
			isEdited = true;
		}
		
	}
}