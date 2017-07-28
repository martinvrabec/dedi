package dedi.ui.models;

import java.beans.PropertyChangeListener;

public interface IModel {
	
	public void addPropertyChangeListener(PropertyChangeListener listener);
	
	public void removePropertyChangeListener(PropertyChangeListener listener);
}
