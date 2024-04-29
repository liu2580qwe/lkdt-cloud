package org.lkdt.modules.radar.supports.radarServer.sync;

import com.google.common.cache.*;
import org.lkdt.modules.radar.supports.radarServer.ConvertCode;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

@Component("radarMessageTools")
public class RadarMessageTools {

	private static final Logger LOGGER = LoggerFactory.getLogger(RadarMessageTools.class);

	private static final int timeOut = 30;
	// 缓存接口这里是LoadingCache，LoadingCache在缓存项不存在时可以自动加载缓存
	private static LoadingCache<String, SyncFuture<String>> futureCache = CacheBuilder
			.newBuilder()
			// 设置缓存容器的初始容量为10
			.initialCapacity(100)
			// maximumSize 设置缓存大小
			.maximumSize(10000)
			// 设置并发级别为20，并发级别是指可以同时写缓存的线程数
			.concurrencyLevel(20)
			// expireAfterWrite设置写缓存后8秒钟过期
			.expireAfterWrite(timeOut, TimeUnit.SECONDS)
			// 设置缓存的移除通知
			.removalListener(new RemovalListener<Object, Object>() {
				@Override
				public void onRemoval(
						RemovalNotification<Object, Object> notification) {
					LOGGER.debug("LoadingCache: {} was removed, cause is {}",
							notification.getKey(), notification.getCause());
				}
			})
			// build方法中可以指定CacheLoader，在缓存不存在时通过CacheLoader的实现自动加载缓存
			.build(new CacheLoader<String, SyncFuture<String>>() {
				@Override
				public SyncFuture<String> load(String key) throws Exception {
					// 当获取key的缓存不存在时，不需要自动添加
					return null;
				}
			});

	public boolean sendMsg(final String text, ChannelHandlerContext ctx) {

		ByteBuf bufff = Unpooled.buffer();// netty需要用ByteBuf传输
		bufff.writeBytes(ConvertCode.hexString2Bytes(text));// 对接需要16进制

		ChannelFuture future = ctx.writeAndFlush(bufff);
		future.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future)
					throws Exception {

				if (future.isSuccess()) {
					LOGGER.info("===========发送异步成功:" + text);
				} else {
					LOGGER.info("------------------发送异步失败:" + text);
				}
			}
		});
		return true;
	}

	public String sendSyncMsg(final String text, String orgCode) {
		ChannelHandlerContext ctx = RadarServerHandler.ONLINE_BRIDGES.get(orgCode);
		if (ctx == null) {
			return "";
		}
		return this.sendSyncMsg(text, orgCode, ctx);
	}

	public String sendSyncMsg(final String text, final String brgid,
			ChannelHandlerContext ctx) {
		String result = "";

		try {
			if (futureCache.getIfPresent(brgid) != null) {
				return "";
			}

			SyncFuture<String> syncFuture = new SyncFuture<String>();
			// 放入缓存中
			futureCache.put(brgid, syncFuture);

			ByteBuf bufff = Unpooled.buffer();// netty需要用ByteBuf传输
			bufff.writeBytes(ConvertCode.hexString2Bytes(text));// 对接需要16进制

			ChannelFuture future = ctx.writeAndFlush(bufff);
			future.addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture future)
						throws Exception {
					InetSocketAddress insocket = (InetSocketAddress) future
							.channel().remoteAddress();
					String clientIP = insocket.getAddress().getHostAddress();
					if (future.isSuccess()) {
						LOGGER.info("===========发送成功:" + text + "   桥: "
								+ RadarServerHandler.getBridgeName(brgid)
								+ "......" + clientIP);
					} else {
						LOGGER.info("------------------发送失败:" + text
								+ "   桥: "
								+ RadarServerHandler.getBridgeName(brgid)
								+ "......" + clientIP);
					}
				}
			});
			// 等待 8 秒
			result = syncFuture.get(5, TimeUnit.SECONDS);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;

	}

	public void ackSyncMsg(String dataId, String msg) {
		// 从缓存中获取数据
		SyncFuture<String> syncFuture = futureCache.getIfPresent(dataId);

		// 如果不为null, 则通知返回
		if (syncFuture != null) {
			syncFuture.setResponse(msg);
			// 主动释放
			futureCache.invalidate(dataId);
		}
	}

}