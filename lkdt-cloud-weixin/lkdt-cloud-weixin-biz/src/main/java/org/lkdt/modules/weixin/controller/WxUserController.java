package org.lkdt.modules.weixin.controller;

import java.text.SimpleDateFormat;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.lkdt.common.api.vo.Result;
import org.lkdt.common.aspect.annotation.AutoLog;
import org.lkdt.common.system.base.controller.CloudController;
import org.lkdt.common.system.query.QueryGenerator;
import org.lkdt.common.util.StringUtils;
import org.lkdt.modules.fog.api.FogApi;
import org.lkdt.modules.fog.calculator.FcFactory;
import org.lkdt.modules.fog.calculator.FogCalculator;
import org.lkdt.modules.fog.entity.AlarmNoticeModel;
import org.lkdt.modules.weixin.entity.WxUser;
import org.lkdt.modules.weixin.service.IWxService;
import org.lkdt.modules.weixin.service.IWxUserService;
import org.lkdt.modules.weixin.utils.wx.WxPushUtil;
import org.lkdt.modules.weixin.utils.wx.WxUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @Description: 微信用户管理
 * @Author: jeecg-boot
 * @Date: 2021-04-24
 * @Version: V1.0
 */
@Api(tags = "微信用户管理")
@RestController
@RequestMapping("/weixin/wxUser")
@Slf4j
public class WxUserController extends CloudController<WxUser, IWxUserService> {
    @Autowired
    private IWxUserService wxUserService;

    @Autowired
    private WxPushUtil wxPushUtil;

    @Autowired
    private IWxService wxService;

    @Autowired
    private FogApi fogApi;

    @Autowired
    private FcFactory fcFactory;


    /**
     * 分页列表查询
     *
     * @param zcWxUser
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "微信用户管理-分页列表查询")
    @ApiOperation(value = "微信用户管理-分页列表查询", notes = "微信用户管理-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(WxUser zcWxUser,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<WxUser> queryWrapper = QueryGenerator.initQueryWrapper(zcWxUser, req.getParameterMap());
        Page<WxUser> page = new Page<WxUser>(pageNo, pageSize);
        IPage<WxUser> pageList = wxUserService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 添加
     *
     * @param zcWxUser
     * @return
     */
    @AutoLog(value = "微信用户管理-添加")
    @ApiOperation(value = "微信用户管理-添加", notes = "微信用户管理-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody WxUser zcWxUser) {
        wxUserService.save(zcWxUser);
        return Result.ok("添加成功！");
    }

