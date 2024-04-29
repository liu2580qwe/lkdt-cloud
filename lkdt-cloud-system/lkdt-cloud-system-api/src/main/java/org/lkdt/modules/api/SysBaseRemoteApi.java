package org.lkdt.modules.api;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import org.lkdt.common.api.vo.Result;
import org.lkdt.common.constant.ServiceNameConstants;
import org.lkdt.common.system.vo.*;
import org.lkdt.modules.api.factory.SysBaseRemoteApiFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @Description: TODO
 * @author: scott
 * @date: 2020年05月21日 14:32
 */
@Component
@FeignClient(contextId = "sysBaseRemoteApi", value = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = SysBaseRemoteApiFallbackFactory.class)
public interface SysBaseRemoteApi {

    @GetMapping("/sys/user/info/{username}")
    Result<LoginUser> getUserByName(@PathVariable("username") String username);

    /**
     * 保存日志
     * @param jsonObject
     */
    @PostMapping("/sys/log/save")
    void saveSysLog(@RequestBody JSONObject jsonObject);

    /**
     * 通过编码和存储的value查询字典text、
     * @return
     */
    @GetMapping("/sys/user/queryDictTextByKey")
    String queryDictTextByKey(@RequestParam("code") String code, @RequestParam("key") String key);

    /**
     * 通过编码和存储的value查询字典text
     * @param code
     * @return
     */
    @GetMapping("/sys/user/queryDictItemsByCode")
    List<DictModel> queryDictItemsByCode(@RequestParam("code") String code);

    /**
     * 通过编码和存储的value查询表字典的text
     * @param table 表名
     * @param text  表字段
     * @param code  表字段
     * @param key   表字段code的值
     * @return
     */
    @GetMapping("/sys/user/queryTableDictTextByKey")
    String queryTableDictTextByKey(@RequestParam("table") String table, @RequestParam("text") String text, @RequestParam("code") String code, @RequestParam("key") String key);

//    /**
//     * 日志添加
//     * @param LogContent 内容
//     * @param logType 日志类型(0:操作日志;1:登录日志;2:定时任务)
//     * @param operatetype 操作类型(1:添加;2:修改;3:删除;)
//     */
//
//    void addLog(String LogContent, Integer logType, Integer operatetype);
//
//    /**
//     * 获取当前数据库类型
//     * @return
//     * @throws Exception
//     */
//    public String getDatabaseType() throws SQLException;
//
//    /**
//     * 查询所有部门 作为字典信息 id -->value,departName -->text
//     * @return
//     */
//    public List<DictModel> queryAllDepartBackDictModel();
//
//    /**
//     * 查询所有部门，拼接查询条件
//     * @return
//     */
//    List<JSONObject> queryAllDepart(Wrapper wrapper);
//
//    /**
//     * 发送系统消息
//     * @param fromUser 发送人(用户登录账户)
//     * @param toUser  发送给(用户登录账户)
//     * @param title  消息主题
//     * @param msgContent  消息内容
//     */
//    public void sendSysAnnouncement(String fromUser,String toUser,String title, String msgContent);
//
//    /**
//     * 发送系统消息
//     * @param fromUser 发送人(用户登录账户)
//     * @param toUser   发送给(用户登录账户)
//     * @param title    通知标题
//     * @param map  	   模板参数
//     * @param templateCode  模板编码
//     */
//    public void sendSysAnnouncement(String fromUser, String toUser, String title, Map<String, String> map, String templateCode);
//
//    /**
//     *
//     * @param fromUser 发送人(用户登录账户)
//     * @param toUser 发送给(用户登录账户)
//     * @param title 通知标题
//     * @param map 模板参数
//     * @param templateCode 模板编码
//     * @param busType 业务类型
//     * @param busId 业务id
//     */
//    public void sendSysAnnouncement(String fromUser, String toUser,String title, Map<String, String> map, String templateCode,String busType,String busId);
//
//    /**
//     * 通过消息中心模板，生成推送内容
//     *
//     * @param templateCode 模板编码
//     * @param map          模板参数
//     * @return
//     */
//    public String parseTemplateByCode(String templateCode, Map<String, String> map);
//
//    /**
//     * 发送系统消息
//     * @param fromUser 发送人(用户登录账户)
//     * @param toUser  发送给(用户登录账户)
//     * @param title  消息主题
//     * @param msgContent  消息内容
//     * @param setMsgCategory  消息类型 1:消息2:系统消息
//     */
//    public void sendSysAnnouncement(String fromUser, String toUser, String title, String msgContent, String setMsgCategory);
//
//    /**queryTableDictByKeys
//     * 发送系统消息
//     * @param fromUser 发送人(用户登录账户)
//     * @param toUser  发送给(用户登录账户)
//     * @param title  消息主题
//     * @param msgContent  消息内容
//     * @param setMsgCategory  消息类型 1:消息2:系统消息
//     * @param busType  业务类型
//     * @param busId  业务id
//     */
//    public void sendSysAnnouncement(String fromUser, String toUser, String title, String msgContent, String setMsgCategory,String busType,String busId);
//
//    /**
//     * 根据业务类型及业务id修改消息已读
//     * @param busType
//     * @param busId
//     */
//    public void updateSysAnnounReadFlag(String busType,String busId);
//    /**
//     * 查询表字典 支持过滤数据
//     * @param table
//     * @param text
//     * @param code
//     * @param filterSql
//     * @return
//     */
//    public List<DictModel> queryFilterTableDictInfo(String table, String text, String code, String filterSql);
//
//    /**
//     * 查询指定table的 text code 获取字典，包含text和value
//     * @param table
//     * @param text
//     * @param code
//     * @param keyArray
//     * @return
//     */
//    @Deprecated
//    public List<String> queryTableDictByKeys(String table, String text, String code, String[] keyArray);
//
//    /**
//     * 获取所有有效用户
//     * @return
//     */
//    public List<ComboModel> queryAllUser();
//
//    /**
//     * 获取所有有效用户 带参
//     * userIds 默认选中用户
//     * @return
//     */
//    public JSONObject queryAllUser(String[] userIds, int pageNo, int pageSize);
//
//    /**
//     * 获取所有有效用户 拼接查询条件
//     *
//     * @return
//     */
//    List<JSONObject> queryAllUser(Wrapper wrapper);
//
//    /**
//     * 获取所有角色
//     * @return
//     */
//    public List<ComboModel> queryAllRole();
//
//    /**
//     * 获取所有角色 带参
//     * roleIds 默认选中角色
//     * @return
//     */
//    public List<ComboModel> queryAllRole(String[] roleIds );
//
//    /**
//     * 通过用户账号查询角色Id集合
//     * @param username
//     * @return
//     */
//    public List<String> getRoleIdsByUsername(String username);
//
//    /**
//     * 通过部门编号查询部门id
//     * @param orgCode
//     * @return
//     */
//    public String getDepartIdsByOrgCode(String orgCode);
//
//    /**
//     * 查询上一级部门
//     * @param departId
//     * @return
//     */
//    public DictModel getParentDepartId(String departId);

