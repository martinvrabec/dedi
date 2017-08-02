package dedi.ui.models;

import javax.vecmath.Vector2d;

import dedi.configuration.calculations.NumericRange;

public class Results extends AbstractModel implements IResultsModel {
	private NumericRange visibleQRange;
	private NumericRange fullQRange;
	private NumericRange requestedQRange;
	
	private Vector2d visibleRangeStartPoint;
	private Vector2d visibleRangeEndPoint;
	private Vector2d requestedRangeStartPoint;
	private Vector2d requestedRangeEndPoint;
	
	private boolean hasSolution;
	private boolean isSatisfied;
	
	
	// Getters
	public NumericRange getVisibleQRange() {
		return visibleQRange;
	}


	public NumericRange getFullQRange() {
		return fullQRange;
	}


	public NumericRange getRequestedQRange() {
		return requestedQRange;
	}


	public Vector2d getVisibleRangeStartPoint() {
		return visibleRangeStartPoint;
	}


	public Vector2d getVisibleRangeEndPoint() {
		return visibleRangeEndPoint;
	}


	public Vector2d getRequestedRangeStartPoint() {
		return requestedRangeStartPoint;
	}


	public Vector2d getRequestedRangeEndPoint() {
		return requestedRangeEndPoint;
	}


	public boolean getIsSatisfied() {
		return isSatisfied;
	}

	
	public boolean getHasSolution(){
		return hasSolution;
	}


	// Setters
	@Override
	public void setVisibleQRange(NumericRange range, Vector2d startPt, Vector2d endPt){
		NumericRange oldRange = visibleQRange;
		visibleQRange = range;
		visibleRangeStartPoint = startPt;
		visibleRangeEndPoint = endPt;
		isSatisfied = isSatisfied();
		hasSolution = (visibleQRange != null);
		if(isStateValid()) firePropertyChange(VISIBLE_Q_RANGE_PROPERTY, oldRange, visibleQRange);
	}
	
	
	@Override
	public void setFullQRange(NumericRange range){
		firePropertyChange(FULL_Q_RANGE_PROPERTY, fullQRange, fullQRange = range);
	}
	
	
	@Override
	public void setRequestedQRange(NumericRange range, Vector2d startPt, Vector2d endPt){
		NumericRange oldRange = requestedQRange;
		requestedQRange = range;
		requestedRangeStartPoint = startPt;
		requestedRangeEndPoint = endPt;
		isSatisfied = isSatisfied();
		if(isStateValid()) firePropertyChange(REQUESTED_Q_RANGE_PROPERTY, oldRange, requestedQRange);
	}
	
	
	private boolean isSatisfied(){
		return  visibleQRange != null && requestedQRange != null &&
					visibleQRange.contains(requestedQRange);
	}
	
	
	private boolean isStateValid(){
		// TODO Implement this method.
		return true;
	}
}
