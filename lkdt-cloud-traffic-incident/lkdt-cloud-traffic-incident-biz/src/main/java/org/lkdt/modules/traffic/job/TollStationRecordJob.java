package org.lkdt.modules.traffic.job;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.lkdt.common.util.StringUtils;
import org.lkdt.modules.traffic.entity.TollStationEvent;
import org.lkdt.modules.traffic.entity.TollStationRecorder;
import org.lkdt.modules.traffic.service.ITollStationEventService;
import org.lkdt.modules.traffic.service.ITollStationRecorderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 获取路段解封信息
 */
@Component
public class TollStationRecordJob {
    Logger logger = LoggerFactory.getLogger(TollStationRecordJob.class);

    private final static String LOCK_KEY = "LOCK_KEY_GET_GANNG_CAMERA";

    private final static long TIMEOUT = 10000;

//    @Autowired
//    RedisLock redisLock;

    @Autowired
    private ITollStationRecorderService tollStationRecorderDao;

    @Autowired
    private ITollStationEventService tollStationEventDao;

    @Autowired
    JobUtil jobUtil;

    @XxlJob("getStationRecord")
    public ReturnT<String> getStationRecord() {
        try {
            //所有收费站信息
            JSONArray tollStations = jobUtil.getJAApp("https://pubwechat.jchc.cn/kg_pidwx/followts/getAllTollStationByPro?proid=32");
            //收费站封路信息
            JSONArray tsEvents = jobUtil.getJAApp("https://pubwechat.jchc.cn/kg_pidwx/evcr/getAllTsEvents.?provName=%E6%B1%9F%E8%8B%8F&province=32");
            if (tollStations == null) {
                logger.error("ERROR20200212-获取收费站信息出错");
                return null;
            }
            if (tsEvents == null) {
                logger.error("ERROR20200212-获取路段解封信息出错");
                return null;
            }

            //事件入库
            exeEventTs(tsEvents);

            JSONObject jsonObject = null;
            //解封信息入库
            Map<String, Object> map1 = new HashMap<String, Object>();
            map1.put("allow", "0");
            List<TollStationRecorder> tList = tollStationRecorderDao.list(new QueryWrapper<TollStationRecorder>().eq("allow", "0"));
            for (TollStationRecorder t : tList) {
                if (!containTs(tsEvents, t.getTollstationid())) {
                    t.setAllow("1");
                    t.setAllowtime(new Date());
                    tollStationRecorderDao.updateById(t);
                    //原方法
//                            tollStationRecorderDao.update(t);
                }
            }
            //封路信息入库
            Map<String, Object> mapInit = new HashMap<String, Object>();
            mapInit.put("allow", "0");
            //查询已封路入库的信息
            List<TollStationRecorder> list = tollStationRecorderDao.list(new QueryWrapper<TollStationRecorder>().eq("allow", "0"));
            for (int i = 0; i < tsEvents.size(); i++) {
                jsonObject = tsEvents.getJSONObject(i);
//                String infotype = jsonObject.getString("infotype");
//                String exetime = jsonObject.getString("exetime");
                //开启
//                if(StringUtils.isEmpty(infotype) || ("0".equals(infotype) && !StringUtils.isEmpty(exetime))
//                        || ("1".equals(infotype) && StringUtils.isEmpty(exetime))){
//                    continue;
//                } else{//关闭
                String tollstationid = jsonObject.getString("tollstationid");
                //收费站信息
                JSONObject infoJObj = getJObj(tollStations, tollstationid);
                //添加收费站封路信息
                //增加非空添加判断
                if (infoJObj != null) {

                    infoJObj.put("eventid", jsonObject.getString("eventid"));
                    infoJObj.put("eventname", jsonObject.getString("eventname"));
                    infoJObj.put("closetime", jsonObject.getString("closetime"));
                    infoJObj.put("closereason", jsonObject.getString("closereason"));
                    infoJObj.put("closereasontext", jsonObject.getString("closereasontext"));
                    infoJObj.put("estimatopen", jsonObject.getString("estimatopen"));
                    infoJObj.put("croadcid", jsonObject.getString("croadcid"));
                    infoJObj.put("infotype", jsonObject.getString("infotype"));
                    infoJObj.put("noticetime", jsonObject.getString("noticetime"));
                    infoJObj.put("exetime", jsonObject.getString("exetime"));
                    infoJObj.put("stationname", jsonObject.getString("stationname"));
                    TollStationRecorder tollStationRecorder = new TollStationRecorder();
                    tollStationRecorder.setAllow("0");//禁行
                    tollStationRecorder.setForbidtime(new Date());//设置抓取时间
                    if (invalid(infoJObj, tollStationRecorder)) {
                        //是否入库
                        boolean bool = true;
                        for (TollStationRecorder t : list) {
                            if (tollstationid.equals(t.getTollstationid())) {
                                bool = false;
                                break;
                            }
                        }
                        if (bool) {
                            if (tollStationRecorderDao.save(tollStationRecorder)) {
                                //追加已入库信息
                                list.add(tollStationRecorder);
                            }
                        }
                    }
//                }
                }
            }
        } catch (Exception e) {
            logger.error("收费站封路数据处理异常", e);
        }
        return ReturnT.SUCCESS;
    }

