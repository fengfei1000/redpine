package fengfei.redpine.client;

import static org.jboss.netty.channel.Channels.pipeline;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.stream.ChunkedWriteHandler;

public class FileClientPipelineFactory implements ChannelPipelineFactory {

	public FileClientPipelineFactory() {

	}

	public ChannelPipeline getPipeline() throws Exception {
		// Create a default pipeline implementation.
		ChannelPipeline pipeline = pipeline();

		// to be used since huge file transfer
		pipeline.addLast("chunkedWriter", new ChunkedWriteHandler());

		pipeline.addLast("handler", new FileClientHandler());
		return pipeline;
	}
}
