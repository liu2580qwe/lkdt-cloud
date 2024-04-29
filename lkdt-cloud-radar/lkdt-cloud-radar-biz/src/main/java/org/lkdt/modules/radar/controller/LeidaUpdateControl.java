package org.lkdt.modules.radar.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/radar/public")
public class LeidaUpdateControl {

	Logger logger = LoggerFactory.getLogger(this.getClass());

//	@Value("${radar.updatePath}")
//	private String updatePath = "E:\\updateJar";

	@Value("${radar.jarPath}")
	private String updatePath;

	@Autowired
	private ServletContext servletContext;

	@RequestMapping(value = "/queryVersion", method = RequestMethod.GET)
	public List<String> queryVersion() {
		File file = new File(updatePath);
		File[] files = file.listFiles(pathname -> pathname.getName().endsWith(".jar"));
		List<String> jars = new ArrayList<>();
		if(files != null){
			for (File f : files) {
				jars.add(f.getName());
			}
		}
		return jars;
	}

	@ResponseBody
	@RequestMapping(value = "/downloadJar", method = RequestMethod.GET)
	public void downloadJar(String fileName, HttpServletResponse response) throws IOException {
		MediaType mediaType = getMediaTypeForFileName(fileName);
		File file = new File(updatePath + File.separator + fileName);

		response.setContentType(mediaType.getType());
		response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName());
		response.setContentLength((int) file.length());
		BufferedInputStream inStream = null;
		try {
			inStream = new BufferedInputStream(new FileInputStream(file));
			BufferedOutputStream outStream = new BufferedOutputStream(response.getOutputStream());

			byte[] buffer = new byte[1024];
			int bytesRead = 0;
			while ((bytesRead = inStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, bytesRead);
			}
			outStream.flush();

		} catch (IOException e) {
			logger.error("下载" + fileName + "错误", e);
		} finally {
			if (inStream != null) {
				inStream.close();
			}
		}
	}

	public MediaType getMediaTypeForFileName(String fileName) {
		String mineType = this.servletContext.getMimeType(fileName);
		try {
			MediaType mediaType = MediaType.parseMediaType(mineType);
			return mediaType;
		} catch (Exception e) {
			return MediaType.APPLICATION_OCTET_STREAM;
		}
	}
}
