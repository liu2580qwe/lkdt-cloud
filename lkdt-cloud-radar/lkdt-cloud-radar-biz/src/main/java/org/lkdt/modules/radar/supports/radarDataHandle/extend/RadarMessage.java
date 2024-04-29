package org.lkdt.modules.radar.supports.radarDataHandle.extend;

public abstract class RadarMessage<T> implements JsonSerializable {

    transient protected static final Integer MESSAGE_MEDIA_TYPE_TEXT = 0;

    private Long fromUid;

    private Long toUid;

    private T content;

    private Integer msgType;  //接收者类型 -1：系统消息， O：单聊，1：群聊

    private Integer mediaType; //消息内容类型： 0：文本， 1：图片


    /**
     * Gets msg type.
     *
     * @return the msg type
     * 0：系统消息
     * 1：文本消息
     * 2：图片消息
     */
    abstract public Integer getMediaType(); //消息类型（文本消息）

    public static Integer getMessageMediaTypeText() {
        return MESSAGE_MEDIA_TYPE_TEXT;
    }

    public Long getFromUid() {
        return fromUid;
    }

    public void setFromUid(Long fromUid) {
        this.fromUid = fromUid;
    }

    public Long getToUid() {
        return toUid;
    }

    public void setToUid(Long toUid) {
        this.toUid = toUid;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }

    public Integer getMsgType() {
        return msgType;
    }

    public void setMsgType(Integer msgType) {
        this.msgType = msgType;
    }

    public void setMediaType(Integer mediaType) {
        this.mediaType = mediaType;
    }
}