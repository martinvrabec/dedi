package dedi.ui.controllers;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

import javax.vecmath.Vector2d;

import dedi.configuration.BeamlineConfiguration;
import dedi.configuration.calculations.NumericRange;
import dedi.configuration.calculations.scattering.ScatteringQuantity;
import dedi.ui.models.AbstractModel;
import dedi.ui.models.IResultsModel;
import dedi.ui.models.Results;

public abstract class AbstractResultsController extends AbstractController implements Observer {
	private List<IResultsModel> registeredModels;
	protected BeamlineConfiguration configuration;
	
	
	public AbstractResultsController(BeamlineConfiguration configuration) {
		registeredModels = new ArrayList<>();
		this.configuration = configuration;
		configuration.addObserver(this);
	}
	
	
	public void addModel(IResultsModel model){
		registeredModels.add(model);
		model.addPropertyChangeListener(this);
	}
	
	
	public void removeModel(IResultsModel model){
		registeredModels.remove(model);
		model.removePropertyChangeListener(this);
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
	
	
	protected Object getModelProperty(String propertyName){
   	 	for (IResultsModel model: registeredModels) {
	            try {
	            	Method method = model.getClass().getDeclaredMethod("get" + propertyName);
	                return method.invoke(model);
	            } catch (Exception ex) {
	                //  Handle exception.
	            }
	     }
   	 	 return null;
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

}
