package org.lkdt.modules.radar.supports.radarServer.sync;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;

@Component
public class RadarServer implements ApplicationListener<ContextRefreshedEvent> {

	private static final Logger logger = LoggerFactory.getLogger(RadarServer.class);
	//
	// /**
	// *@Fields DELIMITER : 自定义分隔符，服务端和客户端要保持一致
	// */
	// public static final String DELIMITER = "@@";

	/**
	 * @Fields boss : boss 线程组用于处理连接工作, 默认是系统CPU个数的两倍，也可以根据实际情况指定
	 */
	private EventLoopGroup boss = new NioEventLoopGroup();

	/**
	 * @Fields work : work 线程组用于数据处理, 默认是系统CPU个数的两倍，也可以根据实际情况指定
	 */
	private EventLoopGroup work = new NioEventLoopGroup();

	/**
	 * @Fields port : 监听端口
	 */
	private Integer port = 602;

	@Autowired
	private RadarServerHandlerInitializer handlerInitializer;

	private static boolean isInit = false;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (isInit) {
			return;
		}

		startNetty();
		isInit = true;
	}


	/**
	 * @throws InterruptedException
	 * @Description: 启动Netty Server
	 * @Since: 2019年9月12日下午4:21:35
	 */
	@PostConstruct
	public void start() throws InterruptedException {

	}

	private void startNetty() {

		try {
			ServerBootstrap bootstrap = new ServerBootstrap();

			bootstrap.group(boss, work)
					// 指定Channel
					.channel(NioServerSocketChannel.class)
					// 使用指定的端口设置套接字地址
					.localAddress(new InetSocketAddress(port))

					// 服务端可连接队列数,对应TCP/IP协议listen函数中backlog参数
					.option(ChannelOption.SO_BACKLOG, 1024)

					// 设置TCP长连接,一般如果两个小时内没有数据的通信时,TCP会自动发送一个活动探测数据报文
					.childOption(ChannelOption.SO_KEEPALIVE, true)

					// 将小的数据包包装成更大的帧进行传送，提高网络的负载,即TCP延迟传输
					.childOption(ChannelOption.TCP_NODELAY, false)
					.childOption(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(2048))
					.childHandler(handlerInitializer);

			ChannelFuture future = bootstrap.bind().sync();

			if (future.isSuccess()) {
				logger.info("启动 Netty Server...");
			}

			// 等待连接被关闭

		} catch (Exception e) {
			logger.error("启动 Netty错误", e);
		} finally {
			// 出现异常终止
		}

	}

	@PreDestroy
	public void destory() throws InterruptedException {
		boss.shutdownGracefully().sync();
		work.shutdownGracefully().sync();
		logger.info("关闭Netty...");
	}
}