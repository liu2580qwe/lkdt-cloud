package org.lkdt.modules.radar.supports.radarDataService;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.lkdt.common.util.CommonUtils;
import org.lkdt.common.util.StringUtils;
import org.lkdt.modules.api.SysBaseRemoteApi;
import org.lkdt.modules.radar.entity.RiskValues;
import org.lkdt.modules.radar.entity.ZcLdEventRadarInfo;
import org.lkdt.modules.radar.entity.ZcLdRadarFrameTargetAll;
import org.lkdt.modules.radar.entity.ZcLdRiskEventManage;
import org.lkdt.modules.radar.entity.ZcLdThreeStatusCoefficient;
import org.lkdt.modules.radar.mapper.RiskValuesMapper;
import org.lkdt.modules.radar.mapper.ZcLdEquipmentMapper;
import org.lkdt.modules.radar.mapper.ZcLdLaneInfoMapper;
import org.lkdt.modules.radar.mapper.ZcLdRadarFrameTargetAllMapper;
import org.lkdt.modules.radar.mapper.ZcLdRiskEventManageMapper;
import org.lkdt.modules.radar.mapper.ZcLdThreeStatusCoefficientMapper;
import org.lkdt.modules.radar.service.IZcLdThreeStatusCoefficientService;
import org.lkdt.modules.radar.supports.radarDataHandle.RadarEventDataGrab;
import org.lkdt.modules.radar.supports.radarDataService.observers.RadarObserver;
import org.lkdt.modules.radar.supports.radarDataService.observers.DO.RadarObjDO;
import org.lkdt.modules.radar.supports.radarDataService.radarCamUnion.CameraComponent;
import org.lkdt.modules.radar.supports.radarDataService.radarCamUnion.service.RecordCall;
import org.lkdt.modules.radar.supports.radarTools.BeanUtil;
import org.lkdt.modules.radar.supports.radarTools.DO.RadarDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.RestTemplate;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;

/**
 * 雷达数据读取
 *
 * 数据统计入口
 */
public class DataReader {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    /**雷达数据临时存储*/
    //private volatile ArrayBlockingQueue<RadarDO> arrayBlockingQueue = new ArrayBlockingQueue<>(4096);

    /**雷达数据缓存*/
    private RadarDO[] radarDOS = new RadarDO[3];

    /**计算线程*/
    private Thread dataThread;

    /**雷达ID*/
    private String radarId;

    /**来向大车平均速度值d*/
    protected double dLai = 82.1;
    /**去向大车平均速度值d*/
    protected double dQu = 82.1;

    /**来向速度离散度e*/
    protected double eLai = 20;
    /**去向速度离散度e*/
    protected double eQu = 20;

    /**来向c值*/
    protected double cLai = 22;
    /**去向c值*/
    protected double cQu = 22;

    /**是否白天*/
    protected boolean isDay = true;

    /**白天来向[大车数量][小车数量] 白天去向[大车数量][小车数量]*/
    protected double[] dayCount = new double[]{5, 5, 3};
    protected double[] dayCountAvg = new double[]{102.05, 100.85, 100.76};
    /**夜晚来向[大车数量][小车数量] 夜晚去向[大车数量][小车数量]*/
    protected double[] nightCount = new double[]{3, 1};
    protected double[] nightCountAvg = new double[]{100.18, 100.01};

    /**车辆计数*/
    public int countNumLai = 0;
    public int countNumQu = 0;
    /**平均速度*/
    public double avgSpeedLai = 0;
    public double avgSpeedQu = 0;
    /**道路拥堵*/
    public int carNumLai = 0;
    public int carNumQu = 0;
    /**三态 1：自由；2：干扰；3：排队*/
    public int threeStatusLai = 0;
    public int threeStatusQu = 0;
    /**风险 1：低；2：中；3：高*/
    public int riskLai = 0;
    public int riskQu = 0;

    /**两速三急累计*/
    public TwoSpeedThreeHurriedExt twoSpeedThreeHurriedExt;

    /**风险等级评估*/
    public RiskValues riskValuesLai = new RiskValues();

    public RiskValues riskValuesQu = new RiskValues();

    List<RiskValues> riskValuesList = new ArrayList<>();

    RadarEventDataGrab radarEventDataGrab;

    /**观察者*/
    private List<RadarObserver> radarObserverList = new ArrayList<>();

    /**入库缓存**/
    private volatile List<ZcLdRadarFrameTargetAll> zcLdRadarFrameTargetAllListCache = new ArrayList<>();

    private volatile int zcLdRadarFrameTargetAllListCache_size = 0;

    //雷达实时数据入库控制
    private boolean radarRealDataLibControl = true;

    ZcLdRadarFrameTargetAllMapper zcLdRadarFrameTargetAllMapper;

    ZcLdLaneInfoMapper zcLdLaneInfoMapper;

    ZcLdEquipmentMapper zcLdEquipmentMapper;
    
    IZcLdThreeStatusCoefficientService zcLdThreeStatusCoefficientService;

    JdbcTemplate jdbcTemplate;

    TwoSpeedThreeHurried twoSpeedThreeHurried;
    ConfluenceArea confluenceArea;
    Obstacle obstacle;
    ThreeStatus threeStatus;

    RiskValuesMapper riskValuesMapper;

    SysBaseRemoteApi sysBaseAPI;

    CameraComponent cameraComponent;

    ZcLdRiskEventManageMapper riskEventManageMapper;
    
    ZcLdThreeStatusCoefficientMapper threeStatusCoefficientMapper;
    
    RestTemplate restTemplate;

    private volatile boolean dataReaderFlag = true;
    
    /**
     * 超低速、超高速、停车
     */
    private long lastEventTime8888 = 0L;
    
    /**
     * 急加速、急减速
     */
    private long lastEventTime1003 = 0L;
    
    /**
     * 急停、撞车
     */
    private long lastEventTime1100 = 0L;
    
    /**
     * 急变道
     */
    private long lastEventTime1005 = 0L;
    
    /**
     * 三态风险识别-P临界1
     */
    private double plj1 = 0.91;
    
    /**
     * 三态风险识别-P临界2
     */
    private double plj2 = 0.618;
    
    private String uploadType = "minio";
    
    /**
     * 每一帧三态系数入库缓存
     */
    private volatile List<ZcLdThreeStatusCoefficient> zcLdThreeStatusCoefficientListCache = new ArrayList<>();
    

