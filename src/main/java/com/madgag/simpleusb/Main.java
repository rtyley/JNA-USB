package com.madgag.simpleusb;

import com.sun.jna.ptr.PointerByReference;

import libusbone.LibusboneLibrary;

public class Main {
	public static void main(String[] args) {
		LibusboneLibrary lib = LibusboneLibrary.INSTANCE;
		
		PointerByReference ctx = new PointerByReference();
		LibusboneLibrary.libusb_context[] bang=new LibusboneLibrary.libusb_context[0];
		int r=lib.libusb_init(ctx);
		LibusboneLibrary.libusb_context cont = new LibusboneLibrary.libusb_context(ctx.getValue());
		System.out.println("r="+r);
		lib.libusb_exit(cont);
		System.out.println("bye");
	}
}
