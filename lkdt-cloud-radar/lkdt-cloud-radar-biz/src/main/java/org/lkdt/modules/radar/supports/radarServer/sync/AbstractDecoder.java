package org.lkdt.modules.radar.supports.radarServer.sync;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import org.lkdt.modules.radar.supports.radarDataHandle.RadarEventDataGrab;
import org.lkdt.modules.radar.supports.radarServer.dataHandle.ServerListen;
import org.lkdt.modules.radar.supports.radarTools.DO.RadarDO;
import org.lkdt.modules.radar.supports.radarTools.RadarUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractDecoder extends ByteToMessageDecoder {

    /**已注册通道集*/
    public static ConcurrentHashMap<String, ChannelHandlerContext> channelMaps = new ConcurrentHashMap<>();

    /**总连接数*/
    public static AtomicInteger nConnection = new AtomicInteger(0);

    /**缺省：服务端*/
    protected AbstractParseEvent abstractParseEvent;

    public AbstractDecoder(){

    }

    public void addParseEventCall(AbstractParseEvent abstractParseEvent){
        this.abstractParseEvent = abstractParseEvent;
    }

//    static AtomicInteger other = new AtomicInteger(0);
//    static AtomicInteger traj = new AtomicInteger(0);
//    static AtomicInteger trajTotal = new AtomicInteger(0);
//    static AtomicInteger tongji = new AtomicInteger(0);
//    static AtomicInteger tongjiTotal = new AtomicInteger(0);
//    static AtomicInteger xintiao = new AtomicInteger(0);
//    static AtomicInteger wuxiao = new AtomicInteger(0);
//    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    @SuppressWarnings("Duplicates")
    protected void parseBytesToHandle(ChannelHandlerContext ctx, ByteBuf in){
        //System.out.println(sdf.format(Calendar.getInstance().getTimeInMillis()) + ": fffffffffffffffffffffffffffffffffffffff");
        ByteBuf byteBuf = in.readBytes(in.readableBytes());
        int bytesLength = byteBuf.readableBytes();
        byte[] bytes = new byte[bytesLength];
        byteBuf.getBytes(0, bytes);
        if(byteBuf.readableBytes() < 4){
            //System.out.println("parse无效" + RadarUtils.byteToHexStr(bytes));
            //wuxiao.incrementAndGet();
            return;
        }

        CharSequence charSequence = byteBuf.readCharSequence(4, CharsetUtil.US_ASCII);
        String frameHead = charSequence.toString(); //"" + (char)bytes[0] + (char)bytes[1] + (char)bytes[2] + (char)bytes[3];

        //实时数据
        if("TRAJ".equalsIgnoreCase(frameHead)){
            //System.out.println("***" + frameHead + "***");

            int frameLengthRemain = byteBuf.readInt();

            //校验位, 2个字节 4个字母
            if(bytes.length < frameLengthRemain + 8){
                //数据无效
                //System.out.println("TRAJ无效数据：" + new String(bytes, CharsetUtil.UTF_8));
                return;
            }

            ByteBuf byteBuf_traj = byteBuf.readBytes(frameLengthRemain);
            int len = byteBuf_traj.readableBytes();
            ByteBuf dataBodyBuf = byteBuf_traj.readBytes(len - 2);
            int length = dataBodyBuf.readableBytes();
            byte[] dataBodyArr = new byte[length];
            dataBodyBuf.readBytes(dataBodyArr);
            ReferenceCountUtil.release(dataBodyBuf);

            int crc1 = byteBuf_traj.readByte() & 0xFF;
            int crc2 = byteBuf_traj.readByte() & 0xFF;
            ReferenceCountUtil.release(byteBuf_traj);
            int crc16_int = (crc1 << 8) + crc2;

            int crc16_coputed = RadarUtils.getCRCShort(dataBodyArr);
            if(crc16_int == crc16_coputed){
                //traj.incrementAndGet();
                //System.out.println("验证通过");
                abstractParseEvent.TRAJBeforeParse(ctx, bytes, frameHead);
                //数据解析72~
                RadarDO radarDO = dataHandler(ctx, dataBodyArr);
                abstractParseEvent.TRAJAfterParse(ctx, bytes, frameHead, radarDO);
            } else {
                //System.out.print("");
            }
            //trajTotal.incrementAndGet();
        } else if("TFCF".equalsIgnoreCase(frameHead)){
            //统计数据
            //System.out.println("***" + frameHead + "***");

            int frameLengthRemain = byteBuf.readInt();

            //校验位, 2个字节 4个字母
            if(bytes.length < frameLengthRemain + 8){
                //数据无效
                //System.out.println("TFCF无效数据：" + tfcf_str);
                return;
            }
            ByteBuf byteBuf_tfcf = byteBuf.readBytes(frameLengthRemain);
            int len = byteBuf_tfcf.readableBytes();
            ByteBuf dataBodyBuf = byteBuf_tfcf.readBytes(len - 2);
            int length = dataBodyBuf.readableBytes();
            byte[] dataBodyArr = new byte[length];
            dataBodyBuf.readBytes(dataBodyArr);
            ReferenceCountUtil.release(dataBodyBuf);

            int crc1 = byteBuf_tfcf.readByte() & 0xFF;
            int crc2 = byteBuf_tfcf.readByte() & 0xFF;
            ReferenceCountUtil.release(byteBuf_tfcf);
            int crc16_int = (crc1 << 8) + crc2;

            int crc16_coputed = RadarUtils.getCRCShort(dataBodyArr);
            if(crc16_int == crc16_coputed){
                //tongji.incrementAndGet();
                //System.out.println("统计数据【待处理】：" + dataBodyArr);
                abstractParseEvent.TFCFBeforeParse(ctx, bytes, frameHead);
                RadarDO radarDO = tongJiDataHandle(ctx, dataBodyArr);
                abstractParseEvent.TFCFAfterParse(ctx, bytes, frameHead, radarDO);
            } else {
                //System.out.print("");
            }
            //tongjiTotal.incrementAndGet();
        } else if("#KKA".equalsIgnoreCase(frameHead)){
            //xintiao.incrementAndGet();
            //接收心跳包
            abstractParseEvent.KKAHandle(ctx, bytes, frameHead);
            //System.out.println("接收心跳包！");
        } else {
            //other.incrementAndGet();
            byte[] bytes1 = new byte[]{bytes[0], bytes[1], bytes[2], bytes[3]};
            //System.out.print("其它数据头：" + new String(bytes1, CharsetUtil.UTF_8));
            //System.out.println(" 其它数据：" + RadarUtils.byteToHexStr(bytes));
        }
        ReferenceCountUtil.release(byteBuf);
    }

//    static {
//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                System.out.printf("traj: %s, tongji: %s, trajTotal: %s, tongjiTotal: %s, xintiao: %s, wuxiao: %s, other: %s, \n",
//                        traj, tongji, trajTotal, tongjiTotal, xintiao, wuxiao, other);
//            }
//        }, 0, 60000);
//    }

    /**
     * 数据解析
     * [{targetId:"目标ID",sX:"x坐标", sY:"y坐标", vX:"x速度", vY:"y速度", aX:"x加速度", aY:"y加速度", laneNum:"车道号", carType:"车辆类型",
     * event:"事件类型", snowId:"雪花ID", lng:"经度", lat:"维度", x1:"X1距离", currTarget:"当前目标计数", carLength:"车辆长度"}]
     * @param data
     */
    @SuppressWarnings("Duplicates")
    protected RadarDO dataHandler(ChannelHandlerContext ctx, byte[] dataBodyBuf){
        ByteBuf data = Unpooled.copiedBuffer(dataBodyBuf);
        RadarDO radarDO = new RadarDO();
        //目标检测数据
        JSONArray jsonArray = new JSONArray();
        //数据时间戳【仅参考】
        byte[] ymdhmsTemp = new byte[]{data.readByte(), data.readByte(), data.readByte(), data.readByte(), data.readByte(), data.readByte()};//6字节
        String ymdhms = RadarUtils.byteTo10Str(ymdhmsTemp);
        //long timestamp = RadarUtils.hexToLong(data.substring(12, 28));//因雷达固件不稳定，偶发数值过大的bug
        long timestamp = 0;
        int bL8 = 8;
        while(bL8-- > 0){ //8字节
            long byteX = data.readByte() & 0xFF;
            timestamp += byteX << bL8 * 8;
        }
        String radarKeyId = "" + data.readShort();//2字节
        //如下字节数据保留
        //data.readBytes(56);
        data.readerIndex(data.readerIndex() + 56);
//        data.readByte();//排队起始位置 1字节
//        data.readByte();//1/12车道排队长度 1字节
//        data.readByte();//2/12车道排队长度 1字节
//        data.readByte();//3/12车道排队长度 1字节
//        data.readByte();//4/12车道排队长度 1字节
//        data.readByte();//5/12车道排队长度 1字节
//        data.readByte();//6/12车道排队长度 1字节
//        data.readByte();//7/12车道排队长度 1字节
//        data.readByte();//8/12车道排队长度 1字节
//        data.readByte();//9/12车道排队长度 1字节
//        data.readByte();//10/12车道排队长度 1字节
//        data.readByte();//11/12车道排队长度 1字节
//        data.readByte();//12/12车道排队长度 1字节
//        data.readShort();//帧计数器 2字节
//        data.readByte();//道路拥堵情况 1字节
//        data.readByte();//保留 1字节
//        data.readShort();//CntNtpInfact 2字节
//        data.readLong();//时间戳1 8字节
//        data.readLong();//时间戳2 8字节
//        data.readLong();//时间戳3 8字节
//        data.readShort();//请求NTP次数 2字节
//        data.readShort();//NTP校时延迟 2字节
//        data.readLong(); //上一次校时时间戳 8字节
//        data.readByte();//目标总数目 1字节
        //72~  len=80
        //String targets = data.substring(72*2);
        //System.out.println(targets.length()*1.0/80.0);
        if(data.readableBytes() % 80 == 0){
            int circularNum = data.readableBytes()/80;
            //System.out.println("数据长度：" + targets.length() + "\n数据个数：" + circularNum);
            for(int i = 0; i < circularNum; i++){
//                int tarStart = i*80*2;
//                int tarEnd = tarStart + 80*2;
//                String target = data.substring(tarStart, tarEnd);
                //System.out.println(target);
                JSONObject obj = new JSONObject();
                obj.putOnce("targetId", data.readByte() & 0xFF); //目标ID 1字节
                obj.putOnce("sX", data.readFloat()); //x坐标 4字节
                obj.putOnce("sY", data.readFloat()); //y坐标 4字节
                obj.putOnce("vX", data.readFloat()); //x速度 4字节
                obj.putOnce("vY", data.readFloat()); //y速度 4字节
                obj.putOnce("aX", data.readFloat()); //x加速度 4字节
                obj.putOnce("aY", data.readFloat()); //y加速度 4字节
                obj.putOnce("laneNum", data.readByte() & 0xFF); //车道号 1字节
                obj.putOnce("carType", data.readByte() & 0xFF); //车辆类型 1字节
                obj.putOnce("event", data.readByte() & 0xFF); //事件类型 1字节
//                data.readLong(); //雪花ID 8字节
//                data.readFloat(); //经度 4字节
//                data.readFloat(); //维度 4字节
//                data.readFloat(); //X1距离 4字节
//                data.readShort(); //当前目标计数 2字节
                data.readerIndex(data.readerIndex() + 22);
                obj.putOnce("carLength", data.readByte() & 0xFF);//车辆长度 1字节
                //data.readBytes(29); //保留字29*2=58
                data.readerIndex(data.readerIndex() + 29);
                jsonArray.add(obj);
            }
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////
            radarDO.setDate(Calendar.getInstance().getTime());
            radarDO.setNanoSecond(System.nanoTime());
            radarDO.setDataBody(jsonArray);
            radarDO.setYmdhms(ymdhms);
            radarDO.setTimestamp(timestamp);
            radarDO.setRadarKeyId(radarKeyId);
            //System.out.println("radarKeyId: " + radarKeyId);
            abstractParseEvent.dataHandler(ctx, radarDO);
            RadarEventDataGrab radarEventDataGrab = ServerListen.radarEventDataGrabs.get(radarKeyId);
            if(radarEventDataGrab != null){
                radarEventDataGrab.arrayBlockingQueue.offer(radarDO);
            }
            //System.out.println(sdf.format(timestamp));
            //System.out.println("缓存数目：" + arrayBlockingQueue.size());
            //System.out.println(jsonArray.toString());
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////
        }
        ReferenceCountUtil.release(data);
        return radarDO;
    }

    /**
     * 统计数据解析
     * @param data
     */
    @SuppressWarnings("Duplicates")
    private RadarDO tongJiDataHandle(ChannelHandlerContext ctx, byte[] dataBodyBuf){
        ByteBuf data = Unpooled.copiedBuffer(dataBodyBuf);
        RadarDO radarDO = new RadarDO();
        //目标检测数据
        JSONArray jsonArray = new JSONArray();
        //数据时间戳【仅参考】
        //数据时间戳【仅参考】
        byte[] ymdhmsTemp = new byte[]{data.readByte(), data.readByte(), data.readByte(), data.readByte(), data.readByte(), data.readByte()};
        String ymdhms = RadarUtils.byteTo10Str(ymdhmsTemp);
        String radarKeyId = "" + data.readShort();//2字节
        int SectionNumber = data.readByte() & 0xFF;//1字节
        int SectionLocation = data.readByte() & 0xFF;//1字节
        //data.readBytes(22);//保留
        data.readerIndex(data.readerIndex() + 22);
        //37~  len=32

        //System.out.println(targets.length()*1.0/80.0);
        if(data.readableBytes() % 37 == 0){
            int circularNum = data.readableBytes()/37;
            //System.out.println("数据长度：" + targets.length() + "\n数据个数：" + circularNum);
            for(int i = 0; i < circularNum; i++){
                JSONObject obj = new JSONObject();
                // 保留字0~35，车道号36~37，平均车速38~39，车道占有率40~41，车头时距42~44，车流量46~49，排队长度50~53，
                // 小型车流量54~57，大型车流量58~61，中型车流量62~65，车头间距66~69，车道空间占有率70~71，保留72~73
                //data.readBytes(18);
                data.readerIndex(data.readerIndex() + 18);
                obj.putOnce("lane", data.readByte() & 0xFF); //车道号
                obj.putOnce("averageSpeed", data.readByte() & 0xFF); //平均车速
                obj.putOnce("occupancy", data.readByte() & 0xFF); //车道占有率
                obj.putOnce("headWay", data.readShort()); //车头时距
                obj.putOnce("flow", data.readShort()); //车流量
                obj.putOnce("queueLength", data.readShort()); //排队长度
                obj.putOnce("sumMini", data.readShort()); //小型车流量
                obj.putOnce("sumLarge", data.readShort()); //大型车流量
                obj.putOnce("sumMid", data.readShort()); //中型车流量
                obj.putOnce("headWay2", data.readShort()); //车头间距
                obj.putOnce("Occupancy2", data.readByte() & 0xFF); //车道空间占有率
                //data.readByte();//保留
                data.readerIndex(data.readerIndex() + 1);
                jsonArray.add(obj);
            }
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////
            radarDO.setDate(new Date());
            radarDO.setDataBody(jsonArray);
            radarDO.setRadarKeyId(radarKeyId);
            abstractParseEvent.tongJiDataHandler(ctx, radarDO);
            RadarEventDataGrab radarEventDataGrab = ServerListen.radarEventDataGrabs.get(radarKeyId);
            if(radarEventDataGrab != null){
                radarEventDataGrab.liveTJQueue.offer(radarDO);
            }
            if(RadarUtils.RUN_IN_DEBUG){
                //System.out.println("统计数据：" + timestamp + " " + SectionNumber + " " + SectionLocation + " " + jsonArray.toString());
                //System.out.println("统计数据：" + data);
            }
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////
        }
        ReferenceCountUtil.release(data);
        return radarDO;
    }
}
