package dedi.ui.views.results;

import java.util.List;

import javax.measure.unit.Unit;

import dedi.configuration.calculations.scattering.ScatteringQuantity;
import dedi.ui.models.AbstractModel;
import dedi.ui.models.ResultsModel;

public class ResultsViewModel extends AbstractModel {
	private List<ScatteringQuantity> quantities;
	private ScatteringQuantity currentQuantity;
	private List<Unit<?>> currentUnits;
	private Unit<?> currentUnit;
	private Double requestedMin;
	private Double requestedMax;
	private Double visibleMin;
	private Double visibleMax;
	
	
	public static String QUANTITIES_PROPERTY = "Quantities";
	public static String CURRENT_QUANTITY_PROPERTY = "CurrentQuantity";
	public static String CURRENT_UNIT_PROPERTY = "CurrentUnit";
	public static String CURRENT_UNITS_PROPERTY = "CurrentUnits";
	public static String REQUESTED_MIN_PROPERTY = "RequestedMin";
	public static String REQUESTED_MAX_PROPERTY = "RequestedMax";
	public static String VISIBLE_MIN_PROPERTY = "VisibleMin";
	public static String VISIBLE_MAX_PROPERTY = "VisibleMax";
	
	
	public void setCurrentQuantity(ScatteringQuantity currentQuantity) {
		firePropertyChange(CURRENT_QUANTITY_PROPERTY, this.currentQuantity, this.currentQuantity = currentQuantity);
	}

	public void setCurrentUnit(Unit<?> currentUnit) {
		firePropertyChange(CURRENT_UNIT_PROPERTY, this.currentUnit, this.currentUnit = currentUnit);
	}
	
	
	public void setQuantities(List<ScatteringQuantity> quantities) {
		firePropertyChange(QUANTITIES_PROPERTY, this.quantities, this.quantities = quantities);
	}
	
	
	public void setCurrentUnits(List<Unit<?>> currentUnits) {
		firePropertyChange(CURRENT_UNITS_PROPERTY, this.currentUnits, this.currentUnits = currentUnits);
	}

	
	public void setRequestedMin(Double requestedMin) {
		firePropertyChange(REQUESTED_MIN_PROPERTY, this.requestedMin, this.requestedMin = requestedMin);
	}

	
	public void setRequestedMax(Double requestedMax) {
		firePropertyChange(REQUESTED_MAX_PROPERTY, this.requestedMax, this.requestedMax = requestedMax);
	}

	
	public void setVisibleMin(Double visibleMin) {
		firePropertyChange(VISIBLE_MIN_PROPERTY, this.visibleMin, this.visibleMin = visibleMin);
	}

	
	public void setVisibleMax(Double visibleMax) {
		firePropertyChange(VISIBLE_MAX_PROPERTY, this.visibleMax, this.visibleMax = visibleMax);
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
	
}
