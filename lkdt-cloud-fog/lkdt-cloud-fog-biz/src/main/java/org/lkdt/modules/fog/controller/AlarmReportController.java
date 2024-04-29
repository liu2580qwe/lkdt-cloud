package org.lkdt.modules.fog.controller;

import org.lkdt.common.api.vo.Result;
import org.lkdt.common.system.vo.LoginUser;
import org.lkdt.common.util.AlarmLevelUtil;
import org.lkdt.common.util.DateUtils;
import org.lkdt.common.util.ShiroUtils;
import org.lkdt.common.util.StringUtils;
import org.lkdt.modules.fog.entity.AlarmRoad;
import org.lkdt.modules.fog.entity.AlarmRoadControlInfo;
import org.lkdt.modules.fog.service.IAlarmRoadControlInfoService;
import org.lkdt.modules.fog.service.IAlarmRoadService;
import org.lkdt.modules.fog.service.IAlarmService;
import org.lkdt.modules.wind.domain.AlarmDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 告警报表
 * @author wy
 *
 */
@Controller
@RequestMapping("/fog/alarmReport")
public class AlarmReportController{

	protected static final Logger logger = LoggerFactory.getLogger(AlarmReportController.class);
	

	@Autowired
	private IAlarmRoadService alarmRoadService;
	

	@Autowired
	private IAlarmService alarmService;
	
	@Autowired
	private IAlarmRoadControlInfoService alarmRoadControlInfoService;

	@ResponseBody
	@GetMapping("/queryRoadClose")
	public List<AlarmRoadControlInfo> queryRoadClose(String roadAlarmId, Model model){
		return alarmRoadControlInfoService.queryAlarmRoadControlList(roadAlarmId);
	}

	/**
	 * Query the list of warning cameras according to date and road section
	 * @param hwId
	 * @param hwIds
	 * @param alarmDate
	 * @return
	 */
	@GetMapping ("/queryEquAlarm")
	public @ResponseBody Result<?> queryEquAlarm(String hwId,String hwIds,@RequestParam("alarmDate") String alarmDate,@RequestParam(value = "alarmLevel",required = false)String level)
	{
		//Accept the parameters from the front end and encapsulate
		Map<String,Object> resultMap = new HashMap<>();
		Map<String,Object> map = new HashMap<>();
		if(hwId != null && StringUtils.isNotEmpty(hwId))
			map.put("hwId", hwId);
		if(hwIds != null && StringUtils.isNotEmpty(hwIds))
			map.put("hwIds", hwIds.split(","));
		if(alarmDate != null && StringUtils.isNotEmpty(alarmDate))
			map.put("begintime", alarmDate);
		if(level != null && StringUtils.isNotEmpty(level))
			map.put("alarmLevel", level);
		//Obtain road segment ids according to user permissions
		if(StringUtils.isEmpty(hwIds) && StringUtils.isEmpty(hwId))
		{
			LoginUser user = ShiroUtils.getUser();
			if (user != null && user.getId() != null && !user.isAdmin()){
				List<String> ids = user.getHwIds();
				//TODO Test code: String hwIdsS = StringUtils.join(ids.toArray(), ",")+"27";
				String hwIdsS = StringUtils.join(ids.toArray(), ",");
				map.put("hwIds", hwIdsS.split(","));
			}
		}
		//Query the list of warning cameras according to date and road section
		List<AlarmDO> equAlarm = alarmService.queryEquAlarm(map);
		resultMap.put("equAlarm", equAlarm);
		return Result.ok(resultMap);
	}

	/**
	 * 路段告警统计报表（按年统计）
	 * @param params
	 * @return
	 */
	@ResponseBody
	@GetMapping("/yearReportStatistics")
	public Result<?> yearReportStatistics(@RequestParam Map<String, Object> params){
		Map<String,Object> resultMap = queryAlarmReport(params,"YEAR");
		return Result.ok(resultMap);
	}

