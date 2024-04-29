package org.lkdt.modules.traffic.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.lkdt.common.api.vo.Result;
import org.lkdt.common.aspect.annotation.AutoLog;
import org.lkdt.common.system.base.controller.CloudController;
import org.lkdt.common.system.query.QueryGenerator;
import org.lkdt.common.util.DateUtils;
import org.lkdt.modules.traffic.entity.TollStationRecorder;
import org.lkdt.modules.traffic.service.ITollStationRecorderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @Description: 收费站封路事件详细
 * @Author: jeecg-boot
 * @Date:   2021-06-03
 * @Version: V1.0
 */
@Api(tags="收费站封路事件详细")
@RestController
@RequestMapping("/traffic/tollStationRecorder")
@Slf4j
public class TollStationRecorderController extends CloudController<TollStationRecorder, ITollStationRecorderService> {
	@Autowired
	private ITollStationRecorderService tollStationRecorderService;
	
	/**
	 * 分页列表查询
	 *
	 * @param tollStationRecorder
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "收费站封路事件详细-分页列表查询")
	@ApiOperation(value="收费站封路事件详细-分页列表查询", notes="收费站封路事件详细-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(TollStationRecorder tollStationRecorder,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<TollStationRecorder> queryWrapper = QueryGenerator.initQueryWrapper(tollStationRecorder, req.getParameterMap());
		Page<TollStationRecorder> page = new Page<TollStationRecorder>(pageNo, pageSize);
		IPage<TollStationRecorder> pageList = tollStationRecorderService.page(page, queryWrapper);
		return Result.ok(pageList);
	}

	 /**
	  * 查询所有
	  *
	  * @return
	  */
	 @AutoLog(value = "收费站封路事件详细-查询所有")
	 @ApiOperation(value="收费站封路事件详细-查询所有", notes="收费站封路事件详细-查询所有")
	 @GetMapping(value = "/queryAllList")
	 public Result<?> queryAllList(@RequestParam String beginTime,@RequestParam String endTime) {
		 QueryWrapper<TollStationRecorder> queryWrapper = new QueryWrapper<>();
		 queryWrapper.between("create_time",beginTime,endTime);
		 queryWrapper.orderByDesc("create_time");
		 List<TollStationRecorder> pageList = tollStationRecorderService.list(queryWrapper);
		 List<TollStationRecorder> t=new ArrayList<>();
		 for (TollStationRecorder to:pageList) {
			 if (to.getForbidtime() != null && to.getAllowtime() != null) {
				 Long time = to.getAllowtime().getTime() - to.getForbidtime().getTime();
				 Date date = new Date(time);
				 String continuousTime = DateUtils.format(date, "hh小时mm分");
				 to.setContinuousTime(continuousTime);
			 }
			 to.setControlLevel("特级管制");
		 }
		 return Result.ok(pageList);
	 }
	
	/**
	 *   添加
	 *
	 * @param tollStationRecorder
	 * @return
	 */
	@AutoLog(value = "收费站封路事件详细-添加")
	@ApiOperation(value="收费站封路事件详细-添加", notes="收费站封路事件详细-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody TollStationRecorder tollStationRecorder) {
		tollStationRecorderService.save(tollStationRecorder);
		return Result.ok("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param tollStationRecorder
	 * @return
	 */
	@AutoLog(value = "收费站封路事件详细-编辑")
	@ApiOperation(value="收费站封路事件详细-编辑", notes="收费站封路事件详细-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody TollStationRecorder tollStationRecorder) {
		tollStationRecorderService.updateById(tollStationRecorder);
		return Result.ok("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "收费站封路事件详细-通过id删除")
	@ApiOperation(value="收费站封路事件详细-通过id删除", notes="收费站封路事件详细-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		tollStationRecorderService.removeById(id);
		return Result.ok("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "收费站封路事件详细-批量删除")
	@ApiOperation(value="收费站封路事件详细-批量删除", notes="收费站封路事件详细-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.tollStationRecorderService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "收费站封路事件详细-通过id查询")
	@ApiOperation(value="收费站封路事件详细-通过id查询", notes="收费站封路事件详细-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		TollStationRecorder tollStationRecorder = tollStationRecorderService.getById(id);
		if(tollStationRecorder==null) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(tollStationRecorder);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param tollStationRecorder
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, TollStationRecorder tollStationRecorder) {
        return super.exportXls(request, tollStationRecorder, TollStationRecorder.class, "收费站封路事件详细");
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
        return super.importExcel(request, response, TollStationRecorder.class);
    }

}
