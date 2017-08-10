package dedi.ui.views.plot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
import org.eclipse.dawnsci.plotting.api.trace.IImageTrace.DownsampleType;
import org.eclipse.dawnsci.plotting.api.trace.ILineTrace;
import org.eclipse.january.dataset.Dataset;
import org.eclipse.january.dataset.DatasetFactory;
import org.eclipse.january.dataset.DoubleDataset;
import org.eclipse.january.dataset.IDataset;
import org.eclipse.january.dataset.Slice;
import org.eclipse.january.dataset.SliceND;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import dedi.configuration.calculations.BeamlineConfigurationUtil;
import dedi.configuration.calculations.scattering.D;
import dedi.configuration.calculations.scattering.Q;
import dedi.configuration.devices.Beamstop;
import dedi.configuration.devices.CameraTube;
import uk.ac.diamond.scisoft.analysis.crystallography.CalibrantSpacing;
import uk.ac.diamond.scisoft.analysis.crystallography.CalibrationFactory;
import uk.ac.diamond.scisoft.analysis.crystallography.CalibrationStandards;
import uk.ac.diamond.scisoft.analysis.crystallography.HKL;

public abstract class BaseBeamlineConfigurationPlotterImpl extends AbstractBeamlineConfigurationPlotter {	
	private final String DETECTOR_REGION = "Detector";
	private final String CAMERA_TUBE_REGION = "Camera Tube";
	
	private List<IRegion> calibrantRingRegions = new ArrayList<>();

	private Map<Integer, Dataset> maskCache;
	private final int MAX_CACHE_SIZE = 10;


