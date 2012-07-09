package fengfei.redpine.dfs;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class Main {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		String SrcFile = "C:\\WINdows\\Fonts\\msjhbd.ttf";
		String DesFile = ".\\msjhbd.ttf";

		RandomAccessFile rafi = new RandomAccessFile(SrcFile, "r");
		RandomAccessFile rafo = new RandomAccessFile(DesFile, "rw");
		FileChannel fci = rafi.getChannel();
		FileChannel fco = rafo.getChannel();
		long size = fci.size();
		MappedByteBuffer mbbi = fci.map(FileChannel.MapMode.READ_ONLY, 0, size);
		MappedByteBuffer mbbo = fco.map(FileChannel.MapMode.READ_WRITE, 0, size);

		long start = System.currentTimeMillis();
		mbbo.put(mbbi);

//		for (int i = 0; i < size; i++) {
//			// byte b = mbbi.get(i);
//			// mbbo.put(i, b);
//			mbbi.get(buf);
//			mbbo.put(buf);
//
//		}

		rafi.close();
		rafo.close();
		System.out
				.println("Spend: " + (double) (System.currentTimeMillis() - start) / 1000 + "s");

	}

	public static void ss() throws Exception {
		ByteBuffer byteBuf = ByteBuffer.allocate(1024 * 14 * 1024);
		byte[] bbb = new byte[14 * 1024 * 1024];
		FileInputStream fis = new FileInputStream(
				"e://data/other/UltraEdit_17.00.0.1035_SC.exe");
		FileOutputStream fos = new FileOutputStream("e://data/other/outFile.txt");
		FileChannel fc = fis.getChannel();
		long timeStar = System.currentTimeMillis();// 得到当前的时间
		fc.read(byteBuf);// 1 读取
		MappedByteBuffer mbb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
		System.out.println(fc.size() / 1024);
		long timeEnd = System.currentTimeMillis();// 得到当前的时间
		System.out.println("Read time :" + (timeEnd - timeStar) + "ms");
		timeStar = System.currentTimeMillis();
		fos.write(bbb);// 2.写入
		mbb.flip();
		timeEnd = System.currentTimeMillis();
		System.out.println("Write time :" + (timeEnd - timeStar) + "ms");
		fos.flush();
		fc.close();
		fis.close();
	}

}
