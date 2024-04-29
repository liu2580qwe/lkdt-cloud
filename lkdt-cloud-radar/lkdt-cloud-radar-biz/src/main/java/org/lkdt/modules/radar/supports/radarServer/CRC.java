package org.lkdt.modules.radar.supports.radarServer;

public class CRC {
	/**
	 * 一个字节包含位的数量 8
	 */
	private static final int BITS_OF_BYTE = 8;
	/**
	 * 多项式
	 */
	private static final int POLYNOMIAL = 0xA001;
	/**
	 * 初始值
	 */
	private static final int INITIAL_VALUE = 0xFFFF;

	public static void main(String[] args) {
		String result = "133700000005002b0e0100";
		
		String orgStr = ConvertCode.hexString2String(result);
	
		System.out.println( orgStr);
		
	}

	public static int[] hexStr2Bytes(String src) {
//		String[] tmp = src.split(" ");
//		int[] ret = new int[tmp.length];
//		for (int i = 0; i < tmp.length; i++) {
//			ret[i] = Integer.decode("0x" + tmp[i]);
//		}
		

		int l = src.length() / 2;
		int[] ret = new int[l];
		for (int i = 0; i < l; i++) {
			ret[i] = Integer.decode("0x" + src.substring(i * 2, i * 2 + 2)); 
			// (byte) Integer.valueOf(src.substring(i * 2, i * 2 + 2), 16).byteValue();
		}
		return ret;
	}

	/**
	 * CRC16 编码
	 *
	 * @param bytes 编码内容
	 * @return 编码结果
	 */
	public static String crc16(int[] bytes) {
		int res = INITIAL_VALUE;
		for (int data : bytes) {
			res = res ^ data;
			for (int i = 0; i < BITS_OF_BYTE; i++) {
				res = (res & 0x0001) == 1 ? (res >> 1) ^ POLYNOMIAL : res >> 1;
			}
		}
		res = revert(res);

		String hex = Integer.toHexString(res);
		if (hex.length() < 4) {
			hex = "0000" + hex;
			hex = hex.substring(hex.length() - 4);
		}
		return hex;
	}

	/**
	 * 翻转16位的高八位和低八位字节
	 *
	 * @param src 翻转数字
	 * @return 翻转结果
	 */
	private static int revert(int src) {
		int lowByte = (src & 0xFF00) >> 8;
		int highByte = (src & 0x00FF) << 8;
		return lowByte | highByte;
	}
}
