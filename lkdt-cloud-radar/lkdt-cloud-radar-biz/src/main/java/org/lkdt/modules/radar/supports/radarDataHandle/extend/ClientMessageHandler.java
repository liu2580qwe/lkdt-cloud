package org.lkdt.modules.radar.supports.radarDataHandle.extend;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Scanner;

public class ClientMessageHandler extends SimpleChannelInboundHandler<LengthTransfer> {

    private Channel channel;

    private final Scanner scanner = new Scanner(System.in);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        channel = ctx.channel();
    }

    public void sendMessage(RadarMessage message) {
        if (channel != null && channel.isWritable()) {
            LengthTransfer transfer = new DefaultLengthTransferPacket<>(DefaultLengthTransferPacket.PACKET_TYPE_DATA, message);
            channel.writeAndFlush(transfer);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LengthTransfer msg) throws Exception {

        byte[] bytes = msg.getBytes();

        String content = new String(bytes);

        System.out.println("接收到回传消息：" + content);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println(cause);
    }

}