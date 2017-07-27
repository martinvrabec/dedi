package dedi.ui.views.results;

import java.util.List;

import javax.measure.unit.Unit;

import dedi.configuration.BeamlineConfiguration;
import dedi.configuration.calculations.scattering.ScatteringQuantity;
import dedi.ui.controllers.AbstractController;
import dedi.ui.controllers.AbstractResultsController;
import dedi.ui.controllers.DefaultResultsController;
import dedi.ui.models.ResultsModel;
import dedi.ui.models.ResultsService;

public abstract class AbstractResultsViewController extends AbstractController {
	protected ResultsModel resultsModel;
	protected AbstractResultsController resultsController;
	protected ResultsViewModel viewModel;
	
	
	public AbstractResultsViewController(ResultsViewModel viewModel) {
		resultsModel = ResultsService.getInstance().getModel();
		resultsController = ResultsService.getInstance().getController();
		this.viewModel = viewModel;
		this.addModel(viewModel);
		this.addModel(resultsModel);
	}
	
	
	
	public abstract void updateQuantities(List<ScatteringQuantity> newQuantities);
	
	public abstract void updateCurrentQuantity(ScatteringQuantity newQuantity);
	
	public abstract void updateCurrentUnits(List<Unit<?>> newUnits);
	
	public abstract void updateCurrentUnit(Unit<?> newUnit);
	
	public abstract void updateRequestedMin(String newMin);
	
	public abstract void updateRequestedMin(Double newMin);
	
	public abstract void updateRequestedMax(String newMax);
	
	public abstract void updateRequestedMax(Double newMax);
	
	public abstract void updateVisibleMin(String newMin);
	
	public abstract void updateVisibleMin(Double newMin);
	
	public abstract void updateVisibleMax(String newMax);
	
	public abstract void updateVisibleMax(Double newMax);
}
