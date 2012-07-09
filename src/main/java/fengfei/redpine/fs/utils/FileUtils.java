package fengfei.redpine.fs.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class FileUtils {

	public static void write(InputStream in, String filePath, long size)
			throws IOException {

		RandomAccessFile rafo = new RandomAccessFile(filePath, "rw");
		rafo.setLength(size);
		FileChannel fco = rafo.getChannel();
		MappedByteBuffer mbbo = fco
				.map(FileChannel.MapMode.READ_WRITE, 0, size);
		int avSize = 0;
		byte[] buf = new byte[1024];
		while ((avSize = in.read(buf)) > 0) {
			mbbo.put(buf, 0, avSize);
		}

		rafo.close();
	}

	public static FileOutputStream read(String filePath) throws IOException {
		return new FileOutputStream(filePath);
	}

	public static void main(String[] args) throws IOException {

		String sfile = "C:\\Users\\Public\\Pictures\\Sample Pictures\\Chrysanthemum.jpg";
		File file = new File(sfile);

		FileInputStream in = new FileInputStream(file);
		long start = System.currentTimeMillis();
		for (int i = 0; i < 800; i++) {
			String filePath = "e:\\hah\\pic" + i + ".jpg";
			write(in, filePath, file.length());
			in.getChannel().position(0);
		}
		in.close();

		System.out.println("Spend: " + (System.currentTimeMillis() - start)
				/ 800 + "ms");
	}
}
