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
import org.lkdt.modules.radar.entity.ZcLdEventRadarInfo;
import org.lkdt.modules.radar.service.IZcLdEventRadarInfoService;

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
 * @Description: 事件雷达数据表
 * @Author: jeecg-boot
 * @Date:   2021-03-23
 * @Version: V1.0
 */
@Api(tags="事件雷达数据表")
@RestController
@RequestMapping("/radar/zcLdEventRadarInfo")
@Slf4j
public class ZcLdEventRadarInfoController extends CloudController<ZcLdEventRadarInfo, IZcLdEventRadarInfoService> {
	@Autowired
	private IZcLdEventRadarInfoService zcLdEventRadarInfoService;
	
	/**
	 * 分页列表查询
	 *
	 * @param zcLdEventRadarInfo
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "事件雷达数据表-分页列表查询")
	@ApiOperation(value="事件雷达数据表-分页列表查询", notes="事件雷达数据表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(ZcLdEventRadarInfo zcLdEventRadarInfo,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<ZcLdEventRadarInfo> queryWrapper = QueryGenerator.initQueryWrapper(zcLdEventRadarInfo, req.getParameterMap());
		Page<ZcLdEventRadarInfo> page = new Page<ZcLdEventRadarInfo>(pageNo, pageSize);
		IPage<ZcLdEventRadarInfo> pageList = zcLdEventRadarInfoService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param zcLdEventRadarInfo
	 * @return
	 */
	@AutoLog(value = "事件雷达数据表-添加")
	@ApiOperation(value="事件雷达数据表-添加", notes="事件雷达数据表-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody ZcLdEventRadarInfo zcLdEventRadarInfo) {
		zcLdEventRadarInfoService.save(zcLdEventRadarInfo);
		return Result.ok("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param zcLdEventRadarInfo
	 * @return
	 */
	@AutoLog(value = "事件雷达数据表-编辑")
	@ApiOperation(value="事件雷达数据表-编辑", notes="事件雷达数据表-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody ZcLdEventRadarInfo zcLdEventRadarInfo) {
		zcLdEventRadarInfoService.updateById(zcLdEventRadarInfo);
		return Result.ok("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "事件雷达数据表-通过id删除")
	@ApiOperation(value="事件雷达数据表-通过id删除", notes="事件雷达数据表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		zcLdEventRadarInfoService.removeById(id);
		return Result.ok("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "事件雷达数据表-批量删除")
	@ApiOperation(value="事件雷达数据表-批量删除", notes="事件雷达数据表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.zcLdEventRadarInfoService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "事件雷达数据表-通过id查询")
	@ApiOperation(value="事件雷达数据表-通过id查询", notes="事件雷达数据表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		ZcLdEventRadarInfo zcLdEventRadarInfo = zcLdEventRadarInfoService.getById(id);
		if(zcLdEventRadarInfo==null) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(zcLdEventRadarInfo);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param zcLdEventRadarInfo
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, ZcLdEventRadarInfo zcLdEventRadarInfo) {
        return super.exportXls(request, zcLdEventRadarInfo, ZcLdEventRadarInfo.class, "事件雷达数据表");
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
        return super.importExcel(request, response, ZcLdEventRadarInfo.class);
    }

}
