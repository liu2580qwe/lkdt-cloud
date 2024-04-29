package org.lkdt.modules.fog.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.lkdt.common.api.vo.Result;
import org.lkdt.common.util.HighWayUtil;
import org.lkdt.common.util.StringUtils;
import org.lkdt.modules.fog.calculator.FcFactory;
import org.lkdt.modules.fog.calculator.FogCalRedis;
import org.lkdt.modules.fog.calculator.FogCalculator;
import org.lkdt.modules.fog.channel.FogChannelUtil;
import org.lkdt.modules.fog.entity.AlarmRoadModel;
import org.lkdt.modules.fog.entity.AlertThreepart;
import org.lkdt.modules.fog.entity.AlertThreepartModel;
import org.lkdt.modules.fog.entity.AlertThreepartRecord;
import org.lkdt.modules.fog.mapper.AlertThreepartMapper;
import org.lkdt.modules.fog.mapper.AlertThreepartRecordMapper;
import org.lkdt.modules.fog.service.IAlertThreepartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.*;

/**
 * @Description: 三方告警信息
 * @Author: jeecg-boot
 * @Date:   2021-04-27
 * @Version: V1.0
 */
@Service
public class AlertThreepartServiceImpl extends ServiceImpl<AlertThreepartMapper, AlertThreepart> implements IAlertThreepartService {

	@Value("${lkdt.isAutoAlarm}")
	private String isAutoAlarm;

	@Resource
	private AlertThreepartMapper alertThreepartMapper;

	@Resource
	private AlertThreepartRecordMapper alertThreepartRecordMapper;

	@Autowired
	private FogCalRedis fogCalRedis;

	@Autowired
	private FcFactory fcFactory;

	@Autowired
	private FogChannelUtil fogChannelUtil;

	@Autowired
	private HighWayUtil highWayUtil;