    /**
     * 编辑
     *
     * @param wxUser
     * @return
     */
    @AutoLog(value = "微信用户管理-编辑")
    @ApiOperation(value = "微信用户管理-编辑", notes = "微信用户管理-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody WxUser wxUser) {
        WxUser oldWxUser = wxUserService.getById(wxUser.getOpenid());
        boolean updateTag = false;
        // 角色改变
        if (!StringUtils.equals(oldWxUser.getRole(), wxUser.getRole())) {
            String[] openids = new String[1];
            openids[0] = wxUser.getOpenid();
            // 更新标签
            updateTag = WxUtil.updateTag(oldWxUser.getRole(), wxUser.getRole(), openids, wxPushUtil.getAccess_token());
        }
        // 标签更新成功，修改数据库
        if (updateTag) {
            wxUserService.updateById(wxUser);
            wxPushUtil.delOpenids();
            return Result.ok("编辑成功!");
        }
        return Result.ok("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "微信用户管理-通过id删除")
    @ApiOperation(value = "微信用户管理-通过id删除", notes = "微信用户管理-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        wxUserService.removeById(id);
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "微信用户管理-批量删除")
    @ApiOperation(value = "微信用户管理-批量删除", notes = "微信用户管理-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.wxUserService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok("批量删除成功!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "微信用户管理-通过id查询")
    @ApiOperation(value = "微信用户管理-通过id查询", notes = "微信用户管理-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        WxUser zcWxUser = wxUserService.getById(id);
        if (zcWxUser == null) {
            return Result.error("未找到对应数据");
        }
        return Result.ok(zcWxUser);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param zcWxUser
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, WxUser zcWxUser) {
        return super.exportXls(request, zcWxUser, WxUser.class, "微信用户管理");
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
        return super.importExcel(request, response, WxUser.class);
    }

    /***********************微信端 start*************************/

    @AutoLog(value = "获取微信公众号openid")
    @RequestMapping("/getPubOpenIdByMinCode")
    @ResponseBody
    public Result<?> getPubOpenIdByMinCode(String minCode) {
        return wxUserService.getPubOpenIdByMinCode(minCode);
    }


    @ApiOperation(value = "小程序获取用户信息", notes = "")
    @RequestMapping("/getUserInfo")
    @ResponseBody
    public Result<?> getUserInfo(String code, String encryptedData, String iv) {
        return wxUserService.getUserInfo(code, encryptedData, iv);
    }

    @ApiOperation(value = "微信认证手机号", notes = "")
    @RequestMapping("/phoneNumber")
    @ResponseBody
    public Result<?> phoneNumber(String pubOpenId, String code, String encryptedData, String iv) {
        return wxUserService.phoneNumber(pubOpenId, code, encryptedData, iv);
    }

    @ApiOperation(value = "交管审核主页", notes = "")
    @RequestMapping("/policeHome")
    @ResponseBody
    public String mapHome() {
        return WxUtil.LEVEL_CONTROL_HOME;
    }

    @ApiOperation(value = "分级管制入口-测试", notes = "")
    @RequestMapping("/levelControlhomeTest")
    public ModelAndView levelControlhomeTest(String pubOpenId) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("weixin/levelControl/levelControlhomeTest");
        mv.addObject("auth", wxUserService.getAuthStatus(pubOpenId));
        mv.addObject("openid", pubOpenId);
        return mv;
    }

    @ApiOperation(value = "分级管制入口", notes = "")
    @RequestMapping("/levelControlhome")
    public ModelAndView levelControlhome(String pubOpenId) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("weixin/levelControl/levelControlhome");
        mv.addObject("auth", wxUserService.getAuthStatus(pubOpenId));
        mv.addObject("openid", pubOpenId);
        return mv;
    }


    @ApiOperation(value = "分级管制跳转", notes = "")
    @RequestMapping("/levelControl")
    @ResponseBody
    public WxUser levelControl(@RequestBody String body) {
        JSONObject obj = JSONObject.parseObject(body);
        String openid = obj.getString("openid");
        WxUser wxUser = wxUserService.getById(openid);
        if (wxUser != null) {
            return wxUser;
        } else {
            return null;
        }

    }

//	@ApiOperation(value = "重定向绑定警号页", notes = "")
//	@RequestMapping("/bindTrafficPoliceNumber")
//	public String bindTrafficPoliceNumber() {
//		return "/weixin/levelControl/bindTrafficPoliceNumber";
//	}

    @ApiOperation(value = "警号确认", notes = "")
    @RequestMapping("/bindTrafficPolice")
    @ResponseBody
    public Result<?> bindTrafficPolice(@RequestBody String body) {
        JSONObject obj = JSONObject.parseObject(body);
        String policeId = obj.getString("policeId");
        String openid = obj.getString("openid");

        if (openid == null || openid.isEmpty()) {
            return Result.error("获取openid异常");
        }
        if (policeId == null || policeId.isEmpty()) {
            return Result.error("请输入警号");
        }

        try {
            WxUser wxUser = wxUserService.getById(openid);

            if (wxUser != null) {
                if (org.apache.commons.lang.StringUtils.equalsIgnoreCase(wxUser.getPoliceId(), policeId)) {
                    return Result.ok();
                } else {
                    return Result.error("警号不匹配");
                }
            } else {
                return Result.error("未查询到信息");
//				wxUser = new WxUserDO();
//				wxUser.setOpenid(openid);
//				wxUser.setPoliceId(policeId);
//				wxUserService.save(wxUser);
//				return R.ok();
            }
        } catch (Exception e) {
            return Result.error("警号查询错误");
        }


    }

