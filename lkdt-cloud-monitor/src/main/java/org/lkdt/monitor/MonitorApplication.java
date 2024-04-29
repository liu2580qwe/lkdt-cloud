package org.lkdt.monitor;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Description: Spring Boot admin监控服务
 * @author: scott
 * @date: 2020年05月17日 18:25
 */
@SpringBootApplication
@EnableAdminServer
public class MonitorApplication {
    public static void main(String[] args) {
        try {
            SpringApplication.run(MonitorApplication.class);
        } catch (Exception e) {
            System.err.println("Monitor启动报错 :" + e);
            System.exit(0);
        }
    }
}
