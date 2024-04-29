package org.lkdt.modules.fog.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.api.R;
import org.lkdt.common.api.vo.Result;
import org.lkdt.modules.fog.entity.AlarmLog;

import java.sql.Timestamp;

/**
 * 沙盘推演
 * @author 
 *
 */
public interface DryRunningService {

	/**
	 * 推送沙盘数据
	 * @param starttime 	开始时间
	 * @param endtime		结束时间
	 * @param requestStr	沙盘环境（请求地址）
	 * @param intervalTime	时间价格
	 * @param magnification 沙盘演练倍率
	 * @return
	 */
	public Result sandTableData(Timestamp starttime, Timestamp endtime, String requestStr, int intervalTime, int magnification);
	
    JSONObject getSendFogJSONObject(AlarmLog alarmLog);
}
