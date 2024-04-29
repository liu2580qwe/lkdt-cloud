package org.lkdt.modules.fog.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.lkdt.common.api.vo.Result;
import org.lkdt.common.aspect.annotation.AutoLog;
import org.lkdt.common.system.query.QueryGenerator;
import org.lkdt.common.system.vo.DictModel;
import org.lkdt.common.system.vo.LoginUser;
import org.lkdt.common.util.*;
import org.lkdt.modules.api.HighwayApi;
import org.lkdt.modules.api.SysBaseRemoteApi;
import org.lkdt.modules.fog.entity.Alarm;
import org.lkdt.modules.fog.entity.AlarmRoad;
import org.lkdt.modules.fog.entity.AlarmRoadModel;
import org.lkdt.modules.fog.service.IAlarmRoadService;
import org.lkdt.modules.fog.service.IAlarmService;
import org.lkdt.modules.fog.vo.AlarmRoadPage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;
/**
 * @Description: warning road table
 * @Author: Cai Xibei
 * @Date: 2021-04-27
 * @Version: V1.0
 */
@Api(tags = "告警道路表")
@RestController
@RequestMapping("/fog/alarmRoad")
@Slf4j
public class AlarmRoadController {
    @Autowired
    private IAlarmRoadService alarmRoadService;
    @Autowired
    private IAlarmService alarmService;

    @Autowired
    private HighWayUtil highWayUtil;

    @Autowired
    private HighwayApi highwayApi;

    @Autowired
    private SysBaseRemoteApi sysBaseRemoteApi;
    /**
     * pagination list query
     * @param alarmRoad
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "告警道路表-分页列表查询")
    @ApiOperation(value = "告警道路表-分页列表查询", notes = "告警道路表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(AlarmRoad alarmRoad,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        List<String> ids = new ArrayList<>(25);
        if(!StringUtils.isEmpty(alarmRoad.getHwId())){
            ids = highwayApi.queryChildNodes(alarmRoad.getHwId());
            ids.add(alarmRoad.getHwId());
            alarmRoad.setHwId(String.join(",",ids));
        }
        QueryWrapper<AlarmRoad> queryWrapper = QueryGenerator.initQueryWrapper(alarmRoad, req.getParameterMap());
        //Because when there is only one value for ids, it is not in query but fuzzy query, so we have to disable fuzzy query in this situation
        if(ids.size()==1){
            queryWrapper.eq("hw_id",alarmRoad.getHwId());
        }
        queryWrapper.orderByDesc("starttime");
        Page<AlarmRoad> page = new Page<AlarmRoad>(pageNo, pageSize);
        IPage<AlarmRoad> pageList = alarmRoadService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    /**
     * add to
     *
     * @param alarmRoadPage
     * @return
     */
    @AutoLog(value = "告警道路表-添加")
    @ApiOperation(value = "告警道路表-添加", notes = "告警道路表-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody AlarmRoadPage alarmRoadPage) {
        AlarmRoad alarmRoad = new AlarmRoad();
        BeanUtils.copyProperties(alarmRoadPage, alarmRoad);
        alarmRoadService.saveMain(alarmRoad, alarmRoadPage.getAlarmList());
        return Result.ok("添加成功！");
    }

    /**
     * edit
     *
     * @param alarmRoadPage
     * @return
     */
    @AutoLog(value = "告警道路表-编辑")
    @ApiOperation(value = "告警道路表-编辑", notes = "告警道路表-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody AlarmRoadPage alarmRoadPage) {
        AlarmRoad alarmRoad = new AlarmRoad();
        BeanUtils.copyProperties(alarmRoadPage, alarmRoad);
        AlarmRoad alarmRoadEntity = alarmRoadService.getById(alarmRoad.getId());
        if (alarmRoadEntity == null) {
            return Result.error("未找到对应数据");
        }
        alarmRoadService.updateMain(alarmRoad, alarmRoadPage.getAlarmList());
        return Result.ok("编辑成功!");
    }

    /**
     * delete by id
     *
     * @param id
     * @return
     */
    @AutoLog(value = "告警道路表-通过id删除")
    @ApiOperation(value = "告警道路表-通过id删除", notes = "告警道路表-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        alarmRoadService.delMain(id);
        return Result.ok("删除成功!");
    }

