package org.lkdt.modules.radar.supports.radarDataService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.lkdt.common.system.vo.DictModel;
import org.lkdt.common.util.StringUtils;
import org.lkdt.modules.api.SysBaseRemoteApi;
import org.lkdt.modules.radar.supports.radarTools.DO.RadarDO;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import lombok.Data;

/**
 * 首页实时事件
 */
public class TwoSpeedThreeHurriedExt {

    private final int frameSizeOf24h = 24*60;//分钟

    private final int frameSizeOf1min = 60*14;//帧/分钟

    /**实时事件集合1h*/
    private List<Map<String, LiveEvent>> liveEvent1hMapList = new ArrayList<>(frameSizeOf24h);

    /**实时事件集合1min*/
    private Map<String, LiveEvent> liveEvent1minMap = null;

    private SysBaseRemoteApi sysBaseAPI;

    private DataReader dataReader;

    private List<DictModel> radarEventTypeList;

    private List<DictModel> laneTypeList;

    TwoSpeedThreeHurriedExt(DataReader dataReader, SysBaseRemoteApi sysBaseAPI){
        this.dataReader = dataReader;
        this.sysBaseAPI = sysBaseAPI;
        this.radarEventTypeList = sysBaseAPI.queryDictItemsByCode("radar_event_type");
        this.laneTypeList = sysBaseAPI.queryDictItemsByCode("lane_type");
    }

    /**1分钟内的数据统计*/
    @Data
    private class LiveEvent {

        /**车道号*/
        public int lane;

        /**车道描述*/
        public String laneRoad;

        /**车道描述Msg*/
        public String laneRoadMsg;

        /**事件类型*/
        public String eventType;

        /**事件类型Msg*/
        public String eventTypeMsg;

        /**事件计数*/
        public int countNum;

        /**countNum++*/
        public void countNumAdd(){
            this.countNum++;
        }

        public LiveEvent(int lane, String laneRoad, String eventType, int countNum) {
            this.lane = lane;
            setLaneHandle(lane);
            this.eventType = eventType;
            setEventTypeHandle(eventType);
            this.countNum = countNum;
        }

        public void setLaneHandle(int lane) {
            String laneRoad = dataReader.radarEventDataGrab.searchZcLdLaneRoad(lane);
            this.laneRoad = laneRoad;

            for (DictModel radarEventType : laneTypeList) {
                if (radarEventType.getValue().equals(laneRoad)) {
                    this.laneRoadMsg = radarEventType.getText();
                    break;
                }
            }

        }

        public void setEventTypeHandle(String eventType) {
            for (DictModel laneType : radarEventTypeList) {
                if (laneType.getValue().equals(eventType)) {
                    this.eventTypeMsg = laneType.getText();
                    break;
                }
            }
        }

    }

    //1分钟计数 [0, 60*14)
    private int countOf1min = 0;

