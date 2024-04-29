package org.lkdt.modules.system.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.lkdt.common.api.vo.Result;
import org.lkdt.common.aspect.annotation.AutoLog;
import org.lkdt.common.system.base.controller.CloudController;
import org.lkdt.common.system.query.QueryGenerator;
import org.lkdt.common.system.vo.HighwayModel;
import org.lkdt.common.util.HighWayUtil;
import org.lkdt.common.util.RedisUtil;
import org.lkdt.common.util.ShiroUtils;
import org.lkdt.common.util.oConvertUtils;
import org.lkdt.modules.system.entity.Highway;
import org.lkdt.modules.system.entity.HighwayDepart;
import org.lkdt.modules.system.service.IHighwayDepartService;
import org.lkdt.modules.system.service.IHighwayService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @Description: zc_highway
 * @Author: jeecg-boot
 * @Date: 2021-04-23
 * @Version: V1.0
 */
@Api(tags = "zc_highway")
@RestController
@RequestMapping("/sys/zcHighway")
@Slf4j
public class HighwayController extends CloudController<Highway, IHighwayService> {
    @Autowired
    private IHighwayService zcHighwayService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private HighWayUtil highWayUtil;
    @Autowired
    private IHighwayDepartService departService;

    /**
     * @return
     * @author 蔡西贝
     * 查询Tree树组件数据，统一数据返回格式
     */
    @AutoLog(value = "zc_highway-树组件列表查询")
    @ApiOperation(value = "zc_highway-树组件列表查询", notes = "zc_highway-树组件列表查询")
    @GetMapping(value = "/nodeList")
    public Result<?> queryTreeList(Highway zcHighway,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req,HttpServletResponse response) {
        List<String> hwIds=ShiroUtils.getUser().getHwIds();
        QueryWrapper<Highway> queryWrapper = QueryGenerator.initQueryWrapper(zcHighway, req.getParameterMap());

        if (!ShiroUtils.isAdmin()){
            queryWrapper.in("id", hwIds);
        }
        List<Highway> pageList = zcHighwayService.list(queryWrapper);
//        List<Highway> highways = new ArrayList<>();
//        for (Highway h:pageList) {
//           if (!h.getPid().equals("0")){
//               highways.add(zcHighwayService.getOne(new QueryWrapper<Highway>().eq("id",h.getPid())));
//           }
//           highways.add(h);
//        }
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
        List<Highway> findHighway=highways;
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
                List<Highway> children=new ArrayList<>();
                //获取与传入集合的交集
                children.addAll(findHighway);
                children.retainAll(list);
                //递归
                List<Map<String, Object>> childList = queryChildList(children, req, true);
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
        if (oConvertUtils.isEmpty(parentId)) {
            parentId = "0";
        }
        String name=req.getParameter("name");
        if (name != null){
          Page<Highway> page = new Page<Highway>(pageNo, pageSize);
          IPage<Highway> pageList = zcHighwayService.page(page, new QueryWrapper<Highway>().like("name",name));
          return Result.ok(pageList);
        }
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


    /**
     * 添加
     *
     * @param jsonObject
     * @return
     */
    @AutoLog(value = "zc_highway-添加")
    @ApiOperation(value = "zc_highway-添加", notes = "zc_highway-添加")
    @PostMapping(value = "/add")
    @Transactional
    public Result<?> add(@RequestBody JSONObject jsonObject) {
        //ZcHighway zcHighway=highWayDepartVO.getZcHighway();
        String selectedDeparts = jsonObject.getString("selecteddeparts");
        String[] deptIds = selectedDeparts.split(",");
        Highway zcHighway = JSON.parseObject(jsonObject.toJSONString(), Highway.class);
        //增加是否有字节点默认值，默认否
        if(zcHighway.getHasChild()==null){
            zcHighway.setHasChild("0");
        }
        zcHighwayService.addZcHighway(zcHighway);
        HighwayModel highwayModel = new HighwayModel();
        BeanUtils.copyProperties(zcHighway, highwayModel);
        boolean bool = redisUtil.set(highWayUtil.highway_rootList + zcHighway.getId(), highwayModel);
        if (bool) {
            for (String hw : deptIds) {
                HighwayDepart zcHighwayDepart = new HighwayDepart();
                zcHighwayDepart.setHwId(zcHighway.getId());
                zcHighwayDepart.setDepId(hw);
                departService.save(zcHighwayDepart);
            }
            this.saveParentDepart(zcHighway);
            return Result.ok("添加成功!");
        } else {
            return Result.error("添加失败");
        }
    }

    /**
     * 编辑
     *
     * @param jsonObject
     * @return
     */
    @AutoLog(value = "zc_highway-编辑")
    @ApiOperation(value = "zc_highway-编辑", notes = "zc_highway-编辑")
    @PutMapping(value = "/edit")
    @Transactional
    public Result<?> edit(@RequestBody JSONObject jsonObject) {
        String selectedDeparts = jsonObject.getString("selecteddeparts");
        String[] deptIds = selectedDeparts.split(",");
        Highway zcHighway = JSON.parseObject(jsonObject.toJSONString(), Highway.class);
        //增加是否有字节点默认值，默认否
        if(zcHighway.getHasChild()==null){
            zcHighway.setHasChild("0");
        }
        departService.remove(new QueryWrapper<HighwayDepart>().in("hw_id",zcHighway.getPid(),zcHighway.getId()));
        zcHighwayService.updateZcHighway(zcHighway);
        HighwayModel highwayModel = new HighwayModel();
        BeanUtils.copyProperties(zcHighway, highwayModel);
        if (zcHighway.getPid().equals(zcHighway.getId())){
            return Result.error("编辑失败，父节点不可以选自己");
        }
        boolean bool = redisUtil.set(highWayUtil.highway_rootList + zcHighway.getId(), highwayModel);
        if (bool) {
            for (String hw : deptIds) {
                //增加部门路段绑定
                HighwayDepart zcHighwayDepart = new HighwayDepart();
                zcHighwayDepart.setHwId(zcHighway.getId());
                zcHighwayDepart.setDepId(hw);
                departService.save(zcHighwayDepart);
            }
            this.saveParentDepart(zcHighway);
            return Result.ok("编辑成功!");
        } else {
            return Result.error("编辑失败");
        }
    }

    /**
     * 通过id删除
     * 判断是否为父节点，如果不是进行父节点路段部门绑定
     * @param id
     * @return
     */
    @AutoLog(value = "zc_highway-通过id删除")
    @ApiOperation(value = "zc_highway-通过id删除", notes = "zc_highway-通过id删除")
    @DeleteMapping(value = "/delete")
    @Transactional
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        Highway highway=zcHighwayService.getById(id);
        if (highway.getHasChild().equals("1")){
            return Result.error("请先删除子节点");
        }
        boolean del=redisUtil.del(highWayUtil.highway_rootList + id);
        if (del){
            zcHighwayService.deleteZcHighway(id);
            departService.remove(new QueryWrapper<HighwayDepart>().eq("hw_id",id));
            this.saveParentDepart(highway);
        }else {
         return Result.error("删除失败");
        }
        return Result.ok("删除成功!");
    }

