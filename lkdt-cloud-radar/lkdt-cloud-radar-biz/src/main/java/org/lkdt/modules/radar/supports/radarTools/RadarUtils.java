package org.lkdt.modules.radar.supports.radarTools;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.lang.management.ManagementFactory;
import java.math.BigInteger;
import java.util.List;

public abstract class RadarUtils {
    public static final int RadarEventSize = 1024;
    public static final int RadarFlowSize = 1024;
    //public static final int RadarFlowSize = 24000;

    public static boolean RUN_IN_DEBUG = false;

    static {
        RUN_IN_DEBUG = RadarUtils.isDebug();
        if(RUN_IN_DEBUG){
            System.out.println("DEBUG模式启动");
        } else {
            System.out.println("RUN模式启动");
        }
    }

    public static int[] hexStr2Int(String src) {
        int l = src.length() / 2;
        int[] ret = new int[l];
        for (int i = 0; i < l; i++) {
            ret[i] = Integer.decode("0x" + src.substring(i * 2, i * 2 + 2));
            // (byte) Integer.valueOf(src.substring(i * 2, i * 2 + 2), 16).byteValue();
        }
        return ret;
    }

    /**
     * 16进制字符串转byte数组
     * @param src
     * @return
     */
    public static byte[] hexStr2Bytes(String src) {
        int l = src.length() / 2;
        byte[] ret = new byte[l];
        for (int i = 0; i < l; i++) {
            short tempS = Short.decode("0x" + src.substring(i * 2, i * 2 + 2));
            ret[i] = (byte)tempS;
        }
        return ret;
    }

    /**
     * bytes to 16进制
     * @param bytes
     * @return
     */
    public static String byteToHexStr(byte[] bytes){
        String returnStr = "";
        for(byte b: bytes){
            String b_ = Integer.toHexString(b & 0xFF);
            if(b_.length() == 1){
                b_ = "0" + b_;
            }
            returnStr += b_;
        }
        //取消结尾多余的0字符
//        int traj_subNum = returnStr.length();
//        while(returnStr.charAt(traj_subNum - 1) == '0'){
//            traj_subNum --;
//        }
//        return returnStr.substring(0, traj_subNum);
        return returnStr;
    }

    /**
     * bytes to 十进制
     * @param bytes
     * @return
     */
    public static String byteTo10Str(byte[] bytes){
        String returnStr = "";
        for(byte b: bytes){
            String b_ = String.valueOf(b);
            if(b_.length() == 1){
                b_ = "0" + b_;
            }
            returnStr += b_;
        }
        //取消结尾多余的0字符
//        int traj_subNum = returnStr.length();
//        while(returnStr.charAt(traj_subNum - 1) == '0'){
//            traj_subNum --;
//        }
//        return returnStr.substring(0, traj_subNum);
        return returnStr;
    }

    /**
     * crc校验
     * @return
     */
    public static String crcVerify(String data){
        data = data.replace(" ", "");
        int len = data.length();
        if (!(len % 2 == 0)) {
            return "0000";
        }
        int num = len / 2;
        byte[] para = new byte[num];
        for (int i = 0; i < num; i++) {
            int value = Integer.valueOf(data.substring(i * 2, 2 * (i + 1)), 16);
            para[i] = (byte) value;
        }
        return getCRC(para);
    }

    /**
     * 计算CRC16校验码
     *
     * @param bytes 字节数组
     * @return {@link String} 校验码
     * @since 1.0
     */
    public static String getCRC(byte[] bytes) {
        //CRC寄存器全为1
        int CRC = 0x0000ffff;
        //多项式校验值
        int POLYNOMIAL = 0x0000a001;
        int i, j;
        for (i = 0; i < bytes.length; i++) {
            CRC ^= ((int) bytes[i] & 0x000000ff);
            for (j = 0; j < 8; j++) {
                if ((CRC & 0x00000001) != 0) {
                    CRC >>= 1;
                    CRC ^= POLYNOMIAL;
                } else {
                    CRC >>= 1;
                }
            }
        }
        //结果转换为16进制
        String result = Integer.toHexString(CRC).toUpperCase();
        if (result.length() != 4) {
            StringBuffer sb = new StringBuffer("0000");
            result = sb.replace(4 - result.length(), 4, result).toString();
        }
        return result;
    }

    /**
     * 计算CRC16校验码
     *
     * @param bytes 字节数组
     * @return {@link String} 校验码
     * @since 1.0
     */
    public static int getCRCShort(byte[] bytes) {
        //CRC寄存器全为1
        int CRC = 0x0000ffff;
        //多项式校验值
        int POLYNOMIAL = 0x0000a001;
        int i, j;
        for (i = 0; i < bytes.length; i++) {
            CRC ^= ((int) bytes[i] & 0x000000ff);
            for (j = 0; j < 8; j++) {
                if ((CRC & 0x00000001) != 0) {
                    CRC >>= 1;
                    CRC ^= POLYNOMIAL;
                } else {
                    CRC >>= 1;
                }
            }
        }
        return CRC;
    }

    /**
     * 十六进制整数转十进制整数
     * @param str
     * @return
     */
    public static int hexToInt(String str){
        return Integer.parseInt(str, 16);
    }

    /**
     * 十六进制整数转十进制long整数
     * @param str
     * @return
     */
    public static long hexToLong(String str){
        return Long.parseLong(str, 16);
    }

    /**
     * 将4字节的16进制字符串，转换为32位带符号的十进制浮点型
     * @param str 4字节 16进制字符
     * @return
     */
    public static float hexToFloat(String str){
        return Float.intBitsToFloat(new BigInteger(str,16).intValue());
    }

    /**
     * 将带符号的32位浮点数装换为16进制
     * @param value
     * @return
     */
    public static String folatToHexString(Float value){
        return Integer.toHexString(Float.floatToIntBits(value));
    }


    public static boolean isDebug(){
        List<String> args_ = ManagementFactory.getRuntimeMXBean().getInputArguments();
        boolean isDebug = false;
        for (String arg : args_) {
            if (arg.startsWith("-Xrunjdwp") || arg.startsWith("-agentlib:jdwp")) {
                isDebug = true;
                break;
            }
        }
        return isDebug;
    }

    public static void main(String[] args) {
        //System.out.println(Integer.parseInt("02", 16));
//        byte[] bytes = new byte[]{0, 0, 1, 123, -25, 81, 37, -113};
//        System.out.println(RadarUtils.byteToHexStr(bytes));
//        System.out.println(RadarUtils.hexToLong(RadarUtils.byteToHexStr(bytes)));
//        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(RadarUtils.hexToLong(RadarUtils.byteToHexStr(bytes))));
//        byte b = 0b1000000;
//        int bInt = b;
//        System.out.println(b);
        byte[] bytes = new byte[5];
        ByteBuf byteBuf = Unpooled.buffer(5);
        byteBuf.writeByte(1);
        byteBuf.writeByte(2);
        byteBuf.writeByte(3);
        byteBuf.writeByte(4);
        byteBuf.writeByte(5);
        byteBuf.getBytes(0, bytes);
        for (byte aByte : bytes) {
            System.out.println(aByte);
        }
    }
}
