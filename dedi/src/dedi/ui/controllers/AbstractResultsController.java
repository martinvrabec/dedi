package dedi.ui.controllers;

import java.util.Observer;

import javax.vecmath.Vector2d;

import dedi.configuration.BeamlineConfiguration;
import dedi.configuration.calculations.NumericRange;
import dedi.configuration.calculations.scattering.ScatteringQuantity;
import dedi.ui.models.ResultsModel;

public abstract class AbstractResultsController extends AbstractController implements Observer {
	protected BeamlineConfiguration configuration;
	protected ResultsModel resultsModel;
	
	public AbstractResultsController(BeamlineConfiguration configuration, ResultsModel model) {
		this.configuration = configuration;
		configuration.addObserver(this);
		resultsModel = model;
		this.addModel(resultsModel);
	}
	
	
	public abstract void updateRequestedQRange(ScatteringQuantity minRequested, ScatteringQuantity maxRequested);
}
