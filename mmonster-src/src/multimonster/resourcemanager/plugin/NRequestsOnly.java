package multimonster.resourcemanager.plugin;

import java.util.HashSet;
import java.util.Iterator;

import multimonster.common.resource.Costs;
import multimonster.common.resource.ResourceRequestIdentifier;
import multimonster.common.setting.Setting;
import multimonster.common.setting.SettingDomain;
import multimonster.common.setting.SettingValue;
import multimonster.exceptions.MultiMonsterException;
import multimonster.resourcemanager.ManagementPlugIn;
import multimonster.resourcemanager.ResourceRequest;
import multimonster.resourcemanager.exceptions.ManagementException;
import multimonster.resourcemanager.exceptions.NoResourcesAvailable;
import multimonster.resourcemanager.exceptions.UnknownRequest;
import multimonster.systemadministration.SettingProxy;

import org.apache.log4j.Logger;

/**
 * @author Holger Velke
 */
public class NRequestsOnly extends ManagementPlugIn {

	private static final String ALLOWED_REQUEST_SETTING = "allowed requests";
	
	private static SettingProxy settingProxy = null;
	private static Logger log = null;
	
	static {
		log  = Logger.getLogger(NRequestsOnly.class);
		
		try {
			settingProxy = SettingProxy.getInstance(NRequestsOnly.class);
			
			Setting allowedRequests = new Setting(
					ALLOWED_REQUEST_SETTING,
					new SettingValue(4),
					new SettingDomain(0, 1000),
					"the maximum number of parallel grantet resource requests");
			
			settingProxy.registerSetting(allowedRequests);
			
		} catch (MultiMonsterException e) {
			log.error("unable to use settings", e);
		}
	}

	// the requests allowed to run
	private RequestVector granted = null;
	private int n_allowed = 40;
	
	public NRequestsOnly () {		
		// initialize form Settings
		if (settingProxy != null) {
			SettingValue allowed = settingProxy.getValue(ALLOWED_REQUEST_SETTING);
			n_allowed = ((Integer)allowed.getValueCont()).intValue();
		} else {
			this.n_allowed = 40;
		}
		
		granted = new RequestVector(n_allowed);
	}
	
	/**
	 * @see multimonster.resourcemanager.ManagementPlugIn#reserve(multimonster.resourcemanager.ResourceRequest)
	 */
	public void reserve(ResourceRequest request) throws ManagementException {
		
		checkSettings();
		
		if (granted.isFree()) {
			granted.add(request);
		} else {			
			throw new NoResourcesAvailable("");
		}
	}

	/**
	 * @see multimonster.resourcemanager.ManagementPlugIn#free(multimonster.common.resource.ResourceRequestIdentifier)
	 */
	public Costs free(ResourceRequestIdentifier rrId)
		throws ManagementException {

		Costs realCosts = null;
		ResourceRequest request = null;
		
		request = granted.remove(rrId);
		if (null != request) {
			realCosts = request.getEstimatedCosts();
		} else {
			// unknown Request
			throw new UnknownRequest(
					"no request for ResourceRequestIdentifier" + rrId);			
		}
		
		checkSettings();

		return realCosts;
	}

	private void checkSettings(){
		int new_n_allowed;
		
		log.debug("Number of parallel requests = "+n_allowed);
		
		if (settingProxy != null) {
			SettingValue allowed = settingProxy.getValue(ALLOWED_REQUEST_SETTING);
			new_n_allowed = ((Integer)allowed.getValueCont()).intValue();
			if (n_allowed != new_n_allowed) {
				n_allowed = new_n_allowed;
				granted.resize(n_allowed);
				log.debug("NEW - Number of parallel requests = "+n_allowed);
			}
		} 
	}
	
	private class RequestVector {

		private HashSet requests;
		private int size;
		
		RequestVector(int size){
			this.requests = new HashSet();
			this.size = size; 
		}
		
		public void add(ResourceRequest request){
			requests.add(request);
		}
		
		public boolean isFree(){
			log.debug("Free in Vector: "+free());
			return 0 < free();
		}
		
		public ResourceRequest remove(ResourceRequestIdentifier rrId){			
			Iterator i = requests.iterator();
			while(i.hasNext()){
				ResourceRequest request = (ResourceRequest) i.next();
				if (rrId.equals(request.getRrId())){
					requests.remove(request);
					return request;
				}
			}
			return null;
		}
		
		public void resize(int new_size) {
			this.size = new_size;
		}
		
		private int free(){
			return (size - requests.size());
		}
	}
}
