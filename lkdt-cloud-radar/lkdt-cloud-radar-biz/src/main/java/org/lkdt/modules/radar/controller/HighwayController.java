package org.lkdt.modules.radar.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.lkdt.common.api.vo.Result;
import org.lkdt.common.aspect.annotation.AutoLog;
import org.lkdt.common.system.query.QueryGenerator;
import org.lkdt.common.util.HighWayUtil;
import org.lkdt.common.util.RedisUtil;
import org.lkdt.common.util.StringUtils;
import org.lkdt.modules.radar.entity.Highway;
import org.lkdt.modules.radar.service.IHighwayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
/**
 * @Description: zc_highway
 * @Author: lius
 * @Date: 2021-04-23
 * @Version: V1.0
 */
@Api(tags = "zc_highway")
@RestController
@RequestMapping("/sys/zcHighway")
@Slf4j
public class HighwayController{
    @Autowired
    private IHighwayService zcHighwayService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private HighWayUtil highWayUtil;

    /**
     * 查询Tree树组件数据，统一数据返回格式
     * @return
     */
    @AutoLog(value = "zc_highway-树组件列表查询")
    @ApiOperation(value = "zc_highway-树组件列表查询", notes = "zc_highway-树组件列表查询")
    @GetMapping(value = "/nodeList")
    public Result<?> queryTreeList(Highway zcHighway,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        List<Highway> pageList = zcHighwayService.list();
        List<Map<String, Object>> nodeList = queryChildList(pageList, req, false);
        return Result.ok(nodeList);
    }

    /**
     * 递归查询子节点
     *
     * @param highways
     * @param req
     * @param isChild  用来筛选父节点进行递归
     * @return
     */
    public List<Map<String, Object>> queryChildList(List<Highway> highways, HttpServletRequest req, boolean isChild) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        //将查询的所有结果进行筛选，对父节点进行递归-代表是根节点
        List<Highway> highwayList = new ArrayList<>();
        if (!isChild) {
            for (Highway zcHighway : highways) {
                if (zcHighway.getPid().equals("0")) {
                    highwayList.add(zcHighway);
                }
            }
            highways = highwayList;
        }
        for (Highway zcHighway : highways) {
            Map<String, Object> nodeMap = new HashMap<>();
            nodeMap.put("title", zcHighway.getDetail());
            nodeMap.put("key", zcHighway.getId());
            //根节点
            if (zcHighway.getHasChild().equals("1")) {
                //查询子节点列表
                Highway zcHighway1 = new Highway();
                zcHighway1.setPid(zcHighway.getId());
                QueryWrapper<Highway> queryWrapper = QueryGenerator.initQueryWrapper(zcHighway1, req.getParameterMap());
                // 使用 eq 防止模糊查询
                queryWrapper.eq("pid", zcHighway.getId());
                List<Highway> list = zcHighwayService.list(queryWrapper);
                //递归
                List<Map<String, Object>> childList = queryChildList(list, req, true);
                nodeMap.put("children", childList);
                nodeMap.put("isLeaf", false);
            }
            //叶子节点
            if (zcHighway.getHasChild().equals("0")) {
                nodeMap.put("isLeaf", true);
            }
            //单亲节点
            if (zcHighway.getHasChild().equals("0") && zcHighway.getPid().equals("0")) {
                nodeMap.put("isLeaf", true);
            }
            resultList.add(nodeMap);
        }
        return resultList;
    }


    /**
     * 分页列表查询
     *
     * @param zcHighway
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "zc_highway-分页列表查询")
    @ApiOperation(value = "zc_highway-分页列表查询", notes = "zc_highway-分页列表查询")
    @GetMapping(value = "/rootList")
    public Result<?> queryPageList(Highway zcHighway,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        String parentId = zcHighway.getPid();
        if (StringUtils.isEmpty(parentId)) {
            parentId = "0";
        }
        String name=req.getParameter("name");
        zcHighway.setPid(null);
        QueryWrapper<Highway> queryWrapper = QueryGenerator.initQueryWrapper(zcHighway, req.getParameterMap());
        // 使用 eq 防止模糊查询
        queryWrapper.eq("pid", parentId);
        Page<Highway> page = new Page<Highway>(pageNo, pageSize);
        IPage<Highway> pageList = zcHighwayService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 获取子数据
     *
     * @param zcHighway
     * @return
     */
    @AutoLog(value = "zc_highway-获取子数据")
    @ApiOperation(value = "zc_highway-获取子数据", notes = "zc_highway-获取子数据")
    @GetMapping(value = "/childList")
    public Result<?> queryPageList(Highway zcHighway) {
        String pid = zcHighway.getPid();
        List<Highway> list = zcHighwayService.list(new QueryWrapper<Highway>().eq("pid", pid));
        Page<Highway> page = new Page<Highway>();
        page.setRecords(list);
        page.setTotal(list.size());
        return Result.ok(page);
    }

    @GetMapping("/queryChildNodesByHwId")
    public List<String> queryChildNodesByHwId(@RequestParam String hwId){
        return zcHighwayService.queryChildNodesByHwId(hwId);
    }

}
