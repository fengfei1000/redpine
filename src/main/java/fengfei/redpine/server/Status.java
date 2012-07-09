package fengfei.redpine.server;

import java.util.HashMap;
import java.util.Map;

public enum Status {
	Processing(100),
	Transporting(101),
	Success(200),
	ServerError(400),
	FileNonExisted(401),
	ErrorMethod(402);

	private final int value;
	private static Map<Integer, Status> cache = new HashMap<Integer, Status>();
	static {
		for (Status type : values()) {
			cache.put(type.value, type);
		}
	}

	private Status(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static Status valueOf(int value) {
		return cache.get(value);
	}

	public static Status find(String name) {
		if (name == null || "".equals(name)) {
			return null;
		}
		Status[] fs = values();
		for (Status enumType : fs) {
			if (enumType.name().equalsIgnoreCase(name)) {
				return enumType;
			}

		}
		throw new IllegalArgumentException("Non-exist the enum type, error arg name:" + name);
	}
}
