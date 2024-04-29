package org.lkdt.modules.fog.calculator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class FogListenerMap {

	@Autowired
	FcFactory fcFactory;

	public static Map<String, String> LISTENER_MAP = new HashMap<String, String>();
	
	/**感动科技控股服务器SYSNAME**/
	public static String SYSNAME_GANDONGKEJI = "GANDONGKEJI";
	
	/**沙盘推演SYSNAME**/
	public static String SYSNAME_SANDTABLE = "沙盘";
	
	public String getAiListenerIP(String eqId) {
		String sysName = fcFactory.getCalculator(eqId).getSysName();
		return LISTENER_MAP.get(sysName);
	}

}
