package org.lkdt.modules.fog.mongodb;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.lkdt.common.api.vo.Result;
import org.lkdt.common.system.vo.LoginUser;
import org.lkdt.common.util.ShiroUtils;
import org.lkdt.common.util.StringUtils;
import org.lkdt.common.util.oConvertUtils;
import org.lkdt.modules.fog.entity.AlarmLog;
import org.lkdt.modules.wind.entity.WindLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.query.UpdateDefinition;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * mongo日志操作
 *
 * @program: fog
 * @create: 2020-09-30 11:40
 **/
@Component
@Slf4j
public class MongoLogTemplate {

    @Autowired
    MongoTemplate mongoTemplate;

//    @Value("${lkdt.oss.endpoint}")
//    private String endpoint;
//
//    @Value("${lkdt.oss.bucketName}")
//    private String bucketName;

    @Value("${lkdt.imageHost}")
    private String imageHost;

    /**
     * mongo集合列表
     */
    private Set<String> collectionNames = null;

    public Set<String> getCollectionNames() {
        return collectionNames;
    }

    /**
     * 初始化mongo集合列表
     */
    @PostConstruct
    public void initMongoCellections() {
        collectionNames = mongoTemplate.getCollectionNames();
    }

    /**
     * yyyyMM
     */
    public final String str_ymd = "yyyyMM";

    /**
     * yyyy-MM-dd HH:mm:ss.SSS
     */
    public final String str_dateTime = "yyyy-MM-dd HH:mm:ss.SSS";

    public final String alarmLog_ = "alarmLog_";

    public final String windLog_ = "windLog";

