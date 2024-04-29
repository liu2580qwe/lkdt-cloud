package org.lkdt.modules.fog.job;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by lvfang on 2017/3/18.
 *
 * 短信发送工具类  SMS发送
 */
public class SendMessageUtil {
	
	protected static final Logger logger = LoggerFactory.getLogger(SendMessageUtil.class);

	private static final String addr = "http://api.sms.cn/sms/";
	private static final String userId = "njzzy2018";// "用户名";
	
	/*
	 * 如uid是：test，登录密码是：123123 pwd=md5(123123test),即
	 * pwd=b9887c5ebb23ebb294acab183ecf0769
	 * 
	 */
	private static final String template = "520082";// 短信模板
	private static final String pwd = "46dcd42f9961cc3f9f46bd7e397dba91";// "MD5加密";
	private static final String encode = "utf8";
	
	// 产品名称:云通信短信API产品,开发者无需替换
	private static final String product = "Dysmsapi";
    // 产品域名,开发者无需替换
	private static final String domain = "dysmsapi.aliyuncs.com";

    // TODO 此处需要替换成开发者自己的AK(在阿里云访问控制台寻找)
	private static final String accessKeyId = "LTAI4G7Rk6YgVGRU9MxBd2iQ";           
	private static final String accessKeySecret = "FGwoesLyZHl8syyvepxSm1mfuxqukn"; 
	private static final String signName = "中交信科集团海德科技"; 

	public static void send(String msgContent, String mobile,String templateNumber) throws Exception {

		// 组建请求
		mobile=mobile.replaceAll("，", ",");
		String straddr = addr + "?ac=send&uid=" + userId + "&pwd=" + pwd + "&mobile=" + mobile+"&template=" + templateNumber
				+ "&encode=" + encode + "&content=" + msgContent;

		StringBuffer sb = new StringBuffer(straddr);
		System.out.println("URL:" + sb);

		// 发送请求
		URL url = new URL(sb.toString());
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

		// 返回结果
		String inputline = in.readLine();
		System.out.println("Response:" + inputline);

	}
	public static void main(String[] args) {
		String templateNumber = "SMS_188635216";
		String content = "{\"location\":\"" + "G15连云港" + "\",\"level\":\"" + "三"
				+ "\",\"foglevel\":\"" + "大雾" + "\",\"distance\":\"" + 180
				+ "\",\"equcode\":\"" + "K1006" + "\"}";
//		SendMessageUtil.send(content, phones, templateNumber);
		try {
			SendMessageUtil.sendSms("15996496542", templateNumber, content);
		} catch (ClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.error("短信发送成功"+content);
	}
	public static String template520082(String location, String level,String foglever, int distance,String equCode) {
		String temp = "【团雾检测】"+location+"路段"+equCode+"处发生"+foglever+",当前能见度"+distance+"米,建议实施"+level+"级管制。";
		return temp;
	}
	
	public static SendSmsResponse sendSms(String telephone, String templateCode,String templateParam) throws ClientException {
		try {
			telephone=telephone.replaceAll("，", ",");
	        // 可自助调整超时时间
	        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
	        System.setProperty("sun.net.client.defaultReadTimeout", "10000");

	        // 初始化acsClient,暂不支持region化
	        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
	        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
	        IAcsClient acsClient = new DefaultAcsClient(profile);

	        // 组装请求对象-具体描述见控制台-文档部分内容
	        SendSmsRequest request = new SendSmsRequest();
	        // 必填:待发送手机号
	        request.setPhoneNumbers(telephone);
	        // 必填:短信签名-可在短信控制台中找到
	        request.setSignName(signName);
	        // 必填:短信模板-可在短信控制台中找到
	        request.setTemplateCode(templateCode);
	        // 可选:模板中的变量替换JSON串,如模板内容为"亲爱的用户,您的验证码为${code}"时,此处的值为
	        request.setTemplateParam(templateParam);

	        // 选填-上行短信扩展码(无特殊需求用户请忽略此字段)
	        // request.setSmsUpExtendCode("90997");

	        // 可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
//	        request.setOutId("yourOutId");

	        // hint 此处可能会抛出异常，注意catch
	        SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
	        if(sendSmsResponse.getCode()!= null && sendSmsResponse.getCode().equals("OK")){
	            logger.error("短信发送成功！"+JSON.toJSONString(sendSmsResponse));
	        }else {
	        	logger.error("短信发送失败！"+JSON.toJSONString(sendSmsResponse));
	        }
	        return sendSmsResponse;
		} catch (Exception e) {
			logger.error(e.toString());
			e.printStackTrace();
			return null;
		}
		
    }
	
}
