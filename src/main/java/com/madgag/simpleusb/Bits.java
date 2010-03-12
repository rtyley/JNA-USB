package com.madgag.simpleusb;

import java.nio.ByteBuffer;

public class Bits {

	public static short getShortL(byte[] b, int offset) {
		return makeShort(b[offset + 1], b[offset + 0]);
	}

	public static short getShortB(byte[] b, int offset) {
		return makeShort(b[offset + 0], b[offset + 1]);
	}

	public static int getIntL(byte[] b, int offset) {
		return makeInt(b[offset + 3], b[offset + 2], b[offset + 1],
				b[offset + 0]);
	}

	public static int getIntB(byte[] b, int offset) {
		return makeInt(b[offset + 0], b[offset + 1], b[offset + 2],
				b[offset + 3]);
	}

	static short makeShort(byte b1, byte b0) {
		return (short) (((b1 & 0xff) << 8) | ((b0 & 0xff) << 0));
	}

	static int makeInt(byte b3, byte b2, byte b1, byte b0) {
		return (int) ((((b3 & 0xff) << 24) | ((b2 & 0xff) << 16)
				| ((b1 & 0xff) << 8) | ((b0 & 0xff) << 0)));
	}

	private static byte short1(short x) {
		return (byte) (x >> 8);
	}

	private static byte short0(short x) {
		return (byte) (x >> 0);
	}

	public static void putShortL(byte[] b, int offset, short x) {
		b[offset + 0] = short0(x);
		b[offset + 1] = short1(x);
	}

	private static byte int3(int x) {
		return (byte) (x >> 24);
	}

	private static byte int2(int x) {
		return (byte) (x >> 16);
	}

	private static byte int1(int x) {
		return (byte) (x >> 8);
	}

	private static byte int0(int x) {
		return (byte) (x >> 0);
	}

	public static void putIntL(byte[] b, int offset, int x) {
		b[offset + 3] = int3(x);
		b[offset + 2] = int2(x);
		b[offset + 1] = int1(x);
		b[offset + 0] = int0(x);
	}
}
