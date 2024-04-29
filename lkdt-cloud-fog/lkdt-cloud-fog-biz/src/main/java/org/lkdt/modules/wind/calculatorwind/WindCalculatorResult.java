package org.lkdt.modules.wind.calculatorwind;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;
@Data
public class WindCalculatorResult implements Serializable{
	private Float winds;//风速
	private Date time;
	private Float windd;//计算参数,风向
	public WindCalculatorResult(float winds, float windd, Date time) {
		this.windd = windd;
		this.winds = winds;
		this.time = time;
	}
}
