package org.lkdt.modules.radar.supports.radarDataHandle.extend;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import org.lkdt.modules.radar.supports.radarServer.sync.AbstractParseEvent;
import org.lkdt.modules.radar.supports.radarTools.DO.RadarDO;
import org.lkdt.modules.radar.supports.radarTools.RadarUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 解码器老版本【弃用】
 */
@Deprecated
public class RadarByteToMessageDecoderOV extends ByteToMessageDecoder {

    private AbstractParseEvent abstractParseEvent = new AbstractParseEvent() {
        @Override
        public void TRAJBeforeParse(ChannelHandlerContext ctx, byte[] original, String frameHead) {

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

        }

        @Override
        public void dataHandler(ChannelHandlerContext ctx, RadarDO radarDO) {

        }

        @Override
        public void tongJiDataHandler(ChannelHandlerContext ctx, RadarDO radarDO) {

        }

    };

    public void addParseEventCall(AbstractParseEvent abstractParseEvent){
        this.abstractParseEvent = abstractParseEvent;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        //System.out.println(in);

        byte[] bytes = new byte[in.readableBytes()];
        try {
            in.readBytes(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            parseBytesToHandle(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    @SuppressWarnings("Duplicates")
    private void parseBytesToHandle(byte[] bytes){
        //System.out.print(sdf.format(new Date()) + "||");
        if(bytes.length < 4){
            //System.out.println("parse无效" + RadarUtils.byteToHexStr(bytes));
            return;
        }
        String frameHead = "" + (char)bytes[0] + (char)bytes[1] + (char)bytes[2] + (char)bytes[3];

        //实时数据
        if("TRAJ".equalsIgnoreCase(frameHead)){
            //System.out.println("***" + frameHead + "***");
            int frameLength1 = bytes[4] & 0xFF;
            int frameLength2 = bytes[5] & 0xFF;
            int frameLength3 = bytes[6] & 0xFF;
            int frameLength4 = bytes[7] & 0xFF;
            int frameLength = 8 + frameLength1 * (int)(Math.pow(16, 6)) + frameLength2 * (int)(Math.pow(16, 4)) +
                    frameLength3 * (int)(Math.pow(16, 2)) + frameLength4;

            String traj_str = RadarUtils.byteToHexStr(bytes);
            //System.out.println(traj_str);

            //校验位, 2个字节 4个字母
            if(traj_str.length() < frameLength*2){
                //数据无效
                //System.out.println("TRAJ无效数据：" + traj_str);
                return;
            }
            traj_str = traj_str.substring(0, frameLength*2);
            //System.out.println(traj_str);
            int len = traj_str.length();
            String crc16_str = traj_str.substring(len - 4, len);
            String dataBody = traj_str.substring(8*2, traj_str.length() - 4);
            String crc16_coputed = RadarUtils.crcVerify(dataBody);
            if(StringUtils.equalsIgnoreCase(crc16_str, crc16_coputed)){
                //System.out.println("验证通过");
                //数据解析72~
                JSONArray jsonArray = dataHandler(dataBody);
            }
        } else if("TFCF".equalsIgnoreCase(frameHead)){
            //统计数据
            //System.out.println("***" + frameHead + "***");
            int frameLength1 = bytes[4] & 0xFF;
            int frameLength2 = bytes[5] & 0xFF;
            int frameLength3 = bytes[6] & 0xFF;
            int frameLength4 = bytes[7] & 0xFF;
            int frameLength = 8 + frameLength1 * (int)(Math.pow(16, 6)) + frameLength2 * (int)(Math.pow(16, 4)) +
                    frameLength3 * (int)(Math.pow(16, 2)) + frameLength4;

            String tfcf_str = RadarUtils.byteToHexStr(bytes);
            //System.out.println(tfcf_str);

            //校验位, 2个字节 4个字母
            if(tfcf_str.length() < frameLength*2){
                //数据无效
                //System.out.println("TFCF无效数据：" + tfcf_str);
                return;
            }
            tfcf_str = tfcf_str.substring(0, frameLength*2);
            //System.out.println(tfcf_str);
            int len = tfcf_str.length();
            String crc16_str = tfcf_str.substring(len - 4, len);
            //System.out.println(tfcf_str.substring(8*2+1, tfcf_str.length() - 4));
            String dataBody = tfcf_str.substring(8*2, tfcf_str.length() - 4);
            String crc16_coputed = RadarUtils.crcVerify(dataBody);
            if(StringUtils.equalsIgnoreCase(crc16_str, crc16_coputed)){
                //System.out.println("统计数据【待处理】：" + tfcf_str);
                JSONArray jsonArray = tongJiDataHandle(dataBody);
            }
        } else if("#KKA".equalsIgnoreCase(frameHead)){
            //接收心跳包
            //System.out.println("接收心跳包！");
        }

    }

    /**
     * 数据解析
     * [{targetId:"目标ID",sX:"x坐标", sY:"y坐标", vX:"x速度", vY:"y速度", aX:"x加速度", aY:"y加速度", laneNum:"车道号", carType:"车辆类型",
     * event:"事件类型", snowId:"雪花ID", lng:"经度", lat:"维度", x1:"X1距离", currTarget:"当前目标计数", carLength:"车辆长度"}]
     * @param data
     */
    private JSONArray dataHandler(String data){
        //目标检测数据
        JSONArray jsonArray = new JSONArray();
        //数据时间戳【仅参考】
        String ymdhms = data.substring(0, 12);
        //long timestamp = RadarUtils.hexToLong(data.substring(12, 28));//因雷达固件不稳定，偶发数值过大的bug
        long timestamp = RadarUtils.hexToLong(data.substring(13, 28));
        //72~  len=80
        String targets = data.substring(72*2);
        //System.out.println(targets.length()*1.0/80.0);
        if(targets.length() % (80*2) == 0){
            int circularNum = targets.length()/(80*2);
            //System.out.println("数据长度：" + targets.length() + "\n数据个数：" + circularNum);
            for(int i = 0; i < circularNum; i++){
                int tarStart = i*80*2;
                int tarEnd = tarStart + 80*2;
                String target = targets.substring(tarStart, tarEnd);
                //System.out.println(target);
                JSONObject obj = new JSONObject();
                obj.putOnce("targetId", RadarUtils.hexToInt(target.substring(0, 2))); //目标ID
                obj.putOnce("sX", RadarUtils.hexToFloat(target.substring(2, 10))); //x坐标
                obj.putOnce("sY", RadarUtils.hexToFloat(target.substring(10, 18))); //y坐标
                obj.putOnce("vX", RadarUtils.hexToFloat(target.substring(18, 26))); //x速度
                obj.putOnce("vY", RadarUtils.hexToFloat(target.substring(26, 34))); //y速度
                obj.putOnce("aX", RadarUtils.hexToFloat(target.substring(34, 42))); //x加速度
                obj.putOnce("aY", RadarUtils.hexToFloat(target.substring(42, 50))); //y加速度
                obj.putOnce("laneNum", RadarUtils.hexToInt(target.substring(50, 52))); //车道号
                obj.putOnce("carType", RadarUtils.hexToInt(target.substring(52, 54))); //车辆类型
                obj.putOnce("event", RadarUtils.hexToInt(target.substring(54, 56))); //事件类型
                //obj.putOnce("snowId", target.substring(56, 72)); //雪花ID
                //obj.putOnce("lng", target.substring(72, 80)); //经度
                //obj.putOnce("lat", target.substring(80, 88)); //维度
                //obj.putOnce("x1", target.substring(88, 96)); //X1距离
                //obj.putOnce("currTarget", target.substring(96, 100)); //当前目标计数
                obj.putOnce("carLength", RadarUtils.hexToInt(target.substring(100, 102)));//车辆长度
                //保留字29*2=58
                jsonArray.add(obj);
            }
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////
            RadarDO radarDO = new RadarDO();
            radarDO.setDate(Calendar.getInstance().getTime());
            radarDO.setNanoSecond(System.nanoTime());
            radarDO.setDataBody(jsonArray);
            radarDO.setYmdhms(ymdhms);
            radarDO.setTimestamp(timestamp);
            //System.out.println(sdf.format(timestamp));
            //System.out.println("缓存数目：" + arrayBlockingQueue.size());
            //System.out.println(jsonArray.toString());
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////
        }
        return jsonArray;
    }

    /**
     * 统计数据解析
     * @param data
     */
    private JSONArray tongJiDataHandle(String data){
        //目标检测数据
        JSONArray jsonArray = new JSONArray();
        //数据时间戳【仅参考】
        String timestamp = data.substring(0, 12);
        int SectionNumber = RadarUtils.hexToInt(data.substring(16, 18));
        int SectionLocation = RadarUtils.hexToInt(data.substring(18, 20));
        //37~  len=32
        String targets = data.substring(32*2);
        //System.out.println(targets.length()*1.0/80.0);
        if(targets.length() % (37*2) == 0){
            int circularNum = targets.length()/(37*2);
            //System.out.println("数据长度：" + targets.length() + "\n数据个数：" + circularNum);
            for(int i = 0; i < circularNum; i++){
                int tarStart = i*37*2;
                int tarEnd = tarStart + 37*2;
                String target = targets.substring(tarStart, tarEnd);
                //System.out.println(target);
                JSONObject obj = new JSONObject();
                // 保留字0~35，车道号36~37，平均车速38~39，车道占有率40~41，车头时距42~44，车流量46~49，排队长度50~53，
                // 小型车流量54~57，大型车流量58~61，中型车流量62~65，车头间距66~69，车道空间占有率70~71，保留72~73
                obj.putOnce("lane", RadarUtils.hexToInt(target.substring(36, 38))); //车道号
                obj.putOnce("averageSpeed", RadarUtils.hexToInt(target.substring(38, 40))); //平均车速
                obj.putOnce("occupancy", RadarUtils.hexToInt(target.substring(40, 42))); //车道占有率
                obj.putOnce("headWay", RadarUtils.hexToInt(target.substring(42, 46))); //车头时距
                obj.putOnce("flow", RadarUtils.hexToInt(target.substring(46, 50))); //车流量
                obj.putOnce("queueLength", RadarUtils.hexToInt(target.substring(50, 54))); //排队长度
                obj.putOnce("sumMini", RadarUtils.hexToInt(target.substring(54, 58))); //小型车流量
                obj.putOnce("sumLarge", RadarUtils.hexToInt(target.substring(58, 62))); //大型车流量
                obj.putOnce("sumMid", RadarUtils.hexToInt(target.substring(62, 66))); //中型车流量
                obj.putOnce("headWay2", RadarUtils.hexToInt(target.substring(66, 70))); //车头间距
                obj.putOnce("Occupancy2", RadarUtils.hexToInt(target.substring(70, 72))); //车道空间占有率
                jsonArray.add(obj);
            }
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////
            RadarDO radarDO = new RadarDO();
            radarDO.setDate(new Date());
            radarDO.setDataBody(jsonArray);
            if(RadarUtils.RUN_IN_DEBUG){
                //System.out.println("统计数据：" + timestamp + " " + SectionNumber + " " + SectionLocation + " " + jsonArray.toString());
                //System.out.println("统计数据：" + data);
            }
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////
        }
        return jsonArray;
    }


}