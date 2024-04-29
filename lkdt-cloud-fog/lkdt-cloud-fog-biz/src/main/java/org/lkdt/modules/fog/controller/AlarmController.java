package org.lkdt.modules.fog.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.lkdt.common.api.vo.Result;
import org.lkdt.common.aspect.annotation.AutoLog;
import org.lkdt.common.aspect.annotation.Idempotent;
import org.lkdt.common.system.query.QueryGenerator;
import org.lkdt.common.system.vo.LoginUser;
import org.lkdt.common.util.*;
import org.lkdt.modules.api.SysBaseRemoteApi;
import org.lkdt.modules.fog.calculator.FcFactory;
import org.lkdt.modules.fog.calculator.FogCalRedis;
import org.lkdt.modules.fog.calculator.FogCalculator;
import org.lkdt.modules.fog.calculator.FogSpaceCal;
import org.lkdt.modules.fog.entity.*;
import org.lkdt.modules.fog.mapper.AlarmRoadMapper;
import org.lkdt.modules.fog.service.*;
import org.lkdt.modules.fog.vo.AlarmCountVo;
import org.lkdt.modules.fog.vo.ChartData;
import org.lkdt.modules.system.util.DictUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author HuangJunYao
 * @date 2021/5/7
 */
@Api(tags = "告警道路表")
@RestController
@RequestMapping("/fog/alarm")
@Slf4j
public class AlarmController {

    @Autowired
    private FogCalRedis fogCalRedis;

    @Autowired
    private FcFactory fcFactory;

    @Autowired
    private FogSpaceCal fogSpaceCal;

    @Autowired
    private IAlarmRoadService alarmRoadService;

    @Autowired
    private IAlarmService alarmService;

    @Autowired
    private IAlarmNoticeService alarmNoticeService;

    @Autowired
    private IArtificalModiLogService artificalModiLogService;

    @Autowired
    private SysBaseRemoteApi sysBaseRemoteApi;

    @Autowired
    private IEquipmentService equipmentService;

    @Autowired
    private DictUtil dictUtil;

    /**
     * pagination list query
     *
     * @param alarm
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "告警道路表-分页列表查询")
    @ApiOperation(value = "告警道路表-分页列表查询", notes = "告警道路表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(Alarm alarm,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<Alarm> queryWrapper = QueryGenerator.initQueryWrapper(alarm, req.getParameterMap());
        queryWrapper.orderByDesc("begintime");
        Page<Alarm> page = new Page<Alarm>(pageNo, pageSize);
        IPage<Alarm> pageList = alarmService.page(page, queryWrapper);
        List<Alarm> alarms=new ArrayList<>();
        for (Alarm a:pageList.getRecords()) {
            Equipment equipment = equipmentService.getById(a.getEpId());
            String dictItems = sysBaseRemoteApi.queryTableDictTextByKey("zc_highway","name","id",equipment.getHwId());
            a.setHwId(dictItems+" "+equipment.getEquName());
            alarms.add(a);
        }
        pageList.setRecords(alarms);
        return Result.ok(pageList);
    }

    /**
     * pagination list query
     *
     * @return
     */
    @AutoLog(value = "获取低能见度集合")
    @ApiOperation(value = "获取低能见度get集合", notes = "获取低能见度集合")
    @GetMapping(value = "/queryMinDistanceList")
    public Result<?> queryMinDistanceList(@RequestParam String beginTime,@RequestParam String endTime) {
        QueryWrapper<Alarm> queryWrapper = new QueryWrapper<>();
        queryWrapper.between("begintime",beginTime,endTime);
        queryWrapper.orderByDesc("begintime");
        List<Alarm> pageList = alarmService.list(queryWrapper);
        List<Alarm> alarms=new ArrayList<>();
        for (Alarm a : pageList) {
            FogCalculator fogCalculator=fcFactory.getCalculator(a.getEpId());
            EquipmentModel equipment=fogCalculator.getEquipment();
            //获取路段名及桩号
            String dictItems = "";
            if (equipment != null) {
                dictItems=equipment.getHwName();
//                dictItems = sysBaseRemoteApi.queryTableDictTextByKey("zc_highway", "name", "id", equipment.getHwId());
                String type="";
                if (a.getFogType()!=null){
//                     type = sysBaseRemoteApi.queryDictTextByKey("section_warning_type", a.getFogType());
                     type = dictUtil.translateDictValue("section_warning_type","","", a.getFogType());
                }
                if (a.getBegintime() != null && a.getEndtime() != null) {
                    Long time = a.getEndtime().getTime() - a.getBegintime().getTime();
                    Date date = new Date(time);
                    String continuousTime = DateUtils.format(date, "hh小时mm分");
                    a.setContinuousTime(continuousTime);
                }
                a.setHwId(dictItems + " " + equipment.getEquName());
                a.setFogType(type);
                alarms.add(a);
            }
        }
        return Result.ok(alarms);
    }

