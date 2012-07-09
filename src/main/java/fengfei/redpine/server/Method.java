package fengfei.redpine.server;

import java.util.HashMap;
import java.util.Map;

public enum Method {
	GetFile(10),
	PutFile(20);

	private final int value;
	private static Map<Integer, Method> cache = new HashMap<Integer, Method>();
	static {
		for (Method type : values()) {
			cache.put(type.value, type);
		}
	}

	private Method(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static Method valueOf(int value) {
		return cache.get(value);
	}

	public static Method find(String name) {
		if (name == null || "".equals(name)) {
			return null;
		}
		Method[] fs = values();
		for (Method enumType : fs) {
			if (enumType.name().equalsIgnoreCase(name)) {
				return enumType;
			}

		}
		throw new IllegalArgumentException("Non-exist the enum type, error arg name:" + name);
	}
}
