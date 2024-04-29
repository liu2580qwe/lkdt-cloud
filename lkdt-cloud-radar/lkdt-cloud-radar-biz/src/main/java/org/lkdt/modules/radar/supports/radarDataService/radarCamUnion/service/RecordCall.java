package org.lkdt.modules.radar.supports.radarDataService.radarCamUnion.service;

import cn.hutool.json.JSONObject;

public interface RecordCall {

    /**
     * 事件：录屏后
     * @param eventType 事件类型
     * @param videoDir 视频url
     * @return
     */
    public void callback(String eventType, String videoDir);

}
