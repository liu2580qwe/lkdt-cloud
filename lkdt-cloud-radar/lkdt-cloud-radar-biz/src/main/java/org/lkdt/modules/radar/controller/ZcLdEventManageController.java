package org.lkdt.modules.radar.controller;
import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.minio.MinioClient;
import org.lkdt.common.api.vo.Result;
import org.lkdt.common.system.query.QueryGenerator;
import org.lkdt.modules.radar.entity.ZcLdEventManage;
import org.lkdt.modules.radar.service.IZcLdEventManageService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.lkdt.common.system.base.controller.CloudController;
import org.lkdt.common.util.MinioUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.lkdt.common.aspect.annotation.AutoLog;
 /**
  * @project org.lkdt.modules.radar.controller.ZcLdEventManageController
  * @package org.lkdt.modules.radar.controller
  * @className ZcLdEventManageController
  * @author Cai Xibei
  * @version 1.0.0
  * @description 雷达事件管理
  * @createTime 2021/8/4 14:17
  */
@Api(tags="雷达事件管理")
@RestController
@RequestMapping("/radar/zcLdEventManage")
@Slf4j
public class ZcLdEventManageController extends CloudController<ZcLdEventManage, IZcLdEventManageService> {
	@Autowired
	private IZcLdEventManageService zcLdEventManageService;

	@Value("${lkdt.minio.bucketName}")
	private String bucketName;
	/**
	 * 分页列表查询
	 *
	 * @param zcLdEventManage
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "雷达事件管理-分页列表查询")
	@ApiOperation(value="雷达事件管理-分页列表查询", notes="雷达事件管理-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(ZcLdEventManage zcLdEventManage,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<ZcLdEventManage> queryWrapper = QueryGenerator.initQueryWrapper(zcLdEventManage, req.getParameterMap());
		Page<ZcLdEventManage> page = new Page<ZcLdEventManage>(pageNo, pageSize);
		IPage<ZcLdEventManage> pageList = zcLdEventManageService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param zcLdEventManage
	 * @return
	 */
	@AutoLog(value = "雷达事件管理-添加")
	@ApiOperation(value="雷达事件管理-添加", notes="雷达事件管理-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody ZcLdEventManage zcLdEventManage) {
		zcLdEventManageService.save(zcLdEventManage);
		return Result.ok("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param zcLdEventManage
	 * @return
	 */
	@AutoLog(value = "雷达事件管理-编辑")
	@ApiOperation(value="雷达事件管理-编辑", notes="雷达事件管理-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody ZcLdEventManage zcLdEventManage) {
		zcLdEventManageService.updateById(zcLdEventManage);
		return Result.ok("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "雷达事件管理-通过id删除")
	@ApiOperation(value="雷达事件管理-通过id删除", notes="雷达事件管理-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		//0. 删除记录前删除mino中的视频
		ZcLdEventManage zcLdEventManage = zcLdEventManageService.getById(id);
		try {
			// todo 删除不生效...
			MinioUtil.removeObject(bucketName,zcLdEventManage.getVedioName());
		} catch (Exception e) {
			return Result.ok(e.getMessage());
		}
		//1. 删除记录
		zcLdEventManageService.removeById(id);
		return Result.ok("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "雷达事件管理-批量删除")
	@ApiOperation(value="雷达事件管理-批量删除", notes="雷达事件管理-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.zcLdEventManageService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "雷达事件管理-通过id查询")
	@ApiOperation(value="雷达事件管理-通过id查询", notes="雷达事件管理-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		ZcLdEventManage zcLdEventManage = zcLdEventManageService.getById(id);
		if(zcLdEventManage==null) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(zcLdEventManage);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param zcLdEventManage
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, ZcLdEventManage zcLdEventManage) {
        return super.exportXls(request, zcLdEventManage, ZcLdEventManage.class, "雷达事件管理");
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
        return super.importExcel(request, response, ZcLdEventManage.class);
    }
    
    
    
    

}
