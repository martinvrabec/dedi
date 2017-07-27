package dedi.ui.views.plot;

import javax.vecmath.Vector2d;

import org.dawnsci.plotting.tools.preference.detector.DiffractionDetector;
import org.eclipse.dawnsci.analysis.api.roi.IROI;
import org.eclipse.dawnsci.analysis.dataset.roi.CircularROI;
import org.eclipse.dawnsci.analysis.dataset.roi.EllipticalROI;
import org.eclipse.dawnsci.analysis.dataset.roi.LinearROI;
import org.eclipse.dawnsci.analysis.dataset.roi.RectangularROI;
import org.eclipse.dawnsci.plotting.api.IPlottingSystem;
import org.eclipse.dawnsci.plotting.api.axis.IAxis;
import org.eclipse.dawnsci.plotting.api.region.IRegion;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import dedi.configuration.calculations.geometry.Ray;
import dedi.configuration.devices.Beamstop;
import dedi.configuration.devices.CameraTube;

public class PixelSpacePlot extends AbstractBeamlineConfigurationPlotter {
	public PixelSpacePlot(IPlottingSystem<Composite> system, BeamlineConfigurationPlotView view) {
		super(system, view);
	}
	

	@Override
	protected void createDetectorRegion(){
		if(view.getDetector() == null || !view.detectorIsPlot()) return;
		
		try {
			detectorRegion = system.createRegion("Detector", IRegion.RegionType.BOX);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		detectorROI = new RectangularROI(0, 0, 
				view.getDetector().getNumberOfPixelsX(), 
				view.getDetector().getNumberOfPixelsY(), 0);
		
		addRegion(detectorRegion, detectorROI, view.getLegend().getColor("Detector"));
		
		if(system.isRescale()){
			IAxis yAxis = system.getAxis("");
			yAxis.setInverted(true);
			IAxis xAxis = system.getAxis("X-Axis");
			
			double ysize = view.getDetector().getNumberOfPixelsY();
			double xsize = view.getDetector().getNumberOfPixelsX();
			
			yAxis.setRange(Math.max(xsize, ysize), 0);
			xAxis.setRange(0, Math.max(xsize, ysize));
		}
	}
	
	
	@Override
	protected void createBeamstopRegion(){
		if(view.getBeamstop() == null || view.getDetector() == null || !view.beamstopIsPlot()) return;
		
		try {
			beamstopRegion = system.createRegion("Beamstop", IRegion.RegionType.CIRCLE);
			clearanceRegion = system.createRegion("Clearance", IRegion.RegionType.ELLIPSE);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		
		clearanceROI = new EllipticalROI(view.getClearance() + view.getBeamstop().getDiameterMM()/2/view.getDetector().getXPixelMM(),
										 view.getClearance() + view.getBeamstop().getDiameterMM()/2/view.getDetector().getYPixelMM(), 0,
				                         view.getBeamstop().getXCentre(), view.getBeamstop().getYCentre());
		beamstopROI = new EllipticalROI(view.getBeamstop().getDiameterMM()/2/view.getDetector().getXPixelMM(), 
										view.getBeamstop().getDiameterMM()/2/view.getDetector().getYPixelMM(), 0, 
										view.getBeamstop().getXCentre(), view.getBeamstop().getYCentre());
		
		addRegion(clearanceRegion, clearanceROI, view.getLegend().getColor("Clearance"));
		addRegion(beamstopRegion, beamstopROI, view.getLegend().getColor("Beamstop"));
	}
	
	
	@Override
	protected void createRay(){
		if(view.getBeamstop() == null || view.getDetector() == null || view.getAngle() == null) return;
		
		Vector2d startPoint = results.getVisibleRangeStartPoint();
		Vector2d endPoint = results.getVisibleRangeEndPoint();
		
		if(startPoint == null || endPoint == null) return;
		
		
		try {
			visibleRangeRegion1 = system.createRegion("Ray1", IRegion.RegionType.LINE);
			visibleRangeRegion2 = system.createRegion("Ray2", IRegion.RegionType.LINE);
			inaccessibleRangeRegion = system.createRegion("Ray3", IRegion.RegionType.LINE);
			requestedRangeRegion = system.createRegion("Ray4", IRegion.RegionType.LINE);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		
		if(!results.getIsSatisfied()){	
			visibleRangeROI1 = new LinearROI(new double[] {startPoint.x/view.getDetector().getXPixelMM(), 
					                            startPoint.y/view.getDetector().getYPixelMM()}, 
					               new double[] {endPoint.x/view.getDetector().getXPixelMM(), 
				                                 endPoint.y/view.getDetector().getYPixelMM()});
			
			visibleRangeRegion1.setLineWidth(1000);
			inaccessibleRangeROI = new LinearROI(new double[] {view.getBeamstop().getXCentre(), 
															   view.getBeamstop().getYCentre()},
					                             new double[] {startPoint.x/view.getDetector().getXPixelMM(), 
	                								           startPoint.y/view.getDetector().getYPixelMM()});
			inaccessibleRangeRegion.setLineWidth(1000);
			
			addRegion(visibleRangeRegion1, visibleRangeROI1, new Color(Display.getDefault(), 205, 133, 63));
			addRegion(inaccessibleRangeRegion, inaccessibleRangeROI, Display.getDefault().getSystemColor(SWT.COLOR_RED));
		} else{
			Vector2d reqestedRangeStartPoint = results.getRequestedRangeStartPoint();
			Vector2d reqestedRangeEndPoint = results.getRequestedRangeEndPoint();
			
			visibleRangeROI1 = new LinearROI(new double[] {startPoint.x/view.getDetector().getXPixelMM(), 
										                   startPoint.y/view.getDetector().getYPixelMM()}, 
										      new double[] {reqestedRangeStartPoint.x/view.getDetector().getXPixelMM(), 
										    		        reqestedRangeStartPoint.y/view.getDetector().getYPixelMM()});
			
			visibleRangeROI2 = new LinearROI(new double[] {reqestedRangeEndPoint.x/view.getDetector().getXPixelMM(), 
														   reqestedRangeEndPoint.y/view.getDetector().getYPixelMM()}, 
										      new double[] {endPoint.x/view.getDetector().getXPixelMM(), 
										    		        endPoint.y/view.getDetector().getYPixelMM()});
			requestedRangeROI = new LinearROI(new double[] {reqestedRangeStartPoint.x/view.getDetector().getXPixelMM(),	
															reqestedRangeStartPoint.y/view.getDetector().getYPixelMM()}, 
					                          new double[] {reqestedRangeEndPoint.x/view.getDetector().getXPixelMM(),
					                        		        reqestedRangeEndPoint.y/view.getDetector().getYPixelMM()});
			
			inaccessibleRangeROI = new LinearROI(new double[] {view.getBeamstop().getXCentre(), 
															   view.getBeamstop().getYCentre()},
								                 new double[] {startPoint.x/view.getDetector().getXPixelMM(), 
														       startPoint.y/view.getDetector().getYPixelMM()});
			
			addRegion(visibleRangeRegion1, visibleRangeROI1, new Color(Display.getDefault(), 205, 133, 63));
			addRegion(visibleRangeRegion2, visibleRangeROI2, new Color(Display.getDefault(), 205, 133, 63));
			addRegion(inaccessibleRangeRegion, inaccessibleRangeROI, Display.getDefault().getSystemColor(SWT.COLOR_RED));
			addRegion(requestedRangeRegion, requestedRangeROI, Display.getDefault().getSystemColor(SWT.COLOR_DARK_GREEN));
		}
	}
	
	
	@Override
	protected void createCameraTubeRegion(){
		if(view.getDetector() == null || view.getCameraTube() == null || !view.cameraTubeIsPlot()) return;
		
		try {
			cameraTubeRegion = system.createRegion("Camera Tube", IRegion.RegionType.CIRCLE);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		
		cameraTubeROI = new EllipticalROI(view.getCameraTube().getDiameterMM()/2/view.getDetector().getXPixelMM(), 
										  view.getCameraTube().getDiameterMM()/2/view.getDetector().getYPixelMM(), 0, 
										  view.getCameraTube().getXCentre(), view.getCameraTube().getYCentre());
		addRegion(cameraTubeRegion, cameraTubeROI, view.getLegend().getColor("Camera tube"));
		
	}
}