    /**
     * batch deletion
     * @param ids
     * @return
     */
    @AutoLog(value = "告警道路表-批量删除")
    @ApiOperation(value = "告警道路表-批量删除", notes = "告警道路表-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.alarmRoadService.delBatchMain(Arrays.asList(ids.split(",")));
        return Result.ok("批量删除成功！");
    }

    /**
     * query by id
     * @param id
     * @return
     */
    @AutoLog(value = "告警道路表-通过id查询")
    @ApiOperation(value = "告警道路表-通过id查询", notes = "告警道路表-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        AlarmRoad alarmRoad = alarmRoadService.getById(id);
        if (alarmRoad == null) {
            return Result.error("未找到对应数据");
        }
        return Result.ok(alarmRoad);

    }

    /**
     * query by id
     * @param id
     * @return
     */
    @AutoLog(value = "告警表通过主表ID查询")
    @ApiOperation(value = "告警表主表ID查询", notes = "告警表-通主表ID查询")
    @GetMapping(value = "/queryAlarmByMainId")
    public Result<?> queryAlarmListByMainId(@RequestParam(name = "id", required = true) String id,
                                            Alarm alarm,
                                            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                            @RequestParam(name = "pageSize", defaultValue = "" + Integer.MAX_VALUE) Integer pageSize,
                                            HttpServletRequest req) {
        alarm.setRoadAlarmId(id);
        alarm.setId("");
        QueryWrapper<Alarm> queryWrapper = QueryGenerator.initQueryWrapper(alarm, req.getParameterMap());
        queryWrapper.orderByDesc("begintime");
        Page<Alarm> page = new Page<Alarm>(pageNo, pageSize);
        IPage<Alarm> alarmList = alarmService.page(page, queryWrapper);
        return Result.ok(alarmList);
    }