    /**
     * 蓝色页面，确认提交请求
     *
     * @param alarmModel
     * @return
     */
    @Idempotent(keyName = "epId")
    @ResponseBody
    @RequestMapping("/confirm")
    public Result<?> confirm(@RequestBody AlarmModel alarmModel) {
        Integer sendsms = alarmModel.getSendsms();
        String phones = alarmModel.getPhones();
        String openid = alarmModel.getOpenid();
        Alarm alarm = new Alarm();
        BeanUtils.copyProperties(alarmModel, alarm);
        if ("zhiban".equals(openid)) {
            openid = ShiroUtils.getUserId();
        }
        FogCalculator cal = fcFactory.getCalculator(alarm.getEpId());
        try {
            ArtificalModiLog artificalModiLog = new ArtificalModiLog();
            artificalModiLog.setId(StringUtils.getUUID());
            artificalModiLog.setArtificialAlarmDistance(String.valueOf(alarm.getDistance()));
            if (cal.getDistance() != null) {
                artificalModiLog.setArtificialAlarmDistanceInit(String.valueOf(cal.getDistance()));
            }
            artificalModiLog.setArtificialAlarmImgUrl(alarm.getImgpath());
            artificalModiLog.setCreateTime(new Date());
            artificalModiLog.setCreateBy(openid);
            artificalModiLog.setExceptionType("0");
            artificalModiLog.setLogType("2");
            artificalModiLogService.save(artificalModiLog);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        int alarmtype = 0;
        int distance = alarm.getDistance().intValue();
        if (distance > 200) {
            alarmtype = 4;
            if (cal.isFogNow()) {
                alarm.setId(cal.getAlarmId());
                fogCalRedis.setAlarmEndTimeRedis(new Date(), cal);
            }
        } else {
            alarmtype = 1;
            if (cal.getAlarmStartTime() == null) {
                fogCalRedis.setAlarmStartTimeRedis(new Date(), cal);
            }
            alarm.setConfirmtime(new Date());
            alarm.setConfirmor(openid);
            fogCalRedis.setAlarmImgpathRedis(alarm.getImgpath(), cal);
            fogCalRedis.setAlarmImgtimeRedis(alarm.getImgtime(), cal);
            if (!cal.isFogNow()) {
                fogCalRedis.setSmDistanceRedis(distance, cal);
            }
        }
        fogCalRedis.fogOption(alarmtype, true, "", cal);
        fogCalRedis.setDistanceRedis(alarm.getDistance().intValue(), cal);
        fogCalRedis.setConfirmDistanceRedis(distance, cal);
        fogCalRedis.setAlarmLevelRedis(AlarmLevelUtil.getLevelByDist(distance), cal);
        fogCalRedis.setConfirmDateRedis(new Date(), cal);
        fogCalRedis.setUpdateTimeRedis(new Date(), cal);
        if(alarmtype == 1){
            alarm.setId(cal.getAlarmId());
        }
        alarm.setType(alarmtype);
        alarm.setEquName(cal.getEquipment().getEquName());
        if (fogSpaceCal.isFog(cal)) {
            alarm.setFogType("11");
        } else {
            alarm.setFogType("12");
        }
        try {
            log.error("路段告警发送" + alarm);
            // 更新zc_alarm_road表数据
            alarmRoadService.alarmRoad(alarm, "1".equals(sendsms), phones, openid, "1");
        } catch (Exception e) {
            log.error("路段告警发送失败", e);
            e.printStackTrace();
        }
        if (distance > 200) {
            fogCalRedis.setAlarmStartTimeRedis(null, cal);
            alarm.setLevel(cal.getLevel());
            if (cal.getSmDistance() > 0) {
                alarm.setDistance(Float.valueOf(cal.getSmDistance()));
            } else {
                alarm.setDistance(200f);
            }
            alarm.setEndtime(new Date());
            alarm.setImgtime(cal.getAlarmImgtime());
            alarm.setImgpath(cal.getAlarmImgpath());
            alarm.setLevel(AlarmLevelUtil.getLevelByDist(cal.getSmDistance()));
        }
        // 更新告警信息
        if (StringUtils.isNotEmpty(cal.getNoticeId())) {
            AlarmNotice notice = new AlarmNotice();
            notice.setId(cal.getNoticeId());
            notice.setIseffective("1");
            notice.setHandletime(new Date());
            notice.setHandler(openid);
            notice.setDistance(String.valueOf(distance));
            alarmNoticeService.updateById(notice);
        }
        AlarmRoadModel alarmRoadDO = fogCalRedis.getAlarmRoadRedis(cal);
        if (StringUtils.isNotEmpty(alarm.getId())) {
            Alarm oldalarm = alarmService.getById(alarm.getId());
            if (oldalarm != null && StringUtils.isNotEmpty(oldalarm.getId())) {
                alarmService.updateById(alarm);
            } else {
                fogCalRedis.setAlarmEndTimeRedis(null, cal);
                alarm.setRoadAlarmId(alarmRoadDO.getId());
                alarm.setBegintime(new Date());
                alarmService.save(alarm);
            }
        } else if (alarmtype == 1) {
            fogCalRedis.setAlarmEndTimeRedis(null, cal);
            alarm.setId(StringUtils.getUUID());
            alarm.setBegintime(new Date());
            alarm.setRoadAlarmId(alarmRoadDO.getId());
            alarmService.save(alarm);
        }
        if (alarmtype == 4) {
            fogCalRedis.setSmDistanceRedis(distance, cal);
        }
        return Result.ok();
    }

    /**
     * 查询待确认摄像头列表
     *
     * @return
     */
    @ResponseBody
    @GetMapping("/getConfirmList")
    public String getConfirmList() {
        JSONObject resultObject = new JSONObject();
        //未告警的待确认
        JSONArray awaitingConfirmWithoutWarning = new JSONArray();
        //已告警的待确认
        JSONArray alarmedToBeConfirmed = new JSONArray();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        try {
            Set<FogCalculator> fogCalList = fcFactory.needConfigByMan(true);
            for (FogCalculator fogCalculator : fogCalList) {
                // 遍历摄像头数据
                JSONObject jsonObject = new JSONObject();
                //能见度<=200米的待解除不显示
                jsonObject.put("distance", fogCalculator.getDistance());// 距离
                jsonObject.put("epId", fogCalculator.getEpId());// 摄像头ID
                jsonObject.put("equName", fogCalculator.getEquipment().getEquName());// 桩号
                jsonObject.put("address", fogCalculator.getEquipment().getEquLocation());// 地址
                jsonObject.put("state", fogCalculator.getEquipment().getState());// 地址
                jsonObject.put("cameraType", fogCalculator.getCameraType());// 是否需要确认
                jsonObject.put("hwId", fogCalculator.getEquipment().getHwId());// 是否需要确认
                jsonObject.put("hwName", fogCalculator.getEquipment().getHwName());// 获取所属路段名
                jsonObject.put("cameraTypeDesc", AlarmLevelUtil.getAlarmType(fogCalculator.getCameraType()));
                try {
                    Date begintime_db = fogCalculator.getAlarmStartTime();
                    String beginTime = begintime_db != null ? simpleDateFormat.format(begintime_db) : "";
                    if (StringUtils.isEmpty(beginTime)) {
                        beginTime = simpleDateFormat.format(now);
                    }
                    jsonObject.put("begintime", beginTime);// 开始时间
                    long minute = (now.getTime() - begintime_db.getTime()) / 1000 / 60;
                    if (minute > 60) {
                        double hours = Math.round(minute / 60.0);
                        jsonObject.put("forTime", hours + "小时");// 持续时间
                    } else {
                        jsonObject.put("forTime", minute + "分钟");// 持续时间
                    }
                } catch (Exception e) {
                    jsonObject.put("begintime", simpleDateFormat.format(now));// 开始时间
                    jsonObject.put("forTime", "分钟");// 持续时间
                }
                //摄像机状态
                jsonObject.put("stateDesc", sysBaseRemoteApi.queryDictTextByKey("equipment_state", fogCalculator.getEquipment().getState()));
                //图片
                jsonObject.put("imgpath", fogCalculator.getImgpath());
                jsonObject.put("imgtime", fogCalculator.getImgtime());
                jsonObject.put("imgYMD", DateUtils.format(fogCalculator.getImgtime(), "yyyy-M-d"));
                jsonObject.put("hwName", fogCalculator.getEquipment().getHwName());
                //等级
                String alarmLevel = fogCalculator.getAlarmLevel();
                if (fogCalculator.isFogNow()) {
                    if (StringUtils.isEmpty(alarmLevel)) {
                        jsonObject.put("alarmLevel", AlarmLevelUtil.getLevelDescByDist(fogCalculator.getSmDistance()));
                    } else {
                        try {
                            jsonObject.put("alarmLevel", AlarmLevelUtil.getLevelDescByDist(fogCalculator.getSmDistance()));
//                            int level = Integer.parseInt(alarmLevel);
//                            jsonObject.put("alarmLevel", AlarmLevelUtil.getAlarmLevelDesc(level));
                        } catch (Exception e) {
                            jsonObject.put("alarmLevel", alarmLevel);
                        }
                    }
                    alarmedToBeConfirmed.add(jsonObject);
                } else {
                    jsonObject.put("alarmLevel", "无告警");
                    awaitingConfirmWithoutWarning.add(jsonObject);
                }
            }
            resultObject.put("noWarning", awaitingConfirmWithoutWarning);
            resultObject.put("hasWarning", alarmedToBeConfirmed);
            return resultObject.toJSONString();
        } catch (Exception e) {
            return resultObject.toJSONString();
        }
    }

    /**
     * 首页统计：根据摄像头id统计一天的可见距离
     *
     * @param date 日期
     * @param epId 摄像头id
     * @return
     * @throws Exception 异常
     */
    @ResponseBody
    @GetMapping("/distanceStatistic")
    public String distanceStatistic(String startDate, String endDate, @RequestParam("date") String date, @RequestParam("epId") String epId) {
        // 统计日期
        return alarmService.getDistanceStatistic(startDate, endDate, date, epId);
    }

    @PostMapping("/checkAlarmRoad")
    @ResponseBody
    public Result<?> checkAlarmRoad(@RequestBody Alarm alarm) {
        String s = alarmRoadService.checkAlarmRoad(alarm);
        if (StringUtils.isNotEmpty(s)) {
            return Result.ok(s);
        }
//		alarmRoadService.alarmRoad(alarm, false, "");‘
        return Result.error("操作失败");
    }

    @AutoLog("告警日志")
    @ApiOperation(value = "告警日志-查询告警日志", notes = "查询告警日志")
    @GetMapping("/getDistanceStatistic")
    public Result<?> getDistanceStatistic(@RequestParam("date") String date, @RequestParam("epId") String epId) throws Exception {

        Result<String> result = new Result<>();
        result.setResult(alarmService.getDistanceStatistic(date, epId));
        result.setSuccess(true);
        return result;
    }

    /**
     * 根据告警摄像头ID统计指定时间段的可视距离
     *
     * @param beginTime
     * @param endTime
     * @param epId
     * @return
     */
    @ResponseBody
    @GetMapping("/alarmDistanceStatistic")
    public String alarmDistanceStatistic(String beginTime, String endTime, String epId) {
        // 统计日期
        return alarmService.alarmDistanceStatistic(beginTime, endTime, epId);
    }

    /**
     * 保存
     */
    @ResponseBody
    @PostMapping("/adjustAlarm")
    public Result<?> adjustAlarm(@RequestBody Alarm alarm, String epId) {
        alarm.setEpId(epId);
        if (alarm.getBegintime() == null || alarm.getEndtime() == null) {
            return Result.error("时间格式不匹配");
        } else if (alarm.getBegintime().getTime() >= alarm.getEndtime().getTime()) {
            return Result.error("结束时间不能大于开始时间");
        }

        alarm.setLevel(alarm.getLevel());
        if (StringUtils.isEmpty(alarm.getId()) || StringUtils.equals("-", alarm.getId())) {
            alarm.setId(StringUtils.getUUID());
            alarm.setIseffective("1");
            alarm.setFogType("11");
            alarmService.save(alarm);
        } else {
            if (!alarmService.updateById(alarm)) {
                alarm.setIseffective("1");
                alarm.setFogType("11");
                alarmService.save(alarm);
            }

        }

        return Result.ok();

    }

    /**
     * 按摄像头统计告警数
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("/getAlarmCountGroupEpId")
    public Result getAlarmCountGroupEpId() {
        LoginUser user = ShiroUtils.getUser();
        if (user == null) {
            return Result.error("登录过期，请重新登录");
        }
        Map<String, Object> params = new HashMap<String, Object>();
        if (!user.isAdmin()) {
            List<String> list = user.getHwIds();
            String hwIds = String.join(",", list);
            params.put("hwIds", hwIds);
        }
        List<AlarmCountVo> counts = alarmService.getAlarmCountGroupEpId(params);
        return Result.ok(counts);
    }

    /**
     * 年度路段告警统计
     * @param yearString 年份
     * @return Result
     */
    @RequestMapping("/queryRoadAlarmByYear/{year}")
    public @ResponseBody Result<?> queryRoadAlarmByYear(@PathVariable("year") String yearString) {
        //1. 获取登录用户信息，进行权限控制
        LoginUser user = ShiroUtils.getUser();
        if (user == null) {
            return Result.error("登录过期，请重新登录");
        }
        Map<String, Object> params = new HashMap<>();
        List<String> userHwIds = user.getHwIds();
        if (!user.isAdmin()) {
            String hwIds = String.join(",", userHwIds);
            params.put("hwIds", hwIds);
        }
        params.put("yearString",yearString);
        try {
            List<Map<String, Object>> list2 = alarmService.queryRoadAlarmByYear(params);
            return Result.ok(list2);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 告警按日、月统计
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("/getAlarmCount")
    public Result getAlarmCount() {
        LoginUser user = ShiroUtils.getUser();
        if (user == null) {
            return Result.error("登录过期，请重新登录");
        }
        Map<String, Object> params = new HashMap<>();
        List<String> list = user.getHwIds();
        if (!user.isAdmin()) {
            String hwIds = String.join(",", list);
            params.put("hwIds", hwIds);
        }
        int todayCount = alarmService.queryListToday(params);
        int monthCount = alarmService.queryListMonth(params);
        int todayMileage = todayCount * 2;
        int monthMileage = monthCount * 2;
        Map<String, Object> returnMap = new HashMap<String, Object>();
        returnMap.put("todayCount", todayCount);
        returnMap.put("monthCount", monthCount);
        returnMap.put("todayMileage", todayMileage);
        returnMap.put("monthMileage", monthMileage);

        if (user.isAdmin()) {
            int bindCount = alarmRoadService.queryBindCount();
            int roadMonth = alarmRoadService.queryMonthCount();
            returnMap.put("roadMonth", roadMonth);
            returnMap.put("unBindCount", roadMonth - bindCount);
        }
        return Result.ok(returnMap);
    }

    /**
     * 告警按日、月统计
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("/getAlarmTypeCount")
    public Result getAlarmTypeCount() {
        List<ChartData> list = alarmService.queryAlarmByroadAlarmType();
        List type = new ArrayList();
        List count = new ArrayList();
        for (ChartData q : list) {
            type.add(q.getType());
            count.add(q.getSum());
        }
        ChartData chartData = new ChartData();
        chartData.setSeriesData(count);
        chartData.setXAxisData(type);
        return Result.ok(chartData);
    }


    @ResponseBody
    @GetMapping("/getfogStat")
    public Result getFogStat() {

        List<FogCalculator> foglist = fcFactory.getCalList(true);
//        Collections.sort(foglist);
        if (foglist == null) {
            return null;
        }
        JSONArray results = new JSONArray();

        List<String> list = new ArrayList<>();
        for (FogCalculator f : foglist) {
            if (f.getEquipment().getHwId() == null) {
                continue;
            }
            if (!list.contains(f.getEquipment().getHwId())) {
                list.add(f.getEquipment().getHwId());
            }
        }
        Collections.sort(list);
        if (list.size() > 0) {
            for (String hwIdStr : list) {
                boolean bool = true;
                JSONObject result = new JSONObject();
                JSONArray xAxisData = new JSONArray();
                JSONArray epIds = new JSONArray();
                JSONArray data = new JSONArray();
                String title = "";
                for (FogCalculator fc : foglist) {
                    if (fc != null && hwIdStr.equals(fc.getEquipment().getHwId())) {

                        if (bool) {
                            bool = false;
                            title = fc.getEquipment().getHwName();
                        }

                        if (fc.getDistance() == null || fc.getDistance() == 0) {
                            continue;
                        }
                        if (fc.getDistance() != null) {
                            if (fc.getDistance() > 300) {
                                data.add(fc.getDistance() > 300 ? 300 : fc.getDistance());
                            } else if (!fc.isFogNow() && !ShiroUtils.isZhiBan()) {
                                data.add(300);
                            } else {
                                data.add(fc.getDistance());
                            }

                        } else {
                            data.add(0);
                        }

                        xAxisData.add(fc.getEquipment().getEquName());

                        epIds.add(fc.getEpId());
                    }
                }
                result.put("xAxisData", xAxisData);
                result.put("data", data);
                result.put("epIds", epIds);
                result.put("title", title);
                results.add(result);
            }
            return Result.ok(results);
        }
        return Result.ok(results);
    }

    /**
     * 获取告警数据
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("/getAlarmCals")
    public Result getAlarmCals() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        boolean isAdmin = true;
        if (ShiroUtils.getUser() != null) {
            isAdmin = ShiroUtils.isAdmin();
        }

        Long now = System.currentTimeMillis();
        //一级公路下第一个摄像头列表
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = null;
        //已告警所有摄像头
        List<FogCalculator> listFogCalAlarmed = new ArrayList<>();
        if (!isAdmin) {
            //获取计算器摄像头列表
            listFogCalAlarmed = fcFactory.getCalculators();
        } else {
            listFogCalAlarmed = fcFactory.getCalculators();
        }

        try {
            for (FogCalculator fogCalculator : listFogCalAlarmed) {
                //遍历摄像头数据
                if (StringUtils.isEmpty(fogCalculator.getAlarmId())) {
                    continue;
                }
                if (!isAdmin && !fogCalculator.isFogNow()) {
                    continue;
                }
                if (isAdmin && fogCalculator.getCameraType() < 1) {
                    continue;
                }else if((now - fogCalculator.getImgtime().getTime()) > fcFactory.checkFogValTime){
                    continue;
                }

                //当前用户绑定的路段为空，取所有路段，否则取用户绑定的路段
                jsonObject = new JSONObject();
                if (fogCalculator.getDistance() != null) {
                    if (fogCalculator.getDistance() <= 0) {
                        continue;
                    }
                    jsonObject.put("distance", fogCalculator.getDistance());//距离
                } else if (fogCalculator.getEquipment().getEquName().indexOf("路段") > -1 && fogCalculator.isFogNow()) {
                    jsonObject.put("distance", fogCalculator.getAlarmDistance());//距离
                } else {
                    continue;

                }
                if (!"0".equals(fogCalculator.getEquipment().getState()) && !"9".equals(fogCalculator.getEquipment().getState())) {
                    continue;
                }
                jsonObject.put("cameraType", fogCalculator.getCameraType());//1：有雾未确认 3：升级未确认 5：解除未确认
                jsonObject.put("lon", fogCalculator.getEquipment().getLon());//经度
                jsonObject.put("lat", fogCalculator.getEquipment().getLat());//纬度
                jsonObject.put("epId", fogCalculator.getEpId());//摄像头ID
                if (fogCalculator.getDistance() != null) {
                    jsonObject.put("imgfn", fogCalculator.getImgpath());
                } else {
                    jsonObject.put("imgfn", -1);
                }
                jsonObject.put("equName", fogCalculator.getEquipment().getEquName());//桩号
                jsonObject.put("address", fogCalculator.getEquipment().getEquLocation());//地址
                jsonObject.put("fogType", fogSpaceCal.isFog(fogCalculator));//雾霾类型
                try {
                    Date begintime_db = fogCalculator.getAlarmStartTime();
                    String beginTime = begintime_db != null ? simpleDateFormat.format(begintime_db) : "";
                    if (StringUtils.isEmpty(beginTime)) {
                        beginTime = simpleDateFormat.format(now);
                    }
                    jsonObject.put("begintime", beginTime);//开始时间
                    long minute = (now - begintime_db.getTime()) / 1000 / 60;
                    if (minute > 60) {
                        double hours = Math.round(minute / 60.0);
                        jsonObject.put("forTime", hours + "小时");//持续时间
                    } else {
                        jsonObject.put("forTime", minute + "分钟");//持续时间
                    }
                } catch (Exception e) {
                    log.error("首页获取告警时间出现错误 方法：selectRealAlarmInfoList()", e);
                    jsonObject.put("begintime", "");//开始时间
                    jsonObject.put("forTime", "分钟");//持续时间
                }

                jsonArray.add(jsonObject);

            }
        } catch (Exception e) {
            log.error("未知错误,method:selectRealAlarmInfoList", e);
            return Result.error("获取告警数据错误");
        }
        return Result.ok(jsonArray);
    }

    /**
     * 根据大风告警点ID统计指定时间段的风信息
     *
     * @param beginTime
     * @param endTime
     * @param epId
     * @return
     */
    @ResponseBody
    @GetMapping("/alarmDistanceStatisticWind")
    public String alarmDistanceStatisticWind(String beginTime, String endTime, String epId) {
        // 统计日期
        return alarmService.alarmDistanceStatisticWind(beginTime, endTime, epId);
    }

    /**
     * 根据时间和道路获取能见度报表
     * @param beginTime
     * @param endTime
     * @param hwId
     * @return
     */
    @ResponseBody
    @GetMapping("/getVisibilityReport")
    public JSONObject getVisibilityReport(String beginTime, String endTime, String hwId) {

        JSONObject jsonObject=alarmService.getVisibilityReport(beginTime,endTime,hwId);

        return jsonObject;
    }

}
