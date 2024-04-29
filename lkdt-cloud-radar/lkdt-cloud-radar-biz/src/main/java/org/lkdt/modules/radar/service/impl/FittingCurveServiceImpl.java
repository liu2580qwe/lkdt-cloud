package org.lkdt.modules.radar.service.impl;

import java.awt.BasicStroke;
import java.awt.Font;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lkdt.common.util.ImageUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.lkdt.modules.radar.math.MyMath;
import org.lkdt.modules.radar.math.RadarInput;
import org.lkdt.modules.radar.service.IFittingCurveService;

@Service
public class FittingCurveServiceImpl implements IFittingCurveService {

	@Override
	public Map<String, Object> queryCategoryData(List<List<Double>> data ,int times) {
		Map<String, Object> map = new HashMap<>();
//		List<List<Double>> data = inputData();
		MyMath math = new MyMath();
		double[] equation = math.polynomialCurve(data.get(0), data.get(1), times);
//		System.out.println(Arrays.toString(equation));
		map.put("dataList", data);
		map.put("equation", equation);
		return map;
	}

	@Override
	public JFreeChart getChart(List<List<Double>> data, double[] equation, int times) {
		// 创建主题样式
		StandardChartTheme standardChartTheme = new StandardChartTheme("CN");
		// 设置标题字体
		standardChartTheme.setExtraLargeFont(new Font("隶书", Font.BOLD, 20));
		// 设置图例的字体
		standardChartTheme.setRegularFont(new Font("宋书", Font.PLAIN, 15));
		// 设置轴向的字体
		standardChartTheme.setLargeFont(new Font("宋书", Font.PLAIN, 15));
		// 应用主题样式
		ChartFactory.setChartTheme(standardChartTheme);
		
		// 获取X和Y轴数据集
		XYDataset xydataset = getXYDataset(data, equation);
		// 创建用坐标表示的折线图
//		JFreeChart xyChart = ChartFactory.createXYLineChart(times + "次多项式拟合光滑曲线", "X轴", "Y轴", xydataset,
//				PlotOrientation.VERTICAL, true, true, false);
		// 创建散点图
		JFreeChart xyChart = ChartFactory.createScatterPlot(times + "次多项式拟合光滑曲线", "X轴", "Y轴", xydataset,
				PlotOrientation.VERTICAL, true, true, false);
		// 生成坐标点点的形状
		XYPlot plot = (XYPlot) xyChart.getPlot();

		XYItemRenderer r = plot.getRenderer();
		if (r instanceof XYLineAndShapeRenderer) {
			XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
			renderer.setDefaultShapesVisible(true);// 坐标点的形状是否可见
			renderer.setDefaultShapesFilled(true);
//			int MEAN_SERIES = 0;
//		    renderer.setSeriesStroke( MEAN_SERIES, new BasicStroke( 2 ) );
//		    renderer.setSeriesLinesVisible( MEAN_SERIES, true );
//		    renderer.setSeriesShapesVisible( MEAN_SERIES, false );
		}
		ValueAxis yAxis = plot.getRangeAxis();
		yAxis.setLowerMargin(2);
		return xyChart;
	}

	@Override
	public String saveAsFile(JFreeChart chart, String outputPath, int weight, int height) {
		
		/** 保存本地图片 ***/
		FileOutputStream out = null;
		try {
			File outFile = new File(outputPath);
			if (!outFile.getParentFile().exists()) {
				outFile.getParentFile().mkdirs();
			}
			out = new FileOutputStream(outputPath);
			// 保存为PNG文件
			ChartUtils.writeChartAsPNG(out, chart, weight, height, null);
			// 保存为JPEG文件
			// ChartUtils.writeChartAsJPEG(out, chart, weight, height);
			out.flush();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					// do nothing
				}
			}
		}
		String base64 = ImageUtils.GetImageStr(outputPath);
		return "data:image/jpg;base64," + base64;
		
		/** 不存本地图片 **/
//		ByteArrayOutputStream baos = null;
//        try{
//            baos = new ByteArrayOutputStream();
//            ChartUtils.writeChartAsJPEG(baos, chart, weight,height,null);
//            baos.flush();
//            baos.close();
//            Base64.Encoder encoder = Base64.getEncoder();
//            String base64 = encoder.encodeToString(baos.toByteArray());
//            return "data:image/jpg;base64," + base64;
//        } catch (Exception e){
//            e.printStackTrace();
//        } finally {
//            if(baos != null){
//                try {
//					baos.close();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//            }
//        }
//        return null;
		
	}

	// 模拟设置绘图数据（点）
	public List<List<Double>> inputData() {
		// x为x轴坐标
		List<Double> x = new ArrayList<Double>();
		List<Double> y = new ArrayList<Double>();

		RadarInput rader = new RadarInput();
		JSONArray results = rader.readInputs();
		for (Object obj : results) {
			JSONObject js = (JSONObject) obj;
			y.add(Double.parseDouble(js.getString("sX")));
			x.add(Double.parseDouble(js.getString("sY")));
		}

		List<List<Double>> list = new ArrayList<List<Double>>();
		list.add(x);
		list.add(y);
		return list;

	}

	// 数据集按照逻辑关系添加到对应的集合
	public XYDataset getXYDataset(List<List<Double>> data, double[] equation) {

		// 预设数据点数据集
		XYSeries s2 = new XYSeries("点点连线");
		for (int i = 0; i < data.get(0).size(); i++) {
			s2.add(data.get(0).get(i), data.get(1).get(i));
		}
		// 拟合曲线绘制 数据集 XYSeries s1 = new XYSeries("拟合曲线");
		// 获取拟合多项式系数，equation在构造方法中已经实例化
		double[] list = equation;
		// 获取预设的点数据

		// get Max and Min of x;
		List<Double> xList = data.get(0);
		double max = this.getMax(xList);
		double min = this.getMin(xList);
		double step = max - min;
		double x = min;
		double step2 = step / 800.0;
		// 按照多项式的形式 还原多项式，并利用多项式计算给定x时y的值
		XYSeries s1 = new XYSeries("多项式");
		for (int i = 0; i < 800; i++) {
			x = x + step2;
			int num = list.length - 1;
			double temp = 0.0;
			for (int j = 0; j < list.length; j++) {
				temp = temp + Math.pow(x, (num - j)) * list[j];
			}
			s1.add(x, temp);
		}

		// 把预设数据集合拟合数据集添加到XYSeriesCollection
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(s1);
		dataset.addSeries(s2);
		return dataset;

	}

	// 获取List中Double数据的最大最小值
	public double getMax(List<Double> data) {
		double res = data.get(0);
		for (int i = 0; i < data.size() - 1; i++) {
			if (res < data.get(i + 1)) {
				res = data.get(i + 1);
			}
		}
		return res;
	}

	public double getMin(List<Double> data) {
		double res = data.get(0);
		for (int i = 0; i < data.size() - 1; i++) {
			if (res > data.get(i + 1)) {
				res = data.get(i + 1);
			}
		}
		return res;
	}

}
