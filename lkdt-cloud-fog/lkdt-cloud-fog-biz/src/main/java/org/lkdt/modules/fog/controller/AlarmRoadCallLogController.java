package org.lkdt.modules.fog.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;

import org.lkdt.common.api.vo.Result;
import org.lkdt.common.aspect.annotation.AutoLog;
import org.lkdt.common.system.base.controller.CloudController;
import org.lkdt.common.system.query.QueryGenerator;
import org.lkdt.modules.fog.entity.AlarmRoadCallLog;
import org.lkdt.modules.fog.service.IAlarmRoadCallLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

 /**
 * @Description: 路段告警电话通知日志表
 * @Author: jeecg-boot
 * @Date:   2021-05-07
 * @Version: V1.0
 */
@Api(tags="路段告警电话通知日志表")
@RestController
@RequestMapping("/fog/alarmRoadCallLog")
@Slf4j
public class AlarmRoadCallLogController extends CloudController<AlarmRoadCallLog, IAlarmRoadCallLogService> {
	@Autowired	
	private IAlarmRoadCallLogService alarmRoadCallLogService;
	
	/**
	 * 分页列表查询
	 *
	 * @param alarmRoadCallLog
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "路段告警电话通知日志表-分页列表查询")
	@ApiOperation(value="路段告警电话通知日志表-分页列表查询", notes="路段告警电话通知日志表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(AlarmRoadCallLog alarmRoadCallLog,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<AlarmRoadCallLog> queryWrapper = QueryGenerator.initQueryWrapper(alarmRoadCallLog, req.getParameterMap());
		Page<AlarmRoadCallLog> page = new Page<AlarmRoadCallLog>(pageNo, pageSize);
		IPage<AlarmRoadCallLog> pageList = alarmRoadCallLogService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param alarmRoadCallLog
	 * @return
	 */
	@AutoLog(value = "路段告警电话通知日志表-添加")
	@ApiOperation(value="路段告警电话通知日志表-添加", notes="路段告警电话通知日志表-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody AlarmRoadCallLog alarmRoadCallLog) {
		alarmRoadCallLogService.save(alarmRoadCallLog);
		return Result.ok("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param alarmRoadCallLog
	 * @return
	 */
	@AutoLog(value = "路段告警电话通知日志表-编辑")
	@ApiOperation(value="路段告警电话通知日志表-编辑", notes="路段告警电话通知日志表-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody AlarmRoadCallLog alarmRoadCallLog) {
		alarmRoadCallLogService.updateById(alarmRoadCallLog);
		return Result.ok("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "路段告警电话通知日志表-通过id删除")
	@ApiOperation(value="路段告警电话通知日志表-通过id删除", notes="路段告警电话通知日志表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		alarmRoadCallLogService.removeById(id);
		return Result.ok("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "路段告警电话通知日志表-批量删除")
	@ApiOperation(value="路段告警电话通知日志表-批量删除", notes="路段告警电话通知日志表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.alarmRoadCallLogService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "路段告警电话通知日志表-通过id查询")
	@ApiOperation(value="路段告警电话通知日志表-通过id查询", notes="路段告警电话通知日志表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		AlarmRoadCallLog alarmRoadCallLog = alarmRoadCallLogService.getById(id);
		if(alarmRoadCallLog==null) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(alarmRoadCallLog);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param alarmRoadCallLog
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, AlarmRoadCallLog alarmRoadCallLog) {
        return super.exportXls(request, alarmRoadCallLog, AlarmRoadCallLog.class, "路段告警电话通知日志表");
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
        return super.importExcel(request, response, AlarmRoadCallLog.class);
    }

}
