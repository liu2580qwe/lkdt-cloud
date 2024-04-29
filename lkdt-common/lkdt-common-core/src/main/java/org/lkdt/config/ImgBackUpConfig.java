package org.lkdt.config;

import java.io.File;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * fog服务端 图片文件备份配置
 * &img-back-up-config: #接收AI服务器图片文件配置
 * 		&saveRealPath: #解压后文件保存路径
 */
@Component
@ConfigurationProperties(prefix="img-back-up-config")
public class ImgBackUpConfig {
	/**解压后文件保存路径*/
	private String saveRealPath;
	/**接收文件保存路径*/
	private String savePath;
	/**临时文件名*/
	private String tempName;
	/**临时文件路径+名*/
	private String filePath;
	/**上传文件开始*/
	private String start;
	/**上传中*/
	private String ing;
	/**上传文件结束*/
	private String end;
	/**访问相对路径*/
	private String relativePath;

	/**
	 * 	savePath: imgZIP\ #接收文件保存路径 saveRealPath+savePath
	 *	tempName: $$$temp$.temp #临时文件名
	 *	#filePath: "" #临时文件路径+名 saveRealPath+savePath+tempName
	 *	start: start #上传文件开始
	 *	ing: ing #上传中
	 *	end: end #上传文件结束
	 */
	public ImgBackUpConfig(){
		this.savePath = "imgZIP"+File.separator;
		this.tempName = "$$$temp$.temp";
		this.start = "start";
		this.ing = "ing";
		this.end = "end";
	}

	public String getSaveRealPath() {
		return saveRealPath;
	}

	public void setSaveRealPath(String saveRealPath) {
		this.saveRealPath = saveRealPath;
	}
	public String getRelativePath() {
		return relativePath;
	}
	
	public void setRelativePath(String relativePath) {
		this.relativePath = relativePath;
	}

	public String getSavePath() {
		return saveRealPath+savePath;
	}

	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}

	public String getTempName() {
		return tempName;
	}

	public void setTempName(String tempName) {
		this.tempName = tempName;
	}

	public String getFilePath() {
		return saveRealPath+savePath+tempName;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getIng() {
		return ing;
	}

	public void setIng(String ing) {
		this.ing = ing;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}
}
