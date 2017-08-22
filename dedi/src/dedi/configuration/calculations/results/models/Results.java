package dedi.configuration.calculations.results.models;

import javax.vecmath.Vector2d;

import dedi.configuration.calculations.NumericRange;

/**
 * The default implementation of {@link IResultsModel}.
 *
 */
public class Results extends AbstractModel implements IResultsModel {
	private NumericRange visibleQRange;
	private NumericRange fullQRange;
	private Double requestedQRangeMin;
	private Double requestedQRangeMax;
	
	private Vector2d visibleRangeStartPoint;
	private Vector2d visibleRangeEndPoint;
	private Vector2d requestedRangeStartPoint;
	private Vector2d requestedRangeEndPoint;
	
	
	
	// Getters
	public NumericRange getVisibleQRange() {
		return visibleQRange;
	}


	public NumericRange getFullQRange() {
		return fullQRange;
	}


	public Double getRequestedQRangeMin() {
		return requestedQRangeMin;
	}
	
	
	public Double getRequestedQRangeMax() {
		return requestedQRangeMax;
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
		return  visibleQRange != null && requestedQRangeMin != null && requestedQRangeMax != null &&
				visibleQRange.contains(new NumericRange(requestedQRangeMin, requestedQRangeMax));
	}

	
	public boolean getHasSolution(){
		return (visibleQRange != null);
	}


	// Setters
	@Override
	public void setVisibleQRange(NumericRange range, Vector2d startPt, Vector2d endPt){
		NumericRange oldRange = visibleQRange;
		visibleQRange = range;
		visibleRangeStartPoint = startPt;
		visibleRangeEndPoint = endPt;
		firePropertyChange(ResultConstants.VISIBLE_Q_RANGE_PROPERTY, oldRange, visibleQRange);
	}
	
	
	@Override
	public void setFullQRange(NumericRange range){
		firePropertyChange(ResultConstants.FULL_Q_RANGE_PROPERTY, fullQRange, fullQRange = range);
	}
	
	
	@Override
	public void setRequestedQRangeMin(Double min, Vector2d startPt) {
		Double oldMin = requestedQRangeMin;
		requestedQRangeMin = min;
		requestedRangeStartPoint = startPt;
		firePropertyChange(ResultConstants.REQUESTED_Q_RANGE_MIN_PROPERTY, oldMin, requestedQRangeMin);
	}
	
	@Override
	public void setRequestedQRangeMax(Double max, Vector2d endPt) {
		Double oldMax = requestedQRangeMax;
		requestedQRangeMax = max;
		requestedRangeEndPoint = endPt;
		firePropertyChange(ResultConstants.REQUESTED_Q_RANGE_MAX_PROPERTY, oldMax, requestedQRangeMax);
	}
}
