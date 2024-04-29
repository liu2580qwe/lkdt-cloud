package org.lkdt.modules.fog.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.lkdt.common.util.DateUtils;
import org.lkdt.common.util.ShiroUtils;
import org.lkdt.modules.fog.calculator.FcFactory;
import org.lkdt.modules.fog.calculator.FogCalculator;
import org.lkdt.modules.fog.calculator.WcFactory;
import org.lkdt.modules.fog.calculator.WindCalculator;
import org.lkdt.modules.fog.entity.Alarm;
import org.lkdt.modules.fog.entity.AlarmLog;
import org.lkdt.modules.fog.mapper.AlarmMapper;
import org.lkdt.modules.fog.mongodb.MongoLogTemplate;
import org.lkdt.modules.fog.service.IAlarmService;
import org.lkdt.modules.fog.service.IEquipmentService;
import org.lkdt.modules.fog.vo.AlarmCountVo;
import org.lkdt.modules.fog.vo.ChartData;
import org.lkdt.modules.wind.domain.AlarmDO;
import org.lkdt.modules.wind.entity.WindLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Description: 告警表
 * @Author: jeecg-boot
 * @Date: 2021-04-27
 * @Version: V1.0
 */
@Service
public class AlarmServiceImpl extends ServiceImpl<AlarmMapper, Alarm> implements IAlarmService {
    private Logger logger = LoggerFactory.getLogger(AlarmServiceImpl.class);
    @Autowired
    private AlarmMapper alarmMapper;
    @Autowired
    private MongoLogTemplate mongoLogTemplate;
    @Autowired
    FcFactory fcFactory;
    @Autowired
    private WcFactory wcFactory;
    @Autowired
    private IAlarmService alarmService;
    @Autowired
    private IEquipmentService equipmentService;

    @Override
    public List<Alarm> selectByMainId(String mainId) {
        return alarmMapper.selectByMainId(mainId);
    }
    @Override
    public List<Alarm> selectByAlarmRoadIdAndEndTime(String mainId) {
        return alarmMapper.selectByMainId(mainId);
    }

    @Override
    public Alarm selectById(String id) {
        return alarmMapper.selectById(id);
    }

    @Override
    public List<Alarm> queryAlarmCalenderData(String hwid, String start, String end) {
        return alarmMapper.queryAlarmCalenderData(hwid, start, end);
    }

    @Override
    public List<Alarm> queryDayAlarm(String start, String level) {
        return alarmMapper.queryDayAlarm(start, level);
    }

    /**
     * 获取时间段内的日志文件
     *
     * @param beginTime
     * @param endTime
     * @return
     */
    @Override
    public List<AlarmLog> getFileList(String beginTime, String endTime, String epId) {

        if (beginTime.compareTo(endTime) > 0) {    //beginTime不能大于endTime
            return new ArrayList<AlarmLog>();
        }

        String startDate = beginTime.substring(0, 7).replaceAll("-", "");
        ;
        String endDate = endTime.substring(0, 7).replaceAll("-", "");
        ;
        List<AlarmLog> alarmLoglist = new ArrayList<AlarmLog>();
        if (startDate.equals(endDate)) {
            // 告警日志文件
//			File file = new File(System.getProperty("user.dir") + "//applog//alarm-" + tdate + "//" + tdate + ".log");
//			filelist.add(file);
            alarmLoglist = mongoLogTemplate.find(endDate, epId, beginTime, endTime);
        } else {
//			File file = new File(System.getProperty("user.dir") + "//applog//alarm-" + tdate + "//" + tdate + ".log");
//			File file2 = new File(
//					System.getProperty("user.dir") + "//applog//alarm-" + newdate + "//" + newdate + ".log");
//			filelist.add(file);
//			filelist.add(file2);
            List<AlarmLog> alarmLoglist1 = mongoLogTemplate.find(startDate, epId, beginTime, endTime);
            List<AlarmLog> alarmLoglist2 = mongoLogTemplate.find(endDate, epId, beginTime, endTime);
            alarmLoglist.addAll(alarmLoglist1);
            alarmLoglist.addAll(alarmLoglist2);
        }
        return alarmLoglist;
    }