    /**
     * 事件入库
     *
     * @param tsEvents
     */
    private void exeEventTs(JSONArray tsEvents) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        try {
            //封路事件获取
            Map<String, Object> eventMap = new HashMap<>();
            eventMap.put("isdel", "0");
            List<TollStationEvent> tollStationEventS = tollStationEventDao.list(new QueryWrapper<TollStationEvent>().eq("isdel", "0"));
            //解封路事件入库
            for (TollStationEvent t : tollStationEventS) {
                if (!containTsEvent(tsEvents, t.getId())) {
                    t.setIsdel("1");
                    t.setDeltime(sdf.format(new Date()));
                    tollStationEventDao.updateById(t);
//                    tollStationEventDao.update(t);
                }
            }
            tollStationEventS = tollStationEventDao.list(new QueryWrapper<TollStationEvent>().eq("isdel", "0"));
            JSONObject jsonEventObject = null;
            for (int i = 0; i < tsEvents.size(); i++) {
                jsonEventObject = tsEvents.getJSONObject(i).getJSONObject("event");
                String eventid = jsonEventObject.getString("eventid");
                TollStationEvent tollStationEvent = new TollStationEvent();
                tollStationEvent.setIsdel("0");
                tollStationEvent.setPutawaytime(sdf.format(new Date()));
                if (invalidEvent(jsonEventObject, tollStationEvent)) {
                    //是否入库
                    boolean bool = true;
                    for (TollStationEvent t : tollStationEventS) {
                        if (eventid.equals(t.getId())) {
                            bool = false;
                            break;
                        }
                    }
                    if (bool) {
                        try {
                            if (!tollStationEventDao.updateById(tollStationEvent)) {
                                if (tollStationEventDao.save(tollStationEvent)) {
                                    //追加已入库信息
                                    tollStationEventS.add(tollStationEvent);
                                }
                            } else {
                                //追加已入库信息
                                tollStationEventS.add(tollStationEvent);
                            }
                        } catch (Exception e) {
                            logger.error("事件入库异常", e);
                        }

                    }
                }
            }
        } catch (Exception e) {
            logger.error("封路事件异常", e);
        }
    }

    /**
     * 查找事件关闭信息
     *
     * @param tsEvents
     * @param eventId  参数
     * @return
     */
    private boolean containTsEvent(JSONArray tsEvents, String eventId) {
        try {
            for (int i = 0; i < tsEvents.size(); i++) {
                if (eventId.equals(tsEvents.getJSONObject(i).getJSONObject("event").getString("eventid"))) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 查找关闭信息
     *
     * @param tsEvents
     * @param paramTollstationid 参数
     * @return
     */
    private boolean containTs(JSONArray tsEvents, String paramTollstationid) {
        try {
            for (int i = 0; i < tsEvents.size(); i++) {
                if (paramTollstationid.equals(tsEvents.getJSONObject(i).getString("tollstationid"))) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 遍历查找收费站信息
     *
     * @param tollStations
     * @param paramTollstationid 参数
     * @return
     */
    private JSONObject getJObj(JSONArray tollStations, String paramTollstationid) {
        try {
            for (int i = 0; i < tollStations.size(); i++) {
                JSONArray relinfo = tollStations.getJSONObject(i).getJSONArray("relinfo");
                for (int j = 0; j < relinfo.size(); j++) {
                    if (paramTollstationid.equals(relinfo.getJSONObject(j).getString("tollstationid"))) {
                        return relinfo.getJSONObject(j);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 判断数据是否合法
     *
     * @param jsonObject       收费站信息
     * @param tollStationEvent 入库信息
     * @return
     */
    private boolean invalidEvent(JSONObject jsonObject, TollStationEvent tollStationEvent) {
        try {
            String eventid = jobUtil.toStringHandle(jsonObject.getString("eventid"));
            //数据校验
            if (StringUtils.isEmpty(eventid)) {
                return false;
            }
            tollStationEvent.setId(jsonObject.getString("eventid"));
            tollStationEvent.setEventname(jsonObject.getString("eventname"));
            tollStationEvent.setTypeid(jsonObject.getString("typeid"));
            tollStationEvent.setUnitid(jsonObject.getString("unitid"));
            tollStationEvent.setUnitname(jsonObject.getString("unitname"));
            tollStationEvent.setSectioncenterid(jsonObject.getString("sectioncenterid"));
            tollStationEvent.setSectioncentername(jsonObject.getString("sectioncentername"));
            tollStationEvent.setCreatejopnum(jsonObject.getString("createjopnum"));
            tollStationEvent.setCreatename(jsonObject.getString("createname"));
            tollStationEvent.setRoadid(jsonObject.getString("roadid"));
            tollStationEvent.setRoadname(jsonObject.getString("roadname"));
            tollStationEvent.setEventstate(jsonObject.getString("eventstate"));
            tollStationEvent.setAccidentdesc(jsonObject.getString("accidentdesc"));
            //json转日期
            String create = (String) jsonObject.get("createtime");
            Timestamp createtime = Timestamp.valueOf(create);
            tollStationEvent.setCreateTime(createtime);
            String update = (String) jsonObject.get("updatetime");
            Timestamp updatetime = Timestamp.valueOf(update);
            tollStationEvent.setUpdateTime(updatetime);
            tollStationEvent.setEffectroad(jsonObject.getString("effectRoad"));
            tollStationEvent.setTotalpoints(jsonObject.getString("totalpoints"));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断数据是否合法
     *
     * @param jsonObject          收费站信息
     * @param tollStationRecorder 入库信息
     * @return
     */
    private boolean invalid(JSONObject jsonObject, TollStationRecorder tollStationRecorder) {
        try {
            String tollstationid = jobUtil.toStringHandle(jsonObject.getString("tollstationid"));
            //数据校验
            if (StringUtils.isEmpty(tollstationid)) {
                return false;
            }
//            tollStationRecorder.setId(StringUtils.getUUID());
            tollStationRecorder.setTollstationid(jobUtil.toStringHandle(jsonObject.getString("tollstationid")));
            tollStationRecorder.setTsname(jobUtil.toStringHandle(jsonObject.getString("tsname")));
            tollStationRecorder.setTsnum(jobUtil.toStringHandle(jsonObject.getString("tsnum")));
            tollStationRecorder.setRoadname(jobUtil.toStringHandle(jsonObject.getString("roadname")));
            tollStationRecorder.setRoadnum(jobUtil.toStringHandle(jsonObject.getString("roadnum")));
            tollStationRecorder.setLat(jobUtil.toStringHandle(jsonObject.getString("lat")));
            tollStationRecorder.setLon(jobUtil.toStringHandle(jsonObject.getString("lon")));
            tollStationRecorder.setDirection(jobUtil.toStringHandle(jsonObject.getString("direction")));
            tollStationRecorder.setIo(jobUtil.toStringHandle(jsonObject.getString("io")));
            tollStationRecorder.setClosereason(jobUtil.toStringHandle(jsonObject.getString("closereason")));
            tollStationRecorder.setClosereasontext(jobUtil.toStringHandle(jsonObject.getString("closereasontext")));
            tollStationRecorder.setClosetime(jobUtil.toStringHandle(jsonObject.getString("closetime")));
            tollStationRecorder.setCroadcid(jobUtil.toStringHandle(jsonObject.getString("croadcid")));
            tollStationRecorder.setCunitid(jobUtil.toStringHandle(jsonObject.getString("cunitid")));
            tollStationRecorder.setDatatype(jobUtil.toStringHandle(jsonObject.getString("datatype")));
            tollStationRecorder.setEstimatopen(jobUtil.toStringHandle(jsonObject.getString("estimatopen")));
            tollStationRecorder.setEventid(jobUtil.toStringHandle(jsonObject.getString("eventid")));
            tollStationRecorder.setEventname(jobUtil.toStringHandle(jsonObject.getString("eventname")));
            tollStationRecorder.setExetime(jobUtil.toStringHandle(jsonObject.getString("exetime")));
//            tollStationRecorder.setId(jobUtil.toStringHandle(jsonObject.getString("id")));
            tollStationRecorder.setInfotype(jobUtil.toStringHandle(jsonObject.getString("infotype")));
            tollStationRecorder.setNoticetime(jobUtil.toStringHandle(jsonObject.getString("noticetime")));
            tollStationRecorder.setOldid(jobUtil.toStringHandle(jsonObject.getString("oldid")));
            tollStationRecorder.setStationname(jobUtil.toStringHandle(jsonObject.getString("stationname")));
            tollStationRecorder.setStationnames(jobUtil.toStringHandle(jsonObject.getString("stationNames")));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
