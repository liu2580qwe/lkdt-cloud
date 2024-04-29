package org.lkdt.common.util;

import java.util.UUID;

/**
 * @author zcloud
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils{
	public static String getUUID(){
		String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
		return uuid;
	}
}