    /**
     * 获取时间段内的日志文件
     *
     * @param beginTime
     * @param endTime
     * @return
     */
    @Override
    public List<WindLog> getWindFileList(String beginTime, String endTime, String windId) {

        if (beginTime.compareTo(endTime) > 0) {    //beginTime不能大于endTime
            return new ArrayList<WindLog>();
        }
        if (!beginTime.endsWith(".000")) {
            beginTime = beginTime + ".000";
        }
        if (!endTime.endsWith(".000")) {
            endTime = endTime + ".000";
        }
        List<WindLog> alarmLoglist = new ArrayList<WindLog>();
        alarmLoglist = mongoLogTemplate.findWind(windId, beginTime, endTime);
        return alarmLoglist;
    }

    @Override
    public String getDistanceStatistic(String beginTime, String endTime, String date, String epId) {
        if (StringUtils.isEmpty(beginTime) || StringUtils.isEmpty(endTime)) {
            //缺省
            return this.getDistanceStatistic(date, epId);
        }
        if (beginTime.compareTo(endTime) > 0) {    //beginTime不能大于endTime
            return "";
        }
        if (!beginTime.endsWith(".000")) {
            beginTime = beginTime + ".000";
        }
        if (!endTime.endsWith(".000")) {
            endTime = endTime + ".000";
        }
        try {
            List<AlarmLog> alarmLoglist = getFileList(beginTime, endTime, epId);
            Map<String, Alarm> alarmMap = null;
            try {
                alarmMap = getAlarmMap(DateUtils.parseDate(beginTime, "yyyy-MM-dd HH:mm:ss.SSS"), DateUtils.parseDate(endTime, "yyyy-MM-dd HH:mm:ss.SSS"));
            } catch (ParseException e) {
                log.error("时间格式错误，请使用yyyy-MM-dd HH:mm:ss.SSS", e);
                return "时间格式错误，请使用yyyy-MM-dd HH:mm:ss.SSS";
            }
            // 读取文件
            return read_file_byLine(alarmLoglist, alarmMap, epId, "LOG");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("getDistanceStatistic异常", e);
        }
        //缺省
        return this.getDistanceStatistic(date, epId);
    }

