package org.lkdt.modules.radar.supports.radarTools.shell;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import org.jfree.data.category.DefaultCategoryDataset;
import org.lkdt.common.util.StringUtils;
import org.lkdt.modules.radar.supports.radarDataService.observers.DO.RadarObjDO;
import org.lkdt.modules.radar.supports.radarTools.DO.RadarDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.stream.Collectors;

/**
 * 新算法计算雷达车流量
 * 通行速度指数n帧平均一次
 */
public class CalcRadarDataTrafficSpeedCoefficientByFrame {

    private static Logger logger = LoggerFactory.getLogger(CalcRadarDataTrafficSpeedCoefficientByFrame.class);

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
                if(sY > 200 || sY < 100){
                    continue;
                }
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

    static List<Float> floats = new ArrayList<>();
    @SuppressWarnings("Duplicates")
    private static void executeTest(RadarDO radarDO){
        //System.out.println("radarDO" + ++countFrame);
        //道路交通流三态数据读取
        List<RadarObjDO> radarObjDOList = laneLiveData(radarDO);

        List<Float> vBigList = new ArrayList<>();
        List<Float> vSmallList = new ArrayList<>();
        List<Float> vBigListHun = new ArrayList<>();
        List<Float> vSmallListHun = new ArrayList<>();
        double mBigSum = 0;
        double mBigSumHun = 0;
        double nSmallSum = 0;
        double nSmallSumHun = 0;
        float vLimitSmall = 120;
        float vLimitBig = 90;
        for(RadarObjDO radarObjDO: radarObjDOList){
            //车道号
            int laneNum = radarObjDO.getLaneNum();
            String laneNumDesc = zcLdLaneMap.get(laneNum);
            if(StringUtils.isNotEmpty(laneNumDesc) && (laneNumDesc.endsWith("-C"))){
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
            } else if(StringUtils.isNotEmpty(laneNumDesc) && (laneNumDesc.endsWith("-2"))){
                //1小型车，2大型车，3中型车
                int carType = radarObjDO.getCarType();
                float vY = Math.abs(radarObjDO.getvY());
                if(carType == 1){
                    vSmallListHun.add(vY);
                    float vY_ = vY/vLimitSmall;
                    nSmallSumHun += vY_ > 1? 1: vY_;
                } else {
                    vBigListHun.add(vY);
                    float vY_ = vY/vLimitBig;
                    mBigSumHun += vY_ > 1? 1: vY_;
                }
            }
        }

        //道路交通流三态算法计算
        float pV = 0;
        float vV = 0;
        //当前能见度值
        Integer distance = 500;
        if(distance == null || distance > 200){
            int nSmall = vSmallList.size();
            int nSmallHun = vSmallListHun.size();
            int mBig = vBigList.size();
            int mBigHun = vBigListHun.size();

            if(nSmall + mBig == 0 && nSmallHun + mBigHun == 0){
                pV = 1;
            } else if(nSmall + mBig == 0 && nSmallHun + mBigHun == 1){
                pV = 1;
            } else if(nSmall + mBig == 1 && nSmallHun + mBigHun == 0){
                pV = 1;
            } else if(nSmall + mBig == 0 || nSmallHun + mBigHun == 0){
                if(nSmall + mBig > 0){
                    double pXiao = nSmallSum;
                    double pDa = mBigSum;
                    pV = (float) ((pXiao + pDa) / (nSmall + mBig));

                    if(nSmall + mBig == 0){
                        vV = 0;
                    } else {
                        vV = (float) ((nSmallSum + mBigSum) / (nSmall + mBig));
                    }
                } else {
                    double pXiao = nSmallSumHun;
                    double pDa = mBigSumHun;
                    pV = (float) ((pXiao + pDa) / (nSmallHun + mBigHun));

                    if(nSmallHun + mBigHun == 0){
                        vV = 0;
                    } else {
                        vV = (float) ((nSmallSumHun + mBigSumHun) / (nSmallHun + mBigHun));
                    }
                }
            } else {
                //System.out.printf("忽略计算，超车道数量：小车%s大车%s，混合车道数量小车%s大车%s\n", nSmall, mBig, nSmallHun, mBigHun);
                return;
            }
            floats.add(pV);
            if(floats.size() >= 14){
                Double pV_ =  floats.stream().mapToDouble(Float::floatValue).average().getAsDouble();
                System.out.printf("【通行速度指数14帧平均一次】pv=%s，最后一帧—>超车道数量：小车%s大车%s，混合车道数量小车%s大车%s\n", pV_, nSmall, mBig, nSmallHun, mBigHun);
                floats.clear();
            }
        }
    }

    /**
     * 解析车道瞬时数据
     */
    @SuppressWarnings("Duplicates")
    public static List<RadarObjDO> laneLiveData(RadarDO radarDO){
        JSONArray jsonArray = radarDO.getDataBody();
        ///////////////////////start
        //////////////////////////////////////////////
        List<RadarObjDO> radarObjDOList = new ArrayList<>();
        for(int i = 0; i < jsonArray.size(); i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            //每辆车解析
            RadarObjDO radarObjDO = new RadarObjDO();
            readData(radarObjDO, jsonObject);

            String data = zcLdLaneMap.get(radarObjDO.getLaneNum());

            if(StringUtils.isNotEmpty(data) && (data.endsWith("-C") || data.endsWith("-2"))){
                radarObjDOList.add(radarObjDO);
            }

        }
        ///////////////////////end
        //////////////////////////////////////////////

        return radarObjDOList;
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
