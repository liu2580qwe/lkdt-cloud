package org.lkdt.modules.weixin.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.lkdt.common.api.vo.Result;

/**
 * 微信server
 * @date 2019-12-09
 */
public interface IWxService {
    /**
     * 获取路段摄像头
     * @return
     */
    String getEquWWxxByHw();

    /**
     * 获取路段信息
     * @param hwId
     * @return
     */
    String getHighways(Long hwId);

    /**
     * 保存路段信息
     * @param obj
     * @return
     */
    Result<?> saveHwInfo(JSONObject obj);

    /**
     * 获取openid
     * @param obj
     * @return
     */
    String getOpenFid(JSONObject obj);

    /**
     * 获取config
     * @param obj
     * @return
     */
    String getConfig(JSONObject obj);

    /**
     * 获取用户关注路段
     * @param openid
     * @return
     */
    String getHwIdsByOpenid();

    /**
     * 获取用户关注路段-菜单结构
     * @param openid
     * @return
     */
    String getTreeHwIdsByOpenid();

    /**
     * 删除关联路段
     * @param obj
     * @return
     */
    Result<?> deleteHwOp(JSONObject obj);

    /**
     * 获取所有路段摄像头数据
     * @param openid
     * @return
     */
    String getEquWWxxAll();

    /**
     * 告警雾霾，散雾
     * @return
     */
    String getWXAlarm();

	String getEquWWxxByHwAbn();

    JSONArray getSubscribeRoad();

//    JSONArray getSubscribeRoad(String openid);
}
