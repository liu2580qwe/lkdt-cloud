package org.lkdt.modules.weixin.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.lkdt.common.api.vo.Result;
import org.lkdt.common.aspect.annotation.AutoLog;
import org.lkdt.common.system.base.controller.CloudController;
import org.lkdt.common.system.query.QueryGenerator;
import org.lkdt.modules.weixin.entity.WxManor;
import org.lkdt.modules.weixin.service.IWxManorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

 /**
 * @Description: 微信用户路段管理
 * @Author: jeecg-boot
 * @Date:   2021-04-26
 * @Version: V1.0
 */
@Api(tags="微信用户路段管理")
@RestController
@RequestMapping("/weixin/zcWxManor")
@Slf4j
public class WxManorController extends CloudController<WxManor, IWxManorService> {
	@Autowired
	private IWxManorService zcWxManorService;
	
	/**
	 * 分页列表查询
	 *
	 * @param zcWxManor
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "微信用户路段管理-分页列表查询")
	@ApiOperation(value="微信用户路段管理-分页列表查询", notes="微信用户路段管理-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(WxManor zcWxManor,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<WxManor> queryWrapper = QueryGenerator.initQueryWrapper(zcWxManor, req.getParameterMap());
		Page<WxManor> page = new Page<WxManor>(pageNo, pageSize);
		IPage<WxManor> pageList = zcWxManorService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param zcWxManor
	 * @return
	 */
	@AutoLog(value = "微信用户路段管理-添加")
	@ApiOperation(value="微信用户路段管理-添加", notes="微信用户路段管理-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody WxManor zcWxManor) {
		zcWxManorService.save(zcWxManor);
		return Result.ok("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param zcWxManor
	 * @return
	 */
	@AutoLog(value = "微信用户路段管理-编辑")
	@ApiOperation(value="微信用户路段管理-编辑", notes="微信用户路段管理-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody WxManor zcWxManor) {
		zcWxManorService.updateById(zcWxManor);
		return Result.ok("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "微信用户路段管理-通过id删除")
	@ApiOperation(value="微信用户路段管理-通过id删除", notes="微信用户路段管理-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		zcWxManorService.removeById(id);
		return Result.ok("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "微信用户路段管理-批量删除")
	@ApiOperation(value="微信用户路段管理-批量删除", notes="微信用户路段管理-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.zcWxManorService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "微信用户路段管理-通过id查询")
	@ApiOperation(value="微信用户路段管理-通过id查询", notes="微信用户路段管理-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		WxManor zcWxManor = zcWxManorService.getById(id);
		if(zcWxManor==null) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(zcWxManor);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param zcWxManor
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, WxManor zcWxManor) {
        return super.exportXls(request, zcWxManor, WxManor.class, "微信用户路段管理");
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
        return super.importExcel(request, response, WxManor.class);
    }

}
