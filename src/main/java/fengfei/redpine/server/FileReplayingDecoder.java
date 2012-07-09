package fengfei.redpine.server;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.replay.ReplayingDecoder;
import org.msgpack.MessagePack;

import fengfei.redpine.server.FileReplayingDecoder.DecoderState;

public class FileReplayingDecoder extends ReplayingDecoder<DecoderState> {

	MessagePack pack = new MessagePack();
	ServerConfig serverConfig;
	private long length;
	int headerLength;
	long bodyLength;
	private int chunkSize;
	private FileRequest request;

	public FileReplayingDecoder(ServerConfig serverConfig) {
		// Set the initial state.
		super(DecoderState.READ_HEAD_LENGTH);
		this.serverConfig = serverConfig;
		chunkSize = serverConfig.getChunkSize();

	}

	@Override
	protected Object decode(
			ChannelHandlerContext ctx,
			Channel channel,
			ChannelBuffer buffer,
			DecoderState state) throws Exception {
		switch (state) {

		case READ_HEAD_LENGTH:
			length = buffer.readLong();
			headerLength = buffer.readInt();
			length -= headerLength;
			length -= 8;
			checkpoint(DecoderState.READ_HEAD_CONTENT);
		case READ_HEAD_CONTENT:
			byte[] dst = new byte[headerLength];
			buffer.readBytes(dst);
			request = pack.read(dst, FileRequest.class);

			chunkSize = request.chunkSize;
			checkpoint(DecoderState.READ_BODY_LENGTH);
			System.out.println(request);

			return request;
		case READ_BODY_LENGTH:
			bodyLength = buffer.readLong();
			request.fileLength = bodyLength;
			checkpoint(DecoderState.READ_BODY_CONTENT);
			System.out.println("bodyLength: " + bodyLength);
		case READ_BODY_CONTENT:
			int readLimit = actualReadableBytes();
			if (readLimit == 0) {
				return null;
			}
			int toRead = chunkSize;

			if (toRead > readLimit) {
				toRead = readLimit;
			}
			if (length < chunkSize) {
				toRead = (int) length;
			}
			ChannelBuffer frame = buffer.readBytes(toRead);
			length -= toRead;

			BigDataChunk chunk = new DefaultBigDataChunk(frame);
			chunk.setLast(false);
			System.out.println("length: " + length);
			if (length <= 0) {
				chunk.setLast(true);
				checkpoint(DecoderState.READ_HEAD_LENGTH);
			}

			return chunk;
		default:
			throw new Error("Shouldn't reach here.");
		}
	}

	public static enum DecoderState {
		READ_HEAD_LENGTH,
		READ_HEAD_CONTENT,
		READ_BODY_LENGTH,
		READ_BODY_CONTENT;
	}

}
