package org.lkdt.modules.fog.calculator;

import cn.hutool.core.bean.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.lkdt.common.system.vo.HighwayModel;
import org.lkdt.common.system.vo.LoginUser;
import org.lkdt.common.util.*;
import org.lkdt.modules.fog.entity.AlarmRoad;
import org.lkdt.modules.fog.entity.Equipment;
import org.lkdt.modules.fog.service.IAlarmRoadService;
import org.lkdt.modules.fog.service.IEquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class CalInit {

    /**
     * 数据时间
     **/
    public final ConcurrentHashMap<String, Long> fogValTime = new ConcurrentHashMap<String, Long>();

    @Autowired
    IEquipmentService equipmentService;

//    @Autowired
//    FogvalueService fogvalueService;

//    @Autowired
//    EquipExceptionDao equipExceptionDao;

    @Autowired
    private HighWayUtil highWayUtil;


    @Autowired
    private IAlarmRoadService alarmRoadService;

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    SpringContextUtils springContextUtils;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    FogCalRedis fogCalRedis;

    @Autowired
    private FcFactory fcFactory;

    @PostConstruct
    public void init() {
//		thisFcFactory = this;

        //spring容器提前注入
        springContextUtils.setApplicationContext(applicationContext);
/** 路段计算器列表 */
        alarmRoadInit();

        caculatorInit();

//		highWayInit();


//		ConcurrentHashMap<Long, AlarmRoad> fogRoadCalculatorMap =
//		caculatorInit(fogRoadCalculatorMap);
    }

    public boolean initRedisCache() {

        try {
            Set<Object> alarmEpIds = redisUtil.sGet(fcFactory.ALARM_EPIDS_CACHE_NAME);
            if (alarmEpIds != null && alarmEpIds.size() != 0) {
                log.error("有告警，禁止初始化缓存");
                return false;
            }

            Set<Object> unAlarmEpIds = redisUtil.sGet(fcFactory.UNCONFIRM_ALARM_EPIDS_CACHE_NAME);
            if (unAlarmEpIds != null && unAlarmEpIds.size() != 0) {
                log.error("有告警，禁止初始化缓存");
                return false;
            }

            Set<Object> hwIds = redisUtil.sGet(fcFactory.ALL_HWIDS_CACHE_NAME);
            for (Object o : hwIds) {
                redisUtil.del(fcFactory.HIGHWAY_CACHE_NAME + o);
                redisUtil.del(fcFactory.CAMERAS_CACHE_NAME + o);
                redisUtil.del(fcFactory.ALARM_ROAD_CACHE_NAME + o);
            }

            Set<Object> epIds = redisUtil.sGet(fcFactory.ALL_EQIDS_CACHE_NAME);
            for (Object o : epIds) {
                redisUtil.del(fcFactory.CAMERA_CACHE_NAME + o);
                redisUtil.del(fcFactory.LATE_DISTANCE_CACHE_NAME + o);
            }

            redisUtil.del(fcFactory.ALL_HWIDS_CACHE_NAME);
            redisUtil.del(fcFactory.ALL_EQIDS_CACHE_NAME);
            redisUtil.del(fcFactory.ALARM_EPIDS_CACHE_NAME);
            redisUtil.del(fcFactory.ALARM_HWIDS_CACHE_NAME);

            caculatorInit();
            alarmRoadInit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 计算器信息初始化
     */
    private boolean caculatorInit() {
        try {
            List<Equipment> cameras = equipmentService.list();
            for (Equipment cd : cameras) {
                FogCalculator fogCalculator = createCalculator(cd);
                if (fogCalculator != null) {
                    putCalculator(fogCalculator, cd.getId());
                }

            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 排序链接数据
     *
     * @param listFogCalculator
     */

    /**
     * 路段告警信息初始化
     */
    private boolean alarmRoadInit() {
        try {
            // 获取摄像头列表
            List<HighwayModel> highways = highWayUtil.getAllList();
            // 处理摄像头数据
            for (HighwayModel hw : highways) {
                //路段告警信息初始化
                Map<String, Object> oldMap = redisUtil.hmget(fcFactory.ALARM_ROAD_CACHE_NAME + hw.getId());
                if (oldMap.isEmpty()) {
                    AlarmRoad alarmRoad = createAlarmRoad(hw);
                    Map<String, Object> alarmRoadMap = BeanUtil.beanToMap(alarmRoad);
                    redisUtil.hmset(fcFactory.ALARM_ROAD_CACHE_NAME + hw.getId(), alarmRoadMap);
                }
                //路段信息初始化
                Map<String, Object> hwMap = BeanUtil.beanToMap(hw);
                redisUtil.hmset(fcFactory.HIGHWAY_CACHE_NAME + hw.getId(), hwMap);
                //所有路段
                redisUtil.sSet(fcFactory.ALL_HWIDS_CACHE_NAME, hw.getId());
            }
            return true;
            // 排序处理
        } catch (Exception e) {
            log.error("未知异常：路段告警信息初始化失败", e);
            return false;
        }
    }

    private AlarmRoad createAlarmRoad(HighwayModel hw) {
//        AlarmRoad alarmRoad = alarmRoadService.getNotEndtimeByHwId(hw.getId());
//        AlarmRoad alarmRoad = null;
//        if (alarmRoad == null) {
        AlarmRoad alarmRoad = new AlarmRoad();
        alarmRoad.setHwId(hw.getId());
        alarmRoad.setAlarmLevel("0");
//        }
//        alarmRoad.setAllCameras(new ArrayList<>());
        return alarmRoad;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    /**
     * 根据用户权限获取计算器信息列表
     */
    public List<FogCalculator> getCalculators() {

        LoginUser user = ShiroUtils.getUser();
        ConcurrentHashMap<String, FogCalculator> calMap = new ConcurrentHashMap<String, FogCalculator>();
        List<String> hwIds = new ArrayList<>();
        if (user == null || user.getId() == null || user.isAdmin()) {
            for (Object o : redisUtil.sGet(fcFactory.ALL_HWIDS_CACHE_NAME)) {
                hwIds.add(String.valueOf(o));
            }
        } else {
            hwIds = user.getHwIds();
        }
        List<FogCalculator> fogCalculatorListForUser = new ArrayList<FogCalculator>();
        for (String hwId : hwIds) {
            List<FogCalculator> call = getCalculatorsByHwId(hwId);
            if (call != null) {
                fogCalculatorListForUser.addAll(call);
            }
        }
        return fogCalculatorListForUser;

    }

    /**
     * 根据用户权限获取hwIds
     */
    public List<String> getHwIds() {

        LoginUser user = ShiroUtils.getUser();
        ConcurrentHashMap<String, FogCalculator> calMap = new ConcurrentHashMap<String, FogCalculator>();
        List<String> hwIds = new ArrayList<>();
        if (user == null || user.getId() == null || user.isAdmin()) {
            for (Object o : redisUtil.sGet(fcFactory.ALL_HWIDS_CACHE_NAME)) {
                hwIds.add(String.valueOf(o));
            }
        } else {
            hwIds = user.getHwIds();
        }
        return hwIds;

    }

    /**
     * 获取路段告警信息
     */
    public AlarmRoad getAlarmRoadByHwId(String hwId) {
        try {
            Map<String, Object> wayMap = redisUtil.hmget(fcFactory.ALARM_ROAD_CACHE_NAME + hwId);
            return BeanUtil.mapToBean(wayMap, AlarmRoad.class, false);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public List<FogCalculator> getCalculatorsByHwId(String HwId) {
        try {
            List<FogCalculator> returnCals = new ArrayList<FogCalculator>();
            Set<Object> calSet = redisUtil.zSetGetAll(fcFactory.CAMERAS_CACHE_NAME + HwId);
            if (calSet == null) {
                return returnCals;
            }
            List<String> list = new ArrayList<>();
            for (Object o : calSet) {
                if (o != null) {
                    list.add(fcFactory.CAMERA_CACHE_NAME + (String) o);

//                    FogCalculator cal = getCalculator((String) o);
//                    if (cal != null) {
//                        returnCals.add(cal);
//                    }
                }
            }
            List<LinkedHashMap> hashList = redisUtil.hashGetBatch(list);
            for (LinkedHashMap o : hashList) {
                if (o.size() > 0) {
                    returnCals.add(BeanUtils.mapToBean(o, new FogCalculator()));
                }
            }
            return returnCals;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<FogCalculator> getCalculatorsByHwIdNormal(String HwId) {
        try {
            List<FogCalculator> calList = getCalculatorsByHwId(HwId);
            List<FogCalculator> resultList = new ArrayList<>();
            for (FogCalculator cal : calList) {
                if (calIsEffective(cal, true)) {
                    resultList.add(cal);
                }
            }
            return resultList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<FogCalculator> getCalculatorsByCalSet(Set<Object> calSet) {
        try {
            List<FogCalculator> returnCals = new ArrayList<FogCalculator>();
            if (calSet == null) {
                return returnCals;
            }
            List<String> list = new ArrayList<>();
            for (Object o : calSet) {
                if (o != null) {
                    list.add(fcFactory.CAMERA_CACHE_NAME + (String) o);

//                    FogCalculator cal = getCalculator((String) o);
//                    if (cal != null) {
//                        returnCals.add(cal);
//                    }
                }
            }
            List<LinkedHashMap> hashList = redisUtil.hashGetBatch(list);
            for (LinkedHashMap o : hashList) {
                if (o.size() > 0) {
                    returnCals.add(BeanUtils.mapToBean(o, new FogCalculator()));
                }
            }
            return returnCals;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<AlarmRoad> getAlarmRoadByarSet(Set<Object> arSet) {
        try {
            List<AlarmRoad> returnCals = new ArrayList<AlarmRoad>();
            if (arSet == null) {
                return returnCals;
            }
            List<String> list = new ArrayList<>();
            for (Object o : arSet) {
                if (o != null) {
                    list.add((String) o);

//                    FogCalculator cal = getCalculator((String) o);
//                    if (cal != null) {
//                        returnCals.add(cal);
//                    }
                }
            }
            List<LinkedHashMap> hashList = redisUtil.hashGetBatch(list);
            for (LinkedHashMap o : hashList) {
                if (o.size() > 0) {
                    returnCals.add(BeanUtil.mapToBean(o, AlarmRoad.class, false));
                }
            }
            return returnCals;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取路段信息
     */
    public HighwayModel getHighwayByHwId(String hwId) {
        try {
            Map<String, Object> wayMap = redisUtil.hmget(fcFactory.HIGHWAY_CACHE_NAME + hwId);
            return BeanUtil.mapToBean(wayMap, HighwayModel.class, false);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 根据路段ID获取包括子节点的所有有数据摄像头
     *
     * @param hwId
     * @return
     */
    public List<FogCalculator> getCalculatorsByHwIdIdPath(String hwId) {
        List<FogCalculator> returnCals = new ArrayList<FogCalculator>();
        for (String userHwId : getHwIds()) {
            HighwayModel highway = getHighwayByHwId(userHwId);
            if (highway.getIdpath().contains("," + hwId + ",")) {
                List<FogCalculator> cals = getCalculatorsByHwId(String.valueOf(highway.getId()));
                returnCals.addAll(cals);
            }
        }
        return returnCals;
    }

    /**
     * 根据路段ID获取包括子节点的所有摄像头
     *
     * @param hwId
     * @return
     */
    public List<FogCalculator> getAllCalculatorsByHwIdIdPath(String hwId) {
        List<FogCalculator> returnCals = new ArrayList<FogCalculator>();
        List<FogCalculator> cals = getCalculators();
        for (FogCalculator cal : cals) {
            if (cal.getEquipment().getIdpath().contains("," + String.valueOf(hwId) + ",")) {
                returnCals.add(cal);
            }
        }
        return returnCals;
    }

    public Set<String> getParentHwIdsByHwIds(List<String> hwIds) {
        Set<String> parentHwIds = new HashSet<>();
        for (String hwId : hwIds) {
            String[] idPath = getHighwayByHwId(hwId).getIdpath().split(",");
            if (idPath.length >= 3) {
                parentHwIds.add(idPath[2]);
            }
        }
        return parentHwIds;
    }

    /**
     * 获取计算器信息
     */
    public FogCalculator getCalculator(String epId) {
        try {
            if (StringUtils.isEmpty(epId)) {
                return null;
            }
            Map<String, Object> calMap = redisUtil.hmget(fcFactory.CAMERA_CACHE_NAME + epId);
            return BeanUtils.mapToBean(calMap, new FogCalculator());
        } catch (Exception e) {
            log.error("-----缓存获取失败 epId=" + epId);
            e.printStackTrace();
            return null;
        }


    }

    /**
     * 移除计算器信息
     */
    public boolean removeCalculator(String epId, String hwId) {

        try {
            Long l = redisUtil.zSetRemove(fcFactory.HIGHWAY_CACHE_NAME + hwId, epId);
            boolean b = redisUtil.del(fcFactory.CAMERA_CACHE_NAME + epId);
            return l != 0 && b;
        } catch (Exception e) {
            log.error("-----缓存删除失败 epId=" + epId);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 添加计算器信息
     */
    public boolean putCalculator(FogCalculator fogCalculator, String epId) {
        try {
            Map<String, Object> oldMap = redisUtil.hmget(fcFactory.CAMERA_CACHE_NAME + epId);
            if (oldMap != null && oldMap.size() != 0) {
                log.error("-----摄像头已存在缓存, epId=" + epId);
                return true;
            }
            fogCalculator.setEpId(epId);
            Map<String, Object> calMap = BeanUtils.beanToMap(fogCalculator);
            redisUtil.hmset(fcFactory.CAMERA_CACHE_NAME + epId, calMap);

            Integer[] ints = FogCalculator.parseEquName(fogCalculator.getEquipment().getEquName());
            if (ints != null && ints.length == 2 && ints[0] != null && ints[1] != null) {
                double score = ints[0] * 1000 + ints[1];//K100+222=100222
                redisUtil.zSetAdd(fcFactory.CAMERAS_CACHE_NAME + fogCalculator.getEquipment().getHwId(), epId, score);
                //所有摄像头
                redisUtil.sSet(fcFactory.ALL_EQIDS_CACHE_NAME, epId);

                return true;
            } else {
                log.error("-----摄像头名称不符合规则, equName=" + fogCalculator.getEquipment().getEquName());
                return false;
            }

        } catch (Exception e) {
            log.error("-----缓存添加失败 epId=" + epId);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 更新计算器信息
     */
    public boolean updateCalculator(FogCalculator fogCalculator, String epId) {
        try {
            fogCalculator.setEpId(epId);
            Map<String, Object> calMap = BeanUtils.beanToMap(fogCalculator);
            redisUtil.hmset(fcFactory.CAMERA_CACHE_NAME + epId, calMap);
            return true;
        } catch (Exception e) {
            log.error("-----缓存添加失败 epId=" + epId, e);
            e.printStackTrace();
            return false;
        }
    }

    public Set<FogCalculator> needConfigByMan(boolean isall) {
        Set<FogCalculator> result = new HashSet<FogCalculator>();
        long now = System.currentTimeMillis();
        int count = 0;
        long a = System.currentTimeMillis();
        Set<Object> unconfirmAlarmEpIds = redisUtil.sGet(fcFactory.UNCONFIRM_ALARM_EPIDS_CACHE_NAME);
        if (unconfirmAlarmEpIds == null) {
            return null;
        }
        long b = System.currentTimeMillis();
        List<FogCalculator> calList = getCalculatorsByCalSet(unconfirmAlarmEpIds);
        if (calList == null) {
            return null;
        }
        long c = System.currentTimeMillis();

        Set<Object> stringSet = redisUtil.getLikeKeys(fcFactory.ALARM_ROAD_CACHE_NAME);
        List<AlarmRoad> AlarmRoadList = getAlarmRoadByarSet(stringSet);
        Map<String, AlarmRoad> AlarmRoadMap = new HashMap<>();
        long d1 = System.currentTimeMillis();

        for (AlarmRoad road : AlarmRoadList) {
            AlarmRoadMap.put(road.getHwId(), road);
            if (Integer.parseInt(road.getAlarmLevel()) < 2) {
                continue;
            }
            FogCalculator lastCal = check100(this.getCalculatorsByHwId(road.getHwId().toString()));
            if (lastCal != null) {
                fogCalRedis.fogOption(5, false, "0", lastCal);
                result.add(lastCal);
            }
        }
        for (FogCalculator cal : calList) {
//            if (cal.getHwId() == 43) {
//                continue;
//            }
            AlarmRoad alarmRoad = AlarmRoadMap.get(cal.getEquipment().getHwId());
            if (cal.getDistance() == null) {
                continue;
            }
            // 0:未确认
            if ((cal.getCameraType() == 1 && cal.getDistance() <= 200) || cal.getCameraType() == 5) {
                count++;
                if (isall) {
                    result.add(cal);
                } else {
                    if (count >= 5 || now - cal.getUpdateTime().getTime() > 10 * 60 * 1000) {
                        result.add(cal);
                    }
                }

            } else if ("0".equals(cal.getEquipment().getState()) && alarmRoad != null && cal.getDistance() != null
                    && Integer.parseInt(alarmRoad.getAlarmLevel()) > 0// 当前路段有雾
                    && Integer.parseInt(AlarmLevelUtil.getLevelByDist(cal.getDistance())) > Integer
                    .parseInt(alarmRoad.getAlarmLevel())// 摄像头告警等级大于路段告警等级
                    && cal.getDistance() > 0) {
                // 判断等级提升
//				cal.fogOption(2, false, "0");
                result.add(cal);
            }
        }
        return result;
    }

    // 判断能见度回升
    public FogCalculator check100(List<FogCalculator> camList) {
        FogCalculator lastCal = null;
        for (FogCalculator cal : camList) {
            if (cal.getDistance() != null && cal.isFogNow()) {
                if (cal.getDistance() <= fcFactory.getPhaseRelease()) {
                    return null;
                } else if (cal.getDistance() <= 200) {
                    lastCal = cal;
                }
            }
        }
        return lastCal;
    }

    // 判断能见度回升
    public String checkFogValTime() {
        Long now = System.currentTimeMillis();
        for (Entry<String, Long> entry : fogValTime.entrySet()) {
            if ((now - entry.getValue()) > fcFactory.checkFogValTime && !"AI-FOG-NSX".equals(entry.getKey())) {
                return entry.getKey();
            }
        }
        return null;

    }

    /**
     * @param normal：是否只查询正常
     * @return
     */
    public List<FogCalculator> getCalList(boolean normal) {
        try {
            List<FogCalculator> fogval = getCalculators();
            List<FogCalculator> foglist = new ArrayList<FogCalculator>();
            for (FogCalculator cal : fogval) {
                if (!calIsEffective(cal, normal)) {
                    continue;
                }
                foglist.add(cal);
            }
            return foglist;

        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.toString());
            return null;
        }

    }

    //是否正常
    public boolean calIsEffective(FogCalculator cal, boolean normal) {
        if (cal == null || cal.getDistance() == null || cal.getDistance() <= 0) {
            return false;
        }
        if (System.currentTimeMillis() - cal.getImgtime().getTime() > 24 * 60 * 60 * 1000) {
            return false;
        }
        if (normal) {
            if (!"0".equals(cal.getEquipment().getState())) {
                return false;
            }
        }
        return true;
    }

    public boolean cal(FogCalculator cal, boolean normal) {
        if (cal == null || cal.getDistance() == null || cal.getDistance() <= 0) {
            return false;
        }
        if (System.currentTimeMillis() - cal.getImgtime().getTime() > 24 * 60 * 60 * 1000) {
            return false;
        }
        if (normal) {
            if (!"0".equals(cal.getEquipment().getState())) {
                return false;
            }
        }
        return true;
    }

    /**
     * 摄像头信息>>>计算器实体对象
     *
     * @param camera 摄像头实体
     * @return
     */
    public FogCalculator createCalculator(Equipment camera) {
        // 返回对象
        FogCalculator calEquipment = new FogCalculator();
        // 路段信息
//		AlarmRoad road = fogRoadCalculatorMap.get(camera.getHwId());
//		calEquipment.setAlarmRoad(road);
//		road.getAllCameras().add(calEquipment);
        // 摄像头id(AI专用)
//		calEquipment.setEpId(camera.getEpId());
        // 设备主键
        calEquipment.setEpId(camera.getId());
        // 设备Code
        calEquipment.getEquipment().setEquCode(camera.getEquCode());
        // 设备名称
        calEquipment.getEquipment().setEquName(camera.getEquName());
        // 直播流地址
//        calEquipment.setRtsp(camera.getRtsp());
        // 设备位置
        calEquipment.getEquipment().setEquLocation(camera.getEquLocation());
        // 关联公路
        calEquipment.getEquipment().setHwId(camera.getHwId());
        HighwayModel highwayModel = highWayUtil.getById(camera.getHwId());
        if (highwayModel == null) {
            return null;
        }
        calEquipment.getEquipment().setHwName(highwayModel.getName());
        // 关联公路实体idpath
        calEquipment.getEquipment().setIdpath(highwayModel.getIdpath());
        // 经度
        calEquipment.getEquipment().setLon(camera.getLon());
        // 纬度
        calEquipment.getEquipment().setLat(camera.getLat());
//        // 调整后经度
//        calEquipment.setLonAfterAdjust(camera.getLonAfterAdjust());
//        // 调整后纬度
//        calEquipment.setLatAfterAdjust(camera.getLatAfterAdjust());
        // 摄像头描述
        calEquipment.getEquipment().setEquLocation(camera.getEquLocation());
        // 摄像头状态
        calEquipment.getEquipment().setState(camera.getState());
        // 感动科技cameraNum
        calEquipment.getEquipment().setCameraNum(camera.getCameraNum());
        // 感动科技cameraNum
        calEquipment.getEquipment().setGdHwId(camera.getGdHwId());
        // 白天系数
//		if (camera.getDayx() != null) {
//			calEquipment.setDayx(camera.getDayx());
//		}
        // 晚上系数
//		if (camera.getNightx() != null) {
//			calEquipment.setNightx(camera.getNightx());
//		}
        /*************** 告警缓存 start **************/
//		AlarmDO alarm = new AlarmDO();
//		alarm.setAlarmId(camera.getAlarmId());
//		alarm.setDistance(camera.getAlarmDistance());
//		alarm.setImgpath(camera.getAlarmImgpath());
////		alarm.setLevel(camera.getAlarmLevel());
//		alarm.setBegintime(camera.getAlarmStartTime());
//		alarm.setImgtime(camera.getAlarmImgtime());
//		calEquipment.startAlarmFromDB(alarm, camera.getState());
        /*************** 告警缓存 end **************/
//		String groupName = camera.getGroupname();
//		try {
//			int endIndex = groupName.indexOf("_");
        // 摄像头组名
//			calEquipment.setGroupname(endIndex == -1?groupName:groupName.substring(0,endIndex));
//		} catch (Exception e) {
//			log.error("摄像头组名解析错误》》》》》》groupName：" + groupName, e);
//			throw new RuntimeException();
//		}
        // ai参数设置
//		String aiparam = camera.getAiparam();
//		if (StringUtils.isNotEmpty(aiparam)) {
//			String[] tmp = aiparam.split(":"); //4:23:0:252.25
//			if (tmp.length > 2) {
//				calEquipment.setDayStart(tmp[0]);
//				calEquipment.setDayEnd(tmp[1]);
//			} else {
//				calEquipment.setDayStart("4");
//				calEquipment.setDayEnd("20");
//			}
//		} else {
//			calEquipment.setDayStart("4");
//			calEquipment.setDayEnd("20");
//		}
        return calEquipment;
    }

//				try{
//					hwGroup = equip.getIdpath().substring(0,equip.getIdpath().indexOf(",",3));
//					//数据分组
//					if(fogCalculatorMap_.get(hwGroup) == null){
//						ConcurrentHashMap<String,FogCalculator> concurrentHashMap = new ConcurrentHashMap<String,FogCalculator>();
//						concurrentHashMap.put(this.CAMERA_CACHE_NAME + equip.getEpId(),equip);
//						fogCalculatorMap_.put(hwGroup,concurrentHashMap);
//					} else {
//						fogCalculatorMap_.get(hwGroup).put(this.CAMERA_CACHE_NAME + equip.getEpId(),equip);
//					}
//				} catch (Exception e) {
//					log.error("解析处理错误",e);
//				}

//    /**
//     * 摄像头异常入库
//     *
//     * @param fdo
//     */
//    public void equipExceptionHandle(FogHistoryDO fdo) {
//        try {
//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
//            Date date = new Date();
//            // 摄像头异常入库
//            Map<String, Object> map = new HashMap<>();
//            map.put("epId", fdo.getEpId());
//            map.put("startDate", simpleDateFormat.format(date));
//            EquipExceptionDO equipExceptionDO = this.equipExceptionDao.getByEpIdAndStartDate(map);
//            if (equipExceptionDO == null) {
//                EquipExceptionDO equip = new EquipExceptionDO();
//                equip.setEquipExceptionId(StringUtils.getUUID());
//                equip.setEpId(fdo.getEpId());
//                equip.setStartDate(simpleDateFormat.format(date));
//                equip.setStartTime(date);
//                equip.setLastTime(date);
//                equip.setDayCount(1);
//                // 保存
//                this.equipExceptionDao.save(equip);
//            } else {
//                equipExceptionDO.setLastTime(new Date());
//                equipExceptionDO.setDayCount(equipExceptionDO.getDayCount() + 1);
//                // 更新
//                this.equipExceptionDao.update(equipExceptionDO);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 筛选最低能见度的cal
     *
     * @param calList
     * @return
     */
    public FogCalculator minCal(List<FogCalculator> calList) {
        if (calList.size() == 0) {
            return null;
        }
        FogCalculator minCal = null;
        for (FogCalculator cal : calList) {
            if (minCal == null || (cal.getDistance() != null && cal.getDistance() < minCal.getDistance())) {
                minCal = cal;
            }
        }
        return minCal;

    }

}
