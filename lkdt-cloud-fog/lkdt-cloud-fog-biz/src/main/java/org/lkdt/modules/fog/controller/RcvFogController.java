package org.lkdt.modules.fog.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.lkdt.common.api.vo.Result;
import org.lkdt.common.aspect.annotation.AutoLog;
import org.lkdt.common.system.vo.HighwayModel;
import org.lkdt.common.util.HighWayUtil;
import org.lkdt.common.util.StringUtils;
import org.lkdt.modules.fog.calculator.CalculatorService;
import org.lkdt.modules.fog.calculator.FcFactory;
import org.lkdt.modules.fog.calculator.FogCalculator;
import org.lkdt.modules.fog.calculator.FogListenerMap;
import org.lkdt.modules.fog.entity.Equipment;
import org.lkdt.modules.fog.service.IAlarmService;
import org.lkdt.modules.fog.service.IEquipmentService;
import org.lkdt.modules.fog.service.IThirdPartyService;
import org.lkdt.modules.fog.service.ImageService;
import org.lkdt.modules.fog.vo.FogHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lkdt.modules.fog.controller.AlarmReportController.logger;

/**
 * @author HuangJunYao
 * @date 2021/5/7
 */
@RestController
@Slf4j
public class RcvFogController {
    @Autowired
    private ImageService imageService;

    @Value("${useoss.enable}")
    private boolean ossEnable;

    @Autowired
    private CalculatorService calculatorService;

    @Autowired
    private IEquipmentService equipmentService;

    @Autowired
    private IAlarmService alarmService;

    @Autowired
    private FcFactory fcFactory;

    @Autowired
    private IThirdPartyService thirdPartyService;

    @Autowired
    private HighWayUtil highWayUtil;

    /**
     * 回应AI服务，网络正常
     *
     * @return
     * @throws Exception
     */
    @SuppressWarnings({"unchecked"})
    @ResponseBody
    @PostMapping("/public/rcvfog")
    Result<?> rcvfog(@RequestBody Map<String, Object> info) throws Exception {
        // 获取雾可见度
        String sysName = (String) info.get("sysName");
        log.error("---> 接收数据:" + sysName + "-----sysUrl" + (String) info.get("sysUrl"));
//		log.error("---> 接收数据:" + info.toString());
        String sysUrl = (String) info.get("sysUrl");
        FogListenerMap.LISTENER_MAP.put(sysName, sysUrl);

        List<Map<String, Object>> result = (List<Map<String, Object>>) info.get("result");
        if (result == null || result.size() == 0) {
            return Result.ok();
        }
        List<FogHistory> fhs = new ArrayList<FogHistory>();
        // ossEnable 判断是否只使用oss不用本地
        if(ossEnable){
            //TODO 单OSS存储
            for (Map<String, Object> row : result) {
                FogHistory fdo = imageService.createOssAiImg(row);
                if (fdo != null) {
                    fhs.add(fdo);
                }
            }
        }else{
            // TODO OSS+本地
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (Map<String, Object> row : result) {
                FogHistory fdo = new FogHistory();
                String epId = (String) row.get("epId");
                fdo.setEpId(epId);
                fdo.setFmodel((String) row.get("fmodel"));
                fdo.setfValue((Integer) row.get("fValue"));
                String fogtime = (String) row.get("fogtime");
                if (StringUtils.isNotEmpty(fogtime)){
                    fdo.setfSampleTime(format.parse(fogtime));
                }
                String img = (String) row.get("fimg");
                if (StringUtils.isNotEmpty(img)) {
                    // 保存图片
                    FogCalculator fc = fcFactory.getCalculator(epId);
                    if(fc==null) {
                        logger.error("---> 摄像头未初始化:" + epId);
                        continue;
                    }
                    fdo.setImgfn((String) row.get("fname"));
                    FogHistory fdo2 = new FogHistory();
                    fdo2 = imageService.createLocalAiImg(row);
//                    ImageUtils.base64ToImage(img, imgpath);
//                    fdo2 = imageService.createOssAiImg(row);
                    fhs.add(fdo2);
                }
            }
        }

        calculatorService.execute(fhs, sysName);
        return Result.ok();
    }

    /**
     * 获取所有建模数据的数量
     *
     * @return
     */
    @GetMapping("/public/queryEquipmentCount")
    public int queryEquipmentCount() {
        int count = equipmentService.queryEquipmentCount();
        return count;
    }

