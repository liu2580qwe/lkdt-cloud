package org.lkdt.modules.api.fallback;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.lkdt.common.api.vo.Result;
import org.lkdt.common.system.vo.*;
import org.lkdt.modules.api.SysBaseRemoteApi;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Slf4j
public class SysBaseRemoteApiFallback implements SysBaseRemoteApi {

    @Setter
    private Throwable cause;

    @Override
    public Result<LoginUser> getUserByName(String username) {
        log.info("--获取用户信息异常--username:"+username, cause);
        return null;
    }

    @Override
    public void saveSysLog(JSONObject jsonObject) {
        log.info("--包存日志信息异常", cause);
    }

    @Override
    public String queryDictTextByKey(String code, String key) {
        log.info("--查询字典信息异常, code:"+code+", key:"+key, cause);
        return null;
    }

    @Override
    public List<DictModel> queryDictItemsByCode(String code) {
        log.info("--查询字典信息异常, code:"+code,cause);
        return null;
    }

    @Override
    public String queryTableDictTextByKey(String table, String text, String code, String key) {
        log.info("--查询表字典信息异常, table:"+table+", text:"+text+", code:"+code+", key:"+key, cause);
        return null;
    }

//    @Override
//    public void addLog(String LogContent, Integer logType, Integer operatetype) {
//
//    }
//
//    @Override
//    public String getDatabaseType() throws SQLException {
//        return null;
//    }
//
//    @Override
//    public List<DictModel> queryAllDepartBackDictModel() {
//        return null;
//    }
//
//    @Override
//    public List<JSONObject> queryAllDepart(Wrapper wrapper) {
//        return null;
//    }
//
//    @Override
//    public void sendSysAnnouncement(String fromUser, String toUser, String title, String msgContent) {
//
//    }
//
//    @Override
//    public void sendSysAnnouncement(String fromUser, String toUser, String title, Map<String, String> map, String templateCode) {
//
//    }
//
//    @Override
//    public void sendSysAnnouncement(String fromUser, String toUser, String title, Map<String, String> map, String templateCode, String busType, String busId) {
//
//    }
//
//    @Override
//    public String parseTemplateByCode(String templateCode, Map<String, String> map) {
//        return null;
//    }
//
//    @Override
//    public void sendSysAnnouncement(String fromUser, String toUser, String title, String msgContent, String setMsgCategory) {
//
//    }
//
//    @Override
//    public void sendSysAnnouncement(String fromUser, String toUser, String title, String msgContent, String setMsgCategory, String busType, String busId) {
//
//    }
//
//    @Override
//    public void updateSysAnnounReadFlag(String busType, String busId) {
//
//    }
//
//    @Override
//    public List<DictModel> queryFilterTableDictInfo(String table, String text, String code, String filterSql) {
//        return null;
//    }
//
//    @Override
//    public List<String> queryTableDictByKeys(String table, String text, String code, String[] keyArray) {
//        return null;
//    }
//
//    @Override
//    public List<ComboModel> queryAllUser() {
//        return null;
//    }
//
//    @Override
//    public JSONObject queryAllUser(String[] userIds, int pageNo, int pageSize) {
//        return null;
//    }
//
//    @Override
//    public List<JSONObject> queryAllUser(Wrapper wrapper) {
//        return null;
//    }
//
//    @Override
//    public List<ComboModel> queryAllRole() {
//        return null;
//    }
//
//    @Override
//    public List<ComboModel> queryAllRole(String[] roleIds) {
//        return null;
//    }
//
//    @Override
//    public List<String> getRoleIdsByUsername(String username) {
//        return null;
//    }
//
//    @Override
//    public String getDepartIdsByOrgCode(String orgCode) {
//        return null;
//    }
//
//    @Override
//    public DictModel getParentDepartId(String departId) {
//        return null;
//    }

    @Override
    public List<SysDepartModel> getAllSysDepart() {
        return null;
    }

//    @Override
//    public DynamicDataSourceModel getDynamicDbSourceById(String dbSourceId) {
//        return null;
//    }
//
//    @Override
//    public DynamicDataSourceModel getDynamicDbSourceByCode(String dbSourceCode) {
//        return null;
//    }
//
//    @Override
//    public List<String> getDeptHeadByDepId(String deptId) {
//        return null;
//    }
//
//    @Override
//    public String upload(MultipartFile file, String bizPath, String uploadType) {
//        return null;
//    }
//
//    @Override
//    public String upload(MultipartFile file, String bizPath, String uploadType, String customBucket) {
//        return null;
//    }
//
//    @Override
//    public void viewAndDownload(String filePath, String uploadpath, String uploadType, HttpServletResponse response) {
//
//    }
//
//    @Override
//    public void sendWebSocketMsg(String[] userIds, String cmd) {
//
//    }
//
//    @Override
//    public List<LoginUser> queryAllUserByIds(String[] userIds) {
//        return null;
//    }
//
//    @Override
//    public void meetingSignWebsocket(String userId) {
//
//    }
//
//    @Override
//    public List<LoginUser> queryUserByNames(String[] userNames) {
//        return null;
//    }
}
