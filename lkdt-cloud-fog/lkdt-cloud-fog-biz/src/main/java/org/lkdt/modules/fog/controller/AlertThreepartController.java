package org.lkdt.modules.fog.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.lkdt.common.api.vo.Result;
import org.lkdt.common.aspect.annotation.AutoLog;
import org.lkdt.common.system.query.QueryGenerator;
import org.lkdt.common.system.vo.LoginUser;
import org.lkdt.common.util.HighWayUtil;
import org.lkdt.common.util.StringUtils;
import org.lkdt.common.util.oConvertUtils;
import org.lkdt.modules.api.HighwayApi;
import org.lkdt.modules.fog.calculator.FcFactory;
import org.lkdt.modules.fog.entity.AlertThreepart;
import org.lkdt.modules.fog.entity.AlertThreepartModel;
import org.lkdt.modules.fog.entity.AlertThreepartRecord;
import org.lkdt.modules.fog.service.IAlertThreepartRecordService;
import org.lkdt.modules.fog.service.IAlertThreepartService;
import org.lkdt.modules.fog.vo.AlertThreepartPage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

 /**
 * @Description: 三方告警信息
 * @Author: jeecg-boot
 * @Date:   2021-04-27
 * @Version: V1.0
 */
@Api(tags="三方告警信息")
@RestController
@RequestMapping("/fog/alertThreepart")
@Slf4j
public class AlertThreepartController {
	@Autowired
	private IAlertThreepartService alertThreepartService;
	@Autowired
	private IAlertThreepartRecordService alertThreepartRecordService;
	@Autowired
	private HighwayApi highwayApi;
	@Autowired
	private HighWayUtil highWayUtil;
	 @Autowired
	 private FcFactory fcFactory;
	/**
	 * 分页列表查询
	 *
	 * @param alertThreepart
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "三方告警信息-分页列表查询")
	@ApiOperation(value="三方告警信息-分页列表查询", notes="三方告警信息-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(AlertThreepart alertThreepart,
                                   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                   HttpServletRequest req) {
		List<String> ids = new ArrayList<>(25);
		if(!StringUtils.isEmpty(alertThreepart.getHwId())){
			ids = highwayApi.queryChildNodes(alertThreepart.getHwId());
			ids.add(alertThreepart.getHwId());
			alertThreepart.setHwId(String.join(",",ids));
		}
		QueryWrapper<AlertThreepart> queryWrapper = QueryGenerator.initQueryWrapper(alertThreepart, req.getParameterMap());
		//Because when there is only one value for ids, it is not in query but fuzzy query, so we have to disable fuzzy query in this situation
		if(ids.size()==1){
			queryWrapper.eq("hw_id",alertThreepart.getHwId());
		}
		Page<AlertThreepart> page = new Page<AlertThreepart>(pageNo, pageSize);
		IPage<AlertThreepart> pageList = alertThreepartService.page(page, queryWrapper);
		return Result.ok(pageList);
	}

	 @AutoLog(value = "三方告警信息-查询所有")
	 @ApiOperation(value="三方告警信息-查询所有", notes="三方告警信息-查询所有")
	 @GetMapping(value = "/queryAllList")
	 public Result<?> queryAllList(@RequestParam String beginTime,@RequestParam String endTime) {
		 QueryWrapper<AlertThreepart> queryWrapper = new QueryWrapper<>();
		 queryWrapper.between("create_time",beginTime,endTime);
		 queryWrapper.orderByDesc("create_time");
		 List<AlertThreepart> pageList = alertThreepartService.list(queryWrapper);
		 String hwName="";
		 String eqName="";
		 for (AlertThreepart a:pageList){
		 	 hwName=highWayUtil.getById(a.getHwId()).getName();
			 eqName=fcFactory.getCalculator(a.getEpId()).getEquipment().getEquName();
			 //将路段名与桩号存入id
			 a.setHwId(hwName);
			 a.setEpId(eqName);

		 }
		 return Result.ok(pageList);
	 }

	/**
	 *   添加
	 *
	 * @param alertThreepartPage
	 * @return
	 */
	@AutoLog(value = "三方告警信息-添加")
	@ApiOperation(value="三方告警信息-添加", notes="三方告警信息-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody AlertThreepartPage alertThreepartPage) {
		AlertThreepart alertThreepart = new AlertThreepart();
		BeanUtils.copyProperties(alertThreepartPage, alertThreepart);
		alertThreepartService.saveMain(alertThreepart, alertThreepartPage.getAlertThreepartRecordList());
		return Result.ok("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param alertThreepartPage
	 * @return
	 */
	@AutoLog(value = "三方告警信息-编辑")
	@ApiOperation(value="三方告警信息-编辑", notes="三方告警信息-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody AlertThreepartPage alertThreepartPage) {
		AlertThreepart alertThreepart = new AlertThreepart();
		BeanUtils.copyProperties(alertThreepartPage, alertThreepart);
		AlertThreepart alertThreepartEntity = alertThreepartService.getById(alertThreepart.getId());
		if(alertThreepartEntity==null) {
			return Result.error("未找到对应数据");
		}
		alertThreepartService.updateMain(alertThreepart, alertThreepartPage.getAlertThreepartRecordList());
		return Result.ok("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "三方告警信息-通过id删除")
	@ApiOperation(value="三方告警信息-通过id删除", notes="三方告警信息-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		alertThreepartService.delMain(id);
		return Result.ok("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "三方告警信息-批量删除")
	@ApiOperation(value="三方告警信息-批量删除", notes="三方告警信息-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.alertThreepartService.delBatchMain(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功！");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "三方告警信息-通过id查询")
	@ApiOperation(value="三方告警信息-通过id查询", notes="三方告警信息-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		AlertThreepart alertThreepart = alertThreepartService.getById(id);
		if(alertThreepart==null) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(alertThreepart);

	}
	
	/**
	 * 通过id查询
	 *
	 * @param alertThreepartRecord
	 * @return
	 */
	@AutoLog(value = "三方告警信息操作记录-通过主表ID查询")
	@ApiOperation(value="三方告警信息操作记录-通过主表ID查询", notes="三方告警信息操作记录-通过主表ID查询")
	@GetMapping(value = "/queryAlertThreepartRecordByMainId")
	public Result<?> queryAlertThreepartRecordListByMainId(AlertThreepartRecord alertThreepartRecord) {
		List<AlertThreepartRecord> alertThreepartRecordList = alertThreepartRecordService.selectByMainId(alertThreepartRecord.getId());
		IPage <AlertThreepartRecord> page = new Page<>();
		page.setRecords(alertThreepartRecordList);
		page.setTotal(alertThreepartRecordList.size());
		return Result.ok(page);
	}

	 /**
	  * confirmDetails
	  * @param alertThreepartId
	  * @param openid
	  * @return
	  */
	 @AutoLog(value = "三方告警信息操作记录-通过主表ID查询")
	 @ApiOperation(value="三方告警信息操作记录-通过主表ID查询", notes="三方告警信息操作记录-通过主表ID查询")
	 @GetMapping(value = "/confirmDetails")
	 AlertThreepartModel confirmDetails(@RequestParam String alertThreepartId, @RequestParam String openid){
	 	return alertThreepartService.confirmDetails(alertThreepartId, openid);
	 }

    /**
    * 导出excel
    *
    * @param request
    * @param alertThreepart
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, AlertThreepart alertThreepart) {
      // Step.1 组装查询条件查询数据
      QueryWrapper<AlertThreepart> queryWrapper = QueryGenerator.initQueryWrapper(alertThreepart, request.getParameterMap());
      LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

      //Step.2 获取导出数据
      List<AlertThreepart> queryList = alertThreepartService.list(queryWrapper);
      // 过滤选中数据
      String selections = request.getParameter("selections");
      List<AlertThreepart> alertThreepartList = new ArrayList<AlertThreepart>();
      if(oConvertUtils.isEmpty(selections)) {
          alertThreepartList = queryList;
      }else {
          List<String> selectionList = Arrays.asList(selections.split(","));
          alertThreepartList = queryList.stream().filter(item -> selectionList.contains(item.getId())).collect(Collectors.toList());
      }

      // Step.3 组装pageList
      List<AlertThreepartPage> pageList = new ArrayList<AlertThreepartPage>();
      for (AlertThreepart main : alertThreepartList) {
          AlertThreepartPage vo = new AlertThreepartPage();
          BeanUtils.copyProperties(main, vo);
          List<AlertThreepartRecord> alertThreepartRecordList = alertThreepartRecordService.selectByMainId(main.getId());
          vo.setAlertThreepartRecordList(alertThreepartRecordList);
          pageList.add(vo);
      }

      // Step.4 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      mv.addObject(NormalExcelConstants.FILE_NAME, "三方告警信息列表");
      mv.addObject(NormalExcelConstants.CLASS, AlertThreepartPage.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("三方告警信息数据", "导出人:"+sysUser.getRealname(), "三方告警信息"));
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
              List<AlertThreepartPage> list = ExcelImportUtil.importExcel(file.getInputStream(), AlertThreepartPage.class, params);
              for (AlertThreepartPage page : list) {
                  AlertThreepart po = new AlertThreepart();
                  BeanUtils.copyProperties(page, po);
                  alertThreepartService.saveMain(po, page.getAlertThreepartRecordList());
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

	 @ResponseBody
	 @PostMapping("/listAll")
	 public List<AlertThreepartModel> listAll(@RequestParam Map<String, Object> params){
		 //查询列表数据
		 List<AlertThreepartModel> alertThreepartList = alertThreepartService.listAll(params);
		 return alertThreepartList;
	 }
}
