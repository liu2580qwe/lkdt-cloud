package org.lkdt.modules.radar.supports.mongodb;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import org.lkdt.modules.radar.supports.radarTools.DO.MongoZcLdEventRadarInfo;
import org.bson.Document;
import org.lkdt.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * mongo操作
 *
 * @create: 2021-05-27
 **/
@Component
public class MongoRadarTemplate {

    @Autowired
    MongoTemplate mongoTemplate;

    /**mongo集合列表*/
    private Set<String> collectionNames = null;

    public Set<String> getCollectionNames() {
        return collectionNames;
    }

    /**
     * 初始化mongo集合列表
     */
    @PostConstruct
    public void initMongoCellections(){
        collectionNames = mongoTemplate.getCollectionNames();
    }

    /**yyyyMM*/
    public final String str_ymd = "yyyyMM";

    /**yyyy-MM-dd HH:mm:ss.SSS*/
    public final String str_dateTime = "yyyy-MM-dd HH:mm:ss.SSS";

    public final String MongoZcLdEventRadarInfo_ = "MongoZcLdEventRadarInfo_";

    /**
     * 插入告警日志
     * @param mongoZcLdEventRadarInfo
     * @return
     */
    public boolean insert(MongoZcLdEventRadarInfo mongoZcLdEventRadarInfo){
        SimpleDateFormat sdf_ymd = new SimpleDateFormat(str_ymd);
        if(StringUtils.isEmpty(mongoZcLdEventRadarInfo.getId())){
            return false;
        }
        Date date = mongoZcLdEventRadarInfo.getCreateTime();
        String string = sdf_ymd.format(date);
        if(StringUtils.isEmpty(string)){
            return false;
        }
        String collectionName = MongoZcLdEventRadarInfo_ + string;

        //建立索引
        if(!collectionNames.contains(collectionName)){
            this.dbIndex(collectionName);
        }

        mongoTemplate.insert(mongoZcLdEventRadarInfo, collectionName);

        return true;
    }

    /**
     * 创建索引:未创建集合则创建,已有集合不创建
     * @param collectionName 集合名称
     */
    private void dbIndex(String collectionName){
        //数据库实施集合获取
        collectionNames = mongoTemplate.getCollectionNames();
        if(!collectionNames.contains(collectionName)){
            //创建集合
            MongoCollection<Document> mongoCollection = mongoTemplate.getCollection(collectionName);
            //同步内存集合名称
            collectionNames.add(collectionName);
            //该参数为索引的属性配置
            IndexOptions idx_createTime = new IndexOptions();
            idx_createTime.background(true);
            idx_createTime.name("idx_createTime");
            mongoCollection.createIndex(Indexes.ascending("createTime"), idx_createTime);
            //Document document = new Document("dateTime", "hashed");
            // Document key为索引的列名称，value为索引类型，在userId上创建hashed类型索引
            IndexOptions indexOptions_id = new IndexOptions();
            indexOptions_id.background(true);
            indexOptions_id.name("idx_id");
            mongoCollection.createIndex(Indexes.ascending("id"), indexOptions_id);
        }
    }

    /**
     * 批量插入
     * @param mongoZcLdEventRadarInfos 信息
     * @param date 创建时间
     * @return
     */
    public boolean insert(List<MongoZcLdEventRadarInfo> mongoZcLdEventRadarInfos, Date date){
        SimpleDateFormat sdf_ymd = new SimpleDateFormat(str_ymd);
        String collectionName = MongoZcLdEventRadarInfo_ + sdf_ymd.format(date);

        //建立索引
        if(!collectionNames.contains(collectionName)){
            this.dbIndex(collectionName);
        }

        //批量插入
        mongoTemplate.insert(mongoZcLdEventRadarInfos, collectionName);

        return true;
    }

