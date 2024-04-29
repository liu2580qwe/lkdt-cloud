package org.lkdt.modules.radar.supports.radarTools.shell;

import org.lkdt.modules.radar.entity.ZcLdRadarFrameTargetAll;
import org.lkdt.common.util.StringUtils;
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

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.List;

/**
 * 计算雷达数据脚本
 *
 * 碰撞时距
 */
public class CalculateRadarDataShell {

    public static final String url = "jdbc:mysql://192.168.3.100:3306/radar_test_20210506?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8&useSSL=false" +
            "&zeroDateTimeconvertToNull&useCursorFetch=true&defaultFetchSize=1000";

    public static final String user = "root";
    public static final String password = "root";

    public static void main(String[] args) {
        Connection connection = null ;
        PreparedStatement preparedStatement = null ;

        int hour = 0;
        int count = 0;

        //定义折线图数据集
        DefaultCategoryDataset dataSet1 = new DefaultCategoryDataset();
        DefaultCategoryDataset dataSet2 = new DefaultCategoryDataset();
        DefaultCategoryDataset dataSet3 = new DefaultCategoryDataset();
        DefaultCategoryDataset dataSet5 = new DefaultCategoryDataset();
        DefaultCategoryDataset dataSet6 = new DefaultCategoryDataset();
        DefaultCategoryDataset dataSet7 = new DefaultCategoryDataset();

        //定义碰撞时距数据集
        DefaultCategoryDataset pzDataSet1 = new DefaultCategoryDataset();
        DefaultCategoryDataset pzDataSet2 = new DefaultCategoryDataset();
        DefaultCategoryDataset pzDataSet3 = new DefaultCategoryDataset();
        DefaultCategoryDataset pzDataSet5 = new DefaultCategoryDataset();
        DefaultCategoryDataset pzDataSet6 = new DefaultCategoryDataset();
        DefaultCategoryDataset pzDataSet7 = new DefaultCategoryDataset();

        //定义碰撞车距数据集
        DefaultCategoryDataset pCDataSet1 = new DefaultCategoryDataset();
        DefaultCategoryDataset pCDataSet2 = new DefaultCategoryDataset();
        DefaultCategoryDataset pCDataSet3 = new DefaultCategoryDataset();
        DefaultCategoryDataset pCDataSet5 = new DefaultCategoryDataset();
        DefaultCategoryDataset pCDataSet6 = new DefaultCategoryDataset();
        DefaultCategoryDataset pCDataSet7 = new DefaultCategoryDataset();

        try{
            //加载注册驱动
            Class.forName("com.mysql.jdbc.Driver");

            //获取连接
            connection = DriverManager.getConnection(url, user, password);
            connection.setAutoCommit(false);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

            //while(true) {
                long start = System.currentTimeMillis();
                //读数据
                preparedStatement = connection.prepareStatement(
                    "select * from zc_ld_radar_frame_target_all_backup where radar_id = ? AND date_time > ? AND date_time <= ? order by date_time",
                    ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
                preparedStatement.setString(1, "1001");
//                String time1 = hour%24 >= 10? "" + hour%24: "0"+hour%24;
//                String time2 = (hour+1)%24 >= 10? "" + (hour+1)%24: "0"+(hour+1)%24;
//                String startTime = "2021-05-01 " + time1 + ":00:00.000";
//                String endTime = "2021-05-01 " + time2 + ":00:00.000";
                String startTime = "2021-05-01 21:00:00.000";
                String endTime = "2021-05-01 22:00:00.000";
                preparedStatement.setString(2, startTime);
                preparedStatement.setString(3, endTime);

//                if(++hour == 2){
//                    break;
//                }

                System.out.println(preparedStatement.toString());
                ResultSet resultSet = preparedStatement.executeQuery();

                //页数初始化
                System.out.println("next batch!");

                long nanoSecondOld = 0L;
                List<ZcLdRadarFrameTargetAll> zcLdRadarFrameTargetAlls = new ArrayList<>();
                ZcLdRadarFrameTargetAll zcLdRadarFrameTargetAll = null;

                while(resultSet.next()) {
                    count++;

//                    if(count == 10000){
//                        break;
//                    }

                    if(count%10000 == 0){
                        System.out.print(count + ",");
                    }
                    //时距计算
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
                        Map<Integer, List<ZcLdRadarFrameTargetAll>> map = new HashMap<>();
                        if(zcLdRadarFrameTargetAlls.size() > 0){
                            //筛选
                            for (ZcLdRadarFrameTargetAll z :zcLdRadarFrameTargetAlls) {
                                int laneNum_ = z.getLaneNum();
                                if(map.get(laneNum_) == null){
                                    List<ZcLdRadarFrameTargetAll> list = new ArrayList<>();
                                    list.add(z);
                                    map.put(laneNum_, list);
                                } else {
                                    map.get(laneNum_).add(z);
                                }
                            }

                            //定义各车道平均时距
                            double[] avgTimeDistances = new double[]{20, 20, 20, 20, 20, 20, 20, 20};
                            double[] avgPZTimeDistances = new double[]{30, 30, 30, 30, 30, 30, 30, 30};
                            double[] avgPCTimeDistances = new double[]{500, 500, 500, 500, 500, 500, 500, 500};
                            for(Map.Entry<Integer, List<ZcLdRadarFrameTargetAll>> entry: map.entrySet()){
                                List<ZcLdRadarFrameTargetAll> list = entry.getValue();

                                //定义平均时距, 默认时距是10s
                                double avgTimeDistance = 0;
                                double avgPZTimeDistance = 0;
                                double avgPCTimeDistance = 0;
                                if(list.size() <= 1){
                                    //跳过当前车道
                                    continue;
                                }

                                Collections.sort(list);
                                //计算当前车道数据

                                //定义单车道时距集合
                                List<Double> avgTimeDistanceList = new ArrayList<>();
                                List<Double> avgPZTimeDistanceList = new ArrayList<>();
                                List<Double> avgPCTimeDistanceList = new ArrayList<>();
                                for(int i = 0; i < list.size(); i++){
                                    if(i > 0){
                                        double avgT = (list.get(i).getSY() - list.get(i-1).getSY())/Math.abs(list.get(i-1).getVY())*3.6;//*3.6 把km/h转换成m/s
                                        double avgPZT = (list.get(i).getSY() + list.get(i).getCarLength() - list.get(i-1).getSY())/Math.abs(list.get(i-1).getVY())*3.6;//*3.6 把km/h转换成m/s
                                        double a1 = list.get(i-1).getCarType() > 1? 4: 6;//大车加速度取4小车取6
                                        double a2 = list.get(i).getCarType() > 1? 4: 6;//大车加速度取4小车取6
                                        double v1 = Math.abs(list.get(i-1).getVY());
                                        double v2 = Math.abs(list.get(i).getVY());
                                        double avgPCT = v1*1 + Math.pow(v1, 2)/(2*a1) - Math.pow(v2, 2)/(2*a2) + 5;//单位是米
                                        avgTimeDistanceList.add(avgT);
                                        avgPZTimeDistanceList.add(avgPZT);
                                        avgPCTimeDistanceList.add(avgPCT);
                                    }
                                }
                                //求平均
                                avgTimeDistance = avgTimeDistanceList.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
                                avgPZTimeDistance = avgPZTimeDistanceList.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
                                avgPCTimeDistance = avgPCTimeDistanceList.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
                                avgTimeDistances[entry.getKey()] = avgTimeDistance;
                                avgPZTimeDistances[entry.getKey()] = avgPZTimeDistance;
                                avgPCTimeDistances[entry.getKey()] = avgPCTimeDistance;
                            }

                            String xData = sdf.format(dateTime);
                            //统计表数据集添加
                            dataSet1.addValue(avgTimeDistances[1] > 20? 20:avgTimeDistances[1], "1车道", xData);
                            dataSet2.addValue(avgTimeDistances[2] > 20? 20:avgTimeDistances[2], "2车道", xData);
                            dataSet3.addValue(avgTimeDistances[3] > 20? 20:avgTimeDistances[3], "3车道", xData);
                            dataSet5.addValue(avgTimeDistances[5] > 20? 20:avgTimeDistances[5], "5车道", xData);
                            dataSet6.addValue(avgTimeDistances[6] > 20? 20:avgTimeDistances[6], "6车道", xData);
                            dataSet7.addValue(avgTimeDistances[7] > 20? 20:avgTimeDistances[7], "7车道", xData);

                            //碰撞时距
                            pzDataSet1.addValue(avgPZTimeDistances[1] > 30? 30:avgPZTimeDistances[1], "1车道", xData);
                            pzDataSet2.addValue(avgPZTimeDistances[2] > 30? 30:avgPZTimeDistances[2], "2车道", xData);
                            pzDataSet3.addValue(avgPZTimeDistances[3] > 30? 30:avgPZTimeDistances[3], "3车道", xData);
                            pzDataSet5.addValue(avgPZTimeDistances[5] > 30? 30:avgPZTimeDistances[5], "5车道", xData);
                            pzDataSet6.addValue(avgPZTimeDistances[6] > 30? 30:avgPZTimeDistances[6], "6车道", xData);
                            pzDataSet7.addValue(avgPZTimeDistances[7] > 30? 30:avgPZTimeDistances[7], "7车道", xData);

                            //碰撞车距
                            pCDataSet1.addValue(avgPCTimeDistances[1] > 500 || avgPCTimeDistances[1] < 0? 500:avgPCTimeDistances[1], "1车道", xData);
                            pCDataSet2.addValue(avgPCTimeDistances[2] > 500 || avgPCTimeDistances[2] < 0? 500:avgPCTimeDistances[2], "2车道", xData);
                            pCDataSet3.addValue(avgPCTimeDistances[3] > 500 || avgPCTimeDistances[3] < 0? 500:avgPCTimeDistances[3], "3车道", xData);
                            pCDataSet5.addValue(avgPCTimeDistances[5] > 500 || avgPCTimeDistances[5] < 0? 500:avgPCTimeDistances[5], "5车道", xData);
                            pCDataSet6.addValue(avgPCTimeDistances[6] > 500 || avgPCTimeDistances[6] < 0? 500:avgPCTimeDistances[6], "6车道", xData);
                            pCDataSet7.addValue(avgPCTimeDistances[7] > 500 || avgPCTimeDistances[7] < 0? 500:avgPCTimeDistances[7], "7车道", xData);

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
                System.out.println();
                long end = System.currentTimeMillis();
                System.out.println("已计算数据总量是：" + count + ",      当前批次耗时：" + (end - start) + ",      时间：" + new Date());
            //}

//            generateLine(dataSet1, "1车道车头时距图", "车头时距", startTime + "至" + endTime);
//            generateLine(dataSet2, "2车道车头时距图", "车头时距", startTime + "至" + endTime);
//            generateLine(dataSet3, "3车道车头时距图", "车头时距", startTime + "至" + endTime);
//            generateLine(dataSet5, "5车道车头时距图", "车头时距", startTime + "至" + endTime);
//            generateLine(dataSet6, "6车道车头时距图", "车头时距", startTime + "至" + endTime);
//            generateLine(dataSet7, "7车道车头时距图", "车头时距", startTime + "至" + endTime);
//
            generateLine(pzDataSet1, "1车道碰撞时距图", "碰撞时距", startTime + "至" + endTime);
            generateLine(pzDataSet2, "2车道碰撞时距图", "碰撞时距", startTime + "至" + endTime);
            generateLine(pzDataSet3, "3车道碰撞时距图", "碰撞时距", startTime + "至" + endTime);
            generateLine(pzDataSet5, "5车道碰撞时距图", "碰撞时距", startTime + "至" + endTime);
            generateLine(pzDataSet6, "6车道碰撞时距图", "碰撞时距", startTime + "至" + endTime);
            generateLine(pzDataSet7, "7车道碰撞时距图", "碰撞时距", startTime + "至" + endTime);

            generateLine(pCDataSet1, "1车道碰撞车距图", "碰撞车距", startTime + "至" + endTime);
            generateLine(pCDataSet2, "2车道碰撞车距图", "碰撞车距", startTime + "至" + endTime);
            generateLine(pCDataSet3, "3车道碰撞车距图", "碰撞车距", startTime + "至" + endTime);
            generateLine(pCDataSet5, "5车道碰撞车距图", "碰撞车距", startTime + "至" + endTime);
            generateLine(pCDataSet6, "6车道碰撞车距图", "碰撞车距", startTime + "至" + endTime);
            generateLine(pCDataSet7, "7车道碰撞车距图", "碰撞车距", startTime + "至" + endTime);
        } catch (Exception e) {

            e.printStackTrace();
        } finally {
            System.out.println("final:count = " + count);
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

    public static void main2(String[] args) {
        generateLine(GetDataset(), "折线图", "纵坐标", "时间");
    }

    /**
     * 生成折线图
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
