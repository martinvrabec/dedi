package dedi.ui.views.plot;

import javax.vecmath.Vector2d;

import org.eclipse.dawnsci.analysis.api.roi.IROI;
import org.eclipse.dawnsci.analysis.api.roi.IRectangularROI;
import org.eclipse.dawnsci.analysis.dataset.roi.EllipticalROI;
import org.eclipse.dawnsci.analysis.dataset.roi.LinearROI;
import org.eclipse.dawnsci.analysis.dataset.roi.RectangularROI;
import org.eclipse.dawnsci.plotting.api.annotation.IAnnotation;
import org.eclipse.dawnsci.plotting.api.axis.IAxis;
import org.eclipse.dawnsci.plotting.api.axis.IPositionListener;
import org.eclipse.dawnsci.plotting.api.axis.PositionEvent;
import org.eclipse.dawnsci.plotting.api.region.IRegion;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

public abstract class BaseBeamlineConfigurationPlotterImpl extends AbstractBeamlineConfigurationPlotter {
	protected IRegion detectorRegion;
	protected IROI detectorROI;
	protected IRegion beamstopRegion;
	protected IROI beamstopROI;
	protected IRegion clearanceRegion;
	protected IROI clearanceROI;
	protected IRegion cameraTubeRegion;
	protected IROI cameraTubeROI;
	protected IRegion visibleRangeRegion1;
	protected IROI visibleRangeROI1;
	protected IRegion visibleRangeRegion2;
	protected IROI visibleRangeROI2;
	protected IRegion inaccessibleRangeRegion;
	protected IROI inaccessibleRangeROI;
	protected IRegion requestedRangeRegion;
	protected IROI requestedRangeROI;
	
	
	public BaseBeamlineConfigurationPlotterImpl(IBeamlineConfigurationPlotView view) {
		super(view);
	}
	
	
	protected void createDetectorRegion(){
		try {
			detectorRegion = system.createRegion("Detector", IRegion.RegionType.BOX);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		detectorROI = new RectangularROI(getDetectorTopLeftX(), getDetectorTopLeftY(), getDetectorWidth(), getDetectorHeight(), 0);
		addRegion(detectorRegion, detectorROI, legend.getColour("Detector"));
	};
	
	
	protected void createBeamstopRegion(){
		try {
			beamstopRegion = system.createRegion("Beamstop", IRegion.RegionType.ELLIPSE);
			clearanceRegion = system.createRegion("Clearance", IRegion.RegionType.ELLIPSE);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		double clearanceMajor = getClearanceMajor();
		double clearanceMinor = getClearanceMinor();
		double beamstopMajor = getBeamstopMajor();
		double beamstopMinor = getBeamstopMinor();
		double beamstopCentreX = getBeamstopCentreX();
		double beamstopCentreY = getBeamstopCentreY();
		
		clearanceROI = new EllipticalROI(clearanceMajor + beamstopMajor, clearanceMinor + beamstopMinor, 0,
				                         beamstopCentreX, beamstopCentreY);
		beamstopROI = new EllipticalROI(beamstopMajor, beamstopMinor, 0, beamstopCentreX, beamstopCentreY);
		
		addRegion(clearanceRegion, clearanceROI, legend.getColour("Clearance"));
		addRegion(beamstopRegion, beamstopROI,legend.getColour("Beamstop"));
	}
	
	
	protected void createCameraTubeRegion(){
		try {
			cameraTubeRegion = system.createRegion("Camera Tube", IRegion.RegionType.ELLIPSE);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		cameraTubeROI = new EllipticalROI(getCameraTubeMajor(), getCameraTubeMinor(), 0, getCameraTubeCentreX(), getCameraTubeCentreY());
		
		addRegion(cameraTubeRegion, cameraTubeROI, legend.getColour("Camera tube"));
	}
	
	
	protected void createRay(){
		Vector2d visibleRangeStartPoint = resultsController.getVisibleRangeStartPoint();
		Vector2d visibleRangeEndPoint = resultsController.getVisibleRangeEndPoint();
		Vector2d requestedRangeStartPoint = resultsController.getRequestedRangeStartPoint();
		Vector2d requestedRangeEndPoint = resultsController.getRequestedRangeEndPoint();
		
		if(visibleRangeStartPoint == null || visibleRangeEndPoint == null) return;
		
		try {
			visibleRangeRegion1 = system.createRegion("Ray1", IRegion.RegionType.LINE);
			visibleRangeRegion2 = system.createRegion("Ray2", IRegion.RegionType.LINE);
			inaccessibleRangeRegion = system.createRegion("Ray3", IRegion.RegionType.LINE);
			requestedRangeRegion = system.createRegion("Ray4", IRegion.RegionType.LINE);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		
		inaccessibleRangeROI = new LinearROI(new double[] {getBeamstopCentreX(), 
				                                           getBeamstopCentreY()},
											 new double[] {getVisibleRangeStartPointX(), 
											 		       getVisibleRangeStartPointY()});
		
		addRegion(inaccessibleRangeRegion, inaccessibleRangeROI, Display.getDefault().getSystemColor(SWT.COLOR_RED));
		
		
		if(!resultsController.getIsSatisfied() || requestedRangeStartPoint == null || requestedRangeEndPoint == null){	
			visibleRangeROI1 = new LinearROI(new double[] {getVisibleRangeStartPointX(), 
														   getVisibleRangeStartPointY()}, 
					                         new double[] {getVisibleRangeEndPointX(), 
					                        		       getVisibleRangeEndPointY()});
	
			addRegion(visibleRangeRegion1, visibleRangeROI1, new Color(Display.getDefault(), 205, 133, 63));
		} else {
			visibleRangeROI1 = new LinearROI(new double[] {getVisibleRangeStartPointX(), 
										                   getVisibleRangeStartPointY()}, 
										      new double[] {getRequestedRangeStartPointX(), 
										    		        getRequestedRangeStartPointY()});
			
			visibleRangeROI2 = new LinearROI(new double[] {getRequestedRangeEndPointX(), 
														   getRequestedRangeEndPointY()}, 
										      new double[] {getVisibleRangeEndPointX(), 
										    		        getVisibleRangeEndPointY()});
			
			requestedRangeROI = new LinearROI(new double[] {getRequestedRangeStartPointX(),	
															getRequestedRangeStartPointY()}, 
					                          new double[] {getRequestedRangeEndPointX(),
					                        		        getRequestedRangeEndPointY()});
			
			addRegion(visibleRangeRegion1, visibleRangeROI1, new Color(Display.getDefault(), 205, 133, 63));
			addRegion(visibleRangeRegion2, visibleRangeROI2, new Color(Display.getDefault(), 205, 133, 63));
			addRegion(requestedRangeRegion, requestedRangeROI, Display.getDefault().getSystemColor(SWT.COLOR_DARK_GREEN));
		}
	}
	
	
	protected void addRegion(IRegion region, IROI roi, Color colour){
		region.setROI(roi);
		region.setMobile(false);
		region.setActive(false);
		region.setUserRegion(false);
		region.setRegionColor(colour);
		system.addRegion(region);
	}
	
	public void rescalePlot(){
		if(system.isRescale()){
			IAxis yAxis = system.getAxis("");
			yAxis.setInverted(true);
			IAxis xAxis = system.getAxis("X-Axis");
			
			double maxX = Double.MIN_VALUE;
			double minX = Double.MAX_VALUE;
			double maxY = Double.MIN_VALUE;
			double minY = Double.MAX_VALUE;
			
			IROI rois[] = {detectorROI, cameraTubeROI, beamstopROI};
			for(int i = 0; i < rois.length; i++){
				if(rois[i] != null){
					IRectangularROI bounds = rois[i].getBounds();
					maxX = Math.max(maxX, bounds.getPointX() + bounds.getLength(0));
					maxY = Math.max(maxY, bounds.getPointY() + bounds.getLength(1));
					minX = Math.min(minX,  bounds.getPointX());
					minY = Math.min(minY, bounds.getPointY());
				}
			}
			
			if(detectorROI == null && cameraTubeROI == null && beamstopROI == null){
				maxX = 100; minX = -100; maxY = 100; minY = -100;
			}
				
			
			double length = Math.max(maxX-minX, maxY-minY);
			yAxis.setRange(minY + length, minY);
			xAxis.setRange(minX, minX + length);
		}
	}

	
	protected abstract double getDetectorWidth();
	
	protected abstract double getDetectorHeight();
	
	protected abstract double getDetectorTopLeftX();
	
	protected abstract double getDetectorTopLeftY();
	
	protected abstract double getClearanceMajor();
	
	protected abstract double getClearanceMinor();
	
	protected abstract double getBeamstopMajor();
	
	protected abstract double getBeamstopMinor(); 
	
	protected abstract double getBeamstopCentreX();
	
	protected abstract double getBeamstopCentreY();
	
	protected abstract double getCameraTubeMajor();
	
	protected abstract double getCameraTubeMinor();
	
	protected abstract double getCameraTubeCentreX();
	
	protected abstract double getCameraTubeCentreY();
	
	protected abstract double getVisibleRangeStartPointX();
	
	protected abstract double getVisibleRangeStartPointY();
	
	protected abstract double getVisibleRangeEndPointX();
	
	protected abstract double getVisibleRangeEndPointY();
	
	protected abstract double getRequestedRangeStartPointX();
	
	protected abstract double getRequestedRangeStartPointY();
	
	protected abstract double getRequestedRangeEndPointX();
	
	protected abstract double getRequestedRangeEndPointY();
}
