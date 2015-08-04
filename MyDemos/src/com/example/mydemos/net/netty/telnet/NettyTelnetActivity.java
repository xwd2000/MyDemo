package com.example.mydemos.net.netty.telnet;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.LinkedList;
import java.util.Queue;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mydemos.R;

public class NettyTelnetActivity extends Activity implements OnClickListener {
	private String host = "192.168.42.242";
	private int port = 8081;

	private EditText etInputMessage;
	private TextView tvMessageShow;
	private MessageThread sendThread;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case TelnetClientHandler.MET_MESSAGE_HANDLER_KEY:
				tvMessageShow.setText(tvMessageShow.getText() + "\n" + msg.obj);
				break;

			default:
				break;
			}

		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.netty_telnet_activity);
		instanceView();
		initView();

	}

	public void instanceView() {
		etInputMessage = (EditText) findViewById(R.id.et_netty_telnet_input_message_box);
		tvMessageShow = (TextView) findViewById(R.id.tv_netty_telnet_message_show);
	}

	public void initView() {
		sendThread = new MessageThread();
		sendThread.start();

	}

	public class MessageThread extends Thread {

		private Queue<String> messageQueue;

		public MessageThread() {
			messageQueue = new LinkedList<String>();
		}

		@Override
		public void run() {

			EventLoopGroup group = new NioEventLoopGroup();
			try {
				TelnetClientHandler tch = new TelnetClientHandler(handler);
				Bootstrap b = new Bootstrap();
				b.group(group).channel(NioSocketChannel.class)
						.handler(new TelnetClientInitializer(tch));

				// Start the connection attempt.
				Channel ch = b.connect(host, port).sync().channel();

				// Read commands from the stdin.
				ChannelFuture lastWriteFuture = null;
				synchronized (this) {
					for (;;) {
						if (messageQueue.isEmpty()) {
							wait();
						}
						String line = messageQueue.poll();
						if (line == null) {
							continue;
						}
						// Sends the received line to the server.
						lastWriteFuture = ch.writeAndFlush(line + "\r\n");

						// If user typed the 'bye' command, wait until the
						// server closes
						// the connection.
						if ("bye".equals(line.toLowerCase())) {
							ch.closeFuture().sync();
							break;
						}
					}
				}
				// Wait until all messages are flushed before closing the
				// channel.
				if (lastWriteFuture != null) {
					lastWriteFuture.sync();
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				group.shutdownGracefully();
			}

		}

		public void sendMessage(String message) {
			synchronized (this) {

				messageQueue.add(message);
				notify();
			}
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_netty_telnet_send_text_button:

			sendThread.sendMessage(etInputMessage.getText().toString());
			break;

		default:
			break;
		}

	}
}
