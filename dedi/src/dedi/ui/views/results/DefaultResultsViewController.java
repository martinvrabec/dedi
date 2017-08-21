package dedi.ui.views.results;

import java.beans.PropertyChangeEvent;
import java.util.List;
import java.util.Observable;

import javax.measure.unit.Unit;

import dedi.configuration.calculations.scattering.ScatteringQuantity;

public class DefaultResultsViewController extends AbstractResultsViewController {
	private List<ScatteringQuantity> quantities;
	private ScatteringQuantity currentQuantity;
	private List<Unit<?>> currentUnits;
	private Unit<?> currentUnit;
	
	
	public DefaultResultsViewController(ResultsViewModel viewModel) {
		super(viewModel);
	}
	
	
	@Override
	public void update(Observable o, Object arg) {
		propertyChange(new PropertyChangeEvent(beamlineConfiguration, ResultsViewConstants.BEAMLINE_CONFIGURATION_PROPERTY, null, beamlineConfiguration));	
	}
	
	
	@Override
	public void updateQuantities(List<ScatteringQuantity> newQuantities) {
		quantities = newQuantities;
		if(newQuantities != null && !newQuantities.isEmpty()) updateCurrentQuantity(newQuantities.get(0));
	}
	
	@Override
	public void updateCurrentQuantity(ScatteringQuantity newQuantity) {
		currentQuantity = newQuantity;
	}
	
	@Override
	public void updateCurrentUnits(List<Unit<?>> newUnits) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void updateCurrentUnit(Unit<?> newUnit) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void updateRequestedMin(String newMin) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void updateRequestedMax(String newMax) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void updateRequestedMin(Double newMin) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void updateRequestedMax(Double newMax) {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void getVisibleRangeFromResultsController() {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void getRequestedRangeFromResultsController() {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void getFullRangeFromResultsController() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Double getQResolution(Double value) {
		// TODO Auto-generated method stub
		return null;
	}
}
