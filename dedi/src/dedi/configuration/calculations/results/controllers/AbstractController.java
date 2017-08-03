package dedi.configuration.calculations.results.controllers;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import dedi.configuration.calculations.results.models.AbstractModel;
import dedi.configuration.calculations.results.models.IModel;
import dedi.configuration.calculations.results.models.IResultsModel;


public abstract class AbstractController<T extends IModel> implements PropertyChangeListener {
	    protected List<PropertyChangeListener> registeredViews;
	    protected List<T> registeredModels;

	    public AbstractController() {
	        registeredViews = new ArrayList<PropertyChangeListener>();
	        registeredModels = new ArrayList<T>();
	    }

	    
	    public void addView(PropertyChangeListener view) {
	        registeredViews.add(view);
	    }

	    public void removeView(PropertyChangeListener view) {
	        registeredViews.remove(view);
	    }


	    public void addModel(T model){
			registeredModels.add(model);
			model.addPropertyChangeListener(this);
		}
		
		
		public void removeModel(T model){
			registeredModels.remove(model);
			model.removePropertyChangeListener(this);
		}
		
	    
	    //  Use this to observe property changes from registered models
	    //  and propagate them on to all the views
	    public void propertyChange(PropertyChangeEvent evt) {
	        for (PropertyChangeListener view: registeredViews) {
	            view.propertyChange(evt);
	        }
	    }
	    
        
	    // Convenience methods. Can be used to make controllers as independent of their models as possible.
	    // However, concrete controller classes can define their own ways of manipulating the models as well.
		protected Object getModelProperty(String propertyName){
	   	 	for (T model: registeredModels) {
		            try {
		            	Method method = model.getClass().getDeclaredMethod("get" + propertyName);
		                return method.invoke(model);
		            } catch (Exception ex) {
		                //  Handle exception.
		            }
		     }
	   	 	 return null;
	    }
		

	    protected void setModelProperty(String propertyName, Object newValue, Class clazz) {
	        for (T model: registeredModels) {
	            try {
	            	Method method = model.getClass().
	                    getMethod("set"+propertyName, new Class[] {clazz});
	                method.invoke(model, newValue);
	            } catch (Exception ex) {
	                //  Do nothing.
	            }
	        }
	    }
}