    /**
     * 事件实时计数
     * @param radarDO 最新一帧数据
     */
    protected void homeLiveEvent(RadarDO radarDO){
        try {

            if(countOf1min == 0){

                //追加到24h集合中
                if(liveEvent1minMap != null){
                    //空间已满
                    if(liveEvent1hMapList.size() >= frameSizeOf24h){
                        //移除开始一分钟的数据
                        liveEvent1hMapList.remove(0);
                    }
                    liveEvent1hMapList.add(liveEvent1minMap);
                }

                //创建新1min的map
                liveEvent1minMap = new TreeMap<>();
            }

            if(++countOf1min >= frameSizeOf1min){
                countOf1min = 0;
            }

//            if (this.dataReader.getRadarId().equals("1007")) {
//                System.out.printf("countOf1min: %s\n", countOf1min);
//                System.out.printf("liveEvent1minMap: %s\n", JSONUtil.parseFromMap(liveEvent1minMap).toString());
//            }

            //获取一帧数据的所有事件集合
            JSONArray allEvent = radarDO.getDataBody();
            Iterator<Object> ite = allEvent.iterator();
            while(ite.hasNext()){
                JSONObject jObj = (JSONObject) ite.next();
                //三急
                String eventType1001 = StringUtils.isNotEmpty(jObj.getStr("eventType1001"))? "1001": null;
                String eventType1002 = StringUtils.isNotEmpty(jObj.getStr("eventType1002"))? "1002": null;
                String eventType1005 = StringUtils.isNotEmpty(jObj.getStr("eventType1005"))? "1005": null;
                String eventType1006 = StringUtils.isNotEmpty(jObj.getStr("eventType1006"))? "1006": null;
                String eventType8888 = StringUtils.isNotEmpty(jObj.getStr("eventType8888"))? "8888": null;
                String eventType9999 = StringUtils.isNotEmpty(jObj.getStr("eventType9999"))? "9999": null;
                //两速
                String eventType1003 = StringUtils.isNotEmpty(jObj.getStr("eventType1003"))? "1003": null;
                String eventType1004 = StringUtils.isNotEmpty(jObj.getStr("eventType1004"))? "1004": null;
                String[] eventType = new String[]{eventType1001, eventType1002, eventType1005, eventType1006, eventType8888, eventType9999, eventType1003, eventType1004};
                if(eventType1001 != null || eventType1002 != null || eventType1005 != null
                        || eventType1006 != null || eventType8888 != null || eventType9999 != null
                        || eventType1003 != null || eventType1004 != null){
                    int lane = jObj.getInt("laneNum");
                    for (String s : eventType) {
                        //有事件add
                        putEvent(liveEvent1minMap, lane, s);
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void putEvent(Map<String, LiveEvent> liveEventMap, int lane, String eventType){
        if(StringUtils.isNotEmpty(eventType)){
            String key = lane + "_" + eventType;
            LiveEvent liveEvent = liveEventMap.get(key);

            //新增
            if(liveEvent == null){
                liveEvent = new LiveEvent(lane, "", eventType, 1);
                liveEventMap.put(key, liveEvent);
            }
            //更新
            else {
                liveEvent.countNumAdd();
            }
        }
    }

    /**
     * 1 ~ 24*60
     * @return 获取最近的N分钟数据
     */
    public JSONArray getEventTypesOfN(int N){
        try{
            int size = liveEvent1hMapList.size();

            //参数不合法
            if(N <= 0 || size == 0){
                return new JSONArray();
            }

            if(N > size){
                N = size;
            }

            List<Map<String, LiveEvent>> liveEventList = liveEvent1hMapList.subList(size - N, size);
            Map<String, LiveEvent> returnMap = new TreeMap<>();
            for (Map<String, LiveEvent> stringLiveEventMap : liveEventList) {
                for(Map.Entry<String, LiveEvent> m: stringLiveEventMap.entrySet()){
                    LiveEvent liveEvent = returnMap.get(m.getKey());
                    if(liveEvent == null){
                        //拷贝
                        LiveEvent newTemp = new LiveEvent(m.getValue().getLane(), m.getValue().laneRoad,
                                m.getValue().getEventType(), m.getValue().getCountNum());
                        returnMap.put(m.getKey(), newTemp);
                    } else {
                        liveEvent.setCountNum(liveEvent.getCountNum() + m.getValue().getCountNum());
                    }
                }
            }

            JSONArray returnArray = new JSONArray();
            for(Map.Entry<String, LiveEvent> m: returnMap.entrySet()){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("lane", m.getValue().getLane());
                jsonObject.put("laneRoad", m.getValue().getLaneRoad());
                jsonObject.put("laneRoadMsg", m.getValue().getLaneRoadMsg());
                jsonObject.put("eventType", m.getValue().getEventType());
                jsonObject.put("eventTypeMsg", m.getValue().getEventTypeMsg());
                jsonObject.put("countNum", m.getValue().getCountNum());
                returnArray.add(jsonObject);
            }

//            if (this.dataReader.getRadarId().equals("1007")) {
//                System.out.printf("returnArray countOf1min: %s\n", returnArray);
//            }

            return returnArray;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new JSONArray();

    }

}
