package org.lkdt.modules.system.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.lkdt.common.api.vo.Result;
import org.lkdt.common.aspect.annotation.AutoLog;
import org.lkdt.common.system.api.ISysBaseAPI;
import org.lkdt.common.system.base.controller.CloudController;
import org.lkdt.common.system.vo.LoginUser;
import org.lkdt.common.util.ShiroUtils;
import org.lkdt.modules.system.entity.ZcEmployeeDailyreport;
import org.lkdt.modules.system.service.IZcEmployeeDailyreportService;
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

import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: 员工日报表
 * @Author: jeecg-boot
 * @Date: 2021-04-30
 * @Version: V1.0
 */
@Api(tags = "员工日报表")
@RestController
@RequestMapping("/sys/dailyreport")
@Slf4j
public class ZcEmployeeDailyreportController
		extends CloudController<ZcEmployeeDailyreport, IZcEmployeeDailyreportService> {
	@Autowired
	private IZcEmployeeDailyreportService zcEmployeeDailyreportService;

	@Autowired
	private ISysBaseAPI sysBaseAPI;

	/**
	 * 分页列表查询
	 *
	 * @param zcEmployeeDailyreport
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "员工日报表-分页列表查询")
	@ApiOperation(value="员工日报表-分页列表查询", notes="员工日报表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(String createTime,
								   HttpServletRequest req) {
//		QueryWrapper<ZcEmployeeDailyreport> queryWrapper = QueryGenerator.initQueryWrapper(zcEmployeeDailyreport, req.getParameterMap());
//		Page<ZcEmployeeDailyreport> page = new Page<ZcEmployeeDailyreport>(pageNo, pageSize);
//		IPage<ZcEmployeeDailyreport> pageList = zcEmployeeDailyreportService.page(page, queryWrapper);
//		return Result.ok(pageList);
		DateTime start = DateUtil.parse(createTime + " 00:00:00");
		DateTime end = DateUtil.parse(createTime + " 23:59:59");
		LambdaQueryWrapper<ZcEmployeeDailyreport> var3 = new LambdaQueryWrapper<ZcEmployeeDailyreport>();
		var3.between(ZcEmployeeDailyreport::getCreateTime, start, end);
		List<ZcEmployeeDailyreport> lst = zcEmployeeDailyreportService.list(var3);
		if (lst.isEmpty()) {
			return Result.ok();
		}
		
		List<String> usernames = new ArrayList<String>();
		for (ZcEmployeeDailyreport rpt: lst) {
			usernames.add(rpt.getCreateBy());
		}
		List<LoginUser> users = sysBaseAPI.queryUserByNames(usernames.toArray(new String[lst.size()]));
		Map<String, LoginUser> userMap = new HashMap<>();
		for (LoginUser u : users) {
			userMap.put(u.getUsername(), u);
		}
		
		
		JSONArray resultjson = new JSONArray();
		for (ZcEmployeeDailyreport rpt: lst) {
			JSONObject json = new JSONObject();
			LoginUser u = userMap.get(rpt.getCreateBy());
			json.put("pname", u.getRealname());
			json.put("avatar", u.getAvatar());
			JSONArray worknote = new JSONArray();
			if (StringUtil.isNotEmpty(rpt.getWorknote())) {
				 worknote = JSONArray.parseArray(rpt.getWorknote());
			}
			json.put("worknote", worknote);
			
			JSONArray plans = new JSONArray();
			if (StringUtil.isNotEmpty(rpt.getTplan())) {
				plans = JSONArray.parseArray(rpt.getTplan());
			}
			json.put("plans", plans);
			resultjson.add(json);
		}
		
		return Result.ok(resultjson);
	}

	/**
	 * 添加
	 *
	 * @param zcEmployeeDailyreport
	 * @return
	 */
	@AutoLog(value = "员工日报表-添加")
	@ApiOperation(value = "员工日报表-添加", notes = "员工日报表-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody ZcEmployeeDailyreport zcEmployeeDailyreport) {

		String today = DateUtil.today();
		DateTime start = DateUtil.parse(today + " 00:00:00");
		DateTime end = DateUtil.parse(today + " 23:59:59");
		// 删除今天的日报
		LambdaQueryWrapper<ZcEmployeeDailyreport> var3 = new LambdaQueryWrapper();
		var3.eq(ZcEmployeeDailyreport::getCreateBy, ShiroUtils.getUser().getUsername());
		var3.between(ZcEmployeeDailyreport::getCreateTime, start, end);
		zcEmployeeDailyreportService.remove(var3);
		zcEmployeeDailyreportService.save(zcEmployeeDailyreport);
		return Result.ok("添加成功！");
	}

	/**
	 * 编辑
	 *
	 * @param zcEmployeeDailyreport
	 * @return
	 */
	@AutoLog(value = "员工日报表-编辑")
	@ApiOperation(value = "员工日报表-编辑", notes = "员工日报表-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody ZcEmployeeDailyreport zcEmployeeDailyreport) {
		zcEmployeeDailyreportService.updateById(zcEmployeeDailyreport);
		return Result.ok("编辑成功!");
	}

	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "员工日报表-通过id删除")
	@ApiOperation(value = "员工日报表-通过id删除", notes = "员工日报表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
		zcEmployeeDailyreportService.removeById(id);
		return Result.ok("删除成功!");
	}

	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "员工日报表-批量删除")
	@ApiOperation(value = "员工日报表-批量删除", notes = "员工日报表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
		this.zcEmployeeDailyreportService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "员工日报表-通过id查询")
	@ApiOperation(value = "员工日报表-通过id查询", notes = "员工日报表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
		ZcEmployeeDailyreport zcEmployeeDailyreport = zcEmployeeDailyreportService.getById(id);
		if (zcEmployeeDailyreport == null) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(zcEmployeeDailyreport);
	}

	/**
	 * 导出excel
	 *
	 * @param request
	 * @param zcEmployeeDailyreport
	 */
	@RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(HttpServletRequest request, ZcEmployeeDailyreport zcEmployeeDailyreport) {
		return super.exportXls(request, zcEmployeeDailyreport, ZcEmployeeDailyreport.class, "员工日报表");
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
		return super.importExcel(request, response, ZcEmployeeDailyreport.class);
	}

}
