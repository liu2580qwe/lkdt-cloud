package org.lkdt.modules.radar.controller;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lkdt.common.api.vo.Result;
import org.lkdt.common.system.vo.DictModel;
import org.lkdt.common.util.CommonUtils;
import org.lkdt.common.util.StringUtils;
import org.lkdt.common.util.oConvertUtils;
import org.lkdt.modules.api.SysBaseRemoteApi;
import org.lkdt.modules.radar.entity.LdUnitSituationDirection;
import org.lkdt.modules.radar.entity.LdUnitSituationLane;
import org.lkdt.modules.radar.entity.ZcLdEquipment;
import org.lkdt.modules.radar.entity.ZcLdEventManage;
import org.lkdt.modules.radar.entity.ZcLdLaneInfo;
import org.lkdt.modules.radar.entity.ZcLdRadarVideo;
import org.lkdt.modules.radar.entity.ZcLdRoadflowThreestatus;
import org.lkdt.modules.radar.entity.ZcLdUnit;
import org.lkdt.modules.radar.entity.ZcLdWind;
import org.lkdt.modules.radar.service.ILdUnitSituationDirectionService;
import org.lkdt.modules.radar.service.ILdUnitSituationLaneService;
import org.lkdt.modules.radar.service.IZcLdEventManageService;
import org.lkdt.modules.radar.service.IZcLdLaneInfoService;
import org.lkdt.modules.radar.service.IZcLdRadarEquipmentService;
import org.lkdt.modules.radar.service.IZcLdRadarVideoService;
import org.lkdt.modules.radar.service.IZcLdRoadflowThreestatusService;
import org.lkdt.modules.radar.service.IZcLdThreeStatusCoefficientService;
import org.lkdt.modules.radar.service.IZcLdUnitService;
import org.lkdt.modules.radar.service.IZcLdWindService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
/**
 * 公共接口
 * @author Cai Xibei
 * @version 1.0.0
 * @project zcloud-boot-parent
 * @ClassName org.lkdt.modules.radar.controller.LeidaPublicController
 * @createTime 2021年07月29日 11:26:00
 */
@Api(tags="公共接口")
@RestController
@Slf4j
@RequestMapping("/radar/public")
public class LeidaPublicController {
    public static Map<String,JSONObject> unitMap = new HashMap<>();
    @Autowired
    private IZcLdUnitService zcLdUnitService;
    @Autowired
    private IZcLdRadarEquipmentService iZcLdRadarEquipmentService;
    @Autowired
    private IZcLdRadarVideoService iZcLdRadarVideoService;
    @Autowired
    private IZcLdLaneInfoService iZcLdLaneInfoService;
    @Autowired
    private ILdUnitSituationLaneService ldUnitSituationLaneService;
    @Autowired
    private ILdUnitSituationDirectionService ldUnitSituationDirectionService;
    @Autowired
	private IZcLdEventManageService zcLdEventManageService;
    @Autowired
    private IZcLdWindService iZcLdWindService;
    @Value("${lkdt.uploadTypeQZJ}")
    private String uploadTypeQZJ;
    @Autowired
    private IZcLdRoadflowThreestatusService zcLdRoadflowThreestatusService;
    @Autowired
    private SysBaseRemoteApi sysBaseRemoteApi;
    @Autowired
    private IZcLdThreeStatusCoefficientService zcLdThreeStatusCoefficientService;
    
    private Map<String,Double> avgVyMap = new HashMap<>();
    

    /**
     * 交通流三态数据
     * @param jsonObject 接口数据
     * @return 操作反馈
     */
    @RequestMapping("/roadFlowThreeStatus")
    @ApiOperation("交通流三态数据")
    public Result<?> roadFlowThreeStatus(@RequestBody JSONObject jsonObject){
        ZcLdRoadflowThreestatus zcLdRoadflowThreestatus = JSONObject.toJavaObject(jsonObject,ZcLdRoadflowThreestatus.class);
        zcLdRoadflowThreestatusService.save(zcLdRoadflowThreestatus);
        return Result.ok("操作成功！");
    }

    /**
     * 设备实时检测功能、雷达态势入库
     * @param jsonObject 单元设备集合对象
     * @return 反馈消息，接口调用成功与否
     */
    @RequestMapping("/equipmentDetection")
    @ApiOperation("接收前置机设备心跳数据接口")
    @Transactional
    public Result<?> equipmentDetection(@RequestBody JSONObject jsonObject){
        //1. 获取单元ID（设备实时检测）
        String unitId = jsonObject.getString("unitId");
        if(StringUtils.isEmpty(unitId)){
            return Result.error("The link unit ID is missing in the interface parameter'query Params'");
        }
        //3. 不存在，新增
        if(!unitMap.containsKey(unitId)){
            unitMap.put(unitId,jsonObject);
            return Result.ok("success");
        }
        //4. 存在，更新数据
        JSONObject equObject = unitMap.get(unitId);
        updateData(equObject,unitId,jsonObject);
        //5. 获取车道级态势、来去向态势数据（雷达态势功能）
        radarSituationWarehousing(jsonObject);
        return Result.ok("success");
    }

