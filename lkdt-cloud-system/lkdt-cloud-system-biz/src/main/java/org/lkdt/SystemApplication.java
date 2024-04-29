package org.lkdt;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.core.env.Environment;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
@EnableSwagger2
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan({"org.lkdt.modules.**.mapper*","org.jeecg.modules.**.mapper*"})
public class SystemApplication {

  public static void main(String[] args) throws UnknownHostException {

    ConfigurableApplicationContext application = SpringApplication.run(SystemApplication.class, args);
    Environment env = application.getEnvironment();
    String ip = InetAddress.getLocalHost().getHostAddress();
    String port = env.getProperty("server.port");
    log.info("\n----------------------------------------------------------\n\t" +
        "Application lkdt-Boot is running! Access URLs:\n\t" +
        "Local: \t\thttp://localhost:" + port  + "/\n\t" +
        "External: \thttp://" + ip + ":" + port  + "/\n\t" +
        "Swagger-UI: \t\thttp://" + ip + ":" + port  + "/doc.html\n" +
        "----------------------------------------------------------");

  }
}