    /**
     *
     * @param highway
     */
    public void saveParentDepart(Highway highway){
        if (!highway.getPid().equals("0")){
            departService.remove(new QueryWrapper<HighwayDepart>().in("hw_id",highway.getPid()));
            //根据父节点获取所有子节点路段id
            List<String> hwIds=zcHighwayService.queryChildNodesByHwId(highway.getPid());
            //根据子路段id获取部门
            List<HighwayDepart> highwayDeparts=departService.list(new QueryWrapper<HighwayDepart>().in("hw_id",hwIds));
            Set<String> departs=new HashSet<>();
            for (HighwayDepart highwayDepart:highwayDeparts) {
                departs.add(highwayDepart.getDepId());
            }
            //遍历存储父节点部门
            for (String parentDepart:departs) {
                HighwayDepart depart=new HighwayDepart();
                depart.setDepId(parentDepart);
                depart.setHwId(highway.getPid());
                departService.saveOrUpdate(depart);
            }
        }
    }
    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "zc_highway-批量删除")
    @ApiOperation(value = "zc_highway-批量删除", notes = "zc_highway-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.zcHighwayService.removeByIds(Arrays.asList(ids.split(",")));
        List<String> list = new ArrayList(Arrays.asList(ids.split(",")));
        for (String id : list) {
            redisUtil.del(highWayUtil.highway_rootList + id);
        }
        return Result.ok("批量删除成功！");
    }


    /**
     * 导出excel
     *
     * @param request
     * @param zcHighway
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, Highway zcHighway) {
        return super.exportXls(request, zcHighway, Highway.class, "zc_highway");
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
        return super.importExcel(request, response, Highway.class);
    }

    @GetMapping("/queryChildNodesByHwId")
    public List<String> queryChildNodesByHwId(@RequestParam String hwId){
        return zcHighwayService.queryChildNodesByHwId(hwId);
    }

    @GetMapping("/queryHighwaysByHwId")
    public Highway queryHighwaysByHwId(@RequestParam String hwId){
        return zcHighwayService.queryHighwaysByHwId(hwId);
    }
}