    /**
     * 更新数据
     * @param equObject
     * @param unitId
     * @param jsonObject
     */
    private void updateData(JSONObject equObject,String unitId,JSONObject jsonObject){
        equObject.put("currDateTime",jsonObject.getString("currDateTime"));
        equObject.put("jarLastTime",jsonObject.getString("jarLastTime"));
        equObject.put("jarName",jsonObject.getString("jarName"));
        equObject.put("jarVersion",jsonObject.getString("jarVersion"));
        equObject.put("radar",jsonObject.getJSONArray("radar"));
        equObject.put("cameraList",jsonObject.getJSONArray("cameraList"));
        equObject.put("cpuUsage",jsonObject.getString("cpuUsage"));
        equObject.put("memUsage",jsonObject.getString("memUsage"));
        equObject.put("storageUsage",jsonObject.getJSONObject("storageUsage"));
        unitMap.put(unitId,equObject);
    }

    /**
     * 雷达态势入库
     * @param object 接口参数数据
     */
    private void radarSituationWarehousing(JSONObject object){
        JSONArray ldUnitSituationDirections = null;
        JSONArray ldUnitSituationLanes = null;
        JSONArray radars = object.getJSONArray("radar");
        for(int radarIndex=0;radarIndex<radars.size();radarIndex++){
            JSONObject radarObject = radars.getJSONObject(radarIndex);
            // 获取数据，转化态势对象
            ldUnitSituationDirections = radarObject.getJSONArray("ldUnitSituationDirection");
            ldUnitSituationLanes = radarObject.getJSONArray("ldUnitSituationLane");
            // 态势入库
            if(!oConvertUtils.isEmpty(ldUnitSituationDirections)){
                for(int index=0;index<ldUnitSituationDirections.size();index++){
                    LdUnitSituationDirection ldUnitSituationDirection = ldUnitSituationDirections.getObject(index,LdUnitSituationDirection.class);
                    ldUnitSituationDirectionService.save(ldUnitSituationDirection);
                }
            }
            if(!oConvertUtils.isEmpty(ldUnitSituationLanes)){
                for(int index=0;index<ldUnitSituationLanes.size();index++){
                    LdUnitSituationLane ldUnitSituationLane = ldUnitSituationLanes.getObject(index,LdUnitSituationLane.class);
                    ldUnitSituationLaneService.save(ldUnitSituationLane);
                }
            }
        }
    }

    /**
     * 设备在线信息---根据单元ID查询
     * @param queryParams 查询条件
     * @return 查询结果
     */
    @ApiOperation("单元设备信息获取接口")
    @RequestMapping("/queryByUnitId")
    public Result<?> equipmentOnlineInfo(@RequestBody JSONObject queryParams){
        // 1. 获取查询参数
        String unitId = queryParams.getString("unitId");
        if(StringUtils.isEmpty(unitId)){
            return Result.error("The link unit ID is missing in the interface parameter'queryParams'");
        }
        // 2. 根据id查询雷达单元信息
        ZcLdUnit zcLdUnit = zcLdUnitService.selectLdUnitById(unitId);
        List<ZcLdLaneInfo> zcLdLaneInfos = new ArrayList<>();
        if(!oConvertUtils.isEmpty(zcLdUnit)){
            // 3. 根据单元id查询单元雷达列表
            List<ZcLdEquipment> equipments = iZcLdRadarEquipmentService.selectByMainId(unitId);
            for(ZcLdEquipment equipment:equipments){
                // 4. 根据雷达id查询摄像头设备--关联表查询
                List<ZcLdRadarVideo> videos = iZcLdRadarVideoService.selectByEpId(equipment.getId());
                equipment.setVideos(videos);
                // 5. 根据雷达id查询路段信息列表
                zcLdLaneInfos.addAll(iZcLdLaneInfoService.selectByMainId(equipment.getId()));
                //根据雷达ID查询大风配置--关联表查询
                List<ZcLdWind> winds = iZcLdWindService.selectByEpId(equipment.getId());
                equipment.setWinds(winds);
            }
            // 6. 查询雷达事件类型数据字典
            List<DictModel> radarEventTypes = sysBaseRemoteApi.queryDictItemsByCode("radar_event_type");
            // 7. 查询车道类型数据字典
            List<DictModel> laneTypes = sysBaseRemoteApi.queryDictItemsByCode("lane_type");
            zcLdUnit.setEquipments(equipments);
            zcLdUnit.setZcLdLaneInfos(zcLdLaneInfos);
            zcLdUnit.setLane_types(laneTypes);
            zcLdUnit.setRadar_event_types(radarEventTypes);
        }
        return Result.ok(zcLdUnit);
    }

