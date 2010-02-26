package com.madgag.simpleusb;

import com.sun.jna.ptr.PointerByReference;

import libusbone.LibusboneLibrary;

public class Main {
	public static void main(String[] args) {
		LibusboneLibrary lib = LibusboneLibrary.INSTANCE;
		
		PointerByReference ctx = new PointerByReference();
		LibusboneLibrary.libusb_context[] bang=new LibusboneLibrary.libusb_context[0];
		int r=lib.libusb_init(ctx);
		LibusboneLibrary.libusb_context libUsbContext = new LibusboneLibrary.libusb_context(ctx.getValue());
		System.out.println("r="+r);
		
		PointerByReference deviceList = new PointerByReference();
		int cnt=lib.libusb_get_device_list(libUsbContext, deviceList );
		System.out.println("choco tomato "+cnt);
		//list.getValue();
		
		libusbone.LibusboneLibrary.libusb_device[] realList= null;
		
		lib.libusb_free_device_list(deviceList.getPointer(), 1);
		System.out.println("sap");
		
		lib.libusb_exit(libUsbContext);
		System.out.println("bye");
	}
}
