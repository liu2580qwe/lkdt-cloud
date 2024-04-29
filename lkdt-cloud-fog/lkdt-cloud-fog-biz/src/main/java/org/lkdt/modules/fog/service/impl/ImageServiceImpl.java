package org.lkdt.modules.fog.service.impl;

import cn.hutool.core.net.URLDecoder;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.lkdt.common.util.ImageUtils;
import org.lkdt.common.util.MinioUtil;
import org.lkdt.common.util.StringUtils;
import org.lkdt.common.util.oss.OssBootUtil;
import org.lkdt.config.ImgBackUpConfig;
import org.lkdt.modules.fog.calculator.FcFactory;
import org.lkdt.modules.fog.calculator.FogCalculator;
import org.lkdt.modules.fog.service.ImageService;
import org.lkdt.modules.fog.vo.FogHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xmlpull.v1.XmlPullParserException;
import sun.misc.BASE64Decoder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
@Slf4j
@Service
public class ImageServiceImpl implements ImageService {
	@Autowired
	FcFactory fcFactory;
	@Autowired
	private ImgBackUpConfig imgBackUpConfig;

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");

//	public String getImgUrl(String dateStr, String epId, String imgName) {
//		return "/system/alarm/showImg?epId=" + epId + "&dateStr=" + dateStr + "&fname=" + imgName;
//	}

	public String getOssImgName(String dateStr, String epId, String imgName) {
		return dateStr + "/" + epId + "/" + imgName;
	}
	
	/**
	 * 保存文件到OSS
	 * @param row
	 * @return
	 * @throws ParseException
	 */
	@Override
	public FogHistory createOssAiImg(Map<String, Object> row) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		FogHistory fdo = new FogHistory();
		String epId = (String) row.get("epId");		//摄像头id
		fdo.setEpId(epId);
		String fmodel = (String) row.get("fmodel");	// 计算模式 白天/黑夜/ZC/沙盘ST
		fdo.setFmodel(fmodel);
		fdo.setfValue(Integer.parseInt(row.get("fValue").toString()));	//可视距离
		String fogtime = (String) row.get("fogtime"); 	//计算时间  yyyy-MM-dd HH:mm:ss
		if (StringUtils.isNotEmpty(fogtime)){
			fdo.setfSampleTime(format.parse(fogtime));
		}

		//沙盘模式
		if(StringUtils.equalsIgnoreCase(fmodel, "ST")) {
			String imgurl = (String) row.get("imgurl");
			imgurl = URLDecoder.decode(imgurl, Charset.defaultCharset()).replaceAll("\\+","%2B");
			if (StringUtils.isNotEmpty(imgurl)) {
				fdo.setImgfn((String) row.get("fname"));	//图片名称
				fdo.setImgurl(imgurl);
				return fdo;
			}
		}else {	//非沙盘模式
			String img = (String) row.get("fimg"); 		//base64
			if (StringUtils.isNotEmpty(img)) {
				// 保存图片
				FogCalculator fc = fcFactory.getCalculator(epId);
				if(fc==null) {
					log.error("---> 摄像头未初始化:" + epId);
					return null;
				}


				String imgPath = getOssImgName(sdf.format(fdo.getfSampleTime()), fc.getEpId(), (String) row.get("fname"));

				this.upload(img, imgPath);
				String ossUrl = this.getOssObjectURL(imgPath, 60 * 24 * 365);//获取oss地址，有效期一年
				fdo.setImgfn(ossUrl);	//摄像头名称
				return fdo;
			}
		}

