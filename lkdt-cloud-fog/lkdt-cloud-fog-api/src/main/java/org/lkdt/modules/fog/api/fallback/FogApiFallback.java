package org.lkdt.modules.fog.api.fallback;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.lkdt.common.api.vo.Result;
import org.lkdt.modules.fog.api.FogApi;
import org.lkdt.modules.fog.entity.*;

import java.util.List;
import java.util.Map;

@Slf4j
public class FogApiFallback implements FogApi {

    @Setter
    private Throwable cause;

    @Override
    public List<AlarmRoadModel> queryListByParams(Map<String, Object> params) {
        log.info("--通过组织ID查询路段:"+params, cause);
        return null;
    }

    @Override
    public String getDistanceStatistic(String date, String epId) {
        return null;
    }

    @Override
    public Result<?> edit(EquipmentModel equipment) {
        return null;
    }

    @Override
    public List<String> queryCameraIdsByDeptId(String deptId) {
        log.info( ""+cause);
        return null;
    }

    @Override
    public String getConfirmList() {
        log.error("查询待确认摄像头列表:", cause);
        return null;
    }

    @Override
    public Result<?> checkAlarmRoad(AlarmModel alarm) {
        log.error("checkAlarmRoad:", cause);
        return null;
    }

    @Override
    public Result<?> confirm(AlarmModel alarm) {
        log.error("confirm:" + alarm, cause);
        return null;
    }

    @Override
    public AlertThreepartModel confirmDetails(String alertThreepartId, String openid) {
        log.error("confirmDetails:" + alertThreepartId, cause);
        return null;
    }

    @Override
    public List<AlarmNoticeModel> queryAlermNoticelist(Map<String, Object> map) {
        log.error("queryAlermNoticelist:" + map, cause);
        return null;
    }

    @Override
    public AlarmNoticeModel queryAlermNoticeByNoticeId(String noticeId) {
        log.error("queryAlermNoticeByNoticeId:" + noticeId, cause);
        return null;
    }

    @Override
    public List<AlertThreepartModel> listAll(Map<String, Object> params) {
        log.error("listAll:" + params, cause);
        return null;
    }

    @Override
    public String alarmDistanceStatisticWind(String beginTime, String endTime, String epId) {
        log.error("alarmDistanceStatisticWind:" + epId, cause);
        return null;
    }
}
