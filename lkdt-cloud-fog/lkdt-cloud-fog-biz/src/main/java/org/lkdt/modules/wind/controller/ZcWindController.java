package org.lkdt.modules.wind.controller;

import com.alibaba.fastjson.JSONArray;
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
import org.lkdt.common.util.AlarmLevelUtil;
import org.lkdt.modules.fog.calculator.WcFactory;
import org.lkdt.modules.fog.calculator.WindCalculator;
import org.lkdt.modules.wind.domain.WindDO;
import org.lkdt.modules.wind.entity.ZcWind;
import org.lkdt.modules.wind.service.IZcWindService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
 /**
 * @Description: gale controller
 * @Author: Cai Xibei
 * @Date:   2021-06-02
 * @Version: V1.0
 */
@Api(tags="大风")
@RestController
@RequestMapping("/wind/zcWind")
@Slf4j
public class ZcWindController extends CloudController<ZcWind, IZcWindService> {
	@Autowired
	private IZcWindService zcWindService;

	@Autowired
	private WcFactory wcFactory;

	/**
	 * pagination list query
	 * @param zcWind
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "大风-分页列表查询")
	@ApiOperation(value="大风-分页列表查询", notes="大风-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(ZcWind zcWind,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<ZcWind> queryWrapper = QueryGenerator.initQueryWrapper(zcWind, req.getParameterMap());
		Page<ZcWind> page = new Page<ZcWind>(pageNo, pageSize);
		IPage<ZcWind> pageList = zcWindService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	/**
	 *   add to
	 * @param zcWind
	 * @return
	 */
	@AutoLog(value = "大风-添加")
	@ApiOperation(value="大风-添加", notes="大风-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody ZcWind zcWind) {
		zcWindService.save(zcWind);
		return Result.ok("添加成功！");
	}
	
	/**
	 *  edit
	 * @param zcWind
	 * @return
	 */
	@AutoLog(value = "大风-编辑")
	@ApiOperation(value="大风-编辑", notes="大风-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody ZcWind zcWind) {
		zcWindService.updateById(zcWind);
		return Result.ok("编辑成功!");
	}
	
	/**
	 *   delete by id
	 * @param id
	 * @return
	 */
	@AutoLog(value = "大风-通过id删除")
	@ApiOperation(value="大风-通过id删除", notes="大风-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		zcWindService.removeById(id);
		return Result.ok("删除成功!");
	}
	
	/**
	 *  batch deletion
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "大风-批量删除")
	@ApiOperation(value="大风-批量删除", notes="大风-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.zcWindService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}
	
	/**
	 * query by id
	 * @param id
	 * @return
	 */
	@AutoLog(value = "大风-通过id查询")
	@ApiOperation(value="大风-通过id查询", notes="大风-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		ZcWind zcWind = zcWindService.getById(id);
		if(zcWind==null) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(zcWind);
	}

    /**
    * export excel
    * @param request
    * @param zcWind
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, ZcWind zcWind) {
        return super.exportXls(request, zcWind, ZcWind.class, "大风");
    }

    /**
  	*  import data through excel
    * @param request
    * @param response
    * @return
    */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, ZcWind.class);
    }

	 /**
	  * gale warning
	  * @return
	  */
	 @RequestMapping(value = "/windAlarm", method = RequestMethod.POST)
	 public @ResponseBody Result windTest() {
		 JSONArray jsonArray = new JSONArray();
		 Map<String,Object> map = new HashMap<>();
		 List<WindDO> winds = zcWindService.listWindAlarm(map);
		 for(WindDO wind:winds) {
			 JSONObject jsonObject1 = new JSONObject();
			 jsonObject1.put("lng",wind.getLon());
			 jsonObject1.put("lat",wind.getLat());
			 jsonObject1.put("lnglat",new String[]{wind.getLon().toString(),wind.getLat().toString()});
			 jsonObject1.put("epId",wind.getId());
			 jsonObject1.put("equLocation",wind.getWindLocation());
			 WindCalculator windCalculator = wcFactory.getCalculator(wind.getId());
			 if(windCalculator != null){
				 jsonObject1.put("windLevel", AlarmLevelUtil.getLevelByWinds(windCalculator.getWinds()));
			 } else {
				 jsonObject1.put("windLevel",-1);
			 }
			 try{
				 windCalculator = wcFactory.getCalculator(wind.getId());
			 } catch (Exception e){}
			 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			 jsonObject1.put("time",windCalculator == null?"":sdf.format(windCalculator.getTime()));
			 jsonObject1.put("winds",windCalculator == null?"":windCalculator.getWinds());
			 jsonObject1.put("windd",windCalculator == null?"":windCalculator.getWindd());
			 jsonObject1.put("prediction",windCalculator == null?"":windCalculator.getPrediction());
			 jsonArray.add(jsonObject1);
		 }
		 return Result.ok(jsonArray);
	 }

}
