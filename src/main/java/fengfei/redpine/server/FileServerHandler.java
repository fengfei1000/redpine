package fengfei.redpine.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureProgressListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.DefaultFileRegion;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.FileRegion;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.stream.ChunkedFile;
import org.msgpack.MessagePack;

import fengfei.redpine.fs.FileStore;
import fengfei.redpine.fs.Path;

public class FileServerHandler extends SimpleChannelUpstreamHandler {

	FileStore fileStore;
	ServerConfig serverConfig;
	MessagePack pack = new MessagePack();
	private boolean readingChunks;
	FileChannel fileChannel;

	FileRequest request;

	public FileServerHandler(FileStore fileStore, ServerConfig serverConfig) {
		super();
		this.fileStore = fileStore;
		this.serverConfig = serverConfig;
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {

		if (e.getMessage() instanceof FileRequest) {
			request = (FileRequest) e.getMessage();
			Method method = request.getMethod();

			System.out.println(request);
			switch (method) {
			case GetFile:
				writeResponseFile(ctx, e.getChannel(), request);
				break;
			case PutFile:
				String filePath = Path.getPath(request);
				File file = new File(filePath);
				if (!file.exists()) {
					file.getParentFile().mkdirs();
					// file.mkdirs();
				}

				RandomAccessFile raf = new RandomAccessFile(file, "rw");
				raf.setLength(request.fileLength);
				fileChannel = raf.getChannel();

				break;

			default:
				break;
			}
		}
		if (e.getMessage() instanceof BigDataChunk) {

			BigDataChunk chunk = (BigDataChunk) e.getMessage();
			ChannelBuffer buffer = chunk.getContent();
			int size = buffer.readableBytes();

			// buffer.readBytes(fileChannel, size);

			MappedByteBuffer mappedByteBuffer = fileChannel.map(
					FileChannel.MapMode.READ_WRITE,
					fileChannel.position(),
					size);
			buffer.readBytes(mappedByteBuffer);

			if (chunk.isLast()) {
				fileChannel.close();
				System.out.println("close fileChannel: ");
				fileChannel = null;
			}
			System.out.println("write file" + chunk.isLast() + ", size: " + size);
		}

		// fileChannel.close();

	}

	void writeResponseFile(ChannelHandlerContext ctx, Channel channel, FileRequest request)
			throws IOException {

		if (request.getMethod() != Method.GetFile) {
			sendError(ctx, request, Status.ErrorMethod);
			return;
		}
		final String filePath = Path.getPath(request);

		File file = new File(filePath);
		if (file.isHidden() || !file.exists() || !file.isFile()) {
			sendError(ctx, request, Status.FileNonExisted);
			return;
		}

		RandomAccessFile raf;
		try {
			raf = new RandomAccessFile(file, "r");
		} catch (FileNotFoundException fnfe) {
			sendError(ctx, request, Status.FileNonExisted);
			return;
		}
		long fileLength = raf.length();

		FileResponse response = new FileResponse();
		response.seq = request.seq;
		response.id = request.id;
		response.fileName = request.fileName;
		response.chunkSize = request.chunkSize <= 1024 ? serverConfig.getChunkSize() : request.chunkSize;
		response.method = Method.PutFile.name();
		response.fileLength = fileLength;
		byte[] data = pack.write(response);
		ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
		buffer.writeLong(8 + data.length + fileLength);
		buffer.writeInt(data.length);
		buffer.writeBytes(data);
		buffer.writeLong(fileLength);

		// write reponse header
		channel.write(buffer);
		// write file
		ChannelFuture writeFuture;
		if (serverConfig.isFileZeroCopy()) {
			// No encryption - use zero-copy.
			final FileRegion region = new DefaultFileRegion(raf.getChannel(), 0, fileLength);
			writeFuture = channel.write(region);
			writeFuture.addListener(new ChannelFutureProgressListener() {

				public void operationComplete(ChannelFuture future) {
					region.releaseExternalResources();
				}

				public void operationProgressed(
						ChannelFuture future,
						long amount,
						long current,
						long total) {
					System.out
							.printf("%s: %d / %d (+%d)%n", filePath, current, total, amount);
				}
			});

		} else { // Cannot use zero-copy for encryption or nonsupport .
			writeFuture = channel.write(new ChunkedFile(raf, 0, fileLength, 8192));

		}

	}

	private void sendError(ChannelHandlerContext ctx, FileRequest request, Status status) {

		FileResponse response = new FileResponse();
		response.seq = request.seq;
		response.id = request.id;
		response.fileName = request.fileName;
		response.chunkSize = request.chunkSize <= 1024 ? serverConfig.getChunkSize() : request.chunkSize;
		response.method = Method.PutFile.name();
		response.statusCode = status.getValue();

		ctx.getChannel().write(response);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		Channel ch = e.getChannel();
		Throwable cause = e.getCause();

		cause.printStackTrace();

	}

}
