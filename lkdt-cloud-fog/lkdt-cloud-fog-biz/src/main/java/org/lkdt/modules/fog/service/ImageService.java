package org.lkdt.modules.fog.service;

import org.lkdt.modules.fog.vo.FogHistory;

import java.text.ParseException;
import java.util.Map;

public interface ImageService {

	FogHistory createOssAiImg(Map<String, Object> row) throws ParseException;

	Boolean upload(String imageBase64, String imgpath);

	String getOssObjectURL(String imgPath, int expires);

	boolean deleteCamImg(String fname);

	public String createLocalAiImg(String dateStr, String camid, String imgName);

	public FogHistory createLocalAiImg(Map<String, Object> row);
}