    /**
     * 查询id
     * @return
     */
    public MongoZcLdEventRadarInfo find(String collectionName, String id){
        try {
            if(StringUtils.isNotEmpty(collectionName)){
                MongoZcLdEventRadarInfo mongoZcLdEventRadarInfo = mongoTemplate.findById(id, MongoZcLdEventRadarInfo.class, collectionName);
                return mongoZcLdEventRadarInfo;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 查询告警日志:正则左匹配
     * @return
     */
    public List<MongoZcLdEventRadarInfo> find(String collectionName, String id, String prefixTime){
        try {
            if(StringUtils.isNotEmpty(collectionName) && StringUtils.isNotEmpty(prefixTime)){
                Pattern epId_pattern = Pattern.compile("^" + id, Pattern.CASE_INSENSITIVE);
                Pattern pattern = Pattern.compile("^" + prefixTime + ".*$", Pattern.CASE_INSENSITIVE);
                Query query = new Query(Criteria.where("createTime").regex(pattern).and("equId").regex(epId_pattern));
                query.with(Sort.by(Sort.Direction.ASC, "createTime"));
                List<MongoZcLdEventRadarInfo> mongoZcLdEventRadarInfos = mongoTemplate.find(query, MongoZcLdEventRadarInfo.class, MongoZcLdEventRadarInfo_ + collectionName);
                return mongoZcLdEventRadarInfos;
            }
            return new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * 查询告警日志
     * @param collectionName
     * @param id
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return
     */
    public List<MongoZcLdEventRadarInfo> find(String collectionName, String id, String startTime, String endTime){
        try {
            if(StringUtils.isNotEmpty(collectionName) && StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime)){
                Criteria criteria = Criteria.where("createTime").lte(endTime).gte(startTime);
                if(StringUtils.isNotEmpty(id)){
//                    Pattern epId_pattern = Pattern.compile("^" + epId, Pattern.CASE_INSENSITIVE);
//                    criteria.andOperator(Criteria.where("epId").is(epId_pattern));
                    criteria.and("epId").is(id);
                }
                Query query = new Query(criteria);
                query.with(Sort.by(Sort.Direction.ASC, "createTime"));
                List<MongoZcLdEventRadarInfo> mongoZcLdEventRadarInfos = mongoTemplate.find(query, MongoZcLdEventRadarInfo.class, MongoZcLdEventRadarInfo_ + collectionName);
                return mongoZcLdEventRadarInfos;
            }
            return new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * 查询告警日志分页
     * @param collectionName
     * @param id
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return
     */
    public Map<String, Object> find(String collectionName, String id, String startTime, String endTime, int limit, int offset){
        try {
            if(StringUtils.isNotEmpty(collectionName) && StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime)){
                Criteria criteria = Criteria.where("createTime").lte(endTime).gte(startTime);
                if(StringUtils.isNotEmpty(id)){
//                    Pattern epId_pattern = Pattern.compile("^" + epId, Pattern.CASE_INSENSITIVE);
//                    criteria.andOperator(Criteria.where("epId").is(epId_pattern));
                    criteria.and("epId").is(id);
                }
                Query query = new Query(criteria);
                query.with(Sort.by(Sort.Direction.ASC, "createTime"));
                // 设置起始数
                query.skip((offset - 1) * limit)
                        // 设置查询条数
                        .limit(limit);

                // 查询记录总数
                int totalCount = (int) mongoTemplate.count(query, MongoZcLdEventRadarInfo.class, MongoZcLdEventRadarInfo_ + collectionName);
                // 数据总页数
                int totalPage = totalCount % limit == 0 ? totalCount / limit : totalCount / limit + 1;

                // 设置记录总数和总页数
                Map<String, Object> resultMap = new HashMap<String, Object>();
                resultMap.put("totalCount", totalCount);
                resultMap.put("totalPage", totalPage);

                List<MongoZcLdEventRadarInfo> mongoZcLdEventRadarInfo = mongoTemplate.find(query, MongoZcLdEventRadarInfo.class, MongoZcLdEventRadarInfo_ + collectionName);
                resultMap.put("mongoZcLdEventRadarInfo", mongoZcLdEventRadarInfo);
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
     * @param collectionName
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return
     */
    public List<MongoZcLdEventRadarInfo> findByTime(String collectionName, String startTime, String endTime){
        try {
            if(StringUtils.isNotEmpty(collectionName) && StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime)){
                Query query = new Query(Criteria.where("createTime").lte(endTime).gte(startTime));
                query.with(Sort.by(Sort.Direction.ASC, "createTime"));
                List<MongoZcLdEventRadarInfo> mongoZcLdEventRadarInfos = mongoTemplate.find(query, MongoZcLdEventRadarInfo.class, collectionName);
                return mongoZcLdEventRadarInfos;
            }
            return new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * 分页查询
     * @param collectionName 集合名称
     * @param currentPage 当前页数
     * @param pageSize 页大小
     * @return
     */
    public List<MongoZcLdEventRadarInfo> findByPage(String collectionName, Integer currentPage, Integer pageSize){
        //创建查询对象
        Query query = new Query();
        //设置起始数
        query.skip((currentPage - 1) * pageSize);
        //设置查询条数
        query.limit(pageSize);
        //查询当前页数据集合
        List<MongoZcLdEventRadarInfo> mongoZcLdEventRadarInfos = mongoTemplate.find(query, MongoZcLdEventRadarInfo.class, collectionName);
        //查询总记录数
        //int count = (int) mongoTemplate.count(query, MongoZcLdEventRadarInfo.class);
        return mongoZcLdEventRadarInfos;
    }

    /**
     * 获取集合记录个数
     * @param collectionName 集合名称
     * @return
     */
    public long count(String collectionName){
        return mongoTemplate.getCollection(collectionName).countDocuments();
    }

}
