package org.lkdt.modules.traffic.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.lkdt.common.api.vo.Result;
import org.lkdt.common.aspect.annotation.AutoLog;
import org.lkdt.common.system.base.controller.CloudController;
import org.lkdt.common.system.query.QueryGenerator;
import org.lkdt.common.system.vo.LoginUser;
import org.lkdt.common.util.oConvertUtils;
import org.lkdt.modules.traffic.entity.TollStationEvent;
import org.lkdt.modules.traffic.entity.TollStationRecorder;
import org.lkdt.modules.traffic.service.ITollStationEventService;
import org.lkdt.modules.traffic.service.ITollStationRecorderService;
import org.lkdt.modules.traffic.vo.TollStationEventPage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

 /**
 * @Description: 收费站封路事件
 * @Author: jeecg-boot
 * @Date:   2021-06-03
 * @Version: V1.0
 */
@Api(tags="收费站封路事件")
@RestController
@RequestMapping("/traffic/tollStationEvent")
@Slf4j
public class TollStationEventController extends CloudController<TollStationEvent, ITollStationEventService> {
	 @Autowired
	 private ITollStationEventService TollStationEventService;
	 @Autowired
	 private ITollStationRecorderService tollStationRecorderService;

	 /**
	  * 分页列表查询
	  *
	  * @param TollStationEvent
	  * @param pageNo
	  * @param pageSize
	  * @param req
	  * @return
	  */
	 @AutoLog(value = "收费站封路事件-分页列表查询")
	 @ApiOperation(value="收费站封路事件-分页列表查询", notes="收费站封路事件-分页列表查询")
	 @GetMapping(value = "/list")
	 public Result<?> queryPageList(TollStationEvent TollStationEvent,
									@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									@RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									HttpServletRequest req) {
		 QueryWrapper<TollStationEvent> queryWrapper = QueryGenerator.initQueryWrapper(TollStationEvent, req.getParameterMap());
		 Page<TollStationEvent> page = new Page<TollStationEvent>(pageNo, pageSize);
		 IPage<TollStationEvent> pageList = TollStationEventService.page(page, queryWrapper);
		 return Result.ok(pageList);
	 }

	 /**
	  *   添加
	  *
	  * @param TollStationEventPage
	  * @return
	  */
	 @AutoLog(value = "收费站封路事件-添加")
	 @ApiOperation(value="收费站封路事件-添加", notes="收费站封路事件-添加")
	 @PostMapping(value = "/add")
	 public Result<?> add(@RequestBody TollStationEventPage TollStationEventPage) {
		 TollStationEvent TollStationEvent = new TollStationEvent();
		 BeanUtils.copyProperties(TollStationEventPage, TollStationEvent);
		 TollStationEventService.saveMain(TollStationEvent, TollStationEventPage.getTollStationRecorderList());
		 return Result.ok("添加成功！");
	 }

	 /**
	  *  编辑
	  *
	  * @param TollStationEventPage
	  * @return
	  */
	 @AutoLog(value = "收费站封路事件-编辑")
	 @ApiOperation(value="收费站封路事件-编辑", notes="收费站封路事件-编辑")
	 @PutMapping(value = "/edit")
	 public Result<?> edit(@RequestBody TollStationEventPage TollStationEventPage) {
		 TollStationEvent TollStationEvent = new TollStationEvent();
		 BeanUtils.copyProperties(TollStationEventPage, TollStationEvent);
		 TollStationEvent TollStationEventEntity = TollStationEventService.getById(TollStationEvent.getId());
		 if(TollStationEventEntity==null) {
			 return Result.error("未找到对应数据");
		 }
		 TollStationEventService.updateMain(TollStationEvent, TollStationEventPage.getTollStationRecorderList());
		 return Result.ok("编辑成功!");
	 }

