package org.lkdt.modules.traffic.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.lkdt.common.util.StringUtils;
import org.lkdt.modules.traffic.entity.TrafficIncidentRecord;
import org.lkdt.modules.traffic.service.ITrafficIncidentRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * 获取交通事件
 *
 * @author wy
 */
@Component
public class TrafficIncidentRecordJob {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final static String LOCK_KEY = "LOCK_KEY_GET_GANNG_CAMERA";

    private final static long TIMEOUT = 10000;

	/*@Autowired
	RedisLock redisLock;*/

    @Autowired
    private ITrafficIncidentRecordService trafficIncidentRecordDao;

//	@Autowired
//	private TrafficDataCollectionComponent trafficDataCollectionComponent;

//	@Override
//	public void execute(JobExecutionContext context) throws JobExecutionException {
//		trafficDataCollectionComponent.execute();
//		getAllFC();
//	}

    /**
     * 江苏96777数据搜集
     *
     * @return
     */
    @XxlJob("getTrafficIncidentRecord")
    public ReturnT<String> getTrafficIncidentRecord() {
        try {
            Map<String, TrafficIncidentRecord> eventMap = new HashMap<String, TrafficIncidentRecord>();
            List<String> eventIdList = new ArrayList<String>();
            //eventId={'事故event':'1006001','施工plan':'1006002','control':'1006007','道路管制road':'1006006','拥堵busy':'1006008','交通事件traffic':'1006010','恶劣天气weather':'1006009'}; //接口传入参数
            String eventUrl = "https://wx.js96777.com/JiangSuAPIServer/index.php/WXSvgApi/getEventDataByTypesNews?eventtype=1006001,1006002,1006006,1006008,1006009,1006010";
            HttpHeaders eventHeaders = new HttpHeaders();
            MediaType eventType = MediaType.parseMediaType("application/json; charset=UTF-8");
            eventHeaders.setContentType(eventType);

            HttpEntity<String> eventFormEntity = new HttpEntity<String>("", eventHeaders);
            RestTemplate eventRestTemplate = new RestTemplate();
            ResponseEntity<String> eventResult = eventRestTemplate.postForEntity(eventUrl, eventFormEntity, String.class);
            JSONArray jsonArray = JSON.parseArray(eventResult.getBody());
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONArray eventArray = JSON.parseArray(jsonArray.getString(i));
                for (int j = 0; j < eventArray.size(); j++) {
                    JSONObject json = JSON.parseObject(eventArray.getString(j));
                    TrafficIncidentRecord tir = new TrafficIncidentRecord();
                    //增加判断json非空
                    if (json.getString("eventid") != null) {
                        tir.setId(json.getString("eventid").split("/")[0]);
                        tir.setEventtype(json.getString("eventid").split("/")[1]);
                    }
                    if (json.getString("xy") != null) {
                        tir.setLon(json.getString("xy").split(",")[0]);
                        tir.setLat(json.getString("xy").split(",")[1]);
                    }
                    //KEY键用eventid，去掉eventid重复的事件
                    eventMap.put(tir.getId(), tir);
                }
            }
            if (eventMap != null && !eventMap.isEmpty()) {
                eventIdList = trafficIncidentRecordDao.getEventIdList();
                for (String key : eventMap.keySet()) {
                    if (!eventIdList.contains(key)) {
                        String uri2 = "https://wx.js96777.com/JiangSuAPIServer/WXSvgApi/getEventDetailsById?eventids=" + key;
                        HttpHeaders headers2 = new HttpHeaders();
                        MediaType type2 = MediaType.parseMediaType("application/json; charset=UTF-8");
                        headers2.setContentType(type2);

                        HttpEntity<String> formEntity2 = new HttpEntity<String>("", headers2);
                        RestTemplate restTemplate2 = new RestTemplate();
                        ResponseEntity<String> result2 = restTemplate2.postForEntity(uri2, formEntity2, String.class);
                        String eventdetails = convert(result2.getBody());
                        if (eventdetails.indexOf("eventdetails") > -1) {
                            eventdetails = eventdetails.substring(13, eventdetails.length() - 1);
                        }

                        JSONArray detailsArray = JSON.parseArray(eventdetails);
                        for (int i = 0; i < detailsArray.size(); i++) {
                            JSONObject json = JSON.parseObject(detailsArray.getString(i));
//
                            TrafficIncidentRecord tir = eventMap.get(key);
                            tir.setType(json.getString("type"));
                            tir.setReportout(json.getString("reportout"));
                            tir.setOcctime(json.getDate("occtime"));
                            tir.setPlanovertime(json.getDate("planovertime"));
                            if (!StringUtils.equals(tir.getEventtype(), "1006010")) {
                                tir.setRealovertime(json.getDate("realovertime"));
                            }
                            tir.setRoadname(json.getString("roadname"));
                            tir.setJamspeed(json.getString("jamspeed"));
                            tir.setJamdist(json.getString("jamdist"));
                            tir.setLongtime(json.getDate("longtime"));
                            tir.setDirectionname(json.getString("directionname"));
                            tir.setUpdateTime(json.getDate("updatetime"));
                            tir.setSourceType("1");
                            tir.setInputtime(new Date());
                            tir.setIssend("N");

                            trafficIncidentRecordDao.save(tir);
                        }

                    }
                }
            }

        } catch (Exception e) {
            e.getStackTrace();
            logger.error("异常" + e);
        }
        return ReturnT.SUCCESS;
    }


    /**
     * 解析包含unicode编码
     *
     * @param utfString
     * @return
     */
    public static String convert(String utfString) {
        StringBuilder sb = new StringBuilder();
        int i = -1;
        int pos = 0;
        int iint = 0;
        while ((i = utfString.indexOf("\\u", pos)) != -1) {
            String sd = utfString.substring(pos, i);
            sb.append(sd);
            iint = i + 5;

            if (iint < utfString.length()) {
                pos = i + 6;
                sb.append((char) Integer.parseInt(utfString.substring(i + 2, i + 6), 16));
            }
        }
        String endStr = utfString.substring(iint + 1, utfString.length());
        return sb + "" + endStr;
    }

}
