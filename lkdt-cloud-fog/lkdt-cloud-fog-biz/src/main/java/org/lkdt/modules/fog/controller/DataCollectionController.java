package org.lkdt.modules.fog.controller;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.lkdt.common.api.vo.Result;
import org.lkdt.common.aspect.annotation.AutoLog;
import org.lkdt.common.system.base.controller.CloudController;
import org.lkdt.common.system.query.QueryGenerator;
import org.lkdt.common.util.StringUtils;
import org.lkdt.modules.api.HighwayApi;
import org.lkdt.modules.fog.entity.DataCollection;
import org.lkdt.modules.fog.service.IDataCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
 /**
 * @Description:  数据采集
 * @Author: jeecg-boot
 * @Date:   2021-07-01
 * @Version: V1.0
 */
@Api(tags=" 数据采集")
@RestController
@RequestMapping("/fog/dataCollection")
@Slf4j
public class DataCollectionController extends CloudController<DataCollection, IDataCollectionService> {
	@Autowired
	private IDataCollectionService dataCollectionService;

	@Autowired
	private HighwayApi highwayApi;
	
	/**
	 * 分页列表查询
	 *
	 * @param dataCollection
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = " 数据采集-分页列表查询")
	@ApiOperation(value=" 数据采集-分页列表查询", notes=" 数据采集-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(DataCollection dataCollection,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		List<String> ids = new ArrayList<>();
		if(!StringUtils.isEmpty(dataCollection.getHwId())){
			ids = highwayApi.queryChildNodes(dataCollection.getHwId());
			ids.add(dataCollection.getHwId());
			dataCollection.setHwId(String.join(",",ids));
		}
		QueryWrapper<DataCollection> queryWrapper = QueryGenerator.initQueryWrapper(dataCollection, req.getParameterMap());
		if(ids.size()==1){
			queryWrapper.eq("hw_id",dataCollection.getHwId());
		}
		Page<DataCollection> page = new Page<DataCollection>(pageNo, pageSize);
		IPage<DataCollection> pageList = dataCollectionService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param dataCollection
	 * @return
	 */
	@AutoLog(value = " 数据采集-添加")
	@ApiOperation(value=" 数据采集-添加", notes=" 数据采集-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody DataCollection dataCollection) {
		dataCollectionService.save(dataCollection);
		return Result.ok("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param dataCollection
	 * @return
	 */
	@AutoLog(value = " 数据采集-编辑")
	@ApiOperation(value=" 数据采集-编辑", notes=" 数据采集-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody DataCollection dataCollection) {
		dataCollectionService.updateById(dataCollection);
		return Result.ok("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = " 数据采集-通过id删除")
	@ApiOperation(value=" 数据采集-通过id删除", notes=" 数据采集-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		dataCollectionService.removeById(id);
		return Result.ok("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = " 数据采集-批量删除")
	@ApiOperation(value=" 数据采集-批量删除", notes=" 数据采集-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.dataCollectionService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = " 数据采集-通过id查询")
	@ApiOperation(value=" 数据采集-通过id查询", notes=" 数据采集-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		DataCollection dataCollection = dataCollectionService.getById(id);
		if(dataCollection==null) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(dataCollection);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param dataCollection
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, DataCollection dataCollection) {
        return super.exportXls(request, dataCollection, DataCollection.class, " 数据采集");
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
        return super.importExcel(request, response, DataCollection.class);
    }

}
