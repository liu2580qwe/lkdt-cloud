package org.lkdt.modules.radar.supports.radarDataHandle.extend;

/**
 * 数据传输接口协议
 */
public interface LengthTransfer extends JsonSerializable {

    //传输数据内容的字节数组
    byte[] getBytes();

    //数据内容的字节数组长度
    Integer getLength();

}