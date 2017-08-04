package dedi.configuration.calculations.results.controllers;

import java.util.Observer;

import javax.vecmath.Vector2d;

import dedi.configuration.BeamlineConfiguration;
import dedi.configuration.calculations.NumericRange;
import dedi.configuration.calculations.results.models.IResultsModel;
import dedi.configuration.calculations.scattering.ScatteringQuantity;


public abstract class AbstractResultsController extends AbstractController<IResultsModel> implements Observer {
	protected BeamlineConfiguration configuration;
	
	
	public AbstractResultsController(BeamlineConfiguration configuration) {
		this.configuration = configuration;
		configuration.addObserver(this);
	}
	

	public abstract void updateRequestedQRange(ScatteringQuantity minRequested, ScatteringQuantity maxRequested);
	
		
	protected void setVisibleQRange(NumericRange range, Vector2d startPt, Vector2d endPt){
		for(IResultsModel model : registeredModels) model.setVisibleQRange(range, startPt, endPt);
	}
	
	
	protected void setFullQRange(NumericRange range){
		for(IResultsModel model : registeredModels) model.setFullQRange(range);
	}
	
	
	protected void setRequestedQRange(NumericRange range, Vector2d startPt, Vector2d endPt){
		for(IResultsModel model : registeredModels) model.setRequestedQRange(range, startPt, endPt);
	}
	
	
	public NumericRange getVisibleQRange() {
		return (NumericRange) getModelProperty(IResultsModel.VISIBLE_Q_RANGE_PROPERTY);
	}
	
	
	public NumericRange getFullQRange() {
		return (NumericRange) getModelProperty(IResultsModel.FULL_Q_RANGE_PROPERTY);
	}
	

	public NumericRange getRequestedQRange() {
		return  (NumericRange) getModelProperty(IResultsModel.REQUESTED_Q_RANGE_PROPERTY);
	}
	
	
	public Vector2d getVisibleRangeStartPoint() {
		return (Vector2d) getModelProperty(IResultsModel.VISIBLE_RANGE_START_POINT_PROPERTY);
	}
	

	public Vector2d getVisibleRangeEndPoint() {
		return (Vector2d) getModelProperty(IResultsModel.VISIBLE_RANGE_END_POINT_PROPERTY);
	}


	public Vector2d getRequestedRangeStartPoint() {
		return (Vector2d) getModelProperty(IResultsModel.REQUESTED_RANGE_START_POINT_PROPERTY);
	}


	public Vector2d getRequestedRangeEndPoint() {
		return (Vector2d) getModelProperty(IResultsModel.REQUESTED_RANGE_END_POINT_PROPERTY);
	}


	public boolean getIsSatisfied() {
		return (boolean) getModelProperty(IResultsModel.IS_SATISFIED_PROPERTY);
	}


	public boolean getHasSolution() {
		return (boolean) getModelProperty(IResultsModel.HAS_SOLUTION_PROPERTY);
	}
	
	
	/**
	 * @return The {@link BeamlineConfiguration} instance that this controller uses to compute the results.
	 */
	public BeamlineConfiguration getBeamlineConfiguration(){
		return configuration;
	}
}
