package org.lkdt.modules.radar.supports.radarDataHandle;

import org.lkdt.modules.radar.supports.radarDataHandle.extend.RadarClientV2;
import org.lkdt.modules.radar.supports.radarDataService.DataReader;
import org.lkdt.modules.radar.supports.radarTools.DO.RadarDO;
import org.lkdt.modules.radar.supports.radarTools.RadarUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 事件雷达数据缓存
 */
public class RadarEventDataGrab {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    /**数据解析标记*/
    private volatile boolean originDataHandleFlag = true;
    /**雷达有效范围（米） 缺省值*/
    private Integer RANGE_SIZE = 50;
    /**雷达有效范围最大值 缺省值*/
    private Integer RANGE_HIGH = 170;
    /**雷达有效范围最小值 缺省值*/
    private Integer RANGE_LOW = 120;
    /**雷达数据全局缓存队列*/
    public static final ArrayBlockingQueue<RadarDO> COMMON_ArrayBlockingQueue = new ArrayBlockingQueue<>(RadarUtils.RadarEventSize * 7);
    public static ThreadPoolExecutor executor = new ThreadPoolExecutor(
            1,
            7,
            Long.MAX_VALUE, /* timeout */
            TimeUnit.NANOSECONDS,
            new PriorityBlockingQueue<Runnable>(),
            new ThreadPoolExecutor.DiscardOldestPolicy());
    /**执行线程池*/
    //public ExecutorService executors = Executors.newSingleThreadExecutor();
    /**雷达数据缓存队列*/
    public volatile ArrayBlockingQueue<RadarDO> arrayBlockingQueue = new ArrayBlockingQueue<>(RadarUtils.RadarEventSize);
    /**测试用：实时数据*/
    public volatile ArrayBlockingQueue<RadarDO> liveQueue = new ArrayBlockingQueue<>(10);
    /**测试用：实时统计数据*/
    public volatile ArrayBlockingQueue<RadarDO> liveTJQueue = new ArrayBlockingQueue<>(10);
    /**瞬时数据*/
    private volatile RadarDO momentRadarDO;
    /**瞬时处理数据*/
    private volatile RadarDO momentHandleRadarDO;
    private volatile Thread thread;
    private RadarClientV2 radarClient;
    private String radarChannelLongId;
    /**线程处理开关*/
    private volatile boolean threadHandleFlag = true;
    /**数据处理线程*/
    private volatile Thread dataHandleThread;
    /**车道信息*/
    volatile Map<Integer, String> zcLdLaneMap;
    //事件雷达数据实时统计
    public volatile DataReader dataReader = null;
    private volatile Socket socket;
    private volatile OutputStream outputStream;
    private volatile InputStream inputStream;
    private String radarId = "1001";
    private String host = "10.100.0.12";
    private int port = 5001;
    private volatile long timeOldClock = 0;
    /**上次最新数据时间戳*/
    public volatile long timeClock = 0;
    /**上次心跳时间*/
    public volatile long heartBeatPacketClock = 0;

    public void destroy() {
        this.originDataHandleFlag = false;
    }

    public Integer getRANGE_SIZE() { return RANGE_SIZE; }
    public Integer getRANGE_HIGH() { return RANGE_HIGH; }
    public Integer getRANGE_LOW() { return RANGE_LOW; }

    public RadarClientV2 getRadarClient() {
        return radarClient;
    }

    public void setRadarClient(RadarClientV2 radarClient) {
        this.radarClient = radarClient;
    }

    public String getRadarChannelLongId() {
        return radarChannelLongId;
    }

    public void setRadarChannelLongId(String radarChannelLongId) {
        this.radarChannelLongId = radarChannelLongId;
    }

    public ArrayBlockingQueue getArrayBlockingQueue() {
        return arrayBlockingQueue;
    }

    public ArrayBlockingQueue<RadarDO> getLiveQueue() {
        return liveQueue;
    }

    public ArrayBlockingQueue<RadarDO> getLiveTJQueue() {
        return liveTJQueue;
    }

    public RadarDO getMomentRadarDO() {
        return momentRadarDO;
    }

    public void setMomentRadarDO(RadarDO momentRadarDO) {
        this.momentRadarDO = momentRadarDO;
    }

