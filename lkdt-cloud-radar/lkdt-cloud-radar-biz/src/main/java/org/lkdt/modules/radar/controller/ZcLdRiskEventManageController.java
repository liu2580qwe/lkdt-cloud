package org.lkdt.modules.radar.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.lkdt.common.api.vo.Result;
import org.lkdt.common.aspect.annotation.AutoLog;
import org.lkdt.common.system.base.controller.CloudController;
import org.lkdt.common.system.query.QueryGenerator;
import org.lkdt.common.system.vo.DictModel;
import org.lkdt.common.util.StringUtils;
import org.lkdt.modules.api.SysBaseRemoteApi;
import org.lkdt.modules.radar.entity.ZcLdEquipment;
import org.lkdt.modules.radar.entity.ZcLdRiskEventManage;
import org.lkdt.modules.radar.service.IZcLdEquipmentService;
import org.lkdt.modules.radar.service.IZcLdRiskEventManageService;
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
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

 /**
 * @Description: 雷达事件管理
 * @Author: jeecg-boot
 * @Date:   2021-08-27
 * @Version: V1.0
 */
@Api(tags="雷达事件管理")
@RestController
@RequestMapping("/radar/zcLdRiskEventManage")
@Slf4j
public class ZcLdRiskEventManageController extends CloudController<ZcLdRiskEventManage, IZcLdRiskEventManageService> {
	
	@Autowired
	private IZcLdRiskEventManageService zcLdRiskEventManageService;
	
	@Autowired
    private SysBaseRemoteApi sysBaseRemoteApi;
	
	@Autowired
	private IZcLdEquipmentService zcLdEquipmentService;
	
	
	/**
	 * 分页列表查询
	 *
	 * @param zcLdRiskEventManage
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "雷达事件管理-分页列表查询")
	@ApiOperation(value="雷达事件管理-分页列表查询", notes="雷达事件管理-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(ZcLdRiskEventManage zcLdRiskEventManage,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<ZcLdRiskEventManage> queryWrapper = QueryGenerator.initQueryWrapper(zcLdRiskEventManage, req.getParameterMap());
		queryWrapper.ne("video_url","-");
		queryWrapper.isNotNull("video_url");
		Page<ZcLdRiskEventManage> page = new Page<>(pageNo, pageSize);
		IPage<ZcLdRiskEventManage> pageList = zcLdRiskEventManageService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param zcLdRiskEventManage
	 * @return
	 */
	@AutoLog(value = "雷达事件管理-添加")
	@ApiOperation(value="雷达事件管理-添加", notes="雷达事件管理-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody ZcLdRiskEventManage zcLdRiskEventManage) {
		zcLdRiskEventManageService.save(zcLdRiskEventManage);
		return Result.ok("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param zcLdRiskEventManage
	 * @return
	 */
	@AutoLog(value = "雷达事件管理-编辑")
	@ApiOperation(value="雷达事件管理-编辑", notes="雷达事件管理-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody ZcLdRiskEventManage zcLdRiskEventManage) {
		zcLdRiskEventManageService.updateById(zcLdRiskEventManage);
		return Result.ok("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "雷达事件管理-通过id删除")
	@ApiOperation(value="雷达事件管理-通过id删除", notes="雷达事件管理-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		zcLdRiskEventManageService.removeById(id);
		return Result.ok("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "雷达事件管理-批量删除")
	@ApiOperation(value="雷达事件管理-批量删除", notes="雷达事件管理-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.zcLdRiskEventManageService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "雷达事件管理-通过id查询")
	@ApiOperation(value="雷达事件管理-通过id查询", notes="雷达事件管理-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		ZcLdRiskEventManage zcLdRiskEventManage = zcLdRiskEventManageService.getById(id);
		if(zcLdRiskEventManage==null) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(zcLdRiskEventManage);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param zcLdRiskEventManage
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, ZcLdRiskEventManage zcLdRiskEventManage) {
        return super.exportXls(request, zcLdRiskEventManage, ZcLdRiskEventManage.class, "雷达事件管理");
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
        return super.importExcel(request, response, ZcLdRiskEventManage.class);
    }
    
    
    /***************** 报表 *********************/
    @AutoLog(value = "日报统计")
	@ApiOperation(value="日报统计", notes="日报统计")
    @GetMapping(value = "/queryDailyStatistics")
    public Result<?> queryDailyStatistics(@RequestParam("beginTime") String beginTime,
    		@RequestParam("endTime") String endTime){
    	JSONArray series = new JSONArray();
    	// 雷达事件数据
    	List<ZcLdRiskEventManage> dataList = zcLdRiskEventManageService.queryDailyStatistics(beginTime, endTime);
    	
    	// 查询雷达事件类型数据字典
        List<DictModel> eventTypes = sysBaseRemoteApi.queryDictItemsByCode("radar_event_type");
        
        // 雷达设备
        List<ZcLdEquipment> equipments = zcLdEquipmentService.queryEquipment();
        
        List<String> equList = new ArrayList<>();
        for (ZcLdEquipment equ : equipments) {
        	if(StringUtils.equals(equ.getEquType(), "001")) {
        		equList.add(equ.getEquName());
        	}
		}
        
        List<String> eventTypeList = new ArrayList<String>();
        for (DictModel item : eventTypes) {
        	eventTypeList.add(item.getText());
        	JSONObject eventJson = new JSONObject();
			eventJson.put("name", item.getText());
			eventJson.put("type", "bar");
			JSONObject emphasis = new JSONObject();
			emphasis.put("focus", "series");
			eventJson.put("emphasis", emphasis);
			Integer[] data = new Integer[equList.size()];
			for (int i = 0 ; i < equipments.size(); i++) {
				boolean isExists = false;
				ZcLdEquipment equ = equipments.get(i);
				if(StringUtils.equals(equ.getEquType(), "001")) {
					for(ZcLdRiskEventManage EventData : dataList) {
						if(StringUtils.equals(equ.getId(), EventData.getRadarId()) &&
								StringUtils.equals(item.getValue(), EventData.getEventType()) ) {
							isExists = true;
							data[i] = EventData.getTjCount();
						}
					}
				}
				if(!isExists) {
					data[i] = 0;
				}
			}
			
			eventJson.put("data", data);
			series.add(eventJson);
		}
        
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("legend", eventTypeList);
        jsonObject.put("xAxisData", equList);
        jsonObject.put("series", series);
        return Result.ok(jsonObject);
    }
    
    

}
