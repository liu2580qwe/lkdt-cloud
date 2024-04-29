package org.lkdt.modules.radar.controller;
import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.lkdt.common.api.vo.Result;
import org.lkdt.common.system.query.QueryGenerator;
import org.lkdt.modules.radar.entity.ZcLdWindRadarRelation;
import org.lkdt.modules.radar.service.IZcLdWindRadarRelationService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.lkdt.common.system.base.controller.CloudController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.lkdt.common.aspect.annotation.AutoLog;
 /**
  * 大风与雷达关联关系表
  * @project org.lkdt.modules.radar.controller.ZcLdWindRadarRelationController
  * @package org.lkdt.modules.radar.controller
  * @className ZcLdWindRadarRelationController
  * @author Cai Xibei
  * @version 1.0.0
  * @createTime 2021/8/17 16:40
  */
@Api(tags="大风与雷达关联关系表")
@RestController
@RequestMapping("/radar/zcLdWindRadarRelation")
@Slf4j
public class ZcLdWindRadarRelationController extends CloudController<ZcLdWindRadarRelation, IZcLdWindRadarRelationService> {
	@Autowired
	private IZcLdWindRadarRelationService zcLdWindRadarRelationService;
	
	/**
	 * 分页列表查询
	 *
	 * @param zcLdWindRadarRelation
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "大风与雷达关联关系表-分页列表查询")
	@ApiOperation(value="大风与雷达关联关系表-分页列表查询", notes="大风与雷达关联关系表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(ZcLdWindRadarRelation zcLdWindRadarRelation,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<ZcLdWindRadarRelation> queryWrapper = QueryGenerator.initQueryWrapper(zcLdWindRadarRelation, req.getParameterMap());
		Page<ZcLdWindRadarRelation> page = new Page<ZcLdWindRadarRelation>(pageNo, pageSize);
		IPage<ZcLdWindRadarRelation> pageList = zcLdWindRadarRelationService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param zcLdWindRadarRelation
	 * @return
	 */
	@AutoLog(value = "大风与雷达关联关系表-添加")
	@ApiOperation(value="大风与雷达关联关系表-添加", notes="大风与雷达关联关系表-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody ZcLdWindRadarRelation zcLdWindRadarRelation) {
		zcLdWindRadarRelationService.save(zcLdWindRadarRelation);
		return Result.ok("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param zcLdWindRadarRelation
	 * @return
	 */
	@AutoLog(value = "大风与雷达关联关系表-编辑")
	@ApiOperation(value="大风与雷达关联关系表-编辑", notes="大风与雷达关联关系表-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody ZcLdWindRadarRelation zcLdWindRadarRelation) {
		zcLdWindRadarRelationService.updateById(zcLdWindRadarRelation);
		return Result.ok("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "大风与雷达关联关系表-通过id删除")
	@ApiOperation(value="大风与雷达关联关系表-通过id删除", notes="大风与雷达关联关系表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		zcLdWindRadarRelationService.removeById(id);
		return Result.ok("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "大风与雷达关联关系表-批量删除")
	@ApiOperation(value="大风与雷达关联关系表-批量删除", notes="大风与雷达关联关系表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.zcLdWindRadarRelationService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "大风与雷达关联关系表-通过id查询")
	@ApiOperation(value="大风与雷达关联关系表-通过id查询", notes="大风与雷达关联关系表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		ZcLdWindRadarRelation zcLdWindRadarRelation = zcLdWindRadarRelationService.getById(id);
		if(zcLdWindRadarRelation==null) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(zcLdWindRadarRelation);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param zcLdWindRadarRelation
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, ZcLdWindRadarRelation zcLdWindRadarRelation) {
        return super.exportXls(request, zcLdWindRadarRelation, ZcLdWindRadarRelation.class, "大风与雷达关联关系表");
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
        return super.importExcel(request, response, ZcLdWindRadarRelation.class);
    }
}
