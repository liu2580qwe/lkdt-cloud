package org.lkdt.modules.radar.supports.radarDataHandle.extend;

public class RadarMessageText extends RadarMessage<String> {

    @Override
    public Integer getMediaType() {
        return MESSAGE_MEDIA_TYPE_TEXT;
    }
}