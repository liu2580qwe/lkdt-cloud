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
import org.lkdt.common.system.vo.LoginUser;
import org.lkdt.common.util.oConvertUtils;
import org.lkdt.modules.radar.entity.ZcLdEquipment;
import org.lkdt.modules.radar.entity.ZcLdEquipmentVideo;
import org.lkdt.modules.radar.entity.ZcLdRadarVideo;
import org.lkdt.modules.radar.service.IZcLdEquipmentVideoService;
import org.lkdt.modules.radar.service.IZcLdRadarEquipmentService;
import org.lkdt.modules.radar.service.IZcLdRadarVideoService;
import org.lkdt.modules.radar.service.impl.ZcLdEquipmentVideoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
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
 * @Date:   2021-07-26
 * @Version: V1.0
 */
@Api(tags="雷达设备表")
@RestController
@RequestMapping("/radar/zcLdRadarEquipment")
@Slf4j
public class ZcLdRadarEquipmentController extends CloudController<ZcLdEquipment, IZcLdRadarEquipmentService> {

	@Autowired
	private IZcLdRadarEquipmentService zcLdRadarEquipmentService;

	@Autowired
	private IZcLdRadarVideoService zcLdRadarVideoService;

	@Autowired
	private IZcLdEquipmentVideoService zcLdEquipmentVideoService;

	@Autowired
	private ZcLdEquipmentVideoServiceImpl zcLdEquipmentVideoServiceImpl;

	/**
	 * 分页列表查询
	 * @param zcLdRadarEquipment
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "雷达设备表-分页列表查询")
	@ApiOperation(value="雷达设备表-分页列表查询", notes="雷达设备表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(ZcLdEquipment zcLdRadarEquipment,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<ZcLdEquipment> queryWrapper = QueryGenerator.initQueryWrapper(zcLdRadarEquipment, req.getParameterMap());
		Page<ZcLdEquipment> page = new Page<ZcLdEquipment>(pageNo, pageSize);
		IPage<ZcLdEquipment> pageList = zcLdRadarEquipmentService.page(page, queryWrapper);
		return Result.ok(pageList);
	}

	/**
     *   添加
     * @param zcLdRadarEquipment
     * @return
     */
    @AutoLog(value = "雷达设备表-添加")
    @ApiOperation(value="雷达设备表-添加", notes="雷达设备表-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody ZcLdEquipment zcLdRadarEquipment) {
        zcLdRadarEquipmentService.save(zcLdRadarEquipment);
        return Result.ok("添加成功！");
    }

    /**
     *  编辑
     * @param zcLdRadarEquipment
     * @return
     */
    @AutoLog(value = "雷达设备表-编辑")
    @ApiOperation(value="雷达设备表-编辑", notes="雷达设备表-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody ZcLdEquipment zcLdRadarEquipment) {
        zcLdRadarEquipmentService.updateById(zcLdRadarEquipment);
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
		List<String> ids = zcLdEquipmentVideoServiceImpl.selectSingleId(id);
		if(ids.size()!=0){
			return Result.error("请删除摄像头数据后再删除设备!");
		}
		zcLdRadarEquipmentService.removeById(id);
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
        return Result.ok("批量删除成功!");
    }

