package org.lkdt.modules.weixin.job;

import com.xxl.job.core.handler.annotation.XxlJob;
import org.lkdt.modules.weixin.utils.wx.WxPushUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Dell
 */
@Component
public class WXJob {

    @Autowired
    WxPushUtil wXPushUtil;

    /**
     * Timed tasks for calculating gale data
     *
     * @return
     */
    @XxlJob("wxTokenJobHandler")
    public void wxTokenJob() {
        wXPushUtil.getWXToken();
    }

}
