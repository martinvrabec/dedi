package dedi.configuration;

import java.util.Observable;

import javax.measure.quantity.Angle;
import javax.measure.quantity.Length;
import javax.vecmath.Vector2d;

import org.dawnsci.plotting.tools.Vector3dutil;
import org.dawnsci.plotting.tools.preference.detector.DiffractionDetector;
import org.eclipse.dawnsci.analysis.api.diffraction.DetectorProperties;
import org.jscience.physics.amount.Amount;

import dedi.configuration.calculations.BeamlineConfigurationUtil;
import dedi.configuration.calculations.geometry.Ray;
import dedi.configuration.devices.Beamstop;
import dedi.configuration.devices.CameraTube;

public final class BeamlineConfiguration extends Observable {
	private DiffractionDetector detector;
	private DetectorProperties detectorProperties;  // Currently not used.
	private Beamstop beamstop;
	private CameraTube cameraTube;
	private Double angle;
	private Double cameraLength;
	private Integer clearance; 
	private Double wavelength;
	private Double minWavelength;
	private Double maxWavelength;
	private Double minCameraLength;
	private Double maxCameraLength;
	
	private static final BeamlineConfiguration INSTANCE = new BeamlineConfiguration();
	
	public BeamlineConfiguration() {
		detector = null;
		detectorProperties = null;
		beamstop = null;
		cameraTube = null;
		angle = null;
		cameraLength = null;
		clearance = null;
		wavelength = null;
		minWavelength = null;
		maxWavelength = null;
		minCameraLength = null;
		maxCameraLength = null;
	}
	
	public static BeamlineConfiguration getInstance(){
		return INSTANCE;
	}

	public DiffractionDetector getDetector() {
		return detector;
	}

	
	/**
	 * A convenience method that provides the width of the detector in millimetres,
	 * as the {@link DiffractionDetector} class provides it in pixels only.
	 * 
	 * @return The detector width in millimetres.
	 */
	public double getDetectorWidthMM(){
		return getDetector().getNumberOfPixelsX()*getDetector().getXPixelMM();
	}
	
	
	/**
	 * A convenience method that provides the height of the detector in millimetres,
	 * as the {@link DiffractionDetector} class provides it in pixels only.
	 * 
	 * @return The detector height in millimetres.
	 */
	public double getDetectorHeightMM(){
		return getDetector().getNumberOfPixelsY()*getDetector().getYPixelMM();
	}
	
	
	public void setDetector(DiffractionDetector detector) {
		this.detector = detector;
		setChanged();
		notifyObservers();
	}

	public DetectorProperties getDetectorProperties() {
		return detectorProperties;
	}

	public void setDetectorProperties(DetectorProperties detectorProperties) {
		this.detectorProperties = detectorProperties;
		setChanged();
		notifyObservers();
	}


	public Double getAngle() {
		return angle;
	}

	public void setAngle(Double angle) {
		this.angle = angle;
		setChanged();
		notifyObservers();
	}

	public Double getCameraLength() {
		return cameraLength;
	}

	public void setCameraLength(Double cameraLength) {
		this.cameraLength = cameraLength;
		setChanged();
		notifyObservers();
	}

	public Integer getClearance() {
		return clearance;
	}
	
	
	/**
	 * Within the {@link BeamlineConfiguration}, clearance is specified in pixels, 
	 * so this method converts this to millimetres.
	 * Since the pixels of the detector are allowed to have their height different from their width,
	 * the clearance actually becomes an ellipse. 
	 * This method returns the length of this ellipse's semi-major axis.
	 * 
	 * @return The length of the semi-major axis of the clearance in millimetres.
	 */
	public double getClearanceMajorMM() {
		return getClearance()*getDetector().getXPixelMM();
	}
	
	
	/**
	 * Within the {@link BeamlineConfiguration}, clearance is specified in pixels, 
	 * so this method converts this to millimetres.
	 * Since the pixels of the detector are allowed to have their height different from their width,
	 * the clearance actually becomes an ellipse. 
	 * This method returns the length of this ellipse's semi-minor axis.
	 * 
	 * @return The length of the semi-minor axis of the clearance in millimetres.
	 */
	public double getClearanceMinorMM() {
		return getClearance()*getDetector().getYPixelMM();
	}
	
    
	public double getClearanceAndBeamstopMajorMM(){
		return getClearanceMajorMM() + getBeamstop().getRadiusMM();
	}
	
	
	public double getClearanceAndBeamstopMinorMM(){
		return getClearanceMinorMM() + getBeamstop().getRadiusMM();
	}
	
	
	public double getClearanceAndBeamstopMajorPixels(){
		return getClearance() + getBeamstopMajorPixels();
	}
	
	
	public double getClearanceAndBeamstopMinorPixels(){
		return getClearance() + getBeamstopMinorPixels();
	}
	
	
	public void setClearance(Integer clearance) {
		this.clearance = clearance;
		setChanged();
		notifyObservers();
	}

	public Double getWavelength() {
		return wavelength;
	}

	public void setWavelength(Double wavelength) {
		this.wavelength = wavelength;
		setChanged();
		notifyObservers();
	}

	public Beamstop getBeamstop() {
		return beamstop;
	}

	
	public double getBeamstopMajorPixels(){
		return getBeamstop().getRadiusMM()
                /getDetector().getXPixelMM();
	}
	
	
	public double getBeamstopMinorPixels(){
		return getBeamstop().getRadiusMM()
                /getDetector().getYPixelMM();
	}
	
	
	public double getBeamstopXCentreMM(){
		return getBeamstop().getXCentre()*getDetector().getXPixelMM();
	}
	
	public double getBeamstopYCentreMM(){
		return getBeamstop().getYCentre()*getDetector().getYPixelMM();
	}
	
	public void setBeamstop(Beamstop beamstop) {
		this.beamstop = beamstop;
		setChanged();
		notifyObservers();
	}

	public CameraTube getCameraTube() {
		return cameraTube;
	}
	
	
	public double getCameraTubeMajorPixels(){
		return getCameraTube().getRadiusMM()
                /getDetector().getXPixelMM();
	}
	
	
	public double getCameraTubeMinorPixels(){
		return getCameraTube().getRadiusMM()
                /getDetector().getYPixelMM();
	}
	
	
	public double getCameraTubeXCentreMM(){
		return getCameraTube().getXCentre()*getDetector().getXPixelMM();
	}

	public double getCameraTubeYCentreMM(){
		return getCameraTube().getYCentre()*getDetector().getYPixelMM();
	}
	
	public void setCameraTube(CameraTube cameraTube) {
		this.cameraTube = cameraTube;
		setChanged();
		notifyObservers();
	}
	
	public Double getMaxWavelength() {
		return maxWavelength;
	}

	public void setMaxWavelength(Double wavelength) {
		this.maxWavelength = wavelength;
		setChanged();
		notifyObservers();
	}
	
	public Double getMinWavelength() {
		return minWavelength;
	}

	public void setMinWavelength(Double wavelength) {
		this.minWavelength = wavelength;
		setChanged();
		notifyObservers();
	}
	
	public Double getMinCameraLength() {
		return minCameraLength;
	}

	public void setMinCameraLength(Double cameraLength) {
		this.minCameraLength = cameraLength;
		setChanged();
		notifyObservers();
	}
	
	public Double getMaxCameraLength() {
		return maxCameraLength;
	}

	public void setMaxCameraLength(Double cameraLength) {
		this.maxCameraLength = cameraLength;
		setChanged();
		notifyObservers();
	}
}
