package org.lkdt.modules.fog.calculator;

import org.lkdt.common.util.StringUtils;
import org.lkdt.modules.fog.entity.EquipmentModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 *  团雾检测计算工具
 *  @author zhangzb
 *  @crateDate 2019-04-30
 */
@Component
public class FogSpaceCal {

    @Autowired
    FcFactory fcFactory;

    private static final BigDecimal R = new BigDecimal(6378137.0);//地球半径 单位：米

    private static final Double earthR = 6378137.0;//地球半径 单位：米

    /*定义同一区域摄像头范围*/
    private static final Double nearBySize = 1000.0;//区域范围 单位：米

    /*同一个区域摄像头报警个数设置，小于或等于此个数的区域中有团雾*/
    private static final Integer equipmentAlertSize = 2;

    /**
     * 是否团雾
     * @param fogCalculator 传入设备对象
     * @return
     */
    public <fogCalculator> boolean isFog(FogCalculator fogCalculator){
        int alertNum = 0;
//        ConcurrentHashMap<String, FogCalculator>  mapFogCalculator = FcFactory.fogNowMap;
        Set<FogCalculator> fogSet = fcFactory.needConfigByMan(true);
        String epId = fogCalculator.getEpId();
        try {
        	 for(FogCalculator cal : fogSet){
                 if(!cal.getEpId().equalsIgnoreCase(epId)
                         && isNearBy(fogCalculator,cal)){
                     alertNum ++;
                 }
             }
             if(alertNum <= equipmentAlertSize){
                 return true;
             }
        }catch (Exception e) {
        	   return false;
		}
       
        return false;
    }

    /**
     * 是否团雾
     * @param equipment 传入设备对象
     * @param listEquip 所有报警的对象
     * @return
     */
    public static boolean isFog(EquipmentModel equipment, List<EquipmentModel> listEquip){
        int alertNum = 0;
        for(EquipmentModel e:listEquip){
            if(!equipment.getId().equalsIgnoreCase(e.getId()) && isNearBy(equipment,e)){
                alertNum ++;
            }
        }
        if(alertNum <= equipmentAlertSize){
            return true;
        }
        return false;
    }