	@Override
	@Transactional
	public void saveMain(AlertThreepart alertThreepart, List<AlertThreepartRecord> alertThreepartRecordList) {
		alertThreepartMapper.insert(alertThreepart);
		if(alertThreepartRecordList!=null && alertThreepartRecordList.size()>0) {
			for(AlertThreepartRecord entity:alertThreepartRecordList) {
				//外键设置
				entity.setAlertThreepartId(alertThreepart.getId());
				alertThreepartRecordMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional
	public void updateMain(AlertThreepart alertThreepart,List<AlertThreepartRecord> alertThreepartRecordList) {
		alertThreepartMapper.updateById(alertThreepart);
		
		//1.先删除子表数据
		alertThreepartRecordMapper.deleteByMainId(alertThreepart.getId());
		
		//2.子表数据重新插入
		if(alertThreepartRecordList!=null && alertThreepartRecordList.size()>0) {
			for(AlertThreepartRecord entity:alertThreepartRecordList) {
				//外键设置
				entity.setAlertThreepartId(alertThreepart.getId());
				alertThreepartRecordMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional
	public void delMain(String id) {
		alertThreepartRecordMapper.deleteByMainId(id);
		alertThreepartMapper.deleteById(id);
	}

	@Override
	@Transactional
	public void delBatchMain(Collection<? extends Serializable> idList) {
		for(Serializable id:idList) {
			alertThreepartRecordMapper.deleteByMainId(id.toString());
			alertThreepartMapper.deleteById(id);
		}
	}

	/**
	 * 发送告警消息接口
	 *
	 * @return
	 */
	@Override
	public Result<?> sendAlertMessage(AlarmRoadModel alarmRoad, String epId, String openId, boolean sendSms, String type) {
		FogCalculator cal = fcFactory.getCalculator(epId);
		AlertThreepart alertThreepartDO = new AlertThreepart();
		alertThreepartDO.setHwId(alarmRoad.getHwId()); //路段ID
		alertThreepartDO.setRoadAlarmId(alarmRoad.getId()); //告警路段信息id
		alertThreepartDO.setAlarmLevel(alarmRoad.getAlarmLevel()); //告警等级
		alertThreepartDO.setMindistanceNow(alarmRoad.getMindistanceNow().intValue()); //当前能见度
		alertThreepartDO.setMindistanceHis(alarmRoad.getMindistanceHis().intValue()); //历史最低能见度
		alertThreepartDO.setImgtime(cal.getImgtime()); //图片时间
		alertThreepartDO.setImgpath(cal.getImgpath()); //图片地址
		alertThreepartDO.setAlertTime(alarmRoad.getStarttime()); //消息推送时间
		alertThreepartDO.setEpId(epId); //摄像头ID
		alertThreepartDO.setOpenid(openId);
		alertThreepartDO.setTrend(fogCalRedis.getTrend(5, cal));
		if(sendSms) {
			alertThreepartDO.setOperation("2");	//微信+短信
		}else {
			alertThreepartDO.setOperation("1");	//微信
		}

		try{
			alertThreepartDO.setName(cal.getEquipment().getHwName()); //name
		} catch (Exception e){
			e.printStackTrace();
		}
		return this.sendAlertMessage(alertThreepartDO,type);
	}

	/**
	 * 发送告警消息接口
	 *
	 * @param alertThreepartDO hw_id 路段ID
	 *                         alarm_level 告警等级
	 *                         mindistance_now 当前能见度
	 *                         mindistance_his 历史最低能见度
	 *                         imgtime 图片时间
	 *                         imgpath 图片地址
	 *                         alert_time 消息推送时间
	 *                         ep_id 摄像头ID
	 * @return
	 */
	@Override
	public Result<?> sendAlertMessage(AlertThreepart alertThreepartDO, String type) {
		alertThreepartDO.setId(StringUtils.getUUID());
		alertThreepartDO.setDetailUpdateTime(new Date());
		Result<?> result = new Result<>();
		//入库成功 开始发送消息
		try{
			log.error("333"+ JSON.toJSONString(alertThreepartDO));
			if(alertThreepartMapper.insert(alertThreepartDO) > 0){
				//不是自动告警才发送微信消息
				if(!StringUtils.equals("1", isAutoAlarm)){
					if(StringUtils.equals(type, "1")) {	//系统告警
						JSONObject jsonObject = (JSONObject) JSONObject.toJSON(alertThreepartDO);
						jsonObject.put("epName",alertThreepartDO.getName());
						fogChannelUtil.threePartPushOutPut(jsonObject);
//					result = weixinClient.sendAlarmInfoToPolice(jsonObject);
					}else if(StringUtils.equals(type, "2")) {	//巡逻交警上报值班室
//					boolean bool = wXPushUtil.sendAlarmInfoToPolice2(alertThreepartDO);
					}
				}
				return Result.ok();
			} else {
				log.error("发送告警消息入库失败ERROR:AlertThreepartServiceImpl->sendAlertMessage");
			}
		} catch (Exception e){
			log.error("发送告警消息异常",e);
		}
		return Result.ok();
	}

	/**
	 * 告警信息
	 * @param alertThreepartId
	 */
	@Override
	public AlertThreepartModel confirmDetails(String alertThreepartId, String openid){
		//记录操作日志
//		this.loggerSave(alertThreepartId,openid,"0");
		AlertThreepart alertThreepart = alertThreepartMapper.selectById(alertThreepartId);
		AlertThreepartModel alertThreepartModel = new AlertThreepartModel();
		BeanUtils.copyProperties(alertThreepart, alertThreepartModel);
		alertThreepartModel.setDetail(highWayUtil.getById(alertThreepartModel.getHwId()).getDetail());
		return alertThreepartModel;
	}

	@Override
	public List<AlertThreepartModel> listAll(Map<String, Object> params) {
		List<AlertThreepart> alertThreepartDOS = alertThreepartMapper.listByParams(params);
		List<AlertThreepartModel> alertThreepartModels = new ArrayList<>();
		//设置状态
		for(AlertThreepart alertThreepart:alertThreepartDOS){
			AlertThreepartModel a = new AlertThreepartModel();
			BeanUtils.copyProperties(alertThreepart, a);
			try{
				Map<String, Object> mapRecord = new HashMap<>();
				mapRecord.put("notOperation","0");
				mapRecord.put("alertThreepartId",a.getId());
				List<AlertThreepartRecord> record = alertThreepartRecordMapper.selectByMap(mapRecord);
				String[] handleStatus = new String[]{"","",""};
				boolean bool1 = true, bool2 = true, bool3 = true;
				for(AlertThreepartRecord ar:record){
					if(ar.getOperation().equals("1")){//已上报
						if(bool1){
							handleStatus[0] = "已上报,";
						}
						bool1 = false;
					} else if(ar.getOperation().equals("2")){//已通报
						if(bool2){
							handleStatus[1] = "已通报,";
						}
						bool2 = false;
					} else if(ar.getOperation().equals("3")){//解除
						if(bool3){
							handleStatus[2] = "已解除,";
						}
						bool3 = false;
					}
				}
				String handleStatus_ = "";
				for(String s:handleStatus){
					if(StringUtils.isNotEmpty(s)){
						handleStatus_ += s;
					}
				}
				if(handleStatus_.length() > 0){
					handleStatus_ = handleStatus_.substring(0,handleStatus_.length() - 1);
				}
				a.setHandleStatus(handleStatus_);
				alertThreepartModels.add(a);
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		return alertThreepartModels;
	}

}
