package org.lkdt.modules.radar.math;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class MyMath {

	// 多项式拟合
	public double[] polynomialCurve(List<Double> x, List<Double> y, int degree) {

		WeightedObservedPoints points = new WeightedObservedPoints();

		for (int index = 0; index < x.size(); index++) {
			points.add(x.get(index), y.get(index));
		}

		PolynomialCurveFitter fitter = PolynomialCurveFitter.create(degree);
		double[] result = fitter.fit(points.toList());
		
		ArrayUtils.reverse(result);
		return result;
	}
	public double[] polynomialCurve(JSONArray datas, int degree, boolean order) {

		WeightedObservedPoints points = new WeightedObservedPoints();

		for (int index = 0; index < datas.size(); index++) {
			JSONObject js = datas.getJSONObject(index);
			if (order) {
				points.add(Double.parseDouble(js.getString("sX")), Double.parseDouble(js.getString("sY")));
			} else {
				points.add(Double.parseDouble(js.getString("sY")), Double.parseDouble(js.getString("sX")));
			}
			
		}

		PolynomialCurveFitter fitter = PolynomialCurveFitter.create(degree);
		double[] result = fitter.fit(points.toList());
		return result;
	}

	// 以下代码为最小二乘法计算多项式系数
	// 最小二乘法多项式拟合
	public double[] leastSquaresMethod(List<Double> x, List<Double> y, int m) {
		if (x.size() != y.size() || x.size() <= m + 1) {
			return new double[]{};
		}
		double[] result = null;
		List<Double> S = new ArrayList<Double>();
		List<Double> T = new ArrayList<Double>();
		// 计算S0 S1 …… S2m
		for (int i = 0; i <= 2 * m; i++) {
			double si = 0.0;
			for (double xx : x) {
				si = si + Math.pow(xx, i);
			}
			S.add(si);
		}
		// 计算T0 T1 …… Tm
		for (int j = 0; j <= m; j++) {
			double ti = 0.0;
			for (int k = 0; k < y.size(); k++) {
				ti = ti + y.get(k) * Math.pow(x.get(k), j);
			}
			T.add(ti);
		}

		// 把S和T 放入二维数组，作为矩阵
		double[][] matrix = new double[m + 1][m + 2];
		for (int k = 0; k < m + 1; k++) {
			double[] matrixi = matrix[k];
			for (int q = 0; q < m + 1; q++) {
				matrixi[q] = S.get(k + q);
			}
			matrixi[m + 1] = T.get(k);
		}
//		for (int p = 0; p < matrix.length; p++) {
//			for (int pp = 0; pp < matrix[p].length; pp++) {
//				System.out.print(" matrix[" + p + "][" + pp + "]=" + matrix[p][pp]);
//			}
//			System.out.println();
//		}
		// 把矩阵转化为三角矩阵
		matrix = this.matrixConvert(matrix);
		// 计算多项式系数，多项式从高到低排列
		result = this.MatrixCalcu(matrix);
		return result;
	}

	// 计算一元多次方程前面的系数， 其排列为 xm xm-1 …… x0（多项式次数从高到低排列）
	public double[] MatrixCalcu(double[][] d) {

		int i = d.length - 1;
		int j = d[0].length - 1;
		List<Double> list = new ArrayList<Double>();
		double res = d[i][j] / d[i][j - 1];
		list.add(res);

		for (int k = i - 1; k >= 0; k--) {
			double num = d[k][j];
			for (int q = j - 1; q > k; q--) {
				num = num - d[k][q] * list.get(j - 1 - q);
			}
			res = num / d[k][k];
			list.add(res);
		}

		double[] doubles = new double[list.size()];
		for (int m = 0; m < list.size(); m++) {
			doubles[m] = list.get(m).doubleValue();
		}

		return doubles;
	}

	// 矩阵转换为三角矩阵
	public double[][] matrixConvert(double[][] d) {
		for (int i = 0; i < d.length - 1; i++) {
			double[] dd1 = d[i];
			double num1 = dd1[i];

			for (int j = i; j < d.length - 1; j++) {
				double[] dd2 = d[j + 1];
				double num2 = dd2[i];

				for (int k = 0; k < dd2.length; k++) {
					dd2[k] = (dd2[k] * num1 - dd1[k] * num2);
				}
			}
		}
//		for (int ii = 0; ii < d.length; ii++) {
//			for (int kk = 0; kk < d[ii].length; kk++)
//				System.out.print(d[ii][kk] + " ");
//			System.out.println();
//		}
		return d;
	}

}
