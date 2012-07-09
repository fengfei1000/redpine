package fengfei.redpine.dfs;

import java.io.IOException;
import java.io.RandomAccessFile;

public class Redpine {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		long lo = System.currentTimeMillis();
		for (int i = 0; i < 5; i++) {

			RandomAccessFile raf = new RandomAccessFile("L:/hah/abc.txt " + i, "rw");
			raf.setLength(2 * 1024 * 1024 * 1024 - 1);
			raf.close();
		}

		System.out.print(System.currentTimeMillis() - lo);

	}
}
