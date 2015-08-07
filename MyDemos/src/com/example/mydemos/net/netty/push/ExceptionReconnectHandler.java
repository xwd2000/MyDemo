package com.example.mydemos.net.netty.push;

import java.net.ConnectException;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.channel.SimpleChannelInboundHandler;

public class ExceptionReconnectHandler extends
		SimpleChannelInboundHandler<Object> {
	private final Client client;
	private long startTime = -1;
	private boolean reconnect = true;

	public ExceptionReconnectHandler(Client client) {
		super();
		this.client = client;
	}

	public void setReconnect(boolean reconnect){
		this.reconnect=reconnect;
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		if (startTime < 0) {
			startTime = System.currentTimeMillis();
		}
		println("Connected to: " + ctx.channel().remoteAddress());
		super.channelActive(ctx);
	}

	@Override
	public void channelUnregistered(final ChannelHandlerContext ctx)
			throws Exception {
		println("Sleeping for: " + Client.RECONNECT_DELAY + 's');
		if (reconnect) {
			final EventLoop loop = ctx.channel().eventLoop();
			loop.schedule(new Runnable() {
				@Override
				public void run() {
					println("Reconnecting to: " + ctx.channel().remoteAddress());
					client.configureBootstrap(new Bootstrap(), loop).connect();
				}
			}, Client.RECONNECT_DELAY, TimeUnit.SECONDS);
		}
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		println("Disconnected from: " + ctx.channel().remoteAddress());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		if (cause instanceof ConnectException) {
			startTime = -1;
			println("Failed to connect: " + cause.getMessage());
		}
		cause.printStackTrace();
		ctx.close();
	}

	void println(String msg) {
		if (startTime < 0) {
			System.err.format("[SERVER IS DOWN] %s%n", msg);
		} else {
			System.err.format("[UPTIME: %5ds] %s%n",
					(System.currentTimeMillis() - startTime) / 1000, msg);
		}
	}

	@Override
	protected void channelRead0(ChannelHandlerContext arg0, Object arg1)
			throws Exception {
	}
}
