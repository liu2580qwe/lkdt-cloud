package org.lkdt.modules.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.lkdt.common.api.vo.Result;
import org.lkdt.common.aspect.annotation.AutoLog;
import org.lkdt.common.system.base.controller.CloudController;
import org.lkdt.common.system.query.QueryGenerator;
import org.lkdt.common.util.HighWayUtil;
import org.lkdt.modules.entity.ZcHighway;
import org.lkdt.modules.system.entity.Highway;
import org.lkdt.modules.system.entity.HighwayDepart;
import org.lkdt.modules.system.service.IHighwayService;
import org.lkdt.modules.system.service.ISysDepartService;
import org.lkdt.modules.system.service.IHighwayDepartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: 路段组织关联表
 * @Author: jeecg-boot
 * @Date:   2021-04-28
 * @Version: V1.0
 */
@Api(tags="路段组织关联表")
@RestController
@RequestMapping("/sys/zcHighwayDepart")
@Slf4j
public class HighwayDepartController extends CloudController<HighwayDepart, IHighwayDepartService> {
	@Autowired
	private IHighwayDepartService zcHighwayDepartService;
	@Autowired
	private ISysDepartService sysDepartService;
	@Autowired
	private IHighwayService highwayService;
	/**
	 * 分页列表查询
	 *
	 * @param zcHighwayDepart
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "路段组织关联表-分页列表查询")
	@ApiOperation(value="路段组织关联表-分页列表查询", notes="路段组织关联表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(HighwayDepart zcHighwayDepart,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<HighwayDepart> queryWrapper = QueryGenerator.initQueryWrapper(zcHighwayDepart, req.getParameterMap());
		Page<HighwayDepart> page = new Page<HighwayDepart>(pageNo, pageSize);
		IPage<HighwayDepart> pageList = zcHighwayDepartService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param zcHighwayDepart
	 * @return
	 */
	@AutoLog(value = "路段组织关联表-添加")
	@ApiOperation(value="路段组织关联表-添加", notes="路段组织关联表-添加")
	@PostMapping(value = "/add")
	@Transactional
	public Result<?> add(@RequestBody HighwayDepart zcHighwayDepart) {
		zcHighwayDepartService.save(zcHighwayDepart);
		Highway highWay= highwayService.getById(zcHighwayDepart);
		List<HighwayDepart> parent=zcHighwayDepartService.list(new QueryWrapper<HighwayDepart>().eq("hw_id",highWay.getPid())
				.eq("dep_id",zcHighwayDepart.getDepId()));
		if (highWay.getPid()!=null && CollectionUtils.isNotEmpty(parent)){
			HighwayDepart parentHighWay=new HighwayDepart();
			parentHighWay.setDepId(zcHighwayDepart.getDepId());
			parentHighWay.setHwId(highWay.getPid());
			zcHighwayDepartService.saveOrUpdate(parentHighWay);
		}
		return Result.ok("添加成功！");
	}

	
	/**
	 *  编辑
	 *
	 * @param zcHighwayDepart
	 * @return
	 */
	@AutoLog(value = "路段组织关联表-编辑")
	@ApiOperation(value="路段组织关联表-编辑", notes="路段组织关联表-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody HighwayDepart zcHighwayDepart) {
		zcHighwayDepartService.saveOrUpdate(zcHighwayDepart);
		Highway highWay= highwayService.getById(zcHighwayDepart);
		List<HighwayDepart> parent=zcHighwayDepartService.list(new QueryWrapper<HighwayDepart>().eq("hw_id",highWay.getPid())
				.eq("dep_id",zcHighwayDepart.getDepId()));
		if (highWay.getPid()!=null && CollectionUtils.isNotEmpty(parent)){
			HighwayDepart parentHighWay=new HighwayDepart();
			parentHighWay.setDepId(zcHighwayDepart.getDepId());
			parentHighWay.setHwId(highWay.getPid());
			zcHighwayDepartService.saveOrUpdate(parentHighWay);
		}
		return Result.ok("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "路段组织关联表-通过id删除")
	@ApiOperation(value="路段组织关联表-通过id删除", notes="路段组织关联表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		zcHighwayDepartService.removeById(id);
		return Result.ok("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "路段组织关联表-批量删除")
	@ApiOperation(value="路段组织关联表-批量删除", notes="路段组织关联表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.zcHighwayDepartService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "路段组织关联表-通过id查询")
	@ApiOperation(value="路段组织关联表-通过id查询", notes="路段组织关联表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		HighwayDepart zcHighwayDepart = zcHighwayDepartService.getById(id);
		if(zcHighwayDepart==null) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(zcHighwayDepart);
	}

	 /**
	  * 通过id查询
	  *
	  * @param hwid
	  * @return
	  */
	 @AutoLog(value = "路段组织关联表-通过id查询")
	 @ApiOperation(value="路段组织关联表-通过id查询", notes="路段组织关联表-通过id查询")
	 @GetMapping(value = "/queryByHwId")
	 public Result<?> queryByHwId(@RequestParam(name = "hwid", required = true) String hwid) {
		 List<String> zcHighwayDepart = zcHighwayDepartService.queryDeptByHwId(hwid);
		 if(zcHighwayDepart==null) {
			 return Result.error("未找到对应数据");
		 }
		 /*for (ZcHighwayDepart depart:zcHighwayDepart) {
		 	if (sysDepartService.getById(depart.getDepId())!=null){
				departs.add(sysDepartService.getById(depart.getDepId()));
			}
		 }*/
		 return Result.ok(zcHighwayDepart);
	 }

	/**
	 * 通过组织ID查询路段
	 *
	 * @param deptId
	 * @return
	 */
	@AutoLog(value = "路段组织关联表-通过组织ID查询路段")
	@ApiOperation(value="路段组织关联表-通过组织ID查询路段", notes="路段组织关联表-通过组织ID查询路段")
	@GetMapping(value = "/queryHwIdsByDeptId")
	public Result<?> queryHwIdsByDeptId(@RequestParam(name = "deptId", required = true) String deptId) {
		List<String> hwIds = zcHighwayDepartService.queryHwIdsByDeptId(deptId);
		if(hwIds==null) {
			return Result.error("未找到对应数据");
		}
		 /*for (ZcHighwayDepart depart:zcHighwayDepart) {
		 	if (sysDepartService.getById(depart.getDepId())!=null){
				departs.add(sysDepartService.getById(depart.getDepId()));
			}
		 }*/
		return Result.ok(hwIds);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param zcHighwayDepart
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, HighwayDepart zcHighwayDepart) {
        return super.exportXls(request, zcHighwayDepart, HighwayDepart.class, "路段组织关联表");
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
        return super.importExcel(request, response, HighwayDepart.class);
    }

}
