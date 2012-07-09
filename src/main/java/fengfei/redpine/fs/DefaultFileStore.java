package fengfei.redpine.fs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

import fengfei.redpine.fs.utils.FileUtils;

public class DefaultFileStore implements FileStore {


	@Override
	public void store(InputStream in, String filePath, long size)
			throws IOException {
		FileUtils.write(in, filePath, size);

	}

	@Override
	public OutputStream read(String filePath) throws IOException {
		return FileUtils.read(filePath);
	}

	@Override
	public FileChannel readChannel(String filePath) throws IOException {
		RandomAccessFile raf = new RandomAccessFile(filePath, "r");
		return raf.getChannel();
	}

}
