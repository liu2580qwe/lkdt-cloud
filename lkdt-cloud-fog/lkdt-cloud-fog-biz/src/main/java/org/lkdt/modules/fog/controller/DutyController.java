package org.lkdt.modules.fog.controller;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiOperation;
import org.lkdt.common.aspect.annotation.AutoLog;
import org.lkdt.common.system.vo.DictModel;
import org.lkdt.common.util.AlarmLevelUtil;
import org.lkdt.common.util.DateUtils;
import org.lkdt.common.util.StringUtils;
import org.lkdt.modules.api.HighwayApi;
import org.lkdt.modules.api.SysBaseRemoteApi;
import org.lkdt.modules.fog.calculator.FcFactory;
import org.lkdt.modules.fog.calculator.FogCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
/**
 * @author CaiXibei
 * @date 21-05-08
 */
@RestController
@RequestMapping("/fog/duty")
public class DutyController {

    @Autowired
    private FcFactory factory;

    @Autowired
    private SysBaseRemoteApi sysBaseRemoteApi;

    @Autowired
    private HighwayApi highwayApi;

    @AutoLog(value = "值班页查询")
    @ApiOperation(value="值班页查询", notes="值班页查询")
    @PostMapping("/queryEquipmentByHwId")
    public JSONObject queryEquipmentByHwId(@RequestBody JSONObject jsonParams){
        //return result object
        JSONObject resultObj = new JSONObject();
        //abnormal equipment
        JSONArray errorArray = new JSONArray();
        //all equipment
        JSONArray allArray = new JSONArray();
        //alarm equipment
        JSONArray alarmArray = new JSONArray();
        //effective equipment
        JSONArray effectiveArray = new JSONArray();

        //Query group fog calculation results according to hwid
        List<FogCalculator> calculators = new ArrayList<>();
        List<String> ids = highwayApi.queryChildNodes(jsonParams.get("hwId").toString());
        ids.add(jsonParams.get("hwId").toString());
        for(String id:ids){
            calculators.addAll(factory.getCalculatorsByHwId(id));
        }
        for (FogCalculator fogCalculator:calculators) {
            //The visibility is empty or the visibility is less than or equal to 0
            if(fogCalculator.getDistance() != null && fogCalculator.getDistance() > 0) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("stateDesc",sysBaseRemoteApi.queryDictTextByKey("equipment_state",fogCalculator.getEquipment().getState()));
                jsonObject.put("epId", fogCalculator.getEpId());
                jsonObject.put("alarmId", fogCalculator.getAlarmId());
                jsonObject.put("equCode", fogCalculator.getEquipment().getEquCode());
                jsonObject.put("equName", fogCalculator.getEquipment().getEquName());
                jsonObject.put("hwId", fogCalculator.getEquipment().getHwId());
                jsonObject.put("hwName", fogCalculator.getEquipment().getHwName());
                jsonObject.put("address", fogCalculator.getEquipment().getEquLocation());
                jsonObject.put("state", fogCalculator.getEquipment().getState());

                //determine whether it is in a cloud state
                String alarmLevel = fogCalculator.getAlarmLevel();
                if(fogCalculator.isFogNow()){
                    if(StringUtils.isEmpty(alarmLevel)){
                        jsonObject.put("alarmLevel", AlarmLevelUtil.getLevelDescByDist(fogCalculator.getSmDistance()) );
                    }else{
                        try {
                            int level = Integer.parseInt(alarmLevel);
                            jsonObject.put("alarmLevel", AlarmLevelUtil.getAlarmLevelDesc(level) );
                        } catch (Exception e) {
                            jsonObject.put("alarmLevel", alarmLevel );
                        }
                    }
                }else {
                    jsonObject.put("alarmLevel", "无告警" );
                }
                //visibility
                jsonObject.put("distance", fogCalculator.getDistance());
                jsonObject.put("imgpath", fogCalculator.getImgpath());
                jsonObject.put("imgtime", fogCalculator.getImgtime());
                //camera status
                jsonObject.put("cameraType", fogCalculator.getCameraType());
                //picture hour minute second
                jsonObject.put("imgHMS", DateUtils.format(fogCalculator.getImgtime(), "HH:mm:ss") );
                //picture year month day
                jsonObject.put("imgYMD", DateUtils.format(fogCalculator.getImgtime(), "yyyy-M-d"));
                allArray.add(jsonObject);
                if(factory.calIsEffective(fogCalculator, true)) {
                    effectiveArray.add(jsonObject);
                }
                if(!StringUtils.equals(fogCalculator.getEquipment().getState(), "0") && !StringUtils.equals(fogCalculator.getEquipment().getState(), "9")){
                    errorArray.add(jsonObject);
                }else{
                    if(fogCalculator.getCameraType() >= 2){
                        alarmArray.add(jsonObject);
                    }
                }
            }
        }
        resultObj.put("errorArray",errorArray);
        resultObj.put("allArray",allArray);
        resultObj.put("alarmArray",alarmArray);
        resultObj.put("effectiveArray",effectiveArray);
        return resultObj;
    }
}
