package org.lkdt.modules.radar.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.lkdt.common.api.vo.Result;
import org.lkdt.common.system.query.QueryGenerator;
import org.lkdt.common.util.oConvertUtils;
import org.lkdt.modules.radar.entity.ZcLdCameraCapture;
import org.lkdt.modules.radar.service.IZcLdCameraCaptureService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.lkdt.common.system.base.controller.CloudController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.lkdt.common.aspect.annotation.AutoLog;

 /**
 * @Description: 视频截图数据表
 * @Author: jeecg-boot
 * @Date:   2021-03-23
 * @Version: V1.0
 */
@Api(tags="视频截图数据表")
@RestController
@RequestMapping("/radar/zcLdCameraCapture")
@Slf4j
public class ZcLdCameraCaptureController extends CloudController<ZcLdCameraCapture, IZcLdCameraCaptureService> {
	@Autowired
	private IZcLdCameraCaptureService zcLdCameraCaptureService;
	
	/**
	 * 分页列表查询
	 *
	 * @param zcLdCameraCapture
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "视频截图数据表-分页列表查询")
	@ApiOperation(value="视频截图数据表-分页列表查询", notes="视频截图数据表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(ZcLdCameraCapture zcLdCameraCapture,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<ZcLdCameraCapture> queryWrapper = QueryGenerator.initQueryWrapper(zcLdCameraCapture, req.getParameterMap());
		Page<ZcLdCameraCapture> page = new Page<ZcLdCameraCapture>(pageNo, pageSize);
		IPage<ZcLdCameraCapture> pageList = zcLdCameraCaptureService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param zcLdCameraCapture
	 * @return
	 */
	@AutoLog(value = "视频截图数据表-添加")
	@ApiOperation(value="视频截图数据表-添加", notes="视频截图数据表-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody ZcLdCameraCapture zcLdCameraCapture) {
		zcLdCameraCaptureService.save(zcLdCameraCapture);
		return Result.ok("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param zcLdCameraCapture
	 * @return
	 */
	@AutoLog(value = "视频截图数据表-编辑")
	@ApiOperation(value="视频截图数据表-编辑", notes="视频截图数据表-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody ZcLdCameraCapture zcLdCameraCapture) {
		zcLdCameraCaptureService.updateById(zcLdCameraCapture);
		return Result.ok("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "视频截图数据表-通过id删除")
	@ApiOperation(value="视频截图数据表-通过id删除", notes="视频截图数据表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		zcLdCameraCaptureService.removeById(id);
		return Result.ok("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "视频截图数据表-批量删除")
	@ApiOperation(value="视频截图数据表-批量删除", notes="视频截图数据表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.zcLdCameraCaptureService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "视频截图数据表-通过id查询")
	@ApiOperation(value="视频截图数据表-通过id查询", notes="视频截图数据表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		ZcLdCameraCapture zcLdCameraCapture = zcLdCameraCaptureService.getById(id);
		if(zcLdCameraCapture==null) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(zcLdCameraCapture);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param zcLdCameraCapture
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, ZcLdCameraCapture zcLdCameraCapture) {
        return super.exportXls(request, zcLdCameraCapture, ZcLdCameraCapture.class, "视频截图数据表");
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
        return super.importExcel(request, response, ZcLdCameraCapture.class);
    }

}
