package dedi.ui.views.results;

import java.util.List;
import java.util.Observer;

import javax.measure.unit.Unit;

import dedi.configuration.BeamlineConfiguration;
import dedi.configuration.calculations.results.controllers.AbstractController;
import dedi.configuration.calculations.results.controllers.AbstractResultsController;
import dedi.configuration.calculations.results.models.IModel;
import dedi.configuration.calculations.results.models.IResultsModel;
import dedi.configuration.calculations.results.models.ResultsService;
import dedi.configuration.calculations.scattering.ScatteringQuantity;

public abstract class AbstractResultsViewController extends AbstractController<IModel> implements Observer {
	protected IResultsModel resultsModel; // Note that access to the resultsModel should be done via the resultsController.
	protected AbstractResultsController resultsController;
	protected BeamlineConfiguration beamlineConfiguration;
	protected ResultsViewModel viewModel;
	
	public static String BEAMLINE_CONFIGURATION_PROPERTY = "BeamlineConfiguration";
	
	public AbstractResultsViewController(ResultsViewModel viewModel) {
		resultsModel = ResultsService.getInstance().getModel();
		resultsController = ResultsService.getInstance().getController();
		beamlineConfiguration = resultsController.getBeamlineConfiguration();
		beamlineConfiguration.addObserver(this);
		this.viewModel = viewModel;
		addModel(viewModel);
		addModel(resultsModel);
	}
	
	
	public abstract void updateQuantities(List<ScatteringQuantity> newQuantities);
	
	public abstract void updateCurrentQuantity(ScatteringQuantity newQuantity);
	
	public abstract void updateCurrentUnits(List<Unit<?>> newUnits);
	
	public abstract void updateCurrentUnit(Unit<?> newUnit);
	
	public abstract void updateRequestedMin(String newMin);
	
	public abstract void updateRequestedMin(Double newMin);
	
	public abstract void updateRequestedMax(String newMax);
	
	public abstract void updateRequestedMax(Double newMax);
	
	protected abstract void updateVisibleMin(Double newMin);
	
	protected abstract void updateVisibleMax(Double newMax);
	
	protected abstract void updateFullRangeMin(Double newMin);
	
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
	
	
	protected Double getWavelength(){
		return beamlineConfiguration.getWavelength();
	}
	
	// TODO Maybe add protected setters as well, so that concrete controllers don't need to directly use setModelProperty(),
	// so that they are independent of the PROPERTY strings in the models.
}
