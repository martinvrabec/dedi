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
		
		BeamlineConfigurationBean beamlineConfiguration1 = new BeamlineConfigurationBean();
		beamlineConfiguration1.setName("I22 SAXS Config 1");
		beamlineConfiguration1.setBeamstopDiameter(3);
		beamlineConfiguration1.setBeamstopXCentre(737.5);
		beamlineConfiguration1.setBeamstopYCentre(0);
		beamlineConfiguration1.setCameraTubeDiameter(350);
		beamlineConfiguration1.setCameraTubeXCentre(737.5);
		beamlineConfiguration1.setCameraTubeYCentre(839.5);
		beamlineConfiguration1.setClearance(10);
		beamlineConfiguration1.setMinWavelength(0.1);
		beamlineConfiguration1.setMaxWavelength(0.5);
		beamlineConfiguration1.setMinCameraLength(1.2);
		beamlineConfiguration1.setMaxCameraLength(9.7);
		beamlineConfiguration1.setCameraLengthStepSize(0.25);
		
		BeamlineConfigurationBean beamlineConfiguration2 = new BeamlineConfigurationBean();
		beamlineConfiguration2.setName("I22 SAXS Config 2");
		beamlineConfiguration2.setBeamstopDiameter(3);
		beamlineConfiguration2.setBeamstopXCentre(737.5);
		beamlineConfiguration2.setBeamstopYCentre(839.5);
		beamlineConfiguration2.setCameraTubeDiameter(350);
		beamlineConfiguration2.setCameraTubeXCentre(737.5);
		beamlineConfiguration2.setCameraTubeYCentre(839.5);
		beamlineConfiguration2.setClearance(10);
		beamlineConfiguration2.setMinWavelength(0.1);
		beamlineConfiguration2.setMaxWavelength(0.5);
		beamlineConfiguration2.setMinCameraLength(1.2);
		beamlineConfiguration2.setMaxCameraLength(9.7);
		beamlineConfiguration2.setCameraLengthStepSize(0.25);
		
		DiffractionDetector dd = new DiffractionDetector();
		dd.setDetectorName("Pilatus2m");
		dd.setxPixelSize(Amount.valueOf(0.172, SI.MILLIMETRE));
		dd.setyPixelSize(Amount.valueOf(0.172, SI.MILLIMETRE));
		dd.setNumberOfPixelsX(1475);
		dd.setNumberOfPixelsY(1679);
		
		if(detectors != null){
			int index = detectors.indexOf(dd);
			if(index != -1){
				beamlineConfiguration1.setDetector(detectors.get(index));
				beamlineConfiguration2.setDetector(detectors.get(index));
			} else {
				beamlineConfiguration1.setDetector(detectors.get(0));
				beamlineConfiguration2.setDetector(detectors.get(0));
			}
		}
		
		beamlineConfigurations.addBeamlineConfiguration(beamlineConfiguration1);
		beamlineConfigurations.setBeamlineConfiguration(beamlineConfiguration1);
		beamlineConfigurations.addBeamlineConfiguration(beamlineConfiguration2);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		XMLEncoder xmlEncoder = new XMLEncoder(baos);
		xmlEncoder.writeObject(beamlineConfigurations);
		xmlEncoder.close();
		
		store.setDefault(PreferenceConstants.BEAMLINE_CONFIGURATION, baos.toString());
		
		BeamlineConfigurationPreferenceHelper.addDetectorPropertyChangeListener(e -> initializeDefaultPreferences());
	}
}
