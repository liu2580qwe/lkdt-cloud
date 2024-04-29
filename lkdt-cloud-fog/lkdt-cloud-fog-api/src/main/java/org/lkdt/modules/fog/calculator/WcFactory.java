package org.lkdt.modules.fog.calculator;

import lombok.extern.slf4j.Slf4j;
import org.lkdt.common.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Slf4j
@Service
public class WcFactory {
	protected static final Logger logger = LoggerFactory.getLogger(WcFactory.class);

	private static WcFactory thisFcFactory;

	/**设备名前缀*/
    public static final String CAMERA_CACHE_NAME = "wind:";

	/**计算器列表*/
	/*private static final ConcurrentHashMap<String, WindCalculator> fogCalculatorMap = new ConcurrentHashMap<String, WindCalculator>();*/

	@Autowired
	private RedisUtil redisUtil;

	@PostConstruct
	public void init() {
		logger.error("Initialize WcFactory");
		thisFcFactory = this;
	}

	/**
	 * 初始化
	 * @return
	 */
	/*public static WcFactory getThisFcFactory() {
		return thisFcFactory;
	}*/

	/**
	 * 计算器信息初始化
	 */
	private boolean caculatorInit(){
		try{
			/*fogCalculatorMap.clear();*/
		} catch (Exception e) {
			logger.error("未知异常：摄像头信息初始化失败",e);
			return false;
		}
		return true;
	}

	/**获取计算器信息列表*/
	/*public static ConcurrentHashMap<String, WindCalculator> getCalculators() {
		return fogCalculatorMap;
	}*/

	/**获取计算器信息*/
	public WindCalculator getCalculator(String epId) {
		return (WindCalculator)redisUtil.get(CAMERA_CACHE_NAME+epId);
	}

	/**移除计算器信息*/
	public boolean removeCalculator(String epId) {
		return thisFcFactory.caculatorInit();
	}

	/**添加计算器信息*/
	public void putCalculator(WindCalculator windCalculator,String epId) {
		redisUtil.set(CAMERA_CACHE_NAME+epId,windCalculator);
	}
}
