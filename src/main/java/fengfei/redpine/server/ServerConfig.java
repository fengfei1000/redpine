package fengfei.redpine.server;

public class ServerConfig {

	public static final int DEFAULT_PORT = 8022;
	private int chunkSize = 8192;
	private int port = DEFAULT_PORT;
	private boolean isFileZeroCopy = true;

	public int getChunkSize() {
		return chunkSize;
	}

	public void setChunkSize(int chunkSize) {
		this.chunkSize = chunkSize;
	}

	public boolean isFileZeroCopy() {
		return isFileZeroCopy;
	}

	public void setFileZeroCopy(boolean isFileZeroCopy) {
		this.isFileZeroCopy = isFileZeroCopy;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

}
