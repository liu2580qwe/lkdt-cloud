package org.lkdt.modules.weixin.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.api.R;
import org.lkdt.common.api.vo.Result;
import org.lkdt.common.util.StringUtils;
import org.lkdt.modules.weixin.entity.WxUser;
import org.lkdt.modules.weixin.mapper.WxUserMapper;
import org.lkdt.modules.weixin.service.IWxUserService;
import org.lkdt.modules.weixin.utils.wx.WxPushUtil;
import org.lkdt.modules.weixin.utils.wx.WxUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 微信用户管理
 * @Author: jeecg-boot
 * @Date:   2021-04-23
 * @Version: V1.0
 */
@Service
public class WxUserServiceImpl extends ServiceImpl<WxUserMapper, WxUser> implements IWxUserService {

    @Autowired
    private WxUserMapper wxUserMapper;

    @Override
    public Result<?> getPubOpenIdByMinCode(String minCode) {
        try{
            JSONObject jsonObject = WxUtil.openIdSessionKey(minCode);
            String minOpenId = jsonObject.getString("openid");
            String unionid = jsonObject.getString("unionid");

            WxUser wxUserDO = wxUserMapper.getByUnionId(unionid);
            if(wxUserDO == null) {
                wxUserDO = wxUserMapper.getByMinOpenId(minOpenId);
            }
//            wxPushUtil.putOpenid_Code(wxUserDO.getOpenid(), minCode);
            Map<String,String> map = new HashMap<>();
            map.put("pubOpenId",wxUserDO.getOpenid());
            return Result.ok(map);
        } catch (Exception e){
            log.error("获取微信公众号openid异常", e);
        }
        return Result.error("获取微信公众号openid失败");
    }

    /**
     * 小程序获取用户信息
     * @param code
     * @param encryptedData
     * @param iv
     * @return
     */
    @Override
    public Result<?> getUserInfo(String code, String encryptedData, String iv) {
        try{
            JSONObject os = WxUtil.openIdSessionKey(code);
            JSONObject jsonObject = WxUtil.getUserInfo(encryptedData,os.getString("session_key"),iv);
            log.error("授权获取用户敏感信息【getPubOpenId】:"+jsonObject);
            WxUser wxUser = wxUserMapper.getByUnionId(jsonObject.getString("unionId"));
            try{
                wxUser.setMinopenid(jsonObject.getString("openId"));
                wxUserMapper.updateById(wxUser);
            } catch (Exception e){
                log.error("更新minopenid异常",e);
            }
            Map<String, String> resultMap = new HashMap<>();
            resultMap.put("openid",wxUser.getOpenid());
            resultMap.put("unionId", wxUser.getUnionid());
            resultMap.put("phone",wxUser.getPhone());
            return Result.ok(resultMap);
        } catch (Exception e){
            log.error("小程序获取公众号openid异常：", e);
        }
        return Result.error("小程序获取公众号openid失败");
    }

    /**
     * 授权获取手机号
     * @param pubOpenId 公众号openid
     * @param code
     * @param encryptedData
     * @param iv
     * @return
     */
    @Override
    public Result<?> phoneNumber(String pubOpenId, String code, String encryptedData, String iv) {
        try{
            JSONObject os = WxUtil.openIdSessionKey(code);
            //String openId = os.getString("openid");//小程序openid
            JSONObject jsonObject = WxUtil.getUserInfo(encryptedData,os.getString("session_key"),iv);
            log.error("授权获取用户敏感信息【phoneNumber】:"+jsonObject);
            if(!StringUtils.isEmpty(pubOpenId)){
                WxUser wxUser = wxUserMapper.getByMinOpenId(pubOpenId);
                String phoneNumber = jsonObject.getString("phoneNumber");
                wxUser.setPhone(phoneNumber);
                //入库
                if(wxUserMapper.updateById(wxUser) > 0){
                    return Result.ok();
                }
            }
        } catch (Exception e){
            log.error("手机号入库异常：", e);
        }
        return Result.error("手机号入库失败");
    }

    /**
     * 获取授权码 0：未授权，1：已授权
     * @param pubOpenId
     * @return
     */
    @Override
    public String getAuthStatus(String pubOpenId) {
        try{
            WxUser wxUser = getById(pubOpenId);
            if(wxUser == null || StringUtils.isEmpty(wxUser.getPhone()) || StringUtils.isEmpty(wxUser.getMinopenid())){
                return "0";
            }
            return "1";
        } catch (Exception e){
            log.error("获取授权码异常",e);
        }
        return "0";
    }
}
