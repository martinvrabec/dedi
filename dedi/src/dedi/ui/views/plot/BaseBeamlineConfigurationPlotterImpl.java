package dedi.ui.views.plot;

import java.util.Arrays;
import java.util.List;

import javax.measure.unit.SI;
import javax.vecmath.Vector2d;

import org.dawnsci.plotting.tools.preference.detector.DiffractionDetector;
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
import org.eclipse.dawnsci.plotting.api.trace.IImageTrace;
import org.eclipse.january.dataset.Dataset;
import org.eclipse.january.dataset.DatasetFactory;
import org.eclipse.january.dataset.DoubleDataset;
import org.eclipse.january.dataset.IDataset;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import dedi.configuration.calculations.BeamlineConfigurationUtil;
import dedi.configuration.calculations.scattering.D;
import dedi.configuration.calculations.scattering.Q;
import uk.ac.diamond.scisoft.analysis.crystallography.CalibrantSpacing;
import uk.ac.diamond.scisoft.analysis.crystallography.CalibrationFactory;
import uk.ac.diamond.scisoft.analysis.crystallography.CalibrationStandards;
import uk.ac.diamond.scisoft.analysis.crystallography.HKL;

public abstract class BaseBeamlineConfigurationPlotterImpl extends AbstractBeamlineConfigurationPlotter {	
	private Dataset mask;
	private DiffractionDetector previousDetector;
	private IRegion detectorRegion;
	private IRegion cameraTubeRegion;


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
		IROI detectorROI = new RectangularROI(getDetectorTopLeftX(), getDetectorTopLeftY(), getDetectorWidth(), getDetectorHeight(), 0);
		addRegion(detectorRegion, detectorROI, legend.getColour("Detector"));
	};
	
	
	protected void createBeamstopRegion(){
		IRegion beamstopRegion;
		IRegion clearanceRegion;
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
		
		IROI clearanceROI = new EllipticalROI(clearanceMajor + beamstopMajor, clearanceMinor + beamstopMinor, 0,
				                         beamstopCentreX, beamstopCentreY);
		IROI beamstopROI = new EllipticalROI(beamstopMajor, beamstopMinor, 0, beamstopCentreX, beamstopCentreY);
		
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
		
		IROI cameraTubeROI = new EllipticalROI(getCameraTubeMajor(),getCameraTubeMinor(), 0, 
										  getCameraTubeCentreX(), getCameraTubeCentreY());
		
		cameraTubeRegion.setAlpha(50);
		addRegion(cameraTubeRegion, cameraTubeROI, legend.getColour("Camera tube"));
	}
	
	
	protected void createRay() {
		IRegion visibleRangeRegion1;
		IRegion visibleRangeRegion2;
		IRegion inaccessibleRangeRegion;
		IRegion requestedRangeRegion;
		
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
		
		
		IROI inaccessibleRangeROI = new LinearROI(new double[] {getBeamstopCentreX(), 
				                                           getBeamstopCentreY()},
											 new double[] {getVisibleRangeStartPointX(), 
											 		       getVisibleRangeStartPointY()});
		
		addRegion(inaccessibleRangeRegion, inaccessibleRangeROI, Display.getDefault().getSystemColor(SWT.COLOR_RED));
		
		
		if(!resultsController.getIsSatisfied() || requestedRangeStartPoint == null || requestedRangeEndPoint == null){	
			IROI visibleRangeROI1 = new LinearROI(new double[] {getVisibleRangeStartPointX(), 
														   getVisibleRangeStartPointY()}, 
					                         new double[] {getVisibleRangeEndPointX(), 
					                        		       getVisibleRangeEndPointY()});
	
			addRegion(visibleRangeRegion1, visibleRangeROI1, new Color(Display.getDefault(), 205, 133, 63));
		} else {
			IROI visibleRangeROI1 = new LinearROI(new double[] {getVisibleRangeStartPointX(), 
										                   getVisibleRangeStartPointY()}, 
										      new double[] {getRequestedRangeStartPointX(), 
										    		        getRequestedRangeStartPointY()});
			
			IROI visibleRangeROI2 = new LinearROI(new double[] {getRequestedRangeEndPointX(), 
														   getRequestedRangeEndPointY()}, 
										      new double[] {getVisibleRangeEndPointX(), 
										    		        getVisibleRangeEndPointY()});
			
			IROI requestedRangeROI = new LinearROI(new double[] {getRequestedRangeStartPointX(),	
															getRequestedRangeStartPointY()}, 
					                          new double[] {getRequestedRangeEndPointX(),
					                        		        getRequestedRangeEndPointY()});
			
			addRegion(visibleRangeRegion1, visibleRangeROI1, new Color(Display.getDefault(), 205, 133, 63));
			addRegion(visibleRangeRegion2, visibleRangeROI2, new Color(Display.getDefault(), 205, 133, 63));
			addRegion(requestedRangeRegion, requestedRangeROI, Display.getDefault().getSystemColor(SWT.COLOR_DARK_GREEN));
		}
	}
	
	
	protected void createCalibrantRings(){
	   if(selectedCalibrant == null) return;
	   List<HKL> hkls = selectedCalibrant.getHKLs();
	   
	   String ringName = "Ring";
	   for(int i = 0; i < hkls.size(); i++){
		   Q q = new D(hkls.get(i).getD()).toQ();
		   
		   if(beamlineConfiguration.getCameraTube() != null &&
				   (getCalibrantRingMajor(q) > getCameraTubeMajor() || getCalibrantRingMinor(q) > getCameraTubeMinor())) 
			   continue;
		   
		   IRegion ringRegion = null;
		   try {
				ringRegion = system.createRegion(ringName + i, IRegion.RegionType.ELLIPSE);
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}

		   IROI ringROI = new EllipticalROI(getCalibrantRingMajor(q), getCalibrantRingMinor(q), 0, getBeamstopCentreX(), getBeamstopCentreY());
				  
		   ringRegion.setFill(false);
		   addRegion(ringRegion, ringROI, Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
	   }
	}
	
	
	/*protected void createMask(){
		if(mask == null) return;
		
		int width = mask.getShape()[1]; // width is the number of columns
		int height = mask.getShape()[0]; // height is the number of rows
		
		Dataset xAxis = DatasetFactory.createFromObject(new double[width]);
		for(int i = 0; i < width; i++) 
			xAxis.set(getDetectorTopLeftX() + getHorizontalLengthFromPixels(i), i);
		
		Dataset yAxis = DatasetFactory.createFromObject(new double[height]);
		for(int i = 0; i < height; i++) 
			yAxis.set(getDetectorTopLeftY() + getVerticalLengthFromPixels(i), i);
		
		
		final IImageTrace image = system.createImageTrace("Mask");
		image.setData(mask, Arrays.asList(xAxis, yAxis), false);
		image.setGlobalRange(new double[]{-800, 450, -400, 450});
		image.setAlpha(255);
		system.addTrace(image);
	}*/
	
	
	protected void createMask(){
		DiffractionDetector detector = beamlineConfiguration.getDetector();
		
		if(detector.getNumberOfHorizontalModules() == 0 || detector.getNumberOfVerticalModules() == 0 
		   || (detector.getXGap() == 0 && detector.getYGap() == 0)) return;
		
		
		int detectorWidth = detector.getNumberOfPixelsX();  // Number of columns
		int detectorHeight = detector.getNumberOfPixelsY(); // Number of rows
		
		int gapWidth = detector.getXGap();
		int gapHeight = detector.getYGap();
		
		int moduleWidth = (detectorWidth - (detector.getNumberOfHorizontalModules()-1)*gapWidth)/
				          detector.getNumberOfHorizontalModules();
		int moduleHeight = (detectorHeight - (detector.getNumberOfVerticalModules()-1)*gapHeight)/
				           detector.getNumberOfVerticalModules();
		
		if(mask == null || (previousDetector != null && !previousDetector.equals(detector))){
			mask = DatasetFactory.ones(new int[]{detectorHeight, detectorWidth}, Dataset.BOOL);
		
			for(int row = 0; row < detectorHeight; row++){
				for(int i = moduleWidth; i < detectorWidth; i += moduleWidth + gapWidth){
					for(int j = 0; j < gapWidth && i+j < detectorWidth; j++){
						mask.set(false, row, i+j);
					}
				}
			}
			for(int col = 0; col < detectorWidth; col++){
				for(int i = moduleHeight; i < detectorHeight; i += moduleHeight + gapHeight){
					for(int j = 0; j < gapHeight && i+j < detectorHeight; j++){
						mask.set(false,  i+j, col);
					}
				}
			}
		}
		
		
		Dataset xAxis = DatasetFactory.createFromObject(new double[detectorWidth]);
		for(int i = 0; i < detectorWidth; i++) 
			xAxis.set(getDetectorTopLeftX() + getHorizontalLengthFromPixels(i), i);
		
		Dataset yAxis = DatasetFactory.createFromObject(new double[detectorHeight]);
		for(int i = 0; i < detectorHeight; i++) 
			yAxis.set(getDetectorTopLeftY() + getVerticalLengthFromPixels(i), i);
		
		
		final IImageTrace image = system.createImageTrace("Mask");
		image.setData(mask, Arrays.asList(xAxis, yAxis), false);
		image.setGlobalRange(getGlobalRange());  
		image.setAlpha(255);
		system.addTrace(image);
		
		previousDetector = detector;
	}
	
	
	protected void createEmptyTrace(){
		final IImageTrace image = system.createImageTrace("Dot");
		image.setData(DatasetFactory.createFromObject(new boolean[1][1]), 
				      Arrays.asList(DatasetFactory.createFromObject(new boolean[1]),
				    		  		DatasetFactory.createFromObject(new boolean[1])), false);
		image.setGlobalRange(getGlobalRange());
		image.setAlpha(0);
		system.addTrace(image);
	    
	}
	
	
	private double[] getGlobalRange(){
		IAxis systemYAxis = system.getAxes().get(1);
		IAxis systemXAxis = system.getAxes().get(0);
		
		return new double[]{systemXAxis.getLower(), systemXAxis.getUpper(), 
                systemYAxis.getUpper(), systemYAxis.getLower()}; // Assuming y axis is inverted.
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
			IAxis yAxis = system.getAxes().get(1);
			yAxis.setInverted(true);
			IAxis xAxis = system.getAxes().get(0);
			
			double maxX = Double.MIN_VALUE;
			double minX = Double.MAX_VALUE;
			double maxY = Double.MIN_VALUE;
			double minY = Double.MAX_VALUE;
			
			// The regions to take into account when scaling the plot
			List<IRegion> regions = Arrays.asList(detectorRegion, cameraTubeRegion);
			
			for(IRegion region : regions){
				IROI roi = region.getROI();
				if(roi != null){
					IRectangularROI bounds = roi.getBounds();
					maxX = Math.max(maxX, bounds.getPointX() + bounds.getLength(0));
					maxY = Math.max(maxY, bounds.getPointY() + bounds.getLength(1));
					minX = Math.min(minX,  bounds.getPointX());
					minY = Math.min(minY, bounds.getPointY());
				}
			}
			
			if(maxX == Double.MIN_VALUE || minX == Double.MAX_VALUE || maxY == Double.MIN_VALUE || minY == Double.MAX_VALUE){
				maxX = 100; minX = -100; maxY = 100; minY = -100;
			}
				
			
			double length = Math.max(maxX-minX, maxY-minY);
			yAxis.setRange(minY + length, minY);
			xAxis.setRange(minX, minX + length);
		}
	}


	protected abstract double getDetectorTopLeftX();
	
	protected abstract double getDetectorTopLeftY();
	
	protected abstract double getHorizontalLengthFromMM(double lengthMM);
	
	protected abstract double getHorizontalLengthFromPixels(double lengthPixels);

	protected abstract double getVerticalLengthFromMM(double lengthMM);
	
	protected abstract double getVerticalLengthFromPixels(double lengthPixels);

	
	protected double getDetectorWidth(){
		return getHorizontalLengthFromMM(beamlineConfiguration.getDetectorWidthMM());
	}
	
	
	protected double getDetectorHeight(){
		return getVerticalLengthFromMM(beamlineConfiguration.getDetectorHeightMM());
	}
	
	
	protected double getClearanceMajor(){
		return getHorizontalLengthFromPixels(beamlineConfiguration.getClearance());
	}
	
	
	protected double getClearanceMinor(){
		return getVerticalLengthFromPixels(beamlineConfiguration.getClearance());
	}
	
	
	protected double getBeamstopMajor(){
		return getHorizontalLengthFromMM(beamlineConfiguration.getBeamstop().getRadiusMM());
	}
	
	
	protected double getBeamstopMinor(){
		return getVerticalLengthFromMM(beamlineConfiguration.getBeamstop().getRadiusMM());
	}
	
	
	protected double getBeamstopCentreX(){
		return getDetectorTopLeftX() + getHorizontalLengthFromMM(beamlineConfiguration.getBeamstopXCentreMM());
	}
	
	
	protected double getBeamstopCentreY(){
		return getDetectorTopLeftY() + getVerticalLengthFromMM(beamlineConfiguration.getBeamstopYCentreMM());
	}
	
	
	protected double getCameraTubeMajor(){
		return getHorizontalLengthFromMM(beamlineConfiguration.getCameraTube().getRadiusMM());
	}
	
	
	protected double getCameraTubeMinor(){
		return getVerticalLengthFromMM(beamlineConfiguration.getCameraTube().getRadiusMM());
	}
	
	
	protected double getCameraTubeCentreX(){
		return getDetectorTopLeftX() + getHorizontalLengthFromMM(beamlineConfiguration.getCameraTubeXCentreMM());
	}
	
	protected double getCameraTubeCentreY(){
		return getDetectorTopLeftY() + getVerticalLengthFromMM(beamlineConfiguration.getCameraTubeYCentreMM());
	}
	
	
	protected double getVisibleRangeStartPointX(){
		return getDetectorTopLeftX() + getHorizontalLengthFromMM(resultsController.getVisibleRangeStartPoint().x);
	}
	
	
	protected double getVisibleRangeStartPointY(){
		return getDetectorTopLeftY() + getVerticalLengthFromMM(resultsController.getVisibleRangeStartPoint().y);
	}
	
	
	protected double getVisibleRangeEndPointX(){
		return getDetectorTopLeftX() + getHorizontalLengthFromMM(resultsController.getVisibleRangeEndPoint().x);
	}
	
	
	protected double getVisibleRangeEndPointY(){
		return getDetectorTopLeftY() + getVerticalLengthFromMM(resultsController.getVisibleRangeEndPoint().y);
	}
	
	
	protected double getRequestedRangeStartPointX(){
		return getDetectorTopLeftX() + getHorizontalLengthFromMM(resultsController.getRequestedRangeStartPoint().x);
	}
	
	
	protected double getRequestedRangeStartPointY(){
		return getDetectorTopLeftY() + getVerticalLengthFromMM(resultsController.getRequestedRangeStartPoint().y);
	}
	
	
	protected double getRequestedRangeEndPointX(){
		return getDetectorTopLeftX() + getHorizontalLengthFromMM(resultsController.getRequestedRangeEndPoint().x);
	}
	
	
	protected double getRequestedRangeEndPointY(){
		return getDetectorTopLeftY() + getVerticalLengthFromMM(resultsController.getRequestedRangeEndPoint().y);
	}
	
	
	protected double getCalibrantRingMajor(Q q){
		return getHorizontalLengthFromMM(1.0e3*BeamlineConfigurationUtil.calculateDistanceFromQValue(q.getValue().to(Q.BASE_UNIT).getEstimatedValue(), 
                beamlineConfiguration.getCameraLength(), beamlineConfiguration.getWavelength())); 
	}
	
	
	protected double getCalibrantRingMinor(Q q){
		return getVerticalLengthFromMM(1.0e3*BeamlineConfigurationUtil.calculateDistanceFromQValue(q.getValue().to(Q.BASE_UNIT).getEstimatedValue(), 
                beamlineConfiguration.getCameraLength(), beamlineConfiguration.getWavelength()));
	}
}
