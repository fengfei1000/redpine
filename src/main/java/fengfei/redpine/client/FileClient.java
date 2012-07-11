package fengfei.redpine.client;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelFutureProgressListener;
import org.jboss.netty.channel.DefaultFileRegion;
import org.jboss.netty.channel.FileRegion;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.stream.ChunkedFile;
import org.msgpack.MessagePack;

import fengfei.redpine.server.FileRequest;
import fengfei.redpine.server.Method;

public class FileClient {

    public static final int DEFAULT_PORT = 8022;
    public static final int DEFAULT_TIMEOUT = 12000;

    MessagePack pack = new MessagePack();
    private String host;

    private int port = DEFAULT_PORT;
    private long timeout = DEFAULT_TIMEOUT;
    int chunkSize = 2048;
    private ClientBootstrap bootstrap;
    Channel channel;

    public FileClient() {
        this("localhost", DEFAULT_PORT, DEFAULT_TIMEOUT);
    }

    public FileClient(String host) {
        this(host, DEFAULT_PORT, DEFAULT_TIMEOUT);
    }

    public FileClient(String host, long timeout) {
        this(host, DEFAULT_PORT, timeout);
    }

    public FileClient(String host, int port, long timeout) {
        super();
        this.host = host;
        this.port = port;
        this.timeout = timeout;
        init();
    }

    protected void init() {
        // Configure the client.
        bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(
            Executors.newCachedThreadPool(),
            Executors.newCachedThreadPool()));

        // Set up the event pipeline factory.
        bootstrap.setPipelineFactory(new FileClientPipelineFactory());
        // Options for a new channel
        bootstrap.setOption("tcpNoDelay", true);
        bootstrap.setOption("receiveBufferSize", 1048576);
        ChannelFuture future = bootstrap.connect(new InetSocketAddress("localhost", 8022));
        // Wait until the connection is made successfully.
        future.awaitUninterruptibly(timeout);
        future.addListener(new ChannelFutureListener() {

            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                System.out.println("Connected.");
            }
        });
        if (!future.isSuccess()) {
            future.getCause().printStackTrace();
            bootstrap.releaseExternalResources();
            return;
        }
        channel = future.getChannel();

    }

    public void close() {

        ChannelFuture close = channel.close().awaitUninterruptibly();
        close.addListener(new ChannelFutureListener() {

            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                System.out.println("Closed.");

            }
        });
        // Shut down thread pools to exit.
        bootstrap.releaseExternalResources();
    }

    static AtomicLong ct = new AtomicLong();

    public void writeFileFromStream(
        final InputStream in,
        long length,
        String id,
        String date,
        String fileName) throws IOException {
        FileRequest request = createRequest(id, date, fileName);
        byte[] data = pack.write(request);

        ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
        buffer.writeLong(8 + data.length + length);
        buffer.writeInt(data.length);
        buffer.writeBytes(data);
        buffer.writeLong(length);
        channel.write(buffer);
        System.out.println(request);
        byte[] bs = new byte[2048];
        int len = 0;
        while ((len = in.read(bs)) > 0) {
            channel.write(ChannelBuffers.wrappedBuffer(bs, 0, len)).awaitUninterruptibly();
        }

    }

    private FileRequest createRequest(String id, String date, String fileName) {
        FileRequest request = new FileRequest();
        request.seq = UUID.randomUUID().toString().toUpperCase();
        request.id = id;
        request.date = date;
        request.fileName = fileName;// file.getName();
        request.chunkSize = chunkSize;
        request.method = Method.PutFile.name();
        return request;
    }

    public void writeFile(final File file, String id, String date) throws IOException {

        FileRequest request = createRequest(id, date, file.getName());

        byte[] data = pack.write(request);
        RandomAccessFile raf = new RandomAccessFile(file, "r");
        long length = raf.length();
        // MappedByteBuffer bf =
        // raf.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, length);
        ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
        buffer.writeLong(8 + data.length + length);
        buffer.writeInt(data.length);
        buffer.writeBytes(data);
        buffer.writeLong(length);
        channel.write(buffer);
        System.out.println(request);
        // ChunkedFile chunkedFile = new ChunkedFile(raf);
        // channel.write(chunkedFile);
        // No encryption - use zero-copy.
        final FileRegion region = new DefaultFileRegion(raf.getChannel(), 0, length);
        ChannelFuture writeFuture = channel.write(region);
        writeFuture.addListener(new ChannelFutureProgressListener() {

            public void operationComplete(ChannelFuture future) {
                region.releaseExternalResources();
            }

            public void operationProgressed(
                ChannelFuture future,
                long amount,
                long current,
                long total) {
                System.out.printf("%s: %d / %d (+%d)%n", file.getName(), current, total, amount);
            }
        });
        writeFuture.awaitUninterruptibly();
        raf.close();
    }

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        final String id = "fengfei1000";
        final String date = "20120710";
        for (int i = 0; i < 3; i++) {
            new Thread() {

                public void run() {
                    try {
                        FileClient client = new FileClient();
                        File folder = new File("J:\\out\\500px.com");
                        File[] fs = folder.listFiles();
                        for (File f : fs) {
                            if (f.isFile()) {
                                client.writeFile(f, id, date);
                                // Thread.sleep(12);
                            }
                        }
                        client.writeFile(new File("e:\\我的签名2.jpg"), id, date);
                        client.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                };
            }.start();
        }

    }
}
