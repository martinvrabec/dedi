package dedi.configuration.calculations.results.models;

import dedi.configuration.BeamlineConfiguration;
import dedi.configuration.calculations.results.controllers.AbstractResultsController;
import dedi.configuration.calculations.results.controllers.DefaultResultsController;

public final class ResultsService {
	private static final ResultsService INSTANCE = new ResultsService();
	
	private AbstractResultsController controller;
	private IResultsModel results;
	private BeamlineConfiguration configuration;
	
	private ResultsService(){
		results = new Results();
		configuration = new BeamlineConfiguration();
		controller = new DefaultResultsController(configuration);
		controller.addModel(results);
		controller.update(null, null);
	}
	
	
	public static ResultsService getInstance(){
		return INSTANCE;
	}


	public AbstractResultsController getController() {
		return controller;
	}


	public IResultsModel getResults() {
		return results;
	}
	
	
	/**
	 * @return The {@link BeamlineConfiguration} instance that this service's {@link Results} are based on.
	 */
	public BeamlineConfiguration getBeamlineConfiguration(){
		return controller.getBeamlineConfiguration();
	}
}
