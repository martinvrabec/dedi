package dedi.ui.views.results;

import java.util.List;

import javax.measure.unit.Unit;

import dedi.configuration.calculations.results.models.AbstractModel;
import dedi.configuration.calculations.scattering.ScatteringQuantity;

public class ResultsViewModel extends AbstractModel {
	private List<ScatteringQuantity> quantities;
	private ScatteringQuantity currentQuantity;
	private List<Unit<?>> currentUnits;
	private Unit<?> currentUnit;
	private Double requestedMin;
	private Double requestedMax;
	private Double visibleMin;
	private Double visibleMax;
	private Double fullRangeMin;
	private Double fullRangeMax;
	
	
	public void setCurrentQuantity(ScatteringQuantity currentQuantity) {
		firePropertyChange(ResultsViewConstants.CURRENT_QUANTITY_PROPERTY, this.currentQuantity, this.currentQuantity = currentQuantity);
	}

	public void setCurrentUnit(Unit<?> currentUnit) {
		firePropertyChange(ResultsViewConstants.CURRENT_UNIT_PROPERTY, this.currentUnit, this.currentUnit = currentUnit);
	}
	
	
	public void setQuantities(List<ScatteringQuantity> quantities) {
		firePropertyChange(ResultsViewConstants.QUANTITIES_PROPERTY, this.quantities, this.quantities = quantities);
	}
	
	
	public void setCurrentUnits(List<Unit<?>> currentUnits) {
		firePropertyChange(ResultsViewConstants.CURRENT_UNITS_PROPERTY, this.currentUnits, this.currentUnits = currentUnits);
	}

	
	public void setRequestedMin(Double requestedMin) {
		firePropertyChange(ResultsViewConstants.REQUESTED_MIN_PROPERTY, this.requestedMin, this.requestedMin = requestedMin);
	}

	
	public void setRequestedMax(Double requestedMax) {
		firePropertyChange(ResultsViewConstants.REQUESTED_MAX_PROPERTY, this.requestedMax, this.requestedMax = requestedMax);
	}

	
	public void setVisibleMin(Double visibleMin) {
		firePropertyChange(ResultsViewConstants.VISIBLE_MIN_PROPERTY, this.visibleMin, this.visibleMin = visibleMin);
	}

	
	public void setVisibleMax(Double visibleMax) {
		firePropertyChange(ResultsViewConstants.VISIBLE_MAX_PROPERTY, this.visibleMax, this.visibleMax = visibleMax);
	}
	
	
	public void setFullRangeMin(Double fullRangeMin) {
		firePropertyChange(ResultsViewConstants.FULL_RANGE_MIN_PROPERTY, this.fullRangeMin, this.fullRangeMin = fullRangeMin);
	}

	
	public void setFullRangeMax(Double fullRangeMax) {
		firePropertyChange(ResultsViewConstants.FULL_RANGE_MAX_PROPERTY, this.fullRangeMax, this.fullRangeMax = fullRangeMax);
	}

	
	public List<ScatteringQuantity> getQuantities() {
		return quantities;
	}

	
	public List<Unit<?>> getCurrentUnits() {
		return currentUnits;
	}

	
	public ScatteringQuantity getCurrentQuantity() {
		return currentQuantity;
	}
	
	
	public Unit<?> getCurrentUnit() {
		return currentUnit;
	}
	
	
	public Double getRequestedMin() {
		return requestedMin;
	}

	
	public Double getRequestedMax() {
		return requestedMax;
	}

	
	public Double getVisibleMin() {
		return visibleMin;
	}

	
	public Double getVisibleMax() {
		return visibleMax;
	}	
	
	
	public Double getFullRangeMin() {
		return fullRangeMin;
	}

	
	public Double getFullRangeMax() {
		return fullRangeMax;
	}	
	
}
