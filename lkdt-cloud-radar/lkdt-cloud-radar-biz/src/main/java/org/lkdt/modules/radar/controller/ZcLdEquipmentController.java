package org.lkdt.modules.radar.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.lkdt.common.system.vo.DictModel;
import org.lkdt.common.system.vo.LoginUser;
import org.lkdt.common.util.StringUtils;
import org.lkdt.common.util.oConvertUtils;
import org.lkdt.modules.api.SysBaseRemoteApi;
import org.lkdt.modules.radar.entity.ZcLdEquipment;
import org.lkdt.modules.radar.entity.ZcLdLaneInfo;
import org.lkdt.modules.radar.service.IZcLdEquipmentService;
import org.lkdt.modules.radar.service.IZcLdLaneInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

 /**
 * @Description: 雷达设备表
 * @Author: jeecg-boot
 * @Date:   2021-03-11
 * @Version: V1.0
 */
@Api(tags="雷达设备表")
@RestController
@RequestMapping("/radar/zcLdEquipment")
@Slf4j
public class ZcLdEquipmentController extends CloudController<ZcLdEquipment, IZcLdEquipmentService> {

	@Autowired
	private IZcLdEquipmentService zcLdEquipmentService;

	@Autowired
	private IZcLdLaneInfoService zcLdLaneInfoService;
	
	@Autowired
    private SysBaseRemoteApi sysBaseRemoteApi;


	/*---------------------------------主表处理-begin-------------------------------------*/

	/**
	 * 分页列表查询
	 * @param zcLdEquipment
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "雷达设备表-分页列表查询")
	@ApiOperation(value="雷达设备表-分页列表查询", notes="雷达设备表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(ZcLdEquipment zcLdEquipment,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<ZcLdEquipment> queryWrapper = QueryGenerator.initQueryWrapper(zcLdEquipment, req.getParameterMap());
		Page<ZcLdEquipment> page = new Page<ZcLdEquipment>(pageNo, pageSize);
		IPage<ZcLdEquipment> pageList = zcLdEquipmentService.page(page, queryWrapper);
		return Result.ok(pageList);
	}

	/**
     *   添加
     * @param zcLdEquipment
     * @return
     */
    @AutoLog(value = "雷达设备表-添加")
    @ApiOperation(value="雷达设备表-添加", notes="雷达设备表-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody ZcLdEquipment zcLdEquipment) {
        zcLdEquipmentService.save(zcLdEquipment);
        return Result.ok("添加成功！");
    }

    /**
     *  编辑
     * @param zcLdEquipment
     * @return
     */
    @AutoLog(value = "雷达设备表-编辑")
    @ApiOperation(value="雷达设备表-编辑", notes="雷达设备表-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody ZcLdEquipment zcLdEquipment) {
        zcLdEquipmentService.updateById(zcLdEquipment);
        return Result.ok("编辑成功!");
    }

