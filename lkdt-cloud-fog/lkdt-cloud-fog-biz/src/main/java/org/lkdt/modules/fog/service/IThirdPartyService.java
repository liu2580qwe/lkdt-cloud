package org.lkdt.modules.fog.service;

import org.lkdt.modules.fog.calculator.FogCalculator;

import java.util.List;
import java.util.Map;

/**
 * @author Huangjunyao
 * E-mail:897581567@qq.com
 * @version 创建时间：2020年4月24日 上午10:28:25
 * 第三方数据发送接口
 */
public interface IThirdPartyService {

    void thirdPartySend(String url, Map<String, Object> map);

    /**
     * 验证发送能见度数据
     * @param cal
     */
    void checkDistanceSend(FogCalculator cal);
    /**
     * 发送视频拉流异常数据
     * @param epIds
     */
    void sendVideoStreamEx(List<String> epIds);
    /**
     * 发送视频质量异常数据
     * @param cal
     */
    void sendVideoQualityEx(FogCalculator cal);

}
 