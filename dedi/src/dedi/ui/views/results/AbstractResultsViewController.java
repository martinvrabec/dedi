package dedi.ui.views.results;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.measure.unit.Unit;

import dedi.configuration.BeamlineConfiguration;
import dedi.configuration.calculations.scattering.ScatteringQuantity;
import dedi.ui.controllers.AbstractController;
import dedi.ui.controllers.AbstractResultsController;
import dedi.ui.controllers.DefaultResultsController;
import dedi.ui.models.AbstractModel;
import dedi.ui.models.Results;
import dedi.ui.models.ResultsService;

public abstract class AbstractResultsViewController extends AbstractController {
	protected Results resultsModel; // Note that access to the resultsModel should be done via the resultsController.
	protected AbstractResultsController resultsController;
	protected ResultsViewModel viewModel;
	private List<AbstractModel> registeredModels;
	
	
	public AbstractResultsViewController(ResultsViewModel viewModel) {
		registeredModels = new ArrayList<AbstractModel>();
		resultsModel = ResultsService.getInstance().getModel();
		resultsController = ResultsService.getInstance().getController();
		this.viewModel = viewModel;
		this.addModel(viewModel);
		this.addModel(resultsModel);
		
	}
	
	
	protected void addModel(AbstractModel model) {
        registeredModels.add(model);
        model.addPropertyChangeListener(this);
    }

	
    protected void removeModel(AbstractModel model) {
        registeredModels.remove(model);
        model.removePropertyChangeListener(this);
    }
	
	

    protected void setModelProperty(String propertyName, Object newValue, Class clazz) {
        for (AbstractModel model: registeredModels) {
            try {
            	Method method = model.getClass().
                    getMethod("set"+propertyName, new Class[] {clazz});
                method.invoke(model, newValue);
            } catch (Exception ex) {
                //  Do nothing.
            }
        }
    }
    
    
    protected Object getModelProperty(String propertyName){
    	 for (AbstractModel model: registeredModels) {
	            try {
	            	Method method = model.getClass().getDeclaredMethod("get" + propertyName);
	                return method.invoke(model);
	            } catch (Exception ex) {
	                //  Handle exception.
	            }
	     }
    	 return null;
    }
    
    
	public abstract void updateQuantities(List<ScatteringQuantity> newQuantities);
	
	public abstract void updateCurrentQuantity(ScatteringQuantity newQuantity);
	
	public abstract void updateCurrentUnits(List<Unit<?>> newUnits);
	
	public abstract void updateCurrentUnit(Unit<?> newUnit);
	
	public abstract void updateRequestedMin(String newMin);
	
	public abstract void updateRequestedMin(Double newMin);
	
	public abstract void updateRequestedMax(String newMax);
	
	public abstract void updateRequestedMax(Double newMax);
	
	//protected abstract void updateVisibleMin(String newMin);
	
	protected abstract void updateVisibleMin(Double newMin);
	
	//protected abstract void updateVisibleMax(String newMax);
	
	protected abstract void updateVisibleMax(Double newMax);
	
	//protected abstract void updateVisibleMin(String newMin);
	
	protected abstract void updateFullRangeMin(Double newMin);
		
	//protected abstract void updateVisibleMax(String newMax);
		
	protected abstract void updateFullRangeMax(Double newMax);
	
	
	public Double getRequestedMin(){
		return (Double) getModelProperty(ResultsViewModel.REQUESTED_MIN_PROPERTY);
	}
	
	public Double getRequestedMax(){
		return (Double) getModelProperty(ResultsViewModel.REQUESTED_MAX_PROPERTY);
	}
	
	public Double getVisibleMin(){
		return (Double) getModelProperty(ResultsViewModel.VISIBLE_MIN_PROPERTY);
	}
	
	public Double getVisibleMax(){
		return (Double) getModelProperty(ResultsViewModel.VISIBLE_MAX_PROPERTY);
	}
	
	public Double getFullRangeMin(){
		return (Double) getModelProperty(ResultsViewModel.FULL_RANGE_MIN_PROPERTY);
	}
	
	public Double getFullRangeMax(){
		return (Double) getModelProperty(ResultsViewModel.FULL_RANGE_MAX_PROPERTY);
	}
	
	public boolean hasSolution(){
		return getVisibleMin() != null && getVisibleMax() != null; 
	}
	
	public boolean isSatisfied(){
		return resultsController.getIsSatisfied();
	}
	

	protected Unit<?> getCurrentUnit(){
		return (Unit<?>) getModelProperty(ResultsViewModel.CURRENT_UNIT_PROPERTY);
	}
	
	
	protected ScatteringQuantity getCurrentQuantity(){
		return (ScatteringQuantity) getModelProperty(ResultsViewModel.CURRENT_QUANTITY_PROPERTY);
	}
	
}
