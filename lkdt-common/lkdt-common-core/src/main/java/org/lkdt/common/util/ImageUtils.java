package org.lkdt.common.util;

import cn.hutool.core.codec.Base64Encoder;
import com.alibaba.druid.util.Base64;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

@Slf4j
public class ImageUtils {

	/**
	 * 图片转化成base64字符串
	 * @param imgFilePath:文件路径
	 * @return
	 */
    public static String GetImageStr(String imgFilePath){//将图片文件转化为字节数组字符串，并对其进行Base64编码处理  
        InputStream in = null;  
        byte[] data = null;  
        //读取图片字节数组  
        try{  
            in = new FileInputStream(imgFilePath);          
            data = new byte[in.available()];  
            in.read(data);  
            in.close();  
        }   
        catch (IOException e){  
            e.printStackTrace();  
        }  
        //对字节数组Base64编码  
        Base64Encoder.encode(data);
        return org.apache.shiro.codec.Base64.encodeToString(data);
//        return Base64.byteArrayToBase64(data);
    }
    
    /**
     * base64转图片
     * @param base64
     * @param fileName:文件路径
     * @return
     */
    public static File base64ToImage(String base64, String fileName) {
		File f1 = null;
		try {
			byte[] bt  = Base64.base64ToByteArray(base64);
			ByteArrayInputStream bais = new ByteArrayInputStream(bt);
			BufferedImage srcImg = ImageIO.read(bais);
			f1 = new File(fileName);
			if (!f1.getParentFile().exists()) {
				f1.getParentFile().mkdirs();
			}
			ImageIO.write(srcImg, "JPEG", f1);
		} catch (Exception e) {
			log.error(e.toString());
		}
        return f1;
	}

    public static InputStream base64ToInputStream(String base64) {
        ByteArrayInputStream stream = null;
        try {
            byte[] bytes = java.util.Base64.getDecoder().decode(base64);
            stream = new ByteArrayInputStream(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stream;
    }

    /**
     * 根据URL地址获取图片
     * @param imgUrl
     * @return
     */
    public static BufferedImage getBufferedImage(String imgUrl) {
        URL url = null;  
           InputStream is = null;  
           BufferedImage img = null;  
           try {  
               url = new URL(imgUrl);  
               is = url.openStream();  
               img = ImageIO.read(is);  
           } catch (MalformedURLException e) {  
               e.printStackTrace();  
           } catch (IOException e) {  
               e.printStackTrace();  
           } finally {  
                 
               try {  
                   is.close();  
               } catch (IOException e) {  
                   e.printStackTrace();  
               }  
           }  
           return img;  
   }
    
    
    
    public static void main(String[] args) {
    	
//    	String path1 = "C:/Users/asd3404/Desktop/fuwuq/28/货6/12_43_42_vehicleFace.jpg";
//    	
//		String s1 = ImageUtils.GetImageStr(path1);
//		System.out.println(s1);
//		System.out.println(s1.length());
		
//		String outPath1 = "C:\\Users\\asd3404\\Desktop\\2\\vehicleSide.jpg";
//		ImageUtils.base64ToImage("",outPath1);
		
//		String path2 = "C:\\Users\\asd3404\\Desktop\\fuwuq\\28\\货6\\12_43_43_vehicleSide.jpg";
//		String s2 = ImageUtils.GetImageStr(path2);
//		System.out.println(s2);
//		System.out.println(s2.length());
		
//		String outPath2 = "C:\\Users\\asd3404\\Desktop\\2\\Plate.jpg";
//		ImageUtils.base64ToImage(s2,outPath2);
		System.out.println("…………………………………………………………………………………………………………………………");
//		
//		String path3 = "C:/Users/asd3404/Desktop/fuwuq/28/货6/12_43_43_vehiclePlate.jpg";
//    	
//		String s3 = ImageUtils.GetImageStr(path3);
//		System.out.println(s3);
//		System.out.println(s3.length());
		
//		BufferedImage image = ImageUtils.getBufferedImage("https://ainjdjc.jchc.cn/adt//2020-09-07/01_1599446620154_sideimg.jpg");
		BufferedImage image = ImageUtils.getBufferedImage("D:/opt/upFiles/2020-09-07/sxjgl_dongbugs_320600_G15_K1137_609_3_1_101_1599450919708_sideimg.jpg");

		System.out.println("image.getWidth()=============="+image.getWidth());
		//车身图片宽度超过350才传给感动科技
		if (image!=null && image.getWidth() > 350) {
			System.out.println("1111111111111111111");
		}else {
			System.out.println("222222222222222222222222");
		}
		
	}
    
}
