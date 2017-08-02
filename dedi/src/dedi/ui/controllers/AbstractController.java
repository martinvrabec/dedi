package dedi.ui.controllers;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;


public abstract class AbstractController implements PropertyChangeListener {
	    private List<PropertyChangeListener> registeredViews;

	    public AbstractController() {
	        registeredViews = new ArrayList<PropertyChangeListener>();
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
}

