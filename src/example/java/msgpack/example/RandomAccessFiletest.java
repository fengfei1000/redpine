package msgpack.example;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class RandomAccessFiletest {

	static int block = 1024;
	static int[] block_ = { 1 * block, 2 * block, 4 * block, 8 * block, 16 * block, 32 * block, 64 * block, 128 * block, 256 * block, 512 * block, 1024 * block, 2 * 1024 * block, 4 * 1024 * block, 8 * 1024 * block, 16 * 1024 * block, 32 * 1024 * block };

	public static void main(String[] args) throws Exception {

		//
		for (int i = 1; i < block_.length; i++) {
			block = block_[i];
			long l = System.currentTimeMillis();
			testRead();
			System.out.println(block + "	" + (System.currentTimeMillis() - l));
		}
	}

	private static void testRead() throws Exception {
		File file = new File("J:\\IMG_8591.CR2");// exxe.sql.bak
		long size = file.length();
		RandomAccessFile accessFile = new RandomAccessFile(file, "r");
		FileChannel fci = accessFile.getChannel();
		MappedByteBuffer mbbi = fci.map(FileChannel.MapMode.READ_ONLY, 0, size);
		byte[] b = new byte[block];
		long length = Math.round(size / block);
		long position = 0;
		for (long i = 0; i < length; i++) {
			mbbi.get(b);
//			accessFile.read(b);
			position += block;
			if (position <= size) {
				accessFile.seek(position);
			} 
			// System.out
			// .println(position + "-" + block + "-" + size + "-" + (size -
			// position));

			// String str=new String(b);
		}
		accessFile.close();
	}
}
