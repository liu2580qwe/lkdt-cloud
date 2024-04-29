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
import org.lkdt.modules.radar.entity.ZcLdEventInfo;
import org.lkdt.modules.radar.service.IZcLdEventInfoService;

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
 * @Description: 事件信息表
 * @Author: jeecg-boot
 * @Date:   2021-03-23
 * @Version: V1.0
 */
@Api(tags="事件信息表")
@RestController
@RequestMapping("/radar/zcLdEventInfo")
@Slf4j
public class ZcLdEventInfoController extends CloudController<ZcLdEventInfo, IZcLdEventInfoService> {
	@Autowired
	private IZcLdEventInfoService zcLdEventInfoService;
	
	/**
	 * 分页列表查询
	 *
	 * @param zcLdEventInfo
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "事件信息表-分页列表查询")
	@ApiOperation(value="事件信息表-分页列表查询", notes="事件信息表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(ZcLdEventInfo zcLdEventInfo,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<ZcLdEventInfo> queryWrapper = QueryGenerator.initQueryWrapper(zcLdEventInfo, req.getParameterMap());
		Page<ZcLdEventInfo> page = new Page<ZcLdEventInfo>(pageNo, pageSize);
		IPage<ZcLdEventInfo> pageList = zcLdEventInfoService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param zcLdEventInfo
	 * @return
	 */
	@AutoLog(value = "事件信息表-添加")
	@ApiOperation(value="事件信息表-添加", notes="事件信息表-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody ZcLdEventInfo zcLdEventInfo) {
		zcLdEventInfoService.save(zcLdEventInfo);
		return Result.ok("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param zcLdEventInfo
	 * @return
	 */
	@AutoLog(value = "事件信息表-编辑")
	@ApiOperation(value="事件信息表-编辑", notes="事件信息表-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody ZcLdEventInfo zcLdEventInfo) {
		zcLdEventInfoService.updateById(zcLdEventInfo);
		return Result.ok("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "事件信息表-通过id删除")
	@ApiOperation(value="事件信息表-通过id删除", notes="事件信息表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		zcLdEventInfoService.removeById(id);
		return Result.ok("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "事件信息表-批量删除")
	@ApiOperation(value="事件信息表-批量删除", notes="事件信息表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.zcLdEventInfoService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "事件信息表-通过id查询")
	@ApiOperation(value="事件信息表-通过id查询", notes="事件信息表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		ZcLdEventInfo zcLdEventInfo = zcLdEventInfoService.getById(id);
		if(zcLdEventInfo==null) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(zcLdEventInfo);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param zcLdEventInfo
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, ZcLdEventInfo zcLdEventInfo) {
        return super.exportXls(request, zcLdEventInfo, ZcLdEventInfo.class, "事件信息表");
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
        return super.importExcel(request, response, ZcLdEventInfo.class);
    }

}
