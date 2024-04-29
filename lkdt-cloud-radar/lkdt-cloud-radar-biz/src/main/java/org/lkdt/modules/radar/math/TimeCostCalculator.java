package org.lkdt.modules.radar.math;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

interface TestCase {
	public Object run(List<Object> params) throws Exception;

	public List<Object> getParams();
}

class CalcFFT implements TestCase {
	public CalcFFT() {

	}

	@Override
	public List<Object> getParams() {
		return null;
	}

	@Override
	public Object run(List<Object> params) throws Exception {

		RadarInput reader = new RadarInput();
		JSONArray d = reader.readInputs();
		JSONArray datas = new JSONArray(128);
		for (int i = 0; i < 128;i++) {
			datas.add(d.getJSONObject(i));
		}
		Complex[] inputData = new Complex[datas.size()];
		for (int i = 0; i < datas.size(); i++) {

			JSONObject json = datas.getJSONObject(i);
			inputData[i] = new Complex(json.getDoubleValue("sX"), json.getDoubleValue("sY"));
		}

		System.out.print("本算例用于计算快速傅立叶变换。正在初始化 计算数据(" + datas.size() + "点)... ...");

		FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
		Complex[] result = fft.transform(inputData, TransformType.FORWARD);
		System.out.println();
		for (Complex c : result) {
			System.out.print(c.toString() + ", ");
		}
		System.out.println();
		System.out.println(result.length);
		return result;
	}

//	private final int arrayLength = 4 * 1024 * 1024;

}

public class TimeCostCalculator {
	public TimeCostCalculator() {
	}

	/**
	 * 计算指定对象的运行时间开销。
	 * 
	 * @param testCase 指定被测对象。
	 * @return 返回sub.run的时间开销，单位为s。
	 * @throws Exception
	 */
	public double calcTimeCost(TestCase testCase) throws Exception {
		List<Object> params = testCase.getParams();
		long startTime = System.nanoTime();
		testCase.run(params);
		long stopTime = System.nanoTime();
		System.out.println("start: " + startTime + " / stop: " + stopTime);
		double timeCost = (stopTime - startTime) * 1.0e-9;
		// double timeCost = BigDecimal.valueOf(stopTime - startTime, 9).doubleValue();
		return timeCost;
	}

	public static void main(String[] args) throws Exception {
		TimeCostCalculator tcc = new TimeCostCalculator();
		double timeCost;

		System.out.println("--------------------------------------------------------------------------");
		timeCost = tcc.calcTimeCost(new CalcFFT());
		System.out.println("time cost is: " + timeCost + "s");
		System.out.println("--------------------------------------------------------------------------");
	}

}
