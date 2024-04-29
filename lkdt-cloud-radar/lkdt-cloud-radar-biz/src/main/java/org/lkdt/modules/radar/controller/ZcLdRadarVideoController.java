package org.lkdt.modules.radar.controller;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.lkdt.modules.radar.entity.ZcLdEquipmentVideo;
import org.lkdt.modules.radar.service.IZcLdEquipmentVideoService;
import org.lkdt.common.api.vo.Result;
import org.lkdt.common.system.query.QueryGenerator;
import org.lkdt.modules.radar.entity.ZcLdRadarVideo;
import org.lkdt.modules.radar.service.IZcLdRadarVideoService;
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
 * @Description: 雷达设备摄像头表
 * @Author: jeecg-boot
 * @Date:   2021-07-29
 * @Version: V1.0
 */
@Api(tags="雷达设备摄像头表")
@RestController
@RequestMapping("/radar/zcLdRadarVideo")
@Slf4j
public class ZcLdRadarVideoController extends CloudController<ZcLdRadarVideo, IZcLdRadarVideoService> {
	@Autowired
	private IZcLdRadarVideoService zcLdRadarVideoService;

	@Autowired
	private IZcLdEquipmentVideoService iZcLdEquipmentVideoService;
	
	/**
	 * 分页列表查询
	 *
	 * @param zcLdRadarVideo
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "雷达设备摄像头表-分页列表查询")
	@ApiOperation(value="雷达设备摄像头表-分页列表查询", notes="雷达设备摄像头表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(ZcLdRadarVideo zcLdRadarVideo,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<ZcLdRadarVideo> queryWrapper = QueryGenerator.initQueryWrapper(zcLdRadarVideo, req.getParameterMap());
		Page<ZcLdRadarVideo> page = new Page<ZcLdRadarVideo>(pageNo, pageSize);
		IPage<ZcLdRadarVideo> pageList = zcLdRadarVideoService.page(page, queryWrapper);
		// 根据vedio_id查询关联表,判断摄像头是否和雷达关联
		for(ZcLdRadarVideo zcLdRadarVideo1:pageList.getRecords()){
			List<ZcLdEquipmentVideo> zcLdEquipmentVideos = iZcLdEquipmentVideoService.selectByVedioId(zcLdRadarVideo1.getId());
			if(zcLdEquipmentVideos.size()>0){
				zcLdRadarVideo1.setIsItRelated(true);
			}else{
				zcLdRadarVideo1.setIsItRelated(false);
			}
		}
		return Result.ok(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param zcLdRadarVideo
	 * @return
	 */
	@AutoLog(value = "雷达设备摄像头表-添加")
	@ApiOperation(value="雷达设备摄像头表-添加", notes="雷达设备摄像头表-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody ZcLdRadarVideo zcLdRadarVideo) {
		zcLdRadarVideoService.save(zcLdRadarVideo);
		return Result.ok("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param zcLdRadarVideo
	 * @return
	 */
	@AutoLog(value = "雷达设备摄像头表-编辑")
	@ApiOperation(value="雷达设备摄像头表-编辑", notes="雷达设备摄像头表-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody ZcLdRadarVideo zcLdRadarVideo) {
		zcLdRadarVideoService.updateById(zcLdRadarVideo);
		return Result.ok("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "雷达设备摄像头表-通过id删除")
	@ApiOperation(value="雷达设备摄像头表-通过id删除", notes="雷达设备摄像头表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		zcLdRadarVideoService.removeById(id);
		return Result.ok("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "雷达设备摄像头表-批量删除")
	@ApiOperation(value="雷达设备摄像头表-批量删除", notes="雷达设备摄像头表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.zcLdRadarVideoService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "雷达设备摄像头表-通过id查询")
	@ApiOperation(value="雷达设备摄像头表-通过id查询", notes="雷达设备摄像头表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		ZcLdRadarVideo zcLdRadarVideo = zcLdRadarVideoService.getById(id);
		if(zcLdRadarVideo==null) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(zcLdRadarVideo);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param zcLdRadarVideo
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, ZcLdRadarVideo zcLdRadarVideo) {
        return super.exportXls(request, zcLdRadarVideo, ZcLdRadarVideo.class, "雷达设备摄像头表");
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
        return super.importExcel(request, response, ZcLdRadarVideo.class);
    }

}
