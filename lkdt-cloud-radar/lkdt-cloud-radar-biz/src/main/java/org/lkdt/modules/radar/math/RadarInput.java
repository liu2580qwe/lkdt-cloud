package org.lkdt.modules.radar.math;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class RadarInput {
	
	public JSONArray readInputs() {
		
		String url = "C:\\Users\\wy\\Desktop\\temp\\雷达数据2.txt";
		BufferedReader read = null;
		JSONArray results = new JSONArray();
		try {
			read = new BufferedReader(new FileReader(url));
			
			String tmp = "";
			while ((tmp = read.readLine()) != null) {
				if (tmp.length() > 28) {
					tmp = tmp.substring(28);
					if (tmp.startsWith("-")) {
						continue;
					}
					JSONObject json = JSON.parseObject(tmp);
					results.add(json);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (read != null) {
				try {
					read.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return results;
	}
	
	
	public static void main(String[] args) throws IOException {
		RadarInput rader = new RadarInput();
		JSONArray results = rader.readInputs();
		for (Object obj : results) {
			JSONObject js = (JSONObject) obj;
			System.out.println(js.getString("sX")+","+js.getString("sY"));
		}
	}

}
