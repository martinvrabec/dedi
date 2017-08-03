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
	private DetectorProperties detectorProperties;
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

	public void setBeamstop(Beamstop beamstop) {
		this.beamstop = beamstop;
		setChanged();
		notifyObservers();
	}

	public CameraTube getCameraTube() {
		return cameraTube;
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