    public RadarDO getMomentHandleRadarDO() {
        RadarDO temp = this.momentHandleRadarDO;
        this.momentHandleRadarDO = null;
        return temp;
    }

    public void setMomentHandleRadarDO(RadarDO momentHandleRadarDO) {
        this.momentHandleRadarDO = momentHandleRadarDO;
    }

    protected Thread getThread() {
        return thread;
    }

    public boolean getThreadHandleFlag() {
        return threadHandleFlag;
    }

    protected synchronized void setThreadHandleFlag(boolean threadHandleFlag) {
        this.threadHandleFlag = threadHandleFlag;
    }

    protected Thread getDataHandleThread() {
        return dataHandleThread;
    }

    public void setDataHandleThread(Thread dataHandleThread) {
        this.dataHandleThread = dataHandleThread;
    }

    public Map<Integer, String> getZcLdLaneMap() {
        return zcLdLaneMap;
    }

    public void setZcLdLaneMap(Map<Integer, String> zcLdLaneMap) {
        this.zcLdLaneMap = zcLdLaneMap;
    }

    /**
     * 返回“L”或“R”
     * @param laneNum
     * @return L/R
     */
    public String searchZcLdLane(Integer laneNum){
        String laneRoad = zcLdLaneMap.get(laneNum);
        if(laneRoad == null){
            return null;
        }
        if(laneRoad.startsWith("L-")){
            return "L";
        }
        if(laneRoad.startsWith("R-")){
            return "R";
        }
        return null;
    }

    /**
     * 返回车道信息
     * @param laneNum
     * @return 车道信息：L-C、L-2、L-YJ、R-C、R-2、R-YJ
     */
    public String searchZcLdLaneRoad(Integer laneNum){
        return zcLdLaneMap.get(laneNum);
    }

    protected Socket getSocket() { return socket; }
    protected OutputStream getOutputStream() { return outputStream; }
    protected InputStream getInputStream() { return inputStream; }
    protected String getHost() { return host; }
    protected int getPort() { return port; }
    protected long getTimeClock() { return timeClock; }
    protected long getTimeOldClock() { return timeOldClock; }
    protected void setTimeOldClock(long timeOldClock) { this.timeOldClock = timeOldClock; }
    public long getHeartBeatPacketClock() {return heartBeatPacketClock;}
    public void setHeartBeatPacketClock(long heartBeatPacketClock) {this.heartBeatPacketClock = heartBeatPacketClock;}

    public String getRadarId() { return radarId; }

    public RadarEventDataGrab(String host, int port, String radarId, Integer RANGE_HIGH, Integer RANGE_LOW){
        this.host = host;
        this.port = port;
        this.radarId = radarId;
        if(RANGE_HIGH != null && RANGE_LOW != null){
            this.RANGE_HIGH = RANGE_HIGH;
            this.RANGE_LOW = RANGE_LOW;
            this.RANGE_SIZE = RANGE_HIGH - RANGE_LOW;
        }
    }

    private RadarEventDataGrab(){

    }

