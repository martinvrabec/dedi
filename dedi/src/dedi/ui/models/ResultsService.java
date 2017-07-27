package dedi.ui.models;

import dedi.configuration.BeamlineConfiguration;
import dedi.ui.controllers.AbstractResultsController;
import dedi.ui.controllers.DefaultResultsController;

public final class ResultsService {
	private static final ResultsService INSTANCE = new ResultsService();
	
	private AbstractResultsController controller;
	private ResultsModel model;
	
	private ResultsService(){
		model = new ResultsModel();
		controller = new DefaultResultsController(BeamlineConfiguration.getInstance(), model);
		controller.update(null, null);
	}
	
	
	public static ResultsService getInstance(){
		return INSTANCE;
	}


	public AbstractResultsController getController() {
		return controller;
	}


	public ResultsModel getModel() {
		return model;
	}
	
	
}
