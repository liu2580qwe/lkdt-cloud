package org.lkdt.modules.fog.vo;

import org.lkdt.common.util.DateUtils;

import java.io.Serializable;
import java.util.Date;


/**
 * 
 * 
 * @author chglee
 * @email 1992lcg@163.com
 * @date 2019-04-03 15:07:30
 */
public class FogHistory implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer histId;
	//
	private String epId;
	//
	private Integer fValue;
	private Integer fMeter;
	//
	private Date fSampleTime;
	//
	
	private String fmodel;
	
	private String imgfn;
	
	/**
	 * 图片请求url
	 */
	private String imgurl;
	
	public String getFmodel() {
		return fmodel;
	}
	public void setFmodel(String fmodel) {
		this.fmodel = fmodel;
	}
	public String getEpId() {
		return epId;
	}
	public void setEpId(String epId) {
		this.epId = epId;
	}
	public String getImgfn() {
		return imgfn;
	}
	public void setImgfn(String imgfn) {
		this.imgfn = imgfn;
	}
	public Integer getHistId() {
		return histId;
	}
	public void setHistId(Integer histId) {
		this.histId = histId;
	}
	public Integer getfValue() {
		return fValue;
	}
	public void setfValue(Integer fValue) {
		this.fValue = fValue;
	}
	public Integer getfMeter() {
		return fMeter;
	}
	public void setfMeter(Integer fMeter) {
		this.fMeter = fMeter;
	}
	public Date getfSampleTime() {
		return fSampleTime;
	}
	public void setfSampleTime(Date fSampleTime) {
		this.fSampleTime = fSampleTime;
	}
	
	
	
	public String getImgurl() {
		return imgurl;
	}
	public void setImgurl(String imgurl) {
		this.imgurl = imgurl;
	}
	@Override
	public String toString() {
		return "[可见度: " + fValue + " ,何博士结果 :"+ fMeter + ", 时间: " + DateUtils.format(fSampleTime, "yyyy-MM-dd HH:mm:ss")  + "]";
	}
}
