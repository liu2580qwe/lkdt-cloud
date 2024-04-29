package org.lkdt.modules.radar.supports.radarTools.shell;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import org.lkdt.common.util.StringUtils;
import org.lkdt.modules.radar.supports.radarDataService.dataCache.DO.RadarLaneCalcDO;
import org.lkdt.modules.radar.supports.radarDataService.observers.DO.RadarObjDO;
import org.lkdt.modules.radar.supports.radarTools.DO.RadarDO;
import org.jfree.data.category.DefaultCategoryDataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * 新算法计算雷达车流量
 */
public class CalcRadarDataTrafficFlowThreeStatus {

    private static Logger logger = LoggerFactory.getLogger(CalcRadarDataTrafficFlowThreeStatus.class);

    public static final String url = "jdbc:mysql://192.168.3.100:3306/radar_new?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8&useSSL=false" +
            "&zeroDateTimeconvertToNull&useCursorFetch=true&defaultFetchSize=1000";

    public static final String user = "root";
    public static final String password = "root";

    public static class RoadFlowData {
        public String radarId;

        //交通流自由态阈值 交通流高风险干扰态阈值 交通流中风险干扰态阈值 交通流中风险排队态阈值 交通流低风险排队态阈值
        public volatile Integer roadFlowRiskValue;

        public volatile String roadFlowRiskLabel;

        public volatile String congestionIndex;

        /**闸门控制阀*/
        public volatile boolean timeSwitch = true;

        /**蓄水池1*/
        public ArrayBlockingQueue<List<RadarObjDO>> arrayQTrue = new ArrayBlockingQueue<>(900);

        /**蓄水池2*/
        public ArrayBlockingQueue<List<RadarObjDO>> arrayQFalse = new ArrayBlockingQueue<>(900);
    }

    //交通流数据
    public volatile static Map<String, RoadFlowData> roadFlowDataMap = new HashMap<>();

    static {
        //数据初始化
        RoadFlowData roadFlowData = new RoadFlowData();
        roadFlowData.radarId = "1006";
        roadFlowDataMap.put(roadFlowData.radarId, roadFlowData);
    }

