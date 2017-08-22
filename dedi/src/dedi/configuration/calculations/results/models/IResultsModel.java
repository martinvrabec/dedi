package dedi.configuration.calculations.results.models;

import javax.vecmath.Vector2d;

import dedi.configuration.calculations.NumericRange;

/**
 * A model to store information about the visible, full, and user-requested q range for a particular configuration of an X-ray scattering beamline.
 * (The full range minimum and maximum correspond to the q value that can be seen with the maximum allowed camera length and minimum allowed energy, 
 *  and with the minimum camera length and maximum allowed energy, respectively).
 * 
 * As well as the q values themselves, the model also stores the positions of the end points of the visible and requested range on the detector.
 * (The positions of the end points of the full range are irrelevant, therefore not stored in the model).
 * 
 * The visible range, if it exists, must always have both boundary values specified, hence the model 
 * represents it using a {@link NumericRange}. The requested range, being user-defined, can have one or both of the values unspecified,
 * hence it's necessary to store and access the boundary values individually - they are represented not as a {@link NumericRange},
 * but as two Double values (which might be null).
 */
public interface IResultsModel extends IModel {
	
	public void setVisibleQRange(NumericRange range, Vector2d startPt, Vector2d endPt);
	
	public void setFullQRange(NumericRange range);
	
	public void setRequestedQRangeMin(Double min, Vector2d startPt);
	
	public void setRequestedQRangeMax(Double max, Vector2d endPt);
	
	public NumericRange getVisibleQRange();

	public NumericRange getFullQRange();

	public Double getRequestedQRangeMin();
	
	public Double getRequestedQRangeMax();

	public Vector2d getVisibleRangeStartPoint();

	public Vector2d getVisibleRangeEndPoint();

	public Vector2d getRequestedRangeStartPoint();

	public Vector2d getRequestedRangeEndPoint();

	
	/**
	 * @return Whether the requested q range is within the visible q range.
	 */
	public boolean getIsSatisfied();

	
	/**
	 * @return Whether there is a visible q range.
	 */
	public boolean getHasSolution();
}
