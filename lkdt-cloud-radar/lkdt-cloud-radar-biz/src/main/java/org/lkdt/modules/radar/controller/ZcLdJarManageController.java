package org.lkdt.modules.radar.controller;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.codec.digest.DigestUtils;
import org.lkdt.common.api.vo.Result;
import org.lkdt.common.aspect.annotation.AutoLog;
import org.lkdt.common.system.base.controller.CloudController;
import org.lkdt.common.system.query.QueryGenerator;
import org.lkdt.modules.radar.entity.ZcLdJarManage;
import org.lkdt.modules.radar.service.IZcLdJarManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
 /**
  * Jar包升级表
  * @project org.lkdt.modules.radar.controller.ZcLdJarManageController
  * @package org.lkdt.modules.radar.controller
  * @className ZcLdJarManageController
  * @author Cai Xibei
  * @version 1.0.0
  * @createTime 2021/8/4 9:53
  */
@Api(tags="JAR包升级表")
@RestController
@RequestMapping("/radar/zcLdJarManage")
@Slf4j
public class ZcLdJarManageController extends CloudController<ZcLdJarManage, IZcLdJarManageService> {
	@Autowired
	private IZcLdJarManageService zcLdJarManageService;

	@Value("${radar.jarPath}")
	private String jarPath;
	/**
	 * 分页列表查询
	 *
	 * @param zcLdJarManage
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "JAR包升级表-分页列表查询")
	@ApiOperation(value="JAR包升级表-分页列表查询", notes="JAR包升级表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(ZcLdJarManage zcLdJarManage,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<ZcLdJarManage> queryWrapper = QueryGenerator.initQueryWrapper(zcLdJarManage, req.getParameterMap());
		Page<ZcLdJarManage> page = new Page<ZcLdJarManage>(pageNo, pageSize);
		IPage<ZcLdJarManage> pageList = zcLdJarManageService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param zcLdJarManage
	 * @return
	 */
	@AutoLog(value = "JAR包升级表-添加")
	@ApiOperation(value="JAR包升级表-添加", notes="JAR包升级表-添加")
	@PostMapping(value = "/add")
	public Result<?> add(ZcLdJarManage zcLdJarManage,
						 @RequestParam(value = "file",required = false) MultipartFile file) throws IOException {
		//0. 判断配置文件中的路径是否存在,以及判断要上传的文件是否已经存在
		File directory = new File(jarPath);
		if(!directory.exists()){
			directory.mkdirs();
		}
		File jarFile = new File(jarPath + file.getOriginalFilename());
		if(jarFile.exists()){
			return Result.error("该文件已存在！");
		}
		//1. 先进行文件上传，一旦失败，则不入库
		try {
			if(file==null){
				return Result.error("未上传升级JAR文件！");
			}
			file.transferTo(jarFile);
		} catch (IOException e) {
			return Result.error("文件上传失败！"+e.getMessage());
		}
		// 2. 上传成功后,数据入库
		zcLdJarManage.setFileName(file.getOriginalFilename());
		zcLdJarManage.setMd5Code(DigestUtils.md5Hex(new FileInputStream(jarPath+file.getOriginalFilename())));
		zcLdJarManage.setFilePath(jarPath+file.getOriginalFilename());
		zcLdJarManageService.save(zcLdJarManage);
		return Result.ok("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param zcLdJarManage
	 * @return
	 */
	@AutoLog(value = "JAR包升级表-编辑")
	@ApiOperation(value="JAR包升级表-编辑", notes="JAR包升级表-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody ZcLdJarManage zcLdJarManage) {
		zcLdJarManageService.updateById(zcLdJarManage);
		return Result.ok("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "JAR包升级表-通过id删除")
	@ApiOperation(value="JAR包升级表-通过id删除", notes="JAR包升级表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		//0. 根据id查询记录，删除对应记录文件夹的升级JAR包
		ZcLdJarManage zcLdJarManage = zcLdJarManageService.getById(id);
		String jarPath = zcLdJarManage.getFilePath();
		Path path = Paths.get(jarPath);
		try {
			deleteFile(jarPath);
		} catch (Exception e) {
			return Result.error(e.getMessage());
		}
		//1. 删除文件成功，删除数据库记录
		zcLdJarManageService.removeById(id);
		return Result.ok("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "JAR包升级表-批量删除")
	@ApiOperation(value="JAR包升级表-批量删除", notes="JAR包升级表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	@Transactional
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		//0. 批量删除时也要删除对应jar文件
		List<String> idsList = Arrays.asList(ids.split(","));
		//1. 遍历删除
		for(String id:idsList){
			//1.0 根据id查询记录，删除对应记录文件夹的升级JAR包
			ZcLdJarManage zcLdJarManage = zcLdJarManageService.getById(id);
			String jarPath = zcLdJarManage.getFilePath();
			try {
				deleteFile(jarPath);
			} catch (Exception e) {
				return Result.error(e.getMessage());
			}
			//1.1 删除文件成功，删除数据库记录
			zcLdJarManageService.removeById(id);
		}
		return Result.ok("批量删除成功!");
	}

	 /**
	  * 删除文件具体实现方法
	  * @param filePath 路径
	  * @throws Exception 异常
	  */
	private void deleteFile(String filePath) throws Exception {
		Path path = Paths.get(filePath);
		File file = new File(filePath);
		if(file.exists()){
			Files.delete(path);
		}
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "JAR包升级表-通过id查询")
	@ApiOperation(value="JAR包升级表-通过id查询", notes="JAR包升级表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		ZcLdJarManage zcLdJarManage = zcLdJarManageService.getById(id);
		if(zcLdJarManage==null) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(zcLdJarManage);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param zcLdJarManage
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, ZcLdJarManage zcLdJarManage) {
        return super.exportXls(request, zcLdJarManage, ZcLdJarManage.class, "JAR包升级表");
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
        return super.importExcel(request, response, ZcLdJarManage.class);
    }

}