    /**
     * 导出
     * @return
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, ZcLdEquipment zcLdRadarEquipment) {
        return super.exportXls(request, zcLdRadarEquipment, ZcLdEquipment.class, "雷达设备表");
    }

    /**
     * 导入
     * @return
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, ZcLdEquipment.class);
    }

	/**
	 * 通过主表ID查询
	 * @return
	 */
	@AutoLog(value = "雷达设备摄像头表-通过主表ID查询")
	@ApiOperation(value="雷达设备摄像头表-通过主表ID查询", notes="雷达设备摄像头表-通过主表ID查询")
	@GetMapping(value = "/listZcLdRadarVideoByMainId")
    public Result<?> listZcLdRadarVideoByMainId(ZcLdRadarVideo zcLdRadarVideo,
                                                    @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                    @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                    HttpServletRequest req) {
		List<String> ids = zcLdEquipmentVideoServiceImpl.selectSingleId(req.getParameter("epId"));
		zcLdRadarVideo.setId(String.join(",",ids));
        QueryWrapper<ZcLdRadarVideo> queryWrapper = QueryGenerator.initQueryWrapper(zcLdRadarVideo, req.getParameterMap());
        if(ids.size()<2){
			queryWrapper.eq("id",zcLdRadarVideo.getId());
		}
        Page<ZcLdRadarVideo> page = new Page<>(pageNo, pageSize);
        IPage<ZcLdRadarVideo> pageList = zcLdRadarVideoService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

	/**
	 * 添加
	 * @param zcLdRadarVideo
	 * @return
	 */
	@AutoLog(value = "雷达设备摄像头表-添加")
	@ApiOperation(value="雷达设备摄像头表-添加", notes="雷达设备摄像头表-添加")
	@PostMapping(value = "/addZcLdRadarVideo")
	@Transactional
	public Result<?> addZcLdRadarVideo(@RequestBody ZcLdRadarVideo zcLdRadarVideo,@RequestParam("epId")String epId,ZcLdEquipmentVideo zcLdEquipmentVideo) {
		zcLdRadarVideoService.save(zcLdRadarVideo);
		zcLdEquipmentVideo.setEpId(epId);
		zcLdEquipmentVideo.setVedioId(zcLdRadarVideo.getId());
		zcLdEquipmentVideoService.save(zcLdEquipmentVideo);
		return Result.ok("添加成功！");
	}

    /**
	 * 编辑
	 * @param zcLdRadarVideo
	 * @return
	 */
	@AutoLog(value = "雷达设备摄像头表-编辑")
	@ApiOperation(value="雷达设备摄像头表-编辑", notes="雷达设备摄像头表-编辑")
	@PutMapping(value = "/editZcLdRadarVideo")
	public Result<?> editZcLdRadarVideo(@RequestBody ZcLdRadarVideo zcLdRadarVideo) {
		zcLdRadarVideoService.updateById(zcLdRadarVideo);
		return Result.ok("编辑成功!");
	}

	/**
	 * 通过id删除
	 * @param id vedio_id
	 * @return
	 */
	@AutoLog(value = "雷达设备摄像头表-通过id删除")
	@ApiOperation(value="雷达设备摄像头表-通过id删除", notes="雷达设备摄像头表-通过id删除")
	@DeleteMapping(value = "/deleteZcLdRadarVideo")
	@Transactional
	public Result<?> deleteZcLdRadarVideo(@RequestParam(name="id",required=true) String id) {
		zcLdRadarVideoService.removeById(id);
		zcLdEquipmentVideoService.deleteByVedioId(id);
		return Result.ok("删除成功!");
	}

	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "雷达设备摄像头表-批量删除")
	@ApiOperation(value="雷达设备摄像头表-批量删除", notes="雷达设备摄像头表-批量删除")
	@DeleteMapping(value = "/deleteBatchZcLdRadarVideo")
	public Result<?> deleteBatchZcLdRadarVideo(@RequestParam(name="ids",required=true) String ids) {
	    this.zcLdRadarVideoService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}

    /**
     * 导出
     * @return
     */
    @RequestMapping(value = "/exportZcLdRadarVideo")
    public ModelAndView exportZcLdRadarVideo(HttpServletRequest request, ZcLdRadarVideo zcLdRadarVideo) {
		 // Step.1 组装查询条件
		 QueryWrapper<ZcLdRadarVideo> queryWrapper = QueryGenerator.initQueryWrapper(zcLdRadarVideo, request.getParameterMap());
		 LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

		 // Step.2 获取导出数据
		 List<ZcLdRadarVideo> pageList = zcLdRadarVideoService.list(queryWrapper);
		 List<ZcLdRadarVideo> exportList = null;

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
		 mv.addObject(NormalExcelConstants.FILE_NAME, "雷达设备摄像头表"); //此处设置的filename无效 ,前端会重更新设置一下
		 mv.addObject(NormalExcelConstants.CLASS, ZcLdRadarVideo.class);
		 mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("雷达设备摄像头表报表", "导出人:" + sysUser.getRealname(), "雷达设备摄像头表"));
		 mv.addObject(NormalExcelConstants.DATA_LIST, exportList);
		 return mv;
    }

    /**
     * 导入
     * @return
     */
    @RequestMapping(value = "/importZcLdRadarVideo/{mainId}")
    public Result<?> importZcLdRadarVideo(HttpServletRequest request, HttpServletResponse response, @PathVariable("mainId") String mainId) {
		 MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		 Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
		 for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
			 MultipartFile file = entity.getValue();// 获取上传文件对象
			 ImportParams params = new ImportParams();
			 params.setTitleRows(2);
			 params.setHeadRows(1);
			 params.setNeedSave(true);
			 try {
				 List<ZcLdRadarVideo> list = ExcelImportUtil.importExcel(file.getInputStream(), ZcLdRadarVideo.class, params);
				 for (ZcLdRadarVideo temp : list) {
                    /*todo:temp.setEpId(mainId);*/
				 }
				 long start = System.currentTimeMillis();
				 zcLdRadarVideoService.saveBatch(list);
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

}
