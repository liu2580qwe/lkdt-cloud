package org.lkdt.modules.radar.service;

import java.util.List;
import java.util.Map;

import org.jfree.chart.JFreeChart;

public interface IFittingCurveService {

	
	Map<String,Object> queryCategoryData(List<List<Double>> data ,int times);
	
	JFreeChart getChart(List<List<Double>> data,double[] equation,int times);
	
	String saveAsFile(JFreeChart chart, String outputPath, int weight, int height);
}