    /**
     * 插入告警日志
     *
     * @param alarmLog
     * @return
     */
    public Result<?> insert(AlarmLog alarmLog) {
        SimpleDateFormat sdf_dateTime = new SimpleDateFormat(str_dateTime);
        SimpleDateFormat sdf_ymd = new SimpleDateFormat(str_ymd);
        if (StringUtils.isEmpty(alarmLog.getId())) {
            alarmLog.setId(StringUtils.getUUID());
        }
        String string = null;
        try {
            Date date = sdf_dateTime.parse(alarmLog.getDateTime());
            string = sdf_ymd.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (StringUtils.isEmpty(string)) {
            return Result.error("日期字段数值为空");
        }
        String collectionName = alarmLog_ + string;

        //建立索引
        if (!collectionNames.contains(collectionName)) {
            this.dbIndex(collectionName);
        }

        mongoTemplate.insert(alarmLog, collectionName);

        return Result.ok("插入成功");
    }

    /**
     * 创建索引:未创建集合则创建,已有集合不创建
     *
     * @param collectionName 集合名称
     */
    private void dbIndex(String collectionName) {
        //数据库实施集合获取
        collectionNames = mongoTemplate.getCollectionNames();
        if (!collectionNames.contains(collectionName)) {
            //创建集合
            MongoCollection<Document> mongoCollection = mongoTemplate.getCollection(collectionName);
            //同步内存集合名称
            collectionNames.add(collectionName);
            //该参数为索引的属性配置
            IndexOptions indexOptions = new IndexOptions();
            indexOptions.background(true);
            indexOptions.name("idx_dateTime");
            mongoCollection.createIndex(Indexes.ascending("dateTime"), indexOptions);
            //Document document = new Document("dateTime", "hashed");
            // Document key为索引的列名称，value为索引类型，在userId上创建hashed类型索引
            IndexOptions indexOptions_epId = new IndexOptions();
            indexOptions_epId.background(true);
            indexOptions_epId.name("idx_epId");
            mongoCollection.createIndex(Indexes.ascending("epId"), indexOptions_epId);
        }
    }

    /**
     * 批量插入
     *
     * @param alarmLogs 日志集
     * @param date      日志时间
     * @return
     */
    public Result<?> insert(List<AlarmLog> alarmLogs, Date date) {
        SimpleDateFormat sdf_ymd = new SimpleDateFormat(str_ymd);
        String collectionName = alarmLog_ + sdf_ymd.format(date);

        //建立索引
        if (!collectionNames.contains(collectionName)) {
            this.dbIndex(collectionName);
        }

        //批量插入
        mongoTemplate.insert(alarmLogs, collectionName);

        return Result.ok("插入成功");
    }

    /**
     * 查询告警日志:正则左匹配
     *
     * @return
     */
    public List<AlarmLog> find(String collectionName, String epId, String prefixTime) {
        try {
            if (StringUtils.isNotEmpty(collectionName) && StringUtils.isNotEmpty(prefixTime)) {
                Pattern epId_pattern = Pattern.compile("^" + epId, Pattern.CASE_INSENSITIVE);
                Pattern pattern = Pattern.compile("^" + prefixTime + ".*$", Pattern.CASE_INSENSITIVE);
                Query query = new Query(Criteria.where("dateTime").regex(pattern).and("epId").regex(epId_pattern));
                query.with(Sort.by(Sort.Direction.ASC, "dateTime"));
                List<AlarmLog> alarmLogs = mongoTemplate.find(query, AlarmLog.class, alarmLog_ + collectionName);
                return alarmLogs;
            }
            return new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * 查询告警日志
     *
     * @param collectionName
     * @param epId
     * @param startTime      开始时间
     * @param endTime        结束时间
     * @return
     */
    public List<AlarmLog> find(String collectionName, String epId, String startTime, String endTime) {
        try {
            if (StringUtils.isNotEmpty(collectionName) && StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime)) {
                Criteria criteria = Criteria.where("dateTime").lte(endTime).gte(startTime);
                if (StringUtils.isNotEmpty(epId)) {
//                    Pattern epId_pattern = Pattern.compile("^" + epId, Pattern.CASE_INSENSITIVE);
//                    criteria.andOperator(Criteria.where("epId").is(epId_pattern));
                    criteria.and("epId").is(epId);
                }
                Query query = new Query(criteria);
                query.with(Sort.by(Sort.Direction.ASC, "dateTime"));
                List<AlarmLog> alarmLogs = mongoTemplate.find(query, AlarmLog.class, alarmLog_ + collectionName);
                // TODO 替换图片地址
                LoginUser loginUser = ShiroUtils.getUser();
                if (oConvertUtils.isNotEmpty(loginUser) && StringUtils.isNotEmpty(loginUser.getSysIp()) && StringUtils.isNotEmpty(imageHost)) {
                    for (AlarmLog alarmLog : alarmLogs) {
//                        if(StringUtils.isEmpty(bucketName)){
//                            if(StringUtils.isNotEmpty(alarmLog.getImgName())){
//                                alarmLog.setImgName(alarmLog.getImgName().replace(endpoint,loginUser.getSysIp()));
//                            }
//                        }else{
//                            if(StringUtils.isNotEmpty(alarmLog.getImgName())){
//                                alarmLog.setImgName(alarmLog.getImgName().replace(bucketName+"."+endpoint,loginUser.getSysIp()));
//                            }
//                        }
                        if (StringUtils.isNotEmpty(alarmLog.getImgName())) {
                            alarmLog.setImgName(alarmLog.getImgName().replace(imageHost, loginUser.getSysIp()));
                        }

                    }
                }
                return alarmLogs;
            }
            return new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * 查询告警日志
     *
     * @param windId
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return
     */
    public List<WindLog> findWind(String windId, String startTime, String endTime) {
        try {
            if (StringUtils.isNotEmpty(windLog_) && StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime)) {
                Criteria criteria = Criteria.where("time").lte(endTime).gte(startTime);
                if (StringUtils.isNotEmpty(windId)) {
//                    Pattern epId_pattern = Pattern.compile("^" + windId, Pattern.CASE_INSENSITIVE);
//                    criteria.andOperator(Criteria.where("windId").is(epId_pattern));
                    criteria.and("windId").is(windId);
                }
                Query query = new Query(criteria);
                query.with(Sort.by(Sort.Direction.ASC, "time"));
                List<WindLog> alarmLogs = mongoTemplate.find(query, WindLog.class, windLog_);
                return alarmLogs;
            }
            return new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * 查询告警日志分页
     *
     * @param collectionName
     * @param epId
     * @param startTime      开始时间
     * @param endTime        结束时间
     * @return
     */
    public Map<String, Object> find(String collectionName, String epId, String startTime, String endTime, int limit, int offset) {
        try {
            if (StringUtils.isNotEmpty(collectionName) && StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime)) {
                Criteria criteria = Criteria.where("dateTime").lte(endTime).gte(startTime);
                if (StringUtils.isNotEmpty(epId)) {
//                    Pattern epId_pattern = Pattern.compile("^" + epId, Pattern.CASE_INSENSITIVE);
//                    criteria.andOperator(Criteria.where("epId").is(epId_pattern));
                    criteria.and("epId").is(epId);
                }
                Query query = new Query(criteria);
                query.with(Sort.by(Sort.Direction.ASC, "dateTime"));
                // 设置起始数
                query.skip((offset - 1) * limit)
                        // 设置查询条数
                        .limit(limit);

                // 查询记录总数
                int totalCount = (int) mongoTemplate.count(query, AlarmLog.class, alarmLog_ + collectionName);
                // 数据总页数
                int totalPage = totalCount % limit == 0 ? totalCount / limit : totalCount / limit + 1;

                // 设置记录总数和总页数
                Map<String, Object> resultMap = new HashMap<String, Object>();
                resultMap.put("totalCount", totalCount);
                resultMap.put("totalPage", totalPage);

                List<AlarmLog> alarmLogs = mongoTemplate.find(query, AlarmLog.class, alarmLog_ + collectionName);
                resultMap.put("alarmLogs", alarmLogs);
                return resultMap;
            }
            return new HashMap<String, Object>();
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<String, Object>();
        }
    }

    /**
     * 查询告警日志:时间范围
     *
     * @param collectionName
     * @param startTime      开始时间
     * @param endTime        结束时间
     * @return
     */
    public List<AlarmLog> findByTime(String collectionName, String startTime, String endTime) {
        try {
            if (StringUtils.isNotEmpty(collectionName) && StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime)) {
                Query query = new Query(Criteria.where("dateTime").lte(endTime).gte(startTime));
                query.with(Sort.by(Sort.Direction.ASC, "dateTime"));
                List<AlarmLog> alarmLogs = mongoTemplate.find(query, AlarmLog.class, collectionName);
                return alarmLogs;
            }
            return new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * 分页查询
     *
     * @param collectionName 集合名称
     * @param currentPage    当前页数
     * @param pageSize       页大小
     * @return
     */
    public List<AlarmLog> findByPage(String collectionName, Integer currentPage, Integer pageSize) {
        //创建查询对象
        Query query = new Query();
        //设置起始数
        query.skip((currentPage - 1) * pageSize);
        //设置查询条数
        query.limit(pageSize);
        //查询当前页数据集合
        List<AlarmLog> alarmLogList = mongoTemplate.find(query, AlarmLog.class, collectionName);
        //查询总记录数
        //int count = (int) mongoTemplate.count(query, AlarmLog.class);
        return alarmLogList;
    }

    /**
     * 获取集合记录个数
     *
     * @param collectionName 集合名称
     * @return
     */
    public long count(String collectionName) {
        return mongoTemplate.getCollection(collectionName).countDocuments();
    }

    /**
     * 保存告警日志（插入或更新）
     *
     * @param alarmLog
     * @return
     */
    public Result<?> save(AlarmLog alarmLog, String collectionName) {

        mongoTemplate.save(alarmLog, collectionName);

        return Result.ok("保存成功");
    }

    /**
     * 分页，查询所有告警日志
     *
     * @param currentPage
     * @param pageSize
     * @return
     */
    public Map<String, Object> queryAllAlarmLog(String collectionName, Integer currentPage, Integer pageSize, String startDate,String endDate,String epId) {
        Map<String, Object> pageMap = new HashMap<>();
        // 1. 查所有集合数据
        Query query = new Query();
        if (StringUtils.isNotEmpty(startDate))
            query.addCriteria(Criteria.where("dateTime").gte(startDate).lte(endDate));
        if (StringUtils.isNotEmpty(epId))
            query.addCriteria(Criteria.where("epId").is(epId));
        pageMap.put("count", mongoTemplate.count(query, alarmLog_ + collectionName));
        query.skip((currentPage - 1) * pageSize);
        query.limit(pageSize);
        List<AlarmLog> alarmLogs = mongoTemplate.find(query, AlarmLog.class, alarmLog_ + collectionName);
        pageMap.put("alarmLogs", alarmLogs);
//        // 2. 分页
//        int maxPage = alarmLogs.size()/pageSize+1;
//        int toIndex = currentPage*pageSize;
//        int fromIndex = ( currentPage-1)*pageSize;
//        if( currentPage>=maxPage){
//            toIndex = alarmLogs.size();
//            fromIndex = (alarmLogs.size()/pageSize)*pageSize;
//        }
//        alarmLogs.subList(fromIndex,toIndex);
        return pageMap;
    }

    /**
     * 根据id修改记录
     * @param alarmLog
     */
    public UpdateResult updateAlarmLog(AlarmLog alarmLog,String time){
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(alarmLog.getId()));
        UpdateResult result = mongoTemplate.updateFirst(query, Update.update("modifyVal", alarmLog.getModifyVal()),AlarmLog.class,alarmLog_+time);
        return result;
    }



}
