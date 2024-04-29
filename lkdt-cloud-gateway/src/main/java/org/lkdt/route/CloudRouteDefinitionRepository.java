package org.lkdt.route;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import lombok.extern.slf4j.Slf4j;
import org.lkdt.utils.GatewayUtils;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class CloudRouteDefinitionRepository implements RouteDefinitionRepository {

    public static final String GATEWAY_ROUTES = "geteway_routes";

//    @Value("${spring.redis.host}")
//@Value("${spring.cloud.nacos.config.shared-configs}")
//    private String host;

    @Resource
    private RedisTemplate redisTemplate ;

    //TODO getRouteDefinitions一直自己在刷新 懵逼中，怎么改成我想让他刷的时候才刷
    //https://blog.csdn.net/yu_kang/article/details/100092967
    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        log.info("---进入动态路由加载---");
//        log.info(host);
        try {
            Object str = redisTemplate.opsForValue().get(GATEWAY_ROUTES);
            if(str==null || str.toString().equals("")){
                log.info("无路由配置");
            }else{
                log.info(str.toString());
                JSONArray array = JSON.parseArray(str.toString());
                List<RouteDefinition> ls = GatewayUtils.getRoutesByJson(array);
                return Flux.fromIterable(ls);
            }
        } catch (Exception e) {
            log.info("GatewayUtils.getRoutesByJson 出错!");
            e.printStackTrace();
        }
        return Flux.fromIterable(new ArrayList<>());
    }

    @Override
    public Mono<Void> save(Mono<RouteDefinition> route) {
        return null;
    }

    @Override
    public Mono<Void> delete(Mono<String> routeId) {
        return null;
    }
}
