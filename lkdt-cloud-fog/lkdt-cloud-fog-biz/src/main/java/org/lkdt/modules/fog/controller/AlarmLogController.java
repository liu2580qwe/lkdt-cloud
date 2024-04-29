package org.lkdt.modules.fog.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ch.qos.logback.core.pattern.ConverterUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mongodb.client.result.UpdateResult;
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
import org.lkdt.common.util.StringUtils;
import org.lkdt.common.util.oConvertUtils;
import org.lkdt.modules.fog.entity.Alarm;
import org.lkdt.modules.fog.entity.AlarmLog;
import org.lkdt.modules.fog.mongodb.MongoLogTemplate;
import org.lkdt.modules.fog.service.IAlarmLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

 /**
 * @Description: 告警日志
 * @Author: jeecg-boot
 * @Date:   2021-10-08
 * @Version: V1.0
 */
@Api(tags="告警日志")
@RestController
@RequestMapping("/fog/alarmLog")
@Slf4j
public class AlarmLogController extends CloudController<AlarmLog, IAlarmLogService> {
	@Autowired
	private IAlarmLogService alarmLogService;

	@Autowired
	private MongoLogTemplate mongoLogTemplate;

	/**
	 * 分页列表查询
	 *
	 * @param alarmLog
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "告警日志-分页列表查询")
	@ApiOperation(value="告警日志-分页列表查询", notes="告警日志-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(AlarmLog alarmLog,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) throws ParseException {
		IPage<AlarmLog> pageList = new Page<>(pageNo, pageSize);
		Date nameDate = (new SimpleDateFormat("yyyy-MM-dd")).parse(req.getParameterMap().get("startDate")[0]);
		Date startDate = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).parse(req.getParameterMap().get("startDate")[0]);
		Date endDate = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).parse(req.getParameterMap().get("endDate")[0]);
		String epId = StringUtils.EMPTY;
		if(oConvertUtils.isNotEmpty(req.getParameterMap().get("epId"))){
			epId = req.getParameterMap().get("epId")[0];
		}
		SimpleDateFormat nameDateFormat = new SimpleDateFormat("yyyyMM");
		SimpleDateFormat timeDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Map<String,Object> pageMap = mongoLogTemplate.queryAllAlarmLog(
				nameDateFormat.format(nameDate),
				pageNo, pageSize,
				timeDateFormat.format(startDate),
				timeDateFormat.format(endDate),
				epId);
		pageList.setRecords((List)pageMap.get("alarmLogs"));
		pageList.setTotal((Long)pageMap.get("count"));
		return Result.ok(pageList);
	}

	 /**
	  *  编辑
	  *
	  * @param alarmLog
	  * @return
	  */
	 @AutoLog(value = "告警日志-编辑")
	 @ApiOperation(value="告警日志-编辑", notes="告警日志-编辑")
	 @PutMapping(value = "/update")
	 public Result<?> edit(@RequestBody AlarmLog alarmLog) throws ParseException {
		 Date nameDate = (new SimpleDateFormat("yyyy-MM-dd")).parse(alarmLog.getDateTime());
		 SimpleDateFormat nameDateFormat = new SimpleDateFormat("yyyyMM");
		 String collectionName = nameDateFormat.format(nameDate);
		 UpdateResult result = mongoLogTemplate.updateAlarmLog(alarmLog,collectionName);
		 return Result.ok(result);
	 }
	
//	/**
//	 *   添加
//	 *
//	 * @param alarmLog
//	 * @return
//	 */
//	@AutoLog(value = "告警日志-添加")
//	@ApiOperation(value="告警日志-添加", notes="告警日志-添加")
//	@PostMapping(value = "/add")
//	public Result<?> add(@RequestBody AlarmLog alarmLog) {
//		alarmLogService.save(alarmLog);
//		return Result.ok("添加成功！");
//	}

//
//	/**
//	 *   通过id删除
//	 *
//	 * @param id
//	 * @return
//	 */
//	@AutoLog(value = "告警日志-通过id删除")
//	@ApiOperation(value="告警日志-通过id删除", notes="告警日志-通过id删除")
//	@DeleteMapping(value = "/delete")
//	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
//		alarmLogService.removeById(id);
//		return Result.ok("删除成功!");
//	}
//
//	/**
//	 *  批量删除
//	 *
//	 * @param ids
//	 * @return
//	 */
//	@AutoLog(value = "告警日志-批量删除")
//	@ApiOperation(value="告警日志-批量删除", notes="告警日志-批量删除")
//	@DeleteMapping(value = "/deleteBatch")
//	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
//		this.alarmLogService.removeByIds(Arrays.asList(ids.split(",")));
//		return Result.ok("批量删除成功!");
//	}
//
//	/**
//	 * 通过id查询
//	 *
//	 * @param id
//	 * @return
//	 */
//	@AutoLog(value = "告警日志-通过id查询")
//	@ApiOperation(value="告警日志-通过id查询", notes="告警日志-通过id查询")
//	@GetMapping(value = "/queryById")
//	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
//		AlarmLog alarmLog = alarmLogService.getById(id);
//		if(alarmLog==null) {
//			return Result.error("未找到对应数据");
//		}
//		return Result.ok(alarmLog);
//	}
//
//    /**
//    * 导出excel
//    *
//    * @param request
//    * @param alarmLog
//    */
//    @RequestMapping(value = "/exportXls")
//    public ModelAndView exportXls(HttpServletRequest request, AlarmLog alarmLog) {
//        return super.exportXls(request, alarmLog, AlarmLog.class, "告警日志");
//    }
//
//    /**
//      * 通过excel导入数据
//    *
//    * @param request
//    * @param response
//    * @return
//    */
//    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
//    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
//        return super.importExcel(request, response, AlarmLog.class);
//    }

}
