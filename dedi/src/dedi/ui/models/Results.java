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
	
	/*
	public void setFullQRange(NumericRange fullQRange){
		firePropertyChange(FULL_Q_RANGE_PROPERTY, this.fullQRange, this.fullQRange = fullQRange);
	}
	
	
	public void setVisibleQRange(NumericRange visibleQRange){
		firePropertyChange(VISIBLE_Q_RANGE_PROPERTY, this.visibleQRange, this.visibleQRange = visibleQRange);
	}
	
	
	public void setRequestedQRange(NumericRange requestedQRange){
		firePropertyChange(REQUESTED_Q_RANGE_PROPERTY, this.requestedQRange, this.requestedQRange = requestedQRange);
	}
	
	
	public void setVisibleRangeStartPoint(Vector2d startPt){
		firePropertyChange(VISIBLE_RANGE_START_POINT_PROPERTY, this.visibleRangeStartPoint, this.visibleRangeStartPoint = startPt);
	}
	
	
	public void setVisibleRangeEndPoint(Vector2d endPt){
		firePropertyChange(VISIBLE_RANGE_END_POINT_PROPERTY, this.visibleRangeEndPoint, this.visibleRangeEndPoint = endPt);
	}
	
	
	public void setRequestedRangeStartPoint(Vector2d startPt){
		firePropertyChange(REQUESTED_RANGE_START_POINT_PROPERTY, this.requestedRangeStartPoint, this.requestedRangeStartPoint = startPt);
	}
	
	
	public void setRequestedRangeEndPoint(Vector2d endPt){
		firePropertyChange(REQUESTED_RANGE_END_POINT_PROPERTY, this.requestedRangeEndPoint, this.requestedRangeEndPoint = endPt);
	}
	
	
	public void setHasSolution(boolean value){
		firePropertyChange(HAS_SOLUTION_PROPERTY, hasSolution, hasSolution = value);
	}
	
	
	public void setIsSatisfied(boolean value){
		firePropertyChange(IS_SATISFIED_PROPERTY, isSatisfied, isSatisfied = value);
	}
	*/
	
	private boolean isSatisfied(){
		return  visibleQRange != null && requestedQRange != null &&
					visibleQRange.contains(requestedQRange);
	}
	
	
	private boolean isStateValid(){
		// TODO Implement this method.
		return true;
	}
}
