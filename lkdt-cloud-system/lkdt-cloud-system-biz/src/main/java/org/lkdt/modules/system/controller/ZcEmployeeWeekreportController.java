package org.lkdt.modules.system.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.lkdt.common.api.vo.Result;
import org.lkdt.common.aspect.annotation.AutoLog;
import org.lkdt.common.system.base.controller.CloudController;
import org.lkdt.common.system.query.QueryGenerator;
import org.lkdt.common.util.ShiroUtils;
import org.lkdt.common.util.StringUtils;
import org.lkdt.modules.system.entity.ZcEmployeeDailyreport;
import org.lkdt.modules.system.entity.ZcEmployeeWeekreport;
import org.lkdt.modules.system.service.IZcEmployeeDailyreportService;
import org.lkdt.modules.system.service.IZcEmployeeWeekreportService;
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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.core.date.DateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: 员工周报表
 * @Author: jeecg-boot
 * @Date: 2021-05-18
 * @Version: V1.0
 */
@Api(tags = "员工周报表")
@RestController
@RequestMapping("/sys/weekreport")
@Slf4j
public class ZcEmployeeWeekreportController
		extends CloudController<ZcEmployeeWeekreport, IZcEmployeeWeekreportService> {
	@Autowired
	private IZcEmployeeWeekreportService zcEmployeeWeekreportService;
	
	@Autowired
	private IZcEmployeeDailyreportService zcEmployeeDailyreportService;

	/**
	 * 分页列表查询
	 *
	 * @param zcEmployeeWeekreport
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "员工周报表-分页列表查询")
	@ApiOperation(value = "员工周报表-分页列表查询", notes = "员工周报表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(ZcEmployeeWeekreport zcEmployeeWeekreport,
			@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
	
		
		QueryWrapper<ZcEmployeeWeekreport> queryWrapper = QueryGenerator.initQueryWrapper(zcEmployeeWeekreport,
				req.getParameterMap());
		queryWrapper.eq("rtype", zcEmployeeWeekreport.getRtype());
		
		String multiUser = req.getParameter("multiUser");
		
		if (!StringUtils.isEmpty(multiUser)) {
			String tmp[] = multiUser.split(",");
			queryWrapper.in("create_by", tmp);
		}
		Page<ZcEmployeeWeekreport> page = new Page<ZcEmployeeWeekreport>(pageNo, pageSize);
		IPage<ZcEmployeeWeekreport> pageList = zcEmployeeWeekreportService.page(page, queryWrapper);
		return Result.ok(pageList);
	}

	@AutoLog(value = "员工周报表-周报初始化")
	@ApiOperation(value = "员工周报表-周报初始化", notes = "员工周报表-周报初始化")
	@GetMapping(value = "/init")
	public Result<?> initPage(@RequestParam(name = "rtype", defaultValue = "") String rtype, HttpServletRequest req) {

//		Page<ZcEmployeeWeekreport> page = new Page<ZcEmployeeWeekreport>(pageNo, pageSize);
//		IPage<ZcEmployeeWeekreport> pageList = zcEmployeeWeekreportService.page(page, queryWrapper);
//		return Result.ok(pageList);
		if("W".equalsIgnoreCase(rtype)) {
			LambdaQueryWrapper<ZcEmployeeDailyreport> var3 = new LambdaQueryWrapper();
			var3.eq(ZcEmployeeDailyreport::getCreateBy, ShiroUtils.getUser().getUsername());
			Date today = new Date();
			Date startDay = DateUtil.beginOfWeek(today);
			Date endDay = DateUtil.endOfWeek(today);
			var3.between(ZcEmployeeDailyreport::getCreateTime, startDay, endDay);
			List<ZcEmployeeDailyreport> lst = zcEmployeeDailyreportService.list(var3);
			JSONArray result = new JSONArray();
			for (ZcEmployeeDailyreport dr: lst) {
				String worknote = dr.getWorknote();
				JSONArray array = JSONArray.parseArray(worknote);
				for (Object row : array) {
					JSONObject obj = (JSONObject) row;
					result.add(obj.getString("note"));
				}
			}
			return Result.ok(result);
		}
		if("M".equalsIgnoreCase(rtype)) {
			LambdaQueryWrapper<ZcEmployeeWeekreport> var3 = new LambdaQueryWrapper();
			var3.eq(ZcEmployeeWeekreport::getCreateBy, ShiroUtils.getUser().getUsername());
			Date today = new Date();
			Date startDay = DateUtil.beginOfMonth(today);
			Date endDay = DateUtil.endOfMonth(today);
			var3.between(ZcEmployeeWeekreport::getCreateTime, startDay, endDay);
			List<ZcEmployeeWeekreport> lst = zcEmployeeWeekreportService.list(var3);
			JSONArray result = new JSONArray();
			for (ZcEmployeeWeekreport dr: lst) {
				String worknote = dr.getWorknote();
				JSONArray array = JSONArray.parseArray(worknote);
				for (Object row : array) {
					JSONObject obj = (JSONObject) row;
				 	result.add(obj.getString("note"));
				}
			}
			return Result.ok(result);
		}
		
		return Result.ok();
	}

	/**
	 * 添加
	 *
	 * @param zcEmployeeWeekreport
	 * @return
	 */
	@AutoLog(value = "员工周报表-添加")
	@ApiOperation(value = "员工周报表-添加", notes = "员工周报表-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody ZcEmployeeWeekreport zcEmployeeWeekreport) {
		String type=zcEmployeeWeekreport.getRtype();
		Date createTime = zcEmployeeWeekreport.getCreateTime();
		if (type.equals("W")){
			int tare = DateUtil.weekOfMonth(createTime);
			int m = DateUtil.month(createTime)+1;
			zcEmployeeWeekreport.setTarea(m+"月 第"+tare+'周');
			zcEmployeeWeekreport.setCreatername(ShiroUtils.getUser().getRealname());
			List<ZcEmployeeWeekreport> week=zcEmployeeWeekreportService.getBaseMapper().selectList(
					new QueryWrapper<ZcEmployeeWeekreport>().eq("tarea",zcEmployeeWeekreport.getTarea()).eq("creatername",zcEmployeeWeekreport.getCreatername())
			);
			if (week.size()==0 || week==null){
				zcEmployeeWeekreportService.save(zcEmployeeWeekreport);
				return Result.ok("添加成功！");
			}else {
				zcEmployeeWeekreportService.remove(
						new QueryWrapper<ZcEmployeeWeekreport>().eq("tarea",zcEmployeeWeekreport.getTarea()).eq("creatername",zcEmployeeWeekreport.getCreatername())
				);
				zcEmployeeWeekreportService.save(zcEmployeeWeekreport);
				return Result.ok("添加成功！");
			}
		}else if (type.equals("M")){
			int tare = DateUtil.weekOfMonth(createTime);
			int m = DateUtil.month(createTime)+1;
			zcEmployeeWeekreport.setTarea(m+"月");
			zcEmployeeWeekreport.setCreatername(ShiroUtils.getUser().getRealname());
			zcEmployeeWeekreportService.save(zcEmployeeWeekreport);
			return Result.ok("添加成功！");
		}else {
			return Result.error("添加失败");
		}
	}

	/**
	 * 编辑
	 *
	 * @param zcEmployeeWeekreport
	 * @return
	 */
	@AutoLog(value = "员工周报表-编辑")
	@ApiOperation(value = "员工周报表-编辑", notes = "员工周报表-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody ZcEmployeeWeekreport zcEmployeeWeekreport) {
		zcEmployeeWeekreportService.updateById(zcEmployeeWeekreport);
		return Result.ok("编辑成功!");
	}

	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "员工周报表-通过id删除")
	@ApiOperation(value = "员工周报表-通过id删除", notes = "员工周报表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
		zcEmployeeWeekreportService.removeById(id);
		return Result.ok("删除成功!");
	}

	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "员工周报表-批量删除")
	@ApiOperation(value = "员工周报表-批量删除", notes = "员工周报表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
		this.zcEmployeeWeekreportService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "员工周报表-通过id查询")
	@ApiOperation(value = "员工周报表-通过id查询", notes = "员工周报表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
		ZcEmployeeWeekreport zcEmployeeWeekreport = zcEmployeeWeekreportService.getById(id);
		if (zcEmployeeWeekreport == null) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(zcEmployeeWeekreport);
	}

	/**
	 * 导出excel
	 *
	 * @param request
	 * @param zcEmployeeWeekreport
	 */
	@RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(HttpServletRequest request, ZcEmployeeWeekreport zcEmployeeWeekreport) {
		return super.exportXls(request, zcEmployeeWeekreport, ZcEmployeeWeekreport.class, "员工周报表");
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
		return super.importExcel(request, response, ZcEmployeeWeekreport.class);
	}

}