	 /**
	  *   通过id删除
	  *
	  * @param id
	  * @return
	  */
	 @AutoLog(value = "收费站封路事件-通过id删除")
	 @ApiOperation(value="收费站封路事件-通过id删除", notes="收费站封路事件-通过id删除")
	 @DeleteMapping(value = "/delete")
	 public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		 TollStationEventService.delMain(id);
		 return Result.ok("删除成功!");
	 }

	 /**
	  *  批量删除
	  *
	  * @param ids
	  * @return
	  */
	 @AutoLog(value = "收费站封路事件-批量删除")
	 @ApiOperation(value="收费站封路事件-批量删除", notes="收费站封路事件-批量删除")
	 @DeleteMapping(value = "/deleteBatch")
	 public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		 this.TollStationEventService.delBatchMain(Arrays.asList(ids.split(",")));
		 return Result.ok("批量删除成功！");
	 }

	 /**
	  * 通过id查询
	  *
	  * @param id
	  * @return
	  */
	 @AutoLog(value = "收费站封路事件-通过id查询")
	 @ApiOperation(value="收费站封路事件-通过id查询", notes="收费站封路事件-通过id查询")
	 @GetMapping(value = "/queryById")
	 public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		 TollStationEvent TollStationEvent = TollStationEventService.getById(id);
		 if(TollStationEvent==null) {
			 return Result.error("未找到对应数据");
		 }
		 return Result.ok(TollStationEvent);

	 }

	 /**
	  * 通过id查询
	  *
	  * @param id
	  * @return
	  */
	 @AutoLog(value = "收费站封路事件详细-通过主表ID查询")
	 @ApiOperation(value="收费站封路事件详细-通过主表ID查询", notes="收费站封路事件详细-通过主表ID查询")
	 @GetMapping(value = "/queryTollStationRecorderByMainId")
	 public Result<?> queryTollStationRecorderListByMainId(@RequestParam(name="id",required=true) String id) {
		 List<TollStationRecorder> tollStationRecorderList = tollStationRecorderService.selectByMainId(id);
		 IPage <TollStationRecorder> page = new Page<>();
		 page.setRecords(tollStationRecorderList);
		 page.setTotal(tollStationRecorderList.size());
		 return Result.ok(page);
	 }

	 /**
	  * 导出excel
	  *
	  * @param request
	  * @param TollStationEvent
	  */
	 @RequestMapping(value = "/exportXls")
	 public ModelAndView exportXls(HttpServletRequest request, TollStationEvent TollStationEvent) {
		 // Step.1 组装查询条件查询数据
		 QueryWrapper<TollStationEvent> queryWrapper = QueryGenerator.initQueryWrapper(TollStationEvent, request.getParameterMap());
		 LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

		 //Step.2 获取导出数据
		 List<TollStationEvent> queryList = TollStationEventService.list(queryWrapper);
		 // 过滤选中数据
		 String selections = request.getParameter("selections");
		 List<TollStationEvent> TollStationEventList = new ArrayList<TollStationEvent>();
		 if(oConvertUtils.isEmpty(selections)) {
			 TollStationEventList = queryList;
		 }else {
			 List<String> selectionList = Arrays.asList(selections.split(","));
			 TollStationEventList = queryList.stream().filter(item -> selectionList.contains(item.getId())).collect(Collectors.toList());
		 }

		 // Step.3 组装pageList
		 List<TollStationEventPage> pageList = new ArrayList<TollStationEventPage>();
		 for (TollStationEvent main : TollStationEventList) {
			 TollStationEventPage vo = new TollStationEventPage();
			 BeanUtils.copyProperties(main, vo);
			 List<TollStationRecorder> tollStationRecorderList = tollStationRecorderService.selectByMainId(main.getId());
			 vo.setTollStationRecorderList(tollStationRecorderList);
			 pageList.add(vo);
		 }

		 // Step.4 AutoPoi 导出Excel
		 ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
		 mv.addObject(NormalExcelConstants.FILE_NAME, "收费站封路事件列表");
		 mv.addObject(NormalExcelConstants.CLASS, TollStationEventPage.class);
		 mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("收费站封路事件数据", "导出人:"+sysUser.getRealname(), "收费站封路事件"));
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
			 MultipartFile file = entity.getValue();// 获取上传文件对象
			 ImportParams params = new ImportParams();
			 params.setTitleRows(2);
			 params.setHeadRows(1);
			 params.setNeedSave(true);
			 try {
				 List<TollStationEventPage> list = ExcelImportUtil.importExcel(file.getInputStream(), TollStationEventPage.class, params);
				 for (TollStationEventPage page : list) {
					 TollStationEvent po = new TollStationEvent();
					 BeanUtils.copyProperties(page, po);
					 TollStationEventService.saveMain(po, page.getTollStationRecorderList());
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

}
