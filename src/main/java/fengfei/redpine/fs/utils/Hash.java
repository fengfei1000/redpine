package fengfei.redpine.fs.utils;

public class Hash {

	private static final long FNV_BASIS = 0x811c9dc5;
	private static final long FNV_PRIME = (1 << 24) + 0x193;
	public static final long FNV_BASIS_64 = 0xCBF29CE484222325L;
	public static final long FNV_PRIME_64 = 1099511628211L;

	public static int hash(byte[] key) {
		long hash = FNV_BASIS;
		for (int i = 0; i < key.length; i++) {
			hash ^= 0xFF & key[i];
			hash *= FNV_PRIME;
		}

		return (int) hash;
	}

	public static long hash64(long val) {
		long hashval = FNV_BASIS_64;

		for (int i = 0; i < 8; i++) {
			long octet = val & 0x00ff;
			val = val >> 8;

			hashval = hashval ^ octet;
			hashval = hashval * FNV_PRIME_64;
		}
		return Math.abs(hashval);
	}

}
