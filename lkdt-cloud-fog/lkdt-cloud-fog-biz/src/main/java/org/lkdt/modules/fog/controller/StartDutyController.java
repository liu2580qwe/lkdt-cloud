package org.lkdt.modules.fog.controller;

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
import org.lkdt.common.util.ShiroUtils;
import org.lkdt.modules.api.SysBaseRemoteApi;
import org.lkdt.modules.fog.entity.DutyClock;
import org.lkdt.modules.fog.entity.StartDuty;
import org.lkdt.modules.fog.service.IDutyClockService;
import org.lkdt.modules.fog.service.IStartDutyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

 /**
 * @Description: 值班记录表
 * @Author: jeecg-boot
 * @Date:   2021-06-28
 * @Version: V1.0
 */
@Api(tags="值班记录表")
@RestController
@RequestMapping("/fog/startDuty")
@Slf4j
public class StartDutyController extends CloudController<StartDuty, IStartDutyService> {
	@Autowired
	private IStartDutyService startDutyService;
	@Autowired
	private IDutyClockService dutyClockService;
	@Autowired
	private SysBaseRemoteApi sysBaseRemoteApi;
	/**
	 * 分页列表查询
	 *
	 * @param startDuty
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "值班记录表-分页列表查询")
	@ApiOperation(value="值班记录表-分页列表查询", notes="值班记录表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(StartDuty startDuty,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		req.getParameterMap().get("column");
		QueryWrapper<StartDuty> queryWrapper = QueryGenerator.initQueryWrapper(startDuty, req.getParameterMap());
		queryWrapper.orderByDesc("start_time");
		Page<StartDuty> page = new Page<StartDuty>(pageNo, pageSize);
		IPage<StartDuty> pageList = startDutyService.page(page, queryWrapper);
		return Result.ok(pageList);
	}

	 @AutoLog(value = "值班记录表-查询所有")
	 @ApiOperation(value="值班记录表-查询所有", notes="值班记录表-查询所有")
	 @GetMapping(value = "/queryAllList")
	 public Result<?> queryAllList(StartDuty startDuty,
									HttpServletRequest req) {
		 QueryWrapper<StartDuty> queryWrapper = QueryGenerator.initQueryWrapper(startDuty, req.getParameterMap());
		 List<StartDuty> pageList = startDutyService.list(queryWrapper);
		 return Result.ok(pageList);
	 }

	 /**
	  * 通过id查询
	  *
	  * @param id
	  * @return
	  */
	 @AutoLog(value = "值班打卡-通过主表ID查询")
	 @ApiOperation(value="值班打卡-通过主表ID查询", notes="值班打卡-通过主表ID查询")
	 @GetMapping(value = "/queryDutyClockByMainId")
	 public Result<?> queryDutyClockListByMainId(@RequestParam(name="id",required=true) String id) {
		 List<DutyClock> dutyClockList = dutyClockService.selectByMainId(id);
		 IPage <DutyClock> page = new Page<>();
		 page.setRecords(dutyClockList);
		 page.setTotal(dutyClockList.size());
		 return Result.ok(page);
	 }

	/**
	 *   添加
	 *
	 * @param startDuty
	 * @return
	 */
	@AutoLog(value = "值班记录表-添加")
	@ApiOperation(value="值班记录表-添加", notes="值班记录表-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody StartDuty startDuty) {
		startDuty.setCreateBy(ShiroUtils.getUserId());
		startDuty.setStartTime(new Date());
		startDutyService.save(startDuty);
		return Result.ok("添加成功！");
	}


	 /**
	  *   添加值班打卡
	  *
	  * @param dutyClock
	  * @return
	  */
	 @AutoLog(value = "值班打卡表-添加")
	 @ApiOperation(value = "值班打卡表-添加", notes = "值班打卡表-添加")
	 @PostMapping(value = "/addClock")
	 public Result<?> addClock(@RequestBody DutyClock dutyClock) {
		 if (dutyClock.getStartDutyId() != null) {
			 dutyClock.setClockState("1");
			 dutyClockService.save(dutyClock);
		 }
		 return Result.ok("添加成功！");
	 }
	
	/**
	 *  编辑
	 *
	 * @param startDuty
	 * @return
	 */
	@AutoLog(value = "值班记录表-编辑")
	@ApiOperation(value="值班记录表-编辑", notes="值班记录表-编辑")
	@PostMapping(value = "/edit")
	public Result<?> edit(@RequestBody StartDuty startDuty) {
		startDuty.setEndTime(new Date());
		startDutyService.updateById(startDuty);
		return Result.ok("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "值班记录表-通过id删除")
	@ApiOperation(value="值班记录表-通过id删除", notes="值班记录表-通过id删除")
	@PostMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		startDutyService.removeById(id);
		return Result.ok("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "值班记录表-批量删除")
	@ApiOperation(value="值班记录表-批量删除", notes="值班记录表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.startDutyService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "值班记录表-通过id查询")
	@ApiOperation(value="值班记录表-通过id查询", notes="值班记录表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		StartDuty startDuty = startDutyService.getById(id);
		String name = sysBaseRemoteApi.queryTableDictTextByKey("sys_user", "realname", "id", startDuty.getCreateBy());
		startDuty.setCreateBy(name);
		if(startDuty==null) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(startDuty);
	}
	 /**
	  * 通过userName查询
	  */
	 @AutoLog(value = "值班记录表-通过userName查询")
	 @ApiOperation(value = "值班记录表-通过id查询", notes = "值班记录表-通过userName查询")
	 @GetMapping(value = "/queryByUserName")
	 public Result<?> queryByUserName() {
		 QueryWrapper<StartDuty> query = new QueryWrapper<>();
		 query.eq("create_by",ShiroUtils.getUserId());
		 query.and(wrapper->wrapper.isNull("end_time"));
		 StartDuty startDuty = startDutyService.getOne(query);
		 if (startDuty == null) {
			 return Result.error("未找到对应数据");
		 }
		 return Result.ok(startDuty);
	 }

    /**
    * 导出excel
    *
    * @param request
    * @param startDuty
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, StartDuty startDuty) {
        return super.exportXls(request, startDuty, StartDuty.class, "值班记录表");
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
        return super.importExcel(request, response, StartDuty.class);
    }

}
