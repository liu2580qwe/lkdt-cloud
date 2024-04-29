package org.lkdt.config;

import io.lettuce.core.ReadFrom;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lkdt.utils.GatewayUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import java.time.Duration;

/**
 * 动态路由配置类
 */
@Slf4j
@Configuration
@AllArgsConstructor
public class DynamicRouteAutoConfiguration {

    private ApplicationEventPublisher applicationEventPublisher;

    /**
     * 配置文件设置为空
     * redis 加载为准
     *
     * @return
     */
    /*@Bean
    public PropertiesRouteDefinitionLocator propertiesRouteDefinitionLocator() {
        return new PropertiesRouteDefinitionLocator(new GatewayProperties());
    }*/

    /**
     * redis 监听配置
     * @param redisConnectionFactory redis 配置
     * @return
     */
    @Bean
    public RedisMessageListenerContainer redisContainer(RedisConnectionFactory redisConnectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.addMessageListener((message, bytes) -> {
            log.info("接收到重新JVM 重新加载路由事件");
            applicationEventPublisher.publishEvent(new RefreshRoutesEvent(this));
        }, new ChannelTopic(GatewayUtils.ROUTE_JVM_RELOAD_TOPIC));
        return container;
    }


    @Bean
    @ConditionalOnProperty(value = "spring.redis.cluster.enable",havingValue = "true")
    public LettuceConnectionFactory redisConnectionFactory(RedisProperties redisProperties) {
        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration(redisProperties.getCluster().getNodes());
        // https://github.com/lettuce-io/lettuce-core/wiki/Redis-Cluster#user-content-refreshing-the-cluster-topology-view
        ClusterTopologyRefreshOptions clusterTopologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
                .enablePeriodicRefresh()
                .enableAllAdaptiveRefreshTriggers()
                .refreshPeriod(Duration.ofSeconds(5))
                .build();

        ClusterClientOptions clusterClientOptions = ClusterClientOptions.builder()
                .topologyRefreshOptions(clusterTopologyRefreshOptions).build();
        // https://github.com/lettuce-io/lettuce-core/wiki/ReadFrom-Settings
        LettuceClientConfiguration lettuceClientConfiguration = LettuceClientConfiguration.builder()
                //TODO 这块参考别人写的 但是没有他的那个参数 ReadFrom.REPLICA_PREFERRED 所以暂时使用master
                .readFrom(ReadFrom.MASTER_PREFERRED)
                .clientOptions(clusterClientOptions).build();

        return new LettuceConnectionFactory(redisClusterConfiguration, lettuceClientConfiguration);
    }
}