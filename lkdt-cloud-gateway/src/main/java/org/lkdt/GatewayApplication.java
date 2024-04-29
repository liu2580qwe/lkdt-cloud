package org.lkdt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@EnableDiscoveryClient
public class GatewayApplication {

    public static void main(String[] args) {
        try {
            ConfigurableApplicationContext applicationContext = SpringApplication.run(GatewayApplication.class, args);
            String userName = applicationContext.getEnvironment().getProperty("lkdt.test");
            System.err.println("user name :" + userName);
        } catch (Exception e) {
            System.err.println("Gateway启动报错 :" + e);
            System.exit(0);
        }

    }

//    //@Bean
//    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
//        return builder.routes()
//                .route("path_route", r -> r.path("/get")
//                        .uri("http://httpbin.org"))
//                .route("baidu_path_route", r -> r.path("/baidu")
//                        .uri("https://news.baidu.com/guonei"))
//                .route("host_route", r -> r.host("*.myhost.org")
//                        .uri("http://httpbin.org"))
//                .route("rewrite_route", r -> r.host("*.rewrite.org")
//                        .filters(f -> f.rewritePath("/foo/(?<segment>.*)", "/${segment}"))
//                        .uri("http://httpbin.org"))
//                .route("hystrix_route", r -> r.host("*.hystrix.org")
//                        .filters(f -> f.stripPrefix(1).hystrix(c -> c.setName("slowcmd")))
//                        .uri("http://httpbin.org"))
//                .route("hystrix_fallback_route", r -> r.host("*.hystrixfallback.org")
//                        .filters(f -> f.stripPrefix(1).hystrix(c -> c.setName("slowcmd").setFallbackUri("forward:/hystrixfallback")))
//                        .uri("http://httpbin.org"))
//                .build();
//    }

}