	public BaseBeamlineConfigurationPlotterImpl(IBeamlineConfigurationPlotView view) {
		super(view);
		
		maskCache = new LinkedHashMap<Integer, Dataset>(MAX_CACHE_SIZE+1, 0.75F, true){
			@Override
			protected boolean removeEldestEntry(java.util.Map.Entry<Integer, Dataset> eldest) {
				return size() > MAX_CACHE_SIZE;
			}
		};
	}
	
	
	@Override
	public void updatePlot(){
		if(beamlineConfiguration.getDetector() != null) 
			createDetectorRegion();
		else removeRegion("Detector");
		
		if(beamlineConfiguration.getDetector() != null && beamlineConfiguration.getCameraTube() != null) 
			createCameraTubeRegion();
		else removeRegion("Camera Tube");
		
		if(beamlineConfiguration.getBeamstop() != null && beamlineConfiguration.getDetector() != null) 
			createBeamstopRegion();
		else removeRegions(new String[]{"Beamstop", "Clearance"});
		
		if(beamlineConfiguration.getBeamstop() != null && beamlineConfiguration.getDetector() != null && 
		   beamlineConfiguration.getAngle() != null) 
			createRay();
		else removeRegions(new String[] {"Ray1", "Ray2", "Ray3", "Ray4"});
		
		if(beamlineConfiguration.getWavelength() != null && beamlineConfiguration.getCameraLength() != null)
			createCalibrantRings();
		else removeRegions(calibrantRingRegions);
		
		createMask();
		createEmptyTrace();
		rescalePlot();
	}
	
	
	protected void createDetectorRegion(){
		removeRegion(DETECTOR_REGION); 
		if(!detectorIsPlot) return;
		
		IRegion detectorRegion;
		try {
			detectorRegion = system.createRegion(DETECTOR_REGION, IRegion.RegionType.BOX);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		IROI detectorROI = new RectangularROI(getDetectorTopLeftX(), getDetectorTopLeftY(), getDetectorWidth(), getDetectorHeight(), 0);
		addRegion(detectorRegion, detectorROI, legend.getColour("Detector"));
	};
	
	
	protected void createBeamstopRegion(){
		removeRegions(new String[]{"Beamstop", "Clearance"});
		if(!beamstopIsPlot) return;
		
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
		removeRegion(CAMERA_TUBE_REGION);
		if(!cameraTubeIsPlot) return;
		
		IRegion cameraTubeRegion;
		try {
			cameraTubeRegion = system.createRegion(CAMERA_TUBE_REGION, IRegion.RegionType.ELLIPSE);
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
		removeRegions(new String[] {"Ray1", "Ray2", "Ray3", "Ray4"});
		if(!rayIsPlot) return;
		
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
	   removeRegions(calibrantRingRegions);
	   calibrantRingRegions = new ArrayList<>();
	   
	   if(selectedCalibrant == null || !calibrantIsPlot) return;
	   
	   List<HKL> hkls = selectedCalibrant.getHKLs();
	   
	   String ringName = "Ring";
	   for(int i = 0; i < hkls.size(); i++){
		   IRegion ringRegion = null;
		   try {
				ringRegion = system.createRegion(ringName + i, IRegion.RegionType.ELLIPSE);
				calibrantRingRegions.add(ringRegion);
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		   
		   Q q = new D(hkls.get(i).getD()).toQ();
		   IROI ringROI = new EllipticalROI(getCalibrantRingMajor(q), getCalibrantRingMinor(q), 0, getBeamstopCentreX(), getBeamstopCentreY());
				  
		   ringRegion.setFill(false);
		   addRegion(ringRegion, ringROI, Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
	   }
	}
	
	
	/*protected void createMask(){
		removeTrace("Mask");
		if(!maskIsPlot){
			mask = null;
			return;
		}
		
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
		
		if(mask == null || previousMaskDetector == null || (previousMaskDetector != null && !previousMaskDetector.equals(detector))){
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
		
		previousMaskDetector = detector;
	}*/
	
	protected void createMask(){
		removeTrace("Mask");
		if(!maskIsPlot) return;
		
		DiffractionDetector detector = beamlineConfiguration.getDetector();
		
		if(detector.getNumberOfHorizontalModules() == 0 || detector.getNumberOfVerticalModules() == 0 ||
		   (detector.getXGap() == 0 && detector.getYGap() == 0)) return;
		
		int detectorWidth = detector.getNumberOfPixelsX();  // Number of columns
		int detectorHeight = detector.getNumberOfPixelsY(); // Number of rows
		
		int gapWidth = detector.getXGap();
		int gapHeight = detector.getYGap();
		
		int moduleWidth = (detectorWidth - (detector.getNumberOfHorizontalModules()-1)*gapWidth)/
				          detector.getNumberOfHorizontalModules();
		int moduleHeight = (detectorHeight - (detector.getNumberOfVerticalModules()-1)*gapHeight)/
				           detector.getNumberOfVerticalModules();
		
		
		Dataset mask = maskCache.get(Objects.hash(detectorWidth, detectorHeight, gapWidth, gapHeight,
				                                  moduleWidth, moduleHeight));
		
		if(mask == null){
			mask = DatasetFactory.ones(new int[]{detectorHeight, detectorWidth}, Dataset.BOOL);
			
			for(int i = moduleWidth; i < detectorWidth; i += moduleWidth + gapWidth)
				mask.setSlice(false, null, new Slice(i , i+gapWidth));
			
			for(int i = moduleHeight; i < detectorHeight; i += moduleHeight + gapHeight)
				mask.setSlice(false, new Slice(i, i + gapHeight));
			
			maskCache.put(Objects.hash(detectorWidth, detectorHeight, gapWidth, gapHeight,
				                                  moduleWidth, moduleHeight), mask);
		}
		
		Dataset xAxis = DatasetFactory.createRange(getDetectorTopLeftX(), getDetectorTopLeftX() + getHorizontalLengthFromPixels(detectorWidth), getHorizontalLengthFromPixels(1), Dataset.FLOAT64);
		Dataset yAxis = DatasetFactory.createRange(getDetectorTopLeftY(),getDetectorTopLeftY() + getVerticalLengthFromPixels(detectorHeight), getVerticalLengthFromPixels(1), Dataset.FLOAT64);
				
		final IImageTrace image = system.createImageTrace("Mask");
		image.setDownsampleType(DownsampleType.POINT);
		image.setRescaleHistogram(false);
		image.setData(mask, Arrays.asList(xAxis, yAxis), false);
		image.setGlobalRange(getGlobalRange());  
		system.addTrace(image);
	}
	
	
	protected void createEmptyTrace(){
		removeTrace("Dot");
		final IImageTrace image = system.createImageTrace("Dot");
		image.setRescaleHistogram(false);
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
			List<IRegion> regions = Arrays.asList(system.getRegion(DETECTOR_REGION), system.getRegion(CAMERA_TUBE_REGION));
			
			for(IRegion region : regions){
				if(region == null) continue;
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
