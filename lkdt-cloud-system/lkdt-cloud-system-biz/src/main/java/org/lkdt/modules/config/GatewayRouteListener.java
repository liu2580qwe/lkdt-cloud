package org.lkdt.modules.config;

import lombok.extern.slf4j.Slf4j;
import org.lkdt.common.constant.CacheConstant;
import org.lkdt.modules.system.service.ISysGatewayRouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

/**
 * 路由重置监听
 * WebServerInitializedEvent.class 和 implements ApplicationRunner均可实现项目启动初始化路由到redis
 * 但是此处多了一个GatewayRouteInitEvent 所以用这种写法
 */
@Configuration
@Slf4j
public class GatewayRouteListener {

    @Autowired
    private ISysGatewayRouteService sysGatewayRouteService;

    @Async
    @EventListener({WebServerInitializedEvent.class, GatewayRouteInitEvent.class})
    public void initRoute() {
        log.info(" GatewayRouteListener 加载路由信息到redis>>");
        sysGatewayRouteService.addRoute2Redis(CacheConstant.GATEWAY_ROUTES);
    }
}
