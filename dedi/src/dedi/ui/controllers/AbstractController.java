package dedi.ui.controllers;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.ArrayList;

import dedi.ui.models.AbstractModel;
import dedi.ui.views.IView;

public abstract class AbstractController implements PropertyChangeListener {
	    private ArrayList<PropertyChangeListener> registeredViews;
	    private ArrayList<AbstractModel> registeredModels;

	    public AbstractController() {
	        registeredViews = new ArrayList<PropertyChangeListener>();
	        registeredModels = new ArrayList<AbstractModel>();
	    }

	    protected void addModel(AbstractModel model) {
	        registeredModels.add(model);
	        model.addPropertyChangeListener(this);
	    }

	    protected void removeModel(AbstractModel model) {
	        registeredModels.remove(model);
	        model.removePropertyChangeListener(this);
	    }

	    public void addView(PropertyChangeListener view) {
	        registeredViews.add(view);
	    }

	    public void removeView(PropertyChangeListener view) {
	        registeredViews.remove(view);
	    }


	    //  Use this to observe property changes from registered models
	    //  and propagate them on to all the views
	    public void propertyChange(PropertyChangeEvent evt) {
	        for (PropertyChangeListener view: registeredViews) {
	            view.propertyChange(evt);
	        }
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


}

