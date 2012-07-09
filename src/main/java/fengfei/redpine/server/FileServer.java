package fengfei.redpine.server;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.msgpack.MessagePack;

import fengfei.redpine.fs.DefaultFileStore;
import fengfei.redpine.fs.FileStore;

public class FileServer {

	private MessagePack pack = new MessagePack();

	private ServerConfig serverConfig;
	ServerBootstrap bootstrap;
	Channel channel;

	public FileServer() {
		this(new ServerConfig());
	}

	public FileServer(ServerConfig serverConfig) {
		super();
		this.serverConfig = serverConfig;
	}

	public void listen(ServerConfig serverConfig) {
		this.serverConfig = serverConfig;
		listen();

	}

	public void listen() {
		bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(
				Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool()));

		FileStore fileStore = new DefaultFileStore();

		// Set up the event pipeline factory.
		bootstrap.setPipelineFactory(new FileServerPipelineFactory(fileStore, serverConfig));

		// Bind and start to accept incoming connections.
		channel = bootstrap.bind(new InetSocketAddress(8022));
		System.out.println("Server is starting for listening" + channel.getLocalAddress());
		Runtime.getRuntime().addShutdownHook(new Thread() {

			@Override
			public void run() {
				bootstrap.releaseExternalResources();
				System.out.println("Server is closed by outside operation.");
			}
		});
	}

	public void shutdown() {
		ChannelFuture close = channel.close().awaitUninterruptibly();
		close.addListener(new ChannelFutureListener() {

			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				System.out.println("Server is closed by inside invoking.");

			}
		});
		bootstrap.releaseExternalResources();
	}

	/**
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		FileServer server = new FileServer();
		server.listen();
		// Thread.sleep(5000);
		// server.shutdown();
	}

}
