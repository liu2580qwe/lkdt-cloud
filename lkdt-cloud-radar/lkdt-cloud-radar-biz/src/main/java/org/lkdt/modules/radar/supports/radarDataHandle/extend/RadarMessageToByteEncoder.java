package org.lkdt.modules.radar.supports.radarDataHandle.extend;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class RadarMessageToByteEncoder extends MessageToByteEncoder<DefaultLengthTransferPacket> {

    protected void encode(ChannelHandlerContext ctx, RadarMessage msg, ByteBuf out) throws Exception {

//        //发送数据包长度
//        out.writeInt(msg.getLength());
//        //发送数据包
//        out.writeBytes(msg.getBytes());

        System.out.println();

    }


    @Override
    protected void encode(ChannelHandlerContext ctx, DefaultLengthTransferPacket msg, ByteBuf out) throws Exception {
        RadarMessage chatMessage = (RadarMessage) msg.getData();
        byte[] data = (byte[])chatMessage.getContent();
        //out.writeInt(data.getBytes().length);
        out.writeBytes(data);
    }


}