    /**
     * 通过id删除
     * @param id
     * @return
     */
    @AutoLog(value = "雷达设备表-通过id删除")
    @ApiOperation(value="雷达设备表-通过id删除", notes="雷达设备表-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name="id",required=true) String id) {
        zcLdEquipmentService.delMain(id);
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     * @param ids
     * @return
     */
    @AutoLog(value = "雷达设备表-批量删除")
    @ApiOperation(value="雷达设备表-批量删除", notes="雷达设备表-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
        this.zcLdEquipmentService.delBatchMain(Arrays.asList(ids.split(",")));
        return Result.ok("批量删除成功!");
    }

    /**
     * 导出
     * @return
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, ZcLdEquipment zcLdEquipment) {
        return super.exportXls(request, zcLdEquipment, ZcLdEquipment.class, "雷达设备表");
    }

    /**
     * 导入
     * @return
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, ZcLdEquipment.class);
    }
	/*---------------------------------主表处理-end-------------------------------------*/
	

    /*--------------------------------子表处理-雷达车道与道路车道关系表-begin----------------------------------------------*/
	/**
	 * 通过主表ID查询
	 * @return
	 */
	@AutoLog(value = "雷达车道与道路车道关系表-通过主表ID查询")
	@ApiOperation(value="雷达车道与道路车道关系表-通过主表ID查询", notes="雷达车道与道路车道关系表-通过主表ID查询")
	@GetMapping(value = "/listZcLdLaneInfoByMainId")
    public Result<?> listZcLdLaneInfoByMainId(ZcLdLaneInfo zcLdLaneInfo,
                                                    @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                    @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                    HttpServletRequest req) {
        QueryWrapper<ZcLdLaneInfo> queryWrapper = QueryGenerator.initQueryWrapper(zcLdLaneInfo, req.getParameterMap());
        Page<ZcLdLaneInfo> page = new Page<ZcLdLaneInfo>(pageNo, pageSize);
        IPage<ZcLdLaneInfo> pageList = zcLdLaneInfoService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

	/**
	 * 添加
	 * @param zcLdLaneInfo
	 * @return
	 */
	@AutoLog(value = "雷达车道与道路车道关系表-添加")
	@ApiOperation(value="雷达车道与道路车道关系表-添加", notes="雷达车道与道路车道关系表-添加")
	@PostMapping(value = "/addZcLdLaneInfo")
	public Result<?> addZcLdLaneInfo(@RequestBody ZcLdLaneInfo zcLdLaneInfo) {
		zcLdLaneInfoService.save(zcLdLaneInfo);
		return Result.ok("添加成功！");
	}

    /**
	 * 编辑
	 * @param zcLdLaneInfo
	 * @return
	 */
	@AutoLog(value = "雷达车道与道路车道关系表-编辑")
	@ApiOperation(value="雷达车道与道路车道关系表-编辑", notes="雷达车道与道路车道关系表-编辑")
	@PutMapping(value = "/editZcLdLaneInfo")
	public Result<?> editZcLdLaneInfo(@RequestBody ZcLdLaneInfo zcLdLaneInfo) {
		zcLdLaneInfoService.updateById(zcLdLaneInfo);
		return Result.ok("编辑成功!");
	}

	/**
	 * 通过id删除
	 * @param id
	 * @return
	 */
	@AutoLog(value = "雷达车道与道路车道关系表-通过id删除")
	@ApiOperation(value="雷达车道与道路车道关系表-通过id删除", notes="雷达车道与道路车道关系表-通过id删除")
	@DeleteMapping(value = "/deleteZcLdLaneInfo")
	public Result<?> deleteZcLdLaneInfo(@RequestParam(name="id",required=true) String id) {
		zcLdLaneInfoService.removeById(id);
		return Result.ok("删除成功!");
	}

	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "雷达车道与道路车道关系表-批量删除")
	@ApiOperation(value="雷达车道与道路车道关系表-批量删除", notes="雷达车道与道路车道关系表-批量删除")
	@DeleteMapping(value = "/deleteBatchZcLdLaneInfo")
	public Result<?> deleteBatchZcLdLaneInfo(@RequestParam(name="ids",required=true) String ids) {
	    this.zcLdLaneInfoService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}

    /**
     * 导出
     * @return
     */
    @RequestMapping(value = "/exportZcLdLaneInfo")
    public ModelAndView exportZcLdLaneInfo(HttpServletRequest request, ZcLdLaneInfo zcLdLaneInfo) {
		 // Step.1 组装查询条件
		 QueryWrapper<ZcLdLaneInfo> queryWrapper = QueryGenerator.initQueryWrapper(zcLdLaneInfo, request.getParameterMap());
		 LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

		 // Step.2 获取导出数据
		 List<ZcLdLaneInfo> pageList = zcLdLaneInfoService.list(queryWrapper);
		 List<ZcLdLaneInfo> exportList = null;

		 // 过滤选中数据
		 String selections = request.getParameter("selections");
		 if (oConvertUtils.isNotEmpty(selections)) {
			 List<String> selectionList = Arrays.asList(selections.split(","));
			 exportList = pageList.stream().filter(item -> selectionList.contains(item.getId())).collect(Collectors.toList());
		 } else {
			 exportList = pageList;
		 }

		 // Step.3 AutoPoi 导出Excel
		 ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
		 mv.addObject(NormalExcelConstants.FILE_NAME, "雷达车道与道路车道关系表"); //此处设置的filename无效 ,前端会重更新设置一下
		 mv.addObject(NormalExcelConstants.CLASS, ZcLdLaneInfo.class);
		 mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("雷达车道与道路车道关系表报表", "导出人:" + sysUser.getRealname(), "雷达车道与道路车道关系表"));
		 mv.addObject(NormalExcelConstants.DATA_LIST, exportList);
		 return mv;
    }

    /**
     * 导入
     * @return
     */
    @RequestMapping(value = "/importZcLdLaneInfo/{mainId}")
    public Result<?> importZcLdLaneInfo(HttpServletRequest request, HttpServletResponse response, @PathVariable("mainId") String mainId) {
		 MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		 Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
		 for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
			 MultipartFile file = entity.getValue();// 获取上传文件对象
			 ImportParams params = new ImportParams();
			 params.setTitleRows(2);
			 params.setHeadRows(1);
			 params.setNeedSave(true);
			 try {
				 List<ZcLdLaneInfo> list = ExcelImportUtil.importExcel(file.getInputStream(), ZcLdLaneInfo.class, params);
				 for (ZcLdLaneInfo temp : list) {
                    temp.setEquId(mainId);
				 }
				 long start = System.currentTimeMillis();
				 zcLdLaneInfoService.saveBatch(list);
				 log.info("消耗时间" + (System.currentTimeMillis() - start) + "毫秒");
				 return Result.ok("文件导入成功！数据行数：" + list.size());
			 } catch (Exception e) {
				 log.error(e.getMessage(), e);
				 return Result.error("文件导入失败:" + e.getMessage());
			 } finally {
				 try {
					 file.getInputStream().close();
				 } catch (IOException e) {
					 e.printStackTrace();
				 }
			 }
		 }
		 return Result.error("文件导入失败！");
    }

    /*--------------------------------子表处理-雷达车道与道路车道关系表-end----------------------------------------------*/

    
    
    /**
	 * 通过主表ID查询
	 * @return
	 */
	@AutoLog(value = "查询车道--不分页")
	@ApiOperation(value="查询车道--不分页", notes="查询车道--不分页")
	@GetMapping(value = "/querylaneListNoPage")
    public Result<?> querylaneListNoPage(String equId) {
        List<ZcLdLaneInfo> list = zcLdLaneInfoService.queryByMainId(equId);
        List<DictModel> dictList = sysBaseRemoteApi.queryDictItemsByCode("lane_type");
        for (ZcLdLaneInfo lane : list) {
        	for (DictModel dict : dictList) {
    			if(StringUtils.equals(lane.getLaneRoad(), dict.getValue())) {
    				lane.setLaneRoadName(dict.getText());
    			}
    		}
		}
        
        return Result.ok(list);
    }
	
   



}
