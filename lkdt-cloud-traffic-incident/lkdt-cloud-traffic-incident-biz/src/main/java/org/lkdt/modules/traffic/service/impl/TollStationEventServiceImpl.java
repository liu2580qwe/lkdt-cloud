package org.lkdt.modules.traffic.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.lkdt.modules.traffic.entity.TollStationEvent;
import org.lkdt.modules.traffic.entity.TollStationRecorder;
import org.lkdt.modules.traffic.mapper.TollStationEventMapper;
import org.lkdt.modules.traffic.mapper.TollStationRecorderMapper;
import org.lkdt.modules.traffic.service.ITollStationEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @Description: 收费站封路事件
 * @Author: jeecg-boot
 * @Date:   2021-06-03
 * @Version: V1.0
 */
@Service
public class TollStationEventServiceImpl extends ServiceImpl<TollStationEventMapper, TollStationEvent> implements ITollStationEventService {
    @Autowired
    private  TollStationEventMapper TollStationEventMapper;
    @Autowired
    private TollStationRecorderMapper tollStationRecorderMapper;

    @Override
    @Transactional
    public void saveMain(TollStationEvent TollStationEvent, List<TollStationRecorder> tollStationRecorderList) {
        TollStationEventMapper.insert(TollStationEvent);
        if(tollStationRecorderList!=null && tollStationRecorderList.size()>0) {
            for(TollStationRecorder entity:tollStationRecorderList) {
                //外键设置
                entity.setEventid(TollStationEvent.getId());
                tollStationRecorderMapper.insert(entity);
            }
        }
    }

    @Override
    @Transactional
    public void updateMain(TollStationEvent TollStationEvent, List<TollStationRecorder> tollStationRecorderList) {
        TollStationEventMapper.updateById(TollStationEvent);

        //1.先删除子表数据
        tollStationRecorderMapper.deleteByMainId(TollStationEvent.getId());

        //2.子表数据重新插入
        if(tollStationRecorderList!=null && tollStationRecorderList.size()>0) {
            for(TollStationRecorder entity:tollStationRecorderList) {
                //外键设置
                entity.setEventid(TollStationEvent.getId());
                tollStationRecorderMapper.insert(entity);
            }
        }
    }

    @Override
    @Transactional
    public void delMain(String id) {
        tollStationRecorderMapper.deleteByMainId(id);
        TollStationEventMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void delBatchMain(Collection<? extends Serializable> idList) {
        for(Serializable id:idList) {
            tollStationRecorderMapper.deleteByMainId(id.toString());
            TollStationEventMapper.deleteById(id);
        }
    }
}
