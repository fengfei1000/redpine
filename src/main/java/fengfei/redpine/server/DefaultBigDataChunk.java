package fengfei.redpine.server;

import org.jboss.netty.buffer.ChannelBuffer;

public class DefaultBigDataChunk implements BigDataChunk {

	private ChannelBuffer content;
	private boolean last;

	/**
	 * Creates a new instance with the specified chunk content. If an empty
	 * buffer is specified, this chunk becomes the 'end of content' marker.
	 */
	public DefaultBigDataChunk(ChannelBuffer content) {
		setContent(content);
	}

	public ChannelBuffer getContent() {
		return content;
	}

	public void setContent(ChannelBuffer content) {
		if (content == null) {
			throw new NullPointerException("content");
		}
		// last = !content.readable();
		this.content = content;
	}

	public boolean isLast() {
		return last;
	}

	@Override
	public void setLast(boolean isLast) {
		this.last = isLast;
	}

}
