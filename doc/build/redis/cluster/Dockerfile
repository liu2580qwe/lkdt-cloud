FROM redis:5.0

MAINTAINER jzyadmin@163.com

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime

RUN mkdir -p /lkdt-cloud-redis

WORKDIR /lkdt-cloud-redis

ADD sentinel.conf ./

CMD redis-server
