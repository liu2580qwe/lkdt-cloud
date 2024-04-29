package org.lkdt.modules.radar.controller;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.lkdt.common.api.vo.Result;
import org.lkdt.common.aspect.annotation.AutoLog;
import org.lkdt.common.system.base.controller.CloudController;
import org.lkdt.common.system.query.QueryGenerator;
import org.lkdt.modules.radar.entity.ZcLdThreeStatusCoefficient;
import org.lkdt.modules.radar.service.IZcLdThreeStatusCoefficientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

 /**
 * @Description: 交通流三态系数
 * @Author: jeecg-boot
 * @Date:   2021-09-23
 * @Version: V1.0
 */
@Api(tags="交通流三态系数")
@RestController
@RequestMapping("/radar/zcLdThreeStatusCoefficient")
@Slf4j
public class ZcLdThreeStatusCoefficientController extends CloudController<ZcLdThreeStatusCoefficient, IZcLdThreeStatusCoefficientService> {
	@Autowired
	private IZcLdThreeStatusCoefficientService zcLdThreeStatusCoefficientService;
	
	/**
	 * 分页列表查询
	 *
	 * @param zcLdThreeStatusCoefficient
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "交通流三态系数-分页列表查询")
	@ApiOperation(value="交通流三态系数-分页列表查询", notes="交通流三态系数-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(ZcLdThreeStatusCoefficient zcLdThreeStatusCoefficient,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<ZcLdThreeStatusCoefficient> queryWrapper = QueryGenerator.initQueryWrapper(zcLdThreeStatusCoefficient, req.getParameterMap());
		Page<ZcLdThreeStatusCoefficient> page = new Page<ZcLdThreeStatusCoefficient>(pageNo, pageSize);
		IPage<ZcLdThreeStatusCoefficient> pageList = zcLdThreeStatusCoefficientService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param zcLdThreeStatusCoefficient
	 * @return
	 */
	@AutoLog(value = "交通流三态系数-添加")
	@ApiOperation(value="交通流三态系数-添加", notes="交通流三态系数-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody ZcLdThreeStatusCoefficient zcLdThreeStatusCoefficient) {
		zcLdThreeStatusCoefficientService.save(zcLdThreeStatusCoefficient);
		return Result.ok("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param zcLdThreeStatusCoefficient
	 * @return
	 */
	@AutoLog(value = "交通流三态系数-编辑")
	@ApiOperation(value="交通流三态系数-编辑", notes="交通流三态系数-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody ZcLdThreeStatusCoefficient zcLdThreeStatusCoefficient) {
		zcLdThreeStatusCoefficientService.updateById(zcLdThreeStatusCoefficient);
		return Result.ok("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "交通流三态系数-通过id删除")
	@ApiOperation(value="交通流三态系数-通过id删除", notes="交通流三态系数-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		zcLdThreeStatusCoefficientService.removeById(id);
		return Result.ok("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "交通流三态系数-批量删除")
	@ApiOperation(value="交通流三态系数-批量删除", notes="交通流三态系数-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.zcLdThreeStatusCoefficientService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "交通流三态系数-通过id查询")
	@ApiOperation(value="交通流三态系数-通过id查询", notes="交通流三态系数-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		ZcLdThreeStatusCoefficient zcLdThreeStatusCoefficient = zcLdThreeStatusCoefficientService.getById(id);
		if(zcLdThreeStatusCoefficient==null) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(zcLdThreeStatusCoefficient);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param zcLdThreeStatusCoefficient
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, ZcLdThreeStatusCoefficient zcLdThreeStatusCoefficient) {
        return super.exportXls(request, zcLdThreeStatusCoefficient, ZcLdThreeStatusCoefficient.class, "交通流三态系数");
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
        return super.importExcel(request, response, ZcLdThreeStatusCoefficient.class);
    }

}
