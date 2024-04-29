package org.lkdt.modules.weixin.entity;

/**
 * @author HuangJunYao
 * @date 2021/4/26
 */
public class WXMessage extends WxSubscribe {

    /**
     * 文本消息类
     */
    private static final long serialVersionUID = 1L;
    private String Content;
    private String MsgId;
    private String MsgType;

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getMsgId() {
        return MsgId;
    }

    public void setMsgId(String msgId) {
        MsgId = msgId;
    }

    public String getMsgType() {
        return MsgType;
    }

    public void setMsgType(String msgType) {
        MsgType = msgType;
    }
}