	/**
	 * 路段告警统计报表（按月统计）
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@GetMapping("/monthReportStatistics")
	public Map<String,Object> monthReportStatistics(@RequestParam Map<String, Object> params) throws Exception {

		return queryAlarmReport(params,"MONTH");
	}

	public Map<String,Object> queryAlarmReport(Map<String, Object> params,String dateType) {
		Map<String,Object> map = new HashMap<String,Object>();

		List<String> legendDataList = new ArrayList<String>();	//echarts.legend.Data
		List<String> hwNameList = new ArrayList<String>();
		List<String> levenList = new ArrayList<String>();
//		List<HighwayDO> hgDOList = new ArrayList<HighwayDO>();
		List<String> yearList = new ArrayList<String>();	//echarts.xAxis.Data
		Map<String,String> yearMap = new HashMap<String,String>();
		List<Object> seriesList = new ArrayList<Object>();	//echarts.series
		List<String> seriesDataList = new ArrayList<String>();	//echarts.series.Data
		Map<String,Object> seriesMap = new HashMap<String,Object>();
		boolean isClear = false;
		List<String> hwList = new ArrayList<String>();
		Map<String,Object> paramsMap = new HashMap<String, Object>();
		if(params.get("hwId") != null) {
			String hwId = params.get("hwId").toString();
			paramsMap.put("hwIds", new String[] {hwId});
//			HighwayDO hwDO = highwayDao.get(Long.parseLong(hwId));
//			hgDOList.add(hwDO);
		}else {
			if(!ShiroUtils.isAdmin()) {

				//根据USERID查询该用户绑定路段
				hwList = ShiroUtils.getUser().getHwIds();
				paramsMap.put("hwIds", hwList.toArray(new String[hwList.size()]));
			}else {
//				hgDOList = highwayDao.queryHighwayListByBindEqu();
				paramsMap.put("hwIds", null);
			}
		}

		Date date = new Date();
		//当前年份
		String currentYear = DateUtils.format(date, "yyyy");
		//当前月份
		String currentMonth = DateUtils.format(date, "MM");
		//当前日期
		String currentDay = DateUtils.format(date, "dd");

		String selectYear = StringUtils.EMPTY;
		String selectMonth = StringUtils.EMPTY;
		if(params.get("nowYear") != null) {
			selectYear = params.get("nowYear").toString();
		}else {
			selectYear = currentYear;
		}
		if(params.get("nowMonth") != null) {
			selectMonth = params.get("nowMonth").toString();
		}else {
			selectMonth = currentMonth ;
		}

		List<AlarmRoad> list = new ArrayList<AlarmRoad>();		//按时间、路段统计
		List<AlarmRoad> levelList = new ArrayList<AlarmRoad>();	//按时间、雾等级统计
		if(StringUtils.equalsIgnoreCase(dateType, "YEAR")) {
			list = alarmRoadService.queryYearRoadReport(paramsMap);
			levelList = alarmRoadService.queryYearLevelReport(paramsMap);
		}else if(StringUtils.equalsIgnoreCase(dateType, "MONTH")){
			paramsMap.put("nowYear", selectYear);
			list = alarmRoadService.queryMonthRoadReport(paramsMap);
			levelList = alarmRoadService.queryMonthLevelReport(paramsMap);
		}else if(StringUtils.equalsIgnoreCase(dateType, "DAY")){
			paramsMap.put("nowMonth", selectYear+"-"+selectMonth);
			list = alarmRoadService.queryDayRoadReport(paramsMap);
			levelList = alarmRoadService.queryDayLevelReport(paramsMap);
		}

//		if(list == null || list.size() == 0) {
//			map.put("result", "NULL_DATA");
//			return map ;
//		}

		for (AlarmRoad AlarmRoad : list) {
			if(StringUtils.equalsIgnoreCase(dateType, "YEAR")) {
				yearMap.put(AlarmRoad.getStatisticsDate()+"年", AlarmRoad.getStatisticsDate());
			}
		}

		if(StringUtils.equalsIgnoreCase(dateType, "YEAR")) {
			//补全当前年份
			yearMap.put(currentYear+"年", currentYear);
			for (String key : yearMap.keySet()) {
				yearList.add(key);
			}
		}else if(StringUtils.equalsIgnoreCase(dateType, "MONTH")){
			yearList.addAll(addMonth());
		}else if(StringUtils.equalsIgnoreCase(dateType, "DAY")){
			yearList.addAll(addDay(selectYear,selectMonth));
		}


		/****************按时间、路段统计 stsrt **************************/
		//按时间、路段统计的堆叠图的数据 echarts.series
		for (int i = 0; i < list.size(); i++) {

			AlarmRoad AlarmRoad = list.get(i);
			if(hwNameList.contains(AlarmRoad.getHwName())){
				continue;
			}
			hwNameList.add(AlarmRoad.getHwName());
			for (int j = 0; j < yearList.size(); j++) {
				String year = yearList.get(j);
				year = year.substring(0,year.length()-1);
				isClear = false;
				for (int k = 0; k <list.size(); k++) {
					AlarmRoad ar = list.get(k);
					if(AlarmRoad.getHwId() == ar.getHwId() && StringUtils.equals(year, ar.getStatisticsDate())) {
						seriesDataList.add(ar.getStatisticsNum());
						isClear = true;
					}
				}
				if(!isClear) {
					//只补全当前时间及之前维度的数据
					replenishSeries(dateType,seriesDataList,currentYear,selectYear,currentMonth,selectMonth,currentDay, year) ;
				}
			}
			seriesMap.put("name", AlarmRoad.getHwName());

//			seriesMap.put("stack", "路段统计-");
			seriesMap.put("data", seriesDataList);
			seriesMap.put("type", "bar");
			seriesMap.put("barMaxWidth", "18");
			seriesMap.put("id", AlarmRoad.getHwId());
			seriesList.add(seriesMap);

			seriesDataList = new ArrayList<String>();	//echarts.series.Data
			seriesMap = new HashMap<String,Object>();
		}

