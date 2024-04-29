package org.lkdt.modules.fog.service;


import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.service.IService;
import org.lkdt.common.api.vo.Result;
import org.lkdt.modules.fog.entity.*;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @Description: 三方告警信息
 * @Author: jeecg-boot
 * @Date:   2021-04-27
 * @Version: V1.0
 */
public interface IAlertThreepartService extends IService<AlertThreepart> {

	/**
	 * 添加一对多
	 * 
	 */
	public void saveMain(AlertThreepart alertThreepart,List<AlertThreepartRecord> alertThreepartRecordList) ;
	
	/**
	 * 修改一对多
	 * 
	 */
	public void updateMain(AlertThreepart alertThreepart,List<AlertThreepartRecord> alertThreepartRecordList);
	
	/**
	 * 删除一对多
	 */
	public void delMain (String id);
	
	/**
	 * 批量删除一对多
	 */
	public void delBatchMain (Collection<? extends Serializable> idList);

	Result<?> sendAlertMessage(AlarmRoadModel alarmRoad, String epId, String openId, boolean sendSms, String type);

	Result<?> sendAlertMessage(AlertThreepart alertThreepartDO, String type);

    AlertThreepartModel confirmDetails(String alertThreepartId, String openid);

    List<AlertThreepartModel> listAll(Map<String, Object> params);
}