    @Override
    public String alarmDistanceStatistic(String beginTime, String endTime, String epId) {
        if (endTime == null || StringUtils.isEmpty(endTime) || StringUtils.equalsIgnoreCase(endTime, "null")) {
            endTime = DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN);
        }
        if (beginTime.compareTo(endTime) > 0) {    //beginTime不能大于endTime
            return "";
        }
        if (!beginTime.endsWith(".000")) {
            beginTime = beginTime + ".000";
        }
        if (!endTime.endsWith(".000")) {
            endTime = endTime + ".000";
        }
        List<AlarmLog> alarmLoglist = getFileList(beginTime, endTime, epId);
        Map<String, Alarm> alarmMap = null;
        try {
            alarmMap = getAlarmMap(DateUtils.parseDate(beginTime, "yyyy-MM-dd HH:mm:ss.SSS"), DateUtils.parseDate(endTime, "yyyy-MM-dd HH:mm:ss.SSS"));
        } catch (ParseException e) {
            log.error("时间格式错误，请使用yyyy-MM-dd HH:mm:ss.SSS", e);
            return "时间格式错误，请使用yyyy-MM-dd HH:mm:ss.SSS";
        }
        // 读取文件
        return read_file_byLine(alarmLoglist, alarmMap, epId, "ALARM");
    }

    @Override
    public String getDistanceStatistic(String date, String epId) {
        Date now = new Date();
        if (!date.endsWith(".000")) {
            date = date + ".000";
        }
        String newdate = DateUtils.format(now, "yyyy-MM-dd HH:mm:ss.SSS");
        List<AlarmLog> alarmLoglist = getFileList(date, newdate, epId);
        Map<String, Alarm> alarmMap = null;
        try {
            alarmMap = getAlarmMap(DateUtils.parseDate(date, "yyyy-MM-dd HH:mm:ss.SSS"), now);
        } catch (ParseException e) {
            log.error("时间格式错误，请使用yyyy-MM-dd HH:mm:ss.SSS", e);
            return "时间格式错误，请使用yyyy-MM-dd HH:mm:ss.SSS";
        }
        // 读取文件
        return read_file_byLine(alarmLoglist, alarmMap, epId, "LOG");
    }

    private Map<String, Alarm> getAlarmMap(Date date, Date now) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.between("begintime", date, now);
        List<Alarm> alarmList = alarmService.list(queryWrapper);
        Map<String, Alarm> alarmMap = new HashMap<>();
        for (Alarm alarm : alarmList) {
            alarmMap.put(alarm.getId(), alarm);
        }
        return alarmMap;
    }

    /**
     * 首页统计：逐行读取
     *
     * @param alarmLoglist 告警日志list
     * @param alarmMap     告警map
     * @param epId         摄像头Id
     * @param type         区分X轴时间显示的类型，LOG / ALARM
     * @return
     */

    public String read_file_byLine(List<AlarmLog> alarmLoglist, Map<String, Alarm> alarmMap, String epId, String type) {
        // 返回数据
        JSONObject jsonObject = new JSONObject();
        String xAxis = "";
        String xAxisWithImgURL = "";
        String alarmId_xAxis = "";
        String series = "";
        //普通用户数据
        String general_series = "";
        //源数据(src_)：未作调整的数据
        String src_xAxis = "";
        String src_series = "";
        JSONObject imgpaths = new JSONObject();
        JSONObject src_imgpaths = new JSONObject();
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            // 获取文件流
            int count = 0;
            int[] fogValues = new int[5];
            int fogValueSum = 0;
            boolean isZhiBan = ShiroUtils.isZhiBan();
            Map<String, Object> minLineMap = new HashMap<String, Object>();
            List<Map<String, Object>> lineMapList = new ArrayList<Map<String, Object>>();
            for (AlarmLog alarmLog : alarmLoglist) {
                if (alarmLog == null || alarmLog.getId() == null) {
                    continue;
                }

                String dateTime = alarmLog.getDateTime();
                String distanceS = alarmLog.getModifyVal();

                //源
                src_xAxis += dateTime.substring(11, 16) + ",";
                src_series += distanceS + ",";
                //图片名数组
                src_imgpaths.put(dateTime.substring(11, 16).replace(":", ""), alarmLog.getImgName());

                if (StringUtils.equalsIgnoreCase(type, "ALARM")) {    //告警报表
                    minLineMap = new HashMap<String, Object>();
                    minLineMap.put("lineAtt", alarmLog);
                    minLineMap.put("avgVal", Integer.valueOf(distanceS));
                    lineMapList.add(minLineMap);
                } else {

                    int fogValue = Integer.valueOf(distanceS);
                    fogValues[count] = fogValue;
                    fogValueSum = fogValueSum + fogValue;
                    if (count > 0) {
                        if (fogValue < fogValues[count - 1]) {
                            minLineMap = new HashMap<String, Object>();
                            minLineMap.put("lineAtt", alarmLog);
                            minLineMap.put("avgVal", fogValue);
                        }
                    } else {
                        minLineMap = new HashMap<String, Object>();
                        minLineMap.put("lineAtt", alarmLog);
                        minLineMap.put("avgVal", fogValue);
                    }
                    if (count == 4) {
                        minLineMap.put("avgVal", fogValueSum / 5);
                        lineMapList.add(minLineMap);
                        count = 0;
                        fogValues = new int[5];
                        fogValueSum = 0;
                        continue;
                    }
                    count++;

                }
            }
            SimpleDateFormat sdfYYYYMMDDHM = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            for (Map<String, Object> lineMap : lineMapList) {
                AlarmLog alarmLog = (AlarmLog) lineMap.get("lineAtt");
                String dateTime = alarmLog.getDateTime();
                String distanceS = alarmLog.getModifyVal();
                int fogValue = (int) lineMap.get("avgVal");

                imgpaths.put(dateTime.substring(11, 16).replace(":", ""), alarmLog.getImgName());
                xAxisWithImgURL += alarmLog.getImgName();

                if (StringUtils.equalsIgnoreCase(type, "ALARM")) {
                    xAxis += dateTime.substring(0, 16) + ",";

                    alarmId_xAxis += StringUtils.isEmpty(alarmLog.getAlarmId()) ? dateTime.substring(0, 16) + "=" + "-" + ","
                            : dateTime.substring(0, 16) + "=" + alarmLog.getAlarmId() + ",";
                } else {
                    xAxis += dateTime.substring(11, 16) + ",";
                    alarmId_xAxis += StringUtils.isEmpty(alarmLog.getAlarmId()) ? dateTime.substring(11, 16) + "=" + "-" + ","
                            : dateTime.substring(11, 16) + "=" + alarmLog.getAlarmId() + ",";
                }

                if (Integer.valueOf(distanceS) <= 200) {
                    fogValue = Integer.valueOf(distanceS);
                }

                int general_fogValue = fogValue;
                if (fogValue <= 200 && StringUtils.equalsIgnoreCase(type, "LOG")) {
//						int random=(int) (Math.random()*50+250);
                    long time = sdf.parse(dateTime).getTime();
                    int random = 300;
                    int general_random = 300;
                    if (isZhiBan) {
                        random = fogValue;
                    }
                    String alarmId = alarmLog.getAlarmId();
                    if (StringUtils.isNotEmpty(alarmId)) {
                        //根据id获取Alarm对象
                        Alarm alarm = alarmMap.get(alarmId);
                        if (alarm != null) {
                            Date begintime = alarm.getBegintime();
                            if (time > begintime.getTime()) {

                            } else {
                                fogValue = random;
                                general_fogValue = general_random;
                            }
                        } else {
                            fogValue = random;
                            general_fogValue = general_random;
                        }

                    } else {
                        fogValue = random;
                        general_fogValue = general_random;
                    }
                }
                if (fogValue > 500) {
                    series += 500 + ",";
                    general_series += 500 + ",";
                } else {
                    series += fogValue + ",";
                    general_series += general_fogValue + ",";
                }

            }

            // 图表最后一行固定展示当前数据(报表不展示当前时间的数据)
            FogCalculator cal = fcFactory.getCalculator(epId);
            if (StringUtils.isNotEmpty(epId) && cal != null
                    && cal.getDistance() != null
                    && !StringUtils.equalsIgnoreCase(type, "ALARM")) {
//				FogCalculatorResult lastval = fcFactory.getCalculator(epId).getDistance();
                String x = DateUtils.format(cal.getImgtime(), DateUtils.DATE_TIME_PATTERN);
                int fogValue = cal.getDistance();

                Date sdfYYYYMMDDHMstr = sdfYYYYMMDDHM.parse(x.substring(0, 16));
                xAxisWithImgURL += cal.getImgpath() + ",";

                if (StringUtils.equalsIgnoreCase(type, "ALARM")) {
                    xAxis += x.substring(0, 16) + ",";
                    alarmId_xAxis += StringUtils.isEmpty(cal.getAlarmId()) ? x.substring(0, 16) + "=" + "-" + ","
                            : x.substring(0, 16) + "=" + cal.getAlarmId() + ",";
                    src_xAxis += x.substring(0, 16) + ",";
                } else {
                    xAxis += x.substring(11, 16) + ",";
                    alarmId_xAxis += StringUtils.isEmpty(cal.getAlarmId()) ? x.substring(11, 16) + "=" + "-" + ","
                            : x.substring(11, 16) + "=" + cal.getAlarmId() + ",";
                    src_xAxis += x.substring(11, 16) + ",";
                }
                if (fogValue > 500) {
                    series += 500 + ",";
                    src_series += 500 + ",";
                } else {
                    src_series += fogValue + ",";
                    if (fogValue <= 300 && ShiroUtils.getUser() != null && !ShiroUtils.isZhiBan()) {
                        if (!cal.isFogNow()) {
                            fogValue = 300;
                        }
                    }
                    //普通用户
                    int general_fogValue = fogValue;
                    if (general_fogValue <= 300 &&  ShiroUtils.isZhiBan()) {
                        if (!fcFactory.getCalculator(epId).isFogNow()) {
                            general_fogValue = 300;
                        }
                    }
                    series += fogValue + ",";
                    general_series += general_fogValue + ",";
                }
                imgpaths.put(x.substring(11, 16).replace(":", ""), cal.getImgpath());

            }
            String time=cal.getImgpath().split("/")[3];
            jsonObject.put("xAxisWithImgURL", StringUtils.isEmpty(xAxisWithImgURL) ? xAxisWithImgURL : xAxisWithImgURL.substring(0, xAxisWithImgURL.length() - 1));
            jsonObject.put("xAxis", StringUtils.isEmpty(xAxis) ? xAxis : xAxis.substring(0, xAxis.length() - 1));
            jsonObject.put("alarmId_xAxis", StringUtils.isEmpty(alarmId_xAxis) ? alarmId_xAxis : alarmId_xAxis.substring(0, alarmId_xAxis.length() - 1));
            jsonObject.put("series", StringUtils.isEmpty(series) ? series : series.substring(0, series.length() - 1));
            jsonObject.put("general_series", StringUtils.isEmpty(general_series) ? general_series : general_series.substring(0, general_series.length() - 1));
            jsonObject.put("imgpaths", imgpaths);
            jsonObject.put("imgtime",time);
            //源数据(src_)：未作调整的数据
            jsonObject.put("src_xAxis", StringUtils.isEmpty(src_xAxis) ? src_xAxis : src_xAxis.substring(0, src_xAxis.length() - 1));
            jsonObject.put("src_series", StringUtils.isEmpty(src_series) ? src_series : src_series.substring(0, src_series.length() - 1));
            jsonObject.put("src_imgpaths", src_imgpaths);
            return jsonObject.toJSONString();
        } catch (Exception e) {
            logger.error("出现异常：", e);
            return jsonObject.toJSONString();
        }
    }

    /**
     * 当月告警统计
     *
     * @param map
     * @return
     */
    @Override
    public List<AlarmCountVo> getAlarmCountGroupEpId(Map<String, Object> map) {
        List<AlarmCountVo> list = alarmMapper.getAlarmCountGroupEpId(map);
        return list;
    }

    /**
     * 年告警统计
     *
     * @param map
     * @return
     */
    @Override
    public List<Map<String, Object>> queryRoadAlarmByYear(Map<String, Object> map) {
        return alarmMapper.queryRoadAlarmByYear(map);
    }

    @Override
    public List<AlarmDO> selectInfoByCondition(Map<String, Object> map) {
        return alarmMapper.selectInfoByCondition(map);
    }

    @Override
    public Integer queryListToday(Map<String, Object> map) {
        return alarmMapper.queryListToday(map);
    }

    @Override
    public Integer queryListMonth(Map<String, Object> map) {
        return alarmMapper.queryListMonth(map);
    }

    @Override
    public List<ChartData> queryAlarmByroadAlarmType() {
        return alarmMapper.queryAlarmByroadAlarmType();
    }

    @Override
    public List<AlarmDO> queryEquAlarm(Map<String, Object> map) {
        return alarmMapper.queryEquAlarm(map);
    }

    @Override
    public List<Alarm> selectInfoByEpId(Map<String, Object> map) {
        return alarmMapper.selectInfoByEpId(map);
    }

    @Override
    public String alarmDistanceStatisticWind(String beginTime, String endTime, String epId) {
        if (endTime == null || StringUtils.isEmpty(endTime) || StringUtils.equalsIgnoreCase(endTime, "null")) {
            endTime = DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN);
        }
        List<WindLog> filelist = getWindFileList(beginTime, endTime, epId);
        return read_file_byLineWind(filelist, beginTime, endTime, epId);
    }

    @Override
    public JSONObject getVisibilityReport(String beginTime, String endTime, String hwId) {
        JSONObject jsonObject=new JSONObject();
        List equs=new ArrayList();
        LinkedHashSet time=new LinkedHashSet();
        List series=new ArrayList();
        //根据道路id获取设备
        List<FogCalculator> equipments = fcFactory.getCalculatorsByHwId(hwId);
        int i=0;
        for (FogCalculator equ:equipments) {
            equs.add(equ.getEquipment().getEquName());
            //查询告警日志
            FogCalculator cal = fcFactory.getCalculator(equ.getEquipment().getId());
            i++;
            List<AlarmLog> alarmLogs=this.getFileList(beginTime,endTime,equ.getEquipment().getId());

            int t=0;
            for (AlarmLog a:alarmLogs) {
                List data=new ArrayList();
                data.add(i);
                data.add(t);
                data.add(Integer.parseInt(a.getModifyVal()));
                series.add(data);
                time.add(a.getDateTime().substring(11,16));
                t++;
            }
        }
        jsonObject.put("xAxis",time);
        jsonObject.put("yAxis",equs);
        jsonObject.put("series",series);
        return jsonObject;
    }

    /**
     * 大风告警报表--大风时间线/波谷图可用
     *
     * @param filelist
     * @param beginDate
     * @param endDate
     * @param epId
     * @return
     */
    public String read_file_byLineWind(List<WindLog> filelist, String beginDate, String endDate, String epId) {
        // 返回数据
        JSONObject jsonObject = new JSONObject();
        String xAxis = "";
        String series = "";
        JSONObject imgpaths = new JSONObject();
        List<String[]> intList = new ArrayList<String[]>();
        try {
            // 获取文件流
            for (WindLog alarmLog : filelist) {
                // 行数据
                // 逐行读取
                DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String[] w = new String[3];

                w[0] = String.valueOf(getWinddVal(Float.parseFloat(alarmLog.getWinds())));
                w[1] = alarmLog.getTime().substring(11, 16);
                w[2] = alarmLog.getWinds();
                intList.add(w);
                xAxis += alarmLog.getTime().substring(11, 16) + ",";
                if (Float.valueOf(alarmLog.getWinds()) > 60) {
                    series += 40 + ",";
                } else {
                    series += alarmLog.getWinds() + ",";
                }

            }
            WindCalculator cal = wcFactory.getCalculator(epId);
            if (cal != null) {
                JSONArray predictionArray = cal.getPrediction();
                if (predictionArray != null) {
                    for (int i = 0; i < 12; i++) {
                        JSONObject predictionJson = predictionArray.getJSONObject(i);
                        if (predictionJson != null) {
                            String[] w = new String[3];
                            w[0] = String.valueOf(getWinddVal(Float.parseFloat(predictionJson.getString("windd"))));
                            w[1] = predictionJson.getString("time").substring(11, 16);
                            Double winds = predictionJson.getDouble("winds");
                            w[2] = String.valueOf((float) Math.round(predictionJson.getDouble("winds") * 10) / 10);
                            intList.add(w);
                            xAxis += predictionJson.getString("time").substring(11, 16) + ",";
                            if (winds > 60) {
                                series += 40 + ",";
                            } else {
                                series += winds + ",";
                            }
                        }

                    }
                }
            }

            jsonObject.put("xAxis", StringUtils.isEmpty(xAxis) ? xAxis : xAxis.substring(0, xAxis.length() - 1));
            jsonObject.put("series", StringUtils.isEmpty(series) ? series : series.substring(0, series.length() - 1));
            jsonObject.put("imgpaths", imgpaths);
            jsonObject.put("intList", intList);
            return jsonObject.toJSONString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getWinddVal(float windd) {
        if (windd > 337.5 || windd <= 22.5) {
            return 4;
        } else if (windd > 22.5 && windd <= 67.5) {
            return 5;
        } else if (windd > 67.5 && windd <= 112.5) {
            return 6;
        } else if (windd > 112.5 && windd <= 157.5) {
            return 7;
        } else if (windd > 157.5 & windd <= 202.5) {
            return 0;
        } else if (windd > 202.5 && windd <= 247.5) {
            return 1;
        } else if (windd > 247.5 && windd <= 292.5) {
            return 2;
        } else if (windd > 292.5 && windd <= 337.5) {
            return 3;
        }
        return 0;
    }
}
