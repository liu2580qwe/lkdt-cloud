package org.lkdt.modules.radar.supports.radarTools.shell;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * mysql数据迁移
 *
 * 备注：数据量很大的情况下在读取300万行以上的数据时会导致效率急剧下降，不建议使用
 */
public class RadarDataMigration {

    public static final int MaxRowSize = 10000;

    public static final String urlRead = "jdbc:mysql://192.168.3.100:3306/radar?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8&useSSL=false" +
            "&zeroDateTimeconvertToNull&useCursorFetch=true&defaultFetchSize=1000";

    public static final String urlWrite = "jdbc:mysql://192.168.3.100:3306/radar_test_20210506?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8&useSSL=false" +
            "&zeroDateTimeconvertToNull&useCursorFetch=true&defaultFetchSize=1000";

    public static final String user = "root";
    public static final String password = "root";

    public static void main(String[] args) {

        Connection connectionRead = null ;
        PreparedStatement preparedStatementRead = null ;

        Connection connectionWrite = null ;
        PreparedStatement preparedStatementWrite = null ;

        int pageSize = MaxRowSize;
        int pageNum = 0;//

        try {

            //加载注册驱动
            Class.forName("com.mysql.jdbc.Driver");

            //获取连接
            connectionRead = DriverManager.getConnection(urlRead, user, password);
            connectionWrite = DriverManager.getConnection(urlWrite, user, password);
            connectionRead.setAutoCommit(false);
            connectionWrite.setAutoCommit(false);

            //写数据
            preparedStatementWrite = connectionWrite.prepareStatement(
                    "insert into zc_ld_radar_frame_target_all_20210506backup(car_id, radar_id, target_id, s_x, s_y, v_x, v_y, a_x, a_y, lane_num, car_type, event, car_length, date_time, nano_second, partition_key)" +
                            "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");

            while(true){
                long start = System.currentTimeMillis();
                //读数据
                preparedStatementRead = connectionRead.prepareStatement("select * from zc_ld_radar_frame_target_all_stop_20210506 limit ?, ?",
                        ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
                preparedStatementRead.setInt(1, pageNum*pageSize);
                preparedStatementRead.setInt(2, pageSize);
                ResultSet resultSet = preparedStatementRead.executeQuery();
                pageNum ++;
                //不存在记录
                if(!resultSet.isBeforeFirst()){
                    break;
                }

                int count = 0;
                while(resultSet.next()) {

                    ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
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
                    String nanoSecond = resultSet.getString("nano_second");
                    //int partitionKey = resultSet.getInt("partition_key");

//                    System.out.println(carId + "," + radarId + "," + targetId + "," + sX + "," + sY + "," + vX + "," + vY + "," + aX + "," + aY + "," + laneNum
//                            + "," + carType + "," + event + "," + carLength + "," + dateTime + "," + nanoSecond);

                    preparedStatementWrite.setString(1, carId);
                    preparedStatementWrite.setString(2, radarId);
                    preparedStatementWrite.setInt(3, targetId);
                    preparedStatementWrite.setDouble(4, sX);
                    preparedStatementWrite.setDouble(5, sY);
                    preparedStatementWrite.setDouble(6, vX);
                    preparedStatementWrite.setDouble(7, vY);
                    preparedStatementWrite.setDouble(8, aX);
                    preparedStatementWrite.setDouble(9, aY);
                    preparedStatementWrite.setInt(10, laneNum);
                    preparedStatementWrite.setInt(11, carType);
                    preparedStatementWrite.setInt(12, event);
                    Timestamp timestamp = new Timestamp(dateTime.getTime());
                    preparedStatementWrite.setInt(13, carLength);
                    preparedStatementWrite.setTimestamp(14, timestamp);
                    preparedStatementWrite.setString(15, nanoSecond);
                    preparedStatementWrite.setInt(16, Integer.valueOf(sdf.format(dateTime)));
                    preparedStatementWrite.addBatch();
                    count ++;
                    if(count == MaxRowSize){
                        //入库
                        preparedStatementWrite.executeBatch();
                        connectionWrite.commit();
                        System.out.print("progress:pageSize = " + pageSize + ", pageNum = " + pageNum);
                        count = 0;
                    }

                }
                if(count != 0){
                    //入库
                    preparedStatementWrite.executeBatch();
                    connectionWrite.commit();
                    System.out.print("progress:pageSize = " + pageSize + ", pageNum = " + pageNum);
                }
                long end = System.currentTimeMillis();
                System.out.println(" 耗时：" + (end - start) + "时间：" + new Date());
            }

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }finally {
            System.out.println("final:pageSize = " + pageSize + ", pageNum = " + pageNum);
            //释放资源
            if (preparedStatementRead != null) {
                try {
                    preparedStatementRead.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connectionRead != null) {
                try {
                    connectionRead.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (preparedStatementWrite != null) {
                try {
                    preparedStatementWrite.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connectionWrite != null) {
                try {
                    connectionWrite.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
