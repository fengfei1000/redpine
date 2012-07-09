package fengfei.redpine.fs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

public interface FileStore {
	void store(InputStream in, String filePath, long size) throws IOException;

	OutputStream read(String filePath) throws IOException;

	FileChannel readChannel(String filePath) throws IOException;

}
