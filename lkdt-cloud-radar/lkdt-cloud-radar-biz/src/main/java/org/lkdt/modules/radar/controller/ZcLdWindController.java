package org.lkdt.modules.radar.controller;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.lkdt.common.api.vo.Result;
import org.lkdt.common.aspect.annotation.AutoLog;
import org.lkdt.common.system.base.controller.CloudController;
import org.lkdt.common.system.query.QueryGenerator;
import org.lkdt.common.util.StringUtils;
import org.lkdt.modules.radar.entity.ZcLdWind;
import org.lkdt.modules.radar.entity.ZcLdWindRadarRelation;
import org.lkdt.modules.radar.service.IZcLdWindRadarRelationService;
import org.lkdt.modules.radar.service.IZcLdWindService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
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
  * 大风配置表
  * @project org.lkdt.modules.radar.controller.ZcLdWindController
  * @package org.lkdt.modules.radar.controller
  * @className ZcLdWindController
  * @author Cai Xibei
  * @version 1.0.0
  * @createTime 2021/8/17 14:19
  */
@Api(tags="大风配置表")
@RestController
@RequestMapping("/radar/zcLdWind")
@Slf4j
public class ZcLdWindController extends CloudController<ZcLdWind, IZcLdWindService> {
	@Autowired
	private IZcLdWindService zcLdWindService;

	@Autowired
	private IZcLdWindRadarRelationService iZcLdWindRadarRelationService;
	
	/**
	 * 分页列表查询
	 *
	 * @param zcLdWind 大风实体
	 * @param pageNo 页码
	 * @param pageSize 页容量
	 * @param req 请求实体
	 * @return 返回数据
	 */
	@AutoLog(value = "大风配置表-分页列表查询")
	@ApiOperation(value="大风配置表-分页列表查询", notes="大风配置表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(ZcLdWind zcLdWind,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		//1. 查询关联表
		String radarId = req.getParameterMap().get("radarId")[0];
		List<String> windIds = iZcLdWindRadarRelationService.selectByRadarId(radarId);
		String windIdStr = String.join(",",windIds);
		zcLdWind.setId(windIdStr);
		//2.构造查询条件
		QueryWrapper<ZcLdWind> queryWrapper = QueryGenerator.initQueryWrapper(zcLdWind, req.getParameterMap());
		if(windIds.size()<=1){
			queryWrapper.eq("id",windIdStr);
		}
		Page<ZcLdWind> page = new Page<>(pageNo, pageSize);
		IPage<ZcLdWind> pageList = zcLdWindService.page(page, queryWrapper);
		return Result.ok(pageList);
	}

	 /**
	  * 添加大风数据，同时新增关联表数据
	  * @param zcLdWind 大风数据
	  * @param radarId 雷达id
	  * @return 操作结果
	  * @see Transactional
	  */
	@AutoLog(value = "大风配置表-添加")
	@ApiOperation(value="大风配置表-添加", notes="大风配置表-添加")
	@PostMapping(value = "/add")
	@Transactional
	public Result<?> add(@RequestBody ZcLdWind zcLdWind,
						 @RequestParam("radarId")String radarId) {
		zcLdWind.setId(StringUtils.getUUID());
		//1. 添加关联信息
		ZcLdWindRadarRelation zcLdWindRadarRelation = new ZcLdWindRadarRelation();
		zcLdWindRadarRelation.setWindId(zcLdWind.getId());
		zcLdWindRadarRelation.setRadarId(radarId);
		iZcLdWindRadarRelationService.save(zcLdWindRadarRelation);
		//2. 添加大风信息
		zcLdWindService.save(zcLdWind);
		return Result.ok("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param zcLdWind 大风实体类
	 * @return 修改结果
	 */
	@AutoLog(value = "大风配置表-编辑")
	@ApiOperation(value="大风配置表-编辑", notes="大风配置表-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody ZcLdWind zcLdWind) {
		zcLdWindService.updateById(zcLdWind);
		return Result.ok("编辑成功!");
	}

	 /**
	  * 通过id删除 , 同时还要和删除关联表数据
	  * @param id 要删除的大风id
	  * @param radarId 雷达id
	  * @return 删除结果
	  * @see Transactional
	  */
	@AutoLog(value = "大风配置表-通过id删除")
	@ApiOperation(value="大风配置表-通过id删除", notes="大风配置表-通过id删除")
	@DeleteMapping(value = "/delete")
	@Transactional
	public Result<?> delete(@RequestParam(name="id",required=true) String id,
							@RequestParam(value = "radarId",required = true)String radarId) {
		zcLdWindService.removeById(id);
		iZcLdWindRadarRelationService.delByWindIdAndRadarId(id,radarId);
		return Result.ok("删除成功!");
	}

	 /**
	  * 批量删除
	  * @param ids 批量删除的id
	  * @param radarId 雷达id
	  * @return 删除结果
	  * @see Transactional
	  */
	@AutoLog(value = "大风配置表-批量删除")
	@ApiOperation(value="大风配置表-批量删除", notes="大风配置表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	@Transactional
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids,
								 @RequestParam(value = "radarId",required = true)String radarId) {
		List<String> windIds = Arrays.asList(ids.split(","));
		for(String id:windIds){
			zcLdWindService.removeById(id);
			iZcLdWindRadarRelationService.delByWindIdAndRadarId(id,radarId);
		}
		return Result.ok("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id 要查询的id
	 * @return 查询结果
	 */
	@AutoLog(value = "大风配置表-通过id查询")
	@ApiOperation(value="大风配置表-通过id查询", notes="大风配置表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		ZcLdWind zcLdWind = zcLdWindService.getById(id);
		if(zcLdWind==null) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(zcLdWind);
	}

    /**
    * 导出excel
    *
    * @param request 请求
    * @param zcLdWind 大风类
    * @return 返回
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, ZcLdWind zcLdWind) {
        return super.exportXls(request, zcLdWind, ZcLdWind.class, "大风配置表");
    }

    /**
      * 通过excel导入数据
    *
    * @param request 请求
    * @param response 响应
    * @return 返回
    */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, ZcLdWind.class);
    }
}