		/****************按时间、路段统计 end **************************/

		/****************按时间、雾等级统计 stsrt **************************/
		levenList.add("三级告警");
		levenList.add("二级告警");
		levenList.add("一级告警");
		levenList.add("特级告警");

		//按时间、雾等级统计的堆叠图的数据 echarts.series
		for (int i = 0; i < levenList.size(); i++) {
			String levenName = levenList.get(i);
			for (int j = 0; j < yearList.size(); j++) {
				String year = yearList.get(j);
				year = year.substring(0,year.length()-1);
				isClear = false;
				for (int k = 0; k <levelList.size(); k++) {
					AlarmRoad ar = levelList.get(k);

					if(StringUtils.equals(levenName, AlarmLevelUtil.getAlarmLevelDesc(Integer.valueOf(ar.getAlarmLevel())))
							&& StringUtils.equals(year, ar.getStatisticsDate())) {
						seriesDataList.add(ar.getStatisticsNum());
						isClear = true;
					}
				}
				if(!isClear) {
					//只补全当前时间及之前维度的数据
					replenishSeries(dateType,seriesDataList,currentYear,selectYear,currentMonth,selectMonth,currentDay, year) ;
				}
			}
			seriesMap.put("name", levenName);
			seriesMap.put("type", "bar");
			seriesMap.put("barMaxWidth", "18");
//			seriesMap.put("stack", "雾等级统计-");
			seriesMap.put("data", seriesDataList);
			seriesList.add(seriesMap);

			seriesDataList = new ArrayList<String>();	//echarts.series.Data
			seriesMap = new HashMap<String,Object>();
		}

		/****************按时间、雾等级统计 end **************************/

		legendDataList.addAll(hwNameList);
		legendDataList.addAll(levenList);
		map.put("legendData", legendDataList);
		map.put("xAxisData", yearList);
		map.put("seriesData", seriesList);
		map.put("result", "Y");

		return map;

	}
	private void replenishSeries(String dateType,List<String> seriesDataList,String currentYear,String selectYear,String currentMonth,String selectMonth,String currentDay,String year) {
		if(StringUtils.equalsIgnoreCase(dateType, "YEAR")) {
			seriesDataList.add("0");
		}
		//只补全当前时间及之前维度的数据
		if(StringUtils.equalsIgnoreCase(dateType, "MONTH")){
			if(Integer.valueOf(currentYear) > Integer.valueOf(selectYear)) {
				seriesDataList.add("0");
			}else {
				if(Integer.valueOf(selectMonth) >= Integer.valueOf(year)) {
					seriesDataList.add("0");
				}
			}
		}else if(StringUtils.equalsIgnoreCase(dateType, "DAY")){
			if(Integer.valueOf(currentYear+currentMonth) > Integer.valueOf(selectYear+selectMonth) ) {
				seriesDataList.add("0");
			}else {
				if((Integer.valueOf(currentDay) >= Integer.valueOf(year))) {
					seriesDataList.add("0");
				}
			}
		}
	}
	public List<String> addMonth(){
		List<String> list = new ArrayList<String>();
		list.add("01月");
		list.add("02月");
		list.add("03月");
		list.add("04月");
		list.add("05月");
		list.add("06月");
		list.add("07月");
		list.add("08月");
		list.add("09月");
		list.add("10月");
		list.add("11月");
		list.add("12月");
		return list;
	}

	public List<String> addDay(String year,String month){
		List<String> list = new ArrayList<String>();
		list.add("01号");
		list.add("02号");
		list.add("03号");
		list.add("04号");
		list.add("05号");
		list.add("06号");
		list.add("07号");
		list.add("08号");
		list.add("09号");
		list.add("10号");
		list.add("11号");
		list.add("12号");
		list.add("13号");
		list.add("14号");
		list.add("15号");
		list.add("16号");
		list.add("17号");
		list.add("18号");
		list.add("19号");
		list.add("20号");
		list.add("21号");
		list.add("22号");
		list.add("23号");
		list.add("24号");
		list.add("25号");
		list.add("26号");
		list.add("27号");
		list.add("28号");
		if(StringUtils.equals(month, "01") || StringUtils.equals(month, "03")
				|| StringUtils.equals(month, "05")  || StringUtils.equals(month, "07")
				|| StringUtils.equals(month, "08") || StringUtils.equals(month, "10")
				|| StringUtils.equals(month, "12") ) {
			list.add("29号");
			list.add("30号");
			list.add("31号");
		}
		if(StringUtils.equals(month, "04") || StringUtils.equals(month, "06")
				|| StringUtils.equals(month, "09")  || StringUtils.equals(month, "11")  ) {
			list.add("29号");
			list.add("30号");
		}
		if(StringUtils.equals(month, "02") && Integer.valueOf(year) % 4 == 0 ) {
			list.add("29号");
		}

		return list;
	}
}
