package org.lkdt.modules.radar.supports.radarDataHandle.extend;

import org.lkdt.modules.radar.supports.radarDataHandle.RadarEventDataGrab;
import org.lkdt.modules.radar.supports.radarServer.sync.AbstractParseEvent;
import org.lkdt.modules.radar.supports.radarTools.DO.RadarDO;
import org.lkdt.modules.radar.supports.radarTools.RadarUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class RadarClientV2 {

    private String host;
    private Integer port;
    private RadarEventDataGrab radarEventDataGrab;

    private ClientMessageHandler messageHandler = null;

    private Bootstrap bootstrap;

    public RadarClientV2(String host, Integer port, RadarEventDataGrab radarEventDataGrab) {
        this.host = host;
        this.port = port;
        this.radarEventDataGrab = radarEventDataGrab;
        this.lastHeartHeapTime = 0;
        this.lastDataTime = 0;
        this.bootstrap = setup();
    }

    private Bootstrap setup() {
        EventLoopGroup group = new NioEventLoopGroup();
        return new Bootstrap()
            .group(group)
            .channel(NioSocketChannel.class)
            .option(ChannelOption.SO_KEEPALIVE,true)
            .option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(11*1024))
            .handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();

                    //添加日志handler
                    LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);
                    pipeline.addLast(loggingHandler);

                    //处理出站消息的处理器
                    pipeline.addLast(new RadarMessageToByteEncoder());

                    RadarByteToMessageDecoder radarByteToMessageDecoder = new RadarByteToMessageDecoder();
                    radarByteToMessageDecoder.addParseEventCall(new AbstractParseEvent() {

                        @Override
                        public void TRAJBeforeParse(ChannelHandlerContext ctx, byte[] original, String frameHead) {
                            lastDataTime = Calendar.getInstance().getTimeInMillis();
                        }

                        @Override
                        public void TRAJAfterParse(ChannelHandlerContext ctx, byte[] original, String frameHead, RadarDO radarDO) {

                        }

                        @Override
                        public void TFCFBeforeParse(ChannelHandlerContext ctx, byte[] original, String frameHead) {

                        }

                        @Override
                        public void TFCFAfterParse(ChannelHandlerContext ctx, byte[] original, String frameHead, RadarDO radarDO) {

                        }

                        @Override
                        public void KKAHandle(ChannelHandlerContext ctx, byte[] original, String frameHead) {
                            lastHeartHeapTime = Calendar.getInstance().getTimeInMillis();
                        }

                        @Override
                        public void dataHandler(ChannelHandlerContext ctx, RadarDO radarDO) {
                            radarDO.setRadarId(radarEventDataGrab.getRadarId());
                            radarEventDataGrab.COMMON_ArrayBlockingQueue.offer(radarDO);
                            radarEventDataGrab.setMomentRadarDO(radarDO);
                            radarEventDataGrab.timeClock = new Date().getTime();
                        }

                        @Override
                        public void tongJiDataHandler(ChannelHandlerContext ctx, RadarDO radarDO) {
                            //实时统计数据
                            radarEventDataGrab.liveTJQueue.offer(radarDO);
                        }

                    });
                    //处理入站消息的处理器
                    pipeline.addLast(radarByteToMessageDecoder);

                    messageHandler = new ClientMessageHandler();
                    //业务处理器
                    pipeline.addLast(messageHandler);

                }
            });
    }

    public void run() throws InterruptedException {
        bootstrap.connect(host,port).sync()
                .channel().closeFuture().sync();
    }

    public void connect() {
        //System.out.println("connect " + host + " " + Thread.currentThread());
        bootstrap.connect(host, port).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    System.out.printf("【%s】连接成功 %s:%s\n", radarEventDataGrab.getRadarId(), host, port);
                    sendStartCommand();
                } else {
                    System.out.printf("【%s】连接失败 %s:%s\n", radarEventDataGrab.getRadarId(), host, port);
                    //5秒后重新连接
                    future.channel().eventLoop().schedule(new Runnable() {
                        @Override
                        public void run() {
                            //System.out.println("reconnect " + host + " " + Thread.currentThread());
                            connect();
                        }
                    }, 5, TimeUnit.SECONDS);
                }
            }
        });
    }

    public void runWithListener(GenericFutureListener listener) throws InterruptedException {
        bootstrap.connect(host,port).addListener(listener).sync()
                .channel().closeFuture().sync();
    }


    public void shutdown() {
        bootstrap.group().shutdownGracefully();
    }

    /**
     * 雷达开始运行命令, 实时数据与统计数据同时发出
     */
    public void sendStartCommand() {
        if(messageHandler != null){
            RadarMessage message = new RadarMessageText();
            String command = "43 41 50 4d 00 00 00 04 00 c8 00 04";
            byte[] bytes = RadarUtils.hexStr2Bytes(command.replace(" ", ""));
            message.setContent(bytes);
            messageHandler.sendMessage(message);
        } else {
            System.out.printf("【%s】: messageHandler is null\n", radarEventDataGrab.getRadarId());
        }

    }

    private long lastHeartHeapTime = 0;

    public long getLastHeartHeapTime() {
        return lastHeartHeapTime;
    }

    private long lastDataTime = 0;

    public long getLastDataTime() {
        return lastDataTime;
    }
}