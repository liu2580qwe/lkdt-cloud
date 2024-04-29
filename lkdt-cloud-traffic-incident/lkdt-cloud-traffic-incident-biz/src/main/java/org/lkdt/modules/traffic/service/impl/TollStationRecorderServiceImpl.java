package org.lkdt.modules.traffic.service.impl;

import org.lkdt.modules.traffic.entity.TollStationRecorder;
import org.lkdt.modules.traffic.mapper.TollStationRecorderMapper;
import org.lkdt.modules.traffic.service.ITollStationRecorderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: 收费站封路事件详细
 * @Author: jeecg-boot
 * @Date:   2021-06-03
 * @Version: V1.0
 */
@Service
public class TollStationRecorderServiceImpl extends ServiceImpl<TollStationRecorderMapper, TollStationRecorder> implements ITollStationRecorderService {
    @Autowired
    private TollStationRecorderMapper tollStationRecorderMapper;

    @Override
    public List<TollStationRecorder> selectByMainId(String mainId) {
        return tollStationRecorderMapper.selectByMainId(mainId);
    }
}
