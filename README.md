Jeecg-Cloud 快速开发平台
===============

当前最新版本： 1.0.0-beta（发布日期：20200526）

- 基于Spring Boot 2.1.3、 Spring Cloud Greenwich.SR3 & Alibaba、 Shiro、JWT 的快速开发平台
- 前端采用 ant-design-vue，简单易用封装各种组件，轻松满足企业级项目需求
- 更多功能介绍，请参考 [JeecgBoot版本](https://github.com/zhangdaiscott/jeecg-boot)

    <a href="http://doc.cloud.jeecg.com" target="_blank">部署文档</a>  | <a href="https://www.bilibili.com/video/BV1pV411r7xW" target="_blank">入门视频</a>  |  <a target="_blank" href="http://boot.jeecg.com"> 在线体验</a> | <a target="_blank" href="http://www.jeecg.com"> 官方网站</a> 

### 技术体系


- 1、Nacos 服务注册和发现
- 2、Nacos 统一配置中心
- 3、熔断降级限流 sentinel
- 4、feign配合sentinel使用
- 5、SpringCloud Gateway
- 6、JWT + Shiro 权限控制
- 7、分布式文件系统 Minio、阿里OSS
- 8、服务监控 actuator
- 9、Spring Boot Admin服务监控
- 10、链路跟踪 SkyWalking
- 11、Spring Cloud Stream+RabbitMQ
- 12、分布式事务 Seata
- 13、分布式定时任务 XXL-JOB
- 14、ELK分布式日志
- 15、支持 jenkins、docker-compose、k8s

## 系统架构图

![输入图片说明](https://static.jeecg.com/upload/test/jeecg-cloud%E5%BE%AE%E6%9C%8D%E5%8A%A1%E6%9E%B6%E6%9E%84%E5%9B%BE%20mini_1590566044258.png "在这里输入图片标题")

## 项目关系图
![](https://static.jeecg.com/upload/test/jeecg_cloud_project_ref_1591173131283.png)


## 技术架构
- 基础框架：Spring Boot 2.1.3.RELEASE
-  Spring Cloud Greenwich.SR3
-  Spring Cloud Alibaba 2.1.0.RELEASE
- 持久层框架：Mybatis-plus_3.1.2
- 安全框架：Apache Shiro 1.4.0，Jwt_3.7.0
- 数据库连接池：阿里巴巴Druid 1.1.10
- 缓存框架：redis
- 日志打印：logback
- 其他：fastjson，poi，Swagger-ui，quartz, lombok（简化代码）等。



## 开发环境

- 语言：Java 8
- IDE： IDEA 或 Eclipse （安装lombok插件）
- 依赖管理：Maven
- 数据库：MySQL5.7  &  Oracle 11g
- 缓存：Redis

#### 核心依赖 


依赖 | 版本
---|---
Spring Boot |  Spring Boot 2.1.3.RELEASE
Spring Cloud | Greenwich.SR3
Spring Cloud Alibaba  | 2.1.0.RELEASE
Mybatis Plus | 3.1.2
Shiro | 1.4.0
Jwt | 3.7.0

#### 模块说明
```
# 前端项目源码
ant-design-vue-jeecg  --> https://github.com/zhangdaiscott/jeecg-boot/tree/master/ant-design-vue-jeecg

# 后端项目结构
jeecg-cloud
├── jeecg-common -- 系统公共模块 
│    ├── jeecg-common-core -- 公共工具类核心包
│    ├── jeecg-common-cloud -- 集成springcloud依赖
│    ├── jeecg-common-cloud-starter -- jeecg cloud quick starter
├── jeecg-cloud-gateway -- Spring Cloud Gateway网关[9999]
├── jeecg-cloud-monitor -- Spring Boot admin监控服务[9111]
├── jeecg-cloud-system -- 用户权限系统管理模块
│    └── jeecg-cloud-system-api -- 用户权限管理系统——公共api
│    └── jeecg-cloud-system-biz -- 用户权限管理系统——业务管理模块[8001]
├── jeecg-cloud-demo -- 微服务示例模块
│    ├── jeecg-cloud-demo-api -- 微服务示例模块——公共api
│    ├── jeecg-cloud-demo-biz -- 微服务示例模块——业务管理模块[8002]
```

### 聚合swaggerAPI文档
```
http://localhost:9999/doc.html
```
### Admin监控
```
http://localhost:9111 (admin/admin)
```

### 测试请求

```
-  直接访问demo服务请求
http://localhost:8002/demo/hello

-  直接访问system服务请求
http://localhost:8001/sys/randomImage/12121

-  网关跳转demo请求：
http://localhost:9999/demo/hello

-  网关跳转system服务请求
验证码：  http://localhost:9999/sys/randomImage/12121
登录接口：http://localhost:9999/sys/login
```

登录参数 JSON
 ``` xml
{
	"captcha": "aqun",
	"checkKey": "12121",
	"password": "123456",
	"username": "admin"
}
 ```
 
### Docker Compose 镜像制作
 ``` 
# 1.先进行项目打包
mvn clean package

# 2.重新构建镜像__容器组（当你改变本地代码）
docker-compose build

# 3.取代运行中的镜像__容器组
docker-compose up -d

# 4.nacos中创建俩配置文件
参考：doc/NACOSCONFIG
``` 


### Docker其他命令备用
 ``` 
# 创建单个镜像
docker build -t jeecg-system/centos:v1 . 
# 启动单个镜像
docker run -p 8001:8001 jeecg-system/centos:v1

# 登录数据库
mysql -u root -p
use jeecg-cloud
show tables


自定义Docker容器的 hostname
 ``` 