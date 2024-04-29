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
import org.lkdt.modules.radar.entity.LdUnitSituationLane;
import org.lkdt.modules.radar.service.ILdUnitSituationLaneService;

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
 * @Description: 雷达单元态势（车道级别）
 * @Author: jeecg-boot
 * @Date:   2021-07-30
 * @Version: V1.0
 */
@Api(tags="雷达单元态势（车道级别）")
@RestController
@RequestMapping("/radar/ldUnitSituationLane")
@Slf4j
public class LdUnitSituationLaneController extends CloudController<LdUnitSituationLane, ILdUnitSituationLaneService> {
	@Autowired
	private ILdUnitSituationLaneService ldUnitSituationLaneService;
	
	/**
	 * 分页列表查询
	 *
	 * @param ldUnitSituationLane
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "雷达单元态势（车道级别）-分页列表查询")
	@ApiOperation(value="雷达单元态势（车道级别）-分页列表查询", notes="雷达单元态势（车道级别）-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(LdUnitSituationLane ldUnitSituationLane,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<LdUnitSituationLane> queryWrapper = QueryGenerator.initQueryWrapper(ldUnitSituationLane, req.getParameterMap());
		Page<LdUnitSituationLane> page = new Page<LdUnitSituationLane>(pageNo, pageSize);
		IPage<LdUnitSituationLane> pageList = ldUnitSituationLaneService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param ldUnitSituationLane
	 * @return
	 */
	@AutoLog(value = "雷达单元态势（车道级别）-添加")
	@ApiOperation(value="雷达单元态势（车道级别）-添加", notes="雷达单元态势（车道级别）-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody LdUnitSituationLane ldUnitSituationLane) {
		ldUnitSituationLaneService.save(ldUnitSituationLane);
		return Result.ok("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param ldUnitSituationLane
	 * @return
	 */
	@AutoLog(value = "雷达单元态势（车道级别）-编辑")
	@ApiOperation(value="雷达单元态势（车道级别）-编辑", notes="雷达单元态势（车道级别）-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody LdUnitSituationLane ldUnitSituationLane) {
		ldUnitSituationLaneService.updateById(ldUnitSituationLane);
		return Result.ok("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "雷达单元态势（车道级别）-通过id删除")
	@ApiOperation(value="雷达单元态势（车道级别）-通过id删除", notes="雷达单元态势（车道级别）-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		ldUnitSituationLaneService.removeById(id);
		return Result.ok("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "雷达单元态势（车道级别）-批量删除")
	@ApiOperation(value="雷达单元态势（车道级别）-批量删除", notes="雷达单元态势（车道级别）-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.ldUnitSituationLaneService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "雷达单元态势（车道级别）-通过id查询")
	@ApiOperation(value="雷达单元态势（车道级别）-通过id查询", notes="雷达单元态势（车道级别）-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		LdUnitSituationLane ldUnitSituationLane = ldUnitSituationLaneService.getById(id);
		if(ldUnitSituationLane==null) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(ldUnitSituationLane);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param ldUnitSituationLane
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, LdUnitSituationLane ldUnitSituationLane) {
        return super.exportXls(request, ldUnitSituationLane, LdUnitSituationLane.class, "雷达单元态势（车道级别）");
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
        return super.importExcel(request, response, LdUnitSituationLane.class);
    }

}
