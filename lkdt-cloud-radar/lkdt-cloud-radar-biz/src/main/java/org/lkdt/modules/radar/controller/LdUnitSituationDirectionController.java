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
import org.lkdt.modules.radar.entity.LdUnitSituationDirection;
import org.lkdt.modules.radar.service.ILdUnitSituationDirectionService;

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
 * @Description: 雷达单元态势（来/去向级别）
 * @Author: jeecg-boot
 * @Date:   2021-07-30
 * @Version: V1.0
 */
@Api(tags="雷达单元态势（来/去向级别）")
@RestController
@RequestMapping("/radar/ldUnitSituationDirection")
@Slf4j
public class LdUnitSituationDirectionController extends CloudController<LdUnitSituationDirection, ILdUnitSituationDirectionService> {
	@Autowired
	private ILdUnitSituationDirectionService ldUnitSituationDirectionService;
	
	/**
	 * 分页列表查询
	 *
	 * @param ldUnitSituationDirection
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "雷达单元态势（来/去向级别）-分页列表查询")
	@ApiOperation(value="雷达单元态势（来/去向级别）-分页列表查询", notes="雷达单元态势（来/去向级别）-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(LdUnitSituationDirection ldUnitSituationDirection,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<LdUnitSituationDirection> queryWrapper = QueryGenerator.initQueryWrapper(ldUnitSituationDirection, req.getParameterMap());
		Page<LdUnitSituationDirection> page = new Page<LdUnitSituationDirection>(pageNo, pageSize);
		IPage<LdUnitSituationDirection> pageList = ldUnitSituationDirectionService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param ldUnitSituationDirection
	 * @return
	 */
	@AutoLog(value = "雷达单元态势（来/去向级别）-添加")
	@ApiOperation(value="雷达单元态势（来/去向级别）-添加", notes="雷达单元态势（来/去向级别）-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody LdUnitSituationDirection ldUnitSituationDirection) {
		ldUnitSituationDirectionService.save(ldUnitSituationDirection);
		return Result.ok("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param ldUnitSituationDirection
	 * @return
	 */
	@AutoLog(value = "雷达单元态势（来/去向级别）-编辑")
	@ApiOperation(value="雷达单元态势（来/去向级别）-编辑", notes="雷达单元态势（来/去向级别）-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody LdUnitSituationDirection ldUnitSituationDirection) {
		ldUnitSituationDirectionService.updateById(ldUnitSituationDirection);
		return Result.ok("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "雷达单元态势（来/去向级别）-通过id删除")
	@ApiOperation(value="雷达单元态势（来/去向级别）-通过id删除", notes="雷达单元态势（来/去向级别）-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		ldUnitSituationDirectionService.removeById(id);
		return Result.ok("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "雷达单元态势（来/去向级别）-批量删除")
	@ApiOperation(value="雷达单元态势（来/去向级别）-批量删除", notes="雷达单元态势（来/去向级别）-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.ldUnitSituationDirectionService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "雷达单元态势（来/去向级别）-通过id查询")
	@ApiOperation(value="雷达单元态势（来/去向级别）-通过id查询", notes="雷达单元态势（来/去向级别）-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		LdUnitSituationDirection ldUnitSituationDirection = ldUnitSituationDirectionService.getById(id);
		if(ldUnitSituationDirection==null) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(ldUnitSituationDirection);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param ldUnitSituationDirection
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, LdUnitSituationDirection ldUnitSituationDirection) {
        return super.exportXls(request, ldUnitSituationDirection, LdUnitSituationDirection.class, "雷达单元态势（来/去向级别）");
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
        return super.importExcel(request, response, LdUnitSituationDirection.class);
    }

}
