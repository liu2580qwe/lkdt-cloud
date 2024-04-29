package org.lkdt.modules.radar.controller;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.lkdt.common.api.vo.Result;
import org.lkdt.common.aspect.annotation.AutoLog;
import org.lkdt.common.system.query.QueryGenerator;
import org.lkdt.common.system.vo.LoginUser;
import org.lkdt.common.util.StringUtils;
import org.lkdt.common.util.oConvertUtils;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.lkdt.modules.radar.entity.ZcLdEquipment;
import org.lkdt.modules.radar.entity.ZcLdRadarVideo;
import org.lkdt.modules.radar.entity.ZcLdUnit;
import org.lkdt.modules.radar.service.IHighwayService;
import org.lkdt.modules.radar.service.IZcLdRadarEquipmentService;
import org.lkdt.modules.radar.service.IZcLdRadarVideoService;
import org.lkdt.modules.radar.service.IZcLdUnitService;
import org.lkdt.modules.radar.vo.ZcLdUnitPage;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
 /**
 * @description 雷达单元表
 * @author jeecg-boot
 * @date   2021-07-23
 * @version V1.0
 */
@Api(tags="雷达单元表")
@RestController
@RequestMapping("/radar/zcLdUnit")
@Slf4j
public class ZcLdUnitController {
	@Autowired
	private IZcLdUnitService zcLdUnitService;
	@Autowired
	private IZcLdRadarEquipmentService zcLdRadarEquipmentService;
	 @Autowired
	 private IZcLdRadarVideoService iZcLdRadarVideoService;
	@Autowired
	private IHighwayService iHighwayService;
	 /**
	  * 雷达设备在线信息的初始化
	  */
	 @PostMapping("/initializeRadarRealTimeInformation")
	 public Result<?> initializeRadarRealTimeInformation(){
		 //1. 查询所有路段下的所有单元
		 List<ZcLdUnit> ldUnits = zcLdUnitService.list();
		 try {
			 //2. 初始化存储容器unitMap
			 for (ZcLdUnit ldUnit:ldUnits){
				if(!LeidaPublicController.unitMap.containsKey(ldUnit.getId())){
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("camOnlineNumber",0);
					jsonObject.put("camOfflineNumber",0);
					jsonObject.put("radarOnlineNumber",0);
					jsonObject.put("radarOfflineNumber",0);
					jsonObject.put("currDateTime","1900-12-01 12:00:00.222");
					jsonObject.put("unitId",ldUnit.getId());
					jsonObject.put("jarLastTime","1900-12-01 12:00:00.222");
					jsonObject.put("jarName","");
					jsonObject.put("jarVersion","");
					jsonObject.put("cpuUsage",0);
					jsonObject.put("memUsage",0);
					jsonObject.put("storageUsage",null);
					//3. 根据单元id查询雷达设备
					List<ZcLdEquipment> equipments = zcLdRadarEquipmentService.selectByMainId(ldUnit.getId());
					JSONArray equipmentsArray = new JSONArray();
					for(ZcLdEquipment equipment:equipments){
						JSONObject equObj = new JSONObject();
						equObj.put("radarId",equipment.getId());
						equObj.put("dataLastTime","1900-12-01 12:00:00.222");
						equObj.put("heatLastTime","1900-12-01 12:00:00.222");
						equObj.put("radarStatus",0);
						equipmentsArray.add(equObj);
					}
					jsonObject.put("radarOfflineNumber",equipmentsArray.size());
					jsonObject.put("radar",equipmentsArray);
					//4. 根据单元id查询摄像头设备
					List<ZcLdRadarVideo> videos = iZcLdRadarVideoService.selectByUnitId(ldUnit.getId());
					JSONArray videosArray = new JSONArray();
					for(ZcLdRadarVideo video:videos){
						JSONObject videoObj = new JSONObject();
						videoObj.put("cameraId","");
						videoObj.put("id",video.getId());
						videoObj.put("status",0);
						videosArray.add(videoObj);
					}
					jsonObject.put("camOfflineNumber",videosArray.size());
					jsonObject.put("cameraList",videosArray);
					LeidaPublicController.unitMap.put(ldUnit.getId(),jsonObject);
				}
			 }
		 } catch (Exception e) {
			 return Result.error("初始化失败！");
		 }
		 return Result.ok("初始化成功！");
	 }
	/**
	 * 分页列表查询
	 *
	 * @param zcLdUnit
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "雷达单元表-分页列表查询")
	@ApiOperation(value="雷达单元表-分页列表查询", notes="雷达单元表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(ZcLdUnit zcLdUnit,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		List<String> ids = new ArrayList<>(25);
		if(!StringUtils.isEmpty(zcLdUnit.getHwId())){
			ids = iHighwayService.queryChildNodesByHwId(zcLdUnit.getHwId());
			ids.add(zcLdUnit.getHwId());
			zcLdUnit.setHwId(String.join(",",ids));
		}
		QueryWrapper<ZcLdUnit> queryWrapper = QueryGenerator.initQueryWrapper(zcLdUnit, req.getParameterMap());
		if(ids.size()==1){
			queryWrapper.eq("hw_id",zcLdUnit.getHwId());
		}
		Page<ZcLdUnit> page = new Page<>(pageNo, pageSize);
		IPage<ZcLdUnit> pageList = zcLdUnitService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param zcLdUnitPage
	 * @return
	 */
	@AutoLog(value = "雷达单元表-添加")
	@ApiOperation(value="雷达单元表-添加", notes="雷达单元表-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody ZcLdUnitPage zcLdUnitPage) {
		ZcLdUnit zcLdUnit = new ZcLdUnit();
		BeanUtils.copyProperties(zcLdUnitPage, zcLdUnit);
		zcLdUnitService.saveMain(zcLdUnit, zcLdUnitPage.getZcLdRadarEquipmentList());
		return Result.ok("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param zcLdUnitPage
	 * @return
	 */
	@AutoLog(value = "雷达单元表-编辑")
	@ApiOperation(value="雷达单元表-编辑", notes="雷达单元表-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody ZcLdUnitPage zcLdUnitPage) {
		ZcLdUnit zcLdUnit = new ZcLdUnit();
		BeanUtils.copyProperties(zcLdUnitPage, zcLdUnit);
		ZcLdUnit zcLdUnitEntity = zcLdUnitService.getById(zcLdUnit.getId());
		if(zcLdUnitEntity==null) {
			return Result.error("未找到对应数据");
		}
		zcLdUnitService.updateMain(zcLdUnit, zcLdUnitPage.getZcLdRadarEquipmentList());
		return Result.ok("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "雷达单元表-通过id删除")
	@ApiOperation(value="雷达单元表-通过id删除", notes="雷达单元表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		List<ZcLdEquipment> radarEquipments= zcLdRadarEquipmentService.selectByMainId(id);
		if(radarEquipments.size()!=0){
			return Result.error("删除失败!该路段单元仍存有设备...");
		}
		zcLdUnitService.delMain(id);
		return Result.ok("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "雷达单元表-批量删除")
	@ApiOperation(value="雷达单元表-批量删除", notes="雷达单元表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		List<String> idList = Arrays.asList(ids.split(","));
		for(String id:idList){
			List<ZcLdEquipment> equipments = zcLdRadarEquipmentService.selectByMainId(id);
			if(equipments.size()!=0){
				return Result.error("批量删除失败！单元下仍有设备...");
			}
			zcLdUnitService.delMain(id);
		}
		return Result.ok("批量删除成功！");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "雷达单元表-通过id查询")
	@ApiOperation(value="雷达单元表-通过id查询", notes="雷达单元表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		ZcLdUnit zcLdUnit = zcLdUnitService.getById(id);
		if(zcLdUnit==null) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(zcLdUnit);

	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "雷达设备表-通过主表ID查询")
	@ApiOperation(value="雷达设备表-通过主表ID查询", notes="雷达设备表-通过主表ID查询")
	@GetMapping(value = "/queryZcLdRadarEquipmentByMainId")
	public Result<?> queryZcLdRadarEquipmentListByMainId(@RequestParam(name="id",required=true) String id) {
		List<ZcLdEquipment> zcLdRadarEquipmentList = zcLdRadarEquipmentService.selectByMainId(id);
		IPage <ZcLdEquipment> page = new Page<>();
		page.setRecords(zcLdRadarEquipmentList);
		page.setTotal(zcLdRadarEquipmentList.size());
		return Result.ok(page);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param zcLdUnit
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, ZcLdUnit zcLdUnit) {
      // Step.1 组装查询条件查询数据
      QueryWrapper<ZcLdUnit> queryWrapper = QueryGenerator.initQueryWrapper(zcLdUnit, request.getParameterMap());
      LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

      //Step.2 获取导出数据
      List<ZcLdUnit> queryList = zcLdUnitService.list(queryWrapper);
      // 过滤选中数据
      String selections = request.getParameter("selections");
      List<ZcLdUnit> zcLdUnitList = new ArrayList<ZcLdUnit>();
      if(oConvertUtils.isEmpty(selections)) {
          zcLdUnitList = queryList;
      }else {
          List<String> selectionList = Arrays.asList(selections.split(","));
          zcLdUnitList = queryList.stream().filter(item -> selectionList.contains(item.getId())).collect(Collectors.toList());
      }

      // Step.3 组装pageList
      List<ZcLdUnitPage> pageList = new ArrayList<ZcLdUnitPage>();
      for (ZcLdUnit main : zcLdUnitList) {
          ZcLdUnitPage vo = new ZcLdUnitPage();
          BeanUtils.copyProperties(main, vo);
          List<ZcLdEquipment> zcLdRadarEquipmentList = zcLdRadarEquipmentService.selectByMainId(main.getId());
          vo.setZcLdRadarEquipmentList(zcLdRadarEquipmentList);
          pageList.add(vo);
      }

      // Step.4 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      mv.addObject(NormalExcelConstants.FILE_NAME, "雷达单元表列表");
      mv.addObject(NormalExcelConstants.CLASS, ZcLdUnitPage.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("雷达单元表数据", "导出人:"+sysUser.getRealname(), "雷达单元表"));
      mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
      return mv;
    }

    /**
    * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
      MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
      Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
      for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
          MultipartFile file = entity.getValue();
          ImportParams params = new ImportParams();
          params.setTitleRows(2);
          params.setHeadRows(1);
          params.setNeedSave(true);
          try {
              List<ZcLdUnitPage> list = ExcelImportUtil.importExcel(file.getInputStream(), ZcLdUnitPage.class, params);
              for (ZcLdUnitPage page : list) {
                  ZcLdUnit po = new ZcLdUnit();
                  BeanUtils.copyProperties(page, po);
                  zcLdUnitService.saveMain(po, page.getZcLdRadarEquipmentList());
              }
              return Result.ok("文件导入成功！数据行数:" + list.size());
          } catch (Exception e) {
              log.error(e.getMessage(),e);
              return Result.error("文件导入失败:"+e.getMessage());
          } finally {
              try {
                  file.getInputStream().close();
              } catch (IOException e) {
                  e.printStackTrace();
              }
          }
      }
      return Result.ok("文件导入失败！");
    }

	 /**
	  * 设备在线信息
	  * @param queryParams 查询条件
	  * @return 反馈信息
	  */
	@ApiOperation("设备在线信息")
	@RequestMapping("/equipmentOnlineInfo")
	public Result<?> equipmentOnlineInfo(@RequestBody JSONObject queryParams,
										 @RequestParam(value = "page",required = true,defaultValue = "1")Integer page,
										 @RequestParam(value = "pageSize",required = true,defaultValue = "8")Integer pageSize){
		//1. 获取查询参数
		String hwId = queryParams.getString("hwId");
		String frontEndProcessorStatus = queryParams.getString("frontEndProcessorStatus");
		String hasOffLine = queryParams.getString("hasOffLine");
		String unitId = queryParams.getString("unitId");
		//2. 模糊查询单元ID
		List<String> idlikes = new ArrayList<>();
		if(!StringUtils.isEmpty(unitId)){
			idlikes = zcLdUnitService.selectIdsLikeId(unitId);
		}
		//3. 查询树子节点，查询路段下的所有单元id
		List<String> keys = new ArrayList<>();
		List<String> hwids = iHighwayService.queryChildNodesByHwId(hwId);
		hwids.add(hwId);
		for(String id:hwids){
			keys.addAll(zcLdUnitService.selectIdsByHwId(id));
		}
		//4. 如果前台根据单元编码查询，这句代码是用来过滤的，否则查询路段下的所有单元
		if(!StringUtils.isEmpty(unitId)){
			keys.retainAll(idlikes);
		}
		//5. 检测设备在线情况
		List<JSONObject> jsonObjects = new ArrayList<>();
		for(String key:keys){
			if(LeidaPublicController.unitMap.containsKey(key)){
				JSONObject jsonObject = LeidaPublicController.unitMap.get(key);
				jsonObject.put("radarOnlineNumber",0);
				jsonObject.put("radarOfflineNumber",0);
				jsonObject.put("camOnlineNumber",0);
				jsonObject.put("camOfflineNumber",0);
				//6 判断前置机是否离线---currDateTime
				Date date = jsonObject.getObject("currDateTime",Date.class);
				Date now = new Date();
				long differenceTime = (now.getTime()-date.getTime())/1000/60;
				if(differenceTime>5){
					jsonObject.put("frontEndProcessorStatus","0");
				}else{
					jsonObject.put("frontEndProcessorStatus","1");
				}
				//7. 作为条件查询的判断---如果前置机状态和要查询状态不一致，则不添加进jsonObjects
				if(!StringUtils.isEmpty(frontEndProcessorStatus)&&!frontEndProcessorStatus.equals(jsonObject.getString("frontEndProcessorStatus"))){
					continue;
				}
				//8. 判断雷达是否离线----heatLastTime
				JSONArray radarArray = jsonObject.getJSONArray("radar");
				if(!oConvertUtils.isEmpty(radarArray)){
					for(int index=0;index<radarArray.size();index++){
						JSONObject radarObject = radarArray.getJSONObject(index);
						Date radarDate = radarObject.getObject("heatLastTime",Date.class);
						if(!oConvertUtils.isEmpty(radarDate)){
							differenceTime = (now.getTime()-radarDate.getTime())/1000/60;
							if(differenceTime>5){
								radarObject.put("radarStatus","0");
								jsonObject.put("radarOfflineNumber",jsonObject.getIntValue("radarOfflineNumber")+1);
							}else{
								radarObject.put("radarStatus","1");
								jsonObject.put("radarOnlineNumber",jsonObject.getIntValue("radarOnlineNumber")+1);
							}
						}
					}
				}
				//9. 作为条件查询的判断,hasOffLine(0单元雷达存在离线，1单元雷达全在线)  radarOfflineNumber离线数量
				//9.1 查离线，离线数量 1+
				boolean bool1 = "0".equals(hasOffLine) && jsonObject.getIntValue("radarOfflineNumber") > 0;
				//9.2 查在线，离线数量 0
				boolean bool2 = "1".equals(hasOffLine)&&jsonObject.getIntValue("radarOfflineNumber")==0;
				if((!StringUtils.isEmpty(hasOffLine))&&!(bool1||bool2)){
					continue;
				}
				//10. 获取摄像机在线情况
				JSONArray cameraArray = jsonObject.getJSONArray("cameraList");
				if(!oConvertUtils.isEmpty(cameraArray)){
					for(int i=0;i<cameraArray.size();i++){
						JSONObject camObject = cameraArray.getJSONObject(i);
						if("NORMAL".equalsIgnoreCase(camObject.getString("status"))){
							jsonObject.put("camOnlineNumber",jsonObject.getIntValue("camOnlineNumber")+1);
						}else{
							jsonObject.put("camOfflineNumber",jsonObject.getIntValue("camOfflineNumber")+1);
						}
					}
				}
				jsonObjects.add(jsonObject);
			}
		}
		//11. 分页
		int maxPage = jsonObjects.size()/pageSize+1;
		int toIndex = page*pageSize;
		int fromIndex = ( page-1)*pageSize;
		if( page>=maxPage){
			toIndex = jsonObjects.size();
			fromIndex = (jsonObjects.size()/pageSize)*pageSize;
		}
		List<JSONObject> resObj = jsonObjects.subList(fromIndex,toIndex);
		if(jsonObjects.size()!=0){
			resObj.get(0).put("total",jsonObjects.size());
		}else{
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("total",0);
			jsonObjects.add(jsonObject);
		}
		return Result.ok(resObj);
	}
}
