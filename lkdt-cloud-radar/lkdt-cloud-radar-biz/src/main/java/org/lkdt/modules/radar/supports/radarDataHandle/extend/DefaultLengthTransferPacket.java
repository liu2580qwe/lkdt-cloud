package org.lkdt.modules.radar.supports.radarDataHandle.extend;

import java.nio.charset.StandardCharsets;

public class DefaultLengthTransferPacket<T> implements LengthTransfer {

    transient public static final byte PACKET_TYPE_DATA = 1;   //数据包
    transient public static final byte PACKET_TYPE_MIND = 2;   //心跳包

    transient public static final String PROTOCOL_VERSION = "1.0";

    //数据包类型
    private byte type;

    //时间戳
    private Long timestamp = System.currentTimeMillis() / 1000;

    //协议版本号
    private String version = PROTOCOL_VERSION;

    //数据内容
    private T data;

    public T getData() {
        return data;
    }

    private DefaultLengthTransferPacket() {
        throw new AssertionError("sorry, you can not call this private constructor!");
    }

    public DefaultLengthTransferPacket(byte type) {
        this.type = type;
    }

    public DefaultLengthTransferPacket(byte type, T data) {
        this.type = type;
        this.data = data;
    }

    public Integer getLength() {
        return this.getBytes().length;
    }

    public byte[] getBytes(){
        return this.toJsonString().getBytes(StandardCharsets.UTF_8);
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}