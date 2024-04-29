package org.lkdt.modules.radar.supports.radarTools.shell;

import org.lkdt.modules.radar.entity.ZcLdEventInfo;
import org.lkdt.modules.radar.entity.ZcLdEventRadarInfo;
import org.lkdt.modules.radar.entity.ZcLdRadarFrameTargetAll;
import org.lkdt.modules.radar.entity.baseEntity.ZcLDBaseEntity;
import org.lkdt.modules.radar.supports.radarTools.RadarEventPool;
import org.jfree.chart.*;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.math.BigDecimal;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.*;

/**
 * 新算法计算雷达车道级车流量
 */
public class CalcRadarDataCarFlowShell2 {

    public static final String url = "jdbc:mysql://192.168.3.100:3306/radar_test_20210506?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8&useSSL=false" +
            "&zeroDateTimeconvertToNull&useCursorFetch=true&defaultFetchSize=1000";

    public static final String user = "root";
    public static final String password = "root";

    public static void main(String[] args) {
        Connection connection = null ;
        PreparedStatement preparedStatement = null ;

        String[] startTimeArray = {"2021-05-01 00:00:00.000", "2021-05-01 01:00:00.000", "2021-05-01 02:00:00.000", "2021-05-01 03:00:00.000", "2021-05-01 04:00:00.000"
                , "2021-05-01 05:00:00.000", "2021-05-01 06:00:00.000", "2021-05-01 07:00:00.000", "2021-05-01 08:00:00.000", "2021-05-01 09:00:00.000"
                , "2021-05-01 10:00:00.000", "2021-05-01 11:00:00.000", "2021-05-01 12:00:00.000", "2021-05-01 13:00:00.000", "2021-05-01 14:00:00.000"
                , "2021-05-01 15:00:00.000", "2021-05-01 16:00:00.000", "2021-05-01 17:00:00.000", "2021-05-01 18:00:00.000", "2021-05-01 19:00:00.000"
                , "2021-05-01 20:00:00.000", "2021-05-01 21:00:00.000", "2021-05-01 22:00:00.000", "2021-05-01 23:00:00.000"};
        String[] endTimeArray = {"2021-05-01 01:00:00.000", "2021-05-01 02:00:00.000", "2021-05-01 03:00:00.000", "2021-05-01 04:00:00.000"
                , "2021-05-01 05:00:00.000", "2021-05-01 06:00:00.000", "2021-05-01 07:00:00.000", "2021-05-01 08:00:00.000", "2021-05-01 09:00:00.000"
                , "2021-05-01 10:00:00.000", "2021-05-01 11:00:00.000", "2021-05-01 12:00:00.000", "2021-05-01 13:00:00.000", "2021-05-01 14:00:00.000"
                , "2021-05-01 15:00:00.000", "2021-05-01 16:00:00.000", "2021-05-01 17:00:00.000", "2021-05-01 18:00:00.000", "2021-05-01 19:00:00.000"
                , "2021-05-01 20:00:00.000", "2021-05-01 21:00:00.000", "2021-05-01 22:00:00.000", "2021-05-01 23:00:00.000", "2021-05-02 00:00:00.000"};

        int countLai = 0;
        int countQu = 0;

        //定义折线图数据集
        DefaultCategoryDataset dataSetCarFlowLai = new DefaultCategoryDataset();
        DefaultCategoryDataset dataSetCarFlowQu = new DefaultCategoryDataset();
        DefaultCategoryDataset dataSetCarFlowHour = new DefaultCategoryDataset();

        try{
            //加载注册驱动
            Class.forName("com.mysql.jdbc.Driver");

            //获取连接
            connection = DriverManager.getConnection(url, user, password);
            connection.setAutoCommit(false);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            for(int i = 0; i < startTimeArray.length; i++){

                int count1=0, count2=0, count3=0, count4=0, count5=0, count6=0, count7=0;

                long start = System.currentTimeMillis();
                //读数据
                preparedStatement = connection.prepareStatement(
                        "select * from zc_ld_radar_frame_target_all_backup where radar_id = ? AND date_time > ? AND date_time <= ? order by date_time",
                        ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
                preparedStatement.setString(1, "1001");
                preparedStatement.setString(2, startTimeArray[i]);
                preparedStatement.setString(3, endTimeArray[i]);
                ResultSet resultSet = preparedStatement.executeQuery();

                /**数据帧ID缓存old*/
                //List<Integer> integerListOld = new ArrayList<>();
                //定义雷达事件池
                RadarEventPool radarEventPool = new RadarEventPool();
                int low  = 100;
                int high = 250;
                long nanoSecondOld = 0L;
                List<ZcLdRadarFrameTargetAll> zcLdRadarFrameTargetAlls = new ArrayList<>();
                ZcLdRadarFrameTargetAll zcLdRadarFrameTargetAll = null;
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

                    if(nanoSecondOld == nanoSecond){
                        zcLdRadarFrameTargetAll = new ZcLdRadarFrameTargetAll();
                        zcLdRadarFrameTargetAll.setCarId(carId);
                        zcLdRadarFrameTargetAll.setRadarId(radarId);
                        zcLdRadarFrameTargetAll.setTargetId(targetId);
                        zcLdRadarFrameTargetAll.setSX(sX);
                        zcLdRadarFrameTargetAll.setSY(sY);
                        zcLdRadarFrameTargetAll.setVX(vX);
                        zcLdRadarFrameTargetAll.setVY(vY);
                        zcLdRadarFrameTargetAll.setAX(aX);
                        zcLdRadarFrameTargetAll.setAY(aY);
                        zcLdRadarFrameTargetAll.setLaneNum(laneNum);
                        zcLdRadarFrameTargetAll.setCarType(carType);
                        zcLdRadarFrameTargetAll.setEvent(event);
                        zcLdRadarFrameTargetAll.setCarLength(carLength);
                        zcLdRadarFrameTargetAll.setDateTime(dateTime);
                        zcLdRadarFrameTargetAll.setNanoSecond(nanoSecond);
                        zcLdRadarFrameTargetAlls.add(zcLdRadarFrameTargetAll);
                    } else {
                        //一帧数据解析计算------开始
                        /////////////////////////////////////////////////////////////////
                        if(zcLdRadarFrameTargetAlls.size() > 0) {
                            /**数据帧ID缓存*/
                            List<String> dataFrameList = new ArrayList<>();
                            //获取常规池数据
                            Map<String, ZcLdEventRadarInfo> commonPool = radarEventPool.getCommonPool();
                            //筛选
                            for (ZcLdRadarFrameTargetAll z: zcLdRadarFrameTargetAlls) {
                                //数据解析
                                int targetId_ = z.getTargetId();//目标ID
                                double sX_ = z.getSX();//x坐标
                                double sY_ = z.getSY();//y坐标
                                double vX_ = z.getVX();//x速度
                                double vY_ = z.getVY();//y速度
                                double aX_ = z.getAX();//x加速度
                                double aY_ = z.getAY();//y加速度
                                int laneNum_ = z.getLaneNum();//车道号
                                //1 小型车，2 大型 车，3 超大型车
                                int carType_ = z.getCarType();//车辆类型
                                //0 无事件 1 车辆停止 2 车辆反方向行驶 （逆行） 3 车辆超速行驶 5 车辆实线变道
                                //101 车辆停止消失 102 车辆反方向行 驶消失 103 车辆超速行驶 消失105 车辆实线变道 消失
                                int event_ = z.getEvent();//事件类型
                                int carLength_ = z.getCarLength();//车辆长度
                                if(sY_ >= low && sY_ <= high) {
                                    //ID缓存
                                    dataFrameList.add("" + targetId_);
                                    ZcLdEventRadarInfo zcLdEventRadarInfoPool = commonPool.get("" + targetId_);
                                    //更新数据
                                    if(zcLdEventRadarInfoPool != null){
                                        //数值计算处理
                                        BigDecimal avg = zcLdEventRadarInfoPool.getSpeedAvg();
                                        BigDecimal max = zcLdEventRadarInfoPool.getSpeedMax();
                                        BigDecimal min = zcLdEventRadarInfoPool.getSpeedMin();
                                        avg = BigDecimal.valueOf(vY_).add(avg).divide(new BigDecimal(2));
                                        max = BigDecimal.valueOf(vY_).abs().compareTo(max.abs()) > 0? BigDecimal.valueOf(vY_): max;
                                        min = BigDecimal.valueOf(vY_).abs().compareTo(min.abs()) < 0? BigDecimal.valueOf(vY_): min;
                                        BigDecimal speedX = new BigDecimal(vX_);
                                        BigDecimal speedY = new BigDecimal(vY_);
                                        Date endTime = zcLdEventRadarInfoPool.getCreateTime();
                                        BigDecimal coordinateX = new BigDecimal(sX_);
                                        BigDecimal coordinateY = new BigDecimal(sY_);
                                        //常规池新增数据
                                        zcLdEventRadarInfoPool.setTargetId("" + targetId_);//目标ID
                                        zcLdEventRadarInfoPool.setEquId(radarId);//设备ID
                                        zcLdEventRadarInfoPool.setSpeedEndX(speedX);//X速度
                                        zcLdEventRadarInfoPool.setSpeedEndY(speedY);//Y速度
                                        zcLdEventRadarInfoPool.setLaneEndRadar(laneNum_);//雷达车道号
                                        zcLdEventRadarInfoPool.setLaneEndRoad(null);//道路车道号
                                        zcLdEventRadarInfoPool.setCarType(String.valueOf(carType_));//车型
                                        zcLdEventRadarInfoPool.setCarLength(carLength_);//车厂


                                        if (zcLdEventRadarInfoPool.getLastY() != -1 ) {
                                            if (Math.abs(vY_ - zcLdEventRadarInfoPool.getLastY()) < 3) {
                                                zcLdEventRadarInfoPool.setSpeedMax(max);// 最高行驶速度
                                            }
                                        }

                                        zcLdEventRadarInfoPool.setSpeedMin(min);//最低行驶速度
                                        zcLdEventRadarInfoPool.setSpeedAvg(avg);//平均行驶速度
                                        zcLdEventRadarInfoPool.setEndTime(z.getDateTime());//出雷达时间
                                        zcLdEventRadarInfoPool.setEndCoordinateX(coordinateX);//出雷达坐标X
                                        zcLdEventRadarInfoPool.setEndCoordinateY(coordinateY);//出雷达坐标Y
                                        //常规池数据装入
                                        commonPool.put("" + targetId_, zcLdEventRadarInfoPool);

                                        zcLdEventRadarInfoPool.setLastY(coordinateY.floatValue());
                                    } else {
                                        //新数据
                                        ZcLdEventRadarInfo zcLdEventRadarInfo = new ZcLdEventRadarInfo();
                                        //常规池新增数据
                                        zcLdEventRadarInfo.setTargetId("" + targetId_);//目标ID
                                        zcLdEventRadarInfo.setEquId(radarId);//设备ID
                                        zcLdEventRadarInfo.setSpeedStartX(new BigDecimal(vX_));//X速度
                                        zcLdEventRadarInfo.setSpeedStartY(new BigDecimal(vY_));//Y速度
                                        zcLdEventRadarInfo.setLaneStartRadar(laneNum_);//雷达车道号 1、2、3、4、5、6、7
                                        zcLdEventRadarInfo.setLaneStartRoad(null);//道路车道号
                                        zcLdEventRadarInfo.setCarType(String.valueOf(carType_));//车型
                                        zcLdEventRadarInfo.setCarLength(carLength_);//车厂
                                        zcLdEventRadarInfo.setSpeedMax(new BigDecimal(vY_));//最高行驶速度
                                        zcLdEventRadarInfo.setSpeedMin(new BigDecimal(vY_));//最低行驶速度
                                        zcLdEventRadarInfo.setSpeedAvg(new BigDecimal(vY_));//平均行驶速度
                                        zcLdEventRadarInfo.setBeginTime(z.getDateTime());//进雷达时间
                                        zcLdEventRadarInfo.setEndTime(null);//出雷达时间
                                        zcLdEventRadarInfo.setBeginCoordinateX(new BigDecimal(sX_));//进雷达坐标X
                                        zcLdEventRadarInfo.setBeginCoordinateY(new BigDecimal(sY_));//进雷达坐标Y
                                        zcLdEventRadarInfo.setEndCoordinateX(null);//出雷达坐标X
                                        zcLdEventRadarInfo.setEndCoordinateY(null);//出雷达坐标Y
                                        //常规池数据装入
                                        commonPool.put("" + targetId_, zcLdEventRadarInfo);
                                    }


                                }

                            }
                            try{
                                //常规池待删除数据
                                List<String> commonRemoved = new ArrayList<>();
                                //遍历常规池
                                for(Map.Entry<String, ZcLdEventRadarInfo> map: commonPool.entrySet()){
                                    //新ID不包含旧ID，数据丢入丢失池中
                                    //1、信号消失时立即处理
                                    if(!dataFrameList.contains(map.getKey())){
                                        ZcLdEventRadarInfo diuShiData = map.getValue();
                                        commonRemoved.add(map.getKey());
                                        if(diuShiData != null){
                                            //已丢失数据
                                            //计算车流量数据
                                            if(validFilter(diuShiData)){
                                                //未去噪
//                                                if(diuShiData.getLaneEndRadar() < 4){
//                                                    countLai++;
//                                                    dataSetCarFlowLai.addValue(diuShiData.getBeginCoordinateY().doubleValue(), "来向开始Y值", sdf.format(diuShiData.getBeginTime()));
//                                                    dataSetCarFlowLai.addValue(diuShiData.getEndCoordinateY().doubleValue(), "来向结束Y值", sdf.format(diuShiData.getBeginTime()));
//                                                    switch (diuShiData.getLaneEndRadar()){
//                                                        case 1: count1++; break;
//                                                        case 2: count2++; break;
//                                                        case 3: count3++; break;
//                                                        case 4: count4++; break;
//                                                        case 5: count5++; break;
//                                                        case 6: count6++; break;
//                                                        case 7: count7++; break;
//                                                        default:break;
//                                                    }
//                                                } else if(diuShiData.getLaneEndRadar() > 4){
//                                                    //去噪
//                                                    countQu++;
//                                                    dataSetCarFlowQu.addValue(diuShiData.getBeginCoordinateY().doubleValue(), "去向开始Y值", sdf.format(diuShiData.getBeginTime()));
//                                                    dataSetCarFlowQu.addValue(diuShiData.getEndCoordinateY().doubleValue(), "去向结束Y值", sdf.format(diuShiData.getBeginTime()));
//                                                    switch (diuShiData.getLaneEndRadar()){
//                                                        case 1: count1++; break;
//                                                        case 2: count2++; break;
//                                                        case 3: count3++; break;
//                                                        case 4: count4++; break;
//                                                        case 5: count5++; break;
//                                                        case 6: count6++; break;
//                                                        case 7: count7++; break;
//                                                        default:break;
//                                                    }
//                                                }
                                                //去噪
                                                if(diuShiData.getLaneEndRadar() < 4){
                                                    //去噪
                                                    if(diuShiData.getBeginCoordinateY().doubleValue() > 240 && diuShiData.getBeginCoordinateY().doubleValue() < 260){
                                                        if(diuShiData.getEndCoordinateY().doubleValue() > 90 && diuShiData.getEndCoordinateY().doubleValue() < 110){
                                                            countLai++;
                                                            dataSetCarFlowLai.addValue(diuShiData.getBeginCoordinateY().doubleValue(), "来向开始Y值", sdf.format(diuShiData.getBeginTime()));
                                                            dataSetCarFlowLai.addValue(diuShiData.getEndCoordinateY().doubleValue(), "来向结束Y值", sdf.format(diuShiData.getBeginTime()));
                                                            switch (diuShiData.getLaneEndRadar()){
                                                                case 1: count1++; break;
                                                                case 2: count2++; break;
                                                                case 3: count3++; break;
                                                                case 4: count4++; break;
                                                                case 5: count5++; break;
                                                                case 6: count6++; break;
                                                                case 7: count7++; break;
                                                                default:break;
                                                            }
                                                        }
                                                    }
                                                } else if(diuShiData.getLaneEndRadar() > 4){
                                                    //去噪
                                                    if(diuShiData.getBeginCoordinateY().doubleValue() > 90 && diuShiData.getBeginCoordinateY().doubleValue() < 110){
                                                        if(diuShiData.getEndCoordinateY().doubleValue() > 240 && diuShiData.getEndCoordinateY().doubleValue() < 260) {
                                                            countQu++;
                                                            dataSetCarFlowQu.addValue(diuShiData.getBeginCoordinateY().doubleValue(), "去向开始Y值", sdf.format(diuShiData.getBeginTime()));
                                                            dataSetCarFlowQu.addValue(diuShiData.getEndCoordinateY().doubleValue(), "去向结束Y值", sdf.format(diuShiData.getBeginTime()));
                                                            switch (diuShiData.getLaneEndRadar()){
                                                                case 1: count1++; break;
                                                                case 2: count2++; break;
                                                                case 3: count3++; break;
                                                                case 4: count4++; break;
                                                                case 5: count5++; break;
                                                                case 6: count6++; break;
                                                                case 7: count7++; break;
                                                                default:break;
                                                            }
                                                        }
                                                    }
                                                }
                                                //System.out.println(diuShiData);

                                            }
                                        }
                                    }
                                }
                                //常规池删除已丢失数据
                                for(String s: commonRemoved){
                                    commonPool.remove(s);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                        //一帧数据解析计算------结束
                        /////////////////////////////////////////////////////////////////

                        //帧数据，首条数据
                        zcLdRadarFrameTargetAlls.clear();
                        nanoSecondOld = nanoSecond;
                        zcLdRadarFrameTargetAll = new ZcLdRadarFrameTargetAll();
                        zcLdRadarFrameTargetAll.setCarId(carId);
                        zcLdRadarFrameTargetAll.setRadarId(radarId);
                        zcLdRadarFrameTargetAll.setTargetId(targetId);
                        zcLdRadarFrameTargetAll.setSX(sX);
                        zcLdRadarFrameTargetAll.setSY(sY);
                        zcLdRadarFrameTargetAll.setVX(vX);
                        zcLdRadarFrameTargetAll.setVY(vY);
                        zcLdRadarFrameTargetAll.setAX(aX);
                        zcLdRadarFrameTargetAll.setAY(aY);
                        zcLdRadarFrameTargetAll.setLaneNum(laneNum);
                        zcLdRadarFrameTargetAll.setCarType(carType);
                        zcLdRadarFrameTargetAll.setEvent(event);
                        zcLdRadarFrameTargetAll.setCarLength(carLength);
                        zcLdRadarFrameTargetAll.setDateTime(dateTime);
                        zcLdRadarFrameTargetAll.setNanoSecond(nanoSecond);
                        zcLdRadarFrameTargetAlls.add(zcLdRadarFrameTargetAll);
                    }

                }

                dataSetCarFlowHour.addValue(count1, "1车道", (i+1) + "h");
                dataSetCarFlowHour.addValue(count2, "2车道", (i+1) + "h");
                dataSetCarFlowHour.addValue(count3, "3车道", (i+1) + "h");
                dataSetCarFlowHour.addValue(count4, "4车道", (i+1) + "h");
                dataSetCarFlowHour.addValue(count5, "5车道", (i+1) + "h");
                dataSetCarFlowHour.addValue(count6, "6车道", (i+1) + "h");
                dataSetCarFlowHour.addValue(count7, "7车道", (i+1) + "h");

                long end = System.currentTimeMillis();
                System.out.println("计数：" + countLai + "【来向】，" + countQu + "【去向】，耗时：" + (end-start) + "，操作：" + preparedStatement.toString());
            }

            generateLineJPEG(dataSetCarFlowLai, "1001车流量折线图", "来向进出雷达坐标","时间" ,
                    "C:\\Users\\zhangzhenbiao\\Desktop\\来向进出雷达Y坐标.jpg");
            generateLineJPEG(dataSetCarFlowQu, "1001车流量折线图", "去向进出雷达坐标","时间" ,
                    "C:\\Users\\zhangzhenbiao\\Desktop\\去向进出雷达Y坐标.jpg");
            generateLineJPEG(dataSetCarFlowHour, "1001车流量-以小时为区间", "车流量/小时","小时" ,
                    "C:\\Users\\zhangzhenbiao\\Desktop\\车流量每小时.jpg");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("总计数:" + countLai + "【来向】，" + countQu + "【去向】");
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

    private static void countLane(int lane, int count1, int count2, int count3, int count4, int count5, int count6, int count7){
        switch (lane){
            case 1: count1++; break;
            case 2: count2++; break;
            case 3: count3++; break;
            case 4: count4++; break;
            case 5: count5++; break;
            case 6: count6++; break;
            case 7: count7++; break;
            default:break;
        }
    }

    private static boolean validFilter(ZcLDBaseEntity zcLDBaseEntity) {
        if(zcLDBaseEntity instanceof ZcLdEventRadarInfo){
            //检测目标物详情
            ZcLdEventRadarInfo zcLdEventRadarInfo = (ZcLdEventRadarInfo) zcLDBaseEntity;
            //1、过滤y坐标小于100或者大于250米的目标【已在数据抓取时过滤】
//            BigDecimal beginY = zcLdEventRadarInfo.getBeginCoordinateY();
//            BigDecimal endY = zcLdEventRadarInfo.getEndCoordinateY();


            try{

                //2、结束时间为NULL
                Date endDate = zcLdEventRadarInfo.getEndTime();
                if(endDate == null){
                    return false;
                }

                //3、开始时间与结束时间时间差小于1秒
                long beginTime = zcLdEventRadarInfo.getBeginTime().getTime();
                long endTime = zcLdEventRadarInfo.getEndTime().getTime();

                if(endTime - beginTime < 1000){
                    return false;
                }
                //debug调试
                //System.out.println(zcLdEventRadarInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if(zcLDBaseEntity instanceof ZcLdEventInfo){
            //事件检测详情
            ZcLdEventInfo zcLdEventInfo = (ZcLdEventInfo) zcLDBaseEntity;

            //debug调试
            //System.out.println(zcLdEventInfo);
        }
        return true;
    }

    public static void main2(String[] args) {
        generateLine(GetDataset(), "折线图", "纵坐标", "时间");
    }

    /**
     * 生成折线图jpeg
     * @return
     */
    protected static void generateLineJPEG(CategoryDataset categoryDataset, String title, String axisLabel, String xLabel, String fileName){
        System.out.println("生成折线图jpeg");
//        StandardChartTheme mChartTheme = new StandardChartTheme("CN");
//        mChartTheme.setLargeFont(new Font("黑体", Font.BOLD, 20));
//        mChartTheme.setExtraLargeFont(new Font("宋体", Font.PLAIN, 15));
//        mChartTheme.setRegularFont(new Font("宋体", Font.PLAIN, 15));
//        ChartFactory.setChartTheme(mChartTheme);
        try{
            CategoryDataset mDataset = categoryDataset;
            JFreeChart chart = ChartFactory.createLineChart(
                    title,//图名字
                    xLabel,//横坐标
                    axisLabel,//纵坐标
                    mDataset,//数据集
                    PlotOrientation.VERTICAL,
                    true, // 显示图例
                    true, // 采用标准生成器
                    false);// 是否生成超链接

            //重新设置图标标题，改变字体 
            chart.setTitle(new TextTitle(title, new Font("黑体", Font.ITALIC , 22)));
            //取得统计图标的第一个图例 
            LegendTitle legend = chart.getLegend(0);
            //修改图例的字体 
            legend.setItemFont(new Font("宋体", Font.BOLD, 14));

            CategoryPlot plot = (CategoryPlot)chart.getPlot();

            //设置柱状图到图片上端的距离
            ValueAxis rangeAxis = plot.getRangeAxis();
            rangeAxis.setUpperMargin(0.1);
//            //取得横轴 
//            CategoryAxis categoryAxis = plot.getDomainAxis();
//            //设置横轴显示标签的字体 
//            categoryAxis.setLabelFont(new Font("宋体" , Font.BOLD , 22));
//            //分类标签以45度角倾斜 
//            categoryAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
//            categoryAxis.setTickLabelFont(new Font("宋体" , Font.BOLD , 18));
            //取得纵轴 
            NumberAxis numberAxis = (NumberAxis)plot.getRangeAxis();
            //设置纵轴显示标签的字体 
            numberAxis.setLabelFont(new Font("宋体" , Font.BOLD , 22));

            ByteArrayOutputStream baos = null;
            try{
                baos = new ByteArrayOutputStream();
                ChartUtils.writeChartAsJPEG(baos, chart, 2000,1000,null);
                baos.flush();
                baos.close();
//                Base64.Encoder encoder = Base64.getEncoder();
//                String base64 = encoder.encodeToString(baos.toByteArray());
//                return "data:image/jpg;base64," + base64;
                ByteArrayInputStream baIs = new ByteArrayInputStream(baos.toByteArray());
                BufferedImage bufferedImage = ImageIO.read(baIs);
                //ImageIO.write(bufferedImage, "jpg", new File("C:\\Users\\zhangzhenbiao\\Desktop\\bufferedImage.jpg"));
                ImageIO.write(bufferedImage, "jpg", new File(fileName));
                baIs.close();
            } catch (Exception e){
                e.printStackTrace();
            } finally {
                if(baos != null){
                    baos.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 弹出生成折线图
     * @return
     */
    protected static String generateLine(CategoryDataset categoryDataset, String title, String axisLabel, String xLabel){
        System.out.println("生成折线图");
        StandardChartTheme mChartTheme = new StandardChartTheme("CN");
        mChartTheme.setLargeFont(new Font("黑体", Font.BOLD, 20));
        mChartTheme.setExtraLargeFont(new Font("宋体", Font.PLAIN, 15));
        mChartTheme.setRegularFont(new Font("宋体", Font.PLAIN, 15));
        ChartFactory.setChartTheme(mChartTheme);
        CategoryDataset mDataset = categoryDataset;
        JFreeChart mChart = ChartFactory.createLineChart(
                title,//图名字
                xLabel,//横坐标
                axisLabel,//纵坐标
                mDataset,//数据集
                PlotOrientation.VERTICAL,
                true, // 显示图例
                true, // 采用标准生成器
                false);// 是否生成超链接
        CategoryPlot mPlot = (CategoryPlot)mChart.getPlot();
        mPlot.setBackgroundPaint(Color.white);
        mPlot.setRangeGridlinePaint(Color.BLUE);//背景底部横虚线
        mPlot.setOutlinePaint(Color.RED);//边界线


        //Y轴设置
//        NumberAxis vn = (NumberAxis) mPlot.getRangeAxis();
//        vn.setUpperMargin(0.1);
//        vn.setLowerMargin(0.1);
//        vn.setAutoRangeMinimumSize(0.01);//最小跨度
//        vn.setLowerBound(0.70);//最小值显示
//        vn.setUpperBound(1.10);
        LineAndShapeRenderer lasp = (LineAndShapeRenderer) mPlot.getRenderer();// 获取显示线条的对象
        lasp.setDrawOutlines(true);// 设置拐点不同用不同的形状
        lasp.setUseFillPaint(true);// 设置线条是否被显示填充颜色

        //X轴
        CategoryAxis domainAxis = mPlot.getDomainAxis();
        //domainAxis.setLowerMargin(-0.08);

        //mChart
        ChartFrame mChartFrame = new ChartFrame(title, mChart);
        mChartFrame.pack();
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        mChartFrame.setVisible(true);
        return null;
    }

    public static CategoryDataset GetDataset() {
        DefaultCategoryDataset mDataset = new DefaultCategoryDataset();
        mDataset.addValue(1.10, "First", "5");
        mDataset.addValue(1.05, "First", "10");
        mDataset.addValue(1.00, "First", "15");
        mDataset.addValue(0.95, "First", "20");
        mDataset.addValue(0.90, "First", "25");

        mDataset.addValue(1.05, "Second", "5");
        mDataset.addValue(1.00, "Second", "10");
        mDataset.addValue(0.96, "Second", "15");
        mDataset.addValue(0.91, "Second", "20");
        mDataset.addValue(0.88, "Second", "25");

        mDataset.addValue(1.02, "Third", "5");
        mDataset.addValue(0.90, "Third", "10");
        mDataset.addValue(0.88, "Third", "15");
        mDataset.addValue(0.85, "Third", "20");
        mDataset.addValue(0.7, "Third", "25");
        return mDataset;
    }

    /**
     * 生成条形图
     * @return
     */
    protected String generateBar(List<BarBase> barBaseList, String title, String categoryAxisLabel){
        try{
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            for(BarBase b: barBaseList){
                dataset.addValue(b.getValue(),b.getRowKey(),b.getColumnKey());
            }

            JFreeChart chart = ChartFactory.createBarChart(title,categoryAxisLabel,"次数",
                    dataset, PlotOrientation.VERTICAL ,true,false,false);
            //重新设置图标标题，改变字体 
            chart.setTitle(new TextTitle(title, new Font("黑体", Font.ITALIC , 22)));
            //取得统计图标的第一个图例 
            LegendTitle legend = chart.getLegend(0);
            //修改图例的字体 
            legend.setItemFont(new Font("宋体", Font.BOLD, 14));

            CategoryPlot plot = (CategoryPlot)chart.getPlot();

            //设置柱状图到图片上端的距离
            ValueAxis rangeAxis = plot.getRangeAxis();
            rangeAxis.setUpperMargin(0.1);
            //取得横轴 
            CategoryAxis categoryAxis = plot.getDomainAxis();
            //设置横轴显示标签的字体 
            categoryAxis.setLabelFont(new Font("宋体" , Font.BOLD , 22));
            //分类标签以45度角倾斜 
            categoryAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
            categoryAxis.setTickLabelFont(new Font("宋体" , Font.BOLD , 18));
            //取得纵轴 
            NumberAxis numberAxis = (NumberAxis)plot.getRangeAxis();
            //设置纵轴显示标签的字体 
            numberAxis.setLabelFont(new Font("宋体" , Font.BOLD , 22));

            ByteArrayOutputStream baos = null;
            try{
                baos = new ByteArrayOutputStream();
                ChartUtils.writeChartAsJPEG(baos, chart, 500,400,null);
                baos.flush();
                baos.close();
                Base64.Encoder encoder = Base64.getEncoder();
                String base64 = encoder.encodeToString(baos.toByteArray());
                return "data:image/jpg;base64," + base64;
            } catch (Exception e){
                e.printStackTrace();
            } finally {
                if(baos != null){
                    baos.close();
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * BarBase
     */
    private class BarBase{

        private String rowKey;

        private String columnKey;

        private int value;

        public String getRowKey() {
            return rowKey;
        }

        public String getColumnKey() {
            return columnKey;
        }

        public int getValue() {
            return value;
        }

        public BarBase setRowKey(String rowKey) {
            this.rowKey = rowKey;
            return this;
        }

        public BarBase setColumnKey(String columnKey) {
            this.columnKey = columnKey;
            return this;
        }

        public BarBase setValue(int value) {
            this.value = value;
            return this;
        }
    }
}
