package org.lkdt.modules.weixin.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.lkdt.common.api.vo.Result;
import org.lkdt.common.system.vo.HighwayModel;
import org.lkdt.common.util.DateUtils;
import org.lkdt.common.util.HighWayUtil;
import org.lkdt.common.util.ShiroUtils;
import org.lkdt.common.util.StringUtils;
import org.lkdt.modules.api.SysBaseRemoteApi;
import org.lkdt.modules.fog.calculator.FcFactory;
import org.lkdt.modules.fog.calculator.FogCalRedis;
import org.lkdt.modules.fog.calculator.FogCalculator;
import org.lkdt.modules.fog.calculator.FogSpaceCal;
import org.lkdt.modules.weixin.entity.Tree;
import org.lkdt.modules.weixin.entity.WxSubscribe;
import org.lkdt.modules.weixin.service.IWxService;
import org.lkdt.modules.weixin.service.IWxSubscribeService;
import org.lkdt.modules.weixin.utils.BuildTree;
import org.lkdt.modules.weixin.utils.WeChatMapUtil;
import org.lkdt.modules.weixin.utils.wx.WxPushUtil;
import org.lkdt.modules.weixin.utils.wx.WxUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 微信server
 *
 * @date 2019-12-09
 */
@Slf4j
@Service
public class WxServiceImpl implements IWxService {

    @Autowired
    IWxSubscribeService wxSubscribeService;

    @Autowired
    FcFactory fcFactory;

    @Autowired
    WxPushUtil wxPushUtil;

    @Autowired
    HighWayUtil highWayUtil;

    @Autowired
    WeChatMapUtil weChatMapUtil;

    @Autowired
    FogCalRedis fogCalRedis;

    @Autowired
    private SysBaseRemoteApi sysBaseRemoteApi;

    /**
     * 获取已关注路段摄像头
     *
     * @return
     */
    @Override
    public String getEquWWxxByHw() {
        try {
            //openId判空
            List<FogCalculator> fogCalculatorList = fcFactory.getCalculators();
            JSONArray jsonArray = new JSONArray();
            FogCalculator firstCal = null;
            for (FogCalculator e : fogCalculatorList) {
                //验证经纬度合法性 过滤无数据摄像头
                if (e == null) {
                    continue;
                }

                if (fogCalRedis.getPrevious(e) == null || !StringUtils.equals(e.getEquipment().getHwId(), fogCalRedis.getPrevious(e).getEquipment().getHwId())) {
                    firstCal = e;
                }
                if (e.getDistance() == null) {
                    continue;
                }
                if (e.isAbn()) {
                    continue;
                }
                if (e.getImgtime() == null || System.currentTimeMillis() - e.getImgtime().getTime() > 60 * 60 * 1000) {
                    continue;
                }
                if (StringUtils.isEmpty(e.getImgpath())) {
                    continue;
                }
                if (StringUtils.isEmpty(e.getEquipment().getLon()) || StringUtils.isEmpty(e.getEquipment().getLat())) {
                    continue;
                }
                int space = 0;
                if (e !=  firstCal && StringUtils.isNotEmpty(firstCal.getEquipment().getLon()) && StringUtils.isNotEmpty(firstCal.getEquipment().getLat())) {
                    space = (int) FogSpaceCal.getDistanceByLonLat(Double.valueOf(firstCal.getEquipment().getLon()),
                            Double.valueOf(firstCal.getEquipment().getLat()),
                            Double.valueOf(e.getEquipment().getLon()),
                            Double.valueOf(e.getEquipment().getLat())) / 1000;
                }
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("lnglat", new String[]{e.getEquipment().getLon(), e.getEquipment().getLat()});
                jsonObject.put("lng", e.getEquipment().getLon());
                jsonObject.put("lat", e.getEquipment().getLat());
                jsonObject.put("epId", e.getEpId());
                jsonObject.put("equCode", e.getEquipment().getEquCode());
                jsonObject.put("equName", e.getEquipment().getEquName());
                jsonObject.put("equLocation", e.getEquipment().getEquLocation());
                jsonObject.put("state", e.getEquipment().getState());
                jsonObject.put("space", space);
                jsonArray.add(jsonObject);
            }
            return jsonArray.toJSONString();
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONArray().toJSONString();
        }
    }

