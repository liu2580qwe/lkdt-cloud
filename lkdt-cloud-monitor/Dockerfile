FROM anapsix/alpine-java:8_server-jre_unlimited

MAINTAINER lkdtos@163.com

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime

RUN mkdir -p /lkdt-cloud-monitor

WORKDIR /ROOT

EXPOSE 9111

ADD ./target/lkdt-cloud-monitor-2.2.0.jar ./

CMD java -Djava.security.egd=file:/dev/./urandom -jar lkdt-cloud-monitor-2.2.0.jar
