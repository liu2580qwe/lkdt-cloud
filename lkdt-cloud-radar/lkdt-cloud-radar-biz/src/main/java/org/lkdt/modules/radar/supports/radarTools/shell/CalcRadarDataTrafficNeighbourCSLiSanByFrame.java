package org.lkdt.modules.radar.supports.radarTools.shell;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.google.api.client.util.ArrayMap;
import org.lkdt.common.util.StringUtils;
import org.lkdt.modules.radar.entity.ZcLdRadarFrameCar;
import org.lkdt.modules.radar.supports.radarDataService.observers.DO.RadarObjDO;
import org.lkdt.modules.radar.supports.radarTools.DO.RadarDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;

/**
 * RSD 为风险性车速离散度
 * 用前后左右车速度差计算 离散度
 */
public class CalcRadarDataTrafficNeighbourCSLiSanByFrame {

    private static Logger logger = LoggerFactory.getLogger(CalcRadarDataTrafficNeighbourCSLiSanByFrame.class);

    static Map<Integer, String> zcLdLaneMap = new HashMap<>();
    static {
        zcLdLaneMap.put(1, "R-C");
        zcLdLaneMap.put(2, "R-2");
        zcLdLaneMap.put(3, "R-YJ");
    }

    public static final String url = "jdbc:mysql://192.168.3.100:3306/radar_new?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8&useSSL=false" +
            "&zeroDateTimeconvertToNull&useCursorFetch=true&defaultFetchSize=1000";

    public static final String user = "root";
    public static final String password = "root";