    //线程池
    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    synchronized public void setDataReaderFlag(boolean dataReaderFlag) {
        this.dataReaderFlag = dataReaderFlag;
    }

    public boolean getDataReaderFlag() {
        return dataReaderFlag;
    }

    /**
     * @see DataReader#init()
     * @param radarId
     */
    public DataReader(String radarId){
        this.radarId = radarId;
    }

    public DataReader init(BeanUtil beanUtil){
        try {
            this.zcLdRadarFrameTargetAllMapper = beanUtil.getBean(ZcLdRadarFrameTargetAllMapper.class);

            this.zcLdLaneInfoMapper = beanUtil.getBean(ZcLdLaneInfoMapper.class);

            this.zcLdEquipmentMapper = beanUtil.getBean(ZcLdEquipmentMapper.class);
            
            this.zcLdThreeStatusCoefficientService = beanUtil.getBean(IZcLdThreeStatusCoefficientService.class);

            this.jdbcTemplate = beanUtil.getBean(JdbcTemplate.class);

            this.riskValuesMapper = beanUtil.getBean(RiskValuesMapper.class);

            this.sysBaseAPI = beanUtil.getBean(SysBaseRemoteApi.class);

            this.cameraComponent = beanUtil.getBean(CameraComponent.class);
            
            this.restTemplate = beanUtil.getBean(RestTemplate.class);

            this.twoSpeedThreeHurried = beanUtil.getBean(TwoSpeedThreeHurried.class);
            this.confluenceArea = beanUtil.getBean(ConfluenceArea.class);
            this.obstacle = beanUtil.getBean(Obstacle.class);
            this.threeStatus = beanUtil.getBean(ThreeStatus.class);

            this.riskEventManageMapper = beanUtil.getBean(ZcLdRiskEventManageMapper.class);

            this.twoSpeedThreeHurriedExt = new TwoSpeedThreeHurriedExt(this, sysBaseAPI);
            
            this.threeStatusCoefficientMapper = beanUtil.getBean(ZcLdThreeStatusCoefficientMapper.class);

            //RadarObserver radarLaneCalculator = new RadarLaneCalculator(radarId);
            //this.attach(radarLaneCalculator);
//        RadarObserver timeCacheQueue = new TimeCacheQueue(radarId);
//        this.attach(timeCacheQueue);
//        RadarObserver timeCacheQueue30s = new TimeCacheQueue30s(radarId);
//        this.attach(timeCacheQueue30s);
//        RadarObserver timeCacheQueue5s = new TimeCacheQueue5s(radarId);
//        this.attach(timeCacheQueue5s);
            //雷达数据实时统计定时任务销毁
            //if(!dataReaderFlag){
            //    radarLaneCalculator.destroyTimeCounter();
            //}
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * 每帧数据入库
     * 每帧数据
     * @param radarId
     * @param radarDO
     */
    public void targetAllMapperInsertLib(String radarId, RadarDO radarDO){
        if(dataReaderFlag){
            //数据处理
            if(radarDO != null){
                JSONArray jsonArray = radarDO.getDataBody();
                //通知
                //this.notifyAll(this, jsonArray);
                //TODO: 数据操作
                if(jsonArray.size() > 0){
                    //入库
                    for(JSONObject obj: jsonArray.jsonIter()){
                        int targetId = obj.getInt("targetId");//目标ID
                        double sX = obj.getDouble("sX");//x坐标
                        double sY = obj.getDouble("sY");//y坐标
                        double vX = obj.getDouble("vX");//x速度
                        double vY = obj.getDouble("vY");//y速度
                        double aX = obj.getDouble("aX");//x加速度
                        double aY = obj.getDouble("aY");//y加速度
                        int laneNum = obj.getInt("laneNum");//车道号
                        //1 小型车，2 大型 车，3 超大型车
                        int carType = obj.getInt("carType");//车辆类型
                        //0 无事件 1 车辆停止 2 车辆反方向行驶 （逆行） 3 车辆超速行驶 5 车辆实线变道
                        //101 车辆停止消失 102 车辆反方向行 驶消失 103 车辆超速行驶 消失105 车辆实线变道 消失
                        int event = obj.getInt("event");//事件类型
                        int carLength = obj.getInt("carLength");//车辆长度
                        ZcLdRadarFrameTargetAll zcLdRadarFrameTargetAll = new ZcLdRadarFrameTargetAll();
                        zcLdRadarFrameTargetAll.setCarId(StringUtils.getUUID());
                        zcLdRadarFrameTargetAll.setRadarId(radarId);
                        zcLdRadarFrameTargetAll.setTargetId(targetId);
                        zcLdRadarFrameTargetAll.setSX(sX);
                        zcLdRadarFrameTargetAll.setSY(sY);
                        zcLdRadarFrameTargetAll.setVX(vX);
                        zcLdRadarFrameTargetAll.setVY(vY);
                        zcLdRadarFrameTargetAll.setAX(aX);
                        zcLdRadarFrameTargetAll.setAY(aY);
                        zcLdRadarFrameTargetAll.setLaneNum(laneNum);
                        zcLdRadarFrameTargetAll.setCarType(carType);
                        zcLdRadarFrameTargetAll.setEvent(event);
                        zcLdRadarFrameTargetAll.setCarLength(carLength);
                        zcLdRadarFrameTargetAll.setDateTime(radarDO.getDate());
                        zcLdRadarFrameTargetAll.setNanoSecond(radarDO.getNanoSecond());
                        zcLdRadarFrameTargetAllListCache.add(zcLdRadarFrameTargetAll);
                    }
                    //TODO: 缓存处理，以后修改为分布式缓存
                    if(++zcLdRadarFrameTargetAllListCache_size > 1000){//1000
                        zcLdRadarFrameTargetAllListCache_size = 0;
                        try{
                            //入库
                            if(radarRealDataLibControl){

                                List<ZcLdRadarFrameTargetAll> tempList = new ArrayList<>();
                                tempList.addAll(zcLdRadarFrameTargetAllListCache);

                                Runnable runnable = new RunnableZcLdRadarFrameTargetAllImpl(tempList);
                                executorService.submit(runnable);

//                                executorService.submit(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        int insertBatchCount = zcLdRadarFrameTargetAllMapper.insertBatch(tempList);
//                                        logger.info("雷达原始数据入库，数据条目：{}，已入库条目：{}，雷达：{}", tempList.size(), insertBatchCount, radarId);
//                                    }
//                                });
                            }
                        } catch (Exception e) {
                            logger.error("插入异常", e);
                        }
                        zcLdRadarFrameTargetAllListCache.clear();
                    }

                }
            }
        }
    }

    /**
     * 每辆车数据入库前触发
     * @param radarId
     * @param radarDO
     */
    public void targetBeforeInsertLib(String radarId, ZcLdEventRadarInfo zcLdEventRadarInfo){
    	//合流区模式六
    	try {
    		confluenceArea.confluenceAreaMode6(zcLdEventRadarInfo);
//    		eventAndCamera(map, zcLdEventRadarInfo);
		} catch (Exception e) {
			logger.error("合流区场景6输出异常", e.getMessage());
		}
    	
    	//障碍物算法
    	try {
    		ZcLdRiskEventManage eventManage = obstacle.obstacleServe(zcLdEventRadarInfo);
    		if(eventManage != null) {
    			logger.info("雷达："+radarId+"；事件类型(障碍物)："+eventManage.getEventType()+"；准备录屏");
    			//TODO 录屏+数据入库
                cameraComponent.record(radarId, eventManage.getEventType(), new RecordCall() {
                    @Override
                    public void callback(String eventType, String videoDir) {
                    	String vUrl = CommonUtils.upload(videoDir, "lkdt_radar_"+radarId, uploadType);
                    	if(vUrl.indexOf("192.168.3.100:9000") > -1) {
                			vUrl = vUrl.replace("192.168.3.100:9000", "218.94.44.74/lkdtCloudMinio");
                		}
                        eventManage.setVideoUrl(vUrl);
                        eventManage.setId(StringUtils.getUUID());
                        riskEventManageMapper.insert(eventManage);
                    }
                });
    		}
//    		eventAndCamera(map, zcLdEventRadarInfo);
		} catch (Exception e) {
			logger.error("障碍物算法异常", e.getMessage());
		}
    }
    
    /**
     * 事件入库、触发雷摄联动
     * @param map
     * @param zcLdEventRadarInfo
     */
    private void eventAndCamera(Map<String, String> map, ZcLdEventRadarInfo zcLdEventRadarInfo) {
    	String LHLQ = "", RHLQ = "";
		if(map != null) {
        	LHLQ = StringUtils.isEmpty(map.get("L"))? "": map.get("L");
        	RHLQ = StringUtils.isEmpty(map.get("R"))? "": map.get("R");
        }
		
		//来向
        if(StringUtils.isNotEmpty(LHLQ) ){
            //入库操作
            RiskValues riskValuesNew = new RiskValues();
//            riskValuesNew.setId(null);
            riskValuesNew.setRadarId(radarId);
            riskValuesNew.setDateTime(zcLdEventRadarInfo.getBeginTime());
            riskValuesNew.setD(dLai);
            riskValuesNew.setE(eLai);
            riskValuesNew.setC(cLai);
            riskValuesNew.setEventType(LHLQ);
            riskValuesNew.setDirection("L");

            riskValuesList.add(riskValuesNew);

            //TODO:触发录屏事件
            //cameraComponent.record(this.radarId, LHLQ, null);
        }

        //去向
        if(StringUtils.isNotEmpty(RHLQ)){
            //入库操作
            RiskValues riskValuesNew = new RiskValues();
//            riskValuesNew.setId(null);
            riskValuesNew.setRadarId(radarId);
            riskValuesNew.setDateTime(zcLdEventRadarInfo.getBeginTime());
            riskValuesNew.setD(dQu);
            riskValuesNew.setE(eQu);
            riskValuesNew.setC(cQu);
            riskValuesNew.setEventType(RHLQ);
            riskValuesNew.setDirection("R");

            riskValuesList.add(riskValuesNew);

            //TODO:触发录屏事件
            //cameraComponent.record(this.radarId, RHLQ, null);
        }
    }

    /**
     * 雷达各项指标计算
     */
    public void targetRadarDataValueCalculator(RadarEventDataGrab radarEventDataGrab, RadarDO radarDO){
    	//三态系数计算
    	try {
    		//入库开关是打开，并且在指定的时间段内
        	if(TwoSpeedThreeHurried.threeStatusCoefficientIsSaveMap.get(radarId).getCoefficientIsSave() != null &&
        			StringUtils.equals(TwoSpeedThreeHurried.threeStatusCoefficientIsSaveMap.get(radarId).getCoefficientIsSave(), "O") &&
        			radarDO.getDate().getTime() > TwoSpeedThreeHurried.threeStatusCoefficientIsSaveMap.get(radarId).getSaveTimeStart().getTime() &&
        			radarDO.getDate().getTime() < TwoSpeedThreeHurried.threeStatusCoefficientIsSaveMap.get(radarId).getSaveTimeEnd().getTime() ) {
        		List<ZcLdThreeStatusCoefficient> coefficientList = (List<ZcLdThreeStatusCoefficient>)threeStatus.radarThreeStatus(radarDO, radarId).get("ENTITY_LIST");
        		if(coefficientList != null && !coefficientList.isEmpty()) {
        			zcLdThreeStatusCoefficientListCache.addAll(coefficientList);
        			if(zcLdThreeStatusCoefficientListCache.size() > 1000) {
        				//批量入库
        				List<ZcLdThreeStatusCoefficient> tempList = new ArrayList<ZcLdThreeStatusCoefficient>();
        				tempList.addAll(zcLdThreeStatusCoefficientListCache);
        				
        				Runnable runnable = new RunnableThreeStatusCoefficientBatchInsertMapper(tempList);
                        executorService.submit(runnable);
                        
        				zcLdThreeStatusCoefficientListCache.clear();
        			}
        		}
        	}
		} catch (Exception e) {
			logger.error("雷达："+radarId+"；三态系数计算异常："+ e);
		}
    	
    	//三态判断
    	try {
    		//是否计算三态风险开关
			if(TwoSpeedThreeHurried.calculateMap.get(radarId) != null &&
					StringUtils.equals(TwoSpeedThreeHurried.calculateMap.get(radarId), "O")) {
				//30秒计算周期结果
				List<ZcLdThreeStatusCoefficient> coefficientList = (List<ZcLdThreeStatusCoefficient>)threeStatus.radarThreeStatus(radarDO, radarId).get("LIST30");
	    		if(coefficientList != null) {
	    			if(!coefficientList.isEmpty()) {
	    				for(ZcLdThreeStatusCoefficient c : coefficientList) {
	        				//三态系数是否入库（30秒周期计算结果）
//	        				if(TwoSpeedThreeHurried.threeStatusCoefficientIsSaveMap.get(radarId).getCoefficientIsSave() != null &&
//	        						StringUtils.equals(TwoSpeedThreeHurried.threeStatusCoefficientIsSaveMap.get(radarId).getCoefficientIsSave(), "O")) {
//	        					c.setId(StringUtils.getUUID());
//	            				Runnable runnable = new RunnableThreeStatusCoefficientMapper(c);
//	            	            executorService.submit(runnable);
//	        				}
	    					
	    					sendRadarAvgSpeed(radarId, c.getDirection(), c.getCoefficientV());
	        				
	    					double[] threeStatusEquation = TwoSpeedThreeHurried.threeStatusEquationMap.get(radarId+"_"+c.getDirection());
	        				double fp = Math.pow(c.getCoefficientP(),2) * threeStatusEquation[0] + c.getCoefficientP() * threeStatusEquation[1] + threeStatusEquation[2];
	        				if(c.getCoefficientP() == null || c.getCoefficientG() == null ) {
	        					logger.info(String.format("三态风险识别，雷达：{%s}；当前：自由态（P={%s}）;（G={%s}）", radarId,c.getCoefficientP(),c.getCoefficientG()));
	        					continue;
	        				}
	        				//三态风险识别
	        				if(c.getCoefficientP() > plj1) {	//自由态
	        					if(c.getCoefficientG() <= 0.15) {
	        						logger.info(String.format("三态风险识别，雷达：{%s}；当前：自由态（P={%s}）;风险等级：低风险-可能发生（G={%s}）", radarId,c.getCoefficientP(),c.getCoefficientG()));
	        					}else if(c.getCoefficientG() > 0.15 && c.getCoefficientG() < 0.3 ) {
	        						logger.info(String.format("三态风险识别，雷达：{%s}；当前：自由态（P={%s}）;风险等级：中风险-不经常发生（G={%s}）", radarId,c.getCoefficientP(),c.getCoefficientG()));
	        					}else if(c.getCoefficientG() >= 0.3 ) {
	        						logger.info(String.format("三态风险识别，雷达：{%s}；当前：自由态（P={%s}）;风险等级：高风险-很少发生（G={%s}）", radarId,c.getCoefficientP(),c.getCoefficientG()));
	        					}
	        				}else if(c.getCoefficientP() >= plj2 && c.getCoefficientP() <= plj1) {		//干扰态
	        					if(c.getCoefficientG() <= 0.15) {
	        						logger.info(String.format("三态风险识别，雷达：{%s}；当前：干扰态（P={%s}）;风险等级：低风险-经常发生（G={%s}）", radarId,c.getCoefficientP(),c.getCoefficientG()));
	        					}else if(c.getCoefficientG() > 0.2 && c.getCoefficientG() < 0.3 && c.getCoefficientG() < fp ) {
	        						logger.info(String.format("三态风险识别，雷达：{%s}；当前：干扰态（P={%s}）;风险等级：中风险-可能发生（G={%s}）", radarId,c.getCoefficientP(),c.getCoefficientG()));
	        					}else if(c.getCoefficientG() >= 0.3 && c.getCoefficientG() > fp ) {
	        						logger.info(String.format("三态风险识别，雷达：{%s}；当前：干扰态（P={%s}）;风险等级：高风险-不经常发生（G={%s}）", radarId,c.getCoefficientP(),c.getCoefficientG()));
	        					}
	        				}else if(c.getCoefficientP() < plj2) {	//排队态
	        					if(c.getCoefficientG() <= 0.15) {
	        						logger.info(String.format("三态风险识别，雷达：{%s}；当前：排队态（P={%s}）;风险等级：低风险-可能发生（G={%s}）", radarId,c.getCoefficientP(),c.getCoefficientG()));
	        					}else if(c.getCoefficientG() > 0.15 && c.getCoefficientG() < 0.3 ) {
	        						logger.info(String.format("三态风险识别，雷达：{%s}；当前：排队态（P={%s}）;风险等级：中风险-不经常发生（G={%s}）", radarId,c.getCoefficientP(),c.getCoefficientG()));
	        					}else if(c.getCoefficientG() >= 0.3 && c.getCoefficientG() > fp) {
	        						logger.info(String.format("三态风险识别，雷达：{%s}；当前：排队态（P={%s}）;风险等级：高风险-很少发生（G={%s}）", radarId,c.getCoefficientP(),c.getCoefficientG()));
	        					}
	        				}
	        			}
	    			}else {
	    				logger.info(String.format("三态风险识别，雷达：{%s}；当前：自由态，无车", radarId));
	    			}
	    		}
	    		
	    		//每一帧计算结果
	    		List<ZcLdThreeStatusCoefficient> coefficientListCurrent = (List<ZcLdThreeStatusCoefficient>)threeStatus.radarThreeStatus(radarDO, radarId).get("ENTITY_LIST");
	    		threeStatusValue(coefficientListCurrent, this);
	    		
			}
		} catch (Exception e) {
			logger.error("雷达："+radarId+"；三态判断异常："+ e);
		}

    	if(radarDO == null) {
    		return ;
    	}

    	//判断白天、夜晚
        Calendar calendar = Calendar.getInstance();
    	int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
    	if(hourOfDay > 18 || hourOfDay < 6){
    	    isDay = false;
        } else {
            isDay = true;
        }

    	

        this.radarEventDataGrab = radarEventDataGrab;

        //缓存三帧数据
        radarDOS[0] = radarDOS[1];
        radarDOS[1] = radarDOS[2];
        radarDOS[2] = radarDO;
        
        if(radarDOS[0] == null || radarDOS[1] == null || radarDOS[2] == null){
            return;
        }
        //目标缓存
        Set<Integer> targetIdSet = new HashSet<>();
        Set<Integer> targetIdSet0 = new HashSet<>();
        Set<Integer> targetIdSet1 = new HashSet<>();
        Set<Integer> targetIdSet2 = new HashSet<>();
        JSONArray jsonArrays0 = radarDOS[0].getDataBody();
        JSONArray jsonArrays1 = radarDOS[1].getDataBody();
        JSONArray jsonArrays2 = radarDOS[2].getDataBody();
        int size = Math.max(jsonArrays0.size(), Math.max(jsonArrays1.size(), jsonArrays2.size()));
        for(int i = 0; i < size; i++){
            if(i < jsonArrays0.size()){
                //每辆车解析
                RadarObjDO radarObjDO = new RadarObjDO();
                this.readData(radarObjDO, jsonArrays0.getJSONObject(i), this);
                targetIdSet0.add(radarObjDO.getTargetId());
            }
            if(i < jsonArrays1.size()){
                //每辆车解析
                RadarObjDO radarObjDO = new RadarObjDO();
                this.readData(radarObjDO, jsonArrays1.getJSONObject(i), this);
                targetIdSet1.add(radarObjDO.getTargetId());
            }
            if(i < jsonArrays2.size()){
                //每辆车解析
                RadarObjDO radarObjDO = new RadarObjDO();
                this.readData(radarObjDO, jsonArrays2.getJSONObject(i), this);
                targetIdSet2.add(radarObjDO.getTargetId());
            }
        }
        //目标缓存求交集
        targetIdSet.clear();
        targetIdSet.addAll(targetIdSet0);
        targetIdSet.retainAll(targetIdSet1);
        targetIdSet.retainAll(targetIdSet2);

        //1、计算超车道（小车）、混合道大车和道路平均车速（单向三车道以上：左侧超车道因没有大车其平均速度可视为小车平均速度）
        //2、计算道路小车速度110~130区间时大车平均速度值d及相对大车平均速度d的速度离散度统计分布取值e
        //3、计算小车流自由状态
        //4、低风险
        //5、中风险
        //6、高风险
        RiskValues[] riskValues = RiskValueCalculator.calcAvg(radarDOS, targetIdSet, this, cameraComponent);
        
        //超低速、超高速、停车
        ZcLdRiskEventManage eventManage8888 = null;
        try {
        	eventManage8888 = twoSpeedThreeHurried.speedSuddenChange(radarDO, radarId);
        	if(eventManage8888 != null) {
        		//不录屏
        		if(StringUtils.equals(eventManage8888.getEventType(), "1001") || 
        				StringUtils.equals(eventManage8888.getEventType(), "1002") || 
        				StringUtils.equals(eventManage8888.getEventType(), "1003") || 
        				StringUtils.equals(eventManage8888.getEventType(), "1004")) {
        			//1秒之内入库一次
        			if(lastEventTime1003 == 0 || radarDO.getDate().getTime() - lastEventTime1003 >= 1000) {
        				eventManage8888.setId(StringUtils.getUUID());
        				eventManage8888.setVideoUrl("-");
            			
            			Runnable runnable = new RunnableRiskEventManageMapper(eventManage8888);
                        executorService.submit(runnable);
            			
            			lastEventTime1003 = radarDO.getDate().getTime();
        			}
        		}else {	//录屏
        			//5秒之内启动一次录屏
                	if(lastEventTime8888 == 0 || radarDO.getDate().getTime() - lastEventTime8888 >= 5000) {
                		logger.info("雷达："+radarId+"；事件类型(停车)："+eventManage8888.getEventType()+"；准备录屏");
                		//TODO 录屏+数据入库
                        ZcLdRiskEventManage eventManage8888_ = eventManage8888;
                        cameraComponent.record(radarId, eventManage8888_.getEventType(), new RecordCall() {
                            @Override
                            public void callback(String eventType, String videoDir) {
                            	logger.info("雷达："+radarId+"；事件类型："+eventManage8888_.getEventType()+"；录屏回调=========="+videoDir);
                            	String vUrl = "-";
                            	if(!StringUtils.equals(videoDir, "ERROR")) {
                            		vUrl = CommonUtils.upload(videoDir, "lkdt_radar_"+radarId, uploadType);
                            		if(vUrl.indexOf("192.168.3.100:9000") > -1) {
                            			vUrl = vUrl.replace("192.168.3.100:9000", "218.94.44.74/lkdtCloudMinio");
                            		}
                            	}
                            	eventManage8888_.setVideoUrl(vUrl);
                                eventManage8888_.setId(StringUtils.getUUID());
                                riskEventManageMapper.insert(eventManage8888_);
                            }
                        });

                		long dd = radarDO.getDate().getTime() - lastEventTime8888;
                		lastEventTime8888 = radarDO.getDate().getTime();
                		logger.info("超低速、超高速、停车 ；雷达："+this.radarId+"；事件类型："+eventManage8888.getEventType()+"；lastEventTime2："+lastEventTime8888+"；时差："+dd+"毫秒；");
                	}
        		}
        		
        	}
        } catch (Exception e) {
            logger.error("两速输出异常："+ e);
        }
        
        //合流区
        try {
        	confluenceArea.confluenceAreaMode(radarDOS, targetIdSet, radarId);
		} catch (Exception e) {
			logger.error("合流区算法输出异常："+ e);
		}
        
        //急停、撞车
        ZcLdRiskEventManage eventManage1100 = null;
        try {
        	eventManage1100 = twoSpeedThreeHurried.twoSpeedThreeHurriedEvent(radarDOS, targetIdSet, radarId);
        	if(eventManage1100 != null) {
        		//不录屏
        		if(StringUtils.equals(eventManage1100.getEventType(), "1005")) {
        			//1秒之内入库一次
        			if(lastEventTime1005 == 0 || radarDO.getDate().getTime() - lastEventTime1005 >= 1000) {
        				eventManage1100.setId(StringUtils.getUUID());
        				eventManage1100.setVideoUrl("-");
            			
            			Runnable runnable = new RunnableRiskEventManageMapper(eventManage1100);
                        executorService.submit(runnable);
            			
            			lastEventTime1005 = radarDO.getDate().getTime();
        			}
        		}else {	//录屏
        			//5秒之内启动一次录屏
                	if(lastEventTime1100 == 0 || radarDO.getDate().getTime() - lastEventTime1100 >= 5000) {
                		logger.info("雷达："+radarId+"；事件类型(急停)："+eventManage1100.getEventType()+"；准备录屏");
                		//TODO 录屏+数据入库
                        ZcLdRiskEventManage eventManage1100_ = eventManage1100;
                        cameraComponent.record(radarId, eventManage1100_.getEventType(), new RecordCall() {
                            @Override
                            public void callback(String eventType, String videoDir) {
                            	logger.info("雷达："+radarId+"；事件类型："+eventManage1100_.getEventType()+"；录屏回调=========="+videoDir);
                            	String vUrl = "-";
                            	if(!StringUtils.equals(videoDir, "ERROR")) {
                            		vUrl = CommonUtils.upload(videoDir, "lkdt_radar_"+radarId, uploadType);
                            		if(vUrl.indexOf("192.168.3.100:9000") > -1) {
                            			vUrl = vUrl.replace("192.168.3.100:9000", "218.94.44.74/lkdtCloudMinio");
                            		}
                            	}
                                eventManage1100_.setVideoUrl(vUrl);
                                eventManage1100_.setId(StringUtils.getUUID());
                                riskEventManageMapper.insert(eventManage1100_);
                            }
                        });
                		
                		long dd = radarDO.getDate().getTime() - lastEventTime1100;
                		lastEventTime1100 = radarDO.getDate().getTime();
                		logger.info("急停、撞车录屏；雷达："+this.radarId+"；事件类型："+eventManage1100.getEventType()+"；lastEventTime1："+lastEventTime1100+"；时差："+dd+"毫秒；");
                	}
        		}
        	}
        } catch (Exception e) {
            logger.error("三急输出异常："+ e);
        }

//        String L = "", R = "", L2 = "", R2 = "";
//        /*三急*/
//        String L_CL1 = "", L_CL2 = "", L_CL3 = "", R_CL1 = "", R_CL2 = "", R_CL3 = "";
//        /*两速*/
//        String L2_CL1 = "", L2_CL2 = "", L2_CL3 = "", R2_CL1 = "", R2_CL2 = "", R2_CL3 = "";
//        //事件获取
//        if(eventType != null){
//            L = StringUtils.isEmpty(eventType.get("L"))? "": eventType.get("L");
//            R = StringUtils.isEmpty(eventType.get("R"))? "": eventType.get("R");
//
//            //来向车型
//            L_CL1 = StringUtils.isEmpty(eventType.get("L_CL1"))? "": eventType.get("L_CL1");
//            //来向开始车道
//            L_CL2 = StringUtils.isEmpty(eventType.get("L_CL2"))? "": eventType.get("L_CL2");
//            //来向结束车道
//            L_CL3 = StringUtils.isEmpty(eventType.get("L_CL3"))? "": eventType.get("L_CL3");
//            R_CL1 = StringUtils.isEmpty(eventType.get("R_CL1"))? "": eventType.get("R_CL1");
//            R_CL2 = StringUtils.isEmpty(eventType.get("R_CL2"))? "": eventType.get("R_CL2");
//            R_CL3 = StringUtils.isEmpty(eventType.get("R_CL3"))? "": eventType.get("R_CL3");
//        }
//        if(eventType2 != null){
//            L2 = StringUtils.isEmpty(eventType2.get("L"))? "": eventType2.get("L");
//            R2 = StringUtils.isEmpty(eventType2.get("R"))? "": eventType2.get("R");
//
//            //来向车型
//            L2_CL1 = StringUtils.isEmpty(eventType2.get("L_CL1"))? "": eventType2.get("L_CL1");
//            //来向开始车道
//            L2_CL2 = StringUtils.isEmpty(eventType2.get("L_CL2"))? "": eventType2.get("L_CL2");
//            //来向结束车道
//            L2_CL3 = StringUtils.isEmpty(eventType2.get("L_CL3"))? "": eventType2.get("L_CL3");
//            R2_CL1 = StringUtils.isEmpty(eventType2.get("R_CL1"))? "": eventType2.get("R_CL1");
//            R2_CL2 = StringUtils.isEmpty(eventType2.get("R_CL2"))? "": eventType2.get("R_CL2");
//            R2_CL3 = StringUtils.isEmpty(eventType2.get("R_CL3"))? "": eventType2.get("R_CL3");
//        }
//        if(eventTypeHlq != null) {
//        	LHLQ = StringUtils.isEmpty(eventTypeHlq.get("L"))? "": eventTypeHlq.get("L");
//        	RHLQ = StringUtils.isEmpty(eventTypeHlq.get("R"))? "": eventTypeHlq.get("R");
//        }

        //实时事件累计
        twoSpeedThreeHurriedExt.homeLiveEvent(radarDO);

        //来向
        if((riskValues[0] != null && riskValues[0].getRiskValue() > 1)){
            //入库操作
            RiskValues riskValuesNew = new RiskValues();
//            riskValuesNew.setId(null);
            riskValuesNew.setRadarId(radarId);
            riskValuesNew.setDateTime(radarDO.getDate());
            riskValuesNew.setD(dLai);
            riskValuesNew.setE(eLai);
            riskValuesNew.setC(cLai);
            riskValuesNew.setRiskValue(riskValues[0].getRiskValue());
            riskValuesNew.setWhole(riskValues[0].getWhole());
            riskValuesNew.setWholeXiao(riskValues[0].getWholeXiao());
            riskValuesNew.setWholeDa(riskValues[0].getWholeDa());
            riskValuesNew.setDeltV(riskValues[0].getDeltV());
            riskValuesNew.setWholeDiscreteValue(riskValues[0].getWholeDiscreteValue());
//            String eventTypes = L + L2;
//            riskValuesNew.setEventType(eventTypes);
            riskValuesNew.setDirection(riskValues[0].getDirection());

//            riskValuesNew.setCarType(L_CL1 + L2_CL1);
//            riskValuesNew.setCarStartLane(L_CL2 + L2_CL2);
//            riskValuesNew.setCarEndLane(L_CL3 + L2_CL3);

            riskValuesList.add(riskValuesNew);

            //TODO:触发录屏事件
//            cameraComponent.record(this.radarId, eventTypes + "{" + riskValues[0].getRiskValue() + "}");
            //cameraComponent.record(this.radarId, "{" + riskValues[0].getRiskValue() + "}", null);
        }

        //去向
        if((riskValues[1] != null && riskValues[1].getRiskValue() > 1)){
            //入库操作
            RiskValues riskValuesNew = new RiskValues();
//            riskValuesNew.setId(null);
            riskValuesNew.setRadarId(radarId);
            riskValuesNew.setDateTime(radarDO.getDate());
            riskValuesNew.setD(dQu);
            riskValuesNew.setE(eQu);
            riskValuesNew.setC(cQu);
            riskValuesNew.setRiskValue(riskValues[1].getRiskValue());
            riskValuesNew.setWhole(riskValues[1].getWhole());
            riskValuesNew.setRiskValue(riskValues[1].getRiskValue());
            riskValuesNew.setWhole(riskValues[1].getWhole());
            riskValuesNew.setWholeXiao(riskValues[1].getWholeXiao());
            riskValuesNew.setWholeDa(riskValues[1].getWholeDa());
            riskValuesNew.setDeltV(riskValues[1].getDeltV());
            riskValuesNew.setWholeDiscreteValue(riskValues[1].getWholeDiscreteValue());
//            String eventTypes = R + R2;
//            riskValuesNew.setEventType(eventTypes);
            riskValuesNew.setDirection(riskValues[1].getDirection());

//            riskValuesNew.setCarType(R_CL1 + R2_CL1);
//            riskValuesNew.setCarStartLane(R_CL2 + R2_CL2);
//            riskValuesNew.setCarEndLane(R_CL3 + R2_CL3);

            riskValuesList.add(riskValuesNew);

            //TODO:触发录屏事件
//            cameraComponent.record(this.radarId, eventTypes + "{" + riskValues[1].getRiskValue() + "}");
            //cameraComponent.record(this.radarId, "{" + riskValues[1].getRiskValue() + "}", null);
        }

        if(riskValuesList.size() > 500) {

            List<RiskValues> riskValuesListNew = new ArrayList<>();
            riskValuesListNew.addAll(riskValuesList);

            Runnable runnable = new RunnableRiskValuesImpl(riskValuesListNew);
            executorService.submit(runnable);

//                executorService.submit(()->{
//                    riskValuesMapper.insertBatch(riskValuesListNew);
//                });
            riskValuesList.clear();
        }

    }

    public static void readData(RadarObjDO radarObjDO, JSONObject obj, DataReader dataReader){
        RiskValueCalculator.getPolynomialLane(obj, dataReader);
        int targetId = obj.getInt("targetId");//目标ID
        float sX = obj.getFloat("sX");//x坐标
        float sY = obj.getFloat("sY");//y坐标
        float vX = obj.getFloat("vX");//x速度
        float vY = obj.getFloat("vY");//y速度
        float aX = obj.getFloat("aX");//x加速度
        float aY = obj.getFloat("aY");//y加速度
        int laneNum = obj.getInt("laneNum");//车道号
        int polynomialLane = obj.getInt("polynomialLane");//车道号
        //1 小型车，2 大型车，3 超大型车
        int carType = obj.getInt("carType");//车辆类型
        //0 无事件 1 车辆停止 2 车辆反方向行驶 （逆行） 3 车辆超速行驶 5 车辆实线变道
        //101 车辆停止消失 102 车辆反方向行 驶消失 103 车辆超速行驶 消失105 车辆实线变道 消失
        int event = obj.getInt("event");//事件类型
        int carLength = obj.getInt("carLength");//车辆长度
        radarObjDO.setTargetId(targetId).setsX(sX).setsY(sY).setvX(vX).setvY(vY).setaX(aX).setaY(aY)
                .setLaneNum(laneNum).setCarType(carType).setEvent(event).setCarLength(carLength)
                .setPolynomialLane(polynomialLane);
    }
    
    /**
     * 解析三态数据，返回前端显示
     * @param radarDO
     * @param coefficientListCurrent
     */
    private void threeStatusValue(List<ZcLdThreeStatusCoefficient> coefficientListCurrent,DataReader dataReader) {
    	if(coefficientListCurrent != null) {
			if(!coefficientListCurrent.isEmpty()) {
				for(ZcLdThreeStatusCoefficient c : coefficientListCurrent) {
					double[] threeStatusEquation = TwoSpeedThreeHurried.threeStatusEquationMap.get(radarId+"_"+c.getDirection());
    				double fp = Math.pow(c.getCoefficientP(),2) * threeStatusEquation[0] + c.getCoefficientP() * threeStatusEquation[1] + threeStatusEquation[2];
    				if(StringUtils.equals(c.getDirection(), "L")) {
    					if(c.getCoefficientP() == null || c.getCoefficientG() == null ) {
        					dataReader.threeStatusLai = 1;
        					dataReader.riskLai = 1;
        					continue;
        				}
        				//三态风险识别
        				if(c.getCoefficientP() > plj1) {	//自由态
        					dataReader.threeStatusLai = 1;
        					if(c.getCoefficientG() <= 0.15) {	//低风险-可能发生
        						dataReader.riskLai = 1;
        					}else if(c.getCoefficientG() > 0.15 && c.getCoefficientG() < 0.3 ) {	//中风险-不经常发生
        						dataReader.riskLai = 2;
        					}else if(c.getCoefficientG() >= 0.3 ) {	//高风险-很少发生
        						dataReader.riskLai = 3;
        					}
        				}else if(c.getCoefficientP() >= plj2 && c.getCoefficientP() <= plj1) {		//干扰态
        					dataReader.threeStatusLai = 2;
        					if(c.getCoefficientG() <= 0.15) {	//低风险-经常发生
        						dataReader.riskLai = 1;
        					}else if(c.getCoefficientG() > 0.2 && c.getCoefficientG() < 0.3 && c.getCoefficientG() < fp ) {	//中风险-可能发生
        						dataReader.riskLai = 2;
        					}else if(c.getCoefficientG() >= 0.3 && c.getCoefficientG() > fp ) {	//高风险-不经常发生
        						dataReader.riskLai = 3;
        					}
        				}else if(c.getCoefficientP() < plj2) {	//排队态
        					dataReader.threeStatusLai = 3;
        					if(c.getCoefficientG() <= 0.15) {	//低风险-可能发生
        						dataReader.riskLai = 1;
        					}else if(c.getCoefficientG() > 0.15 && c.getCoefficientG() < 0.3 ) {	//中风险-不经常发生
        						dataReader.riskLai = 2;
        					}else if(c.getCoefficientG() >= 0.3 && c.getCoefficientG() > fp) {	//高风险-很少发生
        						dataReader.riskLai = 3;
        					}
        				}
    				}else {
    					if(c.getCoefficientP() == null || c.getCoefficientG() == null ) {
        					dataReader.threeStatusQu = 1;
        					dataReader.riskQu = 1;
        					continue;
        				}
        				//三态风险识别
        				if(c.getCoefficientP() > plj1) {	//自由态
        					dataReader.threeStatusQu = 1;
        					if(c.getCoefficientG() <= 0.15) {	//低风险-可能发生
        						dataReader.riskQu = 1;
        					}else if(c.getCoefficientG() > 0.15 && c.getCoefficientG() < 0.3 ) {	//中风险-不经常发生
        						dataReader.riskQu = 2;
        					}else if(c.getCoefficientG() >= 0.3 ) {	//高风险-很少发生
        						dataReader.riskQu = 3;
        					}
        				}else if(c.getCoefficientP() >= plj2 && c.getCoefficientP() <= plj1) {		//干扰态
        					dataReader.threeStatusQu = 2;
        					if(c.getCoefficientG() <= 0.15) {	//低风险-经常发生
        						dataReader.riskQu = 1;
        					}else if(c.getCoefficientG() > 0.2 && c.getCoefficientG() < 0.3 && c.getCoefficientG() < fp ) {	//中风险-可能发生
        						dataReader.riskQu = 2;
        					}else if(c.getCoefficientG() >= 0.3 && c.getCoefficientG() > fp ) {	//高风险-不经常发生
        						dataReader.riskQu = 3;
        					}
        				}else if(c.getCoefficientP() < plj2) {	//排队态
        					dataReader.threeStatusQu = 3;
        					if(c.getCoefficientG() <= 0.15) {	//低风险-可能发生
        						dataReader.riskQu = 1;
        					}else if(c.getCoefficientG() > 0.15 && c.getCoefficientG() < 0.3 ) {	//中风险-不经常发生
        						dataReader.riskQu = 2;
        					}else if(c.getCoefficientG() >= 0.3 && c.getCoefficientG() > fp) {	//高风险-很少发生
        						dataReader.riskQu = 3;
        					}
        				}
    				}
    			}
			}else {		//无车
				dataReader.threeStatusLai = 1;
				dataReader.riskLai = 1;
				dataReader.threeStatusQu = 1;
				dataReader.riskQu = 1;
			}
		}
    }
    
    
    /**
     * 30秒周期数据传到雷达程序
     * @param radarId
     * @param direction
     * @param avgVy
     */
    private void sendRadarAvgSpeed(String radarId, String direction, double avgVy) {
    	JSONObject json = new JSONObject();
        json.put("radarId", radarId);
        json.put("direction", direction);
        json.put("avgVy", avgVy);

        //TODO: 迁移前置机需要把IP地址改为配置文件获取
        String url = "http://127.0.0.1:9999/radar/public"+ "/addRadarAvgSpeed";
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        HttpEntity<String> formEntity = new HttpEntity<String>(json.toString(), headers);
        String s = restTemplate.postForEntity(url,formEntity,String.class).getBody();
        logger.info("数据推送结果："+s);
    }
   



    /**
     * TODO: 目标数据拟合处理
     * @param zcLdEventRadarInfo
     */
    public void targetRadarDataTrack(ZcLdEventRadarInfo zcLdEventRadarInfo){
        //TODO:目标数据拟合处理

    }

//    /**
//     * 数据输入
//     * @param radarDO
//     */
//    synchronized public void putArrayBlockingQueueOfByteArray(RadarDO radarDO){
//        this.arrayBlockingQueue.offer(radarDO);
//    }

    private void attach(RadarObserver radarObserver){
        this.radarObserverList.add(radarObserver);
    }

//    public int getArrayBlockingQueueSizeInUse(){
//        return this.arrayBlockingQueue.size();
//    }
//
//    public int getArrayBlockingQueueRemainingCapacity(){
//        return this.arrayBlockingQueue.remainingCapacity();
//    }

    public Thread getDataThread() {
        return dataThread;
    }

    public String getRadarId() {
        return radarId;
    }

    /**
     * 通知
     * this.notifyAll(this, jsonArray);
     * @param dataReader
     */
    private void notifyAll(DataReader dataReader, JSONArray jsonArray){
        for(RadarObserver r: radarObserverList){
            r.radarDataHandle(dataReader, jsonArray);
        }
    }

    class RunnableZcLdRadarFrameTargetAllImpl implements Runnable {

        List<ZcLdRadarFrameTargetAll> zList;

        public RunnableZcLdRadarFrameTargetAllImpl(List<ZcLdRadarFrameTargetAll> zList){
            this.zList = zList;
        }

        @Override
        public void run() {
            //雷达原始数据入库
            int insertBatchCount = zcLdRadarFrameTargetAllMapper.insertBatch(zList);
            logger.info("雷达原始数据入库，数据条目：{}，已入库条目：{}，雷达：{}", zList.size(), insertBatchCount, radarId);
        }
    }

    class RunnableRiskValuesImpl implements Runnable {

        List<RiskValues> rList;

        public RunnableRiskValuesImpl(List<RiskValues> rList){
            this.rList = rList;
        }

        @Override
        public void run() {
            //风险等级评估值入库
        	riskValuesMapper.insertBatch(rList);
        }
    }
    
    class RunnableThreeStatusCoefficientMapper implements Runnable {

    	ZcLdThreeStatusCoefficient zcLdThreeStatusCoefficient;
    	
    	public RunnableThreeStatusCoefficientMapper(ZcLdThreeStatusCoefficient zcLdThreeStatusCoefficient) {
    		this.zcLdThreeStatusCoefficient = zcLdThreeStatusCoefficient;
    	}
    	
		@Override
		public void run() {
			//三态系数入库(30秒周期)
			threeStatusCoefficientMapper.insert(zcLdThreeStatusCoefficient);
		}
    }
    
    class RunnableThreeStatusCoefficientBatchInsertMapper implements Runnable {

        List<ZcLdThreeStatusCoefficient> zList;

        public RunnableThreeStatusCoefficientBatchInsertMapper(List<ZcLdThreeStatusCoefficient> zList){
            this.zList = zList;
        }

        @Override
        public void run() {
        	//三态系数入库(每一帧)
            int insertBatchCount = threeStatusCoefficientMapper.insertBatch(zList);
            logger.info("三态系数入库(每一帧)，数据条目：{}，已入库条目：{}，雷达：{}", zList.size(), insertBatchCount, radarId);
        }
    }
    
    class RunnableRiskEventManageMapper implements Runnable {

    	ZcLdRiskEventManage zcLdRiskEventManage;
    	
    	public RunnableRiskEventManageMapper(ZcLdRiskEventManage zcLdRiskEventManage) {
    		this.zcLdRiskEventManage = zcLdRiskEventManage;
    	}
    	
		@Override
		public void run() {
			//雷达事件入库
			riskEventManageMapper.insert(zcLdRiskEventManage);
		}
    }

    public static void main(String[] args) {
        try{
            try {
                File file = new File("C:\\Users\\zhangzhenbiao\\Desktop\\cam_1001_10.100.0.12_8013\\1623320.jpg");
                throw new RuntimeException("123");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                System.out.println("subEnd");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("结束");
        }

    }

}
