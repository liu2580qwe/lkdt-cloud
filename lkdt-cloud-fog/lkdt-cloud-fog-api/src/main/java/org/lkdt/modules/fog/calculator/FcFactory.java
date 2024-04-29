package org.lkdt.modules.fog.calculator;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import org.lkdt.common.system.vo.HighwayModel;
import org.lkdt.common.system.vo.LoginUser;
import org.lkdt.common.util.*;
import org.lkdt.modules.fog.entity.AlarmRoadModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class FcFactory {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    private FcFactory thisFcFactory;

    public long sendTime;


    private final int PhaseRelease = 100;

    public final long checkFogValTime = 10 * 60 * 1000;

    public int getPhaseRelease() {
        return PhaseRelease;
    }

    /**
     * 设备名前缀 Map
     */
    public final String CAMERA_CACHE_NAME = "fog:camera:";

    /**
     * 路段摄像头前缀 zSet
     */
    public final String CAMERAS_CACHE_NAME = "fog:highway_cameras:";

    /**
     * 路段名前缀 Map
     */
    public final String HIGHWAY_CACHE_NAME = "fog:highway:";

    private final String FOG_SYSTEM_NAME = "fog:systemName:";

    /**
     * 最近五条能见度缓存前缀 List
     */
    public final String LATE_DISTANCE_CACHE_NAME = "fog:late_distance:";

    /**
     * 告警路段信息缓存前缀 Map
     */
    public final String ALARM_ROAD_CACHE_NAME = "fog:alarm_road:";

    /**
     * 所有路段缓存前缀 Set
     */
    public final String ALL_HWIDS_CACHE_NAME = "fog:all_hwIds";

    /**
     * 所有摄像头缓存前缀 Set
     */
    public final String ALL_EQIDS_CACHE_NAME = "fog:all_eqIds";

    /**
     * 告警摄像头ID缓存前缀 Set
     */
    public final String ALARM_EPIDS_CACHE_NAME = "fog:alarm_epIds";

    /**
     * 告警路段ID缓存前缀 Set
     */
    public final String ALARM_HWIDS_CACHE_NAME = "fog:alarm_hwIds";

    /**
     * 未确认告警摄像头ID缓存前缀 Set
     */
    public final String UNCONFIRM_ALARM_EPIDS_CACHE_NAME = "fog:unconfirm_alarm_epids";

    /** 计算器列表 */
    /*private final ConcurrentHashMap<String, FogCalculator> fogCalculatorMap = new ConcurrentHashMap<String, FogCalculator>();*/

    /** 已确认告警列表 **/
    /*public final ConcurrentHashMap<String, FogCalculator> fogNowMap = new ConcurrentHashMap<String, FogCalculator>();*/

    /**
     * 数据时间
     **/
    /*public final ConcurrentHashMap<String, Long> fogValTime = new ConcurrentHashMap<String, Long>();*/

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    SpringContextUtils springContextUtils;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    FogCalRedis fogCalRedis;

    @Autowired
    HighWayUtil highWayUtil;

//    @Value("${lkdt.oss.endpoint}")
//    private String endpoint;
//
//    @Value("${lkdt.oss.bucketName}")
//    private String bucketName;

    @Value("${lkdt.imageHost}")
    private String imageHost;


    /**
     * 根据用户权限获取计算器信息列表
     */
    public List<FogCalculator> getCalculators() {

        LoginUser user = ShiroUtils.getUser();
        ConcurrentHashMap<String, FogCalculator> calMap = new ConcurrentHashMap<String, FogCalculator>();
        List<String> hwIds = new ArrayList<>();
        if (user == null || user.getId() == null || user.isAdmin()) {
            for (Object o : redisUtil.sGet(ALL_HWIDS_CACHE_NAME)) {
                hwIds.add(String.valueOf(o));
            }
        } else {
            hwIds = user.getHwIds();
            if (CollectionUtil.isEmpty(hwIds)) {
                for (Object o : redisUtil.sGet(ALL_HWIDS_CACHE_NAME)) {
                    hwIds.add(String.valueOf(o));
                }
            }
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
            for (Object o : redisUtil.sGet(ALL_HWIDS_CACHE_NAME)) {
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
    public AlarmRoadModel getAlarmRoadByHwId(String hwId) {
        try {
            Map<String, Object> wayMap = redisUtil.hmget(ALARM_ROAD_CACHE_NAME + hwId);
            return BeanUtil.mapToBean(wayMap, AlarmRoadModel.class, false);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public List<FogCalculator> getCalculatorsByHwId(String HwId) {
        try {
            List<FogCalculator> returnCals = new ArrayList<FogCalculator>();
            Set<Object> calSet = redisUtil.zSetGetAll(CAMERAS_CACHE_NAME + HwId);
            if (calSet == null) {
                return returnCals;
            }
            List<String> list = new ArrayList<>();
            for (Object o : calSet) {
                if (o != null) {
                    list.add(CAMERA_CACHE_NAME + (String) o);

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
                    list.add(CAMERA_CACHE_NAME + (String) o);

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

    public List<AlarmRoadModel> getAlarmRoadByarSet(Set<Object> arSet) {
        try {
            List<AlarmRoadModel> returnCals = new ArrayList<AlarmRoadModel>();
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
                    returnCals.add(BeanUtil.mapToBean(o, AlarmRoadModel.class, false));
                }
            }
            return returnCals;
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
            HighwayModel highway = highWayUtil.getById(userHwId);
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
            String[] idPath = highWayUtil.getById(hwId).getIdpath().split(",");
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
            Map<String, Object> calMap = redisUtil.hmget(CAMERA_CACHE_NAME + epId);
            // TODO 替换图片地址
            FogCalculator calculator = BeanUtils.mapToBean(calMap, new FogCalculator());
            LoginUser loginUser = ShiroUtils.getUser();
            if (oConvertUtils.isNotEmpty(loginUser) && StringUtils.isNotEmpty(loginUser.getSysIp()) && StringUtils.isNotEmpty(imageHost)) {
//                if(StringUtils.isEmpty(bucketName)){
//                    if(StringUtils.isNotEmpty(calculator.getImgpath())){
//                        calculator.setImgpath(calculator.getImgpath().replace(endpoint, loginUser.getSysIp()));
//                    }
//                }else{
//                    if(StringUtils.isNotEmpty(calculator.getImgpath())) {
//                        calculator.setImgpath(calculator.getImgpath().replace(bucketName + "." + endpoint, loginUser.getSysIp()));
//                    }
//                }
                if (StringUtils.isNotEmpty(calculator.getImgpath())) {
                    calculator.setImgpath(calculator.getImgpath().replace(imageHost, loginUser.getSysIp()));
                }
            }
            return calculator;
        } catch (Exception e) {
            logger.error("-----缓存获取失败 epId=" + epId);
            e.printStackTrace();
            return null;
        }


    }

    /**
     * 移除计算器信息
     */
    public boolean removeCalculator(String epId, String hwId) {

        try {
            Long l = redisUtil.zSetRemove(HIGHWAY_CACHE_NAME + hwId, epId);
            boolean b = redisUtil.del(CAMERA_CACHE_NAME + epId);
            return l != 0 && b;
        } catch (Exception e) {
            logger.error("-----缓存删除失败 epId=" + epId);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 添加计算器信息
     */
    public boolean putCalculator(FogCalculator fogCalculator, String epId) {
        try {
            Map<String, Object> oldMap = redisUtil.hmget(CAMERA_CACHE_NAME + epId);
            if (oldMap != null && oldMap.size() != 0) {
                logger.error("-----摄像头已存在缓存, epId=" + epId);
                return true;
            }
            fogCalculator.setEpId(epId);
            Map<String, Object> calMap = BeanUtils.beanToMap(fogCalculator);
            redisUtil.hmset(CAMERA_CACHE_NAME + epId, calMap);

            Integer[] ints = FogCalculator.parseEquName(fogCalculator.getEquipment().getEquName());
            if (ints != null && ints.length == 2 && ints[0] != null && ints[1] != null) {
                double score = ints[0] * 1000 + ints[1];//K100+222=100222
                redisUtil.zSetAdd(CAMERAS_CACHE_NAME + fogCalculator.getEquipment().getHwId(), epId, score);
                //所有摄像头
                redisUtil.sSet(ALL_EQIDS_CACHE_NAME, epId);

                return true;
            } else {
                logger.error("-----摄像头名称不符合规则, equName=" + fogCalculator.getEquipment().getEquName());
                return false;
            }

        } catch (Exception e) {
            logger.error("-----缓存添加失败 epId=" + epId);
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
            redisUtil.hmset(CAMERA_CACHE_NAME + epId, calMap);
            return true;
        } catch (Exception e) {
            logger.error("-----缓存添加失败 epId=" + epId, e);
            e.printStackTrace();
            return false;
        }
    }

    public FogCalculator needCall() {
        long now = System.currentTimeMillis();
        Set<Object> unconfirmAlarmEpIds = redisUtil.sGet(this.UNCONFIRM_ALARM_EPIDS_CACHE_NAME);
        if (unconfirmAlarmEpIds == null) {
            return null;
        }
        List<FogCalculator> calList = getCalculatorsByCalSet(unconfirmAlarmEpIds);
        if (calList == null) {
            return null;
        }
        for (FogCalculator cal : calList) {
//            if (cal.getHwId() == 43) {
//                continue;
//            }
            if (cal.getDistance() == null) {
                continue;
            }
            // 0:未确认
            if ((cal.getCameraType() == 1 && cal.getDistance() <= 200)) {
                if (now - cal.getUpdateTime().getTime() > 15 * 60 * 1000) {
                    return cal;
                }
            }
        }
        return null;
    }

    public Set<FogCalculator> needConfigByMan(boolean isall) {
        Set<FogCalculator> result = new HashSet<FogCalculator>();
        long now = System.currentTimeMillis();
//        int count = 0;
        Set<Object> unconfirmAlarmEpIds = redisUtil.sGet(this.UNCONFIRM_ALARM_EPIDS_CACHE_NAME);
        if (unconfirmAlarmEpIds == null) {
            return null;
        }
        List<FogCalculator> calList = getCalculatorsByCalSet(unconfirmAlarmEpIds);
        if (calList == null) {
            return null;
        }

        Set<Object> stringSet = redisUtil.getLikeKeys(this.ALARM_ROAD_CACHE_NAME);
        List<AlarmRoadModel> AlarmRoadModelList = getAlarmRoadByarSet(stringSet);
        Map<String, AlarmRoadModel> AlarmRoadModelMap = new HashMap<>();

        for (AlarmRoadModel road : AlarmRoadModelList) {
            AlarmRoadModelMap.put(road.getHwId(), road);
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
            AlarmRoadModel alarmRoad = AlarmRoadModelMap.get(cal.getEquipment().getHwId());
            if (!calIsEffective(cal, true)) {
                continue;
            }
            if ("5".equals(cal.getCameraType()) && cal.getDistance() <= 200) {
                continue;
            }
            // 0:未确认
            if ((cal.getCameraType() == 1 && cal.getDistance() <= 200) || cal.getCameraType() == 5) {
//                count++;
                if (isall) {
                    result.add(cal);
                } else {
                    if (/*count >= 5 || */now - cal.getUpdateTime().getTime() > 10 * 60 * 1000) {
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
                if (isall) {
                    result.add(cal);
                } else {
                    if (cal.getDistance() > 30) {
                        result.add(cal);
                    }

                }
            }
        }
        return result;
    }

    // 判断能见度回升
    public FogCalculator check100(List<FogCalculator> camList) {
        FogCalculator lastCal = null;
        for (FogCalculator cal : camList) {
            if (cal.getDistance() != null && cal.isFogNow()) {
                if (cal.getDistance() <= this.getPhaseRelease()) {
                    return null;
                } else if (cal.getDistance() <= 200) {
                    lastCal = cal;
                }
            }
        }
        return lastCal;
    }

    // 判断能见度回升
    public Map<String, Long> checkFogValTime() {
        Long now = System.currentTimeMillis();
        Map map = new HashMap();
        Set<Object> keys = redisUtil.getLikeKeys(FOG_SYSTEM_NAME);
        for (Object key : keys) {
            String systemName = (String) key;
            Long cacheTime = (long) redisUtil.get(systemName);
            map.put(key, now - cacheTime);
        }
        return map;
    }

    /**
     * Put time into redis
     *
     * @param systemName
     * @param currentTime
     */
    public void saveSysTime(String systemName, Long currentTime) {
        redisUtil.set(FOG_SYSTEM_NAME + systemName, currentTime);
    }

    /**
     * delete
     *
     * @param systemName
     */
    public void deleteSysTime(String systemName) {
        redisUtil.del(FOG_SYSTEM_NAME + systemName);
    }

    public void deleteSysTimeByKey(String key) {
        redisUtil.del(key);
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
            logger.error(e.toString());
            return null;
        }

    }

    /**
     * 是否正常摄像头：有能见度、有图片时间、图片时间在一小时内
     *
     * @param cal
     * @param normal 是否过滤异常摄像头
     * @return
     */
    public boolean calIsEffective(FogCalculator cal, boolean normal) {
        if (cal == null || cal.getDistance() == null || cal.getDistance() <= 0) {
            return false;
        }
        if (cal.getImgtime() == null || System.currentTimeMillis() - cal.getImgtime().getTime() > 1 * 60 * 60 * 1000) {
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

    /**
     * 根据key 和 epId 查询权重
     *
     * @param hwId 道路id
     * @param epId 设备id
     * @return Long 排名
     */
    public Double hwCamScoreScore(String hwId, String epId) {
        return redisUtil.zSetScore(CAMERAS_CACHE_NAME + hwId, epId);
    }

}