    @SuppressWarnings("Duplicates")
    public static void main(String[] args) {
        Connection connection = null ;
        PreparedStatement preparedStatement = null ;

        String sql = "SELECT * FROM 20210731zc_ld_radar_frame_target_all WHERE date_time >= '2021-07-02 00:00:00.038' " +
                "AND date_time < '2021-07-02 01:00:00.000' AND radar_id = '1006' order by date_time";

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
                //数据过滤
//                if(sY > 200 || sY < 100){
//                    radarDO = new RadarDO();
//                    radarDO.setDataBody(new JSONArray());
//                    executeTest(radarDO);
//                    continue;
//                }
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
            //System.out.println("耗时：" + (end - start));
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

    static List<Double> floats = new ArrayList<>();
    @SuppressWarnings("Duplicates")
    private static void executeTest(RadarDO radarDO){
        if(radarDO != null){
            JSONArray jsonArray = radarDO.getDataBody();
            if(jsonArray.size() == 0){
                return;
            }
            Date dateTime = radarDO.getDate();

            //解析瞬时数据
            List<RadarObjDO> radarObjDOList = laneLiveData(radarDO);

            //按车道细分划分
            Map<Integer, List<RadarObjDO>> laneMap = new TreeMap<>((Integer o1, Integer o2) -> o2 - o1);

            //数据处理
            for(RadarObjDO radarObjDO: radarObjDOList){
                //解析雷达数据，按车道区分
                if(laneMap.get(radarObjDO.getLaneNum()) == null){
                    List<RadarObjDO> list = new ArrayList<>();
                    list.add(radarObjDO);
                    laneMap.put(radarObjDO.getLaneNum(), list);
                } else {
                    laneMap.get(radarObjDO.getLaneNum()).add(radarObjDO);
                }
            }

            //当前路段离散速度差集合
            List<Float> roadDiscreteDifferList = new ArrayList<>();

            byte laneFlag = 0;
            //上一车道数据
            List<RadarObjDO> previewRadarObjDOs = null;
            //计算车道
            for(Map.Entry<Integer, List<RadarObjDO>> m: laneMap.entrySet()){
                if(laneFlag == 0){
                    laneFlag = 1;
                    previewRadarObjDOs = m.getValue();
                    //排序preview
                    Collections.sort(previewRadarObjDOs, (o1, o2) -> o1.getsY() - o2.getsY() > 0? 1: o1.getsY() - o2.getsY() == 0? 0: -1);
                    continue;
                }
                //车道号
                int currentLaneNum = m.getKey();
                //当前车道数据
                List<RadarObjDO> currentRadarObjDOs = m.getValue();
                //排序current
                Collections.sort(currentRadarObjDOs, (o1, o2) -> o1.getsY() - o2.getsY() > 0? 1: o1.getsY() - o2.getsY() == 0? 0: -1);
                //合并两车道数据
                List<RadarObjDO> mergeTwoLaneRadarObjDOs = new ArrayList<>();
                mergeTwoLaneRadarObjDOs.addAll(previewRadarObjDOs);
                mergeTwoLaneRadarObjDOs.addAll(currentRadarObjDOs);
                //排序mergeTwoLane
                Collections.sort(mergeTwoLaneRadarObjDOs, (o1, o2) -> o1.getsY() - o2.getsY() > 0? 1: o1.getsY() - o2.getsY() == 0? 0: -1);

                //上一车道速度差
                List<Float> previewLaneDiffers = new ArrayList<>();
                //当前车道速度差
                List<Float> currentLaneDiffers = new ArrayList<>();
                //合并车道速度差
                List<Float> mergeTwoLaneDiffers = new ArrayList<>();

                calcLaneDiffer(previewRadarObjDOs, previewLaneDiffers);

                calcLaneDiffer(currentRadarObjDOs, currentLaneDiffers);

                calcLaneDiffer(mergeTwoLaneRadarObjDOs, mergeTwoLaneDiffers);

//                //计算离散度
//                double previewDiscreteValue = previewLaneDiffers.stream().mapToDouble(Float::floatValue).sum();
//                double currentDiscreteValue = currentLaneDiffers.stream().mapToDouble(Float::floatValue).sum();
//                double mergeTwoDiscreteValue = mergeTwoLaneDiffers.stream().mapToDouble(Float::floatValue).sum();
//                double discreteValue = (previewDiscreteValue + currentDiscreteValue + mergeTwoDiscreteValue)
//                        / (previewLaneDiffers.size() + currentLaneDiffers.size() + mergeTwoLaneRadarObjDOs.size());
                if(!roadDiscreteDifferList.contains(previewLaneDiffers)){
                    roadDiscreteDifferList.addAll(previewLaneDiffers);
                }
                if(!roadDiscreteDifferList.contains(currentLaneDiffers)){
                    roadDiscreteDifferList.addAll(currentLaneDiffers);
                }
                if(!roadDiscreteDifferList.contains(mergeTwoLaneDiffers)){
                    roadDiscreteDifferList.addAll(mergeTwoLaneDiffers);
                }

                //最后
                previewRadarObjDOs = m.getValue();
            }
            double discreteValue = 0;
            try {
                if(roadDiscreteDifferList.size() > 0){
                    discreteValue = roadDiscreteDifferList.stream().mapToDouble(Float::floatValue).average().getAsDouble();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            floats.add(discreteValue);
            if(floats.size() >= 14){
                System.out.printf("相邻速度差离散值：%s\n", floats.stream().mapToDouble(Double::doubleValue).average().getAsDouble());
                floats.clear();
            }
            
//            if(discreteValue > 0) {
//            	floats.add(discreteValue);
//            }
//            //据以半分钟为周期取平均值

        }
    }

    /**
     * 计算车道级速度差
     * @param previewRadarObjDOs
     * @param previewLaneDiffers
     */
    @SuppressWarnings("Duplicates")
    public static void calcLaneDiffer(List<RadarObjDO> previewRadarObjDOs, List<Float> previewLaneDiffers){
        byte previewFlag = 0;
        RadarObjDO previewRadarObjDO = null;
        for(RadarObjDO radarObjDO: previewRadarObjDOs){
            if(previewFlag == 0){
                previewFlag = 1;
                previewRadarObjDO = radarObjDO;
                continue;
            }
            RadarObjDO currentRadarObjDO = radarObjDO;
            
            //同车道所有相邻车以及不同车 道 120m 以内最近的相邻车的车速差
            if(Math.abs(previewRadarObjDO.getsY() - currentRadarObjDO.getsY()) <= 120) {
            	float differ = Math.abs(Math.abs(previewRadarObjDO.getvY()) - Math.abs(currentRadarObjDO.getvY()));
                previewLaneDiffers.add(differ);
            }

            //最后赋值
            previewRadarObjDO = currentRadarObjDO;
        }
    }

    /**
     * 解析车道瞬时数据
     */
    @SuppressWarnings("Duplicates")
    public static List<RadarObjDO> laneLiveData(RadarDO radarDO){
        JSONArray jsonArray = radarDO.getDataBody();
        List<RadarObjDO> radarObjDOList = new ArrayList<>();
        for(int i = 0; i < jsonArray.size(); i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            //每辆车解析
            RadarObjDO radarObjDO = new RadarObjDO();
            readData(radarObjDO, jsonObject);
            radarObjDOList.add(radarObjDO);
        }
        return radarObjDOList;
    }

    @SuppressWarnings("Duplicates")
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