    @ApiOperation(value = "获取用户关注路段信息", notes = "")
    @RequestMapping("/bindRoad")
    @ResponseBody
    public String bindRoad(@RequestBody String body) {
        JSONObject obj = JSONObject.parseObject(body);

        String ld = wxService.getHwIdsByOpenid();
        JSONObject ldJSON = JSONObject.parseObject(ld);
        JSONArray jsonArray = ldJSON.getJSONArray("hwIds");

        if (jsonArray == null || jsonArray.isEmpty()) {
            //未关注路段，转向关注路段页
            return "/weixin/wxUser/bindRoadTrafficPolice";
        } else {
            //已关注路段，转向警情列表页
            return "/weixin/alertThreepart/confirmListTP";
        }
    }

    @ApiOperation(value = "重定向管制通知页", notes = "")
    @RequestMapping("/controlNotice")
    public String controlNotice(Model model) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("iseffective", "1");
        List<AlarmNoticeModel> anList = fogApi.queryAlermNoticelist(map);
        List<AlarmNoticeModel> alarmNoticeList = new ArrayList<AlarmNoticeModel>();
        for (AlarmNoticeModel AlarmNoticeModel : anList) {
            JSONObject jsoon = JSONObject.parseObject(AlarmNoticeModel.getSendtext());
            AlarmNoticeModel.setEquName(jsoon.getString("equName"));
            AlarmNoticeModel.setDistance(jsoon.getString("distance"));

            FogCalculator cal = fcFactory.getCalculator(AlarmNoticeModel.getEpId());
            if (cal != null) {
                AlarmNoticeModel.setHwName(cal.getEquipment().getHwName());
            }
            alarmNoticeList.add(AlarmNoticeModel);
        }
        model.addAttribute("alarmNoticeList", alarmNoticeList);
        return "/weixin/levelControl/controlNotice";
    }

    @ApiOperation(value = "重定向绑定路段页", notes = "")
    @RequestMapping("/bindRoadTrafficPolice")
    public String bindRoadTrafficPolice() {
        return "/weixin/levelControl/bindRoadTrafficPolice";
    }


    @ApiOperation(value = "重定向管制通知详情页", notes = "")
    @RequestMapping("/controlNoticeDetails")
    public String controlNoticeDetails(@RequestParam String noticeId, Model model) {
        AlarmNoticeModel AlarmNoticeModel = fogApi.queryAlermNoticeByNoticeId(noticeId);
        JSONObject jsoon = JSONObject.parseObject(AlarmNoticeModel.getSendtext());
        AlarmNoticeModel.setEquName(jsoon.getString("equName"));
        AlarmNoticeModel.setDistance(jsoon.getString("distance"));

        FogCalculator cal = fcFactory.getCalculator(AlarmNoticeModel.getEpId());
        if (cal != null) {
            AlarmNoticeModel.setHwName(cal.getEquipment().getHwName());
        }

        model.addAttribute("AlarmNoticeModel", AlarmNoticeModel);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-M-d");
        //https://ainjdjc.jchc.cn/system/alarm/showImg?fname=1585651469018.jpg&camId=K800S-1&dateStr=2020-3-31
        String fname = jsoon.getString("imgpath");
        String camId = AlarmNoticeModel.getEpId();
        Date date = new Date(Long.parseLong(jsoon.getString("imgtime")));
        String dateStr = simpleDateFormat.format(date);

        String imgPath = fname;
        model.addAttribute("imgPath", imgPath);
        return "/weixin/levelControl/controlNoticeDetails";
    }

    /***********************微信端 end*************************/

}
