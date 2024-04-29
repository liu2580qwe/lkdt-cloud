package org.lkdt.modules.weixin.controller;

import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiOperation;
import org.lkdt.common.api.vo.Result;
import org.lkdt.common.constant.CacheConstant;
import org.lkdt.common.constant.CommonConstant;
import org.lkdt.common.system.util.JwtUtil;
import org.lkdt.common.system.vo.LoginUser;
import org.lkdt.common.util.RedisUtil;
import org.lkdt.modules.system.entity.SysUser;
import org.lkdt.modules.weixin.service.IWxSubscribeService;
import org.lkdt.modules.weixin.utils.wx.WxPushUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author HuangJunYao
 * @date 2021/5/13
 */
@Controller
@RequestMapping("/weixin")
public class WxLoginController {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private IWxSubscribeService wxSubscribeService;

    @Autowired
    private WxPushUtil wxPushUtil;

    @ApiOperation("登录接口")
    @ResponseBody
    @RequestMapping(value = "/wxLogin", method = RequestMethod.POST)
    public Result<JSONObject> wxLogin(@RequestParam String openid){
        Result<JSONObject> result = new Result<JSONObject>();
        String username = openid;

//		SysUser sysUser = sysUserService.getUserByName(username);
        SysUser sysUser = new SysUser();
        sysUser.setId(openid);
        sysUser.setUsername(openid);
        sysUser.setPassword(openid);
        //用户登录信息
        wxUserInfo(sysUser, result);
        return result;
    }

    /**
     * 用户信息
     *
     * @param sysUser
     * @param result
     * @return
     */
    private Result<JSONObject> wxUserInfo(SysUser sysUser, Result<JSONObject> result) {
        String syspassword = sysUser.getPassword();
        String username = sysUser.getUsername();
        // 生成token
        String token = JwtUtil.sign(username, syspassword);
        // 设置token缓存有效时间
        redisUtil.set(CommonConstant.PREFIX_USER_TOKEN + username, username);
//		redisUtil.expire(CommonConstant.PREFIX_USER_TOKEN + token, JwtUtil.EXPIRE_TIME*2 / 1000);
        //------------------------------------------------------------------------------------------
        LoginUser vo = new LoginUser();
        BeanUtils.copyProperties(sysUser,vo);
        vo.setPassword(SecureUtil.md5(sysUser.getPassword()));
        if(!vo.isAdmin()){
            List<String> hwIds = wxSubscribeService.getHwIdsByOpenid(username);
            vo.setHwIds(hwIds);
        }
        vo.setStatus(1);
        redisUtil.set(CacheConstant.SYS_USERS_CACHE_JWT +":" +token, vo);
		redisUtil.expire(CacheConstant.SYS_USERS_CACHE_JWT +":" +token, JwtUtil.EXPIRE_TIME*2 / 100);
        //------------------------------------------------------------------------------------------

        // 获取用户部门信息

        JSONObject obj = new JSONObject();
        obj.put("token", token);
        obj.put("userInfo", vo);
        obj.put("wxUserInfo", JSONObject.parseObject(wxPushUtil.getUserInfo(username)));
        result.setResult(obj);
        result.success("登录成功");
        return result;
    }
}
