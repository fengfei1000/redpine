package fengfei.redpine.server;

import org.msgpack.annotation.Message;

/**
 * <pre>
 *     protocol
 * 
 *   header format: length | header content byte |
 *   content: total  byte[]
 * 
 * </pre>
 * 
 */
@Message
public class FileRequest {

	public String seq;
	public String id;
	public String date;
	public String fileName;
	public String method;
	public int chunkSize;
	public long fileLength;

	public Method getMethod() {
		return Method.find(method);
	}

	@Override
	public String toString() {
		return "FileRequest [seq=" + seq + ", id=" + id + ", date=" + date + ", fileName=" + fileName + ", method=" + method + ", chunkSize=" + chunkSize + ", fileLength=" + fileLength + "]";
	}

}
