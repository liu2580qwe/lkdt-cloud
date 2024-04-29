package org.lkdt.modules.fog.controller;

import com.alibaba.fastjson.JSONObject;
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
import org.lkdt.modules.fog.entity.AlarmNotice;
import org.lkdt.modules.fog.entity.AlarmNoticeModel;
import org.lkdt.modules.fog.service.IAlarmNoticeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @Description: zc_alarm_notice
 * @Author: jeecg-boot
 * @Date:   2021-04-28
 * @Version: V1.0
 */
@Api(tags="zc_alarm_notice")
@RestController
@RequestMapping("/fog/zcAlarmNotice")
@Slf4j
public class AlarmNoticeController extends CloudController<AlarmNotice, IAlarmNoticeService> {
	@Autowired
	private IAlarmNoticeService zcAlarmNoticeService;
	
	/**
	 * 分页列表查询
	 *
	 * @param alarmNotice
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "zc_alarm_notice-分页列表查询")
	@ApiOperation(value="zc_alarm_notice-分页列表查询", notes="zc_alarm_notice-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(AlarmNotice alarmNotice,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<AlarmNotice> queryWrapper = QueryGenerator.initQueryWrapper(alarmNotice, req.getParameterMap());
		Page<AlarmNotice> page = new Page<AlarmNotice>(pageNo, pageSize);
		IPage<AlarmNotice> pageList = zcAlarmNoticeService.page(page, queryWrapper);
		JSONObject json=new JSONObject();
		for (AlarmNotice a:pageList.getRecords()) {
			if (a.getSendtext()!=null){
				json=JSONObject.parseObject(a.getSendtext());
				if (json.getString("imgpath")!=null && json.getString("imgtime")!=null){
					a.setImgpath(json.get("imgpath").toString());
					//JSON时间戳格式转换
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String imgtime=df.format(json.get("imgtime"));
					Timestamp time=Timestamp.valueOf(imgtime);
					a.setImgtime(time);
				}
			}
		}
		return Result.ok(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param alarmNotice
	 * @return
	 */
	@AutoLog(value = "zc_alarm_notice-添加")
	@ApiOperation(value="zc_alarm_notice-添加", notes="zc_alarm_notice-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody AlarmNotice alarmNotice) {
		zcAlarmNoticeService.save(alarmNotice);
		return Result.ok("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param alarmNotice
	 * @return
	 */
	@AutoLog(value = "zc_alarm_notice-编辑")
	@ApiOperation(value="zc_alarm_notice-编辑", notes="zc_alarm_notice-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody AlarmNotice alarmNotice) {
		zcAlarmNoticeService.updateById(alarmNotice);
		return Result.ok("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "zc_alarm_notice-通过id删除")
	@ApiOperation(value="zc_alarm_notice-通过id删除", notes="zc_alarm_notice-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		zcAlarmNoticeService.removeById(id);
		return Result.ok("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "zc_alarm_notice-批量删除")
	@ApiOperation(value="zc_alarm_notice-批量删除", notes="zc_alarm_notice-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.zcAlarmNoticeService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "zc_alarm_notice-通过id查询")
	@ApiOperation(value="zc_alarm_notice-通过id查询", notes="zc_alarm_notice-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		AlarmNotice alarmNotice = zcAlarmNoticeService.getById(id);
		if(alarmNotice ==null) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(alarmNotice);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param alarmNotice
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, AlarmNotice alarmNotice) {
        return super.exportXls(request, alarmNotice, AlarmNotice.class, "zc_alarm_notice");
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
        return super.importExcel(request, response, AlarmNotice.class);
    }

	/**
	 * 通过id查询
	 *
	 * @param map
	 * @return
	 */
	@AutoLog(value = "zc_alarm_notice-通过id查询")
	@ApiOperation(value="zc_alarm_notice-通过id查询", notes="zc_alarm_notice-通过id查询")
	@GetMapping(value = "/queryAlermNoticelist")
	public List<AlarmNoticeModel> queryAlermNoticelist(Map<String, Object> map) {
		List<AlarmNoticeModel> alarmNoticeList = zcAlarmNoticeService.queryAlermNoticelist(map);
		return alarmNoticeList;
	}
}
