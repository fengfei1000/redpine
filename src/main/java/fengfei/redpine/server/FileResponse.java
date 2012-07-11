package fengfei.redpine.server;

import org.msgpack.annotation.Message;

/**
 * *
 * 
 * <pre>
 *     protocol
 * 
 *   header format: length | header content byte |
 *   content: total  byte[]
 * 
 * </pre>
 * 
 * @author wtt
 * 
 */
@Message
public class FileResponse {

    public String seq;
    public String id;
    public String date;
    public String fileName;
    public String method;
    public int statusCode;
    public int chunkSize;
    public long fileLength;
    public String fileId;
}
