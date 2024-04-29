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
import org.lkdt.modules.traffic.entity.TrafficIncidentRecord;
import org.lkdt.modules.traffic.service.ITrafficIncidentRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;


 /**
 * @Description: 道路事件记录
 * @Author: jeecg-boot
 * @Date:   2021-06-03
 * @Version: V1.0
 */
@Api(tags="道路事件记录")
@RestController
@RequestMapping("/traffic/trafficIncidentRecord")
@Slf4j
public class TrafficIncidentRecordController extends CloudController<TrafficIncidentRecord, ITrafficIncidentRecordService> {
	@Autowired
	private ITrafficIncidentRecordService trafficIncidentRecordService;
	
	/**
	 * 分页列表查询
	 *
	 * @param trafficIncidentRecord
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "道路事件记录-分页列表查询")
	@ApiOperation(value="道路事件记录-分页列表查询", notes="道路事件记录-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(TrafficIncidentRecord trafficIncidentRecord,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<TrafficIncidentRecord> queryWrapper = QueryGenerator.initQueryWrapper(trafficIncidentRecord, req.getParameterMap());
		Page<TrafficIncidentRecord> page = new Page<TrafficIncidentRecord>(pageNo, pageSize);
		IPage<TrafficIncidentRecord> pageList = trafficIncidentRecordService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param trafficIncidentRecord
	 * @return
	 */
	@AutoLog(value = "道路事件记录-添加")
	@ApiOperation(value="道路事件记录-添加", notes="道路事件记录-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody TrafficIncidentRecord trafficIncidentRecord) {
		trafficIncidentRecordService.save(trafficIncidentRecord);
		return Result.ok("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param trafficIncidentRecord
	 * @return
	 */
	@AutoLog(value = "道路事件记录-编辑")
	@ApiOperation(value="道路事件记录-编辑", notes="道路事件记录-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody TrafficIncidentRecord trafficIncidentRecord) {
		trafficIncidentRecordService.updateById(trafficIncidentRecord);
		return Result.ok("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "道路事件记录-通过id删除")
	@ApiOperation(value="道路事件记录-通过id删除", notes="道路事件记录-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		trafficIncidentRecordService.removeById(id);
		return Result.ok("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "道路事件记录-批量删除")
	@ApiOperation(value="道路事件记录-批量删除", notes="道路事件记录-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.trafficIncidentRecordService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "道路事件记录-通过id查询")
	@ApiOperation(value="道路事件记录-通过id查询", notes="道路事件记录-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		TrafficIncidentRecord trafficIncidentRecord = trafficIncidentRecordService.getById(id);
		if(trafficIncidentRecord==null) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(trafficIncidentRecord);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param trafficIncidentRecord
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, TrafficIncidentRecord trafficIncidentRecord) {
        return super.exportXls(request, trafficIncidentRecord, TrafficIncidentRecord.class, "道路事件记录");
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
        return super.importExcel(request, response, TrafficIncidentRecord.class);
    }

}
