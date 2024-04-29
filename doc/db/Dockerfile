FROM mysql:8.0.19

MAINTAINER lkdtos@163.com

ENV TZ=Asia/Shanghai

RUN ln -sf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

COPY ./lkdtcloud_mysql5.7.sql /docker-entrypoint-initdb.d


RUN mkdir -p /lkdt-cloud-mysql
COPY ./nacos-mysql.sql /lkdt-cloud-mysql