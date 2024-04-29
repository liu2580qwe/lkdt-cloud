package org.lkdt.modules.weixin.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class CheckUtil
{
  public static final String tooken = "NJzzy123";
  protected static final Logger logger = LoggerFactory.getLogger(CheckUtil.class);
  public static boolean checkSignature(String signature, String timestamp, String nonce)
  {
	//排序
      String[] arr = {tooken, timestamp, nonce};
      Arrays.sort(arr);

      StringBuilder content = new StringBuilder();
      for (int i = 0; i < arr.length; i++) {
          content.append(arr[i]);
      }

      //sha1Hex 加密
      MessageDigest md = null;
      String temp = null;
      try {
          md = MessageDigest.getInstance("SHA-1");
          byte[] digest = md.digest(content.toString().getBytes());
          temp = byteToStr(digest);
      } catch (NoSuchAlgorithmException e) {
          e.printStackTrace();
      }
      logger.error("微信"+temp.toLowerCase()+"----------"+signature);
      if ((temp.toLowerCase()).equals(signature)){
          return true;
      }
      return false;
  }
  
  private static String byteToStr(byte[] byteArray){
      String strDigest = "";
      for (int i = 0; i < byteArray.length; i++) {
          strDigest += byteToHexStr(byteArray[i]);
      }
      return strDigest;
  }

  private static String byteToHexStr(byte mByte){
      char[] Digit = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A','B', 'C', 'D', 'E', 'F' };
      char[] tempArr = new char[2];
      tempArr[0] = Digit[(mByte >>> 4)& 0X0F];
      tempArr[1] = Digit[mByte & 0X0F];
      String s = new String(tempArr);
      return s;
  }

  public static String getSha1(String str)
  {
    if ((str == null) || (str.length() == 0))
    {
      return null;
    }

    char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
      'a', 'b', 'c', 'd', 'e', 'f' };
    try
    {
      MessageDigest mdTemp = MessageDigest.getInstance("SHA1");

      mdTemp.update(str.getBytes("UTF-8"));

      byte[] md = mdTemp.digest();

      int j = md.length;

      char[] buf = new char[j * 2];

      int k = 0;

      for (int i = 0; i < j; ++i)
      {
        byte byte0 = md[i];

        buf[(k++)] = hexDigits[(byte0 >>> 4 & 0xF)];

        buf[(k++)] = hexDigits[(byte0 & 0xF)];
      }

      return new String(buf);
    }
    catch (Exception e)
    {
    }

    return null;
  }
}