    /**
     * export excel
     * @param request
     * @param alarmRoad
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, AlarmRoad alarmRoad) {
        // Step.1 assemble query conditions to query data
        QueryWrapper<AlarmRoad> queryWrapper = QueryGenerator.initQueryWrapper(alarmRoad, request.getParameterMap());
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        //Step.2 get export data
        List<AlarmRoad> queryList = alarmRoadService.list(queryWrapper);
        // filter selected data
        String selections = request.getParameter("selections");
        List<AlarmRoad> alarmRoadList = new ArrayList<>();
        if (oConvertUtils.isEmpty(selections)) {
            alarmRoadList = queryList;
        } else {
            List<String> selectionList = Arrays.asList(selections.split(","));
            alarmRoadList = queryList.stream().filter(item -> selectionList.contains(item.getId())).collect(Collectors.toList());
        }
        // Step.3 assemble the page list
        List<AlarmRoadPage> pageList = new ArrayList<AlarmRoadPage>();
        for (AlarmRoad main : alarmRoadList) {
            AlarmRoadPage vo = new AlarmRoadPage();
            BeanUtils.copyProperties(main, vo);
            List<Alarm> alarmList = alarmService.selectByMainId(main.getId());
            vo.setAlarmList(alarmList);
            pageList.add(vo);
        }
        // Step.4 AutoPoi - export to excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, "告警道路表列表");
        mv.addObject(NormalExcelConstants.CLASS, AlarmRoadPage.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("告警道路表数据", "导出人:" + sysUser.getRealname(), "告警道路表"));
        mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
        return mv;
    }

    /**
     * import data through excel
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            // get uploaded file object
            MultipartFile file = entity.getValue();
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                List<AlarmRoadPage> list = ExcelImportUtil.importExcel(file.getInputStream(), AlarmRoadPage.class, params);
                for (AlarmRoadPage page : list) {
                    AlarmRoad po = new AlarmRoad();
                    BeanUtils.copyProperties(page, po);
                    alarmRoadService.saveMain(po, page.getAlarmList());
                }
                return Result.ok("The file is imported successfully! Number of data rows:" + list.size());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Result.error("文件导入失败:" + e.getMessage());
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return Result.ok("文件导入失败！");
    }

    /**
     * Query road section warning data based on conditions
     * @param params
     * @return
     */
    @AutoLog(value = "告警道路表-根据条件查询路段告警数据")
    @ApiOperation(value = "告警道路表-根据条件查询路段告警数据", notes = "告警道路表-根据条件查询路段告警数据")
    @PostMapping(value = "/queryListByParams")
    public List<AlarmRoadModel> queryListByParams(@RequestBody Map<String, Object> params) {
        QueryWrapper query=new QueryWrapper();
        query.in("hw_id",(List)params.get("hwIds"));
        query.gt("starttime",params.get("starttime"));
        if ("asc".equals(params.get("order"))){
            query.orderByAsc(params.get("sort"));
        }else if ("desc".equals(params.get("order"))){
            query.orderByDesc(params.get("sort"));
        }
        List<AlarmRoad> alarmRoads=alarmRoadService.list(query);
        List<AlarmRoadModel> alarmRoadModels = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(alarmRoads)) {
            for (AlarmRoad alarmRoad : alarmRoads) {
                AlarmRoadModel alarmRoadModel = new AlarmRoadModel();
                BeanUtils.copyProperties(alarmRoad, alarmRoadModel);
                alarmRoadModel.setHwName(highWayUtil.getById(alarmRoadModel.getHwId()).getName());
                alarmRoadModels.add(alarmRoadModel);
            }
        }
        return alarmRoadModels;
    }

    /**
     * Statistics calendar data by date and alarm level
     * @param hwId
     * @param hwIds
     * @return
     */
    @GetMapping("/queryCalendarAlarm")
    public @ResponseBody JSONArray queryCalendarAlarm(String hwId,String hwIds)
    {
        //Accept incoming hwid and hwid
        Map<String,Object> map = new HashMap<>();
        if(hwId != null && StringUtils.isNotEmpty(hwId))
            map.put("hwId", hwId);
        if(hwIds != null && StringUtils.isNotEmpty(hwIds))
            map.put("hwIds", hwIds.split(","));

        //Obtain the road section id according to user permissions
        if(StringUtils.isEmpty(hwIds) && StringUtils.isEmpty(hwId))
        {
            LoginUser user = ShiroUtils.getUser();
            if (user != null && user.getId() != null && !user.isAdmin())
            {
                try{
                    List<String> ids = user.getHwIds();
                    String hwIdsS = StringUtils.join(ids.toArray(), ",");
                    map.put("hwIds", hwIdsS.split(","));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        //Statistics calendar data by date and alarm level
        List<AlarmRoad> alarmRoads = alarmRoadService.queryCalendarAlarm(map);
        JSONArray jsonArray = new JSONArray();
        for (AlarmRoad alarmRoad:alarmRoads)
        {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ids",alarmRoad.getId());
            jsonObject.put("start",DateUtils.format(alarmRoad.getStarttime(),"yyyy-MM-dd"));
            jsonObject.put("title",alarmRoad.getUpdateBy());
            jsonObject.put("level",alarmRoad.getAlarmLevel());
            switch (alarmRoad.getAlarmLevel())
            {
                case "1":
                    jsonObject.put("color","#d31851");
                    break;
                case "2":
                    jsonObject.put("color","#ff8400");
                    break;
                case "3":
                    jsonObject.put("color","#fff600");
                    break;
                case "4":
                    jsonObject.put("color","#46a5ff");
                    break;
            }
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    @AutoLog("告警日历查询")
    @ApiOperation(value = "告警日历查询-查询所有", notes = "查询所有告警信息")
    @GetMapping("/queryAlarmCalenderData")
    public Result<?> queryAlarmCalenderData(@RequestParam(value = "hwid", required = false, defaultValue = "") String hwid,
                                            @RequestParam(value = "start", required = false) String starttime,
                                            @RequestParam(value = "end", required = false) String endtime,
                                            HttpServletRequest request) {
        //Return the result set, define an initial capacity, avoid repeated expansion
        List<Map<String, Object>> resultList = new ArrayList<>(25);
        try {
            //Query child node
            List<String> ids = highwayApi.queryChildNodes(hwid);
            ids.add(hwid);
            List<Alarm> alarmRoadList = new ArrayList<>();
            for(String id:ids){
                //query alarm calendar information
                alarmRoadList.addAll(alarmService.queryAlarmCalenderData(id, starttime, endtime));
            }
            //The front-end calendar component will be automatically adjusted to the front-end component display according to the start-end time
            for (Alarm alarm : alarmRoadList) {
                Map<String, Object> resMap = new HashMap<>();
                resMap.put("start", alarm.getBegintime());
                /*resMap.put("end", alarm.getEndtime());*/
                resMap.put("display", "block");
                //alarmLevelTitle is the title that the front-end component needs to display
                String alarmLevelTitle = "";
                //Temporary processing method, for alarms with a level of null value, we set it to the lowest level (the lowest level in the judgment logic)
                String level = alarm.getLevel();
                if (oConvertUtils.isEmpty(level)) {
                    level = "0";
                }
                //For sorting, let me explain: 1. The level field cannot be changed, the front end needs to pass the level to the child page 2. The sort requires a field, but the front-end calendar component is in ascending order
                switch (level) {
                    case "4":
						alarmLevelTitle = "特级(0~30m)："+alarm.getUpdateBy()+"个";
						resMap.put("id",4);
						resMap.put("level",Integer.valueOf(level));
						resMap.put("orderFlag",-Integer.valueOf(level));
						resMap.put("color","#ff0000");
                        break;
                    case "3":
                        alarmLevelTitle = "1级(30~50m)：" + alarm.getUpdateBy() + "个";
                        resMap.put("id", 3);
                        resMap.put("orderFlag",4-Integer.valueOf(level));
                        resMap.put("level", Integer.valueOf(level));
                        resMap.put("color", "#2c6739");
                        break;
                    case "2":
                        alarmLevelTitle = "2级(50~100m)：" + alarm.getUpdateBy() + "个";
                        resMap.put("color", "#ffd700");
                        resMap.put("orderFlag",4-Integer.valueOf(level));
                        resMap.put("level", Integer.valueOf(level));
                        resMap.put("id", 2);
                        break;
                    case "1":
                        alarmLevelTitle = "3级(100m~200m)：" + alarm.getUpdateBy() + "个";
                        resMap.put("color", "#ff9300");
                        resMap.put("orderFlag",4-Integer.valueOf(level));
                        resMap.put("id", 1);
                        resMap.put("level", Integer.valueOf(level));
                        break;
                    case "0":
						/*alarmLevelTitle = "4级(150~200m)："+alarm.getUpdateBy()+"个";
						resMap.put("color","green");
						resMap.put("level",Integer.valueOf(level));
						resMap.put("id",4);*/
                        break;
                    default:
                        break;
                }
                if (StringUtils.isEmpty(alarmLevelTitle)) {
                    continue;
                }
                resMap.put("title", alarmLevelTitle);
                resultList.add(resMap);
            }
        } catch (Exception exception) {
            return Result.error(exception.getMessage());
        }
        return Result.ok(resultList);
    }

    @AutoLog("告警查询")
    @ApiOperation(value = "告警查询-查询某一天某等级", notes = "查询某一天某等级告警信息")
    @GetMapping("/queryDayAlarm/")
    public Result<?> queryDayAlarm(@RequestParam("start") String start, @RequestParam("level") String level) throws Exception {
        List<Alarm> alarmRoadList = null;
        IPage<Alarm> page=new Page();
        try {
            alarmRoadList = alarmService.queryDayAlarm(start, level);
            page.setRecords(alarmRoadList);
//            page.setSize(1);
//            page.setTotal(alarmRoadList.size());
        } catch (Exception exception) {
            exception.printStackTrace();
            Result.error("查询失败");
        }
        return Result.ok(page);
    }

    @AutoLog("告警详情")
    @ApiOperation(value = "告警详情-查询告警详情信息", notes = "查询告警详情信息")
    @GetMapping("/queryAlarmById")
    public Result<?> queryAlarmById(@RequestParam("id") String id, HttpServletRequest request) throws Exception {
        Alarm alarm = new Alarm();
        alarm.setId(id);
        IPage<Alarm> pageList = null;
        try {
            QueryWrapper<Alarm> queryWrapper = QueryGenerator.initQueryWrapper(alarm, request.getParameterMap());
            Page<Alarm> page = new Page<>(1, Integer.MAX_VALUE);
            pageList = alarmService.page(page, queryWrapper);
        } catch (Exception exception) {
            exception.printStackTrace();
            Result.error("查询失败");
        }
        return Result.ok(pageList);
    }

    /**
     * Query road section alarm information according to the user bound road section
     * @return
     */
    @ResponseBody
    @GetMapping("/queryAlarmRoadByUser")
    public String queryAlarmRoadByUser() {
        List<String> hwIds = null;
        QueryWrapper<AlarmRoad> warpper = new QueryWrapper<AlarmRoad>();
        if (!ShiroUtils.isAdmin()) {
            hwIds = ShiroUtils.getUser().getHwIds();
            if (CollectionUtils.isEmpty(hwIds)){
                return "";
            }
            warpper.in("hw_id", hwIds);
        }
        List<AlarmRoad> alarmRoadDoList = alarmRoadService.list(warpper);
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = null;
        for (AlarmRoad ar : alarmRoadDoList) {
            jsonObject = new JSONObject();
            jsonObject.put("roadAlarmId", ar.getId());
            jsonObject.put("hwId", ar.getHwId());
            jsonObject.put("hwName", highWayUtil.getById(ar.getHwId()).getName());
            jsonObject.put("alarmLevel", ar.getAlarmLevel());
            jsonObject.put("alarmLevelDesc", AlarmLevelUtil.getLevelDesc(Integer.parseInt(ar.getAlarmLevel())));
            jsonObject.put("mindistanceNow", ar.getMindistanceNow());
            jsonObject.put("mindistanceHis", ar.getMindistanceHis());
            jsonObject.put("starttime", DateUtils.format(ar.getStarttime(), "yyyy-MM-dd HH:mm:ss"));
            jsonObject.put("imgpath", ar.getImgpath());
            jsonObject.put("imgtime", DateUtils.format(ar.getImgtime(), "yyyy-MM-dd HH:mm:ss"));
            jsonArray.add(jsonObject);
        }
        return jsonArray.toJSONString();
    }

    /**
     * 首页-年告警统计接口
     * @param nowDate 当前时间（yyyy-MM-dd）, 类型: java.util.Date
     * @return Result
     */
    @GetMapping("/queryAnnualAlarmData")
    @ApiOperation("首页-年告警统计接口")
    public @ResponseBody Result<?> queryTheNumberOfAnnualAlarms(@RequestParam String nowDate) throws ParseException {
        //0. 获取登录用户信息，进行权限控制
        LoginUser user = ShiroUtils.getUser();
        if (user == null) {
            return Result.error("登录过期，请重新登录");
        }
        Map<String, Object> params = new HashMap<>();
        List<String> userHwIds = user.getHwIds();
        if (!user.isAdmin()) {
            String hwIds = String.join(",", userHwIds);
            params.put("hwIds", hwIds);
        }
        params.put("nowDate",DateUtils.parseDate(nowDate,"yyyy-MM-dd"));
        //1. 查询年告警统计信息（表：zc_alarm_road 条件：nowDate 分组：road_alarm_type）
        List<AlarmRoad> alarmRoadList = alarmRoadService.queryTheNumberOfAnnualAlarms(params);
        //2 .查询路段告警类型数据字典
        List<DictModel> dictItems = sysBaseRemoteApi.queryDictItemsByCode("section_warning_type");
        //3. 处理数据
        List<AlarmRoad> list = new ArrayList<>();
        Map<String,AlarmRoad> roadMap = new HashMap<>();
        for(AlarmRoad alarmRoad:alarmRoadList){
            roadMap.put(alarmRoad.getRoadAlarmType(),alarmRoad);
        }
        for(DictModel dictItem:dictItems){
            AlarmRoad alarmRoad = null;
            if(roadMap.containsKey(dictItem.getValue())){
                alarmRoad = roadMap.get(dictItem.getValue());
                String alarmTypeText = sysBaseRemoteApi.queryDictTextByKey("section_warning_type",alarmRoad.getRoadAlarmType());
                alarmRoad.setRoadAlarmType(alarmTypeText);
                list.add(roadMap.get(dictItem.getValue()));
            }else{
                alarmRoad = new AlarmRoad();
                alarmRoad.setRoadAlarmType(dictItem.getText());
                alarmRoad.setCount(0);
                list.add(alarmRoad);
            }
        }
        //4. 返回
        return Result.ok(list);
    }

}
