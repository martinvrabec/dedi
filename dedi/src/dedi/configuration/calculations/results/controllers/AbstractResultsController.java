package dedi.configuration.calculations.results.controllers;

import java.util.Observer;

import javax.measure.unit.Unit;
import javax.vecmath.Vector2d;

import org.jscience.physics.amount.Amount;

import dedi.configuration.BeamlineConfiguration;
import dedi.configuration.calculations.NumericRange;
import dedi.configuration.calculations.results.models.IResultsModel;
import dedi.configuration.calculations.results.models.ResultConstants;
import dedi.configuration.calculations.scattering.Q;
import dedi.configuration.calculations.scattering.ScatteringQuantity;


public abstract class AbstractResultsController extends AbstractController<IResultsModel> implements Observer {
	protected BeamlineConfiguration configuration;
	
	// PROPERTY constant used to notify listeners that the beamline configuration has changed.
	public static final String BEAMLINE_CONFIGURATION_PROPERTY = "BeamlineConfiguration";
	
	
	public AbstractResultsController(BeamlineConfiguration configuration) {
		this.configuration = configuration;
		configuration.addObserver(this);
	}
	
	
	// Methods that allow views to set the requested range entered by the user.

	public abstract void updateRequestedQRangeMin(Double minRequested);
	
	public abstract void updateRequestedQRangeMax(Double maxRequested);
	
	
	public void updateRequestedMin(String newMin, ScatteringQuantity quantity, Unit<?> unit){
		double minValue;
		try{
			minValue = Double.parseDouble(newMin);
			quantity.setValue(Amount.valueOf(minValue, unit));
			updateRequestedQRangeMin(quantity.toQ().getValue().to(Q.BASE_UNIT).getEstimatedValue());
		} catch(NumberFormatException ex){
			updateRequestedQRangeMin(null);
		}
	}
	
	
	public void updateRequestedMax(String newMax, ScatteringQuantity quantity, Unit<?> unit){
		double maxValue;
		try{
			maxValue = Double.parseDouble(newMax);
			quantity.setValue(Amount.valueOf(maxValue, unit));
			updateRequestedQRangeMax(quantity.toQ().getValue().to(Q.BASE_UNIT).getEstimatedValue());
		} catch(NumberFormatException ex){
			updateRequestedQRangeMax(null);
		}
	}
	
	
	
	// Convenience setter methods that can be used by concrete controllers to update the data in the registered models.
		
	protected void setVisibleQRange(NumericRange range, Vector2d startPt, Vector2d endPt){
		for(IResultsModel model : registeredModels) model.setVisibleQRange(range, startPt, endPt);
	}
	
	
	protected void setFullQRange(NumericRange range){
		for(IResultsModel model : registeredModels) model.setFullQRange(range);
	}
	
	
	protected void setRequestedQRangeMin(Double min, Vector2d startPt){
		for(IResultsModel model : registeredModels) model.setRequestedQRangeMin(min, startPt);
	}
	
	
	protected void setRequestedQRangeMax(Double max, Vector2d endPt){
		for(IResultsModel model : registeredModels) model.setRequestedQRangeMax(max, endPt);
	}
	
	
	
	// Getter methods that allow views to access the data stored in the models.
	// They allow views to be entirely independent of the underlying models, and interact only with the controller.
	// The controller also provides convenient methods for converting the model data into different forms that might 
	// be required by the views, such as converting q values to other scattering quantities.
	
	
	public NumericRange getVisibleQRange() {
		return (NumericRange) getModelProperty(ResultConstants.VISIBLE_Q_RANGE_PROPERTY);
	}
	
	
	public NumericRange getFullQRange() {
		return (NumericRange) getModelProperty(ResultConstants.FULL_Q_RANGE_PROPERTY);
	}
	

	public Double getRequestedQRangeMin() {
		return  (Double) getModelProperty(ResultConstants.REQUESTED_Q_RANGE_MIN_PROPERTY);
	}
	
	
	public Double getRequestedQRangeMax() {
		return  (Double) getModelProperty(ResultConstants.REQUESTED_Q_RANGE_MAX_PROPERTY);
	}
	
	
	public Vector2d getVisibleRangeStartPoint() {
		return (Vector2d) getModelProperty(ResultConstants.VISIBLE_RANGE_START_POINT_PROPERTY);
	}
	

	public Vector2d getVisibleRangeEndPoint() {
		return (Vector2d) getModelProperty(ResultConstants.VISIBLE_RANGE_END_POINT_PROPERTY);
	}


	public Vector2d getRequestedRangeStartPoint() {
		return (Vector2d) getModelProperty(ResultConstants.REQUESTED_RANGE_START_POINT_PROPERTY);
	}


	public Vector2d getRequestedRangeEndPoint() {
		return (Vector2d) getModelProperty(ResultConstants.REQUESTED_RANGE_END_POINT_PROPERTY);
	}


	public boolean getIsSatisfied() {
		return (boolean) getModelProperty(ResultConstants.IS_SATISFIED_PROPERTY);
	}


	public boolean getHasSolution() {
		return (boolean) getModelProperty(ResultConstants.HAS_SOLUTION_PROPERTY);
	}
	
	
	/**
	 * @return The {@link BeamlineConfiguration} instance that this controller uses to compute the results.
	 */
	public BeamlineConfiguration getBeamlineConfiguration(){
		return configuration;
	}
	
	
	public NumericRange getVisibleRange(ScatteringQuantity quantity, Unit<?> unit) {
		return convertRange(getVisibleQRange(), new Q(), quantity, Q.BASE_UNIT, unit);
	}
	
	
	public NumericRange getFullRange(ScatteringQuantity quantity, Unit<?> unit) {
		return convertRange(getFullQRange(), new Q(), quantity, Q.BASE_UNIT, unit);
	}
	
	
	public Double getRequestedRangeMin(ScatteringQuantity quantity, Unit<?> unit) {
		return convertValue(getRequestedQRangeMin(), new Q(), quantity, Q.BASE_UNIT, unit);
	}
	
	
	public Double getRequestedRangeMax(ScatteringQuantity quantity, Unit<?> unit) {
		return convertValue(getRequestedQRangeMax(), new Q(), quantity, Q.BASE_UNIT, unit);
	}
	
	
	private NumericRange convertRange(NumericRange range, ScatteringQuantity oldQuantity, ScatteringQuantity newQuantity, 
            Unit<?> oldUnit, Unit<?> newUnit){
		
		if(range == null) return null;
		
		Double min = convertValue(range.getMin(), oldQuantity, newQuantity, oldUnit, newUnit);
		Double max = convertValue(range.getMax(), oldQuantity, newQuantity, oldUnit, newUnit);
		
		return (min == null || max == null) ? null : new NumericRange(min, max);
	}
	
	
	private Double convertValue(Double value, ScatteringQuantity oldQuantity, ScatteringQuantity newQuantity, 
                           Unit<?> oldUnit, Unit<?> newUnit){
		if(value == null) return null;
		oldQuantity.setValue(Amount.valueOf(value, oldUnit));
		ScatteringQuantity newSQ = oldQuantity.to(newQuantity);
		
		return (newSQ == null) ? null : newSQ.getValue().to(newUnit).getEstimatedValue();
	}
	
	
	public abstract Double getQResolution(double qValue);
}
