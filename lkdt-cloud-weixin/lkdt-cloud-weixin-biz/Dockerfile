FROM anapsix/alpine-java:8_server-jre_unlimited

MAINTAINER lkdtos@163.com

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime

RUN mkdir -p /lkdt-cloud-weixin-biz

WORKDIR /ROOT

EXPOSE 8096

ADD ./target/lkdt-cloud-weixin-biz-2.2.0.jar ./

CMD java -Djava.security.egd=file:/dev/./urandom -jar -Duser.timezone=GMT+08 lkdt-cloud-weixin-biz-2.2.0.jar
