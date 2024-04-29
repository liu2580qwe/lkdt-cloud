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
import org.lkdt.common.system.base.controller.CloudController;
import org.lkdt.common.system.query.QueryGenerator;
import org.lkdt.common.system.vo.HighwayModel;
import org.lkdt.common.system.vo.LoginUser;
import org.lkdt.common.util.HighWayUtil;
import org.lkdt.common.util.ShiroUtils;
import org.lkdt.common.util.StringUtils;
import org.lkdt.common.util.oConvertUtils;
import org.lkdt.modules.api.HighwayApi;
import org.lkdt.modules.api.SysBaseRemoteApi;
import org.lkdt.modules.fog.calculator.*;
import org.lkdt.modules.fog.entity.ArtificalModiLog;
import org.lkdt.modules.fog.entity.Equipment;
import org.lkdt.modules.fog.entity.EquipmentModel;
import org.lkdt.modules.fog.service.IArtificalModiLogService;
import org.lkdt.modules.fog.service.IEquipmentService;
import org.lkdt.modules.fog.service.IThirdPartyService;
import org.lkdt.modules.fog.vo.EquipmentFcVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @Description: Camera equipment table
 * @Author: Cai Xibei
 * @Date: 2021-04-23
 * @Version: V1.0
 */
@Api(tags = "Camera equipment table")
@RestController
@RequestMapping("/fog/zcEquipment")
@Slf4j
public class EquipmentController extends CloudController<Equipment, IEquipmentService> {
    @Autowired
    private IEquipmentService equipmentService;
    @Autowired
    private FcFactory fcFactory;
    @Autowired
    private IArtificalModiLogService artificalModiLogService;
    @Autowired
    private FogCalRedis fogCalRedis;
    @Autowired
    private SysBaseRemoteApi sysBaseRemoteApi;
    @Autowired
    private HighwayApi highwayApi;
    @Autowired
    private WcFactory wcFactory;
    @Autowired
    private HighWayUtil highWayUtil;
    @Autowired
    private IThirdPartyService thirdPartyService;


