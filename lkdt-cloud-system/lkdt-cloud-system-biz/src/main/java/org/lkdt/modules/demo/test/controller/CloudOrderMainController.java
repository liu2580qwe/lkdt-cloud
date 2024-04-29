package org.lkdt.modules.demo.test.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.lkdt.common.api.vo.Result;
import org.lkdt.common.system.base.controller.CloudController;
import org.lkdt.common.system.query.QueryGenerator;
import org.lkdt.common.system.vo.LoginUser;
import org.lkdt.modules.demo.test.entity.CloudOrderCustomer;
import org.lkdt.modules.demo.test.entity.CloudOrderMain;
import org.lkdt.modules.demo.test.entity.CloudOrderTicket;
import org.lkdt.modules.demo.test.service.ICloudOrderCustomerService;
import org.lkdt.modules.demo.test.service.ICloudOrderMainService;
import org.lkdt.modules.demo.test.service.ICloudOrderTicketService;
import org.lkdt.modules.demo.test.vo.CloudOrderMainPage;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.BeanUtils;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description: 一对多示例（JEditableTable行编辑）
 * @Author: jeecg-boot
 * @Date:2019-02-15
 * @Version: V2.0
 */
@RestController
@RequestMapping("/test/jeecgOrderMain")
@Slf4j
public class CloudOrderMainController extends CloudController<CloudOrderMain, ICloudOrderMainService> {

    @Autowired
    private ICloudOrderMainService jeecgOrderMainService;
    @Autowired
    private ICloudOrderCustomerService jeecgOrderCustomerService;
    @Autowired
    private ICloudOrderTicketService jeecgOrderTicketService;

    /**
     * 分页列表查询
     *
     * @param jeecgOrderMain
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @GetMapping(value = "/list")
    public Result<?> queryPageList(CloudOrderMain jeecgOrderMain, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo, @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        QueryWrapper<CloudOrderMain> queryWrapper = QueryGenerator.initQueryWrapper(jeecgOrderMain, req.getParameterMap());
        Page<CloudOrderMain> page = new Page<CloudOrderMain>(pageNo, pageSize);
        IPage<CloudOrderMain> pageList = jeecgOrderMainService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 添加
     *
     * @param cloudOrderMainPage
     * @return
     */
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody CloudOrderMainPage cloudOrderMainPage) {
        CloudOrderMain jeecgOrderMain = new CloudOrderMain();
        BeanUtils.copyProperties(cloudOrderMainPage, jeecgOrderMain);
        jeecgOrderMainService.saveMain(jeecgOrderMain, cloudOrderMainPage.getJeecgOrderCustomerList(), cloudOrderMainPage.getJeecgOrderTicketList());
        return Result.ok("添加成功！");
    }

    /**
     * 编辑
     *
     * @param cloudOrderMainPage
     * @return
     */
    @PutMapping(value = "/edit")
    public Result<?> eidt(@RequestBody CloudOrderMainPage cloudOrderMainPage) {
        CloudOrderMain jeecgOrderMain = new CloudOrderMain();
        BeanUtils.copyProperties(cloudOrderMainPage, jeecgOrderMain);
        jeecgOrderMainService.updateMain(jeecgOrderMain, cloudOrderMainPage.getJeecgOrderCustomerList(), cloudOrderMainPage.getJeecgOrderTicketList());
        return Result.ok("编辑成功！");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        jeecgOrderMainService.delMain(id);
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.jeecgOrderMainService.delBatchMain(Arrays.asList(ids.split(",")));
        return Result.ok("批量删除成功!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        CloudOrderMain jeecgOrderMain = jeecgOrderMainService.getById(id);
        return Result.ok(jeecgOrderMain);
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/queryOrderCustomerListByMainId")
    public Result<?> queryOrderCustomerListByMainId(@RequestParam(name = "id", required = true) String id) {
        List<CloudOrderCustomer> jeecgOrderCustomerList = jeecgOrderCustomerService.selectCustomersByMainId(id);
        return Result.ok(jeecgOrderCustomerList);
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/queryOrderTicketListByMainId")
    public Result<?> queryOrderTicketListByMainId(@RequestParam(name = "id", required = true) String id) {
        List<CloudOrderTicket> jeecgOrderTicketList = jeecgOrderTicketService.selectTicketsByMainId(id);
        return Result.ok(jeecgOrderTicketList);
    }

    /**
     * 导出excel
     *
     * @param request
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, CloudOrderMain jeecgOrderMain) {
        // Step.1 组装查询条件
        QueryWrapper<CloudOrderMain> queryWrapper = QueryGenerator.initQueryWrapper(jeecgOrderMain, request.getParameterMap());
        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        //获取当前用户
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        List<CloudOrderMainPage> pageList = new ArrayList<CloudOrderMainPage>();

        List<CloudOrderMain> jeecgOrderMainList = jeecgOrderMainService.list(queryWrapper);
        for (CloudOrderMain orderMain : jeecgOrderMainList) {
            CloudOrderMainPage vo = new CloudOrderMainPage();
            BeanUtils.copyProperties(orderMain, vo);
            // 查询机票
            List<CloudOrderTicket> jeecgOrderTicketList = jeecgOrderTicketService.selectTicketsByMainId(orderMain.getId());
            vo.setJeecgOrderTicketList(jeecgOrderTicketList);
            // 查询客户
            List<CloudOrderCustomer> jeecgOrderCustomerList = jeecgOrderCustomerService.selectCustomersByMainId(orderMain.getId());
            vo.setJeecgOrderCustomerList(jeecgOrderCustomerList);
            pageList.add(vo);
        }

        // 导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "一对多订单示例");
        // 注解对象Class
        mv.addObject(NormalExcelConstants.CLASS, CloudOrderMainPage.class);
        // 自定义表格参数
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("自定义导出Excel内容标题", "导出人:" + sysUser.getRealname(), "自定义Sheet名字"));
        // 导出数据列表
        mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
        return mv;
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param
     * @return
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();// 获取上传文件对象
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(2);
            params.setNeedSave(true);
            try {
                List<CloudOrderMainPage> list = ExcelImportUtil.importExcel(file.getInputStream(), CloudOrderMainPage.class, params);
                for (CloudOrderMainPage page : list) {
                    CloudOrderMain po = new CloudOrderMain();
                    BeanUtils.copyProperties(page, po);
                    jeecgOrderMainService.saveMain(po, page.getJeecgOrderCustomerList(), page.getJeecgOrderTicketList());
                }
                return Result.ok("文件导入成功！");
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Result.error("文件导入失败：" + e.getMessage());
            } finally {
                try {
                    file.getInputStream().close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return Result.error("文件导入失败！");
    }

}
