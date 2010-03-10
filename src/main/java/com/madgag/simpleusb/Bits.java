package com.madgag.simpleusb;

public class Bits {

	public static short getShortL(byte[] b, int offset) {
		return makeShort(b[offset + 1], b[offset + 0]);
	}

	public static short getShortB(byte[] b, int offset) {
		return makeShort(b[offset + 0], b[offset + 1]);
	}
	
	public static int getIntL(byte[] b, int offset) {
		return makeInt(b[offset + 3], b[offset + 2], b[offset + 1], b[offset + 0]);
	}

	public static int getIntB(byte[] b, int offset) {
		return makeInt(b[offset + 0], b[offset + 1], b[offset + 2],	b[offset + 3]);
	}

	static short makeShort(byte b1, byte b0) {
		return (short) (((b1 & 0xff) << 8) | ((b0 & 0xff) << 0));
	}
	
	static int makeInt(byte b3, byte b2, byte b1, byte b0) {
		return (int) ((((b3 & 0xff) << 24) | ((b2 & 0xff) << 16) | ((b1 & 0xff) << 8) | ((b0 & 0xff) << 0)));
	}
}