    /**
     * 获取已关注路段异常摄像头
     *
     * @return
     */
    @Override
    public String getEquWWxxByHwAbn() {
        try {
            //openId判空
            //处理摄像头数据
            List<FogCalculator> fogCalculatorList = fcFactory.getCalculators();
            JSONArray jsonArray = new JSONArray();
            Long now = System.currentTimeMillis();
            try {
                for (FogCalculator calculator : fogCalculatorList) {
                    FogCalculator fogCalculator = fcFactory.getCalculator(calculator.getEpId());
                    if (fogCalculator == null) {
                        continue;
                    }
                    if (fogCalculator.getDistance() == null) {
                        continue;
                    }
                    if (fogCalculator.getImgtime() == null || now - fogCalculator.getImgtime().getTime() > 60 * 60 * 1000) {
                        continue;
                    }
                    if (fogCalculator.isAbn()) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("lnglat", new String[]{fogCalculator.getEquipment().getLon(), fogCalculator.getEquipment().getLat()});
                        jsonObject.put("lng", fogCalculator.getEquipment().getLon());
                        jsonObject.put("lat", fogCalculator.getEquipment().getLat());
                        jsonObject.put("epId", fogCalculator.getEpId());
                        jsonObject.put("equCode", fogCalculator.getEquipment().getEquCode());
                        jsonObject.put("equName", fogCalculator.getEquipment().getEquName());
                        jsonObject.put("equLocation", fogCalculator.getEquipment().getEquLocation());
                        jsonObject.put("state", calculator.getEquipment().getState());
                        jsonObject.put("stateDesc", sysBaseRemoteApi.queryDictTextByKey("equipment_state",fogCalculator.getEquipment().getState()));
                        jsonArray.add(jsonObject);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonArray.toJSONString();
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONArray().toJSONString();
        }
    }


    /**
     * 获取所有路段摄像头数据
     *
     * @return
     */
    @Override
    public String getEquWWxxAll() {
        try {
            //openId判空
            List<FogCalculator> fogCalculatorList = fcFactory.getCalList(true);
            //返回
            JSONArray jsonArray = new JSONArray();
            FogCalculator firstCal = null;
            for (FogCalculator e : fogCalculatorList) {
                //验证经纬度合法性 过滤无数据摄像头
                if (fogCalRedis.getPrevious(e) == null || !StringUtils.equals(e.getEquipment().getHwId(), fogCalRedis.getPrevious(e).getEquipment().getHwId())) {
                    firstCal = e;
                }
                if (e.getImgtime() == null || new Date().getTime() - e.getImgtime().getTime() > 60 * 60 * 1000) {
                    continue;
                }
                if (StringUtils.isEmpty(e.getEquipment().getLon()) || StringUtils.isEmpty(e.getEquipment().getLat()) ||
                        StringUtils.isEmpty(e.getImgpath())) {
                    continue;
                }
                int space = 0;
                if (e !=  firstCal && StringUtils.isNotEmpty(firstCal.getEquipment().getLon()) && StringUtils.isNotEmpty(firstCal.getEquipment().getLat())) {
                    space = (int) FogSpaceCal.getDistanceByLonLat(Double.valueOf(firstCal.getEquipment().getLon()),
                            Double.valueOf(firstCal.getEquipment().getLat()),
                            Double.valueOf(e.getEquipment().getLon()),
                            Double.valueOf(e.getEquipment().getLat())) / 1000;
                }
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("lnglat", new String[]{e.getEquipment().getLon(), e.getEquipment().getLat()});
                jsonObject.put("lat", e.getEquipment().getLat());
                jsonObject.put("lng", e.getEquipment().getLon());
                jsonObject.put("epId", e.getEpId());
                jsonObject.put("equCode", e.getEquipment().getEquCode());
                jsonObject.put("equName", e.getEquipment().getEquName());
                jsonObject.put("equLocation", e.getEquipment().getEquLocation());
                jsonObject.put("space",space);
                jsonArray.add(jsonObject);
            }
            return jsonArray.toJSONString();
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONArray().toJSONString();
        }
    }

    /**
     * 告警雾霾，散雾
     *
     * @return
     */
    @Override
    public String getWXAlarm() {
        List<FogCalculator> fogCals = fcFactory.getCalculators();
        JSONArray alarms = new JSONArray();
        JSONArray ends = new JSONArray();
        JSONObject wxAlarm = new JSONObject();
        try {
            for (FogCalculator fogCal : fogCals) {
                if (fogCal == null) {
                    continue;
                }
                if (StringUtils.isEmpty(fogCal.getEquipment().getLon()) || StringUtils.isEmpty(fogCal.getEquipment().getLat())) {
                    continue;
                }
                try {
                    //雾霾
                    if ((fogCal.getDistance() != null) && fogCal.isFogNow()) {
                        JSONObject alarm = new JSONObject();
                        alarm.put("epId", fogCal.getEpId());
                        alarm.put("equName", fogCal.getEquipment().getEquName());
                        alarm.put("equLocation", fogCal.getEquipment().getEquLocation());
                        alarm.put("lat", fogCal.getEquipment().getLat());
                        alarm.put("lng", fogCal.getEquipment().getLon());
                        alarm.put("lnglat", new String[]{fogCal.getEquipment().getLon(), fogCal.getEquipment().getLat()});
                        alarm.put("distance", fogCal.getDistance());
                        alarm.put("alarmStartTime", DateUtils.format(fogCal.getAlarmStartTime(), DateUtils.DATE_TIME_PATTERN));
                        alarms.add(alarm);
                    } else if (fogCal.getEquipment().getEquName().indexOf("路段") > -1 && fogCal.isFogNow()) {
                        JSONObject alarm = new JSONObject();
                        alarm.put("epId", fogCal.getEpId());
                        alarm.put("equName", fogCal.getEquipment().getEquName());
                        alarm.put("equLocation", fogCal.getEquipment().getEquLocation());
                        alarm.put("lat", fogCal.getEquipment().getLat());
                        alarm.put("lng", fogCal.getEquipment().getLon());
                        alarm.put("lnglat", new String[]{fogCal.getEquipment().getLon(), fogCal.getEquipment().getLat()});
                        alarm.put("distance", fogCal.getSmDistance());
                        alarm.put("alarmStartTime", DateUtils.format(fogCal.getAlarmStartTime(), DateUtils.DATE_TIME_PATTERN));
                        alarms.add(alarm);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    //散雾
                    if (StringUtils.isEmpty(fogCal.getAlarmId()) && fogCal.getAlarmEndTime() != null) {
                        if (new Date().getTime() - fogCal.getAlarmEndTime().getTime() < 60 * 60 * 1000) {
                            JSONObject end = new JSONObject();
                            end.put("epId", fogCal.getEpId());
                            end.put("equLocation", fogCal.getEquipment().getEquLocation());
                            end.put("equName", fogCal.getEquipment().getEquName());
                            end.put("lat", fogCal.getEquipment().getLat());
                            end.put("lng", fogCal.getEquipment().getLon());
                            end.put("lnglat", new String[]{fogCal.getEquipment().getLon(), fogCal.getEquipment().getLat()});
                            end.put("distance", fogCal.getDistance());
                            end.put("alarmEndTime", DateUtils.format(fogCal.getAlarmEndTime(), DateUtils.DATE_TIME_PATTERN));
                            ends.add(end);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            wxAlarm.put("alarms", alarms);
            wxAlarm.put("ends", ends);
            return wxAlarm.toJSONString();
        } catch (Exception e) {
            e.printStackTrace();
            return wxAlarm.toJSONString();
        }
    }

    /**
     * 获取路段信息
     *
     * @param hwId
     * @return
     */
    @Override
    public String getHighways(Long hwId) {
        JSONObject jsonObject = new JSONObject();
        try {
            List<Tree<HighwayModel>> trees = new ArrayList<>();
            List<HighwayModel> HighwayModelList = highWayUtil.getAllList();
            for (HighwayModel highway : HighwayModelList) {
                Tree<HighwayModel> tree = new Tree<>();
                tree.setId(String.valueOf(highway.getId()));
                tree.setText(highway.getName());
                tree.setParentId(highway.getPid());
                Map<String, Object> state = new HashMap<>(16);
                state.put("opened", true);
                state.put("detail", highway.getDetail());
                state.put("nameDetail", highway.getName() + "-" + highway.getDetail());
                tree.setState(state);
                trees.add(tree);
            }
            // 默认顶级菜单为０，根据数据库实际情况调整
            Tree<HighwayModel> t = BuildTree.build(trees, "江苏省高速");
            jsonObject.put("highways", t.getChildren());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject.toJSONString();
    }

    /**
     * 保存路段信息
     *
     * @param obj
     * @return
     */
    @Override
    @Transactional
    public Result<?> saveHwInfo(JSONObject obj) {
        //获取参数
        try {
            String openId = obj.getString("openid");
            JSONArray jsonArrayDel = obj.getJSONArray("delHwIds");
            Map<String, Object> map = new HashMap<>();
            map.put("openid", openId);
            for (int i = 0; i < jsonArrayDel.size(); i++) {
                String hwId = jsonArrayDel.getJSONObject(i).getString("hwId");
                map.put("hwId", hwId);
                //验证删除
                wxSubscribeService.removeByOpenIdAndHwId(map);
            }
            JSONArray jsonArray = obj.getJSONArray("hwIds");
            for (int i = 0; i < jsonArray.size(); i++) {
                String hwId = jsonArray.getJSONObject(i).getString("hwId");
                WxSubscribe wxSubscribeDO = new WxSubscribe();
                wxSubscribeDO.setCreateTime(new Date());
                wxSubscribeDO.setOpenid(openId);
                wxSubscribeDO.setSubscribeHighway(hwId);
                wxSubscribeDO.setTousername(WxUtil.TOUSERNAME);
                map.put("hwId", hwId);
                //验证保存
                if (StringUtils.isNotEmpty(openId) && StringUtils.isNotEmpty(hwId)) {
                    wxSubscribeService.removeByOpenIdAndHwId(map);
                    wxSubscribeService.save(wxSubscribeDO);
                }
            }
            return Result.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("保存失败");
        }
    }

    /**
     * 获取openid
     *
     * @param obj
     * @return
     */
    @Override
    public String getOpenFid(JSONObject obj) {
        JSONObject jsonObject = new JSONObject();
        //获取openId
        String openId = WxUtil.getOpenId(String.valueOf(obj.get("code")));
        //openId判空
        if (StringUtils.isEmpty(openId)) {
            return jsonObject.toJSONString();
        }
        jsonObject.put("openid", openId);
        try {
            //配置 appId timestamp nonceStr signature
            JSONObject config = weChatMapUtil.generateWxTicket(wxPushUtil.getJsapi_ticket(), String.valueOf(obj.get("url")));
            jsonObject.put("config", config);
            JSONObject userInfo = JSONObject.parseObject(wxPushUtil.getUserInfo(openId));
            jsonObject.put("userInfo", userInfo);
        } catch (Exception e) {
            log.error("获取openid异常service-imp-WXServiceImpl", e);
        }
        return jsonObject.toJSONString();
    }

    /**
     * 获取config
     *
     * @param obj
     * @return
     */
    @Override
    public String getConfig(JSONObject obj) {
        JSONObject jsonObject = new JSONObject();
        //获取openId
        String openId = String.valueOf(obj.get("openId"));
        //openId判空
        if (StringUtils.isEmpty(openId)) {
            return jsonObject.toJSONString();
        }
        jsonObject.put("openid", openId);
        try {
            //配置 appId timestamp nonceStr signature
            JSONObject config = weChatMapUtil.generateWxTicket(wxPushUtil.getJsapi_ticket(), String.valueOf(obj.get("url")));
            jsonObject.put("config", config);
            JSONObject userInfo = JSONObject.parseObject(wxPushUtil.getUserInfo(openId));
            jsonObject.put("userInfo", userInfo);
        } catch (Exception e) {
            log.error("获取config异常service-imp-WXServiceImpl", e);
        }
        return jsonObject.toJSONString();
    }

    /**
     * 获取用户关注路段
     *
     * @return
     */
    @Override
    public String getHwIdsByOpenid() {
        JSONObject jsonObject = new JSONObject();
        //查询
        JSONArray jsonArray = new JSONArray();
        List<String> hwIds = ShiroUtils.getUser().getHwIds();
        //格式化
        for (String hwId : hwIds) {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("hwId", hwId);
            jsonArray.add(jsonObj);
        }
        if (jsonArray.size() > 0) {
            jsonObject.put("hwIds", jsonArray);
        }
        return jsonObject.toJSONString();
    }

    /**
     * 获取用户关注路段-菜单结构
     *
     * @return
     */
    @Override
    public String getTreeHwIdsByOpenid() {
        JSONObject jsonObject = new JSONObject();
        try {
            List<Tree<HighwayModel>> trees = new ArrayList<Tree<HighwayModel>>();
            Map queryMap = new HashMap<String, Object>();
            //已关注路段
            List<String> hwIds = ShiroUtils.getUser().getHwIds();
            //已添加
            List<String> added = new ArrayList<>();
            for (String hwId : hwIds) {
                HighwayModel hwDO = highWayUtil.getById(hwId);
                HighwayModel hwDOP = highWayUtil.getById(hwDO.getPid());

                //子节点
                Tree<HighwayModel> tree = new Tree<>();
                if (!added.contains(String.valueOf(hwDO.getId()))) {
                    tree.setId(String.valueOf(hwDO.getId()));
                    tree.setParentId(hwDO.getPid().toString());
                    tree.setText(hwDO.getName());
                    Map<String, Object> state = new HashMap<>(16);
                    state.put("opened", true);
                    state.put("detail", hwDO.getDetail());
                    state.put("nameDetail", hwDO.getName() + "-" + hwDO.getDetail());
                    tree.setState(state);
                    trees.add(tree);
                    //添加
                    added.add(tree.getId());
                }

                //父节点
                Tree<HighwayModel> tree2 = new Tree<>();
                if (!added.contains(String.valueOf(hwDOP.getId()))) {
                    tree2.setId(String.valueOf(hwDOP.getId()));
                    tree2.setText(hwDOP.getName());
                    tree2.setParentId(hwDOP.getPid());
                    Map<String, Object> state2 = new HashMap<>(16);
                    state2.put("detail", hwDOP.getDetail());
                    state2.put("opened", true);
                    state2.put("nameDetail", hwDOP.getName() + "-" + hwDOP.getDetail());
                    tree2.setState(state2);
                    trees.add(tree2);
                    added.add(tree2.getId());
                }
            }
            // 默认顶级菜单为０，根据数据库实际情况调整
            Tree<HighwayModel> t = BuildTree.buildByTop_1(trees, "江苏省高速");
            jsonObject.put("highways", t.getChildren());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject.toJSONString();
    }

    /**
     * 删除关联路段
     *
     * @param obj
     * @return
     */
    @Override
    public Result<?> deleteHwOp(JSONObject obj) {
        try {
            //获取参数
            String openId = obj.getString("openid");
            String hwId = obj.getString("hwId");
            Map<String, Object> map = new HashMap<>();
            map.put("openid", openId);
            map.put("hwId", hwId);
            //验证删除
            wxSubscribeService.removeByOpenIdAndHwId(map);
            return Result.ok("");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("错误");
        }
    }

    @Override
    public JSONArray getSubscribeRoad() {
        JSONArray j = new JSONArray();
        //获取已关注路段
        List<String> hwIds = ShiroUtils.getUser().getHwIds();
        for (String hwId : hwIds) {
            try {
                JSONObject object = new JSONObject();
                HighwayModel HighwayModel = highWayUtil.getById(hwId);
                object.put("label", HighwayModel.getName());
                object.put("value", HighwayModel.getId());
                j.add(object);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return j;
    }


}
