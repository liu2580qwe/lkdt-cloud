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
import org.lkdt.modules.fog.entity.ArtificalModiLog;
import org.lkdt.modules.fog.service.IArtificalModiLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

 /**
 * @Description: 人工数据修改日志表
 * @Author: jeecg-boot
 * @Date:   2021-05-07
 * @Version: V1.0
 */
@Api(tags="人工数据修改日志表")
@RestController
@RequestMapping("/fog/artificalModiLog")
@Slf4j
public class ArtificalModiLogController extends CloudController<ArtificalModiLog, IArtificalModiLogService> {
	@Autowired
	private IArtificalModiLogService artificalModiLogService;
	
	/**
	 * 分页列表查询
	 *
	 * @param artificalModiLog
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "人工数据修改日志表-分页列表查询")
	@ApiOperation(value="人工数据修改日志表-分页列表查询", notes="人工数据修改日志表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(ArtificalModiLog artificalModiLog,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<ArtificalModiLog> queryWrapper = QueryGenerator.initQueryWrapper(artificalModiLog, req.getParameterMap());
		Page<ArtificalModiLog> page = new Page<ArtificalModiLog>(pageNo, pageSize);
		IPage<ArtificalModiLog> pageList = artificalModiLogService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param artificalModiLog
	 * @return
	 */
	@AutoLog(value = "人工数据修改日志表-添加")
	@ApiOperation(value="人工数据修改日志表-添加", notes="人工数据修改日志表-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody ArtificalModiLog artificalModiLog) {
		artificalModiLogService.save(artificalModiLog);
		return Result.ok("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param artificalModiLog
	 * @return
	 */
	@AutoLog(value = "人工数据修改日志表-编辑")
	@ApiOperation(value="人工数据修改日志表-编辑", notes="人工数据修改日志表-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody ArtificalModiLog artificalModiLog) {
		artificalModiLogService.updateById(artificalModiLog);
		return Result.ok("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "人工数据修改日志表-通过id删除")
	@ApiOperation(value="人工数据修改日志表-通过id删除", notes="人工数据修改日志表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		artificalModiLogService.removeById(id);
		return Result.ok("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "人工数据修改日志表-批量删除")
	@ApiOperation(value="人工数据修改日志表-批量删除", notes="人工数据修改日志表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.artificalModiLogService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "人工数据修改日志表-通过id查询")
	@ApiOperation(value="人工数据修改日志表-通过id查询", notes="人工数据修改日志表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		ArtificalModiLog artificalModiLog = artificalModiLogService.getById(id);
		if(artificalModiLog==null) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(artificalModiLog);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param artificalModiLog
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, ArtificalModiLog artificalModiLog) {
        return super.exportXls(request, artificalModiLog, ArtificalModiLog.class, "人工数据修改日志表");
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
        return super.importExcel(request, response, ArtificalModiLog.class);
    }

}
