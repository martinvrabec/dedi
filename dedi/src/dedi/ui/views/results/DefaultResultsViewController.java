package dedi.ui.views.results;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Observable;

import javax.measure.unit.Unit;

import org.jscience.physics.amount.Amount;

import dedi.configuration.BeamlineConfiguration;
import dedi.configuration.calculations.NumericRange;
import dedi.configuration.calculations.scattering.Q;
import dedi.configuration.calculations.scattering.ScatteringQuantity;
import dedi.ui.models.ResultsModel;
import dedi.ui.models.ResultsService;

public class DefaultResultsViewController extends AbstractResultsViewController {
	
	public DefaultResultsViewController(ResultsViewModel viewModel) {
		super(viewModel);
	}

	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().equals(ResultsModel.VISIBLE_Q_RANGE_PROPERTY)){
			NumericRange visibleQRange = resultsModel.getVisibleQRange();
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
		super.propertyChange(evt);
    }
	
	
	@Override
	public void updateQuantities(List<ScatteringQuantity> newQuantities) {
		setModelProperty(ResultsViewModel.QUANTITIES_PROPERTY, newQuantities, List.class);
		updateCurrentQuantity(newQuantities.get(0));
	};
	
	
	@Override
	public void updateCurrentQuantity(ScatteringQuantity newQuantity){
		ScatteringQuantity oldQuantity = (ScatteringQuantity) getModelProperty(ResultsViewModel.CURRENT_QUANTITY_PROPERTY);
		if(oldQuantity != null && newQuantity.getClass().equals(oldQuantity.getClass())) return;
		Unit<?> oldUnit = (Unit<?> ) getModelProperty(ResultsViewModel.CURRENT_UNIT_PROPERTY);
		
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
		ScatteringQuantity currentQuantity = (ScatteringQuantity) getModelProperty(ResultsViewModel.CURRENT_QUANTITY_PROPERTY);
		Unit<?> oldUnit = (Unit<?> ) getModelProperty(ResultsViewModel.CURRENT_UNIT_PROPERTY);
		if(oldUnit != null && oldUnit.equals(newUnit)) return;
		
		setModelProperty(ResultsViewModel.CURRENT_UNIT_PROPERTY, newUnit, Unit.class);
		
		if(currentQuantity != null || oldUnit != null) 
			updateRanges(currentQuantity, currentQuantity, oldUnit, newUnit);
	}
	
	
	private void updateRanges(ScatteringQuantity oldQuantity, ScatteringQuantity newQuantity, 
            Unit<?> oldUnit, Unit<?> newUnit){
		Double visibleMin = (Double) getModelProperty(ResultsViewModel.VISIBLE_MIN_PROPERTY);
		Double visibleMax = (Double) getModelProperty(ResultsViewModel.VISIBLE_MAX_PROPERTY);
		Double requestedMin = (Double) getModelProperty(ResultsViewModel.REQUESTED_MIN_PROPERTY);
		Double requestedMax = (Double) getModelProperty(ResultsViewModel.REQUESTED_MAX_PROPERTY);
		
		Double newVisibleMin = convert(visibleMin, oldQuantity, newQuantity, oldUnit, newUnit);
		Double newVisibleMax = convert(visibleMax, oldQuantity, newQuantity, oldUnit, newUnit);
		Double newRequestedMin = convert(requestedMin, oldQuantity, newQuantity, oldUnit, newUnit);
		Double newRequestedMax = convert(requestedMax, oldQuantity, newQuantity, oldUnit, newUnit);
		
		
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
		
		updateVisibleMin(newVisibleMin);
		updateVisibleMax(newVisibleMax);
		updateRequestedMin(newRequestedMin);
		updateRequestedMax(newRequestedMax);
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
		Double minValue = (Double) getModelProperty(ResultsViewModel.REQUESTED_MIN_PROPERTY);
		Double maxValue = (Double) getModelProperty(ResultsViewModel.REQUESTED_MAX_PROPERTY);
		
		if(minValue == null || maxValue == null){
			resultsController.updateRequestedQRange(null, null);
			return;
		}
		
		minValue = convert(minValue, getCurrentQuantity(), new Q(), getCurrentUnit(), Q.BASE_UNIT);
		maxValue = convert(maxValue, getCurrentQuantity(), new Q(), getCurrentUnit(), Q.BASE_UNIT);
		
		resultsController.updateRequestedQRange(new Q(minValue), new Q(maxValue));
	}
	
	
	@Override
	public void updateVisibleMin(String newMin){
		double minValue;
		try{
			minValue = Double.parseDouble(newMin);
			setModelProperty(ResultsViewModel.VISIBLE_MIN_PROPERTY, minValue, Double.class);
		} catch(NumberFormatException ex){
			setModelProperty(ResultsViewModel.VISIBLE_MIN_PROPERTY, null, Double.class);
			setModelProperty(ResultsModel.VISIBLE_Q_RANGE_PROPERTY, null, NumericRange.class);
		}
	}
	
	
	@Override
	public void updateVisibleMin(Double newMin){
		setModelProperty(ResultsViewModel.VISIBLE_MIN_PROPERTY, newMin, Double.class);
	}
	
	
	@Override
	public void updateVisibleMax(String newMax){
		double maxValue;
		try{
			maxValue = Double.parseDouble(newMax);
			setModelProperty(ResultsViewModel.VISIBLE_MAX_PROPERTY, maxValue, Double.class);
		} catch(NumberFormatException ex){
			setModelProperty(ResultsViewModel.VISIBLE_MAX_PROPERTY, null, Double.class);
			setModelProperty(ResultsModel.VISIBLE_Q_RANGE_PROPERTY, null, NumericRange.class);
		}
	}
	
	
	@Override
	public void updateVisibleMax(Double newMax){
		setModelProperty(ResultsViewModel.VISIBLE_MAX_PROPERTY, newMax, Double.class);
	}
	
	
	private Unit<?> getCurrentUnit(){
		return (Unit<?>) getModelProperty(ResultsViewModel.CURRENT_UNIT_PROPERTY);
	}
	
	
	private ScatteringQuantity getCurrentQuantity(){
		return (ScatteringQuantity) getModelProperty(ResultsViewModel.CURRENT_QUANTITY_PROPERTY);
	}
}
