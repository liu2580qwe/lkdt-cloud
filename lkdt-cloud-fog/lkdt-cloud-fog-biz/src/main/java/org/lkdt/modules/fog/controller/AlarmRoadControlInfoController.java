package org.lkdt.modules.fog.controller;

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
import org.lkdt.modules.fog.entity.AlarmRoadControlInfo;
import org.lkdt.modules.fog.service.IAlarmRoadControlInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

 /**
 * @Description: 路段告警与封路信息关系表
 * @Author: jeecg-boot
 * @Date:   2021-06-03
 * @Version: V1.0
 */
@Api(tags="路段告警与封路信息关系表")
@RestController
@RequestMapping("/fog/alarmRoadControlInfo")
@Slf4j
public class AlarmRoadControlInfoController extends CloudController<AlarmRoadControlInfo, IAlarmRoadControlInfoService> {
	@Autowired
	private IAlarmRoadControlInfoService alarmRoadControlInfoService;
	
	/**
	 * 分页列表查询
	 *
	 * @param alarmRoadControlInfo
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "路段告警与封路信息关系表-分页列表查询")
	@ApiOperation(value="路段告警与封路信息关系表-分页列表查询", notes="路段告警与封路信息关系表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(AlarmRoadControlInfo alarmRoadControlInfo,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<AlarmRoadControlInfo> queryWrapper = QueryGenerator.initQueryWrapper(alarmRoadControlInfo, req.getParameterMap());
		Page<AlarmRoadControlInfo> page = new Page<AlarmRoadControlInfo>(pageNo, pageSize);
		IPage<AlarmRoadControlInfo> pageList = alarmRoadControlInfoService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param alarmRoadControlInfo
	 * @return
	 */
	@AutoLog(value = "路段告警与封路信息关系表-添加")
	@ApiOperation(value="路段告警与封路信息关系表-添加", notes="路段告警与封路信息关系表-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody AlarmRoadControlInfo alarmRoadControlInfo) {
		alarmRoadControlInfoService.save(alarmRoadControlInfo);
		return Result.ok("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param alarmRoadControlInfo
	 * @return
	 */
	@AutoLog(value = "路段告警与封路信息关系表-编辑")
	@ApiOperation(value="路段告警与封路信息关系表-编辑", notes="路段告警与封路信息关系表-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody AlarmRoadControlInfo alarmRoadControlInfo) {
		alarmRoadControlInfoService.updateById(alarmRoadControlInfo);
		return Result.ok("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "路段告警与封路信息关系表-通过id删除")
	@ApiOperation(value="路段告警与封路信息关系表-通过id删除", notes="路段告警与封路信息关系表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		alarmRoadControlInfoService.removeById(id);
		return Result.ok("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "路段告警与封路信息关系表-批量删除")
	@ApiOperation(value="路段告警与封路信息关系表-批量删除", notes="路段告警与封路信息关系表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.alarmRoadControlInfoService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "路段告警与封路信息关系表-通过id查询")
	@ApiOperation(value="路段告警与封路信息关系表-通过id查询", notes="路段告警与封路信息关系表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		AlarmRoadControlInfo alarmRoadControlInfo = alarmRoadControlInfoService.getById(id);
		if(alarmRoadControlInfo==null) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(alarmRoadControlInfo);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param alarmRoadControlInfo
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, AlarmRoadControlInfo alarmRoadControlInfo) {
        return super.exportXls(request, alarmRoadControlInfo, AlarmRoadControlInfo.class, "路段告警与封路信息关系表");
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
        return super.importExcel(request, response, AlarmRoadControlInfo.class);
    }

}