    /**
     * Pagination list query
     *
     * @param equipment
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "Camera equipment table-paging list query")
    @ApiOperation(value = "Camera equipment table-paging list query", notes = "Camera equipment table-paging list query")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(Equipment equipment,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        List<String> ids = new ArrayList<>(25);
        if (!StringUtils.isEmpty(equipment.getHwId())) {
            ids = highwayApi.queryChildNodes(equipment.getHwId());
            ids.add(equipment.getHwId());
            equipment.setHwId(String.join(",", ids));
        }
        QueryWrapper<Equipment> queryWrapper = QueryGenerator.initQueryWrapper(equipment, req.getParameterMap());
        //Because when ids has only one value, it is not in the in query but in the fuzzy query, so in this case we must disable the fuzzy query
        if (ids.size() == 1) {
            queryWrapper.eq("hw_id", equipment.getHwId());
        }
        Page<Equipment> page = new Page<>(pageNo, pageSize);
        IPage<Equipment> pageList = equipmentService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    /**
     * add to
     *
     * @param equipment
     * @return
     */
    @AutoLog(value = "Camera equipment table-add")
    @ApiOperation(value = "Camera equipment table-add", notes = "Camera equipment table-add")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody Equipment equipment) {
        equipmentService.save(equipment);
        return Result.ok("添加成功！");
    }

    /**
     * edit
     *
     * @param equipmentModel
     * @return
     */
    @AutoLog(value = "Camera equipment table-edit")
    @ApiOperation(value = "Camera equipment table-edit", notes = "Camera equipment table-edit")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody EquipmentModel equipmentModel) {
        // processing modification time and processor
        FogCalculator cal = fcFactory.getCalculator(equipmentModel.getId());
        Equipment equipment = new Equipment();
        BeanUtils.copyProperties(equipmentModel, equipment);
        equipmentModel = cal.getEquipment();
        try {
            reflect(equipment,equipmentModel);
        } catch (Exception e) {
            log.error("",e);
        }
        Date now = new Date();
        equipment.setUpdateTime(now);

        if (StringUtils.isNotEmpty(equipment.getState())) {
            int type = 0;
            if ("0".equals(equipment.getState())) {
                type = -2;
            } else {
                type = -1;
            }
            fogCalRedis.fogOption(type, true, equipment.getState(), cal);
            try {
                ArtificalModiLog artificalModiLog = new ArtificalModiLog();
                artificalModiLog.setId(StringUtils.getUUID());
                artificalModiLog.setArtificialAlarmImgUrl(cal.getImgpath());
                artificalModiLog.setCreateTime(now);
                artificalModiLog.setCreateBy(equipment.getUpdateBy());
                artificalModiLog.setExceptionType(equipment.getState());
                artificalModiLog.setLogType("1");
                artificalModiLogService.save(artificalModiLog);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (ShiroUtils.getUser() != null) {
            equipment.setUpdateBy(ShiroUtils.getUserId().toString());
        } else {
            equipment.setUpdateBy("1");
        }
        //TODO: Synchronously modify redis's data when editing
        fogCalRedis.setEquipmentRedis(equipmentModel);
        equipmentService.updateById(equipment);
        if("11".equals(equipment.getState()) || "12".equals(equipment.getState()) || "13".equals(equipment.getState())){
            thirdPartyService.sendVideoQualityEx(cal);
        }
        /*fcFactory.putCalculator(cal, equipment);*/
        return Result.ok("编辑成功!");
    }

    /**
     * 遍历对象属性，更新数据
     * @param e
     * @param equipmentModel
     * @throws Exception
     */
    public void reflect(Equipment e,EquipmentModel equipmentModel) throws Exception{
        Class cls = e.getClass();
        Field[] fields = cls.getDeclaredFields();
        for(int i=0; i<fields.length; i++){
            Field f = fields[i];
            f.setAccessible(true);
            if(!oConvertUtils.isEmpty(f.get(e))){
                f.set(equipmentModel,f.get(e));
            }
            System.out.println("属性名:" + f.getName() + " 属性值:" + f.get(e));
        }
    }

    /**
     * delete by id
     *
     * @param id
     * @return
     */
    @AutoLog(value = "Camera device table-delete by id")
    @ApiOperation(value = "Camera device table-delete by id", notes = "Camera device table-delete by id")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        equipmentService.removeById(id);
        return Result.ok("删除成功!");
    }

    /**
     * batch deletion
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "Camera device table-batch delete")
    @ApiOperation(value = "Camera device table-batch delete", notes = "Camera device table-batch delete")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.equipmentService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok("批量删除成功!");
    }

    /**
     * query by id
     *
     * @param id
     * @return
     */
    @AutoLog(value = "Camera equipment table-query by id")
    @ApiOperation(value = "Camera equipment table-query by id", notes = "Camera equipment table-query by id")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        Equipment equipment = equipmentService.getById(id);
        if (equipment == null) {
            return Result.error("未找到对应数据");
        }
        return Result.ok(equipment);
    }

    /**
     * export excel
     *
     * @param request
     * @param equipment
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, Equipment equipment) {
        return super.exportXls(request, equipment, Equipment.class, "摄像头设备表");
    }

    /**
     * import data through excel
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, Equipment.class);
    }

    @ResponseBody
    @PostMapping("/lookup/updateimgpath/{epId}")
    public JSONObject updateimgpath(@PathVariable("epId") String epId) {
	/*HashMap history=cameraService.getCameraImg(epId);
	JSONObject result=new JSONObject();
	if(history!=null) {
		String path=(String) history.get("imgfn");
		int fMeter=(Integer) history.get("fMeter");
		result.put("path", path);
		result.put("fMeter", fMeter);
	}*/
        JSONObject result = new JSONObject();
        FogCalculator cal = fcFactory.getCalculator(epId);
        if (cal != null) {
            result.put("fMeter", (cal.getDistance() != null ? cal.getDistance() : ""));
            result.put("imgpath", cal.getImgpath());
        }
        return result;
        /*return "system/equipment/edit";*/
    }

    /**
     * get a list of abnormal cameras
     *
     * @return
     */
    @RequestMapping("/getExeptionEqu")
    public @ResponseBody
    Result getExeptionEqu() {
        try {
//            Map<String, Object> param = new HashMap<>();
            LoginUser user = ShiroUtils.getUser();
            QueryWrapper wrapper = new QueryWrapper();
            if (user != null && user.getId() != null && !user.isAdmin()) {
                try {
                    wrapper.in("hw_id",user.getHwIds());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            List<Equipment> equipmentDOList = equipmentService.list(wrapper);
            //processing camera data
            JSONArray jsonArray = new JSONArray();
            try {
                for (Equipment cd : equipmentDOList) {
                    if ("0".equals(cd.getState()) || "9".equals(cd.getState())) {
                        continue;
                    }
                    FogCalculator fogCalculator = fcFactory.getCalculator(cd.getId());
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("lnglat", new String[]{fogCalculator.getEquipment().getLon(), fogCalculator.getEquipment().getLat()});
                    jsonObject.put("lng", fogCalculator.getEquipment().getLon());
                    jsonObject.put("lat", fogCalculator.getEquipment().getLat());
                    jsonObject.put("epId", fogCalculator.getEpId());
                    jsonObject.put("equCode", fogCalculator.getEquipment().getEquCode());
                    jsonObject.put("equName", fogCalculator.getEquipment().getEquName());
                    jsonObject.put("equLocation", fogCalculator.getEquipment().getEquLocation());
                    jsonObject.put("imgpath", fogCalculator.getImgpath());
                    if (cd.getState() == null) {
                        cd.setState("6");
                    }
                    jsonObject.put("state", cd.getState());
                    jsonObject.put("stateName", sysBaseRemoteApi.queryDictTextByKey("equipment_state", cd.getState()));
                    jsonArray.add(jsonObject);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return Result.ok(jsonArray);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询接口异常,获取摄像头数据失败....");
        }
    }

    /**
     * get the percentage of abnormal cameras
     *
     * @return
     */
    @RequestMapping("/getExeptionEquRate")
    public @ResponseBody
    Result getExeptionEquRate() {
        try {
            Map<String, Object> param = new HashMap<>();
            QueryWrapper wrapper = new QueryWrapper(param);
            LoginUser user = ShiroUtils.getUser();
            if (user != null && user.getId() != null && !user.isAdmin()) {
                try {
                    List<String> hwIds = user.getHwIds();
                    wrapper.in("hw_id",hwIds);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            List<Equipment> equipmentDOList = equipmentService.list(wrapper);
            //unqualified
            long isNotRight = 0;
            //qualified
            long isRight = 0;
            for (Equipment cd : equipmentDOList) {
                if ("0".equals(cd.getState()) || "9".equals(cd.getState())) {
                    isRight++;
                } else {
                    isNotRight++;
                }
            }
            JSONArray jsonArray = new JSONArray();
            JSONObject rightObj = new JSONObject();
            rightObj.put("name", "合格视频");
            rightObj.put("value", isRight);
            JSONObject notRightObj = new JSONObject();
            notRightObj.put("name", "不合格视频");
            notRightObj.put("value", isNotRight);
            jsonArray.add(rightObj);
            jsonArray.add(notRightObj);
            return Result.ok(jsonArray);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询接口异常,获取摄像头数据失败....");
        }
    }

    /**
     * Get the percentage of abnormal cameras
     *
     * @return
     */
    @RequestMapping("/getAllEquipments")
    public @ResponseBody
    Result getAllEquipments() {
        try {
            //openId判空
            List<FogCalculator> fogCalculatorList = fcFactory.getCalculators();
            JSONArray jsonArray = new JSONArray();
            FogCalculator firstCal = null;
            for (FogCalculator e : fogCalculatorList) {
                //Verify the legitimacy of latitude and longitude, filter no data cameras
                if (e == null) {
                    continue;
                }
                if (fogCalRedis.getPrevious(e) == null || !StringUtils.equals(e.getEquipment().getHwId(), fogCalRedis.getPrevious(e).getEquipment().getHwId())) {
                    firstCal = e;
                }
                if (e.getDistance() == null) {
                    continue;
                }
                if (e.isAbn()) {
                    continue;
                }
                if (e.getImgtime() == null || System.currentTimeMillis() - e.getImgtime().getTime() > 60 * 60 * 1000) {
                    continue;
                }
                if (StringUtils.isEmpty(e.getImgpath())) {
                    continue;
                }
                if (StringUtils.isEmpty(e.getEquipment().getLon()) || StringUtils.isEmpty(e.getEquipment().getLat())) {
                    continue;
                }
                int space = 0;
                if (e != firstCal && StringUtils.isNotEmpty(firstCal.getEquipment().getLon()) && StringUtils.isNotEmpty(firstCal.getEquipment().getLat())) {
                    space = (int) FogSpaceCal.getDistanceByLonLat(Double.valueOf(firstCal.getEquipment().getLon()),
                            Double.valueOf(firstCal.getEquipment().getLat()),
                            Double.valueOf(e.getEquipment().getLon()),
                            Double.valueOf(e.getEquipment().getLat())) / 1000;
                }
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("lnglat", new String[]{e.getEquipment().getLon(), e.getEquipment().getLat()});
                jsonObject.put("lng", e.getEquipment().getLon());
                jsonObject.put("lat", e.getEquipment().getLat());
                jsonObject.put("epId", e.getEpId());
                jsonObject.put("equCode", e.getEquipment().getEquCode());
                jsonObject.put("equName", e.getEquipment().getEquName());
                jsonObject.put("equLocation", e.getEquipment().getEquLocation());
                jsonObject.put("state", e.getEquipment().getState());
                jsonObject.put("space", space);
                jsonArray.add(jsonObject);
            }
            return Result.ok(jsonArray);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取摄像头列表失败");
        }
    }

    @GetMapping("/queryCameraIdsByDeptId")
    public List<String> queryCameraIdsByDeptId(@RequestParam(required = true, value = "deptId") String deptId) {
        return equipmentService.queryCameraIdsByDeptId(deptId);
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
        } else {
            return Result.error("获取失败");
        }
    }

    @ResponseBody
    @GetMapping("/equipment/lookup/updateWinds")
    public JSONObject updateWinds(@RequestParam String epId) {
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
            if (fogCalculator != null) {
                result.put("imgpath", (fogCalculator.getImgpath()));
            }
        }

        return result;
        //return "system/equipment/edit";
    }
}
