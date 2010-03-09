package com.madgag.simpleusb;

public enum UsbEndpointDirection {
	OUT,
	IN
	;
	
	public static UsbEndpointDirection from(byte addressByte) {
		return UsbEndpointDirection.values()[(addressByte & 1<<7) >> 7];
	}
}
