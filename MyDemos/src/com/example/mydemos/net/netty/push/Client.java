package com.example.mydemos.net.netty.push;

/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * Keeps sending random data to the specified address.
 */
public class Client {
	 static final int RECONNECT_DELAY = 5;
    private final String host;
    private final int port;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }


    public void run() {
        configureBootstrap(new Bootstrap()).connect();
    }

    private Bootstrap configureBootstrap(Bootstrap b) {
        return configureBootstrap(b, new NioEventLoopGroup());
    }

    Bootstrap configureBootstrap(Bootstrap b, EventLoopGroup g) {
        b.group(g)
         .channel(NioSocketChannel.class)
         .remoteAddress(host, port)
         .handler(
        		 new ChannelInitializer<SocketChannel>() {
               	  @Override
                     public void initChannel(SocketChannel ch) throws Exception {
                    	   ChannelPipeline pipeline = ch.pipeline();
                    	   pipeline.addLast("framer", new DelimiterBasedFrameDecoder(
                                   8192, Delimiters.lineDelimiter()));
                          pipeline.addLast("decoder", new StringDecoder());
                          pipeline.addLast("encoder", new StringEncoder());
                          pipeline.addLast(new IdleStateHandler(0, 5, 0), new ClientHandler(Client.this));
                     }
                });

        return b;
    }
    
    
    
    
    public static void main(String[] args) throws Exception {
        // Print usage if no argument is specified.
        

        // Parse options.
        final String host = "10.20.34.109";
        final int port = 7777;
        new Client(host, port).run();
    }
}
