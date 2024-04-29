package org.lkdt.modules.radar.supports.radarServer.sync;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.ipfilter.IpFilterRule;
import io.netty.handler.ipfilter.IpFilterRuleType;
import io.netty.handler.ipfilter.RuleBasedIpFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

@Component
public class RadarServerHandlerInitializer extends ChannelInitializer<Channel> {

    /** 
     *@Fields serverHandler : 服务处理
     */ 
    @Autowired
    private RadarServerHandler serverHandler;
    
    
    @Override
    protected void initChannel(Channel ch) throws Exception {

        // 通过socketChannel去获得对应的管道
        ChannelPipeline channelPipeline = ch.pipeline();
        
        
        IpFilterRule rejectAll = new IpFilterRule() {
            @Override
            public boolean matches(InetSocketAddress remoteAddress) {
//            	String clientIP = remoteAddress.getAddress().getHostAddress();
//            	if (clientIP.startsWith("127")) {
//            		return true;
//            	}
                return false;
            }

            @Override
            public IpFilterRuleType ruleType() {
                return IpFilterRuleType.REJECT;
            }
        };
        
        RuleBasedIpFilter filter = new RuleBasedIpFilter(rejectAll );
        channelPipeline.addLast("ipFilter", filter);

        
        channelPipeline.addLast(serverHandler);
    }

}