    public static void main(String[] args) {
        Connection connection = null ;
        PreparedStatement preparedStatement = null ;

        String sql = "SELECT * FROM 20210731zc_ld_radar_frame_target_all WHERE date_time >= '2021-07-02 00:00:00' " +
                "AND date_time < '2021-07-02 01:00:00' AND radar_id = '1006' order by date_time";

        //定义折线图数据集
        DefaultCategoryDataset dataSetCarFlowLai = new DefaultCategoryDataset();

        Thread thread = new Thread(() -> {
            while(true){
                try {
                    Thread.sleep(60*1000);
                    threeStatusCalc();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

        try{
            //加载注册驱动
            Class.forName("com.mysql.jdbc.Driver");
            //获取连接
            connection = DriverManager.getConnection(url, user, password);
            connection.setAutoCommit(false);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            long start = System.currentTimeMillis();
            //读数据
            preparedStatement = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            ResultSet resultSet = preparedStatement.executeQuery();

            int count = 0;
            Date tempDate = null;
            RadarDO radarDO = null;
            while (resultSet.next()) {
                String carId = resultSet.getString("car_id");
                String radarId = resultSet.getString("radar_id");
                int targetId = resultSet.getInt("target_id");
                double sX = resultSet.getDouble("s_x");
                double sY = resultSet.getDouble("s_y");
                double vX = resultSet.getDouble("v_x");
                double vY = resultSet.getDouble("v_y");
                double aX = resultSet.getDouble("a_x");
                double aY = resultSet.getDouble("a_y");
                int laneNum = resultSet.getInt("lane_num");
                int carType = resultSet.getInt("car_type");
                int event = resultSet.getInt("event");
                int carLength = resultSet.getInt("car_length");
                Date dateTime = new Date(resultSet.getTimestamp("date_time").getTime());
                long nanoSecond = resultSet.getLong("nano_second");
                //第一条数据
                if(tempDate == null || tempDate.compareTo(dateTime) != 0){
                    if(tempDate != null && tempDate.compareTo(dateTime) != 0){
                        executeTest(radarDO);
                    }
                    radarDO = new RadarDO();
                    JSONArray jsonArray = new JSONArray();
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("carId", carId);
                    jsonObject.put("radarId", radarId);
                    jsonObject.put("targetId", targetId);
                    jsonObject.put("sX", sX);
                    jsonObject.put("sY", sY);
                    jsonObject.put("vX", vX);
                    jsonObject.put("vY", vY);
                    jsonObject.put("aX", aX);
                    jsonObject.put("aY", aY);
                    jsonObject.put("laneNum", laneNum);
                    jsonObject.put("carType", carType);
                    jsonObject.put("event", event);
                    jsonObject.put("carLength", carLength);
                    jsonObject.put("carId", carId);
                    jsonObject.put("nanoSecond", nanoSecond);
                    tempDate = dateTime;
                    radarDO.setDate(tempDate);
                    radarDO.setNanoSecond(nanoSecond);
                    radarDO.setTimestamp(tempDate.getTime());
                    radarDO.setYmdhms(null);
                    radarDO.setDataBody(jsonArray);
                    radarDO.getDataBody().put(jsonObject);
                    //模拟雷达数据
                    Thread.sleep(71);

                } else {//同一帧数据
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("carId", carId);
                    jsonObject.put("radarId", radarId);
                    jsonObject.put("targetId", targetId);
                    jsonObject.put("sX", sX);
                    jsonObject.put("sY", sY);
                    jsonObject.put("vX", vX);
                    jsonObject.put("vY", vY);
                    jsonObject.put("aX", aX);
                    jsonObject.put("aY", aY);
                    jsonObject.put("laneNum", laneNum);
                    jsonObject.put("carType", carType);
                    jsonObject.put("event", event);
                    jsonObject.put("carLength", carLength);
                    jsonObject.put("carId", carId);
                    jsonObject.put("nanoSecond", nanoSecond);
                    radarDO.getDataBody().put(jsonObject);
                }

                //System.out.println(++count);
            }
            executeTest(radarDO);
            long end = System.currentTimeMillis();
            System.out.println("耗时：" + (end - start));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //释放资源
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static int countFrame = 0;
    private static void executeTest(RadarDO radarDO){
        //System.out.println("radarDO" + ++countFrame);
        //道路交通流三态数据读取
        laneLiveData(radarDO);
    }

    /**
     * 定时任务，计算三态数据
     */
    public static void threeStatusCalc() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        for(Map.Entry<String, RoadFlowData> $map: roadFlowDataMap.entrySet()){
            RoadFlowData roadFlowData = $map.getValue();
            //切换蓄水池
            roadFlowData.timeSwitch = !roadFlowData.timeSwitch;

            ArrayBlockingQueue<List<RadarObjDO>> current = null;

            //获取出水口
            if(roadFlowData.timeSwitch){
                //arrayQFalse
                current = roadFlowData.arrayQFalse;
            } else {
                //arrayQTrue
                current = roadFlowData.arrayQTrue;
            }

            List<Float> vBigList = new ArrayList<>();
            List<Float> vSmallList = new ArrayList<>();
            double mBigSum = 0;
            double nSmallSum = 0;
            float vLimitSmall = 120;
            float vLimitBig = 90;

            Map<Integer, RadarObjDO> persistFrame = new HashMap<>();
            int num = 0;
            while(true){
                List<RadarObjDO> list = null;
                if(current != null){
                    list = current.poll();
                }
                if(list != null){
                    if(num == 0){
                        //首批数据
                        for(RadarObjDO radarObjDO: list){
                            persistFrame.put(radarObjDO.getTargetId(), radarObjDO);
                        }
                    } else {
                        List<Integer> targetIds = new ArrayList<>();
                        for(RadarObjDO radarObjDO: list){
                            //更新永久池数据
                            persistFrame.put(radarObjDO.getTargetId(), radarObjDO);
                            targetIds.add(radarObjDO.getTargetId());
                        }
                        List<Integer> removedList = new ArrayList<>();
                        //数据分析
                        for(Map.Entry<Integer, RadarObjDO> map: persistFrame.entrySet()){
                            if(!targetIds.contains(map.getKey())){
                                removedList.add(map.getKey());
                                //车辆计数
                                RadarObjDO radarObjDO = map.getValue();
                                //1小型车，2大型车，3中型车
                                int carType = radarObjDO.getCarType();
                                float vY = Math.abs(radarObjDO.getvY());
                                if(carType == 1){
                                    vSmallList.add(vY);
                                    float vY_ = vY/vLimitSmall;
                                    nSmallSum += vY_ > 1? 1: vY_;
                                } else {
                                    vBigList.add(vY);
                                    float vY_ = vY/vLimitBig;
                                    mBigSum += vY_ > 1? 1: vY_;
                                }
                            }
                        }
                        //删除冗余数据
                        for(Integer i: removedList){
                            persistFrame.remove(i);
                        }
                    }
                    num++;
                } else {
                    //退出
                    break;
                }
            }

            //道路交通流三态算法计算
            float pV = 0;
            float vV = 0;
            //当前能见度值
            Integer distance = 500;
            if(distance == null || distance > 200){
                int nSmall = vSmallList.size();
                int mBig = vBigList.size();


                if(nSmall + mBig == 0){
                    pV = 1;
                } else {
//                    double pXiao = nSmallSum/vLimitSmall;
//                    double pDa = mBigSum/vLimitBig;
                    double pXiao = nSmallSum;
                    double pDa = mBigSum;
                    pV = (float) ((pXiao + pDa) / (nSmall + mBig));

                    if(nSmall + mBig == 0){
                        vV = 0;
                    } else {
                        vV = (float) ((nSmallSum + mBigSum) / (nSmall + mBig));
                    }
                }

                if(pV >= 0.83){
                    //交通流自由态阈值
                    roadFlowData.roadFlowRiskValue = 1;//1:自由态,2:高风险干扰态,3:中风险干扰态,4:中风险排队态,5:低风险排队
                    roadFlowData.roadFlowRiskLabel = "自由态";
                    roadFlowData.congestionIndex = "Ⅰ"; //ⅠⅡ Ⅲ Ⅳ
                } else if(pV >= 0.58){
                    //交通流高风险干扰态阈值
                    roadFlowData.roadFlowRiskValue = 2;
                    roadFlowData.roadFlowRiskLabel = "高风险干扰态";
                    roadFlowData.congestionIndex = "Ⅱ";
                } else if(pV >= 0.25){
                    //交通流中风险干扰态阈值
                    roadFlowData.roadFlowRiskValue = 3;
                    roadFlowData.roadFlowRiskLabel = "中风险干扰态";
                    roadFlowData.congestionIndex = "Ⅲ";
                } else {
                    if(vV > 10){
                        //交通流中风险排队态阈值
                        roadFlowData.roadFlowRiskValue = 4;
                        roadFlowData.roadFlowRiskLabel = "中风险排队态";
                    } else {
                        //交通流低风险排队态阈值
                        roadFlowData.roadFlowRiskValue = 5;
                        roadFlowData.roadFlowRiskLabel = "低风险排队";
                    }
                    roadFlowData.congestionIndex = "Ⅳ";

                }

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("unitId", "xxxxxx");
                jsonObject.put("radarId", roadFlowData.radarId);
                jsonObject.put("dataTime", sdf.format(new Date()));
                jsonObject.put("roadFlowRiskValue", roadFlowData.roadFlowRiskValue);//1:自由态,2:高风险干扰态,3:中风险干扰态,4:中风险排队态,5:低风险排队
                jsonObject.put("roadFlowRiskLabel", roadFlowData.roadFlowRiskLabel);
                jsonObject.put("congestionIndex", roadFlowData.congestionIndex);//ⅠⅡ Ⅲ Ⅳ
                jsonObject.put("pV", pV);//态势阈值
                jsonObject.put("distance", distance);
                jsonObject.put("smallNum", vSmallList.size());
                jsonObject.put("bigNum", vBigList.size());
                jsonObject.put("vLimitSmall", vLimitSmall);
                jsonObject.put("vLimitBig", vLimitBig);
                logger.error("【道路交通流三态算法计算】{}", jsonObject.toString());
            }

        }
    }

    /**
     * 解析车道瞬时数据
     */
    public static List<RadarLaneCalcDO> laneLiveData(RadarDO radarDO){
        JSONArray jsonArray = radarDO.getDataBody();
        //按车道划分
        Map<Integer, List<RadarObjDO>> map = new HashMap<>();
        //解析雷达数据，按车道区分
        ///////////////////////start
        //////////////////////////////////////////////
        for(int i = 0; i < jsonArray.size(); i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            //每辆车解析
            RadarObjDO radarObjDO = new RadarObjDO();
            readData(radarObjDO, jsonObject);

            if(map.get(radarObjDO.getLaneNum()) == null){
                List<RadarObjDO> list = new ArrayList<>();
                list.add(radarObjDO);
                map.put(radarObjDO.getLaneNum(), list);
            } else {
                map.get(radarObjDO.getLaneNum()).add(radarObjDO);
            }

        }
        ///////////////////////end
        //////////////////////////////////////////////

        List<RadarLaneCalcDO> radarLaneCalcDOList = new ArrayList<>();

        Map<Integer, String> zcLdLaneMap = new HashMap<>();
        zcLdLaneMap.put(1, "R-C");
        zcLdLaneMap.put(2, "R-2");
        zcLdLaneMap.put(3, "R-YJ");
        //车道排序:小->大
        for(HashMap.Entry<Integer, List<RadarObjDO>> entry: map.entrySet()){

            //排序【目的：计算平均时距】
            Collections.sort(entry.getValue());

            //道路交通流三态数据读取
            try {
                String data = zcLdLaneMap.get(entry.getKey());
                if(StringUtils.isNotEmpty(data) && data.endsWith("-C")){
                    RoadFlowData roadFlowData = roadFlowDataMap.get("1006");
                    if(roadFlowData != null){
                        if(roadFlowData.timeSwitch) {
                            roadFlowData.arrayQTrue.offer(entry.getValue());
                        } else {
                            roadFlowData.arrayQFalse.offer(entry.getValue());
                        }

                    }
                }
            } catch (Exception e) {
                logger.error("道路交通流三态数据读取", e);
            }

        }

        return radarLaneCalcDOList;
    }

    protected static void readData(RadarObjDO radarObjDO, JSONObject obj){
        int targetId = obj.getInt("targetId");//目标ID
        float sX = obj.getFloat("sX");//x坐标
        float sY = obj.getFloat("sY");//y坐标
        float vX = obj.getFloat("vX");//x速度
        float vY = obj.getFloat("vY");//y速度
        float aX = obj.getFloat("aX");//x加速度
        float aY = obj.getFloat("aY");//y加速度
        int laneNum = obj.getInt("laneNum");//车道号
        //1 小型车，2 大型车，3 超大型车
        int carType = obj.getInt("carType");//车辆类型
        //0 无事件 1 车辆停止 2 车辆反方向行驶 （逆行） 3 车辆超速行驶 5 车辆实线变道
        //101 车辆停止消失 102 车辆反方向行 驶消失 103 车辆超速行驶 消失105 车辆实线变道 消失
        int event = obj.getInt("event");//事件类型
        int carLength = obj.getInt("carLength");//车辆长度
        radarObjDO.setTargetId(targetId).setsX(sX).setsY(sY).setvX(vX).setvY(vY).setaX(aX).setaY(aY)
                .setLaneNum(laneNum).setCarType(carType).setEvent(event).setCarLength(carLength);
    }

}
