package fengfei.redpine.server;

import static org.jboss.netty.channel.Channels.pipeline;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.stream.ChunkedWriteHandler;

import fengfei.redpine.fs.DefaultFileStore;
import fengfei.redpine.fs.FileStore;

public class FileServerPipelineFactory implements ChannelPipelineFactory {
	FileStore fileStore;
	ServerConfig serverConfig;

	public FileServerPipelineFactory(FileStore fileStore,
			ServerConfig serverConfig) {
		super();
		this.fileStore = fileStore;
		this.serverConfig = serverConfig;
	}

	public ChannelPipeline getPipeline() throws Exception {
		// Create a default pipeline implementation.
		ChannelPipeline pipeline = pipeline();

		// Uncomment the following line if you want HTTPS
		// SSLEngine engine =
		// SecureChatSslContextFactory.getServerContext().createSSLEngine();
		// engine.setUseClientMode(false);
		// pipeline.addLast("ssl", new SslHandler(engine));
		// pipeline.addLast("", new LengthFieldBasedFrameDecoder(2048, 0, 4, 0,
		// 4));
		// pipeline.addLast("LengthFieldPrependerEncoder",
		// new LengthFieldPrepender(4));

		pipeline.addLast("decoder", new FileReplayingDecoder(serverConfig));
		pipeline.addLast("streamer", new ChunkedWriteHandler());

		pipeline.addLast("handler", new FileServerHandler(fileStore,
				serverConfig));

		return pipeline;
	}
}
