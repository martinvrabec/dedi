package dedi.configuration.preferences;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.measure.unit.SI;

import org.dawnsci.plotting.tools.preference.detector.DiffractionDetector;
import org.dawnsci.plotting.tools.preference.detector.DiffractionDetectorConstants;
import org.dawnsci.plotting.tools.preference.detector.DiffractionDetectors;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.jscience.physics.amount.Amount;

import dedi.Activator;

public class BeamlineConfigurationPreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		
		List<DiffractionDetector> detectors = BeamlineConfigurationPreferenceHelper.getDetectorsListFromPreference();
		
		BeamlineConfigurations beamlineConfigurations = new BeamlineConfigurations(); 
		
		BeamlineConfigurationBean bc1 = new BeamlineConfigurationBean();
		bc1.setName("I22 SAXS Config 1");
		bc1.setBeamstopDiameter(4);
		bc1.setBeamstopXCentre(737.5);
		bc1.setBeamstopYCentre(0);
		bc1.setCameraTubeDiameter(350);
		bc1.setCameraTubeXCentre(737.5);
		bc1.setCameraTubeYCentre(839.5);
		bc1.setClearance(10);
		bc1.setMinWavelength(0.1);
		bc1.setMaxWavelength(0.5);
		bc1.setMinCameraLength(1.2);
		bc1.setMaxCameraLength(9.7);
		bc1.setCameraLengthStepSize(0.25);
		
		BeamlineConfigurationBean bc2 = new BeamlineConfigurationBean();
		bc2.setName("I22 SAXS Config 2");
		bc2.setBeamstopDiameter(4);
		bc2.setBeamstopXCentre(737.5);
		bc2.setBeamstopYCentre(839.5);
		bc2.setCameraTubeDiameter(350);
		bc2.setCameraTubeXCentre(737.5);
		bc2.setCameraTubeYCentre(839.5);
		bc2.setClearance(10);
		bc2.setMinWavelength(0.1);
		bc2.setMaxWavelength(0.5);
		bc2.setMinCameraLength(1.2);
		bc2.setMaxCameraLength(9.7);
		bc2.setCameraLengthStepSize(0.25);
		
		
		BeamlineConfigurationBean bc3 = new BeamlineConfigurationBean();
		bc3.setName("I22 WAXS");
		bc3.setBeamstopDiameter(4);
		bc3.setBeamstopXCentre(737.5);
		bc3.setBeamstopYCentre(839.5);
		bc3.setCameraTubeDiameter(0);
		bc3.setCameraTubeXCentre(0);
		bc3.setCameraTubeYCentre(0);
		bc3.setClearance(10);
		bc3.setMinWavelength(0.1);
		bc3.setMaxWavelength(0.5);
		bc3.setMinCameraLength(0.1);
		bc3.setMaxCameraLength(9.7);
		bc3.setCameraLengthStepSize(0.01);
		
		
		DiffractionDetector dd = new DiffractionDetector();
		dd.setDetectorName("Pilatus2m");
		dd.setxPixelSize(Amount.valueOf(0.172, SI.MILLIMETRE));
		dd.setyPixelSize(Amount.valueOf(0.172, SI.MILLIMETRE));
		dd.setNumberOfPixelsX(1475);
		dd.setNumberOfPixelsY(1679);
		
		if(detectors != null){
			int index = detectors.indexOf(dd);
			if(index != -1){
				bc1.setDetector(detectors.get(index));
				bc2.setDetector(detectors.get(0));
				bc3.setDetector(detectors.get(index));
			} else {
				bc1.setDetector(detectors.get(0));
				bc2.setDetector(detectors.get(0));
				bc3.setDetector(detectors.get(0));
			}
		}
		
		beamlineConfigurations.addBeamlineConfiguration(bc1);
		beamlineConfigurations.setBeamlineConfiguration(bc1);
		beamlineConfigurations.addBeamlineConfiguration(bc2);
		beamlineConfigurations.addBeamlineConfiguration(bc3);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		XMLEncoder xmlEncoder = new XMLEncoder(baos);
		xmlEncoder.writeObject(beamlineConfigurations);
		xmlEncoder.close();
		
		store.setDefault(PreferenceConstants.BEAMLINE_CONFIGURATION, baos.toString());
		
		BeamlineConfigurationPreferenceHelper.addDetectorPropertyChangeListener(e -> initializeDefaultPreferences());
	}
}
