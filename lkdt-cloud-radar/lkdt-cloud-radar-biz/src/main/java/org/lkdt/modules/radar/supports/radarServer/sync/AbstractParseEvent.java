package org.lkdt.modules.radar.supports.radarServer.sync;

import org.lkdt.modules.radar.supports.radarTools.DO.RadarDO;
import io.netty.channel.ChannelHandlerContext;

public interface AbstractParseEvent {

    /**
     * 实时数据解析前
     * @param ctx
     * @param original
     * @param frameHead
     */
    public void TRAJBeforeParse(ChannelHandlerContext ctx, byte[] original, String frameHead);

    /**
     * 实时数据解析后
     * @param ctx
     * @param original
     * @param frameHead
     * @param radarDO
     */
    public void TRAJAfterParse(ChannelHandlerContext ctx, byte[] original, String frameHead, RadarDO radarDO);

    /**
     * 统计数据解析前
     * @param ctx
     * @param original
     * @param frameHead
     */
    public void TFCFBeforeParse(ChannelHandlerContext ctx, byte[] original, String frameHead);

    /**
     * 统计数据解析后
     * @param ctx
     * @param original
     * @param frameHead
     * @param radarDO
     */
    public void TFCFAfterParse(ChannelHandlerContext ctx, byte[] original, String frameHead, RadarDO radarDO);

    /**
     * 心跳数据
     * @param ctx
     * @param original
     * @param frameHead
     */
    public void KKAHandle(ChannelHandlerContext ctx, byte[] original, String frameHead);

    /**
     * 实时数据处理
     * @param ctx
     * @param radarDO
     */
    public void dataHandler(ChannelHandlerContext ctx, RadarDO radarDO);

    /**
     * 统计数据处理
     * @param ctx
     * @param radarDO
     */
    public void tongJiDataHandler(ChannelHandlerContext ctx, RadarDO radarDO);

}
