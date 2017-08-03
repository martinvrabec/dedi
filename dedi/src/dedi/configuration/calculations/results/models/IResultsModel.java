package dedi.configuration.calculations.results.models;

import javax.vecmath.Vector2d;

import dedi.configuration.calculations.NumericRange;

public interface IResultsModel extends IModel {
	public static String FULL_Q_RANGE_PROPERTY = "FullQRange";
	public static String VISIBLE_Q_RANGE_PROPERTY = "VisibleQRange";
	public static String REQUESTED_Q_RANGE_PROPERTY = "RequestedQRange";
	public static String VISIBLE_RANGE_START_POINT_PROPERTY = "VisibleRangeStartPoint";
	public static String VISIBLE_RANGE_END_POINT_PROPERTY = "VisibleRangeEndPoint";
	public static String REQUESTED_RANGE_START_POINT_PROPERTY = "RequestedRangeStartPoint";
	public static String REQUESTED_RANGE_END_POINT_PROPERTY = "RequestedRangeEndPoint";
	public static String HAS_SOLUTION_PROPERTY = "HasSolution";
	public static String IS_SATISFIED_PROPERTY = "IsSatisfied";
	
	public void setVisibleQRange(NumericRange range, Vector2d startPt, Vector2d endPt);
	
	public void setFullQRange(NumericRange range);
	
	public void setRequestedQRange(NumericRange range, Vector2d startPt, Vector2d endPt);
	
	public NumericRange getVisibleQRange();

	public NumericRange getFullQRange();

	public NumericRange getRequestedQRange();

	public Vector2d getVisibleRangeStartPoint();

	public Vector2d getVisibleRangeEndPoint();

	public Vector2d getRequestedRangeStartPoint();

	public Vector2d getRequestedRangeEndPoint();

	public boolean getIsSatisfied();

	public boolean getHasSolution();

}
