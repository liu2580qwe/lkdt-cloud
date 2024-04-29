package org.lkdt.modules.radar.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.lkdt.modules.radar.entity.ZcLdThreeStatusCoefficient;
import org.lkdt.modules.radar.mapper.ZcLdThreeStatusCoefficientMapper;
import org.lkdt.modules.radar.service.IFittingCurveService;
import org.lkdt.modules.radar.service.IZcLdThreeStatusCoefficientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 交通流三态系数
 * @Author: jeecg-boot
 * @Date:   2021-09-23
 * @Version: V1.0
 */
@Service
public class ZcLdThreeStatusCoefficientServiceImpl extends ServiceImpl<ZcLdThreeStatusCoefficientMapper, ZcLdThreeStatusCoefficient> implements IZcLdThreeStatusCoefficientService {

	@Autowired
	private ZcLdThreeStatusCoefficientMapper zcLdThreeStatusCoefficientMapper;
	
	@Autowired
	private IFittingCurveService fittingCurveService;
	
	@Override
	public List<ZcLdThreeStatusCoefficient> queryByPElt(String radarId, String direction, String beginTime,
			String endTime) {
		
		return zcLdThreeStatusCoefficientMapper.queryByPElt(radarId, direction, beginTime, endTime);
	}

	@Override
	public double[] getThreeStatusEquation(String radarId, String direction, String beginTime, String endTime) {
		List<ZcLdThreeStatusCoefficient> list = zcLdThreeStatusCoefficientMapper.queryByPElt(radarId, direction, beginTime, endTime);
		if(list == null || list.isEmpty()) {
			return null;
		}
		Double[] p = new Double[list.size()];
		Double[] g = new Double[list.size()];
		double tempP = 0.0;
		List<Double> total = new ArrayList<>();
		int count = 0 ;
		for (int i = 0; i < list.size(); i++) {
			ZcLdThreeStatusCoefficient e = list.get(i);
			if(tempP == 0) {
				tempP = e.getCoefficientP();
			}
			
			if(tempP != e.getCoefficientP()) {
				p[count] = tempP;
				g[count] = percentile(total,0.85);
//				System.out.println("P："+tempP+"；G："+g[count]+"；size："+total.size());
				tempP = e.getCoefficientP();
				count++;
				total = new ArrayList<>();
				total.add(e.getCoefficientG());
				
			}else {
				total.add(e.getCoefficientG());
			}
			if(i == list.size() -1) {
				p[count] = tempP;
				g[count] = percentile(total,0.85);
//				System.out.println("P："+tempP+"；size："+total.size());
			}
		}
		
		List<Double> p2 = new ArrayList<>();
		List<Double> g2 = new ArrayList<>();
		for(int i = 0 ; i < count ; i++) {
			p2.add(p[i]);
			g2.add(g[i]);
		}
		
		List<List<Double>> data = new ArrayList<>();
		data.add(p2);
		data.add(g2);
		Map<String,Object> map = fittingCurveService.queryCategoryData(data, 2);
		System.out.println("雷达："+radarId+"；-------------start------------");
		System.out.println(p2.toString());
		System.out.println(g2.toString());
		System.out.println("雷达："+radarId+"；-------------end------------");
		double[] equation = (double[])map.get("equation");
		return equation;
	}
	
	
	/**
     * 百分位数
     * @param total
     * @param percentile 百分数
     * @return
     */
    private static double percentile(List<Double> total,double percentile) {
    	//集合排序
        Collections.sort(total);
        int size = total.size();
        double px =  percentile*(size-1);
        int i = (int)Math.floor(px);
        double g = px - i;
        
        if(g==0){
			return total.get(i);
		}else{
			return (1-g)*total.get(i)+g*total.get(i+1);
		}
        
    }

}