    /**
     * 查询所有部门
     * @return
     */
    @GetMapping("/sys/sysDepart/listAllDepart")
    public List<SysDepartModel> getAllSysDepart();

//    /**
//     * 根据 id 查询数据库中存储的 DynamicDataSourceModel
//     *
//     * @param dbSourceId
//     * @return
//     */
//    DynamicDataSourceModel getDynamicDbSourceById(String dbSourceId);
//
//    /**
//     * 根据 code 查询数据库中存储的 DynamicDataSourceModel
//     *
//     * @param dbSourceCode
//     * @return
//     */
//    DynamicDataSourceModel getDynamicDbSourceByCode(String dbSourceCode);
//
//    /**
//     * 根据部门Id获取部门负责人
//     * @param deptId
//     * @return
//     */
//    public List<String> getDeptHeadByDepId(String deptId);
//
//    /**
//     * 文件上传
//     * @param file 文件
//     * @param bizPath 自定义路径
//     * @param uploadType 上传方式
//     * @return
//     */
//    public String upload(MultipartFile file,String bizPath,String uploadType);
//
//    /**
//     * 文件上传 自定义桶
//     * @param file
//     * @param bizPath
//     * @param uploadType
//     * @param customBucket
//     * @return
//     */
//    public String upload(MultipartFile file, String bizPath, String uploadType, String customBucket);
//
//    /**
//     * 文档管理文件下载预览
//     * @param filePath
//     * @param uploadpath
//     * @param response
//     */
//    public void viewAndDownload(String filePath, String uploadpath, String uploadType, HttpServletResponse response);
//
//    /**
//     * 给指定用户发消息
//     * @param userIds
//     * @param cmd
//     */
//    public void sendWebSocketMsg(String[] userIds, String cmd);
//
//    /**
//     * 根据id获取所有参与用户
//     * userIds
//     * @return
//     */
//    public List<LoginUser> queryAllUserByIds(String[] userIds);
//
//    /**
//     * 将会议签到信息推动到预览
//     * userIds
//     * @return
//     * @param userId
//     */
//    void meetingSignWebsocket(String userId);
//
//    /**
//     * 根据name获取所有参与用户
//     * userNames
//     * @return
//     */
//    List<LoginUser> queryUserByNames(String[] userNames);

}
