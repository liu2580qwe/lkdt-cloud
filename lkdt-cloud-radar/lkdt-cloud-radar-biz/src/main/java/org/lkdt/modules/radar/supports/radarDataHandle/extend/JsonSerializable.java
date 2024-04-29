package org.lkdt.modules.radar.supports.radarDataHandle.extend;


import cn.hutool.json.JSONUtil;

public interface JsonSerializable {

    default String toJsonString() {
        return JSONUtil.parse(this).toStringPretty();
    }

}