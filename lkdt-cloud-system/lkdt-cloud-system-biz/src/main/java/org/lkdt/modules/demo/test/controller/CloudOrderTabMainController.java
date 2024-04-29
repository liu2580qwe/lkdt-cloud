package org.lkdt.modules.demo.test.controller;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.lkdt.common.api.vo.Result;
import org.lkdt.common.system.query.QueryGenerator;
import org.lkdt.modules.demo.test.entity.CloudOrderCustomer;
import org.lkdt.modules.demo.test.entity.CloudOrderMain;
import org.lkdt.modules.demo.test.entity.CloudOrderTicket;
import org.lkdt.modules.demo.test.service.ICloudOrderCustomerService;
import org.lkdt.modules.demo.test.service.ICloudOrderMainService;
import org.lkdt.modules.demo.test.service.ICloudOrderTicketService;
import org.lkdt.modules.demo.test.vo.CloudOrderMainPage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description: 一对多示例（ERP TAB风格）
 * @Author: ZhiLin
 * @Date: 2019-02-20
 * @Version: v2.0
 */
@Slf4j
@RestController
@RequestMapping("/test/order")
public class CloudOrderTabMainController {

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
    @GetMapping(value = "/orderList")
    public Result<?> respondePagedData(CloudOrderMain jeecgOrderMain,
                                       @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                       @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                       HttpServletRequest req) {
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
        jeecgOrderMainService.save(jeecgOrderMain);
        return Result.ok("添加成功!");
    }

    /**
     * 编辑
     *
     * @param cloudOrderMainPage
     * @return
     */
    @PutMapping("/edit")
    public Result<?> edit(@RequestBody CloudOrderMainPage cloudOrderMainPage) {
        CloudOrderMain jeecgOrderMain = new CloudOrderMain();
        BeanUtils.copyProperties(cloudOrderMainPage, jeecgOrderMain);
        jeecgOrderMainService.updateById(jeecgOrderMain);
        return Result.ok("编辑成功!");
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
        this.jeecgOrderMainService.removeByIds(Arrays.asList(ids.split(",")));
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
     * @param jeecgOrderCustomer
     * @return
     */
    @GetMapping(value = "/listOrderCustomerByMainId")
    public Result<?> queryOrderCustomerListByMainId(CloudOrderCustomer jeecgOrderCustomer,
                                                    @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                    @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                    HttpServletRequest req) {
        QueryWrapper<CloudOrderCustomer> queryWrapper = QueryGenerator.initQueryWrapper(jeecgOrderCustomer, req.getParameterMap());
        Page<CloudOrderCustomer> page = new Page<CloudOrderCustomer>(pageNo, pageSize);
        IPage<CloudOrderCustomer> pageList = jeecgOrderCustomerService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 通过id查询
     *
     * @param jeecgOrderTicket
     * @return
     */
    @GetMapping(value = "/listOrderTicketByMainId")
    public Result<?> queryOrderTicketListByMainId(CloudOrderTicket jeecgOrderTicket,
                                                  @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                  @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                  HttpServletRequest req) {
        QueryWrapper<CloudOrderTicket> queryWrapper = QueryGenerator.initQueryWrapper(jeecgOrderTicket, req.getParameterMap());
        Page<CloudOrderTicket> page = new Page<CloudOrderTicket>(pageNo, pageSize);
        IPage<CloudOrderTicket> pageList = jeecgOrderTicketService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 添加
     *
     * @param jeecgOrderCustomer
     * @return
     */
    @PostMapping(value = "/addCustomer")
    public Result<?> addCustomer(@RequestBody CloudOrderCustomer jeecgOrderCustomer) {
        jeecgOrderCustomerService.save(jeecgOrderCustomer);
        return Result.ok("添加成功!");
    }

    /**
     * 编辑
     *
     * @param jeecgOrderCustomer
     * @return
     */
    @PutMapping("/editCustomer")
    public Result<?> editCustomer(@RequestBody CloudOrderCustomer jeecgOrderCustomer) {
        jeecgOrderCustomerService.updateById(jeecgOrderCustomer);
        return Result.ok("添加成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @DeleteMapping(value = "/deleteCustomer")
    public Result<?> deleteCustomer(@RequestParam(name = "id", required = true) String id) {
        jeecgOrderCustomerService.removeById(id);
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping(value = "/deleteBatchCustomer")
    public Result<?> deleteBatchCustomer(@RequestParam(name = "ids", required = true) String ids) {
        this.jeecgOrderCustomerService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok("批量删除成功!");
    }

    /**
     * 添加
     *
     * @param jeecgOrderTicket
     * @return
     */
    @PostMapping(value = "/addTicket")
    public Result<?> addTicket(@RequestBody CloudOrderTicket jeecgOrderTicket) {
        jeecgOrderTicketService.save(jeecgOrderTicket);
        return Result.ok("添加成功!");
    }

    /**
     * 编辑
     *
     * @param jeecgOrderTicket
     * @return
     */
    @PutMapping("/editTicket")
    public Result<?> editTicket(@RequestBody CloudOrderTicket jeecgOrderTicket) {
        jeecgOrderTicketService.updateById(jeecgOrderTicket);
        return Result.ok("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @DeleteMapping(value = "/deleteTicket")
    public Result<?> deleteTicket(@RequestParam(name = "id", required = true) String id) {
        jeecgOrderTicketService.removeById(id);
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping(value = "/deleteBatchTicket")
    public Result<?> deleteBatchTicket(@RequestParam(name = "ids", required = true) String ids) {
        this.jeecgOrderTicketService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok("批量删除成功!");
    }

}