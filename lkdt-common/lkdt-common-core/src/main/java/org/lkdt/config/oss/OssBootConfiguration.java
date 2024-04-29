package org.lkdt.config.oss;

import org.lkdt.common.util.oss.OssBootUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OssBootConfiguration {

    @Value("${lkdt.oss.endpoint}")
    private String endpoint;
    @Value("${lkdt.oss.accessKey}")
    private String accessKeyId;
    @Value("${lkdt.oss.secretKey}")
    private String accessKeySecret;
    @Value("${lkdt.oss.bucketName}")
    private String bucketName;
    @Value("${lkdt.oss.staticDomain}")
    private String staticDomain;


    @Bean
    public void initOssBootConfiguration() {
        OssBootUtil.setEndPoint(endpoint);
        OssBootUtil.setAccessKeyId(accessKeyId);
        OssBootUtil.setAccessKeySecret(accessKeySecret);
        OssBootUtil.setBucketName(bucketName);
        OssBootUtil.setStaticDomain(staticDomain);
        OssBootUtil.initOSS();
    }
}