    protected RadarEventDataGrab run(String name){
        radarClient = new RadarClientV2(host, port, this);
        radarClient.connect();
        return this;
    }

//    @Deprecated
//    protected RadarEventDataGrab run2(String name){
//        thread = new Thread(()->{
//            try {
//                runData();
//            } catch (ConnectException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }, name);
//        thread.start();
//        return this;
//    }
//
//    /**
//     * 命令发送
//     * @param outPutStream
//     * @param i
//     *      1:接收实时计算和统计数据
//     *      2:接收实时数据
//     * @throws IOException
//     */
//    @Deprecated
//    protected void sentCMD(OutputStream outPutStream, int i) throws IOException {
//        //接收实时计算和统计数据
//        if(i == 1){
//            String capm = "43 41 50 4d 00 00 00 04 00 c8 00 04";
//            outputStream.write(RadarUtils.hexStr2Bytes(capm.replace(" ", "")));
//            outputStream.flush();
//        } else if(i == 2){
//            //接收实时数据
//            String capm = "43 41 50 4d 00 00 00 04 00 c8 00 01";
//            outputStream.write(RadarUtils.hexStr2Bytes(capm.replace(" ", "")));
//            outputStream.flush();
//        }
//    }
//
//    @Deprecated
//    protected void runData() throws IOException {
//
////        String INIT = "INIT";
////        byte[] INIT_ = INIT.getBytes();
//        logger.info("正在连接主机：" + host + "，端口号：" + port);
//        try{
//            socket = new Socket(host, port);
//
//            logger.info("正在连接主机：" + host + "，端口号：" + port + "【成功】");
//
//            //客户端输出
//            outputStream = socket.getOutputStream();
//    //        outputStream.write(INIT_);
//    //        outputStream.flush();
//            sentCMD(outputStream, 1);
//
//            inputStream = socket.getInputStream();
//    //        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
//    //        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//            while (originDataHandleFlag){
//                byte[] bytes = new byte[4096];
//                int n = 0;
//                while((n = inputStream.read(bytes)) != -1){
//                    String frameHead = "" + (char)bytes[0] + (char)bytes[1] + (char)bytes[2] + (char)bytes[3];
//
//                    //实时数据
//                    if("TRAJ".equalsIgnoreCase(frameHead)){
//                        //System.out.println("***" + frameHead + "***");
//                        int frameLength1 = bytes[4] & 0xFF;
//                        int frameLength2 = bytes[5] & 0xFF;
//                        int frameLength3 = bytes[6] & 0xFF;
//                        int frameLength4 = bytes[7] & 0xFF;
//                        int frameLength = 8 + frameLength1 * (int)(Math.pow(16, 6)) + frameLength2 * (int)(Math.pow(16, 4)) +
//                                frameLength3 * (int)(Math.pow(16, 2)) + frameLength4;
//
//                        String traj_str = RadarUtils.byteToHexStr(bytes);
//                        //System.out.println(traj_str);
//
//                        //校验位, 2个字节 4个字母
//                        traj_str = traj_str.substring(0, frameLength*2);
//                        //System.out.println(traj_str);
//                        int len = traj_str.length();
//                        String crc16_str = traj_str.substring(len - 4, len);
//                        String dataBody = traj_str.substring(8*2, traj_str.length() - 4);
//                        String crc16_coputed = RadarUtils.crcVerify(dataBody);
//                        if(StringUtils.equalsIgnoreCase(crc16_str, crc16_coputed)){
//                            //System.out.println("验证通过");
//                            //数据解析72~
//                            dataHandler(dataBody);
//                        }
//                    } else if("TFCF".equalsIgnoreCase(frameHead)){
//                        //统计数据
//                        //System.out.println("***" + frameHead + "***");
//                        int frameLength1 = bytes[4] & 0xFF;
//                        int frameLength2 = bytes[5] & 0xFF;
//                        int frameLength3 = bytes[6] & 0xFF;
//                        int frameLength4 = bytes[7] & 0xFF;
//                        int frameLength = 8 + frameLength1 * (int)(Math.pow(16, 6)) + frameLength2 * (int)(Math.pow(16, 4)) +
//                                frameLength3 * (int)(Math.pow(16, 2)) + frameLength4;
//
//                        String tfcf_str = RadarUtils.byteToHexStr(bytes);
//                        //System.out.println(tfcf_str);
//
//                        //校验位, 2个字节 4个字母
//                        tfcf_str = tfcf_str.substring(0, frameLength*2);
//                        //System.out.println(tfcf_str);
//                        int len = tfcf_str.length();
//                        String crc16_str = tfcf_str.substring(len - 4, len);
//                        //System.out.println(tfcf_str.substring(8*2+1, tfcf_str.length() - 4));
//                        String dataBody = tfcf_str.substring(8*2, tfcf_str.length() - 4);
//                        String crc16_coputed = RadarUtils.crcVerify(dataBody);
//                        if(StringUtils.equalsIgnoreCase(crc16_str, crc16_coputed)){
//                            //System.out.println("统计数据【待处理】：" + tfcf_str);
//                            tongJiDataHandle(dataBody);
//                        }
//                    } else if("#KKA".equalsIgnoreCase(frameHead)){
//                        //接收心跳包
//                        this.setHeartBeatPacketClock(new Date().getTime());
//                    }
//                }
//                try {
//                    Thread.sleep(10);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        } catch (Exception e) {
//            logger.error("java.net.ConnectException", "Connection timed out: connect");
//        } finally {
//            if(inputStream != null){
//                try {
//                    inputStream.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            if(outputStream != null){
//                try {
//                    outputStream.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//    /**
//     * 数据解析
//     * [{targetId:"目标ID",sX:"x坐标", sY:"y坐标", vX:"x速度", vY:"y速度", aX:"x加速度", aY:"y加速度", laneNum:"车道号", carType:"车辆类型",
//     * event:"事件类型", snowId:"雪花ID", lng:"经度", lat:"维度", x1:"X1距离", currTarget:"当前目标计数", carLength:"车辆长度"}]
//     * @param data
//     */
//    @Deprecated
//    private void dataHandler(String data){
//        //目标检测数据
//        JSONArray jsonArray = new JSONArray();
//        //数据时间戳【仅参考】
//        String ymdhms = data.substring(0, 6);
//        long timestamp = RadarUtils.hexToLong(data.substring(6, 14));
//        //72~  len=80
//        String targets = data.substring(72*2);
//        //System.out.println(targets.length()*1.0/80.0);
//        if(targets.length() % (80*2) == 0){
//            int circularNum = targets.length()/(80*2);
//            //System.out.println("数据长度：" + targets.length() + "\n数据个数：" + circularNum);
//            for(int i = 0; i < circularNum; i++){
//                int tarStart = i*80*2;
//                int tarEnd = tarStart + 80*2;
//                String target = targets.substring(tarStart, tarEnd);
//                //System.out.println(target);
//                JSONObject obj = new JSONObject();
//                obj.putOnce("targetId", RadarUtils.hexToInt(target.substring(0, 2))); //目标ID
//                obj.putOnce("sX", RadarUtils.hexToFloat(target.substring(2, 10))); //x坐标
//                obj.putOnce("sY", RadarUtils.hexToFloat(target.substring(10, 18))); //y坐标
//                obj.putOnce("vX", RadarUtils.hexToFloat(target.substring(18, 26))); //x速度
//                obj.putOnce("vY", RadarUtils.hexToFloat(target.substring(26, 34))); //y速度
//                obj.putOnce("aX", RadarUtils.hexToFloat(target.substring(34, 42))); //x加速度
//                obj.putOnce("aY", RadarUtils.hexToFloat(target.substring(42, 50))); //y加速度
//                obj.putOnce("laneNum", RadarUtils.hexToInt(target.substring(50, 52))); //车道号
//                obj.putOnce("carType", RadarUtils.hexToInt(target.substring(52, 54))); //车辆类型
//                obj.putOnce("event", RadarUtils.hexToInt(target.substring(54, 56))); //事件类型
//                //obj.putOnce("snowId", target.substring(56, 72)); //雪花ID
//                //obj.putOnce("lng", target.substring(72, 80)); //经度
//                //obj.putOnce("lat", target.substring(80, 88)); //维度
//                //obj.putOnce("x1", target.substring(88, 96)); //X1距离
//                //obj.putOnce("currTarget", target.substring(96, 100)); //当前目标计数
//                obj.putOnce("carLength", RadarUtils.hexToInt(target.substring(100, 102)));//车辆长度
//                //保留字29*2=58
//                jsonArray.add(obj);
//            }
//            /////////////////////////////////////////////////////////////////////////////////////////////////////////////
//            /////////////////////////////////////////////////////////////////////////////////////////////////////////////
//            RadarDO radarDO = new RadarDO();
//            radarDO.setDate(Calendar.getInstance().getTime());
//            radarDO.setNanoSecond(System.nanoTime());
//            radarDO.setDataBody(jsonArray);
//            radarDO.setYmdhms(ymdhms);
//            radarDO.setTimestamp(timestamp);
//            arrayBlockingQueue.offer(radarDO);
//            this.setMomentRadarDO(radarDO);
//            timeClock = new Date().getTime();
//            //System.out.println("缓存数目：" + arrayBlockingQueue.size());
//            //System.out.println(jsonArray.toString());
//            /////////////////////////////////////////////////////////////////////////////////////////////////////////////
//            /////////////////////////////////////////////////////////////////////////////////////////////////////////////
//        }
//
//    }
//
//    /**
//     * 统计数据解析
//     * @param data
//     */
//    @Deprecated
//    private void tongJiDataHandle(String data){
//        //目标检测数据
//        JSONArray jsonArray = new JSONArray();
//        //数据时间戳【仅参考】
//        String timestamp = data.substring(0, 12);
//        int SectionNumber = RadarUtils.hexToInt(data.substring(16, 18));
//        int SectionLocation = RadarUtils.hexToInt(data.substring(18, 20));
//        //37~  len=32
//        String targets = data.substring(32*2);
//        //System.out.println(targets.length()*1.0/80.0);
//        if(targets.length() % (37*2) == 0){
//            int circularNum = targets.length()/(37*2);
//            //System.out.println("数据长度：" + targets.length() + "\n数据个数：" + circularNum);
//            for(int i = 0; i < circularNum; i++){
//                int tarStart = i*37*2;
//                int tarEnd = tarStart + 37*2;
//                String target = targets.substring(tarStart, tarEnd);
//                //System.out.println(target);
//                JSONObject obj = new JSONObject();
//                // 保留字0~35，车道号36~37，平均车速38~39，车道占有率40~41，车头时距42~44，车流量46~49，排队长度50~53，
//                // 小型车流量54~57，大型车流量58~61，中型车流量62~65，车头间距66~69，车道空间占有率70~71，保留72~73
//                obj.putOnce("lane", RadarUtils.hexToInt(target.substring(36, 38))); //车道号
//                obj.putOnce("averageSpeed", RadarUtils.hexToInt(target.substring(38, 40))); //平均车速
//                obj.putOnce("occupancy", RadarUtils.hexToInt(target.substring(40, 42))); //车道占有率
//                obj.putOnce("headWay", RadarUtils.hexToInt(target.substring(42, 46))); //车头时距
//                obj.putOnce("flow", RadarUtils.hexToInt(target.substring(46, 50))); //车流量
//                obj.putOnce("queueLength", RadarUtils.hexToInt(target.substring(50, 54))); //排队长度
//                obj.putOnce("sumMini", RadarUtils.hexToInt(target.substring(54, 58))); //小型车流量
//                obj.putOnce("sumLarge", RadarUtils.hexToInt(target.substring(58, 62))); //大型车流量
//                obj.putOnce("sumMid", RadarUtils.hexToInt(target.substring(62, 66))); //中型车流量
//                obj.putOnce("headWay2", RadarUtils.hexToInt(target.substring(66, 70))); //车头间距
//                obj.putOnce("Occupancy2", RadarUtils.hexToInt(target.substring(70, 72))); //车道空间占有率
//                jsonArray.add(obj);
//            }
//
//            /////////////////////////////////////////////////////////////////////////////////////////////////////////////
//            /////////////////////////////////////////////////////////////////////////////////////////////////////////////
//            RadarDO radarDO = new RadarDO();
//            radarDO.setDate(new Date());
//            radarDO.setDataBody(jsonArray);
//            //实时统计数据
//            liveTJQueue.offer(radarDO);
//            if(RadarUtils.RUN_IN_DEBUG){
//                //System.out.println("统计数据：" + timestamp + " " + SectionNumber + " " + SectionLocation + " " + jsonArray.toString());
//                //System.out.println("统计数据：" + data);
//            }
//            /////////////////////////////////////////////////////////////////////////////////////////////////////////////
//            /////////////////////////////////////////////////////////////////////////////////////////////////////////////
//        }
//    }




    //命令
    //INIT 网络连接
    //RSET 复位
    //GBYE 断开连接
    //SRCH 算法参数查询，参数详情参见附录2
    //SEPR 参数保存，将参数存入雷达硬件FLASH
    //SRNT 查询网络参数
    //RNTP 查询NTP参数
    //OPTP 开启NPT功能0：关闭，1：开启
    //RSID 设备ID查询
    //RSTM 设置设备时间
    //RRTM 查询设备时间
    //STMZ 时区设置
    //RTMZ 时区查询
    //RSLB 车道信息设置
    //RLOG 读取日志
    //帧头
    //TRAJ 实时航迹数据
    //TFCF 统计数据，最小单位1s
    //PNTP NTP参数
    //#KKA 心跳包
    //PLOG 雷达日志信息

    public static void main(String[] args){
        while(true){
            System.out.println(new Date().getTime());
        }
//        Thread thread = new Thread(()->{
//            try {
//                new RadarEventDataGrab().runData();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
//        thread.start();
//        Thread config = new Thread(() -> {
//
//        });
//        config.start();
    }
}