    /**
     * 接收前置机事件
     * @param zcLdEventManage 雷达事件对象
     * @param vedioFile 视频文件
     * @return Result<?> 反馈消息
     */
	@ApiOperation(value="接收前置机事件", notes="接收")
	@PostMapping(value = "/addRadarEvent")
	public Result<?> addRadarEvent(ZcLdEventManage zcLdEventManage,
                                   @RequestParam(value = "vedioFile") MultipartFile vedioFile) {
        // 1. 文件上传到 minio对象存储服务器
        String radarId = zcLdEventManage.getRadarId();
        if(StringUtils.isEmpty(radarId)){
            return Result.error("参数字段错误！");
        }
        String videoUrl = CommonUtils.upload(vedioFile,radarId,uploadTypeQZJ);
        // 2. 雷达事件数据入库
        zcLdEventManage.setVideoUrl(videoUrl);
        zcLdEventManage.setVedioName(videoUrl.substring(videoUrl.lastIndexOf("/")+1));
        zcLdEventManageService.save(zcLdEventManage);
    	return Result.ok("入库成功！");
    }
    
	
	@ApiOperation(value = "根据 g 值与 p 值的(白天)关系图，取푝 ∈ (0.7,1.0)时，以每 0.01 为一段， g 值对应的 85%位数据")
	@GetMapping(value = "/percentile85")
    public Result<?> percentile85(@RequestParam("radarId") String radarId,
    		@RequestParam("direction") String direction,
    		@RequestParam("beginTime") String beginTime,
    		@RequestParam("endTime") String endTime){
		
		
		double[] equation = zcLdThreeStatusCoefficientService.getThreeStatusEquation(radarId, direction, beginTime, endTime);
    	return Result.ok(equation);
    }
	
	
	@ApiOperation(value="接收前置机雷达平均速度（30秒周期）", notes="接收")
	@PostMapping(value = "/addRadarAvgSpeed")
	public Result<?> addRadarAvgSpeed(@RequestBody JSONObject jsonObject) {
        try {
        	if(jsonObject != null && !jsonObject.isEmpty()) {
        		avgVyMap.put(jsonObject.getString("radarId")+"_"+jsonObject.getString("direction"), jsonObject.getDouble("avgVy"));
        	}
        	return Result.ok("接收成功");
		} catch (Exception e) {
			return Result.error(e.getMessage());
		}
    }
	
	@ApiOperation(value = "雷达平均速度曲线图")
	@GetMapping(value = "/queryRadarAvgSpeed")
	public Result<?> queryRadarAvgSpeed(){
		JSONObject jsonObject = new JSONObject();
		List<String> legendList = new ArrayList<>();
		JSONObject seriesJson = new JSONObject();
		List<Double> seriesData = new ArrayList<>();
		JSONObject yAxis = new JSONObject();
		try {
			if(avgVyMap != null && !avgVyMap.isEmpty()) {
				//查询雷达设备
				List<ZcLdEquipment> list = iZcLdRadarEquipmentService.list();
				for (ZcLdEquipment zcLdEquipment : list) {
	        		for(Map.Entry<String, Double> entry : avgVyMap.entrySet()) {
	        			String key = entry.getKey();
	        			Double value = entry.getValue();
	        			String[] keys = key.split("_");
	        			if(StringUtils.equals(zcLdEquipment.getId(), keys[0])) {
	        				legendList.add(key);
	        				BigDecimal b = new BigDecimal(value);
	        				seriesData.add(b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
	        			}
	        			
	        		}
				}
			}
			
			seriesJson.put("type", "line");
			seriesJson.put("smooth", "true");
			seriesJson.put("data", seriesData);
			
			yAxis.put("type", "value");
			jsonObject.put("xAxis", legendList);
			jsonObject.put("yAxis", yAxis);
			jsonObject.put("legend", legendList);
			jsonObject.put("series", seriesJson);
        	return Result.ok(jsonObject);
		} catch (Exception e) {
			return Result.error(e.getMessage());
		}
	}
    

}