    /**
     * 是否在同一区域
     * @param fogCalculator 选中的点
     * @param newFogCalculator 比较的点
     * @return
     */
    private static boolean isNearBy(FogCalculator fogCalculator,FogCalculator newFogCalculator) {
        try{
            if(StringUtils.isNotEmpty(fogCalculator.getEquipment().getLon()) && StringUtils.isNotEmpty(fogCalculator.getEquipment().getLat())
                    && StringUtils.isNotEmpty(newFogCalculator.getEquipment().getLon()) && StringUtils.isNotEmpty(newFogCalculator.getEquipment().getLat())){
                if (getDistanceByLonLat(Double.valueOf(fogCalculator.getEquipment().getLon()), Double.valueOf(fogCalculator.getEquipment().getLat()), Double.valueOf(newFogCalculator.getEquipment().getLon()), Double.valueOf(newFogCalculator.getEquipment().getLat())) <= nearBySize) {
                    return true;
                }
            }
        } catch (NumberFormatException ne){
            ne.printStackTrace();
            return false;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return false;
    }

    /**
     * 是否在同一区域
     * @param e 选中的点
     * @param newE 比较的点
     * @return
     */
    private static boolean isNearBy(EquipmentModel e,EquipmentModel newE) {
        if (getDistanceByLonLat(Double.valueOf(e.getLon()), Double.valueOf(e.getLat()), Double.valueOf(newE.getLon()), Double.valueOf(newE.getLat())) <= nearBySize) {
            return true;
        }
        return false;
    }

    /**
     * 计算地球两点距离
     * @param longitude1 经度1
     * @param latitude1 纬度1
     * @param longitude2 经度2
     * @param latitude2 纬度2
     * @return 距离【单位：米】
     */
    public static double getDistanceByLonLat(double longitude1, double latitude1, double longitude2, double latitude2) {
        double Lat1 = rad(latitude1); // 纬度
        double Lat2 = rad(latitude2);
        double lat_ = Lat1 - Lat2;//两点纬度之差
        double lon_ = rad(longitude1) - rad(longitude2); //经度之差
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(lat_ / 2), 2) + Math.cos(Lat1) * Math.cos(Lat2) * Math.pow(Math.sin(lon_ / 2), 2)));//计算两点距离的公式
        s = s * earthR;//弧长乘地球半径（半径为米）
        s = Math.round(s * 10000d) / 10000d;//精确距离的数值
        return s;
    }

    /**
     * 角度转换成弧度
     * @param d 角度
     * @return 弧度
     */
    private static double rad(double d) {
        return d * Math.PI / 180.00; //角度转换成弧度
    }

    /**
     * 计算地球两点距离
     * 【BigDecimal计算】
     * @param longitude1 经度1
     * @param latitude1 纬度1
     * @param longitude2 经度2
     * @param latitude2 纬度2
     * @return 距离【单位：米】
     */
    private static BigDecimal getDistanceByLonLat(BigDecimal longitude1, BigDecimal latitude1, BigDecimal longitude2, BigDecimal latitude2) {
        BigDecimal TWO = new BigDecimal(2);
        BigDecimal Lat1 = rad(latitude1); // 纬度
        BigDecimal Lat2 = rad(latitude2);
        BigDecimal a = Lat1.subtract(Lat2);//两点纬度之差
        BigDecimal b = rad(longitude1).subtract(rad(longitude2)); //经度之差
        BigDecimal s = new BigDecimal(2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a.divide(TWO).doubleValue()), 2) + Math.cos(Lat1.doubleValue()) * Math.cos(Lat2.doubleValue()) * Math.pow(Math.sin(b.divide(TWO).doubleValue()), 2))));//计算两点距离的公式
        s = s.multiply(R);//弧长乘地球半径（半径为米）
        //s = Math.round(s * 10000d) / 10000d;//精确距离的数值
        return s;
    }

    /**
     * 角度转换成弧度
     * BigDecimal计算
     * @param d 角度
     * @return 弧度
     */
    private static BigDecimal rad(BigDecimal d) {
        return d.multiply(new BigDecimal(Math.PI)).divide(new BigDecimal(180.00),BigDecimal.ROUND_HALF_UP); //角度转换成弧度
    }

    /**
     * 计算时间长度【单位：秒】
     * @param start
     * @param end
     * @return
     */
    private static String calcTimeLength(Long start,Long end){
        return (end - start)/1000d + "秒";
    }

    /*
        Main测试：
     */
    public static void main(String[] args){
        long start = new Date().getTime();
        System.out.println(getDistanceByLonLat(107.125879,29.727948,107.135652,29.736478));
        System.out.println(calcTimeLength(start,new Date().getTime()));
        start = new Date().getTime();
        System.out.println(getDistanceByLonLat(new BigDecimal(107.125879),new BigDecimal(29.727948),new BigDecimal(107.135652),new BigDecimal(29.736478)));
        System.out.println(calcTimeLength(start,new Date().getTime()));
        /*****************************************************/
        //107.125879,29.727948
        EquipmentModel equip1 = new EquipmentModel();
        equip1.setId("1");
        equip1.setLon("107.125879");
        equip1.setLat("29.727948");
        //107.135652,29.736478
        EquipmentModel equip2 = new EquipmentModel();
        equip2.setId("2");
        equip2.setLon("107.135652");
        equip2.setLat("29.736478");
        //118.823685,32.067847
        EquipmentModel equip3 = new EquipmentModel();
        equip3.setId("3");
        equip3.setLon("118.823685");
        equip3.setLat("32.067847");
        //118.832309,32.077149
        EquipmentModel equip4 = new EquipmentModel();
        equip4.setId("4");
        equip4.setLon("118.832309");
        equip4.setLat("32.077149");
        //118.838058,32.080576
        EquipmentModel equip5 = new EquipmentModel();
        equip5.setId("5");
        equip5.setLon("118.838058");
        equip5.setLat("32.080576");
        //118.858755,32.093302
        EquipmentModel equip6 = new EquipmentModel();
        equip6.setId("6");
        equip6.setLon("118.858755");
        equip6.setLat("32.093302");
        //118.882901,32.033081
        EquipmentModel equip7 = new EquipmentModel();
        equip7.setId("7");
        equip7.setLon("118.882901");
        equip7.setLat("32.033081");
        //118.889225,32.036509
        EquipmentModel equip8 = new EquipmentModel();
        equip8.setId("8");
        equip8.setLon("118.889225");
        equip8.setLat("32.036509");
        //118.884626,32.035529
        EquipmentModel equip9 = new EquipmentModel();
        equip9.setId("9");
        equip9.setLon("118.884626");
        equip9.setLat("32.035529");
        //118.883476,32.036509
        EquipmentModel equip10 = new EquipmentModel();
        equip10.setId("10");
        equip10.setLon("118.883476");
        equip10.setLat("32.036509");
        List<EquipmentModel> list = new ArrayList<EquipmentModel>();
        list.add(equip1);
        list.add(equip2);
        list.add(equip3);
        list.add(equip4);
        list.add(equip5);
        list.add(equip6);
        list.add(equip7);
        list.add(equip8);
        list.add(equip9);
        list.add(equip10);
        System.out.println("*********************************************");
        //System.out.println(getDistanceByLonLat(107.125879,29.727948,107.125879,29.727948));
        for(int i = 0; i < list.size(); i++){
            System.out.print("第"+i+"个摄像头equip"+(i+1)+"("+list.get(i).getLon()+","+list.get(i).getLat()+")"+"，附近的有：");
            for(EquipmentModel e2:list){
                if(!e2.getId().equalsIgnoreCase(list.get(i).getId()) && isNearBy(list.get(i),e2)){
                    System.out.print("("+e2.getLon()+","+e2.getLat()+")");
                }
            }
            System.out.println();
        }
    }

}
