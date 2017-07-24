package dedi.configuration.preferences;


import java.beans.XMLDecoder;
import java.io.ByteArrayInputStream;
import java.util.List;

import org.dawnsci.plotting.tools.preference.detector.DiffractionDetector;
import org.dawnsci.plotting.tools.preference.detector.DiffractionDetectorConstants;
import org.dawnsci.plotting.tools.preference.detector.DiffractionDetectors;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import dedi.Activator;

public class BeamlineConfigurationPreferenceHelper {
	private static IPreferenceStore detectorStore = new ScopedPreferenceStore(InstanceScope.INSTANCE, "org.dawnsci.plotting");
	private static IPreferenceStore beamlineConfigurationsPreferenceStore = Activator.getDefault().getPreferenceStore();
	
	public static List<DiffractionDetector> getDetectorsListFromPreference(){
		List<DiffractionDetector> detectors = null;
		String xml = detectorStore.getString(DiffractionDetectorConstants.DETECTOR);
		 try{
			 XMLDecoder xmlDecoder = new XMLDecoder(new ByteArrayInputStream(xml.getBytes()));
			 DiffractionDetectors diffDetectors = (DiffractionDetectors) xmlDecoder.readObject();
			 detectors = diffDetectors.getDiffractionDetectors();
			 xmlDecoder.close();
		 }catch(Exception e){
		 }
		 return detectors;
	}
	
	
	public static void addDetectorPropertyChangeListener(IPropertyChangeListener listener){
		detectorStore.addPropertyChangeListener(listener);
	}
	
	
	public static List<BeamlineConfigurationBean> getBeamlineConfigurationsListFromPreferences() {
		List<BeamlineConfigurationBean> beamlineConfigurations = null;
		String xml = beamlineConfigurationsPreferenceStore.getString(PreferenceConstants.BEAMLINE_CONFIGURATION);
		try{
			XMLDecoder xmlDecoder = new XMLDecoder(new ByteArrayInputStream(xml.getBytes()));
			BeamlineConfigurations configs = (BeamlineConfigurations) xmlDecoder.readObject();
			beamlineConfigurations = configs.getBeamlineConfigurations();
			xmlDecoder.close();
		}catch(Exception e){
		}
		return beamlineConfigurations;
	}
	
	
	public static BeamlineConfigurations getBeamlineConfigurationsFromPreferences(){
		BeamlineConfigurations beamlineConfigurations = null;
		String xml = beamlineConfigurationsPreferenceStore.getString(PreferenceConstants.BEAMLINE_CONFIGURATION);
		try{
			XMLDecoder xmlDecoder = new XMLDecoder(new ByteArrayInputStream(xml.getBytes()));
			beamlineConfigurations = (BeamlineConfigurations) xmlDecoder.readObject();
			xmlDecoder.close();
		}catch(Exception e){
		}
		return beamlineConfigurations;
	}
	
	
	public static void addBeamlineConfigurationPropertyChangeListener(IPropertyChangeListener listener){
		beamlineConfigurationsPreferenceStore.addPropertyChangeListener(listener);
	}
}
