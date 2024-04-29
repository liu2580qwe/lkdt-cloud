FROM anapsix/alpine-java:8_server-jre_unlimited

MAINTAINER lkdtos@163.com

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime

RUN mkdir -p /lkdt-cloud-gateway

WORKDIR /ROOT

EXPOSE 9999

ADD ./target/lkdt-cloud-gateway-2.2.0.jar ./

CMD java -Djava.security.egd=file:/dev/./urandom -jar lkdt-cloud-gateway-2.2.0.jar