		return null;
	}

	/**
	 * #获取备份本地路径# 获取fog服务图片文件->AI监听图片
	 *
	 * @param dateStr 日期格式yyyy-MM-dd(不补零) 例如2019-5-15,2019-10-1
	 * @param camName 摄像头name
	 * @param imgName 图片名(含扩展名)
	 * @return String 文件路径
	 */
	@Override
	public String createLocalAiImg(String dateStr, String camName, String imgName) {
		try {
			return dateStr + "/" + camName + "/" + imgName;
		} catch (Exception e) {
			log.error("文件获取错误，请检查入参格式以及log日志", e);
		}
		return null;
	}


	/**
	 * #获取备份本地路径# 获取fog服务图片文件->AI监听图片
	 *
	 * @return String 文件路径
	 */
	@Override
	public FogHistory createLocalAiImg(Map<String, Object> row) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		FogHistory fdo = new FogHistory();
		String epId = (String) row.get("epId");		//摄像头id
		fdo.setEpId(epId);
		String fmodel = (String) row.get("fmodel");	// 计算模式 白天/黑夜/ZC/沙盘ST
		fdo.setFmodel(fmodel);
		fdo.setfValue(Integer.parseInt(row.get("fValue").toString()));	//可视距离
		String fogtime = (String) row.get("fogtime"); 	//计算时间  yyyy-MM-dd HH:mm:ss
		if (StringUtils.isNotEmpty(fogtime)){
			try {
				fdo.setfSampleTime(format.parse(fogtime));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		String img = (String) row.get("fimg"); 		//base64
		if (StringUtils.isNotEmpty(img)) {
			// 保存图片
			FogCalculator fc = fcFactory.getCalculator(epId);
			if(fc==null) {
				log.error("---> 摄像头未初始化:" + epId);
				return null;
			}


			String imgPath = createLocalAiImg(sdf.format(fdo.getfSampleTime()), fc.getEpId(), (String) row.get("fname"));
			InputStream inputStream = ImageUtils.base64ToInputStream(img);
			String url = "";
			try {
				url = MinioUtil.upload(inputStream,imgPath);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InvalidKeyException e) {
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (InsufficientDataException e) {
				e.printStackTrace();
			} catch (InternalException e) {
				e.printStackTrace();
			} catch (NoResponseException e) {
				e.printStackTrace();
			} catch (InvalidBucketNameException e) {
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			} catch (ErrorResponseException e) {
				e.printStackTrace();
			} catch (RegionConflictException e) {
				e.printStackTrace();
			} catch (InvalidArgumentException e) {
				e.printStackTrace();
			}

			fdo.setImgfn(url);	//摄像头名称
			return fdo;
		}
		return null;
	}

	/**
	 * oss上传图片
	 * @param imageBase64
	 * @param imgpath 图片地址
	 * @return
	 */
	@Override
	public Boolean upload(String imageBase64, String imgpath){
		if(StringUtils.isEmpty(imageBase64) || StringUtils.isEmpty(imgpath)){
			log.error("未获取到正确参数");
			return false;
		}
		ByteArrayInputStream bais = null;
		try {
			String imgHead = imageBase64.substring(0, 22);
			if("data:image/jpg;base64,".equalsIgnoreCase(imgHead)){
				imageBase64 = imageBase64.replace("data:image/jpg;base64,", "");
			}
			byte[] imageBytes = new BASE64Decoder().decodeBuffer(imageBase64);
			bais = new ByteArrayInputStream(imageBytes);
			String upload = OssBootUtil.upload(bais, imgpath);
			if(StringUtils.isEmpty(upload)){
				log.error("上传失败");
				return false;
			}
//			logger.error("上传成功");
			return true;
		} catch (Exception e) {
			log.error("AI上传失败", e);
		} finally {
			if(bais != null){
				try {
					bais.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		log.error("上传失败");
		return false;
	}

	/**
	 * oss获取图片路径
	 * @param imgPath 图片地址
	 * @param expires 过期时间（分钟）
	 * @return
	 */
	@Override
	public String getOssObjectURL(String imgPath, int expires){
		Calendar date = Calendar.getInstance();
		if(expires == 0){
			expires = 10;
		}
		date.add(Calendar.MINUTE, expires);
		String str = OssBootUtil.getObjectURL(imgPath, date.getTime());
		str = str.replaceAll("\\+", "%2B");
		if(StringUtils.isEmpty(str)){
			return "";
		}
		return str;
	}

	/**
	 * 删除图片
	 *
	 * @param fname 图片名(含扩展名)
	 * @return
	 */
	@Override
	public boolean deleteCamImg(String fname) {
		try{
			OssBootUtil.deleteUrl(fname);
			return true;
		} catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}

	public static void main(String[] args) {
		System.out.println("http://fog-images.oss-cn-hangzhou.aliyuncs.com/2021-3-29/K761_680S-2/1617003822500.jpg?Expires=1648540000&OSSAccessKeyId=LTAI4G7Rk6YgVGRU9MxBd2iQ&Signature=gek2ASl+LLMenpsfW2mVrHxPw9Y=".replaceAll("\\+", "%2B"));
	}
}
