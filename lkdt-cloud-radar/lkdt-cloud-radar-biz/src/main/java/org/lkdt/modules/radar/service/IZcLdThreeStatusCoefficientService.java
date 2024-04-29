package org.lkdt.modules.radar.service;

import java.util.List;

import org.lkdt.modules.radar.entity.ZcLdThreeStatusCoefficient;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 交通流三态系数
 * @Author: jeecg-boot
 * @Date:   2021-09-23
 * @Version: V1.0
 */
public interface IZcLdThreeStatusCoefficientService extends IService<ZcLdThreeStatusCoefficient> {

	/**
	 * 对P值分段(0.7-1.0之间，以每 0.01 为一段) 排序
	 * @param radarId
	 * @param direction
	 * @param beginTime
	 * @param endTime
	 * @return
	 */
	List<ZcLdThreeStatusCoefficient> queryByPElt(String radarId ,String direction ,String beginTime ,String endTime);
	
	/**
	 * 获取三态根据P和G计算的二次函数
	 * @param radarId
	 * @param direction
	 * @param beginTime
	 * @param endTime
	 * @return
	 */
	double[] getThreeStatusEquation(String radarId, String direction, String beginTime, String endTime);
}
