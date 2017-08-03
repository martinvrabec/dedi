package dedi.ui.views.results;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Observable;

import javax.measure.unit.Unit;

import org.jscience.physics.amount.Amount;

import dedi.configuration.BeamlineConfiguration;
import dedi.configuration.calculations.NumericRange;
import dedi.configuration.calculations.results.models.Results;
import dedi.configuration.calculations.results.models.ResultsService;
import dedi.configuration.calculations.scattering.Q;
import dedi.configuration.calculations.scattering.ScatteringQuantity;

public class DefaultResultsViewController extends AbstractResultsViewController {
	
	public DefaultResultsViewController(ResultsViewModel viewModel) {
		super(viewModel);
	}

	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().equals(Results.VISIBLE_Q_RANGE_PROPERTY)){
			NumericRange visibleQRange = resultsController.getVisibleQRange();
			if(visibleQRange != null){
				double minQ = visibleQRange.getMin();
				double maxQ = visibleQRange.getMax();
				if(getCurrentQuantity() != null || getCurrentUnit() != null){
					minQ = convert(minQ, new Q(), getCurrentQuantity(), Q.BASE_UNIT, getCurrentUnit());
					maxQ = convert(maxQ, new Q(), getCurrentQuantity(), Q.BASE_UNIT, getCurrentUnit());
					updateVisibleMin(minQ);
					updateVisibleMax(maxQ);
				}
			} else {
				updateVisibleMin((Double) null);
				updateVisibleMax((Double) null);
			}
			
		}
		if(evt.getPropertyName().equals(Results.FULL_Q_RANGE_PROPERTY)){
			NumericRange fullQRange = resultsController.getFullQRange();
			if(fullQRange != null){
				double minQ = fullQRange.getMin();
				double maxQ = fullQRange.getMax();
				if(getCurrentQuantity() != null || getCurrentUnit() != null){
					minQ = convert(minQ, new Q(), getCurrentQuantity(), Q.BASE_UNIT, getCurrentUnit());
					maxQ = convert(maxQ, new Q(), getCurrentQuantity(), Q.BASE_UNIT, getCurrentUnit());
					updateFullRangeMin(minQ);
					updateFullRangeMax(maxQ);
				}
			} else {
				updateFullRangeMin((Double) null);
				updateFullRangeMax((Double) null);
			}
		}
		super.propertyChange(evt);
    }
	
	
	@Override
	public void updateQuantities(List<ScatteringQuantity> newQuantities) {
		setModelProperty(ResultsViewModel.QUANTITIES_PROPERTY, newQuantities, List.class);
		updateCurrentQuantity(newQuantities.get(0));
	};
	
	
	@Override
	public void updateCurrentQuantity(ScatteringQuantity newQuantity){
		ScatteringQuantity oldQuantity = getCurrentQuantity();
		if(oldQuantity != null && newQuantity.getClass().equals(oldQuantity.getClass())) return;
		Unit<?> oldUnit = getCurrentUnit();
		
		setModelProperty(ResultsViewModel.CURRENT_QUANTITY_PROPERTY, newQuantity, ScatteringQuantity.class);
		setModelProperty(ResultsViewModel.CURRENT_UNITS_PROPERTY, newQuantity.getUnits(), List.class);
		setModelProperty(ResultsViewModel.CURRENT_UNIT_PROPERTY, newQuantity.getUnits().get(0), Unit.class);
		
		if(oldQuantity != null || oldUnit != null)
			updateRanges(oldQuantity, newQuantity, oldUnit, newQuantity.getUnits().get(0));	
	}
	
	
	@Override
	public void updateCurrentUnits(List<Unit<?>> newUnits){
		setModelProperty(ResultsViewModel.CURRENT_UNITS_PROPERTY, newUnits, List.class);
		updateCurrentUnit(newUnits.get(0));
	}
	
	
	@Override
	public void updateCurrentUnit(Unit<?> newUnit){
		ScatteringQuantity currentQuantity = getCurrentQuantity();
		Unit<?> oldUnit = getCurrentUnit();
		if(oldUnit != null && oldUnit.equals(newUnit)) return;
		
		setModelProperty(ResultsViewModel.CURRENT_UNIT_PROPERTY, newUnit, Unit.class);
		
		if(currentQuantity != null || oldUnit != null) 
			updateRanges(currentQuantity, currentQuantity, oldUnit, newUnit);
	}
	
	
	private void updateRanges(ScatteringQuantity oldQuantity, ScatteringQuantity newQuantity, 
            Unit<?> oldUnit, Unit<?> newUnit){
	
		Double newVisibleMin = convert(getVisibleMin(), oldQuantity, newQuantity, oldUnit, newUnit);
		Double newVisibleMax = convert(getVisibleMax(), oldQuantity, newQuantity, oldUnit, newUnit);
		Double newRequestedMin = convert(getRequestedMin(), oldQuantity, newQuantity, oldUnit, newUnit);
		Double newRequestedMax = convert(getRequestedMax(), oldQuantity, newQuantity, oldUnit, newUnit);
		Double newFullRangeMin = convert(getFullRangeMin(), oldQuantity, newQuantity, oldUnit, newUnit);
		Double newFullRangeMax = convert(getFullRangeMax(), oldQuantity, newQuantity, oldUnit, newUnit);
		
		if(newVisibleMin != null && newVisibleMax != null){
			Double temp = newVisibleMin;
			newVisibleMin = Math.min(newVisibleMin, newVisibleMax);
			newVisibleMax = Math.max(temp, newVisibleMax);
		}
		if(newRequestedMin != null && newRequestedMax != null){
			Double temp = newRequestedMin;
			newRequestedMin =  Math.min(newRequestedMin, newRequestedMax);
			newRequestedMax = Math.max(temp, newRequestedMax);
		}
		if(newFullRangeMin != null && newFullRangeMax != null){
			Double temp = newFullRangeMin;
			newFullRangeMin =  Math.min(newFullRangeMin, newFullRangeMax);
			newFullRangeMax = Math.max(temp, newFullRangeMax);
		}
		
		updateVisibleMin(newVisibleMin);
		updateVisibleMax(newVisibleMax);
		updateRequestedMin(newRequestedMin);
		updateRequestedMax(newRequestedMax);
		updateFullRangeMin(newFullRangeMin);
		updateFullRangeMax(newFullRangeMax);
	}
	
	
	private Double convert(Double value, ScatteringQuantity oldQuantity, ScatteringQuantity newQuantity, 
			              Unit<?> oldUnit, Unit<?> newUnit){
		if(value == null) return null;
		oldQuantity.setValue(Amount.valueOf(value, oldUnit));
		return oldQuantity.to(newQuantity).getValue().to(newUnit).getEstimatedValue();
	}
	
	
	
	@Override
	public void updateRequestedMin(String newMin){
		double minValue;
		try{
			minValue = Double.parseDouble(newMin);
			updateRequestedMin(minValue);
		} catch(NumberFormatException ex){
			setModelProperty(ResultsViewModel.REQUESTED_MIN_PROPERTY, null, Double.class);
			resultsController.updateRequestedQRange(null, null);
		}
	}
	
	
	@Override
	public void updateRequestedMin(Double newMin){
		setModelProperty(ResultsViewModel.REQUESTED_MIN_PROPERTY, newMin, Double.class);
		updateRequestedQRange();
	}
	
	
	@Override
	public void updateRequestedMax(String newMax){
		double maxValue;
		try{
			maxValue = Double.parseDouble(newMax);
			updateRequestedMax(maxValue);
		} catch(NumberFormatException ex){
			setModelProperty(ResultsViewModel.REQUESTED_MAX_PROPERTY, null, Double.class);
			resultsController.updateRequestedQRange(null, null);
		}
	}
	
	
	@Override
	public void updateRequestedMax(Double newMax){
		setModelProperty(ResultsViewModel.REQUESTED_MAX_PROPERTY, newMax, Double.class);
		updateRequestedQRange();
	}
	
	
	private void updateRequestedQRange(){
		Double minValue = getRequestedMin();
		Double maxValue = getRequestedMax();
		
		if(minValue == null || maxValue == null){
			resultsController.updateRequestedQRange(null, null);
			return;
		}
		
		minValue = convert(minValue, getCurrentQuantity(), new Q(), getCurrentUnit(), Q.BASE_UNIT);
		maxValue = convert(maxValue, getCurrentQuantity(), new Q(), getCurrentUnit(), Q.BASE_UNIT);
		
		resultsController.updateRequestedQRange(new Q(minValue), new Q(maxValue));
	}
	
	
	@Override
	protected void updateVisibleMin(Double newMin){
		setModelProperty(ResultsViewModel.VISIBLE_MIN_PROPERTY, newMin, Double.class);
	}
	
	
	@Override
	protected void updateVisibleMax(Double newMax){
		setModelProperty(ResultsViewModel.VISIBLE_MAX_PROPERTY, newMax, Double.class);
	}


	@Override
	protected void updateFullRangeMin(Double newMin) {
		setModelProperty(ResultsViewModel.FULL_RANGE_MIN_PROPERTY, newMin, Double.class);
	}


	@Override
	protected void updateFullRangeMax(Double newMax) {
		setModelProperty(ResultsViewModel.FULL_RANGE_MAX_PROPERTY, newMax, Double.class);
	}
	
}
