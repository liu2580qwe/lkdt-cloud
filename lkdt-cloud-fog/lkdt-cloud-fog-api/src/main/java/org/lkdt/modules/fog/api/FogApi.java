package org.lkdt.modules.fog.api;

import org.lkdt.common.api.vo.Result;
import org.lkdt.common.constant.ServiceNameConstants;
import org.lkdt.modules.fog.api.factory.FogApiFallbackFactory;
import org.lkdt.modules.fog.entity.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author HuangJunYao
 * @date 2021/4/30
 */
@Component
@FeignClient(contextId = "fogApi", value = ServiceNameConstants.FOG_SERVICE, fallbackFactory = FogApiFallbackFactory.class)
public interface FogApi {
    /**
     * 根据条件查询路段告警数据
     *
     * @param params
     * @return
     */
    @PostMapping("/fog/alarmRoad/queryListByParams")
    List<AlarmRoadModel> queryListByParams(@RequestBody Map<String, Object> params);

    /**
     *
     * @param date
     * @param epId
     * @return
     */

    @GetMapping("/fog/alarm/getDistanceStatistic")
    String getDistanceStatistic(@RequestParam("date") String date, @RequestParam("epId") String epId);

    /**
     *  编辑摄像头信息
     *
     * @param equipment
     * @return
     */
    @PutMapping(value = "/fog/zcEquipment/edit")
    Result<?> edit(@RequestBody EquipmentModel equipment);

    /**
     * Query camera id based on department ID
     * @param deptId
     * @return Returns a collection of camera ids
     */
    @GetMapping("/fog/zcEquipment/queryCameraIdsByDeptId")
    List<String> queryCameraIdsByDeptId(@RequestParam String deptId);

    /**
     *查询待确认摄像头列表
     *
     * @return
     */

    @GetMapping("/fog/alarm/getConfirmList")
    String getConfirmList();

    /**
     *checkAlarmRoad
     *
     * @return
     */
    @PostMapping("/fog/alarm/checkAlarmRoad")
    Result<?> checkAlarmRoad(@RequestBody AlarmModel alarm);

    /**
     *confirm
     *
     * @return
     */
    @PostMapping("/fog/alarm/confirm")
    Result<?> confirm(@RequestBody AlarmModel alarm);

    @GetMapping("/fog/alertThreepart/confirmDetails")
    AlertThreepartModel confirmDetails(@RequestParam String alertThreepartId, @RequestParam String openid);

    @PostMapping("/fog/zcAlarmNotice/queryAlermNoticelist")
    List<AlarmNoticeModel> queryAlermNoticelist(@RequestBody Map<String, Object> map);

    @GetMapping("/fog/zcAlarmNotice/queryAlermNoticeByNoticeId")
    AlarmNoticeModel queryAlermNoticeByNoticeId(@RequestParam String noticeId);

    @PostMapping("/fog/alertThreepart/listAll")
    List<AlertThreepartModel> listAll(Map<String, Object> params);

    @GetMapping("/fog/alarm/alarmDistanceStatisticWind")
    String alarmDistanceStatisticWind(@RequestParam String beginTime, @RequestParam String endTime, @RequestParam String epId);
}
