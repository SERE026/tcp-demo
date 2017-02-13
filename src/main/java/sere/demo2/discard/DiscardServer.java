package sere.demo2.discard;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class DiscardServer {
	private int port;

	public DiscardServer(int port) {
		this.port = port;
	}

	public void run() throws Exception {
		// NioEventLoopGroup 是用来处理I/O操作的多线程事件循环器
		EventLoopGroup bossGroup = new NioEventLoopGroup(); // 用来接收进来的连接
		EventLoopGroup workGroup = new NioEventLoopGroup(); // 处理已经被接收的连接

		try {
			ServerBootstrap b = new ServerBootstrap(); // NIO 服务的辅助启动类
			b.group(bossGroup, workGroup).channel(NioServerSocketChannel.class) // Channel
																				// 如何接收进来的连接
					.childHandler(new ChannelInitializer<SocketChannel>() { // 帮助使用者配置一个新的
																			// Channel
																			// 处理一个最近的已经接收的
																			// Channel
						@Override
						public void initChannel(SocketChannel sc) throws Exception {
							sc.pipeline().addLast(new DiscardServerHandler());
						};
					}).option(ChannelOption.SO_BACKLOG, 128)
					.childOption(ChannelOption.SO_KEEPALIVE, true);

			// 绑定端口
			ChannelFuture f = b.bind(port).sync();

			f.channel().closeFuture().sync();
		} finally {
			workGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}

	}

	public static void main(String[] args) {
		DiscardServer server = new DiscardServer(18090);
		try {
			server.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
