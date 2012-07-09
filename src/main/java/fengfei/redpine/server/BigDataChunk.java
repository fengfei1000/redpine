package fengfei.redpine.server;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

public interface BigDataChunk {

	/**
	 * Returns {@code true} if and only if this chunk is the 'end of content'
	 * marker.
	 */
	boolean isLast();

	void setLast(boolean isLast);

	/**
	 * Returns the content of this chunk. If this is the 'end of content'
	 * marker, {@link ChannelBuffers#EMPTY_BUFFER} will be returned.
	 */
	ChannelBuffer getContent();

	/**
	 * Sets the content of this chunk. If an empty buffer is specified, this
	 * chunk becomes the 'end of content' marker.
	 */
	void setContent(ChannelBuffer content);
}
