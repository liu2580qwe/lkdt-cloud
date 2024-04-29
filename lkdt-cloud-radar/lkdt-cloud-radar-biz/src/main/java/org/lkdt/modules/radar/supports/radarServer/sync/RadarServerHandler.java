package org.lkdt.modules.radar.supports.radarServer.sync;

import org.lkdt.modules.radar.supports.radarServer.ConvertCode;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@ChannelHandler.Sharable
// 标注一个channel handler可以被多个channel安全地共享
public class RadarServerHandler extends SimpleChannelInboundHandler<ByteBuf> {

	private static final Logger logger = LoggerFactory.getLogger(RadarServerHandler.class);

	public static AtomicInteger nConnection = new AtomicInteger(0);

	public static ConcurrentSkipListMap<String, ChannelHandlerContext> ONLINE_BRIDGES = new ConcurrentSkipListMap<String, ChannelHandlerContext>();

	private AttributeKey<String> key = AttributeKey.valueOf("orgcode");

	public void registerChannel(String brgid, ChannelHandlerContext ctx) {

		if (ONLINE_BRIDGES.containsValue(ctx)) {
			return;
		}
		// 防止重复注册
		if (ONLINE_BRIDGES.containsKey(brgid)) {
			ctx.channel().close();
			return;
		}
		//ZcBridgeInfo brg = zcBridgeInfoService.getById(brgid);
//		if (brg != null) {
//			Attribute<String> channelAttr = ctx.channel().attr(key);
//			channelAttr.set(brgid);
//			ONLINE_BRIDGES.put(brgid, ctx);
//			all_bridges.put(brgid, brg);
//		} else {
//			ctx.channel().close();
//		}
		ctx.channel().close();

	}

	public static String getBridgeName(String id) {
//		if (all_bridges.isEmpty()) {
//			return id;
//		} else {
//			if (StringUtils.isEmpty(id)) {
//				return "未知连接";
//			} else {
//				return id + all_bridges.get(id).getBridgeName();
//			}
//		}
		return null;
	}

	@Autowired
	private RadarMessageTools radarMessageTools;

	@Override
	protected void channelRead0(final ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
		// String txt = msg.toString(CharsetUtil.UTF_8);
		// LOGGER.info("收到客户端的消息：{}", txt);
		// ackMessage(ctx, txt);

		byte[] result1 = new byte[msg.readableBytes()];
		// msg中存储的是ByteBuf类型的数据，把数据读取到byte[]中
		msg.readBytes(result1);
		String message = ConvertCode.receiveHexToString(result1);
		if (StringUtils.isEmpty(message)) {
			ctx.flush();
			return;
		}
		InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
		String clientIP = insocket.getAddress().getHostAddress();
		System.out.println(clientIP);
		// 注册码
//		if (message.startsWith("616e746f6e675f")) {
//			String brgid = ConvertCode.hexString2String(message);
//			brgid = brgid.substring(7);
//			registerChannel(brgid, ctx);
//			logger.info("接收注册码: ....{}......{}", getBridgeName(brgid), clientIP);
//
//
//		} else if (message.startsWith("1103")) {
//			// 11 03 28 41 30 33 41 30 34 41 30 31 00 00 00 00 00 00 00 00
//			// 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
//			// 00 00 00 10 25
//			StringBuffer tmp = new StringBuffer(message.substring(6));
//			tmp.delete(tmp.length() - 4, tmp.length());
//			while (tmp.toString().endsWith("00")) {
//				int start = tmp.length() - 2;
//				tmp.delete(start, start + 2);
//			}
//
//			String orgStr = ConvertCode.hexString2String(tmp.toString());
//			logger.info("获取注册码:。。。。。。" + orgStr);
//
//			if (StringUtils.isNotEmpty(orgStr)) {
//				registerChannel(orgStr, ctx);
//			}
//
//		} else {
//			// 返回同步访问
//			Attribute<String> channelAttr = ctx.channel().attr(key);
//			String orgCode = channelAttr.get();
//
//			logger.info("接收消息:{} 处理者:{}......{}", message, getBridgeName(orgCode), clientIP);
//			if (StringUtils.isNotEmpty(orgCode)) {
//				radarMessageTools.ackSyncMsg(orgCode, message);
//			}
//		}
		ctx.flush();
	}

	/**
	 * @Description: 每次来一个新连接就对连接数加一
	 * @Author:杨攀
	 * @Since: 2019年9月16日下午3:04:42
	 * @param ctx
	 * @throws Exception
	 */
	@Override
	public void channelActive(final ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);

		nConnection.incrementAndGet();

		InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
		String clientIP = insocket.getAddress().getHostAddress();

		logger.info("请求连接...{}，当前连接数: ：{}", clientIP, nConnection.get());
	}

	/**
	 * @Description: 每次与服务器断开的时候，连接数减一
	 * @Author:杨攀
	 * @Since: 2019年9月16日下午3:06:10
	 * @param ctx
	 * @throws Exception
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {

		super.channelInactive(ctx);

		Attribute<String> channelAttr = ctx.channel().attr(key);
		String orgCode = channelAttr.get();

		if (StringUtils.isNotEmpty(orgCode)) {
			ONLINE_BRIDGES.remove(orgCode);
		}

		InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
		String clientIP = insocket.getAddress().getHostAddress();

		nConnection.decrementAndGet();
		logger.info("断开连接...{}当前连接数: ：{}....{}", getBridgeName(orgCode), nConnection.get(), clientIP);
	}

	/**
	 * @Description: 连接异常的时候回调
	 * @Author:杨攀
	 * @Since: 2019年9月16日下午3:06:55
	 * @param ctx
	 * @param cause
	 * @throws Exception
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		super.exceptionCaught(ctx, cause);
		Attribute<String> channelAttr = ctx.channel().attr(key);
		String orgCode = channelAttr.get();
		// 打印错误日志
		logger.error("异常发生 {}", getBridgeName(orgCode));
		// cause.printStackTrace();

		Channel channel = ctx.channel();

		if (channel.isActive()) {
			ctx.close();
		}

	}
}