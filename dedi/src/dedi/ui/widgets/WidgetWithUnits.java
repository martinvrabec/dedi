package dedi.ui.widgets;

import java.util.ArrayList;
import java.util.List;

import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.jscience.physics.amount.Amount;

import dedi.ui.GuiHelper;

public abstract class WidgetWithUnits<T extends Quantity> {
	protected Composite parent;
	protected Label nameLabel;
	protected UnitsProvider<T> unitsProvider;
	protected Amount<T> currentAmount;
	private List<IAmountChangeListener> listeners = new ArrayList<>();
	
	public WidgetWithUnits(Composite parent, String name, UnitsProvider<T> provider) {
		nameLabel = GuiHelper.createLabel(parent, name);
		this.parent = parent;
		unitsProvider = provider;
		unitsProvider.createUnitsProvider();
		
		provider.addUnitsChangeListener(new IUnitsChangeListener() {
			@Override
			public void unitsChanged() {
				if(currentAmount == null) return;
				setValue(currentAmount);
			}
		});
	}
	
	
	public void addAmountChangeListener(IAmountChangeListener listener){
		listeners.add(listener);
	}
	
	
	public void addUnitsChangeListener(IUnitsChangeListener listener){
		unitsProvider.addUnitsChangeListener(listener);
	}
	
	
	private void notifyListeners(){
		for(IAmountChangeListener listener : listeners) listener.amountChanged();
	}
	
	
	public Amount<T> getValue(Unit<T> unit){
		if(currentAmount == null) return null;
		return currentAmount.to(unit);
	}
	

	public Amount<T> getValue(){
		return getValue(unitsProvider.getCurrentUnit());
	}
	
	
	public Unit<T> getCurrentUnit(){
		return unitsProvider.getCurrentUnit();
	}
	
	
	public void clear(){
		currentAmount = null;
		notifyListeners();
		
	}
	
	
	public void setValue(Amount<T> value){
		currentAmount = (value == null) ? null : value.to(unitsProvider.getCurrentUnit());
		notifyListeners();
		parent.layout();
	}
	
	
	public void setValue(double value){
		currentAmount = Amount.valueOf(value, unitsProvider.getCurrentUnit());
		notifyListeners();
		parent.layout();
	}
}
