package fengfei.redpine.fs;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import fengfei.redpine.fs.utils.Hash;
import fengfei.redpine.server.FileRequest;

public class Path {

	static final int MaxFolder = 512;
	static final String root = "./";

	public static String getPath(String id, String date, String name) {
		StringBuilder sb = new StringBuilder(root);
		sb.append(date.substring(0, 6)).append("/");
		// sb.append(hashDiv(id + date + name)).append("/");
		// sb.append(hashDiv(id + name)).append("/");
		sb.append(hashDiv(id)).append("/");
		sb.append(id).append("/");
		sb.append(hashDiv(name, 32)).append("/").append(name);

		return sb.toString();
	}

	public static String hashDiv(String str) {
		return hashDiv(str, MaxFolder);
	}

	public static String hashDiv(String str, int max) {
		int hash = Hash.hash(str.getBytes());
		int div = hash % max;
		return Integer.toString(div < 0 ? -div : div, 16).toUpperCase();
	}

	public static String getPath(FileRequest request) {
		return getPath(request.id, request.date, request.fileName);
	}

	public static void main(String[] args) {

		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Set<String> sets = new HashSet<>();
		int num = 1000000;
		for (int i = 0; i < num; i++) {
			String id = "id" + i;

			String sdate = sdf.format(c.getTime());
			// c.add(Calendar.DAY_OF_MONTH, 1);
			String name = i + "xxx.jpg";
			String path = getPath(id, sdate, name);
			// System.out.println(getPath(id, sdate, name));
			sets.add(path);
		}
		System.out.println(sets.size());
	}
}
