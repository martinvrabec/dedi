package dedi.configuration.calculations.results.models;

import dedi.configuration.BeamlineConfiguration;
import dedi.configuration.calculations.results.controllers.AbstractResultsController;
import dedi.configuration.calculations.results.controllers.DefaultResultsController;

public final class ResultsService {
	private static final ResultsService INSTANCE = new ResultsService();
	
	private AbstractResultsController controller;
	private Results model;
	
	private ResultsService(){
		model = new Results();
		controller = new DefaultResultsController(BeamlineConfiguration.getInstance());
		controller.addModel(model);
		controller.update(null, null);
	}
	
	
	public static ResultsService getInstance(){
		return INSTANCE;
	}


	public AbstractResultsController getController() {
		return controller;
	}


	public Results getModel() {
		return model;
	}
	
	
}