package org.lkdt.modules.radar.supports.radarDataHandle.extend;

import org.lkdt.modules.radar.supports.radarServer.sync.AbstractDecoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

/**
 * 客户端解码器
 */
public class RadarByteToMessageDecoder extends AbstractDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        //System.out.println(in);
        try {
            parseBytesToHandle(ctx, in);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //ReferenceCountUtil.release(in);
        }

    }

}