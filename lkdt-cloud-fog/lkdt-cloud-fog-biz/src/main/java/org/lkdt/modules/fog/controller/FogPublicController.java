package org.lkdt.modules.fog.controller;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.lkdt.common.api.vo.Result;
import org.lkdt.common.system.vo.HighwayModel;
import org.lkdt.common.util.HighWayUtil;
import org.lkdt.modules.fog.calculator.FcFactory;
import org.lkdt.modules.fog.calculator.FogCalculator;
import org.lkdt.modules.fog.calculator.WcFactory;
import org.lkdt.modules.fog.calculator.WindCalculator;
import org.lkdt.modules.fog.service.IAlarmService;
import org.lkdt.modules.fog.vo.EquipmentFcVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fog/public")
@Slf4j
public class FogPublicController {
    @Autowired
    private IAlarmService alarmService;
    @Autowired
    private FcFactory fcFactory;
    @Autowired
    private HighWayUtil highWayUtil;
    @Autowired
    private WcFactory wcFactory;
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
    public String distanceStatistic(String startDate, String endDate, @RequestParam("date")  String date, @RequestParam("epId")  String epId) {
        // 统计日期
        return alarmService.getDistanceStatistic(startDate, endDate, date, epId);
    }
    /**
     * 首页摄像头实时影像查询
     *
     * @param epId
     * @return
     */
    @GetMapping("/home_lookup/{epId}")
    public Result home_lookup(@PathVariable("epId") String epId) {
        FogCalculator cal = fcFactory.getCalculator(epId);
        EquipmentFcVo equipmentFcVo = new EquipmentFcVo();
        if (cal != null) {
            equipmentFcVo.setFogCalculator(cal);
            if (cal.getEquipment().getHwId() != null) {
                HighwayModel highway = highWayUtil.getById(cal.getEquipment().getHwId());
                HighwayModel pHw = highWayUtil.getById(highway.getPid());
                equipmentFcVo.setHighway(pHw);
                //根据路段id获取摄像头列表
                List<FogCalculator> fogCalculators = fcFactory.getCalculatorsByHwId(String.valueOf(cal.getEquipment().getHwId()));
                equipmentFcVo.setFogCalculatorList(fogCalculators);
                return Result.ok(equipmentFcVo);
            } else {
                return Result.error("获取失败");
            }
			 /*Date imgTime = cal.getImgtime();
			 if(imgTime != null) {
				 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			 }*/
        }else {
            return Result.error("获取失败");
        }
    }

    @ResponseBody
    @PostMapping("/lookup/updateWinds/{epId}")
    public JSONObject updateWinds(@PathVariable("epId") String epId) {
//		HashMap history=cameraService.getCameraImg(epId);
//		JSONObject result=new JSONObject();
//		if(history!=null) {
//			String path=(String) history.get("imgfn");
//	    	int fMeter=(Integer) history.get("fMeter");
//	    	result.put("path", path);
//	    	result.put("fMeter", fMeter);
//		}
        JSONObject result = new JSONObject();
        WindCalculator cal = wcFactory.getCalculator(epId);
        if (cal != null) {
            result.put("fMeter", (cal.getWinds()));
            result.put("windd", (cal.getWindd()));
            result.put("adjoinEpid", (cal.getAdjoinEpid()));
            FogCalculator fogCalculator = fcFactory.getCalculator(cal.getAdjoinEpid());
            if (fogCalculator != null){
                result.put("imgpath", (fogCalculator.getImgpath()));
            }
        }

        return result;
        //return "system/equipment/edit";
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
}