    /**
     * 获取所有建模数据（不分页）
     *
     * @return
     */
    @GetMapping("/public/queryEquipments")
    public String queryEquipments() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("offset", null);
        map.put("limit", null);
        List<Equipment> equipments = equipmentService.queryEquipments(map);
        return JSON.toJSONString(equipments);
    }

    /**
     * 根据hwid获取所有建模数据（不分页）
     *
     * @return
     */
    @GetMapping("/public/queryEquipmentsByHwId")
    public String queryEquipmentsByHwId(String hwId, String hwIds) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("offset", null);
        map.put("limit", null);
        boolean flg = false;
        if (org.apache.commons.lang.StringUtils.isNotEmpty(hwId)) {
            map.put("hwId", hwId);
            flg = true;
        }
        if (org.apache.commons.lang.StringUtils.isNotEmpty(hwIds)) {
            map.put("hwIds", hwIds);
            flg = true;
        }
        if (!flg) {
            return "{msg:查询条件为空}";
        }
        List<Equipment> equipments = equipmentService.queryEquipments(map);
        for(Equipment equipment : equipments){
            equipment.setEpId(equipment.getId());
            equipment.setA("224.855");
            equipment.setB("0.999743");
            equipment.setC("0.003779");
        }
        return JSON.toJSONString(equipments);
    }


    /**
     * 获取所有建模数据（分页）
     *
     * @param offset
     * @param limit
     * @return
     */
    @GetMapping("/public/queryEquipmentsPage")
    public String queryEquipmentsPage(@RequestParam int offset, int limit) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("offset", offset);
        if (limit < 1) {
            limit = 0;
        }
        map.put("limit", limit);
        List<Equipment> equipments = equipmentService.queryEquipments(map);
        return JSON.toJSONString(equipments);
    }

    /**
     * 查询摄像头能见度
     * @param param
     * startTime: yyyy-MM-dd HH:mm:ss.SSS
     * endTime: yyyy-MM-dd HH:mm:ss.SSS
     * @return
     */
    @AutoLog(value = "查询摄像头能见度")
    @ApiOperation(value="查询摄像头能见度", notes="查询摄像头能见度")
    @ResponseBody
    @PostMapping("/public/getEquDistance")
    public Result<?> getEquDistance(@RequestBody JSONObject param) {
        Result<JSONObject> result = new Result<>();
        JSONObject jsonObject = new JSONObject();
        try {
            String startTime = param.getString("startTime");
            String endTime = param.getString("endTime");
            String epId = param.getString("epId");
            String distance_statistic = alarmService.getDistanceStatistic(startTime, endTime, "", epId);
            String distance_now = String.valueOf(fcFactory.getCalculator(epId).getDistance());
            jsonObject.put("distance_statistic", distance_statistic);
            jsonObject.put("distance_now", distance_now);
            result.setResult(jsonObject);
            result.setSuccess(true);
            return result;
        } catch (Exception e) {
            log.error("查询摄像头能见度失败", e);
            return Result.error("查询摄像头能见度失败");
        }
    }

    /**
     * 根据机房查摄像头数据
     * @param roomId 机房ID
     * @return <code>java.util.List</code> 查询结果集
     */
    @GetMapping("/public/queryCameraIdsByComputeRoomId")
    public String queryCameraIdsByComputeRoomId(@RequestParam(required = true, value = "roomId") String roomId) {
        return JSON.toJSONString(equipmentService.queryCameraIdsByComputeRoomId(roomId));
    }

    /**
     * 发送拉流异常数据
     * @param epIds
     * @return
     */
    @PostMapping("/public/sendVideoStreamEx")
    public Result<?> sendVideoStreamEx(@RequestBody List<String> epIds) {
        try {
            thirdPartyService.sendVideoStreamEx(epIds);
            return Result.ok("发送拉流异常数据成功");
        } catch (Exception e) {
            log.error("发送拉流异常数据失败", e);
            return Result.error("发送拉流异常数据失败");
        }

    }

    /**
     * 首页摄像头实时影像查询
     *
     * @param epId
     * @return
     */
    @ResponseBody
    @GetMapping("/public/ivics_lookup/{epId}")
    public JSONObject ivics_lookup(@PathVariable("epId") String epId) {
        FogCalculator cal = fcFactory.getCalculator(epId);
        JSONObject equipmentFcVo = new JSONObject();
        if (cal != null) {
            equipmentFcVo.put("fogCalculator",cal);
            if (cal.getEquipment().getHwId() != null) {
                HighwayModel highwayModel = highWayUtil.getById(cal.getEquipment().getHwId());
                HighwayModel pHw = highWayUtil.getById(highwayModel.getPid());
                equipmentFcVo.put("highway",pHw);
                //根据路段id获取摄像头列表
                List<FogCalculator> fogCalculators = fcFactory.getCalculatorsByHwId(String.valueOf(cal.getEquipment().getHwId()));
                equipmentFcVo.put("fogCalculatorList",fogCalculators);
                return equipmentFcVo;
            } else {
                return null;
            }
			 /*Date imgTime = cal.getImgtime();
			 if(imgTime != null) {
				 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			 }*/
        }else {
            return null;
        }
    }

}
