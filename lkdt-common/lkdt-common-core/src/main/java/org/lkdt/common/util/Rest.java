package org.lkdt.common.util;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class Rest {

	/**
	 * @param IP
	 * @return
	 */
	public static String Get(String IP) {
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> result = restTemplate.getForEntity(IP, String.class);
		return result.getBody().toString();
	}

	/**
	 * @param string post请求的参数
	 * @param IP
	 * @return
	 */
	public static String POST(String IP,String string) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
		headers.setContentType(type);
		HttpEntity<String> formEntity = new HttpEntity<String>(string, headers);
		ResponseEntity<String> result = restTemplate.postForEntity(IP, formEntity, String.class);
		return result.getBody().toString();
	}
}
