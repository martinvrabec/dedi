package dedi.configuration.preferences;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BeamlineConfigurations implements Serializable {
	private static final long serialVersionUID = 7297838000309018662L;
	
	private List<BeamlineConfigurationBean> beamlineConfigurations;
	private BeamlineConfigurationBean selected;

	public BeamlineConfigurations() {
		beamlineConfigurations = new ArrayList<BeamlineConfigurationBean>();
	}
	
	public List<BeamlineConfigurationBean> getBeamlineConfigurations() {
		return beamlineConfigurations;
	}

	public void setBeamlineConfigurations(List<BeamlineConfigurationBean> beamlineConfigurations) {
		this.beamlineConfigurations = beamlineConfigurations;
	}
	
	public void addBeamlineConfiguration(BeamlineConfigurationBean beamlineConfiguration){
		beamlineConfigurations.add(beamlineConfiguration);
	}
	
	public void removeBeamlineConfiguration(BeamlineConfigurationBean beamlineConfiguration){
		beamlineConfigurations.remove(beamlineConfiguration);
	}
	
	public BeamlineConfigurationBean getSelected(){
		return selected;
	}
	
	public void setBeamlineConfiguration(BeamlineConfigurationBean selected){
		this.selected = selected;
	}
}
